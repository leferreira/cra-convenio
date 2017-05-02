package br.com.ieptbto.cra.report;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.BaseMediator;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import net.sf.jasperreports.engine.*;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Thasso Aráujo
 *
 */
public class RelatorioUtil implements Serializable {

    protected static final Logger logger = Logger.getLogger(BaseMediator.class);

	private static final long serialVersionUID = 1L;
	private JasperReport jasperReport;
	private HashMap<String, Object> params;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Municipio municipio;
	private Filiado filiado;
	private SituacaoTituloRelatorio situacaoTitulosRelatorio;

	private void initParams() throws IOException {
		this.params = new HashMap<String, Object>();
		this.params.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_CONVENIO_PATH);
		this.params.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_CONVENIO_PATH + "ieptb.gif")));
		this.params.put("DATA_INICIO", new Date(getDataInicio().toDate().getTime()));
		this.params.put("DATA_FIM", new Date(getDataFim().toDate().getTime()));
	}

	public JasperPrint gerarRelatorioTitulosConvenioPorSituacao(Usuario usuario, TituloConvenioBean bean) throws JRException {
		this.dataInicio = new LocalDate(bean.getDataInicio());
		this.dataFim = new LocalDate(bean.getDataFim());
		this.municipio = bean.getMunicipio();
		this.filiado = bean.getFiliado();
		this.situacaoTitulosRelatorio = bean.getSituacaoTitulosRelatorio();
		
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
				} else if (municipio == null && filiado == null && !usuario.getInstituicao().getAdministrarEmpresasFiliadas()) {
                    params.put("CODIGO_PORTADOR", usuario.getInstituicao().getCodigoCompensacao());
                    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioGeralConvenio.jrxml"));
                }
				if (municipio != null && filiado == null) {
					throw new InfraException("Os relatórios onde é informado somente o município como parâmetro, estão temporáriamente indisponíveis!");
//					params.put("PRACA_PROTESTO", municipio.getNomeMunicipio().toUpperCase());
//					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioGeralPorMunicipio.jrxml"));
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
                } else if (municipio == null && filiado == null && !usuario.getInstituicao().getAdministrarEmpresasFiliadas()) {
                    params.put("CODIGO_PORTADOR", usuario.getInstituicao().getCodigoCompensacao());
                    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioPagosConvenio.jrxml"));
                }
				if (municipio != null && filiado == null) {
					throw new InfraException("Os relatórios onde é informado somente o município como parâmetro, estão temporáriamente indisponíveis!");
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
                } else if (municipio == null && filiado == null && !usuario.getInstituicao().getAdministrarEmpresasFiliadas()) {
                    params.put("CODIGO_PORTADOR", usuario.getInstituicao().getCodigoCompensacao());
                    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioProtestadosConvenio.jrxml"));
                }
				if (municipio != null && filiado == null) {
					throw new InfraException("Os relatórios onde é informado somente o município como parâmetro, estão temporáriamente indisponíveis!");
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
                } else if (municipio == null && filiado == null && !usuario.getInstituicao().getAdministrarEmpresasFiliadas()) {
                    params.put("CODIGO_PORTADOR", usuario.getInstituicao().getCodigoCompensacao());
                    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioRetiradosConvenio.jrxml"));
                }
				if (municipio != null && filiado == null) {
					throw new InfraException("Os relatórios onde é informado somente o município como parâmetro, estão temporáriamente indisponíveis!");
				}
			}
		} catch (IOException e) {
		    logger.info(e.getMessage(), e);
			throw new InfraException("Não foi possível fazer o relatório! Entre em contato com o IEPTB...");
		} catch (InfraException ex) {
			throw new InfraException(ex.getMessage());
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			 return DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra", "postgres", "@dminB3g1n");
//			 	return DriverManager.getConnection("jdbc:postgresql://localhost:5432/nova_cra", "postgres", "1234");
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
