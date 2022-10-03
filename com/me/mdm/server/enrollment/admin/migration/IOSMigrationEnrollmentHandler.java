package com.me.mdm.server.enrollment.admin.migration;

import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class IOSMigrationEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public IOSMigrationEnrollmentHandler(final Integer template) {
        super(template);
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject request) throws Exception {
        final JSONObject response = super.getEnrollmentDetails(request);
        final com.me.mdm.core.enrollment.IOSMigrationEnrollmentHandler iosMigrationEnrollmentHandler = new com.me.mdm.core.enrollment.IOSMigrationEnrollmentHandler();
        final JSONObject profile = iosMigrationEnrollmentHandler.getProfileDownloadURL(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), APIUtil.getCustomerID(request));
        response.put("enrollmentURL", (Object)profile.optString("enrollmentUrl"));
        return response;
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new com.me.mdm.core.enrollment.IOSMigrationEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return null;
    }
}
