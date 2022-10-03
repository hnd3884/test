package org.apache.axiom.om.ds;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.axiom.om.OMException;
import java.io.ByteArrayOutputStream;
import org.apache.axiom.om.util.StAXUtils;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.OMOutputFormat;
import java.io.OutputStream;
import java.util.HashMap;
import org.apache.axiom.om.OMDataSourceExt;

public abstract class AbstractOMDataSource implements OMDataSourceExt
{
    private HashMap properties;
    
    public AbstractOMDataSource() {
        this.properties = null;
    }
    
    public final Object getProperty(final String key) {
        return (this.properties == null) ? null : this.properties.get(key);
    }
    
    public final boolean hasProperty(final String key) {
        return this.properties != null && this.properties.containsKey(key);
    }
    
    public final Object setProperty(final String key, final Object value) {
        if (this.properties == null) {
            this.properties = new HashMap();
        }
        return this.properties.put(key, value);
    }
    
    public final void serialize(final OutputStream out, final OMOutputFormat format) throws XMLStreamException {
        final XMLStreamWriter writer = new MTOMXMLStreamWriter(out, format);
        this.serialize(writer);
        writer.flush();
    }
    
    public final void serialize(final Writer writer, final OMOutputFormat format) throws XMLStreamException {
        final MTOMXMLStreamWriter xmlWriter = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer));
        xmlWriter.setOutputFormat(format);
        this.serialize(xmlWriter);
        xmlWriter.flush();
    }
    
    public final byte[] getXMLBytes(final String encoding) throws UnsupportedEncodingException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding(encoding);
        try {
            this.serialize(baos, format);
        }
        catch (final XMLStreamException ex) {
            throw new OMException(ex);
        }
        return baos.toByteArray();
    }
    
    public final InputStream getXMLInputStream(final String encoding) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(this.getXMLBytes(encoding));
    }
    
    public Object getObject() {
        return null;
    }
    
    public void close() {
    }
    
    public OMDataSourceExt copy() {
        return null;
    }
}
