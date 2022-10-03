package org.apache.axiom.om.impl.builder;

import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.OMAttributeEx;
import org.apache.axiom.om.OMElement;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.StAXUtils;
import java.io.InputStream;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import java.util.LinkedHashMap;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import javax.xml.namespace.QName;
import java.util.Map;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.impl.OMContainerEx;
import java.io.Closeable;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.OMXMLParserWrapper;

public abstract class StAXBuilder implements OMXMLParserWrapper, CustomBuilderSupport
{
    private static final Log log;
    protected XMLStreamReader parser;
    protected OMFactoryEx omfactory;
    private final Detachable detachable;
    private final Closeable closeable;
    protected OMContainerEx target;
    protected boolean done;
    protected boolean cache;
    protected boolean parserAccessed;
    protected OMDocument document;
    protected String charEncoding;
    boolean autoClose;
    protected boolean _isClosed;
    protected CustomBuilder customBuilderForPayload;
    protected Map<QName, CustomBuilder> customBuilders;
    protected int maxDepthForCustomBuilders;
    protected DataHandlerReader dataHandlerReader;
    protected int elementLevel;
    protected Exception parserException;
    private final Map<OMContainer, Throwable> discardTracker;
    
    protected StAXBuilder(final OMFactory omFactory, final XMLStreamReader parser, final String encoding, final Detachable detachable, final Closeable closeable) {
        this.done = false;
        this.cache = true;
        this.parserAccessed = false;
        this.charEncoding = null;
        this._isClosed = false;
        this.customBuilderForPayload = null;
        this.customBuilders = null;
        this.maxDepthForCustomBuilders = -1;
        this.elementLevel = 0;
        this.discardTracker = (StAXBuilder.log.isDebugEnabled() ? new LinkedHashMap<OMContainer, Throwable>() : null);
        this.omfactory = (OMFactoryEx)omFactory;
        this.detachable = detachable;
        this.closeable = closeable;
        this.charEncoding = encoding;
        this.initParser(parser);
    }
    
    protected StAXBuilder(final OMFactory omFactory, final XMLStreamReader parser, final Detachable detachable, final Closeable closeable) {
        this(omFactory, parser, parser.getEncoding(), detachable, closeable);
    }
    
    @Deprecated
    protected StAXBuilder(final OMFactory omFactory, final XMLStreamReader parser) {
        this(omFactory, parser, null, null);
    }
    
    @Deprecated
    protected StAXBuilder(final OMFactory omFactory, final XMLStreamReader parser, final String encoding) {
        this(omFactory, parser, encoding, null, null);
    }
    
    private void initParser(final XMLStreamReader parser) {
        if (parser instanceof BuilderAwareReader) {
            ((BuilderAwareReader)parser).setBuilder(this);
        }
        this.dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(parser);
        this.parser = parser;
    }
    
    @Deprecated
    protected StAXBuilder(final XMLStreamReader parser) {
        this(OMAbstractFactory.getOMFactory(), parser);
    }
    
    @Deprecated
    protected StAXBuilder() {
        this.done = false;
        this.cache = true;
        this.parserAccessed = false;
        this.charEncoding = null;
        this._isClosed = false;
        this.customBuilderForPayload = null;
        this.customBuilders = null;
        this.maxDepthForCustomBuilders = -1;
        this.elementLevel = 0;
        this.discardTracker = (StAXBuilder.log.isDebugEnabled() ? new LinkedHashMap<OMContainer, Throwable>() : null);
        this.detachable = null;
        this.closeable = null;
    }
    
    @Deprecated
    public void init(final InputStream inputStream, final String charSetEncoding, final String url, final String contentType) throws OMException {
        try {
            this.parser = StAXUtils.createXMLStreamReader(inputStream);
        }
        catch (final XMLStreamException e1) {
            throw new OMException(e1);
        }
        this.omfactory = (OMFactoryEx)OMAbstractFactory.getOMFactory();
    }
    
