package java.net;

import java.util.Iterator;
import sun.net.SocksProxy;
import sun.net.www.ParseUtil;
import java.io.UnsupportedEncodingException;
import sun.security.action.GetPropertyAction;
import java.security.PrivilegedAction;
import java.io.BufferedOutputStream;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.io.OutputStream;
import java.io.InputStream;

class SocksSocketImpl extends PlainSocketImpl implements SocksConsts
{
    private String server;
    private int serverPort;
    private InetSocketAddress external_address;
    private boolean useV4;
    private Socket cmdsock;
    private InputStream cmdIn;
    private OutputStream cmdOut;
    private boolean applicationSetProxy;
    
    SocksSocketImpl() {
        this.server = null;
        this.serverPort = 1080;
        this.useV4 = false;
        this.cmdsock = null;
        this.cmdIn = null;
        this.cmdOut = null;
    }
    
    SocksSocketImpl(final String server, final int n) {
        this.server = null;
        this.serverPort = 1080;
        this.useV4 = false;
        this.cmdsock = null;
        this.cmdIn = null;
        this.cmdOut = null;
        this.server = server;
        this.serverPort = ((n == -1) ? 1080 : n);
    }
    
    SocksSocketImpl(final Proxy proxy) {
        this.server = null;
        this.serverPort = 1080;
        this.useV4 = false;
        this.cmdsock = null;
        this.cmdIn = null;
        this.cmdOut = null;
        final SocketAddress address = proxy.address();
        if (address instanceof InetSocketAddress) {
            final InetSocketAddress inetSocketAddress = (InetSocketAddress)address;
            this.server = inetSocketAddress.getHostString();
            this.serverPort = inetSocketAddress.getPort();
        }
    }
    
    void setV4() {
        this.useV4 = true;
    }
    
    private synchronized void privilegedConnect(final String s, final int n, final int n2) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    SocksSocketImpl.this.superConnectServer(s, n, n2);
                    SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.getInputStream();
                    SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.getOutputStream();
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private void superConnectServer(final String s, final int n, final int n2) throws IOException {
        super.connect(new InetSocketAddress(s, n), n2);
    }
    
    private static int remainingMillis(final long n) throws IOException {
        if (n == 0L) {
            return 0;
        }
        final long n2 = n - System.currentTimeMillis();
        if (n2 > 0L) {
            return (int)n2;
        }
        throw new SocketTimeoutException();
    }
    
    private int readSocksReply(final InputStream inputStream, final byte[] array) throws IOException {
        return this.readSocksReply(inputStream, array, 0L);
    }
    
    private int readSocksReply(final InputStream inputStream, final byte[] array, final long n) throws IOException {
        int length;
        int i;
        int read;
        for (length = array.length, i = 0; i < length; i += read) {
            try {
                read = ((SocketInputStream)inputStream).read(array, i, length - i, remainingMillis(n));
            }
            catch (final SocketTimeoutException ex) {
                throw new SocketTimeoutException("Connect timed out");
            }
            if (read < 0) {
                throw new SocketException("Malformed reply from SOCKS server");
            }
        }
        return i;
    }
    
    private boolean authenticate(final byte b, final InputStream inputStream, final BufferedOutputStream bufferedOutputStream) throws IOException {
        return this.authenticate(b, inputStream, bufferedOutputStream, 0L);
    }
    
    private boolean authenticate(final byte b, final InputStream inputStream, final BufferedOutputStream bufferedOutputStream, final long n) throws IOException {
        if (b == 0) {
            return true;
        }
        if (b != 2) {
            return false;
        }
        String s = null;
        final PasswordAuthentication passwordAuthentication = AccessController.doPrivileged((PrivilegedAction<PasswordAuthentication>)new PrivilegedAction<PasswordAuthentication>() {
            final /* synthetic */ InetAddress val$addr = InetAddress.getByName(SocksSocketImpl.this.server);
            
            @Override
            public PasswordAuthentication run() {
                return Authenticator.requestPasswordAuthentication(SocksSocketImpl.this.server, this.val$addr, SocksSocketImpl.this.serverPort, "SOCKS5", "SOCKS authentication", null);
            }
        });
        String userName;
        if (passwordAuthentication != null) {
            userName = passwordAuthentication.getUserName();
            s = new String(passwordAuthentication.getPassword());
        }
        else {
            userName = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.name"));
        }
        if (userName == null) {
            return false;
        }
        bufferedOutputStream.write(1);
        bufferedOutputStream.write(userName.length());
        try {
            bufferedOutputStream.write(userName.getBytes("ISO-8859-1"));
        }
        catch (final UnsupportedEncodingException ex) {
            assert false;
        }
        if (s != null) {
            bufferedOutputStream.write(s.length());
            try {
                bufferedOutputStream.write(s.getBytes("ISO-8859-1"));
            }
            catch (final UnsupportedEncodingException ex2) {
                assert false;
            }
        }
        else {
            bufferedOutputStream.write(0);
        }
        bufferedOutputStream.flush();
        final byte[] array = new byte[2];
        if (this.readSocksReply(inputStream, array, n) != 2 || array[1] != 0) {
            bufferedOutputStream.close();
            inputStream.close();
            return false;
        }
        return true;
    }
    
