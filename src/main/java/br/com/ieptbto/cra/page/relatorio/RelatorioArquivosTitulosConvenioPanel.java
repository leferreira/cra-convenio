package br.com.ieptbto.cra.page.relatorio;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unused")
public class RelatorioArquivosTitulosConvenioPanel extends Panel  {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioArquivosTitulosConvenioPanel.class);

	@SpringBean
	FiliadoMediator filiadoMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	RemessaMediator remessaMediator;
	
	private Instituicao instituicao;
	private IModel<Arquivo> model;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Municipio municipio;
	private Instituicao portador;
	private Usuario usuario;
	
	private DropDownChoice<Filiado> comboFiliado;
	private DropDownChoice<Municipio> comboMunicipio;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	
	public RelatorioArquivosTitulosConvenioPanel(String id, IModel<Arquivo> model, Instituicao instituicao, Usuario usuario) {
		super(id, model);
		this.model = model;
		this.usuario = usuario;
		this.instituicao = instituicao;
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(pracaProtesto());
		add(botaoEnviar());
		add(comboFiliado());
	}
	
	private Component botaoEnviar() {
		return new Button("botaoBuscar") {
			/** */
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
//				Arquivo arquivoBuscado = model.getObject();
				
				try {
					
					
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		};
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

	private DropDownChoice<Filiado> comboFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		comboFiliado = new DropDownChoice<Filiado>("filiado", new Model<Filiado>(),filiadoMediator.buscarListaFiliados(usuario.getInstituicao()), renderer);
		comboFiliado.setLabel(new Model<String>("Filiado"));
		comboFiliado.setRequired(true);
		return comboFiliado;		
	}
	private Component pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		this.comboMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(),municipioMediator.listarTodos(), renderer);
		return comboMunicipio;
	}
}