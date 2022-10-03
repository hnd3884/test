package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AndroidZTEnrollmentHandler extends AdminEnrollmentHandler
{
    public static Logger logger;
    public static String ADMIN_BUNDLE_KEY;
    
    public AndroidZTEnrollmentHandler() {
        super(23, "AndroidZTDeviceForEnrollment", "AndroidZTEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateZTEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    public static void deleteAdminEnrollmentTemplate(final Long loginID) throws DataAccessException {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AndroidZTEnrollmentTemplate"));
            final Join templateJoin = new Join("AndroidZTEnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            sQuery.addJoin(templateJoin);
            sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria loginIdCriteria = new Criteria(Column.getColumn("AndroidZTEnrollmentTemplate", "LOGIN_ID"), (Object)loginID, 0);
            sQuery.setCriteria(loginIdCriteria);
            DataAccess.delete("AndroidZTEnrollmentTemplate", loginIdCriteria);
        }
        catch (final Exception ex) {
            Logger.getLogger(AndroidZTEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getZTEnrollmentProfile(final Long userId, final Long customerId) throws Exception {
        final String port = ((Hashtable<K, Object>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_HTTPS_PORT") + "";
        final String fqdn = ((Hashtable<K, String>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_ADDRESS");
        final String templateToken = new EnrollmentTemplateHandler().getTemplateTokenForUserId(userId, this.templateType, customerId);
        final JSONObject dataToSend = new JSONObject();
        dataToSend.put("Fqdn", (Object)fqdn);
        dataToSend.put("Port", (Object)port);
        dataToSend.put("Token", (Object)templateToken);
        final JSONObject jsonObject = dataToSend;
        final String s = "CI";
        CustomerInfoUtil.getInstance();
        jsonObject.put(s, CustomerInfoUtil.isSAS());
        final MDMAPIKeyGeneratorAPI generator = MDMUserAPIKeyGenerator.getInstance();
        if (generator != null) {
            final JSONObject json = new JSONObject();
            json.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(userId));
            json.put("TEMPLATE_TYPE", this.templateType);
            final APIKey key = generator.generateAPIKey(json);
            dataToSend.put("TN", (Object)key.getKeyName());
            dataToSend.put("TV", (Object)key.getKeyValue());
        }
        final JSONObject enrollmentProfile = new JSONObject();
        enrollmentProfile.put(AndroidZTEnrollmentHandler.ADMIN_BUNDLE_KEY, (Object)dataToSend);
        return enrollmentProfile.toString();
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("AndroidZTEnrollmentTemplate", "TEMPLATE_ID"), (Object)templateId, 0);
        final int recordCount = DBUtil.getRecordCount("AndroidZTEnrollmentTemplate", "LOGIN_ID", criteria);
        return recordCount > 0;
    }
    
    static {
        AndroidZTEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
        AndroidZTEnrollmentHandler.ADMIN_BUNDLE_KEY = "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE";
    }
}
