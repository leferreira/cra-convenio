package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.util.EstadoUtils;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings( "serial" )
public class AvalistaInputPanel extends Panel {

	public AvalistaInputPanel(String id, CompoundPropertyModel<Avalista> model) {
		super(id, model);
		add(campoNome());
		add(campoDocumento());
		add(campoEndereco());
		add(campoCidade());
		add(campoCep());
		add(campoUf());
	}

	private TextField<String> campoNome() {
		TextField<String> textField = new TextField<String>("nome");
		textField.setLabel(new Model<String>("Nome do Avalista"));
		textField.setRequired(true);
		return textField;
	}
	
	private TextField<String> campoDocumento() {
		TextField<String> textField = new TextField<String>("documento");
		textField.setLabel(new Model<String>("CPF/CNPJ"));
		textField.setRequired(true);
		return textField;
	}
	
	private DropDownChoice<String> campoUf() {
		DropDownChoice<String> textField = new DropDownChoice<String>("uf", EstadoUtils.getEstadosToList());
		textField.setLabel(new Model<String>("UF"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> campoCep() {
		TextField<String> textField = new TextField<String>("cep");
		textField.setLabel(new Model<String>("CEP"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> campoCidade() {
		TextField<String> textField = new TextField<String>("cidade");
		textField.setLabel(new Model<String>("Cidade"));
		textField.setRequired(true);
		return textField;
	}

	private TextArea<String> campoEndereco() {
		TextArea<String> textField = new TextArea<String>("endereco");
		textField.setLabel(new Model<String>("Endereco"));
		textField.setRequired(true);
		return textField;
	}
}
