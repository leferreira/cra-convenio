package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class BuscarTitulosPage extends BasePage<TituloFiliado> {

	private static final long serialVersionUID = 1L;

	private TituloFiliado tituloFiliado;
	private TituloConvenioBean tituloConvenioBean;
	private Filiado filiado;
	private Usuario usuario;

	public BuscarTitulosPage() { 
		this.tituloFiliado = new TituloFiliado();
		this.tituloConvenioBean = new TituloConvenioBean();
		this.usuario = getUser();
		this.filiado = getFiliadoPorUsuarioCorrente();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formularioBuscarTitulo());
	}

	private Form<TituloConvenioBean> formularioBuscarTitulo() {
		Form<TituloConvenioBean> form = new Form<TituloConvenioBean>("form", getModelForm()){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					if (tituloConvenioBean.getNumeroTitulo() == null && tituloConvenioBean.getDocumentoDevedor() == null 
							&& tituloConvenioBean.getNomeDevedor() == null && tituloConvenioBean.getDataInicio() == null
							&& tituloConvenioBean.getFiliado() == null) {
						if (tituloConvenioBean.getInstiuicaoCartorio() != null) {
							throw new InfraException("Por favor informe mais um parâmetro, além do município selecionado...");
						} else {
							throw new InfraException("Os campos não podem ser nulos! Por favor informe ao menos um parâmetro...");
						}
					}
					setResponsePage(new ListaTitulosPage(tituloConvenioBean, filiado));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível buscar os titulos! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(new BuscarTitulosConvenioInputPanel("buscarTitulosPanel", getModelForm(), usuario.getInstituicao(), filiado));
		return form;
	}
	
	private IModel<TituloConvenioBean> getModelForm(){
		return new CompoundPropertyModel<TituloConvenioBean>(tituloConvenioBean);
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(tituloFiliado);
	}
}
