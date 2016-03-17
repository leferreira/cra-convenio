package br.com.ieptbto.cra.page.titulo;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioEnvioTituloFiliadoPage extends BasePage<Filiado> {

    /***/
    private static final long serialVersionUID = 1L;

    private Filiado filiado;

    public RelatorioEnvioTituloFiliadoPage(Filiado filiado, String message) {
	this.filiado = filiado;

	info(message);
	carregarComponentes();
    }

    private void carregarComponentes() {
	add(new Label("pageName", "ENVIAR TÍTULOS PENDENTES"));
	add(linkRelatorioTitulosFiliadoEnviados());
    }

    private Link<Retorno> linkRelatorioTitulosFiliadoEnviados() {
	Link<Retorno> linkRelatorioRetornoLiberado = new Link<Retorno>("relatorioEnvioPendentes") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		Connection connection = null;
		JasperPrint jasperPrint = null;
		HashMap<String, Object> parametros = new HashMap<String, Object>();

		try {
		    Class.forName("org.postgresql.Driver");
		    connection = DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra", "postgres", "@dminB3g1n");

		    parametros.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_PATH);
		    parametros.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_PATH
			    + "ieptb.gif")));
		    parametros.put("DATA_ENVIO", new LocalDate().toDate());
		    parametros.put("RAZAO_SOCIAL_FILIADO", getFiliado().getRazaoSocial());
		    parametros.put("FILIADO_ID", getFiliado().getId());

		    String urlJasper = "../../relatorio/RelatorioTituloFiliadoEnviado.jrxml";
		    JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(urlJasper));
		    jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

		    File pdf = File.createTempFile("report", ".pdf");
		    JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
		    IResourceStream resourceStream = new FileResourceStream(pdf);
		    getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_TITULOS_ENVIADOS"
			    + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));

		} catch (InfraException ex) {
		    ex.printStackTrace();
		    error(ex.getMessage());
		} catch (Exception e) {
		    error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
		    e.printStackTrace();
		}
	    }
	};
	return linkRelatorioRetornoLiberado;
    }

    public Filiado getFiliado() {
	return filiado;
    }

    @Override
    protected IModel<Filiado> getModel() {
	return new CompoundPropertyModel<Filiado>(filiado);
    }
}