package com.sun.org.apache.xml.internal.security.signature;

import javax.xml.parsers.DocumentBuilder;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import java.io.ByteArrayInputStream;
import java.util.LinkedHashSet;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.util.ArrayList;
import java.io.OutputStream;
import java.util.List;
import org.w3c.dom.Node;
import java.util.Set;
import java.io.InputStream;

public class XMLSignatureInput
{
    private InputStream inputOctetStreamProxy;
    private Set<Node> inputNodeSet;
    private Node subNode;
    private Node excludeNode;
    private boolean excludeComments;
    private boolean isNodeSet;
    private byte[] bytes;
    private boolean secureValidation;
    private String mimeType;
    private String sourceURI;
    private List<NodeFilter> nodeFilters;
    private boolean needsToBeExpanded;
    private OutputStream outputStream;
    private String preCalculatedDigest;
    
    public XMLSignatureInput(final byte[] bytes) {
        this.excludeComments = false;
        this.isNodeSet = false;
        this.nodeFilters = new ArrayList<NodeFilter>();
        this.needsToBeExpanded = false;
        this.bytes = bytes;
    }
    
    public XMLSignatureInput(final InputStream inputOctetStreamProxy) {
        this.excludeComments = false;
        this.isNodeSet = false;
        this.nodeFilters = new ArrayList<NodeFilter>();
        this.needsToBeExpanded = false;
        this.inputOctetStreamProxy = inputOctetStreamProxy;
    }
    
    public XMLSignatureInput(final Node subNode) {
        this.excludeComments = false;
        this.isNodeSet = false;
        this.nodeFilters = new ArrayList<NodeFilter>();
        this.needsToBeExpanded = false;
        this.subNode = subNode;
    }
    
    public XMLSignatureInput(final Set<Node> inputNodeSet) {
        this.excludeComments = false;
        this.isNodeSet = false;
        this.nodeFilters = new ArrayList<NodeFilter>();
        this.needsToBeExpanded = false;
        this.inputNodeSet = inputNodeSet;
    }
    
    public XMLSignatureInput(final String preCalculatedDigest) {
        this.excludeComments = false;
        this.isNodeSet = false;
        this.nodeFilters = new ArrayList<NodeFilter>();
        this.needsToBeExpanded = false;
        this.preCalculatedDigest = preCalculatedDigest;
    }
    
    public boolean isNeedsToBeExpanded() {
        return this.needsToBeExpanded;
    }
    
    public void setNeedsToBeExpanded(final boolean needsToBeExpanded) {
        this.needsToBeExpanded = needsToBeExpanded;
    }
    
