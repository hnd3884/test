package com.btr.proxy.search.java;

import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.fixed.FixedSocksSelector;
import com.btr.proxy.util.Logger;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import java.net.ProxySelector;
import com.btr.proxy.search.ProxySearchStrategy;

public class JavaProxySearchStrategy implements ProxySearchStrategy
{
    public ProxySelector getProxySelector() {
        final ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        if (!this.proxyPropertyPresent()) {
            return null;
        }
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Using settings from Java System Properties", new Object[0]);
        this.setupProxyForProtocol(ps, "http", 80);
        this.setupProxyForProtocol(ps, "https", 443);
        this.setupProxyForProtocol(ps, "ftp", 80);
        this.setupProxyForProtocol(ps, "ftps", 80);
        this.setupSocktProxy(ps);
        return ps;
    }
    
    private boolean proxyPropertyPresent() {
        return System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyHost").trim().length() > 0;
    }
    
    private void setupSocktProxy(final ProtocolDispatchSelector ps) {
        final String host = System.getProperty("socksProxyHost");
        final String port = System.getProperty("socksProxyPort", "1080");
        if (host != null && host.trim().length() > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Socks proxy {0}:{1} found", host, port);
            ps.setSelector("socks", new FixedSocksSelector(host, Integer.parseInt(port)));
        }
    }
    
    private void setupProxyForProtocol(final ProtocolDispatchSelector ps, final String protocol, final int defaultPort) {
        final String host = System.getProperty(protocol + ".proxyHost");
        final String port = System.getProperty(protocol + ".proxyPort", "" + defaultPort);
        String whiteList = System.getProperty(protocol + ".nonProxyHosts", "").replace('|', ',');
        if ("https".equalsIgnoreCase(protocol)) {
            whiteList = System.getProperty("http.nonProxyHosts", "").replace('|', ',');
        }
        if (host == null || host.trim().length() == 0) {
            return;
        }
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, protocol.toUpperCase() + " proxy {0}:{1} found using whitelist: {2}", host, port, whiteList);
        ProxySelector protocolSelector = new FixedProxySelector(host, Integer.parseInt(port));
        if (whiteList.trim().length() > 0) {
            protocolSelector = new ProxyBypassListSelector(whiteList, protocolSelector);
        }
        ps.setSelector(protocol, protocolSelector);
    }
}
