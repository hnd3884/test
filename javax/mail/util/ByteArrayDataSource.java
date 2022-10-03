package javax.mail.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;
import javax.mail.internet.ContentType;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;

public class ByteArrayDataSource implements DataSource
{
    private byte[] data;
    private int len;
    private String type;
    private String name;
    
    public ByteArrayDataSource(final InputStream is, final String type) throws IOException {
        this.len = -1;
        this.name = "";
        final DSByteArrayOutputStream os = new DSByteArrayOutputStream();
        final byte[] buf = new byte[8192];
        int len;
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
        this.data = os.getBuf();
        this.len = os.getCount();
        if (this.data.length - this.len > 262144) {
            this.data = os.toByteArray();
            this.len = this.data.length;
        }
        this.type = type;
    }
    
    public ByteArrayDataSource(final byte[] data, final String type) {
        this.len = -1;
        this.name = "";
        this.data = data;
        this.type = type;
    }
    
    public ByteArrayDataSource(final String data, final String type) throws IOException {
        this.len = -1;
        this.name = "";
        String charset = null;
        try {
            final ContentType ct = new ContentType(type);
            charset = ct.getParameter("charset");
        }
        catch (final ParseException ex) {}
        charset = MimeUtility.javaCharset(charset);
        if (charset == null) {
            charset = MimeUtility.getDefaultJavaCharset();
        }
        this.data = data.getBytes(charset);
        this.type = type;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.data == null) {
            throw new IOException("no data");
        }
        if (this.len < 0) {
            this.len = this.data.length;
        }
        return new SharedByteArrayInputStream(this.data, 0, this.len);
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("cannot do this");
    }
    
    @Override
    public String getContentType() {
        return this.type;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    static class DSByteArrayOutputStream extends ByteArrayOutputStream
    {
        public byte[] getBuf() {
            return this.buf;
        }
        
        public int getCount() {
            return this.count;
        }
    }
}
