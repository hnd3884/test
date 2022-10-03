package org.glassfish.jersey.jaxb.internal;

import javax.xml.transform.TransformerConfigurationException;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.Configuration;
import java.util.logging.Logger;
import javax.xml.transform.TransformerFactory;

public class TransformerFactoryInjectionProvider extends AbstractXmlFactory<TransformerFactory>
{
    private static final Logger LOGGER;
    
    @Inject
    public TransformerFactoryInjectionProvider(final Configuration config) {
        super(config);
    }
    
    @Override
    public TransformerFactory get() {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        if (!this.isXmlSecurityDisabled()) {
            try {
                transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            }
            catch (final TransformerConfigurationException e) {
                TransformerFactoryInjectionProvider.LOGGER.log(Level.CONFIG, LocalizationMessages.UNABLE_TO_SECURE_XML_TRANSFORMER_PROCESSING(), e);
            }
        }
        return transformerFactory;
    }
    
    static {
        LOGGER = Logger.getLogger(TransformerFactoryInjectionProvider.class.getName());
    }
}
