package com.me.mdm.server.enrollment.admin.apple;

import com.me.mdm.server.enrollment.adminenroll.AppleConfigAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class AppleConfiguratorEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public AppleConfiguratorEnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler adminEnrollmentHandler = new com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler();
        final JSONObject downloadURL = adminEnrollmentHandler.getProfileDownloadURL(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), APIUtil.getCustomerID(requestJSON));
        final JSONObject additionalContext = new JSONObject();
        additionalContext.put("enrollmentUrl", (Object)downloadURL.optString("enrollmentUrl"));
        additionalContext.put("serviceConfigUrl", (Object)downloadURL.optString("serviceConfigUrl"));
        final JSONObject serverCompliance = new JSONObject();
        serverCompliance.put("isThirdPartyCertConfigured", (Object)String.valueOf(SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()));
        additionalContext.put("serverDetails", (Object)serverCompliance);
        json.put("additional_context", (Object)additionalContext);
        return json;
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new AppleConfigAssignUserCSVProcessor().operationLabel;
    }
}
