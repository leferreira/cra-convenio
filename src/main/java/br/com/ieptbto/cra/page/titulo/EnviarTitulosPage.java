package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN,CraRoles.SUPER })
public class EnviarTitulosPage extends BasePage<TituloRemessa> {
	
	/***/
	private static final long serialVersionUID = 1L;
	private TituloRemessa titulo;
	private List<TituloRemessa> listaTitulosParaEnvio;
	
	@SpringBean
	TituloMediator tituloMediator;
	
	public EnviarTitulosPage() {
		this.titulo = new TituloRemessa();
		setListaTitulosParaEnvio(tituloMediator.titulosParaEnvioAoConvenio());
		carregarFormEnviar();
		add(carregarTitulosParaEnvio());
	}

	public EnviarTitulosPage(String mensagem) {
		this.titulo = new TituloRemessa();
		setListaTitulosParaEnvio(tituloMediator.titulosParaEnvioAoConvenio());
		info(mensagem);
		carregarFormEnviar();
		add(carregarTitulosParaEnvio());
	}
	
	private void carregarFormEnviar() {
		Form<TituloRemessa> form = new Form<TituloRemessa>("form", getModel()){
			/***/
			private static final long serialVersionUID = 1L;
			@Override
            protected void onSubmit(){
				
			}
		};
		add(form);
	}

	private ListView<TituloRemessa> carregarTitulosParaEnvio() {
		return new ListView<TituloRemessa>("listViewTitulos", getListaTitulosParaEnvio()) {
			/***/
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("rawtypes")
			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();
				
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
		        item.add(new Label("nomeArquivo", tituloLista.getRemessa().getArquivo().getNomeArquivo()));
		        
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloLista.getConfirmacao().getRemessa().getDataRecebimento())));
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				} else { 
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(new Label("valorTitulo", tituloLista.getValorTitulo()));
				Link linkHistorico = new Link("linkHistorico") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
		            }
		        };
		        linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		        item.add(linkHistorico);
		        if (tituloLista.getRetorno() != null){
	        		item.add(new Label("retorno", tituloLista.getRetorno().getRemessa().getArquivo().getNomeArquivo()));
	        		item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getRetorno().getDataOcorrencia())));
		        } else {
		        	item.add(new Label("retorno", StringUtils.EMPTY));
		        	item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getDataOcorrencia())));
		        }
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}
	

	private List<TituloRemessa> getListaTitulosParaEnvio() {
		return listaTitulosParaEnvio;
	}

	private void setListaTitulosParaEnvio(List<TituloRemessa> listaTitulosParaEnvio) {
		this.listaTitulosParaEnvio = listaTitulosParaEnvio;
	}

	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}
}
