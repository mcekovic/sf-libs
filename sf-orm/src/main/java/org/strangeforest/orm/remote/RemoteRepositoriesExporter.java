package org.strangeforest.orm.remote;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

import org.strangeforest.orm.*;

public class RemoteRepositoriesExporter {

	private final LocalDomainContext context;
	private final Registry registry;
	private final List<RepositoryRemote> remoteRepositories;
	private int port;

	public RemoteRepositoriesExporter(LocalDomainContext context, Registry registry) {
		super();
		this.context = context;
		this.registry = registry;
		remoteRepositories = new ArrayList<>();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void export() throws RemoteException {
		for (Repository repository : context.getRepositories()) {
			RepositoryRemote remoteRepository = new RepositoryRemoteImpl(context, repository, port);
			remoteRepositories.add(remoteRepository);
			registry.rebind(getBindName(repository), remoteRepository);
		}
	}

	public void unexport() throws RemoteException, NotBoundException {
		for (Repository repository : context.getRepositories())
			registry.unbind(getBindName(repository));
		for (RepositoryRemote remoteRepository : remoteRepositories)
			UnicastRemoteObject.unexportObject(remoteRepository, true);
	}

	private String getBindName(Repository repository) {
		return repository.getEntityName() + "Repository";
	}
}