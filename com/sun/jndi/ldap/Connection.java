package com.sun.jndi.ldap;

import java.util.Arrays;
import java.io.InterruptedIOException;
import javax.naming.InterruptedNamingException;
import javax.naming.ldap.Control;
import javax.net.ssl.SSLParameters;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.net.ssl.SSLSocket;
import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;
import javax.naming.CommunicationException;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.security.AccessController;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public final class Connection implements Runnable
{
    private static final boolean debug = false;
    private static final int dump = 0;
    private final Thread worker;
    private boolean v3;
    public final String host;
    public final int port;
    private boolean bound;
    private OutputStream traceFile;
    private String traceTagIn;
    private String traceTagOut;
    public InputStream inStream;
    public OutputStream outStream;
    public Socket sock;
    private final LdapClient parent;
    private int outMsgId;
    private LdapRequest pendingRequests;
    volatile IOException closureReason;
    volatile boolean useable;
    int readTimeout;
    int connectTimeout;
    private volatile boolean isUpgradedToStartTls;
    final Object startTlsLock;
    private static final boolean IS_HOSTNAME_VERIFICATION_DISABLED;
    private final Object pauseLock;
    private boolean paused;
    
    private static boolean hostnameVerificationDisabledValue() {
        final String s = AccessController.doPrivileged(() -> System.getProperty("com.sun.jndi.ldap.object.disableEndpointIdentification"));
        return s != null && (s.isEmpty() || Boolean.parseBoolean(s));
    }
    
    void setV3(final boolean v3) {
        this.v3 = v3;
    }
    
    void setBound() {
        this.bound = true;
    }
    
    Connection(final LdapClient parent, final String host, final int port, final String s, final int connectTimeout, final int readTimeout, final OutputStream traceFile) throws NamingException {
        this.v3 = true;
        this.bound = false;
        this.traceFile = null;
        this.traceTagIn = null;
        this.traceTagOut = null;
        this.outMsgId = 0;
        this.pendingRequests = null;
        this.closureReason = null;
        this.useable = true;
        this.startTlsLock = new Object();
        this.pauseLock = new Object();
        this.paused = false;
        this.host = host;
        this.port = port;
        this.parent = parent;
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
        if (traceFile != null) {
            this.traceFile = traceFile;
            this.traceTagIn = "<- " + host + ":" + port + "\n\n";
            this.traceTagOut = "-> " + host + ":" + port + "\n\n";
        }
        try {
            this.sock = this.createSocket(host, port, s, connectTimeout);
            this.inStream = new BufferedInputStream(this.sock.getInputStream());
            this.outStream = new BufferedOutputStream(this.sock.getOutputStream());
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            final CommunicationException ex2 = new CommunicationException(host + ":" + port);
            ex2.setRootCause(targetException);
            throw ex2;
        }
        catch (final Exception rootCause) {
            final CommunicationException ex3 = new CommunicationException(host + ":" + port);
            ex3.setRootCause(rootCause);
            throw ex3;
        }
        (this.worker = Obj.helper.createThread(this)).setDaemon(true);
        this.worker.start();
    }
    
    private Object createInetSocketAddress(final String s, final int n) throws NoSuchMethodException {
        try {
            return Class.forName("java.net.InetSocketAddress").getConstructor(String.class, Integer.TYPE).newInstance(s, new Integer(n));
        }
        catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | IllegalAccessException ex) {
            throw new NoSuchMethodException();
        }
    }
    
    private Socket createSocket(final String s, final int n, final String s2, final int soTimeout) throws Exception {
        Object o = null;
        if (s2 != null) {
            final Class<?> loadClass = Obj.helper.loadClass(s2);
            final Object invoke = loadClass.getMethod("getDefault", (Class[])new Class[0]).invoke(null, new Object[0]);
            if (soTimeout > 0) {
                try {
                    final Method method = loadClass.getMethod("createSocket", (Class[])new Class[0]);
                    final Method method2 = Socket.class.getMethod("connect", Class.forName("java.net.SocketAddress"), Integer.TYPE);
                    final Object inetSocketAddress = this.createInetSocketAddress(s, n);
                    o = method.invoke(invoke, new Object[0]);
                    method2.invoke(o, inetSocketAddress, new Integer(soTimeout));
                }
                catch (final NoSuchMethodException ex) {}
            }
            if (o == null) {
                o = loadClass.getMethod("createSocket", String.class, Integer.TYPE).invoke(invoke, s, new Integer(n));
            }
        }
        else {
            if (soTimeout > 0) {
                try {
                    final Constructor<Socket> constructor = Socket.class.getConstructor((Class<?>[])new Class[0]);
                    final Method method3 = Socket.class.getMethod("connect", Class.forName("java.net.SocketAddress"), Integer.TYPE);
                    final Object inetSocketAddress2 = this.createInetSocketAddress(s, n);
                    o = constructor.newInstance(new Object[0]);
                    method3.invoke(o, inetSocketAddress2, new Integer(soTimeout));
                }
                catch (final NoSuchMethodException ex2) {}
            }
            if (o == null) {
                o = new Socket(s, n);
            }
        }
        if (o instanceof SSLSocket) {
            final SSLSocket sslSocket = (SSLSocket)o;
            if (!Connection.IS_HOSTNAME_VERIFICATION_DISABLED) {
                final SSLParameters sslParameters = sslSocket.getSSLParameters();
                sslParameters.setEndpointIdentificationAlgorithm("LDAPS");
                sslSocket.setSSLParameters(sslParameters);
            }
            if (soTimeout > 0) {
                final int soTimeout2 = sslSocket.getSoTimeout();
                sslSocket.setSoTimeout(soTimeout);
                sslSocket.startHandshake();
                sslSocket.setSoTimeout(soTimeout2);
            }
        }
        return (Socket)o;
    }
    
    synchronized int getMsgId() {
        return ++this.outMsgId;
    }
    
    LdapRequest writeRequest(final BerEncoder berEncoder, final int n) throws IOException {
        return this.writeRequest(berEncoder, n, false, -1);
    }
    
    LdapRequest writeRequest(final BerEncoder berEncoder, final int n, final boolean b) throws IOException {
        return this.writeRequest(berEncoder, n, b, -1);
    }
    
    LdapRequest writeRequest(final BerEncoder berEncoder, final int n, final boolean b, final int n2) throws IOException {
        final LdapRequest ldapRequest = new LdapRequest(n, b, n2);
        this.addRequest(ldapRequest);
        if (this.traceFile != null) {
            Ber.dumpBER(this.traceFile, this.traceTagOut, berEncoder.getBuf(), 0, berEncoder.getDataLen());
        }
        this.unpauseReader();
        try {
            synchronized (this) {
                this.outStream.write(berEncoder.getBuf(), 0, berEncoder.getDataLen());
                this.outStream.flush();
            }
        }
        catch (final IOException closureReason) {
            this.cleanup(null, true);
            throw this.closureReason = closureReason;
        }
        return ldapRequest;
    }
    
    BerDecoder readReply(final LdapRequest ldapRequest) throws IOException, NamingException {
        NamingException ex = null;
        BerDecoder replyBer;
        try {
            replyBer = ldapRequest.getReplyBer(this.readTimeout);
        }
        catch (final InterruptedException ex2) {
            throw new InterruptedNamingException("Interrupted during LDAP operation");
        }
        catch (final CommunicationException ex3) {
            throw ex3;
        }
        catch (final NamingException ex4) {
            ex = ex4;
            replyBer = null;
        }
        if (replyBer == null) {
            this.abandonRequest(ldapRequest, null);
        }
        if (ex != null) {
            throw ex;
        }
        return replyBer;
    }
    
    private synchronized void addRequest(final LdapRequest ldapRequest) {
        if (this.pendingRequests == null) {
            this.pendingRequests = ldapRequest;
            ldapRequest.next = null;
        }
        else {
            ldapRequest.next = this.pendingRequests;
            this.pendingRequests = ldapRequest;
        }
    }
    
    synchronized LdapRequest findRequest(final int n) {
        for (LdapRequest ldapRequest = this.pendingRequests; ldapRequest != null; ldapRequest = ldapRequest.next) {
            if (ldapRequest.msgId == n) {
                return ldapRequest;
            }
        }
        return null;
    }
    
    synchronized void removeRequest(final LdapRequest ldapRequest) {
        LdapRequest ldapRequest2 = this.pendingRequests;
        LdapRequest ldapRequest3 = null;
        while (ldapRequest2 != null) {
            if (ldapRequest2 == ldapRequest) {
                ldapRequest2.cancel();
                if (ldapRequest3 != null) {
                    ldapRequest3.next = ldapRequest2.next;
                }
                else {
                    this.pendingRequests = ldapRequest2.next;
                }
                ldapRequest2.next = null;
            }
            ldapRequest3 = ldapRequest2;
            ldapRequest2 = ldapRequest2.next;
        }
    }
    
    void abandonRequest(final LdapRequest ldapRequest, final Control[] array) {
        this.removeRequest(ldapRequest);
        final BerEncoder berEncoder = new BerEncoder(256);
        final int msgId = this.getMsgId();
        try {
            berEncoder.beginSeq(48);
            berEncoder.encodeInt(msgId);
            berEncoder.encodeInt(ldapRequest.msgId, 80);
            if (this.v3) {
                LdapClient.encodeControls(berEncoder, array);
            }
            berEncoder.endSeq();
            if (this.traceFile != null) {
                Ber.dumpBER(this.traceFile, this.traceTagOut, berEncoder.getBuf(), 0, berEncoder.getDataLen());
            }
            synchronized (this) {
                this.outStream.write(berEncoder.getBuf(), 0, berEncoder.getDataLen());
                this.outStream.flush();
            }
        }
        catch (final IOException ex) {}
    }
    
    synchronized void abandonOutstandingReqs(final Control[] array) {
        for (LdapRequest pendingRequests = this.pendingRequests; pendingRequests != null; pendingRequests = (this.pendingRequests = pendingRequests.next)) {
            this.abandonRequest(pendingRequests, array);
        }
    }
    
    private void ldapUnbind(final Control[] array) {
        final BerEncoder berEncoder = new BerEncoder(256);
        final int msgId = this.getMsgId();
        try {
            berEncoder.beginSeq(48);
            berEncoder.encodeInt(msgId);
            berEncoder.encodeByte(66);
            berEncoder.encodeByte(0);
            if (this.v3) {
                LdapClient.encodeControls(berEncoder, array);
            }
            berEncoder.endSeq();
            if (this.traceFile != null) {
                Ber.dumpBER(this.traceFile, this.traceTagOut, berEncoder.getBuf(), 0, berEncoder.getDataLen());
            }
            synchronized (this) {
                this.outStream.write(berEncoder.getBuf(), 0, berEncoder.getDataLen());
                this.outStream.flush();
            }
        }
        catch (final IOException ex) {}
    }
    
    void cleanup(final Control[] array, final boolean b) {
        boolean b2 = false;
        synchronized (this) {
            this.useable = false;
            if (this.sock != null) {
                try {
                    if (!b) {
                        this.abandonOutstandingReqs(array);
                    }
                    if (this.bound) {
                        this.ldapUnbind(array);
                    }
                }
                finally {
                    try {
                        this.outStream.flush();
                        this.sock.close();
                        this.unpauseReader();
                    }
                    catch (final IOException ex) {}
                    if (!b) {
                        for (LdapRequest ldapRequest = this.pendingRequests; ldapRequest != null; ldapRequest = ldapRequest.next) {
                            ldapRequest.cancel();
                        }
                    }
                    this.sock = null;
                }
                b2 = b;
            }
            if (b2) {
                for (LdapRequest ldapRequest2 = this.pendingRequests; ldapRequest2 != null; ldapRequest2 = ldapRequest2.next) {
                    ldapRequest2.close();
                }
            }
        }
        if (b2) {
            this.parent.processConnectionClosure();
        }
    }
    
    public synchronized void replaceStreams(final InputStream inStream, final OutputStream outStream) {
        this.inStream = inStream;
        try {
            this.outStream.flush();
        }
        catch (final IOException ex) {}
        this.outStream = outStream;
    }
    
    public synchronized void replaceStreams(final InputStream inputStream, final OutputStream outputStream, final boolean isUpgradedToStartTls) {
        synchronized (this.startTlsLock) {
            this.replaceStreams(inputStream, outputStream);
            this.isUpgradedToStartTls = isUpgradedToStartTls;
        }
    }
    
    public boolean isUpgradedToStartTls() {
        return this.isUpgradedToStartTls;
    }
    
    private synchronized InputStream getInputStream() {
        return this.inStream;
    }
    
    private void unpauseReader() throws IOException {
        synchronized (this.pauseLock) {
            if (this.paused) {
                this.paused = false;
                this.pauseLock.notify();
            }
        }
    }
    
    private void pauseReader() throws IOException {
        this.paused = true;
        try {
            while (this.paused) {
                this.pauseLock.wait();
            }
        }
        catch (final InterruptedException ex) {
            throw new InterruptedIOException("Pause/unpause reader has problems.");
        }
    }
    
    @Override
    public void run() {
        InputStream inputStream = null;
        try {
            while (true) {
                try {
                    while (true) {
                        final byte[] array = new byte[129];
                        int n = 0;
                        int n2 = 0;
                        inputStream = this.getInputStream();
                        if (inputStream.read(array, n, 1) < 0) {
                            if (inputStream != this.getInputStream()) {
                                continue;
                            }
                            break;
                        }
                        else {
                            if (array[n++] != 48) {
                                continue;
                            }
                            int i = inputStream.read(array, n, 1);
                            if (i < 0) {
                                break;
                            }
                            int n3 = array[n++];
                            if ((n3 & 0x80) == 0x80) {
                                n2 = (n3 & 0x7F);
                                if (n2 > 4) {
                                    throw new IOException("Length coded with too many bytes: " + n2);
                                }
                                i = 0;
                                boolean b = false;
                                while (i < n2) {
                                    final int read = inputStream.read(array, n + i, n2 - i);
                                    if (read < 0) {
                                        b = true;
                                        break;
                                    }
                                    i += read;
                                }
                                if (b) {
                                    break;
                                }
                                n3 = 0;
                                for (int j = 0; j < n2; ++j) {
                                    n3 = (n3 << 8) + (array[n + j] & 0xFF);
                                }
                                n += i;
                            }
                            if (n2 > i) {
                                throw new IOException("Unexpected EOF while reading length");
                            }
                            if (n3 < 0) {
                                throw new IOException("Length too big: " + ((long)n3 & 0xFFFFFFFFL));
                            }
                            final byte[] fully = readFully(inputStream, n3);
                            final byte[] copy = Arrays.copyOf(array, n + fully.length);
                            System.arraycopy(fully, 0, copy, n, fully.length);
                            final int n4 = n + fully.length;
                            try {
                                final BerDecoder berDecoder = new BerDecoder(copy, 0, n4);
                                if (this.traceFile != null) {
                                    Ber.dumpBER(this.traceFile, this.traceTagIn, copy, 0, n4);
                                }
                                berDecoder.parseSeq(null);
                                final int int1 = berDecoder.parseInt();
                                berDecoder.reset();
                                if (int1 == 0) {
                                    this.parent.processUnsolicited(berDecoder);
                                }
                                else {
                                    final LdapRequest request = this.findRequest(int1);
                                    if (request == null) {
                                        continue;
                                    }
                                    synchronized (this.pauseLock) {
                                        if (!request.addReplyBer(berDecoder)) {
                                            continue;
                                        }
                                        this.pauseReader();
                                    }
                                }
                            }
                            catch (final Ber.DecodeException ex) {}
                        }
                    }
                }
                catch (final IOException ex2) {
                    if (inputStream != this.getInputStream()) {
                        continue;
                    }
                    throw ex2;
                }
                break;
            }
        }
        catch (final IOException closureReason) {
            this.closureReason = closureReason;
        }
        finally {
            this.cleanup(null, true);
        }
    }
    
    private static byte[] readFully(final InputStream inputStream, final int n) throws IOException {
        byte[] array = new byte[Math.min(n, 8192)];
        int i = 0;
        while (i < n) {
            int min;
            if (i >= array.length) {
                min = Math.min(n - i, array.length + 8192);
                if (array.length < i + min) {
                    array = Arrays.copyOf(array, i + min);
                }
            }
            else {
                min = array.length - i;
            }
            final int read = inputStream.read(array, i, min);
            if (read < 0) {
                if (array.length != i) {
                    array = Arrays.copyOf(array, i);
                    break;
                }
                break;
            }
            else {
                i += read;
            }
        }
        return array;
    }
    
    static {
        IS_HOSTNAME_VERIFICATION_DISABLED = hostnameVerificationDisabledValue();
    }
}
