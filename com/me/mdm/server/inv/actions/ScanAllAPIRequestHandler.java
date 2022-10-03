package com.me.mdm.server.inv.actions;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ScanAllAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject apiJSON = apiRequest.toJSONObject();
            final Long customerID = APIUtil.getCustomerID(apiJSON);
            final Long userId = APIUtil.getUserID(apiJSON);
            final String userName = APIUtil.getUserName(apiJSON);
            String lastScanAllInitTime = MDMUtil.getUserParameter(userId, "lastScanAllInitiatedTime");
            final Long currentTime = MDMUtil.getCurrentTimeInMillis();
            MDMUtil.updateUserParameter(userId, "lastScanAllInitiatedTime", String.valueOf(currentTime));
            if (lastScanAllInitTime == null) {
                lastScanAllInitTime = "-1";
            }
            if (Long.parseLong(lastScanAllInitTime) == -1L || currentTime - Long.parseLong(lastScanAllInitTime) > 600000L) {
                final Properties taskProps = new Properties();
                ((Hashtable<String, Long>)taskProps).put("SCHEDULED_SCAN_USER_ID", MDMUtil.getInstance().getLoggedInUserID());
                ((Hashtable<String, String>)taskProps).put("user_name", userName);
                ((Hashtable<String, Long>)taskProps).put("customer_id", customerID);
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("schedulertime", MDMUtil.getCurrentTimeInMillis());
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.inv.ScanAllTask", taskInfoMap, taskProps, "mdmPool");
            }
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "exception in scan all api", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
