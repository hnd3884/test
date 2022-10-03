package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Source;
import com.sun.istack.internal.NotNull;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import org.w3c.dom.Node;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.runtime.output.XMLStreamWriterOutput;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.bind.Marshaller;
import com.sun.xml.internal.bind.api.TypeReference;

final class BridgeImpl<T> extends InternalBridge<T>
{
    private final Name tagName;
    private final JaxBeanInfo<T> bi;
    private final TypeReference typeRef;
    
    public BridgeImpl(final JAXBContextImpl context, final Name tagName, final JaxBeanInfo<T> bi, final TypeReference typeRef) {
        super(context);
        this.tagName = tagName;
        this.bi = bi;
        this.typeRef = typeRef;
    }
    
    @Override
    public void marshal(final Marshaller _m, final T t, final XMLStreamWriter output) throws JAXBException {
        final MarshallerImpl m = (MarshallerImpl)_m;
        m.write(this.tagName, this.bi, t, XMLStreamWriterOutput.create(output, this.context, m.getEscapeHandler()), new StAXPostInitAction(output, m.serializer));
    }
    
    @Override
    public void marshal(final Marshaller _m, final T t, final OutputStream output, final NamespaceContext nsContext) throws JAXBException {
        final MarshallerImpl m = (MarshallerImpl)_m;
        Runnable pia = null;
        if (nsContext != null) {
            pia = new StAXPostInitAction(nsContext, m.serializer);
        }
        m.write(this.tagName, this.bi, t, m.createWriter(output), pia);
    }
    
    @Override
    public void marshal(final Marshaller _m, final T t, final Node output) throws JAXBException {
        final MarshallerImpl m = (MarshallerImpl)_m;
        m.write(this.tagName, this.bi, t, new SAXOutput(new SAX2DOMEx(output)), new DomPostInitAction(output, m.serializer));
    }
    
    @Override
    public void marshal(final Marshaller _m, final T t, final ContentHandler contentHandler) throws JAXBException {
        final MarshallerImpl m = (MarshallerImpl)_m;
        m.write(this.tagName, this.bi, t, new SAXOutput(contentHandler), null);
    }
    
    @Override
    public void marshal(final Marshaller _m, final T t, final Result result) throws JAXBException {
        final MarshallerImpl m = (MarshallerImpl)_m;
        m.write(this.tagName, this.bi, t, m.createXmlOutput(result), m.createPostInitAction(result));
    }
    
    @NotNull
    @Override
    public T unmarshal(final Unmarshaller _u, final XMLStreamReader in) throws JAXBException {
        final UnmarshallerImpl u = (UnmarshallerImpl)_u;
        return ((JAXBElement)u.unmarshal0(in, this.bi)).getValue();
    }
    
    @NotNull
    @Override
    public T unmarshal(final Unmarshaller _u, final Source in) throws JAXBException {
        final UnmarshallerImpl u = (UnmarshallerImpl)_u;
        return ((JAXBElement)u.unmarshal0(in, this.bi)).getValue();
    }
    
    @NotNull
    @Override
    public T unmarshal(final Unmarshaller _u, final InputStream in) throws JAXBException {
        final UnmarshallerImpl u = (UnmarshallerImpl)_u;
        return ((JAXBElement)u.unmarshal0(in, this.bi)).getValue();
    }
    
    @NotNull
    @Override
    public T unmarshal(final Unmarshaller _u, final Node n) throws JAXBException {
        final UnmarshallerImpl u = (UnmarshallerImpl)_u;
        return ((JAXBElement)u.unmarshal0(n, this.bi)).getValue();
    }
    
    @Override
    public TypeReference getTypeReference() {
        return this.typeRef;
    }
    
    public void marshal(final T value, final XMLSerializer out) throws IOException, SAXException, XMLStreamException {
        out.startElement(this.tagName, null);
        if (value == null) {
            out.writeXsiNilTrue();
        }
        else {
            out.childAsXsiType(value, null, this.bi, false);
        }
        out.endElement();
    }
}
