package com.me.mdm.server.enrollment.admin.windows;

import com.me.mdm.server.enrollment.adminenroll.WinModernMgmtAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.WindowsAzureADEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class WindowsModernMgmtEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public WindowsModernMgmtEnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new WindowsAzureADEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new WinModernMgmtAssignUserCSVProcessor().operationLabel;
    }
}
