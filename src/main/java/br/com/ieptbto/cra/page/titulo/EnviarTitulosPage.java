package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class EnviarTitulosPage extends BasePage<TituloFiliado> {
	
	private static final Logger logger = Logger.getLogger(EnviarTitulosPage.class);
	private TituloFiliado titulo;
	private List<TituloFiliado> listaTitulosFiliado;
	
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	
	public EnviarTitulosPage() {
		this.titulo = new TituloFiliado();
		carregarFormEnviar();
		add(carregarTitulosParaEnvio());
	}

	public EnviarTitulosPage(String mensagem) {
		this.titulo = new TituloFiliado();
		info(mensagem);
		carregarFormEnviar();
		add(carregarTitulosParaEnvio());
	}
	
	private void carregarFormEnviar() {
		this.listaTitulosFiliado = getListaTitulosParaEnvio();
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel()){

			@Override
            protected void onSubmit(){
				try {
					tituloFiliadoMediator.enviarTitulosPendentes(listaTitulosFiliado);
				
					setResponsePage(new EnviarTitulosPage("Os títulos foram encaminhados com sucesso !"));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com o IEPTB-TO ! ");
				}
			}
		};
		add(form);
	}

	private ListView<TituloFiliado> carregarTitulosParaEnvio() {
		return new ListView<TituloFiliado>("listViewTitulos", listaTitulosFiliado) {

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();
				
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new Label("credor", tituloLista.getFiliado().getRazaoSocial()));
				
				Link<String> linkAlterar = new Link<String>("linkAlterar") {
					
					@Override
					public void onClick() {
						setResponsePage(new EntradaManualPage(tituloLista));
					}
				}; 
				linkAlterar.add(new Label("devedor", tituloLista.getNomeDevedor()));
				item.add(linkAlterar);
				
				item.add(new Label("dataEmissao", DataUtil.localDateToString(tituloLista.getDataEmissao())));
				item.add(new Label("dataVencimento", DataUtil.localDateToString(tituloLista.getDataVencimento())));
				item.add(new LabelValorMonetario<String>("valor", tituloLista.getValorTitulo()));
				item.add(removerTitulo(tituloLista));
			}
			
			private Component removerTitulo(final TituloFiliado titulo) {
				return new Link<Arquivo>("remover") {
					
					@Override
					public void onClick() {
						
						try {
							tituloFiliadoMediator.removerTituloFiliado(titulo);
							setResponsePage(new EnviarTitulosPage("O título foi removido com sucesso da CRA !"));
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
							error("Não foi possível remover o título ! Entre em contato com a CRA !");
						}
					}
				};
			}
		};
	}

	private List<TituloFiliado> getListaTitulosParaEnvio() {
		return tituloFiliadoMediator.titulosParaEnvioAoConvenio(usuarioFiliadoMediator.buscarEmpresaFiliadaDoUsuario(getUser()));
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
