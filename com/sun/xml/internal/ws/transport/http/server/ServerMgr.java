package com.sun.xml.internal.ws.transport.http.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.Iterator;
import com.sun.xml.internal.ws.server.ServerRtException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpServer;
import java.net.URL;
import com.sun.net.httpserver.HttpContext;
import java.util.HashMap;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Logger;

final class ServerMgr
{
    private static final ServerMgr serverMgr;
    private static final Logger logger;
    private final Map<InetSocketAddress, ServerState> servers;
    
    private ServerMgr() {
        this.servers = new HashMap<InetSocketAddress, ServerState>();
    }
    
    static ServerMgr getInstance() {
        return ServerMgr.serverMgr;
    }
    
    HttpContext createContext(final String address) {
        try {
            final URL url = new URL(address);
            int port = url.getPort();
            if (port == -1) {
                port = url.getDefaultPort();
            }
            InetSocketAddress inetAddress = new InetSocketAddress(url.getHost(), port);
            ServerState state;
            synchronized (this.servers) {
                state = this.servers.get(inetAddress);
                if (state == null) {
                    final int finalPortNum = port;
                    for (final ServerState s : this.servers.values()) {
                        if (s.getServer().getAddress().getPort() == finalPortNum) {
                            state = s;
                            break;
                        }
                    }
                    if (!inetAddress.getAddress().isAnyLocalAddress() || state == null) {
                        ServerMgr.logger.fine("Creating new HTTP Server at " + inetAddress);
                        final HttpServer server = HttpServer.create(inetAddress, 0);
                        server.setExecutor(Executors.newCachedThreadPool());
                        final String path = url.toURI().getPath();
                        ServerMgr.logger.fine("Creating HTTP Context at = " + path);
                        final HttpContext context = server.createContext(path);
                        server.start();
                        inetAddress = server.getAddress();
                        ServerMgr.logger.fine("HTTP server started = " + inetAddress);
                        state = new ServerState(server, path);
                        this.servers.put(inetAddress, state);
                        return context;
                    }
                }
            }
            final HttpServer server = state.getServer();
            if (state.getPaths().contains(url.getPath())) {
                final String err = "Context with URL path " + url.getPath() + " already exists on the server " + server.getAddress();
                ServerMgr.logger.fine(err);
                throw new IllegalArgumentException(err);
            }
            ServerMgr.logger.fine("Creating HTTP Context at = " + url.getPath());
            final HttpContext context2 = server.createContext(url.getPath());
            state.oneMoreContext(url.getPath());
            return context2;
        }
        catch (final Exception e) {
            throw new ServerRtException("server.rt.err", new Object[] { e });
        }
    }
    
    void removeContext(final HttpContext context) {
        final InetSocketAddress inetAddress = context.getServer().getAddress();
        synchronized (this.servers) {
            final ServerState state = this.servers.get(inetAddress);
            final int instances = state.noOfContexts();
            if (instances < 2) {
                ((ExecutorService)state.getServer().getExecutor()).shutdown();
                state.getServer().stop(0);
                this.servers.remove(inetAddress);
            }
            else {
                state.getServer().removeContext(context);
                state.oneLessContext(context.getPath());
            }
        }
    }
    
    static {
        serverMgr = new ServerMgr();
        logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
    }
    
    private static final class ServerState
    {
        private final HttpServer server;
        private int instances;
        private Set<String> paths;
        
        ServerState(final HttpServer server, final String path) {
            this.paths = new HashSet<String>();
            this.server = server;
            this.instances = 1;
            this.paths.add(path);
        }
        
        public HttpServer getServer() {
            return this.server;
        }
        
        public void oneMoreContext(final String path) {
            ++this.instances;
            this.paths.add(path);
        }
        
        public void oneLessContext(final String path) {
            --this.instances;
            this.paths.remove(path);
        }
        
        public int noOfContexts() {
            return this.instances;
        }
        
        public Set<String> getPaths() {
            return this.paths;
        }
    }
}
