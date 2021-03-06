package br.com.ieptbto.cra.hibernate.audit;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.security.UserSession;
import org.apache.wicket.protocol.http.WebSession;
import org.hibernate.envers.RevisionListener;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("unchecked")
public class CraRevisionListener implements RevisionListener {

	@Override
	public void newRevision(final Object revisionEntity) {
		CraCustomRevisionEntity entity = CraCustomRevisionEntity.class.cast(revisionEntity);

		if (WebSession.exists()) {
			UserSession<Usuario> userSession = (UserSession<Usuario>) WebSession.get();
			entity.setLogin(userSession.getUser().getLogin());
			entity.setIdUsuario(userSession.getUser().getId());
		}

	}

}
