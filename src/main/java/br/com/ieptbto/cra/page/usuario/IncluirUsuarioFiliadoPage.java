package br.com.ieptbto.cra.page.usuario;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.GrupoUsuarioMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.EmailValidator;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class IncluirUsuarioFiliadoPage extends BasePage<Usuario> {

	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private DropDownChoice<Filiado> comboFiliado;

	@SpringBean
	FiliadoMediator filiadoMediator;
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	@SpringBean
	GrupoUsuarioMediator grupoUsuarioMediator;

	public IncluirUsuarioFiliadoPage() {
		this.usuario = new Usuario();
		carregarFormulario();
	}

	public IncluirUsuarioFiliadoPage(Usuario usuario) {
		this.usuario = usuario;
		carregarFormulario();
	}

	private void carregarFormulario() {
		Form<Usuario> form = new Form<Usuario>("form", getModel()){
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				UsuarioFiliado usuarioFiliado = new UsuarioFiliado();
				
				try {
					usuario.setInstituicao(getUser().getInstituicao());
					usuario.setGrupoUsuario(grupoUsuarioMediator.buscarGrupo("Usuário"));
					usuarioFiliado.setFiliado(comboFiliado.getModelObject());
					usuarioFiliado.setUsuario(usuario);
					
					if (usuario.getId() != 0) {
						usuarioFiliadoMediator.alterarUsuarioFiliado(usuario, usuarioFiliado);
					} else 
						usuarioFiliadoMediator.salvarUsuarioFiliado(usuario, usuarioFiliado);
					
					setResponsePage(new ListaUsuarioFiliadoPage("Os dados foram salvos com sucesso na CRA ! "));
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
		TextField<String> fieldNome = new TextField<String>("nome");
		fieldNome.setLabel(new Model<String>("Nome"));
		fieldNome.setRequired(true);
		return fieldNome;
	}

	private TextField<String> campoLogin() {
		TextField<String> textField = new TextField<String>("login");
		textField.setLabel(new Model<String>("Login"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> campoSenha() {
		PasswordTextField senha = new PasswordTextField("senha");
		senha.setLabel(new Model<String>("Senha"));
		return senha;
	}

	private TextField<String> campoConfirmarSenha() {
		PasswordTextField confirmarSenha = new PasswordTextField("confirmarSenha");
		confirmarSenha.setLabel(new Model<String>("Confirmar Senha"));
		return confirmarSenha;
	}

	private TextField<String> campoEmail() {
		TextField<String> textField = new TextField<String>("email");
		textField.setLabel(new Model<String>("E-mail"));
		textField.add(new EmailValidator());
		return textField;
	}

	private TextField<String> campoContato() {
		TextField<String> textField = new TextField<String>("contato");
		textField.setLabel(new Model<String>("Contato"));
		return textField;
	}

	private Component campoStatus() {
		List<String> status = Arrays.asList(new String[] { "Ativo", "Não Ativo" });
		return new RadioChoice<String>("situacao", status);
	}

	private DropDownChoice<Filiado> comboFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		comboFiliado = new DropDownChoice<Filiado>("filiado", new Model<Filiado>(),filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
		comboFiliado.setLabel(new Model<String>("Filiado"));
		comboFiliado.setRequired(true);
		return comboFiliado;		
	}
	
	@Override
	protected IModel<Usuario> getModel() {
		return new CompoundPropertyModel<Usuario>(usuario);
	}
}
