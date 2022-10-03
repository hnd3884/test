package com.btr.proxy.search.wpad;

import java.net.UnknownHostException;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.InetAddress;
import java.util.Properties;
import java.io.IOException;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.util.Logger;
import java.net.ProxySelector;
import com.btr.proxy.search.ProxySearchStrategy;

public class WpadProxySearchStrategy implements ProxySearchStrategy
{
    public ProxySelector getProxySelector() throws ProxyException {
        try {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Using WPAD to find a proxy", new Object[0]);
            String pacScriptUrl = this.detectScriptUrlPerDHCP();
            if (pacScriptUrl == null) {
                pacScriptUrl = this.detectScriptUrlPerDNS();
            }
            if (pacScriptUrl == null) {
                return null;
            }
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "PAC script url found: {0}", pacScriptUrl);
            return new PacProxySelector(new UrlPacScriptSource(pacScriptUrl));
        }
        catch (final IOException e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "Error during WPAD search.", e);
            throw new ProxyException(e);
        }
    }
    
    public Properties readSettings() {
        try {
            String pacScriptUrl = this.detectScriptUrlPerDHCP();
            if (pacScriptUrl == null) {
                pacScriptUrl = this.detectScriptUrlPerDNS();
            }
            if (pacScriptUrl == null) {
                return null;
            }
            final Properties result = new Properties();
            result.setProperty("url", pacScriptUrl);
            return result;
        }
        catch (final IOException e) {
            return new Properties();
        }
    }
    
    private String detectScriptUrlPerDNS() throws IOException {
        String result = null;
        String fqdn = InetAddress.getLocalHost().getCanonicalHostName();
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Searching per DNS guessing.", new Object[0]);
        for (int index = fqdn.indexOf(46); index != -1 && result == null; index = fqdn.indexOf(46)) {
            fqdn = fqdn.substring(index + 1);
            if (fqdn.indexOf(46) == -1) {
                break;
            }
            try {
                final URL lookupURL = new URL("http://wpad." + fqdn + "/wpad.dat");
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Trying url: {0}", lookupURL);
                final HttpURLConnection con = (HttpURLConnection)lookupURL.openConnection(Proxy.NO_PROXY);
                con.setInstanceFollowRedirects(true);
                con.setRequestProperty("accept", "application/x-ns-proxy-autoconfig");
                if (con.getResponseCode() == 200) {
                    result = lookupURL.toString();
                }
                con.disconnect();
            }
            catch (final UnknownHostException e) {
                Logger.log(this.getClass(), Logger.LogLevel.DEBUG, "Not available!", new Object[0]);
            }
        }
        return result;
    }
    
    private String detectScriptUrlPerDHCP() {
        Logger.log(this.getClass(), Logger.LogLevel.DEBUG, "Searching per DHCP not supported yet.", new Object[0]);
        return null;
    }
}
