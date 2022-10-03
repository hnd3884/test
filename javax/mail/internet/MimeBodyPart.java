package javax.mail.internet;

import javax.mail.EncodingAware;
import com.sun.mail.util.LineOutputStream;
import com.sun.mail.util.PropUtil;
import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import javax.mail.Header;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import javax.mail.Part;
import javax.mail.Message;
import javax.mail.Multipart;
import com.sun.mail.util.MessageRemovedIOException;
import javax.mail.MessageRemovedException;
import com.sun.mail.util.FolderClosedIOException;
import javax.mail.FolderClosedException;
import com.sun.mail.util.MimeUtil;
import java.io.IOException;
import javax.mail.MessagingException;
import com.sun.mail.util.ASCIIUtility;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.mail.BodyPart;

public class MimeBodyPart extends BodyPart implements MimePart
{
    private static final boolean setDefaultTextCharset;
    private static final boolean setContentTypeFileName;
    private static final boolean encodeFileName;
    private static final boolean decodeFileName;
    private static final boolean ignoreMultipartEncoding;
    private static final boolean allowutf8;
    static final boolean cacheMultipart;
    protected DataHandler dh;
    protected byte[] content;
    protected InputStream contentStream;
    protected InternetHeaders headers;
    protected Object cachedContent;
    
    public MimeBodyPart() {
        this.headers = new InternetHeaders();
    }
    
