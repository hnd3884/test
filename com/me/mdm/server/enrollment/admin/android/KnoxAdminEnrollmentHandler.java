package com.me.mdm.server.enrollment.admin.android;

import com.me.mdm.server.enrollment.adminenroll.KnoxAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.core.enrollment.KNOXAdminEnrollmentHandler;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class KnoxAdminEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public KnoxAdminEnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final KNOXAdminEnrollmentHandler adminEnrollmentHandler = new KNOXAdminEnrollmentHandler();
        final JSONObject additionalContext = new JSONObject();
        additionalContext.put("knoxAPKUrl", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 7));
        additionalContext.put("playStoreUrl", (Object)MDMUtil.getInstance().getMDMApplicationProperties().getProperty("PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION"));
        additionalContext.put("knoxJSONFile", (Object)adminEnrollmentHandler.getKnoxMobileEnrollmentProfile(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), APIUtil.getCustomerID(requestJSON)));
        additionalContext.put("playStoreUrl", (Object)MDMUtil.getInstance().getMDMApplicationProperties().getProperty("PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION"));
        json.put("additional_context", (Object)additionalContext);
        return json;
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new KNOXAdminEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new KnoxAssignUserCSVProcessor().operationLabel;
    }
}
