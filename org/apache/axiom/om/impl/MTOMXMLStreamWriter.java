package org.apache.axiom.om.impl;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.OMText;
import javax.xml.namespace.NamespaceContext;
import javax.activation.DataHandler;
import java.util.Iterator;
import org.apache.axiom.om.OMException;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamWriter;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import java.io.IOException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.util.CommonUtils;
import java.util.LinkedList;
import org.apache.axiom.om.util.XMLStreamWriterFilter;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.om.OMOutputFormat;
import java.util.List;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import javax.xml.stream.XMLStreamWriter;

public class MTOMXMLStreamWriter implements XMLStreamWriter
{
    private static final Log log;
    private XMLStreamWriter xmlWriter;
    private OutputStream outStream;
    private List otherParts;
    private OMMultipartWriter multipartWriter;
    private OutputStream rootPartOutputStream;
    private OMOutputFormat format;
    private final OptimizationPolicy optimizationPolicy;
    private final boolean preserveAttachments;
    private boolean isEndDocument;
    private boolean isComplete;
    private int depth;
    private XMLStreamWriterFilter xmlStreamWriterFilter;
    
    public MTOMXMLStreamWriter(final XMLStreamWriter xmlWriter) {
        this.otherParts = new LinkedList();
        this.isEndDocument = false;
        this.isComplete = false;
        this.depth = 0;
        this.xmlStreamWriterFilter = null;
        this.xmlWriter = xmlWriter;
        if (MTOMXMLStreamWriter.log.isTraceEnabled()) {
            MTOMXMLStreamWriter.log.trace((Object)("Call Stack =" + CommonUtils.callStackToString()));
        }
        this.format = new OMOutputFormat();
        this.optimizationPolicy = new OptimizationPolicyImpl(this.format);
        this.preserveAttachments = true;
    }
    
    public MTOMXMLStreamWriter(final OutputStream outStream, final OMOutputFormat format) throws XMLStreamException, FactoryConfigurationError {
        this(outStream, format, true);
    }
    
    public MTOMXMLStreamWriter(final OutputStream outStream, final OMOutputFormat format, final boolean preserveAttachments) throws XMLStreamException, FactoryConfigurationError {
        this.otherParts = new LinkedList();
        this.isEndDocument = false;
        this.isComplete = false;
        this.depth = 0;
        this.xmlStreamWriterFilter = null;
        if (MTOMXMLStreamWriter.log.isDebugEnabled()) {
            MTOMXMLStreamWriter.log.debug((Object)"Creating MTOMXMLStreamWriter");
            MTOMXMLStreamWriter.log.debug((Object)("OutputStream =" + outStream.getClass()));
            MTOMXMLStreamWriter.log.debug((Object)("OMFormat = " + format.toString()));
            MTOMXMLStreamWriter.log.debug((Object)("preserveAttachments = " + preserveAttachments));
        }
        if (MTOMXMLStreamWriter.log.isTraceEnabled()) {
            MTOMXMLStreamWriter.log.trace((Object)("Call Stack =" + CommonUtils.callStackToString()));
        }
        this.format = format;
        this.outStream = outStream;
        this.preserveAttachments = preserveAttachments;
        String encoding = format.getCharSetEncoding();
        if (encoding == null) {
            format.setCharSetEncoding(encoding = "utf-8");
        }
        this.optimizationPolicy = new OptimizationPolicyImpl(format);
        if (format.isOptimized()) {
            this.multipartWriter = new OMMultipartWriter(outStream, format);
            try {
                this.rootPartOutputStream = this.multipartWriter.writeRootPart();
            }
            catch (final IOException ex) {
                throw new XMLStreamException(ex);
            }
            final ContentIDGenerator contentIDGenerator = new ContentIDGenerator() {
                public String generateContentID(final String existingContentID) {
                    return (existingContentID != null) ? existingContentID : MTOMXMLStreamWriter.this.getNextContentId();
                }
            };
            this.xmlWriter = new XOPEncodingStreamWriter(StAXUtils.createXMLStreamWriter(format.getStAXWriterConfiguration(), this.rootPartOutputStream, encoding), contentIDGenerator, this.optimizationPolicy);
        }
        else {
            this.xmlWriter = StAXUtils.createXMLStreamWriter(format.getStAXWriterConfiguration(), outStream, format.getCharSetEncoding());
        }
        this.xmlStreamWriterFilter = format.getXmlStreamWriterFilter();
        if (this.xmlStreamWriterFilter != null) {
            if (MTOMXMLStreamWriter.log.isDebugEnabled()) {
                MTOMXMLStreamWriter.log.debug((Object)("Installing XMLStreamWriterFilter " + this.xmlStreamWriterFilter));
            }
            this.xmlStreamWriterFilter.setDelegate(this.xmlWriter);
            this.xmlWriter = this.xmlStreamWriterFilter;
        }
    }
    
