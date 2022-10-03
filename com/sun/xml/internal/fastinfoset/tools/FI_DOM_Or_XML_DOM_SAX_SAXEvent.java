package com.sun.xml.internal.fastinfoset.tools;

import javax.xml.transform.Transformer;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import com.sun.xml.internal.fastinfoset.dom.DOMDocumentParser;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.xml.internal.fastinfoset.Decoder;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.InputStream;

public class FI_DOM_Or_XML_DOM_SAX_SAXEvent extends TransformInputOutput
{
    @Override
    public void parse(InputStream document, final OutputStream events, final String workingDirectory) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        document.mark(4);
        final boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        final DocumentBuilder db = dbf.newDocumentBuilder();
        Document d;
        if (isFastInfosetDocument) {
            d = db.newDocument();
            final DOMDocumentParser ddp = new DOMDocumentParser();
            ddp.parse(d, document);
        }
        else {
            if (workingDirectory != null) {
                db.setEntityResolver(TransformInputOutput.createRelativePathResolver(workingDirectory));
            }
            d = db.parse(document);
        }
        final SAXEventSerializer ses = new SAXEventSerializer(events);
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer t = tf.newTransformer();
        t.transform(new DOMSource(d), new SAXResult(ses));
    }
    
    @Override
    public void parse(final InputStream document, final OutputStream events) throws Exception {
        this.parse(document, events, null);
    }
    
    public static void main(final String[] args) throws Exception {
        final FI_DOM_Or_XML_DOM_SAX_SAXEvent p = new FI_DOM_Or_XML_DOM_SAX_SAXEvent();
        p.parse(args);
    }
}
