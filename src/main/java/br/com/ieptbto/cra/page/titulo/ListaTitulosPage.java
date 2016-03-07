package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class ListaTitulosPage extends BasePage<TituloFiliado> {

    @SpringBean
    private TituloFiliadoMediator tituloFiliadoMediator;
    private TituloFiliado tituloFiliado;
    private List<TituloFiliado> titulos;

    public ListaTitulosPage(Filiado filiado, LocalDate dataInicio, LocalDate dataFim, Municipio pracaProtesto, TituloFiliado titulo) {
	this.tituloFiliado = new TituloFiliado();
	this.titulos = tituloFiliadoMediator.consultarTitulosFiliado(filiado, dataInicio, dataFim, pracaProtesto, titulo, null);

	carregarComponentes();
    }

    public ListaTitulosPage(Instituicao convenio, LocalDate dataInicio, LocalDate dataFim, Filiado filiado, Municipio pracaProtesto, TituloFiliado tituloBuscado) {
	this.tituloFiliado = new TituloFiliado();
	this.titulos = tituloFiliadoMediator.consultarTitulosConvenio(convenio, dataInicio, dataFim, filiado, pracaProtesto, tituloBuscado);

	carregarComponentes();
    }

    private void carregarComponentes() {
	add(carregarListaTitulos());
    }

    private ListView<TituloFiliado> carregarListaTitulos() {
	return new ListView<TituloFiliado>("listViewTitulos", getTitulosFiliados()) {

	    @Override
	    protected void populateItem(ListItem<TituloFiliado> item) {
		final TituloFiliado tituloLista = item.getModelObject();
		TituloRemessa tituloRemessa = tituloFiliadoMediator.buscarTituloDoConvenioNaCra(tituloLista);

		item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
		item.add(new Label("emissao", DataUtil.localDateToString(tituloLista.getDataEmissao())));
		item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
		item.add(new LabelValorMonetario<String>("valor", tituloLista.getValorTitulo()));
		item.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));

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

		    if (tituloRemessa.getRetorno() != null) {
			item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
		    } else {
			item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getDataOcorrencia())));
		    }
		    item.add(new Label("situacaoTitulo", tituloRemessa.getSituacaoTitulo().toUpperCase()));
		}
	    }
	};
    }

    private IModel<List<TituloFiliado>> getTitulosFiliados() {
	return new LoadableDetachableModel<List<TituloFiliado>>() {

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
