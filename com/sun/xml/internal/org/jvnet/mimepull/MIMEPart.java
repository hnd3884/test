package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;
import java.util.List;
import java.io.File;
import java.util.logging.Level;
import java.io.InputStream;
import java.util.logging.Logger;

public class MIMEPart
{
    private static final Logger LOGGER;
    private volatile InternetHeaders headers;
    private volatile String contentId;
    private String contentType;
    private String contentTransferEncoding;
    volatile boolean parsed;
    final MIMEMessage msg;
    private final DataHead dataHead;
    
    MIMEPart(final MIMEMessage msg) {
        this.msg = msg;
        this.dataHead = new DataHead(this);
    }
    
    MIMEPart(final MIMEMessage msg, final String contentId) {
        this(msg);
        this.contentId = contentId;
    }
    
    public InputStream read() {
        InputStream is = null;
        try {
            is = MimeUtility.decode(this.dataHead.read(), this.contentTransferEncoding);
        }
        catch (final DecodingException ex) {
            if (MIMEPart.LOGGER.isLoggable(Level.WARNING)) {
                MIMEPart.LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return is;
    }
    
    public void close() {
        this.dataHead.close();
    }
    
    public InputStream readOnce() {
        InputStream is = null;
        try {
            is = MimeUtility.decode(this.dataHead.readOnce(), this.contentTransferEncoding);
        }
        catch (final DecodingException ex) {
            if (MIMEPart.LOGGER.isLoggable(Level.WARNING)) {
                MIMEPart.LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return is;
    }
    
    public void moveTo(final File f) {
        this.dataHead.moveTo(f);
    }
    
    public String getContentId() {
        if (this.contentId == null) {
            this.getHeaders();
        }
        return this.contentId;
    }
    
    public String getContentTransferEncoding() {
        if (this.contentTransferEncoding == null) {
            this.getHeaders();
        }
        return this.contentTransferEncoding;
    }
    
    public String getContentType() {
        if (this.contentType == null) {
            this.getHeaders();
        }
        return this.contentType;
    }
    
    private void getHeaders() {
        while (this.headers == null) {
            if (!this.msg.makeProgress() && this.headers == null) {
                throw new IllegalStateException("Internal Error. Didn't get Headers even after complete parsing.");
            }
        }
    }
    
    public List<String> getHeader(final String name) {
        this.getHeaders();
        assert this.headers != null;
        return this.headers.getHeader(name);
    }
    
    public List<? extends Header> getAllHeaders() {
        this.getHeaders();
        assert this.headers != null;
        return this.headers.getAllHeaders();
    }
    
    void setHeaders(final InternetHeaders headers) {
        this.headers = headers;
        final List<String> ct = this.getHeader("Content-Type");
        this.contentType = ((ct == null) ? "application/octet-stream" : ct.get(0));
        final List<String> cte = this.getHeader("Content-Transfer-Encoding");
        this.contentTransferEncoding = ((cte == null) ? "binary" : cte.get(0));
    }
    
    void addBody(final ByteBuffer buf) {
        this.dataHead.addBody(buf);
    }
    
    void doneParsing() {
        this.parsed = true;
        this.dataHead.doneParsing();
    }
    
    void setContentId(final String cid) {
        this.contentId = cid;
    }
    
    void setContentTransferEncoding(final String cte) {
        this.contentTransferEncoding = cte;
    }
    
    @Override
    public String toString() {
        return "Part=" + this.contentId + ":" + this.contentTransferEncoding;
    }
    
    static {
        LOGGER = Logger.getLogger(MIMEPart.class.getName());
    }
}
