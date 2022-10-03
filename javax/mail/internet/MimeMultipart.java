package javax.mail.internet;

import java.io.EOFException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import com.sun.mail.util.LineInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.LineOutputStream;
import java.io.OutputStream;
import com.sun.mail.util.PropUtil;
import javax.mail.MessageContext;
import javax.mail.MultipartDataSource;
import javax.mail.MessageAware;
import javax.mail.MessagingException;
import javax.mail.BodyPart;
import javax.activation.DataSource;
import javax.mail.Multipart;

public class MimeMultipart extends Multipart
{
    protected DataSource ds;
    protected boolean parsed;
    protected boolean complete;
    protected String preamble;
    protected boolean ignoreMissingEndBoundary;
    protected boolean ignoreMissingBoundaryParameter;
    protected boolean ignoreExistingBoundaryParameter;
    protected boolean allowEmpty;
    
    public MimeMultipart() {
        this("mixed");
    }
    
    public MimeMultipart(final String subtype) {
        this.ds = null;
        this.parsed = true;
        this.complete = true;
        this.preamble = null;
        this.ignoreMissingEndBoundary = true;
        this.ignoreMissingBoundaryParameter = true;
        this.ignoreExistingBoundaryParameter = false;
        this.allowEmpty = false;
        final String boundary = UniqueValue.getUniqueBoundaryValue();
        final ContentType cType = new ContentType("multipart", subtype, null);
        cType.setParameter("boundary", boundary);
        this.contentType = cType.toString();
        this.initializeProperties();
    }
    
    public MimeMultipart(final BodyPart... parts) throws MessagingException {
        this();
        for (final BodyPart bp : parts) {
            super.addBodyPart(bp);
        }
    }
    
    public MimeMultipart(final String subtype, final BodyPart... parts) throws MessagingException {
        this(subtype);
        for (final BodyPart bp : parts) {
            super.addBodyPart(bp);
        }
    }
    
    public MimeMultipart(final DataSource ds) throws MessagingException {
        this.ds = null;
        this.parsed = true;
        this.complete = true;
        this.preamble = null;
        this.ignoreMissingEndBoundary = true;
        this.ignoreMissingBoundaryParameter = true;
        this.ignoreExistingBoundaryParameter = false;
        this.allowEmpty = false;
        if (ds instanceof MessageAware) {
            final MessageContext mc = ((MessageAware)ds).getMessageContext();
            this.setParent(mc.getPart());
        }
        if (ds instanceof MultipartDataSource) {
            this.setMultipartDataSource((MultipartDataSource)ds);
            return;
        }
        this.parsed = false;
        this.ds = ds;
        this.contentType = ds.getContentType();
    }
    
    protected void initializeProperties() {
        this.ignoreMissingEndBoundary = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoremissingendboundary", true);
        this.ignoreMissingBoundaryParameter = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoremissingboundaryparameter", true);
        this.ignoreExistingBoundaryParameter = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoreexistingboundaryparameter", false);
        this.allowEmpty = PropUtil.getBooleanSystemProperty("mail.mime.multipart.allowempty", false);
    }
    
    public synchronized void setSubType(final String subtype) throws MessagingException {
        final ContentType cType = new ContentType(this.contentType);
        cType.setSubType(subtype);
        this.contentType = cType.toString();
    }
    
    @Override
    public synchronized int getCount() throws MessagingException {
        this.parse();
        return super.getCount();
    }
    
    @Override
    public synchronized BodyPart getBodyPart(final int index) throws MessagingException {
        this.parse();
        return super.getBodyPart(index);
    }
    
    public synchronized BodyPart getBodyPart(final String CID) throws MessagingException {
        this.parse();
        for (int count = this.getCount(), i = 0; i < count; ++i) {
            final MimeBodyPart part = (MimeBodyPart)this.getBodyPart(i);
            final String s = part.getContentID();
            if (s != null && s.equals(CID)) {
                return part;
            }
        }
        return null;
    }
    
    @Override
    public boolean removeBodyPart(final BodyPart part) throws MessagingException {
        this.parse();
        return super.removeBodyPart(part);
    }
    
    @Override
    public void removeBodyPart(final int index) throws MessagingException {
        this.parse();
        super.removeBodyPart(index);
    }
    
    @Override
    public synchronized void addBodyPart(final BodyPart part) throws MessagingException {
        this.parse();
        super.addBodyPart(part);
    }
    
    @Override
    public synchronized void addBodyPart(final BodyPart part, final int index) throws MessagingException {
        this.parse();
        super.addBodyPart(part, index);
    }
    
    public synchronized boolean isComplete() throws MessagingException {
        this.parse();
        return this.complete;
    }
    
    public synchronized String getPreamble() throws MessagingException {
        this.parse();
        return this.preamble;
    }
    
    public synchronized void setPreamble(final String preamble) throws MessagingException {
        this.preamble = preamble;
    }
    
    protected synchronized void updateHeaders() throws MessagingException {
        this.parse();
        for (int i = 0; i < this.parts.size(); ++i) {
            this.parts.elementAt(i).updateHeaders();
        }
    }
    
