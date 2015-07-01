package br.com.ieptbto.cra.page.base;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.login.LoginPage;

/**
 * @author Thasso Araújo
 *
 */
public class ContratoModal extends WebPage {
	
	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ContratoModal.class);
	
	private ModalWindow modalWindow;
	private PageReference modalWindowPage;
	private Usuario usuario;
	
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	
	public ContratoModal(final PageReference modalWindowPage, final ModalWindow window, Usuario user) {
        this.modalWindowPage = modalWindowPage;
        this.modalWindow = window;
        setUsuario(user);
        botaoConcordo();
        botaoNaoConcordo();
    }
	
	
	private void botaoConcordo(){
		add(new AjaxLink<Void>("botaoConcordo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick(AjaxRequestTarget target)    {
				
				try {
				
					usuarioFiliadoMediator.confirmarAceiteTermosContrato(getUsuario());
					fecharModal(target);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o aceite dos termos do usuário ! \n");
				}
            }
        });
	}

	private void botaoNaoConcordo(){
		add(new AjaxLink<Void>("botaoNaoConcordo") {
			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target)    {
				
				usuarioFiliadoMediator.naoAceiteTermosContrato(getUsuario());
				fecharModal(target);
				logger.info("O usuário <<" + getUsuario().getNome() + ">> saiu do sistema pois não aceitou os termos !.");
				getSession().invalidateNow();
				setResponsePage(LoginPage.class);
			}
		});
	}
	
	private void fecharModal(AjaxRequestTarget target){
		if (modalWindowPage != null)
			modalWindow.close(target);
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
