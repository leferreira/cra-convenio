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
		String[] rolesIncluir = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER };
		String[] rolesPesquisar = { CraRoles.USER };

		MenuItem menuLateral = menu.addItem("menuLateral", rolesPesquisar);

		/** Menus titulos */
		menuLateral.addItem("EntradaManual", rolesPesquisar); 

		menuLateral.addItem("ConsultarTitulosFiliado", rolesPesquisar);
		menuLateral.addItem("ConsultarTitulosConvenio", rolesPesquisar);
		menuLateral.addItem("EnviarTitulosPendentes", rolesPesquisar);

		/** Menus Relatorios */
		menuLateral.addItem("RelatorioTitulosFiliado", rolesIncluir);
		menuLateral.addItem("RelatorioTitulosConvenio", rolesIncluir);

		/** Menus Aministracao */
		MenuItem menuadmin = menuLateral.addItem("adminConvenio", rolesIncluir);
		menuadmin.setVisible(verificaPermissao());
		menuadmin.addItem("UsuariosFiliadoPage", rolesIncluir);
		menuadmin.addItem("FiliadosPage", rolesIncluir);

	}

	private boolean verificaPermissao() {
		for (String role : usuario.getGrupoUsuario().getRoles()) {
			if (CraRoles.ADMIN.equals(role) || CraRoles.SUPER.equals(role)) {
				return true;
			}
		}
		return false;
	}
}
