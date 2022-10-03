package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import java.io.OutputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import javax.activation.DataSource;
import java.io.InputStream;
import java.util.BitSet;

public class BMMimeMultipart extends MimeMultipart
{
    private boolean begining;
    int[] bcs;
    int[] gss;
    private static final int BUFFER_SIZE = 4096;
    private byte[] buffer;
    private byte[] prevBuffer;
    private BitSet lastPartFound;
    private InputStream in;
    private String boundary;
    int b;
    private boolean lazyAttachments;
    byte[] buf;
    
    public BMMimeMultipart() {
        this.begining = true;
        this.bcs = new int[256];
        this.gss = null;
        this.buffer = new byte[4096];
        this.prevBuffer = new byte[4096];
        this.lastPartFound = new BitSet(1);
        this.in = null;
        this.boundary = null;
        this.b = 0;
        this.lazyAttachments = false;
        this.buf = new byte[1024];
    }
    
    public BMMimeMultipart(final String subtype) {
        super(subtype);
        this.begining = true;
        this.bcs = new int[256];
        this.gss = null;
        this.buffer = new byte[4096];
        this.prevBuffer = new byte[4096];
        this.lastPartFound = new BitSet(1);
        this.in = null;
        this.boundary = null;
        this.b = 0;
        this.lazyAttachments = false;
        this.buf = new byte[1024];
    }
    
    public BMMimeMultipart(final DataSource ds, final ContentType ct) throws MessagingException {
        super(ds, ct);
        this.begining = true;
        this.bcs = new int[256];
        this.gss = null;
        this.buffer = new byte[4096];
        this.prevBuffer = new byte[4096];
        this.lastPartFound = new BitSet(1);
        this.in = null;
        this.boundary = null;
        this.b = 0;
        this.lazyAttachments = false;
        this.buf = new byte[1024];
        this.boundary = ct.getParameter("boundary");
    }
    
    public InputStream initStream() throws MessagingException {
        if (this.in == null) {
            try {
                this.in = this.ds.getInputStream();
                if (!(this.in instanceof ByteArrayInputStream) && !(this.in instanceof BufferedInputStream) && !(this.in instanceof SharedInputStream)) {
                    this.in = new BufferedInputStream(this.in);
                }
            }
            catch (final Exception ex) {
                throw new MessagingException("No inputstream from datasource");
            }
            if (!this.in.markSupported()) {
                throw new MessagingException("InputStream does not support Marking");
            }
        }
        return this.in;
    }
    
    @Override
    protected void parse() throws MessagingException {
        if (this.parsed) {
            return;
        }
        this.initStream();
        SharedInputStream sin = null;
        if (this.in instanceof SharedInputStream) {
            sin = (SharedInputStream)this.in;
        }
        final String bnd = "--" + this.boundary;
        final byte[] bndbytes = ASCIIUtility.getBytes(bnd);
        try {
            this.parse(this.in, bndbytes, sin);
        }
        catch (final IOException ioex) {
            throw new MessagingException("IO Error", ioex);
        }
        catch (final Exception ex) {
            throw new MessagingException("Error", ex);
        }
        this.parsed = true;
    }
    
    public boolean lastBodyPartFound() {
        return this.lastPartFound.get(0);
    }
    
    public MimeBodyPart getNextPart(final InputStream stream, final byte[] pattern, final SharedInputStream sin) throws Exception {
        if (!stream.markSupported()) {
            throw new Exception("InputStream does not support Marking");
        }
        if (this.begining) {
            this.compile(pattern);
            if (!this.skipPreamble(stream, pattern, sin)) {
                throw new Exception("Missing Start Boundary, or boundary does not start on a new line");
            }
            this.begining = false;
        }
        if (this.lastBodyPartFound()) {
            throw new Exception("No parts found in Multipart InputStream");
        }
        if (sin != null) {
            final long start = sin.getPosition();
            this.b = this.readHeaders(stream);
            if (this.b == -1) {
                throw new Exception("End of Stream encountered while reading part headers");
            }
            final long[] v = { -1L };
            this.b = this.readBody(stream, pattern, v, null, sin);
            if (!BMMimeMultipart.ignoreMissingEndBoundary && this.b == -1 && !this.lastBodyPartFound()) {
                throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
            }
            final long end = v[0];
            final MimeBodyPart mbp = this.createMimeBodyPart(sin.newStream(start, end));
            this.addBodyPart(mbp);
            return mbp;
        }
        else {
            final InternetHeaders headers = this.createInternetHeaders(stream);
            final ByteOutputStream baos = new ByteOutputStream();
            this.b = this.readBody(stream, pattern, null, baos, null);
            if (!BMMimeMultipart.ignoreMissingEndBoundary && this.b == -1 && !this.lastBodyPartFound()) {
                throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
            }
            final MimeBodyPart mbp2 = this.createMimeBodyPart(headers, baos.getBytes(), baos.getCount());
            this.addBodyPart(mbp2);
            return mbp2;
        }
    }
    
