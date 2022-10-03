package com.me.mdm.api.termsofuse;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.terms.TermsOfUseFacade;
import com.me.mdm.api.ApiRequestHandler;

public class GetTermsOfUseSupportedLanguagesAPIRequestHandler extends ApiRequestHandler
{
    TermsOfUseFacade termsOfUseFacade;
    
    public GetTermsOfUseSupportedLanguagesAPIRequestHandler() {
        this.termsOfUseFacade = new TermsOfUseFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.termsOfUseFacade.getSupportedLanguages());
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
