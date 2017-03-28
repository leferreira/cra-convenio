package br.com.ieptbto.cra.page.relatorio.convenio;

import org.apache.wicket.model.IModel;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BaseForm;

/**
 * @author Thasso Araujo
 *
 */
public class RelatorioTitulosForm extends BaseForm<TituloConvenioBean> {

	private static final long serialVersionUID = 1L;
	private LocalDate dataInicio;
	private LocalDate dataFim;

	public RelatorioTitulosForm(String id, IModel<TituloConvenioBean> model) {
		super(id, model);
	}

	@Override
	protected void onSubmit() {
		TituloConvenioBean bean = getModelObject();

		try {
			if (bean.getDataInicio() != null) {
				if (bean.getDataFim() != null) {
					dataInicio = new LocalDate(bean.getDataInicio());
					dataFim = new LocalDate(bean.getDataFim());
					if (!dataInicio.isBefore(dataFim) && !dataInicio.isEqual(dataFim))
						throw new InfraException("A data de início deve ser antes da data fim...");
				} else throw new InfraException("As duas datas devem ser preenchidas...");
			}
			setResponsePage(new RelatorioTitulosCsvPage(bean.getSituacaoTitulosRelatorio(), bean.getTipoInstituicao(), bean.getBancoConvenio(),
					bean.getCartorio(), dataInicio, dataFim));
		} catch (InfraException ex) {
			error(ex.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
		}
	}
}