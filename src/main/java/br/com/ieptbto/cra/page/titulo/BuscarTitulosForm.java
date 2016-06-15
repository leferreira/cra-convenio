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
			if (bean.getNumeroTitulo() == null && bean.getDocumentoDevedor() == null && bean.getNomeDevedor() == null && bean.getDataInicio() == null
					&& bean.getFiliado() == null) {
				if (bean.getInstiuicaoCartorio() != null) {
					throw new InfraException("Por favor informe mais um parâmetro, além do município selecionado...");
				} else {
					throw new InfraException("Os campos não podem ser nulos! Por favor informe ao menos um parâmetro...");
				}
			}
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
