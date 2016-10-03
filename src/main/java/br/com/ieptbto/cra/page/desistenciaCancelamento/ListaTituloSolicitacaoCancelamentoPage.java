package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.math.BigDecimal;
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
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.SolicitacaoCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaTituloSolicitacaoCancelamentoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	CancelamentoProtestoMediator cancelamentoProtestoMediator;

	private TituloRemessa tituloRemessa;
	private Instituicao bancoConvenio;
	private Municipio municipio;

	public ListaTituloSolicitacaoCancelamentoPage(TituloRemessa titulo, Instituicao bancoConvenio, Municipio municipio) {
		this.tituloRemessa = titulo;
		this.bancoConvenio = bancoConvenio;
		this.municipio = municipio;

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(listaTitulosCancelamento());
	}

	private ListView<TituloRemessa> listaTitulosCancelamento() {
		return new ListView<TituloRemessa>("listViewTitulos", buscarTitulos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa titulo = item.getModelObject();
				final SolicitacaoCancelamento solicitacaoCancelamento = cancelamentoProtestoMediator.buscarSolicitacaoCancelamentoPorTitulo(titulo);

				item.add(new Label("numeroTitulo", titulo.getNumeroTitulo()));
				item.add(new Label("nomeRemessa", titulo.getRemessa().getArquivo().getNomeArquivo()));

				String municipio = titulo.getRemessa().getInstituicaoDestino().getMunicipio().getNomeMunicipio();
				if (municipio.length() > 20) {
					municipio = municipio.substring(0, 19);
				}
				item.add(new Label("municipio", municipio.toUpperCase()));
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", titulo.getValorTitulo()));
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(titulo));
					}
				};
				if (titulo.getNomeDevedor().length() > 25) {
					linkHistorico.add(new Label("nomeDevedor", titulo.getNomeDevedor().substring(0, 24)));
				} else {
					linkHistorico.add(new Label("nomeDevedor", titulo.getNomeDevedor()));
				}
				item.add(linkHistorico);
				if (titulo.getRetorno() != null) {
					item.add(new Label("retorno", titulo.getRetorno().getRemessa().getArquivo().getNomeArquivo()));
					item.add(new Label("dataSituacao", DataUtil.localDateToString(titulo.getRetorno().getDataOcorrencia())));
				} else {
					item.add(new Label("retorno", StringUtils.EMPTY));
					item.add(new Label("dataSituacao", DataUtil.localDateToString(titulo.getDataOcorrencia())));
				}
				item.add(new Label("situacaoTitulo", titulo.getSituacaoTitulo()));

				Link<TituloRemessa> linkSolicitarCancelamento = new Link<TituloRemessa>("linkSolicitarCancelamento") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TituloSolicitacaoCancelamentoPage(titulo));
					}
				};
				if (titulo.getConfirmacao() != null) {
					if (StringUtils.isNotEmpty(titulo.getConfirmacao().getNumeroProtocoloCartorio().trim())
							&& !titulo.getConfirmacao().getNumeroProtocoloCartorio().trim().equals("0")) {
						if (solicitacaoCancelamento == null) {
							linkSolicitarCancelamento.add(new Label("nomeAcao", "Solicitar".toUpperCase()));
						} else {
							linkSolicitarCancelamento.setEnabled(false);
							linkSolicitarCancelamento.add(new Label("nomeAcao", "Enviado".toUpperCase()));
						}
					} else {
						linkSolicitarCancelamento.setEnabled(false);
						linkSolicitarCancelamento.add(new Label("nomeAcao", "Devolvido".toUpperCase()));
					}
					item.add(new Label("dataConfirmacao", DataUtil.localDateToString(titulo.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
					item.add(new Label("protocolo", titulo.getConfirmacao().getNumeroProtocoloCartorio()));
				} else {
					linkSolicitarCancelamento.setEnabled(false);
					linkSolicitarCancelamento.add(new Label("nomeAcao", "Sem Protocolo".toUpperCase()));
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(linkSolicitarCancelamento);
			}
		};

	}

	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				return cancelamentoProtestoMediator.buscarTitulosParaSolicitarCancelamento(tituloRemessa, bancoConvenio, municipio, getUser());
			}
		};
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}
