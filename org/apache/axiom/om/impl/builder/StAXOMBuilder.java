package org.apache.axiom.om.impl.builder;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.ext.stax.DTDReader;
import javax.xml.stream.Location;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMAbstractFactory;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import org.apache.axiom.om.util.StAXUtils;
import java.io.FileInputStream;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.OMElement;
import java.io.Closeable;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.logging.Log;

public class StAXOMBuilder extends StAXBuilder
{
    private static final Log log;
    private boolean doTrace;
    @Deprecated
    private static int nsCount;
    private boolean namespaceURIInterning;
    private int lookAheadToken;
    
    public StAXOMBuilder(final OMFactory ombuilderFactory, final XMLStreamReader parser, final Detachable detachable, final Closeable closeable) {
        super(ombuilderFactory, parser, detachable, closeable);
        this.doTrace = StAXOMBuilder.log.isDebugEnabled();
        this.namespaceURIInterning = false;
        this.lookAheadToken = -1;
    }
    
    @Deprecated
    public StAXOMBuilder(final OMFactory ombuilderFactory, final XMLStreamReader parser) {
        super(ombuilderFactory, parser);
        this.doTrace = StAXOMBuilder.log.isDebugEnabled();
        this.namespaceURIInterning = false;
        this.lookAheadToken = -1;
    }
    
    public StAXOMBuilder(final OMFactory factory, final XMLStreamReader parser, final OMElement element, final String characterEncoding) {
        super(factory, parser, characterEncoding, null, null);
        this.doTrace = StAXOMBuilder.log.isDebugEnabled();
        this.namespaceURIInterning = false;
        this.lookAheadToken = -1;
        this.elementLevel = 1;
        this.target = (OMContainerEx)element;
        this.populateOMElement(element);
    }
    
    @Deprecated
    public StAXOMBuilder(final OMFactory factory, final XMLStreamReader parser, final OMElement element) {
        this(factory, parser, element, null);
    }
    
    @Deprecated
    public StAXOMBuilder(final String filePath) throws XMLStreamException, FileNotFoundException {
        this(StAXUtils.createXMLStreamReader(new FileInputStream(filePath)));
    }
    
    @Deprecated
    public StAXOMBuilder(final XMLStreamReader parser) {
        this(OMAbstractFactory.getOMFactory(), parser);
    }
    
    @Deprecated
    public StAXOMBuilder(final InputStream inStream) throws XMLStreamException {
        this(StAXUtils.createXMLStreamReader(inStream));
    }
    
    @Deprecated
    public StAXOMBuilder() {
        this.doTrace = StAXOMBuilder.log.isDebugEnabled();
        this.namespaceURIInterning = false;
        this.lookAheadToken = -1;
    }
    
    @Override
    protected OMDocument createDocument() {
        return this.omfactory.createOMDocument(this);
    }
    
    @Override
    public int next() throws OMException {
        if (!this.cache) {
            throw new IllegalStateException("Can't process next node because caching is disabled");
        }
        if (this.done) {
            throw new OMException();
        }
        this.createDocumentIfNecessary();
        final int token = this.parserNext();
        if (!this.cache) {
            return token;
        }
        if (this.doTrace && this.parser != null) {
            final int currentParserToken = this.parser.getEventType();
            if (currentParserToken != token) {
                StAXOMBuilder.log.debug((Object)("WARNING: The current state of the parser is not equal to the state just received from the parser. The current state in the paser is " + XMLEventUtils.getEventTypeString(currentParserToken) + " the state just received is " + XMLEventUtils.getEventTypeString(token)));
            }
            this.logParserState();
        }
        switch (token) {
            case 1: {
                ++this.elementLevel;
                final OMNode node = this.createNextOMElement();
                if (!node.isComplete()) {
                    this.target = (OMContainerEx)node;
                    break;
                }
                break;
            }
            case 4: {
                this.createOMText(4);
                break;
            }
            case 12: {
                this.createOMText(12);
                break;
            }
            case 2: {
                --this.elementLevel;
                this.endElement();
                break;
            }
            case 8: {
                this.done = true;
                ((OMContainerEx)this.document).setComplete(true);
                this.target = null;
                break;
            }
            case 6: {
                this.createOMText(6);
                break;
            }
            case 5: {
                this.createComment();
                break;
            }
            case 11: {
                this.createDTD();
                break;
            }
            case 3: {
                this.createPI();
                break;
            }
            case 9: {
                this.createEntityReference();
                break;
            }
            default: {
                throw new OMException();
            }
        }
        if (this.target == null && !this.done) {
            while (this.parserNext() != 8) {}
            this.done = true;
        }
        return token;
    }
    
