package io.netty.resolver.dns;

import io.netty.util.internal.logging.InternalLoggerFactory;
import javax.naming.directory.DirContext;
import javax.naming.NamingException;
import java.net.URISyntaxException;
import io.netty.util.internal.SocketUtils;
import java.net.URI;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.net.InetSocketAddress;
import java.util.List;
import io.netty.util.internal.logging.InternalLogger;

final class DirContextUtils
{
    private static final InternalLogger logger;
    
    private DirContextUtils() {
    }
    
    static void addNameServers(final List<InetSocketAddress> defaultNameServers, final int defaultPort) {
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns://");
        try {
            final DirContext ctx = new InitialDirContext(env);
            final String dnsUrls = (String)ctx.getEnvironment().get("java.naming.provider.url");
            if (dnsUrls != null && !dnsUrls.isEmpty()) {
                final String[] split;
                final String[] servers = split = dnsUrls.split(" ");
                for (final String server : split) {
                    try {
                        final URI uri = new URI(server);
                        final String host = new URI(server).getHost();
                        if (host == null || host.isEmpty()) {
                            DirContextUtils.logger.debug("Skipping a nameserver URI as host portion could not be extracted: {}", server);
                        }
                        else {
                            final int port = uri.getPort();
                            defaultNameServers.add(SocketUtils.socketAddress(uri.getHost(), (port == -1) ? defaultPort : port));
                        }
                    }
                    catch (final URISyntaxException e) {
                        DirContextUtils.logger.debug("Skipping a malformed nameserver URI: {}", server, e);
                    }
                }
            }
        }
        catch (final NamingException ex) {}
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DirContextUtils.class);
    }
}
