package com.me.mdm.core.enrollment;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.message.MDMMessageProvider;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import java.util.Set;
import javax.ws.rs.core.UriBuilder;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;

public class AppleConfiguratorEnrollmentHandler extends AdminEnrollmentHandler
{
    public AppleConfiguratorEnrollmentHandler() {
        super(11, "AppleConfigDeviceForEnrollment", "AppleConfigEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateAppleConfigEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    public static void deleteAdminEnrollmentTemplate(final Long loginID) throws DataAccessException {
        DataAccess.delete("AppleConfigEnrollmentTemplate", new Criteria(Column.getColumn("AppleConfigEnrollmentTemplate", "LOGIN_ID"), (Object)loginID, 0));
    }
    
    public JSONObject getProfileDownloadURL(final Long userID, final Long customerID) throws Exception {
        final JSONObject downloadUrls = new JSONObject();
        try {
            final String templateToken = new EnrollmentTemplateHandler().getTemplateTokenForUserId(userID, this.templateType, customerID);
            final HashMap params = new HashMap();
            final MDMAPIKeyGeneratorAPI generator = MDMUserAPIKeyGenerator.getInstance();
            if (generator != null) {
                final JSONObject json = new JSONObject();
                json.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(userID));
                json.put("TEMPLATE_TYPE", this.templateType);
                final APIKey key = generator.generateAPIKey(json);
                params.put(key.getKeyName(), key.getKeyValue());
            }
            final String enrollmentUrl = this.constructUrl("/mdm/client/v1/ios/ac/" + templateToken, null);
            params.put("templateToken", templateToken);
            final String serviceConfigUrl = this.constructUrl("/mdm/client/v1/ios/MDMServiceConfig", params);
            downloadUrls.put("enrollmentUrl", (Object)enrollmentUrl);
            downloadUrls.put("serviceConfigUrl", (Object)serviceConfigUrl);
        }
        catch (final Exception ex) {
            Logger.getLogger(AppleConfiguratorEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return downloadUrls;
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
    
    public String constructUrl(final String path, final HashMap params) throws Exception {
        final UriBuilder builder = UriBuilder.fromUri(MDMApiFactoryProvider.getMDMUtilAPI().getServerURLOnTomcatPortForClientAuthSetup());
        builder.path(path);
        if (params != null) {
            final Set keys = params.keySet();
            for (final Object key : keys) {
                final String o = (String)key;
                builder.queryParam(o, new Object[] { params.get(o) });
            }
        }
        final String completeUrl = builder.build(new Object[0]).toURL().toString();
        return completeUrl;
    }
    
    public int getAdminEnrollRequestCount(final Long customerID, final Criteria criteria) throws Exception {
        return super.getAdminEnrollRequestCount(customerID, criteria);
    }
    
    public static void openUrlChangeMsg() {
        try {
            final boolean isAppleConfiguratorConfigured = Boolean.parseBoolean(MDMUtil.getSyMParameter("APPLE_CONFIG_2_CONFIGURED"));
            if (isAppleConfiguratorConfigured) {
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
                final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)11, 0);
                sq.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
                sq.addSelectColumn(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"));
                sq.addSelectColumn(Column.getColumn("EnrollmentTemplate", "ADDED_USER"));
                sq.setCriteria(templateCriteria);
                final DataObject acTemplatesDO = MDMUtil.getPersistence().get(sq);
                if (!acTemplatesDO.isEmpty()) {
                    final AppleConfiguratorEnrollmentHandler handlerObj = new AppleConfiguratorEnrollmentHandler();
                    Long customerID = null;
                    Long userID = null;
                    Criteria enrolledDevicesForThisUserCri = null;
                    final Iterator rowsIt = acTemplatesDO.getRows("EnrollmentTemplate");
                    while (rowsIt.hasNext()) {
                        final Row row = rowsIt.next();
                        customerID = (Long)row.get("CUSTOMER_ID");
                        userID = (Long)row.get("ADDED_USER");
                        enrolledDevicesForThisUserCri = new Criteria(Column.getColumn("EnrollmentTemplate", "ADDED_USER"), (Object)userID, 0);
                        if (handlerObj.getAdminEnrollRequestCount(customerID, enrolledDevicesForThisUserCri) > 0) {
                            MDMMessageProvider.getInstance().openMsgForCustomerUser(userID, "APPLE_CONFIG_URL_UPDATED", customerID);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            AppleConfiguratorEnrollmentHandler.logger.log(Level.SEVERE, "Exception when opening APPLE_CONFIG_URL_UPDATED messages.. ", ex);
        }
    }
    
    public static void closeUrlChangeMsg(final Long userID, final Long customerID) {
        AppleConfiguratorEnrollmentHandler.logger.log(Level.INFO, "Closing Apple Configurator URL updated msg for customer - {0}", customerID);
        MDMMessageProvider.getInstance().closeMsgForCustomerUser(userID, "APPLE_CONFIG_URL_UPDATED", customerID);
        MDMMessageProvider.getInstance().closeMsgForCustomerUser(userID, "APPLE_CONFIG_URL_MIGRATE", customerID);
    }
}
