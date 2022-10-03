package com.me.mdm.onpremise.server.metracker;

import java.util.Hashtable;
import com.me.mdm.onpremise.server.time.ServerTimeValidationUtil;
import com.me.mdm.onpremise.server.settings.NATReachabilityTask;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.doc.DocMgmt;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import java.util.HashMap;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsHandler;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import com.me.mdm.server.easmanagement.EASMgmtDataHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.metracker.MEMDMTrackerUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Enumeration;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import org.json.JSONArray;
import com.me.devicemanagement.onpremise.server.silentupdate.ondemand.SilentUpdateHelper;
import com.me.devicemanagement.onpremise.server.silentupdate.ondemand.METrackerSilentUpdateImpl;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEMDMTrackerSettingsImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    protected Properties mdmSettingsTrackerProperties;
    private Logger logger;
    private String sourceClass;
    public static final String SU_ALERT_MSG_SHOWED_QPPMS = "SUAlertMsgShowedQPPMs";
    public static final String SU_ALERT_MSG_SHOWED_QPPMS_TOTLCUNT = "SUAlertMsgShowedQPPMsTotlCunt";
    public static final String SU_APPROVE_AND_WITHOUT_RESTART_QPPMS = "SUApproveWithoutRestartQPPMs";
    public static final String SU_APPROVE_AND_WITHOUT_RESTART_QPPMS_TOTLCUNT = "SUApproveWithoutRestartQPPMsTotlCunt";
    public static final String SU_APPROVE_AND_RESTART_QPPMS = "SUApproveRestartQPPMs";
    public static final String SU_APPROVE_AND_RESTART_QPPMS_TOTLCUNT = "SUApproveRestartQPPMsTotlCunt";
    public static final String SU_APPROVE_AND_REMINDME_LATTER_QPPMS = "SUApproveRemindMeLatterQPPMs";
    public static final String SU_APPROVE_AND_REMINDME_LATTER_QPPMS_TOTLCUNT = "SUApproveRemindMeLatterQPPMsTotlCunt";
    public static final String SU_RESTART_QPPMS_TOTLCUNT = "SURestartQPPMsTotlcunt";
    public static final String SU_RESTART_QPPMS = "SURestartQPPMs";
    
    public MEMDMTrackerSettingsImpl() {
        this.mdmSettingsTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerMDMSettingsImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "MEMDMTrackerMDMSettingsImpl:getTrackerProperties", "MDM Settings implementation starts...");
            this.addSSLCertificateDetails();
            this.addSettings();
            this.addPrivacyDetails();
            this.addExchangeServerDetails();
            this.addDocMgmtDetails();
            this.addJunkFileDetails();
            this.addADmetricData();
            this.addNATDetails();
            this.addSilentUpdateDetails();
            this.addServerTimeValidationDetails();
            this.addManagedAdDomainWithSslCount();
            this.addMailServerDetails();
            SyMLogger.info(this.logger, this.sourceClass, "MEMDMTrackerMDMSettingsImpl:getTrackerProperties", "General Settings Summary : " + this.mdmSettingsTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MEMDMTrackerMDMSettingsImpl:getTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmSettingsTrackerProperties;
    }
    
    private JSONObject removeStartsWithUniqueKey(final JSONObject inputJson) throws Exception {
        final JSONObject outputJson = new JSONObject();
        final Iterator keys = inputJson.keys();
        while (keys.hasNext()) {
            final String oldKey = keys.next().toString();
            outputJson.put(oldKey.substring(oldKey.indexOf(".") + 1), inputJson.get(oldKey));
        }
        return outputJson;
    }
    
    private void addSilentUpdateDetails() {
        final JSONObject dcTrackJson = new JSONObject();
        try {
            final MEDMTracker silentUpdateImpl = (MEDMTracker)new METrackerSilentUpdateImpl();
            final Properties dmSilentUpdateProps = silentUpdateImpl.getTrackerProperties();
            final Properties mdmpSilentUpdateProps = this.getMDMPSilentupdateTrackerProps();
            final ArrayList<String> latest3QPPMUniqueIds = SilentUpdateHelper.getInstance().getLatest3QPPMUniqueIds();
            dcTrackJson.put("SUAutoApproveEnabled", ((Hashtable<K, Object>)dmSilentUpdateProps).get("SUAutoApproveEnabled"));
            dcTrackJson.put("SUExportFailReq", ((Hashtable<K, Object>)dmSilentUpdateProps).get("SUExportFailReq"));
            dcTrackJson.put("QPPMDownldFailDtls", (Object)this.swapSpecifiedKeysValue(new JSONObject(((Hashtable<K, Object>)dmSilentUpdateProps).get("QPPMDownldFailDtls").toString()), latest3QPPMUniqueIds));
            dcTrackJson.put("QPPMDownldFailDtlsTotlCunt", ((Hashtable<K, Object>)dmSilentUpdateProps).get("QPPMDownldFailDtlsTotlCunt"));
            dcTrackJson.put("DynamicCheckerDownldFailDtls", (Object)this.swapSpecifiedKeysValue(new JSONObject(((Hashtable<K, Object>)dmSilentUpdateProps).get("DynamicCheckerDownldFailDtls").toString()), latest3QPPMUniqueIds));
            dcTrackJson.put("DynamicCheckerDownldFailDtlsTotlCunt", ((Hashtable<K, Object>)dmSilentUpdateProps).get("DynamicCheckerDownldFailDtlsTotlCunt"));
            dcTrackJson.put("SUReminderMELatter", (Object)this.swapSpecifiedKeysValue(new JSONObject(((Hashtable<K, Object>)dmSilentUpdateProps).get("SUReminderMELatter").toString()), this.getLatest3QPPMTaskIds()));
            dcTrackJson.put("SUReminderMELatterTotlCunt", ((Hashtable<K, Object>)dmSilentUpdateProps).get("SUReminderMELatterTotlCunt"));
            dcTrackJson.put("SUIgnoreTheQPPMs", (Object)this.swapSpecifiedKeys(new JSONArray(((Hashtable<K, Object>)dmSilentUpdateProps).get("SUIgnoreTheQPPMs").toString()), latest3QPPMUniqueIds));
            dcTrackJson.put("SUIgnoreTheQPPMsTotlCunt", ((Hashtable<K, Object>)dmSilentUpdateProps).get("SUIgnoreTheQPPMsTotlCunt"));
            dcTrackJson.put("SUDismissQPPMs", (Object)this.swapSpecifiedKeys(new JSONArray(((Hashtable<K, Object>)dmSilentUpdateProps).get("SUDismissQPPMs").toString()), latest3QPPMUniqueIds));
            dcTrackJson.put("SUDismissQPPMsTotlCunt", ((Hashtable<K, Object>)dmSilentUpdateProps).get("SUDismissQPPMsTotlCunt"));
            dcTrackJson.put("SUApproveWithoutRestartQPPMs", (Object)this.swapSpecifiedKeysValue(new JSONObject(((Hashtable<K, Object>)mdmpSilentUpdateProps).get("SUApproveWithoutRestartQPPMs").toString()), this.getLatest3QPPMTaskIds()));
            dcTrackJson.put("SUApproveWithoutRestartQPPMsTotlCunt", ((Hashtable<K, Object>)mdmpSilentUpdateProps).get("SUApproveWithoutRestartQPPMsTotlCunt"));
            dcTrackJson.put("SUApproveRestartQPPMs", (Object)this.swapSpecifiedKeysValue(new JSONObject(((Hashtable<K, Object>)mdmpSilentUpdateProps).get("SUApproveRestartQPPMs").toString()), this.getLatest3QPPMTaskIds()));
            dcTrackJson.put("SUApproveRestartQPPMsTotlCunt", ((Hashtable<K, Object>)mdmpSilentUpdateProps).get("SUApproveRestartQPPMsTotlCunt"));
            dcTrackJson.put("SUApproveRemindMeLatterQPPMs", (Object)this.swapSpecifiedKeysValue(new JSONObject(((Hashtable<K, Object>)mdmpSilentUpdateProps).get("SUApproveRemindMeLatterQPPMs").toString()), this.getLatest3QPPMTaskIds()));
            dcTrackJson.put("SUApproveRemindMeLatterQPPMsTotlCunt", ((Hashtable<K, Object>)mdmpSilentUpdateProps).get("SUApproveRemindMeLatterQPPMsTotlCunt"));
            dcTrackJson.put("SURestartQPPMsTotlcunt", ((Hashtable<K, Object>)mdmpSilentUpdateProps).get("SURestartQPPMsTotlcunt"));
            final JSONObject isAlertMsgShowed = new JSONObject();
            final Properties customerConfigProps = FileAccessUtil.readProperties(SilentUpdateHelper.getInstance().getSilentUpdateUserConfPath() + File.separator + "customer-specific.props");
            final Enumeration<Object> keys = ((Hashtable<Object, V>)customerConfigProps).keys();
            int isAlertShowedCount = 0;
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement().toString();
                if (key.startsWith("AlertMsgShownTrack.")) {
                    ++isAlertShowedCount;
                    final String uniqueId = key.substring("AlertMsgShownTrack.".length());
                    if (!latest3QPPMUniqueIds.contains(uniqueId)) {
                        continue;
                    }
                    isAlertMsgShowed.put(uniqueId, ((Hashtable<K, Object>)customerConfigProps).get(key));
                }
            }
            dcTrackJson.put("SUAlertMsgShowedQPPMs", (Object)isAlertMsgShowed);
            dcTrackJson.put("SUAlertMsgShowedQPPMsTotlCunt", isAlertShowedCount);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSilentUpdateDetails", "Exception occurred while update SilentUpdate tracking details : ", (Throwable)e);
        }
        ((Hashtable<String, String>)this.mdmSettingsTrackerProperties).put("SilentUpdateDetails", dcTrackJson.toString());
    }
    
    public ArrayList<String> getLatest3QPPMTaskIds() {
        final ArrayList<String> list = new ArrayList<String>();
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("SilentUpdateDetails"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetails", "*"));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("SilentUpdateDetails", "MODIFIED_TIME_IN_CRS"), false));
            selectQuery.setRange(new Range(0, 3));
            final Iterator itr = DataAccess.get((SelectQuery)selectQuery).getRows("SilentUpdateDetails");
            while (itr.hasNext()) {
                final Row row = itr.next();
                final String key = row.get("TASK_ID").toString();
                list.add(key);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getLatest3QPPMUniqueIds", "Exception occurred while fetching latest 3 QPPM UniqueIds : ", (Throwable)e);
        }
        return list;
    }
    
    public Properties getMDMPSilentupdateTrackerProps() {
        final Properties mdmpSilentUpdateProps = new Properties();
        try {
            final JSONObject jsonObject = this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("SUApproveWithoutRestartQPPMs."));
            ((Hashtable<String, String>)mdmpSilentUpdateProps).put("SUApproveWithoutRestartQPPMs", String.valueOf(jsonObject));
            ((Hashtable<String, Integer>)mdmpSilentUpdateProps).put("SUApproveWithoutRestartQPPMsTotlCunt", jsonObject.length());
            final JSONObject jsonObject2 = this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("SUApproveRestartQPPMs."));
            ((Hashtable<String, String>)mdmpSilentUpdateProps).put("SUApproveRestartQPPMs", String.valueOf(jsonObject2));
            ((Hashtable<String, Integer>)mdmpSilentUpdateProps).put("SUApproveRestartQPPMsTotlCunt", jsonObject2.length());
            final JSONObject jsonObject3 = this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("SUApproveRemindMeLatterQPPMs."));
            ((Hashtable<String, String>)mdmpSilentUpdateProps).put("SUApproveRemindMeLatterQPPMs", String.valueOf(jsonObject3));
            ((Hashtable<String, Integer>)mdmpSilentUpdateProps).put("SUApproveRemindMeLatterQPPMsTotlCunt", jsonObject3.length());
            final JSONObject jsonObject4 = this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("SURestartQPPMs"));
            ((Hashtable<String, Integer>)mdmpSilentUpdateProps).put("SURestartQPPMsTotlcunt", jsonObject4.length());
            SyMLogger.info(this.logger, this.sourceClass, "getMDMPSilentupdateTrackerProps", "MDMPSUDetails Summary : " + mdmpSilentUpdateProps);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getMDMPSilentupdateTrackerProps", "getMDMPSilentupdateTrackerProps has been failed : ", (Throwable)e);
        }
        return mdmpSilentUpdateProps;
    }
    
    private JSONArray swapSpecifiedKeys(final JSONArray sourceJsonArray, final ArrayList<String> keys) throws Exception {
        final JSONArray destinationJson = new JSONArray();
        for (int i = 0, len = sourceJsonArray.length(); i < len; ++i) {
            final String key = sourceJsonArray.get(i).toString();
            if (keys.contains(key)) {
                destinationJson.put((Object)key);
            }
        }
        return destinationJson;
    }
    
    private void addSSLCertificateDetails() {
        try {
            Boolean itHasIntermediateCertificate = false;
            final Boolean isThirdPartySSLInstalled = ApiFactoryProvider.getServerSettingsAPI().getCertificateType() == 2;
            final Boolean isSelfSignedCA = ApiFactoryProvider.getServerSettingsAPI().getCertificateType() == 3;
            if (isThirdPartySSLInstalled || isSelfSignedCA) {
                final String intermediateCertificateFilePath = SSLCertificateUtil.getInstance().getIntermediateCertificateFilePath();
                if (intermediateCertificateFilePath != null && !intermediateCertificateFilePath.equals("")) {
                    itHasIntermediateCertificate = true;
                }
            }
            this.mdmSettingsTrackerProperties.setProperty("Thirdparty_SSL_Enabled", String.valueOf(isThirdPartySSLInstalled));
            this.mdmSettingsTrackerProperties.setProperty("Using_Intermediate_Cert", String.valueOf(itHasIntermediateCertificate));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSSLCertificateDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private JSONObject swapSpecifiedKeysValue(final JSONObject sourceJson, final ArrayList<String> keys) throws Exception {
        final JSONObject destinationJson = new JSONObject();
        for (final String key : keys) {
            if (sourceJson.has(key)) {
                destinationJson.put(key, sourceJson.get(key));
            }
        }
        return destinationJson;
    }
    
    private int getManagedADDomainCount() {
        final int managedADCount = 0;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDomain"));
        final Criteria criteria = new Criteria(new Column("ManagedDomain", "IS_AD_DOMAIN"), (Object)Boolean.TRUE, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(new Column((String)null, "*"));
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                return resultDO.size("ManagedDomain");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MEMDMTrackerSettingsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return managedADCount;
    }
    
    private void addManagedAdDomainWithSslCount() {
        final String keyName = "Managed_AD_Domain_with_SSL_Count";
        int sslDomains = 0;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDomain"));
        query.addJoin(new Join("ManagedDomain", "ManagedDomainConfig", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDomain", "IS_AD_DOMAIN"), (Object)Boolean.TRUE, 0).and(Column.getColumn("ManagedDomainConfig", "USE_SSL"), (Object)Boolean.TRUE, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        try {
            final DataObject result = SyMUtil.getPersistence().get(query);
            if (!result.isEmpty()) {
                sslDomains = result.size("ManagedDomain");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MEMDMTrackerSettingsImpl.class.getName()).log(Level.WARNING, "Error while getting AD domains with SSL: ", ex);
        }
        this.mdmSettingsTrackerProperties.setProperty(keyName, Integer.toString(sslDomains));
    }
    
    private Properties getNetworkDomainProperties() {
        final Properties domainProps = new Properties();
        try {
            final String somSummary = SyMUtil.getSyMParameter("SoMSummary");
            if (somSummary != null && somSummary.trim().length() > 0) {
                final String[] summaryArray = somSummary.split("\\|");
                for (int i = 0; i < summaryArray.length; ++i) {
                    if (summaryArray[i].startsWith("adc")) {
                        ((Hashtable<String, String>)domainProps).put("adc", summaryArray[i].split("-", 2)[1]);
                    }
                    if (summaryArray[i].startsWith("wgc")) {
                        ((Hashtable<String, String>)domainProps).put("wgc", summaryArray[i].split("-", 2)[1]);
                    }
                    if (summaryArray[i].startsWith("env")) {
                        ((Hashtable<String, String>)domainProps).put("env", summaryArray[i].split("-")[1]);
                    }
                    if (summaryArray[i].startsWith("sErr")) {
                        ((Hashtable<String, String>)domainProps).put("sErr", summaryArray[i].split("-")[1]);
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MEMDMTrackerSettingsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return domainProps;
    }
    
    private void addSettings() throws SyMException {
        final Properties natProps = MEMDMTrackerUtil.getNATConfiguration();
        this.mdmSettingsTrackerProperties.setProperty("Is_NAT_IP", String.valueOf(natProps.getProperty("IS_NAT_IP")));
        this.mdmSettingsTrackerProperties.setProperty("Is_NAT_Configured", String.valueOf(natProps.getProperty("IS_NAT_CONFIGURED")));
        this.mdmSettingsTrackerProperties.setProperty("Mail_Configured", String.valueOf(MEMDMTrackerUtil.isMailServerConfigured()));
        this.mdmSettingsTrackerProperties.setProperty("Proxy", String.valueOf(MEMDMTrackerUtil.getProxySettings()));
        this.mdmSettingsTrackerProperties.setProperty("Fwd_Server", String.valueOf(MEMDMTrackerUtil.getForwardingServerConfigured()));
        this.mdmSettingsTrackerProperties.setProperty("Managed_AD_Domain_Count", String.valueOf(this.getManagedADDomainCount()));
        final Properties domainProps = this.getNetworkDomainProperties();
        this.mdmSettingsTrackerProperties.setProperty("AD_Computer_Count", String.valueOf(((Hashtable<K, Object>)domainProps).get("adc")));
        this.mdmSettingsTrackerProperties.setProperty("WG_Computer_Count", String.valueOf(((Hashtable<K, Object>)domainProps).get("wgc")));
        this.mdmSettingsTrackerProperties.setProperty("Network_Env", String.valueOf(((Hashtable<K, Object>)domainProps).get("env")));
        this.mdmSettingsTrackerProperties.setProperty("NW_Domain_Error", String.valueOf(((Hashtable<K, Object>)domainProps).get("sErr")));
        this.addLocationDetails();
        this.addFeatureSettingDetails();
    }
    
    private void addExchangeServerDetails() {
        JSONObject exchangeServerDetails = new JSONObject();
        try {
            exchangeServerDetails = EASMgmtDataHandler.getInstance().getMeTrackingData();
            exchangeServerDetails.put("REMOVE_EAS_DEVICE", getKeyCountFromMEDCTrackerUtil("REMOVE_EAS_DEVICE"));
            exchangeServerDetails.put("ROLLBACK_BLOCKED_DEVICES", getKeyCountFromMEDCTrackerUtil("ROLLBACK_BLOCKED_DEVICES"));
            final String easProfileSecEmailClk = METrackerUtil.getMETrackParams("EAS_PROFILE_SECURE_EMAIL_CLICK").getProperty("EAS_PROFILE_SECURE_EMAIL_CLICK");
            final int easProfileSecEmailClkCount = (easProfileSecEmailClk == null) ? 0 : Integer.parseInt(easProfileSecEmailClk);
            exchangeServerDetails.put("EAS_PROFILE_SECURE_EMAIL_CLICK", easProfileSecEmailClkCount);
            final String easProfileDontSecEmailClk = METrackerUtil.getMETrackParams("EAS_PROFILE_DONT_SECURE_EMAIL_CLICK").getProperty("EAS_PROFILE_DONT_SECURE_EMAIL_CLICK");
            final int easProfileDontSecEmailClkCount = (easProfileDontSecEmailClk == null) ? 0 : Integer.parseInt(easProfileDontSecEmailClk);
            exchangeServerDetails.put("EAS_PROFILE_DONT_SECURE_EMAIL_CLICK", easProfileDontSecEmailClkCount);
            final String easProfileDoNotShowClk = METrackerUtil.getMETrackParams("EAS_PROFILE_DO_NOT_SHOW_CLICK").getProperty("EAS_PROFILE_DO_NOT_SHOW_CLICK");
            final int easProfileDoNotShowClkCount = (easProfileDoNotShowClk == null) ? 0 : Integer.parseInt(easProfileDoNotShowClk);
            exchangeServerDetails.put("EAS_PROFILE_DO_NOT_SHOW_CLICK", easProfileDoNotShowClkCount);
        }
        catch (final JSONException ex) {
            Logger.getLogger(MEMDMTrackerSettingsImpl.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        ((Hashtable<String, JSONObject>)this.mdmSettingsTrackerProperties).put("EXCHANGE_SERVER_DETAILS", exchangeServerDetails);
    }
    
    public static long getKeyCountFromMEDCTrackerUtil(final String key) {
        final String stringCount = METrackerUtil.getMETrackParams(key).getProperty(key);
        final long count = (stringCount == null) ? 0L : Long.parseLong(stringCount);
        return count;
    }
    
    private void addLocationDetails() {
        final JSONObject locationSettings = new JSONObject();
        try {
            locationSettings.put("Is_Geo_Tracking_Enabled", (Object)MEMDMTrackerUtil.isGeoTrackingEnabled());
            locationSettings.put("Is_Geo_Tracking_Devices_Included", (Object)MEMDMTrackerUtil.isGeoTrackingDevicesIncluded());
            locationSettings.put("Geo_Tracking_Map_Type", (Object)MEMDMTrackerUtil.getGeoLocationMapType());
            locationSettings.put("Is_Google_Map_with_API_Key", (Object)MEMDMTrackerUtil.isGoogleMapwithAuthenticationKey());
            locationSettings.put("Geo_Tracking_Status", MEMDMTrackerUtil.getGeoTrackingStatus());
            final JSONObject lostModeDevices = MEMDMTrackerUtil.geDevicesByLostModeStatus();
            locationSettings.put("Lost_Mode_Initiated_Devices", lostModeDevices.optInt("Lost_Mode_Initiated_Devices", 0));
            locationSettings.put("Lost_Mode_Activated_Devices", lostModeDevices.optInt("Lost_Mode_Activated_Devices", 0));
            locationSettings.put("Lost_Mode_Activation_Failed_Devices", lostModeDevices.optInt("Lost_Mode_Activation_Failed_Devices", 0));
            locationSettings.put("Lost_Mode_DeActivation_Initiated_Devices", lostModeDevices.optInt("Lost_Mode_DeActivation_Initiated_Devices", 0));
            locationSettings.put("Lost_Mode_DeActivated_Devices", lostModeDevices.optInt("Lost_Mode_DeActivated_Devices", 0));
            locationSettings.put("Lost_Mode_DeActivation_Failed_Devices", lostModeDevices.optInt("Lost_Mode_DeActivation_Failed_Devices", 0));
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final JSONObject locationSettingsJSON = LocationSettingsDataHandler.getInstance().getLocationSettingsJSON(customerId);
                locationSettings.put("LocationHistoryEnabled", locationSettingsJSON.optInt("LOCATION_HISTORY_STATUS"));
                locationSettings.put("LocationRadius", locationSettingsJSON.optInt("LOCATION_RADIUS"));
                locationSettings.put("LocationInterval", locationSettingsJSON.optInt("LOCATION_INTERVAL"));
                locationSettings.put("Location_History_Start_Date", (Object)MEMDMTrackerUtil.getLocationHistoryDataStartDate());
                locationSettings.put("Location_History_Data_Count", MEMDMTrackerUtil.getLocationHistoryDataCount());
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(MEMDMTrackerSettingsImpl.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        ((Hashtable<String, JSONObject>)this.mdmSettingsTrackerProperties).put("Geo_Settings", locationSettings);
    }
    
    private void addFeatureSettingDetails() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("FEATURE_SETTING");
        final HashMap<Integer, String> featureSettings = MDMFeatureSettingsHandler.getFeatureSettingsAndTypes();
        final JSONObject featureSettingDetails = new JSONObject();
        try {
            final DMDataSetWrapper dmSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmSet.next()) {
                for (final int i : featureSettings.keySet()) {
                    final String featureName = featureSettings.get(i);
                    featureSettingDetails.put(featureName, dmSet.getValue(featureName));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting Feature setting count: ", e);
        }
        ((Hashtable<String, String>)this.mdmSettingsTrackerProperties).put("FEATURE_SETTING", featureSettingDetails.toString());
    }
    
    private void addDocMgmtDetails() {
        JSONObject docMgmtDetails = new JSONObject();
        try {
            docMgmtDetails = DocMgmtDataHandler.getInstance().getDocMgmtMEtrackingInfo();
            docMgmtDetails.put("DOC_PAGE_VISIT", getKeyCountFromMEDCTrackerUtil("DOC_PAGE_VISIT"));
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
        ((Hashtable<String, JSONObject>)this.mdmSettingsTrackerProperties).put("DocumentDetails", docMgmtDetails);
    }
    
    private void addPrivacyDetails() {
        try {
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                final int count = DBUtil.getRecordCount("MDPrivacyToOwnedBy", "PRIVACY_SETTINGS_ID", (Criteria)null);
                if (count == 0) {
                    this.mdmSettingsTrackerProperties.setProperty("PrivacySettingsConfigured", "false");
                }
                else {
                    this.mdmSettingsTrackerProperties.setProperty("PrivacySettingsConfigured", "true");
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(MEMDMTrackerSettingsImpl.class.getName()).log(Level.SEVERE, "Exception in addPrivacyDetails ", e);
        }
    }
    
    private void addJunkFileDetails() {
        try {
            this.logger.log(Level.INFO, "Starting junk file tracking");
            final ArrayList libList = new ArrayList();
            libList.add("google-api-services-androidenterprise-v1-rev21-1.21.0.jar");
            libList.add("google-oauth-client-1.20.0.jar");
            libList.add("google-http-client-jackson2-1.20.0.jar");
            libList.add("google-http-client-1.20.0.jar");
            libList.add("google-api-services-androidenterprise-v1-rev57-1.22.0.jar");
            libList.add("google-api-services-admin-directory-directory_v1-rev55-1.20.0.jar");
            libList.add("google-api-client-1.20.0.jar");
            libList.add("httpclient-4.0.1.jar");
            libList.add("httpcore-4.0.1.jar");
            Integer junkFileCount = 0;
            final String serverHome = System.getProperty("server.home");
            for (final Object sourceFile : libList) {
                final String fileName = serverHome + File.separator + File.separator + (String)sourceFile;
                if (new File(fileName).exists()) {
                    ++junkFileCount;
                }
            }
            final JSONObject junkFileDetails = new JSONObject();
            junkFileDetails.put("junkFileCount", (Object)junkFileCount.toString());
            this.mdmSettingsTrackerProperties.setProperty("Junk_File_details", junkFileDetails.toString());
        }
        catch (final JSONException ex) {
            Logger.getLogger(MEMDMTrackerSettingsImpl.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    private void addADmetricData() {
        final org.json.simple.JSONArray dirMetricData = MDMUtil.executeSelectQuery(MDMCoreQuery.getInstance().getMDMQueryMap("DIRECTORY_TRACKING_QUERY"));
        if (dirMetricData != null && dirMetricData.size() > 0) {
            final org.json.simple.JSONObject dirData = (org.json.simple.JSONObject)dirMetricData.get(0);
            if (dirData != null) {
                dirData.remove((Object)"CUSTOMER_ID");
                final Iterator itr = dirData.keySet().iterator();
                while (itr != null && itr.hasNext()) {
                    final String key = itr.next();
                    final String val = String.valueOf(dirData.get((Object)key));
                    if (SyMUtil.isStringEmpty(val)) {
                        dirData.put((Object)key, (Object)"0");
                    }
                }
                this.mdmSettingsTrackerProperties.setProperty("DirectoryMetrics", dirData.toString());
            }
        }
    }
    
    private void addNATDetails() {
        final JSONObject NATDetails = new JSONObject();
        final JSONObject NATPortDetails = new JSONObject();
        try {
            NATDetails.put("is_nat_reachable", (Object)NATReachabilityTask.isNATReachable());
            this.mdmSettingsTrackerProperties.setProperty("nat_reachability_details", NATDetails.toString());
            NATPortDetails.put("nat_port_changed", (Object)this.isNATPortChanged());
            this.mdmSettingsTrackerProperties.setProperty("nat_port_details", NATPortDetails.toString());
        }
        catch (final Exception exception) {
            SyMLogger.error(this.logger, this.sourceClass, "addNATDetails", "Exception : ", (Throwable)exception);
        }
    }
    
    private Boolean isNATPortChanged() {
        Boolean isNATPortChanged = false;
        try {
            final Integer latestPort = ((Hashtable<K, Integer>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_HTTPS_PORT");
            MDMUtil.getInstance();
            final Integer installationPort = ((Hashtable<K, Integer>)MDMUtil.getDCServerInfo()).get("HTTPS_PORT");
            if (!installationPort.equals(latestPort)) {
                isNATPortChanged = true;
            }
        }
        catch (final Exception exception) {
            SyMLogger.error(this.logger, this.sourceClass, "isNATPortChanged", "Exception : ", (Throwable)exception);
        }
        return isNATPortChanged;
    }
    
    private void addServerTimeValidationDetails() {
        final JSONObject details = ServerTimeValidationUtil.getMETrackDetails();
        this.mdmSettingsTrackerProperties.setProperty("server_time_validation_details", details.toString());
    }
    
    private void addMailServerDetails() {
        final JSONObject mailServerDetails = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SmtpConfiguration"));
        selectQuery.addSelectColumn(new Column("SmtpConfiguration", "IS_SMTPS_ENABLED"));
        selectQuery.addSelectColumn(new Column("SmtpConfiguration", "IS_TLS_ENABLED"));
        selectQuery.addSelectColumn(new Column("SmtpConfiguration", "PASSWORD"));
        selectQuery.addSelectColumn(new Column("SmtpConfiguration", "SERVERNAME"));
        selectQuery.addSelectColumn(new Column("SmtpConfiguration", "AUTH_TYPE"));
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                mailServerDetails.put("isMailServerConfigured", (Object)Boolean.TRUE);
                final Row mailServerRow = dataObject.getRow("SmtpConfiguration");
                final String password = (String)mailServerRow.get("PASSWORD");
                final String serverName = (String)mailServerRow.get("SERVERNAME");
                final int authType = mailServerRow.getInt("AUTH_TYPE");
                mailServerDetails.put("isTLSEnabled", mailServerRow.get("IS_TLS_ENABLED"));
                mailServerDetails.put("isSMTPSEnabled", mailServerRow.get("IS_SMTPS_ENABLED"));
                mailServerDetails.put("isAuthEnabled", (Object)((password == null) ? Boolean.FALSE : Boolean.TRUE));
                if (authType == 0) {
                    mailServerDetails.put("authType", (Object)"Basic");
                }
                if (authType == 1) {
                    mailServerDetails.put("authType", (Object)"OAuth");
                }
            }
            else {
                mailServerDetails.put("isMailServerConfigured", (Object)Boolean.FALSE);
            }
            this.mdmSettingsTrackerProperties.setProperty("MailServer_Details", mailServerDetails.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while Fetching Mail Server Setting Tracking Details", e);
        }
    }
}
