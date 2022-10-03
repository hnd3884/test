package com.sun.mail.pop3;

import java.io.InputStream;
import java.util.StringTokenizer;
import com.sun.mail.util.LineInputStream;
import javax.mail.MessageRemovedException;
import java.io.EOFException;
import javax.mail.FolderClosedException;
import javax.mail.UIDFolder;
import javax.mail.FetchProfile;
import java.lang.reflect.Constructor;
import javax.mail.Message;
import javax.mail.Flags;
import java.io.IOException;
import java.util.logging.Level;
import javax.mail.FolderNotFoundException;
import javax.mail.MethodNotSupportedException;
import javax.mail.MessagingException;
import javax.mail.Store;
import com.sun.mail.util.MailLogger;
import javax.mail.Folder;

public class POP3Folder extends Folder
{
    private String name;
    private POP3Store store;
    private volatile Protocol port;
    private int total;
    private int size;
    private boolean exists;
    private volatile boolean opened;
    private POP3Message[] message_cache;
    private boolean doneUidl;
    private volatile TempFile fileCache;
    private boolean forceClose;
    MailLogger logger;
    
    protected POP3Folder(final POP3Store store, final String name) {
        super(store);
        this.exists = false;
        this.opened = false;
        this.doneUidl = false;
        this.fileCache = null;
        this.name = name;
        this.store = store;
        if (name.equalsIgnoreCase("INBOX")) {
            this.exists = true;
        }
        this.logger = new MailLogger(this.getClass(), "DEBUG POP3", store.getSession().getDebug(), store.getSession().getDebugOut());
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getFullName() {
        return this.name;
    }
    
    @Override
    public Folder getParent() {
        return new DefaultFolder(this.store);
    }
    
    @Override
    public boolean exists() {
        return this.exists;
    }
    
    @Override
    public Folder[] list(final String pattern) throws MessagingException {
        throw new MessagingException("not a directory");
    }
    
    @Override
    public char getSeparator() {
        return '\0';
    }
    
    @Override
    public int getType() {
        return 1;
    }
    
    @Override
    public boolean create(final int type) throws MessagingException {
        return false;
    }
    
    @Override
    public boolean hasNewMessages() throws MessagingException {
        return false;
    }
    
    @Override
    public Folder getFolder(final String name) throws MessagingException {
        throw new MessagingException("not a directory");
    }
    
    @Override
    public boolean delete(final boolean recurse) throws MessagingException {
        throw new MethodNotSupportedException("delete");
    }
    
    @Override
    public boolean renameTo(final Folder f) throws MessagingException {
        throw new MethodNotSupportedException("renameTo");
    }
    
    @Override
    public synchronized void open(final int mode) throws MessagingException {
        this.checkClosed();
        if (!this.exists) {
            throw new FolderNotFoundException(this, "folder is not INBOX");
        }
        try {
            this.port = this.store.getPort(this);
            final Status s = this.port.stat();
            this.total = s.total;
            this.size = s.size;
            this.mode = mode;
            if (this.store.useFileCache) {
                try {
                    this.fileCache = new TempFile(this.store.fileCacheDir);
                }
                catch (final IOException ex) {
                    this.logger.log(Level.FINE, "failed to create file cache", ex);
                    throw ex;
                }
            }
            this.opened = true;
        }
        catch (final IOException ioex) {
            try {
                if (this.port != null) {
                    this.port.quit();
                }
            }
            catch (final IOException ex2) {}
            finally {
                this.port = null;
                this.store.closePort(this);
            }
            throw new MessagingException("Open failed", ioex);
        }
        this.message_cache = new POP3Message[this.total];
        this.doneUidl = false;
        this.notifyConnectionListeners(1);
    }
    
    @Override
    public synchronized void close(final boolean expunge) throws MessagingException {
        this.checkOpen();
        try {
            if (this.store.rsetBeforeQuit && !this.forceClose) {
                this.port.rset();
            }
            if (expunge && this.mode == 2 && !this.forceClose) {
                for (int i = 0; i < this.message_cache.length; ++i) {
                    final POP3Message m;
                    if ((m = this.message_cache[i]) != null && m.isSet(Flags.Flag.DELETED)) {
                        try {
                            this.port.dele(i + 1);
                        }
                        catch (final IOException ioex) {
                            throw new MessagingException("Exception deleting messages during close", ioex);
                        }
                    }
                }
            }
            for (int i = 0; i < this.message_cache.length; ++i) {
                final POP3Message m;
                if ((m = this.message_cache[i]) != null) {
                    m.invalidate(true);
                }
            }
            if (this.forceClose) {
                this.port.close();
            }
            else {
                this.port.quit();
            }
        }
        catch (final IOException ex) {}
        finally {
            this.port = null;
            this.store.closePort(this);
            this.message_cache = null;
            this.opened = false;
            this.notifyConnectionListeners(3);
            if (this.fileCache != null) {
                this.fileCache.close();
                this.fileCache = null;
            }
        }
    }
    
    @Override
    public synchronized boolean isOpen() {
        if (!this.opened) {
            return false;
        }
        try {
            if (!this.port.noop()) {
                throw new IOException("NOOP failed");
            }
        }
        catch (final IOException ioex) {
            try {
                this.close(false);
            }
            catch (final MessagingException ex) {}
            return false;
        }
        return true;
    }
    
    @Override
    public Flags getPermanentFlags() {
        return new Flags();
    }
    
    @Override
    public synchronized int getMessageCount() throws MessagingException {
        if (!this.opened) {
            return -1;
        }
        this.checkReadable();
        return this.total;
    }
    
    @Override
    public synchronized Message getMessage(final int msgno) throws MessagingException {
        this.checkOpen();
        POP3Message m;
        if ((m = this.message_cache[msgno - 1]) == null) {
            m = this.createMessage(this, msgno);
            this.message_cache[msgno - 1] = m;
        }
        return m;
    }
    
    protected POP3Message createMessage(final Folder f, final int msgno) throws MessagingException {
        POP3Message m = null;
        final Constructor<?> cons = this.store.messageConstructor;
        if (cons != null) {
            try {
                final Object[] o = { this, msgno };
                m = (POP3Message)cons.newInstance(o);
            }
            catch (final Exception ex) {}
        }
        if (m == null) {
            m = new POP3Message(this, msgno);
        }
        return m;
    }
    
    @Override
    public void appendMessages(final Message[] msgs) throws MessagingException {
        throw new MethodNotSupportedException("Append not supported");
    }
    
    @Override
    public Message[] expunge() throws MessagingException {
        throw new MethodNotSupportedException("Expunge not supported");
    }
    
    @Override
    public synchronized void fetch(final Message[] msgs, final FetchProfile fp) throws MessagingException {
        this.checkReadable();
        if (!this.doneUidl && this.store.supportsUidl && fp.contains(UIDFolder.FetchProfileItem.UID)) {
            final String[] uids = new String[this.message_cache.length];
            try {
                if (!this.port.uidl(uids)) {
                    return;
                }
            }
            catch (final EOFException eex) {
                this.close(false);
                throw new FolderClosedException(this, eex.toString());
            }
            catch (final IOException ex) {
                throw new MessagingException("error getting UIDL", ex);
            }
            for (int i = 0; i < uids.length; ++i) {
                if (uids[i] != null) {
                    final POP3Message m = (POP3Message)this.getMessage(i + 1);
                    m.uid = uids[i];
                }
            }
            this.doneUidl = true;
        }
        if (fp.contains(FetchProfile.Item.ENVELOPE)) {
            for (int j = 0; j < msgs.length; ++j) {
                try {
                    final POP3Message msg = (POP3Message)msgs[j];
                    msg.getHeader("");
                    msg.getSize();
                }
                catch (final MessageRemovedException ex2) {}
            }
        }
    }
    
    public synchronized String getUID(final Message msg) throws MessagingException {
        this.checkOpen();
        if (!(msg instanceof POP3Message)) {
            throw new MessagingException("message is not a POP3Message");
        }
        final POP3Message m = (POP3Message)msg;
        try {
            if (!this.store.supportsUidl) {
                return null;
            }
            if (m.uid == "UNKNOWN") {
                m.uid = this.port.uidl(m.getMessageNumber());
            }
            return m.uid;
        }
        catch (final EOFException eex) {
            this.close(false);
            throw new FolderClosedException(this, eex.toString());
        }
        catch (final IOException ex) {
            throw new MessagingException("error getting UIDL", ex);
        }
    }
    
    public synchronized int getSize() throws MessagingException {
        this.checkOpen();
        return this.size;
    }
    
    public synchronized int[] getSizes() throws MessagingException {
        this.checkOpen();
        final int[] sizes = new int[this.total];
        InputStream is = null;
        LineInputStream lis = null;
        try {
            is = this.port.list();
            lis = new LineInputStream(is);
            String line;
            while ((line = lis.readLine()) != null) {
                try {
                    final StringTokenizer st = new StringTokenizer(line);
                    final int msgnum = Integer.parseInt(st.nextToken());
                    final int size = Integer.parseInt(st.nextToken());
                    if (msgnum <= 0 || msgnum > this.total) {
                        continue;
                    }
                    sizes[msgnum - 1] = size;
                }
                catch (final RuntimeException ex) {}
            }
        }
        catch (final IOException ex2) {}
        finally {
            try {
                if (lis != null) {
                    lis.close();
                }
            }
            catch (final IOException ex3) {}
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final IOException ex4) {}
        }
        return sizes;
    }
    
    public synchronized InputStream listCommand() throws MessagingException, IOException {
        this.checkOpen();
        return this.port.list();
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.forceClose = !this.store.finalizeCleanClose;
        try {
            if (this.opened) {
                this.close(false);
            }
        }
        finally {
            super.finalize();
            this.forceClose = false;
        }
    }
    
    private void checkOpen() throws IllegalStateException {
        if (!this.opened) {
            throw new IllegalStateException("Folder is not Open");
        }
    }
    
    private void checkClosed() throws IllegalStateException {
        if (this.opened) {
            throw new IllegalStateException("Folder is Open");
        }
    }
    
    private void checkReadable() throws IllegalStateException {
        if (!this.opened || (this.mode != 1 && this.mode != 2)) {
            throw new IllegalStateException("Folder is not Readable");
        }
    }
    
    Protocol getProtocol() throws MessagingException {
        final Protocol p = this.port;
        this.checkOpen();
        return p;
    }
    
    @Override
    protected void notifyMessageChangedListeners(final int type, final Message m) {
        super.notifyMessageChangedListeners(type, m);
    }
    
    TempFile getFileCache() {
        return this.fileCache;
    }
}
