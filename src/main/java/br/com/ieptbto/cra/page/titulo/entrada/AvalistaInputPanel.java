package br.com.ieptbto.cra.page.titulo.entrada;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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

	public AvalistaInputPanel(String id, IModel<TituloFiliado> model, List<Avalista> avalistas) {
		super(id, model);
		this.avalista = new Avalista();
		this.avalistas = avalistas;
		this.titulo = model.getObject();

		carregarComponentes();
	}

	private void carregarComponentes() {
		Form<Avalista> form = new Form<Avalista>("formAvalista", new CompoundPropertyModel<Avalista>(avalista)) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Avalista avalistaTitulo = getModelObject();

				if (!StringUtils.isBlank(avalistaTitulo.getNome()) && !StringUtils.isBlank(avalistaTitulo.getEndereco())
						&& !StringUtils.isBlank(avalistaTitulo.getCidade()) && !StringUtils.isBlank(avalistaTitulo.getUf())
						&& !StringUtils.isBlank(avalistaTitulo.getBairro()) && !StringUtils.isBlank(avalistaTitulo.getDocumento())
						&& !StringUtils.isBlank(avalistaTitulo.getCep())) {
					getAvalistas().add(avalistaTitulo);

					Avalista novoAvalista = new Avalista();
					setAvalista(novoAvalista);
					getModel().setObject(novoAvalista);
				} else if (StringUtils.isBlank(avalistaTitulo.getNome()) || StringUtils.isBlank(avalistaTitulo.getEndereco())
						|| StringUtils.isBlank(avalistaTitulo.getCidade()) || StringUtils.isBlank(avalistaTitulo.getUf())
						|| StringUtils.isBlank(avalistaTitulo.getBairro()) || StringUtils.isBlank(avalistaTitulo.getDocumento())
						|| StringUtils.isBlank(avalistaTitulo.getCep())) {
					error("O outro devedor incluído não pode ter informações em branco...");
				}
			}
		};
		form.add(campoNome());
		form.add(campoDocumento());
		form.add(campoEndereco());
		form.add(campoCidade());
		form.add(campoCep());
		form.add(campoUf());
		form.add(campoBairro());
		form.add(new Button("submitAvalista"));
		add(form);
	}

	private TextField<String> campoNome() {
		TextField<String> fieldNome;
		fieldNome = new TextField<String>("nome");
		fieldNome.setLabel(new Model<String>("Nome do Avalista"));
		return fieldNome;
	}

	private TextField<String> campoDocumento() {
		TextField<String> fieldDocumento;
		fieldDocumento = new TextField<String>("documento");
		fieldDocumento.setLabel(new Model<String>("CPF/CNPJ do Avalista"));
		return fieldDocumento;
	}

	private DropDownChoice<String> campoUf() {
		DropDownChoice<String> fieldUf;
		fieldUf = new DropDownChoice<String>("uf", EstadoUtils.getEstadosToList());
		fieldUf.setLabel(new Model<String>("UF do Avalista"));
		return fieldUf;
	}

	private TextField<String> campoCep() {
		TextField<String> fieldCep;
		fieldCep = new TextField<String>("cep");
		fieldCep.setLabel(new Model<String>("CEP do Avalista"));
		return fieldCep;
	}

	private TextField<String> campoCidade() {
		TextField<String> fieldCidade;
		fieldCidade = new TextField<String>("cidade");
		fieldCidade.setLabel(new Model<String>("Cidade do Avalista"));
		return fieldCidade;
	}

	private TextArea<String> campoEndereco() {
		TextArea<String> fieldEndereco;
		fieldEndereco = new TextArea<String>("endereco");
		fieldEndereco.setLabel(new Model<String>("Endereco do Avalista"));
		return fieldEndereco;
	}

	private TextField<String> campoBairro() {
		TextField<String> fieldBairro;
		fieldBairro = new TextField<String>("bairro");
		fieldBairro.setLabel(new Model<String>("Bairro do Avalista"));
		return fieldBairro;
	}

	public List<Avalista> getAvalistas() {
		return avalistas;
	}

	public TituloFiliado getTitulo() {
		return titulo;
	}

	public void setAvalista(Avalista avalista) {
		this.avalista = avalista;
	}

	public Avalista getAvalista() {
		return avalista;
	}
}
