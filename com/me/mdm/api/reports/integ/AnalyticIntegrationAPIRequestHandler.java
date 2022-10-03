package com.me.mdm.api.reports.integ;

import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AnalyticIntegrationAPIRequestHandler extends ApiRequestHandler
{
    public Logger logger;
    
    public AnalyticIntegrationAPIRequestHandler() {
        this.logger = Logger.getLogger(AnalyticIntegrationAPIRequestHandler.class.getName());
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject messages = apiRequest.toJSONObject();
            if (!messages.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject body = messages.getJSONObject("msg_body");
            this.logger.log(Level.INFO, "Analytic Integration with MDM for Posting details : {0}", body);
            final MDMAnalyticRequestHandler mdmAnalyticRequestHandler = MDMApiFactoryProvider.getAnalyticRequestHandler();
            mdmAnalyticRequestHandler.addOrUpdateSettings(body);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject messageJSON = new JSONObject();
            messageJSON.put("message", (Object)"Success");
            responseJSON.put("RESPONSE", (Object)messageJSON);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while executing Analytic doPost", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Analytic Integration with MDM to get details ");
            final MDMAnalyticRequestHandler mdmAnalyticRequestHandler = MDMApiFactoryProvider.getAnalyticRequestHandler();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject analyticDetails = mdmAnalyticRequestHandler.getSettings();
            if (analyticDetails == null) {
                throw new APIHTTPException("ANALY0001", new Object[0]);
            }
            responseJSON.put("RESPONSE", (Object)analyticDetails);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception while executing Analytic doGet", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while executing Analytic doGet", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Analytic Integration with MDM to get details ");
            final MDMAnalyticRequestHandler mdmAnalyticRequestHandler = MDMApiFactoryProvider.getAnalyticRequestHandler();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            final JSONObject analyticDetails = mdmAnalyticRequestHandler.getSettings();
            if (analyticDetails == null) {
                throw new APIHTTPException("ANALY0001", new Object[0]);
            }
            mdmAnalyticRequestHandler.deleteSettings();
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception while executing Analytic doGet", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while executing Analytic doGet", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
