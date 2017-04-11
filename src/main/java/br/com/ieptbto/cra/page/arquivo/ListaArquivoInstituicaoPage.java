package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.beans.ArquivoBean;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaArquivoInstituicaoPage extends BasePage<Arquivo> {

	@SpringBean
	private ArquivoMediator arquivoMediator;
	@SpringBean
	private DownloadMediator downloadMediator;
	
	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private Arquivo arquivo;
	private ArquivoBean arquivoFormBean;

	public ListaArquivoInstituicaoPage(ArquivoBean arquivoFormBean, Usuario usuario) {
		this.arquivo = new Arquivo();
		this.arquivoFormBean = arquivoFormBean;
		this.usuario = getUser();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		listaArquivos();
	}

	private void listaArquivos() {
		add(new ListView<Arquivo>("dataTableArquivo", buscarArquivos()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Arquivo> item) {
				final Arquivo arquivo = item.getModelObject();
				item.add(new Label("tipoArquivo", arquivo.getTipoArquivo().getTipoArquivo().constante));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoInstituicaoPage(arquivo));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
				linkArquivo.setEnabled(false);
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(arquivo.getDataEnvio())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(arquivo.getHoraEnvio())));
				item.add(new Label("instituicao", arquivo.getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", arquivo.getInstituicaoRecebe().getNomeFantasia()));
				WebMarkupContainer divInfo = new WebMarkupContainer("divInfo");
				divInfo.add(new AttributeAppender("id", arquivo.getStatusArquivo().getStatusDownload().getLabel()));
				divInfo.add(new Label("status", arquivo.getStatusArquivo().getStatusDownload().getLabel().toUpperCase()));
				item.add(divInfo);
				item.add(downloadArquivoTXT(arquivo));
			}

			private Link<Arquivo> downloadArquivoTXT(final Arquivo arquivo) {
				return new Link<Arquivo>("downloadArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							File file = downloadMediator.baixarArquivoTXT(getUser(), arquivo);
							IResourceStream resourceStream = new FileResourceStream(file);

							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! Favor entrar em contato com a CRA...");
							logger.info(e.getMessage(), e);
						}
					}
				};
			}
		});
	}

	public IModel<List<Arquivo>> buscarArquivos() {
		return new LoadableDetachableModel<List<Arquivo>>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected List<Arquivo> load() {
				ArquivoBean bean = getArquivoFormBean();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				if (bean.getDataInicio() != null) {
					dataInicio = new LocalDate(bean.getDataInicio());
				}
				if (bean.getDataFim() != null) {
					dataFim = new LocalDate(bean.getDataFim());
				}
				return arquivoMediator.buscarArquivos(usuario, bean.getNomeArquivo(), dataInicio, dataFim, bean.getTipoInstituicao(), bean.getBancoConvenio(),
						bean.getTiposArquivos(), bean.getSituacoesArquivos());
			}
		};
	}

	public ArquivoBean getArquivoFormBean() {
		return arquivoFormBean;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}