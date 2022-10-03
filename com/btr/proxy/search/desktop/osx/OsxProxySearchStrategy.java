package com.btr.proxy.search.desktop.osx;

import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.fixed.FixedSocksSelector;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.search.wpad.WpadProxySearchStrategy;
import java.util.Iterator;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.search.browser.ie.IELocalByPassFilter;
import com.btr.proxy.util.UriFilter;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import java.util.List;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.util.PListParser;
import com.btr.proxy.util.Logger;
import java.net.ProxySelector;
import com.btr.proxy.search.ProxySearchStrategy;

public class OsxProxySearchStrategy implements ProxySearchStrategy
{
    public static final String OVERRIDE_SETTINGS_FILE = "com.btr.proxy.osx.settingsFile";
    private static final String SETTINGS_FILE = "/Library/Preferences/SystemConfiguration/preferences.plist";
    
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Detecting OSX proxy settings", new Object[0]);
        try {
            final PListParser.Dict settings = PListParser.load(this.getSettingsFile());
            final Object currentSet = settings.getAtPath("/CurrentSet");
            if (currentSet == null) {
                throw new ProxyException("CurrentSet not defined");
            }
            final PListParser.Dict networkSet = (PListParser.Dict)settings.getAtPath(String.valueOf(currentSet));
            final List<?> serviceOrder = (List<?>)networkSet.getAtPath("/Network/Global/IPv4/ServiceOrder");
            if (serviceOrder == null || serviceOrder.size() == 0) {
                throw new ProxyException("ServiceOrder not defined");
            }
            final Object firstService = serviceOrder.get(0);
            final Object networkService = networkSet.getAtPath("/Network/Service/" + firstService + "/__LINK__");
            if (networkService == null) {
                throw new ProxyException("NetworkService not defined.");
            }
            final PListParser.Dict selectedServiceSettings = (PListParser.Dict)settings.getAtPath("" + networkService);
            final PListParser.Dict proxySettings = (PListParser.Dict)selectedServiceSettings.getAtPath("/Proxies");
            final ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
            this.installSelectorForProtocol(proxySettings, ps, "HTTP");
            this.installSelectorForProtocol(proxySettings, ps, "HTTPS");
            this.installSelectorForProtocol(proxySettings, ps, "FTP");
            this.installSelectorForProtocol(proxySettings, ps, "Gopher");
            this.installSelectorForProtocol(proxySettings, ps, "RTSP");
            this.installSocksProxy(proxySettings, ps);
            ProxySelector result = ps;
            result = this.installPacProxyIfAvailable(proxySettings, result);
            result = this.autodetectProxyIfAvailable(proxySettings, result);
            result = this.installExceptionList(proxySettings, result);
            result = this.installSimpleHostFilter(proxySettings, result);
            return result;
        }
        catch (final PListParser.XmlParseException e) {
            throw new ProxyException(e);
        }
        catch (final IOException e2) {
            throw new ProxyException(e2);
        }
    }
    
    private File getSettingsFile() {
        final File result = new File("/Library/Preferences/SystemConfiguration/preferences.plist");
        final String overrideFile = System.getProperty("com.btr.proxy.osx.settingsFile");
        if (overrideFile != null) {
            return new File(overrideFile);
        }
        return result;
    }
    
    private ProxySelector installSimpleHostFilter(final PListParser.Dict proxySettings, ProxySelector result) {
        if (this.isActive(proxySettings.get("ExcludeSimpleHostnames"))) {
            final List<UriFilter> localBypassFilter = new ArrayList<UriFilter>();
            localBypassFilter.add(new IELocalByPassFilter());
            result = new ProxyBypassListSelector(localBypassFilter, result);
        }
        return result;
    }
    
    private ProxySelector installExceptionList(final PListParser.Dict proxySettings, ProxySelector result) {
        final List<?> proxyExceptions = (List<?>)proxySettings.get("ExceptionsList");
        if (proxyExceptions != null && proxyExceptions.size() > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "OSX uses proxy bypass list: {0}", proxyExceptions);
            final String noProxyList = this.toCommaSeparatedString(proxyExceptions);
            result = new ProxyBypassListSelector(noProxyList, result);
        }
        return result;
    }
    
    private String toCommaSeparatedString(final List<?> proxyExceptions) {
        final StringBuilder result = new StringBuilder();
        for (final Object object : proxyExceptions) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(object);
        }
        return result.toString();
    }
    
    private ProxySelector autodetectProxyIfAvailable(final PListParser.Dict proxySettings, ProxySelector result) throws ProxyException {
        if (this.isActive(proxySettings.get("ProxyAutoDiscoveryEnable"))) {
            final ProxySelector wp = new WpadProxySearchStrategy().getProxySelector();
            if (wp != null) {
                result = wp;
            }
        }
        return result;
    }
    
    private ProxySelector installPacProxyIfAvailable(final PListParser.Dict proxySettings, ProxySelector result) {
        if (this.isActive(proxySettings.get("ProxyAutoConfigEnable"))) {
            final String url = (String)proxySettings.get("ProxyAutoConfigURLString");
            final PacScriptSource pacSource = new UrlPacScriptSource(url);
            result = new PacProxySelector(pacSource);
        }
        return result;
    }
    
    private void installSocksProxy(final PListParser.Dict proxySettings, final ProtocolDispatchSelector ps) {
        if (this.isActive(proxySettings.get("SOCKSEnable"))) {
            final String proxyHost = (String)proxySettings.get("SOCKSProxy");
            final int proxyPort = (int)proxySettings.get("SOCKSPort");
            ps.setSelector("socks", new FixedSocksSelector(proxyHost, proxyPort));
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "OSX socks proxy is {0}:{1}", proxyHost, proxyPort);
        }
    }
    
    private void installSelectorForProtocol(final PListParser.Dict proxySettings, final ProtocolDispatchSelector ps, final String protocol) {
        final String prefix = protocol.trim();
        if (this.isActive(proxySettings.get(prefix + "Enable"))) {
            final String proxyHost = (String)proxySettings.get(prefix + "Proxy");
            final int proxyPort = (int)proxySettings.get(prefix + "Port");
            final FixedProxySelector fp = new FixedProxySelector(proxyHost, proxyPort);
            ps.setSelector(protocol.toLowerCase(), fp);
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "OSX uses for {0} the proxy {1}:{2}", protocol, proxyHost, proxyPort);
        }
    }
    
    private boolean isActive(final Object value) {
        return Integer.valueOf(1).equals(value);
    }
}
