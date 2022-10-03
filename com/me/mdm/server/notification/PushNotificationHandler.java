package com.me.mdm.server.notification;

import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.List;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushNotificationHandler
{
    private final String sourceClass = "PushNotificationHandler";
    private static Logger logger;
    private static PushNotificationHandler pushNotificationHandler;
    
    public static PushNotificationHandler getInstance() {
        if (PushNotificationHandler.pushNotificationHandler == null) {
            PushNotificationHandler.pushNotificationHandler = new PushNotificationHandler();
        }
        return PushNotificationHandler.pushNotificationHandler;
    }
    
    public boolean isChannelUriObtained(final Long resourceID) {
        Boolean retVal = Boolean.FALSE;
        try {
            final JSONObject notificationDetails = this.getNotificationDetails(resourceID, 3);
            if (notificationDetails != null) {
                retVal = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            PushNotificationHandler.logger.log(Level.SEVERE, "Error in isChannelUriObtained()", ex);
        }
        return retVal;
    }
    
    public JSONObject getNotificationDetails(final Long managedDeviceId, final Integer notificationType) throws DataAccessException, JSONException {
        JSONObject notificationDetailJSON = null;
        final Long notificationDetailId = this.getNotificationDetailId(managedDeviceId, notificationType);
        if (notificationDetailId != null) {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("NotificationDetails"));
            final Join iosExtnJoin = new Join("NotificationDetails", "IosNotificationDetailsExtn", new String[] { "NOTIFICATION_DETAILS_ID" }, new String[] { "NOTIFICATION_DETAILS_ID" }, 1);
            sQuery.setCriteria(new Criteria(new Column("NotificationDetails", "NOTIFICATION_DETAILS_ID"), (Object)notificationDetailId, 0));
            sQuery.addJoin(iosExtnJoin);
            sQuery.addSelectColumn(new Column("NotificationDetails", "*"));
            sQuery.addSelectColumn(new Column("IosNotificationDetailsExtn", "*"));
            final DataObject notificationDetailDO = MDMUtil.getPersistence().get(sQuery);
            if (!notificationDetailDO.isEmpty()) {
                notificationDetailJSON = new JSONObject();
                final Row notificationDetailRow = notificationDetailDO.getFirstRow("NotificationDetails");
                notificationDetailJSON.put("NOTIFICATION_DETAILS_ID", (Object)notificationDetailId);
                notificationDetailJSON.put("NOTIFICATION_TOKEN_ENCRYPTED", notificationDetailRow.get("NOTIFICATION_TOKEN_ENCRYPTED"));
                if (notificationDetailDO.containsTable("IosNotificationDetailsExtn")) {
                    final Row iosNotificationDetailRow = notificationDetailDO.getFirstRow("IosNotificationDetailsExtn");
                    notificationDetailJSON.put("TOPIC", iosNotificationDetailRow.get("TOPIC"));
                    notificationDetailJSON.put("PUSH_MAGIC_ENCRYPTED", iosNotificationDetailRow.get("PUSH_MAGIC_ENCRYPTED"));
                    notificationDetailJSON.put("UNLOCK_TOKEN_ENCRYPTED", iosNotificationDetailRow.get("UNLOCK_TOKEN_ENCRYPTED"));
                }
            }
        }
        return notificationDetailJSON;
    }
    
    public JSONObject notifyAppleDeviceWithUdid(final List<String> udidList) throws Exception {
        PushNotificationHandler.logger.log(Level.INFO, "Inside notifyAppleDeviceWithUdid..");
        JSONObject notifyRespFromTempJO = null;
        try {
            final Criteria cri = new Criteria(new Column("IOSEnrollmentTemp", "UDID"), (Object)udidList.toArray(), 8);
            notifyRespFromTempJO = MDMEnrollmentUtil.getInstance().rewakeIosDevices(cri);
            final List<Long> resIdList = new ArrayList<Long>();
            for (final String eachUdid : udidList) {
                final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(eachUdid);
                if (resourceId != null) {
                    resIdList.add(resourceId);
                }
            }
            if (!resIdList.isEmpty()) {
                NotificationHandler.getInstance().SendNotification(resIdList);
            }
        }
        catch (final Exception ex) {
            PushNotificationHandler.logger.log(Level.SEVERE, "Exception in notifyAppleDeviceWithUdid..", ex);
            throw ex;
        }
        return notifyRespFromTempJO;
    }
    
    private Long getNotificationDetailId(final Long managedDeviceId, final Integer notificationType) throws DataAccessException {
        final Criteria managedDeviceUserCriteria = new Criteria(new Column("ManagedDeviceNotification", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0);
        final Criteria notificationTypeCriteria = new Criteria(new Column("ManagedDeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0);
        final DataObject MANAGEDDEVICENOTIFICATIONDO = MDMUtil.getPersistence().get("ManagedDeviceNotification", managedDeviceUserCriteria.and(notificationTypeCriteria));
        if (MANAGEDDEVICENOTIFICATIONDO.isEmpty()) {
            return null;
        }
        return (Long)MANAGEDDEVICENOTIFICATIONDO.getFirstValue("ManagedDeviceNotification", "NOTIFICATION_DETAILS_ID");
    }
    
    public void addOrUpdateManagedIdToNotificationRel(final Long managedDeviceId, final int notificationType, final JSONObject notificationData) throws SyMException, DataAccessException, JSONException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDeviceNotification"));
        final Criteria managedDeviceUserCriteria = new Criteria(new Column("ManagedDeviceNotification", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0);
        final Criteria notificationTypeCriteria = new Criteria(new Column("ManagedDeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(notificationTypeCriteria.and(managedDeviceUserCriteria));
        final DataObject notificationDO = MDMUtil.getPersistence().get(sQuery);
        if (notificationDO.isEmpty()) {
            final Long notificationDetailsId = this.addOrUpdateNotificationDetails(notificationData);
            final Row managedNotificationRow = new Row("ManagedDeviceNotification");
            managedNotificationRow.set("NOTIFICATION_DETAILS_ID", (Object)notificationDetailsId);
            managedNotificationRow.set("MANAGED_DEVICE_ID", (Object)managedDeviceId);
            managedNotificationRow.set("NOTIFICATION_TYPE", (Object)notificationType);
            notificationDO.addRow(managedNotificationRow);
            MDMUtil.getPersistence().update(notificationDO);
        }
        else {
            final Row managedNotificationRow = notificationDO.getFirstRow("ManagedDeviceNotification");
            final Long notificationDetailsId = (Long)managedNotificationRow.get("NOTIFICATION_DETAILS_ID");
            notificationData.put("NOTIFICATION_DETAILS_ID", (Object)notificationDetailsId);
            this.addOrUpdateNotificationDetails(notificationData);
        }
    }
    
    private Long addOrUpdateNotificationDetails(final JSONObject notificationData) throws DataAccessException, JSONException {
        final String notificationToken = (String)notificationData.get("NOTIFICATION_TOKEN_ENCRYPTED");
        Long notificationDetailsId = notificationData.optLong("NOTIFICATION_DETAILS_ID", -1L);
        if (notificationDetailsId == -1L) {
            DataObject notificationDO = MDMUtil.getPersistence().constructDataObject();
            Row notificationDetailRow = new Row("NotificationDetails");
            if (!MDMUtil.isStringEmpty(notificationToken)) {
                notificationDetailRow.set("NOTIFICATION_TOKEN_ENCRYPTED", (Object)notificationToken);
            }
            notificationDO.addRow(notificationDetailRow);
            notificationDO = MDMUtil.getPersistence().add(notificationDO);
            notificationDetailRow = notificationDO.getFirstRow("NotificationDetails");
            notificationDetailsId = (Long)notificationDetailRow.get("NOTIFICATION_DETAILS_ID");
        }
        else {
            final Criteria notificationDetailCriteria = new Criteria(new Column("NotificationDetails", "NOTIFICATION_DETAILS_ID"), (Object)notificationDetailsId, 0);
            final DataObject notificationDO = MDMUtil.getPersistence().get("NotificationDetails", notificationDetailCriteria);
            final Row notificationDetailRow = notificationDO.getFirstRow("NotificationDetails");
            if (!MDMUtil.isStringEmpty(notificationToken)) {
                notificationDetailRow.set("NOTIFICATION_TOKEN_ENCRYPTED", (Object)notificationToken);
            }
            notificationDO.updateRow(notificationDetailRow);
            MDMUtil.getPersistence().update(notificationDO);
        }
        if (MDMUtil.isStringEmpty(notificationToken)) {
            PushNotificationHandler.logger.log(Level.WARNING, "NotificationToken is empty for NotificationId: {0} {1}", new Object[] { notificationDetailsId, notificationToken });
        }
        if (notificationData.has("PUSH_MAGIC_ENCRYPTED") || notificationData.has("UNLOCK_TOKEN")) {
            this.addOrUpdateIosNotificationDetailsExtn(notificationDetailsId, notificationData);
        }
        return notificationDetailsId;
    }
    
    private void addOrUpdateIosNotificationDetailsExtn(final Long notificationDetailsId, final JSONObject notificationData) throws DataAccessException, JSONException {
        final String pushMagic = (String)notificationData.get("PUSH_MAGIC_ENCRYPTED");
        String unlockToken = null;
        if (notificationData.has("UNLOCK_TOKEN_ENCRYPTED")) {
            unlockToken = (String)notificationData.get("UNLOCK_TOKEN_ENCRYPTED");
        }
        final String topic = (String)notificationData.get("TOPIC");
        final Criteria notificationDetailCriteria = new Criteria(new Column("IosNotificationDetailsExtn", "NOTIFICATION_DETAILS_ID"), (Object)notificationDetailsId, 0);
        final DataObject notificationDO = MDMUtil.getPersistence().get("IosNotificationDetailsExtn", notificationDetailCriteria);
        if (notificationDO.isEmpty()) {
            final Row notificationDetailRow = new Row("IosNotificationDetailsExtn");
            notificationDetailRow.set("NOTIFICATION_DETAILS_ID", (Object)notificationDetailsId);
            if (!MDMUtil.isStringEmpty(pushMagic)) {
                notificationDetailRow.set("PUSH_MAGIC_ENCRYPTED", (Object)pushMagic);
            }
            else {
                PushNotificationHandler.logger.log(Level.WARNING, "New PushMagic is empty for NotificationId: {0} {1}", new Object[] { notificationDetailsId, pushMagic });
            }
            if (!MDMUtil.isStringEmpty(unlockToken)) {
                notificationDetailRow.set("UNLOCK_TOKEN_ENCRYPTED", (Object)unlockToken);
            }
            else {
                PushNotificationHandler.logger.log(Level.FINE, "UnlockToken is empty for NotificationId: {0} {1}", new Object[] { notificationDetailsId, unlockToken });
            }
            notificationDetailRow.set("TOPIC", (Object)topic);
            notificationDO.addRow(notificationDetailRow);
        }
        else {
            final Row notificationDetailRow = notificationDO.getFirstRow("IosNotificationDetailsExtn");
            if (!MDMUtil.isStringEmpty(pushMagic)) {
                notificationDetailRow.set("PUSH_MAGIC_ENCRYPTED", (Object)pushMagic);
            }
            else {
                PushNotificationHandler.logger.log(Level.WARNING, "Update PushMagic is empty for NotificationId: {0} {1}", new Object[] { notificationDetailsId, pushMagic });
            }
            if (!MDMUtil.isStringEmpty(unlockToken)) {
                notificationDetailRow.set("UNLOCK_TOKEN_ENCRYPTED", (Object)unlockToken);
                final String oldUT = (String)((notificationDetailRow.get("UNLOCK_TOKEN") == null) ? "null" : notificationDetailRow.get("UNLOCK_TOKEN"));
                PushNotificationHandler.logger.log(Level.INFO, "UnlockToken is updated: NotificationDetailsID: {0}, IsValueChanged: {1}, At: {2}", new Object[] { notificationDetailsId, !oldUT.equals(unlockToken), System.currentTimeMillis() });
            }
            else {
                PushNotificationHandler.logger.log(Level.FINE, "UnlockToken is empty for NotificationId: {0} {1}", new Object[] { notificationDetailsId, unlockToken });
            }
            notificationDetailRow.set("TOPIC", (Object)topic);
            notificationDO.updateRow(notificationDetailRow);
        }
        MDMUtil.getPersistence().update(notificationDO);
    }
    
    public HashMap<Integer, HashMap<String, String>> getWindowsWakeUpCredentials() {
        try {
            final HashMap<Integer, HashMap<String, String>> wakeupCredentials = new HashMap<Integer, HashMap<String, String>>();
            final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("WPNSInfo"));
            sql.addSelectColumn(new Column("WPNSInfo", "*"));
            final DataObject dao = MDMUtil.getPersistence().get(sql);
            final Iterator iter = dao.getRows("WPNSInfo");
            while (iter.hasNext()) {
                final Row row = iter.next();
                final HashMap<String, String> hashMap;
                final HashMap<String, String> wakeUpData = hashMap = new HashMap<String, String>();
                final String s = "PACKAGE_SID";
                MDMUtil.getInstance();
                hashMap.put(s, MDMUtil.decrypt(row.get("PACKAGE_SID").toString()));
                final HashMap<String, String> hashMap2 = wakeUpData;
                final String s2 = "CLIENT_SECRET";
                MDMUtil.getInstance();
                hashMap2.put(s2, MDMUtil.decrypt(row.get("CLIENT_SECRET").toString()));
                final HashMap<String, String> hashMap3 = wakeUpData;
                final String s3 = "PFN";
                MDMUtil.getInstance();
                hashMap3.put(s3, MDMUtil.decrypt(row.get("PFN").toString()));
                wakeupCredentials.put((Integer)row.get("APP_TYPE"), wakeUpData);
            }
            return wakeupCredentials;
        }
        catch (final DataAccessException ex) {
            PushNotificationHandler.logger.log(Level.SEVERE, "Exception in MDMUtil.getWindowsWakeUpCredentials {0}", (Throwable)ex);
            return null;
        }
    }
    
    public String getWindowsPFN() {
        String pfn = null;
        try {
            final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("WPNSInfo"));
            sql.addSelectColumn(new Column("WPNSInfo", "*"));
            final Criteria cri = new Criteria(new Column("WPNSInfo", "APP_TYPE"), (Object)1, 0, false);
            sql.setCriteria(cri);
            MDMUtil.getInstance();
            pfn = MDMUtil.decrypt(MDMUtil.getPersistence().get(sql).getFirstRow("WPNSInfo").get("PFN").toString());
        }
        catch (final DataAccessException ex) {
            PushNotificationHandler.logger.log(Level.SEVERE, "Exception in MDMUtil.getWindowsPFN", (Throwable)ex);
        }
        return pfn;
    }
    
    public void deleteiOSEnrollmentTempData(final String sUDID) throws Exception {
        final Criteria criteria = new Criteria(new Column("IOSEnrollmentTemp", "UDID"), (Object)sUDID, 0, false);
        MDMUtil.getPersistence().delete(criteria);
    }
    
    private DataObject getiOSEnrollmentTempDO(final String sUDID) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("IOSEnrollmentTemp"));
        query.addSelectColumn(new Column("IOSEnrollmentTemp", "*"));
        final Criteria criteria = new Criteria(new Column("IOSEnrollmentTemp", "UDID"), (Object)sUDID, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public JSONObject getiOSEnrollmentTempData(final String sUDID) throws Exception {
        JSONObject hsIOSDeviceInfo = null;
        final DataObject dataObject = this.getiOSEnrollmentTempDO(sUDID);
        if (!dataObject.isEmpty()) {
            hsIOSDeviceInfo = new JSONObject();
            final Row iOSEnrollmentTempRow = dataObject.getFirstRow("IOSEnrollmentTemp");
            hsIOSDeviceInfo.put("TOPIC", iOSEnrollmentTempRow.get("TOPIC"));
            hsIOSDeviceInfo.put("DEVICE_TOKEN", iOSEnrollmentTempRow.get("DEVICE_TOKEN_ENCRYPTED"));
            hsIOSDeviceInfo.put("PUSH_MAGIC", iOSEnrollmentTempRow.get("PUSH_MAGIC"));
            hsIOSDeviceInfo.put("UNLOCK_TOKEN", iOSEnrollmentTempRow.get("UNLOCK_TOKEN"));
            hsIOSDeviceInfo.put("ENROLLMENT_ID", iOSEnrollmentTempRow.get("ENROLLMENT_ID"));
        }
        return hsIOSDeviceInfo;
    }
    
    private void addOrUpdateiOSEnrollmentTempData(final String sUDID, final String sTopic, final String sDeviceToken, final String sPushMagic, final String sUnlockToken) throws Exception {
        final DataObject dataObject = this.getiOSEnrollmentTempDO(sUDID);
        Row iOSEnrollmentTempRow = null;
        if (dataObject.isEmpty()) {
            PushNotificationHandler.logger.log(Level.INFO, "Going to ADD UDID = {0} in IOSEnrollmentTemp", sUDID);
            iOSEnrollmentTempRow = new Row("IOSEnrollmentTemp");
            iOSEnrollmentTempRow.set("UDID", (Object)sUDID);
            iOSEnrollmentTempRow.set("TOPIC", (Object)sTopic);
            if (sDeviceToken != null && !sDeviceToken.isEmpty()) {
                iOSEnrollmentTempRow.set("DEVICE_TOKEN_ENCRYPTED", (Object)sDeviceToken);
            }
            if (sPushMagic != null && !sPushMagic.isEmpty()) {
                iOSEnrollmentTempRow.set("PUSH_MAGIC", (Object)sPushMagic);
            }
            if (sUnlockToken != null && !sUnlockToken.isEmpty()) {
                iOSEnrollmentTempRow.set("UNLOCK_TOKEN", (Object)sUnlockToken);
            }
            dataObject.addRow(iOSEnrollmentTempRow);
            MDMUtil.getPersistence().add(dataObject);
        }
        else {
            PushNotificationHandler.logger.log(Level.INFO, "Going to UPDATE UDID = {0} in IOSEnrollmentTemp", sUDID);
            iOSEnrollmentTempRow = dataObject.getFirstRow("IOSEnrollmentTemp");
            iOSEnrollmentTempRow.set("TOPIC", (Object)sTopic);
            if (sDeviceToken != null && !sDeviceToken.isEmpty()) {
                iOSEnrollmentTempRow.set("DEVICE_TOKEN_ENCRYPTED", (Object)sDeviceToken);
            }
            if (sPushMagic != null && !sPushMagic.isEmpty()) {
                iOSEnrollmentTempRow.set("PUSH_MAGIC", (Object)sPushMagic);
            }
            if (sUnlockToken != null && !sUnlockToken.isEmpty()) {
                iOSEnrollmentTempRow.set("UNLOCK_TOKEN", (Object)sUnlockToken);
            }
            dataObject.updateRow(iOSEnrollmentTempRow);
            MDMUtil.getPersistence().update(dataObject);
        }
    }
    
    public void addOrUpdateiOSEnrollmentTempData(final HashMap hsEntrollValues) throws Exception {
        final String sUDID = hsEntrollValues.get("UDID");
        final String sTopic = hsEntrollValues.get("Topic");
        final String sDeviceToken = hsEntrollValues.get("DeviceToken");
        final String sPushMagic = hsEntrollValues.get("PushMagic");
        final String sUnlockToken = hsEntrollValues.get("UnlockToken");
        if (sUDID != null && sTopic != null) {
            this.addOrUpdateiOSEnrollmentTempData(sUDID, sTopic, sDeviceToken, sPushMagic, sUnlockToken);
        }
        else {
            PushNotificationHandler.logger.log(Level.WARNING, "UDID and Topic can not be null during Authentication Process");
        }
    }
    
    public void updateiOSEnrollmentTempData(final HashMap hsEntrollValues) throws Exception {
        final String sUDID = hsEntrollValues.get("UDID");
        final String sTopic = hsEntrollValues.get("Topic");
        final String sDeviceToken = hsEntrollValues.get("DeviceToken");
        final String sPushMagic = hsEntrollValues.get("PushMagic");
        final String sUnlockToken = hsEntrollValues.get("UnlockToken");
        if (sUDID != null && sTopic != null) {
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("IOSEnrollmentTemp");
            query.setUpdateColumn("TOPIC", (Object)sTopic);
            if (sDeviceToken != null) {
                query.setUpdateColumn("DEVICE_TOKEN_ENCRYPTED", (Object)sDeviceToken);
            }
            if (sPushMagic != null) {
                query.setUpdateColumn("PUSH_MAGIC", (Object)sPushMagic);
            }
            if (sUnlockToken != null) {
                query.setUpdateColumn("UNLOCK_TOKEN", (Object)sUnlockToken);
            }
            final Criteria criteria = new Criteria(new Column("IOSEnrollmentTemp", "UDID"), (Object)sUDID, 0, false);
            query.setCriteria(criteria);
            MDMUtil.getPersistence().update(query);
        }
        else {
            PushNotificationHandler.logger.log(Level.WARNING, "UDID and Topic can not be null during Authentication Process");
        }
    }
    
    public void cloneIOSCommunicationDetails(final Long managedDeviceID, final String deviceUDID) throws SyMException {
        final String sourceMethod = "cloneIOSCommunicationDetails";
        try {
            SyMLogger.info(PushNotificationHandler.logger, "PushNotificationHandler", sourceMethod, "Cloning the temp data from iosenrollmenttemp to ioccommunicationdetails for deviceUDID : " + deviceUDID);
            final Criteria tempCriteria = new Criteria(Column.getColumn("IOSEnrollmentTemp", "UDID"), (Object)deviceUDID, 0, false);
            final DataObject enrollmentTempDO = MDMUtil.getPersistence().get("IOSEnrollmentTemp", tempCriteria);
            if (!enrollmentTempDO.isEmpty()) {
                final Row enrollmentTempRow = enrollmentTempDO.getRow("IOSEnrollmentTemp");
                final JSONObject notificationData = new JSONObject();
                notificationData.put("NOTIFICATION_TOKEN_ENCRYPTED", enrollmentTempRow.get("DEVICE_TOKEN_ENCRYPTED"));
                notificationData.put("PUSH_MAGIC_ENCRYPTED", enrollmentTempRow.get("PUSH_MAGIC"));
                notificationData.put("UNLOCK_TOKEN_ENCRYPTED", enrollmentTempRow.get("UNLOCK_TOKEN"));
                notificationData.put("TOPIC", enrollmentTempRow.get("TOPIC"));
                getInstance().addOrUpdateManagedIdToNotificationRel(managedDeviceID, 1, notificationData);
            }
            DataAccess.delete(tempCriteria);
        }
        catch (final Exception exp) {
            SyMLogger.error(PushNotificationHandler.logger, "PushNotificationHandler", sourceMethod, "Exception occurred while cloning deviceUDID : " + deviceUDID, (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public List<Long> getNotificationTokenDevices(final List<Long> resourceList, final int notificationType) {
        final List<Long> notificationResourceList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDeviceNotification"));
            selectQuery.addJoin(new Join("ManagedDeviceNotification", "NotificationDetails", new String[] { "NOTIFICATION_DETAILS_ID" }, new String[] { "NOTIFICATION_DETAILS_ID" }, 2));
            selectQuery.addSelectColumn(new Column("ManagedDeviceNotification", "NOTIFICATION_DETAILS_ID"));
            selectQuery.addSelectColumn(new Column("ManagedDeviceNotification", "MANAGED_DEVICE_ID"));
            final Criteria notificationTypeCriteria = new Criteria(new Column("ManagedDeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0);
            final Criteria resourceCriteria = new Criteria(new Column("ManagedDeviceNotification", "MANAGED_DEVICE_ID"), (Object)resourceList.toArray(), 8);
            selectQuery.setCriteria(notificationTypeCriteria.and(resourceCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rows = dataObject.getRows("ManagedDeviceNotification");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    notificationResourceList.add((Long)row.get("MANAGED_DEVICE_ID"));
                }
            }
        }
        catch (final DataAccessException e) {
            PushNotificationHandler.logger.log(Level.INFO, "Exception in get notificatio token devices", (Throwable)e);
        }
        return notificationResourceList;
    }
    
    static {
        PushNotificationHandler.logger = Logger.getLogger("MDMLogger");
        PushNotificationHandler.pushNotificationHandler = null;
    }
}
