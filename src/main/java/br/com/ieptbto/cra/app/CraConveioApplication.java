/**
 * 
 */
package br.com.ieptbto.cra.app;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioAnonimo;
import br.com.ieptbto.cra.menu.CraMenu;
import br.com.ieptbto.cra.page.arquivo.BuscarArquivoPage;
import br.com.ieptbto.cra.page.arquivo.EnviarArquivoPage;
import br.com.ieptbto.cra.page.arrecadacaoRecebimentoEmpresa.RetornoRecebimentoEmpresaPage;
import br.com.ieptbto.cra.page.base.AbstractWebPage;
import br.com.ieptbto.cra.page.base.NotFoundPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao.EnviarSolicitacaoDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao.ListaTitulosDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao.SolicitarDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.filiado.IncluirFiliadoPage;
import br.com.ieptbto.cra.page.filiado.ListaFiliadoPage;
import br.com.ieptbto.cra.page.home.HomePage;
import br.com.ieptbto.cra.page.layoutPersonalizado.EnviarArquivoEmpresaPage;
import br.com.ieptbto.cra.page.login.LoginPage;
import br.com.ieptbto.cra.page.relatorio.convenio.RelatorioTitulosConvenioPage;
import br.com.ieptbto.cra.page.relatorio.filiado.RelatorioTItulosEnviadosFiliadoPage;
import br.com.ieptbto.cra.page.relatorio.filiado.RelatorioTitulosFiliadoPage;
import br.com.ieptbto.cra.page.titulo.BuscarTitulosPage;
import br.com.ieptbto.cra.page.titulo.EnviarTitulosPage;
import br.com.ieptbto.cra.page.titulo.entrada.EntradaManualPage;
import br.com.ieptbto.cra.page.usuario.IncluirUsuarioFiliadoPage;
import br.com.ieptbto.cra.page.usuario.ListaUsuarioFiliadoPage;
import br.com.ieptbto.cra.security.ISecureApplication;
import br.com.ieptbto.cra.security.UserRoleAuthorizationStrategy;
import br.com.ieptbto.cra.security.UserRolesAuthorizer;
import br.com.ieptbto.cra.security.UserSession;
import br.com.ieptbto.cra.util.CargaInicialPage;
import br.com.ieptbto.cra.util.DataUtil;
import org.apache.wicket.*;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.joda.time.DateTimeZone;

import java.util.Locale;

/**
 * @author Lefer
 *
 */
public class CraConveioApplication extends WebApplication implements ISecureApplication, IWebApplication {

	public CraConveioApplication() {
	}

	@Override
	public void init() {
		super.init();
		initSpring();
		initConfig();
		initAtributoDeDatas();
		montaPaginas();

	}

	// start page
	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	// page for auth
	@Override
	public Class<? extends Page> getLoginPage() {
		return LoginPage.class;
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new UserSession<Usuario>(request, new UsuarioAnonimo());
	}

	protected void initSpring() {
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
	}

	private void initConfig() {
		getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
 
		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

		getRequestCycleSettings().setTimeout(Duration.minutes(10));

		// don't throw exceptions for missing translations
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		getApplicationSettings().setPageExpiredErrorPage(NotFoundPage.class);
		getApplicationSettings().setAccessDeniedPage(NotFoundPage.class);

		// customized auth strategy
		getSecuritySettings().setAuthorizationStrategy(new UserRoleAuthorizationStrategy(new UserRolesAuthorizer()));

		// make markup friendly as in deployment-mode
		getMarkupSettings().setStripWicketTags(true);

		if (isDevelopmentMode()) {
			// enable ajax debug etc.
			getDebugSettings().setDevelopmentUtilitiesEnabled(true);
			getDebugSettings().setAjaxDebugModeEnabled(true);
			System.out.println(RuntimeConfigurationType.DEVELOPMENT);
		}
	}

	private void montaPaginas() {
		mountPage("LoginPage", LoginPage.class);
		mountPage("HomePage", HomePage.class);
		mountPage("CargaInicial", CargaInicialPage.class);
		mountPage("EmpresasConvenioPage", ListaFiliadoPage.class);
		mountPage("IncluirEmpresaConvenioPage", IncluirFiliadoPage.class);
		mountPage("UsuariosEmpresaConvenioPage", ListaUsuarioFiliadoPage.class);
		mountPage("IncluirUsuarioEmpresaConvenioPage", IncluirUsuarioFiliadoPage.class);
		mountPage("BuscarTitulos", BuscarTitulosPage.class);
		mountPage("RelatorioTitulosFiliado", RelatorioTitulosFiliadoPage.class);
		mountPage("RelatorioTitulosConvenio", RelatorioTitulosConvenioPage.class);
		mountPage("EntradaManual", EntradaManualPage.class);
		mountPage("EnviarTitulosPendentes", EnviarTitulosPage.class);
		mountPage("SolicitarDesistenciaCancelamentoEmpresa", SolicitarDesistenciaCancelamentoPage.class);
		mountPage("ListaTitulosDesistenciaCancelamento", ListaTitulosDesistenciaCancelamentoPage.class);
		mountPage("EnviarSolicitacaoDesistenciaCancelamento", EnviarSolicitacaoDesistenciaCancelamentoPage.class);
		mountPage("RelatorioTitulosEnviadosFiliado", RelatorioTItulosEnviadosFiliadoPage.class);
		mountPage("EnviarArquivoEmpresa", EnviarArquivoEmpresaPage.class);
		mountPage("EnviarArquivo", EnviarArquivoPage.class);
		mountPage("RetornoRecebimentoEmpresa", RetornoRecebimentoEmpresaPage.class);
		mountPage("BuscarArquivo", BuscarArquivoPage.class);
	}

	/**
	 * Configura as data para a aplicação.
	 */
	private void initAtributoDeDatas() {
		Locale.setDefault(DataUtil.LOCALE);
		DateTimeZone.setDefault(DataUtil.ZONE);
	}

	public boolean isDevelopmentMode() {
		return (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT);
	}

	@Override
	public Component createMenuSistema(AbstractWebPage<?> page, String containerId, Usuario usuario) {
		return new CraMenu("menu", usuario);
	}

	@Override
	public String getTituloSistema(AbstractWebPage<?> page) {
		return "CRA - Central de Remessa de Arquivos";
	}

	/**
	 * Metodo utilitario para obter a aplicacao corrente.
	 */
	public static CraConveioApplication get() {
		return (CraConveioApplication) Application.get();
	}

}
