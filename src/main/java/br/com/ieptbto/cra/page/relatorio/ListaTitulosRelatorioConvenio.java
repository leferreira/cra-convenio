package br.com.ieptbto.cra.page.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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
@SuppressWarnings("serial")
public class ListaTitulosRelatorioConvenio extends BasePage<TituloFiliado> {

	private TituloFiliado titulo;
	private Instituicao convenio;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Municipio pracaProtesto;
	private Filiado filiado;
	private List<TituloFiliadoJRDataSource> listaRelatorio;
	private List<TituloFiliado> listaTitulos;
	
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	
	public ListaTitulosRelatorioConvenio(Instituicao convenio, Filiado filiado, LocalDate dataInicio, LocalDate dataFim, Municipio pracaProtesto) {
		this.titulo = new TituloFiliado();
		this.convenio = convenio;
		this.filiado = filiado;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.pracaProtesto = pracaProtesto;
		
		setListaTitulos(tituloFiliadoMediator.buscarTitulosParaRelatorioConvenio(convenio, filiado,dataInicio, dataFim, pracaProtesto));
		add(carregarListaTitulos());
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel());
		form.add(botaoEnviar());
		add(form);
	}
	
	private Button botaoEnviar() {
		return new Button("botaoBuscar") {

			@Override
			public void onSubmit() {
				try {
					JasperPrint jasperPrint = novoRelatorioDeTitulosPorConvenio(convenio, filiado, dataInicio, dataFim, pracaProtesto, getListaRelatorio());

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
	}
	
	private ListView<TituloFiliado> carregarListaTitulos() {
		return new ListView<TituloFiliado>("listViewTitulos", getListaTitulos()) {

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();
				
				TituloRemessa tituloRemessa = tituloFiliadoMediator.buscarTituloDoConvenioNaCra(tituloLista); 
				ListaTitulosRelatorioConvenio.this.parseToTituloFiliadoJRDataSource(tituloLista, tituloRemessa);
				
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("emissao", DataUtil.localDateToString(tituloLista.getDataEmissao())));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new LabelValorMonetario<BigDecimal>("valor", tituloLista.getValorTitulo()));
				item.add(new Label("filiado", tituloLista.getFiliado().getRazaoSocial()));
				
				Link<TituloFiliado> linkHistorico = new Link<TituloFiliado>("linkHistorico") {
					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
		            }
		        };
		        linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		        item.add(linkHistorico);
				
				if (tituloRemessa == null) {
					item.add(new Label("protocolo", StringUtils.EMPTY));
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
	}
	
	private void parseToTituloFiliadoJRDataSource(TituloFiliado tituloLista, TituloRemessa tituloRemessa) {
		TituloFiliadoJRDataSource tituloFiliado = new TituloFiliadoJRDataSource();
		tituloFiliado.parseTituloFiliado(tituloLista, tituloRemessa);
		getListaRelatorio().add(tituloFiliado);
	}
	
	private JasperPrint novoRelatorioDeTitulosPorConvenio(Instituicao convenio,Filiado filiado, LocalDate dataInicio, 
			LocalDate dataFim,	Municipio pracaProtesto, List<TituloFiliadoJRDataSource> listaTitulos) throws JRException {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		
		if (listaTitulos.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");

		parametros.put("CONVENIO", convenio.getNomeFantasia());
		parametros.put("FILIADO", filiado.getRazaoSocial());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(listaTitulos);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioTituloConvenio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public List<TituloFiliadoJRDataSource> getListaRelatorio() {
		if (listaRelatorio == null) {
			listaRelatorio = new ArrayList<TituloFiliadoJRDataSource>();
		}
		return listaRelatorio;
	}
	
	public void setListaRelatorio(List<TituloFiliadoJRDataSource> listaRelatorio) {
		this.listaRelatorio = listaRelatorio;
	}
	
	public List<TituloFiliado> getListaTitulos() {
		return listaTitulos;
	}
	
	public void setListaTitulos(List<TituloFiliado> listaTitulos) {
		this.listaTitulos = listaTitulos;
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}

}
