package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER, CraRoles.ADMIN })
public class BuscarTitulosPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;

	private TituloRemessa tituloRemessa;
	private UsuarioFiliado usuarioFiliado;

	public BuscarTitulosPage() {
		this.tituloRemessa = new TituloRemessa();
		this.usuarioFiliado = usuarioFiliadoMediator.buscarUsuarioFiliado(getUser());

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioBuscarTitulo();

	}

	private void formularioBuscarTitulo() {
		BuscarTitulosFormBean bean = new BuscarTitulosFormBean();
		BuscarTitulosForm form = new BuscarTitulosForm("form", new CompoundPropertyModel<BuscarTitulosFormBean>(bean), getCodigoFiliado());
		form.add(new BuscarTitulosConvenioPanel("buscarTitulosPanel", new CompoundPropertyModel<BuscarTitulosFormBean>(bean),
				getUser().getInstituicao(), getCodigoFiliado()));
		add(form);
	}

	public String getCodigoFiliado() {
		if (usuarioFiliado != null) {
			return usuarioFiliado.getFiliado().getCodigoFiliado();
		}
		return null;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}