    protected OMNode createNextOMElement() {
        OMNode newElement = null;
        if (this.elementLevel == 1 && this.customBuilderForPayload != null) {
            newElement = this.createWithCustomBuilder(this.customBuilderForPayload, this.omfactory);
        }
        else if (this.customBuilders != null && this.elementLevel <= this.maxDepthForCustomBuilders) {
            final String namespace = this.parser.getNamespaceURI();
            final String localPart = this.parser.getLocalName();
            final CustomBuilder customBuilder = this.getCustomBuilder(namespace, localPart);
            if (customBuilder != null) {
                newElement = this.createWithCustomBuilder(customBuilder, this.omfactory);
            }
        }
        if (newElement == null) {
            newElement = this.createOMElement();
        }
        else {
            --this.elementLevel;
        }
        return newElement;
    }
    
    protected OMNode createWithCustomBuilder(final CustomBuilder customBuilder, final OMFactory factory) {
        String namespace = this.parser.getNamespaceURI();
        if (namespace == null) {
            namespace = "";
        }
        final String localPart = this.parser.getLocalName();
        if (StAXOMBuilder.log.isDebugEnabled()) {
            StAXOMBuilder.log.debug((Object)("Invoking CustomBuilder, " + customBuilder.toString() + ", to the OMNode for {" + namespace + "}" + localPart));
        }
        this.target.setComplete(true);
        final OMNode node = customBuilder.create(namespace, localPart, this.target, this.parser, factory);
        this.target.setComplete(false);
        if (StAXOMBuilder.log.isDebugEnabled()) {
            if (node != null) {
                StAXOMBuilder.log.debug((Object)("The CustomBuilder, " + customBuilder.toString() + "successfully constructed the OMNode for {" + namespace + "}" + localPart));
            }
            else {
                StAXOMBuilder.log.debug((Object)("The CustomBuilder, " + customBuilder.toString() + " did not construct an OMNode for {" + namespace + "}" + localPart + ". The OMNode will be constructed using the installed stax om builder"));
            }
            StAXOMBuilder.log.debug((Object)"The current state of the parser is: ");
            this.logParserState();
        }
        return node;
    }
    
