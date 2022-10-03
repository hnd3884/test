package javax.mail;

import java.io.File;
import java.util.Collections;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.URL;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.StringTokenizer;
import com.sun.mail.util.LineInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Collection;
import java.util.ServiceLoader;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.sun.mail.util.MailLogger;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;

public final class Session
{
    private final Properties props;
    private final Authenticator authenticator;
    private final Hashtable<URLName, PasswordAuthentication> authTable;
    private boolean debug;
    private PrintStream out;
    private MailLogger logger;
    private List<Provider> providers;
    private final Map<String, Provider> providersByProtocol;
    private final Map<String, Provider> providersByClassName;
    private final Properties addressMap;
    private boolean loadedProviders;
    private final EventQueue q;
    private static Session defaultSession;
    private static final String confDir;
    
    private Session(final Properties props, final Authenticator authenticator) {
        this.authTable = new Hashtable<URLName, PasswordAuthentication>();
        this.debug = false;
        this.providersByProtocol = new HashMap<String, Provider>();
        this.providersByClassName = new HashMap<String, Provider>();
        this.addressMap = new Properties();
        this.props = props;
        this.authenticator = authenticator;
        if (Boolean.valueOf(props.getProperty("mail.debug"))) {
            this.debug = true;
        }
        this.initLogger();
        this.logger.log(Level.CONFIG, "JavaMail version {0}", "1.6.2");
        Class<?> cl;
        if (authenticator != null) {
            cl = authenticator.getClass();
        }
        else {
            cl = this.getClass();
        }
        this.loadAddressMap(cl);
        this.q = new EventQueue(((Hashtable<K, Executor>)props).get("mail.event.executor"));
    }
    
    private final synchronized void initLogger() {
        this.logger = new MailLogger(this.getClass(), "DEBUG", this.debug, this.getDebugOut());
    }
    
    public static Session getInstance(final Properties props, final Authenticator authenticator) {
        return new Session(props, authenticator);
    }
    
    public static Session getInstance(final Properties props) {
        return new Session(props, null);
    }
    
