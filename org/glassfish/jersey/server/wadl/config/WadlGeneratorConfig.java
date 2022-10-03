package org.glassfish.jersey.server.wadl.config;

import java.util.Hashtable;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.List;

public abstract class WadlGeneratorConfig
{
    public abstract List configure();
    
    public WadlGenerator createWadlGenerator(final InjectionManager injectionManager) {
        List<WadlGeneratorDescription> wadlGeneratorDescriptions;
        try {
            wadlGeneratorDescriptions = this.configure();
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_CONFIGURE(), (Throwable)e);
        }
        for (final WadlGeneratorDescription desc : wadlGeneratorDescriptions) {
            desc.setConfiguratorClass(this.getClass());
        }
        WadlGenerator wadlGenerator;
        try {
            wadlGenerator = WadlGeneratorLoader.loadWadlGeneratorDescriptions(injectionManager, wadlGeneratorDescriptions);
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_LOAD(), (Throwable)e);
        }
        return wadlGenerator;
    }
    
    public static WadlGeneratorConfigDescriptionBuilder generator(final Class<? extends WadlGenerator> generatorClass) {
        return new WadlGeneratorConfigDescriptionBuilder().generator(generatorClass);
    }
    
    public static class WadlGeneratorConfigDescriptionBuilder
    {
        private List<WadlGeneratorDescription> _descriptions;
        private WadlGeneratorDescription _description;
        
        public WadlGeneratorConfigDescriptionBuilder() {
            this._descriptions = new ArrayList<WadlGeneratorDescription>();
        }
        
        public WadlGeneratorConfigDescriptionBuilder generator(final Class<? extends WadlGenerator> generatorClass) {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            (this._description = new WadlGeneratorDescription()).setGeneratorClass(generatorClass);
            return this;
        }
        
        public WadlGeneratorConfigDescriptionBuilder prop(final String propName, final Object propValue) {
            if (this._description.getProperties() == null) {
                this._description.setProperties(new Properties());
            }
            ((Hashtable<String, Object>)this._description.getProperties()).put(propName, propValue);
            return this;
        }
        
        public List<WadlGeneratorDescription> descriptions() {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            return this._descriptions;
        }
        
        public WadlGeneratorConfig build() {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            return new WadlGeneratorConfigImpl(this._descriptions);
        }
    }
    
    static class WadlGeneratorConfigImpl extends WadlGeneratorConfig
    {
        public List<WadlGeneratorDescription> _descriptions;
        
        public WadlGeneratorConfigImpl(final List<WadlGeneratorDescription> descriptions) {
            this._descriptions = descriptions;
        }
        
        @Override
        public List<WadlGeneratorDescription> configure() {
            return this._descriptions;
        }
    }
}
