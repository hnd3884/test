package com.me.mdm.api;

import java.util.Iterator;
import java.util.Properties;
import com.adventnet.i18n.I18N;
import java.util.Map;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.mdm.api.error.APIHTTPException;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductUrlLoaderAPIHandler extends ApiRequestHandler
{
    Logger logger;
    
    public ProductUrlLoaderAPIHandler() {
        this.logger = Logger.getLogger(ProductUrlLoaderAPIHandler.class.getCanonicalName());
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final HttpServletRequest request = apiRequest.httpServletRequest;
        String key = null;
        try {
            key = apiRequest.getParameterList().get("key");
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception while getting key from url");
        }
        final JSONObject responseJsonObject = new JSONObject();
        if (key == null || key.trim().equals("")) {
            this.loadAllValues(responseJsonObject);
        }
        else {
            this.loadSingleValue(responseJsonObject, key);
        }
        return responseJsonObject;
    }
    
    private JSONObject loadAllValues(final JSONObject responseJsonObject) throws APIHTTPException {
        final JSONObject jsonObject = new JSONObject();
        final Properties properties = ProductUrlLoader.getInstance().getGeneralProperites();
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            final String key = entry.getKey();
            String value = entry.getValue();
            try {
                value = I18N.getMsg(value, new Object[0]);
                jsonObject.put(key, (Object)value);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while loading General Properties (Key : " + key + ") (Value : " + value + ") : ", e);
            }
        }
        try {
            responseJsonObject.put("RESPONSE", (Object)jsonObject);
            responseJsonObject.put("status", 200);
            return responseJsonObject;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in loadAllValues() : ", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject loadSingleValue(final JSONObject responseJsonObject, final String key) throws APIHTTPException {
        try {
            final JSONObject jsonObject = new JSONObject();
            final Properties properties = ProductUrlLoader.getInstance().getGeneralProperites();
            String value = "";
            for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
                final String prodKey = entry.getKey();
                if (prodKey.equalsIgnoreCase(key)) {
                    value = entry.getValue();
                    break;
                }
            }
            value = I18N.getMsg(value, new Object[0]);
            jsonObject.put(key, (Object)value);
            responseJsonObject.put("RESPONSE", (Object)jsonObject);
            responseJsonObject.put("status", 200);
            return responseJsonObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in loadSingleValue() : ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