    @Override
    public synchronized void writeTo(final OutputStream os) throws IOException, MessagingException {
        this.parse();
        final String boundary = "--" + new ContentType(this.contentType).getParameter("boundary");
        final LineOutputStream los = new LineOutputStream(os);
        if (this.preamble != null) {
            final byte[] pb = ASCIIUtility.getBytes(this.preamble);
            los.write(pb);
            if (pb.length > 0 && pb[pb.length - 1] != 13 && pb[pb.length - 1] != 10) {
                los.writeln();
            }
        }
        if (this.parts.size() == 0) {
            if (!this.allowEmpty) {
                throw new MessagingException("Empty multipart: " + this.contentType);
            }
            los.writeln(boundary);
            los.writeln();
        }
        else {
            for (int i = 0; i < this.parts.size(); ++i) {
                los.writeln(boundary);
                this.parts.elementAt(i).writeTo(os);
                los.writeln();
            }
        }
        los.writeln(boundary + "--");
    }
    
    protected synchronized void parse() throws MessagingException {
        if (this.parsed) {
            return;
        }
        this.initializeProperties();
        InputStream in = null;
        SharedInputStream sin = null;
        long start = 0L;
        long end = 0L;
        try {
            in = this.ds.getInputStream();
            if (!(in instanceof ByteArrayInputStream) && !(in instanceof BufferedInputStream) && !(in instanceof SharedInputStream)) {
                in = new BufferedInputStream(in);
            }
        }
        catch (final Exception ex) {
            throw new MessagingException("No inputstream from datasource", ex);
        }
        if (in instanceof SharedInputStream) {
            sin = (SharedInputStream)in;
        }
        final ContentType cType = new ContentType(this.contentType);
        String boundary = null;
        if (!this.ignoreExistingBoundaryParameter) {
            final String bp = cType.getParameter("boundary");
            if (bp != null) {
                boundary = "--" + bp;
            }
        }
        if (boundary == null && !this.ignoreMissingBoundaryParameter && !this.ignoreExistingBoundaryParameter) {
            throw new ParseException("Missing boundary parameter");
        }
        try {
            final LineInputStream lin = new LineInputStream(in);
            StringBuilder preamblesb = null;
            String line;
            while ((line = lin.readLine()) != null) {
                int i;
                for (i = line.length() - 1; i >= 0; --i) {
                    final char c = line.charAt(i);
                    if (c != ' ' && c != '\t') {
                        break;
                    }
                }
                line = line.substring(0, i + 1);
                if (boundary != null) {
                    if (line.equals(boundary)) {
                        break;
                    }
                    if (line.length() == boundary.length() + 2 && line.startsWith(boundary) && line.endsWith("--")) {
                        line = null;
                        break;
                    }
                }
                else if (line.length() > 2 && line.startsWith("--")) {
                    if (line.length() <= 4 || !allDashes(line)) {
                        boundary = line;
                        break;
                    }
                }
                if (line.length() > 0) {
                    if (preamblesb == null) {
                        preamblesb = new StringBuilder(line.length() + 2);
                    }
                    preamblesb.append(line).append(System.lineSeparator());
                }
            }
            if (preamblesb != null) {
                this.preamble = preamblesb.toString();
            }
            if (line == null) {
                if (this.allowEmpty) {
                    return;
                }
                throw new ParseException("Missing start boundary");
            }
            else {
                final byte[] bndbytes = ASCIIUtility.getBytes(boundary);
                final int bl = bndbytes.length;
                final int[] bcs = new int[256];
                for (int j = 0; j < bl; ++j) {
                    bcs[bndbytes[j] & 0xFF] = j + 1;
                }
                final int[] gss = new int[bl];
                int k = bl;
            Label_0518:
                while (k > 0) {
                    while (true) {
                        int l;
                        for (l = bl - 1; l >= k; --l) {
                            if (bndbytes[l] != bndbytes[l - k]) {
                                --k;
                                continue Label_0518;
                            }
                            gss[l - 1] = k;
                        }
                        while (l > 0) {
                            gss[--l] = k;
                        }
                        continue;
                    }
                }
                gss[bl - 1] = 1;
                boolean done = false;
                while (!done) {
                    InternetHeaders headers = null;
                    if (sin != null) {
                        start = sin.getPosition();
                        while ((line = lin.readLine()) != null && line.length() > 0) {}
                        if (line == null) {
                            if (!this.ignoreMissingEndBoundary) {
                                throw new ParseException("missing multipart end boundary");
                            }
                            this.complete = false;
                            break;
                        }
                    }
                    else {
                        headers = this.createInternetHeaders(in);
                    }
                    if (!in.markSupported()) {
                        throw new MessagingException("Stream doesn't support mark");
                    }
                    ByteArrayOutputStream buf = null;
                    if (sin == null) {
                        buf = new ByteArrayOutputStream();
                    }
                    else {
                        end = sin.getPosition();
                    }
                    byte[] inbuf = new byte[bl];
                    byte[] previnbuf = new byte[bl];
                    int inSize = 0;
                    int prevSize = 0;
                    boolean first = true;
                    int eolLen;
                    while (true) {
                        in.mark(bl + 4 + 1000);
                        eolLen = 0;
                        inSize = readFully(in, inbuf, 0, bl);
                        if (inSize < bl) {
                            if (!this.ignoreMissingEndBoundary) {
                                throw new ParseException("missing multipart end boundary");
                            }
                            if (sin != null) {
                                end = sin.getPosition();
                            }
                            this.complete = false;
                            done = true;
                            break;
                        }
                        else {
                            int m;
                            for (m = bl - 1; m >= 0 && inbuf[m] == bndbytes[m]; --m) {}
                            if (m < 0) {
                                eolLen = 0;
                                if (!first) {
                                    int b = previnbuf[prevSize - 1];
                                    if (b == 13 || b == 10) {
                                        eolLen = 1;
                                        if (b == 10 && prevSize >= 2) {
                                            b = previnbuf[prevSize - 2];
                                            if (b == 13) {
                                                eolLen = 2;
                                            }
                                        }
                                    }
                                }
                                if (first || eolLen > 0) {
                                    if (sin != null) {
                                        end = sin.getPosition() - bl - eolLen;
                                    }
                                    int b2 = in.read();
                                    if (b2 == 45 && in.read() == 45) {
                                        this.complete = true;
                                        done = true;
                                        break;
                                    }
                                    while (b2 == 32 || b2 == 9) {
                                        b2 = in.read();
                                    }
                                    if (b2 == 10) {
                                        break;
                                    }
                                    if (b2 == 13) {
                                        in.mark(1);
                                        if (in.read() != 10) {
                                            in.reset();
                                            break;
                                        }
                                        break;
                                    }
                                }
                                m = 0;
                            }
                            final int skip = Math.max(m + 1 - bcs[inbuf[m] & 0x7F], gss[m]);
                            if (skip < 2) {
                                if (sin == null && prevSize > 1) {
                                    buf.write(previnbuf, 0, prevSize - 1);
                                }
                                in.reset();
                                this.skipFully(in, 1L);
                                if (prevSize >= 1) {
                                    previnbuf[0] = previnbuf[prevSize - 1];
                                    previnbuf[1] = inbuf[0];
                                    prevSize = 2;
                                }
                                else {
                                    previnbuf[0] = inbuf[0];
                                    prevSize = 1;
                                }
                            }
                            else {
                                if (prevSize > 0 && sin == null) {
                                    buf.write(previnbuf, 0, prevSize);
                                }
                                prevSize = skip;
                                in.reset();
                                this.skipFully(in, prevSize);
                                final byte[] tmp = inbuf;
                                inbuf = previnbuf;
                                previnbuf = tmp;
                            }
                            first = false;
                        }
                    }
                    MimeBodyPart part;
                    if (sin != null) {
                        part = this.createMimeBodyPartIs(sin.newStream(start, end));
                    }
                    else {
                        if (prevSize - eolLen > 0) {
                            buf.write(previnbuf, 0, prevSize - eolLen);
                        }
                        if (!this.complete && inSize > 0) {
                            buf.write(inbuf, 0, inSize);
                        }
                        part = this.createMimeBodyPart(headers, buf.toByteArray());
                    }
                    super.addBodyPart(part);
                }
            }
        }
        catch (final IOException ioex) {
            throw new MessagingException("IO Error", ioex);
        }
        finally {
            try {
                in.close();
            }
            catch (final IOException ex2) {}
        }
        this.parsed = true;
    }
    
