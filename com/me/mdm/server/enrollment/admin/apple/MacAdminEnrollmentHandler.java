package com.me.mdm.server.enrollment.admin.apple;

import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.ModernMacManagementAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.MacModernMgmtEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class MacAdminEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public MacAdminEnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new MacModernMgmtEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new ModernMacManagementAssignUserCSVProcessor().operationLabel;
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        return super.getEnrollmentDetails(requestJSON);
    }
}
