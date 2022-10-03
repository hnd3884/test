package com.sun.mail.pop3;

import javax.mail.Header;
import java.util.Enumeration;
import javax.mail.IllegalWriteException;
import javax.mail.internet.SharedInputStream;
import javax.mail.internet.InternetHeaders;
import javax.mail.MessageRemovedException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.util.logging.Level;
import java.io.IOException;
import java.io.EOFException;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.Folder;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import com.sun.mail.util.ReadableMime;
import javax.mail.internet.MimeMessage;

public class POP3Message extends MimeMessage implements ReadableMime
{
    static final String UNKNOWN = "UNKNOWN";
    private POP3Folder folder;
    private int hdrSize;
    private int msgSize;
    String uid;
    private SoftReference<InputStream> rawData;
    
    public POP3Message(final Folder folder, final int msgno) throws MessagingException {
        super(folder, msgno);
        this.hdrSize = -1;
        this.msgSize = -1;
        this.uid = "UNKNOWN";
        this.rawData = new SoftReference<InputStream>(null);
        assert folder instanceof POP3Folder;
        this.folder = (POP3Folder)folder;
    }
    
    @Override
    public synchronized void setFlags(final Flags newFlags, final boolean set) throws MessagingException {
        final Flags oldFlags = (Flags)this.flags.clone();
        super.setFlags(newFlags, set);
        if (!this.flags.equals(oldFlags)) {
            this.folder.notifyMessageChangedListeners(1, this);
        }
    }
    
    @Override
    public int getSize() throws MessagingException {
        try {
            synchronized (this) {
                if (this.msgSize > 0) {
                    return this.msgSize;
                }
            }
            if (this.headers == null) {
                this.loadHeaders();
            }
            synchronized (this) {
                if (this.msgSize < 0) {
                    this.msgSize = this.folder.getProtocol().list(this.msgnum) - this.hdrSize;
                }
                return this.msgSize;
            }
        }
        catch (final EOFException eex) {
            this.folder.close(false);
            throw new FolderClosedException(this.folder, eex.toString());
        }
        catch (final IOException ex) {
            throw new MessagingException("error getting size", ex);
        }
    }
    
    private InputStream getRawStream(final boolean skipHeader) throws MessagingException {
        InputStream rawcontent = null;
        try {
            synchronized (this) {
                rawcontent = this.rawData.get();
                if (rawcontent == null) {
                    final TempFile cache = this.folder.getFileCache();
                    if (cache != null) {
                        if (this.folder.logger.isLoggable(Level.FINE)) {
                            this.folder.logger.fine("caching message #" + this.msgnum + " in temp file");
                        }
                        final AppendStream os = cache.getAppendStream();
                        final BufferedOutputStream bos = new BufferedOutputStream(os);
                        try {
                            this.folder.getProtocol().retr(this.msgnum, bos);
                        }
                        finally {
                            bos.close();
                        }
                        rawcontent = os.getInputStream();
                    }
                    else {
                        rawcontent = this.folder.getProtocol().retr(this.msgnum, (this.msgSize > 0) ? (this.msgSize + this.hdrSize) : 0);
                    }
                    if (rawcontent == null) {
                        this.expunged = true;
                        throw new MessageRemovedException("can't retrieve message #" + this.msgnum + " in POP3Message.getContentStream");
                    }
                    if (this.headers == null || ((POP3Store)this.folder.getStore()).forgetTopHeaders) {
                        this.headers = new InternetHeaders(rawcontent);
                        this.hdrSize = (int)((SharedInputStream)rawcontent).getPosition();
                    }
                    else {
                        final int offset = 0;
                        int len;
                        do {
                            len = 0;
                            int c1;
                            while ((c1 = rawcontent.read()) >= 0) {
                                if (c1 == 10) {
                                    break;
                                }
                                if (c1 == 13) {
                                    if (rawcontent.available() <= 0) {
                                        break;
                                    }
                                    rawcontent.mark(1);
                                    if (rawcontent.read() != 10) {
                                        rawcontent.reset();
                                        break;
                                    }
                                    break;
                                }
                                else {
                                    ++len;
                                }
                            }
                            if (rawcontent.available() == 0) {
                                break;
                            }
                        } while (len != 0);
                        this.hdrSize = (int)((SharedInputStream)rawcontent).getPosition();
                    }
                    this.msgSize = rawcontent.available();
                    this.rawData = new SoftReference<InputStream>(rawcontent);
                }
            }
        }
        catch (final EOFException eex) {
            this.folder.close(false);
            throw new FolderClosedException(this.folder, eex.toString());
        }
        catch (final IOException ex) {
            throw new MessagingException("error fetching POP3 content", ex);
        }
        rawcontent = ((SharedInputStream)rawcontent).newStream(skipHeader ? ((long)this.hdrSize) : 0L, -1L);
        return rawcontent;
    }
    
    @Override
    protected synchronized InputStream getContentStream() throws MessagingException {
        if (this.contentStream != null) {
            return ((SharedInputStream)this.contentStream).newStream(0L, -1L);
        }
        final InputStream cstream = this.getRawStream(true);
        final TempFile cache = this.folder.getFileCache();
        if (cache != null || ((POP3Store)this.folder.getStore()).keepMessageContent) {
            this.contentStream = ((SharedInputStream)cstream).newStream(0L, -1L);
        }
        return cstream;
    }
    
