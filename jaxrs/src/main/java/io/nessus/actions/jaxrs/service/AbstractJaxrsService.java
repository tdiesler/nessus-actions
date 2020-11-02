package io.nessus.actions.jaxrs.service;

import java.sql.Connection;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.service.AbstractService;
import io.nessus.common.CheckedExceptionWrapper;
import io.nessus.h2.ConnectionFactory;

class AbstractJaxrsService extends AbstractService<NessusConfig> {

	AbstractJaxrsService(NessusConfig config) {
		super(config);
	}

	interface CheckedFunction<T, R> {
		R apply(T value) throws Exception;
	}
	
	protected <R> R withConnection(CheckedFunction<Connection, R> function) {
		ConnectionFactory factory = new ConnectionFactory(config);
		try (Connection con = factory.createConnection()) {
			return function.apply(con);
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
}