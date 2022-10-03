package com.me.mdm.api.metainfo;

import org.json.JSONException;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ProductMetaAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    
    public ProductMetaAPIRequestHandler() {
        this.logger = Logger.getLogger(ProductMetaAPIRequestHandler.class.getName());
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        try {
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.getResponse());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception in ProductMetaAPIRequestHandler", e);
            throw new APIHTTPException(500, null, new Object[0]);
        }
        return responseJSON;
    }
    
    private JSONObject getResponse() {
        JSONObject product_meta = new JSONObject();
        try {
            product_meta = new JSONObject(MDMRestAPIFactoryProvider.getProductMetaAPI().getProductMeta().toString());
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception while getting product meta", (Throwable)e);
        }
        return product_meta;
    }
}
