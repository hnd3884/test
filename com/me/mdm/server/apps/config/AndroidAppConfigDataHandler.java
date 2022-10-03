package com.me.mdm.server.apps.config;

import org.json.JSONArray;
import com.me.mdm.api.APIUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONObject;

public class AndroidAppConfigDataHandler extends AppConfigDataPolicyHandler
{
    @Override
    public void resetAppConfigCommand(final Long configDataItemId, final JSONObject appConfigJSON) {
        this.invokeAppConfigCommand(configDataItemId);
    }
    
    @Override
    public void addAppConfigCommand(final Long configDataItemId, final JSONObject appConfigJSON) {
        this.invokeAppConfigCommand(configDataItemId);
    }
    
    private void invokeAppConfigCommand(final Long configDataItemId) {
        try {
            final ArrayList resourceList = this.getAssociatedResourceList(configDataItemId, Boolean.FALSE);
            DeviceCommandRepository.getInstance().addApplicationConfigurationCommand(resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exeption in app config command distribution", e);
        }
    }
    
    @Override
    public JSONObject addAppConfiguration(final JSONObject apiRequest) throws Exception {
        final Long configDataItem = APIUtil.getResourceID(apiRequest, "payloaditem_id");
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        final Long userID = APIUtil.getUserID(apiRequest);
        final JSONObject configJSON = apiRequest.getJSONObject("msg_body");
        final JSONArray appConfigurationArr = configJSON.getJSONArray("app_configuration");
        final JSONObject managedAppConfigurationData = new JSONObject();
        managedAppConfigurationData.put("CUSTOMER_ID", (Object)customerID);
        managedAppConfigurationData.put("APP_CONFIG", (Object)appConfigurationArr);
        final JSONObject managedAppConfiguration = new JSONObject();
        final Long appConfTempId = new AppConfigDataHandler().getAppConfigTemplateIDFromAppId(configJSON.getLong("APP_ID"));
        managedAppConfiguration.put("APP_CONFIG_TEMPLATE_ID", (Object)appConfTempId);
        managedAppConfiguration.put("APP_CONFIG_NAME", (Object)"Managed App Configuration");
        managedAppConfiguration.put("LAST_MODIFIED_BY", (Object)userID);
        managedAppConfiguration.put("ManagedAppConfigurationData", (Object)managedAppConfigurationData);
        final JSONObject restrictionSchema = new JSONObject();
        restrictionSchema.put("ManagedAppConfiguration", (Object)managedAppConfiguration);
        restrictionSchema.put("PUBLISH_PROFILE", (Object)Boolean.FALSE);
        restrictionSchema.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(apiRequest));
        restrictionSchema.put("LAST_MODIFIED_BY", (Object)APIUtil.getUserID(apiRequest));
        this.addOrUpdateAppConfigPolicy(configDataItem, restrictionSchema);
        final JSONObject headerJSON = apiRequest.getJSONObject("msg_header");
        headerJSON.put("payloaditem_id", (Object)configDataItem);
        return this.getAppConfiguration(apiRequest);
    }
    
    @Override
    public JSONObject modifyAppConfiguration(final JSONObject apiRequest) throws Exception {
        return this.addAppConfiguration(apiRequest);
    }
}
