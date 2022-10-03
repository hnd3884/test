package com.sun.xml.internal.ws.db;

import java.io.File;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import java.util.Map;
import com.sun.xml.internal.ws.spi.db.DatabindingProvider;

public class DatabindingProviderImpl implements DatabindingProvider
{
    private static final String CachedDatabinding = "com.sun.xml.internal.ws.db.DatabindingProviderImpl";
    Map<String, Object> properties;
    
    @Override
    public void init(final Map<String, Object> p) {
        this.properties = p;
    }
    
    DatabindingImpl getCachedDatabindingImpl(final DatabindingConfig config) {
        final Object object = config.properties().get("com.sun.xml.internal.ws.db.DatabindingProviderImpl");
        return (object != null && object instanceof DatabindingImpl) ? ((DatabindingImpl)object) : null;
    }
    
    @Override
    public Databinding create(final DatabindingConfig config) {
        DatabindingImpl impl = this.getCachedDatabindingImpl(config);
        if (impl == null) {
            impl = new DatabindingImpl(this, config);
            config.properties().put("com.sun.xml.internal.ws.db.DatabindingProviderImpl", impl);
        }
        return impl;
    }
    
    @Override
    public WSDLGenerator wsdlGen(final DatabindingConfig config) {
        final DatabindingImpl impl = (DatabindingImpl)this.create(config);
        return new JaxwsWsdlGen(impl);
    }
    
    @Override
    public boolean isFor(final String databindingMode) {
        return true;
    }
    
    public static class JaxwsWsdlGen implements WSDLGenerator
    {
        DatabindingImpl databinding;
        WSDLGenInfo wsdlGenInfo;
        
        JaxwsWsdlGen(final DatabindingImpl impl) {
            this.databinding = impl;
            this.wsdlGenInfo = new WSDLGenInfo();
        }
        
        @Override
        public WSDLGenerator inlineSchema(final boolean inline) {
            this.wsdlGenInfo.setInlineSchemas(inline);
            return this;
        }
        
        @Override
        public WSDLGenerator property(final String name, final Object value) {
            return this;
        }
        
        @Override
        public void generate(final WSDLResolver wsdlResolver) {
            this.wsdlGenInfo.setWsdlResolver(wsdlResolver);
            this.databinding.generateWSDL(this.wsdlGenInfo);
        }
        
        @Override
        public void generate(final File outputDir, final String name) {
            this.databinding.generateWSDL(this.wsdlGenInfo);
        }
    }
}