    public void setOMBuilderFactory(final OMFactory ombuilderFactory) {
        this.omfactory = (OMFactoryEx)ombuilderFactory;
    }
    
    protected abstract void processNamespaceData(final OMElement p0);
    
    protected void processAttributes(final OMElement node) {
        for (int attribCount = this.parser.getAttributeCount(), i = 0; i < attribCount; ++i) {
            final String uri = this.parser.getAttributeNamespace(i);
            final String prefix = this.parser.getAttributePrefix(i);
            OMNamespace namespace = null;
            if (uri != null && uri.length() > 0) {
                namespace = node.findNamespace(uri, prefix);
                if (namespace == null) {
                    namespace = node.declareNamespace(uri, prefix);
                }
            }
            final OMAttribute attr = node.addAttribute(this.parser.getAttributeLocalName(i), this.parser.getAttributeValue(i), namespace);
            attr.setAttributeType(this.parser.getAttributeType(i));
            if (attr instanceof OMAttributeEx) {
                ((OMAttributeEx)attr).setSpecified(this.parser.isAttributeSpecified(i));
            }
        }
    }
    
    protected OMNode createOMText(final int textType) {
        if (this.dataHandlerReader != null && this.dataHandlerReader.isBinary()) {
            Object dataHandlerObject;
            if (this.dataHandlerReader.isDeferred()) {
                dataHandlerObject = this.dataHandlerReader.getDataHandlerProvider();
            }
            else {
                try {
                    dataHandlerObject = this.dataHandlerReader.getDataHandler();
                }
                catch (final XMLStreamException ex) {
                    throw new OMException(ex);
                }
            }
            final OMText text = this.omfactory.createOMText(this.target, dataHandlerObject, this.dataHandlerReader.isOptimized(), true);
            final String contentID = this.dataHandlerReader.getContentID();
            if (contentID != null) {
                text.setContentID(contentID);
            }
            return text;
        }
        String text2;
        try {
            text2 = this.parser.getText();
        }
        catch (final RuntimeException ex2) {
            throw this.parserException = ex2;
        }
        return this.omfactory.createOMText(this.target, text2, textType, true);
    }
    
    private void discarded(final OMContainerEx container) {
        container.discarded();
        if (this.discardTracker != null) {
            this.discardTracker.put(container, new Throwable());
        }
    }
    
    public void debugDiscarded(final Object container) {
        if (StAXBuilder.log.isDebugEnabled() && this.discardTracker != null) {
            final Throwable t = this.discardTracker.get(container);
            if (t != null) {
                StAXBuilder.log.debug((Object)"About to throw NodeUnavailableException. Location of the code that caused the node to be discarded/consumed:", t);
            }
        }
    }
    
    public void discard(final OMElement element) throws OMException {
        this.discard((OMContainer)element);
        element.discard();
    }
    
    public void discard(final OMContainer container) throws OMException {
        int targetElementLevel = this.elementLevel;
        for (OMContainerEx current = this.target; current != container; current = (OMContainerEx)((OMElement)current).getParent()) {
            --targetElementLevel;
        }
        if (targetElementLevel == 0 || (targetElementLevel == 1 && this.document == null)) {
            this.close();
            OMContainerEx current = this.target;
            while (true) {
                this.discarded(current);
                if (current == container) {
                    break;
                }
                current = (OMContainerEx)((OMElement)current).getParent();
            }
            return;
        }
        int skipDepth = 0;
    Label_0279:
        while (true) {
            switch (this.parserNext()) {
                case 1: {
                    ++skipDepth;
                    continue;
                }
                case 2: {
                    if (skipDepth > 0) {
                        --skipDepth;
                        continue;
                    }
                    this.discarded(this.target);
                    final boolean found = container == this.target;
                    this.target = (OMContainerEx)((OMElement)this.target).getParent();
                    --this.elementLevel;
                    if (found) {
                        break Label_0279;
                    }
                    continue;
                }
                case 8: {
                    if (skipDepth != 0 || this.elementLevel != 0) {
                        throw new OMException("Unexpected END_DOCUMENT");
                    }
                    if (this.target != this.document) {
                        throw new OMException("Called discard for an element that is not being built by this builder");
                    }
                    this.discarded(this.target);
                    this.target = null;
                    this.done = true;
                    break Label_0279;
                }
            }
        }
    }
    
