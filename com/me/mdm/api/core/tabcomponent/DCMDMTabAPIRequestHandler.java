package com.me.mdm.api.core.tabcomponent;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DCMDMTabAPIRequestHandler extends ApiRequestHandler
{
    DCMDMTabFacade dcmdmTabFacade;
    
    public DCMDMTabAPIRequestHandler() {
        this.dcmdmTabFacade = new DCMDMTabFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseObject = new JSONObject();
            responseObject.put("RESPONSE", (Object)this.dcmdmTabFacade.getApplicableTabs());
            responseObject.put("status", 200);
            return responseObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Issue on fetching DC MDM tab components", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
