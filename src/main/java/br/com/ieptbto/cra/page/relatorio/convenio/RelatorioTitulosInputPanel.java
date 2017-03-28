package br.com.ieptbto.cra.page.relatorio.convenio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.TipoExportacaoRelatorio;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

public class RelatorioTitulosInputPanel extends Panel {

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	FiliadoMediator filiadoMediator;

	private static final long serialVersionUID = 1L;
	private Instituicao instituicao;

	public RelatorioTitulosInputPanel(String id, IModel<TituloConvenioBean> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao = instituicao;
		add(labelFiliados());
		add(dropDownFiliados());
		add(dateFieldDataInicio());
		add(dateFieldDataFinal());
		add(radioTipoExportacao());
		add(situcaoTituloRelatorio());
		add(dropDownTipoInstituicao());
		add(dropDownBancoConvenios());
		add(dropDownCartorio());
	}

	private DateTextField dateFieldDataInicio() {
		DateTextField dataEnvioInicio = new DateTextField("dataInicio");
		dataEnvioInicio.setLabel(new Model<String>("Período de datas"));
		dataEnvioInicio.setMarkupId("date");
		dataEnvioInicio.setRequired(true);
		return dataEnvioInicio;
	}

	private DateTextField dateFieldDataFinal() {
		DateTextField dataEnvioFinal = new DateTextField("dataFim");
		dataEnvioFinal.setMarkupId("date1");
		return dataEnvioFinal;
	}

	private RadioGroup<String> radioTipoExportacao() {
		RadioGroup<String> radioExportacao = new RadioGroup<String>("tipoExportacao");
		radioExportacao.setLabel(new Model<String>("Tipo Exportação Relatório"));
		radioExportacao.setRequired(true);
		radioExportacao.add(new Radio<String>("pdf", new Model<String>(TipoExportacaoRelatorio.PDF.toString())).setEnabled(false));
		radioExportacao.add(new Radio<String>("csv", new Model<String>(TipoExportacaoRelatorio.CSV.toString())));
		return radioExportacao;
	}

	private RadioGroup<SituacaoTituloRelatorio> situcaoTituloRelatorio() {
		RadioGroup<SituacaoTituloRelatorio> radioSituacaoTituloRelatorio = new RadioGroup<SituacaoTituloRelatorio>("situacaoTitulosRelatorio");
		radioSituacaoTituloRelatorio.setLabel(new Model<String>("Situação dos Títulos"));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("todos", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.GERAL)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("semConfirmacao", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.SEM_CONFIRMACAO)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("comConfirmacao", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.COM_CONFIRMACAO)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("comRetorno", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.COM_RETORNO)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("pagos", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.PAGOS)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("protestados", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.PROTESTADOS)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("retiradosDevolvidos", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("desistencia", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.DESISTÊNCIA_DE_PROTESTO)).setEnabled(false));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("cancelamento", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.CANCELAMENTO_DE_PROTESTO)).setEnabled(false));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("semRetorno", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.AUTORIZACAO_CANCELAMENTO)).setEnabled(false));
		radioSituacaoTituloRelatorio.setRequired(true);
		return radioSituacaoTituloRelatorio;
	}

	private DropDownChoice<Instituicao> dropDownBancoConvenios() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		DropDownChoice<Instituicao> dropDownInstituicao = new DropDownChoice<Instituicao>("bancoConvenio", instituicaoMediator.getConvenios(), renderer);
		dropDownInstituicao.setLabel(new Model<String>("Portador"));
		dropDownInstituicao.setOutputMarkupId(true);
		dropDownInstituicao.setEnabled(false);
		return dropDownInstituicao;
	}

	private DropDownChoice<TipoInstituicaoCRA> dropDownTipoInstituicao() {
		IChoiceRenderer<TipoInstituicaoCRA> renderer = new ChoiceRenderer<TipoInstituicaoCRA>("label");
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>(Arrays.asList(TipoInstituicaoCRA.CONVENIO));
		DropDownChoice<TipoInstituicaoCRA> tipoInstituicao = new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao", choices, renderer);
		tipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
		tipoInstituicao.setEnabled(false);
		return tipoInstituicao;
	}

	private DropDownChoice<Instituicao> dropDownCartorio() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio");
		DropDownChoice<Instituicao> dropDownCartorio = new DropDownChoice<Instituicao>("cartorio", instituicaoMediator.getCartorios(), renderer);
		dropDownCartorio.setOutputMarkupId(true);
		return dropDownCartorio;
	}
	
	private Label labelFiliados() {
		Label label = new Label("labelEmpresasFiliadas");
		label.setVisible(instituicao.getAdministrarEmpresasFiliadas());
		return label;
	}

	private DropDownChoice<Filiado> dropDownFiliados() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		DropDownChoice<Filiado> campoFiliado = new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarListaFiliados(instituicao), renderer);
		campoFiliado.setLabel(new Model<String>("Filiado"));
		campoFiliado.setOutputMarkupId(true);
		campoFiliado.setVisible(instituicao.getAdministrarEmpresasFiliadas());
		return campoFiliado;
	}
}