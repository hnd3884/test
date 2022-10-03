package com.me.mdm.onpremise.server.admin.task;

import java.util.Map;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.util.Date;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMOnpremiseGlobalTask implements SchedulerExecutionInterface
{
    private final Logger logger;
    
    public MDMOnpremiseGlobalTask() {
        this.logger = Logger.getLogger("MDMOnpremiseGlobalTask");
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            this.updateSSLCertificateMessageStatus();
            this.hideOrUnHideMailServerMessage();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void setSSLCertificateExpiryMsgStatus() throws Exception {
        X509Certificate x509Certificate = null;
        Long noOfDays = 0L;
        Date todaysDate = new Date();
        try {
            if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                x509Certificate = CertificateUtils.loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
                Date expiryDate = x509Certificate.getNotAfter();
                final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                expiryDate = sdf.parse(sdf.format(expiryDate));
                todaysDate = sdf.parse(sdf.format(todaysDate));
                noOfDays = expiryDate.getTime() - todaysDate.getTime();
                noOfDays /= 86400000L;
                if (noOfDays <= 30L && noOfDays >= 0L) {
                    MessageProvider.getInstance().unhideMessage("SSL_CERTIFICATE_EXPIRY_MSG", (Long)null);
                    MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRED", (Long)null);
                }
                else if (noOfDays < 0L) {
                    MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRY_MSG", (Long)null);
                    MessageProvider.getInstance().unhideMessage("SSL_CERTIFICATE_EXPIRED", (Long)null);
                }
            }
        }
        catch (final CertificateExpiredException ex) {
            this.logger.log(Level.WARNING, "Exception in certificte Expiry date .So the default Expiry msg opened...", ex);
            MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRY_MSG", (Long)null);
            MessageProvider.getInstance().unhideMessage("SSL_CERTIFICATE_EXPIRED", (Long)null);
        }
    }
    
    private void hideOrUnHideMailServerMessage() {
        try {
            final boolean isMailServerConfigured = ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
            if (isMailServerConfigured) {
                MessageProvider.getInstance().hideMessage("MAIL_SERVER_NOT_CONFIGURED");
            }
            else {
                MessageProvider.getInstance().unhideMessage("MAIL_SERVER_NOT_CONFIGURED");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in hideOrUnHideMailServerMessage() ", e);
        }
    }
    
    public void updateSSLCertificateMessageStatus() throws Exception {
        if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
            this.updateServerCertificateStatus();
            this.updateIntermediateCertificateStatus();
        }
        this.updateSSLcertificateValidityMsgStatus();
    }
    
    private void updateServerCertificateStatus() throws Exception {
        X509Certificate x509Certificate = null;
        Long noOfDays = 0L;
        try {
            x509Certificate = CertificateUtils.loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
            noOfDays = this.getCertificateExpiryDate(x509Certificate);
            if (noOfDays <= 30L && noOfDays >= 0L) {
                MessageProvider.getInstance().unhideMessage("SSL_CERTIFICATE_EXPIRY_MSG", (Long)null);
                MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRED", (Long)null);
            }
            else if (noOfDays < 0L) {
                MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRY_MSG", (Long)null);
                MessageProvider.getInstance().unhideMessage("SSL_CERTIFICATE_EXPIRED", (Long)null);
            }
        }
        catch (final CertificateExpiredException ex) {
            this.logger.log(Level.WARNING, "Exception in certificte Expiry date .So the default Expiry msg opened...", ex);
            MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRY_MSG", (Long)null);
            MessageProvider.getInstance().unhideMessage("SSL_CERTIFICATE_EXPIRED", (Long)null);
        }
    }
    
    private void updateIntermediateCertificateStatus() throws Exception {
        MessageProvider.getInstance().hideMessage("SSL_INTERMEDIATE_CERTIFICATE_EXPIRED", (Long)null);
        X509Certificate x509Certificate = null;
        Long noOfDays = 0L;
        try {
            final String intermediatePath = SSLCertificateUtil.getInstance().getIntermediateCertificateFilePath();
            if (intermediatePath != null && ApiFactoryProvider.getFileAccessAPI().isFileExists(intermediatePath)) {
                x509Certificate = CertificateUtils.loadX509CertificateFromFile(new File(intermediatePath));
                noOfDays = this.getCertificateExpiryDate(x509Certificate);
                if (noOfDays < 0L) {
                    MessageProvider.getInstance().hideMessage("SSL_INTERMEDIATE_CERTIFICATE_EXPIRED", (Long)null);
                }
            }
        }
        catch (final CertificateExpiredException ex) {
            this.logger.log(Level.WARNING, "Exception in certificte Expiry date .So the default Expiry msg opened...", ex);
            MessageProvider.getInstance().unhideMessage("SSL_INTERMEDIATE_CERTIFICATE_EXPIRED", (Long)null);
        }
    }
    
    private void updateSSLcertificateValidityMsgStatus() throws Exception {
        final Long july2019inMills = 1561919400000L;
        final Long september2020inMills = 1598898600000L;
        final Long Millsof824Days = 71452800000L;
        final Long Millsof397Days = 34300800000L;
        X509Certificate x509Certificate = null;
        x509Certificate = CertificateUtils.loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
        final String signatureAlgorithm = x509Certificate.getSigAlgName();
        MessageProvider.getInstance().hideMessage("SSL_SHA1_CERTIFICATE_USAGE", (Long)null);
        MessageProvider.getInstance().hideMessage("SSL_VALIDATION_MSG", (Long)null);
        final Map certificateInfo = SSLCertificateUtil.getInstance().getCertificateDetails(SSLCertificateUtil.getInstance().getServerCertificateFilePath());
        if (signatureAlgorithm != null && signatureAlgorithm.contains("SHA1")) {
            MessageProvider.getInstance().unhideMessage("SSL_SHA1_CERTIFICATE_USAGE", (Long)null);
        }
        final Long certCreationDate = certificateInfo.get("CreationDateInMillis");
        final Long certExpiryDate = certificateInfo.get("ExpiryDateInMillis");
        if (certCreationDate > july2019inMills && certCreationDate < september2020inMills && certCreationDate + Millsof824Days < certExpiryDate) {
            MessageProvider.getInstance().unhideMessage("SSL_VALIDATION_MSG", (Long)null);
        }
        else if (certCreationDate > september2020inMills && certCreationDate + Millsof397Days < certExpiryDate) {
            MessageProvider.getInstance().unhideMessage("SSL_VALIDATION_MSG", (Long)null);
        }
    }
    
    private Long getCertificateExpiryDate(final X509Certificate x509Certificate) throws Exception {
        Long noOfDays = 0L;
        Date todaysDate = new Date();
        Date expiryDate = x509Certificate.getNotAfter();
        final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        expiryDate = sdf.parse(sdf.format(expiryDate));
        todaysDate = sdf.parse(sdf.format(todaysDate));
        noOfDays = expiryDate.getTime() - todaysDate.getTime();
        noOfDays /= 86400000L;
        return noOfDays;
    }
}
