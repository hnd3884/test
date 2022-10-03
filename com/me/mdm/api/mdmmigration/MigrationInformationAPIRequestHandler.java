package com.me.mdm.api.mdmmigration;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MigrationInformationAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            request.urlStartKey = "serviceconfigdetails";
            response.put("RESPONSE", (Object)new MigrationServicesFacade().getProductConfigurationInformation(request.toJSONObject()));
            response.put("status", 200);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting production configuration information details");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
