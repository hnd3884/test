package org.apache.axiom.om.ds;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.OMDataSourceExt;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import org.apache.axiom.om.util.StAXUtils;
import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.logging.Log;

public class ByteArrayDataSource extends OMDataSourceExtBase
{
    private static final Log log;
    ByteArray byteArray;
    
    public ByteArrayDataSource(final byte[] bytes, final String encoding) {
        this.byteArray = null;
        this.byteArray = new ByteArray();
        this.byteArray.bytes = bytes;
        this.byteArray.encoding = encoding;
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        if (ByteArrayDataSource.log.isDebugEnabled()) {
            ByteArrayDataSource.log.debug((Object)"getReader");
        }
        return StAXUtils.createXMLStreamReader(new ByteArrayInputStream(this.byteArray.bytes), this.byteArray.encoding);
    }
    
    public Object getObject() {
        return this.byteArray;
    }
    
    public boolean isDestructiveRead() {
        return false;
    }
    
    public boolean isDestructiveWrite() {
        return false;
    }
    
    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        if (encoding == null) {
            encoding = "utf-8";
        }
        if (ByteArrayDataSource.log.isDebugEnabled()) {
            ByteArrayDataSource.log.debug((Object)("getXMLBytes encoding=" + encoding));
        }
        if (!this.byteArray.encoding.equalsIgnoreCase(encoding)) {
            final String text = new String(this.byteArray.bytes, this.byteArray.encoding);
            this.byteArray.bytes = text.getBytes(encoding);
            this.byteArray.encoding = encoding;
        }
        return this.byteArray.bytes;
    }
    
    public void close() {
        this.byteArray = null;
    }
    
    public OMDataSourceExt copy() {
        return new ByteArrayDataSource(this.byteArray.bytes, this.byteArray.encoding);
    }
    
    static {
        log = LogFactory.getLog((Class)ByteArrayDataSource.class);
    }
    
    public class ByteArray
    {
        public byte[] bytes;
        public String encoding;
    }
}
