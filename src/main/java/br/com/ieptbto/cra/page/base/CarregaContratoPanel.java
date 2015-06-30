package br.com.ieptbto.cra.page.base;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;

public class CarregaContratoPanel extends ModalWindow implements IHeaderContributor {

    /***/
	private static final long serialVersionUID = 1L;

	public CarregaContratoPanel(String id) {
        super(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forScript(getWindowOpenJavaScript(),null));
    }

    @Override
    protected boolean makeContentVisible() {
        return true;
    }
}