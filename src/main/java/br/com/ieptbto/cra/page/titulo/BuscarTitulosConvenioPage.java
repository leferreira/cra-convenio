package br.com.ieptbto.cra.page.titulo;

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
import org.joda.time.LocalDate;

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
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class BuscarTitulosConvenioPage extends BasePage<TituloFiliado>{

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	FiliadoMediator filiadoMediator;
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	
	private TituloFiliado titulo;
	private TextField<String> campoNomeDevedor;
	private TextField<String> campoNumeroTitulo;
	private TextField<String> campoNumeroDocumento;
	private TextField<String> campoDataEmissao;
	private DropDownChoice<Filiado> campoFiliado;
	private DropDownChoice<Municipio> campoPracaProtesto;
	
	private String numeroDocumento;
	private String nomeDevedor;
	private String numeroTitulo;
	private LocalDate dataEmissao;
	private Municipio pracaProtesto;
	private Filiado filiado;
	
	public BuscarTitulosConvenioPage() {
		this.titulo = new TituloFiliado();
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel());
		form.add(new Button("botaoEnviar"){
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (campoNumeroDocumento != null){
					numeroDocumento = campoNumeroDocumento.getModelObject();
				}
				if (campoNomeDevedor != null){
					nomeDevedor = campoNomeDevedor.getModelObject();
				}
				if (campoDataEmissao != null){
					dataEmissao = DataUtil.stringToLocalDate(campoDataEmissao.getModelObject());
				}
				if (campoNumeroTitulo != null){
					numeroTitulo = campoNumeroTitulo.getModelObject();
				}
				if (campoPracaProtesto != null){
					pracaProtesto = campoPracaProtesto.getModelObject();
				}
				if (campoFiliado != null) {
					filiado = campoFiliado.getModelObject(); 					
				}
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
			/***/
			private static final long serialVersionUID = 1L;

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
				} else {
					if (tituloRemessa.getConfirmacao() != null) {
						item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getDataRecebimento())));
						item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
					} else { 
						item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
						item.add(new Label("protocolo", StringUtils.EMPTY));
					}
					
			        if (tituloRemessa.getRetorno() != null){
		        		item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
			        } else {
			        	item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getDataOcorrencia())));
			        }
					item.add(new Label("situacaoTitulo", tituloRemessa.getSituacaoTitulo()));
				}
			}
		};
	}
	
	private TextField<String> campoData(){
		if (campoDataEmissao != null){
			return campoDataEmissao = new TextField<String>("dataEmissao", new Model<String>(campoDataEmissao.getModelObject()));
		}
		return campoDataEmissao = new TextField<String>("dataEmissao");
	}
	
	private TextField<String> numeroTitulo() {
		if (campoNumeroTitulo != null){
			return campoNumeroTitulo = new TextField<String>("numeroTitulo", new Model<String>(campoNumeroTitulo.getModelObject()));
		}
		return campoNumeroTitulo = new TextField<String>("numeroTitulo");
	}
	
	private TextField<String> nomeDevedor() {
		if (campoNomeDevedor != null){
			return campoNomeDevedor = new TextField<String>("nomeDevedor", new Model<String>(campoNomeDevedor.getModelObject()));
		}
		return campoNomeDevedor = new TextField<String>("nomeDevedor");
	}
	
	private TextField<String> documentoDevedor() {
		if (campoNumeroDocumento != null){
			return campoNumeroDocumento = new TextField<String>("documentoDevedor", new Model<String>(campoNumeroDocumento.getModelObject()));
		}
		return campoNumeroDocumento = new TextField<String>("documentoDevedor");
	}
	
	private DropDownChoice<Filiado> comboFiliado() {
		IChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		campoFiliado = new DropDownChoice<Filiado>("filiado", new Model<Filiado>(),filiadoMediator.buscarListaFiliados(getUser().getInstituicao()), renderer);
		campoFiliado.setLabel(new Model<String>("Filiado"));
		return campoFiliado;		
	}
	
	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		campoPracaProtesto = new DropDownChoice<Municipio>("pracaProtesto", municipioMediator.listarTodos(), renderer);
		return campoPracaProtesto;
	}
	
	private IModel<List<TituloFiliado>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloFiliado>>() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloFiliado> load() {
				return tituloFiliadoMediator.consultarTitulosConvenio(getUser().getInstituicao(),nomeDevedor, numeroDocumento,numeroTitulo, dataEmissao, pracaProtesto, filiado );
			}
		};
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
