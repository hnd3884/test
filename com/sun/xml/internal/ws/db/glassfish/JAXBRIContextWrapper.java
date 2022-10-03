package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import java.util.List;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import javax.xml.namespace.QName;
import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Iterator;
import java.util.HashMap;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.util.Map;
import com.sun.xml.internal.ws.spi.db.BindingContext;

class JAXBRIContextWrapper implements BindingContext
{
    private Map<TypeInfo, TypeReference> typeRefs;
    private Map<TypeReference, TypeInfo> typeInfos;
    private JAXBRIContext context;
    
    JAXBRIContextWrapper(final JAXBRIContext cxt, final Map<TypeInfo, TypeReference> refs) {
        this.context = cxt;
        this.typeRefs = refs;
        if (refs != null) {
            this.typeInfos = new HashMap<TypeReference, TypeInfo>();
            for (final TypeInfo ti : refs.keySet()) {
                this.typeInfos.put(this.typeRefs.get(ti), ti);
            }
        }
    }
    
    TypeReference typeReference(final TypeInfo ti) {
        return (this.typeRefs != null) ? this.typeRefs.get(ti) : null;
    }
    
    TypeInfo typeInfo(final TypeReference tr) {
        return (this.typeInfos != null) ? this.typeInfos.get(tr) : null;
    }
    
    @Override
    public Marshaller createMarshaller() throws JAXBException {
        return this.context.createMarshaller();
    }
    
    @Override
    public Unmarshaller createUnmarshaller() throws JAXBException {
        return this.context.createUnmarshaller();
    }
    
    @Override
    public void generateSchema(final SchemaOutputResolver outputResolver) throws IOException {
        this.context.generateSchema(outputResolver);
    }
    
    @Override
    public String getBuildId() {
        return this.context.getBuildId();
    }
    
    @Override
    public QName getElementName(final Class o) throws JAXBException {
        return this.context.getElementName(o);
    }
    
    @Override
    public QName getElementName(final Object o) throws JAXBException {
        return this.context.getElementName(o);
    }
    
    @Override
    public <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(final Class<B> wrapperBean, final String nsUri, final String localName) throws JAXBException {
        return new RawAccessorWrapper(this.context.getElementPropertyAccessor(wrapperBean, nsUri, localName));
    }
    
    @Override
    public List<String> getKnownNamespaceURIs() {
        return this.context.getKnownNamespaceURIs();
    }
    
    public RuntimeTypeInfoSet getRuntimeTypeInfoSet() {
        return this.context.getRuntimeTypeInfoSet();
    }
    
    public QName getTypeName(final TypeReference tr) {
        return this.context.getTypeName(tr);
    }
    
    @Override
    public int hashCode() {
        return this.context.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final JAXBRIContextWrapper other = (JAXBRIContextWrapper)obj;
        return this.context == other.context || (this.context != null && this.context.equals(other.context));
    }
    
    @Override
    public boolean hasSwaRef() {
        return this.context.hasSwaRef();
    }
    
    @Override
    public String toString() {
        return JAXBRIContextWrapper.class.getName() + " : " + this.context.toString();
    }
    
    @Override
    public XMLBridge createBridge(final TypeInfo ti) {
        final TypeReference tr = this.typeRefs.get(ti);
        final Bridge b = this.context.createBridge(tr);
        return (XMLBridge)(WrapperComposite.class.equals(ti.type) ? new WrapperBridge(this, b) : new BridgeWrapper(this, b));
    }
    
    @Override
    public JAXBContext getJAXBContext() {
        return this.context;
    }
    
    @Override
    public QName getTypeName(final TypeInfo ti) {
        final TypeReference tr = this.typeRefs.get(ti);
        return this.context.getTypeName(tr);
    }
    
    @Override
    public XMLBridge createFragmentBridge() {
        return new MarshallerBridge((JAXBContextImpl)this.context);
    }
    
    @Override
    public Object newWrapperInstace(final Class<?> wrapperType) throws InstantiationException, IllegalAccessException {
        return wrapperType.newInstance();
    }
}
