package br.com.ieptbto.cra.page.relatorio.filiado;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.component.DateTextField;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.TituloFiliado;
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
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class RelatorioTItulosEnviadosFiliadoPage extends BasePage<TituloFiliado> {

	private static final long serialVersionUID = 1L;
	private TituloConvenioBean tituloConvenioBean;
	private Filiado filiado;

	public RelatorioTItulosEnviadosFiliadoPage() {
		this.tituloConvenioBean = new TituloConvenioBean();
		this.filiado = getFiliadoPorUsuario();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formRelatorioTitulosEnviados();
	}

	private void formRelatorioTitulosEnviados() {
		Form<TituloConvenioBean> form = new Form<TituloConvenioBean>("form", new CompoundPropertyModel<TituloConvenioBean>(tituloConvenioBean)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Connection connection = null;
				JasperPrint jasperPrint = null;
				HashMap<String, Object> parametros = new HashMap<String, Object>();

				LocalDate dataRelatorio = new LocalDate(getModelObject().getDataInicio());
				try {
					if (dataRelatorio.isAfter(new LocalDate())) {
						throw new InfraException("A data de envio para emitir o relatório não pode ser superior a data atual!");
					}
					Class.forName("org.postgresql.Driver");
					connection = DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra", "postgres", "@dminB3g1n");
//					connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/nova_cra", "postgres", "1234");

					parametros.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_CONVENIO_PATH);
					parametros.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_CONVENIO_PATH + "ieptb.gif")));
					parametros.put("DATA_ENVIO", dataRelatorio.toDate());
					parametros.put("RAZAO_SOCIAL_FILIADO", filiado.getRazaoSocial());
					parametros.put("FILIADO_ID", filiado.getId());

					JasperReport jasperReport = JasperCompileManager.compileReport(
							getClass().getResourceAsStream(ConfiguracaoBase.RELATORIOS_CONVENIO_PATH + "RelatorioTituloFiliadoEnviado.jrxml"));
					jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

					if (jasperPrint.getPages().isEmpty()) {
						throw new InfraException("Não foram encontrados títulos enviados na data de hoje. Verifique se ao enviar, os títulos foram selecionados corretamente...");
					}

					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
							"CRA_RELATORIO_TITULOS_ENVIADOS" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));
				} catch (InfraException ex) {
					logger.info(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
					logger.info(e.getMessage());
				}
			}
		};
		form.add(dateTextFieldDataEnvio());
		add(form);
	}

	private DateTextField dateTextFieldDataEnvio() {
		DateTextField dataTextField = new DateTextField("dataInicio");
		dataTextField.setLabel(new Model<String>("Data de Envio"));
		dataTextField.add(new AttributeAppender("class", "form-control date"));
		dataTextField.setRequired(true);
		return dataTextField;
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return null;
	}
}