    protected void logParserState() {
        if (this.doTrace) {
            final int currentEvent = this.parser.getEventType();
            switch (currentEvent) {
                case 1: {
                    StAXOMBuilder.log.trace((Object)"START_ELEMENT: ");
                    StAXOMBuilder.log.trace((Object)("  QName: " + this.parser.getName()));
                    break;
                }
                case 7: {
                    StAXOMBuilder.log.trace((Object)"START_DOCUMENT: ");
                    break;
                }
                case 4: {
                    StAXOMBuilder.log.trace((Object)"CHARACTERS: ");
                    break;
                }
                case 12: {
                    StAXOMBuilder.log.trace((Object)"CDATA: ");
                    break;
                }
                case 2: {
                    StAXOMBuilder.log.trace((Object)"END_ELEMENT: ");
                    StAXOMBuilder.log.trace((Object)("  QName: " + this.parser.getName()));
                    break;
                }
                case 8: {
                    StAXOMBuilder.log.trace((Object)"END_DOCUMENT: ");
                    break;
                }
                case 6: {
                    StAXOMBuilder.log.trace((Object)"SPACE: ");
                    break;
                }
                case 5: {
                    StAXOMBuilder.log.trace((Object)"COMMENT: ");
                    break;
                }
                case 11: {
                    StAXOMBuilder.log.trace((Object)"DTD: ");
                    StAXOMBuilder.log.trace((Object)("[" + this.parser.getText() + "]"));
                    break;
                }
                case 3: {
                    StAXOMBuilder.log.trace((Object)"PROCESSING_INSTRUCTION: ");
                    StAXOMBuilder.log.trace((Object)("   [" + this.parser.getPITarget() + "][" + this.parser.getPIData() + "]"));
                    break;
                }
                case 9: {
                    StAXOMBuilder.log.trace((Object)"ENTITY_REFERENCE: ");
                    StAXOMBuilder.log.trace((Object)("    " + this.parser.getLocalName() + "[" + this.parser.getText() + "]"));
                    break;
                }
                default: {
                    StAXOMBuilder.log.trace((Object)("UNKNOWN_STATE: " + currentEvent));
                    break;
                }
            }
        }
    }
    
    private void populateOMElement(final OMElement node) {
        this.processNamespaceData(node);
        this.processAttributes(node);
        final Location location = this.parser.getLocation();
        if (location != null) {
            node.setLineNumber(location.getLineNumber());
        }
    }
    
    @Override
    protected final OMNode createOMElement() throws OMException {
        final OMElement node = this.constructNode(this.target, this.parser.getLocalName());
        this.populateOMElement(node);
        return node;
    }
    
    protected OMElement constructNode(final OMContainer parent, final String elementName) {
        return this.omfactory.createOMElement(this.parser.getLocalName(), this.target, this);
    }
    
    protected OMNode createComment() throws OMException {
        return this.omfactory.createOMComment(this.target, this.parser.getText(), true);
    }
    
    protected OMNode createDTD() throws OMException {
        DTDReader dtdReader;
        try {
            dtdReader = (DTDReader)this.parser.getProperty(DTDReader.PROPERTY);
        }
        catch (final IllegalArgumentException ex) {
            dtdReader = null;
        }
        if (dtdReader == null) {
            throw new OMException("Cannot create OMDocType because the XMLStreamReader doesn't support the DTDReader extension");
        }
        String internalSubset = this.getDTDText();
        if (internalSubset != null && internalSubset.length() == 0) {
            internalSubset = null;
        }
        return this.omfactory.createOMDocType(this.target, dtdReader.getRootName(), dtdReader.getPublicId(), dtdReader.getSystemId(), internalSubset, true);
    }
    
    private String getDTDText() throws OMException {
        String text = null;
        try {
            text = this.parser.getText();
        }
        catch (final RuntimeException e) {
            final Boolean b = (Boolean)this.parser.getProperty("javax.xml.stream.isSupportingExternalEntities");
            if (b == null || b == Boolean.TRUE) {
                throw e;
            }
            if (StAXOMBuilder.log.isDebugEnabled()) {
                StAXOMBuilder.log.debug((Object)("An exception occurred while calling getText() for a DOCTYPE.  The exception is ignored because external entites support is disabled.  The ignored exception is " + e));
            }
        }
        return text;
    }
    
    protected OMNode createPI() throws OMException {
        return this.omfactory.createOMProcessingInstruction(this.target, this.parser.getPITarget(), this.parser.getPIData(), true);
    }
    
    protected OMNode createEntityReference() {
        return this.omfactory.createOMEntityReference(this.target, this.parser.getLocalName(), this.parser.getText(), true);
    }
    
    private void endElement() {
        this.target.setComplete(true);
        if (this.elementLevel == 0) {
            this.target = (OMContainerEx)this.document;
        }
        else {
            this.target = (OMContainerEx)((OMElement)this.target).getParent();
        }
    }
    
