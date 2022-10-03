package com.sun.xml.internal.fastinfoset.tools;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import com.sun.xml.internal.fastinfoset.dom.DOMDocumentSerializer;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.OutputStream;
import java.io.InputStream;

public class XML_DOM_FI extends TransformInputOutput
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
        final DOMDocumentSerializer s = new DOMDocumentSerializer();
        s.setOutputStream(finf);
        s.serialize(d);
    }
    
    @Override
    public void parse(final InputStream document, final OutputStream finf) throws Exception {
        this.parse(document, finf, null);
    }
    
    public static void main(final String[] args) throws Exception {
        final XML_DOM_FI p = new XML_DOM_FI();
        p.parse(args);
    }
}
