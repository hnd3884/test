package org.apache.xml.security.signature;

import org.apache.commons.logging.LogFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayOutputStream;
import org.xml.sax.ErrorHandler;
import org.apache.xml.security.utils.IgnoreAllErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.security.exceptions.XMLSecurityRuntimeException;
import java.io.ByteArrayInputStream;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.utils.JavaUtils;
import java.util.HashSet;
import org.apache.xml.security.utils.XMLUtils;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.io.OutputStream;
import java.util.List;
import org.w3c.dom.Node;
import java.util.Set;
import java.io.InputStream;
import org.apache.commons.logging.Log;

public class XMLSignatureInput implements Cloneable
{
    static Log log;
    InputStream _inputOctetStreamProxy;
    Set _inputNodeSet;
    Node _subNode;
    Node excludeNode;
    boolean excludeComments;
    boolean isNodeSet;
    byte[] bytes;
    private String _MIMEType;
    private String _SourceURI;
    List nodeFilters;
    boolean needsToBeExpanded;
    OutputStream outputStream;
    
    public boolean isNeedsToBeExpanded() {
        return this.needsToBeExpanded;
    }
    
    public void setNeedsToBeExpanded(final boolean needsToBeExpanded) {
        this.needsToBeExpanded = needsToBeExpanded;
    }
    
    public XMLSignatureInput(final byte[] bytes) {
        this._inputOctetStreamProxy = null;
        this._inputNodeSet = null;
        this._subNode = null;
        this.excludeNode = null;
        this.excludeComments = false;
        this.isNodeSet = false;
        this.bytes = null;
        this._MIMEType = null;
        this._SourceURI = null;
        this.nodeFilters = new ArrayList();
        this.needsToBeExpanded = false;
        this.outputStream = null;
        this.bytes = bytes;
    }
    
    public XMLSignatureInput(final InputStream inputOctetStreamProxy) {
        this._inputOctetStreamProxy = null;
        this._inputNodeSet = null;
        this._subNode = null;
        this.excludeNode = null;
        this.excludeComments = false;
        this.isNodeSet = false;
        this.bytes = null;
        this._MIMEType = null;
        this._SourceURI = null;
        this.nodeFilters = new ArrayList();
        this.needsToBeExpanded = false;
        this.outputStream = null;
        this._inputOctetStreamProxy = inputOctetStreamProxy;
    }
    
    public XMLSignatureInput(final String s) {
        this(s.getBytes());
    }
    
    public XMLSignatureInput(final String s, final String s2) throws UnsupportedEncodingException {
        this(s.getBytes(s2));
    }
    
    public XMLSignatureInput(final Node subNode) {
        this._inputOctetStreamProxy = null;
        this._inputNodeSet = null;
        this._subNode = null;
        this.excludeNode = null;
        this.excludeComments = false;
        this.isNodeSet = false;
        this.bytes = null;
        this._MIMEType = null;
        this._SourceURI = null;
        this.nodeFilters = new ArrayList();
        this.needsToBeExpanded = false;
        this.outputStream = null;
        this._subNode = subNode;
    }
    
    public XMLSignatureInput(final Set inputNodeSet) {
        this._inputOctetStreamProxy = null;
        this._inputNodeSet = null;
        this._subNode = null;
        this.excludeNode = null;
        this.excludeComments = false;
        this.isNodeSet = false;
        this.bytes = null;
        this._MIMEType = null;
        this._SourceURI = null;
        this.nodeFilters = new ArrayList();
        this.needsToBeExpanded = false;
        this.outputStream = null;
        this._inputNodeSet = inputNodeSet;
    }
    
