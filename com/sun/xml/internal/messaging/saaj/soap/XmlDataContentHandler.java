package com.sun.xml.internal.messaging.saaj.soap;

import javax.xml.transform.Transformer;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;
import java.awt.datatransfer.DataFlavor;
import javax.activation.DataContentHandler;

public class XmlDataContentHandler implements DataContentHandler
{
    public static final String STR_SRC = "javax.xml.transform.stream.StreamSource";
    private static Class streamSourceClass;
    
    public XmlDataContentHandler() throws ClassNotFoundException {
        if (XmlDataContentHandler.streamSourceClass == null) {
            XmlDataContentHandler.streamSourceClass = Class.forName("javax.xml.transform.stream.StreamSource");
        }
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        final DataFlavor[] flavors = { new ActivationDataFlavor(XmlDataContentHandler.streamSourceClass, "text/xml", "XML"), new ActivationDataFlavor(XmlDataContentHandler.streamSourceClass, "application/xml", "XML") };
        return flavors;
    }
    
    @Override
    public Object getTransferData(final DataFlavor flavor, final DataSource dataSource) throws IOException {
        if ((flavor.getMimeType().startsWith("text/xml") || flavor.getMimeType().startsWith("application/xml")) && flavor.getRepresentationClass().getName().equals("javax.xml.transform.stream.StreamSource")) {
            return new StreamSource(dataSource.getInputStream());
        }
        return null;
    }
    
    @Override
    public Object getContent(final DataSource dataSource) throws IOException {
        return new StreamSource(dataSource.getInputStream());
    }
    
    @Override
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (!mimeType.startsWith("text/xml") && !mimeType.startsWith("application/xml")) {
            throw new IOException("Invalid content type \"" + mimeType + "\" for XmlDCH");
        }
        try {
            final Transformer transformer = EfficientStreamingTransformer.newTransformer();
            final StreamResult result = new StreamResult(os);
            if (obj instanceof DataSource) {
                transformer.transform((Source)this.getContent((DataSource)obj), result);
            }
            else {
                Source src = null;
                if (obj instanceof String) {
                    src = new StreamSource(new StringReader((String)obj));
                }
                else {
                    src = (Source)obj;
                }
                transformer.transform(src, result);
            }
        }
        catch (final Exception ex) {
            throw new IOException("Unable to run the JAXP transformer on a stream " + ex.getMessage());
        }
    }
    
    static {
        XmlDataContentHandler.streamSourceClass = null;
    }
}
