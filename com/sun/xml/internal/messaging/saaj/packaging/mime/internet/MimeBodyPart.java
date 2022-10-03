package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import java.io.OutputStream;
import javax.activation.DataSource;
import java.io.UnsupportedEncodingException;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.InputStream;
import javax.activation.DataHandler;

public final class MimeBodyPart
{
    public static final String ATTACHMENT = "attachment";
    public static final String INLINE = "inline";
    private static boolean setDefaultTextCharset;
    private DataHandler dh;
    private byte[] content;
    private int contentLength;
    private int start;
    private InputStream contentStream;
    private final InternetHeaders headers;
    private MimeMultipart parent;
    private MIMEPart mimePart;
    
    public MimeBodyPart() {
        this.start = 0;
        this.headers = new InternetHeaders();
    }
    
    public MimeBodyPart(InputStream is) throws MessagingException {
        this.start = 0;
        if (!(is instanceof ByteArrayInputStream) && !(is instanceof BufferedInputStream) && !(is instanceof SharedInputStream)) {
            is = new BufferedInputStream(is);
        }
        this.headers = new InternetHeaders(is);
        if (is instanceof SharedInputStream) {
            final SharedInputStream sis = (SharedInputStream)is;
            this.contentStream = sis.newStream(sis.getPosition(), -1L);
        }
        else {
            try {
                final ByteOutputStream bos = new ByteOutputStream();
                bos.write(is);
                this.content = bos.getBytes();
                this.contentLength = bos.getCount();
            }
            catch (final IOException ioex) {
                throw new MessagingException("Error reading input stream", ioex);
            }
        }
    }
    
    public MimeBodyPart(final InternetHeaders headers, final byte[] content, final int len) {
        this.start = 0;
        this.headers = headers;
        this.content = content;
        this.contentLength = len;
    }
    
    public MimeBodyPart(final InternetHeaders headers, final byte[] content, final int start, final int len) {
        this.start = 0;
        this.headers = headers;
        this.content = content;
        this.start = start;
        this.contentLength = len;
    }
    
    public MimeBodyPart(final MIMEPart part) {
        this.start = 0;
        this.mimePart = part;
        this.headers = new InternetHeaders();
        final List<? extends Header> hdrs = this.mimePart.getAllHeaders();
        for (final Header hd : hdrs) {
            this.headers.addHeader(hd.getName(), hd.getValue());
        }
    }
    
    public MimeMultipart getParent() {
        return this.parent;
    }
    
    public void setParent(final MimeMultipart parent) {
        this.parent = parent;
    }
    
    public int getSize() {
        if (this.mimePart != null) {
            try {
                return this.mimePart.read().available();
            }
            catch (final IOException ex) {
                return -1;
            }
        }
        if (this.content != null) {
            return this.contentLength;
        }
        if (this.contentStream != null) {
            try {
                final int size = this.contentStream.available();
                if (size > 0) {
                    return size;
                }
            }
            catch (final IOException ex2) {}
        }
        return -1;
    }
    
    public int getLineCount() {
        return -1;
    }
    
    public String getContentType() {
        if (this.mimePart != null) {
            return this.mimePart.getContentType();
        }
        String s = this.getHeader("Content-Type", null);
        if (s == null) {
            s = "text/plain";
        }
        return s;
    }
    
    public boolean isMimeType(final String mimeType) {
        boolean result;
        try {
            final ContentType ct = new ContentType(this.getContentType());
            result = ct.match(mimeType);
        }
        catch (final ParseException ex) {
            result = this.getContentType().equalsIgnoreCase(mimeType);
        }
        return result;
    }
    
    public String getDisposition() throws MessagingException {
        final String s = this.getHeader("Content-Disposition", null);
        if (s == null) {
            return null;
        }
        final ContentDisposition cd = new ContentDisposition(s);
        return cd.getDisposition();
    }
    
    public void setDisposition(String disposition) throws MessagingException {
        if (disposition == null) {
            this.removeHeader("Content-Disposition");
        }
        else {
            final String s = this.getHeader("Content-Disposition", null);
            if (s != null) {
                final ContentDisposition cd = new ContentDisposition(s);
                cd.setDisposition(disposition);
                disposition = cd.toString();
            }
            this.setHeader("Content-Disposition", disposition);
        }
    }
    
