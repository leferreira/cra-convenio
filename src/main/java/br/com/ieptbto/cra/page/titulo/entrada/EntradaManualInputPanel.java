package br.com.ieptbto.cra.page.titulo.entrada;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.enumeration.TipoAlineaCheque;
import br.com.ieptbto.cra.enumeration.EspecieTituloEntradaManual;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.util.EstadoUtils;

public class EntradaManualInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private MunicipioMediator municipioMediator;
	@SpringBean
	private FiliadoMediator filiadoMediator;
	@SpringBean
	private UsuarioFiliadoMediator usuarioFiliadoMediator;

	private DropDownChoice<TipoAlineaCheque> comboAlinea;
	private FileUploadField fileUploadField;

	public EntradaManualInputPanel(String id, IModel<TituloFiliado> model, FileUploadField fileUploadField) {
		super(id, model);
		this.fileUploadField = fileUploadField;

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(fileUploadAnexo());
		add(labelContemAnexo());
		add(textFieldNumeroTitulo());
		add(dateFieldDataEmissao());
		add(dateFieldDataVencimento());
		add(dropDownPracaProtesto());
		add(textFieldDocumentoDevedor());
		add(textFieldValorTitulo());
		add(textFieldValorSaldoTitulo());
		add(textFieldNomeDevedor());
		add(textFieldEnderecoDevedor());
		add(textFieldCidadeDevedor());
		add(textFieldCepDevedor());
		add(dropDownUfDevedor());
		add(textFieldEspecieTitulo());
		add(textFieldBairroDevedor());
		add(dropDownCampoAlinea());
	}

	private Label labelContemAnexo() {
		if (TituloFiliado.class.cast(getDefaultModelObject()).getId() == 0) {
			Label label = new Label("labelContemAnexo", "");
			label.setVisible(false);
			return label;
		}
		if (TituloFiliado.class.cast(getDefaultModelObject()).getAnexo() != null) {
			return new Label("labelContemAnexo", "Este título contém anexo(s). Para alterar, selecione novamente o arquivo e salve as informações!");
		}
		return new Label("labelContemAnexo", "Não contém anexos!");
	}

	private TextField<String> textFieldNumeroTitulo() {
		TextField<String> textField = new TextField<String>("numeroTitulo");
		textField.setLabel(new Model<String>("Nùmero do Título"));
		textField.setRequired(true);
		return textField;
	}

	private DateTextField dateFieldDataEmissao() {
		DateTextField dateField = new DateTextField("dataEmissao", "dd/MM/yyyy");
		if (TituloFiliado.class.cast(getDefaultModelObject()).getDataEmissao() != null) {
			dateField = new DateTextField("dataEmissao", new Model<Date>(TituloFiliado.class.cast(getDefaultModelObject()).getDataEmissao()), "dd/MM/yyyy");
		}
		dateField.setLabel(new Model<String>("Data Emissão"));
		dateField.setRequired(true);
		return dateField;
	}

	private DateTextField dateFieldDataVencimento() {
		DateTextField dateField = new DateTextField("dataVencimento", "dd/MM/yyyy");
		if (TituloFiliado.class.cast(getDefaultModelObject()).getDataVencimento() != null) {
			dateField =
					new DateTextField("dataVencimento", new Model<Date>(TituloFiliado.class.cast(getDefaultModelObject()).getDataVencimento()), "dd/MM/yyyy");
		}
		dateField.setLabel(new Model<String>("Data de Vencimento"));
		dateField.setRequired(true);
		return dateField;
	}

	private DropDownChoice<Municipio> dropDownPracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> comboMunicipio = new DropDownChoice<Municipio>("pracaProtesto", municipioMediator.getMunicipiosTocantins(), renderer);
		comboMunicipio.setLabel(new Model<String>("Município"));
		comboMunicipio.setRequired(true);
		return comboMunicipio;
	}

	private TextField<String> textFieldDocumentoDevedor() {
		TextField<String> textField = new TextField<String>("CpfCnpj");
		textField.setLabel(new Model<String>("CPF/CNPJ"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<BigDecimal> textFieldValorTitulo() {
		TextField<BigDecimal> textField = new TextField<BigDecimal>("valorTitulo");
		textField.setLabel(new Model<String>("Valor do Débito"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private DropDownChoice<String> dropDownUfDevedor() {
		DropDownChoice<String> textField = new DropDownChoice<String>("ufDevedor", EstadoUtils.getEstadosToList());
		textField.setLabel(new Model<String>("UF"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> textFieldCepDevedor() {
		TextField<String> textField = new TextField<String>("cepDevedor");
		textField.setLabel(new Model<String>("CEP"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> textFieldCidadeDevedor() {
		TextField<String> textField = new TextField<String>("cidadeDevedor");
		textField.setLabel(new Model<String>("Cidade"));
		textField.setRequired(true);
		return textField;
	}

	private TextArea<String> textFieldEnderecoDevedor() {
		TextArea<String> textField = new TextArea<String>("enderecoDevedor");
		textField.setLabel(new Model<String>("Endereco"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> textFieldNomeDevedor() {
		TextField<String> textField = new TextField<String>("nomeDevedor");
		textField.setLabel(new Model<String>("Nome do Devedor"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<BigDecimal> textFieldValorSaldoTitulo() {
		TextField<BigDecimal> textField = new TextField<BigDecimal>("valorSaldoTitulo");
		textField.setLabel(new Model<String>("Valor de Protesto"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> textFieldBairroDevedor() {
		TextField<String> textField = new TextField<String>("bairroDevedor");
		textField.setLabel(new Model<String>("Bairro do Devedor"));
		textField.setRequired(true);
		return textField;
	}

	private DropDownChoice<EspecieTituloEntradaManual> textFieldEspecieTitulo() {
		IChoiceRenderer<EspecieTituloEntradaManual> renderer = new ChoiceRenderer<EspecieTituloEntradaManual>("label");
		final DropDownChoice<EspecieTituloEntradaManual> dropDownEspecie =
				new DropDownChoice<EspecieTituloEntradaManual>("especieTitulo", Arrays.asList(EspecieTituloEntradaManual.values()), renderer);
		dropDownEspecie.add(new OnChangeAjaxBehavior() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				EspecieTituloEntradaManual tipoEspecie = dropDownEspecie.getModelObject();
				if (tipoEspecie != null) {
					if (tipoEspecie.equals(EspecieTituloEntradaManual.CH)) {
						comboAlinea.setEnabled(true);
						comboAlinea.setRequired(true);
					} else {
						comboAlinea.setEnabled(false);
						comboAlinea.setRequired(false);
					}
				} else {
					comboAlinea.setEnabled(false);
					comboAlinea.setRequired(false);
				}
				target.add(comboAlinea);
			}
		});
		dropDownEspecie.setLabel(new Model<String>("Espécie do Documento"));
		dropDownEspecie.setRequired(true);
		return dropDownEspecie;
	}

	private DropDownChoice<TipoAlineaCheque> dropDownCampoAlinea() {
		IChoiceRenderer<TipoAlineaCheque> renderer = new ChoiceRenderer<TipoAlineaCheque>("label");
		comboAlinea = new DropDownChoice<TipoAlineaCheque>("alinea", Arrays.asList(TipoAlineaCheque.values()), renderer);
		comboAlinea.setLabel(new Model<String>("Alínea"));
		comboAlinea.setEnabled(false);
		comboAlinea.setOutputMarkupId(true);
		return comboAlinea;
	}

	private FileUploadField fileUploadAnexo() {
		return fileUploadField;
	}
}