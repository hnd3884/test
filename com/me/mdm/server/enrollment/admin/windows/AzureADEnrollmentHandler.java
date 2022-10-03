package com.me.mdm.server.enrollment.admin.windows;

import java.util.Iterator;
import java.util.HashMap;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.WinAzureADAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.WindowsAzureADEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class AzureADEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public AzureADEnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new WindowsAzureADEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new WinAzureADAssignUserCSVProcessor().operationLabel;
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final WindowsAzureADEnrollmentHandler adminEnrollmentHandler = new WindowsAzureADEnrollmentHandler();
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final JSONObject additionalContext = new JSONObject();
        additionalContext.put("azureADIntegrationDetails", (Object)this.convertToJSON(adminEnrollmentHandler.getAzureADEnrollmentDetails(APIUtil.getCustomerID(requestJSON))));
        json.put("additional_context", (Object)additionalContext);
        return json;
    }
    
    JSONObject convertToJSON(final HashMap hashMap) throws Exception {
        final JSONObject finalJSON = new JSONObject();
        for (final String key : hashMap.keySet()) {
            Object value = hashMap.get(key);
            if (value instanceof HashMap) {
                value = this.convertToJSON((HashMap)value);
            }
            finalJSON.put(key, value);
        }
        return finalJSON;
    }
}
