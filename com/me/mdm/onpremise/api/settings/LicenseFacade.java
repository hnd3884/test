package com.me.mdm.onpremise.api.settings;

import java.util.ArrayList;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Locale;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Collection;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.mdm.api.APIUtil;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.fos.FailoverServerUtil;
import com.me.devicemanagement.onpremise.server.license.LicenseUtil;
import com.me.devicemanagement.framework.server.license.LicenseListenerHandler;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.license.LicenseEvent;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.tools.prevalent.WebLicenseUpgrade;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.tools.prevalent.Validation;
import com.adventnet.tools.prevalent.Wield;
import com.adventnet.tools.prevalent.CMDClass;
import com.adventnet.i18n.I18N;
import com.me.mdm.http.HttpException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import java.io.File;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.files.FileFacade;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.factory.MDMLicenseDetailsAPI;

public class LicenseFacade implements MDMLicenseDetailsAPI
{
    Logger logger;
    private String product_version;
    private String product_name;
    private String build_number;
    private String number_of_mobiledevices_purchased;
    private String number_of_mobiledevices_managed;
    private String number_of_users_purchased;
    private String number_of_users_added;
    private String is_multi_lang;
    private String is_fos_enabled;
    private String build_date;
    private String no_of_daystogo;
    private String mobile_device_list;
    private String licensed_to;
    private String product_expiry_date;
    private String expiry_date;
    private String ams_expire_date;
    private String license_type;
    private String upgrade_msg;
    private String primary_contact;
    private String license_path;
    private String upgrade_status;
    private String license_status;
    private String upgrade_success;
    private String upgrade_remarks;
    
    public LicenseFacade() {
        this.logger = Logger.getLogger(LicenseFacade.class.getName());
        this.product_version = "product_version";
        this.product_name = "product_name";
        this.build_number = "build_number";
        this.number_of_mobiledevices_purchased = "number_of_mobiledevices_purchased";
        this.number_of_mobiledevices_managed = "number_of_mobiledevices_managed";
        this.number_of_users_purchased = "number_of_users_purchased";
        this.number_of_users_added = "number_of_users_added";
        this.is_multi_lang = "is_multi_lang";
        this.is_fos_enabled = "is_fos_enabled";
        this.build_date = "build_date";
        this.no_of_daystogo = "no_of_daystogo";
        this.mobile_device_list = "mobile_device_list";
        this.licensed_to = "licensed_to";
        this.product_expiry_date = "product_expiry_date";
        this.expiry_date = "expiry_date";
        this.ams_expire_date = "ams_expire_date";
        this.license_type = "license_type";
        this.upgrade_msg = "upgrade_msg";
        this.primary_contact = "primary_contact";
        this.license_path = "license_path";
        this.upgrade_status = "upgrade_status";
        this.license_status = "license_status";
        this.upgrade_success = "upgrade_success";
        this.upgrade_remarks = "upgrade_remarks";
    }
    
