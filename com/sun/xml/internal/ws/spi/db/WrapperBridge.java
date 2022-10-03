package com.sun.xml.internal.ws.spi.db;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import org.w3c.dom.Node;
import java.util.Iterator;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBException;
import org.xml.sax.Attributes;
import javax.xml.bind.attachment.AttachmentMarshaller;
import org.xml.sax.ContentHandler;

public class WrapperBridge<T> implements XMLBridge<T>
{
    BindingContext parent;
    TypeInfo typeInfo;
    static final String WrapperPrefix = "w";
    static final String WrapperPrefixColon = "w:";
    
    public WrapperBridge(final BindingContext p, final TypeInfo ti) {
        this.parent = p;
        this.typeInfo = ti;
    }
    
    @Override
    public BindingContext context() {
        return this.parent;
    }
    
    @Override
    public TypeInfo getTypeInfo() {
        return this.typeInfo;
    }
    
    @Override
    public final void marshal(final T object, final ContentHandler contentHandler, final AttachmentMarshaller am) throws JAXBException {
        final WrapperComposite w = (WrapperComposite)object;
        final Attributes att = new Attributes() {
            @Override
            public int getLength() {
                return 0;
            }
            
            @Override
            public String getURI(final int index) {
                return null;
            }
            
            @Override
            public String getLocalName(final int index) {
                return null;
            }
            
            @Override
            public String getQName(final int index) {
                return null;
            }
            
            @Override
            public String getType(final int index) {
                return null;
            }
            
            @Override
            public String getValue(final int index) {
                return null;
            }
            
            @Override
            public int getIndex(final String uri, final String localName) {
                return 0;
            }
            
            @Override
            public int getIndex(final String qName) {
                return 0;
            }
            
            @Override
            public String getType(final String uri, final String localName) {
                return null;
            }
            
            @Override
            public String getType(final String qName) {
                return null;
            }
            
            @Override
            public String getValue(final String uri, final String localName) {
                return null;
            }
            
            @Override
            public String getValue(final String qName) {
                return null;
            }
        };
        try {
            contentHandler.startPrefixMapping("w", this.typeInfo.tagName.getNamespaceURI());
            contentHandler.startElement(this.typeInfo.tagName.getNamespaceURI(), this.typeInfo.tagName.getLocalPart(), "w:" + this.typeInfo.tagName.getLocalPart(), att);
        }
        catch (final SAXException e) {
            throw new JAXBException(e);
        }
        if (w.bridges != null) {
            for (int i = 0; i < w.bridges.length; ++i) {
                if (w.bridges[i] instanceof RepeatedElementBridge) {
                    final RepeatedElementBridge rbridge = (RepeatedElementBridge)w.bridges[i];
                    final Iterator itr = rbridge.collectionHandler().iterator(w.values[i]);
                    while (itr.hasNext()) {
                        rbridge.marshal(itr.next(), contentHandler, am);
                    }
                }
                else {
                    w.bridges[i].marshal(w.values[i], contentHandler, am);
                }
            }
        }
        try {
            contentHandler.endElement(this.typeInfo.tagName.getNamespaceURI(), this.typeInfo.tagName.getLocalPart(), null);
            contentHandler.endPrefixMapping("w");
        }
        catch (final SAXException e) {
            throw new JAXBException(e);
        }
    }
    
    @Override
    public void marshal(final T object, final Node output) throws JAXBException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void marshal(final T object, final OutputStream output, final NamespaceContext nsContext, final AttachmentMarshaller am) throws JAXBException {
    }
    
    @Override
    public final void marshal(final T object, final Result result) throws JAXBException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final void marshal(final T object, final XMLStreamWriter output, final AttachmentMarshaller am) throws JAXBException {
        final WrapperComposite w = (WrapperComposite)object;
        try {
            String prefix = output.getPrefix(this.typeInfo.tagName.getNamespaceURI());
            if (prefix == null) {
                prefix = "w";
            }
            output.writeStartElement(prefix, this.typeInfo.tagName.getLocalPart(), this.typeInfo.tagName.getNamespaceURI());
            output.writeNamespace(prefix, this.typeInfo.tagName.getNamespaceURI());
        }
        catch (final XMLStreamException e) {
            e.printStackTrace();
            throw new DatabindingException(e);
        }
        if (w.bridges != null) {
            for (int i = 0; i < w.bridges.length; ++i) {
                if (w.bridges[i] instanceof RepeatedElementBridge) {
                    final RepeatedElementBridge rbridge = (RepeatedElementBridge)w.bridges[i];
                    final Iterator itr = rbridge.collectionHandler().iterator(w.values[i]);
                    while (itr.hasNext()) {
                        rbridge.marshal(itr.next(), output, am);
                    }
                }
                else {
                    w.bridges[i].marshal(w.values[i], output, am);
                }
            }
        }
        try {
            output.writeEndElement();
        }
        catch (final XMLStreamException e) {
            throw new DatabindingException(e);
        }
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
        return false;
    }
}