    public OMElement getDocumentElement() {
        return this.getDocumentElement(false);
    }
    
    public OMElement getDocumentElement(final boolean discardDocument) {
        final OMElement element = this.getDocument().getOMDocumentElement();
        if (discardDocument) {
            ((OMElementEx)element).detachAndDiscardParent();
            this.document = null;
        }
        return element;
    }
    
    @Override
    protected void processNamespaceData(final OMElement node) {
        for (int namespaceCount = this.parser.getNamespaceCount(), i = 0; i < namespaceCount; ++i) {
            String prefix = this.parser.getNamespacePrefix(i);
            String namespaceURI = this.parser.getNamespaceURI(i);
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            else if (this.isNamespaceURIInterning()) {
                namespaceURI = namespaceURI.intern();
            }
            if (prefix == null) {
                prefix = "";
            }
            ((OMElementEx)node).addNamespaceDeclaration(namespaceURI, prefix);
        }
        final String namespaceURI2 = this.parser.getNamespaceURI();
        String prefix = this.parser.getPrefix();
        BuilderUtil.setNamespace(node, namespaceURI2, prefix, this.isNamespaceURIInterning());
    }
    
    @Deprecated
    public void setDoDebug(final boolean doDebug) {
        this.doTrace = doDebug;
    }
    
    @Deprecated
    protected String createPrefix() {
        return "ns" + StAXOMBuilder.nsCount++;
    }
    
    public void setNamespaceURIInterning(final boolean b) {
        this.namespaceURIInterning = b;
    }
    
    public boolean isNamespaceURIInterning() {
        return this.namespaceURIInterning;
    }
    
    @Override
    int parserNext() {
        if (this.lookAheadToken >= 0) {
            if (StAXOMBuilder.log.isDebugEnabled()) {
                StAXOMBuilder.log.debug((Object)("Consuming look-ahead token " + XMLEventUtils.getEventTypeString(this.lookAheadToken)));
            }
            final int token = this.lookAheadToken;
            this.lookAheadToken = -1;
            return token;
        }
        try {
            if (this.parserException == null) {
                int event;
                try {
                    event = this.parser.next();
                }
                catch (final XMLStreamException ex) {
                    throw this.parserException = ex;
                }
                if (event == 8) {
                    if (this.cache && this.elementLevel != 0) {
                        throw new OMException("Unexpected END_DOCUMENT event");
                    }
                    if (this.autoClose) {
                        this.close();
                    }
                }
                return event;
            }
            StAXOMBuilder.log.warn((Object)"Attempt to access a parser that has thrown a parse exception before; rethrowing the original exception.");
            if (this.parserException instanceof XMLStreamException) {
                throw (XMLStreamException)this.parserException;
            }
            throw (RuntimeException)this.parserException;
        }
        catch (final XMLStreamException ex2) {
            throw new DeferredParsingException(ex2);
        }
    }
    
    public boolean lookahead() {
        while (true) {
            if (this.lookAheadToken < 0) {
                this.lookAheadToken = this.parserNext();
            }
            if (this.lookAheadToken == 1) {
                StAXOMBuilder.log.debug((Object)"Performing look-ahead; START_ELEMENT found");
                return true;
            }
            if (this.lookAheadToken == 2 || this.lookAheadToken == 7 || this.lookAheadToken == 8) {
                if (StAXOMBuilder.log.isDebugEnabled()) {
                    StAXOMBuilder.log.debug((Object)("Performing look-ahead; " + XMLEventUtils.getEventTypeString(this.lookAheadToken) + " found"));
                }
                this.next();
                return false;
            }
            this.next();
        }
    }
    
    public boolean isLookahead() {
        return this.lookAheadToken >= 0;
    }
    
    static {
        log = LogFactory.getLog((Class)StAXOMBuilder.class);
        StAXOMBuilder.nsCount = 0;
    }
}
