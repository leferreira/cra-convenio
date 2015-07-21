package br.com.ieptbto.cra.page.titulo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.enumeration.TipoAlineaCheque;
import br.com.ieptbto.cra.enumeration.TipoEspecieTitulo;
import br.com.ieptbto.cra.mediator.AvalistaMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.EstadoUtils;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.USER })
public class EntradaManualPage extends BasePage<TituloFiliado> {

	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	@SpringBean
	AvalistaMediator avalistaMediator;
	private TituloFiliado tituloFiliado;
	private List<Avalista> avalistas;
	private TextField<String> dataVencimentoField;
	private TextField<String> dataEmissaoField;
	private DropDownChoice<TipoAlineaCheque> comboAlinea;

	public EntradaManualPage() {
		this.tituloFiliado = new TituloFiliado();
		setAvalistas(tituloFiliado.getAvalistas());
		
		carregarFormularioTitulo();
	}
	
	public EntradaManualPage(String mensagem) {
		this.tituloFiliado = new TituloFiliado();
		setAvalistas(tituloFiliado.getAvalistas());
		info(mensagem);
		
		carregarFormularioTitulo();
	}
	
	public EntradaManualPage(TituloFiliado titulo) {
		this.tituloFiliado = titulo;
		setAvalistas(avalistaMediator.buscarAvalistasPorTitulo(titulo));
		
		carregarFormularioTitulo();
	}

