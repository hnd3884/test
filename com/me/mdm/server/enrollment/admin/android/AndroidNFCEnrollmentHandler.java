package com.me.mdm.server.enrollment.admin.android;

import java.util.Hashtable;
import com.me.mdm.server.enrollment.adminenroll.AndroidNFCAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import com.me.mdm.core.enrollment.AndroidAdminEnrollmentHandler;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class AndroidNFCEnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public AndroidNFCEnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final AndroidAdminEnrollmentHandler adminEnrollmentHandler = new AndroidAdminEnrollmentHandler();
        final AdminDeviceHandler adminDeviceHandler = new AdminDeviceHandler();
        final JSONObject additionalContext = new JSONObject();
        additionalContext.put("adminAppCount", adminDeviceHandler.getAdminDeviceCount(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()));
        additionalContext.put("adminAgentURL", (Object)adminDeviceHandler.getAdminAgentURL());
        additionalContext.put("adminDeviceModelName", (Object)adminDeviceHandler.getAdminDeviceModel(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()));
        final Properties serverProp = MDMUtil.getDCServerInfo();
        final String serverIP = ((Hashtable<K, String>)serverProp).get("SERVER_MAC_NAME");
        final String serverPort = String.valueOf(((Hashtable<K, Object>)serverProp).get("SERVER_PORT"));
        additionalContext.put("serverName", (Object)serverIP);
        additionalContext.put("serverPort", (Object)serverPort);
        json.put("additional_context", (Object)additionalContext);
        return json;
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new AndroidAdminEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new AndroidNFCAssignUserCSVProcessor().operationLabel;
    }
}
