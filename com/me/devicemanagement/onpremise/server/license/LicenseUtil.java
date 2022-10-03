package com.me.devicemanagement.onpremise.server.license;

import com.adventnet.tools.prevalent.ConsoleOut;
import com.adventnet.tools.prevalent.Validation;
import java.io.File;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.StringTokenizer;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.net.URLEncoder;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.Date;
import java.util.Calendar;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LicenseUtil
{
    public static final String COMPANY_NAME = "license.company.name";
    public static final String REGISTERED_USER_NAME = "license.user.name";
    public static final String LICENSE_TYPE = "license.type";
    public static final String EVALUATION_DAYS = "license.evaluation.days";
    public static final String EXPIRY_DATE = "license.expiry.date";
    public static final String COMPUTER_COUNT = "license.computer.count";
    public static final String MOBILEDEVICE_COUNT = "license.mobiledevice.count";
    public static final String TECHNICIAN_COUNT = "license.technician.count";
    public static final String OSD_SERVER_COUNT = "license.osd.server.count";
    public static final String OSD_WORKSTATION_COUNT = "license.osd.workstation.count";
    private static Logger logger;
    
    public static Map getLicenseProps() {
        final LinkedHashMap licenseInfo = new LinkedHashMap();
        try {
            final LicenseProvider w = LicenseProvider.getInstance();
            final String companyName = w.getCompanyName();
            licenseInfo.put("license.company.name", companyName);
            final String userName = w.getUserName();
            licenseInfo.put("license.user.name", userName);
            final String licenseType = w.getLicenseType();
            licenseInfo.put("license.type", licenseType);
            final String evalDays = "" + w.getEvaluationDays();
            licenseInfo.put("license.evaluation.days", evalDays);
            final String evalExpiry = w.getProductExpiryDate();
            licenseInfo.put("license.expiry.date", evalExpiry);
            if (w.getLicenseType() != null && w.getLicenseType().equals("R")) {
                final String numberOfSystems = LicenseProvider.getInstance().getNoOfComutersManaged();
                licenseInfo.put("license.computer.count", numberOfSystems);
                final String numberOfMobileDevices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
                licenseInfo.put("license.mobiledevice.count", numberOfMobileDevices);
                final Properties techProperties = w.getModuleProperties("Technicians");
                if (techProperties != null && techProperties.getProperty("NumberOfTechnicians") != null) {
                    final String techCount = techProperties.getProperty("NumberOfTechnicians");
                    licenseInfo.put("license.technician.count", techCount);
                }
                final Properties osdProperties = w.getModuleProperties("OSDeployer");
                if (osdProperties != null) {
                    licenseInfo.put("license.osd.server.count", osdProperties.getProperty("NumberOfServerMachines"));
                    licenseInfo.put("license.osd.workstation.count", osdProperties.getProperty("NumberOfWorkstationMachines"));
                }
            }
        }
        catch (final Exception ex) {
            LicenseUtil.logger.log(Level.INFO, "Caught exception while forming the licenseinfo props", ex);
        }
        return licenseInfo;
    }
    
    public static void addOrUpdateLicenseExpiryMsgDetails() {
        final String expiryDate = LicenseProvider.getInstance().getProductExpiryDate();
        final long thisDay = getTodayDateInLong(0);
        LicenseUtil.logger.log(Level.INFO, "Inside LicenseUtil:addOrUpdateLicenseExpiryMsgDetails()");
        MessageProvider.getInstance().hideMessage("PRODUCT_LICENSE_EXPIRED");
        MessageProvider.getInstance().hideMessage("PRODUCT_LICENSE_ABOUT_TO_EXPIRE");
        LicenseUtil.logger.log(Level.INFO, "Inside LicenseUtil:addOrUpdateLicenseExpiryMsgDetails() hided about to expire and expired msg");
        if (LicenseProvider.getInstance().isLicenseExpiryMsgRequired()) {
            if (!"never".equals(expiryDate)) {
                final long expDate_long = converDateToLong(expiryDate, " ");
                LicenseUtil.logger.log(Level.INFO, "license type is  NOT  never : Today in long is = :" + thisDay + "and expiry date in long  =: " + expDate_long);
                if (expDate_long >= thisDay) {
                    final String licenseType = LicenseProvider.getInstance().getLicenseType();
                    final long noOfDays = LicenseProvider.getInstance().getEvaluationDays();
                    if (licenseType != null && licenseType.equals("T")) {
                        LicenseUtil.logger.log(Level.INFO, "license type is " + licenseType + " and noOfDays left are =: " + noOfDays);
                        if (noOfDays <= 7L && noOfDays > 0L) {
                            MessageProvider.getInstance().unhideMessage("PRODUCT_LICENSE_ABOUT_TO_EXPIRE");
                        }
                    }
                    else {
                        final long dayAfter15Days = getTodayDateInLong(14);
                        LicenseUtil.logger.log(Level.INFO, "license type is not trial Day after 15days is =: " + dayAfter15Days);
                        if (expDate_long <= dayAfter15Days) {
                            MessageProvider.getInstance().unhideMessage("PRODUCT_LICENSE_ABOUT_TO_EXPIRE");
                        }
                    }
                }
            }
            else {
                final String amsExpireDate = LicenseProvider.getInstance().getProductAMSExpiryDate();
                if (amsExpireDate != null) {
                    final long expDate_long2 = converDateToLong(amsExpireDate, "-");
                    LicenseUtil.logger.log(Level.INFO, "license type is   never : Today in long is = :" + thisDay + "and ams expiry date in long  =: " + expDate_long2);
                    if (expDate_long2 >= thisDay) {
                        final long dayAfter15Days2 = getTodayDateInLong(14);
                        LicenseUtil.logger.log(Level.INFO, "license type is  never : Expiry Date in long is = :" + expDate_long2 + " and ams Day after 15days is =: " + dayAfter15Days2);
                        if (expDate_long2 <= dayAfter15Days2) {
                            MessageProvider.getInstance().unhideMessage("PRODUCT_LICENSE_ABOUT_TO_EXPIRE");
                        }
                    }
                    else if (expDate_long2 < thisDay) {
                        LicenseUtil.logger.log(Level.INFO, "license type is  never and ams expired  ");
                        MessageProvider.getInstance().unhideMessage("PRODUCT_LICENSE_EXPIRED");
                    }
                }
            }
        }
    }
    
    public static long converDateToLong(final String expiryDate, final String stringTokenizer) {
        return DateTimeUtil.convertDateToLong(expiryDate, stringTokenizer);
    }
    
    private static long getTodayDateInLong(final int dayOfMonth) {
        final Calendar cal = Calendar.getInstance();
        final Date today = new Date();
        cal.setTime(today);
        cal.add(5, dayOfMonth);
        final long thisDay = cal.getTime().getTime();
        return thisDay;
    }
    
    public static String getRenewalMailMessage(final boolean isSubscriptionModel) {
        final LicenseProvider dcLicenseHandler = LicenseProvider.getInstance();
        String productName = ProductUrlLoader.getInstance().getValue("displayname");
        productName = productName.replaceAll(" ", "%20");
        final String newLine = URLEncoder.encode("\n");
        final StringBuffer sbf = new StringBuffer();
        sbf.append(newLine);
        sbf.append(newLine);
        sbf.append("----------------- License Details ----------------- " + newLine);
        sbf.append("Company Name - " + dcLicenseHandler.getCompanyName() + newLine);
        sbf.append("Name - " + dcLicenseHandler.getUserName() + newLine);
        sbf.append("Edition - " + dcLicenseHandler.getProductType() + newLine);
        String subject = productName + "%20Renewal";
        if (!isSubscriptionModel) {
            final String amsExpireDate = dcLicenseHandler.getProductAMSExpiryDate();
            sbf.append("AMS Expiry Date - " + amsExpireDate + newLine);
            subject = productName + "%20AMS%20Renewal";
        }
        final Properties licensDetails = dcLicenseHandler.getModuleProperties("LicenseDetails");
        sbf.append("Expiry Date - " + dcLicenseHandler.getProductExpiryDate() + newLine);
        sbf.append("Primary Contact - " + licensDetails.getProperty("PrimaryContact") + newLine);
        sbf.append("Secondary Contact - " + licensDetails.getProperty("SecondaryContact") + newLine);
        sbf.append("Country - " + licensDetails.getProperty("country") + newLine);
        sbf.append("Users - " + dcLicenseHandler.getNoOfTechnicians() + newLine);
        sbf.append("Computers - " + dcLicenseHandler.getNoOfComutersManaged() + newLine);
        sbf.append("MobileDevices - " + dcLicenseHandler.getNoOfMobileDevicesManaged() + newLine);
        sbf.append("--------------------------------------------------------" + newLine);
        String msg = sbf.toString().trim();
        msg = msg.replaceAll(" ", "%20");
        final String renewalMailId = ProductUrlLoader.getInstance().getValue("renewalmailid");
        final String message = " Contact <a target=_blank class=bodyboldsmall href=mailto:sales@manageengine.com?cc=" + renewalMailId + "&subject=" + subject + "&body=" + msg + ">sales@manageengine.com</a>";
        return message;
    }
    
    public static String getRenewalMailMessageForPayload(final boolean isSubscriptionModel) {
        final LicenseProvider dcLicenseHandler = LicenseProvider.getInstance();
        final String productName = ProductUrlLoader.getInstance().getValue("displayname");
        String subject = productName + " Renewal";
        final String newLine = System.lineSeparator();
        final StringBuilder builder = new StringBuilder();
        builder.append(newLine).append(newLine);
        builder.append("----------------- License Details ----------------- ").append(newLine);
        builder.append("Company Name - ").append(dcLicenseHandler.getCompanyName()).append(newLine);
        builder.append("Name - ").append(dcLicenseHandler.getUserName()).append(newLine);
        builder.append("Edition - ").append(dcLicenseHandler.getProductType()).append(newLine);
        if (!isSubscriptionModel) {
            final String amsExpireDate = dcLicenseHandler.getProductAMSExpiryDate();
            builder.append("AMS Expiry Date - ").append(amsExpireDate).append(newLine);
            subject = productName + "%20AMS%20Renewal";
        }
        final Properties licenseDetails = dcLicenseHandler.getModuleProperties("LicenseDetails");
        builder.append("Expiry Date - ").append(dcLicenseHandler.getProductExpiryDate()).append(newLine);
        builder.append("Primary Contact - ").append(licenseDetails.getProperty("PrimaryContact")).append(newLine);
        builder.append("Secondary Contact - ").append(licenseDetails.getProperty("SecondaryContact")).append(newLine);
        builder.append("Country - ").append(licenseDetails.getProperty("country")).append(newLine);
        builder.append("Users - ").append(dcLicenseHandler.getNoOfTechnicians()).append(newLine);
        builder.append("Computers - ").append(dcLicenseHandler.getNoOfComutersManaged()).append(newLine);
        builder.append("MobileDevices - ").append(dcLicenseHandler.getNoOfMobileDevicesManaged()).append(newLine);
        builder.append("--------------------------------------------------------").append(newLine);
        final String msg = builder.toString().trim();
        final String renewalMailId = ProductUrlLoader.getInstance().getValue("renewalmailid");
        return "mailto:sales@manageengine.com?cc=".concat(renewalMailId).concat("&subject=").concat(subject).concat("&body=").concat(msg);
    }
    
    public static void checkLicenseExpireDate() {
        try {
            final String isTrigged = SyMUtil.getSyMParameter("IS_RESTART_TRIGGERED");
            LicenseUtil.logger.log(Level.INFO, "Server Restart is already triggered : " + isTrigged);
            if (isTrigged != null && isTrigged.equalsIgnoreCase("true")) {
                return;
            }
            final LicenseProvider licenseProvider = LicenseProvider.getInstance();
            final String licenseType = licenseProvider.getLicenseType();
            final String expiryDate = licenseProvider.getProductExpiryDate();
            LicenseUtil.logger.log(Level.INFO, "License Expiry : " + expiryDate);
            if (licenseType != null && licenseType.equalsIgnoreCase("F")) {
                LicenseUtil.logger.log(Level.WARNING, "*****************************************************");
                LicenseUtil.logger.log(Level.WARNING, "License Type already Free mode");
                LicenseUtil.logger.log(Level.WARNING, "*****************************************************");
                SyMUtil.updateSyMParameter("IS_RESTART_TRIGGERED", "true");
                return;
            }
            if (!"never".equals(expiryDate)) {
                Calendar cal = Calendar.getInstance();
                final StringTokenizer stt = new StringTokenizer(expiryDate, " ");
                if (stt.countTokens() == 3) {
                    final int yyyy = Integer.parseInt(stt.nextToken());
                    final int mm = Integer.parseInt(stt.nextToken());
                    final int dd = Integer.parseInt(stt.nextToken());
                    try {
                        cal.set(yyyy, mm - 1, dd);
                        final long expDate_long = cal.getTime().getTime();
                        cal = Calendar.getInstance();
                        final Date today = new Date();
                        cal.setTime(today);
                        final long thisDay = cal.getTime().getTime();
                        if (expDate_long < thisDay) {
                            LicenseUtil.logger.log(Level.WARNING, "*****************************************************");
                            LicenseUtil.logger.log(Level.WARNING, "Server going to restart for license date expires ");
                            LicenseUtil.logger.log(Level.WARNING, "current Date " + today);
                            LicenseUtil.logger.log(Level.WARNING, "Expiry Date " + expiryDate);
                            LicenseUtil.logger.log(Level.WARNING, "*****************************************************");
                            SyMUtil.updateSyMParameter("IS_RESTART_TRIGGERED", "true");
                            SyMUtil.triggerServerRestart("License date expired.");
                        }
                    }
                    catch (final Exception ex) {
                        LicenseUtil.logger.log(Level.WARNING, "Exception while changing the product expiry date to simple date format.", ex);
                        LicenseUtil.logger.log(Level.INFO, "Month value is :" + mm);
                    }
                }
            }
        }
        catch (final Exception ex2) {
            LicenseUtil.logger.log(Level.WARNING, "Caught exception while executing checkLicenseExpireDate", ex2);
        }
    }
    
    public static String encryptLicensekey(final String text, final int type) {
        return ApiFactoryProvider.getCryptoAPI().encrypt(text, Integer.valueOf(type));
    }
    
    public static boolean moveToFreeLicense() {
        final String homeDir = System.getProperty("server.dir");
        final String freeXmlPath = homeDir + File.separatorChar + "lib" + File.separatorChar + "Free.xml";
        final Validation valid = Validation.getInstance();
        if (new File(freeXmlPath).exists() && valid.doValidation(homeDir, "Evaluation User", freeXmlPath, false)) {
            ConsoleOut.println("Your license has expired. Moving to free edition");
            return valid.copyLicenseFile(homeDir, freeXmlPath);
        }
        return false;
    }
    
    static {
        LicenseUtil.logger = Logger.getLogger(LicenseUtil.class.getName());
    }
}
