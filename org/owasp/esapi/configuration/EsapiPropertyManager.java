package org.owasp.esapi.configuration;

import org.owasp.esapi.configuration.consts.EsapiConfiguration;
import java.util.Iterator;
import org.owasp.esapi.errors.ConfigurationException;
import java.util.TreeSet;

public class EsapiPropertyManager implements EsapiPropertyLoader
{
    protected TreeSet<AbstractPrioritizedPropertyLoader> loaders;
    
    public EsapiPropertyManager() {
        this.initLoaders();
    }
    
    @Override
    public int getIntProp(final String propertyName) throws ConfigurationException {
        for (final AbstractPrioritizedPropertyLoader loader : this.loaders) {
            try {
                return loader.getIntProp(propertyName);
            }
            catch (final ConfigurationException e) {
                System.err.println("Property not found in " + loader.name());
                continue;
            }
            break;
        }
        throw new ConfigurationException("Could not find property " + propertyName + " in configuration");
    }
    
    @Override
    public byte[] getByteArrayProp(final String propertyName) throws ConfigurationException {
        for (final AbstractPrioritizedPropertyLoader loader : this.loaders) {
            try {
                return loader.getByteArrayProp(propertyName);
            }
            catch (final ConfigurationException e) {
                System.err.println("Property not found in " + loader.name());
                continue;
            }
            break;
        }
        throw new ConfigurationException("Could not find property " + propertyName + " in configuration");
    }
    
    @Override
    public Boolean getBooleanProp(final String propertyName) throws ConfigurationException {
        for (final AbstractPrioritizedPropertyLoader loader : this.loaders) {
            try {
                return loader.getBooleanProp(propertyName);
            }
            catch (final ConfigurationException e) {
                System.err.println("Property not found in " + loader.name());
                continue;
            }
            break;
        }
        throw new ConfigurationException("Could not find property " + propertyName + " in configuration");
    }
    
    @Override
    public String getStringProp(final String propertyName) throws ConfigurationException {
        for (final AbstractPrioritizedPropertyLoader loader : this.loaders) {
            try {
                return loader.getStringProp(propertyName);
            }
            catch (final ConfigurationException e) {
                System.err.println("Property : " + propertyName + " not found in " + loader.name());
                continue;
            }
            break;
        }
        throw new ConfigurationException("Could not find property " + propertyName + " in configuration");
    }
    
    private void initLoaders() {
        this.loaders = new TreeSet<AbstractPrioritizedPropertyLoader>();
        try {
            this.loaders.add(EsapiPropertyLoaderFactory.createPropertyLoader(EsapiConfiguration.OPSTEAM_ESAPI_CFG));
        }
        catch (final Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            this.loaders.add(EsapiPropertyLoaderFactory.createPropertyLoader(EsapiConfiguration.DEVTEAM_ESAPI_CFG));
        }
        catch (final Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
