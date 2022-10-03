package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MultipartDataSource;
import java.io.InputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import java.io.OutputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import javax.activation.DataSource;

public class MimeMultipart
{
    protected DataSource ds;
    protected boolean parsed;
    protected FinalArrayList parts;
    protected ContentType contentType;
    protected MimeBodyPart parent;
    protected static final boolean ignoreMissingEndBoundary;
    
    public MimeMultipart() {
        this("mixed");
    }
    
    public MimeMultipart(final String subtype) {
        this.ds = null;
        this.parsed = true;
        this.parts = new FinalArrayList();
        final String boundary = UniqueValue.getUniqueBoundaryValue();
        (this.contentType = new ContentType("multipart", subtype, null)).setParameter("boundary", boundary);
    }
    
    public MimeMultipart(final DataSource ds, final ContentType ct) throws MessagingException {
        this.ds = null;
        this.parsed = true;
        this.parts = new FinalArrayList();
        this.parsed = false;
        this.ds = ds;
        if (ct == null) {
            this.contentType = new ContentType(ds.getContentType());
        }
        else {
            this.contentType = ct;
        }
    }
    
    public void setSubType(final String subtype) {
        this.contentType.setSubType(subtype);
    }
    
    public int getCount() throws MessagingException {
        this.parse();
        if (this.parts == null) {
            return 0;
        }
        return this.parts.size();
    }
    
    public MimeBodyPart getBodyPart(final int index) throws MessagingException {
        this.parse();
        if (this.parts == null) {
            throw new IndexOutOfBoundsException("No such BodyPart");
        }
        return this.parts.get(index);
    }
    
    public MimeBodyPart getBodyPart(final String CID) throws MessagingException {
        this.parse();
        for (int count = this.getCount(), i = 0; i < count; ++i) {
            final MimeBodyPart part = this.getBodyPart(i);
            final String s = part.getContentID();
            final String sNoAngle = (s != null) ? s.replaceFirst("^<", "").replaceFirst(">$", "") : null;
            if (s != null && (s.equals(CID) || CID.equals(sNoAngle))) {
                return part;
            }
        }
        return null;
    }
    
