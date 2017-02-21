package br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao;

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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.historico.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaTitulosDesistenciaCancelamentoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;

	private TituloConvenioBean tituloBean;
	private Filiado filiado;
 
	public ListaTitulosDesistenciaCancelamentoPage(TituloConvenioBean tituloBean, Filiado filiado) {
		this.tituloBean = tituloBean;
		this.filiado = filiado;

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

				String protocolo = "";
				if (titulo.getConfirmacao() != null) {
					if (!StringUtils.isBlank(titulo.getConfirmacao().getNumeroProtocoloCartorio())) {
						protocolo = titulo.getConfirmacao().getNumeroProtocoloCartorio();
					}
				}
				item.add(new Label("protocolo", protocolo));
				item.add(new Label("numeroTitulo", titulo.getNumeroTitulo()));

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
					item.add(new Label("dataSituacao", DataUtil.localDateToString(titulo.getRetorno().getDataOcorrencia())));
				} else {
					item.add(new Label("dataSituacao", DataUtil.localDateToString(titulo.getDataOcorrencia())));
				}

				String situacaoTitulo = titulo.getSituacaoTitulo();
				item.add(new Label("situacaoTitulo", situacaoTitulo));
				Link<TituloRemessa> linkSolicitarCancelamento = new Link<TituloRemessa>("linkSolicitarCancelamento") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new EnviarSolicitacaoDesistenciaCancelamentoPage(titulo));
					}
				};
				linkSolicitarCancelamento.setVisible(false);
				if (situacaoTitulo.equals("ABERTO") || situacaoTitulo.equals("PROTESTADO")) {
					linkSolicitarCancelamento.setVisible(true);
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
				String codigoFiliado = null;
				if (filiado != null) {
					codigoFiliado = filiado.getCodigoFiliado();
				}
				return tituloFiliadoMediator.buscarListaTitulos(getUser(), tituloBean.getDataInicio(), tituloBean.getInstiuicaoCartorio(), tituloBean.getNumeroTitulo(),
						tituloBean.getNomeDevedor(), tituloBean.getDocumentoDevedor(), codigoFiliado);
			}
		};
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return null;
	}
}