    public void writeStartElement(final String string) throws XMLStreamException {
        this.xmlWriter.writeStartElement(string);
        ++this.depth;
    }
    
    public void writeStartElement(final String string, final String string1) throws XMLStreamException {
        this.xmlWriter.writeStartElement(string, string1);
        ++this.depth;
    }
    
    public void writeStartElement(final String string, final String string1, final String string2) throws XMLStreamException {
        this.xmlWriter.writeStartElement(string, string1, string2);
        ++this.depth;
    }
    
    public void writeEmptyElement(final String string, final String string1) throws XMLStreamException {
        this.xmlWriter.writeEmptyElement(string, string1);
    }
    
    public void writeEmptyElement(final String string, final String string1, final String string2) throws XMLStreamException {
        this.xmlWriter.writeEmptyElement(string, string1, string2);
    }
    
    public void writeEmptyElement(final String string) throws XMLStreamException {
        this.xmlWriter.writeEmptyElement(string);
    }
    
    public void writeEndElement() throws XMLStreamException {
        this.xmlWriter.writeEndElement();
        --this.depth;
    }
    
    public void writeEndDocument() throws XMLStreamException {
        MTOMXMLStreamWriter.log.debug((Object)"writeEndDocument");
        this.xmlWriter.writeEndDocument();
        this.isEndDocument = true;
    }
    
    public void close() throws XMLStreamException {
        MTOMXMLStreamWriter.log.debug((Object)"close");
        this.flush();
    }
    
    public void flush() throws XMLStreamException {
        MTOMXMLStreamWriter.log.debug((Object)"Calling MTOMXMLStreamWriter.flush");
        this.xmlWriter.flush();
        if (this.format.isOptimized() && (!this.isComplete & (this.isEndDocument || this.depth == 0))) {
            MTOMXMLStreamWriter.log.debug((Object)"The XML writing is completed.  Now the attachments are written");
            this.isComplete = true;
            try {
                this.rootPartOutputStream.close();
                final XOPEncodingStreamWriter encoder = (XOPEncodingStreamWriter)this.xmlWriter;
                for (final String contentID : encoder.getContentIDs()) {
                    final DataHandler dataHandler = encoder.getDataHandler(contentID);
                    if (this.preserveAttachments || !(dataHandler instanceof DataHandlerExt)) {
                        this.multipartWriter.writePart(dataHandler, contentID);
                    }
                    else {
                        final OutputStream out = this.multipartWriter.writePart(dataHandler.getContentType(), contentID);
                        BufferUtils.inputStream2OutputStream(((DataHandlerExt)dataHandler).readOnce(), out);
                        out.close();
                    }
                }
                for (final Part part : this.otherParts) {
                    this.multipartWriter.writePart(part.getDataHandler(), part.getContentID());
                }
                this.multipartWriter.complete();
            }
            catch (final IOException e) {
                throw new OMException(e);
            }
        }
    }
    
    public void writeAttribute(final String string, final String string1) throws XMLStreamException {
        this.xmlWriter.writeAttribute(string, string1);
    }
    
    public void writeAttribute(final String string, final String string1, final String string2, final String string3) throws XMLStreamException {
        this.xmlWriter.writeAttribute(string, string1, string2, string3);
    }
    
