package com.adventnet.tools.prevalent;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class RestoreBackUp
{
    private static final Logger LOGGER;
    private static ArrayList<Integer> restorationErrorCodes;
    
    public void licenseRestoreValidation(final ThreadWorn ntw, final String licenseBackUpDir, final String licenseDir, final String homeDir) {
        boolean returnValue = false;
        if (ntw != null) {
            returnValue = ntw.isBare();
        }
        if (!returnValue && licenseBackUpDir != null && this.isErrorCodeAllowedForRestoration(LUtil.getErrorDetails())) {
            RestoreBackUp.LOGGER.info(" license restoration started ");
            this.startLicenseRestoration(licenseBackUpDir, licenseDir, homeDir);
        }
    }
    
    private void startLicenseRestoration(final String licenseBackUpDir, final String licenseDir, final String homeDir) {
        try {
            if (this.isBackUpFilesExistence(licenseBackUpDir, homeDir)) {
                this.copyFile(homeDir + File.separator + licenseBackUpDir + File.separator + "product.dat", homeDir + File.separator + licenseDir + File.separator + "product.dat");
                this.copyFile(homeDir + File.separator + licenseBackUpDir + File.separator + "petinfo.dat", homeDir + File.separator + licenseDir + File.separator + "petinfo.dat");
                this.copyFile(homeDir + File.separator + licenseBackUpDir + File.separator + "AdventNetLicense.xml", homeDir + File.separator + licenseDir + File.separator + "AdventNetLicense.xml");
                RestoreBackUp.LOGGER.info("license restoration completed ");
                Wield.getInstance().validateInvoke("License Agreement", homeDir, false, licenseDir, true);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean isBackUpFilesExistence(final String licenseBackUpDir, final String homeDir) {
        final File prdFile = new File(homeDir + File.separator + licenseBackUpDir + File.separator + "product.dat");
        final File petFile = new File(homeDir + File.separator + licenseBackUpDir + File.separator + "petinfo.dat");
        final File licenseFile = new File(homeDir + File.separator + licenseBackUpDir + File.separator + "AdventNetLicense.xml");
        return prdFile.exists() && petFile.exists() && licenseFile.exists();
    }
    
    public void copyFile(final String fromloc, final String toloc) throws IOException {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            final int bufLength = 1024;
            int c = 0;
            int br = 0;
            fos = new FileOutputStream(toloc);
            fis = new FileInputStream(fromloc);
            bis = new BufferedInputStream(fis, bufLength);
            final byte[] bufr = new byte[bufLength];
            while ((c = bis.read(bufr, 0, bufLength)) != -1) {
                fos.write(bufr, 0, c);
                br += c;
            }
            RestoreBackUp.LOGGER.info("Copied back up files successfully ");
        }
        catch (final Exception e) {
            RestoreBackUp.LOGGER.info("Error in copying file ");
            e.printStackTrace();
        }
        finally {
            if (fos != null) {
                fos.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    private boolean isErrorCodeAllowedForRestoration(final ErrorDetails errorDet) {
        return RestoreBackUp.restorationErrorCodes.contains(errorDet.getErrorCode());
    }
    
    public void backUpLicenseFiles(final String homeDir, final String licenseFileDir, String licenseBackUpDir) {
        try {
            if (licenseBackUpDir == null) {
                licenseBackUpDir = "keybkp";
            }
            final File backUpDir = new File(homeDir + File.separator + licenseBackUpDir);
            if (!backUpDir.exists()) {
                backUpDir.mkdirs();
            }
            this.copyFile(homeDir + File.separator + licenseFileDir + File.separator + "product.dat", homeDir + File.separator + licenseBackUpDir + File.separator + "product.dat");
            this.copyFile(homeDir + File.separator + licenseFileDir + File.separator + "petinfo.dat", homeDir + File.separator + licenseBackUpDir + File.separator + "petinfo.dat");
            this.copyFile(homeDir + File.separator + licenseFileDir + File.separator + "AdventNetLicense.xml", homeDir + File.separator + licenseBackUpDir + File.separator + "AdventNetLicense.xml");
            System.out.println("license back up completed ..");
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(RestoreBackUp.class.getName());
        RestoreBackUp.restorationErrorCodes = null;
        (RestoreBackUp.restorationErrorCodes = new ArrayList<Integer>()).add(501);
        RestoreBackUp.restorationErrorCodes.add(469);
        RestoreBackUp.restorationErrorCodes.add(474);
        RestoreBackUp.restorationErrorCodes.add(499);
        RestoreBackUp.restorationErrorCodes.add(520);
        RestoreBackUp.restorationErrorCodes.add(529);
        RestoreBackUp.restorationErrorCodes.add(530);
        RestoreBackUp.restorationErrorCodes.add(531);
        RestoreBackUp.restorationErrorCodes.add(532);
        RestoreBackUp.restorationErrorCodes.add(534);
        RestoreBackUp.restorationErrorCodes.add(535);
        RestoreBackUp.restorationErrorCodes.add(536);
    }
}