    public String getText() throws OMException {
        return this.parser.getText();
    }
    
    public String getNamespace() throws OMException {
        return this.parser.getNamespaceURI();
    }
    
    public int getNamespaceCount() throws OMException {
        try {
            return this.parser.getNamespaceCount();
        }
        catch (final Exception e) {
            throw new OMException(e);
        }
    }
    
    public String getNamespacePrefix(final int index) throws OMException {
        try {
            return this.parser.getNamespacePrefix(index);
        }
        catch (final Exception e) {
            throw new OMException(e);
        }
    }
    
    public String getNamespaceUri(final int index) throws OMException {
        try {
            return this.parser.getNamespaceURI(index);
        }
        catch (final Exception e) {
            throw new OMException(e);
        }
    }
    
    public void setCache(final boolean b) {
        if (this.parserAccessed && b) {
            throw new UnsupportedOperationException("parser accessed. cannot set cache");
        }
        this.cache = b;
    }
    
    public boolean isCache() {
        return this.cache;
    }
    
    public String getName() throws OMException {
        return this.parser.getLocalName();
    }
    
    public String getPrefix() throws OMException {
        return this.parser.getPrefix();
    }
    
    public int getAttributeCount() throws OMException {
        return this.parser.getAttributeCount();
    }
    
    public String getAttributeNamespace(final int arg) throws OMException {
        return this.parser.getAttributeNamespace(arg);
    }
    
    public String getAttributeName(final int arg) throws OMException {
        return this.parser.getAttributeNamespace(arg);
    }
    
    public String getAttributePrefix(final int arg) throws OMException {
        return this.parser.getAttributeNamespace(arg);
    }
    
    public Object getParser() {
        if (this.parserAccessed) {
            throw new IllegalStateException("Parser already accessed!");
        }
        if (!this.cache) {
            this.parserAccessed = true;
            OMContainerEx current = this.target;
            while (this.elementLevel > 0) {
                this.discarded(current);
                current = (OMContainerEx)((OMElement)current).getParent();
                --this.elementLevel;
            }
            if (current != null && current == this.document) {
                this.discarded(current);
            }
            this.target = null;
            return this.parser;
        }
        throw new IllegalStateException("cache must be switched off to access the parser");
    }
    
    public XMLStreamReader disableCaching() {
        this.cache = false;
        this.parserNext();
        if (StAXBuilder.log.isDebugEnabled()) {
            StAXBuilder.log.debug((Object)("Caching disabled; current element level is " + this.elementLevel));
        }
        return this.parser;
    }
    
    public void reenableCaching(final OMContainer container) {
        OMContainerEx current = this.target;
        while (true) {
            this.discarded(current);
            if (this.elementLevel == 0) {
                if (current != container || current != this.document) {
                    throw new IllegalStateException();
                }
                break;
            }
            else {
                --this.elementLevel;
                if (current == container) {
                    break;
                }
                current = (OMContainerEx)((OMElement)current).getParent();
            }
        }
        if (container == this.document) {
            this.target = null;
            this.done = true;
        }
        else if (this.elementLevel == 0 && this.document == null) {
            while (this.parserNext() != 8) {}
            this.target = null;
            this.done = true;
        }
        else {
            this.target = (OMContainerEx)((OMElement)container).getParent();
        }
        if (StAXBuilder.log.isDebugEnabled()) {
            StAXBuilder.log.debug((Object)("Caching re-enabled; new element level: " + this.elementLevel + "; done=" + this.done));
        }
        if (this.done && this.autoClose) {
            this.close();
        }
        this.cache = true;
    }
    
