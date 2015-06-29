package br.com.ieptbto.cra.menu;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
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
	
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliado;

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
		UsuarioFiliado user = usuarioFiliado.buscarUsuarioFiliado(usuario);
		String[] rolesAdmin = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER };
		String[] rolesUser = { CraRoles.USER };

		MenuItem menuLateral = menu.addItem("menuLateral", rolesUser);

		/** Menus Titulos Filiado */
		MenuItem menuTitulosFiliado = menuLateral.addItem("titulosFiliado", rolesUser);
		menuTitulosFiliado.setVisible(verificaPermissao(user , "filiado"));
		menuTitulosFiliado.addItem("EntradaManual", rolesUser);
		menuTitulosFiliado.addItem("ConsultarTitulosFiliado", rolesUser);
		menuTitulosFiliado.addItem("EnviarTitulosPendentes", rolesUser);

		/** Menus Titulos Convenio */
		MenuItem menuTitulosConvenio = menuLateral.addItem("titulosConvenio", rolesAdmin);
		menuTitulosConvenio.setVisible(verificaPermissao(user, "convenio"));
		menuTitulosConvenio.addItem("ConsultarTitulosConvenio", rolesAdmin);

		/** Menus Relatorio Filiado */
		MenuItem menuRelatorioFiliado = menuLateral.addItem("relatorioFiliado", rolesUser);
		menuRelatorioFiliado.setVisible(verificaPermissao(user, "filiado"));
		menuRelatorioFiliado.addItem("RelatorioTitulosFiliado", rolesUser);

		/** Menus Relatorio Convenio */
		MenuItem menuRelatorioConvenio = menuLateral.addItem("relatorioConvenio", rolesAdmin);
		menuRelatorioConvenio.setVisible(verificaPermissao(user, "convenio"));
		menuRelatorioConvenio.addItem("RelatorioTitulosConvenio", rolesAdmin);

		/** Menus Aministracao Convenio */
		MenuItem menuadmin = menuLateral.addItem("adminConvenio", rolesAdmin);
		menuadmin.setVisible(verificaPermissao(user, "convenio"));
		menuadmin.addItem("UsuariosFiliadoPage", rolesAdmin);
		menuadmin.addItem("FiliadosPage", rolesAdmin);
	}

	private boolean verificaPermissao(UsuarioFiliado user, String chave) {
		
		if (user == null && chave.equals("convenio")) {
			return true;
		} else if (user != null && chave.equals("filiado")){
			return true;
		}
		return false;
	}
}
