package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import javax.xml.transform.Source;
import com.sun.istack.internal.Nullable;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamWriter;
import com.sun.istack.internal.NotNull;

public interface XMLBridge<T>
{
    @NotNull
    BindingContext context();
    
    void marshal(final T p0, final XMLStreamWriter p1, final AttachmentMarshaller p2) throws JAXBException;
    
    void marshal(final T p0, final OutputStream p1, final NamespaceContext p2, final AttachmentMarshaller p3) throws JAXBException;
    
    void marshal(final T p0, final Node p1) throws JAXBException;
    
    void marshal(final T p0, final ContentHandler p1, final AttachmentMarshaller p2) throws JAXBException;
    
    void marshal(final T p0, final Result p1) throws JAXBException;
    
    @NotNull
    T unmarshal(@NotNull final XMLStreamReader p0, @Nullable final AttachmentUnmarshaller p1) throws JAXBException;
    
    @NotNull
    T unmarshal(@NotNull final Source p0, @Nullable final AttachmentUnmarshaller p1) throws JAXBException;
    
    @NotNull
    T unmarshal(@NotNull final InputStream p0) throws JAXBException;
    
    @NotNull
    T unmarshal(@NotNull final Node p0, @Nullable final AttachmentUnmarshaller p1) throws JAXBException;
    
    TypeInfo getTypeInfo();
    
    boolean supportOutputStream();
}
