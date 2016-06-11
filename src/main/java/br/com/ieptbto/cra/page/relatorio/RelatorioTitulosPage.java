package br.com.ieptbto.cra.page.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.relatorioConvenio.RelatorioUtil;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class RelatorioTitulosPage extends BasePage<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	@SpringBean
	FiliadoMediator filiadoMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;

	private TituloFiliado titulo;
	private UsuarioFiliado usuarioFiliado;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private RadioChoice<SituacaoTituloRelatorio> radioSituacaoTitulo;

	public RelatorioTitulosPage() {
		this.titulo = new TituloFiliado();
		this.usuarioFiliado = usuarioFiliadoMediator.buscarUsuarioFiliado(getUser());

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormulario();

	}

	private void carregarFormulario() {
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				TituloFiliado titulo = getModelObject();
				Filiado filiado = titulo.getFiliado();
				Municipio municipio = titulo.getPracaProtesto();
				SituacaoTituloRelatorio situacaoTipoRelatorio = radioSituacaoTitulo.getModelObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				if (dataEnvioInicio.getDefaultModelObject() != null) {
					if (dataEnvioFinal.getDefaultModelObject() != null) {
						dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
						dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
						if (!dataInicio.isBefore(dataFim))
							if (!dataInicio.isEqual(dataFim))
								error("A data de início deve ser antes da data fim.");
					} else
						error("As duas datas devem ser preenchidas.");
				}

				try {
					JasperPrint jasperPrint = new RelatorioUtil().gerarRelatorioTitulosConvenioPorSituacao(getUser(), situacaoTipoRelatorio, filiado,
							municipio, dataInicio, dataFim);
					if (jasperPrint.getPages().isEmpty()) {
						throw new InfraException("O sistema não encontrou títulos para o relatório com os parâmetros informados!");
					}
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
							"CRA_RELATORIO_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));

				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório ! Entre em contato com o IEPTB-TO !");
					e.printStackTrace();
				}
			}
		};
		form.add(dataEnvioInicio());
		form.add(dataEnvioFinal());
		form.add(pracaProtesto());
		form.add(dropDownFiliado());
		form.add(tipoRelatorio());
		add(form);
	}

	private TextField<LocalDate> dataEnvioInicio() {
		dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		dataEnvioInicio.setLabel(new Model<String>("Período de Datas"));
		dataEnvioInicio.setRequired(true);
		return dataEnvioInicio;
	}

	private TextField<LocalDate> dataEnvioFinal() {
		return dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}

	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> comboMunicipio =
				new DropDownChoice<Municipio>("pracaProtesto", municipioMediator.getMunicipiosTocantins(), renderer);
		return comboMunicipio;
	}

	private DropDownChoice<Filiado> dropDownFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		DropDownChoice<Filiado> dropDownFiliado;
		if (usuarioFiliado != null) {
			dropDownFiliado = new DropDownChoice<Filiado>("filiado", new Model<Filiado>(usuarioFiliado.getFiliado()),
					filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
			dropDownFiliado.setEnabled(false);
			dropDownFiliado.setOutputMarkupId(true);
		} else {
			dropDownFiliado = new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
		}
		return dropDownFiliado;
	}

	private RadioChoice<SituacaoTituloRelatorio> tipoRelatorio() {
		IChoiceRenderer<SituacaoTituloRelatorio> renderer = new ChoiceRenderer<SituacaoTituloRelatorio>("label");
		List<SituacaoTituloRelatorio> situacoes = new ArrayList<SituacaoTituloRelatorio>();
		situacoes.add(SituacaoTituloRelatorio.GERAL);
		situacoes.add(SituacaoTituloRelatorio.PAGOS);
		situacoes.add(SituacaoTituloRelatorio.PROTESTADOS);
		situacoes.add(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS);
		radioSituacaoTitulo = new RadioChoice<SituacaoTituloRelatorio>("tipoRelatorio", new Model<SituacaoTituloRelatorio>(), situacoes, renderer);
		radioSituacaoTitulo.setRequired(true);
		radioSituacaoTitulo.setLabel(new Model<String>("Situação dos Títulos"));
		return radioSituacaoTitulo;
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}