package br.com.ieptbto.cra.page.titulo;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class BuscarTitulosPage extends BasePage<TituloFiliado>{

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BuscarTitulosPage.class);
	
	private TituloFiliado titulo;
	
	public BuscarTitulosPage() {
		
		this.titulo = new TituloFiliado();
		
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel()){
			/** */
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
//				TituloFiliado titulo = getModelObject();

				try {
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		};		
		form.add(numeroTitulo());
		form.add(numeroProtocoloCartorio());
		form.add(nomeDevedor());
		form.add(documentoDevedor()); 
		add(form);
	}
	
	private TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}
	
	private TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}
	
	private TextField<String> documentoDevedor() {
		return new TextField<String>("documentoDevedor");
	}
	
	private TextField<String> numeroProtocoloCartorio() {
		return new TextField<String>("numeroProtocoloCartorio", new Model<String>());
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
