package org.apache.axiom.om.ds;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import java.util.Iterator;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMOutputFormat;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.OMDataSourceExt;

public abstract class OMDataSourceExtBase implements OMDataSourceExt
{
    private static final Log log;
    HashMap map;
    
    public OMDataSourceExtBase() {
        this.map = null;
    }
    
    public Object getProperty(final String key) {
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }
    
    public Object setProperty(final String key, final Object value) {
        if (this.map == null) {
            this.map = new HashMap();
        }
        return this.map.put(key, value);
    }
    
    public boolean hasProperty(final String key) {
        return this.map != null && this.map.containsKey(key);
    }
    
    public InputStream getXMLInputStream(final String encoding) throws UnsupportedEncodingException {
        if (OMDataSourceExtBase.log.isDebugEnabled()) {
            OMDataSourceExtBase.log.debug((Object)("getXMLInputStream encoding=" + encoding));
        }
        return new ByteArrayInputStream(this.getXMLBytes(encoding));
    }
    
    public void serialize(final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        if (OMDataSourceExtBase.log.isDebugEnabled()) {
            OMDataSourceExtBase.log.debug((Object)("serialize output=" + output + " format=" + format));
        }
        try {
            output.write(this.getXMLBytes(format.getCharSetEncoding()));
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void serialize(final Writer writer, final OMOutputFormat format) throws XMLStreamException {
        if (OMDataSourceExtBase.log.isDebugEnabled()) {
            OMDataSourceExtBase.log.debug((Object)("serialize writer=" + writer + " format=" + format));
        }
        try {
            final String text = new String(this.getXMLBytes(format.getCharSetEncoding()));
            writer.write(text);
        }
        catch (final UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
        catch (final IOException e2) {
            throw new XMLStreamException(e2);
        }
    }
    
    public void serialize(final XMLStreamWriter xmlWriter) throws XMLStreamException {
        if (OMDataSourceExtBase.log.isDebugEnabled()) {
            OMDataSourceExtBase.log.debug((Object)("serialize xmlWriter=" + xmlWriter));
        }
        final OutputStream os = getOutputStream(xmlWriter);
        if (os != null) {
            if (OMDataSourceExtBase.log.isDebugEnabled()) {
                OMDataSourceExtBase.log.debug((Object)"serialize OutputStream optimisation: true");
            }
            final String encoding = getCharacterEncoding(xmlWriter);
            final OMOutputFormat format = new OMOutputFormat();
            format.setCharSetEncoding(encoding);
            this.serialize(os, format);
        }
        else {
            if (OMDataSourceExtBase.log.isDebugEnabled()) {
                OMDataSourceExtBase.log.debug((Object)"serialize OutputStream optimisation: false");
            }
            final XMLStreamReader xmlReader = this.getReader();
            reader2writer(xmlReader, xmlWriter);
        }
    }
    
    private static void reader2writer(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        final StAXOMBuilder builder = new StAXOMBuilder(reader);
        try {
            final OMDocument omDocument = builder.getDocument();
            final Iterator it = omDocument.getChildren();
            while (it.hasNext()) {
                final OMNode omNode = it.next();
                omNode.getNextOMSibling();
                omNode.serializeAndConsume(writer);
            }
        }
        finally {
            builder.close();
        }
    }
    
    private static OutputStream getOutputStream(final XMLStreamWriter writer) throws XMLStreamException {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter)writer).getOutputStream();
        }
        return null;
    }
    
    private static String getCharacterEncoding(final XMLStreamWriter writer) {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter)writer).getCharSetEncoding();
        }
        return null;
    }
    
    static {
        log = LogFactory.getLog((Class)OMDataSourceExtBase.class);
    }
}
