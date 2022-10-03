package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import com.sun.org.apache.xml.internal.serializer.ToTextSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToHTMLSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToTextStream;
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import com.sun.org.apache.xml.internal.serializer.ToXMLStream;
import com.sun.org.apache.xml.internal.serializer.ToUnknownStream;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2StAXStreamWriter;
import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2StAXEventWriter;
import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2DOM;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLEventWriter;
import org.w3c.dom.Node;
import java.io.Writer;
import java.io.OutputStream;

public class TransletOutputHandlerFactory
{
    public static final int STREAM = 0;
    public static final int SAX = 1;
    public static final int DOM = 2;
    public static final int STAX = 3;
    private String _encoding;
    private String _method;
    private int _outputType;
    private OutputStream _ostream;
    private Writer _writer;
    private Node _node;
    private Node _nextSibling;
    private XMLEventWriter _xmlStAXEventWriter;
    private XMLStreamWriter _xmlStAXStreamWriter;
    private int _indentNumber;
    private ContentHandler _handler;
    private LexicalHandler _lexHandler;
    private boolean _overrideDefaultParser;
    
    public static TransletOutputHandlerFactory newInstance() {
        return new TransletOutputHandlerFactory(true);
    }
    
    public static TransletOutputHandlerFactory newInstance(final boolean overrideDefaultParser) {
        return new TransletOutputHandlerFactory(overrideDefaultParser);
    }
    
    public TransletOutputHandlerFactory(final boolean overrideDefaultParser) {
        this._encoding = "utf-8";
        this._method = null;
        this._outputType = 0;
        this._ostream = System.out;
        this._writer = null;
        this._node = null;
        this._nextSibling = null;
        this._xmlStAXEventWriter = null;
        this._xmlStAXStreamWriter = null;
        this._indentNumber = -1;
        this._handler = null;
        this._lexHandler = null;
        this._overrideDefaultParser = overrideDefaultParser;
    }
    
    public void setOutputType(final int outputType) {
        this._outputType = outputType;
    }
    
    public void setEncoding(final String encoding) {
        if (encoding != null) {
            this._encoding = encoding;
        }
    }
    
    public void setOutputMethod(final String method) {
        this._method = method;
    }
    
    public void setOutputStream(final OutputStream ostream) {
        this._ostream = ostream;
    }
    
    public void setWriter(final Writer writer) {
        this._writer = writer;
    }
    
    public void setHandler(final ContentHandler handler) {
        this._handler = handler;
    }
    
    public void setLexicalHandler(final LexicalHandler lex) {
        this._lexHandler = lex;
    }
    
    public void setNode(final Node node) {
        this._node = node;
    }
    
    public Node getNode() {
        return (this._handler instanceof SAX2DOM) ? ((SAX2DOM)this._handler).getDOM() : null;
    }
    
    public void setNextSibling(final Node nextSibling) {
        this._nextSibling = nextSibling;
    }
    
    public XMLEventWriter getXMLEventWriter() {
        return (this._handler instanceof SAX2StAXEventWriter) ? ((SAX2StAXEventWriter)this._handler).getEventWriter() : null;
    }
    
    public void setXMLEventWriter(final XMLEventWriter eventWriter) {
        this._xmlStAXEventWriter = eventWriter;
    }
    
    public XMLStreamWriter getXMLStreamWriter() {
        return (this._handler instanceof SAX2StAXStreamWriter) ? ((SAX2StAXStreamWriter)this._handler).getStreamWriter() : null;
    }
    
    public void setXMLStreamWriter(final XMLStreamWriter streamWriter) {
        this._xmlStAXStreamWriter = streamWriter;
    }
    
    public void setIndentNumber(final int value) {
        this._indentNumber = value;
    }
    
    public SerializationHandler getSerializationHandler() throws IOException, ParserConfigurationException {
        SerializationHandler result = null;
        switch (this._outputType) {
            case 0: {
                if (this._method == null) {
                    result = new ToUnknownStream();
                }
                else if (this._method.equalsIgnoreCase("xml")) {
                    result = new ToXMLStream();
                }
                else if (this._method.equalsIgnoreCase("html")) {
                    result = new ToHTMLStream();
                }
                else if (this._method.equalsIgnoreCase("text")) {
                    result = new ToTextStream();
                }
                if (result != null && this._indentNumber >= 0) {
                    result.setIndentAmount(this._indentNumber);
                }
                result.setEncoding(this._encoding);
                if (this._writer != null) {
                    result.setWriter(this._writer);
                }
                else {
                    result.setOutputStream(this._ostream);
                }
                return result;
            }
            case 2: {
                this._handler = ((this._node != null) ? new SAX2DOM(this._node, this._nextSibling, this._overrideDefaultParser) : new SAX2DOM(this._overrideDefaultParser));
                this._lexHandler = (LexicalHandler)this._handler;
            }
            case 3: {
                if (this._xmlStAXEventWriter != null) {
                    this._handler = new SAX2StAXEventWriter(this._xmlStAXEventWriter);
                }
                else if (this._xmlStAXStreamWriter != null) {
                    this._handler = new SAX2StAXStreamWriter(this._xmlStAXStreamWriter);
                }
                this._lexHandler = (LexicalHandler)this._handler;
            }
            case 1: {
                if (this._method == null) {
                    this._method = "xml";
                }
                if (this._method.equalsIgnoreCase("xml")) {
                    if (this._lexHandler == null) {
                        result = new ToXMLSAXHandler(this._handler, this._encoding);
                    }
                    else {
                        result = new ToXMLSAXHandler(this._handler, this._lexHandler, this._encoding);
                    }
                }
                else if (this._method.equalsIgnoreCase("html")) {
                    if (this._lexHandler == null) {
                        result = new ToHTMLSAXHandler(this._handler, this._encoding);
                    }
                    else {
                        result = new ToHTMLSAXHandler(this._handler, this._lexHandler, this._encoding);
                    }
                }
                else if (this._method.equalsIgnoreCase("text")) {
                    if (this._lexHandler == null) {
                        result = new ToTextSAXHandler(this._handler, this._encoding);
                    }
                    else {
                        result = new ToTextSAXHandler(this._handler, this._lexHandler, this._encoding);
                    }
                }
                return result;
            }
            default: {
                return null;
            }
        }
    }
}
