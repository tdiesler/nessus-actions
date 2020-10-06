package io.nessus.itest.actions.wildfly.ticker;

import javax.enterprise.context.ApplicationScoped;

import org.wildfly.extension.camel.CamelAware;

import io.nessus.actions.model.utils.ModelBasedRouteBuilder;

@CamelAware
@ApplicationScoped
public class ApplicationScopedRouteBuilder extends ModelBasedRouteBuilder {

}