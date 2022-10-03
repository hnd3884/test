package com.me.mdm.onpremise.server.settings;

import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.IOException;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.apache.tika.Tika;
import com.me.mdm.files.FileFacade;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.factory.MDMRebrandAPI;

public class MDMPRebrandImpl implements MDMRebrandAPI
{
    private static Logger logger;
    private static final String[] THEMES;
    private static final String COMPANY_NAME = "company_name";
    private static final String LOGO_PATH = "logo_path";
    private static final String DEFAULT_LOGO_PATH = "default_logo_path";
    private static final String HOME_URL = "home_url";
    private static final String IS_REBRANDED = "is_rebranded";
    
    public JSONObject getRebrandSettings(final APIRequest apiRequest) throws Exception {
        final JSONObject rebrandDetails = new JSONObject();
        String home_url = SyMUtil.getSyMParameter("HOME_URL");
        if (home_url == null) {
            home_url = "";
        }
        rebrandDetails.put("home_url", (Object)home_url);
        rebrandDetails.put("logo_path", (Object)CustomerInfoUtil.getInstance().getRebrandLogoPathForWebConsole());
        rebrandDetails.put("default_logo_path", (Object)this.getDefaultLogoPath());
        rebrandDetails.put("company_name", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getOrgName(APIUtil.optCustomerID(apiRequest.toJSONObject())));
        final String is_rebranded = SyMUtil.getSyMParameter("IS_REBRANDED");
        if (is_rebranded == null || is_rebranded.equalsIgnoreCase("false")) {
            rebrandDetails.put("is_rebranded", false);
        }
        else {
            rebrandDetails.put("is_rebranded", true);
        }
        return rebrandDetails;
    }
    