    @Override
    public InputStream getMimeStream() throws MessagingException {
        return this.getRawStream(false);
    }
    
    public synchronized void invalidate(final boolean invalidateHeaders) {
        this.content = null;
        final InputStream rstream = this.rawData.get();
        if (rstream != null) {
            try {
                rstream.close();
            }
            catch (final IOException ex) {}
            this.rawData = new SoftReference<InputStream>(null);
        }
        if (this.contentStream != null) {
            try {
                this.contentStream.close();
            }
            catch (final IOException ex2) {}
            this.contentStream = null;
        }
        this.msgSize = -1;
        if (invalidateHeaders) {
            this.headers = null;
            this.hdrSize = -1;
        }
    }
    
    public InputStream top(final int n) throws MessagingException {
        try {
            synchronized (this) {
                return this.folder.getProtocol().top(this.msgnum, n);
            }
        }
        catch (final EOFException eex) {
            this.folder.close(false);
            throw new FolderClosedException(this.folder, eex.toString());
        }
        catch (final IOException ex) {
            throw new MessagingException("error getting size", ex);
        }
    }
    
    @Override
    public String[] getHeader(final String name) throws MessagingException {
        if (this.headers == null) {
            this.loadHeaders();
        }
        return this.headers.getHeader(name);
    }
    
    @Override
    public String getHeader(final String name, final String delimiter) throws MessagingException {
        if (this.headers == null) {
            this.loadHeaders();
        }
        return this.headers.getHeader(name, delimiter);
    }
    
    @Override
    public void setHeader(final String name, final String value) throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }
    
    @Override
    public void addHeader(final String name, final String value) throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }
    
    @Override
    public void removeHeader(final String name) throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }
    
    @Override
    public Enumeration<Header> getAllHeaders() throws MessagingException {
        if (this.headers == null) {
            this.loadHeaders();
        }
        return this.headers.getAllHeaders();
    }
    
    @Override
    public Enumeration<Header> getMatchingHeaders(final String[] names) throws MessagingException {
        if (this.headers == null) {
            this.loadHeaders();
        }
        return this.headers.getMatchingHeaders(names);
    }
    
    @Override
    public Enumeration<Header> getNonMatchingHeaders(final String[] names) throws MessagingException {
        if (this.headers == null) {
            this.loadHeaders();
        }
        return this.headers.getNonMatchingHeaders(names);
    }
    
    @Override
    public void addHeaderLine(final String line) throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }
    
    @Override
    public Enumeration<String> getAllHeaderLines() throws MessagingException {
        if (this.headers == null) {
            this.loadHeaders();
        }
        return this.headers.getAllHeaderLines();
    }
    
    @Override
    public Enumeration<String> getMatchingHeaderLines(final String[] names) throws MessagingException {
        if (this.headers == null) {
            this.loadHeaders();
        }
        return this.headers.getMatchingHeaderLines(names);
    }
    
    @Override
    public Enumeration<String> getNonMatchingHeaderLines(final String[] names) throws MessagingException {
        if (this.headers == null) {
            this.loadHeaders();
        }
        return this.headers.getNonMatchingHeaderLines(names);
    }
    
    @Override
    public void saveChanges() throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }
    
    @Override
    public synchronized void writeTo(final OutputStream os, final String[] ignoreList) throws IOException, MessagingException {
        final InputStream rawcontent = this.rawData.get();
        if (rawcontent == null && ignoreList == null && !((POP3Store)this.folder.getStore()).cacheWriteTo) {
            if (this.folder.logger.isLoggable(Level.FINE)) {
                this.folder.logger.fine("streaming msg " + this.msgnum);
            }
            if (!this.folder.getProtocol().retr(this.msgnum, os)) {
                this.expunged = true;
                throw new MessageRemovedException("can't retrieve message #" + this.msgnum + " in POP3Message.writeTo");
            }
        }
        else if (rawcontent != null && ignoreList == null) {
            final InputStream in = ((SharedInputStream)rawcontent).newStream(0L, -1L);
            try {
                final byte[] buf = new byte[16384];
                int len;
                while ((len = in.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
            }
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (final IOException ex) {}
            }
        }
        else {
            super.writeTo(os, ignoreList);
        }
    }
    
    private void loadHeaders() throws MessagingException {
        assert !Thread.holdsLock(this);
        try {
            boolean fetchContent = false;
            synchronized (this) {
                if (this.headers != null) {
                    return;
                }
                InputStream hdrs = null;
                if (((POP3Store)this.folder.getStore()).disableTop || (hdrs = this.folder.getProtocol().top(this.msgnum, 0)) == null) {
                    fetchContent = true;
                }
                else {
                    try {
                        this.hdrSize = hdrs.available();
                        this.headers = new InternetHeaders(hdrs);
                    }
                    finally {
                        hdrs.close();
                    }
                }
            }
            if (fetchContent) {
                InputStream cs = null;
                try {
                    cs = this.getContentStream();
                }
                finally {
                    if (cs != null) {
                        cs.close();
                    }
                }
            }
        }
        catch (final EOFException eex) {
            this.folder.close(false);
            throw new FolderClosedException(this.folder, eex.toString());
        }
        catch (final IOException ex) {
            throw new MessagingException("error loading POP3 headers", ex);
        }
    }
}
