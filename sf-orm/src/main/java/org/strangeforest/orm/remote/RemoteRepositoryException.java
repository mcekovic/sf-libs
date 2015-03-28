package org.strangeforest.orm.remote;

import java.rmi.*;

public class RemoteRepositoryException extends RuntimeException {

	public RemoteRepositoryException(Throwable cause) {
		super(cause);
	}

	public RemoteRepositoryException(RemoteException ex) {
		super(ex.getCause() != null ? ex.getCause() : ex);
	}
}
