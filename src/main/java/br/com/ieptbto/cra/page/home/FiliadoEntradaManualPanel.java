package br.com.ieptbto.cra.page.home;

import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.titulo.EnviarTitulosPage;
import br.com.ieptbto.cra.page.titulo.entrada.EntradaManualPage;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

/**
 * @author Thasso Araujo
 *
 */
public class FiliadoEntradaManualPanel extends Panel {

	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	
	private static final long serialVersionUID = 1L;
	private UsuarioFiliado usuarioFiliado;
	private List<TituloFiliado> titulosFiliado;

	public FiliadoEntradaManualPanel(String id, UsuarioFiliado usuarioFiliado) {
		super(id);
		this.titulosFiliado = tituloFiliadoMediator.buscarTitulosParaEnvio(usuarioFiliado, null);
		add(linkTitulosPendentes());
		add(labelTotalTitulosPendentes());
		add(listaTitulosPendentes());
		setVisible(definirVisibilidade());
	}
	
	private Link<TituloFiliado> linkTitulosPendentes() {
		return new Link<TituloFiliado>("linkTitulos") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new EnviarTitulosPage());
			}
		};
	}

	private ListView<TituloFiliado> listaTitulosPendentes() {
		return new ListView<TituloFiliado>("listViewTitulos", titulosFiliado) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();

				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				Link<String> linkAlterar = new Link<String>("linkAlterar") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new EntradaManualPage(tituloLista));
					}
				};
				linkAlterar.add(new Label("devedor", tituloLista.getNomeDevedor()));
				item.add(linkAlterar);
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(tituloLista.getDataEntrada(), new Date())));
			}
		};
	}

	private Label labelTotalTitulosPendentes() {
		return new Label("qtdTitulos", (titulosFiliado != null) ? titulosFiliado.size() : 0);
	}
	
	private boolean definirVisibilidade() {
		if (usuarioFiliado != null) {
			return true;
		}
		return false;
	}
}
