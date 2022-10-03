package com.me.mdm.server.deployment.app.noui;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.deployment.DeplymentConfigHandler;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppDeploymentPolicyNonUiManager
{
    static final Logger LOGGER;
    
    public Long getDeploymentConfigIdForAppDeployment(final Long customerId, final Long userId, final JSONObject appDepDetails) throws DataAccessException, Exception {
        final Boolean notify = appDepDetails.optBoolean("EMAIL_NOTIFY_END_USER", false);
        final Boolean forceInstall = appDepDetails.optBoolean("FORCE_APP_INSTALL", false);
        final Boolean doNotUninstall = appDepDetails.optBoolean("DO_NOT_UNINSTALL", false);
        final Boolean sendEnrollmentRequest = appDepDetails.optBoolean("SEND_ENROLLMENT_REQUEST", false);
        final String configName = this.constructDeploymentConfigName(appDepDetails);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentConfig"));
        final Join appDeployJoin = new Join("DeploymentConfig", "AppDeploymentPolicy", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2);
        sQuery.addJoin(appDeployJoin);
        final Criteria silentInstallCriteria = new Criteria(new Column("AppDeploymentPolicy", "FORCE_APP_INSTALL"), (Object)forceInstall, 0);
        final Criteria notifyCriteria = new Criteria(new Column("AppDeploymentPolicy", "EMAIL_NOTIFY_END_USER"), (Object)notify, 0);
        final Criteria uninstallCriteria = new Criteria(new Column("AppDeploymentPolicy", "DO_NOT_UNINSTALL"), (Object)doNotUninstall, 0);
        final Criteria sendEnrollmentRequestCriteria = new Criteria(new Column("AppDeploymentPolicy", "SEND_ENROLLMENT_REQUEST"), (Object)sendEnrollmentRequest, 0);
        final Criteria customerIdCriteria = new Criteria(new Column("DeploymentConfig", "CUSTOMER_ID"), (Object)customerId, 0);
        sQuery.setCriteria(customerIdCriteria.and(silentInstallCriteria).and(notifyCriteria).and(uninstallCriteria).and(sendEnrollmentRequestCriteria));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final JSONObject depData = new JSONObject();
            depData.put("LAST_MODIFIED_BY", (Object)userId);
            depData.put("CUSTOMER_ID", (Object)customerId);
            depData.put("DEPLOYMENT_CONFIG_NAME", (Object)configName);
            depData.put("DEPLOYMENT_CONFIG_DESCRIPTION", (Object)("Policy for " + configName));
            final JSONArray depPolicyArr = new JSONArray();
            final JSONObject depPolicyData = new JSONObject();
            depPolicyData.put("DEPLOYMENT_POLICY_TYPE_ID", 301);
            depPolicyData.put("DEPLOYMENT_POLICY_TYPE", (Object)"APP_DEPLOYMENT_POLICY");
            final JSONObject depPolicyDetails = new JSONObject();
            depPolicyDetails.put("EMAIL_NOTIFY_END_USER", (Object)notify);
            depPolicyDetails.put("FORCE_APP_INSTALL", (Object)forceInstall);
            depPolicyDetails.put("DO_NOT_UNINSTALL", (Object)doNotUninstall);
            depPolicyDetails.put("SEND_ENROLLMENT_REQUEST", (Object)sendEnrollmentRequest);
            depPolicyData.put("PolicyDetails", (Object)depPolicyDetails);
            depPolicyArr.put((Object)depPolicyData);
            depData.put("DeploymentPolicy", (Object)depPolicyArr);
            AppDeploymentPolicyNonUiManager.LOGGER.log(Level.INFO, "No default entry available for {0}, so creating new", new Object[] { appDepDetails });
            return new DeplymentConfigHandler().persistDeploymentConfig(depData);
        }
        final Row row = dO.getRow("DeploymentConfig");
        return (Long)row.get("DEPLOYMENT_CONFIG_ID");
    }
    
    private String constructDeploymentConfigName(final JSONObject appDepDetails) {
        final Boolean notify = appDepDetails.optBoolean("EMAIL_NOTIFY_END_USER", false);
        final Boolean forceInstall = appDepDetails.optBoolean("FORCE_APP_INSTALL", false);
        final Boolean doNotUninstall = appDepDetails.optBoolean("DO_NOT_UNINSTALL", false);
        final Boolean sendEnrollmentRequest = appDepDetails.optBoolean("SEND_ENROLLMENT_REQUEST", false);
        final StringBuilder builder = new StringBuilder("");
        builder.append(notify ? "" : "no_");
        builder.append("notify_and_");
        builder.append(forceInstall ? "" : "no_");
        builder.append("forceinstall_and_");
        builder.append(doNotUninstall ? "noUninstall_and_" : "uninstall_and_");
        builder.append(sendEnrollmentRequest ? "sendEnrollmentRequest" : "doNotSendEnrollmentRequest");
        return builder.toString();
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
