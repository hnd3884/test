package com.me.mdm.server.metracker;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackRemoteImpl extends MEMDMTrackerConstants
{
    private Properties mdmRemoteTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackRemoteImpl() {
        this.mdmRemoteTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackRemoteImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM Remote Mgmt impl starts");
            if (!this.mdmRemoteTrackerProperties.isEmpty()) {
                this.mdmRemoteTrackerProperties = new Properties();
            }
            this.addRemoteSessionTrackings();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmRemoteTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMRemotedTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmRemoteTrackerProperties;
    }
    
    public void addRemoteSessionTrackings() {
        final JSONObject remoteSessionJSON = new JSONObject();
        Connection conn = null;
        DataSet ds = null;
        try {
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final boolean isAssistIntegrated = MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(customerId);
                remoteSessionJSON.put("Is_Assist_Integrated", isAssistIntegrated);
                if (isAssistIntegrated) {
                    final SelectQuery remoteQuery = MDMCoreQuery.getInstance().getMDMQueryMap("REMOTE_SESSION_QUERY");
                    final RelationalAPI relapi = RelationalAPI.getInstance();
                    conn = relapi.getConnection();
                    final String sql = relapi.getSelectSQL((Query)remoteQuery);
                    SyMLogger.info(this.logger, sql, "getProperties", "MDM Remote Mgmt impl starts");
                    ds = relapi.executeQuery((Query)remoteQuery, conn);
                    while (ds.next()) {
                        remoteSessionJSON.put("Agent_Denied_Remote_Request", (Object)ds.getValue("REMOTE_REQUEST_REJECTED").toString());
                        remoteSessionJSON.put("Remote_Request_Received_By_Agent", (Object)ds.getValue("REMOTE_REQUEST_RECEIVED").toString());
                        remoteSessionJSON.put("Remote_Request_Sent", (Object)ds.getValue("REMOTE_REQUEST_SENT").toString());
                        remoteSessionJSON.put("RemoteSession_Success_Count", (Object)ds.getValue("REMOTE_SESSION_SUCCESS_COUNT").toString());
                        remoteSessionJSON.put("RemoteSession_Count", (Object)ds.getValue("REMOTE_SESSION_COUNT").toString());
                        remoteSessionJSON.put("RemoteSession_Incomplete_Count", (Object)ds.getValue("REMOTE_SESSION_FAILURE_COUNT").toString());
                        remoteSessionJSON.put("Remote_Not_Supported_In_Agent", (Object)ds.getValue("REMOTE_NOT_SUPPORTED").toString());
                        remoteSessionJSON.put("Remote_Exception_At_Agent", (Object)ds.getValue("REMOTE_EXCEPTION_AT_AGENT").toString());
                        remoteSessionJSON.put("RemoteSession_Max_Session_Count", (Object)ds.getValue("MAX_REMOTE_COUNT").toString());
                        remoteSessionJSON.put("IOS_RemoteSession_Count", (Object)ds.getValue("IOS_REMOTE_SESSION_COUNT").toString());
                        remoteSessionJSON.put("IOS_RemoteSession_Success_Count", (Object)ds.getValue("IOS_REMOTE_SESSION_SUCCESS_COUNT").toString());
                        remoteSessionJSON.put("IOS_RemoteSession_Incomplete_Count", (Object)ds.getValue("IOS_REMOTE_SESSION_FAILURE_COUNT").toString());
                    }
                }
                else {
                    final String authTokenFailureCause = SyMUtil.getSyMParameterFromDB("Auth_Token_Failure_Cause");
                    remoteSessionJSON.put("Auth_Token_Failure_Cause", (Object)authTokenFailureCause);
                }
            }
            this.mdmRemoteTrackerProperties.setProperty("RemoteSession_Details", remoteSessionJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "remoteSessionDetails", "Exception : ", (Throwable)ex);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
    }
}
