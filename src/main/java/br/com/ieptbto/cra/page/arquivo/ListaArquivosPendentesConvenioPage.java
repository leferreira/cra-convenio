package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Thasso Araújo
 *
 */
public class ListaArquivosPendentesConvenioPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private RemessaMediator remessaMediator;
	@SpringBean
	private RelatorioMediator relatorioMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	private Arquivo arquivo;
	private List<Remessa> remessasPendentes;
	private List<DesistenciaProtesto> desistenciasProtesto;
	private List<CancelamentoProtesto> cancelamentoProtestos;
	private List<AutorizacaoCancelamento> autorizacaoCancelamento;
	
	public ListaArquivosPendentesConvenioPage(Usuario user, List<Remessa> remessas) {
		this.arquivo = new Arquivo();
		this.remessasPendentes = remessas;
		
		add(carregarListaArquivos());
		add(carregarListaArquivosDesistenciaProtesto());
		add(carregarListaArquivosCancelamentoProtesto());
		add(carregarListaAutorizacao());
	}
	
	private ListView<Remessa> carregarListaArquivos() {
		return new ListView<Remessa>("dataTableRemessa", getRemessas()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(downloadArquivoTXT(remessa));
				item.add(relatorioArquivo(remessa));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoConvenioPage(remessa.getArquivo()));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
				
				if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
					item.add(new Label("instituicao", remessa.getInstituicaoOrigem().getNomeFantasia()));
					item.add(new Label("envio", remessa.getArquivo().getInstituicaoRecebe().getNomeFantasia()));
					item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
				} else {
					item.add(new Label("instituicao", remessa.getInstituicaoDestino().getNomeFantasia()));
					item.add(new Label("envio", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
					item.add(new Label("destino", remessa.getArquivo().getInstituicaoRecebe().getNomeFantasia()));
				}
				
				item.add(new LabelValorMonetario<BigDecimal>("valor", remessa.getRodape().getSomatorioValorRemessa()));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(remessa.getArquivo().getHoraEnvio())));
				item.add(new Label("status", remessa.getStatusRemessa().getLabel().toUpperCase()).setMarkupId(remessa.getStatusRemessa().getLabel()));
			}

			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							File file = remessaMediator.baixarRemessaTXT(getUser(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);
							
							getRequestCycle().scheduleRequestHandlerAfterCurrent(
									new ResourceStreamRequestHandler(resourceStream, file.getName()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};
			}
			
			private Link<Remessa> relatorioArquivo(final Remessa remessa) {
				return new Link<Remessa>("gerarRelatorio") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						TipoArquivoEnum tipoArquivo = remessa.getArquivo().getTipoArquivo().getTipoArquivo();
						JasperPrint jasperPrint = null;

						try {
							if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
								JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRemessa.jrxml"));
								jasperPrint = relatorioMediator.relatorioRemessa(jasperReport ,remessa, getUser().getInstituicao());
							} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
								JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioConfirmação.jrxml"));
								jasperPrint = relatorioMediator.relatorioConfirmacao(jasperReport, remessa, getUser().getInstituicao());
							} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
								JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRetorno.jrxml"));
								jasperPrint = relatorioMediator.relatorioRetorno(jasperReport ,remessa, getUser().getInstituicao());
							}
							
							File pdf = File.createTempFile("report", ".pdf");
							JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
							IResourceStream resourceStream = new FileResourceStream(pdf);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(
							        new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_" + remessa.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
						} catch (InfraException ex) { 
							error(ex.getMessage());
						} catch (Exception e) { 
							error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
							e.printStackTrace();
						}
					}
				};
			}
		};
	}

	private ListView<DesistenciaProtesto> carregarListaArquivosDesistenciaProtesto() {
		return new ListView<DesistenciaProtesto>("dataTableDesistencia", getDesistenciasProtesto()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<DesistenciaProtesto> item) {
				final DesistenciaProtesto desistenciaProtesto = item.getModelObject();
				item.add(downloadArquivoTXT(desistenciaProtesto));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						// setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(desistenciaProtesto.getRemessaDesistenciaProtesto().getCabecalho().getDataMovimento())));
				item.add(new Label("instituicao", desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("envio", desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(desistenciaProtesto.getCabecalhoCartorio().getCodigoMunicipio()).getNomeFantasia()));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getHoraEnvio())));
				item.add(new Label("status", verificaDownload(desistenciaProtesto).getLabel().toUpperCase()).setMarkupId(verificaDownload(desistenciaProtesto).getLabel()));
			}

			private Link<Remessa> downloadArquivoTXT(final DesistenciaProtesto desistenciaProtesto) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = remessaMediator.baixarDesistenciaTXT(getUser(), desistenciaProtesto);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
			
			private StatusRemessa verificaDownload(DesistenciaProtesto desistencia) {
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					return StatusRemessa.ENVIADO;
				}
				if (desistencia.getDownload().equals(false)) {
					return StatusRemessa.AGUARDANDO;
				}
				return StatusRemessa.RECEBIDO;
			}
		};
	}

	
	private ListView<CancelamentoProtesto> carregarListaArquivosCancelamentoProtesto() {
		return new ListView<CancelamentoProtesto>("dataTableCancelamento", getCancelamentoProtestos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<CancelamentoProtesto> item) {
				final CancelamentoProtesto cancelamento = item.getModelObject();
				item.add(downloadArquivoTXT(cancelamento));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						// setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", cancelamento.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(cancelamento.getRemessaCancelamentoProtesto().getCabecalho().getDataMovimento())));
				item.add(new Label("instituicao", cancelamento.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("envio", cancelamento.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(cancelamento.getCabecalhoCartorio().getCodigoMunicipio()).getNomeFantasia()));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(cancelamento.getRemessaCancelamentoProtesto().getArquivo().getHoraEnvio())));
				item.add(new Label("status", verificaDownload(cancelamento).getLabel().toUpperCase()).setMarkupId(verificaDownload(cancelamento).getLabel()));
			}

			private Link<Remessa> downloadArquivoTXT(final CancelamentoProtesto cancelamento) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = remessaMediator.baixarCancelamentoTXT(getUser(), cancelamento);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
			
			private StatusRemessa verificaDownload(CancelamentoProtesto cancelamento) {
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					return StatusRemessa.ENVIADO;
				}
				if (cancelamento.getDownload().equals(false)) {
					return StatusRemessa.AGUARDANDO;
				}
				return StatusRemessa.RECEBIDO;
			}
		};
	}
	
	private ListView<AutorizacaoCancelamento> carregarListaAutorizacao() {
		return new ListView<AutorizacaoCancelamento>("dataTableAutorizacao", getAutorizacaoCancelamento()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<AutorizacaoCancelamento> item) {
				final AutorizacaoCancelamento ac = item.getModelObject();
				item.add(downloadArquivoTXT(ac));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						// setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", ac.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(ac.getRemessaAutorizacaoCancelamento().getCabecalho().getDataMovimento())));
				item.add(new Label("instituicao", ac.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("envio", ac.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(ac.getCabecalhoCartorio().getCodigoMunicipio()).getNomeFantasia()));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(ac.getRemessaAutorizacaoCancelamento().getArquivo().getHoraEnvio())));
				item.add(new Label("status", verificaDownload(ac).getLabel().toUpperCase()).setMarkupId(verificaDownload(ac).getLabel()));
			}

			private Link<Remessa> downloadArquivoTXT(final AutorizacaoCancelamento ac) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = remessaMediator.baixarAutorizacaoTXT(getUser(), ac);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
			
			private StatusRemessa verificaDownload(AutorizacaoCancelamento ac) {
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					return StatusRemessa.ENVIADO;
				}
				if (ac.getDownload().equals(false)) {
					return StatusRemessa.AGUARDANDO;
				}
				return StatusRemessa.RECEBIDO;
			}
		};
	}
	
	public List<Remessa> getRemessas() {
		if (remessasPendentes == null) {
			remessasPendentes = new ArrayList<Remessa>();
		}
		return remessasPendentes;
	}

	public List<DesistenciaProtesto> getDesistenciasProtesto() {
		if (desistenciasProtesto == null) {
			desistenciasProtesto = new ArrayList<DesistenciaProtesto>();
		}
		return desistenciasProtesto;
	}
	
	public List<AutorizacaoCancelamento> getAutorizacaoCancelamento() {
		if (autorizacaoCancelamento == null) {
			autorizacaoCancelamento = new ArrayList<AutorizacaoCancelamento>();
		}
		return autorizacaoCancelamento;
	}
	
	public List<CancelamentoProtesto> getCancelamentoProtestos() {
		if (cancelamentoProtestos == null) {
			cancelamentoProtestos = new ArrayList<CancelamentoProtesto>();
		}
		return cancelamentoProtestos;
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
