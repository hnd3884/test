package com.sun.xml.internal.fastinfoset.tools;

import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.internal.fastinfoset.Decoder;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.InputStream;

public class FI_SAX_Or_XML_SAX_SAXEvent extends TransformInputOutput
{
    @Override
    public void parse(InputStream document, final OutputStream events, final String workingDirectory) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        document.mark(4);
        final boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        if (isFastInfosetDocument) {
            final SAXDocumentParser parser = new SAXDocumentParser();
            final SAXEventSerializer ses = new SAXEventSerializer(events);
            parser.setContentHandler(ses);
            parser.setProperty("http://xml.org/sax/properties/lexical-handler", ses);
            parser.parse(document);
        }
        else {
            final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            final SAXParser parser2 = parserFactory.newSAXParser();
            final SAXEventSerializer ses2 = new SAXEventSerializer(events);
            final XMLReader reader = parser2.getXMLReader();
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", ses2);
            reader.setContentHandler(ses2);
            if (workingDirectory != null) {
                reader.setEntityResolver(TransformInputOutput.createRelativePathResolver(workingDirectory));
            }
            reader.parse(new InputSource(document));
        }
    }
    
    @Override
    public void parse(final InputStream document, final OutputStream events) throws Exception {
        this.parse(document, events, null);
    }
    
    public static void main(final String[] args) throws Exception {
        final FI_SAX_Or_XML_SAX_SAXEvent p = new FI_SAX_Or_XML_SAX_SAXEvent();
        p.parse(args);
    }
}
