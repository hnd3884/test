package com.me.mdm.api.core.certificate;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.CertificateFacade;
import com.me.mdm.api.ApiRequestHandler;

public class CertificateAPIRequestHandler extends ApiRequestHandler
{
    CertificateFacade facade;
    
    public CertificateAPIRequestHandler() {
        this.facade = new CertificateFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.getCertificateDetails(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.addCertificate(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error during certificate add ", e);
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Error during certificate add ", (Throwable)e2);
            throw new APIHTTPException("COM0005", new Object[] { e2 });
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error during certificate add ", e3);
            throw new APIHTTPException("COM0004", new Object[] { e3 });
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            responseDetails.put("RESPONSE", (Object)this.facade.deleteCertificates(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw new APIHTTPException("COM0005", new Object[] { e2 });
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error during certificate delete ", e3);
            throw new APIHTTPException("COM0004", new Object[] { e3 });
        }
    }
}
