package com.me.devicemanagement.framework.server.mailmanager;

import java.util.Hashtable;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.security.cert.Certificate;
import java.security.MessageDigest;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import java.util.Map;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.io.FileInputStream;
import java.util.Properties;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.logging.Logger;

public class MailerUtils
{
    private static MailerUtils mailerUtils;
    private static Logger logger;
    private static final String STORE_PASS_WORD = "changeit";
    private static final String SMTPS_CERTIFICATE_FILE_NAME = "dc-smtps-cacerts";
    public static final String SMTP_TEST_MAIL_SUB_KEY = "dm.mail.test.sub";
    public static final String SMTP_TEST_MAIL_DESCR = "SMTP Mail server settings test mail";
    private static final char[] HEXDIGITS;
    
    private MailerUtils() {
    }
    
    public String getTestMailSub() {
        String subject = "Test Mail for SMTP Configuration ";
        try {
            subject = I18N.getMsg("dm.mail.test.sub", new Object[0]);
        }
        catch (final Exception exp) {
            MailerUtils.logger.log(Level.SEVERE, "Exception while obtaining I18N value for test mail subject key {0}", exp);
        }
        return subject;
    }
    
    public String getCerticateFile() {
        final char SEP = File.separatorChar;
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String certificatePath = serverHome + SEP + "conf" + File.separator + "dc-smtps-cacerts";
        MailerUtils.logger.log(Level.INFO, "Path to the certificate refered is : " + certificatePath);
        return certificatePath;
    }
    
