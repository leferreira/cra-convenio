package br.com.ieptbto.cra.page.arquivo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;

/**
 * @author Thasso Araújo
 *
 */
public class ListaTitulosDesistenciaCancelamentoPage extends BasePage<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	private TituloFiliado tituloFiliado;
	private List<TituloFiliado> titulos;
	
	public ListaTitulosDesistenciaCancelamentoPage(Filiado filiado, LocalDate dataInicio, LocalDate dataFim, Municipio pracaProtesto, TituloFiliado titulo) {
		this.tituloFiliado = new TituloFiliado();
		this.titulos = tituloFiliadoMediator.consultarTitulosFiliado(filiado, dataInicio, dataFim, pracaProtesto ,titulo, SituacaoTituloConvenio.AGUARDANDO);
		
		carregarComponentes();
	}

	private void carregarComponentes() {
		add(carregarListaTitulos());
	}

	private ListView<TituloFiliado> carregarListaTitulos() {
		return new ListView<TituloFiliado>("listViewTitulos", getTitulosFiliados()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();
				final TituloRemessa tituloRemessa = tituloFiliadoMediator.buscarTituloDoConvenioNaCra(tituloLista); 
				
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new LabelValorMonetario<String>("valor", tituloLista.getValorTitulo()));
				
				Link<TituloFiliado> linkHistorico = new Link<TituloFiliado>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;
					
					@Override
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
					
					item.add(new Label("situacaoTitulo", tituloRemessa.getSituacaoTitulo().toUpperCase()));
				}
				item.add(new Link<TituloFiliado>("solicitar"){
					
					/***/
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onClick() {
						setResponsePage(new TituloDesistenciaCancelamentoSolicitadoPage(tituloLista, tituloRemessa));
					}
				});
			}
		};
	}
	
	private IModel<List<TituloFiliado>> getTitulosFiliados() {
		return new LoadableDetachableModel<List<TituloFiliado>>() {

			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			protected List<TituloFiliado> load() {
				return titulos;
			}
		};
	}
	
	public TituloFiliado getTituloFiliado() {
		return tituloFiliado;
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return null;
	}
}