    public void writeAttribute(final String string, final String string1, final String string2) throws XMLStreamException {
        this.xmlWriter.writeAttribute(string, string1, string2);
    }
    
    public void writeNamespace(final String string, final String string1) throws XMLStreamException {
        this.xmlWriter.writeNamespace(string, string1);
    }
    
    public void writeDefaultNamespace(final String string) throws XMLStreamException {
        this.xmlWriter.writeDefaultNamespace(string);
    }
    
    public void writeComment(final String string) throws XMLStreamException {
        this.xmlWriter.writeComment(string);
    }
    
    public void writeProcessingInstruction(final String string) throws XMLStreamException {
        this.xmlWriter.writeProcessingInstruction(string);
    }
    
    public void writeProcessingInstruction(final String string, final String string1) throws XMLStreamException {
        this.xmlWriter.writeProcessingInstruction(string, string1);
    }
    
    public void writeCData(final String string) throws XMLStreamException {
        this.xmlWriter.writeCData(string);
    }
    
    public void writeDTD(final String string) throws XMLStreamException {
        this.xmlWriter.writeDTD(string);
    }
    
    public void writeEntityRef(final String string) throws XMLStreamException {
        this.xmlWriter.writeEntityRef(string);
    }
    
    public void writeStartDocument() throws XMLStreamException {
        this.xmlWriter.writeStartDocument();
    }
    
    public void writeStartDocument(final String string) throws XMLStreamException {
        this.xmlWriter.writeStartDocument(string);
    }
    
    public void writeStartDocument(final String string, final String string1) throws XMLStreamException {
        this.xmlWriter.writeStartDocument(string, string1);
    }
    
    public void writeCharacters(final String string) throws XMLStreamException {
        this.xmlWriter.writeCharacters(string);
    }
    
    public void writeCharacters(final char[] chars, final int i, final int i1) throws XMLStreamException {
        this.xmlWriter.writeCharacters(chars, i, i1);
    }
    
    public String getPrefix(final String string) throws XMLStreamException {
        return this.xmlWriter.getPrefix(string);
    }
    
    public void setPrefix(final String string, final String string1) throws XMLStreamException {
        this.xmlWriter.setPrefix(string, string1);
    }
    
