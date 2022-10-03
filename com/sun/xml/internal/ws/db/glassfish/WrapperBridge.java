package com.sun.xml.internal.ws.db.glassfish;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import java.io.InputStream;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import org.w3c.dom.Node;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.spi.db.XMLBridge;

public class WrapperBridge<T> implements XMLBridge<T>
{
    private JAXBRIContextWrapper parent;
    private Bridge<T> bridge;
    
    public WrapperBridge(final JAXBRIContextWrapper p, final Bridge<T> b) {
        this.parent = p;
        this.bridge = b;
    }
    
    @Override
    public BindingContext context() {
        return this.parent;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.bridge.equals(obj);
    }
    
    @Override
    public TypeInfo getTypeInfo() {
        return this.parent.typeInfo(this.bridge.getTypeReference());
    }
    
    @Override
    public int hashCode() {
        return this.bridge.hashCode();
    }
    
    static CompositeStructure convert(final Object o) {
        final WrapperComposite w = (WrapperComposite)o;
        final CompositeStructure cs = new CompositeStructure();
        cs.values = w.values;
        cs.bridges = new Bridge[w.bridges.length];
        for (int i = 0; i < cs.bridges.length; ++i) {
            cs.bridges[i] = ((BridgeWrapper)w.bridges[i]).getBridge();
        }
        return cs;
    }
    
    @Override
    public final void marshal(final T object, final ContentHandler contentHandler, final AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal((T)convert(object), contentHandler, am);
    }
    
    @Override
    public void marshal(final T object, final Node output) throws JAXBException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void marshal(final T object, final OutputStream output, final NamespaceContext nsContext, final AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal((T)convert(object), output, nsContext, am);
    }
    
    @Override
    public final void marshal(final T object, final Result result) throws JAXBException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final void marshal(final T object, final XMLStreamWriter output, final AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal((T)convert(object), output, am);
    }
    
    @Override
    public String toString() {
        return BridgeWrapper.class.getName() + " : " + this.bridge.toString();
    }
    
    @Override
    public final T unmarshal(final InputStream in) throws JAXBException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final T unmarshal(final Node n, final AttachmentUnmarshaller au) throws JAXBException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final T unmarshal(final Source in, final AttachmentUnmarshaller au) throws JAXBException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final T unmarshal(final XMLStreamReader in, final AttachmentUnmarshaller au) throws JAXBException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean supportOutputStream() {
        return true;
    }
}
