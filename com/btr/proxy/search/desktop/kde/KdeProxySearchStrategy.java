package com.btr.proxy.search.desktop.kde;

import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.selector.whitelist.UseProxyWhiteListSelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.util.ProxyUtil;
import com.btr.proxy.search.env.EnvProxySearchStrategy;
import java.io.IOException;
import com.btr.proxy.util.ProxyException;
import java.util.Properties;
import com.btr.proxy.search.wpad.WpadProxySearchStrategy;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.selector.direct.NoProxySelector;
import com.btr.proxy.util.Logger;
import java.net.ProxySelector;
import com.btr.proxy.search.ProxySearchStrategy;

public class KdeProxySearchStrategy implements ProxySearchStrategy
{
    private KdeSettingsParser settingsParser;
    
    public KdeProxySearchStrategy() {
        this.settingsParser = new KdeSettingsParser();
    }
    
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Detecting Kde proxy settings", new Object[0]);
        final Properties settings = this.readSettings();
        if (settings == null) {
            return null;
        }
        ProxySelector result = null;
        final int type = Integer.parseInt(settings.getProperty("ProxyType", "-1"));
        switch (type) {
            case 0: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde uses no proxy", new Object[0]);
                result = NoProxySelector.getInstance();
                break;
            }
            case 1: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde uses manual proxy settings", new Object[0]);
                result = this.setupFixedProxySelector(settings);
                break;
            }
            case 2: {
                final String pacScriptUrl = settings.getProperty("Proxy Config Script", "");
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde uses autodetect script {0}", pacScriptUrl);
                result = new PacProxySelector(new UrlPacScriptSource(pacScriptUrl));
                break;
            }
            case 3: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde uses WPAD to detect the proxy", new Object[0]);
                result = new WpadProxySearchStrategy().getProxySelector();
                break;
            }
            case 4: {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde reads proxy from environment", new Object[0]);
                result = this.setupEnvVarSelector(settings);
                break;
            }
        }
        return result;
    }
    
    private Properties readSettings() throws ProxyException {
        try {
            return this.settingsParser.parseSettings();
        }
        catch (final IOException e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "Can't parse settings.", e);
            throw new ProxyException(e);
        }
    }
    
    private ProxySelector setupEnvVarSelector(final Properties settings) {
        final ProxySelector result = new EnvProxySearchStrategy(settings.getProperty("httpProxy"), settings.getProperty("httpsProxy"), settings.getProperty("ftpProxy"), settings.getProperty("NoProxyFor")).getProxySelector();
        return result;
    }
    
    private ProxySelector setupFixedProxySelector(final Properties settings) {
        String proxyVar = settings.getProperty("httpProxy", null);
        final FixedProxySelector httpPS = ProxyUtil.parseProxySettings(proxyVar);
        if (httpPS == null) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde http proxy is {0}", proxyVar);
            return null;
        }
        final ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        ps.setSelector("http", httpPS);
        proxyVar = settings.getProperty("httpsProxy", null);
        final FixedProxySelector httpsPS = ProxyUtil.parseProxySettings(proxyVar);
        if (httpsPS != null) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde https proxy is {0}", proxyVar);
            ps.setSelector("https", httpsPS);
        }
        proxyVar = settings.getProperty("ftpProxy", null);
        final FixedProxySelector ftpPS = ProxyUtil.parseProxySettings(proxyVar);
        if (ftpPS != null) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde ftp proxy is {0}", proxyVar);
            ps.setSelector("ftp", ftpPS);
        }
        final String noProxyList = settings.getProperty("NoProxyFor", null);
        if (noProxyList == null || noProxyList.trim().length() <= 0) {
            return ps;
        }
        final boolean reverse = "true".equals(settings.getProperty("ReversedException", "false"));
        if (reverse) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde proxy blacklist is {0}", noProxyList);
            return new UseProxyWhiteListSelector(noProxyList, ps);
        }
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Kde proxy whitelist is {0}", noProxyList);
        return new ProxyBypassListSelector(noProxyList, ps);
    }
}
