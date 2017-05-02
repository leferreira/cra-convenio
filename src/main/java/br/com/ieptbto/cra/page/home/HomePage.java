package br.com.ieptbto.cra.page.home;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	@SpringBean
	private MunicipioMediator municipioMediator;
	@SpringBean
	private UsuarioFiliadoMediator usuarioFiliadoMediator;

	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private UsuarioFiliado usuarioFiliado;
	
	public HomePage() {
		this.usuario = getUser();
		this.usuarioFiliado = usuarioFiliadoMediator.buscarUsuarioFiliado(usuario);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(panelConvenioLayoutPersonalizadoManual());
		add(panelConvenioCraNacional());
		add(modalContratoEntradaDeTitulos());
		add(panelFiliadoEntradaManual());
	}
	
	private Panel panelConvenioLayoutPersonalizadoManual() {
		return new ConvenioLayoutPersonalizadoManualPanel("convenioLayoutPersonalizadoManualPanel", usuario, usuarioFiliado);
	}
	
	private Panel panelConvenioCraNacional() {
		return new ConvenioCraNacionalPanel("convenioCraNacionalPanel", usuario, usuarioFiliado);
	}

	private Panel panelFiliadoEntradaManual() {
		return new FiliadoEntradaManualPanel("filiadoEntradaManualPanel", usuarioFiliado);
	}

	private ModalWindow modalContratoEntradaDeTitulos() {
		final ModalWindow modalContrato = new ModalWindow("modalContrato");
		modalContrato.setPageCreator(new ModalWindow.PageCreator() {

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

		AjaxLink<?> openModal = new AjaxLink<Void>("showModal") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalContrato.show(target);
			}
		};
		if (!verificarAceiteUsuarioContrato()) {
			openModal.setMarkupId("showModal");
		}
		add(openModal);
		return modalContrato;
	}

	private boolean verificarAceiteUsuarioContrato() {
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
		return "IEPTB-Convênios";
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}