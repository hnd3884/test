package com.btr.proxy.selector.pac;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import com.btr.proxy.util.ProxyUtil;
import java.net.Proxy;
import java.util.List;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import com.btr.proxy.util.Logger;
import java.net.ProxySelector;

public class PacProxySelector extends ProxySelector
{
    private final boolean JAVAX_PARSER;
    private static final String PAC_SOCKS = "SOCKS";
    private static final String PAC_DIRECT = "DIRECT";
    private PacScriptParser pacScriptParser;
    private static volatile boolean enabled;
    
    public PacProxySelector(final PacScriptSource pacSource) {
        this.JAVAX_PARSER = ScriptAvailability.isJavaxScriptingAvailable();
        this.selectEngine(pacSource);
    }
    
    public static void setEnabled(final boolean enable) {
        PacProxySelector.enabled = enable;
    }
    
    public static boolean isEnabled() {
        return PacProxySelector.enabled;
    }
    
    private void selectEngine(final PacScriptSource pacSource) {
        try {
            if (this.JAVAX_PARSER) {
                Logger.log(this.getClass(), Logger.LogLevel.INFO, "Using javax.script JavaScript engine.", new Object[0]);
                this.pacScriptParser = new JavaxPacScriptParser(pacSource);
            }
            else {
                Logger.log(this.getClass(), Logger.LogLevel.INFO, "Using Rhino JavaScript engine.", new Object[0]);
                this.pacScriptParser = new RhinoPacScriptParser(pacSource);
            }
        }
        catch (final Exception e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "PAC parser error.", e);
        }
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
    }
    
    @Override
    public List<Proxy> select(final URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null.");
        }
        if (!PacProxySelector.enabled) {
            return ProxyUtil.noProxyList();
        }
        return this.findProxy(uri);
    }
    
    private List<Proxy> findProxy(final URI uri) {
        try {
            final List<Proxy> proxies = new ArrayList<Proxy>();
            final String parseResult = this.pacScriptParser.evaluate(uri.toString(), uri.getHost());
            final String[] arr$;
            final String[] proxyDefinitions = arr$ = parseResult.split("[;]");
            for (final String proxyDef : arr$) {
                if (proxyDef.trim().length() > 0) {
                    proxies.add(this.buildProxyFromPacResult(proxyDef));
                }
            }
            return proxies;
        }
        catch (final ProxyEvaluationException e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "PAC resolving error.", e);
            return ProxyUtil.noProxyList();
        }
    }
    
    private Proxy buildProxyFromPacResult(final String pacResult) {
        if (pacResult == null || pacResult.trim().length() < 6) {
            return Proxy.NO_PROXY;
        }
        final String proxyDef = pacResult.trim();
        if (proxyDef.toUpperCase().startsWith("DIRECT")) {
            return Proxy.NO_PROXY;
        }
        Proxy.Type type = Proxy.Type.HTTP;
        if (proxyDef.toUpperCase().startsWith("SOCKS")) {
            type = Proxy.Type.SOCKS;
        }
        String host = proxyDef.substring(6);
        Integer port = 80;
        final int indexOfPort = host.indexOf(58);
        if (indexOfPort != -1) {
            port = Integer.parseInt(host.substring(indexOfPort + 1).trim());
            host = host.substring(0, indexOfPort).trim();
        }
        final SocketAddress adr = InetSocketAddress.createUnresolved(host, port);
        return new Proxy(type, adr);
    }
    
    static {
        PacProxySelector.enabled = true;
    }
}
