package br.com.ieptbto.cra.relatorio;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
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

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private HashMap<String, Object> params;
    private Filiado filiado;
    private Municipio municipio;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    private void initParams() throws IOException {
	this.params = new HashMap<String, Object>();
	this.params.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_PATH);
	this.params.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_PATH + "ieptb.gif")));
	this.params.put("DATA_INICIO", getDataInicio().toDate());
	this.params.put("DATA_FIM", getDataFim().toDate());
    }

    public JasperPrint gerarRelatorioTitulosPorSituacaoFiliado(SituacaoTituloRelatorio situacaoTitulosRelatorio, Filiado filiado, Municipio municipio, LocalDate dataInicio, LocalDate dataFim) throws JRException {
	this.filiado = filiado;
	this.municipio = municipio;
	this.dataInicio = dataInicio;
	this.dataFim = dataFim;

	JasperReport jasperReport = null;
	try {
	    initParams();
	    this.params.put("AGENCIA_CODIGO_CEDENTE", getFiliado().getCodigoFiliado());
	    this.params.put("RAZAO_SOCIAL_FILIADO", getFiliado().getRazaoSocial());
	    if (getMunicipio() != null) {
		this.params.put("MUNICIPIO_ID", getMunicipio().getId());
		this.params.put("NOME_MUNICIPIO", getMunicipio().getNomeMunicipio());
		this.params.put("PRACA_PROTESTO", RemoverAcentosUtil.removeAcentos(getMunicipio().getNomeMunicipio().toUpperCase()));
	    }

	    if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.GERAL)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioGeralFiliado.jrxml"));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioGeralFiliadoPorMunicipio.jrxml"));
		}

	    } else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.SEM_CONFIRMACAO)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}

	    } else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.COM_CONFIRMACAO)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}

	    } else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.SEM_RETORNO)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}

	    } else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.COM_RETORNO)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}

	    } else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.PAGOS)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}

	    } else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.PROTESTADOS)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}

	    } else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}

	    } else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.DESISTÊNCIA_DE_PROTESTO)) {
		if (getMunicipio() == null) {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else {
		    jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}

	    }
	} catch (IOException e) {
	    throw new InfraException("Não foi possível localizar o arquivo passado como parâmetro!");
	}
	return JasperFillManager.fillReport(jasperReport, params, getConnection());
    }

    private Connection getConnection() {
	try {
	    Class.forName("org.postgresql.Driver");
	    return DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra", "postgres", "@dminB3g1n");
	    // return
	    // DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra",
	    // "postgres", "1234");
	} catch (Exception e) {
	    throw new InfraException("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
	}
    }

    public Municipio getMunicipio() {
	return municipio;
    }

    public Filiado getFiliado() {
	return filiado;
    }

    public LocalDate getDataInicio() {
	return dataInicio;
    }

    public LocalDate getDataFim() {
	return dataFim;
    }
}
