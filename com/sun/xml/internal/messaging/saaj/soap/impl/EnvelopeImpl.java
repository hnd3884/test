package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import javax.xml.transform.Transformer;
import java.io.IOException;
import javax.xml.transform.Result;
import java.io.Writer;
import java.io.OutputStreamWriter;
import javax.xml.transform.stream.StreamResult;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.OutputStream;
import java.util.logging.Level;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import javax.xml.soap.SOAPBody;
import java.util.Iterator;
import org.w3c.dom.Node;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.Envelope;

public abstract class EnvelopeImpl extends ElementImpl implements Envelope
{
    protected HeaderImpl header;
    protected BodyImpl body;
    String omitXmlDecl;
    String charset;
    String xmlDecl;
    
    protected EnvelopeImpl(final SOAPDocumentImpl ownerDoc, final Name name) {
        super(ownerDoc, name);
        this.omitXmlDecl = "yes";
        this.charset = "utf-8";
        this.xmlDecl = null;
    }
    
    protected EnvelopeImpl(final SOAPDocumentImpl ownerDoc, final QName name) {
        super(ownerDoc, name);
        this.omitXmlDecl = "yes";
        this.charset = "utf-8";
        this.xmlDecl = null;
    }
    
    protected EnvelopeImpl(final SOAPDocumentImpl ownerDoc, final NameImpl name, final boolean createHeader, final boolean createBody) throws SOAPException {
        this(ownerDoc, name);
        this.ensureNamespaceIsDeclared(this.getElementQName().getPrefix(), this.getElementQName().getNamespaceURI());
        if (createHeader) {
            this.addHeader();
        }
        if (createBody) {
            this.addBody();
        }
    }
    
    protected abstract NameImpl getHeaderName(final String p0);
    
    protected abstract NameImpl getBodyName(final String p0);
    
    @Override
    public SOAPHeader addHeader() throws SOAPException {
        return this.addHeader(null);
    }
    
    public SOAPHeader addHeader(String prefix) throws SOAPException {
        if (prefix == null || prefix.equals("")) {
            prefix = this.getPrefix();
        }
        final NameImpl headerName = this.getHeaderName(prefix);
        final NameImpl bodyName = this.getBodyName(prefix);
        HeaderImpl header = null;
        SOAPElement firstChild = null;
        final Iterator eachChild = this.getChildElementNodes();
        if (eachChild.hasNext()) {
            firstChild = eachChild.next();
            if (firstChild.getElementName().equals(headerName)) {
                EnvelopeImpl.log.severe("SAAJ0120.impl.header.already.exists");
                throw new SOAPExceptionImpl("Can't add a header when one is already present.");
            }
            if (!firstChild.getElementName().equals(bodyName)) {
                EnvelopeImpl.log.severe("SAAJ0121.impl.invalid.first.child.of.envelope");
                throw new SOAPExceptionImpl("First child of Envelope must be either a Header or Body");
            }
        }
        header = (HeaderImpl)this.createElement(headerName);
        this.insertBefore(header, firstChild);
        header.ensureNamespaceIsDeclared(headerName.getPrefix(), headerName.getURI());
        return header;
    }
    
    protected void lookForHeader() throws SOAPException {
        final NameImpl headerName = this.getHeaderName(null);
        final HeaderImpl hdr = (HeaderImpl)this.findChild(headerName);
        this.header = hdr;
    }
    
    @Override
    public SOAPHeader getHeader() throws SOAPException {
        this.lookForHeader();
        return this.header;
    }
    
    protected void lookForBody() throws SOAPException {
        final NameImpl bodyName = this.getBodyName(null);
        final BodyImpl bodyChildElement = (BodyImpl)this.findChild(bodyName);
        this.body = bodyChildElement;
    }
    
    @Override
    public SOAPBody addBody() throws SOAPException {
        return this.addBody(null);
    }
    
    public SOAPBody addBody(String prefix) throws SOAPException {
        this.lookForBody();
        if (prefix == null || prefix.equals("")) {
            prefix = this.getPrefix();
        }
        if (this.body == null) {
            final NameImpl bodyName = this.getBodyName(prefix);
            this.insertBefore(this.body = (BodyImpl)this.createElement(bodyName), null);
            this.body.ensureNamespaceIsDeclared(bodyName.getPrefix(), bodyName.getURI());
            return this.body;
        }
        EnvelopeImpl.log.severe("SAAJ0122.impl.body.already.exists");
        throw new SOAPExceptionImpl("Can't add a body when one is already present.");
    }
    
