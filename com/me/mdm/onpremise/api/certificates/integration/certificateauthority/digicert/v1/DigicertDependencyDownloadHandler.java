package com.me.mdm.onpremise.api.certificates.integration.certificateauthority.digicert.v1;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import com.me.mdm.files.FileFacade;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import java.io.InputStream;
import com.zoho.security.api.wrapper.ZipInputStreamWrapper;
import java.io.FileInputStream;
import java.util.List;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import java.util.regex.Pattern;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.logging.Level;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.digicert.v1.DigicertDependenciesCheckHandler;
import java.util.logging.Logger;

public class DigicertDependencyDownloadHandler
{
    public static Logger logger;
    private static DigicertDependencyDownloadHandler handler;
    
    public static DigicertDependencyDownloadHandler getInstance() {
        if (DigicertDependencyDownloadHandler.handler == null) {
            DigicertDependencyDownloadHandler.handler = new DigicertDependencyDownloadHandler();
        }
        return DigicertDependencyDownloadHandler.handler;
    }
    
    public boolean downloadDependencies() throws APIHTTPException {
        try {
            if (!DigicertDependenciesCheckHandler.checkIfDependencyFolerExists()) {
                final Properties properties = new MDMUtil().getMDMApplicationProperties();
                final String downloadURL = properties.getProperty("digicertdependencyjarsdownloadurl");
                final String checksum = properties.getProperty("digicertdependencyjarschecksum");
                ApiFactoryProvider.getFileAccessAPI().createDirectory("DigicertTemp");
                final String fileLocation = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "DigicertTemp" + File.separator + "digicertdependencies";
                DigicertDependencyDownloadHandler.logger.log(Level.INFO, "Downloading digicert dependencies from {0}", downloadURL);
                final DownloadStatus downloadstatus = DownloadManager.getInstance().downloadBinaryFile(downloadURL, fileLocation, checksum, new SSLValidationType[0]);
                DigicertDependencyDownloadHandler.logger.log(Level.INFO, "Downloaded file is available at {0}", fileLocation);
                if (downloadstatus.getStatus() != 0) {
                    DigicertDependencyDownloadHandler.logger.log(Level.WARNING, "Unable to download digicert dependencies {0}", downloadstatus.getErrorMessage());
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
                final String destinationPath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "DigicertTemp";
                this.unzip(fileLocation, destinationPath);
                final String dependencyFileLocation = destinationPath + File.separator + "ManageEngine" + File.separator + "DesktopCentral_Server" + File.separator + "mdm" + File.separator + "integration";
                final String dependencyFileCopyToLocation = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "mdm" + File.separator + "integration";
                FileAccessUtil.copyDirectoryWithinServer(dependencyFileLocation, dependencyFileCopyToLocation);
                final String jarFileLocation = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "DigicertTemp" + File.separator + "ManageEngine" + File.separator + "DesktopCentral_Server" + File.separator + "lib" + File.separator + "third_party_jars" + File.separator + "digicert";
                final String jarFileCopyToLocation = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "lib" + File.separator + "third_party_jars" + File.separator + "digicert";
                FileAccessUtil.copyDirectoryWithinServer(jarFileLocation, jarFileCopyToLocation);
                ApiFactoryProvider.getFileAccessAPI().deleteFile(fileLocation);
                ApiFactoryProvider.getFileAccessAPI().deleteDirectory(new File(fileLocation).getParent());
            }
            MessageProvider.getInstance().unhideMessage("MDM_DIGICERT_RESTART_REQUIRED_NEW");
            return true;
        }
        catch (final Exception e) {
            DigicertDependencyDownloadHandler.logger.log(Level.SEVERE, "Exception while downloading digicert dependencies.", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void unzip(final String sourceZipPath, final String destinationPath) throws IOException {
        DigicertDependencyDownloadHandler.logger.log(Level.FINE, "Extracting the digicert dependencies zip");
        final List<String> allowedExtn = new ArrayList<String>();
        allowedExtn.add("cer");
        allowedExtn.add("jar");
        final Pattern pattern = Pattern.compile("(application/pkix-cert|application/java-archive)");
        final ZipSanitizerRule zipSanitizerRule = new ZipSanitizerRule("zipRule", (String)null, -1L, -1, -1, (List)allowedExtn, (List)null, pattern, (Pattern)null);
        ZipInputStreamWrapper zis = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sourceZipPath);
            zis = new ZipInputStreamWrapper((InputStream)fis, zipSanitizerRule);
            for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry()) {
                if (FileUploadUtil.hasVulnerabilityInFileName(e.getName())) {
                    throw new Exception("Path traversal detected in the zip");
                }
                final String filePath = destinationPath + File.separator + e.getName();
                FileFacade.getInstance().validateFileToUnzip(filePath, destinationPath);
                if (!e.isDirectory()) {
                    this.extractFile(zis, filePath);
                }
                else {
                    final File dir = new File(filePath);
                    dir.mkdirs();
                }
            }
            zis.close();
            DigicertDependencyDownloadHandler.logger.log(Level.FINE, "Digicert dependencies extraction completed");
        }
        catch (final Exception e2) {
            DigicertDependencyDownloadHandler.logger.log(Level.SEVERE, "Exception while extracting digicert dependencies, ", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (zis != null) {
                zis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    private void extractFile(final ZipInputStreamWrapper zipIn, final String filePath) throws Exception {
        final int BUFFER_SIZE = 4096;
        if (!FileFacade.getInstance().testForPathTraversal(filePath)) {
            throw new Exception("Path traversal detected in filepath");
        }
        final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        try {
            final byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn, 0, bytesIn.length)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
        catch (final Exception e) {
            DigicertDependencyDownloadHandler.logger.log(Level.SEVERE, "Exception while extracting digicert dependencies, ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            bos.close();
        }
    }
    
    static {
        DigicertDependencyDownloadHandler.logger = Logger.getLogger(DigicertDependencyDownloadHandler.class.getName());
        DigicertDependencyDownloadHandler.handler = null;
    }
}