    private void connectV4(final InputStream inputStream, final OutputStream outputStream, final InetSocketAddress external_address, final long n) throws IOException {
        if (!(external_address.getAddress() instanceof Inet4Address)) {
            throw new SocketException("SOCKS V4 requires IPv4 only addresses");
        }
        outputStream.write(4);
        outputStream.write(1);
        outputStream.write(external_address.getPort() >> 8 & 0xFF);
        outputStream.write(external_address.getPort() >> 0 & 0xFF);
        outputStream.write(external_address.getAddress().getAddress());
        final String userName = this.getUserName();
        try {
            outputStream.write(userName.getBytes("ISO-8859-1"));
        }
        catch (final UnsupportedEncodingException ex) {
            assert false;
        }
        outputStream.write(0);
        outputStream.flush();
        final byte[] array = new byte[8];
        final int socksReply = this.readSocksReply(inputStream, array, n);
        if (socksReply != 8) {
            throw new SocketException("Reply from SOCKS server has bad length: " + socksReply);
        }
        if (array[0] != 0 && array[0] != 4) {
            throw new SocketException("Reply from SOCKS server has bad version");
        }
        Object o = null;
        switch (array[1]) {
            case 90: {
                this.external_address = external_address;
                break;
            }
            case 91: {
                o = new SocketException("SOCKS request rejected");
                break;
            }
            case 92: {
                o = new SocketException("SOCKS server couldn't reach destination");
                break;
            }
            case 93: {
                o = new SocketException("SOCKS authentication failed");
                break;
            }
            default: {
                o = new SocketException("Reply from SOCKS server contains bad status");
                break;
            }
        }
        if (o != null) {
            inputStream.close();
            outputStream.close();
            throw o;
        }
    }
    
