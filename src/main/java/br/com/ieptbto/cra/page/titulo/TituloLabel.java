package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
public class TituloLabel extends BasePage<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	private TituloFiliado titulo;
	
	public TituloLabel() {
		info("Os dados do título foram salvos com sucesso na CRA !");
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}

}
