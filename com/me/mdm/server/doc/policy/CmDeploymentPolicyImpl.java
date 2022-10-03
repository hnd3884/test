package com.me.mdm.server.doc.policy;

import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.framework.server.util.SyMUtil;
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
import com.me.mdm.server.deployment.DeploymentPolicyDataInterface;

public class CmDeploymentPolicyImpl implements DeploymentPolicyDataInterface
{
    @Override
    public void addOrUpdateDeploymentPolicy(final Long deploymentPolicyId, final JSONObject dataJSON) throws JSONException, DataAccessException {
        final Integer autoDownload = dataJSON.optInt("AUTO_DOWNLOAD", 0);
        final Integer documentShare = dataJSON.optInt("DOCUMENT_SHARE", 0);
        final Integer clipRestrict = dataJSON.optInt("CLIP_RESTRICT", 0);
        final Integer documentDelete = dataJSON.optInt("DOCUMENT_DELETE", 0);
        final Integer screenShotRestrict = dataJSON.optInt("SCREENSHOT_RESTRICT", 0);
        final Integer requirePassword = dataJSON.optInt("REQUIRE_PASSWORD", 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CMDeploymentPolicy"));
        final Criteria depPolicyIdCriteria = new Criteria(new Column("CMDeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)deploymentPolicyId, 0);
        selectQuery.setCriteria(depPolicyIdCriteria);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject doObj = DataAccess.get(selectQuery);
        if (doObj.isEmpty()) {
            final Row row = new Row("CMDeploymentPolicy");
            row.set("DEPLOYMENT_POLICY_ID", (Object)deploymentPolicyId);
            row.set("AUTO_DOWNLOAD", (Object)autoDownload);
            row.set("DOCUMENT_SHARE", (Object)documentShare);
            row.set("CLIP_RESTRICT", (Object)clipRestrict);
            row.set("DOCUMENT_DELETE", (Object)documentDelete);
            row.set("SCREENSHOT_RESTRICT", (Object)screenShotRestrict);
            row.set("REQUIRE_PASSWORD", (Object)requirePassword);
            doObj.addRow(row);
            DataAccess.add(doObj);
        }
        else {
            final Row row = doObj.getRow("CMDeploymentPolicy");
            row.set("AUTO_DOWNLOAD", (Object)autoDownload);
            row.set("DOCUMENT_SHARE", (Object)documentShare);
            row.set("CLIP_RESTRICT", (Object)clipRestrict);
            row.set("DOCUMENT_DELETE", (Object)documentDelete);
            row.set("SCREENSHOT_RESTRICT", (Object)screenShotRestrict);
            row.set("REQUIRE_PASSWORD", (Object)requirePassword);
            doObj.updateRow(row);
            DataAccess.update(doObj);
        }
    }
    
    @Override
    public JSONObject getDeploymentDataPolicy(final Long deploymentPolicyId) throws JSONException, DataAccessException {
        final JSONObject dataJSON = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentPolicy"));
        final Join cmDeployJoin = new Join("DeploymentPolicy", "CMDeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2);
        final Join configDeployJoin = new Join("DeploymentPolicy", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Join aaaUserJoin = new Join("DeploymentConfig", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, 2);
        selectQuery.addJoin(cmDeployJoin);
        selectQuery.addJoin(configDeployJoin);
        selectQuery.addJoin(aaaUserJoin);
        final Criteria depPolicyIdCriteria = new Criteria(new Column("CMDeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)deploymentPolicyId, 0);
        selectQuery.setCriteria(depPolicyIdCriteria);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = SyMUtil.getPersistence().get(selectQuery);
        if (!dO.isEmpty()) {
            final Row depConfig = dO.getRow("DeploymentConfig");
            final Row cmPolicyRow = dO.getRow("CMDeploymentPolicy");
            dataJSON.put("DEPLOYMENT_POLICY_ID", cmPolicyRow.get("DEPLOYMENT_POLICY_ID"));
            dataJSON.put("AUTO_DOWNLOAD", cmPolicyRow.get("AUTO_DOWNLOAD"));
            dataJSON.put("DOCUMENT_DELETE", cmPolicyRow.get("DOCUMENT_DELETE"));
            dataJSON.put("DOCUMENT_SHARE", cmPolicyRow.get("DOCUMENT_SHARE"));
            dataJSON.put("CLIP_RESTRICT", cmPolicyRow.get("CLIP_RESTRICT"));
            dataJSON.put("SCREENSHOT_RESTRICT", cmPolicyRow.get("SCREENSHOT_RESTRICT"));
            dataJSON.put("REQUIRE_PASSWORD", cmPolicyRow.get("REQUIRE_PASSWORD"));
            dataJSON.put("DEPLOYMENT_CONFIG_NAME", depConfig.get("DEPLOYMENT_CONFIG_NAME"));
            dataJSON.put("DEPLOYMENT_CONFIG_DESCRIPTION", depConfig.get("DEPLOYMENT_CONFIG_DESCRIPTION"));
            dataJSON.put("CREATION_TIME", depConfig.get("CREATION_TIME"));
            final Long userId = (Long)depConfig.get("CREATED_BY");
            if (userId != null) {
                final Row aaaUserRow = dO.getRow("AaaUser", new Criteria(new Column("AaaUser", "USER_ID"), (Object)userId, 0));
                if (aaaUserRow != null) {
                    dataJSON.put("CREATED_BY", aaaUserRow.get("FIRST_NAME"));
                }
            }
        }
        return dataJSON;
    }
    
    @Override
    public JSONObject getEffectiveDeploymentDataPolicy(final Long resourceId, final Long profileId) throws JSONException, DataAccessException {
        final JSONObject deploymentPolicyJSON = new JSONObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMResourceToDeploymentConfigs"));
        final Join depConfigJoin = new Join("MDMResourceToDeploymentConfigs", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Join depPolicyJoin = new Join("MDMResourceToDeploymentConfigs", "DeploymentPolicy", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Join appPolicyJoin = new Join("DeploymentPolicy", "CMDeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2);
        final Criteria resourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria profileCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileId, 0);
        sQuery.addJoin(depConfigJoin);
        sQuery.addJoin(depPolicyJoin);
        sQuery.addJoin(appPolicyJoin);
        sQuery.setCriteria(resourceCriteria.and(profileCriteria));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final SortColumn sourceSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "DEPLOYMENT_CONFIG_SOURCE"), false);
        sQuery.addSortColumn(sourceSortColumn);
        final SortColumn timeSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "ADDED_TIME"), false);
        sQuery.addSortColumn(timeSortColumn);
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        if (dO != null && !dO.isEmpty()) {
            final Row row = dO.getRow("CMDeploymentPolicy");
            if (row != null) {
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_ID", row.get("DEPLOYMENT_POLICY_ID"));
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_TYPE", (Object)"APP_DEPLOYMENT_POLICY");
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_TYPE_ID", 301);
                final JSONObject policyDetails = new JSONObject();
                policyDetails.put("DEPLOYMENT_POLICY_ID", row.get("DEPLOYMENT_POLICY_ID"));
                policyDetails.put("AUTO_DOWNLOAD", row.get("AUTO_DOWNLOAD"));
                policyDetails.put("DOCUMENT_SHARE", row.get("DOCUMENT_SHARE"));
                policyDetails.put("DOCUMENT_DELETE", row.get("DOCUMENT_DELETE"));
                policyDetails.put("SCREENSHOT_RESTRICT", row.get("SCREENSHOT_RESTRICT"));
                policyDetails.put("REQUIRE_PASSWORD", row.get("REQUIRE_PASSWORD"));
                policyDetails.put("CLIP_RESTRICT", row.get("CLIP_RESTRICT"));
                deploymentPolicyJSON.put("PolicyDetails", (Object)policyDetails);
            }
        }
        return deploymentPolicyJSON;
    }
}
