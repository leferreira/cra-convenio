package br.com.ieptbto.cra.page.titulo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class BuscarTitulosConvenioPage extends BasePage<TituloFiliado>{

	@SpringBean
	FiliadoMediator filiadoMediator;
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	
	private TituloFiliado titulo;
	private TituloFiliado tituloBuscado;
	private TextField<String> textFieldDataEmissao;

	public BuscarTitulosConvenioPage() {
		this.titulo = new TituloFiliado();
		this.tituloBuscado = new TituloFiliado();
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel());
		form.add(new Button("botaoEnviar"){

			@Override
			public void onSubmit() {
				tituloBuscado = titulo;
				
				if (textFieldDataEmissao.getModelObject() != null)
					tituloBuscado.setDataEmissao(DataUtil.stringToLocalDate(textFieldDataEmissao.getModelObject()));
			}
		});	
		form.add(numeroTitulo());
		form.add(pracaProtesto());
		form.add(nomeDevedor());
		form.add(documentoDevedor()); 
		form.add(comboFiliado());
		form.add(campoData());
		add(form);
		add(carregarListaTitulos());
	}
	
	private ListView<TituloFiliado> carregarListaTitulos() {
		return new ListView<TituloFiliado>("listViewTitulos", buscarTitulos()) {

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();
				
				TituloRemessa tituloRemessa = tituloFiliadoMediator.buscarTituloDoConvenioNaCra(tituloLista); 
				
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("emissao", DataUtil.localDateToString(tituloLista.getDataEmissao())));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new LabelValorMonetario<String>("valor", tituloLista.getValorTitulo()));
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
	}
	
	private IModel<List<TituloFiliado>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloFiliado>>() {

			@Override
			protected List<TituloFiliado> load() {
				if (tituloBuscado.getNumeroTitulo() == null && tituloBuscado.getNomeDevedor() == null && tituloBuscado.getDocumentoDevedor() == null 
						&& tituloBuscado.getDataEmissao() == null && tituloBuscado.getPracaProtesto() == null) {
					return new ArrayList<TituloFiliado>();
				}
				return tituloFiliadoMediator.consultarTitulosConvenio(getUser().getInstituicao(), tituloBuscado);
			}
		};
	}
	
	private TextField<String> campoData(){
		return textFieldDataEmissao = new TextField<String>("dataEmissao", new Model<String>());
	}
	
	private TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}
	
	private TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}
	
	private TextField<String> documentoDevedor() {
		return new TextField<String>("documentoDevedor");
	}
	
	private DropDownChoice<Filiado> comboFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		DropDownChoice<Filiado> campoFiliado = new DropDownChoice<Filiado>("filiado", new Model<Filiado>(),filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
		campoFiliado.setLabel(new Model<String>("Filiado"));
		return campoFiliado;		
	}
	
	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> campoPracaProtesto = new DropDownChoice<Municipio>("pracaProtesto", municipioMediator.getMunicipiosTocantins(), renderer);
		return campoPracaProtesto;
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
