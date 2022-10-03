package com.me.mdm.api.mdmmigration;

import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.error.APIHTTPException;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import com.me.mdm.api.APIRequest;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.ApiRequestHandler;

public class MigrationServiceConfigAPIRequestHandler extends ApiRequestHandler
{
    protected JSONObject getMsgBody(final JSONObject msgJson) throws JSONException {
        JSONObject msgContent = new JSONObject();
        final String msgContentString = JSONUtil.optString(msgJson, "msg_body");
        if (msgContentString != null) {
            msgContent = new JSONObject(msgContentString);
        }
        return msgContent;
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new MigrationServicesFacade().getAPIServiceConfigurationsList(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) {
        final JSONObject responseDetails = new JSONObject();
        try {
            final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            if (natProps.isEmpty()) {
                throw new Exception("NAT settings not configured");
            }
        }
        catch (final Exception e) {
            throw new APIHTTPException("SCN0003", new Object[] { e });
        }
        try {
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new MigrationServicesFacade().addServiceConfig(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while handleConfigureAPIServiceRequest ", (Throwable)e2);
            throw new APIHTTPException("COM0009", new Object[] { e2 });
        }
        catch (final Exception e) {
            throw new APIHTTPException("SCN0001", new Object[] { e });
        }
    }
}
