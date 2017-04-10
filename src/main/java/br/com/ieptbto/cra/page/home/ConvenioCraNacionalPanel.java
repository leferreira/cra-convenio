package br.com.ieptbto.cra.page.home;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

/**
 * @author Thasso Araujo
 *
 */
public class ConvenioCraNacionalPanel extends Panel {

	@SpringBean
	private DownloadMediator downloadMediator;
	
	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private UsuarioFiliado usuarioFiliado;
	private List<Remessa> remessasPendentes;
	
	public ConvenioCraNacionalPanel(String id, Usuario usuario, UsuarioFiliado usuarioFiliado) {
		super(id);
		this.usuario = usuario;
		this.usuarioFiliado = usuarioFiliado;
		this.remessasPendentes = new ArrayList<Remessa>();
		add(labelTotalRemessasPendentes());
		add(listaConfirmacoesPendentes());
		setVisible(definirVisibilidade());
	}
	
	private Label labelTotalRemessasPendentes() {
		return new Label("qtdRemessas", (remessasPendentes != null) ? remessasPendentes.size() : 0);
	}

	private ListView<Remessa> listaConfirmacoesPendentes() {
		return new ListView<Remessa>("listConfirmacoes", remessasPendentes) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(new Label("arquivo", remessa.getArquivo().getNomeArquivo()));
				String nomeMunicipio = remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio();
				item.add(new Label("instituicao", (nomeMunicipio.length() > 19) ? nomeMunicipio.substring(0,19) : nomeMunicipio));
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getDataRecebimento().toDate(), new Date())));
				item.add(downloadArquivoTXT(remessa));
			}

			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							getApplication().getResourceSettings().getPropertiesFactory().clearCache();

							File file = downloadMediator.baixarRemessaTXT(usuario, remessa);
							IResourceStream resourceStream = new FileResourceStream(file);

							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};
			}
		};
	}
	
	private boolean definirVisibilidade() {
		LayoutPadraoXML layout = usuario.getInstituicao().getLayoutPadraoXML();
		if (usuarioFiliado == null && layout == LayoutPadraoXML.CRA_NACIONAL) {
			return true;
		}
		return false;
	}
}
