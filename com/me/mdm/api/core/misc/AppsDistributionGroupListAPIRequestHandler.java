package com.me.mdm.api.core.misc;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AppsDistributionGroupListAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            requestJSON = MDMRestAPIFactoryProvider.getAppFacade().fillProfileCollectionIdForAppPackageReleaseLabel(requestJSON);
            final JSONObject distributionJSON = new GroupFacade().getGroupDistributionListForProfile(requestJSON);
            responseJSON.put("RESPONSE", (Object)distributionJSON);
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, " -- doGet()   >   Error", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
