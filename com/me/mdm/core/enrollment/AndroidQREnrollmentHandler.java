package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import com.me.mdm.api.APIUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AndroidQREnrollmentHandler extends AdminEnrollmentHandler
{
    public static Logger logger;
    public static final String QR_FOLDER_NAME = "qrcodes";
    private static final String QR_FILE_NAME = "qr.png";
    private static final String ADV_QR_FILE_NAME = "adv_qr.png";
    
    public AndroidQREnrollmentHandler() {
        super(22, "AndroidQRDeviceForEnrollment", "AndroidQREnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateQREnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    public static void deleteAdminEnrollmentTemplate(final Long loginID) throws DataAccessException {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AndroidQREnrollmentTemplate"));
            final Join templateJoin = new Join("AndroidQREnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            sQuery.addJoin(templateJoin);
            sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria loginIdCriteria = new Criteria(Column.getColumn("AndroidQREnrollmentTemplate", "LOGIN_ID"), (Object)loginID, 0);
            sQuery.setCriteria(loginIdCriteria);
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            final Iterator iterator = dO.getRows("EnrollmentTemplate");
            final List customerIdList = DBUtil.getColumnValuesAsList(iterator, "CUSTOMER_ID");
            DataAccess.delete("AndroidQREnrollmentTemplate", loginIdCriteria);
        }
        catch (final Exception ex) {
            Logger.getLogger(AndroidQREnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getQREnrollmentProfile(final Long userId, final Long customerId) throws Exception {
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
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("isAndroidEMMBYODEnrollment")) {
            final JSONObject additionalDetails = new JSONObject();
            additionalDetails.put("isDirectEnroll", true);
            dataToSend.put("additionalDetails", (Object)additionalDetails);
        }
        return dataToSend.toString();
    }
    
    public String getAdvQREnrollmentProfile(final Long userId, final Long customerId) throws Exception {
        final JSONObject advQRData = new JSONObject();
        advQRData.put("android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME", (Object)MDMUtil.getInstance().getMDMApplicationProperties().getProperty("PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME"));
        advQRData.put("android.app.extra.PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM", (Object)MDMUtil.getInstance().getMDMApplicationProperties().getProperty("PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM"));
        advQRData.put("android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION", (Object)MDMUtil.getInstance().getMDMApplicationProperties().getProperty("PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION"));
        advQRData.put("android.app.extra.PROVISIONING_SKIP_ENCRYPTION", true);
        advQRData.put("android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE", (Object)new JSONObject(this.getQREnrollmentProfile(userId, customerId)));
        advQRData.put("android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED", true);
        final JSONObject wifiConfig = this.getWifiConfiguration(userId, customerId);
        if (wifiConfig.length() > 0) {
            advQRData.put("android.app.extra.PROVISIONING_WIFI_SSID", (Object)String.valueOf(wifiConfig.get("ssid")));
            advQRData.put("android.app.extra.PROVISIONING_WIFI_PASSWORD", (Object)String.valueOf(wifiConfig.get("password")));
            advQRData.put("android.app.extra.PROVISIONING_WIFI_SECURITY_TYPE", (Object)String.valueOf(wifiConfig.get("security_type")));
            advQRData.put("android.app.extra.PROVISIONING_WIFI_HIDDEN", true);
        }
        return advQRData.toString();
    }
    
    public Long generateAndSaveQRCode(final Long userId, final Long customerId, final boolean forceGenerate) throws Exception {
        return System.currentTimeMillis();
    }
    
    public Long generateAndSaveAdvQRCode(final Long userId, final Long customerId, final boolean forceGenerate) throws Exception {
        return System.currentTimeMillis();
    }
    
    public void regenerateQRCodes() {
        try {
            AndroidQREnrollmentHandler.logger.log(Level.INFO, "Regenerating QR codes for all users");
            final Long[] customerList = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerList != null) {
                for (final Long customerId : customerList) {
                    final List loginIdList = DMUserHandler.getLoginIDsForAAARoleName("MDM_Enrollment_Write", customerId);
                    for (final Object loginId : loginIdList) {
                        final Long userId = DMUserHandler.getDCUserID((Long)loginId);
                        try {
                            this.generateAndSaveQRCode(userId, customerId, true);
                            this.generateAndSaveAdvQRCode(userId, customerId, true);
                            AndroidQREnrollmentHandler.logger.log(Level.INFO, "QR regenarated for customerId-userid {0}-{1}", new Object[] { customerId, userId });
                        }
                        catch (final Exception ex) {
                            AndroidQREnrollmentHandler.logger.log(Level.SEVERE, ex, () -> "Exception when regenerating QR codes userid-" + n + ":");
                        }
                    }
                }
            }
        }
        catch (final SyMException ex2) {
            AndroidQREnrollmentHandler.logger.log(Level.SEVERE, "Exception when fetching customer list for regenerating QR codes:", (Throwable)ex2);
        }
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("AndroidQREnrollmentTemplate", "TEMPLATE_ID"), (Object)templateId, 0);
        final int recordCount = DBUtil.getRecordCount("AndroidQREnrollmentTemplate", "LOGIN_ID", criteria);
        return recordCount > 0;
    }
    
    public JSONObject getWifiConfiguration(final Long userId, final Long customerId) throws Exception {
        final JSONObject wifiConfig = new JSONObject();
        final long templateId = new EnrollmentTemplateHandler().getTemplateTokenIdForUserId(userId, customerId, 22);
        final Criteria templateIdCriteria = new Criteria(Column.getColumn("AdminEnrollmentWifiConfiguration", "TEMPLATE_ID"), (Object)templateId, 0);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AdminEnrollmentWifiConfiguration"));
        sQuery.setCriteria(templateIdCriteria);
        sQuery.addSelectColumn(new Column("AdminEnrollmentWifiConfiguration", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("AdminEnrollmentWifiConfiguration");
            wifiConfig.put("ssid", row.get("SSID"));
            wifiConfig.put("password", row.get("PASSWORD"));
            wifiConfig.put("security_type", row.get("SECURITY_TYPE"));
        }
        return wifiConfig;
    }
    
    public void addOrUpdateWifiConfiguration(final JSONObject requestJSON) throws Exception {
        final long userId = APIUtil.getUserID(requestJSON);
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        final long templateId = new EnrollmentTemplateHandler().getTemplateTokenIdForUserId(userId, customerId, 22);
        final Criteria templateIdCriteria = new Criteria(Column.getColumn("AdminEnrollmentWifiConfiguration", "TEMPLATE_ID"), (Object)templateId, 0);
        final DataObject DO = MDMUtil.getPersistence().get("AdminEnrollmentWifiConfiguration", templateIdCriteria);
        final JSONObject messageBody = requestJSON.getJSONObject("msg_body");
        final String ssid = String.valueOf(messageBody.get("ssid"));
        final String password = String.valueOf(messageBody.get("password"));
        final String securityType = String.valueOf(messageBody.get("security_type"));
        Row msgRow = null;
        if (!DO.isEmpty()) {
            msgRow = DO.getRow("AdminEnrollmentWifiConfiguration");
            msgRow.set("SSID", (Object)ssid);
            msgRow.set("PASSWORD", (Object)password);
            msgRow.set("SECURITY_TYPE", (Object)securityType);
            DO.updateRow(msgRow);
            MDMUtil.getPersistence().update(DO);
        }
        else {
            msgRow = new Row("AdminEnrollmentWifiConfiguration");
            msgRow.set("TEMPLATE_ID", (Object)new EnrollmentTemplateHandler().getTemplateTokenIdForUserId(APIUtil.getUserID(requestJSON), APIUtil.getCustomerID(requestJSON), 22));
            msgRow.set("SSID", (Object)ssid);
            msgRow.set("PASSWORD", (Object)password);
            msgRow.set("SECURITY_TYPE", (Object)securityType);
            DO.addRow(msgRow);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    public void deleteWifiConfiguration(final JSONObject requestJSON) throws Exception {
        final long userId = APIUtil.getUserID(requestJSON);
        final long templateId = new EnrollmentTemplateHandler().getTemplateTokenIdForUserId(userId, APIUtil.getCustomerID(requestJSON), 22);
        final Criteria templateIdCriteria = new Criteria(Column.getColumn("AdminEnrollmentWifiConfiguration", "TEMPLATE_ID"), (Object)templateId, 0);
        DataAccess.delete("AdminEnrollmentWifiConfiguration", templateIdCriteria);
    }
    
    static {
        AndroidQREnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
