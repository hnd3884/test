package org.glassfish.jersey.jaxb.internal;

import javax.inject.Inject;
import javax.ws.rs.core.Configuration;
import javax.xml.parsers.SAXParserFactory;

public class SaxParserFactoryInjectionProvider extends AbstractXmlFactory<SAXParserFactory>
{
    @Inject
    public SaxParserFactoryInjectionProvider(final Configuration config) {
        super(config);
    }
    
    @Override
    public SAXParserFactory get() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        if (!this.isXmlSecurityDisabled()) {
            factory = new SecureSaxParserFactory(factory);
        }
        return factory;
    }
}
