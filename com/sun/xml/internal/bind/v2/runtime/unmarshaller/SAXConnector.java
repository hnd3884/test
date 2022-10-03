package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;
import java.util.logging.Level;
import org.xml.sax.Locator;
import javax.xml.bind.JAXBException;
import java.util.logging.Logger;
import javax.xml.bind.UnmarshallerHandler;

public final class SAXConnector implements UnmarshallerHandler
{
    private LocatorEx loc;
    private static final Logger logger;
    private final StringBuilder buffer;
    private final XmlVisitor next;
    private final UnmarshallingContext context;
    private final XmlVisitor.TextPredictor predictor;
    private final TagNameImpl tagName;
    
    public SAXConnector(final XmlVisitor next, final LocatorEx externalLocator) {
        this.buffer = new StringBuilder();
        this.tagName = new TagNameImpl();
        this.next = next;
        this.context = next.getContext();
        this.predictor = next.getPredictor();
        this.loc = externalLocator;
    }
    
    @Override
    public Object getResult() throws JAXBException, IllegalStateException {
        return this.context.getResult();
    }
    
    public UnmarshallingContext getContext() {
        return this.context;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        if (this.loc != null) {
            return;
        }
        this.loc = new LocatorExWrapper(locator);
    }
    
    @Override
    public void startDocument() throws SAXException {
        if (SAXConnector.logger.isLoggable(Level.FINER)) {
            SAXConnector.logger.log(Level.FINER, "SAXConnector.startDocument");
        }
        this.next.startDocument(this.loc, null);
    }
    
    @Override
    public void endDocument() throws SAXException {
        if (SAXConnector.logger.isLoggable(Level.FINER)) {
            SAXConnector.logger.log(Level.FINER, "SAXConnector.endDocument");
        }
        this.next.endDocument();
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (SAXConnector.logger.isLoggable(Level.FINER)) {
            SAXConnector.logger.log(Level.FINER, "SAXConnector.startPrefixMapping: {0}:{1}", new Object[] { prefix, uri });
        }
        this.next.startPrefixMapping(prefix, uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        if (SAXConnector.logger.isLoggable(Level.FINER)) {
            SAXConnector.logger.log(Level.FINER, "SAXConnector.endPrefixMapping: {0}", new Object[] { prefix });
        }
        this.next.endPrefixMapping(prefix);
    }
    
    @Override
    public void startElement(String uri, String local, String qname, final Attributes atts) throws SAXException {
        if (SAXConnector.logger.isLoggable(Level.FINER)) {
            SAXConnector.logger.log(Level.FINER, "SAXConnector.startElement: {0}:{1}:{2}, attrs: {3}", new Object[] { uri, local, qname, atts });
        }
        if (uri == null || uri.length() == 0) {
            uri = "";
        }
        if (local == null || local.length() == 0) {
            local = qname;
        }
        if (qname == null || qname.length() == 0) {
            qname = local;
        }
        this.processText(!this.context.getCurrentState().isMixed());
        this.tagName.uri = uri;
        this.tagName.local = local;
        this.tagName.qname = qname;
        this.tagName.atts = atts;
        this.next.startElement(this.tagName);
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (SAXConnector.logger.isLoggable(Level.FINER)) {
            SAXConnector.logger.log(Level.FINER, "SAXConnector.startElement: {0}:{1}:{2}", new Object[] { uri, localName, qName });
        }
        this.processText(false);
        this.tagName.uri = uri;
        this.tagName.local = localName;
        this.tagName.qname = qName;
        this.next.endElement(this.tagName);
    }
    
    @Override
    public final void characters(final char[] buf, final int start, final int len) {
        if (SAXConnector.logger.isLoggable(Level.FINEST)) {
            SAXConnector.logger.log(Level.FINEST, "SAXConnector.characters: {0}", buf);
        }
        if (this.predictor.expectText()) {
            this.buffer.append(buf, start, len);
        }
    }
    
    @Override
    public final void ignorableWhitespace(final char[] buf, final int start, final int len) {
        if (SAXConnector.logger.isLoggable(Level.FINEST)) {
            SAXConnector.logger.log(Level.FINEST, "SAXConnector.characters{0}", buf);
        }
        this.characters(buf, start, len);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) {
    }
    
    @Override
    public void skippedEntity(final String name) {
    }
    
    private void processText(final boolean ignorable) throws SAXException {
        if (this.predictor.expectText() && (!ignorable || !WhiteSpaceProcessor.isWhiteSpace(this.buffer))) {
            this.next.text(this.buffer);
        }
        this.buffer.setLength(0);
    }
    
    static {
        logger = Util.getClassLogger();
    }
    
    private static final class TagNameImpl extends TagName
    {
        String qname;
        
        @Override
        public String getQname() {
            return this.qname;
        }
    }
}
