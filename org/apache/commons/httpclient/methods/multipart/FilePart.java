package org.apache.commons.httpclient.methods.multipart;

import org.apache.commons.logging.LogFactory;
import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import org.apache.commons.logging.Log;

public class FilePart extends PartBase
{
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static final String DEFAULT_TRANSFER_ENCODING = "binary";
    private static final Log LOG;
    protected static final String FILE_NAME = "; filename=";
    private static final byte[] FILE_NAME_BYTES;
    private PartSource source;
    
    public FilePart(final String name, final PartSource partSource, final String contentType, final String charset) {
        super(name, (contentType == null) ? "application/octet-stream" : contentType, (charset == null) ? "ISO-8859-1" : charset, "binary");
        if (partSource == null) {
            throw new IllegalArgumentException("Source may not be null");
        }
        this.source = partSource;
    }
    
    public FilePart(final String name, final PartSource partSource) {
        this(name, partSource, null, null);
    }
    
    public FilePart(final String name, final File file) throws FileNotFoundException {
        this(name, new FilePartSource(file), null, null);
    }
    
    public FilePart(final String name, final File file, final String contentType, final String charset) throws FileNotFoundException {
        this(name, new FilePartSource(file), contentType, charset);
    }
    
    public FilePart(final String name, final String fileName, final File file) throws FileNotFoundException {
        this(name, new FilePartSource(fileName, file), null, null);
    }
    
    public FilePart(final String name, final String fileName, final File file, final String contentType, final String charset) throws FileNotFoundException {
        this(name, new FilePartSource(fileName, file), contentType, charset);
    }
    
    protected void sendDispositionHeader(final OutputStream out) throws IOException {
        FilePart.LOG.trace((Object)"enter sendDispositionHeader(OutputStream out)");
        super.sendDispositionHeader(out);
        final String filename = this.source.getFileName();
        if (filename != null) {
            out.write(FilePart.FILE_NAME_BYTES);
            out.write(FilePart.QUOTE_BYTES);
            out.write(EncodingUtil.getAsciiBytes(filename));
            out.write(FilePart.QUOTE_BYTES);
        }
    }
    
    protected void sendData(final OutputStream out) throws IOException {
        FilePart.LOG.trace((Object)"enter sendData(OutputStream out)");
        if (this.lengthOfData() == 0L) {
            FilePart.LOG.debug((Object)"No data to send.");
            return;
        }
        final byte[] tmp = new byte[4096];
        final InputStream instream = this.source.createInputStream();
        try {
            int len;
            while ((len = instream.read(tmp)) >= 0) {
                out.write(tmp, 0, len);
            }
        }
        finally {
            instream.close();
        }
    }
    
    protected PartSource getSource() {
        FilePart.LOG.trace((Object)"enter getSource()");
        return this.source;
    }
    
    protected long lengthOfData() throws IOException {
        FilePart.LOG.trace((Object)"enter lengthOfData()");
        return this.source.getLength();
    }
    
    static {
        LOG = LogFactory.getLog(FilePart.class);
        FILE_NAME_BYTES = EncodingUtil.getAsciiBytes("; filename=");
    }
}
