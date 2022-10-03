package com.me.mdm.api.core.deviceaccounts;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.deviceaccounts.AccountFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAccountConfigRequestHandler extends ApiRequestHandler
{
    AccountFacade account;
    
    public IndividualAccountConfigRequestHandler() {
        this.account = null;
        this.account = new AccountFacade();
    }
    
    @Override
    public Object doGet(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            request.urlStartKey = "configurations";
            response.put("RESPONSE", (Object)this.account.getAccountConfiguration(request.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            request.urlStartKey = "configurations";
            response.put("RESPONSE", (Object)this.account.addOrModifyAccountConfiguration(request.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            request.urlStartKey = "configurations";
            this.account.deleteAccountConfiguration(request.toJSONObject());
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
