package com.me.devicemanagement.onpremise.webclient.admin.certificate.manual;

import java.util.Map;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.Properties;
import java.security.cert.CertificateEncodingException;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Base64;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.util.logging.Level;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.FileUtils;
import java.security.cert.CertificateFactory;
import com.adventnet.mfw.ConsoleOut;
import java.util.Date;
import java.io.File;
import java.util.logging.Logger;

public class BatchCertificateConverter
{
    public static BatchLogHandler logHandler;
    public static Logger logger;
    static String homeLocation;
    static String apacheLocation;
    static String confDirectory;
    
    public static void main(final String[] args) {
        BatchCertificateConverter.homeLocation = args[0];
        BatchCertificateConverter.apacheLocation = BatchCertificateConverter.homeLocation + File.separator + "apache";
        BatchCertificateConverter.confDirectory = BatchCertificateConverter.apacheLocation + File.separator + "conf";
        BatchCertificateConverter.logHandler = new BatchLogHandler(Logger.getLogger(BatchCertificateConverter.class.getName()), BatchCertificateConverter.homeLocation);
        BatchCertificateConverter.logger = BatchCertificateConverter.logHandler.getLogger();
        ConsoleOut.println("\n\nDER to PEM Conversion started at " + new Date());
        if (getCertificateAndConvertToPEM()) {
            ConsoleOut.println("\n\nDER to PEM Conversion completed successfully..");
        }
        else {
            ConsoleOut.println("\n\nDER to PEM Conversion failed..File may have been corrupted/not of expected format");
            ConsoleOut.println("\nIf problem persists, please send the logs to endpointcentral-support@manageengine.com");
        }
    }
    
    static boolean convertDERtoPEM(File serverCertificateFile) {
        InputStream in = null;
        try {
            BatchCertificateConverter.logger.info("DER format found.. Converting to PEM..");
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final byte[] bytes = FileUtils.readFileToByteArray(serverCertificateFile);
            in = new ByteArrayInputStream(bytes);
            final X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
            BatchCertificateConverter.logger.info("Converting to pem initiated..");
            serverCertificateFile = convertToPem(cert, serverCertificateFile);
            if (serverCertificateFile == null) {
                BatchCertificateConverter.logger.severe("Conversion to pem failed..file may have been corrupted..");
                return false;
            }
            return true;
        }
        catch (final IOException ex) {
            BatchCertificateConverter.logger.log(Level.SEVERE, serverCertificateFile + " File may have been in " + "use / File is not accessible..", ex);
            BatchCertificateConverter.logHandler.logExceptionTrace(ex);
            return false;
        }
        catch (final NullPointerException ex2) {
            BatchCertificateConverter.logger.log(Level.SEVERE, "It is advisable to check the input..Make sure administrative privilege is provided..", ex2);
            BatchCertificateConverter.logHandler.logExceptionTrace(ex2);
            return false;
        }
        catch (final CertificateException ex3) {
            BatchCertificateConverter.logger.log(Level.SEVERE, "This tool may not support the certificate given.. Or the certificate could contain invalid characters..", ex3);
            BatchCertificateConverter.logHandler.logExceptionTrace(ex3);
        }
        finally {
            try {
                in.close();
            }
            catch (final IOException ex4) {
                BatchCertificateConverter.logger.severe("IO exception in closing the input stream");
            }
        }
        return false;
    }
    
    public static boolean isDerCertificate(final File serverCertificateFile) {
        BufferedReader certificateReader = null;
        try {
            certificateReader = new BufferedReader(new FileReader(serverCertificateFile));
        }
        catch (final FileNotFoundException ex) {
            BatchCertificateConverter.logger.log(Level.SEVERE, "Certificate File not found.. File may have been deleted meanwhile..", ex);
            BatchCertificateConverter.logHandler.logExceptionTrace(ex);
        }
        String CurrentLine = null;
        boolean isDER = true;
        try {
            while ((CurrentLine = certificateReader.readLine()) != null) {
                if (CurrentLine.contains("BEGIN CERTIFICATE")) {
                    isDER = false;
                    break;
                }
            }
        }
        catch (final IOException ex2) {
            BatchCertificateConverter.logger.log(Level.SEVERE, "Certificate File may have been in use/not available.. Reading certificate failed..", ex2);
            BatchCertificateConverter.logHandler.logExceptionTrace(ex2);
            BatchCertificateConverter.logger.info(" Checking the certificate for DER Format process failed..");
            isDER = false;
        }
        return isDER;
    }
    
