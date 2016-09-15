package br.com.ieptbto.cra.page.titulo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.SolicitacaoCancelamento;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HistoricoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	private RemessaMediator remessaMediator;
	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private CancelamentoProtestoMediator cancelamentoProtestoMediator;
	@SpringBean
	private AutorizacaoCancelamentoMediator autorizacaoCancelamentoMediator;

	private TituloRemessa tituloRemessa;
	private List<ArquivoOcorrenciaBean> arquivosOcorrencias;

	public HistoricoPage(TituloRemessa titulo) {
		this.tituloRemessa = titulo;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarArquivosOcorrencias();
		carregarCampos();

	}

	private void carregarArquivosOcorrencias() {
		TituloRemessa titulo = getTituloRemessa();
		ArquivoOcorrenciaBean novaOcorrencia = null;

		TituloFiliado tituloFiliado = tituloFiliadoMediator.buscarTituloFiliadoProcessadoNaCra(titulo.getNossoNumero(), titulo.getNumeroTitulo());
		if (tituloFiliado != null) {
			novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToTituloFiliado(tituloFiliado);
			getArquivosOcorrencias().add(novaOcorrencia);
		}

		novaOcorrencia = new ArquivoOcorrenciaBean();
		if (titulo.getRemessa() != null) {
			Remessa remessa = remessaMediator.carregarRemessaPorId(titulo.getRemessa());
			titulo.setRemessa(remessa);
		}
		novaOcorrencia.parseToRemessa(titulo.getRemessa());
		getArquivosOcorrencias().add(novaOcorrencia);

		if (titulo.getConfirmacao() != null) {
			Confirmacao confirmacao = tituloMediator.carregarTituloConfirmacao(titulo.getConfirmacao());
			getTituloRemessa().setConfirmacao(confirmacao);
			novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToRemessa(confirmacao.getRemessa());
			getArquivosOcorrencias().add(novaOcorrencia);
		}

		if (titulo.getRetorno() != null) {
			Retorno retorno = tituloMediator.carregarTituloRetorno(titulo.getRetorno());
			getTituloRemessa().setRetorno(retorno);
			novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToRemessa(retorno.getRemessa());
			getArquivosOcorrencias().add(novaOcorrencia);
		}

		if (titulo.getPedidosCancelamento() != null) {
			List<PedidoCancelamento> pedidoCancelamento = cancelamentoProtestoMediator.buscarPedidosCancelamentoProtestoPorTitulo(titulo);
			for (PedidoCancelamento pedido : pedidoCancelamento) {
				novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToCancelamentoProtesto(pedido.getCancelamentoProtesto());
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}

		if (titulo.getPedidosAutorizacaoCancelamento() != null) {
			List<PedidoAutorizacaoCancelamento> pedidosAC = autorizacaoCancelamentoMediator.buscarPedidosAutorizacaoCancelamentoPorTitulo(titulo);
			for (PedidoAutorizacaoCancelamento pedido : pedidosAC) {
				novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToAutorizacaoCanlamento(pedido.getAutorizacaoCancelamento());
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}

		SolicitacaoCancelamento solicitacaoCancelamento = cancelamentoProtestoMediator.buscarSolicitacaoCancelamentoPorTitulo(titulo);
		if (solicitacaoCancelamento != null) {
			novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToSolicitacaoCancelamento(solicitacaoCancelamento);
			getArquivosOcorrencias().add(novaOcorrencia);
		}

		Collections.sort(getArquivosOcorrencias());
		add(listaArquivoOcorrenciaBean());
	}

	private ListView<ArquivoOcorrenciaBean> listaArquivoOcorrenciaBean() {
		return new ListView<ArquivoOcorrenciaBean>("divListaHistorico", getArquivosOcorrencias()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ArquivoOcorrenciaBean> item) {
				final ArquivoOcorrenciaBean arquivoOcorrenciaBean = item.getModelObject();

				if (arquivoOcorrenciaBean.getTituloFiliado() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-success"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-edit")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", ""));
					link.setOutputMarkupId(true);
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", arquivoOcorrenciaBean.getMensagem()));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getRemessa() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);
					Link<Remessa> link = new Link<Remessa>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.setEnabled(false);
					link.add(new Label("conteudoLink", arquivoOcorrenciaBean.getRemessa().getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getCancelamentoProtesto() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink",
							arquivoOcorrenciaBean.getCancelamentoProtesto().getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getAutorizacaoCancelamento() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink",
							arquivoOcorrenciaBean.getAutorizacaoCancelamento().getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getSolicitacaoCancelamento() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-danger"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-times")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", ""));
					link.setOutputMarkupId(true);
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", arquivoOcorrenciaBean.getMensagem()));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				item.add(new Label("data", DataUtil.localDateToString(arquivoOcorrenciaBean.getData())));
				item.add(new Label("hora", DataUtil.localTimeToString("HH:mm:ss", arquivoOcorrenciaBean.getHora())));
				item.add(new Label("usuarioAcao", arquivoOcorrenciaBean.getNomeUsuario()));
			}
		};
	}

	private void carregarCampos() {
		add(numeroProtocoloCartorio());
		add(dataProtocolo());
		add(codigoCartorio());
		add(labelAgenciaCodigoCedente());
		add(dataOcorrencia());
		add(irregularidade());
		add(codigoMunicipio());
		add(pracaProtesto());
		add(status());
		add(nomeSacadorVendedor());
		add(documentoSacador());
		add(ufSacadorVendedor());
		add(cepSacadorVendedor());
		add(cidadeSacadorVendedor());
		add(enderecoSacadorVendedor());
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
		add(valorDemaisDespesas());
	}

	private Label codigoCartorio() {
		Integer codigoCartorio = 0;
		if (getTituloRemessa().getConfirmacao() != null) {
			codigoCartorio = getTituloRemessa().getConfirmacao().getCodigoCartorio();
		}
		if (getTituloRemessa().getRetorno() != null) {
			codigoCartorio = getTituloRemessa().getRetorno().getCodigoCartorio();
		}
		return new Label("codigoCartorio", new Model<String>(codigoCartorio.toString()));
	}

	private Label dataOcorrencia() {
		LocalDate dataOcorrencia = getTituloRemessa().getRemessa().getCabecalho().getDataMovimento();

		if (getTituloRemessa().getConfirmacao() != null) {
			dataOcorrencia = getTituloRemessa().getConfirmacao().getDataOcorrencia();
		}
		if (getTituloRemessa().getRetorno() != null) {
			dataOcorrencia = getTituloRemessa().getRetorno().getDataOcorrencia();
		}
		return new Label("dataOcorrencia", DataUtil.localDateToString(dataOcorrencia));
	}

	private Label irregularidade() {
		CodigoIrregularidade codigoIrregularidade = null;
		String irregularidade = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null) {
			irregularidade = getTituloRemessa().getConfirmacao().getCodigoIrregularidade();
		}
		if (getTituloRemessa().getRetorno() != null) {
			if (getTituloRemessa().getRetorno().getTipoOcorrencia().equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
				irregularidade = getTituloRemessa().getRetorno().getCodigoIrregularidade();
			}
		}
		codigoIrregularidade = CodigoIrregularidade.getIrregularidade(irregularidade);
		if (codigoIrregularidade == null) {
			return new Label("irregularidade", new Model<String>("00"));
		}
		return new Label("irregularidade", new Model<String>(codigoIrregularidade.getMotivo().toUpperCase()));
	}

	private Label codigoMunicipio() {
		return new Label("codigoMunicipio", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getCodigoMunicipio()));
	}

	private Label numeroProtocoloCartorio() {
		String numeroProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null) {
			numeroProtocolo = getTituloRemessa().getConfirmacao().getNumeroProtocoloCartorio();
		}
		return new Label("numeroProtocoloCartorio", new Model<String>(numeroProtocolo));
	}

	private Label dataProtocolo() {
		String dataProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null) {
			dataProtocolo = DataUtil.localDateToString(getTituloRemessa().getConfirmacao().getDataProtocolo());
		}
		return new Label("dataProtocolo", new Model<String>(dataProtocolo));
	}

	private Label pracaProtesto() {
		return new Label("pracaProtesto", new Model<String>(getTituloRemessa().getPracaProtesto()));
	}

	private Label status() {
		return new Label("situacaoTitulo", new Model<String>(getTituloRemessa().getSituacaoTitulo()));
	}

	private Label numeroTitulo() {
		return new Label("numeroTitulo", new Model<String>(getTituloRemessa().getNumeroTitulo()));
	}

	private Label labelAgenciaCodigoCedente() {
		return new Label("agenciaCodigoCedente", getTituloRemessa().getAgenciaCodigoCedente());
	}

	private Label portador() {
		return new Label("portador", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getNomePortador()));
	}

	private Label agencia() {
		return new Label("agencia", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getAgenciaCentralizadora()));
	}

	private Label nossoNumero() {
		return new Label("nossoNumero", new Model<String>(getTituloRemessa().getNossoNumero()));
	}

	private Label especieTitulo() {
		return new Label("especieTitulo", new Model<String>(getTituloRemessa().getEspecieTitulo()));
	}

	private Label dataEmissaoTitulo() {
		return new Label("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataEmissaoTitulo())));
	}

	private Label dataVencimentoTitulo() {
		return new Label("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataVencimentoTitulo())));
	}

	public Label valorTitulo() {
		Label textField = new Label("valorTitulo", new Model<String>("R$ " + getTituloRemessa().getValorTitulo().toString()));
		return textField;
	}

	public Label saldoTitulo() {
		Label textField = new Label("saldoTitulo", new Model<String>("R$ " + getTituloRemessa().getSaldoTitulo().toString()));
		return textField;
	}

	public Label valorCustaCartorio() {
		BigDecimal valorCustaCartorio = BigDecimal.ZERO;
		if (getTituloRemessa().getConfirmacao() != null) {
			valorCustaCartorio = getTituloRemessa().getConfirmacao().getValorCustaCartorio();
		}
		if (getTituloRemessa().getRetorno() != null) {
			valorCustaCartorio = getTituloRemessa().getRetorno().getValorCustaCartorio();
		}
		return new LabelValorMonetario<BigDecimal>("valorCustaCartorio", valorCustaCartorio);
	}

	public Label valorDemaisDespesas() {
		BigDecimal valorDemaisDespesas = BigDecimal.ZERO;
		if (getTituloRemessa().getConfirmacao() != null) {
			valorDemaisDespesas = getTituloRemessa().getConfirmacao().getValorDemaisDespesas();
		}
		if (getTituloRemessa().getRetorno() != null) {
			valorDemaisDespesas = getTituloRemessa().getRetorno().getValorDemaisDespesas();
		}
		return new LabelValorMonetario<BigDecimal>("valorDemaisDespesas", valorDemaisDespesas);
	}

	private Label nomeSacadorVendedor() {
		Label textField = new Label("nomeSacadorVendedor", new Model<String>(getTituloRemessa().getNomeSacadorVendedor()));
		return textField;
	}

	private Label documentoSacador() {
		Label textField = new Label("documentoSacador", new Model<String>(getTituloRemessa().getDocumentoSacador()));
		return textField;
	}

	private Label ufSacadorVendedor() {
		Label textField = new Label("ufSacadorVendedor", new Model<String>(getTituloRemessa().getUfSacadorVendedor()));
		return textField;
	}

	private Label cepSacadorVendedor() {
		Label textField = new Label("cepSacadorVendedor", new Model<String>(getTituloRemessa().getCepSacadorVendedor()));
		return textField;
	}

	private Label cidadeSacadorVendedor() {
		Label textField = new Label("cidadeSacadorVendedor", new Model<String>(getTituloRemessa().getCidadeSacadorVendedor()));
		return textField;
	}

	private Label enderecoSacadorVendedor() {
		Label textField = new Label("enderecoSacadorVendedor", new Model<String>(getTituloRemessa().getEnderecoSacadorVendedor()));
		return textField;
	}

	private Label nomeDevedor() {
		Label textField = new Label("nomeDevedor", new Model<String>(getTituloRemessa().getNomeDevedor()));
		return textField;
	}

	private Label documentoDevedor() {
		Label textField = new Label("documentoDevedor", new Model<String>(getTituloRemessa().getNumeroIdentificacaoDevedor()));
		return textField;
	}

	private Label ufDevedor() {
		Label textField = new Label("ufDevedor", new Model<String>(getTituloRemessa().getUfDevedor()));
		return textField;
	}

	private Label cepDevedor() {
		Label textField = new Label("cepDevedor", new Model<String>(getTituloRemessa().getCepDevedor()));
		return textField;
	}

	private Label cidadeDevedor() {
		Label textField = new Label("cidadeDevedor", new Model<String>(getTituloRemessa().getCidadeDevedor()));
		return textField;
	}

	private Label enderecoDevedor() {
		Label textField = new Label("enderecoDevedor", new Model<String>(getTituloRemessa().getEnderecoDevedor()));
		return textField;
	}

	private TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}

	public List<ArquivoOcorrenciaBean> getArquivosOcorrencias() {
		if (arquivosOcorrencias == null) {
			arquivosOcorrencias = new ArrayList<ArquivoOcorrenciaBean>();
		}
		return arquivosOcorrencias;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}