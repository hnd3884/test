package com.me.devicemanagement.onpremise.server.rebrand.core;

import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.IOException;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.rebrand.core.RebrandConstants;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.adventnet.iam.security.UploadedFileItem;
import com.me.devicemanagement.framework.server.rebrand.core.RebrandUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Map;
import java.util.logging.Logger;

public class DMOnpremiseRebrandUtil
{
    protected Logger logger;
    
    public DMOnpremiseRebrandUtil() {
        this.logger = Logger.getLogger(DMOnpremiseRebrandUtil.class.getName());
    }
    
    public void getRebrandDetails(final Map rebrandDetails) throws Exception {
        this.setWebConsoleMSPAttributes(rebrandDetails);
        if (CustomerInfoUtil.getInstance().isMSP()) {
            rebrandDetails.putAll(RebrandUtil.getRebrandCommDetails());
        }
        rebrandDetails.put("logoPath", CustomerInfoUtil.getInstance().getRebrandLogoPathForWebConsole());
    }
    
    public boolean saveImageForEveryTheme(final UploadedFileItem dcLogo, final int index) throws Exception {
        String directory = DCMetaDataUtil.getInstance().getClientDataParentDir();
        directory = directory + File.separator + "images" + File.separator + RebrandConstants.THEMES[index];
        final boolean saveProdImg = RebrandUtil.saveRebrandImage(dcLogo, directory, "rebranded-logo.gif");
        this.logger.log(Level.INFO, "Copied logo in directory ####### " + RebrandConstants.THEMES[index]);
        return saveProdImg;
    }
    
    private void setWebConsoleMSPAttributes(final Map rebrandDetails) throws Exception {
        try {
            if (CustomerInfoUtil.getInstance().isMSP()) {
                final String text = SyMUtil.getSyMParameter("COPYRIGHT_TEXT");
                rebrandDetails.put("copyrightText", (text == null || text.length() <= 0) ? "" : text);
            }
            String homeUrl = SyMUtil.getSyMParameter("HOME_URL");
            final String isRebranded = SyMUtil.getSyMParameter("IS_REBRANDED");
            if (isRebranded == null || isRebranded.equalsIgnoreCase("FALSE")) {
                rebrandDetails.put("isRebranded", false);
            }
            else {
                rebrandDetails.put("isRebranded", true);
            }
            if (homeUrl == null || homeUrl.length() <= 0) {
                homeUrl = "";
            }
            else {
                homeUrl = (homeUrl.startsWith("http") ? homeUrl : ("http://" + homeUrl));
            }
            rebrandDetails.put("homeURL", homeUrl);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in setting the web console attributes", ex);
            throw ex;
        }
    }
    
    public boolean updateRebrandProperties(final UploadedFileItem dcLogo) throws Exception {
        SyMUtil.updateSyMParameter("IS_REBRANDED", "true");
        final Properties props = new Properties();
        props.setProperty("Rebrand_Logo", dcLogo.getFileName());
        final String confFileName = SyMUtil.getInstallationDir() + File.separator + "logs" + File.separator + "rebrand.props";
        try {
            final File file = new File(confFileName);
            if (file.createNewFile()) {
                this.logger.log(Level.INFO, "file created successfully");
            }
            else {
                this.logger.log(Level.INFO, "file not created, as file already exists.");
            }
        }
        catch (final IOException e) {
            this.logger.log(Level.INFO, "IOException while creating file :" + confFileName);
            return false;
        }
        FileAccessUtil.storeProperties(props, confFileName, (boolean)Boolean.TRUE);
        this.logger.log(Level.INFO, "Had written the the Properties " + props + "in " + confFileName);
        return true;
    }
    
    public boolean updateUrlForRebrandedImage(final String webUrl) throws Exception {
        if (webUrl == null) {
            return false;
        }
        if (webUrl.isEmpty()) {
            SyMUtil.deleteSyMParameter("HOME_URL");
        }
        else {
            final String existingUrl = SyMUtil.getSyMParameter("HOME_URL");
            if (existingUrl != null && webUrl != null && existingUrl.equals(webUrl)) {
                return false;
            }
        }
        SyMUtil.updateSyMParameter("HOME_URL", webUrl);
        this.logger.log(Level.INFO, "Url updated " + webUrl);
        return true;
    }
}
