package com.me.mdm.onpremise.server.settings.proxy;

import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import javax.net.ssl.SSLContext;
import java.util.List;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.logging.Logger;

public class ProxyValidator
{
    private static final Logger LOGGER;
    public static final String MSG_NAME = "PROXY_CERT_MISSING";
    public static final String CERT_NAME = "MDM_PROXY_CERT_NAME";
    
    public static void validate() {
        final List<String> domains = getValidatedDomains();
        SSLContext sslContext = null;
        if (DownloadManager.proxyType != 0 && DownloadManager.proxyType != 3) {
            for (final String domain : domains) {
                try {
                    if (sslContext == null) {
                        sslContext = getSSLContext();
                    }
                    new ProxyClient(sslContext).connect(domain);
                }
                catch (final Exception exception) {
                    final Throwable cause = exception.getCause();
                    if (cause instanceof ProxyCertificateException) {
                        final ProxyCertificateException ex = (ProxyCertificateException)cause;
                        final X509Certificate[] certChain = ex.getCertChain();
                        final X509Certificate[] trustedIssuers = ex.getTrustedIssuers();
                        final String missingIssuer = getMissingIssuerName(certChain, trustedIssuers);
                        if (missingIssuer != null) {
                            SyMUtil.updateSyMParameter("MDM_PROXY_CERT_NAME", missingIssuer);
                            MessageProvider.getInstance().unhideMessage("PROXY_CERT_MISSING");
                            return;
                        }
                    }
                    ProxyValidator.LOGGER.log(Level.WARNING, "Error while checking for proxy certificate: ", exception);
                }
            }
        }
        MessageProvider.getInstance().hideMessage("PROXY_CERT_MISSING");
    }
    
    private static List<String> getValidatedDomains() {
        final List<String> validatedDomains = new ArrayList<String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCDomainExceptionList"));
            selectQuery.addSelectColumn(Column.getColumn("DCDomainExceptionList", "*"));
            selectQuery.setDistinct(true);
            final Criteria mdmDomains = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)21, 0);
            final Criteria domainAccessSuccess = new Criteria(Column.getColumn("DCDomainExceptionList", "STATUS"), (Object)1, 0);
            selectQuery.setCriteria(mdmDomains.and(domainAccessSuccess));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("DCDomainExceptionList");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    validatedDomains.add((String)row.get("URLDOMAIN"));
                }
            }
        }
        catch (final Exception exception) {
            ProxyValidator.LOGGER.log(Level.WARNING, "Exception while getting validated MDM domains: ", exception);
        }
        return validatedDomains;
    }
    
    private static String getMissingIssuerName(final X509Certificate[] certChain, final X509Certificate[] trustedIssuers) {
        if (certChain != null && trustedIssuers != null && certChain.length > 0) {
            final X509Certificate child = certChain[certChain.length - 1];
            for (final X509Certificate parent : trustedIssuers) {
                if (child.getIssuerX500Principal().getName().equalsIgnoreCase(parent.getSubjectX500Principal().getName())) {
                    try {
                        child.verify(parent.getPublicKey());
                        return null;
                    }
                    catch (final Exception exception) {
                        ProxyValidator.LOGGER.log(Level.WARNING, "Error while verifying child certificate: ", exception);
                    }
                }
            }
            return child.getIssuerX500Principal().getName();
        }
        return null;
    }
    
    private static SSLContext getSSLContext() throws Exception {
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore)null);
        final TrustManager[] tms = tmf.getTrustManagers();
        if (tms != null) {
            for (int i = 0; i < tms.length; ++i) {
                if (tms[i] instanceof X509TrustManager) {
                    tms[i] = new TrustManagerDelegate((X509TrustManager)tms[i]);
                }
            }
        }
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tms, null);
        return sslContext;
    }
    
    static {
        LOGGER = Logger.getLogger(ProxyValidator.class.getName());
    }
}
