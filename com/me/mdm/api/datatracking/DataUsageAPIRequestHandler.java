package com.me.mdm.api.datatracking;

import com.me.mdm.http.HttpException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.datausage.DataUsageFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DataUsageAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws HttpException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new DataUsageFacade().getResourceDataUsageSummary(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException | DataAccessException ex) {
            this.logger.log(Level.WARNING, "Exception when getting associated Data usage profile", ex);
            throw new APIHTTPException(500, ex.getMessage(), new Object[0]);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Device not found", e);
            throw e;
        }
    }
}
