package br.com.ieptbto.cra.page.relatorio;

import java.io.Serializable;
import java.math.BigDecimal;

import org.joda.time.LocalDate;

/**
 * @author Thasso Araújo
 *
 */
public class TituloFiliadoJRDataSource implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;
	private String numeroTitulo;
	private LocalDate dataEmissao;
	private String pracaProtesto;
	private String protocolo;
	private String nomeDevedor;
	private BigDecimal valorTitulo;
	private LocalDate dataConfirmacao;
	private LocalDate dataSitucao;
	private String situacaoTituloConvenio;
	private String filiado;

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public LocalDate getDataEmissao() {
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

	public LocalDate getDataConfirmacao() {
		return dataConfirmacao;
	}

	public LocalDate getDataSitucao() {
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

	public void setDataEmissao(LocalDate dataEmissao) {
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

	public void setDataConfirmacao(LocalDate dataConfirmacao) {
		this.dataConfirmacao = dataConfirmacao;
	}

	public void setDataSitucao(LocalDate dataSitucao) {
		this.dataSitucao = dataSitucao;
	}

	public void setSituacaoTituloConvenio(String situacaoTituloConvenio) {
		this.situacaoTituloConvenio = situacaoTituloConvenio;
	}

	public void setFiliado(String filiado) {
		this.filiado = filiado;
	}

}
