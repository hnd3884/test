package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.security.cert.CertificateException;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateEncodingException;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.util.logging.Logger;

public class CertificateConversionUtil
{
    private static Logger logger;
    private static FileAccessAPI fileAccessApi;
    private static CertificateConversionUtil conversionUtil;
    
    private CertificateConversionUtil() {
    }
    
    public static CertificateConversionUtil getInstance() {
        if (CertificateConversionUtil.conversionUtil == null) {
            CertificateConversionUtil.conversionUtil = new CertificateConversionUtil();
        }
        return CertificateConversionUtil.conversionUtil;
    }
    
    public boolean initiateDERtoPEMConversion(final String serverCertificateFile) {
        boolean conversionSuccessful = true;
        if (this.isDerCertificate(serverCertificateFile)) {
            if (this.convertDERtoPEM(serverCertificateFile)) {
                CertificateConversionUtil.logger.log(Level.INFO, "Conversion to pem {0} succeeded", CertificateUtil.getInstance().getNameOfTheFile(serverCertificateFile));
                conversionSuccessful = true;
            }
            else {
                conversionSuccessful = false;
                CertificateConversionUtil.logger.info(CertificateUtil.getInstance().getNameOfTheFile(serverCertificateFile) + " DER to PEM Conversion failed..");
            }
        }
        else {
            CertificateConversionUtil.logger.info("Given Certificate " + CertificateUtil.getInstance().getNameOfTheFile(serverCertificateFile) + " is a PEM Certificate..");
        }
        return conversionSuccessful;
    }
    
    public String convertToPem(final X509Certificate certificate, final String serverCertificateFile) throws CertificateEncodingException {
        OutputStream out = null;
        try {
            CertificateConversionUtil.fileAccessApi.deleteFile(serverCertificateFile);
            out = CertificateConversionUtil.fileAccessApi.writeFile(serverCertificateFile);
            try {
                out.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
                out.write(Base64.encodeBase64(certificate.getEncoded(), true));
                out.write("-----END CERTIFICATE-----".getBytes("US-ASCII"));
            }
            finally {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
            return serverCertificateFile;
        }
        catch (final Exception ex) {
            try {
                if (out != null) {
                    out.close();
                }
                CertificateConversionUtil.logger.log(Level.SEVERE, "Conversion to pem failed..", ex);
            }
            catch (final IOException ex2) {
                CertificateConversionUtil.logger.log(Level.SEVERE, "IO Exception in handling pem conversion", ex);
            }
            return null;
        }
    }
    
    private boolean isDerCertificate(final String serverCertificateFile) {
        BufferedReader certificateReader = null;
        InputStream in = null;
        try {
            in = CertificateConversionUtil.fileAccessApi.readFile(serverCertificateFile);
            certificateReader = new BufferedReader(new InputStreamReader(in));
        }
        catch (final FileNotFoundException ex) {
            try {
                CertificateConversionUtil.logger.log(Level.SEVERE, "Certificate File not found.. File may have been deleted meanwhile..", ex);
                in.close();
                certificateReader.close();
            }
            catch (final IOException ex2) {
                Logger.getLogger(CertificateConversionUtil.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
        catch (final Exception ex3) {
            try {
                CertificateConversionUtil.logger.log(Level.SEVERE, "Couldn't read the file " + serverCertificateFile + " file may be in use/not accessible", ex3);
                if (in != null) {
                    in.close();
                }
                if (certificateReader != null) {
                    certificateReader.close();
                }
            }
            catch (final Exception ex4) {
                Logger.getLogger(CertificateConversionUtil.class.getName()).log(Level.SEVERE, "IO Exception in closing the stream");
            }
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
        catch (final Exception ex5) {
            CertificateConversionUtil.logger.log(Level.SEVERE, "Certificate File may have been in use/not available.. Reading certificate failed..", ex5);
            CertificateConversionUtil.logger.info(" Checking the certificate for DER Format process failed..");
            isDER = false;
            try {
                if (in != null) {
                    in.close();
                }
                if (certificateReader != null) {
                    certificateReader.close();
                }
            }
            catch (final Exception ex5) {
                CertificateConversionUtil.logger.log(Level.SEVERE, "Error in closing the straem.. You can ignore this..");
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (certificateReader != null) {
                    certificateReader.close();
                }
            }
            catch (final Exception ex6) {
                CertificateConversionUtil.logger.log(Level.SEVERE, "Error in closing the straem.. You can ignore this..");
            }
        }
        return isDER;
    }
    
    public boolean convertDERtoPEM(String serverCertificateFile) {
        InputStream in = null;
        try {
            CertificateConversionUtil.logger.info("DER format found.. Converting to PEM..");
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final byte[] bytes = CertificateConversionUtil.fileAccessApi.readFileContentAsArray(serverCertificateFile);
            in = new ByteArrayInputStream(bytes);
            final X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
            CertificateConversionUtil.logger.info("Converting to pem initiated..");
            serverCertificateFile = this.convertToPem(cert, serverCertificateFile);
            if (serverCertificateFile == null) {
                CertificateConversionUtil.logger.severe("Conversion to pem failed..file may have been corrupted..");
                return false;
            }
            return true;
        }
        catch (final CertificateException ex) {
            CertificateConversionUtil.logger.log(Level.SEVERE, "Generation of certificate failed..", ex);
        }
        catch (final Exception ex2) {
            CertificateConversionUtil.logger.log(Level.SEVERE, "Failed in overwriting the Server certificate file..", ex2);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException ex3) {
                CertificateConversionUtil.logger.severe("error in closing the stream");
            }
        }
        return false;
    }
    
    static {
        CertificateConversionUtil.logger = Logger.getLogger("ImportCertificateLogger");
        CertificateConversionUtil.fileAccessApi = ApiFactoryProvider.getFileAccessAPI();
        CertificateConversionUtil.conversionUtil = null;
    }
}