    public String getEncoding() throws MessagingException {
        String s = this.getHeader("Content-Transfer-Encoding", null);
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.equalsIgnoreCase("7bit") || s.equalsIgnoreCase("8bit") || s.equalsIgnoreCase("quoted-printable") || s.equalsIgnoreCase("base64")) {
            return s;
        }
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        int tkType;
        HeaderTokenizer.Token tk;
        do {
            tk = h.next();
            tkType = tk.getType();
            if (tkType == -4) {
                return s;
            }
        } while (tkType != -1);
        return tk.getValue();
    }
    
    public String getContentID() {
        return this.getHeader("Content-ID", null);
    }
    
    public void setContentID(final String cid) {
        if (cid == null) {
            this.removeHeader("Content-ID");
        }
        else {
            this.setHeader("Content-ID", cid);
        }
    }
    
    public String getContentMD5() {
        return this.getHeader("Content-MD5", null);
    }
    
    public void setContentMD5(final String md5) {
        this.setHeader("Content-MD5", md5);
    }
    
    public String[] getContentLanguage() throws MessagingException {
        final String s = this.getHeader("Content-Language", null);
        if (s == null) {
            return null;
        }
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        final FinalArrayList v = new FinalArrayList();
        while (true) {
            final HeaderTokenizer.Token tk = h.next();
            final int tkType = tk.getType();
            if (tkType == -4) {
                break;
            }
            if (tkType != -1) {
                continue;
            }
            v.add(tk.getValue());
        }
        if (v.size() == 0) {
            return null;
        }
        return v.toArray(new String[v.size()]);
    }
    
    public void setContentLanguage(final String[] languages) {
        final StringBuffer sb = new StringBuffer(languages[0]);
        for (int i = 1; i < languages.length; ++i) {
            sb.append(',').append(languages[i]);
        }
        this.setHeader("Content-Language", sb.toString());
    }
    
    public String getDescription() {
        final String rawvalue = this.getHeader("Content-Description", null);
        if (rawvalue == null) {
            return null;
        }
        try {
            return MimeUtility.decodeText(MimeUtility.unfold(rawvalue));
        }
        catch (final UnsupportedEncodingException ex) {
            return rawvalue;
        }
    }
    
    public void setDescription(final String description) throws MessagingException {
        this.setDescription(description, null);
    }
    
    public void setDescription(final String description, final String charset) throws MessagingException {
        if (description == null) {
            this.removeHeader("Content-Description");
            return;
        }
        try {
            this.setHeader("Content-Description", MimeUtility.fold(21, MimeUtility.encodeText(description, charset, null)));
        }
        catch (final UnsupportedEncodingException uex) {
            throw new MessagingException("Encoding error", uex);
        }
    }
    
    public String getFileName() throws MessagingException {
        String filename = null;
        String s = this.getHeader("Content-Disposition", null);
        if (s != null) {
            final ContentDisposition cd = new ContentDisposition(s);
            filename = cd.getParameter("filename");
        }
        if (filename == null) {
            s = this.getHeader("Content-Type", null);
            if (s != null) {
                try {
                    final ContentType ct = new ContentType(s);
                    filename = ct.getParameter("name");
                }
                catch (final ParseException ex) {}
            }
        }
        return filename;
    }
    
    public void setFileName(final String filename) throws MessagingException {
        String s = this.getHeader("Content-Disposition", null);
        final ContentDisposition cd = new ContentDisposition((s == null) ? "attachment" : s);
        cd.setParameter("filename", filename);
        this.setHeader("Content-Disposition", cd.toString());
        s = this.getHeader("Content-Type", null);
        if (s != null) {
            try {
                final ContentType cType = new ContentType(s);
                cType.setParameter("name", filename);
                this.setHeader("Content-Type", cType.toString());
            }
            catch (final ParseException ex) {}
        }
    }
    
    public InputStream getInputStream() throws IOException {
        return this.getDataHandler().getInputStream();
    }
    
    InputStream getContentStream() throws MessagingException {
        if (this.mimePart != null) {
            return this.mimePart.read();
        }
        if (this.contentStream != null) {
            return ((SharedInputStream)this.contentStream).newStream(0L, -1L);
        }
        if (this.content != null) {
            return new ByteArrayInputStream(this.content, this.start, this.contentLength);
        }
        throw new MessagingException("No content");
    }
    
    public InputStream getRawInputStream() throws MessagingException {
        return this.getContentStream();
    }
    
    public DataHandler getDataHandler() {
        if (this.mimePart != null) {
            return new DataHandler(new DataSource() {
                @Override
                public InputStream getInputStream() throws IOException {
                    return MimeBodyPart.this.mimePart.read();
                }
                
                @Override
                public OutputStream getOutputStream() throws IOException {
                    throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option");
                }
                
                @Override
                public String getContentType() {
                    return MimeBodyPart.this.mimePart.getContentType();
                }
                
                @Override
                public String getName() {
                    return "MIMEPart Wrapped DataSource";
                }
            });
        }
        if (this.dh == null) {
            this.dh = new DataHandler(new MimePartDataSource(this));
        }
        return this.dh;
    }
    
    public Object getContent() throws IOException {
        return this.getDataHandler().getContent();
    }
    
    public void setDataHandler(final DataHandler dh) {
        if (this.mimePart != null) {
            this.mimePart = null;
        }
        this.dh = dh;
        this.content = null;
        this.contentStream = null;
        this.removeHeader("Content-Type");
        this.removeHeader("Content-Transfer-Encoding");
    }
    
    public void setContent(final Object o, final String type) {
        if (this.mimePart != null) {
            this.mimePart = null;
        }
        if (o instanceof MimeMultipart) {
            this.setContent((MimeMultipart)o);
        }
        else {
            this.setDataHandler(new DataHandler(o, type));
        }
    }
    
    public void setText(final String text) {
        this.setText(text, null);
    }
    
    public void setText(final String text, String charset) {
        if (charset == null) {
            if (MimeUtility.checkAscii(text) != 1) {
                charset = MimeUtility.getDefaultMIMECharset();
            }
            else {
                charset = "us-ascii";
            }
        }
        this.setContent(text, "text/plain; charset=" + MimeUtility.quote(charset, "()<>@,;:\\\"\t []/?="));
    }
    
    public void setContent(final MimeMultipart mp) {
        if (this.mimePart != null) {
            this.mimePart = null;
        }
        this.setDataHandler(new DataHandler(mp, mp.getContentType().toString()));
        mp.setParent(this);
    }
    
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        final List hdrLines = this.headers.getAllHeaderLines();
        for (int sz = hdrLines.size(), i = 0; i < sz; ++i) {
            OutputUtil.writeln(hdrLines.get(i), os);
        }
        OutputUtil.writeln(os);
        if (this.contentStream != null) {
            ((SharedInputStream)this.contentStream).writeTo(0L, -1L, os);
        }
        else if (this.content != null) {
            os.write(this.content, this.start, this.contentLength);
        }
        else if (this.dh != null) {
            final OutputStream wos = MimeUtility.encode(os, this.getEncoding());
            this.getDataHandler().writeTo(wos);
            if (os != wos) {
                wos.flush();
            }
        }
        else {
            if (this.mimePart == null) {
                throw new MessagingException("no content");
            }
            final OutputStream wos = MimeUtility.encode(os, this.getEncoding());
            this.getDataHandler().writeTo(wos);
            if (os != wos) {
                wos.flush();
            }
        }
    }
    
    public String[] getHeader(final String name) {
        return this.headers.getHeader(name);
    }
    
    public String getHeader(final String name, final String delimiter) {
        return this.headers.getHeader(name, delimiter);
    }
    
    public void setHeader(final String name, final String value) {
        this.headers.setHeader(name, value);
    }
    
    public void addHeader(final String name, final String value) {
        this.headers.addHeader(name, value);
    }
    
    public void removeHeader(final String name) {
        this.headers.removeHeader(name);
    }
    
    public FinalArrayList getAllHeaders() {
        return this.headers.getAllHeaders();
    }
    
    public void addHeaderLine(final String line) {
        this.headers.addHeaderLine(line);
    }
    
    protected void updateHeaders() throws MessagingException {
        final DataHandler dh = this.getDataHandler();
        if (dh == null) {
            return;
        }
        try {
            String type = dh.getContentType();
            boolean composite = false;
            final boolean needCTHeader = this.getHeader("Content-Type") == null;
            final ContentType cType = new ContentType(type);
            if (cType.match("multipart/*")) {
                composite = true;
                final Object o = dh.getContent();
                ((MimeMultipart)o).updateHeaders();
            }
            else if (cType.match("message/rfc822")) {
                composite = true;
            }
            if (!composite) {
                if (this.getHeader("Content-Transfer-Encoding") == null) {
                    this.setEncoding(MimeUtility.getEncoding(dh));
                }
                if (needCTHeader && MimeBodyPart.setDefaultTextCharset && cType.match("text/*") && cType.getParameter("charset") == null) {
                    final String enc = this.getEncoding();
                    String charset;
                    if (enc != null && enc.equalsIgnoreCase("7bit")) {
                        charset = "us-ascii";
                    }
                    else {
                        charset = MimeUtility.getDefaultMIMECharset();
                    }
                    cType.setParameter("charset", charset);
                    type = cType.toString();
                }
            }
            if (needCTHeader) {
                final String s = this.getHeader("Content-Disposition", null);
                if (s != null) {
                    final ContentDisposition cd = new ContentDisposition(s);
                    final String filename = cd.getParameter("filename");
                    if (filename != null) {
                        cType.setParameter("name", filename);
                        type = cType.toString();
                    }
                }
                this.setHeader("Content-Type", type);
            }
        }
        catch (final IOException ex) {
            throw new MessagingException("IOException updating headers", ex);
        }
    }
    
    private void setEncoding(final String encoding) {
        this.setHeader("Content-Transfer-Encoding", encoding);
    }
    
    static {
        MimeBodyPart.setDefaultTextCharset = true;
        try {
            final String s = System.getProperty("mail.mime.setdefaulttextcharset");
            MimeBodyPart.setDefaultTextCharset = (s == null || !s.equalsIgnoreCase("false"));
        }
        catch (final SecurityException ex) {}
    }
}