    @Override
    protected void connect(final SocketAddress socketAddress, final int n) throws IOException {
        long n2;
        if (n == 0) {
            n2 = 0L;
        }
        else {
            final long n3 = System.currentTimeMillis() + n;
            n2 = ((n3 < 0L) ? Long.MAX_VALUE : n3);
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (socketAddress == null || !(socketAddress instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Unsupported address type");
        }
        final InetSocketAddress external_address = (InetSocketAddress)socketAddress;
        if (securityManager != null) {
            if (external_address.isUnresolved()) {
                securityManager.checkConnect(external_address.getHostName(), external_address.getPort());
            }
            else {
                securityManager.checkConnect(external_address.getAddress().getHostAddress(), external_address.getPort());
            }
        }
        if (this.server == null) {
            final ProxySelector proxySelector = AccessController.doPrivileged((PrivilegedAction<ProxySelector>)new PrivilegedAction<ProxySelector>() {
                @Override
                public ProxySelector run() {
                    return ProxySelector.getDefault();
                }
            });
            if (proxySelector == null) {
                super.connect(external_address, remainingMillis(n2));
                return;
            }
            String s = external_address.getHostString();
            if (external_address.getAddress() instanceof Inet6Address && !s.startsWith("[") && s.indexOf(":") >= 0) {
                s = "[" + s + "]";
            }
            URI uri;
            try {
                uri = new URI("socket://" + ParseUtil.encodePath(s) + ":" + external_address.getPort());
            }
            catch (final URISyntaxException ex) {
                assert false : ex;
                uri = null;
            }
            Throwable t = null;
            final Iterator<Proxy> iterator = proxySelector.select(uri).iterator();
            if (iterator == null || !iterator.hasNext()) {
                super.connect(external_address, remainingMillis(n2));
                return;
            }
            while (iterator.hasNext()) {
                final Proxy proxy = iterator.next();
                if (proxy == null || proxy.type() != Proxy.Type.SOCKS) {
                    super.connect(external_address, remainingMillis(n2));
                    return;
                }
                if (!(proxy.address() instanceof InetSocketAddress)) {
                    throw new SocketException("Unknown address type for proxy: " + proxy);
                }
                this.server = ((InetSocketAddress)proxy.address()).getHostString();
                this.serverPort = ((InetSocketAddress)proxy.address()).getPort();
                if (proxy instanceof SocksProxy && ((SocksProxy)proxy).protocolVersion() == 4) {
                    this.useV4 = true;
                }
                try {
                    this.privilegedConnect(this.server, this.serverPort, remainingMillis(n2));
                }
                catch (final IOException ex2) {
                    proxySelector.connectFailed(uri, proxy.address(), ex2);
                    this.server = null;
                    this.serverPort = -1;
                    t = ex2;
                    continue;
                }
                break;
            }
            if (this.server == null) {
                throw new SocketException("Can't connect to SOCKS proxy:" + t.getMessage());
            }
        }
        else {
            try {
                this.privilegedConnect(this.server, this.serverPort, remainingMillis(n2));
            }
            catch (final IOException ex3) {
                throw new SocketException(ex3.getMessage());
            }
        }
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(this.cmdOut, 512);
        final InputStream cmdIn = this.cmdIn;
        if (this.useV4) {
            if (external_address.isUnresolved()) {
                throw new UnknownHostException(external_address.toString());
            }
            this.connectV4(cmdIn, bufferedOutputStream, external_address, n2);
        }
        else {
            bufferedOutputStream.write(5);
            bufferedOutputStream.write(2);
            bufferedOutputStream.write(0);
            bufferedOutputStream.write(2);
            bufferedOutputStream.flush();
            final byte[] array = new byte[2];
            if (this.readSocksReply(cmdIn, array, n2) != 2 || array[0] != 5) {
                if (external_address.isUnresolved()) {
                    throw new UnknownHostException(external_address.toString());
                }
                this.connectV4(cmdIn, bufferedOutputStream, external_address, n2);
            }
            else {
                if (array[1] == -1) {
                    throw new SocketException("SOCKS : No acceptable methods");
                }
                if (!this.authenticate(array[1], cmdIn, bufferedOutputStream, n2)) {
                    throw new SocketException("SOCKS : authentication failed");
                }
                bufferedOutputStream.write(5);
                bufferedOutputStream.write(1);
                bufferedOutputStream.write(0);
                if (external_address.isUnresolved()) {
                    bufferedOutputStream.write(3);
                    bufferedOutputStream.write(external_address.getHostName().length());
                    try {
                        bufferedOutputStream.write(external_address.getHostName().getBytes("ISO-8859-1"));
                    }
                    catch (final UnsupportedEncodingException ex4) {
                        assert false;
                    }
                    bufferedOutputStream.write(external_address.getPort() >> 8 & 0xFF);
                    bufferedOutputStream.write(external_address.getPort() >> 0 & 0xFF);
                }
                else if (external_address.getAddress() instanceof Inet6Address) {
                    bufferedOutputStream.write(4);
                    bufferedOutputStream.write(external_address.getAddress().getAddress());
                    bufferedOutputStream.write(external_address.getPort() >> 8 & 0xFF);
                    bufferedOutputStream.write(external_address.getPort() >> 0 & 0xFF);
                }
                else {
                    bufferedOutputStream.write(1);
                    bufferedOutputStream.write(external_address.getAddress().getAddress());
                    bufferedOutputStream.write(external_address.getPort() >> 8 & 0xFF);
                    bufferedOutputStream.write(external_address.getPort() >> 0 & 0xFF);
                }
                bufferedOutputStream.flush();
                final byte[] array2 = new byte[4];
                if (this.readSocksReply(cmdIn, array2, n2) != 4) {
                    throw new SocketException("Reply from SOCKS server has bad length");
                }
                Object o = null;
                Label_1533: {
                    switch (array2[1]) {
                        case 0: {
                            switch (array2[3]) {
                                case 1: {
                                    if (this.readSocksReply(cmdIn, new byte[4], n2) != 4) {
                                        throw new SocketException("Reply from SOCKS server badly formatted");
                                    }
                                    if (this.readSocksReply(cmdIn, new byte[2], n2) != 2) {
                                        throw new SocketException("Reply from SOCKS server badly formatted");
                                    }
                                    break Label_1533;
                                }
                                case 3: {
                                    final byte[] array3 = { 0 };
                                    if (this.readSocksReply(cmdIn, array3, n2) != 1) {
                                        throw new SocketException("Reply from SOCKS server badly formatted");
                                    }
                                    final int n4 = array3[0] & 0xFF;
                                    if (this.readSocksReply(cmdIn, new byte[n4], n2) != n4) {
                                        throw new SocketException("Reply from SOCKS server badly formatted");
                                    }
                                    if (this.readSocksReply(cmdIn, new byte[2], n2) != 2) {
                                        throw new SocketException("Reply from SOCKS server badly formatted");
                                    }
                                    break Label_1533;
                                }
                                case 4: {
                                    final int n5 = 16;
                                    if (this.readSocksReply(cmdIn, new byte[n5], n2) != n5) {
                                        throw new SocketException("Reply from SOCKS server badly formatted");
                                    }
                                    if (this.readSocksReply(cmdIn, new byte[2], n2) != 2) {
                                        throw new SocketException("Reply from SOCKS server badly formatted");
                                    }
                                    break Label_1533;
                                }
                                default: {
                                    o = new SocketException("Reply from SOCKS server contains wrong code");
                                    break Label_1533;
                                }
                            }
                            break;
                        }
                        case 1: {
                            o = new SocketException("SOCKS server general failure");
                            break;
                        }
                        case 2: {
                            o = new SocketException("SOCKS: Connection not allowed by ruleset");
                            break;
                        }
                        case 3: {
                            o = new SocketException("SOCKS: Network unreachable");
                            break;
                        }
                        case 4: {
                            o = new SocketException("SOCKS: Host unreachable");
                            break;
                        }
                        case 5: {
                            o = new SocketException("SOCKS: Connection refused");
                            break;
                        }
                        case 6: {
                            o = new SocketException("SOCKS: TTL expired");
                            break;
                        }
                        case 7: {
                            o = new SocketException("SOCKS: Command not supported");
                            break;
                        }
                        case 8: {
                            o = new SocketException("SOCKS: address type not supported");
                            break;
                        }
                    }
                }
                if (o != null) {
                    cmdIn.close();
                    bufferedOutputStream.close();
                    throw o;
                }
                this.external_address = external_address;
            }
        }
    }
    
    private void bindV4(final InputStream inputStream, final OutputStream outputStream, final InetAddress inetAddress, final int n) throws IOException {
        if (!(inetAddress instanceof Inet4Address)) {
            throw new SocketException("SOCKS V4 requires IPv4 only addresses");
        }
        super.bind(inetAddress, n);
        byte[] array = inetAddress.getAddress();
        if (inetAddress.isAnyLocalAddress()) {
            array = AccessController.doPrivileged((PrivilegedAction<InetAddress>)new PrivilegedAction<InetAddress>() {
                @Override
                public InetAddress run() {
                    return SocksSocketImpl.this.cmdsock.getLocalAddress();
                }
            }).getAddress();
        }
        outputStream.write(4);
        outputStream.write(2);
        outputStream.write(super.getLocalPort() >> 8 & 0xFF);
        outputStream.write(super.getLocalPort() >> 0 & 0xFF);
        outputStream.write(array);
        final String userName = this.getUserName();
        try {
            outputStream.write(userName.getBytes("ISO-8859-1"));
        }
        catch (final UnsupportedEncodingException ex) {
            assert false;
        }
        outputStream.write(0);
        outputStream.flush();
        final byte[] array2 = new byte[8];
        final int socksReply = this.readSocksReply(inputStream, array2);
        if (socksReply != 8) {
            throw new SocketException("Reply from SOCKS server has bad length: " + socksReply);
        }
        if (array2[0] != 0 && array2[0] != 4) {
            throw new SocketException("Reply from SOCKS server has bad version");
        }
        Object o = null;
        switch (array2[1]) {
            case 90: {
                this.external_address = new InetSocketAddress(inetAddress, n);
                break;
            }
            case 91: {
                o = new SocketException("SOCKS request rejected");
                break;
            }
            case 92: {
                o = new SocketException("SOCKS server couldn't reach destination");
                break;
            }
            case 93: {
                o = new SocketException("SOCKS authentication failed");
                break;
            }
            default: {
                o = new SocketException("Reply from SOCKS server contains bad status");
                break;
            }
        }
        if (o != null) {
            inputStream.close();
            outputStream.close();
            throw o;
        }
    }
    
    protected synchronized void socksBind(final InetSocketAddress inetSocketAddress) throws IOException {
        if (this.socket != null) {
            return;
        }
        if (this.server == null) {
            final ProxySelector proxySelector = AccessController.doPrivileged((PrivilegedAction<ProxySelector>)new PrivilegedAction<ProxySelector>() {
                @Override
                public ProxySelector run() {
                    return ProxySelector.getDefault();
                }
            });
            if (proxySelector == null) {
                return;
            }
            String s = inetSocketAddress.getHostString();
            if (inetSocketAddress.getAddress() instanceof Inet6Address && !s.startsWith("[") && s.indexOf(":") >= 0) {
                s = "[" + s + "]";
            }
            URI uri;
            try {
                uri = new URI("serversocket://" + ParseUtil.encodePath(s) + ":" + inetSocketAddress.getPort());
            }
            catch (final URISyntaxException ex) {
                assert false : ex;
                uri = null;
            }
            Throwable t = null;
            final Iterator<Proxy> iterator = proxySelector.select(uri).iterator();
            if (iterator == null || !iterator.hasNext()) {
                return;
            }
            while (iterator.hasNext()) {
                final Proxy proxy = iterator.next();
                if (proxy == null || proxy.type() != Proxy.Type.SOCKS) {
                    return;
                }
                if (!(proxy.address() instanceof InetSocketAddress)) {
                    throw new SocketException("Unknown address type for proxy: " + proxy);
                }
                this.server = ((InetSocketAddress)proxy.address()).getHostString();
                this.serverPort = ((InetSocketAddress)proxy.address()).getPort();
                if (proxy instanceof SocksProxy && ((SocksProxy)proxy).protocolVersion() == 4) {
                    this.useV4 = true;
                }
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() throws Exception {
                            SocksSocketImpl.this.cmdsock = new Socket(new PlainSocketImpl());
                            SocksSocketImpl.this.cmdsock.connect(new InetSocketAddress(SocksSocketImpl.this.server, SocksSocketImpl.this.serverPort));
                            SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.cmdsock.getInputStream();
                            SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.cmdsock.getOutputStream();
                            return null;
                        }
                    });
                }
                catch (final Exception ex2) {
                    proxySelector.connectFailed(uri, proxy.address(), new SocketException(ex2.getMessage()));
                    this.server = null;
                    this.serverPort = -1;
                    this.cmdsock = null;
                    t = ex2;
                }
            }
            if (this.server == null || this.cmdsock == null) {
                throw new SocketException("Can't connect to SOCKS proxy:" + t.getMessage());
            }
        }
        else {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        SocksSocketImpl.this.cmdsock = new Socket(new PlainSocketImpl());
                        SocksSocketImpl.this.cmdsock.connect(new InetSocketAddress(SocksSocketImpl.this.server, SocksSocketImpl.this.serverPort));
                        SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.cmdsock.getInputStream();
                        SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.cmdsock.getOutputStream();
                        return null;
                    }
                });
            }
            catch (final Exception ex3) {
                throw new SocketException(ex3.getMessage());
            }
        }
        final BufferedOutputStream cmdOut = new BufferedOutputStream(this.cmdOut, 512);
        final InputStream cmdIn = this.cmdIn;
        if (this.useV4) {
            this.bindV4(cmdIn, cmdOut, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            return;
        }
        cmdOut.write(5);
        cmdOut.write(2);
        cmdOut.write(0);
        cmdOut.write(2);
        cmdOut.flush();
        final byte[] array = new byte[2];
        if (this.readSocksReply(cmdIn, array) != 2 || array[0] != 5) {
            this.bindV4(cmdIn, cmdOut, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            return;
        }
        if (array[1] == -1) {
            throw new SocketException("SOCKS : No acceptable methods");
        }
        if (!this.authenticate(array[1], cmdIn, cmdOut)) {
            throw new SocketException("SOCKS : authentication failed");
        }
        cmdOut.write(5);
        cmdOut.write(2);
        cmdOut.write(0);
        final int port = inetSocketAddress.getPort();
        if (inetSocketAddress.isUnresolved()) {
            cmdOut.write(3);
            cmdOut.write(inetSocketAddress.getHostName().length());
            try {
                cmdOut.write(inetSocketAddress.getHostName().getBytes("ISO-8859-1"));
            }
            catch (final UnsupportedEncodingException ex4) {
                assert false;
            }
            cmdOut.write(port >> 8 & 0xFF);
            cmdOut.write(port >> 0 & 0xFF);
        }
        else if (inetSocketAddress.getAddress() instanceof Inet4Address) {
            final byte[] address = inetSocketAddress.getAddress().getAddress();
            cmdOut.write(1);
            cmdOut.write(address);
            cmdOut.write(port >> 8 & 0xFF);
            cmdOut.write(port >> 0 & 0xFF);
            cmdOut.flush();
        }
        else {
            if (!(inetSocketAddress.getAddress() instanceof Inet6Address)) {
                this.cmdsock.close();
                throw new SocketException("unsupported address type : " + inetSocketAddress);
            }
            final byte[] address2 = inetSocketAddress.getAddress().getAddress();
            cmdOut.write(4);
            cmdOut.write(address2);
            cmdOut.write(port >> 8 & 0xFF);
            cmdOut.write(port >> 0 & 0xFF);
            cmdOut.flush();
        }
        final byte[] array2 = new byte[4];
        this.readSocksReply(cmdIn, array2);
        Object o = null;
        switch (array2[1]) {
            case 0: {
                switch (array2[3]) {
                    case 1: {
                        final byte[] array3 = new byte[4];
                        if (this.readSocksReply(cmdIn, array3) != 4) {
                            throw new SocketException("Reply from SOCKS server badly formatted");
                        }
                        final byte[] array4 = new byte[2];
                        if (this.readSocksReply(cmdIn, array4) != 2) {
                            throw new SocketException("Reply from SOCKS server badly formatted");
                        }
                        this.external_address = new InetSocketAddress(new Inet4Address("", array3), ((array4[0] & 0xFF) << 8) + (array4[1] & 0xFF));
                        break;
                    }
                    case 3: {
                        final byte b = array2[1];
                        final byte[] array5 = new byte[b];
                        if (this.readSocksReply(cmdIn, array5) != b) {
                            throw new SocketException("Reply from SOCKS server badly formatted");
                        }
                        final byte[] array6 = new byte[2];
                        if (this.readSocksReply(cmdIn, array6) != 2) {
                            throw new SocketException("Reply from SOCKS server badly formatted");
                        }
                        this.external_address = new InetSocketAddress(new String(array5), ((array6[0] & 0xFF) << 8) + (array6[1] & 0xFF));
                        break;
                    }
                    case 4: {
                        final byte b2 = array2[1];
                        final byte[] array7 = new byte[b2];
                        if (this.readSocksReply(cmdIn, array7) != b2) {
                            throw new SocketException("Reply from SOCKS server badly formatted");
                        }
                        final byte[] array8 = new byte[2];
                        if (this.readSocksReply(cmdIn, array8) != 2) {
                            throw new SocketException("Reply from SOCKS server badly formatted");
                        }
                        this.external_address = new InetSocketAddress(new Inet6Address("", array7), ((array8[0] & 0xFF) << 8) + (array8[1] & 0xFF));
                        break;
                    }
                }
                break;
            }
            case 1: {
                o = new SocketException("SOCKS server general failure");
                break;
            }
            case 2: {
                o = new SocketException("SOCKS: Bind not allowed by ruleset");
                break;
            }
            case 3: {
                o = new SocketException("SOCKS: Network unreachable");
                break;
            }
            case 4: {
                o = new SocketException("SOCKS: Host unreachable");
                break;
            }
            case 5: {
                o = new SocketException("SOCKS: Connection refused");
                break;
            }
            case 6: {
                o = new SocketException("SOCKS: TTL expired");
                break;
            }
            case 7: {
                o = new SocketException("SOCKS: Command not supported");
                break;
            }
            case 8: {
                o = new SocketException("SOCKS: address type not supported");
                break;
            }
        }
        if (o != null) {
            cmdIn.close();
            cmdOut.close();
            this.cmdsock.close();
            this.cmdsock = null;
            throw o;
        }
        this.cmdIn = cmdIn;
        this.cmdOut = cmdOut;
    }
    
    protected void acceptFrom(final SocketImpl socketImpl, final InetSocketAddress inetSocketAddress) throws IOException {
        if (this.cmdsock == null) {
            return;
        }
        final InputStream cmdIn = this.cmdIn;
        this.socksBind(inetSocketAddress);
        cmdIn.read();
        final int read = cmdIn.read();
        cmdIn.read();
        Object o = null;
        InetSocketAddress external_address = null;
        switch (read) {
            case 0: {
                switch (cmdIn.read()) {
                    case 1: {
                        final byte[] array = new byte[4];
                        this.readSocksReply(cmdIn, array);
                        external_address = new InetSocketAddress(new Inet4Address("", array), (cmdIn.read() << 8) + cmdIn.read());
                        break;
                    }
                    case 3: {
                        final byte[] array2 = new byte[cmdIn.read()];
                        this.readSocksReply(cmdIn, array2);
                        external_address = new InetSocketAddress(new String(array2), (cmdIn.read() << 8) + cmdIn.read());
                        break;
                    }
                    case 4: {
                        final byte[] array3 = new byte[16];
                        this.readSocksReply(cmdIn, array3);
                        external_address = new InetSocketAddress(new Inet6Address("", array3), (cmdIn.read() << 8) + cmdIn.read());
                        break;
                    }
                }
                break;
            }
            case 1: {
                o = new SocketException("SOCKS server general failure");
                break;
            }
            case 2: {
                o = new SocketException("SOCKS: Accept not allowed by ruleset");
                break;
            }
            case 3: {
                o = new SocketException("SOCKS: Network unreachable");
                break;
            }
            case 4: {
                o = new SocketException("SOCKS: Host unreachable");
                break;
            }
            case 5: {
                o = new SocketException("SOCKS: Connection refused");
                break;
            }
            case 6: {
                o = new SocketException("SOCKS: TTL expired");
                break;
            }
            case 7: {
                o = new SocketException("SOCKS: Command not supported");
                break;
            }
            case 8: {
                o = new SocketException("SOCKS: address type not supported");
                break;
            }
        }
        if (o != null) {
            this.cmdIn.close();
            this.cmdOut.close();
            this.cmdsock.close();
            this.cmdsock = null;
            throw o;
        }
        if (socketImpl instanceof SocksSocketImpl) {
            ((SocksSocketImpl)socketImpl).external_address = external_address;
        }
        if (socketImpl instanceof PlainSocketImpl) {
            final PlainSocketImpl plainSocketImpl = (PlainSocketImpl)socketImpl;
            plainSocketImpl.setInputStream((SocketInputStream)cmdIn);
            plainSocketImpl.setFileDescriptor(this.cmdsock.getImpl().getFileDescriptor());
            plainSocketImpl.setAddress(this.cmdsock.getImpl().getInetAddress());
            plainSocketImpl.setPort(this.cmdsock.getImpl().getPort());
            plainSocketImpl.setLocalPort(this.cmdsock.getImpl().getLocalPort());
        }
        else {
            socketImpl.fd = this.cmdsock.getImpl().fd;
            socketImpl.address = this.cmdsock.getImpl().address;
            socketImpl.port = this.cmdsock.getImpl().port;
            socketImpl.localport = this.cmdsock.getImpl().localport;
        }
        this.cmdsock = null;
    }
    
    @Override
    protected InetAddress getInetAddress() {
        if (this.external_address != null) {
            return this.external_address.getAddress();
        }
        return super.getInetAddress();
    }
    
    @Override
    protected int getPort() {
        if (this.external_address != null) {
            return this.external_address.getPort();
        }
        return super.getPort();
    }
    
    @Override
    protected int getLocalPort() {
        if (this.socket != null) {
            return super.getLocalPort();
        }
        if (this.external_address != null) {
            return this.external_address.getPort();
        }
        return super.getLocalPort();
    }
    
    @Override
    protected void close() throws IOException {
        if (this.cmdsock != null) {
            this.cmdsock.close();
        }
        this.cmdsock = null;
        super.close();
    }
    
    private String getUserName() {
        String property = "";
        if (this.applicationSetProxy) {
            try {
                property = System.getProperty("user.name");
            }
            catch (final SecurityException ex) {}
        }
        else {
            property = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.name"));
        }
        return property;
    }
}
