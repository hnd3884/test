package com.me.mdm.api.termsofuse;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.terms.TermsOfUseFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualTermsOfUseAPIRequestHandler extends ApiRequestHandler
{
    TermsOfUseFacade termsOfUseFacade;
    
    public IndividualTermsOfUseAPIRequestHandler() {
        this.termsOfUseFacade = new TermsOfUseFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.termsOfUseFacade.getTermsDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.termsOfUseFacade.saveTerms(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.termsOfUseFacade.removeTerms(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