    private static boolean allDashes(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != '-') {
                return false;
            }
        }
        return true;
    }
    
    private static int readFully(final InputStream in, final byte[] buf, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        int total = 0;
        while (len > 0) {
            final int bsize = in.read(buf, off, len);
            if (bsize <= 0) {
                break;
            }
            off += bsize;
            total += bsize;
            len -= bsize;
        }
        return (total > 0) ? total : -1;
    }
    
    private void skipFully(final InputStream in, long offset) throws IOException {
        while (offset > 0L) {
            final long cur = in.skip(offset);
            if (cur <= 0L) {
                throw new EOFException("can't skip");
            }
            offset -= cur;
        }
    }
    
    protected InternetHeaders createInternetHeaders(final InputStream is) throws MessagingException {
        return new InternetHeaders(is);
    }
    
    protected MimeBodyPart createMimeBodyPart(final InternetHeaders headers, final byte[] content) throws MessagingException {
        return new MimeBodyPart(headers, content);
    }
    
    protected MimeBodyPart createMimeBodyPart(final InputStream is) throws MessagingException {
        return new MimeBodyPart(is);
    }
    
    private MimeBodyPart createMimeBodyPartIs(final InputStream is) throws MessagingException {
        try {
            return this.createMimeBodyPart(is);
        }
        finally {
            try {
                is.close();
            }
            catch (final IOException ex) {}
        }
    }
}
