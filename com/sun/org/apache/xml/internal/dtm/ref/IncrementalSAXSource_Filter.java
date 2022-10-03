package com.sun.org.apache.xml.internal.dtm.ref;

import java.io.IOException;
import com.sun.org.apache.xml.internal.utils.ThreadControllerWrapper;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;

final class IncrementalSAXSource_Filter implements IncrementalSAXSource, ContentHandler, DTDHandler, LexicalHandler, ErrorHandler, Runnable
{
    boolean DEBUG;
    private CoroutineManager fCoroutineManager;
    private int fControllerCoroutineID;
    private int fSourceCoroutineID;
    private ContentHandler clientContentHandler;
    private LexicalHandler clientLexicalHandler;
    private DTDHandler clientDTDHandler;
    private ErrorHandler clientErrorHandler;
    private int eventcounter;
    private int frequency;
    private boolean fNoMoreEvents;
    private XMLReader fXMLReader;
    private InputSource fXMLReaderInputSource;
    
    public IncrementalSAXSource_Filter() {
        this.DEBUG = false;
        this.fCoroutineManager = null;
        this.fControllerCoroutineID = -1;
        this.fSourceCoroutineID = -1;
        this.clientContentHandler = null;
        this.clientLexicalHandler = null;
        this.clientDTDHandler = null;
        this.clientErrorHandler = null;
        this.frequency = 5;
        this.fNoMoreEvents = false;
        this.fXMLReader = null;
        this.fXMLReaderInputSource = null;
        this.init(new CoroutineManager(), -1, -1);
    }
    
    public IncrementalSAXSource_Filter(final CoroutineManager co, final int controllerCoroutineID) {
        this.DEBUG = false;
        this.fCoroutineManager = null;
        this.fControllerCoroutineID = -1;
        this.fSourceCoroutineID = -1;
        this.clientContentHandler = null;
        this.clientLexicalHandler = null;
        this.clientDTDHandler = null;
        this.clientErrorHandler = null;
        this.frequency = 5;
        this.fNoMoreEvents = false;
        this.fXMLReader = null;
        this.fXMLReaderInputSource = null;
        this.init(co, controllerCoroutineID, -1);
    }
    
    public static IncrementalSAXSource createIncrementalSAXSource(final CoroutineManager co, final int controllerCoroutineID) {
        return new IncrementalSAXSource_Filter(co, controllerCoroutineID);
    }
    
    public void init(CoroutineManager co, final int controllerCoroutineID, final int sourceCoroutineID) {
        if (co == null) {
            co = new CoroutineManager();
        }
        this.fCoroutineManager = co;
        this.fControllerCoroutineID = co.co_joinCoroutineSet(controllerCoroutineID);
        this.fSourceCoroutineID = co.co_joinCoroutineSet(sourceCoroutineID);
        if (this.fControllerCoroutineID == -1 || this.fSourceCoroutineID == -1) {
            throw new RuntimeException(XMLMessages.createXMLMessage("ER_COJOINROUTINESET_FAILED", null));
        }
        this.fNoMoreEvents = false;
        this.eventcounter = this.frequency;
    }
    
    public void setXMLReader(final XMLReader eventsource) {
        (this.fXMLReader = eventsource).setContentHandler(this);
        eventsource.setDTDHandler(this);
        eventsource.setErrorHandler(this);
        try {
            eventsource.setProperty("http://xml.org/sax/properties/lexical-handler", this);
        }
        catch (final SAXNotRecognizedException ex) {}
        catch (final SAXNotSupportedException ex2) {}
    }
    
    @Override
    public void setContentHandler(final ContentHandler handler) {
        this.clientContentHandler = handler;
    }
    
    @Override
    public void setDTDHandler(final DTDHandler handler) {
        this.clientDTDHandler = handler;
    }
    
    @Override
    public void setLexicalHandler(final LexicalHandler handler) {
        this.clientLexicalHandler = handler;
    }
    
    public void setErrHandler(final ErrorHandler handler) {
        this.clientErrorHandler = handler;
    }
    