    public JSONObject storeLicense(final JSONObject jsonObject) throws Exception {
        final JSONObject requestJSON = jsonObject.getJSONObject("msg_body");
        JSONObject responseJson = new JSONObject();
        try {
            final FileFacade fileFacade = new FileFacade();
            String licFilePathDMTemp = null;
            try {
                if (requestJSON.has(this.license_path)) {
                    final Long fileId = Long.valueOf(requestJSON.get(this.license_path).toString());
                    final String licFilePathDM = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", (Object)fileId)).get("file_path"));
                    licFilePathDMTemp = fileFacade.getTempLocation(licFilePathDM);
                    new FileFacade().writeFile(licFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(licFilePathDM));
                }
            }
            catch (final Exception e) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            if (licFilePathDMTemp != null) {
                final File dispFile = new File(licFilePathDMTemp);
                final String fileName = dispFile.getName();
                if (dispFile.length() > 51200L) {
                    throw new APIHTTPException("LIC0002", new Object[] { "Image Size Larger than 50KB" });
                }
                if (fileName != null && FileUploadUtil.hasVulnerabilityInFileName(fileName)) {
                    this.logger.log(Level.WARNING, "Vulnurale request is recieved so ging to reject. Vulnerable entity : {0}", fileName);
                    throw new HttpException(403, "Request Refused");
                }
                this.logger.log(Level.INFO, "license file name is {0} ", fileName);
                if (fileName != null) {
                    String licensePath = this.storeFile(fileName, dispFile);
                    this.logger.log(Level.INFO, "license file stored path {0} ", licensePath);
                    if (licensePath != null) {
                        licensePath = licensePath.trim();
                    }
                    responseJson = this.upgradeLicense(licensePath);
                }
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in saving proxy details", ex);
            throw new APIHTTPException("LIC0004", new Object[0]);
        }
        return responseJson;
    }
    
    private JSONObject applyLicensAndVerify(final String licensePath) {
        final JSONObject response = new JSONObject();
        final String productHomeDir = System.getProperty("server.home");
        final String oldLicensePath = productHomeDir + File.separator + "lib" + File.separator + "AdventNetLicense.xml";
        final String backupLicensePath = productHomeDir + File.separator + "lib" + File.separator + "AdventNetLicense_old.xml";
        boolean isDeviceLicenseLimitExceed = false;
        boolean licenseAppliedStatus = false;
        boolean isTechLicenseLimitExceed = false;
        try {
            String upgradestatus = I18N.getMsg("dc.license.upgraded_successfully", new Object[0]);
            response.put("deviceLicenseMismatchStatus", (Object)"");
            response.put(this.upgrade_status, (Object)"");
            response.put("TechLicenseLimitExceedStatus", (Object)"");
            response.put("licenseCorrupted", (Object)"");
            this.logger.log(Level.INFO, "old license path {0} ", oldLicensePath);
            this.logger.log(Level.INFO, "new/backup license path {0} ", backupLicensePath);
            this.copyfile(oldLicensePath, backupLicensePath);
            final String home = productHomeDir;
            final CMDClass cmd = new CMDClass();
            boolean status = false;
            final Wield w = Wield.getInstance();
            final Validation valid = Validation.getInstance();
            final String user = cmd.getUserList(licensePath).elementAt(0);
            status = w.doValidation(home, user, licensePath, false, true);
            this.logger.log(Level.INFO, "License Validated and valid status is {0} ", status);
            if (status) {
                valid.copyLicenseFile(home, licensePath);
                LicenseProvider.reGenerateLicenseHandler();
                licenseAppliedStatus = true;
                final int technicianCount = DMUserHandler.getUsersCountWithLogin();
                final String noOfTechniciansNew = LicenseProvider.getInstance().getNoOfTechnicians();
                final String newLicenseType = LicenseProvider.getInstance().getLicenseType();
                int noOftechs = 0;
                if (noOfTechniciansNew != null && !noOfTechniciansNew.equalsIgnoreCase("")) {
                    noOftechs = Integer.valueOf(noOfTechniciansNew);
                    this.logger.log(Level.INFO, "Technicians added :{0} Purchased : {1}", new Object[] { technicianCount, noOftechs });
                    if (technicianCount > noOftechs) {
                        this.logger.log(Level.WARNING, "LicenseUpgrade if Already added tech count > License TechCount true");
                        response.put("AddedTech", technicianCount);
                        response.put("LicenceTech", noOftechs);
                        isTechLicenseLimitExceed = true;
                        final String purchaseLink = I18N.getMsg("dc.license.purchase_technician_license_msg", new Object[] { I18N.getMsg(ProductUrlLoader.getInstance().getValue("store_url"), new Object[0]) });
                        upgradestatus = I18N.getMsg("dc.license.technician_exceed_alert", new Object[] { technicianCount, noOftechs, ProductUrlLoader.getInstance().getValue("displayname") });
                        response.put("TechLicenseLimitExceedStatus", (Object)upgradestatus);
                        response.put(this.upgrade_status, (Object)(upgradestatus + purchaseLink));
                        licenseAppliedStatus = false;
                    }
                }
                else if (newLicenseType != null && !newLicenseType.equalsIgnoreCase("F")) {
                    response.put("licenseCorrupted", (Object)this.setCorruptedLicenseMsg());
                    response.put(this.upgrade_status, (Object)upgradestatus);
                    licenseAppliedStatus = false;
                }
                final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
                this.logger.log(Level.WARNING, "LicenseUpgrade No of mobiles Managed : {0} ", managedDeviceCount);
                String noOfMobileDevicesManaged = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
                if (LicenseProvider.getInstance().getMDMLicenseAPI().isNoOfMobileDevicesManagedIsEmptyString()) {
                    noOfMobileDevicesManaged = "";
                }
                this.logger.log(Level.WARNING, "LicenseUpgrade no Of Mobile Devices purchased : {0} ", noOfMobileDevicesManaged);
                if (noOfMobileDevicesManaged != null && !noOfMobileDevicesManaged.equalsIgnoreCase("unlimited")) {
                    if (!noOfMobileDevicesManaged.equalsIgnoreCase("")) {
                        this.logger.log(Level.WARNING, "LicenseUpgrade inside the mobile check so no unlimited license ");
                        final int noOfMobileDevices = Integer.valueOf(noOfMobileDevicesManaged);
                        if (managedDeviceCount > noOfMobileDevices) {
                            this.logger.log(Level.WARNING, "LicenseUpgrade if managedDeviceCount > noOfMobileDevices purchased  true");
                            response.put("enrolledMobileDevicesCount", managedDeviceCount);
                            response.put("noOfDevice", (Object)noOfMobileDevicesManaged);
                            isDeviceLicenseLimitExceed = true;
                            final String purchaseLink2 = I18N.getMsg("dc.license.purchase_device_license_msg", new Object[0]);
                            upgradestatus = I18N.getMsg("dc.license.managed_device_limit_exceed_alert", new Object[] { managedDeviceCount, noOfMobileDevicesManaged });
                            response.put("deviceLicenseMismatchStatus", (Object)upgradestatus);
                            response.put(this.upgrade_status, (Object)(upgradestatus + purchaseLink2));
                            this.logger.log(Level.WARNING, "LicenseUpgrade upgradestatus {0} ", upgradestatus);
                            licenseAppliedStatus = false;
                        }
                    }
                    else {
                        response.put("licenseCorrupted", (Object)this.setCorruptedLicenseMsg());
                        response.put(this.upgrade_status, (Object)this.setCorruptedLicenseMsg());
                        licenseAppliedStatus = false;
                    }
                    w.validateInvoke("Mobile Device Manager");
                }
                final String deviceLicenseMismatchStatus = (String)response.get("deviceLicenseMismatchStatus");
                final String techLicenseLimitExceedStatus = (String)response.get("TechLicenseLimitExceedStatus");
                if (!deviceLicenseMismatchStatus.isEmpty() || !techLicenseLimitExceedStatus.isEmpty()) {
                    upgradestatus = I18N.getMsg("dc.license.tech_device_exceed_alert", new Object[] { technicianCount, managedDeviceCount, noOftechs, noOfMobileDevicesManaged });
                    final String purchaseLink3 = I18N.getMsg("dc.license.purchase_tech_device_license_msg", new Object[] { I18N.getMsg(ProductUrlLoader.getInstance().getValue("store_url"), new Object[0]) });
                    response.put(this.upgrade_status, (Object)(upgradestatus + purchaseLink3));
                }
            }
            else {
                licenseAppliedStatus = false;
                this.logger.log(Level.WARNING, "LicenseUpgrade License Validation at Wield Level is failed");
            }
            this.logger.log(Level.INFO, "licenseAppliedStatus is {0}", licenseAppliedStatus);
            if (!licenseAppliedStatus) {
                this.logger.log(Level.WARNING, "LicenseUpgrade license revert started ");
                final WebLicenseUpgrade lt1 = new WebLicenseUpgrade(productHomeDir, "lib", backupLicensePath);
                LicenseProvider.reGenerateLicenseHandler();
                status = lt1.isValidLicense();
                this.logger.log(Level.WARNING, "LicenseUpgrade license revert status {0}", status);
            }
            else {
                SyMUtil.deleteSyMParameter("free_edition_computer_defined");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in LicenseUpgradeAction ", e);
        }
        finally {
            LicenseProvider.getInstance().setFreeEditionConfiguredStatus();
            this.deleteBackupLicenseFile(backupLicensePath);
        }
        response.put("licenseAppliedStatus", licenseAppliedStatus);
        return response;
    }
    
    private void deleteBackupLicenseFile(final String filePath) {
        final File f1 = new File(filePath);
        f1.delete();
    }
    
    private String setCorruptedLicenseMsg() {
        String upgradestatus = null;
        try {
            upgradestatus = I18N.getMsg("desktopcentral.admin.dcLicenseCorrupted.license_corruped_message", new Object[] { I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]) });
            this.logger.log(Level.WARNING, "LicenseUpgrade upgradestatus {0} ", upgradestatus);
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Caught an exception while setting Corrupted License Message:", exception);
        }
        return upgradestatus;
    }
    
