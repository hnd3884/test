package sun.net;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.util.Arrays;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.Proxy;

public class NetworkClient
{
    public static final int DEFAULT_READ_TIMEOUT = -1;
    public static final int DEFAULT_CONNECT_TIMEOUT = -1;
    protected Proxy proxy;
    protected Socket serverSocket;
    public PrintStream serverOutput;
    public InputStream serverInput;
    protected static int defaultSoTimeout;
    protected static int defaultConnectTimeout;
    protected int readTimeout;
    protected int connectTimeout;
    protected static String encoding;
    
    private static boolean isASCIISuperset(final String s) throws Exception {
        return Arrays.equals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,".getBytes(s), new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59, 47, 63, 58, 64, 38, 61, 43, 36, 44 });
    }
    
    public void openServer(final String s, final int n) throws IOException, UnknownHostException {
        if (this.serverSocket != null) {
            this.closeServer();
        }
        this.serverSocket = this.doConnect(s, n);
        try {
            this.serverOutput = new PrintStream(new BufferedOutputStream(this.serverSocket.getOutputStream()), true, NetworkClient.encoding);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new InternalError(NetworkClient.encoding + "encoding not found", ex);
        }
        this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
    }
    
    protected Socket doConnect(final String s, final int n) throws IOException, UnknownHostException {
        Socket socket;
        if (this.proxy != null) {
            if (this.proxy.type() == Proxy.Type.SOCKS) {
                socket = AccessController.doPrivileged((PrivilegedAction<Socket>)new PrivilegedAction<Socket>() {
                    @Override
                    public Socket run() {
                        return new Socket(NetworkClient.this.proxy);
                    }
                });
            }
            else if (this.proxy.type() == Proxy.Type.DIRECT) {
                socket = this.createSocket();
            }
            else {
                socket = new Socket(Proxy.NO_PROXY);
            }
        }
        else {
            socket = this.createSocket();
        }
        if (this.connectTimeout >= 0) {
            socket.connect(new InetSocketAddress(s, n), this.connectTimeout);
        }
        else if (NetworkClient.defaultConnectTimeout > 0) {
            socket.connect(new InetSocketAddress(s, n), NetworkClient.defaultConnectTimeout);
        }
        else {
            socket.connect(new InetSocketAddress(s, n));
        }
        if (this.readTimeout >= 0) {
            socket.setSoTimeout(this.readTimeout);
        }
        else if (NetworkClient.defaultSoTimeout > 0) {
            socket.setSoTimeout(NetworkClient.defaultSoTimeout);
        }
        return socket;
    }
    
    protected Socket createSocket() throws IOException {
        return new Socket();
    }
    
    protected InetAddress getLocalAddress() throws IOException {
        if (this.serverSocket == null) {
            throw new IOException("not connected");
        }
        return AccessController.doPrivileged((PrivilegedAction<InetAddress>)new PrivilegedAction<InetAddress>() {
            @Override
            public InetAddress run() {
                return NetworkClient.this.serverSocket.getLocalAddress();
            }
        });
    }
    
    public void closeServer() throws IOException {
        if (!this.serverIsOpen()) {
            return;
        }
        this.serverSocket.close();
        this.serverSocket = null;
        this.serverInput = null;
        this.serverOutput = null;
    }
    
    public boolean serverIsOpen() {
        return this.serverSocket != null;
    }
    
    public NetworkClient(final String s, final int n) throws IOException {
        this.proxy = Proxy.NO_PROXY;
        this.serverSocket = null;
        this.readTimeout = -1;
        this.connectTimeout = -1;
        this.openServer(s, n);
    }
    
    public NetworkClient() {
        this.proxy = Proxy.NO_PROXY;
        this.serverSocket = null;
        this.readTimeout = -1;
        this.connectTimeout = -1;
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    
    public void setReadTimeout(int defaultSoTimeout) {
        if (defaultSoTimeout == -1) {
            defaultSoTimeout = NetworkClient.defaultSoTimeout;
        }
        if (this.serverSocket != null && defaultSoTimeout >= 0) {
            try {
                this.serverSocket.setSoTimeout(defaultSoTimeout);
            }
            catch (final IOException ex) {}
        }
        this.readTimeout = defaultSoTimeout;
    }
    
    public int getReadTimeout() {
        return this.readTimeout;
    }
    
    static {
        final int[] array = { 0, 0 };
        final String[] array2 = { null };
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                array[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 0);
                array[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 0);
                array2[0] = System.getProperty("file.encoding", "ISO8859_1");
                return null;
            }
        });
        if (array[0] != 0) {
            NetworkClient.defaultSoTimeout = array[0];
        }
        if (array[1] != 0) {
            NetworkClient.defaultConnectTimeout = array[1];
        }
        NetworkClient.encoding = array2[0];
        try {
            if (!isASCIISuperset(NetworkClient.encoding)) {
                NetworkClient.encoding = "ISO8859_1";
            }
        }
        catch (final Exception ex) {
            NetworkClient.encoding = "ISO8859_1";
        }
    }
}
