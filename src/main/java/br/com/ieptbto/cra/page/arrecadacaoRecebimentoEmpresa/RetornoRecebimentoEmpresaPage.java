package br.com.ieptbto.cra.page.arrecadacaoRecebimentoEmpresa;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araujo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RetornoRecebimentoEmpresaPage extends BasePage<Arquivo> {
	
	@SpringBean
	ArquivoMediator arquivoMediator;
	@SpringBean
	DownloadMediator downloadMediator;
	
	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private TextField<String> textFieldDataInicio;
	private TextField<String> textFieldDataFim;
	private List<Arquivo> litViewArquivos;

	public RetornoRecebimentoEmpresaPage() {
		this.usuario = getUser();
		this.litViewArquivos = new ArrayList<Arquivo>();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formBuscarArquivo());
		add(divResultadoBusca());
	}

	private Form<Void>  formBuscarArquivo() {
		Form<Void> form = new Form<Void>("form");
		form.add(textFieldDataInicio());
		form.add(textFieldDataFim());
		form.add(new AjaxButton("buttonSearch"){
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (isCamposInvalidos()) {
					target.add(this.getPage());
					return;
				}
				
				LocalDate dataInicio = DataUtil.stringToLocalDate(textFieldDataInicio.getModelObject());
				LocalDate dataFim = DataUtil.stringToLocalDate(textFieldDataFim.getModelObject());
				List<Arquivo> arquivos = arquivoMediator.buscarRetornoParaLayoutRecebimentoEmpresa(usuario, dataInicio, dataFim);
				litViewArquivos.clear();
				litViewArquivos.addAll(arquivos);
				target.add(this.getPage());
			}

			private boolean isCamposInvalidos() {
				String valueDataInicio = textFieldDataInicio.getModelObject();
				String valueDataFim = textFieldDataFim.getModelObject();
				
				if (!StringUtils.isEmpty(valueDataInicio)) {
					if (!StringUtils.isEmpty(valueDataFim)) {
						LocalDate dataInicio = DataUtil.stringToLocalDate(valueDataInicio);
						LocalDate dataFim = DataUtil.stringToLocalDate(valueDataFim);
						if (!dataInicio.isBefore(dataFim) && !dataInicio.isEqual(dataFim)) {
							error("A data de início deve ser antes da data fim...");
							return true;
						}
					} else {
						error("As duas datas devem ser preenchidas...");
						return true;
					}
				} else {
					error("As duas datas devem ser preenchidas...");
					return true;
				}
				return false;
			}
		});  
		return form;
	}

	private TextField<String> textFieldDataInicio() {
		textFieldDataInicio = new TextField<String>("dataInicio", new Model<String>());
		textFieldDataInicio.setRequired(true);
		textFieldDataInicio.setLabel(new Model<String>("Data Início"));
		return textFieldDataInicio;
	}
	
	private TextField<String> textFieldDataFim() {
		return textFieldDataFim = new TextField<String>("dataFim", new Model<String>());
	}
	
	private ListView<Arquivo> divResultadoBusca() {
		return new ListView<Arquivo>("listaResultado", litViewArquivos) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Arquivo> item) {
				final Arquivo arquivo = item.getModelObject();
				item.add(downloadArquivoTXT(arquivo));
				item.add(new Label("instituicao", usuario.getInstituicao().getNomeFantasia()));
				item.add(new Label("dataEnvio", DataUtil.localDateToString(arquivo.getDataEnvio())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(arquivo.getHoraEnvio())));
				item.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
				item.add(new Label("envio", "CRA"));
				item.add(new Label("destino", usuario.getInstituicao().getNomeFantasia()));
				WebMarkupContainer divInfo = new WebMarkupContainer("divInfo");
				divInfo.add(new AttributeAppender("id", arquivo.getStatusArquivo().getStatusDownload().getLabel()));
				divInfo.add(new Label("status", arquivo.getStatusArquivo().getStatusDownload().getLabel().toUpperCase()));
				item.add(divInfo);
			}
			
			private Link<Arquivo> downloadArquivoTXT(final Arquivo arquivo) {
				return new Link<Arquivo>("downloadArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							File file = downloadMediator.baixarRetornoRecebimentoEmpresa(usuario, arquivo);
							if (file == null) {
								throw new InfraException("O arquivo solicitado não contém nenhuma ocorrência de pagamento de título...");
							}
							IResourceStream resourceStream = new FileResourceStream(file);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							getFeedbackPanel().error("Não foi possível fazer o download do arquivo ! Favor entrar em contato com o IEPTB-TO...");
						}
					}
				};
			}
		};
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return null;
	}
}
