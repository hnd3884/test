package jcifs.http;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.Map;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    public static final int DEFAULT_HTTP_PORT = 80;
    private static final Map PROTOCOL_HANDLERS;
    private static final String HANDLER_PKGS_PROPERTY = "java.protocol.handler.pkgs";
    private static final String[] JVM_VENDOR_DEFAULT_PKGS;
    private static URLStreamHandlerFactory factory;
    
    public static void setURLStreamHandlerFactory(final URLStreamHandlerFactory factory) {
        synchronized (Handler.PROTOCOL_HANDLERS) {
            if (Handler.factory != null) {
                throw new IllegalStateException("URLStreamHandlerFactory already set.");
            }
            Handler.PROTOCOL_HANDLERS.clear();
            Handler.factory = factory;
        }
    }
    
    protected int getDefaultPort() {
        return 80;
    }
    
    protected URLConnection openConnection(URL url) throws IOException {
        url = new URL(url, url.toExternalForm(), getDefaultStreamHandler(url.getProtocol()));
        return new NtlmHttpURLConnection((HttpURLConnection)url.openConnection());
    }
    
    private static URLStreamHandler getDefaultStreamHandler(final String protocol) throws IOException {
        synchronized (Handler.PROTOCOL_HANDLERS) {
            URLStreamHandler handler = Handler.PROTOCOL_HANDLERS.get(protocol);
            if (handler != null) {
                return handler;
            }
            if (Handler.factory != null) {
                handler = Handler.factory.createURLStreamHandler(protocol);
            }
            if (handler == null) {
                final String path = System.getProperty("java.protocol.handler.pkgs");
                final StringTokenizer tokenizer = new StringTokenizer(path, "|");
                while (tokenizer.hasMoreTokens()) {
                    final String provider = tokenizer.nextToken().trim();
                    if (provider.equals("jcifs")) {
                        continue;
                    }
                    final String className = provider + "." + protocol + ".Handler";
                    try {
                        Class handlerClass = null;
                        try {
                            handlerClass = Class.forName(className);
                        }
                        catch (final Exception ex2) {}
                        if (handlerClass == null) {
                            handlerClass = ClassLoader.getSystemClassLoader().loadClass(className);
                        }
                        handler = handlerClass.newInstance();
                    }
                    catch (final Exception ex) {
                        continue;
                    }
                    break;
                }
            }
            if (handler == null) {
                for (int i = 0; i < Handler.JVM_VENDOR_DEFAULT_PKGS.length; ++i) {
                    final String className2 = Handler.JVM_VENDOR_DEFAULT_PKGS[i] + "." + protocol + ".Handler";
                    try {
                        Class handlerClass2 = null;
                        try {
                            handlerClass2 = Class.forName(className2);
                        }
                        catch (final Exception ex3) {}
                        if (handlerClass2 == null) {
                            handlerClass2 = ClassLoader.getSystemClassLoader().loadClass(className2);
                        }
                        handler = handlerClass2.newInstance();
                    }
                    catch (final Exception ex4) {}
                    if (handler != null) {
                        break;
                    }
                }
            }
            if (handler == null) {
                throw new IOException("Unable to find default handler for protocol: " + protocol);
            }
            Handler.PROTOCOL_HANDLERS.put(protocol, handler);
            return handler;
        }
    }
    
    static {
        PROTOCOL_HANDLERS = new HashMap();
        JVM_VENDOR_DEFAULT_PKGS = new String[] { "sun.net.www.protocol" };
    }
}
