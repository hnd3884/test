package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.bind.UnmarshalException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.api.TypeReference;
import java.io.InputStream;
import javax.xml.transform.Source;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.MarshalException;
import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class BridgeAdapter<OnWire, InMemory> extends InternalBridge<InMemory>
{
    private final InternalBridge<OnWire> core;
    private final Class<? extends XmlAdapter<OnWire, InMemory>> adapter;
    
    public BridgeAdapter(final InternalBridge<OnWire> core, final Class<? extends XmlAdapter<OnWire, InMemory>> adapter) {
        super(core.getContext());
        this.core = core;
        this.adapter = adapter;
    }
    
    @Override
    public void marshal(final Marshaller m, final InMemory inMemory, final XMLStreamWriter output) throws JAXBException {
        this.core.marshal(m, this.adaptM(m, inMemory), output);
    }
    
    @Override
    public void marshal(final Marshaller m, final InMemory inMemory, final OutputStream output, final NamespaceContext nsc) throws JAXBException {
        this.core.marshal(m, this.adaptM(m, inMemory), output, nsc);
    }
    
    @Override
    public void marshal(final Marshaller m, final InMemory inMemory, final Node output) throws JAXBException {
        this.core.marshal(m, this.adaptM(m, inMemory), output);
    }
    
    @Override
    public void marshal(final Marshaller context, final InMemory inMemory, final ContentHandler contentHandler) throws JAXBException {
        this.core.marshal(context, this.adaptM(context, inMemory), contentHandler);
    }
    
    @Override
    public void marshal(final Marshaller context, final InMemory inMemory, final Result result) throws JAXBException {
        this.core.marshal(context, this.adaptM(context, inMemory), result);
    }
    
    private OnWire adaptM(final Marshaller m, final InMemory v) throws JAXBException {
        final XMLSerializer serializer = ((MarshallerImpl)m).serializer;
        serializer.pushCoordinator();
        try {
            return this._adaptM(serializer, v);
        }
        finally {
            serializer.popCoordinator();
        }
    }
    
    private OnWire _adaptM(final XMLSerializer serializer, final InMemory v) throws MarshalException {
        final XmlAdapter<OnWire, InMemory> a = serializer.getAdapter(this.adapter);
        try {
            return a.marshal(v);
        }
        catch (final Exception e) {
            serializer.handleError(e, v, null);
            throw new MarshalException(e);
        }
    }
    
    @NotNull
    @Override
    public InMemory unmarshal(final Unmarshaller u, final XMLStreamReader in) throws JAXBException {
        return this.adaptU(u, this.core.unmarshal(u, in));
    }
    
    @NotNull
    @Override
    public InMemory unmarshal(final Unmarshaller u, final Source in) throws JAXBException {
        return this.adaptU(u, this.core.unmarshal(u, in));
    }
    
    @NotNull
    @Override
    public InMemory unmarshal(final Unmarshaller u, final InputStream in) throws JAXBException {
        return this.adaptU(u, this.core.unmarshal(u, in));
    }
    
    @NotNull
    @Override
    public InMemory unmarshal(final Unmarshaller u, final Node n) throws JAXBException {
        return this.adaptU(u, this.core.unmarshal(u, n));
    }
    
    @Override
    public TypeReference getTypeReference() {
        return this.core.getTypeReference();
    }
    
    @NotNull
    private InMemory adaptU(final Unmarshaller _u, final OnWire v) throws JAXBException {
        final UnmarshallerImpl u = (UnmarshallerImpl)_u;
        final XmlAdapter<OnWire, InMemory> a = u.coordinator.getAdapter(this.adapter);
        u.coordinator.pushCoordinator();
        try {
            return a.unmarshal(v);
        }
        catch (final Exception e) {
            throw new UnmarshalException(e);
        }
        finally {
            u.coordinator.popCoordinator();
        }
    }
    
    @Override
    void marshal(final InMemory o, final XMLSerializer out) throws IOException, SAXException, XMLStreamException {
        try {
            this.core.marshal(this._adaptM(XMLSerializer.getInstance(), o), out);
        }
        catch (final MarshalException ex) {}
    }
}
