package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class KNOXAdminEnrollmentHandler extends AdminEnrollmentHandler
{
    public static Logger logger;
    
    public KNOXAdminEnrollmentHandler() {
        super(21, "KNOXMobileDeviceForEnrollment", "KNOXMobileEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateKNOXAdminEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    public static void deleteKNOXEnrollmentTemplate(final Long loginID) throws DataAccessException {
        DataAccess.delete("KNOXMobileEnrollmentTemplate", new Criteria(Column.getColumn("KNOXMobileEnrollmentTemplate", "LOGIN_ID"), (Object)loginID, 0));
    }
    
    public String getKnoxMobileEnrollmentProfile(final Long userId, final Long customerId) throws Exception {
        final String port = ((Hashtable<K, Object>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_HTTPS_PORT") + "";
        final String fqdn = ((Hashtable<K, String>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_ADDRESS");
        final String templateToken = new EnrollmentTemplateHandler().getTemplateTokenForUserId(userId, this.templateType, customerId);
        final HashMap dataToSend = new HashMap();
        dataToSend.put("Fqdn", fqdn);
        dataToSend.put("Port", port);
        dataToSend.put("Token", templateToken);
        final HashMap hashMap = dataToSend;
        final String s = "CI";
        CustomerInfoUtil.getInstance();
        hashMap.put(s, CustomerInfoUtil.isSAS());
        final MDMAPIKeyGeneratorAPI generator = MDMUserAPIKeyGenerator.getInstance();
        if (generator != null) {
            final JSONObject json = new JSONObject();
            json.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(userId));
            json.put("TEMPLATE_TYPE", this.templateType);
            final APIKey key = generator.generateAPIKey(json);
            dataToSend.put("TN", key.getKeyName());
            dataToSend.put("TV", key.getKeyValue());
        }
        return JSONUtil.mapToJSON(dataToSend).toString();
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
    
    static {
        KNOXAdminEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
