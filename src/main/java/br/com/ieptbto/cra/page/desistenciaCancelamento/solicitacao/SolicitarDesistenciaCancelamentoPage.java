package br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao;

import br.com.ieptbto.cra.beans.TituloConvenioBean;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.BuscarTitulosConvenioInputPanel;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.LocalDate;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class SolicitarDesistenciaCancelamentoPage extends BasePage<TituloFiliado> {

	private static final long serialVersionUID = 1L;
	private TituloConvenioBean tituloConvenioBean;
	private Filiado filiado;
	private Usuario usuario;
 
	public SolicitarDesistenciaCancelamentoPage() {
		this.tituloConvenioBean = new TituloConvenioBean();
		this.usuario = getUser();
		this.filiado = getFiliadoPorUsuario();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formularioBuscarTitulo());
	}

	private Form<TituloConvenioBean> formularioBuscarTitulo() {
		Form<TituloConvenioBean> form = new Form<TituloConvenioBean>("form", getModelForm()){
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					if (tituloConvenioBean.getNumeroTitulo() == null && tituloConvenioBean.getDocumentoDevedor() == null 
							&& tituloConvenioBean.getNomeDevedor() == null && tituloConvenioBean.getDataInicio() == null
							&& tituloConvenioBean.getFiliado() == null && tituloConvenioBean.getNumeroProtocoloCartorio() == null) {
						if (tituloConvenioBean.getCartorio() != null) {
							throw new InfraException("Por favor informe mais um parâmetro, além do município selecionado...");
						} else {
							throw new InfraException("Os campos não podem ser nulos! Por favor informe ao menos um parâmetro...");
						}
					}
					if (tituloConvenioBean.getDataInicio() != null) {
						if (tituloConvenioBean.getDataFim() != null) {
							LocalDate dataInicio = new LocalDate(tituloConvenioBean.getDataInicio());
							LocalDate dataFim = new LocalDate(tituloConvenioBean.getDataFim());
							if (!dataInicio.isBefore(dataFim) && !dataInicio.isEqual(dataFim))
								throw new InfraException("A data de início deve ser antes da data fim.");
						} else throw new InfraException("As duas datas devem ser preenchidas.");
					}
					
					setResponsePage(new ListaTitulosDesistenciaCancelamentoPage(tituloConvenioBean, filiado));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível buscar os titulos! Entre em contato com o IEPTB-TO... ");
				}
			}
		};
		form.add(new BuscarTitulosConvenioInputPanel("buscarTitulosPanel", getModelForm(), usuario.getInstituicao(), filiado));
		return form;
	}
	
	private IModel<TituloConvenioBean> getModelForm(){
		return new CompoundPropertyModel<TituloConvenioBean>(tituloConvenioBean);
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return null;
	}
}
