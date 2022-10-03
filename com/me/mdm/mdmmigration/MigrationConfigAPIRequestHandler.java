package com.me.mdm.mdmmigration;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MigrationConfigAPIRequestHandler extends APIRequestHandler
{
    private static Logger logger;
    
    @Override
    public JSONObject processRequest(final JSONObject msgJson) {
        JSONObject responseJson = new JSONObject();
        try {
            final String msgType = msgJson.get("msgType").toString();
            if (msgType.equals("AddServiceConfig")) {
                responseJson = this.handleConfigureAPIServiceRequest(this.getMsgContent(msgJson));
            }
            else if (msgType.equals("ModifyServiceConfig")) {
                responseJson = this.handleEditAPIServiceRequest(this.getMsgContent(msgJson));
            }
            else if (msgType.equals("AddAuthInfo")) {
                responseJson = this.handleAddAuthInfoRequest(this.getMsgContent(msgJson));
            }
            else if (msgType.equals("ModifyAuthInfo")) {
                responseJson = this.handleEditAuthInfoRequest(this.getMsgContent(msgJson));
            }
            else if (msgType.equals("GetAPIServices")) {
                responseJson = this.handleGetAPIServicesRequest(this.getMsgContent(msgJson));
            }
            else if (msgType.equals("GetServiceConfigurations")) {
                responseJson = this.handleGetServiceConfigurationsRequest(this.getMsgContent(msgJson));
            }
            else if (msgType.equals("GetServiceConfigurationDetails")) {
                responseJson = this.handleGetServiceConfigurationDetailsRequest(this.getMsgContent(msgJson));
            }
            else if (msgType.equals("DeleteServiceConfig")) {
                responseJson = this.handleDeleteServiceConfigRequest(this.getMsgContent(msgJson));
            }
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Exception while APIRequestHandler processRequest() ", e);
        }
        return responseJson;
    }
    
    private JSONObject handleConfigureAPIServiceRequest(final JSONObject msgJson) {
        JSONObject json = new JSONObject();
        try {
            json = new APIServiceDataHandler().addServiceConfig(msgJson);
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while handleConfigureAPIServiceRequest ", e);
            try {
                json.put("Error", (Object)"100");
                json.put("ErrorMsg", (Object)"Internal Server Error while configuring migration service");
                json.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {}
        }
        return json;
    }
    
    private JSONObject handleEditAPIServiceRequest(final JSONObject msgJson) {
        JSONObject json = new JSONObject();
        try {
            json = new APIServiceDataHandler().editServiceConfig(msgJson, null);
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while handleEditAPIServiceRequest ", e);
            try {
                json.put("Error", (Object)"100");
                json.put("ErrorMsg", (Object)"Internal Server Error while configuring migration service");
                json.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {}
        }
        return json;
    }
    
    private JSONObject handleAddAuthInfoRequest(final JSONObject msgJson) {
        JSONObject json = new JSONObject();
        try {
            json = new APIServiceDataHandler().addAuthDetails(msgJson);
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while handleAddAuthInfoRequest ", e);
            try {
                json.put("Error", (Object)"100");
                json.put("ErrorMsg", (Object)"Internal Server Error while configuring migration service");
                json.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {}
        }
        return json;
    }
    
    private JSONObject handleEditAuthInfoRequest(final JSONObject msgJson) {
        JSONObject json = new JSONObject();
        try {
            json = new APIServiceDataHandler().editAuthDetails(msgJson);
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while handleEditAuthInfoRequest ", e);
            try {
                json.put("Error", (Object)"100");
                json.put("ErrorMsg", (Object)"Internal Server Error while configuring migration service");
                json.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {}
        }
        return json;
    }
    
    private JSONObject handleGetAPIServicesRequest(final JSONObject msgJson) {
        JSONObject json = new JSONObject();
        try {
            json = new APIServiceDataHandler().getAPIServicesList();
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while getAPIServicesList ", e);
            try {
                json.put("Error", (Object)"100");
                json.put("ErrorMsg", (Object)"Internal Server Error while configuring migration service");
                json.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {}
        }
        return json;
    }
    
    private JSONObject handleGetServiceConfigurationsRequest(final JSONObject msgJson) {
        JSONObject json = new JSONObject();
        try {
            json = new APIServiceDataHandler().getAPIServiceConfigurationsList(null);
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while getAPIServiceConfigurationsList ", e);
            try {
                json.put("Error", (Object)"100");
                json.put("ErrorMsg", (Object)"Internal Server Error while configuring migration service");
                json.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {}
        }
        return json;
    }
    
    private JSONObject handleGetServiceConfigurationDetailsRequest(final JSONObject msgJson) {
        JSONObject json = new JSONObject();
        try {
            json = new APIServiceDataHandler().getAPIServiceConfigDetails(msgJson);
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while getAPIServiceConfigDetails ", e);
            try {
                json.put("Error", (Object)"100");
                json.put("ErrorMsg", (Object)"Internal Server Error while configuring migration service");
                json.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {}
        }
        return json;
    }
    
    private JSONObject handleDeleteServiceConfigRequest(final JSONObject msgJson) {
        JSONObject json = new JSONObject();
        try {
            json = new APIServiceDataHandler().deleteServiceConfig(msgJson);
        }
        catch (final Exception e) {
            MigrationConfigAPIRequestHandler.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while deleteServiceConfig ", e);
            try {
                json.put("Error", (Object)"100");
                json.put("ErrorMsg", (Object)"Internal Server Error while configuring migration service");
                json.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {}
        }
        return json;
    }
    
    static {
        MigrationConfigAPIRequestHandler.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
