package br.com.ieptbto.cra.page.titulo;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 * 
 */
public class BuscarTitulosConvenioPanel extends Panel {

    /***/
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BuscarTitulosConvenioPanel.class);

    @SpringBean
    private TituloFiliadoMediator tituloFiliadoMediator;
    @SpringBean
    private MunicipioMediator municipioMediator;
    @SpringBean
    private FiliadoMediator filiadoMediator;
    private IModel<TituloFiliado> modelTituloFiliado;
    private Instituicao instituicaoConvenio;
    private TextField<LocalDate> dataEntradaInicio;
    private TextField<LocalDate> dataEntradaFinal;
    private DropDownChoice<Municipio> campoPracaProtesto;
    private DropDownChoice<Filiado> campoFiliado;

    public BuscarTitulosConvenioPanel(String id, IModel<TituloFiliado> model, Instituicao instituicao) {
	super(id, model);
	this.modelTituloFiliado = model;
	this.instituicaoConvenio = instituicao;

	add(carregarFormulario());
    }

    private Form<TituloFiliado> carregarFormulario() {
	Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModelTituloFiliado()) {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit() {
		TituloFiliado titulo = getModelObject();
		Municipio pracaProtesto = null;
		Filiado filiado = null;
		LocalDate dataInicio = null;
		LocalDate dataFim = null;

		try {
		    if (dataEntradaInicio.getDefaultModelObject() != null) {
			if (dataEntradaFinal.getDefaultModelObject() != null) {
			    dataInicio = DataUtil.stringToLocalDate(dataEntradaInicio.getDefaultModelObject().toString());
			    dataFim = DataUtil.stringToLocalDate(dataEntradaFinal.getDefaultModelObject().toString());
			    if (!dataInicio.isBefore(dataFim))
				if (!dataInicio.isEqual(dataFim))
				    throw new InfraException("A data de início deve ser antes da data fim.");
			} else
			    throw new InfraException("As duas datas devem ser preenchidas.");
		    }

		    if (campoPracaProtesto.getModelObject() != null) {
			pracaProtesto = campoPracaProtesto.getModelObject();
		    }

		    if (campoFiliado.getModelObject() != null) {
			filiado = campoFiliado.getModelObject();
		    }

		    setResponsePage(new ListaTitulosPage(getInstituicaoConvenio(), dataInicio, dataFim, filiado, pracaProtesto, titulo));
		} catch (InfraException ex) {
		    logger.error(ex.getMessage());
		    error(ex.getMessage());
		} catch (Exception e) {
		    logger.error(e.getMessage(), e);
		    error("Não foi possível buscar os titulos! \n Entre em contato com a CRA ");
		}
	    }
	};

	form.add(numeroTitulo());
	form.add(nomeDevedor());
	form.add(documentoDevedor());
	form.add(campoDataEntradaInicio());
	form.add(campoDataEntradaFinal());
	form.add(pracaProtesto());
	form.add(comboFiliado());
	return form;
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

    private TextField<LocalDate> campoDataEntradaInicio() {
	return dataEntradaInicio = new TextField<LocalDate>("dataEntradaInicio", new Model<LocalDate>());
    }

    private TextField<LocalDate> campoDataEntradaFinal() {
	return dataEntradaFinal = new TextField<LocalDate>("dataEntradaFinal", new Model<LocalDate>());
    }

    private DropDownChoice<Municipio> pracaProtesto() {
	IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
	campoPracaProtesto = new DropDownChoice<Municipio>("pracaProtesto", new Model<Municipio>(), municipioMediator.getMunicipiosTocantins(), renderer);
	return campoPracaProtesto;
    }

    private DropDownChoice<Filiado> comboFiliado() {
	IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
	campoFiliado = new DropDownChoice<Filiado>("filiado", new Model<Filiado>(), filiadoMediator.buscarListaFiliados(getInstituicaoConvenio()), renderer);
	campoFiliado.setLabel(new Model<String>("Filiado"));
	return campoFiliado;
    }

    public Instituicao getInstituicaoConvenio() {
	return instituicaoConvenio;
    }

    public IModel<TituloFiliado> getModelTituloFiliado() {
	return modelTituloFiliado;
    }
}