    public static File convertToPem(final X509Certificate certificate, final File serverCertificateFile) throws CertificateEncodingException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(serverCertificateFile);
            try {
                out.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
                out.write(Base64.encodeBase64(certificate.getEncoded(), true));
                out.write("-----END CERTIFICATE-----".getBytes("US-ASCII"));
            }
            finally {
                out.flush();
                out.close();
            }
            return serverCertificateFile;
        }
        catch (final Exception ex) {
            try {
                out.close();
                BatchCertificateConverter.logger.log(Level.SEVERE, "Conversion to pem failed..", ex);
            }
            catch (final IOException ex2) {
                BatchCertificateConverter.logger.log(Level.SEVERE, "IO Exception in handling pem conversion", ex);
            }
            return null;
        }
    }
    
    static boolean getCertificateAndConvertToPEM() {
        boolean conversionSuccessful = true;
        try {
            File serverCertificateFile;
            for (serverCertificateFile = null, serverCertificateFile = BatchUtil.getFileInput("Server Certificate", serverCertificateFile); !BatchUtil.getExtension(serverCertificateFile).equalsIgnoreCase(".crt") && !BatchUtil.getExtension(serverCertificateFile).equalsIgnoreCase(".cer"); serverCertificateFile = BatchUtil.getFileInput("Server Certificate", serverCertificateFile)) {
                ConsoleOut.println("Given file is not a crt / .cer file..");
            }
            BatchUtil.copyFileUsingChannel(serverCertificateFile, new File(serverCertificateFile.getParent() + File.separator + BatchUtil.removeExtension(serverCertificateFile.getName()) + "_back" + BatchUtil.getExtension(serverCertificateFile)));
            conversionSuccessful = initiateConversion(serverCertificateFile);
            if (conversionSuccessful) {
                Properties prop = new Properties();
                try {
                    prop = WebServerUtil.getWebServerSettings();
                }
                catch (final Exception ex) {
                    Logger.getLogger(BatchCertificateConverter.class.getName()).log(Level.SEVERE, null, ex);
                }
                final File confDirectory = new File(BatchCertificateConverter.apacheLocation + File.separator + "conf");
                final File propServerCrtFile = new File(confDirectory + File.separator + prop.getProperty("apache.crt.loc"));
                boolean copyToSameDirectory = false;
                if (propServerCrtFile.getCanonicalPath().equalsIgnoreCase(serverCertificateFile.getCanonicalPath())) {
                    copyToSameDirectory = true;
                }
                if (!copyToSameDirectory) {
                    BatchUtil.copySSLToConf(confDirectory, serverCertificateFile);
                }
                else {
                    BatchUtil.copyAsTempToConf(confDirectory, serverCertificateFile);
                }
                final Map<String, String> hMap = BatchUtil.propertiesToMap(prop);
                BatchCertificateConverter.logger.log(Level.INFO, "Updating websettings.conf file..Following are the entries added..");
                hMap.remove("apache.crt.loc");
                BatchCertificateConverter.logger.log(Level.INFO, "Server Certificate File " + serverCertificateFile.getName());
                hMap.put("apache.crt.loc", serverCertificateFile.getName());
                if (copyToSameDirectory) {
                    serverCertificateFile = BatchUtil.deleteCRTAndRenameTempToCRT(confDirectory, serverCertificateFile);
                }
                try {
                    final Properties outProp = BatchUtil.mapToProperties(hMap);
                    WebServerUtil.storeProperWebServerSettings(outProp);
                }
                catch (final Exception ex2) {
                    BatchCertificateConverter.logger.severe("Error While Storing the Properties to the Websettings.conf file ..");
                    BatchCertificateConverter.logHandler.logExceptionTrace(ex2);
                }
                return true;
            }
        }
        catch (final FileNotFoundException ex3) {
            BatchCertificateConverter.logger.log(Level.SEVERE, "<SERVER-HOME>/conf/websettings.conf file not found..", ex3);
            BatchCertificateConverter.logHandler.logExceptionTrace(ex3);
        }
        catch (final IOException ex4) {
            BatchCertificateConverter.logger.log(Level.SEVERE, ".crt/.key file provided by client may have been in use.. or they are not accessible", ex4);
            BatchCertificateConverter.logHandler.logExceptionTrace(ex4);
        }
        catch (final NullPointerException ex5) {
            BatchCertificateConverter.logger.log(Level.SEVERE, "User may have not granted administrative privilege to this tool.. Copying is prohibited..", ex5);
            BatchCertificateConverter.logHandler.logExceptionTrace(ex5);
        }
        return false;
    }
    
    private static boolean initiateConversion(final File serverCertificateFile) {
        boolean conversionSuccessful = true;
        if (isDerCertificate(serverCertificateFile)) {
            if (convertDERtoPEM(serverCertificateFile)) {
                conversionSuccessful = true;
            }
            else {
                conversionSuccessful = false;
                BatchCertificateConverter.logger.info(serverCertificateFile + " DER to PEM Conversion failed..");
            }
        }
        else {
            BatchCertificateConverter.logger.info(serverCertificateFile.getName() + " PEM Certificate found..");
        }
        return conversionSuccessful;
    }
    
    static {
        BatchCertificateConverter.homeLocation = null;
        BatchCertificateConverter.apacheLocation = null;
        BatchCertificateConverter.confDirectory = null;
    }
}
