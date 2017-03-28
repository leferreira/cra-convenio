package br.com.ieptbto.cra.page.relatorio.convenio;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioTitulosConvenioPage extends BasePage<TituloRemessa> {

	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private TituloConvenioBean tituloConvenioBean; 

	public RelatorioTitulosConvenioPage() {
		this.tituloConvenioBean = new TituloConvenioBean();
		this.usuario = getUser();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(carregarFormulario());
	}

	private RelatorioTitulosForm carregarFormulario() {
		this.tituloConvenioBean.setTipoInstituicao(TipoInstituicaoCRA.CONVENIO);
		this.tituloConvenioBean.setBancoConvenio(usuario.getInstituicao());
		
		IModel<TituloConvenioBean> model = new CompoundPropertyModel<TituloConvenioBean>(tituloConvenioBean);
		RelatorioTitulosForm form = new RelatorioTitulosForm("form", model);
		form.add(new RelatorioTitulosInputPanel("relatorioTitulosInputPanel", model, usuario.getInstituicao()));
		return form;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return null;
	}
}