    public void setReturnFrequency(int events) {
        if (events < 1) {
            events = 1;
        }
        final int n = events;
        this.eventcounter = n;
        this.frequency = n;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.characters(ch, start, length);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        if (this.clientContentHandler != null) {
            this.clientContentHandler.endDocument();
        }
        this.eventcounter = 0;
        this.co_yield(false);
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.endElement(namespaceURI, localName, qName);
        }
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.endPrefixMapping(prefix);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.ignorableWhitespace(ch, start, length);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.processingInstruction(target, data);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.setDocumentLocator(locator);
        }
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.skippedEntity(name);
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.co_entry_pause();
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.startDocument();
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.startElement(namespaceURI, localName, qName, atts);
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        final int eventcounter = this.eventcounter - 1;
        this.eventcounter = eventcounter;
        if (eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
        if (this.clientContentHandler != null) {
            this.clientContentHandler.startPrefixMapping(prefix, uri);
        }
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (null != this.clientLexicalHandler) {
            this.clientLexicalHandler.comment(ch, start, length);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        if (null != this.clientLexicalHandler) {
            this.clientLexicalHandler.endCDATA();
        }
    }
    
    @Override
    public void endDTD() throws SAXException {
        if (null != this.clientLexicalHandler) {
            this.clientLexicalHandler.endDTD();
        }
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
        if (null != this.clientLexicalHandler) {
            this.clientLexicalHandler.endEntity(name);
        }
    }
    
    @Override
    public void startCDATA() throws SAXException {
        if (null != this.clientLexicalHandler) {
            this.clientLexicalHandler.startCDATA();
        }
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        if (null != this.clientLexicalHandler) {
            this.clientLexicalHandler.startDTD(name, publicId, systemId);
        }
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
        if (null != this.clientLexicalHandler) {
            this.clientLexicalHandler.startEntity(name);
        }
    }
    
    @Override
    public void notationDecl(final String a, final String b, final String c) throws SAXException {
        if (null != this.clientDTDHandler) {
            this.clientDTDHandler.notationDecl(a, b, c);
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String a, final String b, final String c, final String d) throws SAXException {
        if (null != this.clientDTDHandler) {
            this.clientDTDHandler.unparsedEntityDecl(a, b, c, d);
        }
    }
    
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        if (null != this.clientErrorHandler) {
            this.clientErrorHandler.error(exception);
        }
    }
    
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        if (null != this.clientErrorHandler) {
            this.clientErrorHandler.error(exception);
        }
        this.eventcounter = 0;
        this.co_yield(false);
    }
    
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        if (null != this.clientErrorHandler) {
            this.clientErrorHandler.error(exception);
        }
    }
    
    public int getSourceCoroutineID() {
        return this.fSourceCoroutineID;
    }
    
    public int getControllerCoroutineID() {
        return this.fControllerCoroutineID;
    }
    
    public CoroutineManager getCoroutineManager() {
        return this.fCoroutineManager;
    }
    
    protected void count_and_yield(final boolean moreExpected) throws SAXException {
        if (!moreExpected) {
            this.eventcounter = 0;
        }
        if (--this.eventcounter <= 0) {
            this.co_yield(true);
            this.eventcounter = this.frequency;
        }
    }
    
    private void co_entry_pause() throws SAXException {
        if (this.fCoroutineManager == null) {
            this.init(null, -1, -1);
        }
        try {
            final Object arg = this.fCoroutineManager.co_entry_pause(this.fSourceCoroutineID);
            if (arg == Boolean.FALSE) {
                this.co_yield(false);
            }
        }
        catch (final NoSuchMethodException e) {
            if (this.DEBUG) {
                e.printStackTrace();
            }
            throw new SAXException(e);
        }
    }
    
    private void co_yield(final boolean moreRemains) throws SAXException {
        if (this.fNoMoreEvents) {
            return;
        }
        try {
            Object arg = Boolean.FALSE;
            if (moreRemains) {
                arg = this.fCoroutineManager.co_resume(Boolean.TRUE, this.fSourceCoroutineID, this.fControllerCoroutineID);
            }
            if (arg == Boolean.FALSE) {
                this.fNoMoreEvents = true;
                if (this.fXMLReader != null) {
                    throw new StopException();
                }
                this.fCoroutineManager.co_exit_to(Boolean.FALSE, this.fSourceCoroutineID, this.fControllerCoroutineID);
            }
        }
        catch (final NoSuchMethodException e) {
            this.fNoMoreEvents = true;
            this.fCoroutineManager.co_exit(this.fSourceCoroutineID);
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startParse(final InputSource source) throws SAXException {
        if (this.fNoMoreEvents) {
            throw new SAXException(XMLMessages.createXMLMessage("ER_INCRSAXSRCFILTER_NOT_RESTARTABLE", null));
        }
        if (this.fXMLReader == null) {
            throw new SAXException(XMLMessages.createXMLMessage("ER_XMLRDR_NOT_BEFORE_STARTPARSE", null));
        }
        this.fXMLReaderInputSource = source;
        ThreadControllerWrapper.runThread(this, -1);
    }
    
    @Override
    public void run() {
        if (this.fXMLReader == null) {
            return;
        }
        if (this.DEBUG) {
            System.out.println("IncrementalSAXSource_Filter parse thread launched");
        }
        Object arg = Boolean.FALSE;
        try {
            this.fXMLReader.parse(this.fXMLReaderInputSource);
        }
        catch (final IOException ex) {
            arg = ex;
        }
        catch (final StopException ex2) {
            if (this.DEBUG) {
                System.out.println("Active IncrementalSAXSource_Filter normal stop exception");
            }
        }
        catch (final SAXException ex3) {
            final Exception inner = ex3.getException();
            if (inner instanceof StopException) {
                if (this.DEBUG) {
                    System.out.println("Active IncrementalSAXSource_Filter normal stop exception");
                }
            }
            else {
                if (this.DEBUG) {
                    System.out.println("Active IncrementalSAXSource_Filter UNEXPECTED SAX exception: " + inner);
                    inner.printStackTrace();
                }
                arg = ex3;
            }
        }
        this.fXMLReader = null;
        try {
            this.fNoMoreEvents = true;
            this.fCoroutineManager.co_exit_to(arg, this.fSourceCoroutineID, this.fControllerCoroutineID);
        }
        catch (final NoSuchMethodException e) {
            e.printStackTrace(System.err);
            this.fCoroutineManager.co_exit(this.fSourceCoroutineID);
        }
    }
    
    @Override
    public Object deliverMoreNodes(final boolean parsemore) {
        if (this.fNoMoreEvents) {
            return Boolean.FALSE;
        }
        try {
            final Object result = this.fCoroutineManager.co_resume(parsemore ? Boolean.TRUE : Boolean.FALSE, this.fControllerCoroutineID, this.fSourceCoroutineID);
            if (result == Boolean.FALSE) {
                this.fCoroutineManager.co_exit(this.fControllerCoroutineID);
            }
            return result;
        }
        catch (final NoSuchMethodException e) {
            return e;
        }
    }
    
    class StopException extends RuntimeException
    {
        static final long serialVersionUID = -1129245796185754956L;
    }
}
