package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class TitulosArquivoConvenioPage extends BasePage<Arquivo> {

    /***/
    private static final long serialVersionUID = 1L;

    @SpringBean
    private TituloMediator tituloMediator;
    @SpringBean
    private ArquivoMediator arquivoMediator;
    private Arquivo arquivo;
    private List<TituloRemessa> titulos;

    public TitulosArquivoConvenioPage(Arquivo arquivo) {
	this.titulos = new ArrayList<TituloRemessa>();
	this.arquivo = arquivo;

	carregarInformacoes();
    }

    private void carregarInformacoes() {
	add(nomeArquivo());
	add(tipoArquivo());
	add(instituicaoEnvio());
	add(instituicaoDestino());
	add(dataEnvio());
	add(usuarioEnvio());
	add(carregarListaTitulos());
	add(botaoGerarRelatorio());
	add(downloadArquivoTXT(getArquivo()));
    }

    private ListView<TituloRemessa> carregarListaTitulos() {
	return new ListView<TituloRemessa>("listViewTituloArquivo", getTitulos()) {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(ListItem<TituloRemessa> item) {
		final TituloRemessa tituloLista = item.getModelObject();
		item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
		item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));
		if (tituloLista.getConfirmacao() != null) {
		    item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
		} else if (tituloLista.getRetorno() != null) {
		    item.add(new Label("protocolo", tituloLista.getRetorno().getNumeroProtocoloCartorio()));
		} else {
		    item.add(new Label("protocolo", StringUtils.EMPTY));
		}
		item.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		item.add(new Label("praca", tituloLista.getPracaProtesto()));
		item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", tituloLista.getValorTitulo()));
		item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
	    }
	};
    }

    private Link<Arquivo> botaoGerarRelatorio() {
	return new Link<Arquivo>("gerarRelatorio") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		error("Não foi possível gerar o relatório ! Por favor entre com contato com o IEPTB-TO !");
		try {

		} catch (InfraException ex) {
		    error(ex.getMessage());
		} catch (Exception e) {
		    error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
		    e.printStackTrace();
		}
	    }
	};
    }

    private Link<Arquivo> downloadArquivoTXT(final Arquivo arquivo) {
	return new Link<Arquivo>("downloadArquivo") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		File file = arquivoMediator.baixarArquivoTXT(getUser().getInstituicao(), arquivo);
		IResourceStream resourceStream = new FileResourceStream(file);

		getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo()));
	    }
	};
    }

    private Label nomeArquivo() {
	return new Label("nomeArquivo", getArquivo().getNomeArquivo());
    }

    private Label tipoArquivo() {
	return new Label("tipo", getArquivo().getTipoArquivo().getTipoArquivo().getLabel());
    }

    private Label instituicaoEnvio() {
	return new Label("instituicaoEnvio", getArquivo().getInstituicaoEnvio().getNomeFantasia());
    }

    private Label instituicaoDestino() {
	return new Label("instituicaoDestino", getArquivo().getInstituicaoRecebe().getNomeFantasia());
    }

    private Label usuarioEnvio() {
	return new Label("usuario", getArquivo().getUsuarioEnvio().getNome());
    }

    private Label dataEnvio() {
	return new Label("dataEnvio", DataUtil.localDateToString(getArquivo().getDataEnvio()));
    }

    public Arquivo getArquivo() {
	return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
	this.arquivo = arquivo;
    }

    private List<TituloRemessa> getTitulos() {
	return titulos;
    }

    @Override
    protected IModel<Arquivo> getModel() {
	return new CompoundPropertyModel<Arquivo>(arquivo);
    }
}