    private JSONObject upgradeLicense(final String licensePath) {
        String upgradestatus = null;
        boolean status = Boolean.FALSE;
        final JSONObject responseJson = new JSONObject();
        try {
            final LicenseProvider licenseProvider = LicenseProvider.getInstance();
            final LicenseEvent licEvent = new LicenseEvent();
            responseJson.put(this.upgrade_success, (Object)Boolean.FALSE);
            licEvent.oldLicenseDetails = new HashMap(licenseProvider.getmoduleProMap());
            final String previouslicenseType = licenseProvider.getLicenseType();
            final JSONObject applyStatusJSON = this.applyLicensAndVerify(licensePath);
            status = Boolean.valueOf(applyStatusJSON.get("licenseAppliedStatus").toString());
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            this.logger.log(Level.INFO, "License Applied Status {0}", status);
            responseJson.put(this.license_status, status);
            if (status) {
                LicenseProvider.reGenerateLicenseHandler();
                upgradestatus = I18N.getMsg("dc.license.upgraded_successfully", new Object[0]);
                if (!LicenseProvider.getInstance().isLanguagePackEnabled()) {
                    this.updateAAAUserProfile();
                }
                final String productType = licenseProvider.getProductType();
                final String licenseType = licenseProvider.getLicenseType();
                licenseProvider.rolehandleForLicenseChage(productType);
                try {
                    LicenseListenerHandler.getInstance().invokeLicenseChangedListeners(licEvent);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.INFO, "Exception in invoking license listeners", ex);
                }
                LicenseUtil.addOrUpdateLicenseExpiryMsgDetails();
                FailoverServerUtil.UpdateFOSLicenseMsgDetails();
                SyMUtil.logLicenseDetails();
                SyMUtil.writeLicenseTypeInFile();
                responseJson.put(this.upgrade_success, (Object)Boolean.TRUE);
                SyMUtil.updateSyMParameter("IS_RESTART_TRIGGERED", "false");
                final String usertype = LicenseProvider.getInstance().getLicenseUserType();
                final String licenseDays = Long.toString(LicenseProvider.getInstance().getEvaluationDays());
                SyMUtil.updateSyMParameter("licenseusertype", usertype);
                SyMUtil.updateSyMParameter("licenseType", licenseType);
                SyMUtil.updateSyMParameter("productType", productType);
                SyMUtil.updateSyMParameter("licenseExpiryDays", licenseDays);
                if (previouslicenseType.equalsIgnoreCase("T") || previouslicenseType.equalsIgnoreCase("F")) {
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("showGdprWidget", "true", (long)CustomerInfoUtil.getInstance().getCustomerId());
                }
                responseJson.put(this.upgrade_status, (Object)upgradestatus);
                DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, "dc.license.upgraded_successfully", (Object)null, true);
            }
            else {
                DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, "mdm.license.upgrade_failed", (Object)null, true);
                this.logger.log(Level.WARNING, "LicenseUpgrade upgrade upgradestatus: {0} ", upgradestatus);
                final String deviceLicenseMismatchStatus = (String)applyStatusJSON.get("deviceLicenseMismatchStatus");
                this.logger.log(Level.WARNING, "LicenseUpgrade upgrade deviceLicenseMismatchStatus: {0} ", deviceLicenseMismatchStatus);
                final String techLicenseLimitExceedStatus = (String)applyStatusJSON.get("TechLicenseLimitExceedStatus");
                this.logger.log(Level.WARNING, "LicenseUpgrade upgrade TechLicenseLimitExceedStatus: {0} ", techLicenseLimitExceedStatus);
                final String licenseCorrupted = (String)applyStatusJSON.get("licenseCorrupted");
                if (deviceLicenseMismatchStatus.isEmpty() && techLicenseLimitExceedStatus.isEmpty() && licenseCorrupted.isEmpty()) {
                    upgradestatus = I18N.getMsg("dc.license.enter_proper_license", new Object[] { "<a target='_blank' href='mailto:" + I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]) + "' >" + I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]) + "</a>", "<span class=\"bodytextunline\"><a href=\"supportPage.do?actionToCall=supportFile&selectedTreeElem=supportFileLayout\" target=\"_blank\">", "</a></span>" });
                }
                else {
                    upgradestatus = (String)applyStatusJSON.get(this.upgrade_status);
                }
                responseJson.put(this.upgrade_status, (Object)upgradestatus);
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception during license upgrade : ", ex2);
            try {
                upgradestatus = I18N.getMsg("dc.license.not_applied_properly", new Object[0]);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception while getting i18n ", e);
            }
            responseJson.put(this.upgrade_status, (Object)upgradestatus);
        }
        try {
            final boolean upgradeSuccess = responseJson.optBoolean(this.upgrade_success, false);
            if (upgradeSuccess) {
                final UserManagementUtil userManagementUtil = new UserManagementUtil();
                final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
                if (userManagementUtil.isDefaultPasswordUsed((long)defaultAdminUVHLoginID)) {
                    responseJson.put(this.upgrade_remarks, (Object)I18N.getMsg("mdmp.license.defaultAdmin.upgrade_success_remarks", new Object[0]));
                    this.logOutDefaultAdmin();
                }
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception while checking for default admin credentials workflow : ", ex2);
        }
        return responseJson;
    }
    
    private void copyfile(final String srFile, final String dtFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            final File f1 = new File(srFile);
            final File f2 = new File(dtFile);
            in = new FileInputStream(f1);
            out = new FileOutputStream(f2);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            this.logger.log(Level.INFO, "File copied.");
        }
        catch (final FileNotFoundException ex) {
            this.logger.log(Level.WARNING, "{0} in  the specified directory.", ex.getMessage());
            System.exit(0);
        }
        catch (final IOException e) {
            this.logger.log(Level.WARNING, e.getMessage());
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.WARNING, e2.getMessage());
            }
        }
    }
    
    private String storeFile(final String fileName, final File file) throws Exception {
        FileOutputStream fout = null;
        File saveFile = null;
        try {
            final InputStream fileInput = new FileInputStream(file);
            final byte[] fileByte = new byte[fileInput.available()];
            fileInput.read(fileByte);
            final String currentPath = System.getProperty("user.dir");
            final String directory = currentPath + File.separator + "license";
            final File saveDir = new File(directory);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            saveFile = new File(directory + File.separator + fileName);
            fout = new FileOutputStream(saveFile);
            fout.write(fileByte);
            fout.close();
            this.logger.log(Level.INFO, "The License File {0} stored successfully...", fileName);
            return saveFile.getAbsolutePath();
        }
        catch (final FileNotFoundException ex) {
            this.logger.log(Level.WARNING, "Exception while storing the licence file under license directory ", ex);
            throw ex;
        }
        catch (final IOException ex2) {
            this.logger.log(Level.WARNING, "Exception while storing the licence file under license directory ", ex2);
            throw ex2;
        }
        finally {
            fout.close();
        }
    }
    
    public JSONObject getLicenseDetails(final JSONObject requestJSON) {
        final JSONObject response = new JSONObject();
        String licenseType = "Not Available";
        final String productName = "Not Available";
        final String productVersion = "Not Available";
        final String buildnumber = "Not Available";
        String upgradeMsg = "";
        boolean isFreeEditionDO = false;
        try {
            final APIUtil apiUtil = new APIUtil();
            final boolean isAdminUser = apiUtil.checkRolesForCurrentUser(new String[] { "Common_Write" });
            String licenseTypeMessage = I18N.getMsg("dc.license.professional_trial_version", new Object[0]);
            response.put(this.product_version, (Object)SyMUtil.getProductProperty("productversion"));
            response.put(this.product_name, (Object)LicenseProvider.getInstance().getProductName());
            response.put(this.build_number, (Object)SyMUtil.getProductProperty("buildnumber"));
            response.put(this.number_of_mobiledevices_purchased, (Object)LicenseProvider.getInstance().getNoOfMobileDevicesManaged());
            response.put(this.number_of_mobiledevices_managed, ManagedDeviceHandler.getInstance().getManagedDeviceCount());
            final String numberOfUsersPurchased = LicenseProvider.getInstance().getNoOfTechnicians();
            if (numberOfUsersPurchased != null) {
                response.put(this.number_of_users_purchased, (Object)numberOfUsersPurchased);
            }
            response.put(this.number_of_users_added, DMUserHandler.getUsersCountWithLogin());
            final String isMultiLang = LicenseProvider.getInstance().isLanguagePackEnabled() ? I18N.getMsg("dc.common.Enabled", new Object[0]) : I18N.getMsg("dc.common.Disabled", new Object[0]);
            response.put(this.is_multi_lang, (Object)isMultiLang);
            final String isFosEnabled = LicenseProvider.getInstance().isFosEnabled() ? I18N.getMsg("dc.common.Enabled", new Object[0]) : I18N.getMsg("dc.common.Disabled", new Object[0]);
            response.put(this.is_fos_enabled, (Object)isFosEnabled);
            final String installationdateInLong = SyMUtil.getInstallationProperty("it");
            if (installationdateInLong != null && !installationdateInLong.equals("")) {
                final long instDate = new Long(installationdateInLong);
                response.put(this.build_date, (Object)Utils.getDate(Long.valueOf(instDate)));
            }
            else {
                response.put(this.build_date, (Object)"--");
            }
            licenseType = LicenseProvider.getInstance().getLicenseType();
            if (licenseType != null) {
                if (licenseType.equals("T")) {
                    licenseTypeMessage = I18N.getMsg("dc.license.professional_trial_version", new Object[0]);
                    upgradeMsg = "dc.license.extend_your_trial_period";
                    final String noOfDaysToGo = String.valueOf(LicenseProvider.getInstance().getEvaluationDays());
                    this.logger.log(Level.INFO, "No of Days to Go {0}", noOfDaysToGo);
                    response.put(this.no_of_daystogo, (Object)noOfDaysToGo);
                    final String licensedTo = LicenseProvider.getInstance().getUserName();
                    response.put(this.licensed_to, (Object)licensedTo);
                }
                else if (licenseType.equals("F")) {
                    if (!isAdminUser) {
                        throw new APIHTTPException("LIC0001", new Object[0]);
                    }
                    licenseTypeMessage = I18N.getMsg("dc.license.edtion.free_edition", new Object[0]);
                    upgradeMsg = "dc.license.upgrade_to_professional";
                    final boolean isFreeEditionForwardRequired = LicenseProvider.getInstance().isFreeEditionForwardRequired();
                    if (isFreeEditionForwardRequired) {
                        final List managedDeviceList = ManagedDeviceHandler.getInstance().getManagedDeviceList();
                        response.put("mobileDeviceList", (Collection)managedDeviceList);
                        isFreeEditionDO = true;
                    }
                    final String licensedTo = LicenseProvider.getInstance().getUserName();
                    response.put(this.licensed_to, (Object)licensedTo);
                }
                else if (licenseType.equals("R")) {
                    String productExpiryDate = LicenseProvider.getInstance().getProductExpiryDate();
                    this.logger.log(Level.INFO, "Product Expiry Date {0}", productExpiryDate);
                    final String editionType = LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType();
                    licenseTypeMessage = I18N.getMsg("dc.license.professional_registered_version", new Object[0]);
                    final String s = editionType;
                    LicenseProvider.getInstance().getMDMLicenseAPI();
                    if (s.equalsIgnoreCase("Enterprise")) {
                        licenseTypeMessage = I18N.getMsg("dc.license.enterprise_registered_version", new Object[0]);
                    }
                    else {
                        final String s2 = editionType;
                        LicenseProvider.getInstance().getMDMLicenseAPI();
                        if (s2.equalsIgnoreCase("Standard")) {
                            licenseTypeMessage = I18N.getMsg("dc.license.standard_registered_version", new Object[0]);
                        }
                    }
                    final String numberOfMobileDevicesPurchased = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
                    if (numberOfMobileDevicesPurchased.equals("unlimited")) {
                        upgradeMsg = "dc.license.renew_subscription";
                    }
                    else {
                        upgradeMsg = "dc.license.increase_mobile_count";
                    }
                    final String licensedTo2 = LicenseProvider.getInstance().getCompanyName();
                    response.put(this.licensed_to, (Object)licensedTo2);
                    if (!productExpiryDate.equalsIgnoreCase("never")) {
                        final Long ExpiryDateInLong = LicenseUtil.converDateToLong(productExpiryDate, " ");
                        this.logger.log(Level.INFO, "Product Expiry Date Converted in Long {0}", ExpiryDateInLong);
                        productExpiryDate = Utils.getDate(ExpiryDateInLong);
                        response.put(this.expiry_date, (Object)DateTimeUtil.longdateToString((long)ExpiryDateInLong, "MM/dd/yyyy"));
                    }
                    if (LicenseProvider.getInstance().isLicenseExpiryMsgRequired() || productExpiryDate.equalsIgnoreCase("never")) {
                        response.put(this.product_expiry_date, (Object)productExpiryDate);
                    }
                    if (productExpiryDate != null && productExpiryDate.equalsIgnoreCase("never") && LicenseProvider.getInstance().isLicenseExpiryMsgRequired()) {
                        final String amsExpireDate = LicenseProvider.getInstance().getProductAMSExpiryDate();
                        if (amsExpireDate != null) {
                            response.put(this.ams_expire_date, (Object)amsExpireDate);
                        }
                    }
                    final String primaryContact = LicenseProvider.getInstance().getPrimaryContact();
                    response.put(this.primary_contact, (Object)((primaryContact != null) ? primaryContact : ""));
                }
            }
            response.put(this.license_type, (Object)licenseTypeMessage);
            response.put(this.upgrade_msg, (Object)I18N.getMsg(upgradeMsg, new Object[] { "<span class=\"bodytextunline\"><a href=\"mailto:sales@manageengine.com\" target=\"_blank\">ZOHO Corp.</a></span>" }));
            response.put("free_edition_page", false);
            if (licenseType != null && licenseType.equals("F") && isFreeEditionDO) {
                response.put("free_edition_page", true);
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting proxy details...", ex);
            throw new APIHTTPException("LIC0003", new Object[0]);
        }
        return response;
    }
    
    private void updateAAAUserProfile() {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUserProfile");
            updateQuery.setUpdateColumn("COUNTRY_CODE", (Object)"US");
            updateQuery.setUpdateColumn("LANGUAGE_CODE", (Object)"en");
            SyMUtil.getPersistence().update(updateQuery);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUserProfile", "USER_ID"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator userIdItr = dataObject.getRows("AaaUserProfile");
                while (userIdItr.hasNext()) {
                    final Row userIdRow = userIdItr.next();
                    final Long userId = (Long)userIdRow.get("USER_ID");
                    final Locale locale = new Locale("en", "US");
                    DMUserHandler.updateUserDataInCache(userId + "_" + "USERLOCALE", (Object)locale);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateAAAUserProfile ", e);
        }
    }
    
    private void logOutDefaultAdmin() {
        try {
            this.logger.info("Default admin credentials are being used. So logging out all sessions of default admin");
            final Long defaultAdminUVHAccountID = DBUtil.getUVHValue("AaaAccount:account_id:0");
            final ArrayList<String> sessionIdList = DMOnPremiseUserUtil.getSessionValueForUser(defaultAdminUVHAccountID);
            DMOnPremiseUserUtil.logoutAllSessions((ArrayList)sessionIdList);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while logging out ", ex);
        }
    }
}
