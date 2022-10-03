package sun.net.www.protocol.ftp;

import java.io.FilterOutputStream;
import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.net.SocketPermission;
import java.io.FileNotFoundException;
import sun.net.www.MeteredStream;
import sun.net.ProgressSource;
import sun.net.ProgressMonitor;
import sun.net.www.MessageHeader;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.net.URI;
import sun.net.ftp.FtpLoginException;
import sun.net.ftp.FtpProtocolException;
import java.net.UnknownHostException;
import sun.security.action.GetPropertyAction;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.ProxySelector;
import sun.net.www.ParseUtil;
import sun.net.util.IPAddressUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import sun.net.ftp.FtpClient;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Proxy;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.net.www.URLConnection;

public class FtpURLConnection extends URLConnection
{
    HttpURLConnection http;
    private Proxy instProxy;
    InputStream is;
    OutputStream os;
    FtpClient ftp;
    Permission permission;
    String password;
    String user;
    String host;
    String pathname;
    String filename;
    String fullpath;
    int port;
    static final int NONE = 0;
    static final int ASCII = 1;
    static final int BIN = 2;
    static final int DIR = 3;
    int type;
    private int connectTimeout;
    private int readTimeout;
    
    static URL checkURL(final URL url) throws IllegalArgumentException {
        if (url != null && url.toExternalForm().indexOf(10) > -1) {
            final MalformedURLException ex = new MalformedURLException("Illegal character in URL");
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        final String checkAuthority = IPAddressUtil.checkAuthority(url);
        if (checkAuthority != null) {
            final MalformedURLException ex2 = new MalformedURLException(checkAuthority);
            throw new IllegalArgumentException(ex2.getMessage(), ex2);
        }
        return url;
    }
    
    public FtpURLConnection(final URL url) {
        this(url, null);
    }
    
    FtpURLConnection(final URL url, final Proxy instProxy) {
        super(checkURL(url));
        this.http = null;
        this.is = null;
        this.os = null;
        this.ftp = null;
        this.type = 0;
        this.connectTimeout = -1;
        this.readTimeout = -1;
        this.instProxy = instProxy;
        this.host = url.getHost();
        this.port = url.getPort();
        final String userInfo = url.getUserInfo();
        if (userInfo != null) {
            int index = userInfo.indexOf(58);
            if (index == -1) {
                this.user = ParseUtil.decode(userInfo);
                this.password = null;
            }
            else {
                this.user = ParseUtil.decode(userInfo.substring(0, index++));
                this.password = ParseUtil.decode(userInfo.substring(index));
            }
        }
    }
    
    private void setTimeouts() {
        if (this.ftp != null) {
            if (this.connectTimeout >= 0) {
                this.ftp.setConnectTimeout(this.connectTimeout);
            }
            if (this.readTimeout >= 0) {
                this.ftp.setReadTimeout(this.readTimeout);
            }
        }
    }
    
    @Override
    public synchronized void connect() throws IOException {
        if (this.connected) {
            return;
        }
        Proxy instProxy = null;
        if (this.instProxy == null) {
            final ProxySelector proxySelector = AccessController.doPrivileged((PrivilegedAction<ProxySelector>)new PrivilegedAction<ProxySelector>() {
                @Override
                public ProxySelector run() {
                    return ProxySelector.getDefault();
                }
            });
            if (proxySelector != null) {
                final URI uri = ParseUtil.toURI(this.url);
                final Iterator<Proxy> iterator = proxySelector.select(uri).iterator();
                while (iterator.hasNext()) {
                    instProxy = iterator.next();
                    if (instProxy == null || instProxy == Proxy.NO_PROXY) {
                        break;
                    }
                    if (instProxy.type() == Proxy.Type.SOCKS) {
                        break;
                    }
                    if (instProxy.type() == Proxy.Type.HTTP && instProxy.address() instanceof InetSocketAddress) {
                        final InetSocketAddress inetSocketAddress = (InetSocketAddress)instProxy.address();
                        try {
                            (this.http = new HttpURLConnection(this.url, instProxy)).setDoInput(this.getDoInput());
                            this.http.setDoOutput(this.getDoOutput());
                            if (this.connectTimeout >= 0) {
                                this.http.setConnectTimeout(this.connectTimeout);
                            }
                            if (this.readTimeout >= 0) {
                                this.http.setReadTimeout(this.readTimeout);
                            }
                            this.http.connect();
                            this.connected = true;
                            return;
                        }
                        catch (final IOException ex) {
                            proxySelector.connectFailed(uri, inetSocketAddress, ex);
                            this.http = null;
                            continue;
                        }
                        break;
                    }
                    proxySelector.connectFailed(uri, instProxy.address(), new IOException("Wrong proxy type"));
                }
            }
        }
        else {
            instProxy = this.instProxy;
            if (instProxy.type() == Proxy.Type.HTTP) {
                (this.http = new HttpURLConnection(this.url, this.instProxy)).setDoInput(this.getDoInput());
                this.http.setDoOutput(this.getDoOutput());
                if (this.connectTimeout >= 0) {
                    this.http.setConnectTimeout(this.connectTimeout);
                }
                if (this.readTimeout >= 0) {
                    this.http.setReadTimeout(this.readTimeout);
                }
                this.http.connect();
                this.connected = true;
                return;
            }
        }
        if (this.user == null) {
            this.user = "anonymous";
            this.password = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("ftp.protocol.user", "Java" + AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.version")) + "@"));
        }
        try {
            this.ftp = FtpClient.create();
            if (instProxy != null) {
                this.ftp.setProxy(instProxy);
            }
            this.setTimeouts();
            if (this.port != -1) {
                this.ftp.connect(new InetSocketAddress(this.host, this.port));
            }
            else {
                this.ftp.connect(new InetSocketAddress(this.host, FtpClient.defaultPort()));
            }
        }
        catch (final UnknownHostException ex2) {
            throw ex2;
        }
        catch (final FtpProtocolException ex3) {
            if (this.ftp != null) {
                try {
                    this.ftp.close();
                }
                catch (final IOException ex4) {
                    ex3.addSuppressed(ex4);
                }
            }
            throw new IOException(ex3);
        }
        try {
            this.ftp.login(this.user, (char[])((this.password == null) ? null : this.password.toCharArray()));
        }
        catch (final FtpProtocolException ex5) {
            this.ftp.close();
            throw new FtpLoginException("Invalid username/password");
        }
        this.connected = true;
    }
    
    private void decodePath(String s) {
        final int index = s.indexOf(";type=");
        if (index >= 0) {
            final String substring = s.substring(index + 6, s.length());
            if ("i".equalsIgnoreCase(substring)) {
                this.type = 2;
            }
            if ("a".equalsIgnoreCase(substring)) {
                this.type = 1;
            }
            if ("d".equalsIgnoreCase(substring)) {
                this.type = 3;
            }
            s = s.substring(0, index);
        }
        if (s != null && s.length() > 1 && s.charAt(0) == '/') {
            s = s.substring(1);
        }
        if (s == null || s.length() == 0) {
            s = "./";
        }
        if (!s.endsWith("/")) {
            final int lastIndex = s.lastIndexOf(47);
            if (lastIndex > 0) {
                this.filename = s.substring(lastIndex + 1, s.length());
                this.filename = ParseUtil.decode(this.filename);
                this.pathname = s.substring(0, lastIndex);
            }
            else {
                this.filename = ParseUtil.decode(s);
                this.pathname = null;
            }
        }
        else {
            this.pathname = s.substring(0, s.length() - 1);
            this.filename = null;
        }
        if (this.pathname != null) {
            this.fullpath = this.pathname + "/" + ((this.filename != null) ? this.filename : "");
        }
        else {
            this.fullpath = this.filename;
        }
    }
    
    private void cd(final String s) throws FtpProtocolException, IOException {
        if (s == null || s.isEmpty()) {
            return;
        }
        if (s.indexOf(47) == -1) {
            this.ftp.changeDirectory(ParseUtil.decode(s));
            return;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "/");
        while (stringTokenizer.hasMoreTokens()) {
            this.ftp.changeDirectory(ParseUtil.decode(stringTokenizer.nextToken()));
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (!this.connected) {
            this.connect();
        }
        if (this.http != null) {
            return this.http.getInputStream();
        }
        if (this.os != null) {
            throw new IOException("Already opened for output");
        }
        if (this.is != null) {
            return this.is;
        }
        final MessageHeader properties = new MessageHeader();
        final boolean b = false;
        try {
            this.decodePath(this.url.getPath());
            if (this.filename == null || this.type == 3) {
                this.ftp.setAsciiType();
                this.cd(this.pathname);
                if (this.filename == null) {
                    this.is = new FtpInputStream(this.ftp, this.ftp.list(null));
                }
                else {
                    this.is = new FtpInputStream(this.ftp, this.ftp.nameList(this.filename));
                }
            }
            else {
                if (this.type == 1) {
                    this.ftp.setAsciiType();
                }
                else {
                    this.ftp.setBinaryType();
                }
                this.cd(this.pathname);
                this.is = new FtpInputStream(this.ftp, this.ftp.getFileStream(this.filename));
            }
            try {
                final long lastTransferSize = this.ftp.getLastTransferSize();
                properties.add("content-length", Long.toString(lastTransferSize));
                if (lastTransferSize > 0L) {
                    final boolean shouldMeterInput = ProgressMonitor.getDefault().shouldMeterInput(this.url, "GET");
                    ProgressSource progressSource = null;
                    if (shouldMeterInput) {
                        progressSource = new ProgressSource(this.url, "GET", lastTransferSize);
                        progressSource.beginTracking();
                    }
                    this.is = new MeteredStream(this.is, progressSource, lastTransferSize);
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            if (b) {
                properties.add("content-type", "text/plain");
                properties.add("access-type", "directory");
            }
            else {
                properties.add("access-type", "file");
                String s = java.net.URLConnection.guessContentTypeFromName(this.fullpath);
                if (s == null && this.is.markSupported()) {
                    s = java.net.URLConnection.guessContentTypeFromStream(this.is);
                }
                if (s != null) {
                    properties.add("content-type", s);
                }
            }
        }
        catch (final FileNotFoundException ex2) {
            try {
                this.cd(this.fullpath);
                this.ftp.setAsciiType();
                this.is = new FtpInputStream(this.ftp, this.ftp.list(null));
                properties.add("content-type", "text/plain");
                properties.add("access-type", "directory");
            }
            catch (final IOException ex3) {
                final FileNotFoundException ex4 = new FileNotFoundException(this.fullpath);
                if (this.ftp != null) {
                    try {
                        this.ftp.close();
                    }
                    catch (final IOException ex5) {
                        ex4.addSuppressed(ex5);
                    }
                }
                throw ex4;
            }
            catch (final FtpProtocolException ex6) {
                final FileNotFoundException ex7 = new FileNotFoundException(this.fullpath);
                if (this.ftp != null) {
                    try {
                        this.ftp.close();
                    }
                    catch (final IOException ex8) {
                        ex7.addSuppressed(ex8);
                    }
                }
                throw ex7;
            }
        }
        catch (final FtpProtocolException ex9) {
            if (this.ftp != null) {
                try {
                    this.ftp.close();
                }
                catch (final IOException ex10) {
                    ex9.addSuppressed(ex10);
                }
            }
            throw new IOException(ex9);
        }
        this.setProperties(properties);
        return this.is;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!this.connected) {
            this.connect();
        }
        if (this.http != null) {
            final OutputStream outputStream = this.http.getOutputStream();
            this.http.getInputStream();
            return outputStream;
        }
        if (this.is != null) {
            throw new IOException("Already opened for input");
        }
        if (this.os != null) {
            return this.os;
        }
        this.decodePath(this.url.getPath());
        if (this.filename == null || this.filename.length() == 0) {
            throw new IOException("illegal filename for a PUT");
        }
        try {
            if (this.pathname != null) {
                this.cd(this.pathname);
            }
            if (this.type == 1) {
                this.ftp.setAsciiType();
            }
            else {
                this.ftp.setBinaryType();
            }
            this.os = new FtpOutputStream(this.ftp, this.ftp.putFileStream(this.filename, false));
        }
        catch (final FtpProtocolException ex) {
            throw new IOException(ex);
        }
        return this.os;
    }
    
    String guessContentTypeFromFilename(final String s) {
        return java.net.URLConnection.guessContentTypeFromName(s);
    }
    
    @Override
    public Permission getPermission() {
        if (this.permission == null) {
            final int port = this.url.getPort();
            this.permission = new SocketPermission(this.host + ":" + ((port < 0) ? FtpClient.defaultPort() : port), "connect");
        }
        return this.permission;
    }
    
    @Override
    public void setRequestProperty(final String s, final String s2) {
        super.setRequestProperty(s, s2);
        if ("type".equals(s)) {
            if ("i".equalsIgnoreCase(s2)) {
                this.type = 2;
            }
            else if ("a".equalsIgnoreCase(s2)) {
                this.type = 1;
            }
            else {
                if (!"d".equalsIgnoreCase(s2)) {
                    throw new IllegalArgumentException("Value of '" + s + "' request property was '" + s2 + "' when it must be either 'i', 'a' or 'd'");
                }
                this.type = 3;
            }
        }
    }
    
    @Override
    public String getRequestProperty(final String s) {
        String requestProperty = super.getRequestProperty(s);
        if (requestProperty == null && "type".equals(s)) {
            requestProperty = ((this.type == 1) ? "a" : ((this.type == 3) ? "d" : "i"));
        }
        return requestProperty;
    }
    
    @Override
    public void setConnectTimeout(final int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalArgumentException("timeouts can't be negative");
        }
        this.connectTimeout = connectTimeout;
    }
    
    @Override
    public int getConnectTimeout() {
        return (this.connectTimeout < 0) ? 0 : this.connectTimeout;
    }
    
    @Override
    public void setReadTimeout(final int readTimeout) {
        if (readTimeout < 0) {
            throw new IllegalArgumentException("timeouts can't be negative");
        }
        this.readTimeout = readTimeout;
    }
    
    @Override
    public int getReadTimeout() {
        return (this.readTimeout < 0) ? 0 : this.readTimeout;
    }
    
    protected class FtpInputStream extends FilterInputStream
    {
        FtpClient ftp;
        
        FtpInputStream(final FtpClient ftp, final InputStream inputStream) {
            super(new BufferedInputStream(inputStream));
            this.ftp = ftp;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            if (this.ftp != null) {
                this.ftp.close();
            }
        }
    }
    
    protected class FtpOutputStream extends FilterOutputStream
    {
        FtpClient ftp;
        
        FtpOutputStream(final FtpClient ftp, final OutputStream outputStream) {
            super(outputStream);
            this.ftp = ftp;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            if (this.ftp != null) {
                this.ftp.close();
            }
        }
    }
}
