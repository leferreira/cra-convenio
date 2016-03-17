package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER, CraRoles.ADMIN })
public class BuscarTitulosPage extends BasePage<TituloFiliado>{

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private UsuarioFiliadoMediator usuarioFiliadoMediator;
	private TituloFiliado tituloFiliado;
	private UsuarioFiliado usuarioFiliado;
	
	public BuscarTitulosPage() {
		this.tituloFiliado = new TituloFiliado();
		this.usuarioFiliado = usuarioFiliadoMediator.buscarUsuarioFiliado(getUser());
		
		carregarPanels();
	}
	
	private void carregarPanels() {
		
		if (getUsuarioFiliado() == null){
			add(new BuscarTitulosConvenioPanel("buscarTitulosPanel", getModel(), getUser().getInstituicao()));
		} else if (getUsuarioFiliado() != null){
			add(new BuscarTitulosFiliadoPanel("buscarTitulosPanel", getModel(), getUsuarioFiliado().getFiliado()));
		}
	}

	public UsuarioFiliado getUsuarioFiliado() {
		return usuarioFiliado;
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(tituloFiliado);
	}
}
