package br.com.ieptbto.cra.page.relatorio;

import java.io.Serializable;

import br.com.ieptbto.cra.entidade.Filiado;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioConvenioBean implements Serializable {

    /***/
    private static final long serialVersionUID = 1L;
    private String nomeFiliado;
    private String municipio;
    private String uf;
    private Integer pendentes;
    private Integer processo;
    private Integer finalizados;
    private Integer total;

    public String getNomeFiliado() {
	return nomeFiliado;
    }

    public String getMunicipio() {
	return municipio;
    }

    public String getUf() {
	return uf;
    }

    public Integer getPendentes() {
	return pendentes;
    }

    public Integer getProcesso() {
	return processo;
    }

    public Integer getFinalizados() {
	return finalizados;
    }

    public Integer getTotal() {
	return total;
    }

    public void setNomeFiliado(String nomeFiliado) {
	this.nomeFiliado = nomeFiliado;
    }

    public void setMunicipio(String municipio) {
	this.municipio = municipio;
    }

    public void setUf(String uf) {
	this.uf = uf;
    }

    public void setPendentes(Integer pendentes) {
	this.pendentes = pendentes;
    }

    public void setProcesso(Integer processo) {
	this.processo = processo;
    }

    public void setFinalizados(Integer finalizados) {
	this.finalizados = finalizados;
    }

    public void setTotal(Integer total) {
	this.total = total;
    }

    public void parseFiliado(Filiado filiado, Integer titulosPendentes, Integer titulosEmProcesso, Integer titulosFinalizados, Integer soma) {
	this.setNomeFiliado(filiado.getRazaoSocial());
	this.setMunicipio(filiado.getMunicipio().getNomeMunicipio());
	this.setUf(filiado.getUf());
	this.setPendentes(titulosPendentes);
	this.setProcesso(titulosEmProcesso);
	this.setFinalizados(titulosFinalizados);
	this.total = soma;
    }
}