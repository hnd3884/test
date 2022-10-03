package com.adventnet.sym.webclient.mdm.config;

import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import com.me.mdm.webclient.transformer.TransformerUtil;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AppUpdatePolicyColumnTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnAlias = tableContext.getPropertyName();
        final boolean hasAppMgmtWritePrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_AppMgmt_Write") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_AppMgmt_Write");
        if (columnAlias.equalsIgnoreCase("Profile.PROFILE_ID")) {
            return hasAppMgmtWritePrivillage;
        }
        if (columnAlias.equalsIgnoreCase("checkbox")) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria storeAppPolicyNameCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)"Store Apps - update policy", 1, false);
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)12, 0);
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(storeAppPolicyNameCriteria.and(profileTypeCriteria).and(customerCriteria));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            return hasAppMgmtWritePrivillage && !dataObject.isEmpty();
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnAlias = tableContext.getPropertyName();
        Object data = tableContext.getPropertyValue();
        final Long appUpdatePolicyId = (Long)tableContext.getAssociatedPropertyValue("PROFILE.PROFILE_ID");
        if (columnAlias.equalsIgnoreCase("checkbox")) {
            final JSONObject payloadData = new JSONObject();
            payloadData.put("appUpdatePolicyId", (Object)appUpdatePolicyId.toString());
            final Boolean isStoreAppPolicy = this.isStoreAppPolicy(tableContext);
            payloadData.put("noCheckBox", (Object)isStoreAppPolicy.toString());
            if (isStoreAppPolicy) {
                payloadData.put("isDisabled", (Object)isStoreAppPolicy.toString());
            }
            columnProperties.put("PAYLOAD", payloadData);
        }
        if (columnAlias.equalsIgnoreCase("Profile.PROFILE_NAME") || columnAlias.equalsIgnoreCase("Profile.PROFILE_ID")) {
            final JSONObject payloadData = new JSONObject();
            payloadData.put("appUpdatePolicyId", (Object)appUpdatePolicyId.toString());
            payloadData.put("cellValue", (Object)data.toString());
            payloadData.put("isStoreAppPolicy", (Object)this.isStoreAppPolicy(tableContext).toString());
            payloadData.put("isStoreAppPolicyEnabled", (Object)this.isStoreAppPolicyEnabled());
            payloadData.put("isUserInRole", ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("All_Managed_Mobile_Devices"));
            columnProperties.put("PAYLOAD", payloadData);
        }
        if (columnAlias.equalsIgnoreCase("APP_COUNT_IN_POLICY_COL")) {
            final Boolean isAllApps = (Boolean)tableContext.getAssociatedPropertyValue("AutoAppUpdatePackageConfig.ALL_APPS");
            data = tableContext.getAssociatedPropertyValue("APP_COUNT_IN_POLICY_COL");
            final JSONObject payloadJSON = new JSONObject();
            if (!isAllApps) {
                payloadJSON.put("cellValue", (data == null) ? Integer.valueOf(0) : data);
            }
            else {
                final Boolean inclusionFlag = (Boolean)tableContext.getAssociatedPropertyValue("AutoAppUpdatePackageConfig.INCLUSION_FLAG");
                Integer totalAppCount = (Integer)TransformerUtil.getPreValuesForTransformer(tableContext.getViewContext(), "TOTAL_APP_COUNT");
                if (!inclusionFlag) {
                    data = ((data == null) ? Integer.valueOf(0) : data);
                    totalAppCount -= (int)data;
                }
                payloadJSON.put("cellValue", (Object)totalAppCount.toString());
            }
            payloadJSON.put("appUpdatePolicyId", (Object)appUpdatePolicyId.toString());
            columnProperties.put("PAYLOAD", payloadJSON);
        }
        if (columnAlias.equalsIgnoreCase("ASSOCIATED_GROUP_COUNT_COL")) {
            data = tableContext.getAssociatedPropertyValue("ASSOCIATED_GROUP_COUNT_COL");
            final JSONObject payloadJSON2 = new JSONObject();
            payloadJSON2.put("appUpdatePolicyId", (Object)appUpdatePolicyId.toString());
            payloadJSON2.put("cellValue", (Object)((data == null) ? Integer.valueOf(0) : data.toString()));
            columnProperties.put("PAYLOAD", payloadJSON2);
        }
    }
    
    private Boolean isStoreAppPolicy(final TransformerContext transformerContext) {
        final String profileName = (String)transformerContext.getAssociatedPropertyValue("Profile.PROFILE_NAME");
        return profileName.equalsIgnoreCase("Store Apps - update policy");
    }
    
    private Boolean isStoreAppPolicyEnabled() throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateConfigToCollection", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final Criteria customerCriteria = new Criteria(Column.getColumn("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria storeAppPolicyCriteria = new Criteria(Column.getColumn("AutoAppUpdateConfigToCollection", "COLLECTION_ID"), (Object)null, 0);
        selectQuery.setCriteria(customerCriteria.and(storeAppPolicyCriteria));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        return !dataObject.isEmpty();
    }
}
