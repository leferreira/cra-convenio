package br.com.ieptbto.cra.page.login;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

@SuppressWarnings("serial")
public class LoginForm extends BaseForm<Usuario> {

	@SpringBean
	private UsuarioMediator usuarioMediator;

	public LoginForm(String id, IModel<Usuario> model) {
		super(id, model);
	}

	public LoginForm(String id, Usuario userModel) {
		this(id, new CompoundPropertyModel<Usuario>(userModel));
	}

	@Override
	public void onSubmit() {

		try {
			Usuario usuario = usuarioMediator.autenticarConvenio(getModelObject().getLogin(), getModelObject().getSenha());

			if (usuario != null) {
				getSession().setUser(usuario);
				setResponsePage(getApp().getHomePage());
			}
		} catch (Exception ex) {
			transError(ex.getMessage());
		}
	}
}
