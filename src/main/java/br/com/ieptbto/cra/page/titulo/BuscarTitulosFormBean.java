package br.com.ieptbto.cra.page.titulo;

import java.io.Serializable;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;

public class BuscarTitulosFormBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	private String numeroTitulo;
	private String nomeDevedor;
	private String documentoDevedor;
	private LocalDate dataInicio;
	private Instituicao instiuicaoCartorio;
	private Filiado filiado;

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public Instituicao getInstiuicaoCartorio() {
		return instiuicaoCartorio;
	}

	public Filiado getFiliado() {
		return filiado;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
	}

	public void setDataInicio(LocalDate dataInicio) {
		this.dataInicio = dataInicio;
	}

	public void setInstiuicaoCartorio(Instituicao instiuicaoCartorio) {
		this.instiuicaoCartorio = instiuicaoCartorio;
	}

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}

}