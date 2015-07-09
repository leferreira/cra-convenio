package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.util.EstadoUtils;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "serial", "unused"})
public class AvalistaInputPanel extends Panel {

	private Avalista avalista = new Avalista();
	private List<Avalista> avalistas;
	
	public AvalistaInputPanel(String id, IModel<TituloFiliado> model, List<Avalista> avalistas) {
		super(id, model);
		this.avalistas = avalistas;
		
		add(campoNome());
		add(campoDocumento());
		add(campoEndereco());
//		add(campoCidade());
//		add(campoCep());
//		add(campoUf());
		add(new Link<Avalista>("adicionarAvalista"){

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private TextField<String> campoNome() {
		TextField<String> textField = new TextField<String>("nomeAvalista");
		textField.setLabel(new Model<String>("Nome do Avalista"));
		textField.setRequired(true);
		return textField;
	}
	
	private TextField<String> campoDocumento() {
		TextField<String> textField = new TextField<String>("documentoAvalista");
		textField.setLabel(new Model<String>("CPF/CNPJ"));
		textField.setRequired(true);
		return textField;
	}
	
	private DropDownChoice<String> ufDevedor() {
		DropDownChoice<String> textField = new DropDownChoice<String>("ufDevedor", EstadoUtils.getEstadosToList());
		textField.setLabel(new Model<String>("UF"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> cepDevedor() {
		TextField<String> textField = new TextField<String>("cepDevedor");
		textField.setLabel(new Model<String>("CEP"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> cidadeDevedor() {
		TextField<String> textField = new TextField<String>("cidadeDevedor");
		textField.setLabel(new Model<String>("Cidade"));
		textField.setRequired(true);
		return textField;
	}

	private TextArea<String> campoEndereco() {
		TextArea<String> textField = new TextArea<String>("enderecoDevedor");
		textField.setLabel(new Model<String>("Endereco"));
		textField.setRequired(true);
		return textField;
	}

	public Avalista getAvalista() {
		return avalista;
	}

	public void setAvalista(Avalista avalista) {
		this.avalista = avalista;
	}

	public List<Avalista> getAvalistas() {
		return avalistas;
	}

	public void setAvalistas(List<Avalista> avalistas) {
		this.avalistas = avalistas;
	}
}
