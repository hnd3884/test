package com.sun.xml.internal.ws.db.glassfish;

import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.Unmarshaller;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import javax.xml.transform.Source;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import java.io.InputStream;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import org.w3c.dom.Node;
import javax.xml.bind.JAXBException;
import org.xml.sax.ContentHandler;
import javax.xml.bind.Marshaller;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.spi.db.XMLBridge;

public class BridgeWrapper<T> implements XMLBridge<T>
{
    private JAXBRIContextWrapper parent;
    private Bridge<T> bridge;
    
    public BridgeWrapper(final JAXBRIContextWrapper p, final Bridge<T> b) {
        this.parent = p;
        this.bridge = b;
    }
    
    @Override
    public BindingContext context() {
        return this.parent;
    }
    
    Bridge getBridge() {
        return this.bridge;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.bridge.equals(obj);
    }
    
    public JAXBRIContext getContext() {
        return this.bridge.getContext();
    }
    
    @Override
    public TypeInfo getTypeInfo() {
        return this.parent.typeInfo(this.bridge.getTypeReference());
    }
    
    @Override
    public int hashCode() {
        return this.bridge.hashCode();
    }
    
    public void marshal(final Marshaller m, final T object, final ContentHandler contentHandler) throws JAXBException {
        this.bridge.marshal(m, object, contentHandler);
    }
    
    public void marshal(final Marshaller m, final T object, final Node output) throws JAXBException {
        this.bridge.marshal(m, object, output);
    }
    
    public void marshal(final Marshaller m, final T object, final OutputStream output, final NamespaceContext nsContext) throws JAXBException {
        this.bridge.marshal(m, object, output, nsContext);
    }
    
    public void marshal(final Marshaller m, final T object, final Result result) throws JAXBException {
        this.bridge.marshal(m, object, result);
    }
    
    public void marshal(final Marshaller m, final T object, final XMLStreamWriter output) throws JAXBException {
        this.bridge.marshal(m, object, output);
    }
    
    @Override
    public final void marshal(final T object, final ContentHandler contentHandler, final AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal(object, contentHandler, am);
    }
    
    public void marshal(final T object, final ContentHandler contentHandler) throws JAXBException {
        this.bridge.marshal(object, contentHandler);
    }
    
    @Override
    public void marshal(final T object, final Node output) throws JAXBException {
        this.bridge.marshal(object, output);
    }
    
    @Override
    public void marshal(final T object, final OutputStream output, final NamespaceContext nsContext, final AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal(object, output, nsContext, am);
    }
    
    public void marshal(final T object, final OutputStream output, final NamespaceContext nsContext) throws JAXBException {
        this.bridge.marshal(object, output, nsContext);
    }
    
    @Override
    public final void marshal(final T object, final Result result) throws JAXBException {
        this.bridge.marshal(object, result);
    }
    
    @Override
    public final void marshal(final T object, final XMLStreamWriter output, final AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal(object, output, am);
    }
    
    public final void marshal(final T object, final XMLStreamWriter output) throws JAXBException {
        this.bridge.marshal(object, output);
    }
    
    @Override
    public String toString() {
        return BridgeWrapper.class.getName() + " : " + this.bridge.toString();
    }
    
    @Override
    public final T unmarshal(final InputStream in) throws JAXBException {
        return this.bridge.unmarshal(in);
    }
    
    @Override
    public final T unmarshal(final Node n, final AttachmentUnmarshaller au) throws JAXBException {
        return this.bridge.unmarshal(n, au);
    }
    
    public final T unmarshal(final Node n) throws JAXBException {
        return this.bridge.unmarshal(n);
    }
    
    @Override
    public final T unmarshal(final Source in, final AttachmentUnmarshaller au) throws JAXBException {
        return this.bridge.unmarshal(in, au);
    }
    
    public final T unmarshal(final Source in) throws DatabindingException {
        try {
            return this.bridge.unmarshal(in);
        }
        catch (final JAXBException e) {
            throw new DatabindingException(e);
        }
    }
    
    public T unmarshal(final Unmarshaller u, final InputStream in) throws JAXBException {
        return this.bridge.unmarshal(u, in);
    }
    
    public T unmarshal(final Unmarshaller context, final Node n) throws JAXBException {
        return this.bridge.unmarshal(context, n);
    }
    
    public T unmarshal(final Unmarshaller u, final Source in) throws JAXBException {
        return this.bridge.unmarshal(u, in);
    }
    
    public T unmarshal(final Unmarshaller u, final XMLStreamReader in) throws JAXBException {
        return this.bridge.unmarshal(u, in);
    }
    
    @Override
    public final T unmarshal(final XMLStreamReader in, final AttachmentUnmarshaller au) throws JAXBException {
        return this.bridge.unmarshal(in, au);
    }
    
    public final T unmarshal(final XMLStreamReader in) throws JAXBException {
        return this.bridge.unmarshal(in);
    }
    
    @Override
    public boolean supportOutputStream() {
        return true;
    }
}
