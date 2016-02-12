package br.com.ieptbto.cra.page.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JRException;
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
public class ListaTitulosRelatorioFiliado extends BasePage<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	private TituloFiliado titulo;
	private Filiado empresaFiliado;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Municipio pracaProtesto;
	private List<TituloFiliadoJRDataSource> listaTitulosRelatorio;
	private List<TituloFiliado> listaTitulos;
	
	public ListaTitulosRelatorioFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim, SituacaoTituloRelatorio tipoRelatorio, Municipio pracaProtesto) {
		this.titulo = new TituloFiliado();
		this.empresaFiliado = filiado;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.pracaProtesto = pracaProtesto;
		this.listaTitulos = tituloFiliadoMediator.buscarTitulosParaRelatorioFiliado(filiado, dataInicio, dataFim, tipoRelatorio, pracaProtesto);
		
		adicionarFormulario(); 
		carregarListaTitulos();
	}
	
	private void adicionarFormulario() {
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel()){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				try {
					JasperPrint jasperPrint = novoRelatorioDeTitulosPorFiliado(empresaFiliado, dataInicio, dataFim, pracaProtesto, getListaTitulosRelatorio());
					
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
							new ResourceStreamRequestHandler(resourceStream, "CRA_Titulos_Enviados.pdf"));
				} catch (InfraException ex) { 
					error(ex.getMessage());
				} catch (Exception e) { 
					error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
					System.out.println(e);
				}
			}
		};
		add(form);
	}

	
	private void carregarListaTitulos() {
		ListView<TituloFiliado> listaViewTitulos = new ListView<TituloFiliado>("listViewTitulos", getListaTitulos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();

				TituloRemessa tituloRemessa = tituloFiliadoMediator.buscarTituloDoConvenioNaCra(tituloLista);
				ListaTitulosRelatorioFiliado.this.parseToTituloFiliadoJRDataSource(tituloLista, tituloRemessa);
				
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("emissao", DataUtil.localDateToString(tituloLista.getDataEmissao())));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new LabelValorMonetario<BigDecimal>("valor", tituloLista.getValorTitulo()));
				
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
					item.add(new Label("protocolo", StringUtils.EMPTY));
					item.add(new Label("dataSituacao", StringUtils.EMPTY));
					item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTituloConvenio().getSituacao().toUpperCase()));
				} else {
					if (tituloRemessa.getConfirmacao() != null) {
						item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
					} else { 
						item.add(new Label("protocolo", StringUtils.EMPTY));
					}
					
			        if (tituloRemessa.getRetorno() != null){
		        		item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
			        } else {
			        	item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getDataOcorrencia())));
			        }
					item.add(new Label("situacaoTitulo", tituloRemessa.getSituacaoTitulo().toUpperCase()));
				}
			}
		};
		add(listaViewTitulos);
	}
	
	private void parseToTituloFiliadoJRDataSource(TituloFiliado tituloLista, TituloRemessa tituloRemessa) {
		TituloFiliadoJRDataSource tituloFiliado = new TituloFiliadoJRDataSource();
		tituloFiliado.parseTituloFiliado(tituloLista, tituloRemessa);
		getListaTitulosRelatorio().add(tituloFiliado);
	}
	

	private JasperPrint novoRelatorioDeTitulosPorFiliado(Filiado filiado, LocalDate dataInicio, 
			LocalDate dataFim,	Municipio pracaProtesto, List<TituloFiliadoJRDataSource> beans) throws JRException {
		
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		
		if (beans.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");

		parametros.put("FILIADO", filiado.getRazaoSocial());
		parametros.put("PRACA", filiado.getRazaoSocial());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioTituloFiliado.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public List<TituloFiliado> getListaTitulos() {
		if (listaTitulos == null) {
			listaTitulos = new ArrayList<TituloFiliado>();
		}
		return listaTitulos;
	}
	
	public List<TituloFiliadoJRDataSource> getListaTitulosRelatorio() {
		if (listaTitulosRelatorio == null) {
			listaTitulosRelatorio = new ArrayList<TituloFiliadoJRDataSource>();
		}
		return listaTitulosRelatorio;
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
