package com.me.mdm.api.core.azuremam;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.directory.service.mam.AzureMamAuditHandler;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AzureMamAuditLog extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequest.toJSONObject());
            if (customerID != null) {
                final JSONObject j = apiRequest.toJSONObject().getJSONObject("msg_body");
                final int eventId = j.getInt("event_id");
                final long resourceId = j.optLong("resource_id");
                final Object remarks_args = j.opt("remarks_args");
                switch (eventId) {
                    case 2083: {
                        AzureMamAuditHandler.getInstance().logEvent(eventId, resourceId, "mdm.mam.added", remarks_args, customerID);
                        break;
                    }
                    case 2084: {
                        AzureMamAuditHandler.getInstance().logEvent(eventId, resourceId, "mdm.mam.upgrade", remarks_args, customerID);
                        break;
                    }
                    case 2085: {
                        AzureMamAuditHandler.getInstance().logEvent(eventId, resourceId, "mdm.mam.removed", remarks_args, customerID);
                        break;
                    }
                    case 2086: {
                        AzureMamAuditHandler.getInstance().logEvent(eventId, resourceId, "mdm.mam.modify.group", remarks_args, customerID);
                        break;
                    }
                    case 2087: {
                        AzureMamAuditHandler.getInstance().logEvent(eventId, resourceId, "mdm.mam.modify.apps", remarks_args, customerID);
                        break;
                    }
                }
                final JSONObject response = new JSONObject();
                response.put("status", 201);
                return response;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error APIHTTPException Occured in doPost /azuremam/", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in doPost /azuremam/", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
