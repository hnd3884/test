package com.me.devicemanagement.onpremise.server.general;

import com.adventnet.iam.security.UploadFileRule;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import com.me.devicemanagement.onpremise.server.util.ZipUtil;
import java.util.ArrayList;
import java.io.File;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.ZipUtilAPI;

public class ZipUtilImpl implements ZipUtilAPI
{
    private static final Logger LOGGER;
    
    public boolean unzip(final String srcFilePath, final String destFilePath, final boolean flag, final boolean isLocal, final String... fileNames) {
        boolean status = false;
        try {
            final String baseDir = System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za.exe";
            final List l = new ArrayList();
            l.add(baseDir);
            l.add("e");
            l.add(srcFilePath);
            l.add("-o" + destFilePath);
            l.add("-mmt=" + ZipUtil.get7ZipCoreCount());
            for (final String file : fileNames) {
                l.add(file);
            }
            if (flag) {
                l.add("-r");
                l.add("-y");
            }
            final ProcessBuilder pbuilder = new ProcessBuilder(l);
            final Process process = pbuilder.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                ZipUtilImpl.LOGGER.log(Level.INFO, line);
            }
            process.waitFor();
            final int exitValue = process.exitValue();
            if (exitValue == 0) {
                status = true;
            }
        }
        catch (final Exception e) {
            ZipUtilImpl.LOGGER.log(Level.WARNING, " Exception in unzip...", e);
            status = false;
        }
        return status;
    }
    
    public boolean checkFileExistinZip(final String srcFile, final String findFile) {
        try {
            ZipUtilImpl.LOGGER.log(Level.INFO, "Inside checkFileExistinIPA()");
            final String baseDir = System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za.exe";
            final ProcessBuilder pbuilder = new ProcessBuilder(new String[] { baseDir, "l", srcFile, findFile, "-r", "-mmt=" + ZipUtil.get7ZipCoreCount() });
            final Process process = pbuilder.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            final String temp = builder.toString();
            process.destroy();
            return temp.contains(findFile);
        }
        catch (final Exception e) {
            ZipUtilImpl.LOGGER.log(Level.WARNING, "Exception in cehcking file in ipa checkFileExistinIPA()");
            return false;
        }
    }
    
    public void createZipFile(final String sourceFolder, final String destFile, final boolean copyBeforeZip, final String copyFolder) throws Exception {
    }
    
    public void showMessage(final String message, final String status, final int index) {
    }
    
    public void setZipSantizerName(final UploadFileRule uploadFileRule, final String zipSanitizer) {
        uploadFileRule.setZipSanitizerName(zipSanitizer);
    }
    
    static {
        LOGGER = Logger.getLogger(ZipUtilImpl.class.getName());
    }
}
