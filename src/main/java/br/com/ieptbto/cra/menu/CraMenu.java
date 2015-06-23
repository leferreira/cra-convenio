package br.com.ieptbto.cra.menu;

import org.apache.wicket.markup.html.panel.Panel;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 */
public class CraMenu extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Usuario usuario;

	public CraMenu(String id, Usuario usuario) {
		super(id);
		this.usuario = usuario;
		Menu menu = new Menu("CraMenu");
		adicionarMenuLateral(menu);
		// adicionarMenuAgenda(menu);
		// adicionarMenuClinico(menu);
		// adicionarMenuRelatorio(menu);

		add(menu);
	}

	private void adicionarMenuLateral(Menu menu) {
		// String[] rolesIncluir = { CraRoles.ADMIN, CraRoles.SUPER,
		// CraRoles.USER };
		String[] rolesAdmin = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER };
		String[] rolesUser = { CraRoles.USER };

		MenuItem menuLateral = menu.addItem("menuLateral", rolesUser);

		/** Menus Filiado */
		menuLateral.addItem("EntradaManual", rolesUser);
		menuLateral.addItem("ConsultarTitulosFiliado", rolesUser).setVisible(verificaPermissao(CraRoles.USER));
		menuLateral.addItem("EnviarTitulosPendentes", rolesUser).setVisible(verificaPermissao(CraRoles.USER));
		menuLateral.addItem("RelatorioTitulosFiliado", rolesUser).setVisible(verificaPermissao(CraRoles.USER));

		/** Menus Convenio */
		menuLateral.addItem("ConsultarTitulosConvenio", rolesAdmin).setVisible(verificaPermissao(CraRoles.ADMIN));
		menuLateral.addItem("RelatorioTitulosConvenio", rolesAdmin).setVisible(verificaPermissao(CraRoles.ADMIN));

		/** Menus Aministracao */
		MenuItem menuadmin = menuLateral.addItem("adminConvenio", rolesAdmin);
		menuadmin.setVisible(verificaPermissao(CraRoles.ADMIN));
		menuadmin.addItem("UsuariosFiliadoPage", rolesAdmin);
		menuadmin.addItem("FiliadosPage", rolesAdmin);
	}

	private boolean verificaPermissao(String permissao) {
		for (String role : usuario.getGrupoUsuario().getRoles()) {
			if (permissao.equals(role)) {
				return true;
			}
		}
		return false;
	}
}