    public MimeBodyPart(InputStream is) throws MessagingException {
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
                this.content = ASCIIUtility.getBytes(is);
            }
            catch (final IOException ioex) {
                throw new MessagingException("Error reading input stream", ioex);
            }
        }
    }
    
    public MimeBodyPart(final InternetHeaders headers, final byte[] content) throws MessagingException {
        this.headers = headers;
        this.content = content;
    }
    
    @Override
    public int getSize() throws MessagingException {
        if (this.content != null) {
            return this.content.length;
        }
        if (this.contentStream != null) {
            try {
                final int size = this.contentStream.available();
                if (size > 0) {
                    return size;
                }
            }
            catch (final IOException ex) {}
        }
        return -1;
    }
    
    @Override
    public int getLineCount() throws MessagingException {
        return -1;
    }
    
    @Override
    public String getContentType() throws MessagingException {
        String s = this.getHeader("Content-Type", null);
        s = MimeUtil.cleanContentType(this, s);
        if (s == null) {
            s = "text/plain";
        }
        return s;
    }
    
    @Override
    public boolean isMimeType(final String mimeType) throws MessagingException {
        return isMimeType(this, mimeType);
    }
    
    @Override
    public String getDisposition() throws MessagingException {
        return getDisposition(this);
    }
    
    @Override
    public void setDisposition(final String disposition) throws MessagingException {
        setDisposition(this, disposition);
    }
    
    @Override
    public String getEncoding() throws MessagingException {
        return getEncoding(this);
    }
    
    @Override
    public String getContentID() throws MessagingException {
        return this.getHeader("Content-Id", null);
    }
    
    public void setContentID(final String cid) throws MessagingException {
        if (cid == null) {
            this.removeHeader("Content-ID");
        }
        else {
            this.setHeader("Content-ID", cid);
        }
    }
    
    @Override
    public String getContentMD5() throws MessagingException {
        return this.getHeader("Content-MD5", null);
    }
    
    @Override
    public void setContentMD5(final String md5) throws MessagingException {
        this.setHeader("Content-MD5", md5);
    }
    
    @Override
    public String[] getContentLanguage() throws MessagingException {
        return getContentLanguage(this);
    }
    
    @Override
    public void setContentLanguage(final String[] languages) throws MessagingException {
        setContentLanguage(this, languages);
    }
    
    @Override
    public String getDescription() throws MessagingException {
        return getDescription(this);
    }
    
    @Override
    public void setDescription(final String description) throws MessagingException {
        this.setDescription(description, null);
    }
    
    public void setDescription(final String description, final String charset) throws MessagingException {
        setDescription(this, description, charset);
    }
    
    @Override
    public String getFileName() throws MessagingException {
        return getFileName(this);
    }
    
    @Override
    public void setFileName(final String filename) throws MessagingException {
        setFileName(this, filename);
    }
    
    @Override
    public InputStream getInputStream() throws IOException, MessagingException {
        return this.getDataHandler().getInputStream();
    }
    
    protected InputStream getContentStream() throws MessagingException {
        if (this.contentStream != null) {
            return ((SharedInputStream)this.contentStream).newStream(0L, -1L);
        }
        if (this.content != null) {
            return new ByteArrayInputStream(this.content);
        }
        throw new MessagingException("No MimeBodyPart content");
    }
    
    public InputStream getRawInputStream() throws MessagingException {
        return this.getContentStream();
    }
    
    @Override
    public DataHandler getDataHandler() throws MessagingException {
        if (this.dh == null) {
            this.dh = new MimePartDataHandler(this);
        }
        return this.dh;
    }
    
    @Override
    public Object getContent() throws IOException, MessagingException {
        if (this.cachedContent != null) {
            return this.cachedContent;
        }
        Object c;
        try {
            c = this.getDataHandler().getContent();
        }
        catch (final FolderClosedIOException fex) {
            throw new FolderClosedException(fex.getFolder(), fex.getMessage());
        }
        catch (final MessageRemovedIOException mex) {
            throw new MessageRemovedException(mex.getMessage());
        }
        if (MimeBodyPart.cacheMultipart && (c instanceof Multipart || c instanceof Message) && (this.content != null || this.contentStream != null)) {
            this.cachedContent = c;
            if (c instanceof MimeMultipart) {
                ((MimeMultipart)c).parse();
            }
        }
        return c;
    }
    
    @Override
    public void setDataHandler(final DataHandler dh) throws MessagingException {
        this.dh = dh;
        this.cachedContent = null;
        invalidateContentHeaders(this);
    }
    
    @Override
    public void setContent(final Object o, final String type) throws MessagingException {
        if (o instanceof Multipart) {
            this.setContent((Multipart)o);
        }
        else {
            this.setDataHandler(new DataHandler(o, type));
        }
    }
    
    @Override
    public void setText(final String text) throws MessagingException {
        this.setText(text, null);
    }
    
    @Override
    public void setText(final String text, final String charset) throws MessagingException {
        setText(this, text, charset, "plain");
    }
    
    @Override
    public void setText(final String text, final String charset, final String subtype) throws MessagingException {
        setText(this, text, charset, subtype);
    }
    
    @Override
    public void setContent(final Multipart mp) throws MessagingException {
        this.setDataHandler(new DataHandler(mp, mp.getContentType()));
        mp.setParent(this);
    }
    
    public void attachFile(final File file) throws IOException, MessagingException {
        final FileDataSource fds = new FileDataSource(file);
        this.setDataHandler(new DataHandler(fds));
        this.setFileName(fds.getName());
        this.setDisposition("attachment");
    }
    
    public void attachFile(final String file) throws IOException, MessagingException {
        final File f = new File(file);
        this.attachFile(f);
    }
    
    public void attachFile(final File file, final String contentType, final String encoding) throws IOException, MessagingException {
        final DataSource fds = new EncodedFileDataSource(file, contentType, encoding);
        this.setDataHandler(new DataHandler(fds));
        this.setFileName(fds.getName());
        this.setDisposition("attachment");
    }
    
    public void attachFile(final String file, final String contentType, final String encoding) throws IOException, MessagingException {
        this.attachFile(new File(file), contentType, encoding);
    }
    
    public void saveFile(final File file) throws IOException, MessagingException {
        OutputStream out = null;
        InputStream in = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            in = this.getInputStream();
            final byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException ex) {}
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (final IOException ex2) {}
        }
    }
    
    public void saveFile(final String file) throws IOException, MessagingException {
        final File f = new File(file);
        this.saveFile(f);
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        writeTo(this, os, null);
    }
    
    @Override
    public String[] getHeader(final String name) throws MessagingException {
        return this.headers.getHeader(name);
    }
    
    @Override
    public String getHeader(final String name, final String delimiter) throws MessagingException {
        return this.headers.getHeader(name, delimiter);
    }
    
    @Override
    public void setHeader(final String name, final String value) throws MessagingException {
        this.headers.setHeader(name, value);
    }
    
    @Override
    public void addHeader(final String name, final String value) throws MessagingException {
        this.headers.addHeader(name, value);
    }
    
    @Override
    public void removeHeader(final String name) throws MessagingException {
        this.headers.removeHeader(name);
    }
    
    @Override
    public Enumeration<Header> getAllHeaders() throws MessagingException {
        return this.headers.getAllHeaders();
    }
    
    @Override
    public Enumeration<Header> getMatchingHeaders(final String[] names) throws MessagingException {
        return this.headers.getMatchingHeaders(names);
    }
    
    @Override
    public Enumeration<Header> getNonMatchingHeaders(final String[] names) throws MessagingException {
        return this.headers.getNonMatchingHeaders(names);
    }
    
    @Override
    public void addHeaderLine(final String line) throws MessagingException {
        this.headers.addHeaderLine(line);
    }
    
    @Override
    public Enumeration<String> getAllHeaderLines() throws MessagingException {
        return this.headers.getAllHeaderLines();
    }
    
    @Override
    public Enumeration<String> getMatchingHeaderLines(final String[] names) throws MessagingException {
        return this.headers.getMatchingHeaderLines(names);
    }
    
    @Override
    public Enumeration<String> getNonMatchingHeaderLines(final String[] names) throws MessagingException {
        return this.headers.getNonMatchingHeaderLines(names);
    }
    
    protected void updateHeaders() throws MessagingException {
        updateHeaders(this);
        if (this.cachedContent != null) {
            this.dh = new DataHandler(this.cachedContent, this.getContentType());
            this.cachedContent = null;
            this.content = null;
            if (this.contentStream != null) {
                try {
                    this.contentStream.close();
                }
                catch (final IOException ex) {}
            }
            this.contentStream = null;
        }
    }
    
    static boolean isMimeType(final MimePart part, final String mimeType) throws MessagingException {
        final String type = part.getContentType();
        try {
            return new ContentType(type).match(mimeType);
        }
        catch (final ParseException ex) {
            try {
                final int i = type.indexOf(59);
                if (i > 0) {
                    return new ContentType(type.substring(0, i)).match(mimeType);
                }
            }
            catch (final ParseException ex2) {}
            return type.equalsIgnoreCase(mimeType);
        }
    }
    
    static void setText(final MimePart part, final String text, String charset, final String subtype) throws MessagingException {
        if (charset == null) {
            if (MimeUtility.checkAscii(text) != 1) {
                charset = MimeUtility.getDefaultMIMECharset();
            }
            else {
                charset = "us-ascii";
            }
        }
        part.setContent(text, "text/" + subtype + "; charset=" + MimeUtility.quote(charset, "()<>@,;:\\\"\t []/?="));
    }
    
    static String getDisposition(final MimePart part) throws MessagingException {
        final String s = part.getHeader("Content-Disposition", null);
        if (s == null) {
            return null;
        }
        final ContentDisposition cd = new ContentDisposition(s);
        return cd.getDisposition();
    }
    
    static void setDisposition(final MimePart part, String disposition) throws MessagingException {
        if (disposition == null) {
            part.removeHeader("Content-Disposition");
        }
        else {
            final String s = part.getHeader("Content-Disposition", null);
            if (s != null) {
                final ContentDisposition cd = new ContentDisposition(s);
                cd.setDisposition(disposition);
                disposition = cd.toString();
            }
            part.setHeader("Content-Disposition", disposition);
        }
    }
    
    static String getDescription(final MimePart part) throws MessagingException {
        final String rawvalue = part.getHeader("Content-Description", null);
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
    
    static void setDescription(final MimePart part, final String description, final String charset) throws MessagingException {
        if (description == null) {
            part.removeHeader("Content-Description");
            return;
        }
        try {
            part.setHeader("Content-Description", MimeUtility.fold(21, MimeUtility.encodeText(description, charset, null)));
        }
        catch (final UnsupportedEncodingException uex) {
            throw new MessagingException("Encoding error", uex);
        }
    }
    
    static String getFileName(final MimePart part) throws MessagingException {
        String filename = null;
        String s = part.getHeader("Content-Disposition", null);
        if (s != null) {
            final ContentDisposition cd = new ContentDisposition(s);
            filename = cd.getParameter("filename");
        }
        if (filename == null) {
            s = part.getHeader("Content-Type", null);
            s = MimeUtil.cleanContentType(part, s);
            if (s != null) {
                try {
                    final ContentType ct = new ContentType(s);
                    filename = ct.getParameter("name");
                }
                catch (final ParseException ex2) {}
            }
        }
        if (MimeBodyPart.decodeFileName && filename != null) {
            try {
                filename = MimeUtility.decodeText(filename);
            }
            catch (final UnsupportedEncodingException ex) {
                throw new MessagingException("Can't decode filename", ex);
            }
        }
        return filename;
    }
    
    static void setFileName(final MimePart part, String name) throws MessagingException {
        if (MimeBodyPart.encodeFileName && name != null) {
            try {
                name = MimeUtility.encodeText(name);
            }
            catch (final UnsupportedEncodingException ex) {
                throw new MessagingException("Can't encode filename", ex);
            }
        }
        String s = part.getHeader("Content-Disposition", null);
        final ContentDisposition cd = new ContentDisposition((s == null) ? "attachment" : s);
        final String charset = MimeUtility.getDefaultMIMECharset();
        ParameterList p = cd.getParameterList();
        if (p == null) {
            p = new ParameterList();
            cd.setParameterList(p);
        }
        if (MimeBodyPart.encodeFileName) {
            p.setLiteral("filename", name);
        }
        else {
            p.set("filename", name, charset);
        }
        part.setHeader("Content-Disposition", cd.toString());
        if (MimeBodyPart.setContentTypeFileName) {
            s = part.getHeader("Content-Type", null);
            s = MimeUtil.cleanContentType(part, s);
            if (s != null) {
                try {
                    final ContentType cType = new ContentType(s);
                    p = cType.getParameterList();
                    if (p == null) {
                        p = new ParameterList();
                        cType.setParameterList(p);
                    }
                    if (MimeBodyPart.encodeFileName) {
                        p.setLiteral("name", name);
                    }
                    else {
                        p.set("name", name, charset);
                    }
                    part.setHeader("Content-Type", cType.toString());
                }
                catch (final ParseException ex2) {}
            }
        }
    }
    
    static String[] getContentLanguage(final MimePart part) throws MessagingException {
        final String s = part.getHeader("Content-Language", null);
        if (s == null) {
            return null;
        }
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        final List<String> v = new ArrayList<String>();
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
        if (v.isEmpty()) {
            return null;
        }
        final String[] language = new String[v.size()];
        v.toArray(language);
        return language;
    }
    
    static void setContentLanguage(final MimePart part, final String[] languages) throws MessagingException {
        final StringBuilder sb = new StringBuilder(languages[0]);
        int len = "Content-Language".length() + 2 + languages[0].length();
        for (int i = 1; i < languages.length; ++i) {
            sb.append(',');
            if (++len > 76) {
                sb.append("\r\n\t");
                len = 8;
            }
            sb.append(languages[i]);
            len += languages[i].length();
        }
        part.setHeader("Content-Language", sb.toString());
    }
    
    static String getEncoding(final MimePart part) throws MessagingException {
        String s = part.getHeader("Content-Transfer-Encoding", null);
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.length() == 0) {
            return null;
        }
        if (s.equalsIgnoreCase("7bit") || s.equalsIgnoreCase("8bit") || s.equalsIgnoreCase("quoted-printable") || s.equalsIgnoreCase("binary") || s.equalsIgnoreCase("base64")) {
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
    
    static void setEncoding(final MimePart part, final String encoding) throws MessagingException {
        part.setHeader("Content-Transfer-Encoding", encoding);
    }
    
    static String restrictEncoding(final MimePart part, final String encoding) throws MessagingException {
        if (!MimeBodyPart.ignoreMultipartEncoding || encoding == null) {
            return encoding;
        }
        if (encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit") || encoding.equalsIgnoreCase("binary")) {
            return encoding;
        }
        final String type = part.getContentType();
        if (type == null) {
            return encoding;
        }
        try {
            final ContentType cType = new ContentType(type);
            if (cType.match("multipart/*")) {
                return null;
            }
            if (cType.match("message/*") && !PropUtil.getBooleanSystemProperty("mail.mime.allowencodedmessages", false)) {
                return null;
            }
        }
        catch (final ParseException ex) {}
        return encoding;
    }
    
    static void updateHeaders(final MimePart part) throws MessagingException {
        final DataHandler dh = part.getDataHandler();
        if (dh == null) {
            return;
        }
        try {
            String type = dh.getContentType();
            boolean composite = false;
            final boolean needCTHeader = part.getHeader("Content-Type") == null;
            final ContentType cType = new ContentType(type);
            if (cType.match("multipart/*")) {
                composite = true;
                Object o;
                if (part instanceof MimeBodyPart) {
                    final MimeBodyPart mbp = (MimeBodyPart)part;
                    o = ((mbp.cachedContent != null) ? mbp.cachedContent : dh.getContent());
                }
                else if (part instanceof MimeMessage) {
                    final MimeMessage msg = (MimeMessage)part;
                    o = ((msg.cachedContent != null) ? msg.cachedContent : dh.getContent());
                }
                else {
                    o = dh.getContent();
                }
                if (!(o instanceof MimeMultipart)) {
                    throw new MessagingException("MIME part of type \"" + type + "\" contains object of type " + o.getClass().getName() + " instead of MimeMultipart");
                }
                ((MimeMultipart)o).updateHeaders();
            }
            else if (cType.match("message/rfc822")) {
                composite = true;
            }
            if (dh instanceof MimePartDataHandler) {
                final MimePartDataHandler mdh = (MimePartDataHandler)dh;
                final MimePart mpart = mdh.getPart();
                if (mpart == part) {
                    return;
                }
                if (needCTHeader) {
                    part.setHeader("Content-Type", mpart.getContentType());
                }
                final String enc = mpart.getEncoding();
                if (enc != null) {
                    setEncoding(part, enc);
                    return;
                }
            }
            if (!composite) {
                if (part.getHeader("Content-Transfer-Encoding") == null) {
                    setEncoding(part, MimeUtility.getEncoding(dh));
                }
                if (needCTHeader && MimeBodyPart.setDefaultTextCharset && cType.match("text/*") && cType.getParameter("charset") == null) {
                    final String enc2 = part.getEncoding();
                    String charset;
                    if (enc2 != null && enc2.equalsIgnoreCase("7bit")) {
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
                if (MimeBodyPart.setContentTypeFileName) {
                    final String s = part.getHeader("Content-Disposition", null);
                    if (s != null) {
                        final ContentDisposition cd = new ContentDisposition(s);
                        final String filename = cd.getParameter("filename");
                        if (filename != null) {
                            ParameterList p = cType.getParameterList();
                            if (p == null) {
                                p = new ParameterList();
                                cType.setParameterList(p);
                            }
                            if (MimeBodyPart.encodeFileName) {
                                p.setLiteral("name", MimeUtility.encodeText(filename));
                            }
                            else {
                                p.set("name", filename, MimeUtility.getDefaultMIMECharset());
                            }
                            type = cType.toString();
                        }
                    }
                }
                part.setHeader("Content-Type", type);
            }
        }
        catch (final IOException ex) {
            throw new MessagingException("IOException updating headers", ex);
        }
    }
    
    static void invalidateContentHeaders(final MimePart part) throws MessagingException {
        part.removeHeader("Content-Type");
        part.removeHeader("Content-Transfer-Encoding");
    }
    
    static void writeTo(final MimePart part, OutputStream os, final String[] ignoreList) throws IOException, MessagingException {
        LineOutputStream los = null;
        if (os instanceof LineOutputStream) {
            los = (LineOutputStream)os;
        }
        else {
            los = new LineOutputStream(os, MimeBodyPart.allowutf8);
        }
        final Enumeration<String> hdrLines = part.getNonMatchingHeaderLines(ignoreList);
        while (hdrLines.hasMoreElements()) {
            los.writeln(hdrLines.nextElement());
        }
        los.writeln();
        InputStream is = null;
        byte[] buf = null;
        try {
            final DataHandler dh = part.getDataHandler();
            if (dh instanceof MimePartDataHandler) {
                final MimePartDataHandler mpdh = (MimePartDataHandler)dh;
                final MimePart mpart = mpdh.getPart();
                if (mpart.getEncoding() != null) {
                    is = mpdh.getContentStream();
                }
            }
            if (is != null) {
                buf = new byte[8192];
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
            }
            else {
                os = MimeUtility.encode(os, restrictEncoding(part, part.getEncoding()));
                part.getDataHandler().writeTo(os);
            }
        }
        finally {
            if (is != null) {
                is.close();
            }
            buf = null;
        }
        os.flush();
    }
    
    static {
        setDefaultTextCharset = PropUtil.getBooleanSystemProperty("mail.mime.setdefaulttextcharset", true);
        setContentTypeFileName = PropUtil.getBooleanSystemProperty("mail.mime.setcontenttypefilename", true);
        encodeFileName = PropUtil.getBooleanSystemProperty("mail.mime.encodefilename", false);
        decodeFileName = PropUtil.getBooleanSystemProperty("mail.mime.decodefilename", false);
        ignoreMultipartEncoding = PropUtil.getBooleanSystemProperty("mail.mime.ignoremultipartencoding", true);
        allowutf8 = PropUtil.getBooleanSystemProperty("mail.mime.allowutf8", true);
        cacheMultipart = PropUtil.getBooleanSystemProperty("mail.mime.cachemultipart", true);
    }
    
    private static class EncodedFileDataSource extends FileDataSource implements EncodingAware
    {
        private String contentType;
        private String encoding;
        
        public EncodedFileDataSource(final File file, final String contentType, final String encoding) {
            super(file);
            this.contentType = contentType;
            this.encoding = encoding;
        }
        
        @Override
        public String getContentType() {
            return (this.contentType != null) ? this.contentType : super.getContentType();
        }
        
        @Override
        public String getEncoding() {
            return this.encoding;
        }
    }
    
    static class MimePartDataHandler extends DataHandler
    {
        MimePart part;
        
        public MimePartDataHandler(final MimePart part) {
            super(new MimePartDataSource(part));
            this.part = part;
        }
        
        InputStream getContentStream() throws MessagingException {
            InputStream is = null;
            if (this.part instanceof MimeBodyPart) {
                final MimeBodyPart mbp = (MimeBodyPart)this.part;
                is = mbp.getContentStream();
            }
            else if (this.part instanceof MimeMessage) {
                final MimeMessage msg = (MimeMessage)this.part;
                is = msg.getContentStream();
            }
            return is;
        }
        
        MimePart getPart() {
            return this.part;
        }
    }
}
