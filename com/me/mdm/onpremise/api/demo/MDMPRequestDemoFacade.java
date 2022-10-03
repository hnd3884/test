package com.me.mdm.onpremise.api.demo;

import com.me.devicemanagement.onpremise.webclient.configurations.RequestDemoAction;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.metrack.EvaluatorTrackerUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public class MDMPRequestDemoFacade
{
    public JSONObject registerRequestDemo(final JSONObject jsonObject) throws Exception {
        final JSONObject msgBody = jsonObject.getJSONObject("msg_body");
        final String userName = APIUtil.getUserName(jsonObject);
        msgBody.put("username", (Object)userName);
        final JSONObject responseJSON = new MDMPRequestDemoHandler().registerRequestDemo(msgBody);
        final JSONObject cookieJSON = new JSONObject();
        cookieJSON.put("cookie_name", (Object)"isRequestDemoPageNeeded");
        cookieJSON.put("cookie_value", (Object)Boolean.FALSE);
        cookieJSON.put("cookie_max_age", 0);
        responseJSON.put("cookie", (Object)cookieJSON);
        EvaluatorTrackerUtil.getInstance().addOrIncrementClickCountForTrialUsers("RequestDemoRegisteredUser", String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()));
        return responseJSON;
    }
    
    public JSONObject skipRequestDemo() throws Exception {
        final JSONObject responseJSON = new JSONObject();
        if (RequestDemoAction.isRequestDemoPageNeeded()) {
            final JSONObject cookieJSON = new JSONObject();
            cookieJSON.put("cookie_name", (Object)"isRequestDemoPageNeeded");
            cookieJSON.put("cookie_value", (Object)Boolean.TRUE);
            cookieJSON.put("cookie_max_age", -1);
            responseJSON.put("cookie", (Object)cookieJSON);
        }
        EvaluatorTrackerUtil.getInstance().addOrIncrementClickCountForTrialUsers("SkipRequestDemoCount", String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()));
        return responseJSON;
    }
    
    public JSONObject neverShowRequestDemoPage() throws Exception {
        final JSONObject responseJSON = new JSONObject();
        if (!RequestDemoAction.isRequestDemoPageNeeded()) {
            final JSONObject cookieJSON = new JSONObject();
            cookieJSON.put("cookie_name", (Object)"isRequestDemoPageNeeded");
            cookieJSON.put("cookie_value", (Object)Boolean.FALSE);
            cookieJSON.put("cookie_max_age", -1);
            responseJSON.put("cookie", (Object)cookieJSON);
        }
        EvaluatorTrackerUtil.getInstance().addOrIncrementClickCountForTrialUsers("SkipRequestDemoCount", String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()));
        return responseJSON;
    }
}
