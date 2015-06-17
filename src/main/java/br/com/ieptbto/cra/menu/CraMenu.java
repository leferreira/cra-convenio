package br.com.ieptbto.cra.menu;

import org.apache.wicket.markup.html.panel.Panel;

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

	public CraMenu(String id) {
		super(id);
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
		/**Menus arquivos*/
		
		/**Menus titulos*/
		menuLateral.addItem("EntradaManual", rolesPesquisar);
		menuLateral.addItem("MonitorarTitulos", rolesPesquisar);
		menuLateral.addItem("EnviarTitulosPendentes", rolesPesquisar);

		/**Menus Relatorios*/
		menuLateral.addItem("RelatorioArquivosTitulos", rolesIncluir);

	    /**Menus Aministracao*/
	    menuLateral.addItem("UsuariosFiliadoPage", rolesIncluir);
	    menuLateral.addItem("FiliadosPage", rolesIncluir);
	    
	}

	// private void adicionarMenuAgenda(Menu menu) {
	// MenuItem menuPrincipal = menu.addItem("menuAgenda");
	// menuPrincipal.addItem("atendimento");
	// menuPrincipal.addItem("pesquisarAtendimento");
	// // menuPrincipal.addItem("agendamento");
	// }
	//
	// private void adicionarMenuClinico(Menu menu) {
	// MenuItem menuPrincipal = menu.addItem("menuClinico");
	// menuPrincipal.addItem("anamnese");
	// menuPrincipal.addItem("consulta");
	// menuPrincipal.addItem("historico");
	// }
	//
	// private void adicionarMenuRelatorio(Menu menu) {
	// MenuItem menuPrincipal = menu.addItem("menuRelatorio");
	// menuPrincipal.addItem("relatorioAtendimento");
	// menuPrincipal.addItem("relatorioAtendimentoTipoServico");
	// menuPrincipal.addItem("relatorioAtendimentoTipoAtendimento");
	// }
	//
	// private void criarMenuEntidade(MenuItem menu, String nome, String[]
	// rolesIncluir, String[] rolesPesquisar) {
	// MenuItem menuPrincipal = menu.addItem("sm" + nome);
	// menuPrincipal.addItem("incluir" + nome, rolesIncluir);
	// menuPrincipal.addItem("pesquisar" + nome, rolesPesquisar);
	//
	// }

}