    public JSONObject saveRebrandSettings(final APIRequest apiRequest) throws Exception {
        final JSONObject jsonObject = apiRequest.toJSONObject().getJSONObject("msg_body");
        final FileFacade fileFacade = new FileFacade();
        String logoFilePathDMTemp = null;
        final Tika tika = new Tika();
        final JSONObject respJSON = new JSONObject();
        final String dispFilePathDM = null;
        final String webUrl = jsonObject.optString("home_url");
        final boolean isUrlUpdated = this.updateUrlForRebrandedImage(webUrl);
        if (isUrlUpdated) {
            respJSON.put("url_update", (Object)"success");
        }
        else {
            respJSON.put("url_update", (Object)"failed");
        }
        if (jsonObject.has("org_logo_id") && jsonObject.getBoolean("is_rebranded")) {
            final Long fileId = Long.valueOf(jsonObject.get("org_logo_id").toString());
            final String logoFilePathDM = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", (Object)fileId)).get("file_path"));
            logoFilePathDMTemp = fileFacade.getTempLocation(logoFilePathDM);
            new FileFacade().writeFile(logoFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(logoFilePathDM));
            if (logoFilePathDMTemp != null) {
                final File tFile = new File(logoFilePathDMTemp);
                final String contentType = tika.detect(tFile);
                if (!APIUtil.isAllowedImageMimeType(contentType)) {
                    throw new APIHTTPException("REBRAND003", new Object[] { "Invalid file Format" });
                }
                final File dispFile = new File(logoFilePathDMTemp);
                final String fileName = dispFile.getName();
                if (dispFile.length() > 51200L) {
                    throw new APIHTTPException("REBRAND003", new Object[] { "Image Size Larger than 50KB" });
                }
                boolean saveProdImg = true;
                for (int i = 0; i < MDMPRebrandImpl.THEMES.length; ++i) {
                    saveProdImg = this.saveImageForEveryTheme(logoFilePathDMTemp, i);
                }
                if (saveProdImg) {
                    DCEventLogUtil.getInstance().addEvent(121, APIUtil.getUserName(apiRequest.toJSONObject()), (HashMap)null, "dc.admin.rebranding.eventLog", (Object)(webUrl + "@@@" + fileName), false, APIUtil.getCustomerID(apiRequest.toJSONObject()));
                    SyMUtil.updateSyMParameter("IS_REBRANDED", "true");
                    respJSON.put("logo_update", (Object)"success");
                    this.updateRebrandProperties(fileName);
                }
                else {
                    respJSON.put("logo_update", (Object)"failed");
                }
            }
            respJSON.put("is_rebranded", true);
            MDMPRebrandImpl.logger.log(Level.INFO, "Rebrand.. Settings saved successfully.. ");
        }
        else if (jsonObject.getBoolean("is_rebranded")) {
            final String is_rebranded = SyMUtil.getSyMParameter("IS_REBRANDED");
            if (!is_rebranded.equalsIgnoreCase("true")) {
                MDMPRebrandImpl.logger.log(Level.SEVERE, "Rebrand.. Default logo param wrongly set..");
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            respJSON.put("is_rebranded", true);
        }
        else {
            if (jsonObject.has("org_logo_id") && !jsonObject.getBoolean("is_rebranded")) {
                MDMPRebrandImpl.logger.log(Level.SEVERE, "Rebrand.. Default logo vs Org logo id params wrongly set..");
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            SyMUtil.updateSyMParameter("IS_REBRANDED", "false");
            respJSON.put("is_rebranded", false);
        }
        if (jsonObject.has("company_name")) {
            MDMCustomerInfoUtil.getInstance().updateCompanyName(APIUtil.optCustomerID(apiRequest.toJSONObject()), jsonObject.get("company_name").toString());
            respJSON.put("company_name_update", (Object)"success");
        }
        else {
            respJSON.put("company_name_update", (Object)"failed");
        }
        return respJSON;
    }
    
    protected boolean saveImageForEveryTheme(final String logo, final int index) throws Exception {
        String directory = DCMetaDataUtil.getInstance().getClientDataParentDir();
        directory = directory + File.separator + "images" + File.separator + MDMPRebrandImpl.THEMES[index];
        final Tika tika = new Tika();
        final File tFile = new File(logo);
        final String contentType = tika.detect(tFile);
        final File file = new File(logo);
        final boolean copyFile = this.saveRebrandImage(file, directory, "rebranded-logo.gif");
        MDMPRebrandImpl.logger.log(Level.INFO, "Rebrand.. Copied logo in directory ####### {0}", MDMPRebrandImpl.THEMES[index]);
        return copyFile;
    }
    
    private boolean saveRebrandImage(final File file, final String directory, final String imgName) {
        String fileName = null;
        try {
            if (file.length() < 51200L && file.length() > 0L) {
                fileName = file.getName();
                final boolean copyFile = ApiFactoryProvider.getFileAccessAPI().copyFile(file.getCanonicalPath(), directory + File.separator + imgName);
                return copyFile;
            }
            MDMPRebrandImpl.logger.log(Level.WARNING, " File size is greater than 50 kb ; So couldn't copy!");
            return false;
        }
        catch (final Exception ex) {
            MDMPRebrandImpl.logger.log(Level.WARNING, "Import Form File Operation failed {0} {1}", new Object[] { fileName, ex });
            return false;
        }
    }
    
    protected boolean updateUrlForRebrandedImage(String webUrl) throws Exception {
        if (webUrl == null || webUrl.equals("")) {
            return false;
        }
        webUrl = (webUrl.startsWith("http") ? webUrl : ("http://" + webUrl));
        final String existingUrl = SyMUtil.getSyMParameter("HOME_URL");
        if (existingUrl != null && webUrl != null && existingUrl.equals(webUrl)) {
            return false;
        }
        SyMUtil.updateSyMParameter("HOME_URL", webUrl);
        return true;
    }
    
    protected void updateRebrandProperties(final String logoFileName) throws Exception {
        final Properties props = new Properties();
        props.setProperty("Rebrand_Logo", logoFileName);
        final String confFileName = SyMUtil.getInstallationDir() + File.separator + "logs" + File.separator + "rebrand.props";
        try {
            final File file = new File(confFileName);
            final boolean success = file.createNewFile();
            if (success) {
                MDMPRebrandImpl.logger.log(Level.INFO, "rebrand.props file created successfully");
            }
            else {
                MDMPRebrandImpl.logger.log(Level.INFO, "rebrand.props file not created, as file already exists.");
            }
        }
        catch (final IOException e) {
            MDMPRebrandImpl.logger.log(Level.INFO, "Rebrand.. IOException while creating file :{0}", confFileName);
        }
        FileAccessUtil.storeProperties(props, confFileName, (boolean)Boolean.TRUE);
        MDMPRebrandImpl.logger.log(Level.INFO, "Rebrand.. Had written the the Properties {0}in {1}", new Object[] { props, confFileName });
    }
    
    protected String getDefaultLogoPath() {
        return "/images/" + SyMUtil.getInstance().getTheme() + "/" + "dc-logo.gif";
    }
    
    static {
        MDMPRebrandImpl.logger = Logger.getLogger(MDMPRebrandImpl.class.getName());
        THEMES = new String[] { "blue", "green", "sdp-blue", "dm-default" };
    }
}