    public static synchronized Session getDefaultInstance(final Properties props, final Authenticator authenticator) {
        if (Session.defaultSession == null) {
            final SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkSetFactory();
            }
            Session.defaultSession = new Session(props, authenticator);
        }
        else if (Session.defaultSession.authenticator != authenticator) {
            if (Session.defaultSession.authenticator == null || authenticator == null || Session.defaultSession.authenticator.getClass().getClassLoader() != authenticator.getClass().getClassLoader()) {
                throw new SecurityException("Access to default session denied");
            }
        }
        return Session.defaultSession;
    }
    
    public static Session getDefaultInstance(final Properties props) {
        return getDefaultInstance(props, null);
    }
    
    public synchronized void setDebug(final boolean debug) {
        this.debug = debug;
        this.initLogger();
        this.logger.log(Level.CONFIG, "setDebug: JavaMail version {0}", "1.6.2");
    }
    
    public synchronized boolean getDebug() {
        return this.debug;
    }
    
    public synchronized void setDebugOut(final PrintStream out) {
        this.out = out;
        this.initLogger();
    }
    
    public synchronized PrintStream getDebugOut() {
        if (this.out == null) {
            return System.out;
        }
        return this.out;
    }
    
    public synchronized Provider[] getProviders() {
        final List<Provider> plist = new ArrayList<Provider>();
        boolean needFallback = true;
        final ServiceLoader<Provider> loader = ServiceLoader.load(Provider.class);
        for (final Provider p : loader) {
            plist.add(p);
            needFallback = false;
        }
        if (!this.loadedProviders) {
            this.loadProviders(needFallback);
        }
        if (this.providers != null) {
            plist.addAll(this.providers);
        }
        final Provider[] _providers = new Provider[plist.size()];
        plist.toArray(_providers);
        return _providers;
    }
    
    public synchronized Provider getProvider(final String protocol) throws NoSuchProviderException {
        if (protocol == null || protocol.length() <= 0) {
            throw new NoSuchProviderException("Invalid protocol: null");
        }
        Provider _provider = null;
        final String _className = this.props.getProperty("mail." + protocol + ".class");
        if (_className != null) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("mail." + protocol + ".class property exists and points to " + _className);
            }
            _provider = this.getProviderByClassName(_className);
        }
        if (_provider == null) {
            _provider = this.getProviderByProtocol(protocol);
        }
        if (_provider == null) {
            throw new NoSuchProviderException("No provider for " + protocol);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("getProvider() returning " + _provider.toString());
        }
        return _provider;
    }
    
    public synchronized void setProvider(final Provider provider) throws NoSuchProviderException {
        if (provider == null) {
            throw new NoSuchProviderException("Can't set null provider");
        }
        this.providersByProtocol.put(provider.getProtocol(), provider);
        this.providersByClassName.put(provider.getClassName(), provider);
        ((Hashtable<String, String>)this.props).put("mail." + provider.getProtocol() + ".class", provider.getClassName());
    }
    
    public Store getStore() throws NoSuchProviderException {
        return this.getStore(this.getProperty("mail.store.protocol"));
    }
    
    public Store getStore(final String protocol) throws NoSuchProviderException {
        return this.getStore(new URLName(protocol, null, -1, null, null, null));
    }
    
    public Store getStore(final URLName url) throws NoSuchProviderException {
        final String protocol = url.getProtocol();
        final Provider p = this.getProvider(protocol);
        return this.getStore(p, url);
    }
    
    public Store getStore(final Provider provider) throws NoSuchProviderException {
        return this.getStore(provider, null);
    }
    
    private Store getStore(final Provider provider, final URLName url) throws NoSuchProviderException {
        if (provider == null || provider.getType() != Provider.Type.STORE) {
            throw new NoSuchProviderException("invalid provider");
        }
        return this.getService(provider, url, Store.class);
    }
    
    public Folder getFolder(final URLName url) throws MessagingException {
        final Store store = this.getStore(url);
        store.connect();
        return store.getFolder(url);
    }
    
    public Transport getTransport() throws NoSuchProviderException {
        String prot = this.getProperty("mail.transport.protocol");
        if (prot != null) {
            return this.getTransport(prot);
        }
        prot = ((Hashtable<K, String>)this.addressMap).get("rfc822");
        if (prot != null) {
            return this.getTransport(prot);
        }
        return this.getTransport("smtp");
    }
    
    public Transport getTransport(final String protocol) throws NoSuchProviderException {
        return this.getTransport(new URLName(protocol, null, -1, null, null, null));
    }
    
    public Transport getTransport(final URLName url) throws NoSuchProviderException {
        final String protocol = url.getProtocol();
        final Provider p = this.getProvider(protocol);
        return this.getTransport(p, url);
    }
    
    public Transport getTransport(final Provider provider) throws NoSuchProviderException {
        return this.getTransport(provider, null);
    }
    
    public Transport getTransport(final Address address) throws NoSuchProviderException {
        String transportProtocol = this.getProperty("mail.transport.protocol." + address.getType());
        if (transportProtocol != null) {
            return this.getTransport(transportProtocol);
        }
        transportProtocol = ((Hashtable<K, String>)this.addressMap).get(address.getType());
        if (transportProtocol != null) {
            return this.getTransport(transportProtocol);
        }
        throw new NoSuchProviderException("No provider for Address type: " + address.getType());
    }
    
    private Transport getTransport(final Provider provider, final URLName url) throws NoSuchProviderException {
        if (provider == null || provider.getType() != Provider.Type.TRANSPORT) {
            throw new NoSuchProviderException("invalid provider");
        }
        return this.getService(provider, url, Transport.class);
    }
    
    private <T extends Service> T getService(final Provider provider, URLName url, final Class<T> type) throws NoSuchProviderException {
        if (provider == null) {
            throw new NoSuchProviderException("null");
        }
        if (url == null) {
            url = new URLName(provider.getProtocol(), null, -1, null, null, null);
        }
        Object service = null;
        ClassLoader cl;
        if (this.authenticator != null) {
            cl = this.authenticator.getClass().getClassLoader();
        }
        else {
            cl = this.getClass().getClassLoader();
        }
        Class<?> serviceClass = null;
        try {
            final ClassLoader ccl = getContextClassLoader();
            if (ccl != null) {
                try {
                    serviceClass = Class.forName(provider.getClassName(), false, ccl);
                }
                catch (final ClassNotFoundException ex4) {}
            }
            if (serviceClass == null || !type.isAssignableFrom(serviceClass)) {
                serviceClass = Class.forName(provider.getClassName(), false, cl);
            }
            if (!type.isAssignableFrom(serviceClass)) {
                throw new ClassCastException(type.getName() + " " + serviceClass.getName());
            }
        }
        catch (final Exception ex1) {
            try {
                serviceClass = Class.forName(provider.getClassName());
                if (!type.isAssignableFrom(serviceClass)) {
                    throw new ClassCastException(type.getName() + " " + serviceClass.getName());
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.FINE, "Exception loading provider", ex2);
                throw new NoSuchProviderException(provider.getProtocol());
            }
        }
        try {
            final Class<?>[] c = { Session.class, URLName.class };
            final Constructor<?> cons = serviceClass.getConstructor(c);
            final Object[] o = { this, url };
            service = cons.newInstance(o);
        }
        catch (final Exception ex3) {
            this.logger.log(Level.FINE, "Exception loading provider", ex3);
            throw new NoSuchProviderException(provider.getProtocol());
        }
        return type.cast(service);
    }
    
    public void setPasswordAuthentication(final URLName url, final PasswordAuthentication pw) {
        if (pw == null) {
            this.authTable.remove(url);
        }
        else {
            this.authTable.put(url, pw);
        }
    }
    
    public PasswordAuthentication getPasswordAuthentication(final URLName url) {
        return this.authTable.get(url);
    }
    
    public PasswordAuthentication requestPasswordAuthentication(final InetAddress addr, final int port, final String protocol, final String prompt, final String defaultUserName) {
        if (this.authenticator != null) {
            return this.authenticator.requestPasswordAuthentication(addr, port, protocol, prompt, defaultUserName);
        }
        return null;
    }
    
    public Properties getProperties() {
        return this.props;
    }
    
    public String getProperty(final String name) {
        return this.props.getProperty(name);
    }
    
    private Provider getProviderByClassName(final String className) {
        Provider p = this.providersByClassName.get(className);
        if (p != null) {
            return p;
        }
        final ServiceLoader<Provider> loader = ServiceLoader.load(Provider.class);
        for (final Provider pp : loader) {
            if (className.equals(pp.getClassName())) {
                return pp;
            }
        }
        if (!this.loadedProviders) {
            this.loadProviders(true);
            p = this.providersByClassName.get(className);
        }
        return p;
    }
    
    private Provider getProviderByProtocol(final String protocol) {
        Provider p = this.providersByProtocol.get(protocol);
        if (p != null) {
            return p;
        }
        final ServiceLoader<Provider> loader = ServiceLoader.load(Provider.class);
        for (final Provider pp : loader) {
            if (protocol.equals(pp.getProtocol())) {
                return pp;
            }
        }
        if (!this.loadedProviders) {
            this.loadProviders(true);
            p = this.providersByProtocol.get(protocol);
        }
        return p;
    }
    
    private void loadProviders(final boolean fallback) {
        final StreamLoader loader = new StreamLoader() {
            @Override
            public void load(final InputStream is) throws IOException {
                Session.this.loadProvidersFromStream(is);
            }
        };
        try {
            if (Session.confDir != null) {
                this.loadFile(Session.confDir + "javamail.providers", loader);
            }
        }
        catch (final SecurityException ex) {}
        Class<?> cl;
        if (this.authenticator != null) {
            cl = this.authenticator.getClass();
        }
        else {
            cl = this.getClass();
        }
        this.loadAllResources("META-INF/javamail.providers", cl, loader);
        this.loadResource("/META-INF/javamail.default.providers", cl, loader, false);
        if ((this.providers == null || this.providers.size() == 0) && fallback) {
            this.logger.config("failed to load any providers, using defaults");
            this.addProvider(new Provider(Provider.Type.STORE, "imap", "com.sun.mail.imap.IMAPStore", "Oracle", "1.6.2"));
            this.addProvider(new Provider(Provider.Type.STORE, "imaps", "com.sun.mail.imap.IMAPSSLStore", "Oracle", "1.6.2"));
            this.addProvider(new Provider(Provider.Type.STORE, "pop3", "com.sun.mail.pop3.POP3Store", "Oracle", "1.6.2"));
            this.addProvider(new Provider(Provider.Type.STORE, "pop3s", "com.sun.mail.pop3.POP3SSLStore", "Oracle", "1.6.2"));
            this.addProvider(new Provider(Provider.Type.TRANSPORT, "smtp", "com.sun.mail.smtp.SMTPTransport", "Oracle", "1.6.2"));
            this.addProvider(new Provider(Provider.Type.TRANSPORT, "smtps", "com.sun.mail.smtp.SMTPSSLTransport", "Oracle", "1.6.2"));
        }
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("Tables of loaded providers from javamail.providers");
            this.logger.config("Providers Listed By Class Name: " + this.providersByClassName.toString());
            this.logger.config("Providers Listed By Protocol: " + this.providersByProtocol.toString());
        }
        this.loadedProviders = true;
    }
    
    private void loadProvidersFromStream(final InputStream is) throws IOException {
        if (is != null) {
            final LineInputStream lis = new LineInputStream(is);
            String currLine;
            while ((currLine = lis.readLine()) != null) {
                if (currLine.startsWith("#")) {
                    continue;
                }
                if (currLine.trim().length() == 0) {
                    continue;
                }
                Provider.Type type = null;
                String protocol = null;
                String className = null;
                String vendor = null;
                String version = null;
                final StringTokenizer tuples = new StringTokenizer(currLine, ";");
                while (tuples.hasMoreTokens()) {
                    final String currTuple = tuples.nextToken().trim();
                    final int sep = currTuple.indexOf("=");
                    if (currTuple.startsWith("protocol=")) {
                        protocol = currTuple.substring(sep + 1);
                    }
                    else if (currTuple.startsWith("type=")) {
                        final String strType = currTuple.substring(sep + 1);
                        if (strType.equalsIgnoreCase("store")) {
                            type = Provider.Type.STORE;
                        }
                        else {
                            if (!strType.equalsIgnoreCase("transport")) {
                                continue;
                            }
                            type = Provider.Type.TRANSPORT;
                        }
                    }
                    else if (currTuple.startsWith("class=")) {
                        className = currTuple.substring(sep + 1);
                    }
                    else if (currTuple.startsWith("vendor=")) {
                        vendor = currTuple.substring(sep + 1);
                    }
                    else {
                        if (!currTuple.startsWith("version=")) {
                            continue;
                        }
                        version = currTuple.substring(sep + 1);
                    }
                }
                if (type == null || protocol == null || className == null || protocol.length() <= 0 || className.length() <= 0) {
                    this.logger.log(Level.CONFIG, "Bad provider entry: {0}", currLine);
                }
                else {
                    final Provider provider = new Provider(type, protocol, className, vendor, version);
                    this.addProvider(provider);
                }
            }
        }
    }
    
    public synchronized void addProvider(final Provider provider) {
        if (this.providers == null) {
            this.providers = new ArrayList<Provider>();
        }
        this.providers.add(provider);
        this.providersByClassName.put(provider.getClassName(), provider);
        if (!this.providersByProtocol.containsKey(provider.getProtocol())) {
            this.providersByProtocol.put(provider.getProtocol(), provider);
        }
    }
    
    private void loadAddressMap(final Class<?> cl) {
        final StreamLoader loader = new StreamLoader() {
            @Override
            public void load(final InputStream is) throws IOException {
                Session.this.addressMap.load(is);
            }
        };
        this.loadResource("/META-INF/javamail.default.address.map", cl, loader, true);
        this.loadAllResources("META-INF/javamail.address.map", cl, loader);
        try {
            if (Session.confDir != null) {
                this.loadFile(Session.confDir + "javamail.address.map", loader);
            }
        }
        catch (final SecurityException ex) {}
        if (this.addressMap.isEmpty()) {
            this.logger.config("failed to load address map, using defaults");
            ((Hashtable<String, String>)this.addressMap).put("rfc822", "smtp");
        }
    }
    
    public synchronized void setProtocolForAddress(final String addresstype, final String protocol) {
        if (protocol == null) {
            this.addressMap.remove(addresstype);
        }
        else {
            ((Hashtable<String, String>)this.addressMap).put(addresstype, protocol);
        }
    }
    
    private void loadFile(final String name, final StreamLoader loader) {
        InputStream clis = null;
        try {
            clis = new BufferedInputStream(new FileInputStream(name));
            loader.load(clis);
            this.logger.log(Level.CONFIG, "successfully loaded file: {0}", name);
        }
        catch (final FileNotFoundException ex) {}
        catch (final IOException e) {
            if (this.logger.isLoggable(Level.CONFIG)) {
                this.logger.log(Level.CONFIG, "not loading file: " + name, e);
            }
        }
        catch (final SecurityException sex) {
            if (this.logger.isLoggable(Level.CONFIG)) {
                this.logger.log(Level.CONFIG, "not loading file: " + name, sex);
            }
        }
        finally {
            try {
                if (clis != null) {
                    clis.close();
                }
            }
            catch (final IOException ex2) {}
        }
    }
    
    private void loadResource(final String name, final Class<?> cl, final StreamLoader loader, final boolean expected) {
        InputStream clis = null;
        try {
            clis = getResourceAsStream(cl, name);
            if (clis != null) {
                loader.load(clis);
                this.logger.log(Level.CONFIG, "successfully loaded resource: {0}", name);
            }
            else if (expected) {
                this.logger.log(Level.WARNING, "expected resource not found: {0}", name);
            }
        }
        catch (final IOException e) {
            this.logger.log(Level.CONFIG, "Exception loading resource", e);
        }
        catch (final SecurityException sex) {
            this.logger.log(Level.CONFIG, "Exception loading resource", sex);
        }
        finally {
            try {
                if (clis != null) {
                    clis.close();
                }
            }
            catch (final IOException ex) {}
        }
    }
    
    private void loadAllResources(final String name, final Class<?> cl, final StreamLoader loader) {
        boolean anyLoaded = false;
        try {
            ClassLoader cld = null;
            cld = getContextClassLoader();
            if (cld == null) {
                cld = cl.getClassLoader();
            }
            URL[] urls;
            if (cld != null) {
                urls = getResources(cld, name);
            }
            else {
                urls = getSystemResources(name);
            }
            if (urls != null) {
                for (int i = 0; i < urls.length; ++i) {
                    final URL url = urls[i];
                    InputStream clis = null;
                    this.logger.log(Level.CONFIG, "URL {0}", url);
                    try {
                        clis = openStream(url);
                        if (clis != null) {
                            loader.load(clis);
                            anyLoaded = true;
                            this.logger.log(Level.CONFIG, "successfully loaded resource: {0}", url);
                        }
                        else {
                            this.logger.log(Level.CONFIG, "not loading resource: {0}", url);
                        }
                    }
                    catch (final FileNotFoundException ex2) {}
                    catch (final IOException ioex) {
                        this.logger.log(Level.CONFIG, "Exception loading resource", ioex);
                    }
                    catch (final SecurityException sex) {
                        this.logger.log(Level.CONFIG, "Exception loading resource", sex);
                    }
                    finally {
                        try {
                            if (clis != null) {
                                clis.close();
                            }
                        }
                        catch (final IOException ex3) {}
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.CONFIG, "Exception loading resource", ex);
        }
        if (!anyLoaded) {
            this.loadResource("/" + name, cl, loader, false);
        }
    }
    
    static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (final SecurityException ex) {}
                return cl;
            }
        });
    }
    
    private static InputStream getResourceAsStream(final Class<?> c, final String name) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction<InputStream>() {
                @Override
                public InputStream run() throws IOException {
                    try {
                        return c.getResourceAsStream(name);
                    }
                    catch (final RuntimeException e) {
                        final IOException ioex = new IOException("ClassLoader.getResourceAsStream failed");
                        ioex.initCause(e);
                        throw ioex;
                    }
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }
    
    private static URL[] getResources(final ClassLoader cl, final String name) {
        return AccessController.doPrivileged((PrivilegedAction<URL[]>)new PrivilegedAction<URL[]>() {
            @Override
            public URL[] run() {
                URL[] ret = null;
                try {
                    final List<URL> v = Collections.list(cl.getResources(name));
                    if (!v.isEmpty()) {
                        ret = new URL[v.size()];
                        v.toArray(ret);
                    }
                }
                catch (final IOException ex) {}
                catch (final SecurityException ex2) {}
                return ret;
            }
        });
    }
    
    private static URL[] getSystemResources(final String name) {
        return AccessController.doPrivileged((PrivilegedAction<URL[]>)new PrivilegedAction<URL[]>() {
            @Override
            public URL[] run() {
                URL[] ret = null;
                try {
                    final List<URL> v = Collections.list(ClassLoader.getSystemResources(name));
                    if (!v.isEmpty()) {
                        ret = new URL[v.size()];
                        v.toArray(ret);
                    }
                }
                catch (final IOException ex) {}
                catch (final SecurityException ex2) {}
                return ret;
            }
        });
    }
    
    private static InputStream openStream(final URL url) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction<InputStream>() {
                @Override
                public InputStream run() throws IOException {
                    return url.openStream();
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }
    
    EventQueue getEventQueue() {
        return this.q;
    }
    
    static {
        Session.defaultSession = null;
        String dir = null;
        try {
            dir = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    final String home = System.getProperty("java.home");
                    final String newdir = home + File.separator + "conf";
                    final File conf = new File(newdir);
                    if (conf.exists()) {
                        return newdir + File.separator;
                    }
                    return home + File.separator + "lib" + File.separator;
                }
            });
        }
        catch (final Exception ex) {}
        confDir = dir;
    }
}
