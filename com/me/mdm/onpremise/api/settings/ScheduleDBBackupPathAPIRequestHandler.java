package com.me.mdm.onpremise.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ScheduleDBBackupPathAPIRequestHandler extends ApiRequestHandler
{
    ScheduleDBBackupFacade scheduleDBBackupFacade;
    
    public ScheduleDBBackupPathAPIRequestHandler() {
        this.scheduleDBBackupFacade = new ScheduleDBBackupFacade();
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.scheduleDBBackupFacade.checkBackupPath(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
