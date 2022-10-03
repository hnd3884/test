package com.sun.xml.internal.fastinfoset.tools;

import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import javax.xml.transform.TransformerFactory;
import java.io.OutputStream;
import java.io.InputStream;

public class FI_SAX_XML extends TransformInputOutput
{
    @Override
    public void parse(final InputStream finf, final OutputStream xml) throws Exception {
        final Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new FastInfosetSource(finf), new StreamResult(xml));
    }
    
    public static void main(final String[] args) throws Exception {
        final FI_SAX_XML p = new FI_SAX_XML();
        p.parse(args);
    }
}
