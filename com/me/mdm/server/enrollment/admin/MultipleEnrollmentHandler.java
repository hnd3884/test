package com.me.mdm.server.enrollment.admin;

import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.MultipleTemplateAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.MultipleMgmtEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;

public class MultipleEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public MultipleEnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new MultipleMgmtEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new MultipleTemplateAssignUserCSVProcessor().operationLabel;
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        return super.getEnrollmentDetails(requestJSON);
    }
}
