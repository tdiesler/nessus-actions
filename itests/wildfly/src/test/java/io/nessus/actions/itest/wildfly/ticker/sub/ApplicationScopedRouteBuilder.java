package io.nessus.actions.itest.wildfly.ticker.sub;

import javax.enterprise.context.ApplicationScoped;

import org.wildfly.extension.camel.CamelAware;

import io.nessus.actions.runner.ModelBasedRouteBuilder;

@CamelAware
@ApplicationScoped
public class ApplicationScopedRouteBuilder extends ModelBasedRouteBuilder {

}