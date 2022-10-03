package org.apache.axiom.om.ds;

import java.io.ByteArrayInputStream;
import org.apache.axiom.om.OMDataSourceExt;
import java.io.ByteArrayOutputStream;
import org.apache.axiom.om.util.StAXUtils;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import java.io.OutputStream;
import java.io.InputStream;

public class InputStreamDataSource extends OMDataSourceExtBase
{
    Data data;
    private static final int BUFFER_LEN = 4096;
    
    public InputStreamDataSource(final InputStream is, final String encoding) {
        this.data = null;
        this.data = new Data();
        this.data.is = is;
        this.data.encoding = encoding;
    }
    
    @Override
    public void serialize(final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        final String encoding = format.getCharSetEncoding();
        try {
            if (!this.data.encoding.equalsIgnoreCase(encoding)) {
                final byte[] bytes = this.getXMLBytes(encoding);
                output.write(bytes);
            }
            else {
                inputStream2OutputStream(this.data.is, output);
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
        catch (final IOException e2) {
            throw new XMLStreamException(e2);
        }
    }
    
    @Override
    public void serialize(final XMLStreamWriter xmlWriter) throws XMLStreamException {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        super.serialize(xmlWriter);
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return StAXUtils.createXMLStreamReader(this.data.is, this.data.encoding);
    }
    
    @Override
    public InputStream getXMLInputStream(final String encoding) throws UnsupportedEncodingException {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return this.data.is;
    }
    
    public Object getObject() {
        return this.data;
    }
    
    public boolean isDestructiveRead() {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return true;
    }
    
    public boolean isDestructiveWrite() {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return true;
    }
    
    public byte[] getXMLBytes(final String encoding) throws UnsupportedEncodingException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding(encoding);
        try {
            this.serialize(baos, format);
        }
        catch (final XMLStreamException e) {
            throw new OMException(e);
        }
        return baos.toByteArray();
    }
    
    public void close() {
        if (this.data.is != null) {
            try {
                this.data.is.close();
            }
            catch (final IOException e) {
                throw new OMException(e);
            }
            this.data.is = null;
        }
    }
    
    public OMDataSourceExt copy() {
        byte[] bytes;
        try {
            bytes = this.getXMLBytes(this.data.encoding);
        }
        catch (final UnsupportedEncodingException e) {
            throw new OMException(e);
        }
        final InputStream is1 = new ByteArrayInputStream(bytes);
        final InputStream is2 = new ByteArrayInputStream(bytes);
        this.data.is = is1;
        return new InputStreamDataSource(is2, this.data.encoding);
    }
    
    private static void inputStream2OutputStream(final InputStream is, final OutputStream os) throws IOException {
        final byte[] buffer = new byte[4096];
        for (int bytesRead = is.read(buffer); bytesRead > 0; bytesRead = is.read(buffer)) {
            os.write(buffer, 0, bytesRead);
        }
    }
    
    public class Data
    {
        public String encoding;
        public InputStream is;
    }
}
