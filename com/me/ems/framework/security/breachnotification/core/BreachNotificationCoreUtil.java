package com.me.ems.framework.security.breachnotification.core;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class BreachNotificationCoreUtil
{
    public static Logger logger;
    public static BreachNotificationCoreUtil instance;
    
    public static BreachNotificationCoreUtil getInstance() {
        if (BreachNotificationCoreUtil.instance == null) {
            BreachNotificationCoreUtil.instance = new BreachNotificationCoreUtil();
        }
        return BreachNotificationCoreUtil.instance;
    }
    
    public String getServerNATAddressDetail() {
        String natDetail = "";
        try {
            final Properties natProperties = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            if (natProperties != null && !natProperties.isEmpty()) {
                natDetail = ((Hashtable<K, String>)natProperties).get("NAT_ADDRESS");
            }
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
        }
        return natDetail;
    }
    
    public String getDCServerIPAddressDetail() {
        String ipAddress = "";
        try {
            final Properties dcServerInfoProperties = SyMUtil.getDCServerInfo();
            if (dcServerInfoProperties != null && !dcServerInfoProperties.isEmpty()) {
                ipAddress = ((Hashtable<K, String>)dcServerInfoProperties).get("SERVER_MAC_IPADDR");
            }
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
        }
        return ipAddress;
    }
    
    public boolean updateUserDetails(final ArrayList emailAddressList, final boolean mailModifiedStatus) throws APIException {
        boolean updateStatus = false;
        try {
            final BreachNotificationAPI breachNotificationAPI = ApiFactoryProvider.getBreachNotificationAPI();
            final Properties userDetails = new Properties();
            if (mailModifiedStatus) {
                final String emailAddress = String.join(",", emailAddressList);
                ((Hashtable<String, String>)userDetails).put("Customer_Email", emailAddress);
                if (CustomerInfoUtil.isSAS()) {
                    ((Hashtable<String, String>)userDetails).put("Domain_Address", "--");
                    ((Hashtable<String, String>)userDetails).put("Build_Version", "--");
                    updateStatus = true;
                }
                else {
                    ((Hashtable<String, String>)userDetails).put("Domain_Address", this.getServerNATAddressDetail());
                    ((Hashtable<String, String>)userDetails).put("Build_Version", breachNotificationAPI.getBuildVersion());
                }
                this.updateMailServiceDB(userDetails, 2);
                if (breachNotificationAPI != null) {
                    updateStatus = breachNotificationAPI.routineUserNotification(userDetails);
                }
            }
            else {
                this.updateMailServiceDB(userDetails, 0);
            }
            return updateStatus;
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
            throw new APIException("GENERIC0005", exp.getMessage(), new String[0]);
        }
    }
    
    public Map getSchedulerUserInfo() {
        final Map userInfo = new HashMap();
        try {
            final DataObject DO = SyMUtil.getPersistence().get("BreachNotificationZCMapping", (Criteria)null);
            if (!DO.isEmpty()) {
                final Row row = DO.getRow("BreachNotificationZCMapping");
                userInfo.put("emailAddress", row.get("EMAIL_ID"));
                userInfo.put("buildVersion", row.get("BUILD_VERSION"));
                userInfo.put("natAddress", row.get("NAT_DETAILS"));
                userInfo.put("updateStatus", row.get("MAIL_MODIFIED_STATUS"));
            }
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
        }
        return userInfo;
    }
    
    public Map getUserInfo() throws APIException {
        try {
            final Map userInfo = new HashMap();
            final DataObject DO = SyMUtil.getPersistence().get("BreachNotificationZCMapping", (Criteria)null);
            if (!DO.isEmpty()) {
                final Row row = DO.getRow("BreachNotificationZCMapping");
                final int mailModifiedStatus = (int)row.get("MAIL_MODIFIED_STATUS");
                final String emailID = (String)row.get("EMAIL_ID");
                if (mailModifiedStatus == 0 || emailID == null || emailID.length() == 0) {
                    userInfo.put("securityAdvisoryStatus", 0);
                }
                else {
                    final String[] emailArray = emailID.split(",");
                    userInfo.put("emailAddress", emailArray);
                    userInfo.put("buildVersion", row.get("BUILD_VERSION"));
                    userInfo.put("natAddress", row.get("NAT_DETAILS"));
                    final long lastUserUpdateTime = (long)row.get("LAST_USER_UPDATE_TIME");
                    final Date lastUpdateTime = new Date(lastUserUpdateTime);
                    final SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                    userInfo.put("lastUpdateTime", formatter.format(lastUpdateTime));
                    if (mailModifiedStatus == 1) {
                        userInfo.put("securityAdvisoryStatus", 3);
                    }
                    else if (this.getReviewStatus(lastUserUpdateTime)) {
                        userInfo.put("securityAdvisoryStatus", 2);
                    }
                    else {
                        userInfo.put("securityAdvisoryStatus", 1);
                    }
                }
            }
            else {
                userInfo.put("securityAdvisoryStatus", 0);
            }
            return userInfo;
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
            throw new APIException("GENERIC0005", exp.getMessage(), new String[0]);
        }
    }
    
    public int getSecurityAdvisoryMailStatus() {
        int mailModifiedStatus = 0;
        try {
            final DataObject DO = SyMUtil.getPersistence().get("BreachNotificationZCMapping", (Criteria)null);
            if (!DO.isEmpty()) {
                final Row row = DO.getRow("BreachNotificationZCMapping");
                mailModifiedStatus = (int)row.get("MAIL_MODIFIED_STATUS");
            }
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
        }
        return mailModifiedStatus;
    }
    
    public void updateMailServiceDB(final Properties userDetails, final int mailModifiedStatus) {
        try {
            final DataObject DO = SyMUtil.getPersistence().get("BreachNotificationZCMapping", (Criteria)null);
            if (DO.isEmpty()) {
                final Row row = new Row("BreachNotificationZCMapping");
                row.set("EMAIL_ID", (Object)userDetails.getProperty("Customer_Email"));
                row.set("MAIL_MODIFIED_STATUS", (Object)mailModifiedStatus);
                row.set("BUILD_VERSION", (Object)userDetails.getProperty("Build_Version"));
                row.set("NAT_DETAILS", (Object)userDetails.getProperty("Domain_Address"));
                row.set("LAST_CREATOR_UPDATE_TIME", (Object)SyMUtil.getCurrentTime());
                row.set("LAST_USER_UPDATE_TIME", (Object)SyMUtil.getCurrentTime());
                DO.addRow(row);
                SyMUtil.getPersistence().add(DO);
            }
            else {
                final Row row = DO.getRow("BreachNotificationZCMapping");
                row.set("MAIL_MODIFIED_STATUS", (Object)mailModifiedStatus);
                row.set("LAST_USER_UPDATE_TIME", (Object)SyMUtil.getCurrentTime());
                if (mailModifiedStatus != 0) {
                    row.set("EMAIL_ID", (Object)userDetails.getProperty("Customer_Email"));
                    row.set("BUILD_VERSION", (Object)userDetails.getProperty("Build_Version"));
                    row.set("NAT_DETAILS", (Object)userDetails.getProperty("Domain_Address"));
                    row.set("LAST_CREATOR_UPDATE_TIME", (Object)SyMUtil.getCurrentTime());
                }
                DO.updateRow(row);
                SyMUtil.getPersistence().update(DO);
            }
            DCEventLogUtil.getInstance().addEvent(11101, this.getUserName(), null, "dc.common.security.breachnotification.db.updated", null, true);
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
        }
    }
    
    public void updateUniqueFormRowID(final String uniqueFormRowId) {
        try {
            final DataObject DO = SyMUtil.getPersistence().get("BreachNotificationZCMapping", (Criteria)null);
            if (!DO.isEmpty()) {
                final Row row = DO.getRow("BreachNotificationZCMapping");
                final String defaultUniqueId = (String)row.get("UNIQUE_FORM_ID");
                if (defaultUniqueId.equals("--")) {
                    row.set("UNIQUE_FORM_ID", (Object)uniqueFormRowId);
                }
                DO.updateRow(row);
                SyMUtil.getPersistence().update(DO);
            }
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
        }
    }
    
    public String getUniqueFormRowID() {
        String dbUniqueId = "--";
        try {
            final DataObject DO = SyMUtil.getPersistence().get("BreachNotificationZCMapping", (Criteria)null);
            if (!DO.isEmpty()) {
                final Row row = DO.getRow("BreachNotificationZCMapping");
                final String currentUniqueId = (String)row.get("UNIQUE_FORM_ID");
                if (!currentUniqueId.equals("--")) {
                    dbUniqueId = currentUniqueId;
                }
            }
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
        }
        return dbUniqueId;
    }
    
    public String getTimeCreatorFormat(final long currentTime) {
        final Date date = new Date(currentTime);
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        return formatter.format(date);
    }
    
    public boolean getReviewStatus(final long lastUserUpdateTime) {
        boolean reviewStatus = false;
        final long diffInMillies = Math.abs(SyMUtil.getCurrentTime() - lastUserUpdateTime);
        final long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diff > 90L) {
            reviewStatus = true;
        }
        return reviewStatus;
    }
    
    public String getUserName() {
        String userName = "";
        try {
            userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            if (userName == null || userName.length() == 0) {
                userName = "DC-SYSTEM-USER";
            }
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationFrameworkImpl", exp);
        }
        return userName;
    }
    
    public boolean isValid(final String strValue) {
        return strValue != null && !strValue.equals("") && !strValue.equals(" ") && !strValue.isEmpty();
    }
    
    public boolean isBuildChanged(final long currentBuildNum, final Integer buildNumFromDB) {
        boolean buildChange = false;
        try {
            if (buildNumFromDB > currentBuildNum) {
                buildChange = true;
            }
        }
        catch (final Exception exp) {
            BreachNotificationCoreUtil.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp);
        }
        return buildChange;
    }
    
    public boolean compareNATDetails(final String dbNATAddress, final String serverNATAddress) {
        boolean status = false;
        if (this.isValid(dbNATAddress) || this.isValid(serverNATAddress)) {
            status = !dbNATAddress.equals(serverNATAddress);
        }
        return status;
    }
    
    public String getUploadURL() {
        final BreachNotificationAPI breachNotificationAPI = ApiFactoryProvider.getBreachNotificationAPI();
        if (breachNotificationAPI != null) {
            return breachNotificationAPI.getUploadURL();
        }
        return "";
    }
    
    public String getFormURL() {
        final BreachNotificationAPI breachNotificationAPI = ApiFactoryProvider.getBreachNotificationAPI();
        if (breachNotificationAPI != null) {
            return breachNotificationAPI.getFormURL();
        }
        return "";
    }
    
    static {
        BreachNotificationCoreUtil.logger = Logger.getLogger("SecurityLogger");
        BreachNotificationCoreUtil.instance = null;
    }
}
