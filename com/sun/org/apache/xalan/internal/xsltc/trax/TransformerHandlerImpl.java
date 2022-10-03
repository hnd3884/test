package com.sun.org.apache.xalan.internal.xsltc.trax;

import org.xml.sax.Attributes;
import javax.xml.transform.dom.DOMResult;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import javax.xml.transform.Source;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import javax.xml.transform.Transformer;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Locator;
import javax.xml.transform.Result;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import org.xml.sax.ext.DeclHandler;
import javax.xml.transform.sax.TransformerHandler;

public class TransformerHandlerImpl implements TransformerHandler, DeclHandler
{
    private TransformerImpl _transformer;
    private AbstractTranslet _translet;
    private String _systemId;
    private SAXImpl _dom;
    private ContentHandler _handler;
    private LexicalHandler _lexHandler;
    private DTDHandler _dtdHandler;
    private DeclHandler _declHandler;
    private Result _result;
    private Locator _locator;
    private boolean _done;
    private boolean _isIdentity;
    
    public TransformerHandlerImpl(final TransformerImpl transformer) {
        this._translet = null;
        this._dom = null;
        this._handler = null;
        this._lexHandler = null;
        this._dtdHandler = null;
        this._declHandler = null;
        this._result = null;
        this._locator = null;
        this._done = false;
        this._isIdentity = false;
        this._transformer = transformer;
        if (transformer.isIdentity()) {
            this._handler = new DefaultHandler();
            this._isIdentity = true;
        }
        else {
            this._translet = this._transformer.getTranslet();
        }
    }
    
    @Override
    public String getSystemId() {
        return this._systemId;
    }
    
    @Override
    public void setSystemId(final String id) {
        this._systemId = id;
    }
    
    @Override
    public Transformer getTransformer() {
        return this._transformer;
    }
    
    @Override
    public void setResult(final Result result) throws IllegalArgumentException {
        this._result = result;
        if (null == result) {
            final ErrorMsg err = new ErrorMsg("ER_RESULT_NULL");
            throw new IllegalArgumentException(err.toString());
        }
        if (this._isIdentity) {
            try {
                final SerializationHandler outputHandler = this._transformer.getOutputHandler(result);
                this._transformer.transferOutputProperties(outputHandler);
                this._handler = outputHandler;
                this._lexHandler = outputHandler;
            }
            catch (final TransformerException e) {
                this._result = null;
            }
        }
        else if (this._done) {
            try {
                this._transformer.setDOM(this._dom);
                this._transformer.transform(null, this._result);
            }
            catch (final TransformerException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this._handler.characters(ch, start, length);
    }
    
    @Override
    public void startDocument() throws SAXException {
        if (this._result == null) {
            final ErrorMsg err = new ErrorMsg("JAXP_SET_RESULT_ERR");
            throw new SAXException(err.toString());
        }
        if (!this._isIdentity) {
            final boolean hasIdCall = this._translet != null && this._translet.hasIdCall();
            XSLTCDTMManager dtmManager = null;
            try {
                dtmManager = this._transformer.getTransformerFactory().createNewDTMManagerInstance();
            }
            catch (final Exception e) {
                throw new SAXException(e);
            }
            DTMWSFilter wsFilter;
            if (this._translet != null && this._translet instanceof StripFilter) {
                wsFilter = new DOMWSFilter(this._translet);
            }
            else {
                wsFilter = null;
            }
            this._dom = (SAXImpl)dtmManager.getDTM(null, false, wsFilter, true, false, hasIdCall);
            this._handler = this._dom.getBuilder();
            this._lexHandler = (LexicalHandler)this._handler;
            this._dtdHandler = (DTDHandler)this._handler;
            this._declHandler = (DeclHandler)this._handler;
            this._dom.setDocumentURI(this._systemId);
            if (this._locator != null) {
                this._handler.setDocumentLocator(this._locator);
            }
        }
        this._handler.startDocument();
    }
    
    @Override
    public void endDocument() throws SAXException {
        this._handler.endDocument();
        if (!this._isIdentity) {
            if (this._result != null) {
                try {
                    this._transformer.setDOM(this._dom);
                    this._transformer.transform(null, this._result);
                }
                catch (final TransformerException e) {
                    throw new SAXException(e);
                }
            }
            this._done = true;
            this._transformer.setDOM(this._dom);
        }
        if (this._isIdentity && this._result instanceof DOMResult) {
            ((DOMResult)this._result).setNode(this._transformer.getTransletOutputHandlerFactory().getNode());
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qname, final Attributes attributes) throws SAXException {
        this._handler.startElement(uri, localName, qname, attributes);
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qname) throws SAXException {
        this._handler.endElement(namespaceURI, localName, qname);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this._handler.processingInstruction(target, data);
    }
    
    @Override
    public void startCDATA() throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.startCDATA();
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.endCDATA();
        }
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.comment(ch, start, length);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this._handler.ignorableWhitespace(ch, start, length);
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this._locator = locator;
        if (this._handler != null) {
            this._handler.setDocumentLocator(locator);
        }
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        this._handler.skippedEntity(name);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this._handler.startPrefixMapping(prefix, uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        this._handler.endPrefixMapping(prefix);
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.startDTD(name, publicId, systemId);
        }
    }
    
    @Override
    public void endDTD() throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.endDTD();
        }
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.startEntity(name);
        }
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.endEntity(name);
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
        if (this._dtdHandler != null) {
            this._dtdHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
        }
    }
    
    @Override
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
        if (this._dtdHandler != null) {
            this._dtdHandler.notationDecl(name, publicId, systemId);
        }
    }
    
    @Override
    public void attributeDecl(final String eName, final String aName, final String type, final String valueDefault, final String value) throws SAXException {
        if (this._declHandler != null) {
            this._declHandler.attributeDecl(eName, aName, type, valueDefault, value);
        }
    }
    
    @Override
    public void elementDecl(final String name, final String model) throws SAXException {
        if (this._declHandler != null) {
            this._declHandler.elementDecl(name, model);
        }
    }
    
    @Override
    public void externalEntityDecl(final String name, final String publicId, final String systemId) throws SAXException {
        if (this._declHandler != null) {
            this._declHandler.externalEntityDecl(name, publicId, systemId);
        }
    }
    
    @Override
    public void internalEntityDecl(final String name, final String value) throws SAXException {
        if (this._declHandler != null) {
            this._declHandler.internalEntityDecl(name, value);
        }
    }
    
    public void reset() {
        this._systemId = null;
        this._dom = null;
        this._handler = null;
        this._lexHandler = null;
        this._dtdHandler = null;
        this._declHandler = null;
        this._result = null;
        this._locator = null;
    }
}
