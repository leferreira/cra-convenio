package br.com.ieptbto.cra.page.titulo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.ArquivoOcorrenciaBean;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoConvenioPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class HistoricoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	private TituloRemessa tituloRemessa;
	private TituloFiliado tituloFiliado;
	private List<ArquivoOcorrenciaBean> arquivosOcorrencias; 
	
	public HistoricoPage(TituloFiliado titulo){
		this.tituloFiliado = titulo;
		this.tituloRemessa = tituloFiliadoMediator.buscarTituloDoConvenioNaCra(titulo);

		adicionarLabels();
		carregarArquivosOcorrencias();
	}
	
	public HistoricoPage(TituloRemessa tituloLista) {
		this.tituloFiliado = new TituloFiliado();
		this.tituloRemessa = tituloLista;
		
		adicionarLabels();
		carregarArquivosOcorrencias();
	}

	private void adicionarLabels() {
		add(numeroProtocoloCartorio());
    	add(dataProtocolo());
    	add(codigoCartorio());
    	add(dataOcorrencia());
    	add(irregularidade());
    	add(codigoMunicipio());
    	add(pracaProtesto());
    	add(cartorio());
    	add(status());
    	add(nomeSacadorVendedor());
    	add(documentoSacador());
    	add(ufSacadorVendedor());
    	add(cepSacadorVendedor());
    	add(cidadeSacadorVendedor());
    	add(enderecoSacadorVendedor());
    	add(complementoRegistro());
    	add(nomeDevedor());
    	add(documentoDevedor());
    	add(ufDevedor());
    	add(cepDevedor());
    	add(cidadeDevedor());
    	add(enderecoDevedor());
    	add(numeroTitulo());
    	add(portador());
    	add(agencia());
    	add(nossoNumero());
    	add(especieTitulo());
    	add(dataEmissaoTitulo());
    	add(dataVencimentoTitulo());
    	add(valorTitulo());
    	add(saldoTitulo());
    	add(valorCustaCartorio());
    	add(valorCustasCartorioDistribuidor());
    	add(valorDemaisDespesas());
    	add(valorGravacaoEletronica());
    	add(nomeCedenteFavorecido());
    	add(agenciaCodigoCedente());
	}
	
	private void carregarArquivosOcorrencias() {
		if (getTituloRemessa() != null) {
			List<Historico> historicoArquivos = getTituloRemessa().getHistoricos();
			
			for (Historico historico : historicoArquivos) {
				ArquivoOcorrenciaBean novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToHistorico(historico);
				
				getArquivosOcorrencias().add(novaOcorrencia);
				if (historico.getRemessa().getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO) ||
						historico.getRemessa().getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
					if (historico.getRemessa().getArquivoGeradoProBanco().getId() != historico.getRemessa().getArquivo().getId()) {
						ArquivoOcorrenciaBean novaOcorrenciaArquivo = new ArquivoOcorrenciaBean();
						novaOcorrenciaArquivo.parseToArquivoGerado(historico.getRemessa().getArquivoGeradoProBanco());
						
						getArquivosOcorrencias().add(novaOcorrenciaArquivo);
					}
				}
			}
			
			if (getTituloRemessa().getPedidoDesistencia() != null) {
				ArquivoOcorrenciaBean novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToDesistenciaProtesto(getTituloRemessa().getPedidoDesistencia().getDesistenciaProtesto());
				
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}
		add(listaArquivoOcorrenciaBean());
	}
	
	private ListView<ArquivoOcorrenciaBean> listaArquivoOcorrenciaBean(){
		return new ListView<ArquivoOcorrenciaBean>("divListaHistorico", getArquivosOcorrencias()) {

			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<ArquivoOcorrenciaBean> item) {
				final ArquivoOcorrenciaBean arquivoOcorrenciaBean = item.getModelObject();
				
				if (arquivoOcorrenciaBean.getRemessa() != null) {
					Link<Remessa> linkArquivo = new Link<Remessa>("linkArquivo") {
						
						/***/
						private static final long serialVersionUID = 1L;
						
						@Override
						public void onClick() {
							setResponsePage(new TitulosArquivoConvenioPage(arquivoOcorrenciaBean.getRemessa().getArquivo()));  
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivoOcorrenciaBean.getRemessa().getArquivo().getNomeArquivo()));
					item.add(linkArquivo);
					item.add(new Label("acao", " enviado em "));
				} 
				
				if (arquivoOcorrenciaBean.getArquivoGerado() != null) {
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {
						
						/***/
						private static final long serialVersionUID = 1L;
						
						@Override
						public void onClick() {
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivoOcorrenciaBean.getArquivoGerado()));
					item.add(linkArquivo);
					item.add(new Label("acao", " liberado em "));
				}
				
				if (arquivoOcorrenciaBean.getDesistenciaProtesto() != null) {
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {
						
						/***/
						private static final long serialVersionUID = 1L;
						
						@Override
						public void onClick() {
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivoOcorrenciaBean.getDesistenciaProtesto().getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
					item.add(linkArquivo);
					item.add(new Label("acao", " enviado em "));
				}
				
				item.add(new Label("dataOcorrencia", arquivoOcorrenciaBean.getDataHora()));
				item.add(new Label("usuarioAcao", arquivoOcorrenciaBean.getNomeUsuario()));
			}
		};
	}
	
	private Label codigoCartorio() {
		String codigoCartorio = StringUtils.EMPTY;
		if (getTituloRemessa() != null) {
			codigoCartorio = getTituloRemessa().getRemessa().getInstituicaoDestino().getCodigoCartorio();
		}
		return new Label("codigoCartorio", new Model<String>(codigoCartorio));
	}

    private Label dataOcorrencia() {
    	LocalDate dataOcorrencia = new LocalDate(getTituloFiliado().getDataEntrada());
    	if (getTituloRemessa() != null) {
    		dataOcorrencia = getTituloRemessa().getRemessa().getCabecalho().getDataMovimento();
    	
			if (getTituloRemessa().getConfirmacao() != null) {
				dataOcorrencia = getTituloRemessa().getConfirmacao().getDataOcorrencia();
			}
			if (getTituloRemessa().getRetorno() != null) {
				dataOcorrencia = getTituloRemessa().getRetorno().getDataOcorrencia();
			}
    	}
		return new Label("dataOcorrencia", DataUtil.localDateToString(dataOcorrencia));
	}
    
    private Label irregularidade() {
    	String irregularidade = StringUtils.EMPTY;
//    	if (getTituloRemessa().getConfirmacao() != null) {
//    		if (getTituloRemessa().getConfirmacao().getCodigoIrregularidade() != null && 
//    				getTituloRemessa().getConfirmacao().getCodigoIrregularidade() != "  ") {
//    		}
//    	}
		return new Label("irregularidade", new Model<String>(irregularidade));
	}
    
    private Label complementoRegistro(){
    	String complementoRegistro = StringUtils.EMPTY;
    	if (getTituloFiliado().getAlinea() != null) {
    		new Label("complementoRegistro", getTituloFiliado().getAlinea());
    	}
    	if (getTituloRemessa() != null) {
    		if (getTituloRemessa().getComplementoRegistro() != null) {
    			if (getTituloRemessa().getComplementoRegistro().length() == 2){
    				complementoRegistro = getTituloRemessa().getComplementoRegistro();
    			}
    		}
    	}
    	return new Label("complementoRegistro", complementoRegistro);
    }
    
    private Label codigoMunicipio() {
		return new Label("codigoMunicipio", new Model<String>(getTituloFiliado().getPracaProtesto().getCodigoIBGE()));
	}
    
    private Label valorGravacaoEletronica() {
    	BigDecimal valorGravacao = BigDecimal.ZERO;
    	if (getTituloRemessa() != null) {
	    	if (getTituloRemessa().getConfirmacao() != null) {
	    		if (getTituloRemessa().getConfirmacao().getTipoOcorrencia() != null) {
	    			String tipoOcorrencia = getTituloRemessa().getConfirmacao().getTipoOcorrencia().trim();
	    			if (!tipoOcorrencia.equals(StringUtils.EMPTY) || tipoOcorrencia.equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
	    				valorGravacao = getTituloRemessa().getRemessa().getInstituicaoOrigem().getValorConfirmacao();
	    			}
	    		}
	    	}
    	}
    	return new LabelValorMonetario<BigDecimal>("valorGravacaoEletronica", valorGravacao);
    }
    
	private Label numeroProtocoloCartorio() {
		String numeroProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa() != null) {
			if (getTituloRemessa().getConfirmacao() != null){
				numeroProtocolo = getTituloRemessa().getConfirmacao().getNumeroProtocoloCartorio();
			} 
		}
		return new Label("numeroProtocoloCartorio", new Model<String>(numeroProtocolo));
	}

	private Label dataProtocolo() {	
		String dataProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa() != null) {
			if (getTituloRemessa().getConfirmacao() != null){
				dataProtocolo = DataUtil.localDateToString(getTituloRemessa().getConfirmacao().getDataProtocolo()); 
			}
		}
		return new Label("dataProtocolo", new Model<String>(dataProtocolo));
	}

	private Label cartorio(){
		String cartorio = StringUtils.EMPTY;
		if (getTituloRemessa() != null) {
			cartorio = getTituloRemessa().getRemessa().getInstituicaoDestino().getNomeFantasia().toUpperCase();
		}
		return new Label("cartorio", new Model<String>(cartorio));
	}

	private Label pracaProtesto() {
		return new Label("pracaProtesto", new Model<String>(getTituloFiliado().getPracaProtesto().getNomeMunicipio().toUpperCase()));
	}

	 private Label status(){ 
		 String status = getTituloFiliado().getSituacaoTituloConvenio().getSituacao().toUpperCase();
		 if (getTituloRemessa() != null) {
			 status = getTituloRemessa().getSituacaoTitulo().toUpperCase();
		 }
		return new Label("situacaoTitulo", new Model<String>(status));
	 }

	 private Label numeroTitulo() {
		return new Label("numeroTitulo", new Model<String>(getTituloFiliado().getNumeroTitulo()));
	 }

 	private Label portador(){
		return new Label("portador", new Model<String>(getUser().getInstituicao().getNomeFantasia().toUpperCase()));
 	}

	 private Label agencia(){
		return new Label("agencia", new Model<String>(getUser().getInstituicao().getAgenciaCentralizadora()));
	 }
	
	private Label nossoNumero() {
		String nossoNumero = StringUtils.EMPTY;
		if (getTituloRemessa() != null) {
			nossoNumero = getTituloRemessa().getNossoNumero();
		}
		return new Label("nossoNumero", new Model<String>(nossoNumero));
	}
	
	private Label especieTitulo() {
		return new Label("especieTitulo", new Model<String>(getTituloFiliado().getEspecieTitulo().getConstante()));
	}
	
	private Label dataEmissaoTitulo() {
		return new Label("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(getTituloFiliado().getDataEmissao())));
	}
	
	private Label dataVencimentoTitulo() {
		return new Label("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(getTituloFiliado().getDataVencimento())));
	}
	
	public Label valorTitulo() {
		Label textField = new Label("valorTitulo", new Model<String>("R$ " + getTituloFiliado().getValorTitulo().toString()));
		return textField;
	}
	
	public Label saldoTitulo() {
		Label textField = new Label("saldoTitulo", new Model<String>("R$ " + getTituloFiliado().getValorSaldoTitulo().toString()));
		return textField;
	}
	
	public Label valorCustaCartorio() {
		BigDecimal valorCustaCartorio = BigDecimal.ZERO;
		if (getTituloRemessa() != null) {
			if (getTituloRemessa().getConfirmacao() != null) {
				valorCustaCartorio = getTituloRemessa().getConfirmacao().getValorCustaCartorio();
			}
			if (getTituloRemessa().getRetorno() != null) {
				valorCustaCartorio = getTituloRemessa().getRetorno().getValorCustaCartorio();
			}
		}
		return new LabelValorMonetario<BigDecimal>("valorCustaCartorio", valorCustaCartorio);
	}
	
	public Label valorCustasCartorioDistribuidor() {
		BigDecimal valorCustasCartorioDistribuidor = BigDecimal.ZERO;
		if (getTituloRemessa() != null) {
			if (getTituloRemessa().getConfirmacao() != null) {
				valorCustasCartorioDistribuidor = getTituloRemessa().getConfirmacao().getValorCustasCartorioDistribuidor();
			}
			if (getTituloRemessa().getRetorno() != null) {
				valorCustasCartorioDistribuidor = getTituloRemessa().getRetorno().getValorCustasCartorioDistribuidor();
			}
		}
		return new LabelValorMonetario<BigDecimal>("valorCustasCartorioDistribuidor", valorCustasCartorioDistribuidor);
	}
	
	public Label valorDemaisDespesas() {
		BigDecimal valorDemaisDespesas = BigDecimal.ZERO;
		if (getTituloRemessa() != null) {
			if (getTituloRemessa().getConfirmacao() != null) {
				valorDemaisDespesas = getTituloRemessa().getConfirmacao().getValorDemaisDespesas();
			}
			if (getTituloRemessa().getRetorno() != null) {
				valorDemaisDespesas = getTituloRemessa().getRetorno().getValorDemaisDespesas();
			}
		}
		return new LabelValorMonetario<BigDecimal>("valorDemaisDespesas", valorDemaisDespesas);
	}
	
	public Label nomeCedenteFavorecido() {
		return new Label("nomeCedenteFavorecido", new Model<String>(getTituloFiliado().getFiliado().getRazaoSocial().toUpperCase()));
	}
	
	public Label agenciaCodigoCedente() {
		String agencia = StringUtils.EMPTY;
		if (getTituloRemessa() != null) {
			agencia = getTituloRemessa().getAgenciaCodigoCedente();
		}
		return new Label("agenciaCodigoCedente", new Model<String>(agencia));
	}
	
	private Label nomeSacadorVendedor() {
		return new Label("nomeSacadorVendedor", new Model<String>(getTituloFiliado().getFiliado().getRazaoSocial().toUpperCase()));
	}

	private Label documentoSacador() {
		return new Label("documentoSacador", new Model<String>(getTituloFiliado().getFiliado().getCnpjCpf()));
	}

	private Label ufSacadorVendedor() {
		Label textField = new Label("ufSacadorVendedor", new Model<String>(getTituloFiliado().getFiliado().getUf()));
		return textField;
	}

	private Label cepSacadorVendedor() {
		Label textField = new Label("cepSacadorVendedor", new Model<String>(getTituloFiliado().getFiliado().getCep()));
		return textField;
	}

	private Label cidadeSacadorVendedor() {
		Label textField = new Label("cidadeSacadorVendedor", new Model<String>(getTituloFiliado().getFiliado().getMunicipio().getNomeMunicipio()));
		return textField;
	}

	private Label enderecoSacadorVendedor() {
		Label textField = new Label("enderecoSacadorVendedor", new Model<String>(getTituloFiliado().getFiliado().getEndereco()));
		return textField;
	}

	private Label nomeDevedor() {
		Label textField = new Label("nomeDevedor", new Model<String>(getTituloFiliado().getNomeDevedor()));
		return textField;
	}

	private Label documentoDevedor() {
		String documentoDevedor = getTituloFiliado().getCpfCnpj();
		if (getTituloRemessa() != null) {
			documentoDevedor = getTituloRemessa().getNumeroIdentificacaoDevedor();
		}
		return new Label("documentoDevedor", new Model<String>(documentoDevedor));
	}

	private Label ufDevedor() {
		Label textField = new Label("ufDevedor", new Model<String>(getTituloFiliado().getUfDevedor()));
		return textField;
	}

	private Label cepDevedor() {
		Label textField = new Label("cepDevedor", new Model<String>(getTituloFiliado().getCepDevedor()));
		return textField;
	}

	private Label cidadeDevedor() {
		Label textField = new Label("cidadeDevedor", new Model<String>(getTituloFiliado().getCidadeDevedor()));
		return textField;
	}

	private Label enderecoDevedor() {
		Label textField = new Label("enderecoDevedor", new Model<String>(getTituloFiliado().getEnderecoDevedor()));
		return textField;
	}
	
	public List<ArquivoOcorrenciaBean> getArquivosOcorrencias() {
		if (arquivosOcorrencias == null) {
			arquivosOcorrencias = new ArrayList<ArquivoOcorrenciaBean>();
		}
		return arquivosOcorrencias;
	}
	
	public TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}
	
	public TituloFiliado getTituloFiliado() {
		return tituloFiliado;
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}