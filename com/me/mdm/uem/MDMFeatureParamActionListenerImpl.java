package com.me.mdm.uem;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMFeatureParamActionListenerImpl
{
    private Logger logger;
    
    public MDMFeatureParamActionListenerImpl() {
        this.logger = Logger.getLogger("MDMModernMgmtLogger");
    }
    
    public JSONObject isFeatureEnabled(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final String paramName = params.getString("paramName");
            final boolean isEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled(paramName);
            returnObj.put("paramValue", isEnabled);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject updateMDMFeatureParameter(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final String paramName = params.getString("paramName");
            final String paramValue = params.getString("paramValue");
            MDMFeatureParamsHandler.getInstance();
            MDMFeatureParamsHandler.updateMDMFeatureParameter(paramName, paramValue);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
}
