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
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class BuscarTitulosFiliadoPanel extends Panel {

    /***/
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BuscarTitulosFiliadoPanel.class);

    @SpringBean
    private MunicipioMediator municipioMediator;
    private IModel<TituloFiliado> modelTituloFiliado;
    private Filiado filiado;
    private TextField<LocalDate> dataEntradaInicio;
    private TextField<LocalDate> dataEntradaFinal;
    private DropDownChoice<Municipio> campoPracaProtesto;

    public BuscarTitulosFiliadoPanel(String id, IModel<TituloFiliado> model, Filiado filiado) {
	super(id, model);
	this.modelTituloFiliado = model;
	this.filiado = filiado;

	add(formularioConsultaTitulo());
    }

    private Form<TituloFiliado> formularioConsultaTitulo() {
	Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModelTituloFiliado()) {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit() {
		TituloFiliado titulo = getModelObject();
		Municipio pracaProtesto = null;
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

		    setResponsePage(new ListaTitulosPage(getFiliado(), dataInicio, dataFim, pracaProtesto, titulo));
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

    public Filiado getFiliado() {
	return filiado;
    }

    public IModel<TituloFiliado> getModelTituloFiliado() {
	return modelTituloFiliado;
    }
}
