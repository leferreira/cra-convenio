package br.com.ieptbto.cra.relatorioConvenio;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Thasso Aráujo
 *
 */
public class RelatorioUtil implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	private JasperReport jasperReport;
	private HashMap<String, Object> params;
	private LocalDate dataInicio;
	private LocalDate dataFim;

	private void initParams() throws IOException {
		this.params = new HashMap<String, Object>();
		this.params.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_CONVENIO_PATH);
		this.params.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_CONVENIO_PATH + "ieptb.gif")));
		this.params.put("DATA_INICIO", new Date(getDataInicio().toDate().getTime()));
		this.params.put("DATA_FIM", new Date(getDataFim().toDate().getTime()));
	}

	public JasperPrint gerarRelatorioTitulosConvenioPorSituacao(Usuario usuario, SituacaoTituloRelatorio situacaoTitulosRelatorio, Filiado filiado,
			Municipio municipio, LocalDate dataInicio, LocalDate dataFim) throws JRException {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;

		try {
			initParams();

			if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.GERAL)) {
				if (municipio != null && filiado != null) {
					params.put("PRACA_PROTESTO", municipio.getNomeMunicipio().toUpperCase());
					params.put("AGENCIA_CODIGO_CEDENTE", filiado.getCodigoFiliado());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioGeral.jrxml"));
				}
				if (municipio == null && filiado != null) {
					params.put("AGENCIA_CODIGO_CEDENTE", filiado.getCodigoFiliado());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioGeralPorFiliado.jrxml"));
				}
				if (municipio != null && filiado == null) {
					throw new InfraException(
							"Os relatórios onde é informado somente o município como parâmetro, estão temporáriamente indisponíveis!");
					// params.put("PRACA_PROTESTO",
					// municipio.getNomeMunicipio().toUpperCase());
					// jasperReport =
					// JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioGeralPorMunicipio.jrxml"));
				}
			}
			if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.PAGOS)) {
				if (municipio != null && filiado != null) {
					params.put("PRACA_PROTESTO", municipio.getNomeMunicipio().toUpperCase());
					params.put("AGENCIA_CODIGO_CEDENTE", filiado.getCodigoFiliado());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioPagos.jrxml"));
				}
				if (municipio == null && filiado != null) {
					params.put("AGENCIA_CODIGO_CEDENTE", filiado.getCodigoFiliado());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioPagosPorFiliado.jrxml"));
				}
				if (municipio != null && filiado == null) {
					throw new InfraException(
							"Os relatórios onde é informado somente o município como parâmetro, estão temporáriamente indisponíveis!");
					// params.put("PRACA_PROTESTO",
					// municipio.getNomeMunicipio().toUpperCase());
					// jasperReport =
					// JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioPagosPorMunicipio.jrxml"));
				}
			}
			if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.PROTESTADOS)) {
				if (municipio != null && filiado != null) {
					params.put("PRACA_PROTESTO", municipio.getNomeMunicipio().toUpperCase());
					params.put("AGENCIA_CODIGO_CEDENTE", filiado.getCodigoFiliado());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioProtestados.jrxml"));
				}
				if (municipio == null && filiado != null) {
					params.put("AGENCIA_CODIGO_CEDENTE", filiado.getCodigoFiliado());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioProtestadosPorFiliado.jrxml"));
				}
				if (municipio != null && filiado == null) {
					throw new InfraException(
							"Os relatórios onde é informado somente o município como parâmetro, estão temporáriamente indisponíveis!");
					// params.put("PRACA_PROTESTO",
					// municipio.getNomeMunicipio().toUpperCase());
					// jasperReport =
					// JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioProtestadosPorMunicipio.jrxml"));
				}
			}
			if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS)) {
				if (municipio != null && filiado != null) {
					params.put("PRACA_PROTESTO", municipio.getNomeMunicipio().toUpperCase());
					params.put("AGENCIA_CODIGO_CEDENTE", filiado.getCodigoFiliado());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioRetiradosDevolvidos.jrxml"));
				}
				if (municipio == null && filiado != null) {
					params.put("AGENCIA_CODIGO_CEDENTE", filiado.getCodigoFiliado());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioRetiradosPorFiliado.jrxml"));
				}
				if (municipio != null && filiado == null) {
					throw new InfraException(
							"Os relatórios onde é informado somente o município como parâmetro, estão temporáriamente indisponíveis!");
					// params.put("PRACA_PROTESTO",
					// municipio.getNomeMunicipio().toUpperCase());
					// jasperReport =
					// JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioRetiradosDevolvidosPorMunicipio.jrxml"));
				}
			}
		} catch (IOException e) {
			throw new InfraException("Não foi possível fazer o relatório! Entre em contato com o IEPTB...");
		} catch (InfraException ex) {
			throw new InfraException(ex.getMessage());
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			 return
			 DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra",
			 "postgres", "@dminB3g1n");
//			return DriverManager.getConnection("jdbc:postgresql://localhost:5432/nova_cra", "postgres", "1234");
		} catch (Exception e) {
			throw new InfraException("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
		}
	}

	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public LocalDate getDataFim() {
		return dataFim;
	}
}
