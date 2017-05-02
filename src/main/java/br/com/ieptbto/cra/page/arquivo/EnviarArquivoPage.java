package br.com.ieptbto.cra.page.arquivo;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.*;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 * 
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class EnviarArquivoPage extends BasePage<Arquivo> {

	/****/
	private static final long serialVersionUID = 852632145;

	@SpringBean
	private ArquivoMediator arquivoMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;

	private Usuario usuario;
	private Arquivo arquivo;
	private Form<Arquivo> form;
	private FileUploadField fileUploadField;

	public EnviarArquivoPage() {
		this.arquivo = new Arquivo();
		this.usuario = getUser();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioEnvioArquivo();
	}

	private void formularioEnvioArquivo() {
		form = new Form<Arquivo>("form", getModel()) {

			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				getFeedbackMessages().clear();

				Arquivo arquivo = getModelObject();
				FileUpload uploadedFile = fileUploadField.getFileUpload();
				List<Exception> erros = new ArrayList<Exception>();

				arquivo.setInstituicaoRecebe(instituicaoMediator.buscarInstituicaoIncial(TipoInstituicaoCRA.CRA.toString()));
				arquivo.setNomeArquivo(uploadedFile.getClientFileName());
				arquivo.setDataRecebimento(new LocalDate().toDate());
				arquivo.setRemessas(new ArrayList<Remessa>());
				try {
					arquivo = arquivoMediator.salvar(arquivo, uploadedFile, getUsuario(), erros);

					if (!erros.isEmpty()) {
						gerarMensagemErros(arquivo, erros);
					} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.REMESSA)) {
						success("O arquivo de Remessa <span class=\"alert-link\">" + arquivo.getNomeArquivo()
								+ "</span> enviado, foi processado com sucesso !");
					} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO)) {
						success("O arquivo de Desistência de Protesto <span class=\"alert-link\">" + arquivo.getNomeArquivo()
								+ "</span> enviado, foi processado com sucesso !");
					}
					erros.clear();

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (ArquivoException ex) {
					logger.info(ex.getMessage(), ex);
					if (CodigoErro.CRA_ARQUIVO_ENVIADO_ANTERIORMENTE.equals(ex.getCodigoErro())) {
						error("Não foi possível importar, pois, o arquivo [ " + ex.getNomeArquivo() + " ] já foi enviado para a CRA !");
					} else {
						error("Não foi possível enviar o arquivo! Favor entrar em contato com a CRA...");
					}
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					error("Não foi possível enviar o arquivo! Favor entrar em contato com a CRA...");
				}
			}

			private void gerarMensagemErros(Arquivo arquivo, List<Exception> erros) {
				TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(arquivo);
				String mensagemErro = null;
				mensagemErro =
						"<span class=\"alert-link\">Por favor, corrija a(s) ocorrência(s) e(ou) erros encontrado(s) no arquivo e o envie novamente:</span>";
				mensagemErro = mensagemErro + "<ul>";
				for (Exception exception : erros) {
					if (TipoArquivoFebraban.REMESSA == tipoArquivo || TipoArquivoFebraban.CONFIRMACAO == tipoArquivo
							|| TipoArquivoFebraban.RETORNO == tipoArquivo) {
						if (TituloException.class.isInstance(exception)) {
							TituloException excecaoTitulo = TituloException.class.cast(exception);
							mensagemErro = mensagemErro + "<li><span class=\"alert-link\">Nº Sequencial do Registro "
									+ excecaoTitulo.getNumeroSequencialRegistro() + ": </span> [ Nosso Número = "
									+ excecaoTitulo.getNossoNumero() + " ]  -  " + excecaoTitulo.getDescricao() + ";</li>";
						} else if (CabecalhoRodapeException.class.isInstance(exception)) {
							CabecalhoRodapeException excecaoCabecalhoRodape = CabecalhoRodapeException.class.cast(exception);

							if (arquivo.getInstituicaoEnvio().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
								if (CodigoErro.CRA_ARQUIVO_CORROMPIDO_SOMA_DE_REGISTROS_DE_TRANSACAO_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER
										.equals(excecaoCabecalhoRodape.getCodigoErro())) {
									excecaoCabecalhoRodape.setCodigoErro(CodigoErro.SERPRO_QUANTIDADE_REGISTROS_INVALIDA);
								}
							}
							mensagemErro = mensagemErro + "<li>" + excecaoCabecalhoRodape.getDescricao() + ";</li>";
						}
					} else if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO == tipoArquivo
							|| TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO == tipoArquivo
							|| TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO == tipoArquivo) {
						DesistenciaCancelamentoException excecaoDesistencia = DesistenciaCancelamentoException.class.cast(exception);
						mensagemErro = mensagemErro + "<li>" + excecaoDesistencia.getDescricao() + ";</li>";
					}
				}
				mensagemErro = mensagemErro + "</ul>";
				warn(mensagemErro);
			}
		};
		form.setMultiPart(true);
		form.setMaxSize(Bytes.megabytes(10));

		form.add(campoArquivo());
		form.add(botaoEnviar());
		add(form);
	}

	private FileUploadField campoArquivo() {
		fileUploadField = new FileUploadField("file", new ListModel<FileUpload>());
		fileUploadField.setRequired(true);
		fileUploadField.setLabel(new Model<String>("Anexo de Arquivo"));
		return fileUploadField;
	}

	private AjaxButton botaoEnviar() {
		return new AjaxButton("enviarArquivo") {

			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}

			@Override
			protected void finalize() throws Throwable {
				super.finalize();
			}
		};
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}