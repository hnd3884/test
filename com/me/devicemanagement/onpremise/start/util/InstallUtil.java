package com.me.devicemanagement.onpremise.start.util;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import org.bouncycastle.openssl.PEMWriter;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.Socket;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;

public class InstallUtil
{
    private static PortCheckerUtil pcUtil;
    public static final int NO_PROBLEM = 0;
    public static final int PORT_OCCUPIED_BY_OTHERS = 1;
    public static final int PORT_OUT_OF_RANGE = 3;
    public static final String DM_ROOT_CA_CERT_NAME = "DMRootCA.crt";
    public static final String DM_ROOT_CA_KEY_NAME = "DMRootCA.key";
    public static final String QUICK_FIXER = "quickfixer";
    public static int exitCode;
    
    public static void main(final String[] args) {
        final String serverHome = args[0];
        System.setProperty("server.home", serverHome);
        String rootCAcertPath = null;
        String rootCAKeyPath = null;
        try {
            rootCAcertPath = getServerRootCertificateFilePath();
            rootCAKeyPath = getServerRootKeyFilePath();
        }
        catch (final Exception ex) {
            System.out.println("Exception while getting Root Path for Server Certificates." + ex);
        }
        final File quickFixerDir = new File(serverHome + File.separator + "quickfixer" + File.separator + "qppm");
        if (!quickFixerDir.exists()) {
            quickFixerDir.mkdirs();
        }
        try {
            if (!new File(rootCAcertPath).exists() && !new File(rootCAKeyPath).exists()) {
                generateCARootCertificate(rootCAcertPath, rootCAKeyPath, new String[0]);
                final File dmRootCACert = new File(rootCAcertPath);
                final File dmRootCAKey = new File(rootCAKeyPath);
                if ("apache".equalsIgnoreCase(getWebServerName(serverHome))) {
                    final String nginxConfDir = serverHome + File.separator + "nginx" + File.separator + "conf";
                    final File nginxDMRootCACert = new File(nginxConfDir + File.separator + "DMRootCA.crt");
                    final File nginxDMRootCAKey = new File(nginxConfDir + File.separator + "DMRootCA.key");
                    copyFile(dmRootCACert, nginxDMRootCACert);
                    copyFile(dmRootCAKey, nginxDMRootCAKey);
                }
                else {
                    final String apacheConfDir = serverHome + File.separator + "apache" + File.separator + "conf";
                    final File apacheDMRootCACert = new File(apacheConfDir + File.separator + "DMRootCA.crt");
                    final File apacheDMRootCAKey = new File(apacheConfDir + File.separator + "DMRootCA.key");
                    copyFile(dmRootCACert, apacheDMRootCACert);
                    copyFile(dmRootCAKey, apacheDMRootCAKey);
                }
            }
            if (!new File(rootCAcertPath).exists()) {
                throw new Exception("Could not update the certificate into truststore because the DMRootCA.crt file missing in setup");
            }
            final String winUtilExe = System.getProperty("server.home") + File.separator + "bin" + File.separator + "dcwinutil.exe";
            StartupUtil.executeCommand(winUtilExe, "-InstallRootCertificate", new File(rootCAcertPath).getCanonicalPath());
        }
        catch (final Exception ex2) {
            System.out.println("Exception while generating root certificate" + ex2);
            setExitCode(3);
        }
        try {
            if (args.length == 1) {
                recordInstallationDetails(args);
                return;
            }
            if (args.length == 2 || args.length == 3) {
                changePort(args);
                System.exit(InstallUtil.exitCode);
            }
            else {
                System.out.println("USAGE: com.adventnet.sym.start.util.InstallUtil <server.xml> WEBSERVER=<portno> ");
                setExitCode(2);
                System.exit(InstallUtil.exitCode);
            }
        }
        catch (final Exception e) {
            setExitCode(1);
            System.exit(InstallUtil.exitCode);
        }
    }
    
    public static String getServerRootCertificateFilePath() throws Exception {
        return getWebServerConfDir(System.getProperty("server.home")) + File.separator + "DMRootCA.crt";
    }
    
    public static String getServerRootKeyFilePath() throws Exception {
        return getWebServerConfDir(System.getProperty("server.home")) + File.separator + "DMRootCA.key";
    }
    
