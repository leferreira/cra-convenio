package br.com.ieptbto.cra.page.titulo.entrada;

import java.util.ArrayList;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.mediator.AvalistaMediator;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class EntradaManualPage extends BasePage<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	@SpringBean
	AvalistaMediator avalistaMediator;
	@SpringBean
	FiliadoMediator filiadoMediator;
	private TituloFiliado tituloFiliado;
	private FileUploadField fileUploadField;

	public EntradaManualPage() {
		this.tituloFiliado = new TituloFiliado();
		this.tituloFiliado.setUsuarioEntradaManual(getUser());
		this.tituloFiliado.setAvalistas(new ArrayList<Avalista>());
		this.tituloFiliado.setFiliado(usuarioFiliadoMediator.buscarEmpresaFiliadaDoUsuario(getUser()));
		this.tituloFiliado.setSetor(filiadoMediator.buscarSetorPadraoFiliado(tituloFiliado.getFiliado()));
		this.tituloFiliado.setSituacaoTituloConvenio(SituacaoTituloConvenio.AGUARDANDO);

		adicionarComponentes();
	}

	public EntradaManualPage(String message) {
		this.tituloFiliado = new TituloFiliado();
		this.tituloFiliado.setUsuarioEntradaManual(getUser());
		this.tituloFiliado.setAvalistas(new ArrayList<Avalista>());
		this.tituloFiliado.setFiliado(usuarioFiliadoMediator.buscarEmpresaFiliadaDoUsuario(getUser()));
		this.tituloFiliado.setSetor(filiadoMediator.buscarSetorPadraoFiliado(tituloFiliado.getFiliado()));
		this.tituloFiliado.setSituacaoTituloConvenio(SituacaoTituloConvenio.AGUARDANDO);

		success(message);
		adicionarComponentes();
	}

	public EntradaManualPage(TituloFiliado titulo) {
		this.tituloFiliado = titulo;
		this.tituloFiliado.setAvalistas(avalistaMediator.buscarAvalistasPorTitulo(titulo));

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		criarCampoAnexo();
		carregarFormularioTitulo();

	}

	public void carregarFormularioTitulo() {
		EntradaManualForm form = new EntradaManualForm("form", getModel(), getUser(), fileUploadField);
		form.add(divSetorFiliado());
		form.add(new EntradaManualInputPanel("entradaManualInputPanel", getModel(), fileUploadField));
		form.setMultiPart(true);
		form.setMaxSize(Bytes.megabytes(5));
		add(form);

		add(new AvalistaInputPanel("avalistaPanel", getModel(), tituloFiliado.getAvalistas()));
		add(listaAvalistas());
	}

	private void criarCampoAnexo() {
		fileUploadField = new FileUploadField("anexo", new ListModel<FileUpload>());
		fileUploadField.setLabel(new Model<String>("Anexo de Documento"));
	}

	private WebMarkupContainer divSetorFiliado() {
		WebMarkupContainer divSetores = new WebMarkupContainer("divSetoresFiliados");
		divSetores.add(campoSetorTitulo());
		divSetores.setVisible(getUser().getInstituicao().getSetoresConvenio());
		return divSetores;
	}

	private DropDownChoice<SetorFiliado> campoSetorTitulo() {
		IChoiceRenderer<SetorFiliado> renderer = new ChoiceRenderer<SetorFiliado>("descricao");
		DropDownChoice<SetorFiliado> comboMunicipio = new DropDownChoice<SetorFiliado>("setor",
				filiadoMediator.buscarSetoresAtivosFiliado(usuarioFiliadoMediator.buscarUsuarioFiliado(getUser()).getFiliado()), renderer);
		comboMunicipio.setLabel(new Model<String>("Setor Filiado"));
		return comboMunicipio;
	}

	private ListView<Avalista> listaAvalistas() {
		return new ListView<Avalista>("listaAvalistas", tituloFiliado.getAvalistas()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Avalista> item) {
				Avalista avalista = item.getModelObject();
				item.add(new Label("contador", item.getIndex() + 1));
				item.add(new Label("nomeAvalista", avalista.getNome()));
				item.add(new Label("documentoAvalista", avalista.getDocumento()));
				item.add(new Label("cidadeAvalista", avalista.getCidade()));
				item.add(new Label("ufAvalista", avalista.getUf()));
				item.add(removerAvalista(avalista));
			}

			private Link<Avalista> removerAvalista(final Avalista avalista) {
				return new Link<Avalista>("remover") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						if (avalista.getId() != 0) {
							avalistaMediator.removerAvalista(avalista);
						}
						tituloFiliado.getAvalistas().remove(avalista);
					}
				};
			}
		};
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(tituloFiliado);
	}
}
