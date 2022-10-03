package com.me.mdm.api.core.certificate;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.CertificateFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DownloadCertificateAPIRequestHandler extends ApiRequestHandler
{
    CertificateFacade facade;
    
    public DownloadCertificateAPIRequestHandler() {
        this.facade = new CertificateFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "certificates";
            this.facade.downloadCertificate(apiRequest);
            return responseDetails;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
