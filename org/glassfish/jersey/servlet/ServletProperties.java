package org.glassfish.jersey.servlet;

import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public final class ServletProperties
{
    public static final String FILTER_CONTEXT_PATH = "jersey.config.servlet.filter.contextPath";
    public static final String FILTER_FORWARD_ON_404 = "jersey.config.servlet.filter.forwardOn404";
    public static final String FILTER_STATIC_CONTENT_REGEX = "jersey.config.servlet.filter.staticContentRegex";
    public static final String JAXRS_APPLICATION_CLASS = "javax.ws.rs.Application";
    public static final String PROVIDER_WEB_APP = "jersey.config.servlet.provider.webapp";
    public static final String QUERY_PARAMS_AS_FORM_PARAMS_DISABLED = "jersey.config.servlet.form.queryParams.disabled";
    public static final String SERVICE_LOCATOR = "jersey.config.servlet.context.serviceLocator";
    
    private ServletProperties() {
    }
}
