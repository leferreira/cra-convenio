package br.com.ieptbto.cra.page.filiados;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class IncluirFiliadoPage extends BasePage<Filiado> {

	/***/
	private static final long serialVersionUID = 1L;
	private Filiado filiado;
	private DropDownChoice<Municipio> comboMunicipio;
	private String situacao;
	
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	FiliadoMediator filiadoMediator;
	
	public IncluirFiliadoPage() {
		this.filiado = new Filiado();
		carregarCampos();
	}
	
	public IncluirFiliadoPage(Filiado filiado) {
		this.filiado = filiado;
		carregarCampos();
	}
	
	private void carregarCampos() {
		Form<?> form = new Form<Filiado>("form", getModel()){

			/***/
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit() {
				Filiado novoFiliado = getModelObject();
				
				try {
					if (filiado.getId() != 0)
						filiadoMediator.alterarFiliado(novoFiliado);
					else {
						novoFiliado.setInstituicaoConvenio(getUser().getInstituicao());
						novoFiliado.setAtivo(vefificaSituacao());
						filiadoMediator.salvarFiliado(novoFiliado);
					}
					setResponsePage(new ListaFiliadoPage("Os dados foram salvos com sucesso na CRA !"));
				} catch (Exception e) {
					System.out.println(e.getMessage());
					error("Não foi possível cadastrar a empresa filiada ! Entre em contato com a CRA !");
				}
			}
			
			private Boolean vefificaSituacao() {
				if (situacao.equals("Ativo"))
					return true;
				return false;
			}
		};
		form.add(campoNomeCredor());
		form.add(campoDocumentoCredor());
		form.add(campoEnderecoCredor());
		form.add(campoCepCredor());
		form.add(campoCidadeCredor());
		form.add(campoUfCredor());
		form.add(campoSituacao());
		add(form);
	}
	
	private TextField<String> campoUfCredor() {
		TextField<String> textField = new TextField<String>("uf");
		textField.setRequired(true);
		return textField;
	}

	private DropDownChoice<Municipio> campoCidadeCredor() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		comboMunicipio = new DropDownChoice<Municipio>("municipio", municipioMediator.listarTodos(), renderer);
		comboMunicipio.setLabel(new Model<String>("Município"));
		comboMunicipio.setRequired(true);
		return comboMunicipio;
	}

	private TextField<String> campoCepCredor() {
		TextField<String> textField = new TextField<String>("cep");
		textField.setRequired(true);
		return textField;
	}

	private TextArea<String> campoEnderecoCredor() {
		TextArea<String> textArea = new TextArea<String>("endereco");
		textArea.setRequired(true);
		return textArea;
	}

	private TextField<String> campoDocumentoCredor() {
		TextField<String> textField = new TextField<String>("cnpjCpf");
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> campoNomeCredor() {
		TextField<String> textField = new TextField<String>("razaoSocial");
		textField.setRequired(true);
		return textField;
	}
	
	private Component campoSituacao() {
		List<String> status = Arrays.asList(new String[] { "Ativo", "Não Ativo" });
		return new RadioChoice<String>("ativo", new PropertyModel<String>(this, "situacao"), status).setRequired(true);
	}

	public Filiado getFiliado() {
		return filiado;
	}

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}	
	
	@Override
	protected IModel<Filiado> getModel() {
		return new CompoundPropertyModel<Filiado>(filiado);
	}
}
