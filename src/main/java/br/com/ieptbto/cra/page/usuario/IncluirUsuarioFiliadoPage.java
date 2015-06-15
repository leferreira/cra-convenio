package br.com.ieptbto.cra.page.usuario;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.EmailValidator;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class IncluirUsuarioFiliadoPage extends BasePage<UsuarioFiliado> {

	private static final long serialVersionUID = 1L;
	private UsuarioFiliado usuarioFiliado;
	private DropDownChoice<Filiado> comboFiliado;

	@SpringBean
	FiliadoMediator filiadoMediator;
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;

	public IncluirUsuarioFiliadoPage() {
		this.usuarioFiliado = new UsuarioFiliado();
		carregarFormulario();
	}

	public IncluirUsuarioFiliadoPage(UsuarioFiliado usuario) {
		this.usuarioFiliado = usuario;
		carregarFormulario();
	}

	private void carregarFormulario() {
		Form<UsuarioFiliado> form = new Form<UsuarioFiliado>("form"){
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				UsuarioFiliado novoUsuario = new UsuarioFiliado();
				
				try {
					novoUsuario.setUsuario(getModelObject().getUsuario());
					novoUsuario.setFiliado(comboFiliado.getModelObject());
					
					usuarioFiliadoMediator.salvarUsuarioFiliado(novoUsuario);
					setResponsePage(new ListaUsuarioFiliadoPage("O novo usuario ["+ novoUsuario.getUsuario().getNome() +"] foi salvo com sucesso ! "));
				} catch (Exception e) {
					System.out.println(e.getMessage());
					error("Não foi possível cadastrar o novo usuário ! Entre em contato com a CRA !");
				}
			}
		};
		form.add(campoNome());
		form.add(campoLogin());
		form.add(campoSenha());
		form.add(campoEmail());
		form.add(campoConfirmarSenha());
		form.add(campoContato());
		form.add(campoStatus());
		form.add(comboFiliado());
		add(form);
	}

	private TextField<String> campoNome() {
		TextField<String> textField = new TextField<String>("usuario.nome");
		textField.setLabel(new Model<String>("Nome"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoLogin() {
		TextField<String> textField = new TextField<String>("usuario.login");
		textField.setLabel(new Model<String>("Login"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoSenha() {
		PasswordTextField senha = new PasswordTextField("usuario.senha");
		senha.setLabel(new Model<String>("Senha"));
		return senha;
	}

	private TextField<String> campoConfirmarSenha() {
		PasswordTextField confirmarSenha = new PasswordTextField("usuario.confirmarSenha");
		confirmarSenha.setLabel(new Model<String>("Confirmar Senha"));
		return confirmarSenha;
	}

	private TextField<String> campoEmail() {
		TextField<String> textField = new TextField<String>("usuario.email");
		textField.setLabel(new Model<String>("E-mail"));
		textField.add(new EmailValidator());
		return textField;
	}

	private TextField<String> campoContato() {
		TextField<String> textField = new TextField<String>("usuario.contato");
		textField.setLabel(new Model<String>("Contato"));
		return textField;
	}

	private Component campoStatus() {
		List<String> status = Arrays.asList(new String[] { "Ativo", "Não Ativo" });
		return new RadioChoice<String>("usuario.situacao", status);
	}

	private DropDownChoice<Filiado> comboFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("nomeCredor");
		comboFiliado = new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
		comboFiliado.setLabel(new Model<String>("Filiado"));
		comboFiliado.setRequired(true);
		return comboFiliado;		
	}
	
	@Override
	protected IModel<UsuarioFiliado> getModel() {
		return new CompoundPropertyModel<UsuarioFiliado>(usuarioFiliado);
	}
}
