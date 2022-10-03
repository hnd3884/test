package com.me.mdm.server.deployment.policy;

import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.deployment.DeploymentPolicyDataInterface;

public class AppDeploymentPolicyImpl implements DeploymentPolicyDataInterface
{
    public static Logger logger;
    
    @Override
    public void addOrUpdateDeploymentPolicy(final Long deploymentPolicyId, final JSONObject dataJSON) throws JSONException, DataAccessException {
        final Boolean notifyEndUser = dataJSON.optBoolean("EMAIL_NOTIFY_END_USER", false);
        final Boolean forceInstallApp = dataJSON.optBoolean("FORCE_APP_INSTALL", false);
        final Boolean doNotUninstall = dataJSON.optBoolean("DO_NOT_UNINSTALL", false);
        final Boolean sendEnrollmentRequest = dataJSON.optBoolean("SEND_ENROLLMENT_REQUEST", false);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppDeploymentPolicy"));
        final Criteria depPolicyIdCriteria = new Criteria(new Column("AppDeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)deploymentPolicyId, 0);
        selectQuery.setCriteria(depPolicyIdCriteria);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(selectQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("AppDeploymentPolicy");
            row.set("DEPLOYMENT_POLICY_ID", (Object)deploymentPolicyId);
            row.set("EMAIL_NOTIFY_END_USER", (Object)notifyEndUser);
            row.set("FORCE_APP_INSTALL", (Object)forceInstallApp);
            row.set("DO_NOT_UNINSTALL", (Object)doNotUninstall);
            row.set("SEND_ENROLLMENT_REQUEST", (Object)sendEnrollmentRequest);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("AppDeploymentPolicy");
            row.set("EMAIL_NOTIFY_END_USER", (Object)notifyEndUser);
            row.set("FORCE_APP_INSTALL", (Object)forceInstallApp);
            row.set("DO_NOT_UNINSTALL", (Object)doNotUninstall);
            row.set("SEND_ENROLLMENT_REQUEST", (Object)sendEnrollmentRequest);
            dO.updateRow(row);
        }
        DataAccess.update(dO);
    }
    
    @Override
    public JSONObject getDeploymentDataPolicy(final Long deploymentPolicyId) throws JSONException, DataAccessException {
        final JSONObject dataJSON = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppDeploymentPolicy"));
        final Criteria depPolicyIdCriteria = new Criteria(new Column("AppDeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)deploymentPolicyId, 0);
        selectQuery.setCriteria(depPolicyIdCriteria);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(selectQuery);
        if (!dO.isEmpty()) {
            final Row row = dO.getRow("AppDeploymentPolicy");
            dataJSON.put("DEPLOYMENT_POLICY_ID", (Object)deploymentPolicyId);
            dataJSON.put("EMAIL_NOTIFY_END_USER", row.get("EMAIL_NOTIFY_END_USER"));
            dataJSON.put("FORCE_APP_INSTALL", row.get("FORCE_APP_INSTALL"));
            dataJSON.put("DO_NOT_UNINSTALL", row.get("DO_NOT_UNINSTALL"));
            dataJSON.put("SEND_ENROLLMENT_REQUEST", row.get("SEND_ENROLLMENT_REQUEST"));
        }
        return dataJSON;
    }
    
    private SelectQuery getQueryForDeploymentPolicyDetails() {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMResourceToDeploymentConfigs"));
        final Join depConfigJoin = new Join("MDMResourceToDeploymentConfigs", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Join depPolicyJoin = new Join("MDMResourceToDeploymentConfigs", "DeploymentPolicy", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Join appPolicyJoin = new Join("DeploymentPolicy", "AppDeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2);
        sQuery.addJoin(depConfigJoin);
        sQuery.addJoin(depPolicyJoin);
        sQuery.addJoin(appPolicyJoin);
        sQuery.addSelectColumn(new Column("MDMResourceToDeploymentConfigs", "*"));
        sQuery.addSelectColumn(new Column("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"));
        sQuery.addSelectColumn(new Column("DeploymentConfig", "CUSTOMER_ID"));
        sQuery.addSelectColumn(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_ID"));
        sQuery.addSelectColumn(new Column("DeploymentPolicy", "DEPLOYMENT_CONFIG_ID"));
        sQuery.addSelectColumn(new Column("AppDeploymentPolicy", "*"));
        return sQuery;
    }
    
    @Override
    public JSONObject getEffectiveDeploymentDataPolicy(final Long resourceId, final Long profileId) throws JSONException, DataAccessException {
        final JSONObject deploymentPolicyJSON = new JSONObject();
        final SelectQuery sQuery = this.getQueryForDeploymentPolicyDetails();
        final Criteria resourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria profileCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileId, 0);
        sQuery.setCriteria(resourceCriteria.and(profileCriteria));
        final SortColumn sourceSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "DEPLOYMENT_CONFIG_SOURCE"), false);
        sQuery.addSortColumn(sourceSortColumn);
        final SortColumn timeSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "ADDED_TIME"), false);
        sQuery.addSortColumn(timeSortColumn);
        final DataObject dO = DataAccess.get(sQuery);
        if (dO != null && !dO.isEmpty()) {
            final Row row = dO.getRow("AppDeploymentPolicy");
            if (row != null) {
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_ID", row.get("DEPLOYMENT_POLICY_ID"));
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_TYPE", (Object)"APP_DEPLOYMENT_POLICY");
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_TYPE_ID", 301);
                final JSONObject policyDetails = new JSONObject();
                policyDetails.put("DEPLOYMENT_POLICY_ID", row.get("DEPLOYMENT_POLICY_ID"));
                policyDetails.put("EMAIL_NOTIFY_END_USER", row.get("EMAIL_NOTIFY_END_USER"));
                policyDetails.put("FORCE_APP_INSTALL", row.get("FORCE_APP_INSTALL"));
                policyDetails.put("DO_NOT_UNINSTALL", row.get("DO_NOT_UNINSTALL"));
                policyDetails.put("SEND_ENROLLMENT_REQUEST", row.get("SEND_ENROLLMENT_REQUEST"));
                deploymentPolicyJSON.put("PolicyDetails", (Object)policyDetails);
            }
        }
        return deploymentPolicyJSON;
    }
    
    public ArrayList getSilentInstallDeployedResources(final List resourceList, final Long profileId, final Long businessStoreID) throws SQLException {
        final ArrayList toBeSilentlyInstalledResourceList = new ArrayList();
        try {
            final SelectQuery sQuery = this.getQueryForDeploymentPolicyDetails();
            final Criteria resourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria profileCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileId, 0);
            Criteria criteria = resourceCriteria.and(profileCriteria);
            if (businessStoreID != null && businessStoreID != -1L) {
                criteria = criteria.and(new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            }
            sQuery.setCriteria(criteria);
            final SortColumn resourceIdSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), false);
            sQuery.addSortColumn(resourceIdSortColumn);
            final SortColumn sourceSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "DEPLOYMENT_CONFIG_SOURCE"), false);
            sQuery.addSortColumn(sourceSortColumn);
            final SortColumn timeSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "ADDED_TIME"), false);
            sQuery.addSortColumn(timeSortColumn);
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)sQuery);
            Long prevResourceId = -1L;
            while (dataSet.next()) {
                final Long resourceID = (Long)dataSet.getValue("RESOURCE_ID");
                final Boolean forceInstall = (Boolean)dataSet.getValue("FORCE_APP_INSTALL");
                if (!prevResourceId.equals(resourceID) && forceInstall) {
                    toBeSilentlyInstalledResourceList.add(resourceID);
                }
                prevResourceId = resourceID;
            }
        }
        catch (final Exception ex) {
            AppDeploymentPolicyImpl.logger.log(Level.INFO, "Exception in getSilentInstallDeployedResources", ex);
        }
        return toBeSilentlyInstalledResourceList;
    }
    
    public JSONObject getAppDeploymentPolicy(final Long deployConfId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppDeploymentPolicy"));
        selectQuery.addJoin(new Join("AppDeploymentPolicy", "DeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2));
        selectQuery.addJoin(new Join("DeploymentPolicy", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"), (Object)deployConfId, 0));
        selectQuery.addSelectColumn(new Column("AppDeploymentPolicy", "DEPLOYMENT_POLICY_ID"));
        selectQuery.addSelectColumn(new Column("AppDeploymentPolicy", "FORCE_APP_INSTALL"));
        selectQuery.addSelectColumn(new Column("AppDeploymentPolicy", "EMAIL_NOTIFY_END_USER"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final JSONObject jsonObject = new JSONObject();
            final Row row = dataObject.getFirstRow("AppDeploymentPolicy");
            jsonObject.put("silent_install", row.get("FORCE_APP_INSTALL"));
            jsonObject.put("notify_user_via_email", row.get("EMAIL_NOTIFY_END_USER"));
            return jsonObject;
        }
        return null;
    }
    
    public JSONObject getAppsDeploymentPolicyForResourceId(final Long resourceId) {
        final JSONObject appsDeploymentPolicyForResourceID = new JSONObject();
        try {
            final SelectQuery sQuery = this.getQueryForDeploymentPolicyDetails();
            final Criteria resourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceId, 0);
            sQuery.setCriteria(resourceCriteria);
            final SortColumn sourceSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "DEPLOYMENT_CONFIG_SOURCE"), false);
            sQuery.addSortColumn(sourceSortColumn);
            final SortColumn timeSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "ADDED_TIME"), false);
            sQuery.addSortColumn(timeSortColumn);
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (dataSet.next()) {
                final Long profileId = (Long)dataSet.getValue("PROFILE_ID");
                if (!appsDeploymentPolicyForResourceID.has(profileId.toString())) {
                    final JSONObject appDeploymentPolicy = new JSONObject();
                    appDeploymentPolicy.put("FORCE_APP_INSTALL", dataSet.getValue("FORCE_APP_INSTALL"));
                    appDeploymentPolicy.put("DO_NOT_UNINSTALL", dataSet.getValue("DO_NOT_UNINSTALL"));
                    appDeploymentPolicy.put("EMAIL_NOTIFY_END_USER", dataSet.getValue("EMAIL_NOTIFY_END_USER"));
                    appDeploymentPolicy.put("SEND_ENROLLMENT_REQUEST", dataSet.getValue("SEND_ENROLLMENT_REQUEST"));
                    appsDeploymentPolicyForResourceID.put(profileId.toString(), (Object)appDeploymentPolicy);
                }
            }
        }
        catch (final Exception ex) {
            AppDeploymentPolicyImpl.logger.log(Level.SEVERE, "Exception in getAppsDeploymentPolicyForResourceId()", ex);
        }
        return appsDeploymentPolicyForResourceID;
    }
    
    static {
        AppDeploymentPolicyImpl.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
