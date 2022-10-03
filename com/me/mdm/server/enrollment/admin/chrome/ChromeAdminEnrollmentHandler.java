package com.me.mdm.server.enrollment.admin.chrome;

import org.json.JSONArray;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.GSuiteChromeAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.GSuiteChromeDeviceEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class ChromeAdminEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public ChromeAdminEnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new GSuiteChromeDeviceEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new GSuiteChromeAssignUserCSVProcessor().operationLabel;
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final JSONObject managedDomains = new AdminEnrollmentFacade().getChromeEnrollDetails(requestJSON);
        final JSONArray jsonArray = managedDomains.getJSONArray("managed_domains");
        final JSONObject additionalContext = new JSONObject();
        additionalContext.put("managed_domains", (Object)managedDomains);
        json.put("additional_context", (Object)additionalContext);
        return json;
    }
}
