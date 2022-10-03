package com.me.mdm.api.core.apps.config;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.businessstore.service.BusinessStoreService;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppLicenseSyncHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public AppLicenseSyncHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        try {
            JSONObject jsonObject = apiRequest.toJSONObject();
            final Long appID = APIUtil.getResourceID(jsonObject, "app_id");
            final Long customerID = APIUtil.getCustomerID(jsonObject);
            apiRequest.urlStartKey = "sync";
            jsonObject = apiRequest.toJSONObject();
            final Long businessStoreID = APIUtil.getResourceID(jsonObject, "syn_id");
            responseJSON.put("RESPONSE", (Object)new StoreFacade().getLicenseSyncStatus(appID, customerID, 1, businessStoreID));
            responseJSON.put("status", 200);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in GET /apps/:id/license/sync", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJSON;
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            JSONObject jsonObject = apiRequest.toJSONObject();
            final Long appID = APIUtil.getResourceID(jsonObject, "app_id");
            final Long customerID = APIUtil.getCustomerID(jsonObject);
            final Long usedID = APIUtil.getUserID(jsonObject);
            apiRequest.urlStartKey = "sync";
            jsonObject = apiRequest.toJSONObject();
            final Long businessStoreID = APIUtil.getResourceID(jsonObject, "syn_id");
            new BusinessStoreService().syncLicense(appID, usedID, businessStoreID, customerID, 1);
            return JSONUtil.toJSON("status", 204);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in POST /apps/:id/license/sync", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[] { ex });
        }
    }
}
