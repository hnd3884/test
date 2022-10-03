package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.bind.Marshaller;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;

public class MarshallerBridge extends Bridge implements XMLBridge
{
    protected MarshallerBridge(final JAXBContextImpl context) {
        super(context);
    }
    
    @Override
    public void marshal(final Marshaller m, final Object object, final XMLStreamWriter output) throws JAXBException {
        m.setProperty("jaxb.fragment", true);
        try {
            m.marshal(object, output);
        }
        finally {
            m.setProperty("jaxb.fragment", false);
        }
    }
    
    @Override
    public void marshal(final Marshaller m, final Object object, final OutputStream output, final NamespaceContext nsContext) throws JAXBException {
        m.setProperty("jaxb.fragment", true);
        try {
            ((MarshallerImpl)m).marshal(object, output, nsContext);
        }
        finally {
            m.setProperty("jaxb.fragment", false);
        }
    }
    
    @Override
    public void marshal(final Marshaller m, final Object object, final Node output) throws JAXBException {
        m.setProperty("jaxb.fragment", true);
        try {
            m.marshal(object, output);
        }
        finally {
            m.setProperty("jaxb.fragment", false);
        }
    }
    
    @Override
    public void marshal(final Marshaller m, final Object object, final ContentHandler contentHandler) throws JAXBException {
        m.setProperty("jaxb.fragment", true);
        try {
            m.marshal(object, contentHandler);
        }
        finally {
            m.setProperty("jaxb.fragment", false);
        }
    }
    
    @Override
    public void marshal(final Marshaller m, final Object object, final Result result) throws JAXBException {
        m.setProperty("jaxb.fragment", true);
        try {
            m.marshal(object, result);
        }
        finally {
            m.setProperty("jaxb.fragment", false);
        }
    }
    
    @Override
    public Object unmarshal(final Unmarshaller u, final XMLStreamReader in) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object unmarshal(final Unmarshaller u, final Source in) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object unmarshal(final Unmarshaller u, final InputStream in) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object unmarshal(final Unmarshaller u, final Node n) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public TypeInfo getTypeInfo() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public TypeReference getTypeReference() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public BindingContext context() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean supportOutputStream() {
        return true;
    }
}
