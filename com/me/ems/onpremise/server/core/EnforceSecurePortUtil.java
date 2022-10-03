package com.me.ems.onpremise.server.core;

import com.adventnet.ds.query.SelectQuery;
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import java.util.HashMap;
import java.util.Map;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.persistence.DataObject;
import java.time.LocalDateTime;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.logging.Logger;

public class EnforceSecurePortUtil
{
    private static Logger logger;
    private static final long COMPLETE_DAY_TIME = 86400000L;
    public static final String SHOW_RESTART_MSG = "showInsecureCommRestartMsg";
    public static final String SERVER_HTTPS_PORT = "serverHTTPSPort";
    public static final String INSECURE_EXPIRY_PROPS = "InsecurePortExpiryPeriod";
    public static final String SECURE_HTTPS_ENABLED_MODE = "HTTPSSecureCommEnabledMode";
    public static final String IS_TFA_BANNER_ENABLED = "IsTFABannerEnabled";
    
    public static boolean isInsecureCommDisabled() {
        boolean isDisabled = false;
        try {
            final Properties wsProps = WebServerUtil.getWebServerSettings();
            isDisabled = Boolean.parseBoolean(wsProps.getProperty("enforce.https.communication"));
        }
        catch (final Exception e) {
            EnforceSecurePortUtil.logger.log(Level.WARNING, "Exception while reading the web settings configuration file", e);
        }
        return isDisabled;
    }
    
    public static void updateEnforcingDate() {
        try {
            if (!isInsecureCommDisabled()) {
                final Column col = Column.getColumn("ServerParams", "PARAM_NAME");
                final Criteria criteria = new Criteria(col, (Object)"InsecurePortExpiryPeriod", 0, false);
                final DataObject insecurePortParamDO = DataAccess.get("ServerParams", criteria);
                if (insecurePortParamDO.isEmpty()) {
                    Long disablePeriod = System.currentTimeMillis();
                    disablePeriod += 1296000000L;
                    final Row paramRow = new Row("ServerParams");
                    paramRow.set("PARAM_NAME", (Object)"InsecurePortExpiryPeriod");
                    paramRow.set("PARAM_VALUE", (Object)disablePeriod);
                    insecurePortParamDO.addRow(paramRow);
                    DataAccess.update(insecurePortParamDO);
                }
                ApiFactoryProvider.getSecurityEnforcementAPI().initiateEnforcementPeriod();
                Logger.getLogger("SecurityLogger").log(Level.INFO, "HTTP Insecure Communication removal enforcement initiated at : " + LocalDateTime.now());
            }
        }
        catch (final Exception ex) {
            EnforceSecurePortUtil.logger.log(Level.INFO, "Exception while updating the insecure port enforcing expiry time period", ex);
        }
    }
    
    public static Integer getEnforcingDate() throws Exception {
        Long expiryDate = 0L;
        final String expiryParam = SyMUtil.getServerParameter("InsecurePortExpiryPeriod");
        if (expiryParam != null) {
            expiryDate = Long.parseLong(expiryParam);
            expiryDate = (expiryDate - System.currentTimeMillis()) / 86400000L;
        }
        return (expiryDate > 0L) ? expiryDate.intValue() : 0;
    }
    
    public static boolean isEnforcingStarted() {
        return SyMUtil.getServerParameter("InsecurePortExpiryPeriod") != null;
    }
    
    public static boolean isTimeToEnforce() {
        boolean status = false;
        try {
            if (!isInsecureCommDisabled() && isEnforcingStarted() && getEnforcingDate() <= 0) {
                status = true;
            }
        }
        catch (final Exception e) {
            EnforceSecurePortUtil.logger.log(Level.WARNING, "Exception while collecting the Insecure communication enforcement details!!!", e);
        }
        return status;
    }
    
    public static boolean isEnforceMsgNeeded() {
        boolean status = false;
        try {
            if (!isInsecureCommDisabled() && isEnforcingStarted() && getEnforcingDate() > 0) {
                status = true;
            }
        }
        catch (final Exception e) {
            EnforceSecurePortUtil.logger.log(Level.WARNING, "Exception while collecting the Insecure communication enforcement alert message details!!!", e);
        }
        return status;
    }
    
    public static boolean enforceInsecureComm(final Boolean autoEnforce) {
        boolean enforceStatus = false;
        try {
            WebServerUtil.addOrUpdateWebServerProps("enforce.https.communication", "true");
            System.setProperty("showInsecureCommRestartMsg", "true");
            if (autoEnforce) {
                ApiFactoryProvider.getSecurityEnforcementAPI().autoEnforcement();
            }
            else {
                ApiFactoryProvider.getSecurityEnforcementAPI().manualEnforcement();
            }
            enforceStatus = true;
        }
        catch (final Exception e) {
            EnforceSecurePortUtil.logger.log(Level.WARNING, "Exception while enforcing the Insecure communication!!!", e);
        }
        return enforceStatus;
    }
    
