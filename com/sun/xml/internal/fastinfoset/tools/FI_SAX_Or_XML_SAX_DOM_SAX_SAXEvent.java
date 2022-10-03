package com.sun.xml.internal.fastinfoset.tools;

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.Transformer;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.TransformerFactory;
import com.sun.xml.internal.fastinfoset.Decoder;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.InputStream;

public class FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent extends TransformInputOutput
{
    @Override
    public void parse(InputStream document, final OutputStream events, final String workingDirectory) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        document.mark(4);
        final boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer t = tf.newTransformer();
        final DOMResult dr = new DOMResult();
        if (isFastInfosetDocument) {
            t.transform(new FastInfosetSource(document), dr);
        }
        else if (workingDirectory != null) {
            final SAXParser parser = this.getParser();
            final XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(TransformInputOutput.createRelativePathResolver(workingDirectory));
            final SAXSource source = new SAXSource(reader, new InputSource(document));
            t.transform(source, dr);
        }
        else {
            t.transform(new StreamSource(document), dr);
        }
        final SAXEventSerializer ses = new SAXEventSerializer(events);
        t.transform(new DOMSource(dr.getNode()), new SAXResult(ses));
    }
    
    @Override
    public void parse(final InputStream document, final OutputStream events) throws Exception {
        this.parse(document, events, null);
    }
    
    private SAXParser getParser() {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        try {
            return saxParserFactory.newSAXParser();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent p = new FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent();
        p.parse(args);
    }
}
