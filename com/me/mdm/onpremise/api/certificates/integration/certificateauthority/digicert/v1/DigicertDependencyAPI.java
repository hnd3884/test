package com.me.mdm.onpremise.api.certificates.integration.certificateauthority.digicert.v1;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DigicertDependencyAPI extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        try {
            final boolean isDigicertDependencyDownloadSuccessful = DigicertDependencyDownloadHandler.getInstance().downloadDependencies();
            if (isDigicertDependencyDownloadSuccessful) {
                response.put("status", 204);
                return response;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
