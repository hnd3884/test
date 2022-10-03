package com.sun.xml.internal.fastinfoset.tools;

import javax.xml.transform.Transformer;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.OutputStream;
import java.io.InputStream;

public class XML_DOM_SAX_FI extends TransformInputOutput
{
    @Override
    public void parse(final InputStream document, final OutputStream finf, final String workingDirectory) throws Exception {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        final DocumentBuilder db = dbf.newDocumentBuilder();
        if (workingDirectory != null) {
            db.setEntityResolver(TransformInputOutput.createRelativePathResolver(workingDirectory));
        }
        final Document d = db.parse(document);
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer t = tf.newTransformer();
        t.transform(new DOMSource(d), new FastInfosetResult(finf));
    }
    
    @Override
    public void parse(final InputStream document, final OutputStream finf) throws Exception {
        this.parse(document, finf, null);
    }
    
    public static void main(final String[] args) throws Exception {
        final XML_DOM_SAX_FI p = new XML_DOM_SAX_FI();
        p.parse(args);
    }
}
