package com.sun.mail.imap;

import com.sun.mail.imap.protocol.FLAGS;
import com.sun.mail.imap.protocol.MODSEQ;
import java.util.Map;
import java.nio.channels.SocketChannel;
import java.net.SocketTimeoutException;
import java.io.InterruptedIOException;
import javax.mail.Quota;
import com.sun.mail.imap.protocol.UID;
import java.util.NoSuchElementException;
import javax.mail.event.MessageCountListener;
import javax.mail.search.SearchException;
import javax.mail.internet.MimeMessage;
import com.sun.mail.iap.Literal;
import java.util.Date;
import java.io.IOException;
import javax.mail.search.SearchTerm;
import javax.mail.search.FlagTerm;
import java.util.logging.Level;
import javax.mail.MessageRemovedException;
import com.sun.mail.imap.protocol.Item;
import com.sun.mail.imap.protocol.MessageSet;
import com.sun.mail.imap.protocol.FetchItem;
import com.sun.mail.iap.Response;
import javax.mail.FetchProfile;
import javax.mail.Message;
import java.util.Iterator;
import com.sun.mail.imap.protocol.MailboxInfo;
import javax.mail.event.MessageChangedEvent;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.UIDSet;
import com.sun.mail.imap.protocol.IMAPResponse;
import java.util.ArrayList;
import javax.mail.ReadOnlyFolderException;
import com.sun.mail.iap.CommandFailedException;
import javax.mail.event.MailEvent;
import java.util.List;
import javax.mail.StoreClosedException;
import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.ConnectionException;
import javax.mail.FolderClosedException;
import javax.mail.MessagingException;
import javax.mail.FolderNotFoundException;
import com.sun.mail.imap.protocol.ListInfo;
import javax.mail.Store;
import com.sun.mail.util.MailLogger;
import com.sun.mail.imap.protocol.Status;
import java.util.Hashtable;
import com.sun.mail.imap.protocol.IMAPProtocol;
import javax.mail.Flags;
import com.sun.mail.iap.ResponseHandler;
import javax.mail.UIDFolder;
import javax.mail.Folder;

public class IMAPFolder extends Folder implements UIDFolder, ResponseHandler
{
    protected volatile String fullName;
    protected String name;
    protected int type;
    protected char separator;
    protected Flags availableFlags;
    protected Flags permanentFlags;
    protected volatile boolean exists;
    protected boolean isNamespace;
    protected volatile String[] attributes;
    protected volatile IMAPProtocol protocol;
    protected MessageCache messageCache;
    protected final Object messageCacheLock;
    protected Hashtable<Long, IMAPMessage> uidTable;
    protected static final char UNKNOWN_SEPARATOR = '\uffff';
    private volatile boolean opened;
    private boolean reallyClosed;
    private static final int RUNNING = 0;
    private static final int IDLE = 1;
    private static final int ABORTING = 2;
    private int idleState;
    private IdleManager idleManager;
    private volatile int total;
    private volatile int recent;
    private int realTotal;
    private long uidvalidity;
    private long uidnext;
    private boolean uidNotSticky;
    private volatile long highestmodseq;
    private boolean doExpungeNotification;
    private Status cachedStatus;
    private long cachedStatusTime;
    private boolean hasMessageCountListener;
    protected MailLogger logger;
    private MailLogger connectionPoolLogger;
    
    protected IMAPFolder(final String fullName, final char separator, final IMAPStore store, final Boolean isNamespace) {
        super(store);
        this.isNamespace = false;
        this.messageCacheLock = new Object();
        this.opened = false;
        this.reallyClosed = true;
        this.idleState = 0;
        this.total = -1;
        this.recent = -1;
        this.realTotal = -1;
        this.uidvalidity = -1L;
        this.uidnext = -1L;
        this.uidNotSticky = false;
        this.highestmodseq = -1L;
        this.doExpungeNotification = true;
        this.cachedStatus = null;
        this.cachedStatusTime = 0L;
        this.hasMessageCountListener = false;
        if (fullName == null) {
            throw new NullPointerException("Folder name is null");
        }
        this.fullName = fullName;
        this.separator = separator;
        this.logger = new MailLogger(this.getClass(), "DEBUG IMAP", store.getSession().getDebug(), store.getSession().getDebugOut());
        this.connectionPoolLogger = store.getConnectionPoolLogger();
        this.isNamespace = false;
        if (separator != '\uffff' && separator != '\0') {
            final int i = this.fullName.indexOf(separator);
            if (i > 0 && i == this.fullName.length() - 1) {
                this.fullName = this.fullName.substring(0, i);
                this.isNamespace = true;
            }
        }
        if (isNamespace != null) {
            this.isNamespace = isNamespace;
        }
    }
    
    protected IMAPFolder(final ListInfo li, final IMAPStore store) {
        this(li.name, li.separator, store, null);
        if (li.hasInferiors) {
            this.type |= 0x2;
        }
        if (li.canOpen) {
            this.type |= 0x1;
        }
        this.exists = true;
        this.attributes = li.attrs;
    }
    
    protected void checkExists() throws MessagingException {
        if (!this.exists && !this.exists()) {
            throw new FolderNotFoundException(this, this.fullName + " not found");
        }
    }
    
    protected void checkClosed() {
        if (this.opened) {
            throw new IllegalStateException("This operation is not allowed on an open folder");
        }
    }
    
    protected void checkOpened() throws FolderClosedException {
        assert Thread.holdsLock(this);
        if (this.opened) {
            return;
        }
        if (this.reallyClosed) {
            throw new IllegalStateException("This operation is not allowed on a closed folder");
        }
        throw new FolderClosedException(this, "Lost folder connection to server");
    }
    
    protected void checkRange(final int msgno) throws MessagingException {
        if (msgno < 1) {
            throw new IndexOutOfBoundsException("message number < 1");
        }
        if (msgno <= this.total) {
            return;
        }
        synchronized (this.messageCacheLock) {
            try {
                this.keepConnectionAlive(false);
            }
            catch (final ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (msgno > this.total) {
            throw new IndexOutOfBoundsException(msgno + " > " + this.total);
        }
    }
    
    private void checkFlags(final Flags flags) throws MessagingException {
        assert Thread.holdsLock(this);
        if (this.mode != 2) {
            throw new IllegalStateException("Cannot change flags on READ_ONLY folder: " + this.fullName);
        }
    }
    
    @Override
    public synchronized String getName() {
        if (this.name == null) {
            try {
                this.name = this.fullName.substring(this.fullName.lastIndexOf(this.getSeparator()) + 1);
            }
            catch (final MessagingException ex) {}
        }
        return this.name;
    }
    
    @Override
    public String getFullName() {
        return this.fullName;
    }
    
    @Override
    public synchronized Folder getParent() throws MessagingException {
        final char c = this.getSeparator();
        final int index;
        if ((index = this.fullName.lastIndexOf(c)) != -1) {
            return ((IMAPStore)this.store).newIMAPFolder(this.fullName.substring(0, index), c);
        }
        return new DefaultFolder((IMAPStore)this.store);
    }
    
    @Override
    public synchronized boolean exists() throws MessagingException {
        ListInfo[] li = null;
        String lname;
        if (this.isNamespace && this.separator != '\0') {
            lname = this.fullName + this.separator;
        }
        else {
            lname = this.fullName;
        }
        li = (ListInfo[])this.doCommand(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.list("", lname);
            }
        });
        if (li != null) {
            final int i = this.findName(li, lname);
            this.fullName = li[i].name;
            this.separator = li[i].separator;
            final int len = this.fullName.length();
            if (this.separator != '\0' && len > 0 && this.fullName.charAt(len - 1) == this.separator) {
                this.fullName = this.fullName.substring(0, len - 1);
            }
            this.type = 0;
            if (li[i].hasInferiors) {
                this.type |= 0x2;
            }
            if (li[i].canOpen) {
                this.type |= 0x1;
            }
            this.exists = true;
            this.attributes = li[i].attrs;
        }
        else {
            this.exists = this.opened;
            this.attributes = null;
        }
        return this.exists;
    }
    
    private int findName(final ListInfo[] li, final String lname) {
        int i;
        for (i = 0; i < li.length && !li[i].name.equals(lname); ++i) {}
        if (i >= li.length) {
            i = 0;
        }
        return i;
    }
    
    @Override
    public Folder[] list(final String pattern) throws MessagingException {
        return this.doList(pattern, false);
    }
    
    @Override
    public Folder[] listSubscribed(final String pattern) throws MessagingException {
        return this.doList(pattern, true);
    }
    