    public String getDefaultCerticateFile() {
        final char SEP = File.separatorChar;
        String serverHome = System.getProperty("server.home");
        String certificatePath = serverHome + SEP + "jre" + SEP + "lib" + SEP + "security" + File.separator + "cacerts";
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            serverHome = System.getProperty("java.home");
            certificatePath = serverHome + SEP + "lib" + SEP + "security" + File.separator + "cacerts";
        }
        MailerUtils.logger.log(Level.INFO, "Path to the certificate refered is : " + certificatePath);
        return certificatePath;
    }
    
    public static MailerUtils getInstance() {
        if (MailerUtils.mailerUtils == null) {
            MailerUtils.mailerUtils = new MailerUtils();
        }
        return MailerUtils.mailerUtils;
    }
    
    private void validateAndDownloadSMTPSKey(final Properties serverProperties) throws Exception {
        boolean status = true;
        final String host = ((Hashtable<K, String>)serverProperties).get("host");
        final int port = ((Hashtable<K, Integer>)serverProperties).get("smtpPort");
        final String stroePassword = ((Hashtable<K, String>)serverProperties).get("stroePassword");
        final char[] passphrase = stroePassword.toCharArray();
        final String certificateFilePath = this.getCerticateFile();
        InputStream in = null;
        final boolean isCertificateExist = ApiFactoryProvider.getFileAccessAPI().isFileExists(certificateFilePath);
        if (isCertificateExist) {
            MailerUtils.logger.log(Level.INFO, "Certificate file already exists");
            in = ApiFactoryProvider.getFileAccessAPI().readFile(certificateFilePath);
        }
        else {
            MailerUtils.logger.log(Level.INFO, "Certificate file is not available. Referring default cacerts");
            in = new FileInputStream(new File(this.getDefaultCerticateFile()));
        }
        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase);
        in.close();
        final SSLContext context = SSLContext.getInstance("TLS");
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        final X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
        final SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[] { tm }, null);
        final SSLSocketFactory factory = context.getSocketFactory();
        final Properties propsForlogPrint = new Properties();
        propsForlogPrint.putAll(serverProperties);
        propsForlogPrint.remove("stroePassword");
        MailerUtils.logger.log(Level.WARNING, "Opening connection to properties : {0} ", propsForlogPrint);
        final SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
        socket.setSoTimeout(10000);
        try {
            MailerUtils.logger.log(Level.WARNING, "Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            MailerUtils.logger.log(Level.WARNING, "No errors, certificate is already trusted");
        }
        catch (final SSLException e) {
            MailerUtils.logger.log(Level.WARNING, "---------------------------------------------------");
            MailerUtils.logger.log(Level.WARNING, "certificate Not Found trusted  " + System.out, e);
            MailerUtils.logger.log(Level.WARNING, "---------------------------------------------------");
            status = false;
        }
        MailerUtils.logger.log(Level.WARNING, "IS certificate  Found  ...   " + status);
        final X509Certificate[] chain = tm.chain;
        if (chain == null) {
            MailerUtils.logger.log(Level.WARNING, "---------------------------------------------------");
            MailerUtils.logger.log(Level.WARNING, "Could not obtain server certificate chain");
            MailerUtils.logger.log(Level.WARNING, "---------------------------------------------------");
            return;
        }
        MailerUtils.logger.log(Level.WARNING, "Server sent " + chain.length + " certificate(s):");
        final MessageDigest sha1 = MessageDigest.getInstance(ChecksumProvider.getSecurityAlgorithm2());
        final MessageDigest md5 = MessageDigest.getInstance(ChecksumProvider.getSecurityAlgorithm1());
        for (int i = 0; i < chain.length; ++i) {
            final X509Certificate cert = chain[i];
            MailerUtils.logger.log(Level.WARNING, " " + (i + 1) + " Subject " + cert.getSubjectDN());
            MailerUtils.logger.log(Level.WARNING, "   Issuer  " + cert.getIssuerDN());
            sha1.update(cert.getEncoded());
            MailerUtils.logger.log(Level.WARNING, "   sha1    " + toHexString(sha1.digest()));
            md5.update(cert.getEncoded());
            MailerUtils.logger.log(Level.WARNING, "   md5     " + toHexString(md5.digest()));
        }
        if (!status) {
            MailerUtils.logger.log(Level.WARNING, "Going to write Certificate File to File  {0} ", certificateFilePath);
            final X509Certificate cert2 = chain[0];
            final String alias = host + "-" + 1;
            ks.setCertificateEntry(alias, cert2);
            final File of = new File(this.getCerticateFile());
            final OutputStream out = new FileOutputStream(of);
            ks.store(out, passphrase);
            out.close();
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                this.writeCertificateforSas();
            }
            MailerUtils.logger.log(Level.WARNING, "" + cert2);
            MailerUtils.logger.log(Level.WARNING, "Added certificate to keystore " + certificateFilePath + " using alias '" + alias + "'");
        }
    }
    
    private void writeCertificateforSas() {
        final String certificateFilePath = this.getCerticateFile();
        InputStream in = null;
        try {
            final File cerFile = new File(certificateFilePath);
            in = new FileInputStream(cerFile);
            MailerUtils.logger.log(Level.INFO, "Certificate file already exists");
            ApiFactoryProvider.getFileAccessAPI().writeFile(certificateFilePath, in);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            if (in != null) {
                try {
                    in.close();
                }
                catch (final Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final Exception exp2) {
                    exp2.printStackTrace();
                }
            }
        }
        try {
            new File(certificateFilePath).deleteOnExit();
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public SSLSocketFactory getSSLSocketFactory() throws Exception {
        InputStream in = null;
        final String certificateFilePath = this.getCerticateFile();
        final boolean isCertificateExist = ApiFactoryProvider.getFileAccessAPI().isFileExists(certificateFilePath);
        if (isCertificateExist) {
            MailerUtils.logger.log(Level.INFO, "Certificate file already exists");
            in = ApiFactoryProvider.getFileAccessAPI().readFile(certificateFilePath);
        }
        else {
            MailerUtils.logger.log(Level.INFO, "Certificate file is not available. Referring default cacerts");
            in = new FileInputStream(new File(this.getDefaultCerticateFile()));
        }
        final char[] passphrase = "changeit".toCharArray();
        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase);
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, passphrase);
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        final TrustManager[] trustManager = tmf.getTrustManagers();
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), trustManager, null);
        final SSLSocketFactory factory = context.getSocketFactory();
        return factory;
    }
    
    private static String toHexString(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xFF;
            sb.append(MailerUtils.HEXDIGITS[b >> 4]);
            sb.append(MailerUtils.HEXDIGITS[b & 0xF]);
            sb.append(' ');
        }
        return sb.toString();
    }
    
    public void validateSMTPSSSLCertificate(final boolean isSmtpsEnabled, final String smtpServer, final String smtpPort) {
        if (isSmtpsEnabled) {
            MailerUtils.logger.log(Level.INFO, "Validating SMTPS SSL Certificate ...");
            final Properties trustProps = new Properties();
            ((Hashtable<String, String>)trustProps).put("host", smtpServer);
            ((Hashtable<String, Integer>)trustProps).put("smtpPort", Integer.parseInt(smtpPort));
            ((Hashtable<String, String>)trustProps).put("stroePassword", "changeit");
            try {
                this.validateAndDownloadSMTPSKey(trustProps);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public Properties getMailServerProperties(final String smtpServer, final String smtpPort, final String smtpUserName, final String smtpPassword, final boolean isTlsEnabled, final boolean isSmtpsEnabled, final String senderName, final String senderAddress) throws Exception {
        final Properties mailProps = this.gerMailServerPorperties(smtpServer, smtpPort, smtpUserName, smtpPassword, isTlsEnabled, isSmtpsEnabled);
        if (senderName != null) {
            mailProps.setProperty("mail.fromName", senderName);
        }
        if (senderAddress != null) {
            mailProps.setProperty("mail.fromAddress", senderAddress);
        }
        return mailProps;
    }
    
    public Properties gerMailServerPorperties(final String smtpServer, final String smtpPort, final String smtpUserName, final String smtpPassword, final boolean isTlsEnabled, final boolean isSmtpsEnabled) {
        final Properties mailProps = new Properties();
        ((Hashtable<String, String>)mailProps).put("mail.smtp.host", smtpServer);
        ((Hashtable<String, String>)mailProps).put("mail.smtp.port", smtpPort);
        if (smtpUserName != null) {
            ((Hashtable<String, String>)mailProps).put("mail.smtp.auth", "true");
            ((Hashtable<String, String>)mailProps).put("mail.smtp.user", smtpUserName);
            ((Hashtable<String, String>)mailProps).put("mail.smtp.password", smtpPassword);
            MailerUtils.logger.log(Level.INFO, "Mail Server Authentication is enabled. UserName : " + smtpUserName);
        }
        else {
            ((Hashtable<String, String>)mailProps).put("mail.smtp.auth", "false");
        }
        if (isTlsEnabled) {
            ((Hashtable<String, String>)mailProps).put("mail.smtp.starttls.enable", "true");
            ((Hashtable<String, String>)mailProps).put("mail.smtp.ssl.trust", smtpServer);
        }
        else {
            ((Hashtable<String, String>)mailProps).put("mail.smtp.starttls.enable", "false");
        }
        if (isSmtpsEnabled) {
            ((Hashtable<String, String>)mailProps).put("mail.transport.protocol", "smtps");
            mailProps.setProperty("mail.smtp.socketFactory.class", "com.me.devicemanagement.framework.server.mailmanager.DCSSLSocketFactory");
            mailProps.setProperty("mail.smtp.socketFactory.port", String.valueOf(smtpPort));
            mailProps.setProperty("mail.smtp.socketFactory.fallback", "false");
            final boolean isCertificateExist = ApiFactoryProvider.getFileAccessAPI().isFileExists(this.getCerticateFile());
            String keyStorePath;
            if (isCertificateExist) {
                MailerUtils.logger.log(Level.INFO, "Certificate file already exists");
                keyStorePath = this.getCerticateFile();
            }
            else {
                MailerUtils.logger.log(Level.INFO, "Certificate file is not available. Referring default cacerts");
                keyStorePath = this.getDefaultCerticateFile();
            }
            mailProps.setProperty("javax.net.ssl.trustStore", keyStorePath);
            mailProps.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        }
        else {
            ((Hashtable<String, String>)mailProps).put("mail.transport.protocol", "smtp");
        }
        ((Hashtable<String, Boolean>)mailProps).put("mail.smtp.isSMTPSEnabled", isSmtpsEnabled);
        ((Hashtable<String, Integer>)mailProps).put("mail.smtp.connectiontimeout", 60000);
        ((Hashtable<String, Integer>)mailProps).put("mail.smtp.timeout", 60000);
        return mailProps;
    }
    
    static {
        MailerUtils.mailerUtils = null;
        MailerUtils.logger = Logger.getLogger(MailerUtils.class.getName());
        HEXDIGITS = "0123456789abcdef".toCharArray();
    }
    
    private static class SavingTrustManager implements X509TrustManager
    {
        private final X509TrustManager tm;
        private X509Certificate[] chain;
        
        SavingTrustManager(final X509TrustManager tm) {
            this.tm = tm;
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            this.chain = chain;
            this.tm.checkServerTrusted(chain, authType);
        }
    }
}
