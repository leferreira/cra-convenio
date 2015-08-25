package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.wicket.markup.html.form.Button;
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
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.EstadoUtils;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class AvalistaInputPanel extends Panel {

	private Avalista avalista;
	private TituloFiliado titulo;
	private List<Avalista> avalistas;

	public AvalistaInputPanel(String id, IModel<TituloFiliado> model, List<Avalista> avalistas) {
		super(id, model);
		this.avalistas = avalistas;
		this.titulo = model.getObject();
		this.avalista = new Avalista();
		
		carregarComponentes();
	}

	private void carregarComponentes() {
		Form<Avalista> formAvalista = new Form<Avalista>("formAvalista",  new CompoundPropertyModel<Avalista>(avalista)){
			@Override
			protected void onSubmit() {
				super.onSubmit();
				Avalista avalistaTitulo = getModelObject();

				try {
					if (avalistaTitulo.getNome() == null || avalistaTitulo.getEndereco() == null ||
							avalistaTitulo.getCidade() == null || avalistaTitulo.getUf() == null ||
							avalistaTitulo.getDocumento() == null || avalistaTitulo.getCep() == null || avalistaTitulo.getBairro() == null) {
						throw new InfraException("O Avalista não pode ter informações em branco !");
					} else {
						getAvalistas().add(avalistaTitulo);		
						
						Avalista novoAvalista = new Avalista();
						setAvalista(novoAvalista);
						getModel().setObject(novoAvalista);
					}
				} catch (InfraException ex) {
					error(ex.getMessage());
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
		formAvalista.add(new Button("adicionarAvalista"));
		add(formAvalista);
	}

	private TextField<String> campoNome() {
		TextField<String> fieldNome = new TextField<String>("nome");
		fieldNome.setLabel(new Model<String>("Nome do Avalista"));
		return fieldNome;
	}

	private TextField<String> campoDocumento() {
		TextField<String> fieldDocumento = new TextField<String>("documento");
		fieldDocumento.setLabel(new Model<String>("CPF/CNPJ do Avalista"));
		return fieldDocumento;
	}

	private DropDownChoice<String> campoUf() {
		DropDownChoice<String> fieldUf = new DropDownChoice<String>("uf",
				EstadoUtils.getEstadosToList());
		fieldUf.setLabel(new Model<String>("UF do Avalista"));
		return fieldUf;
	}

	private TextField<String> campoCep() {
		TextField<String> fieldCep = new TextField<String>("cep");
		fieldCep.setLabel(new Model<String>("CEP do Avalista"));
		return fieldCep;
	}

	private TextField<String> campoCidade() {
		TextField<String> fieldCidade = new TextField<String>("cidade");
		fieldCidade.setLabel(new Model<String>("Cidade do Avalista"));
		return fieldCidade;
	}

	private TextArea<String> campoEndereco() {
		TextArea<String> fieldEndereco = new TextArea<String>("endereco");
		fieldEndereco.setLabel(new Model<String>("Endereco do Avalista"));
		return fieldEndereco;
	}
	
	private TextField<String> campoBairro() {
		TextField<String> fieldBairro = new TextField<String>("bairro");
		fieldBairro.setLabel(new Model<String>("Bairro do Avalista"));
		return fieldBairro;
	}

	public List<Avalista> getAvalistas() {
		return avalistas;
	}

	public void setAvalistas(List<Avalista> avalistas) {
		this.avalistas = avalistas;
	}

	public Avalista getAvalista() {
		return avalista;
	}

	public void setAvalista(Avalista avalista) {
		this.avalista = avalista;
	}

	public TituloFiliado getTitulo() {
		return titulo;
	}
}
