package com.me.mdm.api.certificates;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.digicert.v1.DigicertDependenciesCheckHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class DigicertDependencyCheckApiHandler extends ApiRequestHandler
{
    Logger logger;
    
    public DigicertDependencyCheckApiHandler() {
        this.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) {
        this.logger.log(Level.INFO, "Verifying whether the dependencies for digicert are fulfilled");
        final JSONObject response = new JSONObject();
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CUSTOMER_ID", (Object)APIUtil.optCustomerID(apiRequest.toJSONObject()));
            response.put("RESPONSE", (Object)DigicertDependenciesCheckHandler.checkDependencies(jsonObject));
            response.put("status", 200);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while checking digicert dependencies", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
