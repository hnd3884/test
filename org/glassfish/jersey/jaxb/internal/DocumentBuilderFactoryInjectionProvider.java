package org.glassfish.jersey.jaxb.internal;

import javax.inject.Inject;
import javax.ws.rs.core.Configuration;
import javax.xml.parsers.DocumentBuilderFactory;

public class DocumentBuilderFactoryInjectionProvider extends AbstractXmlFactory<DocumentBuilderFactory>
{
    @Inject
    public DocumentBuilderFactoryInjectionProvider(final Configuration config) {
        super(config);
    }
    
    @Override
    public DocumentBuilderFactory get() {
        final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        if (!this.isXmlSecurityDisabled()) {
            f.setExpandEntityReferences(false);
        }
        return f;
    }
}
