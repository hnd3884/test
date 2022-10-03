package com.sun.xml.internal.ws.db;

import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.BindingID;
import org.xml.sax.EntityResolver;
import javax.xml.transform.Source;
import java.net.URL;
import javax.xml.namespace.QName;
import java.io.InputStream;
import javax.xml.ws.WebServiceException;
import java.util.Properties;
import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import javax.xml.ws.WebServiceFeature;
import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.oracle.webservices.internal.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import java.util.HashMap;
import java.util.Iterator;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.List;
import com.sun.xml.internal.ws.spi.db.DatabindingProvider;
import java.util.Map;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;

public class DatabindingFactoryImpl extends DatabindingFactory
{
    static final String WsRuntimeFactoryDefaultImpl = "com.sun.xml.internal.ws.db.DatabindingProviderImpl";
    protected Map<String, Object> properties;
    protected DatabindingProvider defaultRuntimeFactory;
    protected List<DatabindingProvider> providers;
    
    private static List<DatabindingProvider> providers() {
        final List<DatabindingProvider> factories = new ArrayList<DatabindingProvider>();
        for (final DatabindingProvider p : ServiceFinder.find(DatabindingProvider.class)) {
            factories.add(p);
        }
        return factories;
    }
    
    public DatabindingFactoryImpl() {
        this.properties = new HashMap<String, Object>();
    }
    
    @Override
    public Map<String, Object> properties() {
        return this.properties;
    }
    
     <T> T property(final Class<T> propType, String propName) {
        if (propName == null) {
            propName = propType.getName();
        }
        return propType.cast(this.properties.get(propName));
    }
    
    public DatabindingProvider provider(final DatabindingConfig config) {
        final String mode = this.databindingMode(config);
        if (this.providers == null) {
            this.providers = providers();
        }
        DatabindingProvider provider = null;
        if (this.providers != null) {
            for (final DatabindingProvider p : this.providers) {
                if (p.isFor(mode)) {
                    provider = p;
                }
            }
        }
        if (provider == null) {
            provider = new DatabindingProviderImpl();
        }
        return provider;
    }
    
    @Override
    public Databinding createRuntime(final DatabindingConfig config) {
        final DatabindingProvider provider = this.provider(config);
        return provider.create(config);
    }
    
    public WSDLGenerator createWsdlGen(final DatabindingConfig config) {
        final DatabindingProvider provider = this.provider(config);
        return provider.wsdlGen(config);
    }
    
    String databindingMode(final DatabindingConfig config) {
        if (config.getMappingInfo() != null && config.getMappingInfo().getDatabindingMode() != null) {
            return config.getMappingInfo().getDatabindingMode();
        }
        if (config.getFeatures() != null) {
            for (final WebServiceFeature f : config.getFeatures()) {
                if (f instanceof DatabindingModeFeature) {
                    final DatabindingModeFeature dmf = (DatabindingModeFeature)f;
                    config.properties().putAll(dmf.getProperties());
                    return dmf.getMode();
                }
            }
        }
        return null;
    }
    
    ClassLoader classLoader() {
        ClassLoader classLoader = this.property(ClassLoader.class, null);
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return classLoader;
    }
    
    Properties loadPropertiesFile(final String fileName) {
        final ClassLoader classLoader = this.classLoader();
        final Properties p = new Properties();
        try {
            InputStream is = null;
            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(fileName);
            }
            else {
                is = classLoader.getResourceAsStream(fileName);
            }
            if (is != null) {
                p.load(is);
            }
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
        return p;
    }
    
    @Override
    public Databinding.Builder createBuilder(final Class<?> contractClass, final Class<?> endpointClass) {
        return new ConfigBuilder(this, contractClass, endpointClass);
    }
    
    static class ConfigBuilder implements Databinding.Builder
    {
        DatabindingConfig config;
        DatabindingFactoryImpl factory;
        
        ConfigBuilder(final DatabindingFactoryImpl f, final Class<?> contractClass, final Class<?> implBeanClass) {
            this.factory = f;
            (this.config = new DatabindingConfig()).setContractClass(contractClass);
            this.config.setEndpointClass(implBeanClass);
        }
        
        @Override
        public Databinding.Builder targetNamespace(final String targetNamespace) {
            this.config.getMappingInfo().setTargetNamespace(targetNamespace);
            return this;
        }
        
        @Override
        public Databinding.Builder serviceName(final QName serviceName) {
            this.config.getMappingInfo().setServiceName(serviceName);
            return this;
        }
        
        @Override
        public Databinding.Builder portName(final QName portName) {
            this.config.getMappingInfo().setPortName(portName);
            return this;
        }
        
        @Override
        public Databinding.Builder wsdlURL(final URL wsdlURL) {
            this.config.setWsdlURL(wsdlURL);
            return this;
        }
        
        @Override
        public Databinding.Builder wsdlSource(final Source wsdlSource) {
            this.config.setWsdlSource(wsdlSource);
            return this;
        }
        
        @Override
        public Databinding.Builder entityResolver(final EntityResolver entityResolver) {
            this.config.setEntityResolver(entityResolver);
            return this;
        }
        
        @Override
        public Databinding.Builder classLoader(final ClassLoader classLoader) {
            this.config.setClassLoader(classLoader);
            return this;
        }
        
        @Override
        public Databinding.Builder feature(final WebServiceFeature... f) {
            this.config.setFeatures(f);
            return this;
        }
        
        @Override
        public Databinding.Builder property(final String name, final Object value) {
            this.config.properties().put(name, value);
            if (this.isfor(BindingID.class, name, value)) {
                this.config.getMappingInfo().setBindingID((BindingID)value);
            }
            if (this.isfor(WSBinding.class, name, value)) {
                this.config.setWSBinding((WSBinding)value);
            }
            if (this.isfor(WSDLPort.class, name, value)) {
                this.config.setWsdlPort((WSDLPort)value);
            }
            if (this.isfor(MetadataReader.class, name, value)) {
                this.config.setMetadataReader((MetadataReader)value);
            }
            return this;
        }
        
        boolean isfor(final Class<?> type, final String name, final Object value) {
            return type.getName().equals(name) && type.isInstance(value);
        }
        
        @Override
        public Databinding build() {
            return this.factory.createRuntime(this.config);
        }
        
        @Override
        public WSDLGenerator createWSDLGenerator() {
            return this.factory.createWsdlGen(this.config);
        }
    }
}