    private static void changePort(final String[] args) {
        int httpPort = -1;
        int httpsPort = -1;
        final String serverHome = args[0];
        System.setProperty("server.home", serverHome);
        try {
            String value = args[1].substring(args[1].indexOf("=") + 1);
            httpPort = Integer.parseInt(value);
            if (args.length == 3) {
                value = args[2].substring(args[2].indexOf("=") + 1);
                httpsPort = Integer.parseInt(value);
            }
            else {
                httpsPort = getSSLPortForWebServerPort(httpPort);
            }
        }
        catch (final NumberFormatException nfe) {
            System.out.println("Non integer port was provided. Quitting.");
            setExitCode(4);
            return;
        }
        catch (final Exception e) {
            e.printStackTrace();
            setExitCode(1);
            return;
        }
        final boolean result = true;
        try {
            try {
                WebServerUtil.updateWebServerPorts(httpPort, httpsPort);
            }
            catch (final Exception e2) {
                setExitCode(6);
            }
            setExitCode(WebServerUtil.refreshWebServerSettings());
        }
        catch (final Exception ex) {
            System.out.println("Error while updating webserver port values..." + ex);
            ex.printStackTrace();
            setExitCode(5);
        }
    }
    
    private static void recordInstallationDetails(final String[] installProps) {
        System.out.println("install props: " + installProps);
        String confDir = "";
        String serverHome = "";
        if (installProps.length < 1) {
            confDir = ".." + File.separator + "conf";
            System.out.println("Taking the default value for conf dir: " + confDir);
        }
        else {
            confDir = installProps[0] + File.separator + "conf";
        }
        final String confFileName = confDir + File.separator + "install.conf";
        System.out.println("Product conf file Name: " + confFileName);
        try {
            if (installProps.length < 1) {
                serverHome = new File("..").getCanonicalPath();
            }
            else {
                serverHome = new File(installProps[0]).getCanonicalPath();
            }
            writeInstallationPath(serverHome);
        }
        catch (final IOException ex) {
            Logger.getLogger(InstallUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            final Properties props = StartupUtil.getProperties(confFileName);
            final long date = System.currentTimeMillis();
            if (props.isEmpty()) {
                final Properties prop = new Properties();
                prop.setProperty("it", String.valueOf(date));
                prop.setProperty("lang", System.getProperty("user.language"));
                StartupUtil.storeProperties(prop, confFileName);
                System.out.println("Installation date is recorded as: " + getDateString(date, "MMM dd,yyyy hh:mm a"));
                System.out.println("Writing installation props: " + prop);
            }
            else {
                System.out.println("Installation props already exists: " + props);
                long instDate = 0L;
                final String instDateStr = props.getProperty("it");
                if (instDateStr != null) {
                    try {
                        instDate = Long.parseLong(instDateStr);
                    }
                    catch (final Exception ex2) {
                        instDate = 0L;
                    }
                }
                if (instDate == 0L) {
                    props.setProperty("it", String.valueOf(date));
                    props.setProperty("lang", System.getProperty("user.language"));
                    StartupUtil.storeProperties(props, confFileName);
                    System.out.println("Installation date is recorded as: " + getDateString(date, "MMM dd,yyyy hh:mm a"));
                    System.out.println("Writing installation props: " + props);
                }
                else {
                    System.out.println("Installation date is already present. The value is: " + getDateString(instDate, "MMM dd,yyyy hh:mm a"));
                }
            }
        }
        catch (final Exception ex3) {
            System.out.println("Caught exception while writing installation details in " + confFileName + "\t Exception: " + ex3);
            ex3.printStackTrace();
        }
    }
    
    public static boolean checkAndUpdateCustomerRegistrationDetails() {
        FileInputStream ios = null;
        try {
            final String customerFile = ".." + File.separator + "logs" + File.separator + "customerInfo.txt";
            System.out.println("customerFile :" + customerFile);
            final File file = new File(customerFile);
            if (!file.exists()) {
                return false;
            }
            ios = new FileInputStream(customerFile);
            final Properties prop = new Properties();
            prop.load(ios);
            ios.close();
            if (prop.getProperty("Name").equals("") && prop.getProperty("Phone").equals("") && prop.getProperty("Email").equals("") && prop.getProperty("Country").equals("") && prop.getProperty("Company").equals("")) {
                System.out.println("The customer has not registered his contact during installation...");
                return false;
            }
            System.out.println("The customer has registered his contact during installation...");
            return true;
        }
        catch (final Exception ex) {
            System.out.println("Caught exception while updating whether the customer has registered or not." + ex);
            ex.printStackTrace();
            return false;
        }
        finally {
            try {
                if (ios != null) {
                    ios.close();
                }
            }
            catch (final Exception ex2) {}
        }
    }
    
    public static int isPortUsable(final String portNumber) {
        try {
            final int port = Integer.parseInt(portNumber);
            if (port <= 0 || port > 65535) {
                return 3;
            }
            if (InstallUtil.pcUtil.checkPortAvailability(port)) {
                return 0;
            }
            return 1;
        }
        catch (final Exception e) {
            return 1;
        }
    }
    
    public static synchronized int getWebServerPort(String webServerPortFile) {
        if (webServerPortFile == null) {
            webServerPortFile = "..\\conf\\server.xml";
        }
        if (InstallUtil.pcUtil == null) {
            InstallUtil.pcUtil = new PortCheckerUtil(webServerPortFile);
        }
        else {
            InstallUtil.pcUtil.parseXml(webServerPortFile);
        }
        return InstallUtil.pcUtil.getPort("WebServer");
    }
    
    private static void createPortCheckUtil() {
        if (InstallUtil.pcUtil == null) {
            final String webServerPortFile = "..\\conf\\server.xml";
            InstallUtil.pcUtil = new PortCheckerUtil(webServerPortFile);
        }
    }
    
    public static int getWebServerPort() {
        final String webServerPortFile = "..\\conf\\server.xml";
        if (InstallUtil.pcUtil == null) {
            InstallUtil.pcUtil = new PortCheckerUtil(webServerPortFile);
        }
        else {
            InstallUtil.pcUtil.parseXml(webServerPortFile);
        }
        return InstallUtil.pcUtil.getPort("WebServer");
    }
    
    public static int getSSLPort() {
        final String webServerPortFile = "..\\conf\\server.xml";
        if (InstallUtil.pcUtil == null) {
            InstallUtil.pcUtil = new PortCheckerUtil(webServerPortFile);
        }
        else {
            InstallUtil.pcUtil.parseXml(webServerPortFile);
        }
        return InstallUtil.pcUtil.getPort("SSL");
    }
    
    public static int changeWebServerPort(final int portno) throws Exception {
        final int portUsable = isPortUsable(String.valueOf(portno));
        try {
            if (portUsable == 0) {
                final int sslPort = getSSLPortForWebServerPort(portno);
                final Properties props = new Properties();
                props.setProperty("WebServer", String.valueOf(portno));
                props.setProperty("SSL", String.valueOf(sslPort));
                createPortCheckUtil();
                final boolean result = InstallUtil.pcUtil.changePort(props);
                System.out.println("Change port invoked with props: " + props + "  and return value: " + result);
                return 0;
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
        return portUsable;
    }
    
    private static int getSSLPortForWebServerPort(final int port) throws Exception {
        final int diffVal = 363;
        int sslPort = port + diffVal;
        final boolean isMsp = isMSP();
        System.out.println("is MSP Product? ==> " + isMsp);
        if (isMsp) {
            sslPort = port + 1;
        }
        return sslPort;
    }
    
    public static void zipRecursively(final ZipOutputStream jos, final String directory) throws Exception {
        final File dir = new File(directory);
        final File[] files = dir.listFiles();
        ZipEntry je = null;
        String fi = null;
        File file = null;
        for (int i = 0; i < files.length; ++i) {
            file = files[i];
            fi = file.getCanonicalPath();
            if (file.isDirectory()) {
                je = new ZipEntry(fi + File.separator);
                try {
                    jos.putNextEntry(je);
                }
                catch (final IOException ioe) {
                    System.out.println("Problem while creating zip ");
                }
                zipRecursively(jos, file.getCanonicalPath());
            }
            else if (!fi.endsWith("desktopcentral_db_backup.zip")) {
                je = new ZipEntry(fi);
                try {
                    jos.putNextEntry(je);
                }
                catch (final IOException ioex) {
                    System.out.println("Problem while creating zip ");
                }
                try {
                    getFileContents(file.getCanonicalPath(), jos);
                }
                catch (final Exception fe) {
                    System.out.println("Problem while creating zip ");
                }
            }
        }
    }
    
    private static boolean getFileContents(final String fileName, final OutputStream ous) throws Exception {
        final File fi = new File(fileName);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(fi);
            bis = new BufferedInputStream(fis);
        }
        catch (final FileNotFoundException fnfe) {
            System.out.println("Problem while creating zip ");
            return false;
        }
        try {
            int i;
            while ((i = bis.read()) != -1) {
                ous.write(i);
            }
        }
        catch (final IOException ioe) {
            System.out.println("Problem while creating zip ");
            return false;
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            }
            catch (final Exception e) {
                System.out.println("Problem while creating zip ");
            }
        }
        return true;
    }
    
    public static boolean deleteDir(final File dir) {
        if (dir.isDirectory()) {
            final String[] children = dir.list();
            for (int i = 0; i < children.length; ++i) {
                final boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
    public static int getDBPort(final String databaseParamsConf) {
        String dbConfFile = ".." + File.separator + "conf" + File.separator + "database_params.conf";
        if (databaseParamsConf != null) {
            dbConfFile = databaseParamsConf;
        }
        System.out.println("DB Port will be taken from: " + dbConfFile);
        final Properties props = new Properties();
        FileInputStream fis = null;
        try {
            if (new File(dbConfFile).exists()) {
                fis = new FileInputStream(dbConfFile);
                props.load(fis);
                fis.close();
            }
        }
        catch (final Exception ex) {
            System.out.println("Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {}
        }
        int dbPort = 23306;
        final String connUrl = props.getProperty("url");
        System.out.println("Connection URL from conf file: " + connUrl);
        if (connUrl != null) {
            final String portStr = connUrl.substring(connUrl.lastIndexOf(":") + 1, connUrl.lastIndexOf("/"));
            if (portStr != null) {
                dbPort = Integer.parseInt(portStr);
                System.out.println("DB Port after parsing: " + portStr);
            }
        }
        return dbPort;
    }
    
    public static boolean isPortEngaged(final int portNum) {
        if (portNum < 0) {
            return false;
        }
        try {
            final Socket sock = new Socket((String)null, portNum);
            sock.close();
            System.out.println("Testing port: " + portNum + ". Returning true.");
            return true;
        }
        catch (final Exception ex) {
            System.out.println("Port " + portNum + " not yet ready for connection");
            return false;
        }
    }
    
    public static Properties getProductProperties() {
        Properties props = null;
        try {
            final String fname = ".." + File.separator + "conf" + File.separator + "product.conf";
            props = StartupUtil.getProperties(fname);
        }
        catch (final Exception ex) {
            System.out.println("Caught exception while getting product properties: " + ex);
        }
        return props;
    }
    
    public static String getDateString(final long dateVal, final String format) {
        final Date date = new Date(dateVal);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    
    public static Properties getSystemEnvVars() {
        final Properties envVars = new Properties();
        try {
            Process p = null;
            final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "cmd", "/c", "set" });
            p = processBuilder.start();
            final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                final int idx = line.indexOf(61);
                final String key = line.substring(0, idx);
                final String value = line.substring(idx + 1);
                envVars.setProperty(key, value);
            }
        }
        catch (final Exception ex) {
            System.out.println("Caught exception while retrieving system env variables." + ex);
            ex.printStackTrace();
        }
        return envVars;
    }
    
    public static boolean isMSP() {
        final String value = getMSPProperties("isMSP");
        Boolean isMSP = Boolean.FALSE;
        if (value != null && value.equals("true")) {
            isMSP = Boolean.TRUE;
        }
        return isMSP;
    }
    
    public static String getMSPProperties(final String key) {
        String value = null;
        try {
            final String path = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = path + File.separator + "conf" + File.separator + "msp_properties.conf";
            final Properties props = StartupUtil.getProperties(fname);
            value = props.getProperty(key);
        }
        catch (final Exception ex) {
            System.out.println("Caught exception while getting property from msp_properties file : " + ex);
        }
        return value;
    }
    
    public static void writeInstallationPath(final String serverHome) {
        try {
            final String fname = serverHome + File.separator + "conf" + File.separator + "fos_installation_path.conf";
            final StringBuffer sb = new StringBuffer(1000);
            sb.append("\ninstallation.dir=" + serverHome);
            writeFile(fname, sb.toString().getBytes());
        }
        catch (final IOException ex) {
            System.out.println("Exception occurred while writin fos_installation_path file" + ex);
        }
    }
    
    public static void writeFile(final String fileName, final byte[] content) throws IOException {
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
            System.out.println("Exception occurred while writing file" + e);
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e2) {
                    System.out.println("Exception occurred while closing file output stream" + e2);
                }
            }
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e3) {
                    System.out.println("Exception occurred while closing file output stream" + e3);
                }
            }
        }
    }
    
    private static String getWebServerName(final String serverHome) throws Exception {
        final Properties wsProps = getWebServerProps(serverHome);
        return wsProps.getProperty("webserver.name");
    }
    
    private static String getWebServerConfDir(final String serverHome) throws Exception {
        return serverHome + File.separator + getWebServerName(serverHome) + File.separator + "conf";
    }
    
    private static Properties getWebServerProps(final String serverHome) throws Exception {
        final String webSettingsFile = serverHome + File.separator + "conf" + File.separator + "websettings.conf";
        return StartupUtil.getProperties(webSettingsFile);
    }
    
    private static void copyFile(final File sourceFile, final File destinationFile) throws IOException {
        if (!sourceFile.exists()) {
            throw new FileNotFoundException();
        }
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceFile));
        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destinationFile));
        String buffer;
        while ((buffer = bufferedReader.readLine()) != null) {
            bufferedWriter.write(buffer);
            bufferedWriter.write("\n");
        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
    }
    
    public static void setExitCode(final int code) {
        if (InstallUtil.exitCode == 0 && code != 0) {
            InstallUtil.exitCode = code;
        }
    }
    
    private static void generateCARootCertificate(final String caRootCertificateFileName, final String caRootKeyFileName, final String... authorityNames) throws Exception {
        Security.addProvider((Provider)new BouncyCastleProvider());
        X509Certificate rootCA = null;
        final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(1, 100);
        final Date notAfter = cal.getTime();
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        final KeyPair keyPair = kpGen.generateKeyPair();
        final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
        builderex.addRDN(BCStyle.C, "US");
        builderex.addRDN(BCStyle.ST, "CA");
        builderex.addRDN(BCStyle.OU, "ManageEngine");
        builderex.addRDN(BCStyle.O, "Zoho Corporation");
        builderex.addRDN(BCStyle.CN, "ManageEngineCA");
        final X500NameBuilder issuerBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        issuerBuilder.addRDN(BCStyle.C, "US");
        issuerBuilder.addRDN(BCStyle.ST, "CA");
        issuerBuilder.addRDN(BCStyle.OU, "ManageEngine");
        issuerBuilder.addRDN(BCStyle.O, "Zoho Corporation");
        issuerBuilder.addRDN(BCStyle.CN, "ManageEngineCA");
        if (authorityNames.length > 0) {
            issuerBuilder.addRDN(BCStyle.CN, authorityNames[0]);
            issuerBuilder.addRDN(BCStyle.OU, authorityNames[1]);
            builderex.addRDN(BCStyle.CN, authorityNames[0]);
            builderex.addRDN(BCStyle.OU, authorityNames[1]);
        }
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(issuerBuilder.build(), serialNumber, notBefore, notAfter, builderex.build(), keyPair.getPublic());
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(keyPair.getPrivate());
        builder.addExtension(Extension.basicConstraints, false, (ASN1Encodable)new BasicConstraints(true));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(keyPair.getPublic());
        builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(166);
        builder.addExtension(Extension.keyUsage, false, (ASN1Encodable)keyUsage);
        final X509CertificateHolder holder = builder.build(contentSigner);
        rootCA = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        final PEMWriter certWriter = new PEMWriter((Writer)new FileWriter(caRootCertificateFileName));
        certWriter.writeObject((Object)rootCA);
        certWriter.flush();
        certWriter.close();
        final PEMWriter keyWriter = new PEMWriter((Writer)new FileWriter(caRootKeyFileName));
        keyWriter.writeObject((Object)keyPair.getPrivate());
        keyWriter.flush();
        keyWriter.close();
    }
    
    static {
        InstallUtil.pcUtil = null;
        InstallUtil.exitCode = 0;
    }
}