	public void carregarFormularioTitulo() {

		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel());
		form.add(new Button("salvarTitulo"){
			
			@Override
			public void onSubmit() {
				super.onSubmit();
				TituloFiliado titulo = EntradaManualPage.this.getModel().getObject();
				
				titulo.setAvalistas(getAvalistas());
				titulo.setDataEmissao(DataUtil.stringToLocalDate(dataEmissaoField.getModelObject()));
				titulo.setDataVencimento(DataUtil.stringToLocalDate(dataVencimentoField.getModelObject()));
				
				if (!titulo.getDataEmissao().isBefore(titulo.getDataVencimento())) {
					error("A Data de Emissão do título deve ser antes do Data do Vencimento !");
				} else if (titulo.getId() != 0) {
					tituloFiliadoMediator.alterarTituloFiliado(titulo);
					setResponsePage(new EntradaManualPage("Os dados do título foram salvos com sucesso !"));
				} else {
					titulo.setFiliado(usuarioFiliadoMediator.buscarEmpresaFiliadaDoUsuario(getUser()));
					titulo.setSituacaoTituloConvenio(SituacaoTituloConvenio.AGUARDANDO);
					tituloFiliadoMediator.salvarTituloFiliado(titulo);
					setResponsePage(new EntradaManualPage("Os dados do título foram salvos com sucesso !"));
				}
			}
		});
		form.add(numeroTitulo());
		form.add(dataEmissao());
		form.add(dataVencimento());
		form.add(pracaProtesto());
		form.add(documentoDevedor());
		form.add(valorTitulo());
		form.add(valorSaldoTitulo());
		form.add(nomeDevedor());
		form.add(enderecoDevedor());
		form.add(cidadeDevedor());
		form.add(cepDevedor());
		form.add(ufDevedor());
		form.add(especieTitulo());
		form.add(bairroDevedor());
		form.add(campoAlinea());
		form.add(new AvalistaInputPanel("avalistaPanel", getModel() ,getAvalistas()));
		form.add(carregarListaAvalistas());
		add(form);
	}

	
	private ListView<Avalista> carregarListaAvalistas(){
		return new ListView<Avalista>("listaAvalistas", getAvalistas() ) {
			
			@Override
			protected void populateItem(ListItem<Avalista> item) {
				Avalista avalista = item.getModelObject();
				item.add(new Label("contador", item.getIndex()+1));
				item.add(new Label("nomeAvalista", avalista.getNome()));
				item.add(new Label("documentoAvalista", avalista.getDocumento()));
				item.add(new Label("cidadeAvalista", avalista.getCidade()));
				item.add(new Label("ufAvalista", avalista.getUf()));
				item.add(removerAvalista(avalista));
			}
			
			private Link<Avalista> removerAvalista(final Avalista avalista) {
				return new Link<Avalista>("remover") {
					
					@Override
					public void onClick() {
						if (avalista.getId() != 0) {
							avalistaMediator.removerAvalista(avalista);
						}
						getAvalistas().remove(avalista);
					}
				};
			}
		};
	}

	private TextField<String> numeroTitulo() {
		TextField<String> textField = new TextField<String>("numeroTitulo");
		textField.setLabel(new Model<String>("Nùmero do Título"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> dataEmissao() {
		dataEmissaoField = new TextField<String>("dataEmissao", new Model<String>());
		if (tituloFiliado.getDataEmissao() != null) {
			dataEmissaoField = new TextField<String>("dataEmissao", new Model<String>(DataUtil.localDateToString(tituloFiliado.getDataEmissao())));
		}
		dataEmissaoField.setLabel(new Model<String>("Data Emissão"));
		dataEmissaoField.setRequired(true);
		return dataEmissaoField;
	}

	private TextField<String> dataVencimento() {
		dataVencimentoField = new TextField<String>("dataVencimento", new Model<String>());
		if (tituloFiliado.getDataVencimento() != null) {
			dataVencimentoField = new TextField<String>("dataVencimento", new Model<String>(DataUtil.localDateToString(tituloFiliado.getDataVencimento())));
		}
		dataVencimentoField.setLabel(new Model<String>("Data de Vencimento"));
		dataVencimentoField.setRequired(true);
		return dataVencimentoField;
	}
	
	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> comboMunicipio = new DropDownChoice<Municipio>("pracaProtesto", municipioMediator.listarTodos(), renderer);
		comboMunicipio.setLabel(new Model<String>("Município"));
		comboMunicipio.setRequired(true);
		return comboMunicipio;
	}

	private DropDownChoice<String> ufDevedor() {
		DropDownChoice<String> textField = new DropDownChoice<String>("ufDevedor", EstadoUtils.getEstadosToList());
		textField.setLabel(new Model<String>("UF"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> cepDevedor() {
		TextField<String> textField = new TextField<String>("cepDevedor");
		textField.setLabel(new Model<String>("CEP"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> cidadeDevedor() {
		TextField<String> textField = new TextField<String>("cidadeDevedor");
		textField.setLabel(new Model<String>("Cidade"));
		textField.setRequired(true);
		return textField;
	}

	private TextArea<String> enderecoDevedor() {
		TextArea<String> textField = new TextArea<String>("enderecoDevedor");
		textField.setLabel(new Model<String>("Endereco"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> nomeDevedor() {
		TextField<String> textField = new TextField<String>("nomeDevedor");
		textField.setLabel(new Model<String>("Nome do Devedor"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<BigDecimal> valorSaldoTitulo() {
		TextField<BigDecimal> textField = new TextField<BigDecimal>("valorSaldoTitulo");
		textField.setLabel(new Model<String>("Valor de Protesto"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}
	
	private TextField<String> bairroDevedor() {
		TextField<String> textField = new TextField<String>("bairroDevedor");
		textField.setLabel(new Model<String>("Bairro do Devedor"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<BigDecimal> valorTitulo() {
		TextField<BigDecimal> textField = new TextField<BigDecimal>("valorTitulo");
		textField.setLabel(new Model<String>("Valor do Débito"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> documentoDevedor() {
		TextField<String> textField = new TextField<String>("CpfCnpj");
		textField.setLabel(new Model<String>("CPF/CNPJ"));
		textField.setRequired(true);
		return textField;
	}

	private DropDownChoice<TipoEspecieTitulo> especieTitulo() {
		IChoiceRenderer<TipoEspecieTitulo> renderer = new ChoiceRenderer<TipoEspecieTitulo>("label");
		final DropDownChoice<TipoEspecieTitulo> dropDownEspecie =  new DropDownChoice<TipoEspecieTitulo>("especieTitulo", Arrays.asList(TipoEspecieTitulo.values()), renderer);
		dropDownEspecie.add(new OnChangeAjaxBehavior() {
			@Override
            protected void onUpdate(AjaxRequestTarget target){
                
            	TipoEspecieTitulo tipoEspecie = dropDownEspecie.getModelObject();
            	if (tipoEspecie != null) {
	            	if (tipoEspecie.equals(TipoEspecieTitulo.CH)) {
	            		comboAlinea.setEnabled(true);
	            		comboAlinea.setRequired(true);
	            	} else {
	            		comboAlinea.setEnabled(false);
	            		comboAlinea.setRequired(false);
	            	}
            	} else {
            		comboAlinea.setEnabled(false);
            		comboAlinea.setRequired(false);
            	}
            	target.add(comboAlinea);
            }
        });
		dropDownEspecie.setOutputMarkupId(true);
		return dropDownEspecie;
	}
	
	private DropDownChoice<TipoAlineaCheque> campoAlinea() {
		IChoiceRenderer<TipoAlineaCheque> renderer = new ChoiceRenderer<TipoAlineaCheque>("label");
		comboAlinea = new DropDownChoice<TipoAlineaCheque>("alinea", Arrays.asList(TipoAlineaCheque.values()), renderer);
		comboAlinea.setLabel(new Model<String>("Alínea"));
		comboAlinea.setEnabled(false);
		comboAlinea.setOutputMarkupId(true);
		return comboAlinea;
	}

	public List<Avalista> getAvalistas() {
		if (avalistas == null) {
			avalistas = new ArrayList<Avalista>();
		}
		return avalistas;
	}

	public void setAvalistas(List<Avalista> avalistas) {
		this.avalistas = avalistas;
	}
	
	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(tituloFiliado);
	}
}
