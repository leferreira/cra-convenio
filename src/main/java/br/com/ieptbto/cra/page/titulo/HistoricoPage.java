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
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class HistoricoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;
	
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
		if (tituloRemessa == null){
			return new TextField<String>("pracaProtesto", new Model<String>(tituloFiliado.getPracaProtesto().getNomeMunicipio()));
		}
		return new TextField<String>("pracaProtesto", new Model<String>(tituloRemessa.getPracaProtesto()));
	}

	 private TextField<String> status(){
		 if (tituloRemessa == null){
				return new TextField<String>("situacaoTitulo", new Model<String>(tituloFiliado.getSituacaoTituloConvenio().getSituacao()));
		 }
		 return new TextField<String>("situacaoTitulo", new Model<String>(tituloRemessa.getSituacaoTitulo()));
	 }

	private TextField<String> dataRemessa(){
		if (tituloRemessa == null){
			return new TextField<String>("remessa.arquivo.dataEnvio", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("remessa.arquivo.dataEnvio", new Model<String>(DataUtil.localDateToString(tituloRemessa.getRemessa().getArquivo().getDataEnvio())));
	}
	
	private TextField<String> nomeSacadorVendedor() {
		if (tituloRemessa == null){
			return new TextField<String>("nomeSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getRazaoSocial()));
		}
		return new TextField<String>("nomeSacadorVendedor", new Model<String>(tituloRemessa.getNomeSacadorVendedor()));
	}

	private TextField<String> documentoSacador() {
		if (tituloRemessa == null){
			return new TextField<String>("documentoSacador", new Model<String>(tituloFiliado.getFiliado().getCnpjCpf()));
		}
		return new TextField<String>("documentoSacador", new Model<String>(tituloRemessa.getDocumentoSacador()));
	}

	private TextField<String> ufSacadorVendedor() {
		if (tituloRemessa == null){
			return new TextField<String>("ufSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getUf()));
		}
		return new TextField<String>("ufSacadorVendedor", new Model<String>(tituloRemessa.getUfSacadorVendedor()));
	}

	private TextField<String> cepSacadorVendedor() {
		if (tituloRemessa == null){
			return new TextField<String>("cepSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getCep()));
		}
		return new TextField<String>("cepSacadorVendedor", new Model<String>(tituloRemessa.getCepSacadorVendedor()));
	}

	private TextField<String> cidadeSacadorVendedor() {
		if (tituloRemessa == null){
			return new TextField<String>("cidadeSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getMunicipio().getNomeMunicipio()));
		}
		return new TextField<String>("cidadeSacadorVendedor", new Model<String>(tituloRemessa.getCidadeSacadorVendedor()));
	}

	private TextField<String> enderecoSacadorVendedor() {
		if (tituloRemessa == null){
			return new TextField<String>("enderecoSacadorVendedor", new Model<String>(tituloFiliado.getFiliado().getEndereco()));
		}
		return new TextField<String>("enderecoSacadorVendedor", new Model<String>(tituloRemessa.getEnderecoSacadorVendedor()));
	}

	private TextField<String> nomeDevedor() {
		if (tituloRemessa == null){
			return new TextField<String>("nomeDevedor", new Model<String>(tituloFiliado.getNomeDevedor()));
		}
		return new TextField<String>("nomeDevedor", new Model<String>(tituloRemessa.getNomeDevedor()));
	}

	private TextField<String> documentoDevedor() {
		if (tituloRemessa == null){
			return new TextField<String>("documentoDevedor", new Model<String>(tituloFiliado.getDocumentoDevedor()));
		}
		return new TextField<String>("documentoDevedor", new Model<String>(tituloRemessa.getDocumentoDevedor()));
	}

	private TextField<String> ufDevedor() {
		if (tituloRemessa == null){
			return new TextField<String>("ufDevedor", new Model<String>(tituloFiliado.getUfDevedor()));
		}
		return new TextField<String>("ufDevedor", new Model<String>(tituloRemessa.getUfDevedor()));
	}

	private TextField<String> cepDevedor() {
		if (tituloRemessa == null){
			return new TextField<String>("cepDevedor", new Model<String>(tituloFiliado.getCepDevedor()));
		}
		return new TextField<String>("cepDevedor", new Model<String>(tituloRemessa.getCepDevedor()));
	}

	private TextField<String> cidadeDevedor() {
		if (tituloRemessa == null){
			return new TextField<String>("cidadeDevedor", new Model<String>(tituloFiliado.getCidadeDevedor()));
		}
		return new TextField<String>("cidadeDevedor", new Model<String>(tituloRemessa.getCidadeDevedor()));
	}

	private TextField<String> enderecoDevedor() {
		if (tituloRemessa == null){
			return new TextField<String>("enderecoDevedor", new Model<String>(tituloFiliado.getEnderecoDevedor()));
		}
		return new TextField<String>("enderecoDevedor", new Model<String>(tituloRemessa.getEnderecoDevedor()));
	}

	private TextField<String> numeroTitulo() {
		if (tituloRemessa == null){
			return new TextField<String>("numeroTitulo", new Model<String>(tituloFiliado.getNumeroTitulo()));
		}
		return new TextField<String>("numeroTitulo", new Model<String>(tituloRemessa.getNumeroTitulo()));
	}

	private TextField<String> portador(){
		if (tituloRemessa == null){
			return new TextField<String>("remessa.arquivo.instituicaoEnvio.nomeFantasia", new Model<String>(tituloFiliado.getFiliado().getInstituicaoConvenio().getNomeFantasia()));
		}
		return new TextField<String>("remessa.arquivo.instituicaoEnvio.nomeFantasia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getNomePortador()));
	}

	 private TextField<String> agencia(){
		 if (tituloRemessa == null){
				return new TextField<String>("agencia", new Model<String>(tituloFiliado.getFiliado().getCodigoFiliado()));
			}
		 return new TextField<String>("agencia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getAgenciaCentralizadora()));
	 }

	private TextField<String> nossoNumero() {
		if (tituloRemessa == null){
			return new TextField<String>("nossoNumero", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("nossoNumero", new Model<String>(tituloRemessa.getNossoNumero()));
	}

	private TextField<String> especieTitulo() {
		if (tituloRemessa == null){
			return new TextField<String>("especieTitulo", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("especieTitulo", new Model<String>(tituloRemessa.getEspecieTitulo()));
	}

	private TextField<String> dataEmissaoTitulo() {
		if (tituloRemessa == null){
			return new TextField<String>("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(tituloFiliado.getDataEmissao())));
		}
		return new TextField<String>("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataEmissaoTitulo())));
	}

	private TextField<String> dataVencimentoTitulo() {
		if (tituloRemessa == null){
			return new TextField<String>("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(tituloFiliado.getDataVencimento())));
		}
		return new TextField<String>("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataVencimentoTitulo())));
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
		if (tituloRemessa == null){
			return new TextField<String>("nomeCedenteFavorecido", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("nomeCedenteFavorecido", new Model<String>(tituloRemessa.getNomeCedenteFavorecido()));
	}

	public TextField<String> agenciaCodigoCedente() {
		if (tituloRemessa == null){
			return new TextField<String>("agenciaCodigoCedente", new Model<String>(StringUtils.EMPTY));
		}
		return new TextField<String>("agenciaCodigoCedente", new Model<String>(tituloRemessa.getAgenciaCodigoCedente()));
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}

}