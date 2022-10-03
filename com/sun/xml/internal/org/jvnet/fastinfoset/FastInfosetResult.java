package com.sun.xml.internal.org.jvnet.fastinfoset;

import org.xml.sax.ext.LexicalHandler;
import com.sun.xml.internal.fastinfoset.sax.SAXDocumentSerializer;
import org.xml.sax.ContentHandler;
import java.io.OutputStream;
import javax.xml.transform.sax.SAXResult;

public class FastInfosetResult extends SAXResult
{
    OutputStream _outputStream;
    
    public FastInfosetResult(final OutputStream outputStream) {
        this._outputStream = outputStream;
    }
    
    @Override
    public ContentHandler getHandler() {
        ContentHandler handler = super.getHandler();
        if (handler == null) {
            handler = new SAXDocumentSerializer();
            this.setHandler(handler);
        }
        ((SAXDocumentSerializer)handler).setOutputStream(this._outputStream);
        return handler;
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        return (LexicalHandler)this.getHandler();
    }
    
    public OutputStream getOutputStream() {
        return this._outputStream;
    }
    
    public void setOutputStream(final OutputStream outputStream) {
        this._outputStream = outputStream;
    }
}
