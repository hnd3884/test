package org.glassfish.jersey.jaxb.internal;

import javax.inject.Inject;
import javax.ws.rs.core.Configuration;
import javax.xml.stream.XMLInputFactory;

public class XmlInputFactoryInjectionProvider extends AbstractXmlFactory<XMLInputFactory>
{
    @Inject
    public XmlInputFactoryInjectionProvider(final Configuration config) {
        super(config);
    }
    
    @Override
    public XMLInputFactory get() {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        if (!this.isXmlSecurityDisabled()) {
            factory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        }
        return factory;
    }
}
