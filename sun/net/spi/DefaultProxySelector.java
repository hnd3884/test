package sun.net.spi;

import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.io.IOException;
import java.security.AccessController;
import java.net.SocketAddress;
import sun.net.SocksProxy;
import java.net.InetSocketAddress;
import sun.net.NetProperties;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.net.Proxy;
import java.util.List;
import java.net.URI;
import java.net.ProxySelector;

public class DefaultProxySelector extends ProxySelector
{
    static final String[][] props;
    private static final String SOCKS_PROXY_VERSION = "socksProxyVersion";
    private static boolean hasSystemProxies;
    
    @Override
    public List<Proxy> select(final URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI can't be null.");
        }
        final String scheme = uri.getScheme();
        String host = uri.getHost();
        if (host == null) {
            String s = uri.getAuthority();
            if (s != null) {
                final int index = s.indexOf(64);
                if (index >= 0) {
                    s = s.substring(index + 1);
                }
                final int lastIndex = s.lastIndexOf(58);
                if (lastIndex >= 0) {
                    s = s.substring(0, lastIndex);
                }
                host = s;
            }
        }
        if (scheme == null || host == null) {
            throw new IllegalArgumentException("protocol = " + scheme + " host = " + host);
        }
        final ArrayList list = new ArrayList(1);
        NonProxyInfo nonProxyInfo = null;
        if ("http".equalsIgnoreCase(scheme)) {
            nonProxyInfo = NonProxyInfo.httpNonProxyInfo;
        }
        else if ("https".equalsIgnoreCase(scheme)) {
            nonProxyInfo = NonProxyInfo.httpNonProxyInfo;
        }
        else if ("ftp".equalsIgnoreCase(scheme)) {
            nonProxyInfo = NonProxyInfo.ftpNonProxyInfo;
        }
        else if ("socket".equalsIgnoreCase(scheme)) {
            nonProxyInfo = NonProxyInfo.socksNonProxyInfo;
        }
        list.add(AccessController.doPrivileged((PrivilegedAction<Proxy>)new PrivilegedAction<Proxy>() {
            final /* synthetic */ String val$urlhost = host.toLowerCase();
            
            @Override
            public Proxy run() {
                String value = null;
                int i = 0;
                while (i < DefaultProxySelector.props.length) {
                    if (DefaultProxySelector.props[i][0].equalsIgnoreCase(scheme)) {
                        int j;
                        for (j = 1; j < DefaultProxySelector.props[i].length; ++j) {
                            value = NetProperties.get(DefaultProxySelector.props[i][j] + "Host");
                            if (value != null && value.length() != 0) {
                                break;
                            }
                        }
                        if (value == null || value.length() == 0) {
                            if (DefaultProxySelector.hasSystemProxies) {
                                String val$proto;
                                if (scheme.equalsIgnoreCase("socket")) {
                                    val$proto = "socks";
                                }
                                else {
                                    val$proto = scheme;
                                }
                                final Proxy access$100 = DefaultProxySelector.this.getSystemProxy(val$proto, this.val$urlhost);
                                if (access$100 != null) {
                                    return access$100;
                                }
                            }
                            return Proxy.NO_PROXY;
                        }
                        if (nonProxyInfo != null) {
                            String hostsSource = NetProperties.get(nonProxyInfo.property);
                            synchronized (nonProxyInfo) {
                                if (hostsSource == null) {
                                    if (nonProxyInfo.defaultVal != null) {
                                        hostsSource = nonProxyInfo.defaultVal;
                                    }
                                    else {
                                        nonProxyInfo.hostsSource = null;
                                        nonProxyInfo.pattern = null;
                                    }
                                }
                                else if (hostsSource.length() != 0) {
                                    hostsSource += "|localhost|127.*|[::1]|0.0.0.0|[::0]";
                                }
                                if (hostsSource != null && !hostsSource.equals(nonProxyInfo.hostsSource)) {
                                    nonProxyInfo.pattern = DefaultProxySelector.toPattern(hostsSource);
                                    nonProxyInfo.hostsSource = hostsSource;
                                }
                                if (DefaultProxySelector.shouldNotUseProxyFor(nonProxyInfo.pattern, this.val$urlhost)) {
                                    return Proxy.NO_PROXY;
                                }
                            }
                        }
                        int n = NetProperties.getInteger(DefaultProxySelector.props[i][j] + "Port", 0);
                        if (n == 0 && j < DefaultProxySelector.props[i].length - 1) {
                            for (int k = 1; k < DefaultProxySelector.props[i].length - 1; ++k) {
                                if (k != j && n == 0) {
                                    n = NetProperties.getInteger(DefaultProxySelector.props[i][k] + "Port", 0);
                                }
                            }
                        }
                        if (n == 0) {
                            if (j == DefaultProxySelector.props[i].length - 1) {
                                n = DefaultProxySelector.this.defaultPort("socket");
                            }
                            else {
                                n = DefaultProxySelector.this.defaultPort(scheme);
                            }
                        }
                        final InetSocketAddress unresolved = InetSocketAddress.createUnresolved(value, n);
                        if (j == DefaultProxySelector.props[i].length - 1) {
                            return SocksProxy.create(unresolved, NetProperties.getInteger("socksProxyVersion", 5));
                        }
                        return new Proxy(Proxy.Type.HTTP, unresolved);
                    }
                    else {
                        ++i;
                    }
                }
                return Proxy.NO_PROXY;
            }
        }));
        return list;
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress socketAddress, final IOException ex) {
        if (uri == null || socketAddress == null || ex == null) {
            throw new IllegalArgumentException("Arguments can't be null.");
        }
    }
    
    private int defaultPort(final String s) {
        if ("http".equalsIgnoreCase(s)) {
            return 80;
        }
        if ("https".equalsIgnoreCase(s)) {
            return 443;
        }
        if ("ftp".equalsIgnoreCase(s)) {
            return 80;
        }
        if ("socket".equalsIgnoreCase(s)) {
            return 1080;
        }
        if ("gopher".equalsIgnoreCase(s)) {
            return 80;
        }
        return -1;
    }
    
    private static native boolean init();
    
    private synchronized native Proxy getSystemProxy(final String p0, final String p1);
    
    static boolean shouldNotUseProxyFor(final Pattern pattern, final String s) {
        return pattern != null && !s.isEmpty() && pattern.matcher(s).matches();
    }
    
    static Pattern toPattern(final String s) {
        boolean b = true;
        final StringJoiner stringJoiner = new StringJoiner("|");
        for (final String s2 : s.split("\\|")) {
            if (!s2.isEmpty()) {
                b = false;
                stringJoiner.add(disjunctToRegex(s2.toLowerCase()));
            }
        }
        return b ? null : Pattern.compile(stringJoiner.toString());
    }
    
    static String disjunctToRegex(final String s) {
        String s2;
        if (s.startsWith("*")) {
            s2 = ".*" + Pattern.quote(s.substring(1));
        }
        else if (s.endsWith("*")) {
            s2 = Pattern.quote(s.substring(0, s.length() - 1)) + ".*";
        }
        else {
            s2 = Pattern.quote(s);
        }
        return s2;
    }
    
    static {
        props = new String[][] { { "http", "http.proxy", "proxy", "socksProxy" }, { "https", "https.proxy", "proxy", "socksProxy" }, { "ftp", "ftp.proxy", "ftpProxy", "proxy", "socksProxy" }, { "gopher", "gopherProxy", "socksProxy" }, { "socket", "socksProxy" } };
        DefaultProxySelector.hasSystemProxies = false;
        final Boolean b = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return NetProperties.getBoolean("java.net.useSystemProxies");
            }
        });
        if (b != null && b) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    System.loadLibrary("net");
                    return null;
                }
            });
            DefaultProxySelector.hasSystemProxies = init();
        }
    }
    
    static class NonProxyInfo
    {
        static final String defStringVal = "localhost|127.*|[::1]|0.0.0.0|[::0]";
        String hostsSource;
        Pattern pattern;
        final String property;
        final String defaultVal;
        static NonProxyInfo ftpNonProxyInfo;
        static NonProxyInfo httpNonProxyInfo;
        static NonProxyInfo socksNonProxyInfo;
        
        NonProxyInfo(final String property, final String hostsSource, final Pattern pattern, final String defaultVal) {
            this.property = property;
            this.hostsSource = hostsSource;
            this.pattern = pattern;
            this.defaultVal = defaultVal;
        }
        
        static {
            NonProxyInfo.ftpNonProxyInfo = new NonProxyInfo("ftp.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
            NonProxyInfo.httpNonProxyInfo = new NonProxyInfo("http.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
            NonProxyInfo.socksNonProxyInfo = new NonProxyInfo("socksNonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
        }
    }
}
