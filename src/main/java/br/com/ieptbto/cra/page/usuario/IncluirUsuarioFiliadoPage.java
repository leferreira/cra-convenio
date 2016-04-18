
package br.com.ieptbto.cra.page.usuario;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
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
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.GrupoUsuarioMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.EmailValidator;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirUsuarioFiliadoPage extends BasePage<UsuarioFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(IncluirUsuarioFiliadoPage.class);

	@SpringBean
	FiliadoMediator filiadoMediator;
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	@SpringBean
	UsuarioMediator usuarioMediator;
	@SpringBean
	GrupoUsuarioMediator grupoUsuarioMediator;

	private UsuarioFiliado usuarioFiliado;
	private String senhaAtual;

	public IncluirUsuarioFiliadoPage() {
		this.usuarioFiliado = new UsuarioFiliado();
		adicionarComponentes();
	}

	public IncluirUsuarioFiliadoPage(UsuarioFiliado usuario) {
		this.usuarioFiliado = usuario;
		this.senhaAtual = usuario.getUsuario().getSenha();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormulario();

	}

	private void carregarFormulario() {
		Form<UsuarioFiliado> form = new Form<UsuarioFiliado>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				UsuarioFiliado usuarioFiliado = getModelObject();

				try {
					if (getModelObject().getId() != 0) {
						if (usuarioFiliado.getUsuario().getSenha() != null) {
							usuarioFiliado.getUsuario().setSenha(Usuario.cryptPass(usuarioFiliado.getUsuario().getSenha()));
						} else {
							usuarioFiliado.getUsuario().setSenha(getSenhaAtual());
						}
						usuarioFiliadoMediator.alterarUsuarioFiliado(usuarioFiliado);
						setResponsePage(new ListaUsuarioFiliadoPage("Os dados do usuário foram salvos com sucesso!"));
					} else {
						usuarioFiliado.setTermosContratoAceite(false);
						usuarioFiliado.getUsuario().setInstituicao(getUser().getInstituicao());
						usuarioFiliado.getUsuario().setGrupoUsuario(grupoUsuarioMediator.buscarGrupo("Usuário"));
						if (usuarioMediator.isSenhasIguais(usuarioFiliado.getUsuario())) {
							if (usuarioMediator.isLoginNaoExiste(usuarioFiliado.getUsuario())) {
								usuarioFiliadoMediator.salvarUsuarioFiliado(usuarioFiliado);
								setResponsePage(new ListaUsuarioFiliadoPage("Os dados do usuário foram salvos com sucesso!"));
							} else
								throw new InfraException("Usuário não criado. O login já existe!");
						} else {
							throw new InfraException("As senhas não são iguais!");
						}
					}
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
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
		TextField<String> fieldNome = new TextField<String>("usuario.nome");
		fieldNome.setLabel(new Model<String>("Nome"));
		fieldNome.setRequired(true);
		return fieldNome;
	}

	private TextField<String> campoLogin() {
		TextField<String> textField = new TextField<String>("usuario.login");
		textField.setLabel(new Model<String>("Login"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> campoSenha() {
		PasswordTextField senha = new PasswordTextField("usuario.senha");
		senha.setLabel(new Model<String>("Senha"));
		senha.setRequired(verificarExistencia());
		return senha;
	}

	private TextField<String> campoConfirmarSenha() {
		PasswordTextField confirmarSenha = new PasswordTextField("usuario.confirmarSenha");
		confirmarSenha.setLabel(new Model<String>("Confirmar Senha"));
		confirmarSenha.setRequired(false);
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
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		DropDownChoice<Filiado> comboFiliado =
				new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
		comboFiliado.setLabel(new Model<String>("Filiado"));
		comboFiliado.setRequired(true);
		return comboFiliado;
	}

	private boolean verificarExistencia() {
		if (usuarioFiliado.getId() == 0) {
			return true;
		}
		return false;
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	@Override
	protected IModel<UsuarioFiliado> getModel() {
		return new CompoundPropertyModel<UsuarioFiliado>(usuarioFiliado);
	}
}