    private synchronized Folder[] doList(final String pattern, final boolean subscribed) throws MessagingException {
        this.checkExists();
        if (this.attributes != null && !this.isDirectory()) {
            return new Folder[0];
        }
        final char c = this.getSeparator();
        final ListInfo[] li = (ListInfo[])this.doCommandIgnoreFailure(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                if (subscribed) {
                    return p.lsub("", IMAPFolder.this.fullName + c + pattern);
                }
                return p.list("", IMAPFolder.this.fullName + c + pattern);
            }
        });
        if (li == null) {
            return new Folder[0];
        }
        int start = 0;
        if (li.length > 0 && li[0].name.equals(this.fullName + c)) {
            start = 1;
        }
        final IMAPFolder[] folders = new IMAPFolder[li.length - start];
        final IMAPStore st = (IMAPStore)this.store;
        for (int i = start; i < li.length; ++i) {
            folders[i - start] = st.newIMAPFolder(li[i]);
        }
        return folders;
    }
    
    @Override
    public synchronized char getSeparator() throws MessagingException {
        if (this.separator == '\uffff') {
            ListInfo[] li = null;
            li = (ListInfo[])this.doCommand(new ProtocolCommand() {
                @Override
                public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                    if (p.isREV1()) {
                        return p.list(IMAPFolder.this.fullName, "");
                    }
                    return p.list("", IMAPFolder.this.fullName);
                }
            });
            if (li != null) {
                this.separator = li[0].separator;
            }
            else {
                this.separator = '/';
            }
        }
        return this.separator;
    }
    
    @Override
    public synchronized int getType() throws MessagingException {
        if (this.opened) {
            if (this.attributes == null) {
                this.exists();
            }
        }
        else {
            this.checkExists();
        }
        return this.type;
    }
    
    @Override
    public synchronized boolean isSubscribed() {
        ListInfo[] li = null;
        String lname;
        if (this.isNamespace && this.separator != '\0') {
            lname = this.fullName + this.separator;
        }
        else {
            lname = this.fullName;
        }
        try {
            li = (ListInfo[])this.doProtocolCommand(new ProtocolCommand() {
                @Override
                public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                    return p.lsub("", lname);
                }
            });
        }
        catch (final ProtocolException ex) {}
        if (li != null) {
            final int i = this.findName(li, lname);
            return li[i].canOpen;
        }
        return false;
    }
    
    @Override
    public synchronized void setSubscribed(final boolean subscribe) throws MessagingException {
        this.doCommandIgnoreFailure(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                if (subscribe) {
                    p.subscribe(IMAPFolder.this.fullName);
                }
                else {
                    p.unsubscribe(IMAPFolder.this.fullName);
                }
                return null;
            }
        });
    }
    
    @Override
    public synchronized boolean create(final int type) throws MessagingException {
        char c = '\0';
        if ((type & 0x1) == 0x0) {
            c = this.getSeparator();
        }
        final char sep = c;
        final Object ret = this.doCommandIgnoreFailure(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                if ((type & 0x1) == 0x0) {
                    p.create(IMAPFolder.this.fullName + sep);
                }
                else {
                    p.create(IMAPFolder.this.fullName);
                    if ((type & 0x2) != 0x0) {
                        final ListInfo[] li = p.list("", IMAPFolder.this.fullName);
                        if (li != null && !li[0].hasInferiors) {
                            p.delete(IMAPFolder.this.fullName);
                            throw new ProtocolException("Unsupported type");
                        }
                    }
                }
                return Boolean.TRUE;
            }
        });
        if (ret == null) {
            return false;
        }
        final boolean retb = this.exists();
        if (retb) {
            this.notifyFolderListeners(1);
        }
        return retb;
    }
    
    @Override
    public synchronized boolean hasNewMessages() throws MessagingException {
        synchronized (this.messageCacheLock) {
            if (this.opened) {
                try {
                    this.keepConnectionAlive(true);
                }
                catch (final ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                }
                catch (final ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
                return this.recent > 0;
            }
        }
        ListInfo[] li = null;
        String lname;
        if (this.isNamespace && this.separator != '\0') {
            lname = this.fullName + this.separator;
        }
        else {
            lname = this.fullName;
        }
        li = (ListInfo[])this.doCommandIgnoreFailure(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.list("", lname);
            }
        });
        if (li == null) {
            throw new FolderNotFoundException(this, this.fullName + " not found");
        }
        final int i = this.findName(li, lname);
        if (li[i].changeState == 1) {
            return true;
        }
        if (li[i].changeState == 2) {
            return false;
        }
        try {
            final Status status = this.getStatus();
            return status.recent > 0;
        }
        catch (final BadCommandException bex) {
            return false;
        }
        catch (final ConnectionException cex2) {
            throw new StoreClosedException(this.store, cex2.getMessage());
        }
        catch (final ProtocolException pex2) {
            throw new MessagingException(pex2.getMessage(), pex2);
        }
    }
    
    @Override
    public synchronized Folder getFolder(final String name) throws MessagingException {
        if (this.attributes != null && !this.isDirectory()) {
            throw new MessagingException("Cannot contain subfolders");
        }
        final char c = this.getSeparator();
        return ((IMAPStore)this.store).newIMAPFolder(this.fullName + c + name, c);
    }
    
    @Override
    public synchronized boolean delete(final boolean recurse) throws MessagingException {
        this.checkClosed();
        if (recurse) {
            final Folder[] f = this.list();
            for (int i = 0; i < f.length; ++i) {
                f[i].delete(recurse);
            }
        }
        final Object ret = this.doCommandIgnoreFailure(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                p.delete(IMAPFolder.this.fullName);
                return Boolean.TRUE;
            }
        });
        if (ret == null) {
            return false;
        }
        this.exists = false;
        this.attributes = null;
        this.notifyFolderListeners(2);
        return true;
    }
    
    @Override
    public synchronized boolean renameTo(final Folder f) throws MessagingException {
        this.checkClosed();
        this.checkExists();
        if (f.getStore() != this.store) {
            throw new MessagingException("Can't rename across Stores");
        }
        final Object ret = this.doCommandIgnoreFailure(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                p.rename(IMAPFolder.this.fullName, f.getFullName());
                return Boolean.TRUE;
            }
        });
        if (ret == null) {
            return false;
        }
        this.exists = false;
        this.attributes = null;
        this.notifyFolderRenamedListeners(f);
        return true;
    }
    
    @Override
    public synchronized void open(final int mode) throws MessagingException {
        this.open(mode, null);
    }
    
    public synchronized List<MailEvent> open(final int mode, final ResyncData rd) throws MessagingException {
        this.checkClosed();
        MailboxInfo mi = null;
        this.protocol = ((IMAPStore)this.store).getProtocol(this);
        List<MailEvent> openEvents = null;
        synchronized (this.messageCacheLock) {
            this.protocol.addResponseHandler(this);
            try {
                if (rd != null) {
                    if (rd == ResyncData.CONDSTORE) {
                        if (!this.protocol.isEnabled("CONDSTORE") && !this.protocol.isEnabled("QRESYNC")) {
                            if (this.protocol.hasCapability("CONDSTORE")) {
                                this.protocol.enable("CONDSTORE");
                            }
                            else {
                                this.protocol.enable("QRESYNC");
                            }
                        }
                    }
                    else if (!this.protocol.isEnabled("QRESYNC")) {
                        this.protocol.enable("QRESYNC");
                    }
                }
                if (mode == 1) {
                    mi = this.protocol.examine(this.fullName, rd);
                }
                else {
                    mi = this.protocol.select(this.fullName, rd);
                }
            }
            catch (final CommandFailedException cex) {
                try {
                    this.checkExists();
                    if ((this.type & 0x1) == 0x0) {
                        throw new MessagingException("folder cannot contain messages");
                    }
                    throw new MessagingException(cex.getMessage(), cex);
                }
                finally {
                    this.exists = false;
                    this.attributes = null;
                    this.type = 0;
                    this.releaseProtocol(true);
                }
            }
            catch (final ProtocolException pex) {
                try {
                    throw this.logoutAndThrow(pex.getMessage(), pex);
                }
                finally {
                    this.releaseProtocol(false);
                }
            }
            if (mi.mode != mode && (mode != 2 || mi.mode != 1 || !((IMAPStore)this.store).allowReadOnlySelect())) {
                final ReadOnlyFolderException ife = new ReadOnlyFolderException(this, "Cannot open in desired mode");
                throw this.cleanupAndThrow(ife);
            }
            this.opened = true;
            this.reallyClosed = false;
            this.mode = mi.mode;
            this.availableFlags = mi.availableFlags;
            this.permanentFlags = mi.permanentFlags;
            final int total = mi.total;
            this.realTotal = total;
            this.total = total;
            this.recent = mi.recent;
            this.uidvalidity = mi.uidvalidity;
            this.uidnext = mi.uidnext;
            this.uidNotSticky = mi.uidNotSticky;
            this.highestmodseq = mi.highestmodseq;
            this.messageCache = new MessageCache(this, (IMAPStore)this.store, this.total);
            if (mi.responses != null) {
                openEvents = new ArrayList<MailEvent>();
                for (final IMAPResponse ir : mi.responses) {
                    if (ir.keyEquals("VANISHED")) {
                        final String[] s = ir.readAtomStringList();
                        if (s == null || s.length != 1) {
                            continue;
                        }
                        if (!s[0].equalsIgnoreCase("EARLIER")) {
                            continue;
                        }
                        final String uids = ir.readAtom();
                        final UIDSet[] uidset = UIDSet.parseUIDSets(uids);
                        final long[] luid = UIDSet.toArray(uidset, this.uidnext);
                        if (luid == null || luid.length <= 0) {
                            continue;
                        }
                        openEvents.add(new MessageVanishedEvent(this, luid));
                    }
                    else {
                        if (!ir.keyEquals("FETCH")) {
                            continue;
                        }
                        assert ir instanceof FetchResponse : "!ir instanceof FetchResponse";
                        final Message msg = this.processFetchResponse((FetchResponse)ir);
                        if (msg == null) {
                            continue;
                        }
                        openEvents.add(new MessageChangedEvent(this, 1, msg));
                    }
                }
            }
        }
        this.exists = true;
        this.attributes = null;
        this.notifyConnectionListeners(this.type = 1);
        return openEvents;
    }
    
    private MessagingException cleanupAndThrow(final MessagingException ife) {
        try {
            try {
                this.protocol.close();
                this.releaseProtocol(true);
            }
            catch (final ProtocolException pex) {
                try {
                    this.addSuppressed(ife, this.logoutAndThrow(pex.getMessage(), pex));
                }
                finally {
                    this.releaseProtocol(false);
                }
            }
        }
        catch (final Throwable thr) {
            this.addSuppressed(ife, thr);
        }
        return ife;
    }
    
    private MessagingException logoutAndThrow(final String why, final ProtocolException t) {
        final MessagingException ife = new MessagingException(why, t);
        try {
            this.protocol.logout();
        }
        catch (final Throwable thr) {
            this.addSuppressed(ife, thr);
        }
        return ife;
    }
    
    private void addSuppressed(final Throwable ife, final Throwable thr) {
        if (this.isRecoverable(thr)) {
            ife.addSuppressed(thr);
            return;
        }
        thr.addSuppressed(ife);
        if (thr instanceof Error) {
            throw (Error)thr;
        }
        if (thr instanceof RuntimeException) {
            throw (RuntimeException)thr;
        }
        throw new RuntimeException("unexpected exception", thr);
    }
    
    private boolean isRecoverable(final Throwable t) {
        return t instanceof Exception || t instanceof LinkageError;
    }
    
    @Override
    public synchronized void fetch(final Message[] msgs, final FetchProfile fp) throws MessagingException {
        final boolean isRev1;
        final FetchItem[] fitems;
        synchronized (this.messageCacheLock) {
            this.checkOpened();
            isRev1 = this.protocol.isREV1();
            fitems = this.protocol.getFetchItems();
        }
        final StringBuilder command = new StringBuilder();
        boolean first = true;
        boolean allHeaders = false;
        if (fp.contains(FetchProfile.Item.ENVELOPE)) {
            command.append(this.getEnvelopeCommand());
            first = false;
        }
        if (fp.contains(FetchProfile.Item.FLAGS)) {
            command.append(first ? "FLAGS" : " FLAGS");
            first = false;
        }
        if (fp.contains(FetchProfile.Item.CONTENT_INFO)) {
            command.append(first ? "BODYSTRUCTURE" : " BODYSTRUCTURE");
            first = false;
        }
        if (fp.contains(UIDFolder.FetchProfileItem.UID)) {
            command.append(first ? "UID" : " UID");
            first = false;
        }
        if (fp.contains(FetchProfileItem.HEADERS)) {
            allHeaders = true;
            if (isRev1) {
                command.append(first ? "BODY.PEEK[HEADER]" : " BODY.PEEK[HEADER]");
            }
            else {
                command.append(first ? "RFC822.HEADER" : " RFC822.HEADER");
            }
            first = false;
        }
        if (fp.contains(FetchProfileItem.MESSAGE)) {
            allHeaders = true;
            if (isRev1) {
                command.append(first ? "BODY.PEEK[]" : " BODY.PEEK[]");
            }
            else {
                command.append(first ? "RFC822" : " RFC822");
            }
            first = false;
        }
        if (fp.contains(FetchProfile.Item.SIZE) || fp.contains(FetchProfileItem.SIZE)) {
            command.append(first ? "RFC822.SIZE" : " RFC822.SIZE");
            first = false;
        }
        if (fp.contains(FetchProfileItem.INTERNALDATE)) {
            command.append(first ? "INTERNALDATE" : " INTERNALDATE");
            first = false;
        }
        String[] hdrs = null;
        if (!allHeaders) {
            hdrs = fp.getHeaderNames();
            if (hdrs.length > 0) {
                if (!first) {
                    command.append(" ");
                }
                command.append(this.createHeaderCommand(hdrs, isRev1));
            }
        }
        for (int i = 0; i < fitems.length; ++i) {
            if (fp.contains(fitems[i].getFetchProfileItem())) {
                if (command.length() != 0) {
                    command.append(" ");
                }
                command.append(fitems[i].getName());
            }
        }
        final Utility.Condition condition = new IMAPMessage.FetchProfileCondition(fp, fitems);
        synchronized (this.messageCacheLock) {
            this.checkOpened();
            final MessageSet[] msgsets = Utility.toMessageSetSorted(msgs, condition);
            if (msgsets == null) {
                return;
            }
            Response[] r = null;
            final List<Response> v = new ArrayList<Response>();
            try {
                r = this.getProtocol().fetch(msgsets, command.toString());
            }
            catch (final ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            }
            catch (final CommandFailedException ex) {}
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
            if (r == null) {
                return;
            }
            for (int j = 0; j < r.length; ++j) {
                if (r[j] != null) {
                    if (!(r[j] instanceof FetchResponse)) {
                        v.add(r[j]);
                    }
                    else {
                        final FetchResponse f = (FetchResponse)r[j];
                        final IMAPMessage msg = this.getMessageBySeqNumber(f.getNumber());
                        final int count = f.getItemCount();
                        boolean unsolicitedFlags = false;
                        for (int k = 0; k < count; ++k) {
                            final Item item = f.getItem(k);
                            if (item instanceof Flags && (!fp.contains(FetchProfile.Item.FLAGS) || msg == null)) {
                                unsolicitedFlags = true;
                            }
                            else if (msg != null) {
                                msg.handleFetchItem(item, hdrs, allHeaders);
                            }
                        }
                        if (msg != null) {
                            msg.handleExtensionFetchItems(f.getExtensionItems());
                        }
                        if (unsolicitedFlags) {
                            v.add(f);
                        }
                    }
                }
            }
            if (!v.isEmpty()) {
                final Response[] responses = new Response[v.size()];
                v.toArray(responses);
                this.handleResponses(responses);
            }
        }
    }
    
    protected String getEnvelopeCommand() {
        return "ENVELOPE INTERNALDATE RFC822.SIZE";
    }
    
    protected IMAPMessage newIMAPMessage(final int msgnum) {
        return new IMAPMessage(this, msgnum);
    }
    
    private String createHeaderCommand(final String[] hdrs, final boolean isRev1) {
        StringBuilder sb;
        if (isRev1) {
            sb = new StringBuilder("BODY.PEEK[HEADER.FIELDS (");
        }
        else {
            sb = new StringBuilder("RFC822.HEADER.LINES (");
        }
        for (int i = 0; i < hdrs.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(hdrs[i]);
        }
        if (isRev1) {
            sb.append(")]");
        }
        else {
            sb.append(")");
        }
        return sb.toString();
    }
    
    @Override
    public synchronized void setFlags(final Message[] msgs, final Flags flag, final boolean value) throws MessagingException {
        this.checkOpened();
        this.checkFlags(flag);
        if (msgs.length == 0) {
            return;
        }
        synchronized (this.messageCacheLock) {
            try {
                final IMAPProtocol p = this.getProtocol();
                final MessageSet[] ms = Utility.toMessageSetSorted(msgs, null);
                if (ms == null) {
                    throw new MessageRemovedException("Messages have been removed");
                }
                p.storeFlags(ms, flag, value);
            }
            catch (final ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
    }
    
    @Override
    public synchronized void setFlags(final int start, final int end, final Flags flag, final boolean value) throws MessagingException {
        this.checkOpened();
        final Message[] msgs = new Message[end - start + 1];
        int i = 0;
        for (int n = start; n <= end; ++n) {
            msgs[i++] = this.getMessage(n);
        }
        this.setFlags(msgs, flag, value);
    }
    
    @Override
    public synchronized void setFlags(final int[] msgnums, final Flags flag, final boolean value) throws MessagingException {
        this.checkOpened();
        final Message[] msgs = new Message[msgnums.length];
        for (int i = 0; i < msgnums.length; ++i) {
            msgs[i] = this.getMessage(msgnums[i]);
        }
        this.setFlags(msgs, flag, value);
    }
    
    @Override
    public synchronized void close(final boolean expunge) throws MessagingException {
        this.close(expunge, false);
    }
    
    public synchronized void forceClose() throws MessagingException {
        this.close(false, true);
    }
    
    private void close(final boolean expunge, final boolean force) throws MessagingException {
        assert Thread.holdsLock(this);
        synchronized (this.messageCacheLock) {
            if (!this.opened && this.reallyClosed) {
                throw new IllegalStateException("This operation is not allowed on a closed folder");
            }
            this.reallyClosed = true;
            if (!this.opened) {
                return;
            }
            boolean reuseProtocol = true;
            try {
                this.waitIfIdle();
                if (force) {
                    this.logger.log(Level.FINE, "forcing folder {0} to close", this.fullName);
                    if (this.protocol != null) {
                        this.protocol.disconnect();
                    }
                }
                else if (((IMAPStore)this.store).isConnectionPoolFull()) {
                    this.logger.fine("pool is full, not adding an Authenticated connection");
                    if (expunge && this.protocol != null) {
                        this.protocol.close();
                    }
                    if (this.protocol != null) {
                        this.protocol.logout();
                    }
                }
                else if (!expunge && this.mode == 2) {
                    try {
                        if (this.protocol != null && this.protocol.hasCapability("UNSELECT")) {
                            this.protocol.unselect();
                        }
                        else if (this.protocol != null) {
                            boolean selected = true;
                            try {
                                this.protocol.examine(this.fullName);
                            }
                            catch (final CommandFailedException ex) {
                                selected = false;
                            }
                            if (selected && this.protocol != null) {
                                this.protocol.close();
                            }
                        }
                    }
                    catch (final ProtocolException pex2) {
                        reuseProtocol = false;
                    }
                }
                else if (this.protocol != null) {
                    this.protocol.close();
                }
            }
            catch (final ProtocolException pex3) {
                throw new MessagingException(pex3.getMessage(), pex3);
            }
            finally {
                if (this.opened) {
                    this.cleanup(reuseProtocol);
                }
            }
        }
    }
    
    private void cleanup(final boolean returnToPool) {
        assert Thread.holdsLock(this.messageCacheLock);
        this.releaseProtocol(returnToPool);
        this.messageCache = null;
        this.uidTable = null;
        this.exists = false;
        this.attributes = null;
        this.opened = false;
        this.idleState = 0;
        this.messageCacheLock.notifyAll();
        this.notifyConnectionListeners(3);
    }
    
    @Override
    public synchronized boolean isOpen() {
        synchronized (this.messageCacheLock) {
            if (this.opened) {
                try {
                    this.keepConnectionAlive(false);
                }
                catch (final ProtocolException ex) {}
            }
        }
        return this.opened;
    }
    
    @Override
    public synchronized Flags getPermanentFlags() {
        if (this.permanentFlags == null) {
            return null;
        }
        return (Flags)this.permanentFlags.clone();
    }
    
    @Override
    public synchronized int getMessageCount() throws MessagingException {
        synchronized (this.messageCacheLock) {
            if (this.opened) {
                try {
                    this.keepConnectionAlive(true);
                    return this.total;
                }
                catch (final ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                }
                catch (final ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
        }
        this.checkExists();
        try {
            final Status status = this.getStatus();
            return status.total;
        }
        catch (final BadCommandException bex) {
            IMAPProtocol p = null;
            try {
                p = this.getStoreProtocol();
                final MailboxInfo minfo = p.examine(this.fullName);
                p.close();
                return minfo.total;
            }
            catch (final ProtocolException pex2) {
                throw new MessagingException(pex2.getMessage(), pex2);
            }
            finally {
                this.releaseStoreProtocol(p);
            }
        }
        catch (final ConnectionException cex2) {
            throw new StoreClosedException(this.store, cex2.getMessage());
        }
        catch (final ProtocolException pex3) {
            throw new MessagingException(pex3.getMessage(), pex3);
        }
    }
    
    @Override
    public synchronized int getNewMessageCount() throws MessagingException {
        synchronized (this.messageCacheLock) {
            if (this.opened) {
                try {
                    this.keepConnectionAlive(true);
                    return this.recent;
                }
                catch (final ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                }
                catch (final ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
        }
        this.checkExists();
        try {
            final Status status = this.getStatus();
            return status.recent;
        }
        catch (final BadCommandException bex) {
            IMAPProtocol p = null;
            try {
                p = this.getStoreProtocol();
                final MailboxInfo minfo = p.examine(this.fullName);
                p.close();
                return minfo.recent;
            }
            catch (final ProtocolException pex2) {
                throw new MessagingException(pex2.getMessage(), pex2);
            }
            finally {
                this.releaseStoreProtocol(p);
            }
        }
        catch (final ConnectionException cex2) {
            throw new StoreClosedException(this.store, cex2.getMessage());
        }
        catch (final ProtocolException pex3) {
            throw new MessagingException(pex3.getMessage(), pex3);
        }
    }
    
    @Override
    public synchronized int getUnreadMessageCount() throws MessagingException {
        if (!this.opened) {
            this.checkExists();
            try {
                final Status status = this.getStatus();
                return status.unseen;
            }
            catch (final BadCommandException bex) {
                return -1;
            }
            catch (final ConnectionException cex) {
                throw new StoreClosedException(this.store, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        final Flags f = new Flags();
        f.add(Flags.Flag.SEEN);
        try {
            synchronized (this.messageCacheLock) {
                final int[] matches = this.getProtocol().search(new FlagTerm(f, false));
                return matches.length;
            }
        }
        catch (final ConnectionException cex2) {
            throw new FolderClosedException(this, cex2.getMessage());
        }
        catch (final ProtocolException pex2) {
            throw new MessagingException(pex2.getMessage(), pex2);
        }
    }
    
    @Override
    public synchronized int getDeletedMessageCount() throws MessagingException {
        if (!this.opened) {
            this.checkExists();
            return -1;
        }
        final Flags f = new Flags();
        f.add(Flags.Flag.DELETED);
        try {
            synchronized (this.messageCacheLock) {
                final int[] matches = this.getProtocol().search(new FlagTerm(f, true));
                return matches.length;
            }
        }
        catch (final ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }
    
    private Status getStatus() throws ProtocolException {
        final int statusCacheTimeout = ((IMAPStore)this.store).getStatusCacheTimeout();
        if (statusCacheTimeout > 0 && this.cachedStatus != null && System.currentTimeMillis() - this.cachedStatusTime < statusCacheTimeout) {
            return this.cachedStatus;
        }
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            final Status s = p.status(this.fullName, null);
            if (statusCacheTimeout > 0) {
                this.cachedStatus = s;
                this.cachedStatusTime = System.currentTimeMillis();
            }
            return s;
        }
        finally {
            this.releaseStoreProtocol(p);
        }
    }
    
    @Override
    public synchronized Message getMessage(final int msgnum) throws MessagingException {
        this.checkOpened();
        this.checkRange(msgnum);
        return this.messageCache.getMessage(msgnum);
    }
    
    @Override
    public synchronized Message[] getMessages() throws MessagingException {
        this.checkOpened();
        final int total = this.getMessageCount();
        final Message[] msgs = new Message[total];
        for (int i = 1; i <= total; ++i) {
            msgs[i - 1] = this.messageCache.getMessage(i);
        }
        return msgs;
    }
    
    @Override
    public synchronized void appendMessages(final Message[] msgs) throws MessagingException {
        this.checkExists();
        final int maxsize = ((IMAPStore)this.store).getAppendBufferSize();
        for (int i = 0; i < msgs.length; ++i) {
            final Message m = msgs[i];
            Date d = m.getReceivedDate();
            if (d == null) {
                d = m.getSentDate();
            }
            final Date dd = d;
            final Flags f = m.getFlags();
            MessageLiteral mos;
            try {
                mos = new MessageLiteral(m, (m.getSize() > maxsize) ? 0 : maxsize);
            }
            catch (final IOException ex) {
                throw new MessagingException("IOException while appending messages", ex);
            }
            catch (final MessageRemovedException mrex) {
                continue;
            }
            this.doCommand(new ProtocolCommand() {
                @Override
                public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                    p.append(IMAPFolder.this.fullName, f, dd, mos);
                    return null;
                }
            });
        }
    }
    
    public synchronized AppendUID[] appendUIDMessages(final Message[] msgs) throws MessagingException {
        this.checkExists();
        final int maxsize = ((IMAPStore)this.store).getAppendBufferSize();
        final AppendUID[] uids = new AppendUID[msgs.length];
        for (int i = 0; i < msgs.length; ++i) {
            final Message m = msgs[i];
            MessageLiteral mos;
            try {
                mos = new MessageLiteral(m, (m.getSize() > maxsize) ? 0 : maxsize);
            }
            catch (final IOException ex) {
                throw new MessagingException("IOException while appending messages", ex);
            }
            catch (final MessageRemovedException mrex) {
                continue;
            }
            Date d = m.getReceivedDate();
            if (d == null) {
                d = m.getSentDate();
            }
            final Date dd = d;
            final Flags f = m.getFlags();
            final AppendUID auid = (AppendUID)this.doCommand(new ProtocolCommand() {
                @Override
                public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                    return p.appenduid(IMAPFolder.this.fullName, f, dd, mos);
                }
            });
            uids[i] = auid;
        }
        return uids;
    }
    
    public synchronized Message[] addMessages(final Message[] msgs) throws MessagingException {
        this.checkOpened();
        final Message[] rmsgs = new MimeMessage[msgs.length];
        final AppendUID[] uids = this.appendUIDMessages(msgs);
        for (int i = 0; i < uids.length; ++i) {
            final AppendUID auid = uids[i];
            if (auid != null && auid.uidvalidity == this.uidvalidity) {
                try {
                    rmsgs[i] = this.getMessageByUID(auid.uid);
                }
                catch (final MessagingException ex) {}
            }
        }
        return rmsgs;
    }
    
    @Override
    public synchronized void copyMessages(final Message[] msgs, final Folder folder) throws MessagingException {
        this.copymoveMessages(msgs, folder, false);
    }
    
    public synchronized AppendUID[] copyUIDMessages(final Message[] msgs, final Folder folder) throws MessagingException {
        return this.copymoveUIDMessages(msgs, folder, false);
    }
    
    public synchronized void moveMessages(final Message[] msgs, final Folder folder) throws MessagingException {
        this.copymoveMessages(msgs, folder, true);
    }
    
    public synchronized AppendUID[] moveUIDMessages(final Message[] msgs, final Folder folder) throws MessagingException {
        return this.copymoveUIDMessages(msgs, folder, true);
    }
    
    private synchronized void copymoveMessages(final Message[] msgs, final Folder folder, final boolean move) throws MessagingException {
        this.checkOpened();
        if (msgs.length == 0) {
            return;
        }
        if (folder.getStore() == this.store) {
            synchronized (this.messageCacheLock) {
                try {
                    final IMAPProtocol p = this.getProtocol();
                    final MessageSet[] ms = Utility.toMessageSet(msgs, null);
                    if (ms == null) {
                        throw new MessageRemovedException("Messages have been removed");
                    }
                    if (move) {
                        p.move(ms, folder.getFullName());
                    }
                    else {
                        p.copy(ms, folder.getFullName());
                    }
                }
                catch (final CommandFailedException cfx) {
                    if (cfx.getMessage().indexOf("TRYCREATE") != -1) {
                        throw new FolderNotFoundException(folder, folder.getFullName() + " does not exist");
                    }
                    throw new MessagingException(cfx.getMessage(), cfx);
                }
                catch (final ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                }
                catch (final ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
        }
        else {
            if (move) {
                throw new MessagingException("Move between stores not supported");
            }
            super.copyMessages(msgs, folder);
        }
    }
    
    private synchronized AppendUID[] copymoveUIDMessages(final Message[] msgs, final Folder folder, final boolean move) throws MessagingException {
        this.checkOpened();
        if (msgs.length == 0) {
            return null;
        }
        if (folder.getStore() != this.store) {
            throw new MessagingException(move ? "can't moveUIDMessages to a different store" : "can't copyUIDMessages to a different store");
        }
        final FetchProfile fp = new FetchProfile();
        fp.add(UIDFolder.FetchProfileItem.UID);
        this.fetch(msgs, fp);
        synchronized (this.messageCacheLock) {
            try {
                final IMAPProtocol p = this.getProtocol();
                final MessageSet[] ms = Utility.toMessageSet(msgs, null);
                if (ms == null) {
                    throw new MessageRemovedException("Messages have been removed");
                }
                CopyUID cuid;
                if (move) {
                    cuid = p.moveuid(ms, folder.getFullName());
                }
                else {
                    cuid = p.copyuid(ms, folder.getFullName());
                }
                final long[] srcuids = UIDSet.toArray(cuid.src);
                final long[] dstuids = UIDSet.toArray(cuid.dst);
                final Message[] srcmsgs = this.getMessagesByUID(srcuids);
                final AppendUID[] result = new AppendUID[msgs.length];
                int i = 0;
            Label_0175:
                while (i < msgs.length) {
                    int j = i;
                    while (true) {
                        while (msgs[i] != srcmsgs[j]) {
                            if (++j >= srcmsgs.length) {
                                j = 0;
                            }
                            if (j == i) {
                                ++i;
                                continue Label_0175;
                            }
                        }
                        result[i] = new AppendUID(cuid.uidvalidity, dstuids[j]);
                        continue;
                    }
                }
                return result;
            }
            catch (final CommandFailedException cfx) {
                if (cfx.getMessage().indexOf("TRYCREATE") != -1) {
                    throw new FolderNotFoundException(folder, folder.getFullName() + " does not exist");
                }
                throw new MessagingException(cfx.getMessage(), cfx);
            }
            catch (final ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
    }
    
    @Override
    public synchronized Message[] expunge() throws MessagingException {
        return this.expunge(null);
    }
    
    public synchronized Message[] expunge(final Message[] msgs) throws MessagingException {
        this.checkOpened();
        if (msgs != null) {
            final FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            this.fetch(msgs, fp);
        }
        IMAPMessage[] rmsgs;
        synchronized (this.messageCacheLock) {
            this.doExpungeNotification = false;
            try {
                final IMAPProtocol p = this.getProtocol();
                if (msgs != null) {
                    p.uidexpunge(Utility.toUIDSet(msgs));
                }
                else {
                    p.expunge();
                }
            }
            catch (final CommandFailedException cfx) {
                if (this.mode != 2) {
                    throw new IllegalStateException("Cannot expunge READ_ONLY folder: " + this.fullName);
                }
                throw new MessagingException(cfx.getMessage(), cfx);
            }
            catch (final ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
            finally {
                this.doExpungeNotification = true;
            }
            if (msgs != null) {
                rmsgs = this.messageCache.removeExpungedMessages(msgs);
            }
            else {
                rmsgs = this.messageCache.removeExpungedMessages();
            }
            if (this.uidTable != null) {
                for (int i = 0; i < rmsgs.length; ++i) {
                    final IMAPMessage m = rmsgs[i];
                    final long uid = m.getUID();
                    if (uid != -1L) {
                        this.uidTable.remove(uid);
                    }
                }
            }
            this.total = this.messageCache.size();
        }
        if (rmsgs.length > 0) {
            this.notifyMessageRemovedListeners(true, rmsgs);
        }
        return rmsgs;
    }
    
    @Override
    public synchronized Message[] search(final SearchTerm term) throws MessagingException {
        this.checkOpened();
        try {
            Message[] matchMsgs = null;
            synchronized (this.messageCacheLock) {
                final int[] matches = this.getProtocol().search(term);
                if (matches != null) {
                    matchMsgs = this.getMessagesBySeqNumbers(matches);
                }
            }
            return matchMsgs;
        }
        catch (final CommandFailedException cfx) {
            return super.search(term);
        }
        catch (final SearchException sex) {
            if (((IMAPStore)this.store).throwSearchException()) {
                throw sex;
            }
            return super.search(term);
        }
        catch (final ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }
    
    @Override
    public synchronized Message[] search(final SearchTerm term, final Message[] msgs) throws MessagingException {
        this.checkOpened();
        if (msgs.length == 0) {
            return msgs;
        }
        try {
            Message[] matchMsgs = null;
            synchronized (this.messageCacheLock) {
                final IMAPProtocol p = this.getProtocol();
                final MessageSet[] ms = Utility.toMessageSetSorted(msgs, null);
                if (ms == null) {
                    throw new MessageRemovedException("Messages have been removed");
                }
                final int[] matches = p.search(ms, term);
                if (matches != null) {
                    matchMsgs = this.getMessagesBySeqNumbers(matches);
                }
            }
            return matchMsgs;
        }
        catch (final CommandFailedException cfx) {
            return super.search(term, msgs);
        }
        catch (final SearchException sex) {
            return super.search(term, msgs);
        }
        catch (final ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }
    
    public synchronized Message[] getSortedMessages(final SortTerm[] term) throws MessagingException {
        return this.getSortedMessages(term, null);
    }
    
    public synchronized Message[] getSortedMessages(final SortTerm[] term, final SearchTerm sterm) throws MessagingException {
        this.checkOpened();
        try {
            Message[] matchMsgs = null;
            synchronized (this.messageCacheLock) {
                final int[] matches = this.getProtocol().sort(term, sterm);
                if (matches != null) {
                    matchMsgs = this.getMessagesBySeqNumbers(matches);
                }
            }
            return matchMsgs;
        }
        catch (final CommandFailedException cfx) {
            throw new MessagingException(cfx.getMessage(), cfx);
        }
        catch (final SearchException sex) {
            throw new MessagingException(sex.getMessage(), sex);
        }
        catch (final ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }
    
    @Override
    public synchronized void addMessageCountListener(final MessageCountListener l) {
        super.addMessageCountListener(l);
        this.hasMessageCountListener = true;
    }
    
    @Override
    public synchronized long getUIDValidity() throws MessagingException {
        if (this.opened) {
            return this.uidvalidity;
        }
        IMAPProtocol p = null;
        Status status = null;
        try {
            p = this.getStoreProtocol();
            final String[] item = { "UIDVALIDITY" };
            status = p.status(this.fullName, item);
        }
        catch (final BadCommandException bex) {
            throw new MessagingException("Cannot obtain UIDValidity", bex);
        }
        catch (final ConnectionException cex) {
            this.throwClosedException(cex);
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
        if (status == null) {
            throw new MessagingException("Cannot obtain UIDValidity");
        }
        return status.uidvalidity;
    }
    
    @Override
    public synchronized long getUIDNext() throws MessagingException {
        if (this.opened) {
            return this.uidnext;
        }
        IMAPProtocol p = null;
        Status status = null;
        try {
            p = this.getStoreProtocol();
            final String[] item = { "UIDNEXT" };
            status = p.status(this.fullName, item);
        }
        catch (final BadCommandException bex) {
            throw new MessagingException("Cannot obtain UIDNext", bex);
        }
        catch (final ConnectionException cex) {
            this.throwClosedException(cex);
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
        if (status == null) {
            throw new MessagingException("Cannot obtain UIDNext");
        }
        return status.uidnext;
    }
    
    @Override
    public synchronized Message getMessageByUID(final long uid) throws MessagingException {
        this.checkOpened();
        IMAPMessage m = null;
        try {
            synchronized (this.messageCacheLock) {
                final Long l = uid;
                if (this.uidTable != null) {
                    m = this.uidTable.get(l);
                    if (m != null) {
                        return m;
                    }
                }
                else {
                    this.uidTable = new Hashtable<Long, IMAPMessage>();
                }
                this.getProtocol().fetchSequenceNumber(uid);
                if (this.uidTable != null) {
                    m = this.uidTable.get(l);
                    if (m != null) {
                        return m;
                    }
                }
            }
        }
        catch (final ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        return m;
    }
    
    @Override
    public synchronized Message[] getMessagesByUID(final long start, final long end) throws MessagingException {
        this.checkOpened();
        Message[] msgs;
        try {
            synchronized (this.messageCacheLock) {
                if (this.uidTable == null) {
                    this.uidTable = new Hashtable<Long, IMAPMessage>();
                }
                final long[] ua = this.getProtocol().fetchSequenceNumbers(start, end);
                final List<Message> ma = new ArrayList<Message>();
                for (int i = 0; i < ua.length; ++i) {
                    final Message m = this.uidTable.get(ua[i]);
                    if (m != null) {
                        ma.add(m);
                    }
                }
                msgs = ma.toArray(new Message[ma.size()]);
            }
        }
        catch (final ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        return msgs;
    }
    
    @Override
    public synchronized Message[] getMessagesByUID(final long[] uids) throws MessagingException {
        this.checkOpened();
        try {
            synchronized (this.messageCacheLock) {
                long[] unavailUids = uids;
                if (this.uidTable != null) {
                    final List<Long> v = new ArrayList<Long>();
                    for (final long uid : uids) {
                        if (!this.uidTable.containsKey(uid)) {
                            v.add(uid);
                        }
                    }
                    final int vsize = v.size();
                    unavailUids = new long[vsize];
                    for (int i = 0; i < vsize; ++i) {
                        unavailUids[i] = v.get(i);
                    }
                }
                else {
                    this.uidTable = new Hashtable<Long, IMAPMessage>();
                }
                if (unavailUids.length > 0) {
                    this.getProtocol().fetchSequenceNumbers(unavailUids);
                }
                final Message[] msgs = new Message[uids.length];
                for (int j = 0; j < uids.length; ++j) {
                    msgs[j] = this.uidTable.get(uids[j]);
                }
                return msgs;
            }
        }
        catch (final ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }
    
    @Override
    public synchronized long getUID(final Message message) throws MessagingException {
        if (message.getFolder() != this) {
            throw new NoSuchElementException("Message does not belong to this folder");
        }
        this.checkOpened();
        if (!(message instanceof IMAPMessage)) {
            throw new MessagingException("message is not an IMAPMessage");
        }
        final IMAPMessage m = (IMAPMessage)message;
        long uid;
        if ((uid = m.getUID()) != -1L) {
            return uid;
        }
        synchronized (this.messageCacheLock) {
            try {
                final IMAPProtocol p = this.getProtocol();
                m.checkExpunged();
                final UID u = p.fetchUID(m.getSequenceNumber());
                if (u != null) {
                    uid = u.uid;
                    m.setUID(uid);
                    if (this.uidTable == null) {
                        this.uidTable = new Hashtable<Long, IMAPMessage>();
                    }
                    this.uidTable.put(uid, m);
                }
            }
            catch (final ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        return uid;
    }
    
    public synchronized boolean getUIDNotSticky() throws MessagingException {
        this.checkOpened();
        return this.uidNotSticky;
    }
    
    private Message[] createMessagesForUIDs(final long[] uids) {
        final IMAPMessage[] msgs = new IMAPMessage[uids.length];
        IMAPMessage m;
        for (int i = 0; i < uids.length; msgs[i++] = m, ++i) {
            m = null;
            if (this.uidTable != null) {
                m = this.uidTable.get(uids[i]);
            }
            if (m == null) {
                m = this.newIMAPMessage(-1);
                m.setUID(uids[i]);
                m.setExpunged(true);
            }
        }
        return msgs;
    }
    
    public synchronized long getHighestModSeq() throws MessagingException {
        if (this.opened) {
            return this.highestmodseq;
        }
        IMAPProtocol p = null;
        Status status = null;
        try {
            p = this.getStoreProtocol();
            if (!p.hasCapability("CONDSTORE")) {
                throw new BadCommandException("CONDSTORE not supported");
            }
            final String[] item = { "HIGHESTMODSEQ" };
            status = p.status(this.fullName, item);
        }
        catch (final BadCommandException bex) {
            throw new MessagingException("Cannot obtain HIGHESTMODSEQ", bex);
        }
        catch (final ConnectionException cex) {
            this.throwClosedException(cex);
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
        if (status == null) {
            throw new MessagingException("Cannot obtain HIGHESTMODSEQ");
        }
        return status.highestmodseq;
    }
    
    public synchronized Message[] getMessagesByUIDChangedSince(final long start, final long end, final long modseq) throws MessagingException {
        this.checkOpened();
        try {
            synchronized (this.messageCacheLock) {
                final IMAPProtocol p = this.getProtocol();
                if (!p.hasCapability("CONDSTORE")) {
                    throw new BadCommandException("CONDSTORE not supported");
                }
                final int[] nums = p.uidfetchChangedSince(start, end, modseq);
                return this.getMessagesBySeqNumbers(nums);
            }
        }
        catch (final ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }
    
    public Quota[] getQuota() throws MessagingException {
        return (Quota[])this.doOptionalCommand("QUOTA not supported", new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.getQuotaRoot(IMAPFolder.this.fullName);
            }
        });
    }
    
    public void setQuota(final Quota quota) throws MessagingException {
        this.doOptionalCommand("QUOTA not supported", new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                p.setQuota(quota);
                return null;
            }
        });
    }
    
    public ACL[] getACL() throws MessagingException {
        return (ACL[])this.doOptionalCommand("ACL not supported", new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.getACL(IMAPFolder.this.fullName);
            }
        });
    }
    
    public void addACL(final ACL acl) throws MessagingException {
        this.setACL(acl, '\0');
    }
    
    public void removeACL(final String name) throws MessagingException {
        this.doOptionalCommand("ACL not supported", new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                p.deleteACL(IMAPFolder.this.fullName, name);
                return null;
            }
        });
    }
    
    public void addRights(final ACL acl) throws MessagingException {
        this.setACL(acl, '+');
    }
    
    public void removeRights(final ACL acl) throws MessagingException {
        this.setACL(acl, '-');
    }
    
    public Rights[] listRights(final String name) throws MessagingException {
        return (Rights[])this.doOptionalCommand("ACL not supported", new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.listRights(IMAPFolder.this.fullName, name);
            }
        });
    }
    
    public Rights myRights() throws MessagingException {
        return (Rights)this.doOptionalCommand("ACL not supported", new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.myRights(IMAPFolder.this.fullName);
            }
        });
    }
    
    private void setACL(final ACL acl, final char mod) throws MessagingException {
        this.doOptionalCommand("ACL not supported", new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                p.setACL(IMAPFolder.this.fullName, mod, acl);
                return null;
            }
        });
    }
    
    public synchronized String[] getAttributes() throws MessagingException {
        this.checkExists();
        if (this.attributes == null) {
            this.exists();
        }
        return (this.attributes == null) ? new String[0] : this.attributes.clone();
    }
    
    public void idle() throws MessagingException {
        this.idle(false);
    }
    
    public void idle(final boolean once) throws MessagingException {
        synchronized (this) {
            if (this.protocol != null && this.protocol.getChannel() != null) {
                throw new MessagingException("idle method not supported with SocketChannels");
            }
        }
        if (!this.startIdle(null)) {
            return;
        }
        while (this.handleIdle(once)) {}
        final int minidle = ((IMAPStore)this.store).getMinIdleTime();
        if (minidle > 0) {
            try {
                Thread.sleep(minidle);
            }
            catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    boolean startIdle(final IdleManager im) throws MessagingException {
        assert !Thread.holdsLock(this);
        synchronized (this) {
            this.checkOpened();
            if (im != null && this.idleManager != null && im != this.idleManager) {
                throw new MessagingException("Folder already being watched by another IdleManager");
            }
            final Boolean started = (Boolean)this.doOptionalCommand("IDLE not supported", new ProtocolCommand() {
                @Override
                public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                    if (IMAPFolder.this.idleState == 1 && im != null && im == IMAPFolder.this.idleManager) {
                        return Boolean.TRUE;
                    }
                    if (IMAPFolder.this.idleState == 0) {
                        p.idleStart();
                        IMAPFolder.this.logger.finest("startIdle: set to IDLE");
                        IMAPFolder.this.idleState = 1;
                        IMAPFolder.this.idleManager = im;
                        return Boolean.TRUE;
                    }
                    try {
                        IMAPFolder.this.messageCacheLock.wait();
                    }
                    catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    return Boolean.FALSE;
                }
            });
            this.logger.log(Level.FINEST, "startIdle: return {0}", started);
            return started;
        }
    }
    
    boolean handleIdle(final boolean once) throws MessagingException {
        Response r = null;
        do {
            r = this.protocol.readIdleResponse();
            try {
                synchronized (this.messageCacheLock) {
                    if (r.isBYE() && r.isSynthetic() && this.idleState == 1) {
                        final Exception ex = r.getException();
                        if (ex instanceof InterruptedIOException && ((InterruptedIOException)ex).bytesTransferred == 0) {
                            if (ex instanceof SocketTimeoutException) {
                                this.logger.finest("handleIdle: ignoring socket timeout");
                                r = null;
                            }
                            else {
                                this.logger.finest("handleIdle: interrupting IDLE");
                                final IdleManager im = this.idleManager;
                                if (im != null) {
                                    this.logger.finest("handleIdle: request IdleManager to abort");
                                    im.requestAbort(this);
                                }
                                else {
                                    this.logger.finest("handleIdle: abort IDLE");
                                    this.protocol.idleAbort();
                                    this.idleState = 2;
                                }
                            }
                            continue;
                        }
                    }
                    boolean done = true;
                    try {
                        if (this.protocol == null || !this.protocol.processIdleResponse(r)) {
                            return false;
                        }
                        done = false;
                    }
                    finally {
                        if (done) {
                            this.logger.finest("handleIdle: set to RUNNING");
                            this.idleState = 0;
                            this.idleManager = null;
                            this.messageCacheLock.notifyAll();
                        }
                    }
                    if (!once || this.idleState != 1) {
                        continue;
                    }
                    try {
                        this.protocol.idleAbort();
                    }
                    catch (final Exception ex2) {}
                    this.idleState = 2;
                }
            }
            catch (final ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        } while (r == null || this.protocol.hasResponse());
        return true;
    }
    
    void waitIfIdle() throws ProtocolException {
        assert Thread.holdsLock(this.messageCacheLock);
        while (this.idleState != 0) {
            if (this.idleState == 1) {
                final IdleManager im = this.idleManager;
                if (im != null) {
                    this.logger.finest("waitIfIdle: request IdleManager to abort");
                    im.requestAbort(this);
                }
                else {
                    this.logger.finest("waitIfIdle: abort IDLE");
                    this.protocol.idleAbort();
                    this.idleState = 2;
                }
            }
            else {
                this.logger.log(Level.FINEST, "waitIfIdle: idleState {0}", (Object)this.idleState);
            }
            try {
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest("waitIfIdle: wait to be not idle: " + Thread.currentThread());
                }
                this.messageCacheLock.wait();
                if (!this.logger.isLoggable(Level.FINEST)) {
                    continue;
                }
                this.logger.finest("waitIfIdle: wait done, idleState " + this.idleState + ": " + Thread.currentThread());
                continue;
            }
            catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new ProtocolException("Interrupted waitIfIdle", ex);
            }
            break;
        }
    }
    
    void idleAbort() {
        synchronized (this.messageCacheLock) {
            if (this.idleState == 1 && this.protocol != null) {
                this.protocol.idleAbort();
                this.idleState = 2;
            }
        }
    }
    
    void idleAbortWait() {
        synchronized (this.messageCacheLock) {
            if (this.idleState == 1 && this.protocol != null) {
                this.protocol.idleAbort();
                this.idleState = 2;
                try {
                    while (this.handleIdle(false)) {}
                }
                catch (final Exception ex) {
                    this.logger.log(Level.FINEST, "Exception in idleAbortWait", ex);
                }
                this.logger.finest("IDLE aborted");
            }
        }
    }
    
    SocketChannel getChannel() {
        return (this.protocol != null) ? this.protocol.getChannel() : null;
    }
    
    public Map<String, String> id(final Map<String, String> clientParams) throws MessagingException {
        this.checkOpened();
        return (Map)this.doOptionalCommand("ID not supported", new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.id(clientParams);
            }
        });
    }
    
    public synchronized long getStatusItem(final String item) throws MessagingException {
        if (!this.opened) {
            this.checkExists();
            IMAPProtocol p = null;
            Status status = null;
            try {
                p = this.getStoreProtocol();
                final String[] items = { item };
                status = p.status(this.fullName, items);
                return (status != null) ? status.getItem(item) : -1L;
            }
            catch (final BadCommandException bex) {
                return -1L;
            }
            catch (final ConnectionException cex) {
                throw new StoreClosedException(this.store, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
            finally {
                this.releaseStoreProtocol(p);
            }
        }
        return -1L;
    }
    
    @Override
    public void handleResponse(final Response r) {
        assert Thread.holdsLock(this.messageCacheLock);
        if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
            ((IMAPStore)this.store).handleResponseCode(r);
        }
        if (r.isBYE()) {
            if (this.opened) {
                this.cleanup(false);
            }
            return;
        }
        if (r.isOK()) {
            r.skipSpaces();
            if (r.readByte() == 91) {
                final String s = r.readAtom();
                if (s.equalsIgnoreCase("HIGHESTMODSEQ")) {
                    this.highestmodseq = r.readLong();
                }
            }
            r.reset();
            return;
        }
        if (!r.isUnTagged()) {
            return;
        }
        if (!(r instanceof IMAPResponse)) {
            this.logger.fine("UNEXPECTED RESPONSE : " + r.toString());
            return;
        }
        final IMAPResponse ir = (IMAPResponse)r;
        if (ir.keyEquals("EXISTS")) {
            final int exists = ir.getNumber();
            if (exists <= this.realTotal) {
                return;
            }
            final int count = exists - this.realTotal;
            final Message[] msgs = new Message[count];
            this.messageCache.addMessages(count, this.realTotal + 1);
            int oldtotal = this.total;
            this.realTotal += count;
            this.total += count;
            if (this.hasMessageCountListener) {
                for (int i = 0; i < count; ++i) {
                    msgs[i] = this.messageCache.getMessage(++oldtotal);
                }
                this.notifyMessageAddedListeners(msgs);
            }
        }
        else if (ir.keyEquals("EXPUNGE")) {
            final int seqnum = ir.getNumber();
            if (seqnum > this.realTotal) {
                return;
            }
            Message[] msgs2 = null;
            if (this.doExpungeNotification && this.hasMessageCountListener) {
                msgs2 = new Message[] { this.getMessageBySeqNumber(seqnum) };
                if (msgs2[0] == null) {
                    msgs2 = null;
                }
            }
            this.messageCache.expungeMessage(seqnum);
            --this.realTotal;
            if (msgs2 != null) {
                this.notifyMessageRemovedListeners(false, msgs2);
            }
        }
        else if (ir.keyEquals("VANISHED")) {
            final String[] s2 = ir.readAtomStringList();
            if (s2 == null) {
                final String uids = ir.readAtom();
                final UIDSet[] uidset = UIDSet.parseUIDSets(uids);
                this.realTotal -= (int)UIDSet.size(uidset);
                final long[] luid = UIDSet.toArray(uidset);
                final Message[] messagesForUIDs;
                final Message[] msgs3 = messagesForUIDs = this.createMessagesForUIDs(luid);
                for (final Message m : messagesForUIDs) {
                    if (m.getMessageNumber() > 0) {
                        this.messageCache.expungeMessage(m.getMessageNumber());
                    }
                }
                if (this.doExpungeNotification && this.hasMessageCountListener) {
                    this.notifyMessageRemovedListeners(true, msgs3);
                }
            }
        }
        else if (ir.keyEquals("FETCH")) {
            assert ir instanceof FetchResponse : "!ir instanceof FetchResponse";
            final Message msg = this.processFetchResponse((FetchResponse)ir);
            if (msg != null) {
                this.notifyMessageChangedListeners(1, msg);
            }
        }
        else if (ir.keyEquals("RECENT")) {
            this.recent = ir.getNumber();
        }
    }
    
    private Message processFetchResponse(final FetchResponse fr) {
        IMAPMessage msg = this.getMessageBySeqNumber(fr.getNumber());
        if (msg != null) {
            boolean notify = false;
            final UID uid = fr.getItem(UID.class);
            if (uid != null && msg.getUID() != uid.uid) {
                msg.setUID(uid.uid);
                if (this.uidTable == null) {
                    this.uidTable = new Hashtable<Long, IMAPMessage>();
                }
                this.uidTable.put(uid.uid, msg);
                notify = true;
            }
            final MODSEQ modseq = fr.getItem(MODSEQ.class);
            if (modseq != null && msg._getModSeq() != modseq.modseq) {
                msg.setModSeq(modseq.modseq);
                notify = true;
            }
            final FLAGS flags = fr.getItem(FLAGS.class);
            if (flags != null) {
                msg._setFlags(flags);
                notify = true;
            }
            msg.handleExtensionFetchItems(fr.getExtensionItems());
            if (!notify) {
                msg = null;
            }
        }
        return msg;
    }
    
    void handleResponses(final Response[] r) {
        for (int i = 0; i < r.length; ++i) {
            if (r[i] != null) {
                this.handleResponse(r[i]);
            }
        }
    }
    
    protected synchronized IMAPProtocol getStoreProtocol() throws ProtocolException {
        this.connectionPoolLogger.fine("getStoreProtocol() borrowing a connection");
        return ((IMAPStore)this.store).getFolderStoreProtocol();
    }
    
    protected synchronized void throwClosedException(final ConnectionException cex) throws FolderClosedException, StoreClosedException {
        if ((this.protocol != null && cex.getProtocol() == this.protocol) || (this.protocol == null && !this.reallyClosed)) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        throw new StoreClosedException(this.store, cex.getMessage());
    }
    
    protected IMAPProtocol getProtocol() throws ProtocolException {
        assert Thread.holdsLock(this.messageCacheLock);
        this.waitIfIdle();
        if (this.protocol == null) {
            throw new ConnectionException("Connection closed");
        }
        return this.protocol;
    }
    
    public Object doCommand(final ProtocolCommand cmd) throws MessagingException {
        try {
            return this.doProtocolCommand(cmd);
        }
        catch (final ConnectionException cex) {
            this.throwClosedException(cex);
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        return null;
    }
    
    public Object doOptionalCommand(final String err, final ProtocolCommand cmd) throws MessagingException {
        try {
            return this.doProtocolCommand(cmd);
        }
        catch (final BadCommandException bex) {
            throw new MessagingException(err, bex);
        }
        catch (final ConnectionException cex) {
            this.throwClosedException(cex);
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        return null;
    }
    
    public Object doCommandIgnoreFailure(final ProtocolCommand cmd) throws MessagingException {
        try {
            return this.doProtocolCommand(cmd);
        }
        catch (final CommandFailedException cfx) {
            return null;
        }
        catch (final ConnectionException cex) {
            this.throwClosedException(cex);
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        return null;
    }
    
    protected synchronized Object doProtocolCommand(final ProtocolCommand cmd) throws ProtocolException {
        if (this.protocol != null) {
            synchronized (this.messageCacheLock) {
                return cmd.doCommand(this.getProtocol());
            }
        }
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            return cmd.doCommand(p);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
    }
    
    protected synchronized void releaseStoreProtocol(final IMAPProtocol p) {
        if (p != this.protocol) {
            ((IMAPStore)this.store).releaseFolderStoreProtocol(p);
        }
        else {
            this.logger.fine("releasing our protocol as store protocol?");
        }
    }
    
    protected void releaseProtocol(final boolean returnToPool) {
        if (this.protocol != null) {
            this.protocol.removeResponseHandler(this);
            if (returnToPool) {
                ((IMAPStore)this.store).releaseProtocol(this, this.protocol);
            }
            else {
                this.protocol.disconnect();
                ((IMAPStore)this.store).releaseProtocol(this, null);
            }
            this.protocol = null;
        }
    }
    
    protected void keepConnectionAlive(final boolean keepStoreAlive) throws ProtocolException {
        assert Thread.holdsLock(this.messageCacheLock);
        if (this.protocol == null) {
            return;
        }
        if (System.currentTimeMillis() - this.protocol.getTimestamp() > 1000L) {
            this.waitIfIdle();
            if (this.protocol != null) {
                this.protocol.noop();
            }
        }
        if (keepStoreAlive && ((IMAPStore)this.store).hasSeparateStoreConnection()) {
            IMAPProtocol p = null;
            try {
                p = ((IMAPStore)this.store).getFolderStoreProtocol();
                if (System.currentTimeMillis() - p.getTimestamp() > 1000L) {
                    p.noop();
                }
            }
            finally {
                ((IMAPStore)this.store).releaseFolderStoreProtocol(p);
            }
        }
    }
    
    protected IMAPMessage getMessageBySeqNumber(final int seqnum) {
        if (seqnum > this.messageCache.size()) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("ignoring message number " + seqnum + " outside range " + this.messageCache.size());
            }
            return null;
        }
        return this.messageCache.getMessageBySeqnum(seqnum);
    }
    
    protected IMAPMessage[] getMessagesBySeqNumbers(final int[] seqnums) {
        IMAPMessage[] msgs = new IMAPMessage[seqnums.length];
        int nulls = 0;
        for (int i = 0; i < seqnums.length; ++i) {
            msgs[i] = this.getMessageBySeqNumber(seqnums[i]);
            if (msgs[i] == null) {
                ++nulls;
            }
        }
        if (nulls > 0) {
            final IMAPMessage[] nmsgs = new IMAPMessage[seqnums.length - nulls];
            int j = 0;
            int k = 0;
            while (j < msgs.length) {
                if (msgs[j] != null) {
                    nmsgs[k++] = msgs[j];
                }
                ++j;
            }
            msgs = nmsgs;
        }
        return msgs;
    }
    
    private boolean isDirectory() {
        return (this.type & 0x2) != 0x0;
    }
    
    public static class FetchProfileItem extends FetchProfile.Item
    {
        public static final FetchProfileItem HEADERS;
        @Deprecated
        public static final FetchProfileItem SIZE;
        public static final FetchProfileItem MESSAGE;
        public static final FetchProfileItem INTERNALDATE;
        
        protected FetchProfileItem(final String name) {
            super(name);
        }
        
        static {
            HEADERS = new FetchProfileItem("HEADERS");
            SIZE = new FetchProfileItem("SIZE");
            MESSAGE = new FetchProfileItem("MESSAGE");
            INTERNALDATE = new FetchProfileItem("INTERNALDATE");
        }
    }
    
    public interface ProtocolCommand
    {
        Object doCommand(final IMAPProtocol p0) throws ProtocolException;
    }
}