    public Set<Node> getNodeSet() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
        return this.getNodeSet(false);
    }
    
    public Set<Node> getInputNodeSet() {
        return this.inputNodeSet;
    }
    
    public Set<Node> getNodeSet(final boolean b) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
        if (this.inputNodeSet != null) {
            return this.inputNodeSet;
        }
        if (this.inputOctetStreamProxy == null && this.subNode != null) {
            if (b) {
                XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.subNode));
            }
            this.inputNodeSet = new LinkedHashSet<Node>();
            XMLUtils.getSet(this.subNode, this.inputNodeSet, this.excludeNode, this.excludeComments);
            return this.inputNodeSet;
        }
        if (this.isOctetStream()) {
            this.convertToNodes();
            final LinkedHashSet set = new LinkedHashSet();
            XMLUtils.getSet(this.subNode, set, null, false);
            return set;
        }
        throw new RuntimeException("getNodeSet() called but no input data present");
    }
    
    public InputStream getOctetStream() throws IOException {
        if (this.inputOctetStreamProxy != null) {
            return this.inputOctetStreamProxy;
        }
        if (this.bytes != null) {
            return this.inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
        }
        return null;
    }
    
    public InputStream getOctetStreamReal() {
        return this.inputOctetStreamProxy;
    }
    
    public byte[] getBytes() throws IOException, CanonicalizationException {
        final byte[] bytesFromInputStream = this.getBytesFromInputStream();
        if (bytesFromInputStream != null) {
            return bytesFromInputStream;
        }
        return this.bytes = new Canonicalizer20010315OmitComments().engineCanonicalize(this);
    }
    
    public boolean isNodeSet() {
        return (this.inputOctetStreamProxy == null && this.inputNodeSet != null) || this.isNodeSet;
    }
    
    public boolean isElement() {
        return this.inputOctetStreamProxy == null && this.subNode != null && this.inputNodeSet == null && !this.isNodeSet;
    }
    
    public boolean isOctetStream() {
        return (this.inputOctetStreamProxy != null || this.bytes != null) && this.inputNodeSet == null && this.subNode == null;
    }
    
    public boolean isOutputStreamSet() {
        return this.outputStream != null;
    }
    
    public boolean isByteArray() {
        return this.bytes != null && this.inputNodeSet == null && this.subNode == null;
    }
    
    public boolean isPreCalculatedDigest() {
        return this.preCalculatedDigest != null;
    }
    
    public boolean isInitialized() {
        return this.isOctetStream() || this.isNodeSet();
    }
    
    public String getMIMEType() {
        return this.mimeType;
    }
    
    public void setMIMEType(final String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getSourceURI() {
        return this.sourceURI;
    }
    
    public void setSourceURI(final String sourceURI) {
        this.sourceURI = sourceURI;
    }
    
    @Override
    public String toString() {
        if (this.isNodeSet()) {
            return "XMLSignatureInput/NodeSet/" + this.inputNodeSet.size() + " nodes/" + this.getSourceURI();
        }
        if (this.isElement()) {
            return "XMLSignatureInput/Element/" + this.subNode + " exclude " + this.excludeNode + " comments:" + this.excludeComments + "/" + this.getSourceURI();
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
    
    public String getHTMLRepresentation(final Set<String> set) throws XMLSignatureException {
        return new XMLSignatureInputDebugger(this, set).getHTMLRepresentation();
    }
    
    public Node getExcludeNode() {
        return this.excludeNode;
    }
    
    public void setExcludeNode(final Node excludeNode) {
        this.excludeNode = excludeNode;
    }
    
    public Node getSubNode() {
        return this.subNode;
    }
    
    public boolean isExcludeComments() {
        return this.excludeComments;
    }
    
    public void setExcludeComments(final boolean excludeComments) {
        this.excludeComments = excludeComments;
    }
    
    public void updateOutputStream(final OutputStream outputStream) throws CanonicalizationException, IOException {
        this.updateOutputStream(outputStream, false);
    }
    
    public void updateOutputStream(final OutputStream writer, final boolean b) throws CanonicalizationException, IOException {
        if (writer == this.outputStream) {
            return;
        }
        if (this.bytes != null) {
            writer.write(this.bytes);
        }
        else if (this.inputOctetStreamProxy == null) {
            Canonicalizer20010315 canonicalizer20010315;
            if (b) {
                canonicalizer20010315 = new Canonicalizer11_OmitComments();
            }
            else {
                canonicalizer20010315 = new Canonicalizer20010315OmitComments();
            }
            canonicalizer20010315.setWriter(writer);
            canonicalizer20010315.engineCanonicalize(this);
        }
        else {
            final byte[] array = new byte[4096];
            try {
                int read;
                while ((read = this.inputOctetStreamProxy.read(array)) != -1) {
                    writer.write(array, 0, read);
                }
            }
            catch (final IOException ex) {
                this.inputOctetStreamProxy.close();
                throw ex;
            }
        }
    }
    
    public void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    private byte[] getBytesFromInputStream() throws IOException {
        if (this.bytes != null) {
            return this.bytes;
        }
        if (this.inputOctetStreamProxy == null) {
            return null;
        }
        try {
            this.bytes = JavaUtils.getBytesFromStream(this.inputOctetStreamProxy);
        }
        finally {
            this.inputOctetStreamProxy.close();
        }
        return this.bytes;
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
    
    public List<NodeFilter> getNodeFilters() {
        return this.nodeFilters;
    }
    
    public void setNodeSet(final boolean isNodeSet) {
        this.isNodeSet = isNodeSet;
    }
    
    void convertToNodes() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
        final DocumentBuilder documentBuilder = XMLUtils.createDocumentBuilder(false, this.secureValidation);
        try {
            documentBuilder.setErrorHandler(new IgnoreAllErrorHandler());
            this.subNode = documentBuilder.parse(this.getOctetStream());
        }
        catch (final SAXException ex) {
            byte[] byteArray = null;
            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                byteArrayOutputStream.write("<container>".getBytes(StandardCharsets.UTF_8));
                byteArrayOutputStream.write(this.getBytes());
                byteArrayOutputStream.write("</container>".getBytes(StandardCharsets.UTF_8));
                byteArray = byteArrayOutputStream.toByteArray();
            }
            try (final ByteArrayInputStream is = new ByteArrayInputStream(byteArray)) {
                this.subNode = documentBuilder.parse(is).getDocumentElement().getFirstChild().getFirstChild();
            }
        }
        finally {
            if (this.inputOctetStreamProxy != null) {
                this.inputOctetStreamProxy.close();
            }
            this.inputOctetStreamProxy = null;
            this.bytes = null;
        }
    }
    
    public boolean isSecureValidation() {
        return this.secureValidation;
    }
    
    public void setSecureValidation(final boolean secureValidation) {
        this.secureValidation = secureValidation;
    }
    
    public String getPreCalculatedDigest() {
        return this.preCalculatedDigest;
    }
}
