package com.btr.proxy.search.browser.firefox;

import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.fixed.FixedSocksSelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import java.io.IOException;
import com.btr.proxy.util.ProxyException;
import java.util.Properties;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.search.wpad.WpadProxySearchStrategy;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.selector.direct.NoProxySelector;
import com.btr.proxy.search.desktop.DesktopProxySearchStrategy;
import com.btr.proxy.util.Logger;
import java.net.ProxySelector;
import com.btr.proxy.util.PlatformUtil;
import com.btr.proxy.search.ProxySearchStrategy;

public class FirefoxProxySearchStrategy implements ProxySearchStrategy
{
    private FirefoxProfileSource profileScanner;
    private FirefoxSettingParser settingsParser;
    
    public FirefoxProxySearchStrategy() {
        if (PlatformUtil.getCurrentPlattform() == PlatformUtil.Platform.WIN) {
            this.profileScanner = new WinFirefoxProfileSource();
        }
        else {
            this.profileScanner = new LinuxFirefoxProfileSource();
        }
        this.settingsParser = new FirefoxSettingParser();
    }
    
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Detecting Firefox settings.", new Object[0]);
        final Properties settings = this.readSettings();
        ProxySelector result = null;
        final int type = Integer.parseInt(settings.getProperty("network.proxy.type", "-1"));
        switch (type) {
            case -1: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox uses system settings", new Object[0]);
                result = new DesktopProxySearchStrategy().getProxySelector();
                break;
            }
            case 0: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox uses no proxy", new Object[0]);
                result = NoProxySelector.getInstance();
                break;
            }
            case 1: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox uses manual settings", new Object[0]);
                result = this.setupFixedProxySelector(settings);
                break;
            }
            case 2: {
                final String pacScriptUrl = settings.getProperty("network.proxy.autoconfig_url", "");
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox uses script (PAC) {0}", pacScriptUrl);
                result = new PacProxySelector(new UrlPacScriptSource(pacScriptUrl));
                break;
            }
            case 3: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Netscape compability mode -> uses no proxy", new Object[0]);
                result = NoProxySelector.getInstance();
                break;
            }
            case 4: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox uses automatic detection (WPAD)", new Object[0]);
                result = new WpadProxySearchStrategy().getProxySelector();
                break;
            }
        }
        final String noProxyList = settings.getProperty("network.proxy.no_proxies_on", null);
        if (result != null && noProxyList != null && noProxyList.trim().length() > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox uses proxy bypass list for: {0}", noProxyList);
            result = new ProxyBypassListSelector(noProxyList, result);
        }
        return result;
    }
    
    public Properties readSettings() throws ProxyException {
        try {
            final Properties settings = this.settingsParser.parseSettings(this.profileScanner);
            return settings;
        }
        catch (final IOException e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "Error parsing settings", e);
            throw new ProxyException(e);
        }
    }
    
    private ProxySelector setupFixedProxySelector(final Properties settings) {
        final ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        this.installHttpProxy(ps, settings);
        if (this.isProxyShared(settings)) {
            this.installSharedProxy(ps);
        }
        else {
            this.installFtpProxy(ps, settings);
            this.installSecureProxy(ps, settings);
            this.installSocksProxy(ps, settings);
        }
        return ps;
    }
    
    private void installFtpProxy(final ProtocolDispatchSelector ps, final Properties settings) throws NumberFormatException {
        this.installSelectorForProtocol(ps, settings, "ftp");
    }
    
    private void installHttpProxy(final ProtocolDispatchSelector ps, final Properties settings) throws NumberFormatException {
        this.installSelectorForProtocol(ps, settings, "http");
    }
    
    private boolean isProxyShared(final Properties settings) {
        return Boolean.TRUE.toString().equals(settings.getProperty("network.proxy.share_proxy_settings", "false").toLowerCase());
    }
    
    private void installSharedProxy(final ProtocolDispatchSelector ps) {
        final ProxySelector httpProxy = ps.getSelector("http");
        if (httpProxy != null) {
            ps.setFallbackSelector(httpProxy);
        }
    }
    
    private void installSocksProxy(final ProtocolDispatchSelector ps, final Properties settings) throws NumberFormatException {
        final String proxyHost = settings.getProperty("network.proxy.socks", null);
        final int proxyPort = Integer.parseInt(settings.getProperty("network.proxy.socks_port", "0"));
        if (proxyHost != null && proxyPort != 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox socks proxy is {0}:{1}", proxyHost, proxyPort);
            ps.setSelector("socks", new FixedSocksSelector(proxyHost, proxyPort));
        }
    }
    
    private void installSecureProxy(final ProtocolDispatchSelector ps, final Properties settings) throws NumberFormatException {
        final String proxyHost = settings.getProperty("network.proxy.ssl", null);
        final int proxyPort = Integer.parseInt(settings.getProperty("network.proxy.ssl_port", "0"));
        if (proxyHost != null && proxyPort != 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox secure proxy is {0}:{1}", proxyHost, proxyPort);
            ps.setSelector("https", new FixedProxySelector(proxyHost, proxyPort));
            ps.setSelector("sftp", new FixedProxySelector(proxyHost, proxyPort));
        }
    }
    
    private void installSelectorForProtocol(final ProtocolDispatchSelector ps, final Properties settings, final String protocol) throws NumberFormatException {
        final String proxyHost = settings.getProperty("network.proxy." + protocol, null);
        final int proxyPort = Integer.parseInt(settings.getProperty("network.proxy." + protocol + "_port", "0"));
        if (proxyHost != null && proxyPort != 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox " + protocol + " proxy is {0}:{1}", proxyHost, proxyPort);
            ps.setSelector(protocol, new FixedProxySelector(proxyHost, proxyPort));
        }
    }
}
