package com.me.devicemanagement.onpremise.server.general;

import java.util.Hashtable;
import java.io.File;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateConstants;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.util.Collection;
import java.util.Arrays;
import java.security.cert.Certificate;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.certificate.CertificateCacheHandler;
import java.util.HashMap;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.Encoder;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.general.ServerSettingsAPI;

public class ServerSettingsImpl implements ServerSettingsAPI
{
    private static final Logger LOGGER;
    
    public Properties getNATConfigurationProperties() {
        Properties natProps = new Properties();
        try {
            natProps = NATHandler.getNATConfigurationProperties();
        }
        catch (final Exception e) {
            ServerSettingsImpl.LOGGER.log(Level.SEVERE, "Exception while getting NAT Configuration in DCNATSettingsAPIImpl class... " + e);
        }
        return natProps;
    }
    
    public Properties getProxyConfiguration() {
        final Properties prop = new Properties();
        try {
            final DataObject dobj = SyMUtil.getPersistence().get("ProxyConfiguration", (Criteria)null);
            if (!dobj.isEmpty()) {
                final Row proxyRow = dobj.getFirstRow("ProxyConfiguration");
                if (proxyRow != null) {
                    String proxyHost = null;
                    String proxyPort = null;
                    String proxyUser = null;
                    String proxyPass = null;
                    Integer proxyScriptEna = 0;
                    String proxyScript = "";
                    proxyHost = proxyRow.get("HTTPPROXYHOST").toString();
                    proxyPort = proxyRow.get("HTTPPROXYPORT").toString();
                    proxyUser = proxyRow.get("HTTPPROXYUSER").toString();
                    proxyPass = proxyRow.get("HTTPPROXYPASSWORD").toString();
                    proxyScriptEna = Integer.parseInt(proxyRow.get("PROXYSCRIPT_ENABLED").toString());
                    if (proxyScriptEna == 1) {
                        proxyScript = proxyRow.get("PROXYSCRIPT").toString();
                    }
                    ((Hashtable<String, String>)prop).put("proxyHost", proxyHost);
                    ((Hashtable<String, String>)prop).put("proxyPort", proxyPort);
                    ((Hashtable<String, String>)prop).put("proxyUser", proxyUser);
                    ((Hashtable<String, String>)prop).put("proxyScript", proxyScript);
                    ((Hashtable<String, Integer>)prop).put("proxyScriptEna", proxyScriptEna);
                    if (proxyPass != null) {
                        ((Hashtable<String, String>)prop).put("proxyPass", Encoder.convertFromBase(proxyPass));
                    }
                }
            }
        }
        catch (final Exception ex) {
            ServerSettingsImpl.LOGGER.log(Level.INFO, "Exception in ProxyConfig : ", ex);
        }
        return prop;
    }
    
