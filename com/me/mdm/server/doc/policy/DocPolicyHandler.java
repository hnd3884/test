package com.me.mdm.server.doc.policy;

import com.me.mdm.server.doc.DocSummaryHandler;
import java.util.Iterator;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.me.mdm.server.deployment.DeplymentConfigHandler;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DocPolicyHandler
{
    static DocPolicyHandler docPolicyHandler;
    public static final Logger LOGGER;
    
    public static DocPolicyHandler getInstance() {
        if (DocPolicyHandler.docPolicyHandler == null) {
            DocPolicyHandler.docPolicyHandler = new DocPolicyHandler();
        }
        return DocPolicyHandler.docPolicyHandler;
    }
    
    private JSONObject getDepData(final Long custId, final Long userId, final Long configId, final String configName, final Long policyId, final JSONObject cmDepDetails) {
        final Integer clipRestrict = cmDepDetails.optInt("CLIP_RESTRICT", 0);
        final Integer autoDownload = cmDepDetails.optInt("AUTO_DOWNLOAD", 0);
        final Integer documentShare = cmDepDetails.optInt("DOCUMENT_SHARE", 0);
        final Integer documentDelete = cmDepDetails.optInt("DOCUMENT_DELETE", 0);
        final Integer requirePassword = cmDepDetails.optInt("REQUIRE_PASSWORD", 0);
        final Integer screenShotRestrict = cmDepDetails.optInt("SCREENSHOT_RESTRICT", 0);
        final String description = cmDepDetails.optString("DEPLOYMENT_CONFIG_DESCRIPTION", "--");
        final JSONObject depPolicyDetails = new JSONObject();
        depPolicyDetails.put("AUTO_DOWNLOAD", (Object)autoDownload);
        depPolicyDetails.put("CLIP_RESTRICT", (Object)clipRestrict);
        depPolicyDetails.put("DOCUMENT_SHARE", (Object)documentShare);
        depPolicyDetails.put("DOCUMENT_DELETE", (Object)documentDelete);
        depPolicyDetails.put("REQUIRE_PASSWORD", (Object)requirePassword);
        depPolicyDetails.put("SCREENSHOT_RESTRICT", (Object)screenShotRestrict);
        final JSONObject depPolicyData = new JSONObject();
        if (policyId != null) {
            depPolicyData.put("DEPLOYMENT_POLICY_ID", (Object)policyId);
        }
        depPolicyData.put("PolicyDetails", (Object)depPolicyDetails);
        depPolicyData.put("DEPLOYMENT_POLICY_TYPE", (Object)"CM_DEPLOYMENT_POLICY");
        depPolicyData.put("DEPLOYMENT_POLICY_TYPE_ID", 401);
        final JSONObject depData = new JSONObject();
        depData.put("DeploymentPolicy", (Object)depPolicyData);
        depData.put("CUSTOMER_ID", (Object)custId);
        depData.put("LAST_MODIFIED_BY", (Object)userId);
        if (configId != null) {
            depData.put("DEPLOYMENT_CONFIG_ID", (Object)configId);
        }
        depData.put("DEPLOYMENT_CONFIG_NAME", (Object)configName);
        depData.put("DEPLOYMENT_CONFIG_DESCRIPTION", (Object)description);
        return depData;
    }
    
    public Long addOrUpdateDeploymentPolicy(Long policyId, final Long custId, final Long userId, final JSONObject cmDepDetails) throws Exception {
        final String configName = cmDepDetails.optString("DEPLOYMENT_CONFIG_NAME", "--");
        DataObject dataObject = (DataObject)new WritableDataObject();
        if (policyId != null) {
            final Criteria policyIdCriteria = new Criteria(new Column("DeploymentConfig", "CUSTOMER_ID"), (Object)custId, 0).and(new Criteria(new Column("CMDeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)policyId, 0));
            dataObject = this.getPolicyDataObject(policyIdCriteria);
        }
        final Criteria configNameCriteria = new Criteria(new Column("DeploymentConfig", "DEPLOYMENT_CONFIG_NAME"), (Object)configName, 2);
        final Criteria policyTypeCriteria = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_TYPE_ID"), (Object)401, 0);
        final DataObject configNameDataObject = this.getPolicyDataObject(configNameCriteria.and(policyTypeCriteria));
        if (!configNameDataObject.isEmpty()) {
            final List policyIdList = DBUtil.getColumnValuesAsList(configNameDataObject.getRows("DeploymentPolicy"), "DEPLOYMENT_POLICY_ID");
            if (!policyIdList.contains(policyId) || policyId == null) {
                DocPolicyHandler.LOGGER.log(Level.INFO, "Document Policy Name already Exists");
                throw new APIHTTPException("DOC0008", new Object[0]);
            }
        }
        if (dataObject.isEmpty() && policyId != null) {
            DocPolicyHandler.LOGGER.log(Level.INFO, "Policy id {0} not found ", new Object[] { policyId });
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        if (dataObject.isEmpty() && policyId == null) {
            final JSONObject depData = this.getDepData(custId, userId, null, configName, null, cmDepDetails);
            DocPolicyHandler.LOGGER.log(Level.INFO, "No default entry available for {0}, so creating new", new Object[] { cmDepDetails });
            policyId = new DeplymentConfigHandler().persistSingleDeploymentConfig(depData);
        }
        else {
            final Row row = dataObject.getRow("DeploymentConfig");
            final Long configId = (Long)row.get("DEPLOYMENT_CONFIG_ID");
            final JSONObject depData2 = this.getDepData(custId, userId, configId, configName, policyId, cmDepDetails);
            DocPolicyHandler.LOGGER.log(Level.INFO, "Updating existing value {0}", new Object[] { cmDepDetails });
            new DeplymentConfigHandler().persistSingleDeploymentConfig(depData2);
            DocMgmtDataHandler.getInstance().updateDeviceForPolicyId(policyId);
        }
        return policyId;
    }
    
    private DataObject getPolicyDataObject(final Criteria criteria) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentPolicy"));
        final Join cmDeployJoin = new Join("DeploymentPolicy", "CMDeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2);
        final Join configDeployJoin = new Join("DeploymentPolicy", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        sQuery.addJoin(cmDeployJoin);
        sQuery.addJoin(configDeployJoin);
        if (criteria != null) {
            sQuery.setCriteria(criteria);
        }
        sQuery.addSelectColumn(new Column((String)null, "*"));
        return DataAccess.get(sQuery);
    }
    
    public JSONObject getCmDeploymentPolicyById(final Long deploymentPolicyId, final Long custId, final int deploymentPolicyType) throws DataAccessException {
        final JSONObject dataJSON = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentPolicy"));
        final Join cmDeployJoin = new Join("DeploymentPolicy", "CMDeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2);
        final Join configDeployJoin = new Join("DeploymentPolicy", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Join aaaUserJoin = new Join("DeploymentConfig", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, 2);
        selectQuery.addJoin(cmDeployJoin);
        selectQuery.addJoin(configDeployJoin);
        selectQuery.addJoin(aaaUserJoin);
        final Criteria depPolicyIdCriteria = new Criteria(new Column("CMDeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)deploymentPolicyId, 0);
        final Criteria customerIdCriteria = new Criteria(new Column("DeploymentConfig", "CUSTOMER_ID"), (Object)custId, 0);
        final Criteria policyTypeCriteria = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_TYPE_ID"), (Object)deploymentPolicyType, 0);
        selectQuery.setCriteria(depPolicyIdCriteria.and(customerIdCriteria).and(policyTypeCriteria));
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
    
    public JSONArray getCmDeploymentPolicy(final Long custId) throws Exception {
        final JSONArray objArray = new JSONArray();
        final Criteria custCriteria = new Criteria(new Column("DeploymentConfig", "CUSTOMER_ID"), (Object)custId, 0);
        final DataObject dataObject = this.getPolicyDataObject(custCriteria);
        if (!dataObject.isEmpty()) {
            final Iterator deprows = dataObject.getRows("DeploymentPolicy");
            while (deprows.hasNext()) {
                final Row depRow = deprows.next();
                final Long depId = (Long)depRow.get("DEPLOYMENT_POLICY_ID");
                final Long confId = (Long)depRow.get("DEPLOYMENT_CONFIG_ID");
                final Row row = dataObject.getRow("CMDeploymentPolicy", new Criteria(new Column("CMDeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)depId, 0));
                final Row depConfRow = dataObject.getRow("DeploymentConfig", new Criteria(new Column("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"), (Object)confId, 0));
                if (row != null) {
                    final JSONObject obj = new JSONObject();
                    obj.put("AUTO_DOWNLOAD", row.get("AUTO_DOWNLOAD"));
                    obj.put("CLIP_RESTRICT", row.get("CLIP_RESTRICT"));
                    obj.put("DOCUMENT_SHARE", row.get("DOCUMENT_SHARE"));
                    obj.put("DOCUMENT_DELETE", row.get("DOCUMENT_DELETE"));
                    obj.put("config_name", depConfRow.get("DEPLOYMENT_CONFIG_NAME"));
                    obj.put("REQUIRE_PASSWORD", row.get("REQUIRE_PASSWORD"));
                    obj.put("SCREENSHOT_RESTRICT", row.get("SCREENSHOT_RESTRICT"));
                    obj.put("description", depConfRow.get("DEPLOYMENT_CONFIG_DESCRIPTION"));
                    obj.put("DEPLOYMENT_POLICY_ID", row.get("DEPLOYMENT_POLICY_ID"));
                    objArray.put((Object)obj);
                }
            }
        }
        return objArray;
    }
    
    public JSONObject getCmDeploymentPolicyForPolicyIds(final List<Long> policyIds) throws Exception {
        final JSONObject obj = new JSONObject();
        final Criteria policyIdCriteria = new Criteria(new Column("CMDeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)policyIds.toArray(), 8);
        final DataObject dataObject = this.getPolicyDataObject(policyIdCriteria);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("CMDeploymentPolicy");
            final Long depPolicyId = (Long)row.get("DEPLOYMENT_POLICY_ID");
            final Row depRow = dataObject.getRow("DeploymentPolicy", new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)depPolicyId, 0));
            final Long depConfigId = (Long)depRow.get("DEPLOYMENT_CONFIG_ID");
            final Row depConfigRow = dataObject.getRow("DeploymentConfig", new Criteria(new Column("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"), (Object)depConfigId, 0));
            obj.put("DEPLOYMENT_POLICY_ID", row.get("DEPLOYMENT_POLICY_ID"));
            obj.put("AUTO_DOWNLOAD", row.get("AUTO_DOWNLOAD"));
            obj.put("DOCUMENT_SHARE", row.get("DOCUMENT_SHARE"));
            obj.put("CLIP_RESTRICT", row.get("CLIP_RESTRICT"));
            obj.put("DOCUMENT_DELETE", row.get("DOCUMENT_DELETE"));
            obj.put("SCREENSHOT_RESTRICT", row.get("SCREENSHOT_RESTRICT"));
            obj.put("REQUIRE_PASSWORD", row.get("REQUIRE_PASSWORD"));
            obj.put("DEPLOYMENT_POLICY_ID", depRow.get("DEPLOYMENT_POLICY_ID"));
            obj.put("config_name", depConfigRow.get("DEPLOYMENT_CONFIG_NAME"));
            obj.put("description", depConfigRow.get("DEPLOYMENT_CONFIG_DESCRIPTION"));
        }
        return obj;
    }
    
    public void deleteDeploymentById(final Long deploymentPolicyId, final Long custId) throws Exception {
        final Join cmDeployJoin = new Join("DeploymentPolicy", "CMDeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2);
        final Join configDeployJoin = new Join("DeploymentPolicy", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Criteria depTypeCriteria = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_TYPE_ID"), (Object)401, 0);
        final Criteria customerCriteria = new Criteria(new Column("DeploymentConfig", "CUSTOMER_ID"), (Object)custId, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentPolicy"));
        selectQuery.addJoin(cmDeployJoin);
        selectQuery.addJoin(configDeployJoin);
        final Criteria depConfIdCriteria = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)deploymentPolicyId, 0);
        selectQuery.setCriteria(depTypeCriteria.and(customerCriteria).and(depConfIdCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = SyMUtil.getPersistence().get(selectQuery);
        if (!dO.isEmpty()) {
            DocMgmtDataHandler.getInstance().updateDeviceForPolicyId(deploymentPolicyId);
            final Row deploymentPolicyRow = dO.getRow("DeploymentPolicy");
            final Row deploymentConfigRow = dO.getRow("DeploymentConfig");
            final Row cmPolicyRow = dO.getRow("CMDeploymentPolicy");
            dO.deleteRow(deploymentConfigRow);
            dO.deleteRow(deploymentPolicyRow);
            dO.deleteRow(cmPolicyRow);
            SyMUtil.getPersistence().update(dO);
            DocSummaryHandler.getInstance().reviseDocSummary(null);
            return;
        }
        DocPolicyHandler.LOGGER.log(Level.SEVERE, "POLICY id {0} for delete does not exists", deploymentPolicyId);
        throw new APIHTTPException("DOC0007", new Object[] { deploymentPolicyId });
    }
    
    static {
        DocPolicyHandler.docPolicyHandler = null;
        LOGGER = Logger.getLogger("MDMDocLogger");
    }
}
