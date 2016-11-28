package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class TituloSolicitacaoCancelamentoPage extends BasePage<SolicitacaoDesistenciaCancelamento> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoProtestoMediator;

	private SolicitacaoDesistenciaCancelamento solicitacaoCancelamento;
	private DropDownChoice<CodigoIrregularidade> dropDownMotivoCancelamento;

	public TituloSolicitacaoCancelamentoPage(TituloRemessa titulo) {
		this.solicitacaoCancelamento = new SolicitacaoDesistenciaCancelamento();
		this.solicitacaoCancelamento.setTituloRemessa(titulo);

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		informacoesTitulo();
		formEnviarSolicitacao();
	}

	private void formEnviarSolicitacao() {
		Form<SolicitacaoDesistenciaCancelamento> form = new Form<SolicitacaoDesistenciaCancelamento>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				error("O serviço de cancelamento está temporáriamente indisponível!");
				try {

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					getFeedbackPanel().error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar a solicitação de cancelamento ! Favor entrar em contato com o IEPTB...");
				}
			}
		};
		form.add(dropDownMotivoCancelamento());
		form.add(numeroTituloModal());
		form.add(nomeDevedorModal());
		form.add(saldoTituloModal());
		add(form);
	}

	private void informacoesTitulo() {
		add(pracaProtesto());
		add(status());
		add(nomeSacadorVendedor());
		add(documentoSacador());
		add(nomeDevedor());
		add(documentoDevedor());
		add(numeroTitulo());
		add(especieTitulo());
		add(dataEmissaoTitulo());
		add(dataVencimentoTitulo());
		add(valorTitulo());
		add(saldoTitulo());
		add(numeroProtocoloCartorio());
	}

	private DropDownChoice<CodigoIrregularidade> dropDownMotivoCancelamento() {
		IChoiceRenderer<CodigoIrregularidade> renderer = new ChoiceRenderer<CodigoIrregularidade>("motivo");
		dropDownMotivoCancelamento = new DropDownChoice<CodigoIrregularidade>("tipoMotivo", new Model<CodigoIrregularidade>(),
				Arrays.asList(CodigoIrregularidade.values()), renderer);
		dropDownMotivoCancelamento.setLabel(new Model<String>("Motivo do Cancelameto"));
		dropDownMotivoCancelamento.setOutputMarkupId(true);
		return dropDownMotivoCancelamento;
	}

	private Label pracaProtesto() {
		return new Label("pracaProtesto", new Model<String>(getTituloRemessa().getPracaProtesto()));
	}

	private Label status() {
		return new Label("situacaoTitulo", new Model<String>(getTituloRemessa().getSituacaoTitulo()));
	}

	private Label numeroTitulo() {
		return new Label("numeroTitulo", new Model<String>(getTituloRemessa().getNumeroTitulo()));
	}

	private Label numeroTituloModal() {
		return new Label("numeroTituloModal", new Model<String>(getTituloRemessa().getNumeroTitulo()));
	}

	public Label saldoTituloModal() {
		return new Label("saldoTituloModal", new Model<String>("R$ " + getTituloRemessa().getSaldoTitulo().toString()));
	}

	private Label nomeDevedorModal() {
		return new Label("nomeDevedorModal", new Model<String>(getTituloRemessa().getNomeDevedor()));
	}

	private Label especieTitulo() {
		return new Label("especieTitulo", new Model<String>(getTituloRemessa().getEspecieTitulo()));
	}

	private Label dataEmissaoTitulo() {
		return new Label("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataEmissaoTitulo())));
	}

	private Label dataVencimentoTitulo() {
		return new Label("dataVencimentoTitulo",
				new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataVencimentoTitulo())));
	}

	public Label valorTitulo() {
		return new Label("valorTitulo", new Model<String>("R$ " + getTituloRemessa().getValorTitulo().toString()));
	}

	public Label saldoTitulo() {
		return new Label("saldoTitulo", new Model<String>("R$ " + getTituloRemessa().getSaldoTitulo().toString()));
	}

	private Label numeroProtocoloCartorio() {
		String numeroProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null) {
			numeroProtocolo = getTituloRemessa().getConfirmacao().getNumeroProtocoloCartorio();
		}
		return new Label("numeroProtocoloCartorio", new Model<String>(numeroProtocolo));
	}

	private Label nomeSacadorVendedor() {
		return new Label("nomeSacadorVendedor", new Model<String>(getTituloRemessa().getNomeSacadorVendedor()));
	}

	private Label documentoSacador() {
		return new Label("documentoSacador", new Model<String>(getTituloRemessa().getDocumentoSacador()));
	}

	private Label nomeDevedor() {
		return new Label("nomeDevedor", new Model<String>(getTituloRemessa().getNomeDevedor()));
	}

	private Label documentoDevedor() {
		return new Label("documentoDevedor", new Model<String>(getTituloRemessa().getNumeroIdentificacaoDevedor()));
	}

	private TituloRemessa getTituloRemessa() {
		return solicitacaoCancelamento.getTituloRemessa();
	}

	@Override
	protected IModel<SolicitacaoDesistenciaCancelamento> getModel() {
		return new CompoundPropertyModel<SolicitacaoDesistenciaCancelamento>(solicitacaoCancelamento);
	}
}