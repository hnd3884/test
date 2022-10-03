package com.sun.mail.imap;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Flags;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.BODY;
import java.io.IOException;
import com.sun.mail.util.MessageRemovedIOException;
import com.sun.mail.iap.ProtocolException;
import javax.mail.FolderClosedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.util.FolderClosedIOException;
import com.sun.mail.iap.ByteArray;
import java.io.InputStream;

public class IMAPInputStream extends InputStream
{
    private IMAPMessage msg;
    private String section;
    private int pos;
    private int blksize;
    private int max;
    private byte[] buf;
    private int bufcount;
    private int bufpos;
    private boolean lastBuffer;
    private boolean peek;
    private ByteArray readbuf;
    private static final int slop = 64;
    
    public IMAPInputStream(final IMAPMessage msg, final String section, final int max, final boolean peek) {
        this.msg = msg;
        this.section = section;
        this.max = max;
        this.peek = peek;
        this.pos = 0;
        this.blksize = msg.getFetchBlockSize();
    }
    
    private void forceCheckExpunged() throws MessageRemovedIOException, FolderClosedIOException {
        synchronized (this.msg.getMessageCacheLock()) {
            try {
                this.msg.getProtocol().noop();
            }
            catch (final ConnectionException cex) {
                throw new FolderClosedIOException(this.msg.getFolder(), cex.getMessage());
            }
            catch (final FolderClosedException fex) {
                throw new FolderClosedIOException(fex.getFolder(), fex.getMessage());
            }
            catch (final ProtocolException ex) {}
        }
        if (this.msg.isExpunged()) {
            throw new MessageRemovedIOException();
        }
    }
    
    private void fill() throws IOException {
        if (this.lastBuffer || (this.max != -1 && this.pos >= this.max)) {
            if (this.pos == 0) {
                this.checkSeen();
            }
            this.readbuf = null;
            return;
        }
        BODY b = null;
        if (this.readbuf == null) {
            this.readbuf = new ByteArray(this.blksize + 64);
        }
        int cnt;
        ByteArray ba;
        synchronized (this.msg.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.msg.getProtocol();
                if (this.msg.isExpunged()) {
                    throw new MessageRemovedIOException("No content for expunged message");
                }
                final int seqnum = this.msg.getSequenceNumber();
                cnt = this.blksize;
                if (this.max != -1 && this.pos + this.blksize > this.max) {
                    cnt = this.max - this.pos;
                }
                if (this.peek) {
                    b = p.peekBody(seqnum, this.section, this.pos, cnt, this.readbuf);
                }
                else {
                    b = p.fetchBody(seqnum, this.section, this.pos, cnt, this.readbuf);
                }
            }
            catch (final ProtocolException pex) {
                this.forceCheckExpunged();
                throw new IOException(pex.getMessage());
            }
            catch (final FolderClosedException fex) {
                throw new FolderClosedIOException(fex.getFolder(), fex.getMessage());
            }
            if (b == null || (ba = b.getByteArray()) == null) {
                this.forceCheckExpunged();
                ba = new ByteArray(0);
            }
        }
        if (this.pos == 0) {
            this.checkSeen();
        }
        this.buf = ba.getBytes();
        this.bufpos = ba.getStart();
        final int n = ba.getCount();
        this.lastBuffer = (n < cnt);
        this.bufcount = this.bufpos + n;
        this.pos += n;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (this.bufpos >= this.bufcount) {
            this.fill();
            if (this.bufpos >= this.bufcount) {
                return -1;
            }
        }
        return this.buf[this.bufpos++] & 0xFF;
    }
    
    @Override
    public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
        int avail = this.bufcount - this.bufpos;
        if (avail <= 0) {
            this.fill();
            avail = this.bufcount - this.bufpos;
            if (avail <= 0) {
                return -1;
            }
        }
        final int cnt = (avail < len) ? avail : len;
        System.arraycopy(this.buf, this.bufpos, b, off, cnt);
        this.bufpos += cnt;
        return cnt;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public synchronized int available() throws IOException {
        return this.bufcount - this.bufpos;
    }
    
    private void checkSeen() {
        if (this.peek) {
            return;
        }
        try {
            final Folder f = this.msg.getFolder();
            if (f != null && f.getMode() != 1 && !this.msg.isSet(Flags.Flag.SEEN)) {
                this.msg.setFlag(Flags.Flag.SEEN, true);
            }
        }
        catch (final MessagingException ex) {}
    }
}
