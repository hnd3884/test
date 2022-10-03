package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import sun.misc.BASE64Encoder;
import java.io.File;
import java.security.cert.Certificate;
import java.io.OutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.HashMap;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;

public class IntermediateManager
{
    private static FileAccessAPI fileAccessAPI;
    private static Logger logger;
    private CertificateUtil certificateUtil;
    private static String apacheLocation;
    private static String sourceClass;
    private static IntermediateManager intermediateManager;
    
    public IntermediateManager() {
        this.certificateUtil = CertificateUtil.getInstance();
    }
    
    public static IntermediateManager getInstance() {
        if (IntermediateManager.intermediateManager == null) {
            IntermediateManager.intermediateManager = new IntermediateManager();
        }
        return IntermediateManager.intermediateManager;
    }
    
    public HashMap downloadIntermediateFromServerCertificate(final X509Certificate certificateObj, final String toDownloadFilePath) {
        final String sourceMethod = "downloadIntermediateFromServerCertificate";
        final HashMap resultMap = CertificateUtil.getInstance().downloadFile(CertificateAttributeManager.getInstance().getIntermediateFileLink(certificateObj), toDownloadFilePath);
        try {
            if (resultMap.get("isSuccess")) {
                CertificateConversionUtil.getInstance().initiateDERtoPEMConversion(toDownloadFilePath);
            }
            else if ("true".equalsIgnoreCase(resultMap.get("ldap"))) {
                IntermediateManager.fileAccessAPI.deleteFile(toDownloadFilePath);
            }
            else {
                IntermediateManager.fileAccessAPI.deleteFile(toDownloadFilePath);
            }
        }
        catch (final Exception ex) {
            IntermediateManager.logger.logp(Level.SEVERE, IntermediateManager.sourceClass, sourceMethod, toDownloadFilePath + "deleting the file failed..", ex);
        }
        return resultMap;
    }
    
    public boolean appendIntermediateFiles(final String oldFile, final String toAppendFile) {
        final String sourceMethod = "appendIntermediateFiles";
        try {
            if (!IntermediateManager.fileAccessAPI.isFileExists(oldFile)) {
                IntermediateManager.fileAccessAPI.writeFile(oldFile, "0".getBytes());
                final byte[] toAppendFileBytes = IntermediateManager.fileAccessAPI.readFileContentAsArray(toAppendFile);
                final OutputStream out = IntermediateManager.fileAccessAPI.writeFile(oldFile);
                out.write(toAppendFileBytes);
                out.flush();
                out.close();
                return true;
            }
            if (IntermediateManager.fileAccessAPI.isFileExists(oldFile)) {
                final byte[] oldFileBytes = IntermediateManager.fileAccessAPI.readFileContentAsArray(oldFile);
                IntermediateManager.fileAccessAPI.deleteFile(oldFile);
                final OutputStream out = IntermediateManager.fileAccessAPI.writeFile(oldFile);
                out.write(oldFileBytes);
                out.write(System.getProperty("line.separator").getBytes());
                out.write(IntermediateManager.fileAccessAPI.readFileContentAsArray(toAppendFile));
                out.flush();
                out.close();
                return true;
            }
            IntermediateManager.logger.info("Creation of " + oldFile + " failed..");
            return false;
        }
        catch (final IOException ex) {
            IntermediateManager.logger.logp(Level.SEVERE, IntermediateManager.sourceClass, sourceMethod, oldFile + " creation/appending failed..", ex);
        }
        catch (final Exception ex2) {
            IntermediateManager.logger.logp(Level.SEVERE, IntermediateManager.sourceClass, sourceMethod, oldFile + " creation failed..");
        }
        return false;
    }
    
    void retrieveIntermediateFromPFX(final Certificate[] certificateChain) {
        OutputStream intermediateOut = null;
        OutputStream loopIntermediateStream = null;
        boolean isPFXWriting = false;
        try {
            final String destination = IntermediateManager.apacheLocation + File.separator + "conf" + File.separator + "uploaded_files" + File.separator + "intermediate.crt";
            if (this.isIntermediateExistInPFX(certificateChain)) {
                final BASE64Encoder encoder = new BASE64Encoder();
                for (int i = 0; i < certificateChain.length; ++i) {
                    final X509Certificate currentCertificate = (X509Certificate)certificateChain[i];
                    if (currentCertificate.getBasicConstraints() != -1) {
                        if (!isPFXWriting) {
                            IntermediateManager.fileAccessAPI.writeFile(destination, "0".getBytes());
                            intermediateOut = IntermediateManager.fileAccessAPI.writeFile(destination);
                            final X509Certificate first = (X509Certificate)certificateChain[1];
                            intermediateOut.write(("-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator")).getBytes());
                            encoder.encodeBuffer(first.getEncoded(), intermediateOut);
                            intermediateOut.write(("-----END CERTIFICATE-----" + System.getProperty("line.separator")).getBytes());
                            intermediateOut.flush();
                            intermediateOut.close();
                            isPFXWriting = true;
                        }
                        else {
                            final byte[] toAppendBytes = IntermediateManager.fileAccessAPI.readFileContentAsArray(destination);
                            loopIntermediateStream = IntermediateManager.fileAccessAPI.writeFile(destination);
                            loopIntermediateStream.write(toAppendBytes);
                            loopIntermediateStream.write(("-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator")).getBytes());
                            encoder.encodeBuffer(currentCertificate.getEncoded(), loopIntermediateStream);
                            loopIntermediateStream.write(("-----END CERTIFICATE-----" + System.getProperty("line.separator")).getBytes());
                            loopIntermediateStream.flush();
                            loopIntermediateStream.close();
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            IntermediateManager.logger.log(Level.SEVERE, "Error reading pfx file to fetch intermediate", ex);
            try {
                if (intermediateOut != null && loopIntermediateStream != null) {
                    intermediateOut.close();
                    loopIntermediateStream.close();
                }
            }
            catch (final IOException ex2) {
                IntermediateManager.logger.log(Level.SEVERE, "", ex2);
            }
        }
        finally {
            try {
                if (intermediateOut != null && loopIntermediateStream != null) {
                    intermediateOut.close();
                    loopIntermediateStream.close();
                }
            }
            catch (final IOException ex3) {
                IntermediateManager.logger.log(Level.SEVERE, "", ex3);
            }
        }
    }
    
    boolean isIntermediateExistInPFX(final Certificate[] certificateChain) {
        return certificateChain != null && certificateChain.length > 1;
    }
    
    static {
        IntermediateManager.fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        IntermediateManager.logger = Logger.getLogger("ImportCertificateLogger");
        IntermediateManager.apacheLocation = System.getProperty("server.home") + File.separator + "apache";
        IntermediateManager.sourceClass = "IntermediateManager";
        IntermediateManager.intermediateManager = null;
    }
}
