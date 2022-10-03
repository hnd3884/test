package org.apache.tika.sax;

import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.io.OutputStream;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ContentHandler;
import java.util.Locale;

public class BasicContentHandlerFactory implements ContentHandlerFactory
{
    private final HANDLER_TYPE type;
    private final int writeLimit;
    
    public BasicContentHandlerFactory(final HANDLER_TYPE type, final int writeLimit) {
        this.type = type;
        this.writeLimit = writeLimit;
    }
    
    public static HANDLER_TYPE parseHandlerType(final String handlerTypeName, final HANDLER_TYPE defaultType) {
        if (handlerTypeName == null) {
            return defaultType;
        }
        final String lowerCase;
        final String lcHandlerTypeName = lowerCase = handlerTypeName.toLowerCase(Locale.ROOT);
        switch (lowerCase) {
            case "xml": {
                return HANDLER_TYPE.XML;
            }
            case "text": {
                return HANDLER_TYPE.TEXT;
            }
            case "txt": {
                return HANDLER_TYPE.TEXT;
            }
            case "html": {
                return HANDLER_TYPE.HTML;
            }
            case "body": {
                return HANDLER_TYPE.BODY;
            }
            case "ignore": {
                return HANDLER_TYPE.IGNORE;
            }
            default: {
                return defaultType;
            }
        }
    }
    
    @Override
    public ContentHandler getNewContentHandler() {
        if (this.type == HANDLER_TYPE.BODY) {
            return new BodyContentHandler(this.writeLimit);
        }
        if (this.type == HANDLER_TYPE.IGNORE) {
            return new DefaultHandler();
        }
        if (this.writeLimit > -1) {
            switch (this.type) {
                case TEXT: {
                    return new WriteOutContentHandler(new ToTextContentHandler(), this.writeLimit);
                }
                case HTML: {
                    return new WriteOutContentHandler(new ToHTMLContentHandler(), this.writeLimit);
                }
                case XML: {
                    return new WriteOutContentHandler(new ToXMLContentHandler(), this.writeLimit);
                }
                default: {
                    return new WriteOutContentHandler(new ToTextContentHandler(), this.writeLimit);
                }
            }
        }
        else {
            switch (this.type) {
                case TEXT: {
                    return new ToTextContentHandler();
                }
                case HTML: {
                    return new ToHTMLContentHandler();
                }
                case XML: {
                    return new ToXMLContentHandler();
                }
                default: {
                    return new ToTextContentHandler();
                }
            }
        }
    }
    
    @Override
    public ContentHandler getNewContentHandler(final OutputStream os, final String encoding) throws UnsupportedEncodingException {
        return this.getNewContentHandler(os, Charset.forName(encoding));
    }
    
    @Override
    public ContentHandler getNewContentHandler(final OutputStream os, final Charset charset) {
        if (this.type == HANDLER_TYPE.IGNORE) {
            return new DefaultHandler();
        }
        try {
            if (this.writeLimit > -1) {
                switch (this.type) {
                    case BODY: {
                        return new WriteOutContentHandler(new BodyContentHandler(new OutputStreamWriter(os, charset)), this.writeLimit);
                    }
                    case TEXT: {
                        return new WriteOutContentHandler(new ToTextContentHandler(os, charset.name()), this.writeLimit);
                    }
                    case HTML: {
                        return new WriteOutContentHandler(new ToHTMLContentHandler(os, charset.name()), this.writeLimit);
                    }
                    case XML: {
                        return new WriteOutContentHandler(new ToXMLContentHandler(os, charset.name()), this.writeLimit);
                    }
                    default: {
                        return new WriteOutContentHandler(new ToTextContentHandler(os, charset.name()), this.writeLimit);
                    }
                }
            }
            else {
                switch (this.type) {
                    case BODY: {
                        return new BodyContentHandler(new OutputStreamWriter(os, charset));
                    }
                    case TEXT: {
                        return new ToTextContentHandler(os, charset.name());
                    }
                    case HTML: {
                        return new ToHTMLContentHandler(os, charset.name());
                    }
                    case XML: {
                        return new ToXMLContentHandler(os, charset.name());
                    }
                    default: {
                        return new ToTextContentHandler(os, charset.name());
                    }
                }
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("couldn't find charset for name: " + charset);
        }
    }
    
    public HANDLER_TYPE getType() {
        return this.type;
    }
    
    public int getWriteLimit() {
        return this.writeLimit;
    }
    
    public enum HANDLER_TYPE
    {
        BODY, 
        IGNORE, 
        TEXT, 
        HTML, 
        XML;
    }
}
