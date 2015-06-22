package br.com.ieptbto.cra.page.relatorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unused")
public class ListaTitulosRelatorioFiliado extends BasePage<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;

	private TituloFiliado titulo;
	private Filiado empresaFiliado;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Municipio pracaProtesto;
	private List<TituloFiliadoJRDataSource> listaRelatorio = new ArrayList<TituloFiliadoJRDataSource>();
	
	private List<TituloFiliado> listaTitulos;
	
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	
	public ListaTitulosRelatorioFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim, Municipio pracaProtesto) {
		this.titulo = new TituloFiliado();
		this.empresaFiliado = filiado;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.pracaProtesto = pracaProtesto;
		setListaTitulos(tituloFiliadoMediator.buscarTitulosParaRelatorioFiliado(filiado, dataInicio, dataFim, pracaProtesto));
		add(carregarListaTitulos());
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel());
		form.add(botaoEnviar());
		add(form);
	}
	
	private Component botaoEnviar() {
		return new Button("botaoBuscar") {
			/** */
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				try {
					JasperPrint jasperPrint = novoRelatorioDeTitulosPorFiliado(empresaFiliado, dataInicio, dataFim, pracaProtesto, listaTitulos);
					getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
				} catch (JRException e) {
					e.printStackTrace();
					error("Não foi possível gerar o relatório !");
				}
			}
		};
	}
	
	private ListView<TituloFiliado> carregarListaTitulos() {
		return new ListView<TituloFiliado>("listViewTitulos", getListaTitulos()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();
				TituloRemessa tituloRemessa = tituloFiliadoMediator.buscarTituloDoConvenioNaCra(tituloLista);
				TituloFiliadoJRDataSource tituloFiliado = new TituloFiliadoJRDataSource();
				
				tituloFiliado.setNumeroTitulo(tituloLista.getNumeroTitulo());
				tituloFiliado.setDataEmissao(tituloLista.getDataEmissao());
				tituloFiliado.setPracaProtesto(tituloLista.getPracaProtesto().getNomeMunicipio());
				tituloFiliado.setValorTitulo(tituloLista.getValorTitulo());
				tituloFiliado.setFiliado(tituloLista.getFiliado().getRazaoSocial());
				tituloFiliado.setNomeDevedor(tituloLista.getNomeDevedor());
				
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("emissao", DataUtil.localDateToString(tituloLista.getDataEmissao())));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new LabelValorMonetario<String>("valor", tituloLista.getValorTitulo()));
				
				Link<TituloFiliado> linkHistorico = new Link<TituloFiliado>("linkHistorico") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
		            }
		        };
		        linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		        item.add(linkHistorico);
				
				if (tituloRemessa == null) {
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					item.add(new Label("protocolo", StringUtils.EMPTY));
					item.add(new Label("dataSituacao", StringUtils.EMPTY));
					item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTituloConvenio().getSituacao()));
					tituloFiliado.setSituacaoTituloConvenio(tituloLista.getSituacaoTituloConvenio().getSituacao());
				} else {
					if (tituloRemessa.getConfirmacao() != null) {
						tituloFiliado.setDataConfirmacao(tituloRemessa.getConfirmacao().getRemessa().getDataRecebimento());
						tituloFiliado.setNomeDevedor(tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio());
						item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getDataRecebimento())));
						item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
					} else { 
						item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
						item.add(new Label("protocolo", StringUtils.EMPTY));
					}
					
			        if (tituloRemessa.getRetorno() != null){
			        	tituloFiliado.setDataSitucao(tituloRemessa.getRetorno().getDataOcorrencia());
		        		item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
			        } else {
			        	tituloFiliado.setDataSitucao(tituloRemessa.getDataOcorrencia());
			        	item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getDataOcorrencia())));
			        }
					item.add(new Label("situacaoTitulo", tituloRemessa.getSituacaoTitulo()));
					tituloFiliado.setSituacaoTituloConvenio(tituloRemessa.getSituacaoTitulo());	
				}
				getListaRelatorio().add(tituloFiliado);
			}
		};
	}
	
	public List<TituloFiliadoJRDataSource> getListaRelatorio() {
		return listaRelatorio;
	}

	public List<TituloFiliado> getListaTitulos() {
		return listaTitulos;
	}

	public void setListaTitulos(List<TituloFiliado> listaTitulos) {
		this.listaTitulos = listaTitulos;
	}
	
	private JasperPrint novoRelatorioDeTitulosPorFiliado(Filiado filiado, LocalDate dataInicio, 
			LocalDate dataFim,	Municipio pracaProtesto, List<TituloFiliado> beans) throws JRException {
		
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		
		if (beans.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");

		parametros.put("FILIADO", filiado.getRazaoSocial());
		parametros.put("PRACA", filiado.getRazaoSocial());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioTituloFiliado.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
