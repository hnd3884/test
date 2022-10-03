package com.btr.proxy.search.desktop.gnome;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import java.io.File;
import java.io.IOException;
import com.btr.proxy.util.ProxyException;
import java.util.Properties;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.selector.direct.NoProxySelector;
import com.btr.proxy.util.Logger;
import java.net.ProxySelector;
import com.btr.proxy.search.ProxySearchStrategy;

public class GnomeProxySearchStrategy implements ProxySearchStrategy
{
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Detecting Gnome proxy settings", new Object[0]);
        final Properties settings = this.readSettings();
        String type = settings.getProperty("/system/proxy/mode");
        ProxySelector result = null;
        if (type == null) {
            final String useProxy = settings.getProperty("/system/http_proxy/use_http_proxy");
            if (useProxy == null) {
                return null;
            }
            type = (Boolean.parseBoolean(useProxy) ? "manual" : "none");
        }
        if ("none".equals(type)) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Gnome uses no proxy", new Object[0]);
            result = NoProxySelector.getInstance();
        }
        if ("manual".equals(type)) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Gnome uses manual proxy settings", new Object[0]);
            result = this.setupFixedProxySelector(settings);
        }
        if ("auto".equals(type)) {
            final String pacScriptUrl = settings.getProperty("/system/proxy/autoconfig_url", "");
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Gnome uses autodetect script {0}", pacScriptUrl);
            result = new PacProxySelector(new UrlPacScriptSource(pacScriptUrl));
        }
        final String noProxyList = settings.getProperty("/system/http_proxy/ignore_hosts", null);
        if (result != null && noProxyList != null && noProxyList.trim().length() > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Gnome uses proxy bypass list: {0}", noProxyList);
            result = new ProxyBypassListSelector(noProxyList, result);
        }
        return result;
    }
    
    public Properties readSettings() throws ProxyException {
        final Properties settings = new Properties();
        try {
            this.parseSettings("/system/proxy/", settings);
            this.parseSettings("/system/http_proxy/", settings);
        }
        catch (final IOException e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "Gnome settings file error.", e);
            throw new ProxyException(e);
        }
        return settings;
    }
    
    private File findSettingsFile(final String context) {
        final File userDir = new File(System.getProperty("user.home"));
        final StringBuilder path = new StringBuilder();
        final String[] arr$;
        final String[] parts = arr$ = context.split("/");
        for (final String part : arr$) {
            path.append(part);
            path.append(File.separator);
        }
        final File settingsFile = new File(userDir, ".gconf" + File.separator + path.toString() + "%gconf.xml");
        if (!settingsFile.exists()) {
            Logger.log(this.getClass(), Logger.LogLevel.WARNING, "Gnome settings: {0} not found.", settingsFile);
            return null;
        }
        return settingsFile;
    }
    
    private ProxySelector setupFixedProxySelector(final Properties settings) {
        if (!this.hasProxySettings(settings)) {
            return null;
        }
        final ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        this.installHttpSelector(settings, ps);
        if (this.useForAllProtocols(settings)) {
            ps.setFallbackSelector(ps.getSelector("http"));
        }
        else {
            this.installSecureSelector(settings, ps);
            this.installFtpSelector(settings, ps);
            this.installSocksSelector(settings, ps);
        }
        return ps;
    }
    
    private boolean useForAllProtocols(final Properties settings) {
        return Boolean.parseBoolean(settings.getProperty("/system/http_proxy/use_same_proxy", "false"));
    }
    
    private boolean hasProxySettings(final Properties settings) {
        final String proxyHost = settings.getProperty("/system/http_proxy/host", null);
        return proxyHost != null && proxyHost.length() > 0;
    }
    
    private void installHttpSelector(final Properties settings, final ProtocolDispatchSelector ps) throws NumberFormatException {
        final String proxyHost = settings.getProperty("/system/http_proxy/host", null);
        final int proxyPort = Integer.parseInt(settings.getProperty("/system/http_proxy/port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Gnome http proxy is {0}:{1}", proxyHost, proxyPort);
            ps.setSelector("http", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }
    
    private void installSocksSelector(final Properties settings, final ProtocolDispatchSelector ps) throws NumberFormatException {
        final String proxyHost = settings.getProperty("/system/proxy/socks_host", null);
        final int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/socks_port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Gnome socks proxy is {0}:{1}", proxyHost, proxyPort);
            ps.setSelector("socks", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }
    
    private void installFtpSelector(final Properties settings, final ProtocolDispatchSelector ps) throws NumberFormatException {
        final String proxyHost = settings.getProperty("/system/proxy/ftp_host", null);
        final int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/ftp_port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Gnome ftp proxy is {0}:{1}", proxyHost, proxyPort);
            ps.setSelector("ftp", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }
    
    private void installSecureSelector(final Properties settings, final ProtocolDispatchSelector ps) throws NumberFormatException {
        final String proxyHost = settings.getProperty("/system/proxy/secure_host", null);
        final int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/secure_port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Gnome secure proxy is {0}:{1}", proxyHost, proxyPort);
            ps.setSelector("https", new FixedProxySelector(proxyHost.trim(), proxyPort));
            ps.setSelector("sftp", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }
    
    private Properties parseSettings(final String context, final Properties settings) throws IOException {
        final File settingsFile = this.findSettingsFile(context);
        if (settingsFile == null) {
            return settings;
        }
        try {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(settingsFile);
            final Element root = doc.getDocumentElement();
            for (Node entry = root.getFirstChild(); entry != null; entry = entry.getNextSibling()) {
                if ("entry".equals(entry.getNodeName()) && entry instanceof Element) {
                    final String entryName = ((Element)entry).getAttribute("name");
                    settings.setProperty(context + entryName, this.getEntryValue((Element)entry));
                }
            }
        }
        catch (final SAXException e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "Gnome settings parse error", e);
            throw new IOException(e.getMessage());
        }
        catch (final ParserConfigurationException e2) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "Gnome settings parse error", e2);
            throw new IOException(e2.getMessage());
        }
        return settings;
    }
    
    private String getEntryValue(final Element entry) {
        final String type = entry.getAttribute("type");
        if ("int".equals(type) || "bool".equals(type)) {
            return entry.getAttribute("value");
        }
        if ("string".equals(type)) {
            final NodeList list = entry.getElementsByTagName("stringvalue");
            if (list.getLength() > 0) {
                return list.item(0).getTextContent();
            }
        }
        if ("list".equals(type)) {
            final StringBuilder result = new StringBuilder();
            final NodeList list2 = entry.getElementsByTagName("li");
            for (int i = 0; i < list2.getLength(); ++i) {
                if (result.length() > 0) {
                    result.append(",");
                }
                result.append(this.getEntryValue((Element)list2.item(i)));
            }
            return result.toString();
        }
        return null;
    }
}
