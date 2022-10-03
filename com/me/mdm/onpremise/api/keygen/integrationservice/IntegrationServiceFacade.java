package com.me.mdm.onpremise.api.keygen.integrationservice;

import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import com.me.mdm.onpremise.server.integration.apikey.APIKeyGenerationHandler;
import com.me.devicemanagement.onpremise.server.authentication.IntegrationServiceUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.onpremise.server.integration.IntegrationProductUtil;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class IntegrationServiceFacade
{
    Logger logger;
    
    public IntegrationServiceFacade() {
        this.logger = Logger.getLogger("MDMAPILogger");
    }
    
    public JSONObject createServiceForProduct(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long productId = APIUtil.getResourceID(requestJSON, "integration_product_id");
            this.logger.log(Level.INFO, "Creating the service for the product id {0}", productId);
            final String serviceName = IntegrationProductUtil.getNewInstance().getIntegrationProductName(productId);
            if (serviceName == null) {
                throw new APIHTTPException("COM0008", new Object[] { productId });
            }
            final Long userID = APIUtil.getUserID(requestJSON);
            JSONObject request = new JSONObject();
            request.put("NAME", (Object)serviceName);
            request.put("logged_in_user", (Object)userID);
            request.put("integration_product_id", (Object)productId);
            JSONObject response = IntegrationServiceUtil.getNewInstance().createIntegrationService(request);
            final int status_id = response.getInt("status_id");
            JSONObject responseJSON = new JSONObject();
            switch (status_id) {
                case 100: {
                    final Long serviceId = response.getLong("SERVICE_ID");
                    this.logger.log(Level.INFO, "Created the service {0} for the product id {1}", new Object[] { serviceId, productId });
                    if (productId != -1L) {
                        IntegrationProductUtil.getNewInstance().linkProductAndService(productId, serviceId);
                    }
                    responseJSON = new IntegrationServiceAPIRequestHandler().getIntegrationServiceDetails(serviceId);
                    final Long validity = System.currentTimeMillis() + 15552000000L;
                    request = new JSONObject();
                    request.put("SERVICE_ID", response.getLong("SERVICE_ID"));
                    request.put("scope_ids", (Object)APIKeyGenerationHandler.getInstance().getAllPermissions());
                    request.put("USER_ID", (Object)userID);
                    request.put("VALIDITY", (Object)validity);
                    request.put("logged_in_user", (Object)userID);
                    response = APIKeyUtil.getNewInstance().createAPIKey(request);
                    break;
                }
                case 103: {
                    throw new APIHTTPException("COM0010", new Object[] { serviceName });
                }
            }
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while creating the service for the product", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
