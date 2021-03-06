package br.com.ieptbto.cra.page.filiado;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class ListaFiliadoPage extends BasePage<Filiado> {

	/***/
	private static final long serialVersionUID = 1L;

	private Filiado filiado;

	@SpringBean
	FiliadoMediator filiadoMediator;

	public ListaFiliadoPage() {
		adicionarComponentes();
	}

	public ListaFiliadoPage(String mensagem) {
		adicionarComponentes();
		success(mensagem);
	}

	@Override
	protected void adicionarComponentes() {
		this.filiado = new Filiado();
		add(new Link<IncluirFiliadoPage>("botaoNovo") {

			/***/
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(new IncluirFiliadoPage());
			}
		});
		add(carregarListaFiliados());
	}

	private ListView<Filiado> carregarListaFiliados() {
		return new ListView<Filiado>("listViewFiliado", filiadoMediator.buscarListaFiliados(getUser().getInstituicao())) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Filiado> item) {
				final Filiado filiado = item.getModelObject();

				Link<UsuarioFiliado> linkAlterar = new Link<UsuarioFiliado>("linkAlterar") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new IncluirFiliadoPage(filiado));
					}
				};
				linkAlterar.add(new Label("nome", filiado.getRazaoSocial()));
				item.add(linkAlterar);

				item.add(new Label("documento", filiado.getCnpjCpf()));
				item.add(new Label("cidade", filiado.getMunicipio().getNomeMunicipio()));
				item.add(new Label("uf", filiado.getUf()));
				item.add(new Label("ativo", verificarSituacao(filiado.isAtivo())));
			}
		};
	}

	private String verificarSituacao(Boolean ativo) {
		if (ativo.equals(true))
			return "Sim";
		return "Não";
	}

	@Override
	protected IModel<Filiado> getModel() {
		return new CompoundPropertyModel<Filiado>(filiado);
	}
}
