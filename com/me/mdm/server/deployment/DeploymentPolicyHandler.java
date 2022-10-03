package com.me.mdm.server.deployment;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DeploymentPolicyHandler
{
    static final Logger LOGGER;
    public static final String POLICY_DETAILS = "PolicyDetails";
    
    public Long persistDeploymentPolicies(final JSONObject dataJSON, final Long deploymentConfigId) throws Exception {
        final Long depPolicyId = this.addOrUpdateDeploymentPolicy(dataJSON, deploymentConfigId);
        final Integer policyTypeId = dataJSON.getInt("DEPLOYMENT_POLICY_TYPE_ID");
        final DeploymentPolicyDataInterface deploymentPolicyDataHandler = (DeploymentPolicyDataInterface)Class.forName(DeploymentConfigTypeConstants.CLASS_MAP.get(policyTypeId)).newInstance();
        DeploymentPolicyHandler.LOGGER.log(Level.INFO, "Invoking {0} for adding/updating Deployment policy {1}", new Object[] { DeploymentConfigTypeConstants.CLASS_MAP.get(policyTypeId), dataJSON.getJSONObject("PolicyDetails") });
        deploymentPolicyDataHandler.addOrUpdateDeploymentPolicy(depPolicyId, dataJSON.getJSONObject("PolicyDetails"));
        return depPolicyId;
    }
    
    public JSONObject getDeploymentPolicyDetails(final Long depolymnetPolicyId, final int deploymentConfigType) throws Exception {
        final DeploymentPolicyDataInterface deploymentPolicyDataHandler = (DeploymentPolicyDataInterface)Class.forName(DeploymentConfigTypeConstants.CLASS_MAP.get(deploymentConfigType)).newInstance();
        return deploymentPolicyDataHandler.getDeploymentDataPolicy(depolymnetPolicyId);
    }
    
    public JSONObject getEffectiveDeploymentPolicyDetails(final Long resourceId, final Long profileId, final int deploymentConfigType) throws Exception {
        final DeploymentPolicyDataInterface deploymentPolicyDataHandler = (DeploymentPolicyDataInterface)Class.forName(DeploymentConfigTypeConstants.CLASS_MAP.get(deploymentConfigType)).newInstance();
        return deploymentPolicyDataHandler.getEffectiveDeploymentDataPolicy(resourceId, profileId);
    }
    
    private Long addOrUpdateDeploymentPolicy(final JSONObject dataJSON, final Long deploymentConfigId) throws DataAccessException {
        try {
            final String policyType = String.valueOf(dataJSON.get("DEPLOYMENT_POLICY_TYPE"));
            final Integer policyTypeId = dataJSON.getInt("DEPLOYMENT_POLICY_TYPE_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentPolicy"));
            final Criteria depConfIdCriteria = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_CONFIG_ID"), (Object)deploymentConfigId, 0);
            final Criteria depPolicyTypeIdCriteria = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_TYPE_ID"), (Object)policyTypeId, 0);
            selectQuery.setCriteria(depConfIdCriteria.and(depPolicyTypeIdCriteria));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            DataObject dO = DataAccess.get(selectQuery);
            if (dO.isEmpty()) {
                final Row row = new Row("DeploymentPolicy");
                row.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                row.set("DEPLOYMENT_POLICY_TYPE", (Object)policyType);
                row.set("DEPLOYMENT_POLICY_TYPE_ID", (Object)policyTypeId);
                dO.addRow(row);
            }
            else {
                final Row row = dO.getRow("DeploymentPolicy");
                row.set("DEPLOYMENT_POLICY_TYPE", (Object)policyType);
                row.set("DEPLOYMENT_POLICY_TYPE_ID", (Object)policyTypeId);
                dO.updateRow(row);
            }
            dO = DataAccess.update(dO);
            final Long deploymentPolicyId = (Long)dO.getRow("DeploymentPolicy").get("DEPLOYMENT_POLICY_ID");
            DeploymentPolicyHandler.LOGGER.log(Level.INFO, "Deployment Policy Update for {0}, Data : {1}", new Object[] { deploymentConfigId, dataJSON });
            return deploymentPolicyId;
        }
        catch (final JSONException ex) {
            DeploymentPolicyHandler.LOGGER.log(Level.SEVERE, null, (Throwable)ex);
            throw new IllegalArgumentException("Mandatory keys missing " + ex.getMessage(), ex.getCause());
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
