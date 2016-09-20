package br.com.ieptbto.cra.page.titulo.entrada;

import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoEspecieTitulo;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import br.com.ieptbto.cra.page.base.BaseForm;
import br.com.ieptbto.cra.util.CpfCnpjUtil;

/**
 * @author Thasso Araújo
 *
 */
public class EntradaManualForm extends BaseForm<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private UsuarioFiliadoMediator usuarioFiliadoMediator;
	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	private FiliadoMediator filiadoMediator;

	private FileUploadField fileUploadField;
	private Usuario usuario;

	public EntradaManualForm(String id, IModel<TituloFiliado> model, Usuario usuario, FileUploadField fileUploadField) {
		super(id, model);
		this.fileUploadField = fileUploadField;
		this.usuario = usuario;
	}

	@Override
	protected void onSubmit() {
		TituloFiliado titulo = getModel().getObject();
		titulo.setDataEntrada(new LocalDate().toDate());

		try {
			if (titulo.getDataEmissao().equals(titulo.getDataVencimento())) {
				if (!titulo.getEspecieTitulo().equals(TipoEspecieTitulo.CH) && !titulo.getEspecieTitulo().equals(TipoEspecieTitulo.CDA)
						&& !titulo.getEspecieTitulo().equals(TipoEspecieTitulo.ATC)) {
					throw new InfraException("A Data de Vencimento do título não pode ser igual a data atual!");
				}
			}
			if (new LocalDate(titulo.getDataEmissao()).isAfter(new LocalDate(titulo.getDataVencimento()))) {
				throw new InfraException("A Data de Emissão do título deve ser antes do Data do Vencimento !");
			} else if (new LocalDate(titulo.getDataVencimento()).isAfter(new LocalDate())) {
				throw new InfraException("A Data de Vencimento do título deve ser antes da data atual!");
			} else if (new LocalDate(titulo.getDataEmissao()).isAfter(new LocalDate())) {
				throw new InfraException("A Data de Emissão do título deve ser antes da data atual!");
			}
			if (titulo.getCpfCnpj() != null) {
				String documentoDevedor = titulo.getCpfCnpj().replace(".", "").replace("-", "").replace("/", "");
				if (!CpfCnpjUtil.isValidCNPJ(documentoDevedor) && !CpfCnpjUtil.isValidCPF(documentoDevedor)) {
					throw new InfraException("O CNPJ/CPF do devedor está inválido! Por favor verifique se o documento foi digitado corretamente...");
				}
			}

			if (fileUploadField.getFileUpload() != null) {
				if (!fileUploadField.getFileUpload().getClientFileName().toUpperCase().contains(".ZIP")) {
					throw new InfraException("O anexo do documento devem estar compactados em formato ZIP.");
				}
				if (!Bytes.megabytes(15).greaterThan(fileUploadField.getFileUpload().getSize())) {
					throw new InfraException("Tamanho do arquivo anexo excedido. Limite máximo de 15 MB.");
				}
			}

			tituloFiliadoMediator.salvarTituloConvenio(usuario, titulo, fileUploadField.getFileUpload());
			setResponsePage(new EntradaManualPage("Os dados do título foram salvos com sucesso e está pendente de envio para protesto!"));
		} catch (InfraException e) {
			logger.info(e.getMessage(), e);
			error(e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			error("Não foi possível realizar a entrada manual! Entre em contato com o IEPTB !");
		}
	}
}