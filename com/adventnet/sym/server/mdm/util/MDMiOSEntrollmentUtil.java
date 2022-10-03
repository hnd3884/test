package com.adventnet.sym.server.mdm.util;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.mdm.core.auth.APIKey;
import java.util.Set;
import org.apache.commons.collections.MultiMap;
import com.dd.plist.NSString;
import com.dd.plist.NSSet;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import org.apache.commons.collections.MultiHashMap;
import com.dd.plist.NSArray;
import org.apache.commons.codec.binary.Hex;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.WritableDataObject;
import javax.ws.rs.core.UriBuilder;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.dd.plist.NSObject;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.dd.plist.NSDictionary;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import org.json.simple.JSONObject;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class MDMiOSEntrollmentUtil
{
    public static Logger logger;
    public static Logger clientCertLogger;
    private static MDMiOSEntrollmentUtil mdmEntrollmentUtil;
    
    public static MDMiOSEntrollmentUtil getInstance() {
        if (MDMiOSEntrollmentUtil.mdmEntrollmentUtil == null) {
            MDMiOSEntrollmentUtil.mdmEntrollmentUtil = new MDMiOSEntrollmentUtil();
        }
        return MDMiOSEntrollmentUtil.mdmEntrollmentUtil;
    }
    
    private DataObject getManagedDeviceDO(final Long resourceID) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("ManagedDevice"));
        query.addSelectColumn(new Column("ManagedDevice", "*"));
        final Criteria criteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public boolean updateDeviceOwnedByInfo(final Long resourceID, final Integer iOwnedBy) {
        boolean isUpdated = false;
        try {
            final DataObject managedDeviceDO = this.getManagedDeviceDO(resourceID);
            Row managedDeviceRow = null;
            if (!managedDeviceDO.isEmpty()) {
                managedDeviceRow = managedDeviceDO.getFirstRow("ManagedDevice");
                managedDeviceRow.set("OWNED_BY", (Object)iOwnedBy);
                managedDeviceDO.updateRow(managedDeviceRow);
                MDMUtil.getPersistence().update(managedDeviceDO);
                isUpdated = true;
            }
        }
        catch (final Exception ex) {
            MDMiOSEntrollmentUtil.logger.log(Level.WARNING, "Exception while uploading Device Owned By Info", ex);
        }
        return isUpdated;
    }
    
    private DataObject getOTPPasswordDO(final Long resourceID) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("OTPPassword"));
        query.addSelectColumn(new Column("OTPPassword", "*"));
        final Criteria criteria = new Criteria(new Column("OTPPassword", "RESOURCE_ID"), (Object)resourceID, 0);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public String getServerBaseURL() throws Exception {
        final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        if (natProps.size() > 0) {
            final String serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            final String serverPort = "" + ((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT");
            final StringBuilder baseURLStr = new StringBuilder("https://" + serverIP + ":" + serverPort);
            return baseURLStr.toString();
        }
        final Properties serverProp = MDMUtil.getDCServerInfo();
        final String serverIP2 = ((Hashtable<K, String>)serverProp).get("SERVER_MAC_NAME");
        final String serverPort2 = "" + ((Hashtable<K, Object>)serverProp).get("SERVER_PORT");
        final StringBuilder baseURLStr2 = new StringBuilder("http://" + serverIP2 + ":" + serverPort2);
        return baseURLStr2.toString();
    }
    
    public Boolean removeDevice(final String sDeviceIDs, final String userName, final Long customerId) throws Exception {
        Boolean successfullyRemoved = false;
        if (sDeviceIDs != null) {
            try {
                final String[] sArrDeviceID = sDeviceIDs.split(",");
                this.SendRemoveDeviceCommand(sArrDeviceID, userName, customerId);
                successfullyRemoved = true;
            }
            catch (final Exception ex) {
                successfullyRemoved = false;
            }
        }
        return successfullyRemoved;
    }
    
    private void SendRemoveDeviceCommand(final String[] sArrDeviceID, final String userName, final Long customerId) throws Exception {
        MDMiOSEntrollmentUtil.logger.log(Level.FINE, "Inside SendRemoveDeviceCommand");
        for (int iDeviceResouceIDIndex = 0; iDeviceResouceIDIndex < sArrDeviceID.length; ++iDeviceResouceIDIndex) {
            final Long resourceID = Long.valueOf(sArrDeviceID[iDeviceResouceIDIndex]);
            final String sUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
            final String sDeviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
            final Integer managedDeviceStatus = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(resourceID);
            if (managedDeviceStatus == 2) {
                MDMiOSEntrollmentUtil.logger.log(Level.INFO, "Going to delete managed device: ", resourceID);
                DeviceCommandRepository.getInstance().addRemoveDeviceCommand(sUDID);
                final Criteria criteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
                MDMUtil.getPersistence().delete(criteria);
                final Criteria customGroupRelCriteria = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceID, 0);
                DataAccess.delete("CustomGroupMemberRel", customGroupRelCriteria);
                final List resourceList = new ArrayList();
                resourceList.add(resourceID);
                NotificationHandler.getInstance().SendNotification(resourceList, 1);
            }
            else {
                MDMiOSEntrollmentUtil.logger.log(Level.INFO, "Waiting for approval: Going to delete resource: ", resourceID);
                final Criteria criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceID, 0).and(new Criteria(new Column("Resource", "RESOURCE_TYPE"), (Object)new int[] { 120, 121 }, 8));
                MDMUtil.getPersistence().delete(criteria);
            }
            final String sEventLogRemarks = "dc.mdm.actionlog.enrollment.unmanaged";
            final Object remarksArgs = sDeviceName;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, resourceID, userName, sEventLogRemarks, remarksArgs, customerId);
            final JSONObject logJSON = new JSONObject();
            logJSON.put((Object)"REMARKS", (Object)"delete-success");
            logJSON.put((Object)"RESOURCE_ID", (Object)resourceID);
            logJSON.put((Object)"NAME", (Object)sDeviceName);
            logJSON.put((Object)"UDID", (Object)sUDID);
            MDMOneLineLogger.log(Level.INFO, "DEVICE_REMOVED", logJSON);
        }
    }
    
    public ArrayList getEnrolledDeviceList(final Long customerId) throws Exception {
        final ArrayList enrolledDeviceList = new ArrayList();
        final SelectQueryImpl query = new SelectQueryImpl(new Table("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        query.addSelectColumn(new Column("ManagedDevice", "*"));
        query.addSelectColumn(new Column("Resource", "*"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        query.setCriteria(customerCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        if (dataObject != null) {
            final Iterator iterator = dataObject.getRows("Resource");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final HashMap enrolledDeviceHash = new HashMap();
                enrolledDeviceHash.put("NAME", row.get("NAME"));
                enrolledDeviceHash.put("RESOURCE_ID", row.get("RESOURCE_ID"));
                enrolledDeviceList.add(enrolledDeviceHash);
            }
        }
        return enrolledDeviceList;
    }
    
    public void updateDeviceStatus(final Criteria criteria, final Integer deviceStatus) throws Exception {
        if (deviceStatus != null) {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("ManagedDevice"));
            query.addSelectColumn(new Column("ManagedDevice", "*"));
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
            if (!dataObject.isEmpty()) {
                final Iterator managedDeviceRows = dataObject.getRows("ManagedDevice");
                while (managedDeviceRows.hasNext()) {
                    final Row managedDeviceRow = managedDeviceRows.next();
                    managedDeviceRow.set("MANAGED_STATUS", (Object)deviceStatus);
                    dataObject.updateRow(managedDeviceRow);
                }
                MDMUtil.getPersistence().update(dataObject);
            }
        }
    }
    
    public DeviceMessage processEnrollmentId(final HashMap<String, String> hmap) throws Exception {
        final DeviceMessage dMsg = new DeviceMessage();
        dMsg.messageType = hmap.get("MessageType");
        final String authEnrollmentId = hmap.get("EnrollmentID");
        final int platformType = Integer.valueOf(hmap.get("PlatformType"));
        final SelectQuery enrollAuthQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IOSNativeAppAuthentication"));
        final Join deviceJoin = new Join("IOSNativeAppAuthentication", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        enrollAuthQuery.addJoin(deviceJoin);
        enrollAuthQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        enrollAuthQuery.addSelectColumn(Column.getColumn("IOSNativeAppAuthentication", "MANAGED_DEVICE_ID"));
        enrollAuthQuery.addSelectColumn(Column.getColumn("IOSNativeAppAuthentication", "AUTH_CODE"));
        enrollAuthQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        final Criteria enrollIdCri = new Criteria(Column.getColumn("IOSNativeAppAuthentication", "AUTH_CODE"), (Object)authEnrollmentId, 0);
        enrollAuthQuery.setCriteria(enrollIdCri);
        final DataObject enrollAuthDO = MDMUtil.getPersistence().get(enrollAuthQuery);
        if (!enrollAuthDO.isEmpty()) {
            final Row deviceRow = enrollAuthDO.getFirstRow("ManagedDevice");
            final String udid = (String)deviceRow.get("UDID");
            final Long deviceId = (Long)deviceRow.get("RESOURCE_ID");
            if (deviceId != null) {
                this.postRegistrationHandling(deviceId, platformType);
            }
            MDMEnrollmentUtil.getInstance().deleteEnrollmentId(deviceId);
            dMsg.status = "Acknowledged";
            final org.json.JSONObject response = new org.json.JSONObject();
            if (udid != null) {
                response.put("UDID", (Object)udid);
            }
            dMsg.messageResponse = response;
        }
        else {
            dMsg.status = "Error";
            final org.json.JSONObject response2 = new org.json.JSONObject();
            response2.put("ErrorMsg", (Object)I18N.getMsg("dc.mdm.actionlog.enrollment.invalid_enrollmentId", new Object[0]));
            response2.put("ErrorCode", 12011);
            dMsg.messageResponse = response2;
        }
        return dMsg;
    }
    
    private void postRegistrationHandling(final Long deviceId, final int platformType) {
        try {
            if (platformType == 1) {
                DeviceCommandRepository.getInstance().addDefaultAppCatalogCommand(deviceId, "DefaultRemoveAppCatalogWebClips");
                final List resourceList = new ArrayList();
                resourceList.add(deviceId);
                NotificationHandler.getInstance().SendNotification(resourceList, platformType);
            }
        }
        catch (final Exception ex) {
            MDMiOSEntrollmentUtil.logger.log(Level.WARNING, "Exception in postRegistrationHandling ", ex);
        }
    }
    
    public void addOrUpdateCertificateDetails(final HashMap certificateDetails, final Long certificateId) throws Exception {
        final Criteria certId = new Criteria(new Column("APNSCertificateDetails", "CERTIFICATE_ID"), (Object)certificateId, 0);
        final DataObject apnsCertificateDetailsDO = MDMUtil.getPersistence().get("APNSCertificateDetails", certId);
        Row apnsCertificateDetailsRow = null;
        if (apnsCertificateDetailsDO.isEmpty()) {
            apnsCertificateDetailsRow = new Row("APNSCertificateDetails");
            apnsCertificateDetailsRow.set("CERTIFICATE_ID", (Object)certificateId);
            apnsCertificateDetailsRow.set("EXPIRY_DATE", certificateDetails.get("ExpiryDate"));
            apnsCertificateDetailsRow.set("CERTIFICATE_NAME", certificateDetails.get("CertificateName"));
            apnsCertificateDetailsRow.set("ISSUER_NAME", certificateDetails.get("IssuerName"));
            apnsCertificateDetailsRow.set("ISSUER_OU_NAME", certificateDetails.get("IssuerOrganizationalUnitName"));
            apnsCertificateDetailsRow.set("ISSUER_ORG_NAME", certificateDetails.get("IssuerOrganizationName"));
            apnsCertificateDetailsRow.set("TOPIC", certificateDetails.get("Topic"));
            apnsCertificateDetailsRow.set("CREATION_DATE", certificateDetails.get("CreationDate"));
            apnsCertificateDetailsRow.set("EMAIL_ADDRESS", certificateDetails.get("EmailAddress"));
            apnsCertificateDetailsRow.set("APPLE_ID", certificateDetails.get("AppleId"));
            apnsCertificateDetailsDO.addRow(apnsCertificateDetailsRow);
            MDMUtil.getPersistence().add(apnsCertificateDetailsDO);
        }
        else {
            apnsCertificateDetailsRow = apnsCertificateDetailsDO.getRow("APNSCertificateDetails");
            if (certificateDetails.get("ExpiryDate") != null) {
                apnsCertificateDetailsRow.set("EXPIRY_DATE", certificateDetails.get("ExpiryDate"));
            }
            if (certificateDetails.get("CertificateName") != null) {
                apnsCertificateDetailsRow.set("CERTIFICATE_NAME", certificateDetails.get("CertificateName"));
            }
            if (certificateDetails.get("IssuerName") != null) {
                apnsCertificateDetailsRow.set("ISSUER_NAME", certificateDetails.get("IssuerName"));
            }
            if (certificateDetails.get("IssuerOrganizationalUnitName") != null) {
                apnsCertificateDetailsRow.set("ISSUER_OU_NAME", certificateDetails.get("IssuerOrganizationalUnitName"));
            }
            if (certificateDetails.get("IssuerOrganizationName") != null) {
                apnsCertificateDetailsRow.set("ISSUER_ORG_NAME", certificateDetails.get("IssuerOrganizationName"));
            }
            if (certificateDetails.get("Topic") != null) {
                apnsCertificateDetailsRow.set("TOPIC", certificateDetails.get("Topic"));
            }
            if (certificateDetails.get("CreationDate") != null) {
                apnsCertificateDetailsRow.set("CREATION_DATE", certificateDetails.get("CreationDate"));
            }
            if (certificateDetails.get("EmailAddress") != null) {
                apnsCertificateDetailsRow.set("EMAIL_ADDRESS", certificateDetails.get("EmailAddress"));
            }
            if (certificateDetails.get("AppleId") != null) {
                apnsCertificateDetailsRow.set("APPLE_ID", certificateDetails.get("AppleId"));
            }
            apnsCertificateDetailsDO.updateRow(apnsCertificateDetailsRow);
            MDMUtil.getPersistence().update(apnsCertificateDetailsDO);
        }
    }
    
    public void addOrUpdateIOSWebClipAppCatalogStatus(final List resourceList, final boolean isWebClipAppCatalogEnabled) {
        try {
            final Criteria deviceCri = new Criteria(Column.getColumn("IOSWebClipAppCatalog", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final DataObject dObj = MDMUtil.getPersistence().get("IOSWebClipAppCatalog", deviceCri);
            if (dObj.isEmpty()) {
                for (int j = 0; j < resourceList.size(); ++j) {
                    final Row iOSWebClipRow = new Row("IOSWebClipAppCatalog");
                    iOSWebClipRow.set("RESOURCE_ID", resourceList.get(j));
                    iOSWebClipRow.set("IS_WEBCLIP_ENABLED", (Object)isWebClipAppCatalogEnabled);
                    dObj.addRow(iOSWebClipRow);
                }
            }
            else {
                Criteria criteria = null;
                Row iOSWebClipRow = new Row("IOSWebClipAppCatalog");
                for (int i = 0; i < resourceList.size(); ++i) {
                    criteria = new Criteria(Column.getColumn("IOSWebClipAppCatalog", "RESOURCE_ID"), resourceList.get(i), 0);
                    iOSWebClipRow = dObj.getRow("IOSWebClipAppCatalog", criteria);
                    if (iOSWebClipRow == null) {
                        iOSWebClipRow = new Row("IOSWebClipAppCatalog");
                        iOSWebClipRow.set("RESOURCE_ID", resourceList.get(i));
                        iOSWebClipRow.set("IS_WEBCLIP_ENABLED", (Object)isWebClipAppCatalogEnabled);
                        dObj.addRow(iOSWebClipRow);
                    }
                    else {
                        iOSWebClipRow.set("IS_WEBCLIP_ENABLED", (Object)isWebClipAppCatalogEnabled);
                        dObj.updateRow(iOSWebClipRow);
                    }
                }
            }
            MDMUtil.getPersistence().update(dObj);
        }
        catch (final Exception ex) {
            MDMiOSEntrollmentUtil.logger.log(Level.WARNING, " Exception in addOrUpdateIOSWebClipAppCatalogStatus {0} ", ex);
        }
    }
    
    public boolean isIOSWebClipAppCatalogInstalled(final Long deviceId) {
        boolean isWebClipAppCatalogInstalled = false;
        try {
            final Criteria deviceCri = new Criteria(Column.getColumn("IOSWebClipAppCatalog", "RESOURCE_ID"), (Object)deviceId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("IOSWebClipAppCatalog", deviceCri);
            if (!dObj.isEmpty()) {
                final Object isWebClipAppCatalogInstalledObj = dObj.getValue("IOSWebClipAppCatalog", "IS_WEBCLIP_ENABLED", deviceCri);
                if (isWebClipAppCatalogInstalledObj != null) {
                    isWebClipAppCatalogInstalled = (boolean)isWebClipAppCatalogInstalledObj;
                }
            }
        }
        catch (final Exception ex) {
            MDMiOSEntrollmentUtil.logger.log(Level.WARNING, " Exception in isIOSNativeAgentInstalled {0}", ex);
        }
        return isWebClipAppCatalogInstalled;
    }
    
    public static NSDictionary getMDMDefaultAppConfiguration() {
        final NSDictionary configurationDict = new NSDictionary();
        configurationDict.put("ServerName", (Object)"%ServerName%");
        configurationDict.put("ServerPort", (Object)"%ServerPort%");
        configurationDict.put("UDID", (Object)"%udid%");
        configurationDict.put("ErID", (Object)"%erid%");
        configurationDict.put("IsLanguagePackEnabled", (Object)"%license_pack_enabled%");
        configurationDict.put("authtoken", (Object)"%device_authtoken%");
        configurationDict.put("SCOPE", (Object)"%device_SCOPE%");
        configurationDict.put("Services", (Object)"%services%");
        configurationDict.put("IsSyncServerEnabled", (Object)true);
        configurationDict.put("IsAnnouncementEnabled", (Object)true);
        final boolean documentPolicyAllowClipboardCopy = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("iOSDocumentAllowClipboardCopy");
        final boolean documentPolicyAllowDocumentShare = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("iOSDocumentAllowDocumentShare");
        final boolean webKioskPermission = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("iOSWebKioskUserPermission");
        if (documentPolicyAllowClipboardCopy || documentPolicyAllowDocumentShare) {
            MDMiOSEntrollmentUtil.logger.log(Level.INFO, "Feature Enabled allowDocumentShare{0}allowClipboardCopy:{1}", new Object[] { documentPolicyAllowDocumentShare, documentPolicyAllowClipboardCopy });
            final NSDictionary documentViewerPolicy = new NSDictionary();
            if (documentPolicyAllowDocumentShare) {
                documentViewerPolicy.put("allowDocumentShare", (Object)true);
            }
            if (documentPolicyAllowClipboardCopy) {
                documentViewerPolicy.put("allowClipboardCopy", (Object)"YES");
            }
            configurationDict.put("DocumentViewerPolicy", (NSObject)documentViewerPolicy);
        }
        if (webKioskPermission) {
            configurationDict.put("needUserPermissionsForWebShortcutKiosk", (Object)true);
        }
        return configurationDict;
    }
    
    public static String getPlatformTypeString(final int platfromType) throws Exception {
        switch (platfromType) {
            case 1: {
                return "iOS";
            }
            case 2: {
                return "android";
            }
            case 3: {
                return "windows";
            }
            default: {
                throw new Exception("Unidentified platform type for OS platform");
            }
        }
    }
    
    public DeviceMessage updateAppNotificationToken(final Long resourceId, final HashMap messageMap) {
        try {
            final org.json.JSONObject hAndroidCommMap = new org.json.JSONObject();
            final String hMap = messageMap.get("MsgRequest");
            final org.json.JSONObject message = new org.json.JSONObject(hMap);
            if (message.has("fcmToken")) {
                hAndroidCommMap.put("NOTIFICATION_TOKEN_ENCRYPTED", message.get("fcmToken"));
                PushNotificationHandler.getInstance().addOrUpdateManagedIdToNotificationRel(resourceId, 101, hAndroidCommMap);
            }
            else {
                MDMiOSEntrollmentUtil.logger.log(Level.INFO, "FCM token error");
            }
        }
        catch (final Exception e) {
            MDMiOSEntrollmentUtil.logger.log(Level.SEVERE, "Exception in update app notification token", e);
        }
        final DeviceMessage dMsg = new DeviceMessage();
        dMsg.messageType = messageMap.get("MsgRequestType");
        dMsg.status = "Acknowledged";
        final org.json.JSONObject response = new org.json.JSONObject();
        dMsg.messageResponse = response;
        return dMsg;
    }
    
    private SelectQuery getQueryForEridToCustomeridToMdDevice(final Criteria criteria) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        query.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        query.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"));
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return query;
    }
    
    public boolean validateLegacyServletRequest(final org.json.JSONObject json) {
        Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.INFO, "MDMIOSEnrollmentUtil Validate Legacy Servlet Request.. {0}", new Object[] { json.toString() });
        boolean allowRequest = false;
        try {
            final String udid = json.get("UDID").toString();
            final Long erid = (Long)json.get("ENROLLMENT_REQUEST_ID");
            final Long customerId = (Long)json.get("CUSTOMER_ID");
            final Criteria customerIdCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria eridCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
            final SelectQuery query = this.getQueryForEridToCustomeridToMdDevice(customerIdCri.and(eridCri));
            final DataObject DO = MDMUtil.getPersistence().get(query);
            if (!DO.isEmpty()) {
                final Boolean devicesPresent = DO.getRows("ManagedDevice", (Criteria)null).hasNext();
                final Boolean thisDevicePresent = DO.getRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0, (boolean)Boolean.FALSE)).hasNext();
                if (devicesPresent) {
                    if (!thisDevicePresent) {
                        Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.SEVERE, "ERID for given customerId: {0} and given udid : {1} doesnt match", new Object[] { customerId, udid });
                        allowRequest = false;
                    }
                    else {
                        allowRequest = true;
                    }
                }
                else {
                    allowRequest = true;
                }
                Logger.getLogger("LoggingPurposeLogger").log(Level.INFO, "Legacy Servlet Request.. UDID : {0} ERID : {1}", new Object[] { udid, erid });
            }
            else {
                Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.SEVERE, "No ERID Found for given token");
                allowRequest = false;
            }
        }
        catch (final Exception e) {
            Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.SEVERE, "Exception while Validating the iOS Legacy Servlet Request.. ", e);
        }
        return allowRequest;
    }
    
    public String getReenrollUrl(final Long erid) {
        String refetchUrl = null;
        try {
            final String udid = ManagedDeviceHandler.getInstance().getUDIDFromEnrollmentRequestID(erid);
            final org.json.JSONObject json = new org.json.JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)erid);
            final String encapiKey = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json).getKeyValue();
            final UriBuilder builder = UriBuilder.fromUri(MDMEnrollmentUtil.getInstance().getServerBaseURL());
            builder.path("/mdm/client/v1/refetchconfig");
            builder.queryParam("erid", new Object[] { erid });
            builder.queryParam("encapiKey", new Object[] { encapiKey });
            builder.queryParam("udid", new Object[] { udid });
            refetchUrl = builder.build(new Object[0]).toURL().toString();
        }
        catch (final Exception e) {
            MDMiOSEntrollmentUtil.logger.log(Level.SEVERE, "Exception in get Reenroll Url.. {0}", e);
        }
        return refetchUrl;
    }
    
    public void addReenrollReq(final Long erid) {
        try {
            final Row refetchConfigRow = new Row("DeviceReenrollStatus");
            refetchConfigRow.set("ENROLLMENT_REQUEST_ID", (Object)erid);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(refetchConfigRow);
            MDMUtil.getPersistenceLite().update(dataObject);
        }
        catch (final Exception e) {
            MDMiOSEntrollmentUtil.logger.log(Level.SEVERE, "Exception in add Reenroll Request.. {0}", e);
        }
    }
    
    public boolean getReenrollReq(final Long erid) {
        boolean refetch = false;
        try {
            final Object temp = DBUtil.getValueFromDB("DeviceReenrollStatus", "ENROLLMENT_REQUEST_ID", (Object)erid, "ENROLLMENT_REQUEST_ID");
            if (temp != null) {
                refetch = true;
            }
        }
        catch (final Exception e) {
            MDMiOSEntrollmentUtil.logger.log(Level.SEVERE, "Exception in get Reenroll Request.. {0}", e);
        }
        return refetch;
    }
    
    public void removeReenrollReq(final Long erid) {
        try {
            final DeleteQuery refetchConfigSq = (DeleteQuery)new DeleteQueryImpl("DeviceReenrollStatus");
            refetchConfigSq.setCriteria(new Criteria(new Column("DeviceReenrollStatus", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
            MDMUtil.getPersistenceLite().delete(refetchConfigSq);
        }
        catch (final Exception e) {
            MDMiOSEntrollmentUtil.logger.log(Level.SEVERE, "Exception in delete Reenroll Request.. {0}", e);
        }
    }
    
    public static String getMdmScepServerUrl(final String serverBaseUrl, final Long customerID, final Long enrollmentRequestID, final String encapiKey) throws Exception {
        final String urlPath = "erid=" + enrollmentRequestID + "&encapiKey=" + encapiKey + "&customerId=" + customerID;
        final UriBuilder scepUri = UriBuilder.fromUri(serverBaseUrl);
        scepUri.path("/mdm/enrollment/identitycertificate/scepredirect/" + Hex.encodeHexString(urlPath.getBytes()));
        return scepUri.build(new Object[0]).toURL().toString();
    }
    
    public static NSArray getScepSubject(final Long enrollmentRequestID) {
        MDMiOSEntrollmentUtil.clientCertLogger.log(Level.INFO, "MDMiOSEntrollmentUtil: DATA-IN: Getting scep subject for : {0}", new Object[] { enrollmentRequestID });
        final String commonName = getInstance().getIosMdmIdentity(enrollmentRequestID);
        final NSArray subjectNSArray = getSubjectNSArrayForIosEnrollment("CN=" + commonName);
        return subjectNSArray;
    }
    
    private static NSArray getSubjectNSArrayForIosEnrollment(final String subject) {
        MDMiOSEntrollmentUtil.clientCertLogger.log(Level.INFO, "MDMiOSEnrollmentUtil: DATA-IN: Subject: {0}", new Object[] { subject });
        final MultiMap map = (MultiMap)new MultiHashMap();
        try {
            if (subject != null && !subject.isEmpty()) {
                final LdapName subjectValue = new LdapName(subject);
                final List<Rdn> rdnList = subjectValue.getRdns();
                for (final Rdn rdn : rdnList) {
                    map.put((Object)rdn.getType(), (Object)rdn.getValue().toString());
                }
            }
        }
        catch (final Exception e) {
            MDMiOSEntrollmentUtil.logger.log(Level.SEVERE, e, () -> "MDMiOSEnrollmentUtil: Exception while constructing subject name: " + s);
        }
        final NSArray subjectOIDArray = new NSArray(map.keySet().size());
        final Set<String> rdnKeys = map.keySet();
        int i = 0;
        for (String key : rdnKeys) {
            final List<String> oidList = (List<String>)map.get((Object)key);
            final NSArray OIDPair = new NSArray(oidList.size());
            int j = 0;
            for (final String oid : oidList) {
                final NSSet OIDSet = new NSSet();
                key = (key.equalsIgnoreCase("E") ? "1.2.840.113549.1.9.1" : key);
                key = (key.equalsIgnoreCase("DC") ? "0.9.2342.19200300.100.1.25" : key);
                OIDSet.addObject((NSObject)new NSString(key));
                OIDSet.addObject((NSObject)new NSString(oid));
                OIDPair.setValue(j, (Object)OIDSet);
                ++j;
            }
            subjectOIDArray.setValue(i, (Object)OIDPair);
            ++i;
        }
        MDMiOSEntrollmentUtil.clientCertLogger.log(Level.INFO, "MDMiOSEnrollmentUtil: Subject NSArray construction successfull");
        return subjectOIDArray;
    }
    
    public boolean isApnsConfigFeatureEnabled() {
        final boolean isApnsConfigFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MobileConfigWithApns");
        MDMiOSEntrollmentUtil.clientCertLogger.log(Level.INFO, "MDMiOSEnrollmentUtil: Is apns config feature enabled: {0}", new Object[] { isApnsConfigFeatureEnabled });
        return isApnsConfigFeatureEnabled;
    }
    
    public String getIosMdmIdentity(final Long enrollmentRequestId) {
        final String iosMdmIdentity = "MDM-IDENTITY:" + enrollmentRequestId;
        MDMiOSEntrollmentUtil.clientCertLogger.log(Level.INFO, "MDMiOSEnrollmentUtil: Constructed successfully. IOS MDM identity: {0}", new Object[] { iosMdmIdentity });
        return iosMdmIdentity;
    }
    
    public APIKey checkAndGenerateApiKeyForEnrollmentRequest(final long enrollmentRequestId) {
        MDMiOSEntrollmentUtil.clientCertLogger.log(Level.INFO, "MDMiOSEnrollmentUtil-checkAndGenerateApiKeyForEnrollmentRequest: Checking if api key is present for {0}", new Object[] { enrollmentRequestId });
        final org.json.JSONObject json = new org.json.JSONObject();
        json.put("ENROLLMENT_REQUEST_ID", enrollmentRequestId);
        APIKey apiKey = MDMDeviceAPIKeyGenerator.getInstance().getAPIKey(json);
        if (apiKey == null) {
            MDMiOSEntrollmentUtil.clientCertLogger.log(Level.INFO, "MDMiOSEnrollmentUtil-checkAndGenerateApiKeyForEnrollmentRequest: Api key is not present for {0}, so generating...", new Object[] { enrollmentRequestId });
            apiKey = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
        }
        MDMiOSEntrollmentUtil.clientCertLogger.log(Level.FINE, "MDMiOSEnrollmentUtil-checkAndGenerateApiKeyForEnrollmentRequest: Api key: {0}, Api key version: {1}", new Object[] { apiKey.getKeyValue(), apiKey.getVersion() });
        return apiKey;
    }
    
    public boolean isCACertCreatedAlready(final long customerId) throws Exception {
        final String caCertCreatedStr = CustomerParamsHandler.getInstance().getParameterValue("CA_CERT_CREATED", customerId);
        final boolean isCaCertCreated = caCertCreatedStr != null && Boolean.parseBoolean(caCertCreatedStr);
        return isCaCertCreated;
    }
    
    public Long getCustomerIdForErid(final long erid) throws DataAccessException {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean isSkipCustomerFlagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                isSkipCustomerFlagSet = true;
            }
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            sq.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            sq.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sq.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            sq.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            sq.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            sq.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            final Criteria eridCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
            sq.setCriteria(eridCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("Resource");
                final long customerId = (long)row.get("CUSTOMER_ID");
                MDMiOSEntrollmentUtil.logger.log(Level.FINE, "Customer id found for the given erid {0} is {1}", new Object[] { erid, customerId });
                return customerId;
            }
        }
        catch (final Throwable e) {
            final String eMessage = "Exception while obtaining customer id for the given erid: " + erid;
            MDMiOSEntrollmentUtil.logger.log(Level.SEVERE, eMessage, e);
        }
        finally {
            if (isSkipCustomerFlagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return null;
    }
    
    public long optCustomerIdForErid(final long erid, final long optionalCustomerId) throws DataAccessException {
        try {
            final Long customerId = this.getCustomerIdForErid(erid);
            return (customerId != null) ? customerId : optionalCustomerId;
        }
        catch (final Exception e) {
            MDMiOSEntrollmentUtil.logger.log(Level.WARNING, "No customer id found for the given erid, so returning the optional provided. Erid - {0}, Optional Customer id provided: {1}", new Object[] { erid, optionalCustomerId });
            return optionalCustomerId;
        }
    }
    
    static {
        MDMiOSEntrollmentUtil.logger = Logger.getLogger("MDMEnrollment");
        MDMiOSEntrollmentUtil.clientCertLogger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        MDMiOSEntrollmentUtil.mdmEntrollmentUtil = null;
    }
}
