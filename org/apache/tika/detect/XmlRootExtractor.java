package org.apache.tika.detect;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.tika.utils.XMLReaderUtils;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.commons.io.input.CloseShieldInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.namespace.QName;
import org.apache.tika.parser.ParseContext;

public class XmlRootExtractor
{
    private static final ParseContext EMPTY_CONTEXT;
    
    public QName extractRootElement(final byte[] data) {
        return this.extractRootElement(new ByteArrayInputStream(data));
    }
    
    public QName extractRootElement(final InputStream stream) {
        final ExtractorHandler handler = new ExtractorHandler();
        try {
            XMLReaderUtils.parseSAX((InputStream)new CloseShieldInputStream(stream), new OfflineContentHandler(handler), XmlRootExtractor.EMPTY_CONTEXT);
        }
        catch (final SecurityException e) {
            throw e;
        }
        catch (final Exception ex) {}
        return handler.rootElement;
    }
    
    static {
        EMPTY_CONTEXT = new ParseContext();
    }
    
    private static class ExtractorHandler extends DefaultHandler
    {
        private QName rootElement;
        
        private ExtractorHandler() {
            this.rootElement = null;
        }
        
        @Override
        public void startElement(final String uri, final String local, final String name, final Attributes attributes) throws SAXException {
            this.rootElement = new QName(uri, local);
            throw new SAXException("Aborting: root element received");
        }
    }
}
