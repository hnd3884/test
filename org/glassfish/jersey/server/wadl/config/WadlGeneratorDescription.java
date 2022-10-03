package org.glassfish.jersey.server.wadl.config;

import java.util.Properties;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorDescription
{
    private Class<? extends WadlGenerator> generatorClass;
    private Properties properties;
    private Class<?> configuratorClass;
    
    public WadlGeneratorDescription() {
    }
    
    public WadlGeneratorDescription(final Class<? extends WadlGenerator> generatorClass, final Properties properties) {
        this.generatorClass = generatorClass;
        this.properties = properties;
    }
    
    public Class<? extends WadlGenerator> getGeneratorClass() {
        return this.generatorClass;
    }
    
    public void setGeneratorClass(final Class<? extends WadlGenerator> generatorClass) {
        this.generatorClass = generatorClass;
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
    
    public Class<?> getConfiguratorClass() {
        return this.configuratorClass;
    }
    
    void setConfiguratorClass(final Class<?> configuratorClass) {
        this.configuratorClass = configuratorClass;
    }
}
