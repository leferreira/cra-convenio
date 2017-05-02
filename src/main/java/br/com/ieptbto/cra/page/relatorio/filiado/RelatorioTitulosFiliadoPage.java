package br.com.ieptbto.cra.page.relatorio.filiado;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.component.DateTextField;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.report.RelatorioUtil;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.PeriodoDataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class RelatorioTitulosFiliadoPage extends BasePage<TituloFiliado> {

	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	@SpringBean
	FiliadoMediator filiadoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
 
	private static final long serialVersionUID = 1L;
	private TituloConvenioBean tituloConvenioBean;
	private Usuario usuario;
	private Filiado filiado;

	public RelatorioTitulosFiliadoPage() {
		this.tituloConvenioBean = new TituloConvenioBean();
		this.filiado = getFiliadoPorUsuario();
		this.usuario = getUser();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormulario();
	}

	private void carregarFormulario() {
		Form<TituloConvenioBean> form = new Form<TituloConvenioBean>("form", new CompoundPropertyModel<TituloConvenioBean>(tituloConvenioBean)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				tituloConvenioBean = getModelObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				try {
					if (tituloConvenioBean.getDataInicio() != null) {
						if (tituloConvenioBean.getDataFim() != null) {
							dataInicio = new LocalDate(tituloConvenioBean.getDataInicio());
							dataFim = new LocalDate(tituloConvenioBean.getDataFim());
							if (!dataInicio.isBefore(dataFim) && !dataInicio.isEqual(dataFim))
								throw new InfraException("A data de início deve ser antes da data fim.");
						} else throw new InfraException("As duas datas devem ser preenchidas.");
					}
					if (PeriodoDataUtil.diferencaDeDiasEntreData(dataInicio.toDate(), dataFim.toDate()) > 31) {
						throw new InfraException("Limite máximo do período para o relatório é de 30 dias entre a data inicial e a final.");
					}
					
					JasperPrint jasperPrint = new RelatorioUtil().gerarRelatorioTitulosConvenioPorSituacao(usuario, tituloConvenioBean);
					if (jasperPrint.getPages().isEmpty()) {
						throw new InfraException("O sistema não encontrou títulos para o relatório com os parâmetros informados!");
					}
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
							"CRA_RELATORIO_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));

				} catch (InfraException ex) {
					logger.info(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception e) {
					logger.info(e.getMessage(), e);
					error("Não foi possível gerar o relatório ! Entre em contato com o IEPTB-TO !");
				}
			}
		};
		form.add(dataInicio());
		form.add(dataFinal());
		form.add(pracaProtesto());
		form.add(dropDownFiliado());
		form.add(labelEmpresasFiliadas());
		form.add(tipoRelatorio());
		add(form);
	}

	private DateTextField dataInicio() {
		DateTextField dataEnvioInicio = new DateTextField("dataInicio");
		dataEnvioInicio.setLabel(new Model<String>("Período de Datas"));
		dataEnvioInicio.setRequired(true);
		return dataEnvioInicio;
	}

	private DateTextField dataFinal() {
		return new DateTextField("dataFim");
	}

	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> comboMunicipio = new DropDownChoice<Municipio>("municipio", municipioMediator.getMunicipiosTocantins(), renderer);
		return comboMunicipio;
	}

	private Label labelEmpresasFiliadas() {
		Label label = new Label("labelEmpresasFiliadas");
		label.setVisible(getUser().getInstituicao().getAdministrarEmpresasFiliadas());
		return label;
	}

	private DropDownChoice<Filiado> dropDownFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		DropDownChoice<Filiado> campoFiliado = new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
		campoFiliado.setLabel(new Model<String>("Filiado"));
		campoFiliado.setOutputMarkupId(true);
		campoFiliado.setVisible(getUser().getInstituicao().getAdministrarEmpresasFiliadas());

		if (filiado != null) {
			campoFiliado.setEnabled(false);
			campoFiliado.setModel(new Model<Filiado>());
			campoFiliado.setDefaultModelObject(filiado);
		}
		return campoFiliado;
	}

	private RadioChoice<SituacaoTituloRelatorio> tipoRelatorio() {
		IChoiceRenderer<SituacaoTituloRelatorio> renderer = new ChoiceRenderer<SituacaoTituloRelatorio>("label");
		List<SituacaoTituloRelatorio> situacoes = new ArrayList<SituacaoTituloRelatorio>();
		situacoes.add(SituacaoTituloRelatorio.GERAL);
		situacoes.add(SituacaoTituloRelatorio.PAGOS);
		situacoes.add(SituacaoTituloRelatorio.PROTESTADOS);
		situacoes.add(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS);
		
		RadioChoice<SituacaoTituloRelatorio> radioSituacaoTitulo = new RadioChoice<SituacaoTituloRelatorio>("situacaoTitulosRelatorio", situacoes, renderer);
		radioSituacaoTitulo.setRequired(true);
		radioSituacaoTitulo.setLabel(new Model<String>("Situação dos Títulos"));
		return radioSituacaoTitulo;
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return null;
	}
}