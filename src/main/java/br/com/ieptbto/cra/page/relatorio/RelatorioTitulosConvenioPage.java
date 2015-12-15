package br.com.ieptbto.cra.page.relatorio;

import java.util.Arrays;

import org.apache.wicket.Component;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class RelatorioTitulosConvenioPage extends BasePage<TituloFiliado>  {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private FiliadoMediator filiadoMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;
	@SpringBean
	private RemessaMediator remessaMediator;
	private TituloFiliado titulo;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private TipoRelatorio tipoRelatorio;
	
	public RelatorioTitulosConvenioPage() {
		this.titulo = new TituloFiliado();
		
		carregarFormulario();
	}
	
	private void carregarFormulario() {
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel()){
			
			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onSubmit() {
				TituloFiliado titulo = getModelObject();
				TipoRelatorio tipoRelatorio = getTipoRelatorio();
				Instituicao instituicao = getUser().getInstituicao();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				
				if (dataEnvioInicio.getDefaultModelObject() != null){
					if (dataEnvioFinal.getDefaultModelObject() != null){
						dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
						dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
						if (!dataInicio.isBefore(dataFim))
							if (!dataInicio.isEqual(dataFim))
								error("A data de início deve ser antes da data fim.");
					}else
						error("As duas datas devem ser preenchidas.");
				} 
				setResponsePage(new ListaTitulosRelatorioConvenio(instituicao, titulo.getFiliado(), dataInicio, dataFim, titulo.getPracaProtesto(), tipoRelatorio));
			}
		};
		form.add(dataEnvioInicio());
		form.add(dataEnvioFinal());
		form.add(pracaProtesto());
		form.add(comboFiliado());
		form.add(tipoRelatorio());
		add(form);
	}

	private TextField<LocalDate> dataEnvioInicio() {
		dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		dataEnvioInicio.setLabel(new Model<String>("Período de Datas"));
		dataEnvioInicio.setRequired(true);
		return dataEnvioInicio;
	}
	
	private RadioChoice<TipoRelatorio> tipoRelatorio(){
		IChoiceRenderer<TipoRelatorio> renderer = new ChoiceRenderer<TipoRelatorio>("label");
		RadioChoice<TipoRelatorio> radio = new RadioChoice<>("tipoRelatorio", new Model<TipoRelatorio>(tipoRelatorio), Arrays.asList(TipoRelatorio.values()), renderer);
		radio.setRequired(true);
		radio.setLabel(new Model<String>("Situação dos Títulos"));
		return radio;
	}
	
	private TextField<LocalDate> dataEnvioFinal() {
		return dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}

	private DropDownChoice<Filiado> comboFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		DropDownChoice<Filiado> comboFiliado = new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
		return comboFiliado;		
	}
	private Component pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> comboMunicipio = new DropDownChoice<Municipio>("pracaProtesto" ,municipioMediator.getMunicipiosTocantins(), renderer);
		return comboMunicipio;
	}
	
	public TipoRelatorio getTipoRelatorio() {
		return tipoRelatorio;
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}