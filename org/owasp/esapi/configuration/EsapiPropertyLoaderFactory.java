package org.owasp.esapi.configuration;

import java.io.FileNotFoundException;
import org.owasp.esapi.configuration.consts.EsapiConfigurationType;
import org.owasp.esapi.errors.ConfigurationException;
import org.owasp.esapi.configuration.consts.EsapiConfiguration;

public class EsapiPropertyLoaderFactory
{
    public static AbstractPrioritizedPropertyLoader createPropertyLoader(final EsapiConfiguration cfg) throws ConfigurationException, FileNotFoundException {
        final String cfgPath = System.getProperty(cfg.getConfigName());
        if (cfgPath == null) {
            throw new ConfigurationException("System property [" + cfg.getConfigName() + "] is not set");
        }
        final String fileExtension = cfgPath.substring(cfgPath.lastIndexOf(46) + 1);
        if (EsapiConfigurationType.XML.getTypeName().equals(fileExtension)) {
            return new XmlEsapiPropertyLoader(cfgPath, cfg.getPriority());
        }
        if (EsapiConfigurationType.PROPERTIES.getTypeName().equals(fileExtension)) {
            return new StandardEsapiPropertyLoader(cfgPath, cfg.getPriority());
        }
        throw new ConfigurationException("Configuration storage type [" + fileExtension + "] is not " + "supported");
    }
}
