package com.me.mdm.api.core.deviceaccounts;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.deviceaccounts.AccountFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AccountRequestHandler extends ApiRequestHandler
{
    AccountFacade account;
    
    public AccountRequestHandler() {
        this.account = null;
        this.account = new AccountFacade();
    }
    
    @Override
    public Object doGet(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.account.getAllComputerAccounts(request.toJSONObject()));
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
    public Object doPost(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.account.addOrModifyAccount(request.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
