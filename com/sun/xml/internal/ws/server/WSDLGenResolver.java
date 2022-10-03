package com.sun.xml.internal.ws.server;

import javax.xml.ws.Holder;
import java.net.MalformedURLException;
import javax.xml.ws.WebServiceException;
import java.net.URL;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import javax.xml.transform.Result;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.server.SDDocument;
import java.util.HashMap;
import java.util.ArrayList;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.util.List;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;

final class WSDLGenResolver implements WSDLResolver
{
    private final List<SDDocumentImpl> docs;
    private final List<SDDocumentSource> newDocs;
    private SDDocumentSource concreteWsdlSource;
    private SDDocumentImpl abstractWsdl;
    private SDDocumentImpl concreteWsdl;
    private final Map<String, List<SDDocumentImpl>> nsMapping;
    private final QName serviceName;
    private final QName portTypeName;
    
    public WSDLGenResolver(@NotNull final List<SDDocumentImpl> docs, final QName serviceName, final QName portTypeName) {
        this.newDocs = new ArrayList<SDDocumentSource>();
        this.nsMapping = new HashMap<String, List<SDDocumentImpl>>();
        this.docs = docs;
        this.serviceName = serviceName;
        this.portTypeName = portTypeName;
        for (final SDDocumentImpl doc : docs) {
            if (doc.isWSDL()) {
                final SDDocument.WSDL wsdl = (SDDocument.WSDL)doc;
                if (wsdl.hasPortType()) {
                    this.abstractWsdl = doc;
                }
            }
            if (doc.isSchema()) {
                final SDDocument.Schema schema = (SDDocument.Schema)doc;
                List<SDDocumentImpl> sysIds = this.nsMapping.get(schema.getTargetNamespace());
                if (sysIds == null) {
                    sysIds = new ArrayList<SDDocumentImpl>();
                    this.nsMapping.put(schema.getTargetNamespace(), sysIds);
                }
                sysIds.add(doc);
            }
        }
    }
    
    @Override
    public Result getWSDL(final String filename) {
        final URL url = this.createURL(filename);
        final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
        xsb.setSystemId(url.toExternalForm());
        this.concreteWsdlSource = SDDocumentSource.create(url, xsb);
        this.newDocs.add(this.concreteWsdlSource);
        final XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
        r.setSystemId(filename);
        return r;
    }
    
    private URL createURL(final String filename) {
        try {
            return new URL("file:///" + filename);
        }
        catch (final MalformedURLException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public Result getAbstractWSDL(final Holder<String> filename) {
        if (this.abstractWsdl != null) {
            filename.value = this.abstractWsdl.getURL().toString();
            return null;
        }
        final URL url = this.createURL(filename.value);
        final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
        xsb.setSystemId(url.toExternalForm());
        final SDDocumentSource abstractWsdlSource = SDDocumentSource.create(url, xsb);
        this.newDocs.add(abstractWsdlSource);
        final XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
        r.setSystemId(filename.value);
        return r;
    }
    
    @Override
    public Result getSchemaOutput(final String namespace, final Holder<String> filename) {
        final List<SDDocumentImpl> schemas = this.nsMapping.get(namespace);
        if (schemas == null) {
            final URL url = this.createURL(filename.value);
            final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            xsb.setSystemId(url.toExternalForm());
            final SDDocumentSource sd = SDDocumentSource.create(url, xsb);
            this.newDocs.add(sd);
            final XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
            r.setSystemId(filename.value);
            return r;
        }
        if (schemas.size() > 1) {
            throw new ServerRtException("server.rt.err", new Object[] { "More than one schema for the target namespace " + namespace });
        }
        filename.value = schemas.get(0).getURL().toExternalForm();
        return null;
    }
    
    public SDDocumentImpl updateDocs() {
        for (final SDDocumentSource doc : this.newDocs) {
            final SDDocumentImpl docImpl = SDDocumentImpl.create(doc, this.serviceName, this.portTypeName);
            if (doc == this.concreteWsdlSource) {
                this.concreteWsdl = docImpl;
            }
            this.docs.add(docImpl);
        }
        return this.concreteWsdl;
    }
}
