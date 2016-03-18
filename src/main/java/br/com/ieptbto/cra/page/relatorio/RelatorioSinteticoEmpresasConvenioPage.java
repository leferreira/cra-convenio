package br.com.ieptbto.cra.page.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioSinteticoEmpresasConvenioPage extends BasePage<Instituicao> {

    /***/
    private static final long serialVersionUID = 1L;

    @SpringBean
    private FiliadoMediator filiadoMediador;
    @SpringBean
    private TituloFiliadoMediator tituloFiliadoMediador;
    private Instituicao instituicao;
    private List<Filiado> filiados;
    private List<RelatorioConvenioBean> beansRelatorio;
    private TextField<LocalDate> fieldDataInicio;
    private TextField<LocalDate> fieldDataFinal;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public RelatorioSinteticoEmpresasConvenioPage() {
	this.instituicao = getUser().getInstituicao();

	add(botaoGerarRelatorio());
	add(formularioConsultaTItulosEmpresas());
	add(listaTitulosEmpresas());
    }

    private Form<Filiado> formularioConsultaTItulosEmpresas() {
	Form<Filiado> formPeriodo = new Form<Filiado>("form") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit() {

		if (fieldDataInicio.getDefaultModelObject() != null) {
		    if (fieldDataFinal.getDefaultModelObject() != null) {
			dataInicio = DataUtil.stringToLocalDate(fieldDataInicio.getDefaultModelObject().toString());
			dataFim = DataUtil.stringToLocalDate(fieldDataFinal.getDefaultModelObject().toString());
			if (!dataInicio.isBefore(dataFim))
			    if (!dataInicio.isEqual(dataFim))
				error("A data de início deve ser antes da data fim.");
		    } else
			error("As duas datas devem ser preenchidas.");
		}
		getFiliados().clear();
		getBeansRelatorio().clear();
		getFiliados().addAll(filiadoMediador.buscarListaFiliados(getInstituicao()));
	    }
	};
	formPeriodo.add(comboDataInicio());
	formPeriodo.add(comboDataFim());
	return formPeriodo;
    }

    private Link<Arquivo> botaoGerarRelatorio() {
	return new Link<Arquivo>("gerarRelatorio") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		try {
		    HashMap<String, Object> parametros = new HashMap<String, Object>();
		    if (getBeansRelatorio().isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");

		    parametros.put("CONVENIO", getUser().getInstituicao().getNomeFantasia().toUpperCase());
		    parametros.put("DATA_INICIO", DataUtil.localDateToString(getDataInicio()));
		    parametros.put("DATA_FIM", DataUtil.localDateToString(getDataFim()));

		    JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(getBeansRelatorio());
		    JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioEmpresasConveniosSinteticos.jrxml"));
		    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);

		    File pdf = File.createTempFile("CRA_", ".pdf");
		    JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
		    IResourceStream resourceStream = new FileResourceStream(pdf);
		    getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, pdf.getName()));
		} catch (InfraException ex) {
		    error(ex.getMessage());
		} catch (Exception e) {
		    error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
		    System.out.println(e);
		}
	    }
	};
    }

    private ListView<Filiado> listaTitulosEmpresas() {
	return new ListView<Filiado>("filiados", getFiliados()) {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(ListItem<Filiado> item) {
		Filiado filiado = item.getModelObject();
		Integer titulosPendentes = tituloFiliadoMediador.quatidadeTitulosPendentesEnvioFiliados(filiado, getDataInicio(), getDataFim());
		Integer titulosEmProcesso = tituloFiliadoMediador.quatidadeTitulosEmProcessoFiliados(filiado, getDataInicio(), getDataFim());
		Integer titulosFinalizados = tituloFiliadoMediador.quatidadeTitulosFinalizadosFiliados(filiado, getDataInicio(), getDataFim());
		Integer soma = titulosPendentes + titulosEmProcesso + titulosFinalizados;

		item.add(new Label("filiado", filiado.getRazaoSocial()));
		item.add(new Label("municipio", filiado.getMunicipio().getNomeMunicipio()));
		item.add(new Label("uf", filiado.getUf().toUpperCase()));
		item.add(new Label("pendentes", titulosPendentes));
		item.add(new Label("processo", titulosEmProcesso));
		item.add(new Label("finalizados", titulosFinalizados));
		item.add(new Label("total", soma));

		if (soma > 0) {
		    RelatorioConvenioBean bean = new RelatorioConvenioBean();
		    bean.setNomeFiliado(filiado.getRazaoSocial());
		    bean.setMunicipio(filiado.getMunicipio().getNomeMunicipio().toUpperCase());
		    bean.setUf(filiado.getUf());
		    bean.setPendentes(titulosPendentes);
		    bean.setProcesso(titulosEmProcesso);
		    bean.setFinalizados(titulosFinalizados);
		    bean.setTotal(soma);
		    getBeansRelatorio().add(bean);
		}
	    }
	};
    }

    private TextField<LocalDate> comboDataInicio() {
	fieldDataInicio = new TextField<LocalDate>("dataInicio", new Model<LocalDate>());
	fieldDataInicio.setLabel(new Model<String>("Período de Datas"));
	fieldDataInicio.setRequired(true);
	return fieldDataInicio;
    }

    private TextField<LocalDate> comboDataFim() {
	return fieldDataFinal = new TextField<LocalDate>("dataFinal", new Model<LocalDate>());
    }

    public Instituicao getInstituicao() {
	return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
	this.instituicao = instituicao;
    }

    public List<Filiado> getFiliados() {
	if (filiados == null) {
	    filiados = new ArrayList<Filiado>();
	}
	return filiados;
    }

    public void setFiliados(List<Filiado> filiados) {
	this.filiados = filiados;
    }

    public LocalDate getDataInicio() {
	return dataInicio;
    }

    public LocalDate getDataFim() {
	return dataFim;
    }

    public void setDataInicio(LocalDate dataInicio) {
	this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDate dataFim) {
	this.dataFim = dataFim;
    }

    public List<RelatorioConvenioBean> getBeansRelatorio() {
	if (beansRelatorio == null) {
	    beansRelatorio = new ArrayList<RelatorioConvenioBean>();
	}
	return beansRelatorio;
    }

    @Override
    protected IModel<Instituicao> getModel() {
	return new CompoundPropertyModel<Instituicao>(instituicao);
    }
}
