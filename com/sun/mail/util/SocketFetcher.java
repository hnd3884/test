package com.sun.mail.util;

import java.util.Hashtable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.ConnectException;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.Collection;
import java.util.regex.Pattern;
import java.security.cert.CertificateParsingException;
import java.security.cert.Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocket;
import java.nio.channels.SocketChannel;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import javax.net.SocketFactory;
import java.net.InetAddress;
import java.util.logging.Level;
import java.net.Socket;
import java.util.Properties;

public class SocketFetcher
{
    private static MailLogger logger;
    
    private SocketFetcher() {
    }
    
    public static Socket getSocket(final String host, final int port, Properties props, String prefix, final boolean useSSL) throws IOException {
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("getSocket, host " + host + ", port " + port + ", prefix " + prefix + ", useSSL " + useSSL);
        }
        if (prefix == null) {
            prefix = "socket";
        }
        if (props == null) {
            props = new Properties();
        }
        final int cto = PropUtil.getIntProperty(props, prefix + ".connectiontimeout", -1);
        Socket socket = null;
        final String localaddrstr = props.getProperty(prefix + ".localaddress", null);
        InetAddress localaddr = null;
        if (localaddrstr != null) {
            localaddr = InetAddress.getByName(localaddrstr);
        }
        final int localport = PropUtil.getIntProperty(props, prefix + ".localport", 0);
        final boolean fb = PropUtil.getBooleanProperty(props, prefix + ".socketFactory.fallback", true);
        int sfPort = -1;
        String sfErr = "unknown socket factory";
        final int to = PropUtil.getIntProperty(props, prefix + ".timeout", -1);
        try {
            SocketFactory sf = null;
            String sfPortName = null;
            if (useSSL) {
                final Object sfo = ((Hashtable<K, Object>)props).get(prefix + ".ssl.socketFactory");
                if (sfo instanceof SocketFactory) {
                    sf = (SocketFactory)sfo;
                    sfErr = "SSL socket factory instance " + sf;
                }
                if (sf == null) {
                    final String sfClass = props.getProperty(prefix + ".ssl.socketFactory.class");
                    sf = getSocketFactory(sfClass);
                    sfErr = "SSL socket factory class " + sfClass;
                }
                sfPortName = ".ssl.socketFactory.port";
            }
            if (sf == null) {
                final Object sfo = ((Hashtable<K, Object>)props).get(prefix + ".socketFactory");
                if (sfo instanceof SocketFactory) {
                    sf = (SocketFactory)sfo;
                    sfErr = "socket factory instance " + sf;
                }
                if (sf == null) {
                    final String sfClass = props.getProperty(prefix + ".socketFactory.class");
                    sf = getSocketFactory(sfClass);
                    sfErr = "socket factory class " + sfClass;
                }
                sfPortName = ".socketFactory.port";
            }
            if (sf != null) {
                sfPort = PropUtil.getIntProperty(props, prefix + sfPortName, -1);
                if (sfPort == -1) {
                    sfPort = port;
                }
                socket = createSocket(localaddr, localport, host, sfPort, cto, to, props, prefix, sf, useSSL);
            }
        }
        catch (final SocketTimeoutException sex) {
            throw sex;
        }
        catch (final Exception ex) {
            if (!fb) {
                if (ex instanceof InvocationTargetException) {
                    final Throwable t = ((InvocationTargetException)ex).getTargetException();
                    if (t instanceof Exception) {
                        ex = (Exception)t;
                    }
                }
                if (ex instanceof IOException) {
                    throw (IOException)ex;
                }
                throw new SocketConnectException("Using " + sfErr, ex, host, sfPort, cto);
            }
        }
        if (socket == null) {
            socket = createSocket(localaddr, localport, host, port, cto, to, props, prefix, null, useSSL);
        }
        else if (to >= 0) {
            if (SocketFetcher.logger.isLoggable(Level.FINEST)) {
                SocketFetcher.logger.finest("set socket read timeout " + to);
            }
            socket.setSoTimeout(to);
        }
        return socket;
    }
    
    public static Socket getSocket(final String host, final int port, final Properties props, final String prefix) throws IOException {
        return getSocket(host, port, props, prefix, false);
    }
    
    private static Socket createSocket(final InetAddress localaddr, final int localport, final String host, final int port, final int cto, final int to, final Properties props, final String prefix, SocketFactory sf, final boolean useSSL) throws IOException {
        Socket socket = null;
        if (SocketFetcher.logger.isLoggable(Level.FINEST)) {
            SocketFetcher.logger.finest("create socket: prefix " + prefix + ", localaddr " + localaddr + ", localport " + localport + ", host " + host + ", port " + port + ", connection timeout " + cto + ", timeout " + to + ", socket factory " + sf + ", useSSL " + useSSL);
        }
        String proxyHost = props.getProperty(prefix + ".proxy.host", null);
        final String proxyUser = props.getProperty(prefix + ".proxy.user", null);
        final String proxyPassword = props.getProperty(prefix + ".proxy.password", null);
        int proxyPort = 80;
        String socksHost = null;
        int socksPort = 1080;
        String err = null;
        if (proxyHost != null) {
            final int i = proxyHost.indexOf(58);
            if (i >= 0) {
                try {
                    proxyPort = Integer.parseInt(proxyHost.substring(i + 1));
                }
                catch (final NumberFormatException ex2) {}
                proxyHost = proxyHost.substring(0, i);
            }
            proxyPort = PropUtil.getIntProperty(props, prefix + ".proxy.port", proxyPort);
            err = "Using web proxy host, port: " + proxyHost + ", " + proxyPort;
            if (SocketFetcher.logger.isLoggable(Level.FINER)) {
                SocketFetcher.logger.finer("web proxy host " + proxyHost + ", port " + proxyPort);
                if (proxyUser != null) {
                    SocketFetcher.logger.finer("web proxy user " + proxyUser + ", password " + ((proxyPassword == null) ? "<null>" : "<non-null>"));
                }
            }
        }
        else if ((socksHost = props.getProperty(prefix + ".socks.host", null)) != null) {
            final int i = socksHost.indexOf(58);
            if (i >= 0) {
                try {
                    socksPort = Integer.parseInt(socksHost.substring(i + 1));
                }
                catch (final NumberFormatException ex3) {}
                socksHost = socksHost.substring(0, i);
            }
            socksPort = PropUtil.getIntProperty(props, prefix + ".socks.port", socksPort);
            err = "Using SOCKS host, port: " + socksHost + ", " + socksPort;
            if (SocketFetcher.logger.isLoggable(Level.FINER)) {
                SocketFetcher.logger.finer("socks host " + socksHost + ", port " + socksPort);
            }
        }
        if (sf != null && !(sf instanceof SSLSocketFactory)) {
            socket = sf.createSocket();
        }
        if (socket == null) {
            if (socksHost != null) {
                socket = new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(socksHost, socksPort)));
            }
            else if (PropUtil.getBooleanProperty(props, prefix + ".usesocketchannels", false)) {
                SocketFetcher.logger.finer("using SocketChannels");
                socket = SocketChannel.open().socket();
            }
            else {
                socket = new Socket();
            }
        }
        if (to >= 0) {
            if (SocketFetcher.logger.isLoggable(Level.FINEST)) {
                SocketFetcher.logger.finest("set socket read timeout " + to);
            }
            socket.setSoTimeout(to);
        }
        final int writeTimeout = PropUtil.getIntProperty(props, prefix + ".writetimeout", -1);
        if (writeTimeout != -1) {
            if (SocketFetcher.logger.isLoggable(Level.FINEST)) {
                SocketFetcher.logger.finest("set socket write timeout " + writeTimeout);
            }
            socket = new WriteTimeoutSocket(socket, writeTimeout);
        }
        if (localaddr != null) {
            socket.bind(new InetSocketAddress(localaddr, localport));
        }
        try {
            SocketFetcher.logger.finest("connecting...");
            if (proxyHost != null) {
                proxyConnect(socket, proxyHost, proxyPort, proxyUser, proxyPassword, host, port, cto);
            }
            else if (cto >= 0) {
                socket.connect(new InetSocketAddress(host, port), cto);
            }
            else {
                socket.connect(new InetSocketAddress(host, port));
            }
            SocketFetcher.logger.finest("success!");
        }
        catch (final IOException ex) {
            SocketFetcher.logger.log(Level.FINEST, "connection failed", ex);
            throw new SocketConnectException(err, ex, host, port, cto);
        }
        if ((useSSL || sf instanceof SSLSocketFactory) && !(socket instanceof SSLSocket)) {
            SSLSocketFactory ssf = null;
            Label_1146: {
                final String trusted;
                if ((trusted = props.getProperty(prefix + ".ssl.trust")) != null) {
                    try {
                        final MailSSLSocketFactory msf = new MailSSLSocketFactory();
                        if (trusted.equals("*")) {
                            msf.setTrustAllHosts(true);
                        }
                        else {
                            msf.setTrustedHosts(trusted.split("\\s+"));
                        }
                        ssf = msf;
                        break Label_1146;
                    }
                    catch (final GeneralSecurityException gex) {
                        final IOException ioex = new IOException("Can't create MailSSLSocketFactory");
                        ioex.initCause(gex);
                        throw ioex;
                    }
                }
                if (sf instanceof SSLSocketFactory) {
                    ssf = (SSLSocketFactory)sf;
                }
                else {
                    ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
                }
            }
            socket = ssf.createSocket(socket, host, port, true);
            sf = ssf;
        }
        configureSSLSocket(socket, host, props, prefix, sf);
        return socket;
    }
    
    private static SocketFactory getSocketFactory(final String sfClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (sfClass == null || sfClass.length() == 0) {
            return null;
        }
        final ClassLoader cl = getContextClassLoader();
        Class<?> clsSockFact = null;
        if (cl != null) {
            try {
                clsSockFact = Class.forName(sfClass, false, cl);
            }
            catch (final ClassNotFoundException ex) {}
        }
        if (clsSockFact == null) {
            clsSockFact = Class.forName(sfClass);
        }
        final Method mthGetDefault = clsSockFact.getMethod("getDefault", (Class<?>[])new Class[0]);
        final SocketFactory sf = (SocketFactory)mthGetDefault.invoke(new Object(), new Object[0]);
        return sf;
    }
    
    @Deprecated
    public static Socket startTLS(final Socket socket) throws IOException {
        return startTLS(socket, new Properties(), "socket");
    }
    
    @Deprecated
    public static Socket startTLS(final Socket socket, final Properties props, final String prefix) throws IOException {
        final InetAddress a = socket.getInetAddress();
        final String host = a.getHostName();
        return startTLS(socket, host, props, prefix);
    }
    
    public static Socket startTLS(Socket socket, final String host, final Properties props, final String prefix) throws IOException {
        final int port = socket.getPort();
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("startTLS host " + host + ", port " + port);
        }
        String sfErr = "unknown socket factory";
        try {
            SSLSocketFactory ssf = null;
            SocketFactory sf = null;
            Object sfo = ((Hashtable<K, Object>)props).get(prefix + ".ssl.socketFactory");
            if (sfo instanceof SocketFactory) {
                sf = (SocketFactory)sfo;
                sfErr = "SSL socket factory instance " + sf;
            }
            if (sf == null) {
                final String sfClass = props.getProperty(prefix + ".ssl.socketFactory.class");
                sf = getSocketFactory(sfClass);
                sfErr = "SSL socket factory class " + sfClass;
            }
            if (sf != null && sf instanceof SSLSocketFactory) {
                ssf = (SSLSocketFactory)sf;
            }
            if (ssf == null) {
                sfo = ((Hashtable<K, Object>)props).get(prefix + ".socketFactory");
                if (sfo instanceof SocketFactory) {
                    sf = (SocketFactory)sfo;
                    sfErr = "socket factory instance " + sf;
                }
                if (sf == null) {
                    final String sfClass = props.getProperty(prefix + ".socketFactory.class");
                    sf = getSocketFactory(sfClass);
                    sfErr = "socket factory class " + sfClass;
                }
                if (sf != null && sf instanceof SSLSocketFactory) {
                    ssf = (SSLSocketFactory)sf;
                }
            }
            Label_0471: {
                if (ssf == null) {
                    final String trusted;
                    if ((trusted = props.getProperty(prefix + ".ssl.trust")) != null) {
                        try {
                            final MailSSLSocketFactory msf = new MailSSLSocketFactory();
                            if (trusted.equals("*")) {
                                msf.setTrustAllHosts(true);
                            }
                            else {
                                msf.setTrustedHosts(trusted.split("\\s+"));
                            }
                            ssf = msf;
                            sfErr = "mail SSL socket factory";
                            break Label_0471;
                        }
                        catch (final GeneralSecurityException gex) {
                            final IOException ioex = new IOException("Can't create MailSSLSocketFactory");
                            ioex.initCause(gex);
                            throw ioex;
                        }
                    }
                    ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
                    sfErr = "default SSL socket factory";
                }
            }
            socket = ssf.createSocket(socket, host, port, true);
            configureSSLSocket(socket, host, props, prefix, ssf);
        }
        catch (final Exception ex) {
            if (ex instanceof InvocationTargetException) {
                final Throwable t = ((InvocationTargetException)ex).getTargetException();
                if (t instanceof Exception) {
                    ex = (Exception)t;
                }
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            final IOException ioex2 = new IOException("Exception in startTLS using " + sfErr + ": host, port: " + host + ", " + port + "; Exception: " + ex);
            ioex2.initCause(ex);
            throw ioex2;
        }
        return socket;
    }
    
    private static void configureSSLSocket(final Socket socket, final String host, final Properties props, final String prefix, final SocketFactory sf) throws IOException {
        if (!(socket instanceof SSLSocket)) {
            return;
        }
        final SSLSocket sslsocket = (SSLSocket)socket;
        final String protocols = props.getProperty(prefix + ".ssl.protocols", null);
        if (protocols != null) {
            sslsocket.setEnabledProtocols(stringArray(protocols));
        }
        else {
            final String[] prots = sslsocket.getEnabledProtocols();
            if (SocketFetcher.logger.isLoggable(Level.FINER)) {
                SocketFetcher.logger.finer("SSL enabled protocols before " + Arrays.asList(prots));
            }
            final List<String> eprots = new ArrayList<String>();
            for (int i = 0; i < prots.length; ++i) {
                if (prots[i] != null && !prots[i].startsWith("SSL")) {
                    eprots.add(prots[i]);
                }
            }
            sslsocket.setEnabledProtocols(eprots.toArray(new String[eprots.size()]));
        }
        final String ciphers = props.getProperty(prefix + ".ssl.ciphersuites", null);
        if (ciphers != null) {
            sslsocket.setEnabledCipherSuites(stringArray(ciphers));
        }
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("SSL enabled protocols after " + Arrays.asList(sslsocket.getEnabledProtocols()));
            SocketFetcher.logger.finer("SSL enabled ciphers after " + Arrays.asList(sslsocket.getEnabledCipherSuites()));
        }
        sslsocket.startHandshake();
        final boolean idCheck = PropUtil.getBooleanProperty(props, prefix + ".ssl.checkserveridentity", false);
        if (idCheck) {
            checkServerIdentity(host, sslsocket);
        }
        if (sf instanceof MailSSLSocketFactory) {
            final MailSSLSocketFactory msf = (MailSSLSocketFactory)sf;
            if (!msf.isServerTrusted(host, sslsocket)) {
                throw cleanupAndThrow(sslsocket, new IOException("Server is not trusted: " + host));
            }
        }
    }
    
    private static IOException cleanupAndThrow(final Socket socket, final IOException ife) {
        try {
            socket.close();
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
    
    private static void checkServerIdentity(final String server, final SSLSocket sslSocket) throws IOException {
        try {
            final Certificate[] certChain = sslSocket.getSession().getPeerCertificates();
            if (certChain != null && certChain.length > 0 && certChain[0] instanceof X509Certificate && matchCert(server, (X509Certificate)certChain[0])) {
                return;
            }
        }
        catch (final SSLPeerUnverifiedException e) {
            sslSocket.close();
            final IOException ioex = new IOException("Can't verify identity of server: " + server);
            ioex.initCause(e);
            throw ioex;
        }
        sslSocket.close();
        throw new IOException("Can't verify identity of server: " + server);
    }
    
    private static boolean matchCert(final String server, final X509Certificate cert) {
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("matchCert server " + server + ", cert " + cert);
        }
        try {
            final Class<?> hnc = Class.forName("sun.security.util.HostnameChecker");
            final Method getInstance = hnc.getMethod("getInstance", Byte.TYPE);
            final Object hostnameChecker = getInstance.invoke(new Object(), 2);
            if (SocketFetcher.logger.isLoggable(Level.FINER)) {
                SocketFetcher.logger.finer("using sun.security.util.HostnameChecker");
            }
            final Method match = hnc.getMethod("match", String.class, X509Certificate.class);
            try {
                match.invoke(hostnameChecker, server, cert);
                return true;
            }
            catch (final InvocationTargetException cex) {
                SocketFetcher.logger.log(Level.FINER, "HostnameChecker FAIL", cex);
                return false;
            }
        }
        catch (final Exception ex) {
            SocketFetcher.logger.log(Level.FINER, "NO sun.security.util.HostnameChecker", ex);
            try {
                final Collection<List<?>> names = cert.getSubjectAlternativeNames();
                if (names != null) {
                    boolean foundName = false;
                    for (final List<?> nameEnt : names) {
                        final Integer type = (Integer)nameEnt.get(0);
                        if (type == 2) {
                            foundName = true;
                            final String name = (String)nameEnt.get(1);
                            if (SocketFetcher.logger.isLoggable(Level.FINER)) {
                                SocketFetcher.logger.finer("found name: " + name);
                            }
                            if (matchServer(server, name)) {
                                return true;
                            }
                            continue;
                        }
                    }
                    if (foundName) {
                        return false;
                    }
                }
            }
            catch (final CertificateParsingException ex2) {}
            final Pattern p = Pattern.compile("CN=([^,]*)");
            final Matcher m = p.matcher(cert.getSubjectX500Principal().getName());
            return m.find() && matchServer(server, m.group(1).trim());
        }
    }
    
    private static boolean matchServer(final String server, final String name) {
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("match server " + server + " with " + name);
        }
        if (!name.startsWith("*.")) {
            return server.equalsIgnoreCase(name);
        }
        final String tail = name.substring(2);
        if (tail.length() == 0) {
            return false;
        }
        final int off = server.length() - tail.length();
        return off >= 1 && server.charAt(off - 1) == '.' && server.regionMatches(true, off, tail, 0, tail.length());
    }
    
    private static void proxyConnect(final Socket socket, final String proxyHost, final int proxyPort, final String proxyUser, final String proxyPassword, final String host, final int port, final int cto) throws IOException {
        if (SocketFetcher.logger.isLoggable(Level.FINE)) {
            SocketFetcher.logger.fine("connecting through proxy " + proxyHost + ":" + proxyPort + " to " + host + ":" + port);
        }
        if (cto >= 0) {
            socket.connect(new InetSocketAddress(proxyHost, proxyPort), cto);
        }
        else {
            socket.connect(new InetSocketAddress(proxyHost, proxyPort));
        }
        final PrintStream os = new PrintStream(socket.getOutputStream(), false, StandardCharsets.UTF_8.name());
        final StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("CONNECT ").append(host).append(":").append(port).append(" HTTP/1.1\r\n");
        requestBuilder.append("Host: ").append(host).append(":").append(port).append("\r\n");
        if (proxyUser != null && proxyPassword != null) {
            final byte[] upbytes = (proxyUser + ':' + proxyPassword).getBytes(StandardCharsets.UTF_8);
            final String proxyHeaderValue = new String(BASE64EncoderStream.encode(upbytes), StandardCharsets.US_ASCII);
            requestBuilder.append("Proxy-Authorization: Basic ").append(proxyHeaderValue).append("\r\n");
        }
        requestBuilder.append("Proxy-Connection: keep-alive\r\n\r\n");
        os.print(requestBuilder.toString());
        os.flush();
        final BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        boolean first = true;
        String line;
        while ((line = r.readLine()) != null && line.length() != 0) {
            SocketFetcher.logger.finest(line);
            if (first) {
                final StringTokenizer st = new StringTokenizer(line);
                final String http = st.nextToken();
                final String code = st.nextToken();
                if (!code.equals("200")) {
                    try {
                        socket.close();
                    }
                    catch (final IOException ex2) {}
                    final ConnectException ex = new ConnectException("connection through proxy " + proxyHost + ":" + proxyPort + " to " + host + ":" + port + " failed: " + line);
                    SocketFetcher.logger.log(Level.FINE, "connect failed", ex);
                    throw ex;
                }
                first = false;
            }
        }
    }
    
    private static String[] stringArray(final String s) {
        final StringTokenizer st = new StringTokenizer(s);
        final List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        return tokens.toArray(new String[tokens.size()]);
    }
    
    private static ClassLoader getContextClassLoader() {
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
    
    static {
        SocketFetcher.logger = new MailLogger(SocketFetcher.class, "socket", "DEBUG SocketFetcher", PropUtil.getBooleanSystemProperty("mail.socket.debug", false), System.out);
    }
}