    public void setDefaultNamespace(final String string) throws XMLStreamException {
        this.xmlWriter.setDefaultNamespace(string);
    }
    
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        this.xmlWriter.setNamespaceContext(namespaceContext);
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.xmlWriter.getNamespaceContext();
    }
    
    public Object getProperty(final String string) throws IllegalArgumentException {
        return this.xmlWriter.getProperty(string);
    }
    
    public boolean isOptimized() {
        return this.format.isOptimized();
    }
    
    public String getContentType() {
        return this.format.getContentType();
    }
    
    @Deprecated
    public void writeOptimized(final OMText node) {
        MTOMXMLStreamWriter.log.debug((Object)"Start MTOMXMLStreamWriter.writeOptimized()");
        this.otherParts.add(new Part(node.getContentID(), (DataHandler)node.getDataHandler()));
        MTOMXMLStreamWriter.log.debug((Object)"Exit MTOMXMLStreamWriter.writeOptimized()");
    }
    
    @Deprecated
    public boolean isOptimizedThreshold(final OMText node) {
        try {
            return this.optimizationPolicy.isOptimized((DataHandler)node.getDataHandler(), true);
        }
        catch (final IOException ex) {
            return true;
        }
    }
    
    public String prepareDataHandler(final DataHandler dataHandler) {
        boolean doOptimize;
        try {
            doOptimize = this.optimizationPolicy.isOptimized(dataHandler, true);
        }
        catch (final IOException ex) {
            doOptimize = true;
        }
        if (doOptimize) {
            final String contentID = this.getNextContentId();
            this.otherParts.add(new Part(contentID, dataHandler));
            return contentID;
        }
        return null;
    }
    
    public void setXmlStreamWriter(final XMLStreamWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
    }
    
    public XMLStreamWriter getXmlStreamWriter() {
        return this.xmlWriter;
    }
    
    public String getMimeBoundary() {
        return this.format.getMimeBoundary();
    }
    
    public String getRootContentId() {
        return this.format.getRootContentId();
    }
    
    public String getNextContentId() {
        return this.format.getNextContentId();
    }
    
    public String getCharSetEncoding() {
        return this.format.getCharSetEncoding();
    }
    
    public void setCharSetEncoding(final String charSetEncoding) {
        this.format.setCharSetEncoding(charSetEncoding);
    }
    
    public String getXmlVersion() {
        return this.format.getXmlVersion();
    }
    
    public void setXmlVersion(final String xmlVersion) {
        this.format.setXmlVersion(xmlVersion);
    }
    
    public void setSoap11(final boolean b) {
        this.format.setSOAP11(b);
    }
    
    public boolean isIgnoreXMLDeclaration() {
        return this.format.isIgnoreXMLDeclaration();
    }
    
    public void setIgnoreXMLDeclaration(final boolean ignoreXMLDeclaration) {
        this.format.setIgnoreXMLDeclaration(ignoreXMLDeclaration);
    }
    
    public void setDoOptimize(final boolean b) {
        this.format.setDoOptimize(b);
    }
    
    public OMOutputFormat getOutputFormat() {
        return this.format;
    }
    
    public void setOutputFormat(final OMOutputFormat format) {
        this.format = format;
    }
    
    public OutputStream getOutputStream() throws XMLStreamException {
        if (this.xmlStreamWriterFilter != null) {
            if (MTOMXMLStreamWriter.log.isDebugEnabled()) {
                MTOMXMLStreamWriter.log.debug((Object)("getOutputStream returning null due to presence of XMLStreamWriterFilter " + this.xmlStreamWriterFilter));
            }
            return null;
        }
        OutputStream os = null;
        if (this.rootPartOutputStream != null) {
            os = this.rootPartOutputStream;
        }
        else {
            os = this.outStream;
        }
        if (MTOMXMLStreamWriter.log.isDebugEnabled()) {
            if (os == null) {
                MTOMXMLStreamWriter.log.debug((Object)"Direct access to the output stream is not available.");
            }
            else if (this.rootPartOutputStream != null) {
                MTOMXMLStreamWriter.log.debug((Object)("Returning access to the buffered xml stream: " + this.rootPartOutputStream));
            }
            else {
                MTOMXMLStreamWriter.log.debug((Object)("Returning access to the original output stream: " + os));
            }
        }
        if (os != null) {
            this.writeCharacters("");
            this.flush();
        }
        return os;
    }
    
    public void setFilter(final XMLStreamWriterFilter filter) {
        if (filter != null) {
            if (MTOMXMLStreamWriter.log.isDebugEnabled()) {
                MTOMXMLStreamWriter.log.debug((Object)("setting filter " + filter.getClass()));
            }
            (this.xmlStreamWriterFilter = filter).setDelegate(this.xmlWriter);
            this.xmlWriter = filter;
        }
    }
    
    public XMLStreamWriterFilter removeFilter() {
        XMLStreamWriterFilter filter = null;
        if (this.xmlStreamWriterFilter != null) {
            filter = this.xmlStreamWriterFilter;
            if (MTOMXMLStreamWriter.log.isDebugEnabled()) {
                MTOMXMLStreamWriter.log.debug((Object)("removing filter " + filter.getClass()));
            }
            this.xmlWriter = this.xmlStreamWriterFilter.getDelegate();
            filter.setDelegate(null);
            this.xmlStreamWriterFilter = ((this.xmlWriter instanceof XMLStreamWriterFilter) ? ((XMLStreamWriterFilter)this.xmlWriter) : null);
        }
        return filter;
    }
    
    static {
        log = LogFactory.getLog((Class)MTOMXMLStreamWriter.class);
    }
    
    private static class Part
    {
        private final String contentID;
        private final DataHandler dataHandler;
        
        public Part(final String contentID, final DataHandler dataHandler) {
            this.contentID = contentID;
            this.dataHandler = dataHandler;
        }
        
        public String getContentID() {
            return this.contentID;
        }
        
        public DataHandler getDataHandler() {
            return this.dataHandler;
        }
    }
}