    @Override
    protected SOAPElement addElement(final Name name) throws SOAPException {
        if (this.getBodyName(null).equals(name)) {
            return this.addBody(name.getPrefix());
        }
        if (this.getHeaderName(null).equals(name)) {
            return this.addHeader(name.getPrefix());
        }
        return super.addElement(name);
    }
    
    @Override
    protected SOAPElement addElement(final QName name) throws SOAPException {
        if (this.getBodyName(null).equals(NameImpl.convertToName(name))) {
            return this.addBody(name.getPrefix());
        }
        if (this.getHeaderName(null).equals(NameImpl.convertToName(name))) {
            return this.addHeader(name.getPrefix());
        }
        return super.addElement(name);
    }
    
    @Override
    public SOAPBody getBody() throws SOAPException {
        this.lookForBody();
        return this.body;
    }
    
    @Override
    public Source getContent() {
        return new DOMSource(this.getOwnerDocument());
    }
    
    @Override
    public Name createName(final String localName, final String prefix, final String uri) throws SOAPException {
        if ("xmlns".equals(prefix)) {
            EnvelopeImpl.log.severe("SAAJ0123.impl.no.reserved.xmlns");
            throw new SOAPExceptionImpl("Cannot declare reserved xmlns prefix");
        }
        if (prefix == null && "xmlns".equals(localName)) {
            EnvelopeImpl.log.severe("SAAJ0124.impl.qualified.name.cannot.be.xmlns");
            throw new SOAPExceptionImpl("Qualified name cannot be xmlns");
        }
        return NameImpl.create(localName, prefix, uri);
    }
    
    public Name createName(final String localName, final String prefix) throws SOAPException {
        final String namespace = this.getNamespaceURI(prefix);
        if (namespace == null) {
            EnvelopeImpl.log.log(Level.SEVERE, "SAAJ0126.impl.cannot.locate.ns", new String[] { prefix });
            throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + prefix);
        }
        return NameImpl.create(localName, prefix, namespace);
    }
    
    @Override
    public Name createName(final String localName) throws SOAPException {
        return NameImpl.createFromUnqualifiedName(localName);
    }
    
    public void setOmitXmlDecl(final String value) {
        this.omitXmlDecl = value;
    }
    
    public void setXmlDecl(final String value) {
        this.xmlDecl = value;
    }
    
    private String getOmitXmlDecl() {
        return this.omitXmlDecl;
    }
    
    public void setCharsetEncoding(final String value) {
        this.charset = value;
    }
    
    @Override
    public void output(final OutputStream out) throws IOException {
        try {
            final Transformer transformer = EfficientStreamingTransformer.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.setOutputProperty("encoding", this.charset);
            if (this.omitXmlDecl.equals("no") && this.xmlDecl == null) {
                this.xmlDecl = "<?xml version=\"" + this.getOwnerDocument().getXmlVersion() + "\" encoding=\"" + this.charset + "\" ?>";
            }
            StreamResult result = new StreamResult(out);
            if (this.xmlDecl != null) {
                final OutputStreamWriter writer = new OutputStreamWriter(out, this.charset);
                writer.write(this.xmlDecl);
                writer.flush();
                result = new StreamResult(writer);
            }
            if (EnvelopeImpl.log.isLoggable(Level.FINE)) {
                EnvelopeImpl.log.log(Level.FINE, "SAAJ0190.impl.set.xml.declaration", new String[] { this.omitXmlDecl });
                EnvelopeImpl.log.log(Level.FINE, "SAAJ0191.impl.set.encoding", new String[] { this.charset });
            }
            transformer.transform(this.getContent(), result);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    @Override
    public void output(final OutputStream out, final boolean isFastInfoset) throws IOException {
        if (!isFastInfoset) {
            this.output(out);
        }
        else {
            try {
                final Source source = this.getContent();
                final Transformer transformer = EfficientStreamingTransformer.newTransformer();
                transformer.transform(this.getContent(), FastInfosetReflection.FastInfosetResult_new(out));
            }
            catch (final Exception ex) {
                throw new IOException(ex.getMessage());
            }
        }
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        EnvelopeImpl.log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), newName.getLocalPart() });
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }
}