    public boolean isCompleted() {
        return this.done;
    }
    
    protected abstract OMNode createOMElement() throws OMException;
    
    abstract int parserNext();
    
    public abstract int next() throws OMException;
    
    public CustomBuilder registerCustomBuilder(final QName qName, final int maxDepth, final CustomBuilder customBuilder) {
        CustomBuilder old = null;
        if (this.customBuilders == null) {
            this.customBuilders = new HashMap<QName, CustomBuilder>();
        }
        else {
            old = this.customBuilders.get(qName);
        }
        this.maxDepthForCustomBuilders = ((this.maxDepthForCustomBuilders > maxDepth) ? this.maxDepthForCustomBuilders : maxDepth);
        this.customBuilders.put(qName, customBuilder);
        return old;
    }
    
    public CustomBuilder registerCustomBuilderForPayload(final CustomBuilder customBuilder) {
        final CustomBuilder old = null;
        this.customBuilderForPayload = customBuilder;
        return old;
    }
    
    protected CustomBuilder getCustomBuilder(final String namespace, final String localPart) {
        if (this.customBuilders == null) {
            return null;
        }
        final QName qName = new QName(namespace, localPart);
        return this.customBuilders.get(qName);
    }
    
    public short getBuilderType() {
        return 1;
    }
    
    public void registerExternalContentHandler(final Object obj) {
        throw new UnsupportedOperationException();
    }
    
    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }
    
    protected abstract OMDocument createDocument();
    
    protected void createDocumentIfNecessary() {
        if (this.document == null && this.parser.getEventType() == 7) {
            this.document = this.createDocument();
            if (this.charEncoding != null) {
                this.document.setCharsetEncoding(this.charEncoding);
            }
            this.document.setXMLVersion(this.parser.getVersion());
            this.document.setXMLEncoding(this.parser.getCharacterEncodingScheme());
            this.document.setStandalone(this.parser.isStandalone() ? "yes" : "no");
            this.target = (OMContainerEx)this.document;
        }
    }
    
    public OMDocument getDocument() {
        this.createDocumentIfNecessary();
        if (this.document == null) {
            throw new UnsupportedOperationException("There is no document linked to this builder");
        }
        return this.document;
    }
    
    public String getCharsetEncoding() {
        return this.document.getCharsetEncoding();
    }
    
    public void close() {
        try {
            if (!this.isClosed()) {
                this.parser.close();
                if (this.closeable != null) {
                    this.closeable.close();
                }
            }
        }
        catch (final Throwable e) {
            if (StAXBuilder.log.isDebugEnabled()) {
                StAXBuilder.log.debug((Object)("Exception occurred during parser close.  Processing continues. " + e));
            }
        }
        finally {
            this._isClosed = true;
            this.done = true;
            this.parser = null;
        }
    }
    
    public Object getReaderProperty(final String name) throws IllegalArgumentException {
        if (!this.isClosed()) {
            return this.parser.getProperty(name);
        }
        return null;
    }
    
    public String getCharacterEncoding() {
        if (this.charEncoding == null) {
            return "UTF-8";
        }
        return this.charEncoding;
    }
    
    public void setAutoClose(final boolean autoClose) {
        this.autoClose = autoClose;
    }
    
    public boolean isClosed() {
        return this._isClosed;
    }
    
    @Deprecated
    public void releaseParserOnClose(final boolean value) {
    }
    
    public void detach() throws OMException {
        if (this.detachable != null) {
            this.detachable.detach();
        }
        else {
            while (!this.done) {
                this.next();
            }
        }
    }
    
    static {
        log = LogFactory.getLog((Class)StAXBuilder.class);
    }
}
