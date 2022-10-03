package com.me.mdm.api.termsofuse;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.terms.TermsOfUseFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DownloadTermsOfUseFileAPIRequestHandler extends ApiRequestHandler
{
    TermsOfUseFacade termsOfUseFacade;
    
    public DownloadTermsOfUseFileAPIRequestHandler() {
        this.termsOfUseFacade = new TermsOfUseFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.termsOfUseFacade.downloadTerms(apiRequest);
            return null;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("TER0004", new Object[0]);
        }
    }
}
