package com.me.mdm.api.core.certificate;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.CertificateFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualCertificateAPIRequestHandler extends ApiRequestHandler
{
    CertificateFacade facade;
    
    public IndividualCertificateAPIRequestHandler() {
        this.facade = new CertificateFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "certificates";
            responseDetails.put("RESPONSE", (Object)this.facade.getCertificateDetail(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "certificates";
            responseDetails.put("RESPONSE", (Object)this.facade.deleteCertificate(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception while deleting certificate", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception while deleting certificate", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
}
