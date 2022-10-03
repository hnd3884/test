package com.me.mdm.server.enrollment.admin.android;

import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import org.apache.http.client.utils.URIBuilder;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.AndroidQRAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class AndroidQREnrollmentHandler extends BaseAdminEnrollmentHandler
{
    public static final String USER_PARAM_KEY_ANDROID_EMM_TINY_URL = "ANDROID_EMM_TINY_URL";
    
    public AndroidQREnrollmentHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new com.me.mdm.core.enrollment.AndroidQREnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new AndroidQRAssignUserCSVProcessor().operationLabel;
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final JSONObject jsonObject = new JSONObject();
        final Long userID = APIUtil.getUserID(requestJSON);
        jsonObject.put("qr_data", (Object)new com.me.mdm.core.enrollment.AndroidQREnrollmentHandler().getQREnrollmentProfile(APIUtil.getUserID(requestJSON), APIUtil.getCustomerID(requestJSON)));
        jsonObject.put("adv_qr_data", (Object)new com.me.mdm.core.enrollment.AndroidQREnrollmentHandler().getAdvQREnrollmentProfile(APIUtil.getUserID(requestJSON), APIUtil.getCustomerID(requestJSON)));
        final String enrollmentURL = this.getProfileDownloadURL(userID, APIUtil.getCustomerID(requestJSON));
        try {
            final JSONObject userParamJSON = MDMUtil.getUserParameters(userID, new String[] { "ANDROID_EMM_TINY_URL" });
            if (userParamJSON != null && userParamJSON.has("ANDROID_EMM_TINY_URL")) {
                jsonObject.put("enrollmentTinyURL", (Object)userParamJSON.getString("ANDROID_EMM_TINY_URL"));
            }
            else if (ApiFactoryProvider.getTinyURLHandler() != null) {
                final String tinyUrl = ApiFactoryProvider.getTinyURLHandler().getTinyURL((String)null, enrollmentURL, (Long)null, (String)null);
                MDMUtil.updateUserParameters(userID, new JSONObject().put("ANDROID_EMM_TINY_URL", (Object)tinyUrl));
                jsonObject.put("enrollmentTinyURL", (Object)tinyUrl);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception in TinyURL handling ", ex);
        }
        jsonObject.put("enrollmentURL", (Object)enrollmentURL);
        json.put("additional_context", (Object)jsonObject);
        return json;
    }
    
    public String getProfileDownloadURL(final Long userID, final Long customerID) {
        try {
            final String androdEnrollmentURL = MDMEnrollmentUtil.getInstance().getServerBaseURL() + "/mdm/client/v1/androidemmtoken";
            final MDMAPIKeyGeneratorAPI generator = MDMUserAPIKeyGenerator.getInstance();
            if (generator != null) {
                final JSONObject json = new JSONObject();
                json.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(userID));
                json.put("TEMPLATE_TYPE", 22);
                final APIKey key = generator.generateAPIKey(json);
                final URIBuilder builder = new URIBuilder(androdEnrollmentURL);
                builder.addParameter(key.getKeyName(), key.getKeyValue());
                builder.addParameter("templateToken", new EnrollmentTemplateHandler().getTemplateTokenForUserId(userID, 22, customerID));
                builder.addParameter("customerId", String.valueOf(customerID));
                return builder.build().toURL().toString();
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(AppleConfiguratorEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
