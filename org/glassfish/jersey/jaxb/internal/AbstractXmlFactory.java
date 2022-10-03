package org.glassfish.jersey.jaxb.internal;

import org.glassfish.jersey.internal.util.PropertiesHelper;
import javax.ws.rs.core.Configuration;
import java.util.function.Supplier;

abstract class AbstractXmlFactory<T> implements Supplier<T>
{
    private final Configuration config;
    
    protected AbstractXmlFactory(final Configuration config) {
        this.config = config;
    }
    
    boolean isXmlSecurityDisabled() {
        return PropertiesHelper.isProperty(this.config.getProperty("jersey.config.xml.security.disable"));
    }
}
