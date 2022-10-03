package com.me.mdm.mdmmigration;

import java.util.logging.Level;
import com.me.mdm.mdmmigration.target.AFWMigrationDataUpdateManager;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMMigrationUtil
{
    public static Logger logger;
    public static final String MIGRATION_TOPIC_AFW = "ManagedGooglePlay";
    public static final String MIGRATION_TOPIC_EMMUSERS = "EMMUsersAndAccounts";
    
    public JSONObject updateMigrationDataForRequest(final JSONObject request) throws Exception {
        try {
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget")) {
                throw new APIHTTPException("COM0015", new Object[] { "Migration should be enabled for the server for this action. Contact support ot enalbe it" });
            }
            final JSONObject msgBody = request.getJSONObject("msg_body");
            final String topic = msgBody.optString("topic");
            if (topic == null) {
                throw new APIHTTPException("COM0005", new Object[] { "Missing mandatory param : topic" });
            }
            if (topic.equals("ManagedGooglePlay")) {
                return new AFWMigrationDataUpdateManager().updateAFWDetailsForMigration(request);
            }
            if (topic.equals("EMMUsersAndAccounts")) {
                return new AFWMigrationDataUpdateManager().updateAFWUsersAndAccounts(request);
            }
            throw new APIHTTPException("COM0005", new Object[] { "Value of topic is from allowed values " + topic });
        }
        catch (final APIHTTPException e) {
            MDMMigrationUtil.logger.log(Level.SEVERE, "API Exception", e);
            throw e;
        }
        catch (final Exception e2) {
            MDMMigrationUtil.logger.log(Level.SEVERE, "Exception when getting migration data", e2);
            throw e2;
        }
    }
    
    static {
        MDMMigrationUtil.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
