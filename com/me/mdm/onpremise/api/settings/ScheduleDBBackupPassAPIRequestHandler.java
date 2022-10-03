package com.me.mdm.onpremise.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ScheduleDBBackupPassAPIRequestHandler extends ApiRequestHandler
{
    ScheduleDBBackupFacade scheduleDBBackupFacade;
    
    public ScheduleDBBackupPassAPIRequestHandler() {
        this.scheduleDBBackupFacade = new ScheduleDBBackupFacade();
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.scheduleDBBackupFacade.getscheduleddbpassword(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
