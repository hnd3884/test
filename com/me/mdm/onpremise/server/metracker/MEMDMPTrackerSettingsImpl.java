package com.me.mdm.onpremise.server.metracker;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.webclient.admin.fos.FosTrialLicense;
import com.adventnet.persistence.fos.FOS;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;

public class MEMDMPTrackerSettingsImpl extends MEMDMTrackerSettingsImpl implements MEDMTracker
{
    private Logger logger;
    private String sourceClass;
    
    public MEMDMPTrackerSettingsImpl() {
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerMDMSettingsImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "MEMDMTrackerMDMSettingsImpl:getTrackerProperties", "MDM Settings implementation starts...");
            super.getTrackerProperties();
            this.addFosDetails();
            this.setFsProps();
            this.addTlsDetails();
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MEMDMTrackerMDMSettingsImpl:getTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmSettingsTrackerProperties;
    }
    
    private Properties setFsProps() {
        final JSONObject fwsPropsJSON = new JSONObject();
        final String isFSConfigured = SyMUtil.getSyMParameter("forwarding_server_config");
        try {
            if (isFSConfigured != null && isFSConfigured.equalsIgnoreCase("true")) {
                Properties natProps = null;
                natProps = NATHandler.getNATConfigurationProperties();
                final Properties fwsProps = FwsUtil.fsProps;
                String serverName = natProps.getProperty("NAT_ADDRESS");
                fwsPropsJSON.put("FsIsPublicIP", (Object)"false");
                if (fwsProps != null && fwsProps.getProperty("publicIP") != null) {
                    fwsPropsJSON.put("FsIsPublicIP", (Object)"true");
                    serverName = fwsProps.getProperty("publicIP");
                }
                fwsPropsJSON.put("FsReachable", FwsUtil.isServerUp(serverName, (int)((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT")));
                fwsPropsJSON.put("FsBuildNumber", (Object)fwsProps.getProperty("buildNumber"));
            }
        }
        catch (final DataAccessException e) {
            SyMLogger.error(this.logger, this.sourceClass, "setFsProps", "Exception while getting forwarding server information ", (Throwable)e);
        }
        catch (final JSONException ex) {
            SyMLogger.error(this.logger, this.sourceClass, "setFsProps", "JSON Exception while getting forwarding server information ", (Throwable)ex);
        }
        this.mdmSettingsTrackerProperties.setProperty("FsDetails", fwsPropsJSON.toString());
        return this.mdmSettingsTrackerProperties;
    }
    
    private void addFosDetails() {
        try {
            final JSONObject fosDetailsJSON = new JSONObject();
            fosDetailsJSON.put("FosStatus", (Object)(FOS.isEnabled() ? "Enabled" : "Disabled"));
            final Boolean isFosTrialed = FosTrialLicense.isFosTrialFlagEnabled();
            fosDetailsJSON.put("IsFosTrialed", (Object)isFosTrialed.toString());
            fosDetailsJSON.put("FosTrialValidity", (Object)FosTrialLicense.getFosTrialValidity());
            final Object TakeOver_Count = DBUtil.getSumOfValue("FosParams", "TAKEOVER_COUNT", (Criteria)null);
            final Object Last_TakeOver_Time = DBUtil.getMaxOfValue("FosParams", "LAST_TAKEOVER_TIME", (Criteria)null);
            if (TakeOver_Count != null) {
                fosDetailsJSON.put("TakeOverCount", (Object)Long.valueOf(TakeOver_Count.toString()));
                fosDetailsJSON.put("LatestFosTakeOverTime", (Object)Long.valueOf(Last_TakeOver_Time.toString()));
            }
            this.mdmSettingsTrackerProperties.setProperty("FOS_Details", fosDetailsJSON.toString());
            final JSONObject ReplicationErrorsJson = new JSONObject();
            final DataObject ReplicationDO = SyMUtil.getPersistence().get("FosReplicationErrorCodes", (Criteria)null);
            final Iterator ReplicationItr = ReplicationDO.getRows("FosReplicationErrorCodes");
            while (ReplicationItr.hasNext()) {
                final Row ErrorRow = ReplicationItr.next();
                ReplicationErrorsJson.put(ErrorRow.get("ERROR_CODE").toString(), ErrorRow.get("ERROR_FREQUENCY"));
            }
            this.mdmSettingsTrackerProperties.setProperty("FOS_Replication_Errors", ReplicationErrorsJson.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addFosDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addTlsDetails() {
        final String TLS_DETAILS = "tls_details";
        final String OLDER_TLS_DISABLED = "older_tls_disabled";
        try {
            final Properties webServerProps = WebServerUtil.getWebServerSettings();
            final JSONObject details = new JSONObject().put("older_tls_disabled", Boolean.parseBoolean(webServerProps.getProperty("IsTLSV2Enabled")));
            this.mdmSettingsTrackerProperties.setProperty("tls_details", details.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addTlsDetails", "Exception : ", (Throwable)e);
        }
    }
}