    public Properties getProxyConfiguration(final String url, final Properties prop) {
        final Properties pocProp = new Properties();
        try {
            if (prop.containsKey("proxyScript") && prop.get("proxyScript") != null && !((Hashtable<K, Object>)prop).get("proxyScript").equals("")) {
                String proxyHost = null;
                String proxyPort = null;
                final String proxyScript = ((Hashtable<K, String>)prop).get("proxyScript");
                final PacProxySelector pacProxySelector = new PacProxySelector((PacScriptSource)new UrlPacScriptSource(proxyScript));
                final List<Proxy> proxyList = pacProxySelector.select(new URI(url));
                if (proxyList != null && !proxyList.isEmpty()) {
                    for (final Proxy proxy : proxyList) {
                        final SocketAddress address = proxy.address();
                        if (address != null) {
                            proxyHost = ((InetSocketAddress)address).getHostName();
                            proxyPort = Integer.toString(((InetSocketAddress)address).getPort());
                        }
                    }
                    if (proxyHost != null) {
                        ((Hashtable<String, String>)pocProp).put("proxyHost", proxyHost);
                    }
                    else {
                        ((Hashtable<String, Object>)pocProp).put("proxyHost", ((Hashtable<K, Object>)prop).get("proxyHost"));
                    }
                    if (proxyPort != null) {
                        ((Hashtable<String, String>)pocProp).put("proxyPort", proxyPort);
                    }
                    else {
                        ((Hashtable<String, Object>)pocProp).put("proxyPort", ((Hashtable<K, Object>)prop).get("proxyPort"));
                    }
                    ((Hashtable<String, Object>)pocProp).put("proxyUser", ((Hashtable<K, Object>)prop).get("proxyUser"));
                    ((Hashtable<String, Object>)pocProp).put("proxyPass", ((Hashtable<K, Object>)prop).get("proxyPass"));
                    return pocProp;
                }
                ServerSettingsImpl.LOGGER.log(Level.INFO, "proxyList is null for given Domain  :: " + url);
            }
            else {
                ServerSettingsImpl.LOGGER.log(Level.INFO, "proxyScript  is null for given proxy Details, URL Domain : " + url);
            }
        }
        catch (final Exception ex) {
            ServerSettingsImpl.LOGGER.log(Level.INFO, "Exception in getProxyConfiguration  : ", ex);
        }
        return prop;
    }
    
    public HashMap getAllNATProperties() {
        final HashMap dynaMap = new HashMap();
        return NATHandler.getInstance().setNATvaluesInForm(dynaMap);
    }
    
    public int getCertificateType() throws Exception {
        int certificateType = -1;
        final Object certificateTypeObj = CertificateCacheHandler.getInstance().get("certificateType");
        if (certificateTypeObj != null) {
            certificateType = (int)certificateTypeObj;
        }
        if (certificateType != -1) {
            return certificateType;
        }
        try {
            if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                final List<Certificate> certificateChain = new ArrayList<Certificate>();
                final String serverCertificateFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
                final String intermediateCertificateFilePath = SSLCertificateUtil.getInstance().getIntermediateCertificateFilePath();
                final String serverCACertificateFilePath = SSLCertificateUtil.getInstance().getServerCACertificateFilePath();
                final Certificate serverCertificate = SSLCertificateUtil.getCertificate(serverCertificateFilePath);
                certificateChain.add(serverCertificate);
                final Certificate intermediateCertificate = SSLCertificateUtil.getCertificate(intermediateCertificateFilePath);
                final Certificate serverCACertificate = SSLCertificateUtil.getCertificate(serverCACertificateFilePath);
                if (intermediateCertificate != null) {
                    certificateChain.addAll(Arrays.asList(intermediateCertificate));
                }
                if (serverCACertificate != null) {
                    certificateChain.add(serverCACertificate);
                }
                final Boolean isCAFoundInCACerts = CertificateUtils.verifyCertificateChainAgainstCACertsFile((List)certificateChain);
                if (!isCAFoundInCACerts) {
                    certificateType = 3;
                }
                else {
                    certificateType = 2;
                }
            }
            else {
                final String sanCertGeneratedSysParam = SyMUtil.getSyMParameter("SAN_CERTIFICATE_GENERATED");
                Boolean isSANCertificateGenerated = Boolean.FALSE;
                if (sanCertGeneratedSysParam != null && !sanCertGeneratedSysParam.trim().equalsIgnoreCase("")) {
                    isSANCertificateGenerated = sanCertGeneratedSysParam.equals("true");
                }
                if (isSANCertificateGenerated) {
                    certificateType = 4;
                }
                else {
                    certificateType = 1;
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(CertificateConstants.class.getName()).log(Level.SEVERE, null, ex);
        }
        CertificateCacheHandler.getInstance().put("certificateType", (Object)certificateType);
        return certificateType;
    }
    
    public String getResourceBundleRootDirectory() {
        return System.getProperty("server.home") + File.separator + "lib";
    }
    
    static {
        LOGGER = Logger.getLogger(ServerSettingsImpl.class.getName());
    }
}
