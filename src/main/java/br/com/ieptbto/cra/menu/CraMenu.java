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

		add(menu);
	}

	private void adicionarMenuLateral(Menu menu) {
		String[] rolesAdmin = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER };
		String[] rolesUser = { CraRoles.USER };

		MenuItem menuLateral = menu.addItem("menuLateral", rolesUser);

		UsuarioFiliado user = usuarioFiliado.buscarUsuarioFiliado(usuario);
		/** Menus Titulos Filiado */
		MenuItem menuFiliado = menuLateral.addItem("menuFiliado", rolesUser);
		menuFiliado.setVisible(verificaPermissao(user, "filiado"));
		menuFiliado.addItem("EntradaManual", rolesUser);
		menuFiliado.addItem("EnviarTitulosPendentes", rolesUser);
		menuFiliado.addItem("RelatorioTitulos", rolesUser);
		menuFiliado.addItem("RelatorioTItulosEnviados", rolesUser);
		menuFiliado.addItem("BuscarTitulos", rolesUser);
		menuFiliado.addItem("SolicitarDesistenciaCancelamentoEmpresa", rolesUser);

		/** Menus Titulos Convenio */
		MenuItem menuConvenio = menuLateral.addItem("menuConvenio", rolesAdmin);
		menuConvenio.setVisible(verificaPermissao(user, "convenio"));
		menuConvenio.addItem("BuscarTitulos", rolesAdmin);
		menuConvenio.addItem("RelatorioTitulos", rolesAdmin);
		menuConvenio.addItem("EmpresasConvenioPage", rolesAdmin);
		menuConvenio.addItem("UsuariosEmpresaConvenioPage", rolesAdmin);
	}

	private boolean verificaPermissao(UsuarioFiliado user, String chave) {

		if (user == null && chave.equals("convenio")) {
			return true;
		} else if (user != null && chave.equals("filiado")) {
			return true;
		}
		return false;
	}
}
