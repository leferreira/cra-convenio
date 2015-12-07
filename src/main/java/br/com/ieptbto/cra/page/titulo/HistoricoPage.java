package br.com.ieptbto.cra.page.titulo;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class HistoricoPage extends BasePage<TituloRemessa> {

	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	
	private TituloRemessa tituloRemessa;
	private TituloFiliado tituloFiliado;
	
	public HistoricoPage(TituloFiliado titulo){
		this.tituloRemessa = tituloFiliadoMediator.buscarTituloDoConvenioNaCra(titulo);
		this.tituloFiliado = titulo;
		add(numeroProtocoloCartorio());
		add(dataProtocolo());
		add(codigoCartorio());
		add(pracaProtesto());
		add(nomeSacadorVendedor());
		add(documentoSacador());
		add(ufSacadorVendedor());
		add(cepSacadorVendedor());
		add(cidadeSacadorVendedor());
		add(enderecoSacadorVendedor());
		add(nomeDevedor());
		add(documentoDevedor());
		add(ufDevedor());
		add(cepDevedor());
		add(cidadeDevedor());
		add(enderecoDevedor());
		add(numeroTitulo());
		add(dataRemessa());
		add(portador());
		add(cartorio());
		add(agencia());
		add(nossoNumero());
		add(especieTitulo());
		add(dataEmissaoTitulo());
		add(dataVencimentoTitulo());
		add(valorTitulo());
		add(saldoTitulo());
		add(valorCustaCartorio());
		add(valorGravacaoEletronica());
		add(valorCustasCartorioDistribuidor());
		add(valorDemaisDespesas());
		add(nomeCedenteFavorecido());
		add(agenciaCodigoCedente());
		add(status());
	}
	
	public HistoricoPage(TituloRemessa tituloLista) {
		// TODO Auto-generated constructor stub
	}

	private TextField<String> numeroProtocoloCartorio() {
		if (tituloRemessa == null){
			return new TextField<String>("numeroProtocoloCartorio", new Model<String>(StringUtils.EMPTY));
		} else if (tituloRemessa.getConfirmacao() != null){
			return new TextField<String>("numeroProtocoloCartorio", new Model<String>(tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
		}
		return new TextField<String>("numeroProtocoloCartorio", new Model<String>(StringUtils.EMPTY));
	}

	private TextField<String> dataProtocolo() {
		String dataProtocolo = StringUtils.EMPTY;
		if (tituloRemessa == null){
			dataProtocolo = StringUtils.EMPTY;
		} else if (tituloRemessa.getConfirmacao() != null){
			dataProtocolo = DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataProtocolo()); 
		}
		return new TextField<String>("confirmacao.dataProtocolo", new Model<String>(dataProtocolo));
	}

	private TextField<String> codigoCartorio() {
		if (tituloRemessa == null){
			return new TextField<String>("codigoCartorio", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("codigoCartorio", new Model<String>(tituloRemessa.getCodigoCartorio().toString()));
	}

	private TextField<String> cartorio(){
		if (tituloRemessa == null){
			return new TextField<String>("remessa.instituicaoDestino.nomeFantasia", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("remessa.instituicaoDestino.nomeFantasia", new Model<String>(StringUtils.EMPTY));
	}

	private TextField<String> pracaProtesto() {
		return new TextField<String>("pracaProtesto", new Model<String>(tituloFiliado.getPracaProtesto().getNomeMunicipio()));
	}

	 private TextField<String> status(){
		return new TextField<String>("situacaoTitulo", new Model<String>(tituloFiliado.getSituacaoTituloConvenio().getSituacao()));
	 }

	private TextField<String> dataRemessa(){
		return new TextField<String>("remessa.arquivo.dataEnvio", new Model<String>(StringUtils.EMPTY));
	}
	
	private TextField<String> nomeSacadorVendedor() {
		return new TextField<String>("nomeSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getRazaoSocial()));
	}

	private TextField<String> documentoSacador() {
		return new TextField<String>("documentoSacador", new Model<String>(tituloFiliado.getFiliado().getCnpjCpf()));
	}

	private TextField<String> ufSacadorVendedor() {
		return new TextField<String>("ufSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getUf()));
	}

	private TextField<String> cepSacadorVendedor() {
		return new TextField<String>("cepSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getCep()));
	}

	private TextField<String> cidadeSacadorVendedor() {
		return new TextField<String>("cidadeSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getMunicipio().getNomeMunicipio()));
	}

	private TextField<String> enderecoSacadorVendedor() {
		return new TextField<String>("enderecoSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getEndereco()));
	}

	private TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor", new Model<String>(tituloFiliado.getNomeDevedor()));
	}

	private TextField<String> documentoDevedor() {
		return new TextField<String>("documentoDevedor", new Model<String>(tituloFiliado.getCpfCnpj()));
	}

	private TextField<String> ufDevedor() {
		return new TextField<String>("ufDevedor", new Model<String>(tituloFiliado.getUfDevedor()));
	}

	private TextField<String> cepDevedor() {
		return new TextField<String>("cepDevedor", new Model<String>(tituloFiliado.getCepDevedor()));
	}

	private TextField<String> cidadeDevedor() {
		return new TextField<String>("cidadeDevedor", new Model<String>(tituloFiliado.getCidadeDevedor()));
	}

	private TextField<String> enderecoDevedor() {
		return new TextField<String>("enderecoDevedor", new Model<String>(tituloFiliado.getEnderecoDevedor()));
	}

	private TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo", new Model<String>(tituloFiliado.getNumeroTitulo()));
	}

	private TextField<String> portador(){
		return new TextField<String>("remessa.arquivo.instituicaoEnvio.nomeFantasia", new Model<String>(tituloFiliado.getFiliado().getInstituicaoConvenio().getNomeFantasia()));
	}

	 private TextField<String> agencia(){
		 if (tituloRemessa == null){
				return new TextField<String>("agencia", new Model<String>(tituloFiliado.getFiliado().getCodigoFiliado()));
			}
		 return new TextField<String>("agencia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getAgenciaCentralizadora()));
	 }

	private TextField<String> nossoNumero() {
		return new TextField<String>("nossoNumero", new Model<String>(gerarNossoNumero(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao() + tituloFiliado.getId())));
	}
	
	private String gerarNossoNumero(String nossoNumero) {
		return StringUtils.rightPad(nossoNumero, 15, "0");
	}

	private TextField<String> especieTitulo() {
		return new TextField<String>("especieTitulo", new Model<String>(tituloFiliado.getEspecieTitulo().getConstante()));
	}

	private TextField<String> dataEmissaoTitulo() {
		return new TextField<String>("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(tituloFiliado.getDataEmissao())));
	}

	private TextField<String> dataVencimentoTitulo() {
		return new TextField<String>("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(tituloFiliado.getDataVencimento())));
	}

	public TextField<String> valorTitulo() {
		if (tituloRemessa == null){
			return new TextField<String>("valorTitulo", new Model<String>("R$ " + tituloFiliado.getValorTitulo().toString()));
		}
		return new TextField<String>("valorTitulo", new Model<String>("R$ " + tituloRemessa.getValorTitulo().toString()));
	}

	public TextField<String> saldoTitulo() {
		if (tituloRemessa == null){
			return new TextField<String>("saldoTitulo", new Model<String>("R$ " + tituloFiliado.getValorSaldoTitulo().toString()));
		}
		return new TextField<String>("saldoTitulo", new Model<String>("R$ " + tituloRemessa.getSaldoTitulo().toString()));
	}

	public TextField<String> valorCustaCartorio() {
		if (tituloRemessa == null){
			return new TextField<String>("valorCustaCartorio", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("valorCustaCartorio", new Model<String>("R$ " + tituloRemessa.getValorCustaCartorio().toString()));
	}

	public TextField<String> valorGravacaoEletronica() {
		if (tituloRemessa == null){
			return new TextField<String>("valorGravacaoEletronica", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("valorGravacaoEletronica", new Model<String>("R$ " + tituloRemessa.getValorGravacaoEletronica().toString()));
	}

	public TextField<String> valorCustasCartorioDistribuidor() {
		if (tituloRemessa == null){
			return new TextField<String>("valorCustasCartorioDistribuidor", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("valorCustasCartorioDistribuidor", new Model<String>("R$ " + tituloRemessa.getValorCustasCartorioDistribuidor().toString()));
	}

	public TextField<String> valorDemaisDespesas() {
		if (tituloRemessa == null){
			return new TextField<String>("valorDemaisDespesas", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("valorDemaisDespesas", new Model<String>("R$ " + tituloRemessa.getValorDemaisDespesas().toString()));
	}

	public TextField<String> nomeCedenteFavorecido() {
		return new TextField<String>("nomeCedenteFavorecido", new Model<String>(tituloFiliado.getFiliado().getRazaoSocial()));
	}

	public TextField<String> agenciaCodigoCedente() {
		return new TextField<String>("agenciaCodigoCedente", new Model<String>(tituloFiliado.getFiliado().getCodigoFiliado()));
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}