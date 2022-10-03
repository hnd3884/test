package com.btr.proxy.search.browser.ie;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.util.ProxyUtil;
import java.util.List;
import com.btr.proxy.util.UriFilter;
import java.util.ArrayList;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import java.util.Properties;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.search.desktop.win.Win32ProxyUtils;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.search.desktop.win.Win32IESettings;
import com.btr.proxy.util.Logger;
import java.net.ProxySelector;
import com.btr.proxy.search.ProxySearchStrategy;

public class IEProxySearchStrategy implements ProxySearchStrategy
{
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Detecting IE proxy settings", new Object[0]);
        final Win32IESettings ieSettings = this.readSettings();
        ProxySelector result = this.createPacSelector(ieSettings);
        if (result == null) {
            result = this.createFixedProxySelector(ieSettings);
        }
        return result;
    }
    
    public Win32IESettings readSettings() {
        final Win32IESettings ieSettings = new Win32ProxyUtils().winHttpGetIEProxyConfigForCurrentUser();
        return ieSettings;
    }
    
    private PacProxySelector createPacSelector(final Win32IESettings ieSettings) {
        String pacUrl = null;
        if (ieSettings.isAutoDetect()) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Autodetecting script URL.", new Object[0]);
            pacUrl = new Win32ProxyUtils().winHttpDetectAutoProxyConfigUrl(3);
        }
        if (pacUrl == null) {
            pacUrl = ieSettings.getAutoConfigUrl();
        }
        if (pacUrl != null && pacUrl.trim().length() > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "IE uses script: " + pacUrl, new Object[0]);
            if (pacUrl.startsWith("file://") && !pacUrl.startsWith("file:///")) {
                pacUrl = "file:///" + pacUrl.substring(7);
            }
            return new PacProxySelector(new UrlPacScriptSource(pacUrl));
        }
        return null;
    }
    
    private ProxySelector createFixedProxySelector(final Win32IESettings ieSettings) throws ProxyException {
        final String proxyString = ieSettings.getProxy();
        final String bypassList = ieSettings.getProxyBypass();
        if (proxyString == null) {
            return null;
        }
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "IE uses manual settings: {0} with bypass list: {1}", proxyString, bypassList);
        final Properties p = this.parseProxyList(proxyString);
        final ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        this.addSelectorForProtocol(p, "http", ps);
        this.addSelectorForProtocol(p, "https", ps);
        this.addSelectorForProtocol(p, "ftp", ps);
        this.addSelectorForProtocol(p, "gopher", ps);
        this.addSelectorForProtocol(p, "socks", ps);
        this.addFallbackSelector(p, ps);
        final ProxySelector result = this.setByPassListOnSelector(bypassList, ps);
        return result;
    }
    
    private ProxySelector setByPassListOnSelector(String bypassList, final ProtocolDispatchSelector ps) {
        if (bypassList != null && bypassList.trim().length() > 0) {
            ProxyBypassListSelector result;
            if ("<local>".equals(bypassList.trim())) {
                result = this.buildLocalBypassSelector(ps);
            }
            else {
                bypassList = bypassList.replace(';', ',');
                result = new ProxyBypassListSelector(bypassList, ps);
            }
            return result;
        }
        return ps;
    }
    
    private ProxyBypassListSelector buildLocalBypassSelector(final ProtocolDispatchSelector ps) {
        final List<UriFilter> localBypassFilter = new ArrayList<UriFilter>();
        localBypassFilter.add(new IELocalByPassFilter());
        return new ProxyBypassListSelector(localBypassFilter, ps);
    }
    
    private void addFallbackSelector(final Properties settings, final ProtocolDispatchSelector ps) {
        final String proxy = settings.getProperty("default");
        if (proxy != null) {
            ps.setFallbackSelector(ProxyUtil.parseProxySettings(proxy));
        }
    }
    
    private void addSelectorForProtocol(final Properties settings, final String protocol, final ProtocolDispatchSelector ps) {
        final String proxy = settings.getProperty(protocol);
        if (proxy != null) {
            final FixedProxySelector protocolSelector = ProxyUtil.parseProxySettings(proxy);
            ps.setSelector(protocol, protocolSelector);
        }
    }
    
    private Properties parseProxyList(String proxyString) throws ProxyException {
        final Properties p = new Properties();
        if (proxyString.indexOf(61) == -1) {
            p.setProperty("default", proxyString);
        }
        else {
            try {
                proxyString = proxyString.replace(';', '\n');
                p.load(new ByteArrayInputStream(proxyString.getBytes("ISO-8859-1")));
            }
            catch (final IOException e) {
                Logger.log(this.getClass(), Logger.LogLevel.ERROR, "Error reading IE settings as properties: {0}", e);
                throw new ProxyException(e);
            }
        }
        return p;
    }
}
