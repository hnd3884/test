package com.sun.mail.pop3;

import java.util.Collections;
import java.io.IOException;
import javax.mail.MessagingException;
import com.sun.mail.util.SocketConnectException;
import com.sun.mail.util.MailConnectException;
import java.io.EOFException;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import java.util.logging.Level;
import com.sun.mail.util.PropUtil;
import javax.mail.URLName;
import javax.mail.Session;
import java.io.File;
import java.lang.reflect.Constructor;
import com.sun.mail.util.MailLogger;
import java.util.Map;
import javax.mail.Store;

public class POP3Store extends Store
{
    private String name;
    private int defaultPort;
    private boolean isSSL;
    private Protocol port;
    private POP3Folder portOwner;
    private String host;
    private int portNum;
    private String user;
    private String passwd;
    private boolean useStartTLS;
    private boolean requireStartTLS;
    private boolean usingSSL;
    private Map<String, String> capabilities;
    private MailLogger logger;
    volatile Constructor<?> messageConstructor;
    volatile boolean rsetBeforeQuit;
    volatile boolean disableTop;
    volatile boolean forgetTopHeaders;
    volatile boolean supportsUidl;
    volatile boolean cacheWriteTo;
    volatile boolean useFileCache;
    volatile File fileCacheDir;
    volatile boolean keepMessageContent;
    volatile boolean finalizeCleanClose;
    
    public POP3Store(final Session session, final URLName url) {
        this(session, url, "pop3", false);
    }
    
    public POP3Store(final Session session, final URLName url, String name, boolean isSSL) {
        super(session, url);
        this.name = "pop3";
        this.defaultPort = 110;
        this.isSSL = false;
        this.port = null;
        this.portOwner = null;
        this.host = null;
        this.portNum = -1;
        this.user = null;
        this.passwd = null;
        this.useStartTLS = false;
        this.requireStartTLS = false;
        this.usingSSL = false;
        this.messageConstructor = null;
        this.rsetBeforeQuit = false;
        this.disableTop = false;
        this.forgetTopHeaders = false;
        this.supportsUidl = true;
        this.cacheWriteTo = false;
        this.useFileCache = false;
        this.fileCacheDir = null;
        this.keepMessageContent = false;
        this.finalizeCleanClose = false;
        if (url != null) {
            name = url.getProtocol();
        }
        this.name = name;
        this.logger = new MailLogger(this.getClass(), "DEBUG POP3", session.getDebug(), session.getDebugOut());
        if (!isSSL) {
            isSSL = PropUtil.getBooleanProperty(session.getProperties(), "mail." + name + ".ssl.enable", false);
        }
        if (isSSL) {
            this.defaultPort = 995;
        }
        else {
            this.defaultPort = 110;
        }
        this.isSSL = isSSL;
        this.rsetBeforeQuit = this.getBoolProp("rsetbeforequit");
        this.disableTop = this.getBoolProp("disabletop");
        this.forgetTopHeaders = this.getBoolProp("forgettopheaders");
        this.cacheWriteTo = this.getBoolProp("cachewriteto");
        this.useFileCache = this.getBoolProp("filecache.enable");
        final String dir = session.getProperty("mail." + name + ".filecache.dir");
        if (dir != null && this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail." + name + ".filecache.dir: " + dir);
        }
        if (dir != null) {
            this.fileCacheDir = new File(dir);
        }
        this.keepMessageContent = this.getBoolProp("keepmessagecontent");
        this.useStartTLS = this.getBoolProp("starttls.enable");
        this.requireStartTLS = this.getBoolProp("starttls.required");
        this.finalizeCleanClose = this.getBoolProp("finalizecleanclose");
        final String s = session.getProperty("mail." + name + ".message.class");
        if (s != null) {
            this.logger.log(Level.CONFIG, "message class: {0}", s);
            try {
                final ClassLoader cl = this.getClass().getClassLoader();
                Class<?> messageClass = null;
                try {
                    messageClass = Class.forName(s, false, cl);
                }
                catch (final ClassNotFoundException ex1) {
                    messageClass = Class.forName(s);
                }
                final Class<?>[] c = { Folder.class, Integer.TYPE };
                this.messageConstructor = messageClass.getConstructor(c);
            }
            catch (final Exception ex2) {
                this.logger.log(Level.CONFIG, "failed to load message class", ex2);
            }
        }
    }
    
