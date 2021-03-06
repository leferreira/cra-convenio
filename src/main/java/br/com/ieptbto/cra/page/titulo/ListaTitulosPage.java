package br.com.ieptbto.cra.page.titulo;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.component.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.historico.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
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

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaTitulosPage extends BasePage<TituloRemessa> {

	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;

	private static final long serialVersionUID = 1L;
	private TituloConvenioBean tituloBean;

	public ListaTitulosPage(TituloConvenioBean tituloBean, Filiado filiado) {
		this.tituloBean = tituloBean;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(listaTitulos());
	}

	private ListView<TituloRemessa> listaTitulos() {
		return new ListView<TituloRemessa>("listViewTitulos", buscarTitulos()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));

				String municipio = tituloLista.getRemessa().getInstituicaoDestino().getMunicipio().getNomeMunicipio();
				if (municipio.length() > 20) {
					municipio = municipio.substring(0, 19);
				}
				item.add(new Label("municipio", municipio.toUpperCase()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("dataProtocolo", DataUtil.localDateToString(tituloLista.getConfirmacao().getDataProtocolo())));
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				} else {
					item.add(new Label("dataProtocolo", StringUtils.EMPTY));
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", tituloLista.getSaldoTitulo()));
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
					}
				};
				if (tituloLista.getNomeDevedor().length() > 25) {
					linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor().substring(0, 24).toUpperCase()));
				} else {
					linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor().toUpperCase()));
				}
				item.add(linkHistorico);
				if (tituloLista.getRetorno() != null) {
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getRetorno().getDataOcorrencia())));
				} else if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getConfirmacao().getDataOcorrencia())));
				} else {
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getDataOcorrencia())));
				}
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}

	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				return tituloFiliadoMediator.buscarListaTitulos(getUser(), getFiliadoPorUsuario(), getTituloBean());
			}
		};
	}

    public TituloConvenioBean getTituloBean() {
        return tituloBean;
    }

    @Override
	protected IModel<TituloRemessa> getModel() {
		return null;
	}
}