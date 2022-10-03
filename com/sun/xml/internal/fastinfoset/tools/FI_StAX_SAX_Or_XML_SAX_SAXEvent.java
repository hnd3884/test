package com.sun.xml.internal.fastinfoset.tools;

import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.Decoder;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.InputStream;

public class FI_StAX_SAX_Or_XML_SAX_SAXEvent extends TransformInputOutput
{
    @Override
    public void parse(InputStream document, final OutputStream events) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        document.mark(4);
        final boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        if (isFastInfosetDocument) {
            final StAXDocumentParser parser = new StAXDocumentParser();
            parser.setInputStream(document);
            final SAXEventSerializer ses = new SAXEventSerializer(events);
            final StAX2SAXReader reader = new StAX2SAXReader(parser, ses);
            reader.setLexicalHandler(ses);
            reader.adapt();
        }
        else {
            final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            final SAXParser parser2 = parserFactory.newSAXParser();
            final SAXEventSerializer ses2 = new SAXEventSerializer(events);
            parser2.setProperty("http://xml.org/sax/properties/lexical-handler", ses2);
            parser2.parse(document, ses2);
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final FI_StAX_SAX_Or_XML_SAX_SAXEvent p = new FI_StAX_SAX_Or_XML_SAX_SAXEvent();
        p.parse(args);
    }
}
