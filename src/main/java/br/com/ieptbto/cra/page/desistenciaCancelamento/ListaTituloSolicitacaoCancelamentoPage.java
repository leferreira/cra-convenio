package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

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

			}
		};

	}

	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				return null;
			}
		};
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}
