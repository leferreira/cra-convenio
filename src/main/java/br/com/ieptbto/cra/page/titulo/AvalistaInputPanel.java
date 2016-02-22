package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.util.EstadoUtils;

/**
 * @author Thasso Araújo
 *
 */
public class AvalistaInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	
	private Avalista avalista;
	private TituloFiliado titulo;
	private List<Avalista> avalistas;
	
	private Form<Avalista> formAvalista;

	public AvalistaInputPanel(String id, IModel<TituloFiliado> model, List<Avalista> avalistas) {
		super(id, model);
		this.avalistas = avalistas;
		this.titulo = model.getObject();
		this.avalista = new Avalista();
		
		carregarComponentes();
	}

	private void carregarComponentes() {
		formAvalista = new Form<Avalista>("formAvalista",  new CompoundPropertyModel<Avalista>(avalista)){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Avalista avalistaTitulo = getModelObject();

				try {
					getAvalistas().add(avalistaTitulo);		
					formAvalista.setModelObject(new Avalista());
					
				} catch (Exception ex) {
					error("Não foi possível inserir o avaslista! Por favor entre em contato com o IEPTB !");
				}
			}
		};
		formAvalista.add(campoNome());
		formAvalista.add(campoDocumento());
		formAvalista.add(campoEndereco());
		formAvalista.add(campoCidade());
		formAvalista.add(campoCep());
		formAvalista.add(campoUf());
		formAvalista.add(campoBairro());
		add(formAvalista);
	}

	private TextField<String> campoNome() {
		TextField<String> fieldNome = new TextField<String>("nome");
		fieldNome.setLabel(new Model<String>("Nome do Avalista"));
		fieldNome.setRequired(true);
		return fieldNome;
	}

	private TextField<String> campoDocumento() {
		TextField<String> fieldDocumento = new TextField<String>("documento");
		fieldDocumento.setLabel(new Model<String>("CPF/CNPJ do Avalista"));
		fieldDocumento.setRequired(true);
		return fieldDocumento;
	}

	private DropDownChoice<String> campoUf() {
		DropDownChoice<String> fieldUf = new DropDownChoice<String>("uf",
				EstadoUtils.getEstadosToList());
		fieldUf.setLabel(new Model<String>("UF do Avalista"));
		fieldUf.setRequired(true);
		return fieldUf;
	}

	private TextField<String> campoCep() {
		TextField<String> fieldCep = new TextField<String>("cep");
		fieldCep.setLabel(new Model<String>("CEP do Avalista"));
		fieldCep.setRequired(true);
		return fieldCep;
	}

	private TextField<String> campoCidade() {
		TextField<String> fieldCidade = new TextField<String>("cidade");
		fieldCidade.setLabel(new Model<String>("Cidade do Avalista"));
		fieldCidade.setRequired(true);
		return fieldCidade;
	}

	private TextArea<String> campoEndereco() {
		TextArea<String> fieldEndereco = new TextArea<String>("endereco");
		fieldEndereco.setLabel(new Model<String>("Endereco do Avalista"));
		fieldEndereco.setRequired(true);
		return fieldEndereco;
	}
	
	private TextField<String> campoBairro() {
		TextField<String> fieldBairro = new TextField<String>("bairro");
		fieldBairro.setLabel(new Model<String>("Bairro do Avalista"));
		fieldBairro.setRequired(true);
		return fieldBairro;
	}

	public List<Avalista> getAvalistas() {
		return avalistas;
	}

	public Avalista getAvalista() {
		return avalista;
	}

	public TituloFiliado getTitulo() {
		return titulo;
	}
}
