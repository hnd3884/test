package java.rmi;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.registry.LocateRegistry;
import java.net.MalformedURLException;
import java.rmi.registry.Registry;

public final class Naming
{
    private Naming() {
    }
    
    public static Remote lookup(final String s) throws NotBoundException, MalformedURLException, RemoteException {
        final ParsedNamingURL url = parseURL(s);
        final Registry registry = getRegistry(url);
        if (url.name == null) {
            return registry;
        }
        return registry.lookup(url.name);
    }
    
    public static void bind(final String s, final Remote remote) throws AlreadyBoundException, MalformedURLException, RemoteException {
        final ParsedNamingURL url = parseURL(s);
        final Registry registry = getRegistry(url);
        if (remote == null) {
            throw new NullPointerException("cannot bind to null");
        }
        registry.bind(url.name, remote);
    }
    
    public static void unbind(final String s) throws RemoteException, NotBoundException, MalformedURLException {
        final ParsedNamingURL url = parseURL(s);
        getRegistry(url).unbind(url.name);
    }
    
    public static void rebind(final String s, final Remote remote) throws RemoteException, MalformedURLException {
        final ParsedNamingURL url = parseURL(s);
        final Registry registry = getRegistry(url);
        if (remote == null) {
            throw new NullPointerException("cannot bind to null");
        }
        registry.rebind(url.name, remote);
    }
    
    public static String[] list(final String s) throws RemoteException, MalformedURLException {
        final ParsedNamingURL url = parseURL(s);
        final Registry registry = getRegistry(url);
        String s2 = "";
        if (url.port > 0 || !url.host.equals("")) {
            s2 = s2 + "//" + url.host;
        }
        if (url.port > 0) {
            s2 = s2 + ":" + url.port;
        }
        final String string = s2 + "/";
        final String[] list = registry.list();
        for (int i = 0; i < list.length; ++i) {
            list[i] = string + list[i];
        }
        return list;
    }
    
    private static Registry getRegistry(final ParsedNamingURL parsedNamingURL) throws RemoteException {
        return LocateRegistry.getRegistry(parsedNamingURL.host, parsedNamingURL.port);
    }
    
    private static ParsedNamingURL parseURL(final String s) throws MalformedURLException {
        try {
            return intParseURL(s);
        }
        catch (final URISyntaxException ex) {
            final MalformedURLException ex2 = new MalformedURLException("invalid URL String: " + s);
            ex2.initCause(ex);
            final int index = s.indexOf(58);
            final int index2 = s.indexOf("//:");
            if (index2 < 0) {
                throw ex2;
            }
            if (index2 == 0 || (index > 0 && index2 == index + 1)) {
                final int n = index2 + 2;
                final String string = s.substring(0, n) + "localhost" + s.substring(n);
                try {
                    return intParseURL(string);
                }
                catch (final URISyntaxException ex3) {
                    throw ex2;
                }
                catch (final MalformedURLException ex4) {
                    throw ex4;
                }
            }
            throw ex2;
        }
    }
    
    private static ParsedNamingURL intParseURL(final String s) throws MalformedURLException, URISyntaxException {
        URI uri = new URI(s);
        if (uri.isOpaque()) {
            throw new MalformedURLException("not a hierarchical URL: " + s);
        }
        if (uri.getFragment() != null) {
            throw new MalformedURLException("invalid character, '#', in URL name: " + s);
        }
        if (uri.getQuery() != null) {
            throw new MalformedURLException("invalid character, '?', in URL name: " + s);
        }
        if (uri.getUserInfo() != null) {
            throw new MalformedURLException("invalid character, '@', in URL host: " + s);
        }
        final String scheme = uri.getScheme();
        if (scheme != null && !scheme.equals("rmi")) {
            throw new MalformedURLException("invalid URL scheme: " + s);
        }
        String s2 = uri.getPath();
        if (s2 != null) {
            if (s2.startsWith("/")) {
                s2 = s2.substring(1);
            }
            if (s2.length() == 0) {
                s2 = null;
            }
        }
        String host = uri.getHost();
        Label_0369: {
            if (host == null) {
                host = "";
                try {
                    uri.parseServerAuthority();
                }
                catch (final URISyntaxException ex) {
                    final String authority = uri.getAuthority();
                    if (authority != null && authority.startsWith(":")) {
                        final String string = "localhost" + authority;
                        try {
                            uri = new URI(null, string, null, null, null);
                            uri.parseServerAuthority();
                            break Label_0369;
                        }
                        catch (final URISyntaxException ex2) {
                            throw new MalformedURLException("invalid authority: " + s);
                        }
                    }
                    throw new MalformedURLException("invalid authority: " + s);
                }
            }
        }
        int port = uri.getPort();
        if (port == -1) {
            port = 1099;
        }
        return new ParsedNamingURL(host, port, s2);
    }
    
    private static class ParsedNamingURL
    {
        String host;
        int port;
        String name;
        
        ParsedNamingURL(final String host, final int port, final String name) {
            this.host = host;
            this.port = port;
            this.name = name;
        }
    }
}
