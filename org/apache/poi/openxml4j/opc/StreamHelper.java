package org.apache.poi.openxml4j.opc;

import org.apache.poi.util.IOUtils;
import java.io.InputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.FilterOutputStream;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import org.apache.poi.util.XMLHelper;
import java.io.OutputStream;
import org.w3c.dom.Document;

public final class StreamHelper
{
    private StreamHelper() {
    }
    
    public static boolean saveXmlInStream(final Document xmlContent, final OutputStream outStream) {
        try {
            final Transformer trans = XMLHelper.newTransformer();
            final Source xmlSource = new DOMSource(xmlContent);
            final Result outputTarget = new StreamResult(new FilterOutputStream(outStream) {
                @Override
                public void write(final byte[] b, final int off, final int len) throws IOException {
                    this.out.write(b, off, len);
                }
                
                @Override
                public void close() throws IOException {
                    this.out.flush();
                }
            });
            trans.setOutputProperty("encoding", "UTF-8");
            trans.setOutputProperty("indent", "no");
            trans.setOutputProperty("standalone", "yes");
            trans.transform(xmlSource, outputTarget);
        }
        catch (final TransformerException e) {
            return false;
        }
        return true;
    }
    
    public static boolean copyStream(final InputStream inStream, final OutputStream outStream) {
        try {
            IOUtils.copy(inStream, outStream);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
}