    private final synchronized boolean getBoolProp(String prop) {
        prop = "mail." + this.name + "." + prop;
        final boolean val = PropUtil.getBooleanProperty(this.session.getProperties(), prop, false);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config(prop + ": " + val);
        }
        return val;
    }
    
    @Override
    synchronized Session getSession() {
        return this.session;
    }
    
    @Override
    protected synchronized boolean protocolConnect(final String host, int portNum, final String user, final String passwd) throws MessagingException {
        if (host == null || passwd == null || user == null) {
            return false;
        }
        if (portNum == -1) {
            portNum = PropUtil.getIntProperty(this.session.getProperties(), "mail." + this.name + ".port", -1);
        }
        if (portNum == -1) {
            portNum = this.defaultPort;
        }
        this.host = host;
        this.portNum = portNum;
        this.user = user;
        this.passwd = passwd;
        try {
            this.port = this.getPort(null);
        }
        catch (final EOFException eex) {
            throw new AuthenticationFailedException(eex.getMessage());
        }
        catch (final SocketConnectException scex) {
            throw new MailConnectException(scex);
        }
        catch (final IOException ioex) {
            throw new MessagingException("Connect failed", ioex);
        }
        return true;
    }
    
    @Override
    public synchronized boolean isConnected() {
        if (!super.isConnected()) {
            return false;
        }
        try {
            if (this.port == null) {
                this.port = this.getPort(null);
            }
            else if (!this.port.noop()) {
                throw new IOException("NOOP failed");
            }
            return true;
        }
        catch (final IOException ioex) {
            try {
                super.close();
            }
            catch (final MessagingException ex) {}
            return false;
        }
    }
    
    synchronized Protocol getPort(final POP3Folder owner) throws IOException {
        if (this.port != null && this.portOwner == null) {
            this.portOwner = owner;
            return this.port;
        }
        final Protocol p = new Protocol(this.host, this.portNum, this.logger, this.session.getProperties(), "mail." + this.name, this.isSSL);
        if (this.useStartTLS || this.requireStartTLS) {
            if (p.hasCapability("STLS")) {
                if (p.stls()) {
                    p.setCapabilities(p.capa());
                }
                else if (this.requireStartTLS) {
                    this.logger.fine("STLS required but failed");
                    throw cleanupAndThrow(p, new EOFException("STLS required but failed"));
                }
            }
            else if (this.requireStartTLS) {
                this.logger.fine("STLS required but not supported");
                throw cleanupAndThrow(p, new EOFException("STLS required but not supported"));
            }
        }
        this.capabilities = p.getCapabilities();
        this.usingSSL = p.isSSL();
        if (!this.disableTop && this.capabilities != null && !this.capabilities.containsKey("TOP")) {
            this.disableTop = true;
            this.logger.fine("server doesn't support TOP, disabling it");
        }
        this.supportsUidl = (this.capabilities == null || this.capabilities.containsKey("UIDL"));
        String msg = null;
        if ((msg = p.login(this.user, this.passwd)) != null) {
            throw cleanupAndThrow(p, new EOFException(msg));
        }
        if (this.port == null && owner != null) {
            this.port = p;
            this.portOwner = owner;
        }
        if (this.portOwner == null) {
            this.portOwner = owner;
        }
        return p;
    }
    
    private static IOException cleanupAndThrow(final Protocol p, final IOException ife) {
        try {
            p.quit();
        }
        catch (final Throwable thr) {
            if (isRecoverable(thr)) {
                ife.addSuppressed(thr);
            }
            else {
                thr.addSuppressed(ife);
                if (thr instanceof Error) {
                    throw (Error)thr;
                }
                if (thr instanceof RuntimeException) {
                    throw (RuntimeException)thr;
                }
                throw new RuntimeException("unexpected exception", thr);
            }
        }
        return ife;
    }
    
    private static boolean isRecoverable(final Throwable t) {
        return t instanceof Exception || t instanceof LinkageError;
    }
    
    synchronized void closePort(final POP3Folder owner) {
        if (this.portOwner == owner) {
            this.port = null;
            this.portOwner = null;
        }
    }
    
    @Override
    public synchronized void close() throws MessagingException {
        this.close(false);
    }
    
    synchronized void close(final boolean force) throws MessagingException {
        try {
            if (this.port != null) {
                if (force) {
                    this.port.close();
                }
                else {
                    this.port.quit();
                }
            }
        }
        catch (final IOException ex) {}
        finally {
            this.port = null;
            super.close();
        }
    }
    
    @Override
    public Folder getDefaultFolder() throws MessagingException {
        this.checkConnected();
        return new DefaultFolder(this);
    }
    
    @Override
    public Folder getFolder(final String name) throws MessagingException {
        this.checkConnected();
        return new POP3Folder(this, name);
    }
    
    @Override
    public Folder getFolder(final URLName url) throws MessagingException {
        this.checkConnected();
        return new POP3Folder(this, url.getFile());
    }
    
    public Map<String, String> capabilities() throws MessagingException {
        final Map<String, String> c;
        synchronized (this) {
            c = this.capabilities;
        }
        if (c != null) {
            return Collections.unmodifiableMap((Map<? extends String, ? extends String>)c);
        }
        return Collections.emptyMap();
    }
    
    public synchronized boolean isSSL() {
        return this.usingSSL;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.port != null) {
                this.close(!this.finalizeCleanClose);
            }
        }
        finally {
            super.finalize();
        }
    }
    
    private void checkConnected() throws MessagingException {
        if (!super.isConnected()) {
            throw new MessagingException("Not connected");
        }
    }
}