    public boolean parse(final InputStream stream, final byte[] pattern, final SharedInputStream sin) throws Exception {
        while (!this.lastPartFound.get(0) && this.b != -1) {
            this.getNextPart(stream, pattern, sin);
        }
        return true;
    }
    
    private int readHeaders(final InputStream is) throws Exception {
        int b = is.read();
        while (b != -1) {
            if (b == 13) {
                b = is.read();
                if (b != 10) {
                    continue;
                }
                b = is.read();
                if (b != 13) {
                    continue;
                }
                b = is.read();
                if (b == 10) {
                    return b;
                }
                continue;
            }
            else {
                b = is.read();
            }
        }
        if (b == -1) {
            throw new Exception("End of inputstream while reading Mime-Part Headers");
        }
        return b;
    }
    
    private int readBody(final InputStream is, final byte[] pattern, final long[] posVector, final ByteOutputStream baos, final SharedInputStream sin) throws Exception {
        if (!this.find(is, pattern, posVector, baos, sin)) {
            throw new Exception("Missing boundary delimitier while reading Body Part");
        }
        return this.b;
    }
    
    private boolean skipPreamble(final InputStream is, final byte[] pattern, final SharedInputStream sin) throws Exception {
        if (!this.find(is, pattern, sin)) {
            return false;
        }
        if (this.lastPartFound.get(0)) {
            throw new Exception("Found closing boundary delimiter while trying to skip preamble");
        }
        return true;
    }
    
    public int readNext(final InputStream is, final byte[] buff, final int patternLength, final BitSet eof, final long[] posVector, final SharedInputStream sin) throws Exception {
        int bufferLength = is.read(this.buffer, 0, patternLength);
        if (bufferLength == -1) {
            eof.flip(0);
        }
        else if (bufferLength < patternLength) {
            int temp = 0;
            long pos = 0L;
            int i = bufferLength;
            while (i < patternLength) {
                if (sin != null) {
                    pos = sin.getPosition();
                }
                temp = is.read();
                if (temp == -1) {
                    eof.flip(0);
                    if (sin != null) {
                        posVector[0] = pos;
                        break;
                    }
                    break;
                }
                else {
                    this.buffer[i] = (byte)temp;
                    ++i;
                }
            }
            bufferLength = i;
        }
        return bufferLength;
    }
    
    public boolean find(final InputStream is, final byte[] pattern, final SharedInputStream sin) throws Exception {
        final int l = pattern.length;
        final int lx = l - 1;
        int bufferLength = 0;
        final BitSet eof = new BitSet(1);
        final long[] posVector = { 0L };
        while (true) {
            is.mark(l);
            bufferLength = this.readNext(is, this.buffer, l, eof, posVector, sin);
            if (eof.get(0)) {
                return false;
            }
            int i;
            for (i = lx; i >= 0 && this.buffer[i] == pattern[i]; --i) {}
            if (i < 0) {
                if (!this.skipLWSPAndCRLF(is)) {
                    throw new Exception("Boundary does not terminate with CRLF");
                }
                return true;
            }
            else {
                final int s = Math.max(i + 1 - this.bcs[this.buffer[i] & 0x7F], this.gss[i]);
                is.reset();
                is.skip(s);
            }
        }
    }
    
    public boolean find(final InputStream is, final byte[] pattern, final long[] posVector, final ByteOutputStream out, final SharedInputStream sin) throws Exception {
        final int l = pattern.length;
        final int lx = l - 1;
        int bufferLength = 0;
        int s = 0;
        long endPos = -1L;
        byte[] tmp = null;
        boolean first = true;
        final BitSet eof = new BitSet(1);
        while (true) {
            is.mark(l);
            if (!first) {
                tmp = this.prevBuffer;
                this.prevBuffer = this.buffer;
                this.buffer = tmp;
            }
            if (sin != null) {
                endPos = sin.getPosition();
            }
            bufferLength = this.readNext(is, this.buffer, l, eof, posVector, sin);
            if (bufferLength == -1) {
                this.b = -1;
                if (s == l && sin == null) {
                    out.write(this.prevBuffer, 0, s);
                }
                return true;
            }
            if (bufferLength < l) {
                if (sin == null) {
                    out.write(this.buffer, 0, bufferLength);
                }
                this.b = -1;
                return true;
            }
            int i;
            for (i = lx; i >= 0 && this.buffer[i] == pattern[i]; --i) {}
            if (i < 0) {
                if (s > 0) {
                    if (s <= 2) {
                        if (s == 2) {
                            if (this.prevBuffer[1] != 10) {
                                throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
                            }
                            if (this.prevBuffer[0] != 13 && this.prevBuffer[0] != 10) {
                                out.write(this.prevBuffer, 0, 1);
                            }
                            if (sin != null) {
                                posVector[0] = endPos;
                            }
                        }
                        else if (s == 1) {
                            if (this.prevBuffer[0] != 10) {
                                throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
                            }
                            if (sin != null) {
                                posVector[0] = endPos;
                            }
                        }
                    }
                    else if (s > 2) {
                        if (this.prevBuffer[s - 2] == 13 && this.prevBuffer[s - 1] == 10) {
                            if (sin != null) {
                                posVector[0] = endPos - 2L;
                            }
                            else {
                                out.write(this.prevBuffer, 0, s - 2);
                            }
                        }
                        else {
                            if (this.prevBuffer[s - 1] != 10) {
                                throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
                            }
                            if (sin != null) {
                                posVector[0] = endPos - 1L;
                            }
                            else {
                                out.write(this.prevBuffer, 0, s - 1);
                            }
                        }
                    }
                }
                if (!this.skipLWSPAndCRLF(is)) {}
                return true;
            }
            if (s > 0 && sin == null) {
                if (this.prevBuffer[s - 1] == 13) {
                    if (this.buffer[0] == 10) {
                        int j;
                        for (j = lx - 1, j = lx - 1; j > 0 && this.buffer[j + 1] == pattern[j]; --j) {}
                        if (j == 0) {
                            out.write(this.prevBuffer, 0, s - 1);
                        }
                        else {
                            out.write(this.prevBuffer, 0, s);
                        }
                    }
                    else {
                        out.write(this.prevBuffer, 0, s);
                    }
                }
                else {
                    out.write(this.prevBuffer, 0, s);
                }
            }
            s = Math.max(i + 1 - this.bcs[this.buffer[i] & 0x7F], this.gss[i]);
            is.reset();
            is.skip(s);
            if (!first) {
                continue;
            }
            first = false;
        }
    }
    
