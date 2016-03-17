package br.com.ieptbto.cra.page.usuario;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class ListaUsuarioFiliadoPage extends BasePage<UsuarioFiliado> {

	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;

	private UsuarioFiliado usuario;

	public ListaUsuarioFiliadoPage() {
		carregarCampos();
	}

	public ListaUsuarioFiliadoPage(String mensagem) {
		carregarCampos();
		info(mensagem);
	}

	@SuppressWarnings("rawtypes")
	private void carregarCampos() {
		usuario = new UsuarioFiliado();
		add(new Link("botaoNovo") {

			public void onClick() {
				setResponsePage(new IncluirUsuarioFiliadoPage());
			}
		});
		add(carregarListaUsuario());
	}

	@SuppressWarnings("rawtypes")
	private ListView<UsuarioFiliado> carregarListaUsuario() {
		return new ListView<UsuarioFiliado>("listViewUsuario", usuarioFiliadoMediator.buscarUsuariosDoConvenio(getUser())) {

			@Override
			protected void populateItem(ListItem<UsuarioFiliado> item) {
				final UsuarioFiliado usuarioLista = UsuarioFiliado.class.cast(item.getModelObject());

				Link linkAlterar = new Link("linkAlterar") {

					public void onClick() {
						setResponsePage(new IncluirUsuarioFiliadoPage(usuarioLista));
					}
				};
				linkAlterar.add(new Label("nomeUsuario", usuarioLista.getUsuario().getNome()));
				item.add(linkAlterar);

				item.add(new Label("loginUsuario", usuarioLista.getUsuario().getLogin()));
				item.add(new Label("emailUsuario", usuarioLista.getUsuario().getEmail()));
				item.add(new Label("contato", usuarioLista.getUsuario().getContato()));
				item.add(new Label("instituicaoUsuario", usuarioLista.getFiliado().getRazaoSocial()));
				if (usuarioLista.getUsuario().isStatus()) {
					item.add(new Label("status", "Sim"));
				} else {
					item.add(new Label("status", "Não"));
				}
			}
		};
	}

	@Override
	protected IModel<UsuarioFiliado> getModel() {
		return new CompoundPropertyModel<UsuarioFiliado>(usuario);
	}
}
