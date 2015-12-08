package br.com.ieptbto.cra.page.arquivo;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamentoConvenio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.StatusSolicitacao;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class TituloDesistenciaCancelamentoSolicitadoPage extends BasePage<SolicitacaoDesistenciaCancelamentoConvenio> {

	/***/
	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(TituloDesistenciaCancelamentoSolicitadoPage.class);

	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	private SolicitacaoDesistenciaCancelamentoConvenio solicitacaoDesistenciaCancelamento;
	private TituloFiliado tituloFiliado;
	private TituloRemessa tituloRemessa;
	
	public TituloDesistenciaCancelamentoSolicitadoPage(TituloFiliado titulo, TituloRemessa tituloRemessa) {
		this.solicitacaoDesistenciaCancelamento = new SolicitacaoDesistenciaCancelamentoConvenio();
		this.tituloFiliado = titulo;
		this.tituloRemessa = tituloRemessa;
		
		enviarSolicitacao();
		carregarDadosTitulo();
	}
	
	private void enviarSolicitacao() {
		Form<SolicitacaoDesistenciaCancelamentoConvenio> form = new Form<SolicitacaoDesistenciaCancelamentoConvenio>("form", getModel()) {
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				SolicitacaoDesistenciaCancelamentoConvenio solicitacao = getModelObject();
				solicitacao.setTituloFiliado(getTituloFiliado());
				solicitacao.setDataSolicitacao(new LocalDate());
				solicitacao.setStatusSolicitacao(StatusSolicitacao.ENVIADA); 
				
				try {
					if (getTituloRemessa() != null) {
						if (getTituloRemessa().getConfirmacao() != null) {
							if (getTituloRemessa().getConfirmacao().getNumeroProtocoloCartorio() != null) {
								String numeroProtocolo = getTituloRemessa().getConfirmacao().getNumeroProtocoloCartorio();
								if (!numeroProtocolo.trim().equals(StringUtils.EMPTY) || !numeroProtocolo.trim().equals(0)) {
									throw new InfraException("O título já foi devolvido pelo cartório !");
								}
							}
						} else {
							throw new InfraException("O título não contém protocolo! Aguarde o processamento pelo cartório !");
						}
					}
					if (getTituloFiliado() != null) {
						if (getTituloFiliado().getSolicitacaoDesistenciaCancelamento() != null) {
							throw new InfraException("O título já contém um pedido de Desistência/Cancelamento !");
						}
					}
					
					tituloFiliadoMediator.enviarSolicitacaoDesistenciaCancelamento(solicitacao);
					info("Solicitação de desistência ou cancelamento enviada com sucesso !");
				
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					info("Não foi possível enviar a solicitação de desistência ou cancelamento ! Entre em contato com o IEPTB !");
				}
			}
		};
		form.add(labelTipoSolicitacao());
		add(form);
	}

	private Label labelTipoSolicitacao() {
		String tipoSolicitacao = "DESISTÊNCIA";
		if (getTituloRemessa() != null) {
			if (getTituloRemessa().getRetorno() != null) {
				if (TipoOcorrencia.getTipoOcorrencia(getTituloRemessa().getRetorno().getTipoOcorrencia()).equals(TipoOcorrencia.PROTESTADO)) {
					tipoSolicitacao = "CANCELAMENTO";
				}
			}
		}
		return new Label("tipoSolicitacao", tipoSolicitacao);
	}

	private void carregarDadosTitulo() {
		add(numeroProtocoloCartorio());
    	add(dataProtocolo());
    	add(dataOcorrencia());
    	add(pracaProtesto());
    	add(status());
    	add(nomeDevedor());
    	add(documentoDevedor());
    	add(numeroTitulo());
    	add(especieTitulo());
    	add(dataEmissaoTitulo());
    	add(dataVencimentoTitulo());
    	add(valorTitulo());
    	add(saldoTitulo());
    	add(valorGravacaoEletronica());
	}
	
    private Label dataOcorrencia() {
    	LocalDate dataOcorrencia = getTituloFiliado().getDataEntrada();
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
	
	private Label nomeDevedor() {
		Label textField = new Label("nomeDevedor", new Model<String>(getTituloFiliado().getNomeDevedor()));
		return textField;
	}

	private Label documentoDevedor() {
		String documentoDevedor = getTituloFiliado().getDocumentoDevedor();
		if (getTituloRemessa() != null) {
			documentoDevedor = getTituloRemessa().getNumeroIdentificacaoDevedor();
		}
		return new Label("documentoDevedor", new Model<String>(documentoDevedor));
	}

	public TituloFiliado getTituloFiliado() {
		return tituloFiliado;
	}
	
	public TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}
	
	@Override
	protected IModel<SolicitacaoDesistenciaCancelamentoConvenio> getModel() {
		return new CompoundPropertyModel<SolicitacaoDesistenciaCancelamentoConvenio>(solicitacaoDesistenciaCancelamento);
	}
}