    public Set getNodeSet() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
        return this.getNodeSet(false);
    }
    
    public Set getNodeSet(final boolean b) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
        if (this._inputNodeSet != null) {
            return this._inputNodeSet;
        }
        if (this._inputOctetStreamProxy == null && this._subNode != null) {
            if (b) {
                XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this._subNode));
            }
            this._inputNodeSet = new HashSet();
            XMLUtils.getSet(this._subNode, this._inputNodeSet, this.excludeNode, this.excludeComments);
            return this._inputNodeSet;
        }
        if (this.isOctetStream()) {
            this.convertToNodes();
            final HashSet set = new HashSet();
            XMLUtils.getSet(this._subNode, set, null, false);
            return set;
        }
        throw new RuntimeException("getNodeSet() called but no input data present");
    }
    
    public InputStream getOctetStream() throws IOException {
        return this.getResetableInputStream();
    }
    
    public InputStream getOctetStreamReal() {
        return this._inputOctetStreamProxy;
    }
    
    public byte[] getBytes() throws IOException, CanonicalizationException {
        if (this.bytes != null) {
            return this.bytes;
        }
        final InputStream resetableInputStream = this.getResetableInputStream();
        if (resetableInputStream != null) {
            if (this.bytes == null) {
                resetableInputStream.reset();
                this.bytes = JavaUtils.getBytesFromStream(resetableInputStream);
            }
            return this.bytes;
        }
        return this.bytes = new Canonicalizer20010315OmitComments().engineCanonicalize(this);
    }
    
    public boolean isNodeSet() {
        return (this._inputOctetStreamProxy == null && this._inputNodeSet != null) || this.isNodeSet;
    }
    
    public boolean isElement() {
        return this._inputOctetStreamProxy == null && this._subNode != null && this._inputNodeSet == null && !this.isNodeSet;
    }
    
    public boolean isOctetStream() {
        return (this._inputOctetStreamProxy != null || this.bytes != null) && this._inputNodeSet == null && this._subNode == null;
    }
    
    public boolean isByteArray() {
        return this.bytes != null && this._inputNodeSet == null && this._subNode == null;
    }
    
    public boolean isInitialized() {
        return this.isOctetStream() || this.isNodeSet();
    }
    
    public String getMIMEType() {
        return this._MIMEType;
    }
    
    public void setMIMEType(final String mimeType) {
        this._MIMEType = mimeType;
    }
    
    public String getSourceURI() {
        return this._SourceURI;
    }
    
    public void setSourceURI(final String sourceURI) {
        this._SourceURI = sourceURI;
    }
    
    public String toString() {
        if (this.isNodeSet()) {
            return "XMLSignatureInput/NodeSet/" + this._inputNodeSet.size() + " nodes/" + this.getSourceURI();
        }
        if (this.isElement()) {
            return "XMLSignatureInput/Element/" + this._subNode + " exclude " + this.excludeNode + " comments:" + this.excludeComments + "/" + this.getSourceURI();
        }
        try {
            return "XMLSignatureInput/OctetStream/" + this.getBytes().length + " octets/" + this.getSourceURI();
        }
        catch (final IOException ex) {
            return "XMLSignatureInput/OctetStream//" + this.getSourceURI();
        }
        catch (final CanonicalizationException ex2) {
            return "XMLSignatureInput/OctetStream//" + this.getSourceURI();
        }
    }
    
    public String getHTMLRepresentation() throws XMLSignatureException {
        return new XMLSignatureInputDebugger(this).getHTMLRepresentation();
    }
    
    public String getHTMLRepresentation(final Set set) throws XMLSignatureException {
        return new XMLSignatureInputDebugger(this, set).getHTMLRepresentation();
    }
    
    public Node getExcludeNode() {
        return this.excludeNode;
    }
    
    public void setExcludeNode(final Node excludeNode) {
        this.excludeNode = excludeNode;
    }
    
    public Node getSubNode() {
        return this._subNode;
    }
    
    public boolean isExcludeComments() {
        return this.excludeComments;
    }
    
    public void setExcludeComments(final boolean excludeComments) {
        this.excludeComments = excludeComments;
    }
    
    public void updateOutputStream(final OutputStream writer) throws CanonicalizationException, IOException {
        if (writer == this.outputStream) {
            return;
        }
        if (this.bytes != null) {
            writer.write(this.bytes);
            return;
        }
        if (this._inputOctetStreamProxy == null) {
            final Canonicalizer20010315OmitComments canonicalizer20010315OmitComments = new Canonicalizer20010315OmitComments();
            canonicalizer20010315OmitComments.setWriter(writer);
            canonicalizer20010315OmitComments.engineCanonicalize(this);
            return;
        }
        final InputStream resetableInputStream = this.getResetableInputStream();
        if (this.bytes != null) {
            writer.write(this.bytes, 0, this.bytes.length);
            return;
        }
        resetableInputStream.reset();
        final byte[] array = new byte[1024];
        int read;
        while ((read = resetableInputStream.read(array)) > 0) {
            writer.write(array, 0, read);
        }
    }
    
    public void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    protected InputStream getResetableInputStream() throws IOException {
        if (this._inputOctetStreamProxy instanceof ByteArrayInputStream) {
            if (!this._inputOctetStreamProxy.markSupported()) {
                throw new RuntimeException("Accepted as Markable but not truly been" + this._inputOctetStreamProxy);
            }
            return this._inputOctetStreamProxy;
        }
        else {
            if (this.bytes != null) {
                return this._inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
            }
            if (this._inputOctetStreamProxy == null) {
                return null;
            }
            if (this._inputOctetStreamProxy.markSupported()) {
                XMLSignatureInput.log.info((Object)"Mark Suported but not used as reset");
            }
            this.bytes = JavaUtils.getBytesFromStream(this._inputOctetStreamProxy);
            this._inputOctetStreamProxy.close();
            return this._inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
        }
    }
    
    public void addNodeFilter(final NodeFilter nodeFilter) {
        if (this.isOctetStream()) {
            try {
                this.convertToNodes();
            }
            catch (final Exception ex) {
                throw new XMLSecurityRuntimeException("signature.XMLSignatureInput.nodesetReference", ex);
            }
        }
        this.nodeFilters.add(nodeFilter);
    }
    
    public List getNodeFilters() {
        return this.nodeFilters;
    }
    
    public void setNodeSet(final boolean isNodeSet) {
        this.isNodeSet = isNodeSet;
    }
    
    void convertToNodes() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        instance.setValidating(false);
        instance.setNamespaceAware(true);
        final DocumentBuilder documentBuilder = instance.newDocumentBuilder();
        try {
            documentBuilder.setErrorHandler(new IgnoreAllErrorHandler());
            this._subNode = documentBuilder.parse(this.getOctetStream()).getDocumentElement();
        }
        catch (final SAXException ex) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write("<container>".getBytes());
            byteArrayOutputStream.write(this.getBytes());
            byteArrayOutputStream.write("</container>".getBytes());
            this._subNode = documentBuilder.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).getDocumentElement().getFirstChild().getFirstChild();
        }
        this._inputOctetStreamProxy = null;
        this.bytes = null;
    }
    
    static {
        XMLSignatureInput.log = LogFactory.getLog(XMLSignatureInput.class.getName());
    }
}
