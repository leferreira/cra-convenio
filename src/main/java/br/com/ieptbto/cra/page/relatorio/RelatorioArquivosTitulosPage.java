package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioArquivosTitulosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	private Arquivo arquivo;
	private Form<Arquivo> form;
	private Instituicao instituicao;

	public RelatorioArquivosTitulosPage() {
		this.arquivo = new Arquivo();
		this.instituicao = getUser().getInstituicao();
		
		form = new Form<Arquivo>("form", getModel());
		form.add(new RelatorioArquivosTitulosConvenioPanel("buscarArquivoInputPanel", getModel(), instituicao, getUser()));
		add(form);
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}