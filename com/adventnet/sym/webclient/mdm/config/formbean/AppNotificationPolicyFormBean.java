package com.adventnet.sym.webclient.mdm.config.formbean;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.payload.PayloadException;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.server.config.PayloadProperty;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class AppNotificationPolicyFormBean extends MDMDefaultFormBean
{
    private static JSONUtil jsonUtil;
    
    private void addAppsToNotificationPolicy(final Object notificationPolicyId, final List<Long> appsToBeAdded, final DataObject dataObject) throws Exception {
        for (final Long appGroupId : appsToBeAdded) {
            final Row notificationPolicyToAppRelRow = new Row("MdmAppNotificationPolicyToAppRel");
            notificationPolicyToAppRelRow.set("MDM_APP_NOTIFICATION_POLICY_ID", notificationPolicyId);
            notificationPolicyToAppRelRow.set("APP_GROUP_ID", (Object)appGroupId);
            dataObject.addRow(notificationPolicyToAppRelRow);
        }
    }
    
    private Row createPolicyRow(final JSONObject notificationPolicyJSON) {
        final Row notificationPolicyRow = new Row("MdmAppNotificationPolicy");
        final List notificationPolicyColumns = notificationPolicyRow.getColumns();
        for (int j = 0; j < notificationPolicyColumns.size(); ++j) {
            final PayloadProperty property = new PayloadProperty();
            property.name = notificationPolicyColumns.get(j);
            property.value = notificationPolicyJSON.opt(property.name);
            if (property.name != null && property.value != null) {
                if (!property.name.equals("MDM_APP_NOTIFICATION_POLICY_ID")) {
                    notificationPolicyRow.set(property.name, property.value);
                }
            }
        }
        return notificationPolicyRow;
    }
    
    private void createPolicyAndAddApps(final JSONObject notificationPolicyJSON, final Object configDataItemId, final DataObject dataObject) throws Exception {
        final Row policyRow = this.createPolicyRow(notificationPolicyJSON);
        dataObject.addRow(policyRow);
        final Row configToPolicyRel = new Row("MdmAppNotificationPolicyToConfigRel");
        configToPolicyRel.set("CONFIG_DATA_ITEM_ID", configDataItemId);
        configToPolicyRel.set("MDM_APP_NOTIFICATION_POLICY_ID", policyRow.get("MDM_APP_NOTIFICATION_POLICY_ID"));
        dataObject.addRow(configToPolicyRel);
        this.addAppsToNotificationPolicy(policyRow.get("MDM_APP_NOTIFICATION_POLICY_ID"), AppNotificationPolicyFormBean.jsonUtil.convertLongJSONArrayTOList(notificationPolicyJSON.optJSONArray("APPS")), dataObject);
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException, PayloadException {
        final JSONObject dynaForm = dynaActionForm[0];
        final JSONArray notificationPolicyJSONArray = dynaForm.optJSONArray("APP_NOTIFICATION_POLICY");
        Object configDataItemId = dynaForm.optLong("CONFIG_DATA_ITEM_ID");
        final boolean isUpdate = configDataItemId != null && (long)configDataItemId > 0L;
        try {
            if (isUpdate) {
                dataObject.deleteRows("MdmAppNotificationPolicy", (Criteria)null);
            }
            else {
                this.insertConfigDataItem(dynaForm, dataObject, 0);
                configDataItemId = dataObject.getRow("ConfigDataItem").get("CONFIG_DATA_ITEM_ID");
            }
            for (int i = 0; i < notificationPolicyJSONArray.length(); ++i) {
                final JSONObject notificationPolicyJSON = notificationPolicyJSONArray.optJSONObject(i);
                final JSONArray notificationPolicyAppsJSONArray = notificationPolicyJSON.optJSONArray("APPS");
                this.createPolicyAndAddApps(notificationPolicyJSON, configDataItemId, dataObject);
            }
        }
        catch (final Exception exp) {
            AppNotificationPolicyFormBean.logger.log(Level.SEVERE, "Exception while saving the config data item DO", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        final Iterator configDataItemItr = configDOFromDB.getRows("ConfigDataItem");
        while (configDataItemItr.hasNext()) {
            final Row configDataItemRow = configDataItemItr.next();
            final Row cloneConfigDataItemRow = this.insertClonedConfigDataItem(configID, configDOFromDB, cloneConfigDO);
            if (cloneConfigDataItemRow != null) {
                final Iterator notificationPolicyRows = configDOFromDB.getRows("MdmAppNotificationPolicy");
                while (notificationPolicyRows.hasNext()) {
                    final Row notificationPolicyRow = notificationPolicyRows.next();
                    final Row cloneNotificationPolicyRow = new Row("MdmAppNotificationPolicy");
                    final Row clonePolicyToConfigRow = new Row("MdmAppNotificationPolicyToConfigRel");
                    clonePolicyToConfigRow.set("CONFIG_DATA_ITEM_ID", cloneConfigDataItemRow.get("CONFIG_DATA_ITEM_ID"));
                    clonePolicyToConfigRow.set("MDM_APP_NOTIFICATION_POLICY_ID", cloneNotificationPolicyRow.get("MDM_APP_NOTIFICATION_POLICY_ID"));
                    cloneConfigDO.addRow(clonePolicyToConfigRow);
                    final List columns = notificationPolicyRow.getColumns();
                    for (int i = 0; i < columns.size(); ++i) {
                        final String columnName = columns.get(i);
                        if (!columnName.equals("MDM_APP_NOTIFICATION_POLICY_ID")) {
                            cloneNotificationPolicyRow.set(columnName, notificationPolicyRow.get(columnName));
                        }
                    }
                    cloneConfigDO.addRow(cloneNotificationPolicyRow);
                    final Criteria notificationPolicyCriteria = new Criteria(new Column("MdmAppNotificationPolicyToAppRel", "MDM_APP_NOTIFICATION_POLICY_ID"), notificationPolicyRow.get("MDM_APP_NOTIFICATION_POLICY_ID"), 0);
                    final Iterator appRelItr = configDOFromDB.getRows("MdmAppNotificationPolicyToAppRel", notificationPolicyCriteria);
                    while (appRelItr.hasNext()) {
                        final Row appRelRow = appRelItr.next();
                        final Row cloneAppRelRow = new Row("MdmAppNotificationPolicyToAppRel");
                        cloneAppRelRow.set("MDM_APP_NOTIFICATION_POLICY_ID", cloneNotificationPolicyRow.get("MDM_APP_NOTIFICATION_POLICY_ID"));
                        cloneAppRelRow.set("APP_GROUP_ID", appRelRow.get("APP_GROUP_ID"));
                        cloneConfigDO.addRow(cloneAppRelRow);
                    }
                }
            }
        }
    }
    
    static {
        AppNotificationPolicyFormBean.jsonUtil = new JSONUtil();
    }
}
