package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

public class BuscarTitulosForm extends BaseForm<BuscarTitulosFormBean> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;

	private String codigoFiliado;

	public BuscarTitulosForm(String id, IModel<BuscarTitulosFormBean> model, String codigoFiliado) {
		super(id, model);
		this.codigoFiliado = codigoFiliado;
	}

	protected void onSubmit() {
		BuscarTitulosFormBean bean = getModelObject();

		try {
			setResponsePage(new ListaTitulosPage(bean, codigoFiliado));
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			error(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			error("Não foi possível buscar os titulos! \n Entre em contato com a CRA ");
		}
	};
}
