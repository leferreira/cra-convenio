package br.com.ieptbto.cra.page.relatorio;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class TituloFiliadoJRDataSource implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;
	private String numeroTitulo;
	private String dataEmissao;
	private String pracaProtesto;
	private String protocolo;
	private String nomeDevedor;
	private BigDecimal valorTitulo;
	private BigDecimal valorSaldoTitulo;
	private String dataConfirmacao;
	private String dataSitucao;
	private String situacaoTituloConvenio;
	private String filiado;

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public String getDataEmissao() {
		return dataEmissao;
	}

	public String getPracaProtesto() {
		return pracaProtesto;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	public String getDataConfirmacao() {
		return dataConfirmacao;
	}

	public String getDataSitucao() {
		return dataSitucao;
	}

	public String getSituacaoTituloConvenio() {
		return situacaoTituloConvenio;
	}

	public String getFiliado() {
		return filiado;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public void setPracaProtesto(String pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setValorTitulo(BigDecimal valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setDataConfirmacao(String dataConfirmacao) {
		this.dataConfirmacao = dataConfirmacao;
	}

	public void setDataSitucao(String dataSitucao) {
		this.dataSitucao = dataSitucao;
	}

	public void setSituacaoTituloConvenio(String situacaoTituloConvenio) {
		this.situacaoTituloConvenio = situacaoTituloConvenio;
	}

	public void setFiliado(String filiado) {
		this.filiado = filiado;
	}

	public void parseTituloFiliado(TituloFiliado tituloFiliado, TituloRemessa tituloRemessa) {
		this.setNumeroTitulo(tituloFiliado.getNumeroTitulo());
		this.setDataEmissao(DataUtil.localDateToString(tituloFiliado.getDataEmissao()));
		this.setPracaProtesto(tituloFiliado.getPracaProtesto().getNomeMunicipio().toUpperCase());
		this.setNomeDevedor(tituloFiliado.getNomeDevedor());
		this.setValorTitulo(tituloFiliado.getValorTitulo());
		this.setValorSaldoTitulo(tituloFiliado.getValorSaldoTitulo());
		this.setFiliado(tituloFiliado.getFiliado().getRazaoSocial());
		this.setSituacaoTituloConvenio(tituloFiliado.getSituacaoTituloConvenio().getSituacao().toUpperCase());
		
		this.setProtocolo(StringUtils.EMPTY);
		this.setDataConfirmacao(StringUtils.EMPTY);
		this.setDataSitucao(DataUtil.localDateToString(tituloFiliado.getDataEnvioCRA()));
		if (tituloRemessa != null) {
			if (tituloRemessa.getConfirmacao() != null) {
				this.setProtocolo(tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio());
				this.setDataConfirmacao(DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getDataRecebimento()));
				this.setDataSitucao(DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataOcorrencia()));
			}
			if (tituloRemessa.getRetorno() != null) {
				this.setDataSitucao(DataUtil.localDateToString(tituloFiliado.getDataEnvioCRA()));
			}
			this.setSituacaoTituloConvenio(tituloRemessa.getSituacaoTitulo());
		}
	}

	public BigDecimal getValorSaldoTitulo() {
		return valorSaldoTitulo;
	}

	public void setValorSaldoTitulo(BigDecimal valorSaldoTitulo) {
		this.valorSaldoTitulo = valorSaldoTitulo;
	}
}
