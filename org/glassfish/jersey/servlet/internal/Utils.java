package org.glassfish.jersey.servlet.internal;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.glassfish.jersey.server.ResourceConfig;

public final class Utils
{
    private static final String RESOURCE_CONFIG = "jersey.config.servlet.internal.resourceConfig";
    
    public static void store(final ResourceConfig config, final ServletContext context, final String configName) {
        final String attributeName = "jersey.config.servlet.internal.resourceConfig_" + configName;
        context.setAttribute(attributeName, (Object)config);
    }
    
    public static ResourceConfig retrieve(final ServletContext context, final String configName) {
        final String attributeName = "jersey.config.servlet.internal.resourceConfig_" + configName;
        final ResourceConfig config = (ResourceConfig)context.getAttribute(attributeName);
        context.removeAttribute(attributeName);
        return config;
    }
    
    public static Map<String, Object> getContextParams(final ServletContext servletContext) {
        final Map<String, Object> props = new HashMap<String, Object>();
        final Enumeration names = servletContext.getAttributeNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            props.put(name, servletContext.getAttribute(name));
        }
        return props;
    }
    
    private Utils() {
    }
}