    protected void updateHeaders() throws MessagingException {
        for (int i = 0; i < this.parts.size(); ++i) {
            this.parts.get(i).updateHeaders();
        }
    }
    
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        this.parse();
        final String boundary = "--" + this.contentType.getParameter("boundary");
        for (int i = 0; i < this.parts.size(); ++i) {
            OutputUtil.writeln(boundary, os);
            this.getBodyPart(i).writeTo(os);
            OutputUtil.writeln(os);
        }
        OutputUtil.writeAsAscii(boundary, os);
        OutputUtil.writeAsAscii("--", os);
        os.flush();
    }
    
    protected void parse() throws MessagingException {
        if (this.parsed) {
            return;
        }
        SharedInputStream sin = null;
        long start = 0L;
        long end = 0L;
        boolean foundClosingBoundary = false;
        InputStream in;
        try {
            in = this.ds.getInputStream();
            if (!(in instanceof ByteArrayInputStream) && !(in instanceof BufferedInputStream) && !(in instanceof SharedInputStream)) {
                in = new BufferedInputStream(in);
            }
        }
        catch (final Exception ex) {
            throw new MessagingException("No inputstream from datasource");
        }
        if (in instanceof SharedInputStream) {
            sin = (SharedInputStream)in;
        }
        final String boundary = "--" + this.contentType.getParameter("boundary");
        final byte[] bndbytes = ASCIIUtility.getBytes(boundary);
        final int bl = bndbytes.length;
        try {
            final LineInputStream lin = new LineInputStream(in);
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
                if (line.equals(boundary)) {
                    break;
                }
            }
            if (line == null) {
                throw new MessagingException("Missing start boundary");
            }
            boolean done = false;
            while (!done) {
                InternetHeaders headers = null;
                if (sin != null) {
                    start = sin.getPosition();
                    while ((line = lin.readLine()) != null && line.length() > 0) {}
                    if (line == null) {
                        if (!MimeMultipart.ignoreMissingEndBoundary) {
                            throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
                        }
                        break;
                    }
                }
                else {
                    headers = this.createInternetHeaders(in);
                }
                if (!in.markSupported()) {
                    throw new MessagingException("Stream doesn't support mark");
                }
                ByteOutputStream buf = null;
                if (sin == null) {
                    buf = new ByteOutputStream();
                }
                boolean bol = true;
                int eol1 = -1;
                int eol2 = -1;
                while (true) {
                    if (bol) {
                        in.mark(bl + 4 + 1000);
                        int j;
                        for (j = 0; j < bl && in.read() == bndbytes[j]; ++j) {}
                        if (j == bl) {
                            int b2 = in.read();
                            if (b2 == 45 && in.read() == 45) {
                                done = true;
                                foundClosingBoundary = true;
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
                        in.reset();
                        if (buf != null && eol1 != -1) {
                            buf.write(eol1);
                            if (eol2 != -1) {
                                buf.write(eol2);
                            }
                            eol2 = (eol1 = -1);
                        }
                    }
                    int b3;
                    if ((b3 = in.read()) < 0) {
                        done = true;
                        break;
                    }
                    if (b3 == 13 || b3 == 10) {
                        bol = true;
                        if (sin != null) {
                            end = sin.getPosition() - 1L;
                        }
                        if ((eol1 = b3) != 13) {
                            continue;
                        }
                        in.mark(1);
                        if ((b3 = in.read()) == 10) {
                            eol2 = b3;
                        }
                        else {
                            in.reset();
                        }
                    }
                    else {
                        bol = false;
                        if (buf == null) {
                            continue;
                        }
                        buf.write(b3);
                    }
                }
                MimeBodyPart part;
                if (sin != null) {
                    part = this.createMimeBodyPart(sin.newStream(start, end));
                }
                else {
                    part = this.createMimeBodyPart(headers, buf.getBytes(), buf.getCount());
                }
                this.addBodyPart(part);
            }
        }
        catch (final IOException ioex) {
            throw new MessagingException("IO Error", ioex);
        }
        if (!MimeMultipart.ignoreMissingEndBoundary && !foundClosingBoundary && sin == null) {
            throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
        }
        this.parsed = true;
    }
    
    protected InternetHeaders createInternetHeaders(final InputStream is) throws MessagingException {
        return new InternetHeaders(is);
    }
    
    protected MimeBodyPart createMimeBodyPart(final InternetHeaders headers, final byte[] content, final int len) {
        return new MimeBodyPart(headers, content, len);
    }
    
    protected MimeBodyPart createMimeBodyPart(final InputStream is) throws MessagingException {
        return new MimeBodyPart(is);
    }
    
    protected void setMultipartDataSource(final MultipartDataSource mp) throws MessagingException {
        this.contentType = new ContentType(mp.getContentType());
        for (int count = mp.getCount(), i = 0; i < count; ++i) {
            this.addBodyPart(mp.getBodyPart(i));
        }
    }
    
    public ContentType getContentType() {
        return this.contentType;
    }
    
    public boolean removeBodyPart(final MimeBodyPart part) throws MessagingException {
        if (this.parts == null) {
            throw new MessagingException("No such body part");
        }
        final boolean ret = this.parts.remove(part);
        part.setParent(null);
        return ret;
    }
    
    public void removeBodyPart(final int index) {
        if (this.parts == null) {
            throw new IndexOutOfBoundsException("No such BodyPart");
        }
        final MimeBodyPart part = this.parts.get(index);
        this.parts.remove(index);
        part.setParent(null);
    }
    
    public synchronized void addBodyPart(final MimeBodyPart part) {
        if (this.parts == null) {
            this.parts = new FinalArrayList();
        }
        this.parts.add(part);
        part.setParent(this);
    }
    
    public synchronized void addBodyPart(final MimeBodyPart part, final int index) {
        if (this.parts == null) {
            this.parts = new FinalArrayList();
        }
        this.parts.add(index, part);
        part.setParent(this);
    }
    
    MimeBodyPart getParent() {
        return this.parent;
    }
    
    void setParent(final MimeBodyPart parent) {
        this.parent = parent;
    }
    
    static {
        ignoreMissingEndBoundary = SAAJUtil.getSystemBoolean("saaj.mime.multipart.ignoremissingendboundary");
    }
}
