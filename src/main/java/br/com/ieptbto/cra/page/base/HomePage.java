package br.com.ieptbto.cra.page.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.titulo.EntradaManualPage;
import br.com.ieptbto.cra.page.titulo.EnviarTitulosPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;

	private UsuarioFiliado usuarioFiliado;
	private Arquivo arquivo;
	private List<TituloFiliado> titulosFiliado;

	public HomePage() {
		this.usuarioFiliado = usuarioFiliadoMediator.buscarUsuarioFiliado(getUser());

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarHomeConvenioEAssociados();
		carregarContratoEntradaDeTitulos();
		add(carregarDivHomeFiliado());
		add(carregarDivHomeConvenio());

	}

	private void carregarHomeConvenioEAssociados() {
		if (getUsuarioFiliado() == null) {
			// this.arquivo =
			// remessaMediator.arquivosPendentes(getUser().getInstituicao());
			this.arquivo = new Arquivo();
			this.arquivo.setRemessas(new ArrayList<Remessa>());
			this.titulosFiliado = new ArrayList<TituloFiliado>();
		} else {
			this.arquivo = new Arquivo();
			this.titulosFiliado = tituloFiliadoMediator.buscarTitulosParaEnvio(getEmpresaFiliado(), null);
		}
	}

	private WebMarkupContainer carregarDivHomeConvenio() {
		WebMarkupContainer divConvenio = new WebMarkupContainer("divAssociado");
		divConvenio.setOutputMarkupId(true);

		if (getUsuarioFiliado() != null) {
			divConvenio.setVisible(false);
		}
		divConvenio.add(labelQuantidadeRemessasPendentes());
		divConvenio.add(listaConfirmacoesPendentes());
		return divConvenio;
	}

	private WebMarkupContainer carregarDivHomeFiliado() {
		WebMarkupContainer divFiliado = new WebMarkupContainer("divFiliado");

		if (getUsuarioFiliado() == null) {
			divFiliado.setVisible(false);
		}
		divFiliado.add(labelQuantidadeTitulosPendentes());
		divFiliado.add(listaTitulosPendentes());
		divFiliado.add(linkTitulosPendentes());
		return divFiliado;
	}

	private Label labelQuantidadeRemessasPendentes() {
		int quantidade = 0;
		if (getConfirmacoesPendentes() != null) {
			quantidade = getConfirmacoesPendentes().size();
		}
		return new Label("qtdRemessas", quantidade);
	}

	private ListView<Remessa> listaConfirmacoesPendentes() {
		return new ListView<Remessa>("listConfirmacoes", getConfirmacoesPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(new Label("arquivo", remessa.getArquivo().getNomeArquivo()));
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = remessa.getInstituicaoOrigem().getNomeFantasia();
					item.add(new Label("instituicao", nomeFantasia.toUpperCase()));
				} else {
					item.add(new Label("instituicao", municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio())
							.getNomeMunicipio().toUpperCase()));
				}
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getDataRecebimento().toDate(), new Date())));
				item.add(downloadArquivoTXT(remessa));
			}

			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							getApplication().getResourceSettings().getPropertiesFactory().clearCache();

							File file = remessaMediator.baixarRemessaTXT(getUser(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);

							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};
			}
		};
	}

	private Link<TituloFiliado> linkTitulosPendentes() {
		return new Link<TituloFiliado>("linkTitulos") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new EnviarTitulosPage());
			}
		};
	}

	private ListView<TituloFiliado> listaTitulosPendentes() {
		return new ListView<TituloFiliado>("listViewTitulos", getTitulosFiliado()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();

				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				Link<String> linkAlterar = new Link<String>("linkAlterar") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new EntradaManualPage(tituloLista));
					}
				};
				linkAlterar.add(new Label("devedor", tituloLista.getNomeDevedor()));
				item.add(linkAlterar);
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(tituloLista.getDataEntrada(), new Date())));
			}
		};
	}

	private Label labelQuantidadeTitulosPendentes() {
		int quantidade = 0;
		if (getTitulosFiliado() != null) {
			quantidade = getTitulosFiliado().size();
		}
		return new Label("qtdTitulos", quantidade);
	}

	private void carregarContratoEntradaDeTitulos() {
		final ModalWindow modalContrato = new ModalWindow("modalContrato");
		modalContrato.setPageCreator(new ModalWindow.PageCreator() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public Page createPage() {
				return new ContratoModal(HomePage.this.getPageReference(), modalContrato, getUser());
			}
		});
		modalContrato.setTitle(new Model<String>("Termos e Condições para Entrada de Títulos"));
		modalContrato.setResizable(false);
		modalContrato.setAutoSize(false);
		modalContrato.setInitialWidth(80);
		modalContrato.setInitialHeight(30);
		modalContrato.setMinimalWidth(80);
		modalContrato.setMinimalHeight(30);
		modalContrato.setWidthUnit("em");
		modalContrato.setHeightUnit("em");
		add(modalContrato);

		AjaxLink<?> openModal = new AjaxLink<Void>("showModal") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalContrato.show(target);
			}
		};
		if (!verificarAceiteUsuarioContrato()) {
			openModal.setMarkupId("showModal");
		}
		add(openModal);
	}

	private boolean verificarAceiteUsuarioContrato() {
		UsuarioFiliado usuarioFiliado = usuarioFiliadoMediator.buscarUsuarioFiliado(getUser());
		if (usuarioFiliado != null) {
			return usuarioFiliado.isTermosContratoAceite();
		}
		return true;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	private List<Remessa> getConfirmacoesPendentes() {
		return arquivo.getRemessas();
	}

	public HomePage(PageParameters parameters) {
		error(parameters.get("error"));
	}

	public UsuarioFiliado getUsuarioFiliado() {
		return usuarioFiliado;
	}

	private Filiado getEmpresaFiliado() {
		return usuarioFiliadoMediator.buscarEmpresaFiliadaDoUsuario(getUser());
	}

	public List<TituloFiliado> getTitulosFiliado() {
		if (titulosFiliado == null) {
			titulosFiliado = new ArrayList<TituloFiliado>();
		}
		return titulosFiliado;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}