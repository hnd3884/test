package com.sun.mail.handlers;

import javax.mail.internet.ParseException;
import javax.mail.internet.ContentType;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;

public class text_xml extends text_plain
{
    private static final ActivationDataFlavor[] flavors;
    
    @Override
    protected ActivationDataFlavor[] getDataFlavors() {
        return text_xml.flavors;
    }
    
    @Override
    protected Object getData(final ActivationDataFlavor aFlavor, final DataSource ds) throws IOException {
        if (aFlavor.getRepresentationClass() == String.class) {
            return super.getContent(ds);
        }
        if (aFlavor.getRepresentationClass() == StreamSource.class) {
            return new StreamSource(ds.getInputStream());
        }
        return null;
    }
    
    @Override
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (!this.isXmlType(mimeType)) {
            throw new IOException("Invalid content type \"" + mimeType + "\" for text/xml DCH");
        }
        if (obj instanceof String) {
            super.writeTo(obj, mimeType, os);
            return;
        }
        if (!(obj instanceof DataSource) && !(obj instanceof Source)) {
            throw new IOException("Invalid Object type = " + obj.getClass() + ". XmlDCH can only convert DataSource or Source to XML.");
        }
        try {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            final StreamResult result = new StreamResult(os);
            if (obj instanceof DataSource) {
                transformer.transform(new StreamSource(((DataSource)obj).getInputStream()), result);
            }
            else {
                transformer.transform((Source)obj, result);
            }
        }
        catch (final TransformerException ex) {
            final IOException ioex = new IOException("Unable to run the JAXP transformer on a stream " + ex.getMessage());
            ioex.initCause(ex);
            throw ioex;
        }
        catch (final RuntimeException ex2) {
            final IOException ioex = new IOException("Unable to run the JAXP transformer on a stream " + ex2.getMessage());
            ioex.initCause(ex2);
            throw ioex;
        }
    }
    
    private boolean isXmlType(final String type) {
        try {
            final ContentType ct = new ContentType(type);
            return ct.getSubType().equals("xml") && (ct.getPrimaryType().equals("text") || ct.getPrimaryType().equals("application"));
        }
        catch (final ParseException ex) {
            return false;
        }
        catch (final RuntimeException ex2) {
            return false;
        }
    }
    
    static {
        flavors = new ActivationDataFlavor[] { new ActivationDataFlavor(String.class, "text/xml", "XML String"), new ActivationDataFlavor(String.class, "application/xml", "XML String"), new ActivationDataFlavor(StreamSource.class, "text/xml", "XML"), new ActivationDataFlavor(StreamSource.class, "application/xml", "XML") };
    }
}
