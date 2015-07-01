package br.com.ieptbto.cra.page.base;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;

	public HomePage() {
		super();
		carregarContratoEntradaDeTitulos();
	}
	
	private void carregarContratoEntradaDeTitulos() {

		final ModalWindow modalContrato = new ModalWindow("modalContrato");
		modalContrato.setPageCreator(new ModalWindow.PageCreator() {
            /***/
			private static final long serialVersionUID = 1L;

			@Override
            public Page createPage() {
                return new ContratoModal(HomePage.this.getPageReference(), modalContrato, getUser());
            }
	    });
		modalContrato.setTitle(new Model<String>("Termos e Condições para Entrada de Títulos"));
		modalContrato.setResizable(false);
		modalContrato.setAutoSize(false);
		modalContrato.setInitialWidth(80);
		modalContrato.setInitialHeight(30);
		modalContrato.setMinimalWidth(80);
		modalContrato.setMinimalHeight(30);
        modalContrato.setWidthUnit("em");
        modalContrato.setHeightUnit("em");
        add(modalContrato);
		
		AjaxLink<?> openModal = new AjaxLink<Void>("showModal"){
            /***/
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick(AjaxRequestTarget target){
            	modalContrato.show(target);
            }
		};
		if (!verificarAceiteUsuarioContrato()) {
			openModal.setMarkupId("showModal");
		}
		add(openModal);
	}
	
	private boolean verificarAceiteUsuarioContrato(){
		UsuarioFiliado usuarioFiliado = usuarioFiliadoMediator.buscarUsuarioFiliado(getUser());
		if (usuarioFiliado != null) {
			return usuarioFiliado.isTermosContratoAceite();
		} 
		return true;
	}
	
	public HomePage(PageParameters parameters) {
		error(parameters.get("error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}