    private boolean skipLWSPAndCRLF(final InputStream is) throws Exception {
        this.b = is.read();
        if (this.b == 10) {
            return true;
        }
        if (this.b == 13) {
            this.b = is.read();
            if (this.b == 13) {
                this.b = is.read();
            }
            if (this.b == 10) {
                return true;
            }
            throw new Exception("transport padding after a Mime Boundary  should end in a CRLF, found CR only");
        }
        else {
            if (this.b == 45) {
                this.b = is.read();
                if (this.b != 45) {
                    throw new Exception("Unexpected singular '-' character after Mime Boundary");
                }
                this.lastPartFound.flip(0);
                this.b = is.read();
            }
            while (this.b != -1 && (this.b == 32 || this.b == 9)) {
                this.b = is.read();
                if (this.b == 10) {
                    return true;
                }
                if (this.b != 13) {
                    continue;
                }
                this.b = is.read();
                if (this.b == 13) {
                    this.b = is.read();
                }
                if (this.b == 10) {
                    return true;
                }
            }
            if (this.b != -1) {
                return false;
            }
            if (!this.lastPartFound.get(0)) {
                throw new Exception("End of Multipart Stream before encountering  closing boundary delimiter");
            }
            return true;
        }
    }
    
    private void compile(final byte[] pattern) {
        final int l = pattern.length;
        for (int i = 0; i < l; ++i) {
            this.bcs[pattern[i]] = i + 1;
        }
        this.gss = new int[l];
        int i = l;
    Label_0036:
        while (i > 0) {
            while (true) {
                int j;
                for (j = l - 1; j >= i; --j) {
                    if (pattern[j] != pattern[j - i]) {
                        --i;
                        continue Label_0036;
                    }
                    this.gss[j - 1] = i;
                }
                while (j > 0) {
                    this.gss[--j] = i;
                }
                continue;
            }
        }
        this.gss[l - 1] = 1;
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        if (this.in != null) {
            this.contentType.setParameter("boundary", this.boundary);
        }
        final String bnd = "--" + this.contentType.getParameter("boundary");
        for (int i = 0; i < this.parts.size(); ++i) {
            OutputUtil.writeln(bnd, os);
            this.parts.get(i).writeTo(os);
            OutputUtil.writeln(os);
        }
        if (this.in != null) {
            OutputUtil.writeln(bnd, os);
            if (os instanceof ByteOutputStream && this.lazyAttachments) {
                ((ByteOutputStream)os).write(this.in);
            }
            else {
                final ByteOutputStream baos = new ByteOutputStream(this.in.available());
                baos.write(this.in);
                baos.writeTo(os);
                this.in = baos.newInputStream();
            }
        }
        else {
            OutputUtil.writeAsAscii(bnd, os);
            OutputUtil.writeAsAscii("--", os);
        }
    }
    
    public void setInputStream(final InputStream is) {
        this.in = is;
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
    
    public void setBoundary(final String bnd) {
        this.boundary = bnd;
        if (this.contentType != null) {
            this.contentType.setParameter("boundary", bnd);
        }
    }
    
    public String getBoundary() {
        return this.boundary;
    }
    
    public boolean isEndOfStream() {
        return this.b == -1;
    }
    
    public void setLazyAttachments(final boolean flag) {
        this.lazyAttachments = flag;
    }
}
