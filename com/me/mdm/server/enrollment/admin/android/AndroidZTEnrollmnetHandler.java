package com.me.mdm.server.enrollment.admin.android;

import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.AndroidZeroTouchAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.AndroidZTEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class AndroidZTEnrollmnetHandler extends BaseAdminEnrollmentHandler
{
    public AndroidZTEnrollmnetHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new AndroidZTEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new AndroidZeroTouchAssignUserCSVProcessor().operationLabel;
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final AndroidZTEnrollmentHandler adminEnrollmentHandler = new AndroidZTEnrollmentHandler();
        final JSONObject additionalContext = new JSONObject();
        additionalContext.put("ztJSONFile", (Object)adminEnrollmentHandler.getZTEnrollmentProfile(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), APIUtil.getCustomerID(requestJSON)));
        json.put("additional_context", (Object)additionalContext);
        return json;
    }
}
