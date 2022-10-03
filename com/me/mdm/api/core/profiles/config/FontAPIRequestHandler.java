package com.me.mdm.api.core.profiles.config;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.FontFacade;
import com.me.mdm.api.ApiRequestHandler;

public class FontAPIRequestHandler extends ApiRequestHandler
{
    FontFacade facade;
    
    public FontAPIRequestHandler() {
        this.facade = new FontFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.facade.getAllFonts(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting font ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.facade.deleteBulkFonts(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while deleting bulk webclips", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
