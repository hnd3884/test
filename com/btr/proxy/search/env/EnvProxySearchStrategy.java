package com.btr.proxy.search.env;

import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.util.ProxyUtil;
import com.btr.proxy.util.Logger;
import java.net.ProxySelector;
import java.util.Properties;
import com.btr.proxy.search.ProxySearchStrategy;

public class EnvProxySearchStrategy implements ProxySearchStrategy
{
    private String httpEnv;
    private String httpsEnv;
    private String ftpEnv;
    private String noProxyEnv;
    private String httpProxy;
    private String httpsProxy;
    private String ftpProxy;
    private String noProxy;
    
    public EnvProxySearchStrategy() {
        this("http_proxy", "https_proxy", "ftp_proxy", "no_proxy");
    }
    
    public EnvProxySearchStrategy(final String httpEnv, final String httpsEnv, final String ftpEnv, final String noProxyEnv) {
        this.httpEnv = httpEnv;
        this.httpsEnv = httpsEnv;
        this.ftpEnv = ftpEnv;
        this.noProxyEnv = noProxyEnv;
        this.loadProxySettings();
    }
    
    private void loadProxySettings() {
        this.httpProxy = System.getenv(this.httpEnv);
        this.httpsProxy = System.getenv(this.httpsEnv);
        this.ftpProxy = System.getenv(this.ftpEnv);
        this.noProxy = System.getenv(this.noProxyEnv);
    }
    
    public Properties readSettings() {
        final Properties result = new Properties();
        result.setProperty(this.httpEnv, this.httpProxy);
        result.setProperty(this.httpsEnv, this.httpsProxy);
        result.setProperty(this.ftpEnv, this.ftpProxy);
        result.setProperty(this.noProxyEnv, this.noProxy);
        return result;
    }
    
    public ProxySelector getProxySelector() {
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Inspecting environment variables.", new Object[0]);
        final ProxySelector httpPS = ProxyUtil.parseProxySettings(this.httpProxy);
        if (httpPS == null) {
            return null;
        }
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Http Proxy is {0}", this.httpProxy);
        final ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        ps.setSelector("http", httpPS);
        final ProxySelector httpsPS = ProxyUtil.parseProxySettings(this.httpsProxy);
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Https Proxy is {0}", (httpsPS == null) ? this.httpsProxy : httpsPS);
        ps.setSelector("https", (httpsPS != null) ? httpsPS : httpPS);
        final ProxySelector ftpPS = ProxyUtil.parseProxySettings(this.ftpProxy);
        if (ftpPS != null) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Ftp Proxy is {0}", this.ftpProxy);
            ps.setSelector("ftp", ftpPS);
        }
        ProxySelector result = ps;
        if (this.noProxy != null && this.noProxy.trim().length() > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Using proxy bypass list: {0}", this.noProxy);
            result = new ProxyBypassListSelector(this.noProxy, ps);
        }
        return result;
    }
}
