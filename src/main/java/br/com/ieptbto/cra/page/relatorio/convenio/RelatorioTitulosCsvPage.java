package br.com.ieptbto.cra.page.relatorio.convenio;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.beans.TituloCsvBean;
import br.com.ieptbto.cra.component.dataTable.CustomCraDataTable;
import br.com.ieptbto.cra.dataProvider.TituloCsvProvider;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.view.ViewTitulo;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioTitulosCsvPage extends BasePage<TituloRemessa> {

	@SpringBean
	RelatorioMediator relatorioMediator;
	@SpringBean
	TituloMediator tituloMediator;

	private static final long serialVersionUID = 1L;
	private List<ViewTitulo> titulos;

	public RelatorioTitulosCsvPage(SituacaoTituloRelatorio situacaoTitulo, TipoInstituicaoCRA tipoInstituicao, Instituicao instituicao, 
			Instituicao cartorio, LocalDate dataInicio, LocalDate dataFim) {
		this.titulos = relatorioMediator.relatorioTitulosPorSituacao(situacaoTitulo, tipoInstituicao, instituicao, cartorio, dataInicio, dataFim);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(tableTitulos());
	}

	private CustomCraDataTable<TituloCsvBean> tableTitulos() {
		List<IColumn<TituloCsvBean, String>> columns = new ArrayList<IColumn<TituloCsvBean, String>>();
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("BCO/CONV."), "apresentante"));
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("NOSSO N."), "nossoNumero") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("TÍTULO"), "numeroTitulo") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("PROTOC."), "numeroProtocoloCartorio") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("MUNICÍPIO"), "municipio"));
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("Nº DEV"), "numeroControleDevedor") {

			
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<TituloCsvBean>> item, String id, IModel<TituloCsvBean> model) {
				item.add(new Label(id, model.getObject().getNumeroControleDevedor()));
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("DEVEDOR"), "nomeDevedor"));
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("VALOR"), "saldoTitulo") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-right valor";
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("REMESSA"), "remessa") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center valor";
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("CONFIR."), "dataConfirmacao") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("RETORNO"), "retorno") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center valor";
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("DATA OC."), "dataOcorrencia") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloCsvBean, String>(new Model<String>("SITUAÇÃO"), "situacaoTitulo") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}

			@Override
			public void populateItem(Item<ICellPopulator<TituloCsvBean>> item, String componentId, IModel<TituloCsvBean> rowModel) {
				Label label = new Label(componentId, rowModel.getObject().getSituacaoTitulo());
				label.setMarkupId("info-titulo");
				label.setOutputMarkupId(true);
				item.add(label);
			}
		});
		TituloCsvProvider dataProvider = new TituloCsvProvider(TituloCsvBean.parseToListViewTitulo(titulos));
		return new CustomCraDataTable<TituloCsvBean>("table", columns, dataProvider , true);
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return null;
	}
}