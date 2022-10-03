package javax.mail;

import java.util.Hashtable;
import java.util.EventListener;
import javax.mail.event.MailEvent;
import javax.mail.event.ConnectionEvent;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.concurrent.Executor;
import javax.mail.event.ConnectionListener;
import java.util.Vector;

public abstract class Service implements AutoCloseable
{
    protected Session session;
    protected volatile URLName url;
    protected boolean debug;
    private boolean connected;
    private final Vector<ConnectionListener> connectionListeners;
    private final EventQueue q;
    
    protected Service(final Session session, final URLName urlname) {
        this.url = null;
        this.debug = false;
        this.connected = false;
        this.connectionListeners = new Vector<ConnectionListener>();
        this.session = session;
        this.debug = session.getDebug();
        this.url = urlname;
        String protocol = null;
        String host = null;
        int port = -1;
        String user = null;
        String password = null;
        String file = null;
        if (this.url != null) {
            protocol = this.url.getProtocol();
            host = this.url.getHost();
            port = this.url.getPort();
            user = this.url.getUsername();
            password = this.url.getPassword();
            file = this.url.getFile();
        }
        if (protocol != null) {
            if (host == null) {
                host = session.getProperty("mail." + protocol + ".host");
            }
            if (user == null) {
                user = session.getProperty("mail." + protocol + ".user");
            }
        }
        if (host == null) {
            host = session.getProperty("mail.host");
        }
        if (user == null) {
            user = session.getProperty("mail.user");
        }
        if (user == null) {
            try {
                user = System.getProperty("user.name");
            }
            catch (final SecurityException ex) {}
        }
        this.url = new URLName(protocol, host, port, file, user, password);
        final String scope = session.getProperties().getProperty("mail.event.scope", "folder");
        final Executor executor = ((Hashtable<K, Executor>)session.getProperties()).get("mail.event.executor");
        if (scope.equalsIgnoreCase("application")) {
            this.q = EventQueue.getApplicationEventQueue(executor);
        }
        else if (scope.equalsIgnoreCase("session")) {
            this.q = session.getEventQueue();
        }
        else {
            this.q = new EventQueue(executor);
        }
    }
    
    public void connect() throws MessagingException {
        this.connect(null, null, null);
    }
    
    public void connect(final String host, final String user, final String password) throws MessagingException {
        this.connect(host, -1, user, password);
    }
    
    public void connect(final String user, final String password) throws MessagingException {
        this.connect(null, user, password);
    }
    
    public synchronized void connect(String host, int port, String user, String password) throws MessagingException {
        if (this.isConnected()) {
            throw new IllegalStateException("already connected");
        }
        boolean connected = false;
        boolean save = false;
        String protocol = null;
        String file = null;
        if (this.url != null) {
            protocol = this.url.getProtocol();
            if (host == null) {
                host = this.url.getHost();
            }
            if (port == -1) {
                port = this.url.getPort();
            }
            if (user == null) {
                user = this.url.getUsername();
                if (password == null) {
                    password = this.url.getPassword();
                }
            }
            else if (password == null && user.equals(this.url.getUsername())) {
                password = this.url.getPassword();
            }
            file = this.url.getFile();
        }
        if (protocol != null) {
            if (host == null) {
                host = this.session.getProperty("mail." + protocol + ".host");
            }
            if (user == null) {
                user = this.session.getProperty("mail." + protocol + ".user");
            }
        }
        if (host == null) {
            host = this.session.getProperty("mail.host");
        }
        if (user == null) {
            user = this.session.getProperty("mail.user");
        }
        if (user == null) {
            try {
                user = System.getProperty("user.name");
            }
            catch (final SecurityException ex2) {}
        }
        if (password == null && this.url != null) {
            this.setURLName(new URLName(protocol, host, port, file, user, null));
            final PasswordAuthentication pw = this.session.getPasswordAuthentication(this.getURLName());
            if (pw != null) {
                if (user == null) {
                    user = pw.getUserName();
                    password = pw.getPassword();
                }
                else if (user.equals(pw.getUserName())) {
                    password = pw.getPassword();
                }
            }
            else {
                save = true;
            }
        }
        AuthenticationFailedException authEx = null;
        try {
            connected = this.protocolConnect(host, port, user, password);
        }
        catch (final AuthenticationFailedException ex) {
            authEx = ex;
        }
        if (!connected) {
            InetAddress addr;
            try {
                addr = InetAddress.getByName(host);
            }
            catch (final UnknownHostException e) {
                addr = null;
            }
            final PasswordAuthentication pw = this.session.requestPasswordAuthentication(addr, port, protocol, null, user);
            if (pw != null) {
                user = pw.getUserName();
                password = pw.getPassword();
                connected = this.protocolConnect(host, port, user, password);
            }
        }
        if (connected) {
            this.setURLName(new URLName(protocol, host, port, file, user, password));
            if (save) {
                this.session.setPasswordAuthentication(this.getURLName(), new PasswordAuthentication(user, password));
            }
            this.setConnected(true);
            this.notifyConnectionListeners(1);
            return;
        }
        if (authEx != null) {
            throw authEx;
        }
        if (user == null) {
            throw new AuthenticationFailedException("failed to connect, no user name specified?");
        }
        if (password == null) {
            throw new AuthenticationFailedException("failed to connect, no password specified?");
        }
        throw new AuthenticationFailedException("failed to connect");
    }
    
    protected boolean protocolConnect(final String host, final int port, final String user, final String password) throws MessagingException {
        return false;
    }
    
    public synchronized boolean isConnected() {
        return this.connected;
    }
    
    protected synchronized void setConnected(final boolean connected) {
        this.connected = connected;
    }
    
    @Override
    public synchronized void close() throws MessagingException {
        this.setConnected(false);
        this.notifyConnectionListeners(3);
    }
    
    public URLName getURLName() {
        final URLName url = this.url;
        if (url != null && (url.getPassword() != null || url.getFile() != null)) {
            return new URLName(url.getProtocol(), url.getHost(), url.getPort(), null, url.getUsername(), null);
        }
        return url;
    }
    
    protected void setURLName(final URLName url) {
        this.url = url;
    }
    
    public void addConnectionListener(final ConnectionListener l) {
        this.connectionListeners.addElement(l);
    }
    
    public void removeConnectionListener(final ConnectionListener l) {
        this.connectionListeners.removeElement(l);
    }
    
    protected void notifyConnectionListeners(final int type) {
        if (this.connectionListeners.size() > 0) {
            final ConnectionEvent e = new ConnectionEvent(this, type);
            this.queueEvent(e, this.connectionListeners);
        }
        if (type == 3) {
            this.q.terminateQueue();
        }
    }
    
    @Override
    public String toString() {
        final URLName url = this.getURLName();
        if (url != null) {
            return url.toString();
        }
        return super.toString();
    }
    
    protected void queueEvent(final MailEvent event, final Vector<? extends EventListener> vector) {
        final Vector<? extends EventListener> v = (Vector<? extends EventListener>)vector.clone();
        this.q.enqueue(event, v);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.q.terminateQueue();
        }
        finally {
            super.finalize();
        }
    }
    
    Session getSession() {
        return this.session;
    }
    
    EventQueue getEventQueue() {
        return this.q;
    }
}