    public static Map getMeTrackerDetails() {
        final HashMap trackerDetails = new HashMap();
        try {
            trackerDetails.put("isSecureHttpsEnabled", isInsecureCommDisabled());
            if (isInsecureCommDisabled()) {
                trackerDetails.put("httpsEnabledMode", SyMUtil.getServerParameter("HTTPSSecureCommEnabledMode"));
            }
            else {
                final Boolean enforceStatus = isEnforcingStarted();
                trackerDetails.put("httpsEnforceStarted", enforceStatus);
                if (enforceStatus) {
                    trackerDetails.put("httpsEnforcePendingDays", getEnforcingDate());
                }
            }
        }
        catch (final Exception e) {
            EnforceSecurePortUtil.logger.log(Level.WARNING, "Exception while collecting ME Tracker data for Insecure communication!!!", e);
        }
        return trackerDetails;
    }
    
    public static void triggerHTTPSEnforceRestart() {
        if (Boolean.parseBoolean(System.getProperty("showInsecureCommRestartMsg"))) {
            SyMUtil.triggerServerRestart("HTTPS Enforcement!");
        }
    }
    
    public static Map getEnforceDetails() {
        final HashMap trackerDetails = new HashMap();
        try {
            trackerDetails.put("isSecureHttpsEnabled", isInsecureCommDisabled());
            trackerDetails.put("serverHTTPSPort", WebServerUtil.getHttpsPort());
            if (!isInsecureCommDisabled()) {
                final Boolean enforceStatus = isEnforcingStarted();
                trackerDetails.put("httpsEnforceStarted", enforceStatus);
                if (enforceStatus) {
                    trackerDetails.put("httpsEnforcePendingDays", getEnforcingDate());
                }
            }
        }
        catch (final Exception e) {
            EnforceSecurePortUtil.logger.log(Level.WARNING, "Exception while collecting ME Tracker data for Insecure communication!!!", e);
        }
        return trackerDetails;
    }
    
    public static void generateInsecureCommStaticHTML() {
        try {
            final String sourceFileName = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "html" + File.separator + "insecureCommError_template.html";
            final String destFileName = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "html" + File.separator + "insecureCommunicationError.html";
            if (!new File(destFileName).exists()) {
                String logoURL = ApiFactoryProvider.getUtilAccessAPI().getRebrandLogoPath();
                logoURL = ".." + logoURL.substring(logoURL.indexOf("\\webapps\\DesktopCentral\\") + 23);
                final Properties props = new Properties();
                props.setProperty("central_server_port", String.valueOf(WebServerUtil.getServerPort()));
                props.setProperty("product_logo", logoURL);
                StartupUtil.findAndReplaceStrings(sourceFileName, destFileName, props, "%");
            }
        }
        catch (final Exception ex) {
            EnforceSecurePortUtil.logger.log(Level.WARNING, "Caught exception while generating the http insecure communication education static html file", ex);
        }
    }
    
    public static String getAutoEnforceTime() {
        Date nextRuntime = null;
        try {
            final Criteria crit = new Criteria(Column.getColumn("Schedule", "SCHEDULE_NAME"), (Object)"DCGlobalTaskScheduler", 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Task_Input"));
            final Join join = new Join("Task_Input", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2);
            sq.addJoin(join);
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("Task_Input", "INSTANCE_ID"));
            sq.addSelectColumn(Column.getColumn("Task_Input", "SCHEDULE_TIME"));
            final DataObject instanceDO = SyMUtil.getPersistence().get(sq);
            if (!instanceDO.isEmpty()) {
                final Timestamp nextScheduledTime = (Timestamp)instanceDO.getFirstValue("Task_Input", "SCHEDULE_TIME");
                if (nextScheduledTime != null) {
                    final Calendar time = Calendar.getInstance();
                    time.setTimeInMillis(nextScheduledTime.getTime());
                    final Calendar calendar = Calendar.getInstance();
                    calendar.set(11, time.get(11));
                    calendar.set(12, time.get(12));
                    calendar.set(13, time.get(13));
                    nextRuntime = calendar.getTime();
                    final Date currentTime = new Date();
                    if (currentTime.compareTo(nextRuntime) >= 0) {
                        calendar.add(5, 1);
                        nextRuntime = calendar.getTime();
                    }
                }
            }
        }
        catch (final Exception e) {
            EnforceSecurePortUtil.logger.log(Level.WARNING, "Caught exception while getting auto enforcement time details", e);
        }
        return (nextRuntime != null) ? nextRuntime.toString() : "05:00:00 AM";
    }
    
    static {
        EnforceSecurePortUtil.logger = Logger.getLogger(EnforceSecurePortUtil.class.getName());
    }
}
