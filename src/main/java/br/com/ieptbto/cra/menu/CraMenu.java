package br.com.ieptbto.cra.menu;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 */
public class CraMenu extends Panel {

	@SpringBean
	private UsuarioFiliadoMediator usuarioFiliado;

	private static final long serialVersionUID = 1L;
	private static final String NIVEL_CONVENIO = "convenio";
	private static final String NIVEL_FILIADO = "filiado";
	private Usuario usuario;

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
		menuFiliado.setVisible(verificaPermissao(user, NIVEL_FILIADO));
		menuFiliado.addItem("EntradaManual", rolesUser);
		menuFiliado.addItem("EnviarTitulosPendentes", rolesUser);
		menuFiliado.addItem("BuscarTitulos", rolesUser);
		menuFiliado.addItem("SolicitarDesistenciaCancelamentoEmpresa", rolesUser);
		menuFiliado.addItem("RelatorioTitulosFiliado", rolesUser);
		menuFiliado.addItem("RelatorioTitulosEnviadosFiliado", rolesUser);

		/** Menus Titulos Convenio */
		MenuItem menuConvenio = menuLateral.addItem("menuConvenio", rolesAdmin);
		menuConvenio.setVisible(verificaPermissao(user, NIVEL_CONVENIO));
		menuConvenio.addItem("EnviarArquivoEmpresa", isEnvioLayoutPersonalizado(), rolesAdmin);
		menuConvenio.addItem("EnviarArquivo", isEnvioCraNacional(), rolesAdmin);
		menuConvenio.addItem("RetornoRecebimentoEmpresa", isLayoutRetornoRecebimentoEmpresa(), rolesAdmin);
		menuConvenio.addItem("BuscarArquivo", (isLayoutRetornoRecebimentoEmpresa() == true) ? false : true, rolesAdmin);
		menuConvenio.addItem("SolicitarDesistenciaCancelamentoEmpresa", rolesAdmin);
		menuConvenio.addItem("BuscarTitulos", rolesAdmin);
		menuConvenio.addItem("RelatorioTitulosConvenio", rolesAdmin);
		
		MenuItem menuAdminConvenio = menuLateral.addItem("menuAdminConvenio", rolesAdmin);
		menuAdminConvenio.setVisible(isAdministrarFiliados(user));
		menuAdminConvenio.addItem("EmpresasConvenioPage", rolesAdmin);
		menuAdminConvenio.addItem("UsuariosEmpresaConvenioPage", rolesAdmin);
	}

	private boolean isLayoutRetornoRecebimentoEmpresa() {
		if (usuario != null) {
			return (usuario.getInstituicao().getLayoutRetornoRecebimentoEmpresa()) ? true : false;
		}
		return false;
	}

	private boolean isEnvioLayoutPersonalizado() {
		if (usuario != null) {
			return (LayoutPadraoXML.ENTRADA_MANUAL_LAYOUT_PERSONALIZADO == usuario.getInstituicao().getLayoutPadraoXML()) ? true : false;
		}
		return false;
	}
	
	private boolean isEnvioCraNacional() {
		if (usuario != null) {
			return (LayoutPadraoXML.CRA_NACIONAL == usuario.getInstituicao().getLayoutPadraoXML()) ? true : false;
		}
		return false;
	}

	private boolean isAdministrarFiliados(UsuarioFiliado user) {
		if (usuario != null && user == null) {
			LayoutPadraoXML layout = usuario.getInstituicao().getLayoutPadraoXML();
			if (LayoutPadraoXML.ENTRADA_MANUAL_LAYOUT_PERSONALIZADO == layout) {
				return usuario.getInstituicao().getAdministrarEmpresasFiliadas();
			}
		}
		return false;
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
