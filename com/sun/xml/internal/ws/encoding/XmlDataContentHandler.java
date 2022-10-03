package com.sun.xml.internal.ws.encoding;

import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.OutputStreamWriter;
import javax.xml.transform.Source;
import java.io.OutputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.activation.DataSource;
import java.util.Arrays;
import javax.activation.ActivationDataFlavor;
import javax.xml.transform.stream.StreamSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.DataContentHandler;

public class XmlDataContentHandler implements DataContentHandler
{
    private final DataFlavor[] flavors;
    
    public XmlDataContentHandler() throws ClassNotFoundException {
        (this.flavors = new DataFlavor[3])[0] = new ActivationDataFlavor(StreamSource.class, "text/xml", "XML");
        this.flavors[1] = new ActivationDataFlavor(StreamSource.class, "application/xml", "XML");
        this.flavors[2] = new ActivationDataFlavor(String.class, "text/xml", "XML String");
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return Arrays.copyOf(this.flavors, this.flavors.length);
    }
    
    @Override
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws IOException {
        for (final DataFlavor aFlavor : this.flavors) {
            if (aFlavor.equals(df)) {
                return this.getContent(ds);
            }
        }
        return null;
    }
    
    @Override
    public Object getContent(final DataSource ds) throws IOException {
        final String ctStr = ds.getContentType();
        String charset = null;
        if (ctStr != null) {
            final ContentType ct = new ContentType(ctStr);
            if (!this.isXml(ct)) {
                throw new IOException("Cannot convert DataSource with content type \"" + ctStr + "\" to object in XmlDataContentHandler");
            }
            charset = ct.getParameter("charset");
        }
        return (charset != null) ? new StreamSource(new InputStreamReader(ds.getInputStream()), charset) : new StreamSource(ds.getInputStream());
    }
    
    @Override
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (!(obj instanceof DataSource) && !(obj instanceof Source) && !(obj instanceof String)) {
            throw new IOException("Invalid Object type = " + obj.getClass() + ". XmlDataContentHandler can only convert DataSource|Source|String to XML.");
        }
        final ContentType ct = new ContentType(mimeType);
        if (!this.isXml(ct)) {
            throw new IOException("Invalid content type \"" + mimeType + "\" for XmlDataContentHandler");
        }
        String charset = ct.getParameter("charset");
        if (obj instanceof String) {
            final String s = (String)obj;
            if (charset == null) {
                charset = "utf-8";
            }
            final OutputStreamWriter osw = new OutputStreamWriter(os, charset);
            osw.write(s, 0, s.length());
            osw.flush();
            return;
        }
        final Source source = (Source)((obj instanceof DataSource) ? this.getContent((DataSource)obj) : ((Source)obj));
        try {
            final Transformer transformer = XmlUtil.newTransformer();
            if (charset != null) {
                transformer.setOutputProperty("encoding", charset);
            }
            final StreamResult result = new StreamResult(os);
            transformer.transform(source, result);
        }
        catch (final Exception ex) {
            throw new IOException("Unable to run the JAXP transformer in XmlDataContentHandler " + ex.getMessage());
        }
    }
    
    private boolean isXml(final ContentType ct) {
        return ct.getSubType().equals("xml") && (ct.getPrimaryType().equals("text") || ct.getPrimaryType().equals("application"));
    }
}
