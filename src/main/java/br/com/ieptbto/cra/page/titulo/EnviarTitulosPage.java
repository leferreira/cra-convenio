package br.com.ieptbto.cra.page.titulo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.enumeration.EnumerationSimNao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class EnviarTitulosPage extends BasePage<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EnviarTitulosPage.class);

	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	private FiliadoMediator filiadoMediator;
	@SpringBean
	private UsuarioFiliadoMediator usuarioFiliadoMediator;
	private TituloFiliado titulo;
	private SetorFiliado setorFiliado;
	private List<TituloFiliado> titulos;
	
	private ListView<TituloFiliado> listaTituloFiliado;
	private DropDownChoice<SetorFiliado> selectSetorFiliado;
	
	public EnviarTitulosPage() {
		this.titulo = new TituloFiliado();
		this.setorFiliado = null;
		this.titulos = tituloFiliadoMediator.buscarTitulosParaEnvio(getFiliado(), setorFiliado);
		
		formularioFiltroPorSetor();
		formularioEnviarTitulos();
	}
	
	public EnviarTitulosPage(SetorFiliado setor) {
		this.titulo = new TituloFiliado();
		this.setorFiliado = setor;
		this.titulos = tituloFiliadoMediator.buscarTitulosParaEnvio(getFiliado(), setorFiliado);
		
		formularioFiltroPorSetor();
		formularioEnviarTitulos();
	}
	
	private void formularioEnviarTitulos() {
		final CheckGroup<TituloFiliado> titulosSelecionados = new CheckGroup<TituloFiliado>("group", new ArrayList<TituloFiliado>());
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel()){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				
				try {
					List<TituloFiliado> titulos = (List<TituloFiliado>) titulosSelecionados.getModelObject();
					tituloFiliadoMediator.enviarTitulosPendentes(titulos);
					
					getTitulos().clear();
					getFeedbackPanel().info("Os títulos foram encaminhados com sucesso !");
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com o IEPTB-TO ! ");
				}
			}
		};
		form.add(carregarTitulosParaEnvio());

		form.add(titulosSelecionados);
		listaTituloFiliado.setReuseItems(true);
		titulosSelecionados.add(listaTituloFiliado);
		add(form);
	}

	private void formularioFiltroPorSetor() {
		WebMarkupContainer divSetoresFiliados = new WebMarkupContainer("divSetoresFiliados");
		Form<SetorFiliado> formularioFitroSetor = new Form<SetorFiliado>("formFiltroSetor"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				
				try {
					SetorFiliado setor = selectSetorFiliado.getModelObject();
					setResponsePage(new EnviarTitulosPage(setor));
					
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível filtrar os títulos! \n Entre em contato com o IEPTB-TO ! ");
				}
			}
		};
		formularioFitroSetor.add(comboSetorFiliado());
		if (getUser().getInstituicao().getPermitidoSetoresConvenio().equals(EnumerationSimNao.NAO)) {
			divSetoresFiliados.setVisible(false);
		}
		divSetoresFiliados.add(formularioFitroSetor);
		add(divSetoresFiliados);
	}

	private DropDownChoice<SetorFiliado> comboSetorFiliado() {
		IChoiceRenderer<SetorFiliado> renderer = new ChoiceRenderer<SetorFiliado>("descricao");
		Filiado filiado = usuarioFiliadoMediator.buscarUsuarioFiliado(getUser()).getFiliado();
		selectSetorFiliado = new DropDownChoice<SetorFiliado>("setor", new Model<SetorFiliado>(), filiadoMediator.buscarSetoresFiliado(filiado), renderer);
		if (this.setorFiliado != null) {
			selectSetorFiliado = new DropDownChoice<SetorFiliado>("setor", new Model<SetorFiliado>(setorFiliado), filiadoMediator.buscarSetoresFiliado(filiado), renderer);
		}
		selectSetorFiliado.setRequired(true);
		selectSetorFiliado.setLabel(new Model<String>("Setor"));
		return selectSetorFiliado;
	}
	
	private ListView<TituloFiliado> carregarTitulosParaEnvio() {
		return listaTituloFiliado = new ListView<TituloFiliado>("listViewTitulos", carregarTitulos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();
				item.add(new Check<TituloFiliado>("checkbox", item.getModel()));
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new Label("municipio", tituloLista.getCidadeDevedor()));
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
				
				item.add(new Label("setor", tituloLista.getSetor().getDescricao()));
				item.add(new Label("dataEmissao", DataUtil.localDateToString(tituloLista.getDataEmissao())));
				item.add(new Label("dataVencimento", DataUtil.localDateToString(tituloLista.getDataVencimento())));
				item.add(new LabelValorMonetario<BigDecimal>("valor", tituloLista.getValorTitulo()));
				item.add(removerTitulo(tituloLista));
			}
			
			private Link<TituloFiliado> removerTitulo(final TituloFiliado titulo) {
				return new Link<TituloFiliado>("remover") {
					
					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						
						try {
							tituloFiliadoMediator.removerTituloFiliado(titulo);
							
							getTitulos().remove(titulo);
							getFeedbackPanel().info("O título foi removido com sucesso !");
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
							error("Não foi possível remover o título ! Entre em contato com a CRA !");
						}
					}
				};
			}
		};
	}
	
	private Filiado getFiliado(){
		return usuarioFiliadoMediator.buscarEmpresaFiliadaDoUsuario(getUser());
	}
	
	public IModel<List<TituloFiliado>> carregarTitulos() {
		return new LoadableDetachableModel<List<TituloFiliado>>() {
			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloFiliado> load() {
				return getTitulos();
			}
		};
	}

	private List<TituloFiliado> getTitulos() {
		if (titulos == null) {
			titulos = new ArrayList<TituloFiliado>();
		}
		return titulos;
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
