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

		/** Menus Titulos Filiado */
		MenuItem menuTitulosFiliado = menuLateral.addItem("titulosFiliado", rolesUser);
		menuTitulosFiliado.setVisible(verificaPermissao("filiado" ,CraRoles.USER));
		menuTitulosFiliado.addItem("EntradaManual", rolesUser);
		menuTitulosFiliado.addItem("ConsultarTitulosFiliado", rolesUser);
		menuTitulosFiliado.addItem("EnviarTitulosPendentes", rolesUser);

		/** Menus Titulos Convenio */
		MenuItem menuTitulosConvenio = menuLateral.addItem("titulosConvenio", rolesAdmin);
		menuTitulosConvenio.setVisible(verificaPermissao("convenio" ,CraRoles.ADMIN));
		menuTitulosConvenio.addItem("ConsultarTitulosConvenio", rolesAdmin);

		/** Menus Relatorio Filiado */
		MenuItem menuRelatorioFiliado = menuLateral.addItem("relatorioFiliado", rolesUser);
		menuRelatorioFiliado.setVisible(verificaPermissao("filiado" ,CraRoles.USER));
		menuRelatorioFiliado.addItem("RelatorioTitulosFiliado", rolesUser);

		/** Menus Relatorio Convenio */
		MenuItem menuRelatorioConvenio = menuLateral.addItem("relatorioConvenio", rolesAdmin);
		menuRelatorioConvenio.setVisible(verificaPermissao("convenio" ,CraRoles.ADMIN));
		menuRelatorioConvenio.addItem("RelatorioTitulosConvenio", rolesAdmin);

		/** Menus Aministracao Convenio */
		MenuItem menuadmin = menuLateral.addItem("adminConvenio", rolesAdmin);
		menuadmin.setVisible(verificaPermissao("convenio" ,CraRoles.ADMIN));
		menuadmin.addItem("UsuariosFiliadoPage", rolesAdmin);
		menuadmin.addItem("FiliadosPage", rolesAdmin);
	}

	private boolean verificaPermissao(String tipoUsuario ,String permissao) {
		for (String role : usuario.getGrupoUsuario().getRoles()) {
			if (permissao.equals(role)) {
				if (permissao.equals(CraRoles.ADMIN) && tipoUsuario.equals("convenio")) {
					return true;
				} else if (permissao.equals(CraRoles.ADMIN) && tipoUsuario.equals("filiado")){
					return false;
				} 
			}
		}
		return false;
	}
}
