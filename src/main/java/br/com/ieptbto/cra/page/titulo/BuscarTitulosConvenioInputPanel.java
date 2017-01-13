package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.bean.TituloConvenioBean;
import br.com.ieptbto.cra.component.label.LocalDateTextField;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

/**
 * @author Thasso Araújo
 * 
 */
public class BuscarTitulosConvenioInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	FiliadoMediator filiadoMediator;

	private Instituicao instituicao;
	private Filiado filiado;

	public BuscarTitulosConvenioInputPanel(String id, IModel<TituloConvenioBean> model, Instituicao instituicao, Filiado filiado) {
		super(id, model);
		this.instituicao = instituicao;
		this.filiado = filiado;

		add(textFieldNumeroTitulo());
		add(textFieldNomeDevedor());
		add(textFieldDocumentoDevedor());
		add(textFieldDataInicio());
		add(dropDownCartorioProtesto());
		add(dropDownFiliado());
	}

	private TextField<String> textFieldNumeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}

	private TextField<String> textFieldNomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}

	private TextField<String> textFieldDocumentoDevedor() {
		return new TextField<String>("documentoDevedor");
	}

	private LocalDateTextField textFieldDataInicio() {
		return new LocalDateTextField("dataInicio");
	}

	private DropDownChoice<Instituicao> dropDownCartorioProtesto() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio");
		DropDownChoice<Instituicao> campoPracaProtesto = new DropDownChoice<Instituicao>("instiuicaoCartorio", instituicaoMediator.getCartorios(), renderer);
		return campoPracaProtesto;
	}

	private DropDownChoice<Filiado> dropDownFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		DropDownChoice<Filiado> campoFiliado = new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarListaFiliados(instituicao), renderer);
		campoFiliado.setLabel(new Model<String>("Filiado"));
		campoFiliado.setOutputMarkupId(true);

		if (filiado != null) {
			campoFiliado.setEnabled(false);
			campoFiliado.setModel(new Model<Filiado>());
			campoFiliado.setDefaultModelObject(filiado);
		}
		return campoFiliado;
	}
}