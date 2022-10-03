package com.me.devicemanagement.framework.server.certificate;

import java.util.Hashtable;
import java.security.cert.CertificateEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.cert.CertificateExpiredException;
import com.me.devicemanagement.framework.server.certificate.verifier.CertificateVerificationException;
import java.security.cert.CertPathBuilderException;
import com.me.devicemanagement.framework.server.certificate.verifier.CertificateVerifier;
import java.security.cert.Certificate;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.WildCardStringMatcher;
import java.io.OutputStream;
import java.util.UUID;
import java.io.FileOutputStream;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.Principal;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.logging.Level;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class SSLCertificateUtil
{
    private static final Logger LOGGER;
    private static SSLCertificateUtil sslCertificateUtil;
    private String isThirdPartySSLInstalled;
    private String serverCertFile;
    private String serverKeyFile;
    private String intermediateCertFile;
    private String selfSignedServerCaCertFile;
    private String selfSignedServerCaKeyFile;
    public static String webServerName;
    private static String apache;
    private static String nginx;
    private static String websettingsconf;
    private static String webservernameprops;
    
    protected SSLCertificateUtil() {
        this.isThirdPartySSLInstalled = null;
        this.serverCertFile = null;
        this.serverKeyFile = null;
        this.intermediateCertFile = null;
        this.selfSignedServerCaCertFile = null;
        this.selfSignedServerCaKeyFile = null;
    }
    
    public static SSLCertificateUtil getInstance() {
        if (SSLCertificateUtil.sslCertificateUtil == null) {
            SSLCertificateUtil.sslCertificateUtil = new SSLCertificateUtil();
        }
        return SSLCertificateUtil.sslCertificateUtil;
    }
    
    public static void resetInstance() {
        SSLCertificateUtil.sslCertificateUtil = null;
    }
    
    public boolean isThirdPartySSLInstalled() throws Exception {
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            this.isThirdPartySSLInstalled = "true";
        }
        else if (this.isThirdPartySSLInstalled == null) {
            final String certificateFilePath = this.getServerCertificateFilePath();
            final Map certificateInfo = this.getCertificateDetails(certificateFilePath);
            if (certificateInfo.isEmpty()) {
                this.isThirdPartySSLInstalled = "true";
            }
            else {
                final String sOrganizationName = certificateInfo.get("OrganizationName");
                final String sOrganizationalUnit = certificateInfo.get("OrganizationalUnit");
                if ("Zoho".equalsIgnoreCase(sOrganizationName) && "ManageEngine".equalsIgnoreCase(sOrganizationalUnit)) {
                    this.isThirdPartySSLInstalled = "false";
                }
                else {
                    this.isThirdPartySSLInstalled = "true";
                }
            }
        }
        return Boolean.valueOf(this.isThirdPartySSLInstalled);
    }
    
    public String getSSLCertificateOrganizationName() throws Exception {
        final String certificateFilePath = this.getServerCertificateFilePath();
        final Map certificateInfo = this.getCertificateDetails(certificateFilePath);
        final String organizationName = certificateInfo.get("IssuerOrganizationName");
        return organizationName;
    }
    
    public String getSSLCertificateHostName() throws Exception {
        final String certificateFilePath = this.getServerCertificateFilePath();
        final Map certificateInfo = this.getCertificateDetails(certificateFilePath);
        final String certificateHostName = certificateInfo.get("CertificateName");
        return certificateHostName;
    }
    
    public Set<String> getSSLCertificateHostNames(final String certificateFilePath) throws Exception {
        final Set<String> hostNameList = new HashSet<String>();
        final Map certificateInfo = this.getCertificateDetails(certificateFilePath);
        return this.getSSLCertificateHostNamesFromCertDetails(certificateInfo);
    }
    
    public Set<String> getSSLCertificateHostNames(final X509Certificate certificate) throws Exception {
        final Set<String> hostNameList = new HashSet<String>();
        final Map certificateInfo = this.getCertificateDetails(certificate);
        Logger.getLogger(SSLCertificateUtil.class.getName()).log(Level.INFO, "MAP " + certificateInfo);
        return this.getSSLCertificateHostNamesFromCertDetails(certificateInfo);
    }
    
    public Set<String> getSSLCertificateHostNamesFromCertDetails(final Map certificateInfo) {
        final Set<String> hostNameList = new HashSet<String>();
        final String certificateName = certificateInfo.get("CertificateName");
        if (null != certificateName) {
            hostNameList.add(certificateName);
        }
        final Collection<List<?>> subjectAlternativeList = certificateInfo.get("SubjectAlternativeName");
        if (subjectAlternativeList != null && !subjectAlternativeList.isEmpty()) {
            for (final List nameList : subjectAlternativeList) {
                final Integer generalName = nameList.get(0);
                if (generalName == 2 || generalName == 7) {
                    hostNameList.add(nameList.get(1));
                }
            }
        }
        return hostNameList;
    }
    
    public Set<String> getSSLCertificateHostNames() throws Exception {
        Set<String> hostNameList = null;
        final String certificateFilePath = this.getServerCertificateFilePath();
        hostNameList = this.getSSLCertificateHostNames(certificateFilePath);
        return hostNameList;
    }
    
    public String getWebServerName() throws Exception {
        if (SSLCertificateUtil.webServerName != null && !SSLCertificateUtil.webServerName.equalsIgnoreCase("")) {
            return SSLCertificateUtil.webServerName;
        }
        final String serverHome = System.getProperty("server.home");
        final File webSettingsConfFile = new File(serverHome + File.separator + "conf" + File.separator + SSLCertificateUtil.websettingsconf);
        final Properties properties = new Properties();
        properties.load(new FileInputStream(webSettingsConfFile));
        return properties.getProperty(SSLCertificateUtil.webservernameprops);
    }
    
    public static void setWebServerName(final String webServer) {
        if (webServer != null) {
            SSLCertificateUtil.webServerName = webServer;
        }
    }
    
    public String getServerCertificateFilePath() throws Exception {
        if (this.serverCertFile == null) {
            if (CustomerInfoUtil.isSAS) {
                this.serverCertFile = this.getFilePathFromHttpdSSL("SSLCertificateFile");
            }
            else if (this.serverCertFile == null || !this.serverCertFile.contains(this.getWebServerName())) {
                SSLCertificateUtil.webServerName = this.getWebServerName();
                if (SSLCertificateUtil.apache.equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
                    this.serverCertFile = this.getFilePathFromHttpdSSL("SSLCertificateFile");
                }
                else if (SSLCertificateUtil.nginx.equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
                    try {
                        this.serverCertFile = this.getFilePathFromNginxConf("ssl_certificate");
                    }
                    catch (final FileNotFoundException ex) {
                        this.serverCertFile = this.getFilePathFromHttpdSSL("SSLCertificateFile");
                    }
                }
            }
        }
        return this.serverCertFile;
    }
    
    public String getServerPrivateKeyFilePath() throws Exception {
        if (this.serverKeyFile == null) {
            if (CustomerInfoUtil.isSAS) {
                this.serverKeyFile = this.getFilePathFromHttpdSSL("SSLCertificateKeyFile");
            }
            else if (this.serverKeyFile == null || !this.serverKeyFile.contains(this.getWebServerName())) {
                SSLCertificateUtil.webServerName = this.getWebServerName();
                if (SSLCertificateUtil.webServerName.equalsIgnoreCase(SSLCertificateUtil.apache)) {
                    this.serverKeyFile = this.getFilePathFromHttpdSSL("SSLCertificateKeyFile");
                }
                else if (SSLCertificateUtil.webServerName.equalsIgnoreCase(SSLCertificateUtil.nginx)) {
                    this.serverKeyFile = this.getFilePathFromNginxConf("ssl_certificate_key");
                }
            }
        }
        return this.serverKeyFile;
    }
    
    public String getIntermediateCertificateFilePath() throws Exception {
        if (this.intermediateCertFile == null) {
            if (CustomerInfoUtil.isSAS) {
                this.intermediateCertFile = this.getFilePathFromHttpdSSL("SSLCertificateChainFile");
                if (this.intermediateCertFile == null) {
                    this.intermediateCertFile = this.getFilePathFromHttpdSSL("SSLCACertificateFile");
                }
            }
            else {
                SSLCertificateUtil.webServerName = this.getWebServerName();
                if (SSLCertificateUtil.webServerName.equalsIgnoreCase(SSLCertificateUtil.apache)) {
                    this.intermediateCertFile = this.getFilePathFromHttpdSSL("SSLCertificateChainFile");
                    if (this.intermediateCertFile == null) {
                        this.intermediateCertFile = this.getFilePathFromHttpdSSL("SSLCACertificateFile");
                    }
                }
                else {
                    final String intermediateCert = this.getWebServerSettings().getProperty("apache.ssl.intermediate.ca.file");
                    if (null != intermediateCert && !"".equals(intermediateCert)) {
                        this.intermediateCertFile = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + this.getWebServerName() + File.separator + "conf" + File.separator + intermediateCert;
                    }
                }
            }
        }
        return this.intermediateCertFile;
    }
    
    public String getServerCACertificateFilePath() throws Exception {
        if (this.selfSignedServerCaCertFile == null) {
            if (CustomerInfoUtil.isSAS) {
                this.selfSignedServerCaCertFile = this.getFilePathFromHttpdSSL("SSLCACertificateFile");
            }
            else {
                final String rootCert = this.getWebServerSettings().getProperty("apache.ssl.root.ca.file");
                if (null != rootCert && !"".equals(rootCert)) {
                    this.selfSignedServerCaCertFile = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + this.getWebServerName() + File.separator + "conf" + File.separator + rootCert;
                }
            }
        }
        return this.selfSignedServerCaCertFile;
    }
    
    public Properties getWebServerSettings() throws Exception {
        final String serverHome = SyMUtil.getInstallationDir();
        final String webServerConfDir = serverHome + File.separator + "conf" + File.separator + "websettings.conf";
        final Properties wsProps = FileAccessUtil.readProperties(webServerConfDir);
        if (null != wsProps) {
            return wsProps;
        }
        return null;
    }
    
    public String getServerCAPrivateKeyFilePath() throws Exception {
        if (this.selfSignedServerCaKeyFile == null) {
            final String server_home = SyMUtil.getInstallationDir();
            this.selfSignedServerCaKeyFile = server_home + File.separator + "apache" + File.separator + "conf" + File.separator + "caCert.key";
        }
        return this.selfSignedServerCaKeyFile;
    }
    
    private String getFilePathFromNginxConf(final String attribute) throws Exception {
        final String server_home = SyMUtil.getInstallationDir();
        final String serverHttpSSLFilePath = server_home + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator + "nginx-ssl.conf";
        FileInputStream fis = null;
        try {
            final Properties properties = new Properties();
            fis = new FileInputStream(serverHttpSSLFilePath);
            properties.load(fis);
            final String fileName = server_home + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator + properties.getProperty(attribute);
            return fileName.trim().substring(0, fileName.length() - 1);
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    private String getFilePathFromHttpdSSL(final String attribute) throws Exception {
        final String server_home = SyMUtil.getInstallationDir();
        String serverHttpSSLFilePath = server_home + File.separator + "apache" + File.separator + "conf" + File.separator + "httpd-ssl.conf";
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            serverHttpSSLFilePath = server_home + File.separator + "conf" + File.separator + "httpd-ssl.conf";
        }
        String filePath = null;
        FileInputStream fis = null;
        final Properties props = new Properties();
        try {
            fis = new FileInputStream(new File(serverHttpSSLFilePath));
            props.load(fis);
            filePath = ((Hashtable<K, String>)props).get(attribute);
            if (filePath != null) {
                filePath = filePath.replaceAll("^\"|\"$", "");
            }
            if (filePath != null) {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    filePath = SyMUtil.getInstallationDir() + File.separator + filePath;
                }
            }
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        return filePath;
    }
    
    public Map getCertificateDetails(final X509Certificate cert) throws CertificateParsingException {
        final Map certificateDetails = new HashMap();
        final Date notAfter = cert.getNotAfter();
        final Date notBefore = cert.getNotBefore();
        final Principal subjectPrincipal = cert.getSubjectDN();
        final String subjectName = subjectPrincipal.getName();
        final Collection<List<?>> subjectAlternativeList = cert.getSubjectAlternativeNames();
        if (subjectName != null) {
            final StringTokenizer tokenizer = new StringTokenizer(subjectName, ", ");
            while (tokenizer.hasMoreElements()) {
                final String token = (String)tokenizer.nextElement();
                final String[] strArray = token.split("=");
                if (token.startsWith("CN=")) {
                    if (null == strArray[1] || "".equals(strArray[1])) {
                        continue;
                    }
                    certificateDetails.put("CertificateName", strArray[1]);
                }
                else if (token.startsWith("O=")) {
                    certificateDetails.put("OrganizationName", strArray[1]);
                }
                else {
                    if (!token.startsWith("OU=")) {
                        continue;
                    }
                    certificateDetails.put("OrganizationalUnit", strArray[1]);
                }
            }
        }
        certificateDetails.put("CreationDateInMillis", notBefore.getTime());
        certificateDetails.put("CreationDate", notBefore.toString());
        certificateDetails.put("ExpiryDateInMillis", notAfter.getTime());
        certificateDetails.put("ExpiryDate", notAfter.toString());
        certificateDetails.put("SubjectAlternativeName", subjectAlternativeList);
        certificateDetails.putAll(this.getCertificateIssuerDetails(cert));
        return certificateDetails;
    }
    
    public Map getCertificateIssuerDetails(final X509Certificate cert) throws CertificateParsingException {
        final Map certificateIssuerDetails = new HashMap();
        final Principal issuerPrincipal = cert.getIssuerDN();
        final String issuerName = issuerPrincipal.getName();
        if (issuerName != null) {
            final StringTokenizer tokenizer = new StringTokenizer(issuerName, ",");
            while (tokenizer.hasMoreElements()) {
                String token = (String)tokenizer.nextElement();
                token = token.trim();
                final String[] strArray = token.split("=");
                if (token.startsWith("CN=")) {
                    certificateIssuerDetails.put("IssuerCertificateName", strArray[1]);
                }
                else if (token.startsWith("O=")) {
                    certificateIssuerDetails.put("IssuerOrganizationName", strArray[1]);
                }
                else {
                    if (!token.startsWith("OU=")) {
                        continue;
                    }
                    certificateIssuerDetails.put("IssuerOrganizationalUnit", strArray[1]);
                }
            }
        }
        return certificateIssuerDetails;
    }
    
    public Map getCertificateDetails(final String certificateFilePath) {
        Map certificateDetails = new HashMap();
        X509Certificate cert = null;
        final FileInputStream fis = null;
        try {
            if (certificateFilePath != null) {
                try {
                    cert = getCertificate(certificateFilePath);
                }
                catch (final CertificateParsingException exp) {
                    this.rewriteSSLCertificate(certificateFilePath);
                    this.checkUnWantedSpaceinServerCertificate(certificateFilePath);
                    cert = getCertificate(certificateFilePath);
                }
                catch (final CertificateException ex) {
                    this.rewriteSSLCertificate(certificateFilePath);
                    this.checkUnWantedSpaceinServerCertificate(certificateFilePath);
                    cert = getCertificate(certificateFilePath);
                }
                certificateDetails = this.getCertificateDetails(cert);
            }
        }
        catch (final Exception exp2) {
            SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception while executing getCertificateDetails method... {0}", exp2);
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final IOException ex2) {
                    SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception while closing FileInputStream... {0}", ex2);
                }
            }
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final IOException ex3) {
                    SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception while closing FileInputStream... {0}", ex3);
                }
            }
        }
        return certificateDetails;
    }
    
    private static byte[] readFileContentAsArray(final String fileName) throws Exception {
        FileInputStream fis = null;
        byte[] content = null;
        try {
            if (fileName != null && !fileName.isEmpty() && new File(fileName).exists()) {
                fis = new FileInputStream(fileName);
                content = new byte[fis.available()];
                fis.read(content);
            }
        }
        catch (final IOException e) {
            SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception occurred while reading file", e);
            throw e;
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception e2) {
                    SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                }
            }
        }
        return content;
    }
    
    public static X509Certificate getCertificate(final String certFilePath) throws CertificateParsingException, CertificateException {
        ByteArrayInputStream bais = null;
        X509Certificate certificate = null;
        try {
            final byte[] value = readFileContentAsArray(certFilePath);
            if (value != null) {
                bais = new ByteArrayInputStream(value);
                final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                certificate = (X509Certificate)certFactory.generateCertificate(bais);
            }
        }
        catch (final CertificateParsingException exp) {
            throw exp;
        }
        catch (final CertificateException exp2) {
            throw exp2;
        }
        catch (final Exception exp3) {
            SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception in BouncyCastlePayloadSigning:getCertificate : {0}", exp3);
        }
        return certificate;
    }
    
    private boolean deleteDirectory(final String dirPath) throws Exception {
        final File path = new File(dirPath);
        if (path.isFile()) {
            return path.delete();
        }
        if (path.exists()) {
            final File[] files = path.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    this.deleteDirectory(files[i].toString());
                }
                else {
                    files[i].delete();
                }
            }
        }
        return path.delete();
    }
    
    private void writeFile(final String fileName, final byte[] content) throws IOException {
        FileOutputStream fos = null;
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
            fos.write(content);
        }
        catch (final IOException e) {
            SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception occurred while writing file", e);
            throw e;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e2) {
                    SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                }
            }
        }
    }
    
    private void rewriteSSLCertificate(final String certFilePath) throws Exception {
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String tempFileFolderPath = serverHome + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator + "certificate_backup";
        try {
            new File(tempFileFolderPath).mkdir();
            final String tempFilePath = tempFileFolderPath + File.separator + UUID.randomUUID().toString();
            final byte[] value = readFileContentAsArray(certFilePath);
            final String oldContent = new String(value);
            final String modifiedContent = oldContent.substring(oldContent.indexOf("-----BEGIN CERTIFICATE"), oldContent.length());
            this.writeFile(tempFilePath, modifiedContent.getBytes());
            final String backupFilePath = tempFileFolderPath + File.separator + UUID.randomUUID().toString();
            this.copyFile(certFilePath, backupFilePath);
            this.copyFile(tempFilePath, certFilePath);
            new File(tempFilePath).delete();
        }
        finally {
            this.deleteDirectory(tempFileFolderPath);
        }
    }
    
    public boolean copyFile(final String srcFile, final String destFile) throws Exception {
        return this.copyFile(new File(srcFile), new File(destFile));
    }
    
    private boolean copyFile(final File srcFile, final File destFile) throws Exception {
        boolean retType = false;
        InputStream inFile = null;
        OutputStream outFile = null;
        SSLCertificateUtil.LOGGER.log(Level.INFO, "Going to copy file.......");
        try {
            final String parentLoc = destFile.getParent();
            if (parentLoc != null && !parentLoc.equals("") && !new File(parentLoc).exists()) {
                new File(parentLoc).mkdirs();
            }
            inFile = new FileInputStream(srcFile);
            outFile = new FileOutputStream(destFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inFile.read(buf)) > 0) {
                outFile.write(buf, 0, len);
            }
            retType = true;
        }
        catch (final Exception e) {
            SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception while copying file.......", e);
        }
        finally {
            if (inFile != null) {
                inFile.close();
            }
            if (outFile != null) {
                outFile.close();
            }
        }
        return retType;
    }
    
    public Boolean checkHostNameValidWithSSL(final String natAddress) throws Exception {
        final Boolean isValid = Boolean.FALSE;
        final Set sslCertificateHostName = this.getSSLCertificateHostNames();
        final Iterator names = sslCertificateHostName.iterator();
        while (names.hasNext()) {
            final String hostName = names.next().trim();
            if (new WildCardStringMatcher().isStringMatching(natAddress, hostName)) {
                return Boolean.TRUE;
            }
        }
        return isValid;
    }
    
    public Boolean checkHostNameValidWithCertificate(final String natAddress, final String certificatePath) throws Exception {
        final Boolean isValid = Boolean.FALSE;
        final Set sslCertificateHostName = this.getSSLCertificateHostNames(certificatePath);
        final Iterator names = sslCertificateHostName.iterator();
        while (names.hasNext()) {
            final String hostName = names.next().trim();
            if (new WildCardStringMatcher().isStringMatching(natAddress, hostName)) {
                return Boolean.TRUE;
            }
        }
        return isValid;
    }
    
    public Boolean checkHostNameValidWithCertificate(final String natAddress, final X509Certificate certificate) throws Exception {
        final Boolean isValid = Boolean.FALSE;
        final Set sslCertificateHostName = this.getSSLCertificateHostNames(certificate);
        final Iterator names = sslCertificateHostName.iterator();
        while (names.hasNext()) {
            final String hostName = names.next().trim();
            if (new WildCardStringMatcher().isStringMatching(natAddress, hostName)) {
                return Boolean.TRUE;
            }
        }
        return isValid;
    }
    
    public void checkUnWantedSpaceinServerCertificate(final String path) throws FileNotFoundException, IOException {
        try {
            SSLCertificateUtil.LOGGER.log(Level.INFO, "Going to check any unwanted space occurred in server certificate");
            final ArrayList<String> lines = new ArrayList<String>();
            String line = null;
            boolean isSpaceinLine = false;
            final File file = new File(path);
            FileReader fr = null;
            fr = new FileReader(file);
            BufferedReader br = null;
            br = new BufferedReader(fr);
            String tempLine = null;
            while ((line = br.readLine()) != null) {
                if (line.contains(" ")) {
                    tempLine = line;
                    line = line.trim();
                    if (!line.equals(tempLine)) {
                        isSpaceinLine = true;
                    }
                }
                lines.add(line);
            }
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            }
            catch (final Exception ex) {
                SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception occurred while closing connections for read server certificate", ex);
            }
            if (isSpaceinLine) {
                SSLCertificateUtil.LOGGER.log(Level.INFO, "Going to remove unwanted space in server certificate");
                FileWriter fw = null;
                fw = new FileWriter(file);
                BufferedWriter out = null;
                out = new BufferedWriter(fw);
                for (final String s : lines) {
                    out.write(s + "\n");
                }
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    if (fw != null) {
                        fw.close();
                    }
                }
                catch (final Exception ex2) {
                    SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception occurred while closing connection for writing server certificate", ex2);
                }
            }
            else {
                SSLCertificateUtil.LOGGER.log(Level.INFO, "Server certificate was not having any extra space.");
            }
        }
        catch (final Exception ex3) {
            SSLCertificateUtil.LOGGER.log(Level.WARNING, "Exception occurred while removing unwanted space from server certificate. ", ex3);
        }
    }
    
    public HashMap verifyCertificate(String givenNATAddress) throws Exception {
        final HashMap map = new HashMap();
        boolean hideSSLMismatchMsg = Boolean.FALSE;
        if (givenNATAddress != null && !givenNATAddress.isEmpty()) {
            final Set sslCertificateHostName = getInstance().getSSLCertificateHostNames();
            if (sslCertificateHostName != null && !sslCertificateHostName.isEmpty()) {
                givenNATAddress = givenNATAddress.trim();
                final Iterator names = sslCertificateHostName.iterator();
                while (names.hasNext()) {
                    final String hostName = names.next().trim();
                    if (new WildCardStringMatcher().isStringMatching(givenNATAddress, hostName)) {
                        hideSSLMismatchMsg = Boolean.TRUE;
                    }
                }
            }
        }
        else {
            hideSSLMismatchMsg = Boolean.TRUE;
        }
        if (hideSSLMismatchMsg) {
            map.put("SSL_HOST_NAME_MISMATCH", false);
            MessageProvider.getInstance().hideMessage("SSL_HOST_NAME_MISMATCH");
        }
        else {
            map.put("SSL_HOST_NAME_MISMATCH", true);
            MessageProvider.getInstance().unhideMessage("SSL_HOST_NAME_MISMATCH");
        }
        try {
            map.put("SSL_CERTIFICATE_EXPIRED", false);
            MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRED");
            MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRY_MSG");
            if (getInstance().isThirdPartySSLInstalled()) {
                final Certificate serverCertificate = CertificateUtils.loadX509CertificateFromFile(new File(getInstance().getServerCertificateFilePath()));
                final List<String> filePaths = new ArrayList<String>();
                final String intermediatePath = getInstance().getIntermediateCertificateFilePath();
                if (intermediatePath != null && new File(intermediatePath).exists()) {
                    filePaths.add(intermediatePath);
                }
                final String caPath = getInstance().getServerCACertificateFilePath();
                if (caPath != null && new File(caPath).exists()) {
                    filePaths.add(caPath);
                }
                final Set<Certificate> intermediateCertificateList = new HashSet<Certificate>(CertificateUtils.splitMultipleCertificatesInEachFileToCertificateList(filePaths));
                intermediateCertificateList.addAll(CertificateUtils.getTrustedRootCACertificatesFromCACerts());
                try {
                    CertificateVerifier.verifyCertificate(serverCertificate, intermediateCertificateList);
                    map.put("CERT_CHAIN_NOT_VERIFIED", false);
                    MessageProvider.getInstance().hideMessage("CERT_CHAIN_NOT_VERIFIED");
                }
                catch (final CertificateVerificationException exp) {
                    final Throwable rootCause = exp.getCause();
                    if (rootCause instanceof CertPathBuilderException) {
                        map.put("CERT_CHAIN_NOT_VERIFIED", true);
                        MessageProvider.getInstance().unhideMessage("CERT_CHAIN_NOT_VERIFIED");
                        SSLCertificateUtil.LOGGER.log(Level.SEVERE, "Certificate Chain is not verified. Need to re upload certificate with complete chain.");
                    }
                    else {
                        SSLCertificateUtil.LOGGER.log(Level.SEVERE, "Exception in verifyCertificateChain", exp);
                    }
                }
            }
            else {
                map.put("CERT_CHAIN_NOT_VERIFIED", false);
                MessageProvider.getInstance().hideMessage("CERT_CHAIN_NOT_VERIFIED");
            }
        }
        catch (final CertificateExpiredException ex) {
            try {
                map.put("SSL_CERTIFICATE_EXPIRED", true);
                MessageProvider.getInstance().unhideMessage("SSL_CERTIFICATE_EXPIRED");
                MessageProvider.getInstance().unhideMessage("SSL_CERTIFICATE_EXPIRY_MSG");
                map.put("CERT_CHAIN_NOT_VERIFIED", false);
                MessageProvider.getInstance().hideMessage("CERT_CHAIN_NOT_VERIFIED");
            }
            catch (final Exception e) {
                SSLCertificateUtil.LOGGER.log(Level.INFO, "Exception in getting I18N properties" + e);
            }
        }
        catch (final Exception ex2) {
            map.put("CERT_CHAIN_NOT_VERIFIED", false);
            MessageProvider.getInstance().hideMessage("CERT_CHAIN_NOT_VERIFIED");
            SSLCertificateUtil.LOGGER.log(Level.SEVERE, "Error in certificate verification", ex2);
        }
        CertificateCacheHandler.getInstance().putAll(map);
        return map;
    }
    
    public Date getSSLCertificateCreationDate() throws Exception {
        final String certificateFilePath = this.getServerCertificateFilePath();
        final Map certificateInfo = this.getCertificateDetails(certificateFilePath);
        return new Date(certificateInfo.get("CreationDateInMillis"));
    }
    
    public String getThumbPrint(final X509Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException {
        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        final byte[] der = cert.getEncoded();
        md.update(der);
        final byte[] digest = md.digest();
        final String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }
    
    public void clearSSLCertificateCache() {
        CertificateCacheHandler.getInstance().removeAll();
    }
    
    static {
        LOGGER = Logger.getLogger(SSLCertificateUtil.class.getName());
        SSLCertificateUtil.sslCertificateUtil = null;
        SSLCertificateUtil.webServerName = "";
        SSLCertificateUtil.apache = "apache";
        SSLCertificateUtil.nginx = "nginx";
        SSLCertificateUtil.websettingsconf = "websettings.conf";
        SSLCertificateUtil.webservernameprops = "webserver.name";
    }
}
