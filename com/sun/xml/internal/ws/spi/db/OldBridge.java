package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import javax.xml.transform.Source;
import com.sun.istack.internal.Nullable;
import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import com.sun.xml.internal.bind.v2.runtime.BridgeContextImpl;
import com.sun.xml.internal.bind.api.BridgeContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamWriter;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;

public abstract class OldBridge<T>
{
    protected final JAXBContextImpl context;
    
    protected OldBridge(final JAXBContextImpl context) {
        this.context = context;
    }
    
    @NotNull
    public BindingContext getContext() {
        return null;
    }
    
    public final void marshal(final T object, final XMLStreamWriter output) throws JAXBException {
        this.marshal(object, output, null);
    }
    
    public final void marshal(final T object, final XMLStreamWriter output, final AttachmentMarshaller am) throws JAXBException {
        final Marshaller m = this.context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        this.marshal(m, object, output);
        m.setAttachmentMarshaller(null);
        this.context.marshallerPool.recycle(m);
    }
    
    public final void marshal(@NotNull final BridgeContext context, final T object, final XMLStreamWriter output) throws JAXBException {
        this.marshal(((BridgeContextImpl)context).marshaller, object, output);
    }
    
    public abstract void marshal(@NotNull final Marshaller p0, final T p1, final XMLStreamWriter p2) throws JAXBException;
    
    public void marshal(final T object, final OutputStream output, final NamespaceContext nsContext) throws JAXBException {
        this.marshal(object, output, nsContext, null);
    }
    
    public void marshal(final T object, final OutputStream output, final NamespaceContext nsContext, final AttachmentMarshaller am) throws JAXBException {
        final Marshaller m = this.context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        this.marshal(m, object, output, nsContext);
        m.setAttachmentMarshaller(null);
        this.context.marshallerPool.recycle(m);
    }
    
    public final void marshal(@NotNull final BridgeContext context, final T object, final OutputStream output, final NamespaceContext nsContext) throws JAXBException {
        this.marshal(((BridgeContextImpl)context).marshaller, object, output, nsContext);
    }
    
    public abstract void marshal(@NotNull final Marshaller p0, final T p1, final OutputStream p2, final NamespaceContext p3) throws JAXBException;
    
    public final void marshal(final T object, final Node output) throws JAXBException {
        final Marshaller m = this.context.marshallerPool.take();
        this.marshal(m, object, output);
        this.context.marshallerPool.recycle(m);
    }
    
    public final void marshal(@NotNull final BridgeContext context, final T object, final Node output) throws JAXBException {
        this.marshal(((BridgeContextImpl)context).marshaller, object, output);
    }
    
    public abstract void marshal(@NotNull final Marshaller p0, final T p1, final Node p2) throws JAXBException;
    
    public final void marshal(final T object, final ContentHandler contentHandler) throws JAXBException {
        this.marshal(object, contentHandler, null);
    }
    
    public final void marshal(final T object, final ContentHandler contentHandler, final AttachmentMarshaller am) throws JAXBException {
        final Marshaller m = this.context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        this.marshal(m, object, contentHandler);
        m.setAttachmentMarshaller(null);
        this.context.marshallerPool.recycle(m);
    }
    
    public final void marshal(@NotNull final BridgeContext context, final T object, final ContentHandler contentHandler) throws JAXBException {
        this.marshal(((BridgeContextImpl)context).marshaller, object, contentHandler);
    }
    
    public abstract void marshal(@NotNull final Marshaller p0, final T p1, final ContentHandler p2) throws JAXBException;
    
    public final void marshal(final T object, final Result result) throws JAXBException {
        final Marshaller m = this.context.marshallerPool.take();
        this.marshal(m, object, result);
        this.context.marshallerPool.recycle(m);
    }
    
    public final void marshal(@NotNull final BridgeContext context, final T object, final Result result) throws JAXBException {
        this.marshal(((BridgeContextImpl)context).marshaller, object, result);
    }
    
    public abstract void marshal(@NotNull final Marshaller p0, final T p1, final Result p2) throws JAXBException;
    
    private T exit(final T r, final Unmarshaller u) {
        u.setAttachmentUnmarshaller(null);
        this.context.unmarshallerPool.recycle(u);
        return r;
    }
    
    @NotNull
    public final T unmarshal(@NotNull final XMLStreamReader in) throws JAXBException {
        return this.unmarshal(in, null);
    }
    
    @NotNull
    public final T unmarshal(@NotNull final XMLStreamReader in, @Nullable final AttachmentUnmarshaller au) throws JAXBException {
        final Unmarshaller u = this.context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return this.exit(this.unmarshal(u, in), u);
    }
    
    @NotNull
    public final T unmarshal(@NotNull final BridgeContext context, @NotNull final XMLStreamReader in) throws JAXBException {
        return this.unmarshal(((BridgeContextImpl)context).unmarshaller, in);
    }
    
    @NotNull
    public abstract T unmarshal(@NotNull final Unmarshaller p0, @NotNull final XMLStreamReader p1) throws JAXBException;
    
    @NotNull
    public final T unmarshal(@NotNull final Source in) throws JAXBException {
        return this.unmarshal(in, null);
    }
    
    @NotNull
    public final T unmarshal(@NotNull final Source in, @Nullable final AttachmentUnmarshaller au) throws JAXBException {
        final Unmarshaller u = this.context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return this.exit(this.unmarshal(u, in), u);
    }
    
    @NotNull
    public final T unmarshal(@NotNull final BridgeContext context, @NotNull final Source in) throws JAXBException {
        return this.unmarshal(((BridgeContextImpl)context).unmarshaller, in);
    }
    
    @NotNull
    public abstract T unmarshal(@NotNull final Unmarshaller p0, @NotNull final Source p1) throws JAXBException;
    
    @NotNull
    public final T unmarshal(@NotNull final InputStream in) throws JAXBException {
        final Unmarshaller u = this.context.unmarshallerPool.take();
        return this.exit(this.unmarshal(u, in), u);
    }
    
    @NotNull
    public final T unmarshal(@NotNull final BridgeContext context, @NotNull final InputStream in) throws JAXBException {
        return this.unmarshal(((BridgeContextImpl)context).unmarshaller, in);
    }
    
    @NotNull
    public abstract T unmarshal(@NotNull final Unmarshaller p0, @NotNull final InputStream p1) throws JAXBException;
    
    @NotNull
    public final T unmarshal(@NotNull final Node n) throws JAXBException {
        return this.unmarshal(n, null);
    }
    
    @NotNull
    public final T unmarshal(@NotNull final Node n, @Nullable final AttachmentUnmarshaller au) throws JAXBException {
        final Unmarshaller u = this.context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return this.exit(this.unmarshal(u, n), u);
    }
    
    @NotNull
    public final T unmarshal(@NotNull final BridgeContext context, @NotNull final Node n) throws JAXBException {
        return this.unmarshal(((BridgeContextImpl)context).unmarshaller, n);
    }
    
    @NotNull
    public abstract T unmarshal(@NotNull final Unmarshaller p0, @NotNull final Node p1) throws JAXBException;
    
    public abstract TypeInfo getTypeReference();
}
