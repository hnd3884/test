package com.me.devicemanagement.onpremise.webclient.configurations;

import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.metrack.EvaluatorTrackerUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class RequestDemoAction
{
    static Logger logger;
    
    public static boolean isRequestDemoPageNeeded() {
        try {
            final JSONObject demoRegisteredUsersJSON = ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("RequestDemoRegisteredUser");
            final int demoRegisteredUsersfromJSON = EvaluatorTrackerUtil.getInstance().getCount(String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()), "RequestDemoRegisteredUser");
            if (!demoRegisteredUsersJSON.has(String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID())) && demoRegisteredUsersfromJSON == 0) {
                final JSONObject skipDemoJSON = ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("SkipRequestDemoCount");
                final JSONObject neverShowDemoFile = ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("neverShowAgain");
                final int neverShowDemoJSON = EvaluatorTrackerUtil.getInstance().getCount(String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()), "neverShowAgain");
                if (!neverShowDemoFile.has(String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID())) && neverShowDemoJSON == 0) {
                    int skipCountFromFile = 0;
                    final int skipCountFromJSON = EvaluatorTrackerUtil.getInstance().getCount(String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()), "SkipRequestDemoCount");
                    if (skipDemoJSON.has(String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()))) {
                        skipCountFromFile = skipDemoJSON.optInt(String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()));
                    }
                    RequestDemoAction.logger.log(Level.INFO, " Skip Count from JSON : " + skipCountFromJSON);
                    RequestDemoAction.logger.log(Level.INFO, " Skip Count from File : " + skipCountFromFile);
                    if (skipCountFromFile + skipCountFromJSON < 7) {
                        return true;
                    }
                }
            }
        }
        catch (final Exception e) {
            RequestDemoAction.logger.log(Level.SEVERE, " Exception while checking request demo skip count" + e);
        }
        return false;
    }
    
    static {
        RequestDemoAction.logger = Logger.getLogger(RequestDemoAction.class.getName());
    }
}
