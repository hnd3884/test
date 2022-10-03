package com.me.mdm.server.metracker;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerOSUpdateMgmtImpl extends MEMDMTrackerConstants
{
    private Properties mdmOSUdateTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerOSUpdateMgmtImpl() {
        this.mdmOSUdateTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackOSUpdateMgmtImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM Remote Mgmt impl starts");
            if (!this.mdmOSUdateTrackerProperties.isEmpty()) {
                this.mdmOSUdateTrackerProperties = new Properties();
            }
            this.addOSUpdateProfileTrackings();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmOSUdateTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMRemotedTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmOSUdateTrackerProperties;
    }
    
    public void addOSUpdateProfileTrackings() {
        final JSONObject osUpdateProfileJSON = new JSONObject();
        DMDataSetWrapper ds = null;
        try {
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                final SelectQuery osUpdateProfileQuery = MDMCoreQuery.getInstance().getMDMQueryMap("OSUPDATE_QUERY");
                final String sql = RelationalAPI.getInstance().getSelectSQL((Query)osUpdateProfileQuery);
                SyMLogger.info(this.logger, sql, "getProperties", "MDM OS Update Mgmt impl starts");
                ds = DMDataSetWrapper.executeQuery((Object)osUpdateProfileQuery);
                if (ds.next()) {
                    osUpdateProfileJSON.put("OSUPDATE_OS_PROFILE_COUNT", (Object)ds.getValue("OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ANDROID_OS_PROFILE_COUNT", (Object)ds.getValue("ANDROID_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_IOS_OS_PROFILE_COUNT", (Object)ds.getValue("IOS_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_CHROME_OS_PROFILE_COUNT", (Object)ds.getValue("CHROME_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ASSOCIATED_OS_PROFILE_COUNT", (Object)ds.getValue("ASSOCIATED_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ASSOCIATED_ANDROID_OS_PROFILE_COUNT", (Object)ds.getValue("ASSOCIATED_ANDROID_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ASSOCIATED_IOS_OS_PROFILE_COUNT", (Object)ds.getValue("ASSOCIATED_IOS_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ASSOCIATED_CHROME_OS_PROFILE_COUNT", (Object)ds.getValue("ASSOCIATED_CHROME_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ASSOCIATED_WINDOW_POLICY_OS_PROFILE_COUNT", (Object)ds.getValue("ASSOCIATED_WINDOW_POLICY_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ASSOCIATED_NOTIFY_POLICY_OS_PROFILE_COUNT", (Object)ds.getValue("ASSOCIATED_NOTIFY_POLICY_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ASSOCIATED_FORCE_POLICY_OS_PROFILE_COUNT", (Object)ds.getValue("ASSOCIATED_FORCE_POLICY_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_ASSOCIATED_DIFFER_POLICY_OS_PROFILE_COUNT", (Object)ds.getValue("ASSOCIATED_DIFFER_POLICY_OS_PROFILE_COUNT").toString());
                    osUpdateProfileJSON.put("RUGGED_OS_PROFILE_COUNT", (Object)ds.getValue("RUGGED_OS_PROFILE_COUNT").toString());
                }
            }
            this.mdmOSUdateTrackerProperties.setProperty("OSUpdate_Profile_Details", osUpdateProfileJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "remoteSessionDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    public void addpendingOSUpdateDeviceTrackings() {
        final JSONObject osUpdateProfileJSON = new JSONObject();
        DMDataSetWrapper ds = null;
        try {
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                final SelectQuery pendingosUpdateQuery = MDMCoreQuery.getInstance().getMDMQueryMap("PENDING_OS_QUERY");
                final RelationalAPI relapi = RelationalAPI.getInstance();
                final String sql = relapi.getSelectSQL((Query)pendingosUpdateQuery);
                SyMLogger.info(this.logger, sql, "getProperties", "MDM Pending OS Update device impl starts");
                ds = DMDataSetWrapper.executeQuery((Object)pendingosUpdateQuery);
                if (ds.next()) {
                    osUpdateProfileJSON.put("OSUPDATE_PENDING_ANDROID_COUNT", (Object)ds.getValue("PENDING_ANDROID_COUNT").toString());
                    osUpdateProfileJSON.put("OSUPDATE_PENDING_IOS_COUNT", (Object)ds.getValue("PENDING_IOS_COUNT").toString());
                }
            }
            this.mdmOSUdateTrackerProperties.setProperty("OSUPDATE_Pending_Details", osUpdateProfileJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "remoteSessionDetails", "Exception : ", (Throwable)ex);
        }
    }
}
