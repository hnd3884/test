package com.adventnet.sym.webclient.mdm.config;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMApptoGroupsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (columnalias.equalsIgnoreCase("Profile.PROFILE_NAME") || columnalias.equalsIgnoreCase("next_execution_time")) {
            return MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableScheduleAppUpdates");
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        Object data = tableContext.getPropertyValue();
        final String columnAlias = tableContext.getPropertyName();
        final Long resourceId = (Long)tableContext.getAssociatedPropertyValue("CustomGroup.RESOURCE_ID");
        final Boolean isUpdateAvailable = (Boolean)tableContext.getAssociatedPropertyValue("MdAppCatalogToGroup.IS_UPDATE_AVAILABLE");
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = Boolean.FALSE;
        if (reportType != 4) {
            isExport = Boolean.TRUE;
        }
        if (columnAlias.equals("Resource.NAME")) {
            JSONObject payload = columnProperties.get("PAYLOAD");
            if (payload == null) {
                payload = new JSONObject();
            }
            payload.put("groupId", (Object)resourceId);
            columnProperties.put("PAYLOAD", payload);
            columnProperties.put("VALUE", data);
        }
        if (columnAlias.equals("RecentProfileForResource.MARKED_FOR_DELETE")) {
            String statusStr = "";
            if (data != null) {
                if (data == Boolean.TRUE) {
                    statusStr = I18N.getMsg("dc.mdm.group.view.app_removal", new Object[0]);
                }
                else {
                    statusStr = I18N.getMsg("dc.mdm.group.view.app_installation", new Object[0]);
                }
            }
            columnProperties.put("VALUE", statusStr);
        }
        if (columnAlias.equals("GroupToProfileHistory.COLLECTION_STATUS")) {
            final String statusLabel = (String)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.STATUS_LABEL");
            if (!isUpdateAvailable) {
                final String statusStr2 = I18N.getMsg(statusLabel, new Object[0]);
                columnProperties.put("VALUE", statusStr2);
            }
            else {
                final String statusStr2 = I18N.getMsg("dc.common.YET_TO_UPDATE", new Object[0]);
                columnProperties.put("VALUE", statusStr2);
            }
        }
        if (columnAlias.equals("CollnToResources.REMARKS")) {
            if (!isUpdateAvailable) {
                final Integer statusID = (Integer)tableContext.getAssociatedPropertyValue("GroupToProfileHistory.COLLECTION_STATUS");
                final Boolean isErr = statusID == 7 || statusID == 11 || statusID == 8;
                TransformerUtil.renderRemarksAsText(tableContext, columnProperties, (String)data, isErr, isExport);
                if (columnProperties.containsKey("PAYLOAD")) {
                    final JSONObject jsonObject = columnProperties.get("PAYLOAD");
                    if (jsonObject.has("VALUE")) {
                        final String value = jsonObject.getString("VALUE");
                        if (value.contains("@@@<l>")) {
                            jsonObject.put("VALUE", (Object)MDMUtil.replaceProductUrlLoaderValuesinText(value, "AppDistribtionPage"));
                        }
                    }
                    if (jsonObject.has("READKB")) {
                        final String readKB = jsonObject.getString("READKB");
                        if (readKB != null) {
                            jsonObject.put("READKB", (Object)MDMUtil.replaceProductUrlLoaderValuesinText(readKB, "AppDistribtionPage"));
                        }
                    }
                }
            }
            else {
                columnProperties.put("VALUE", I18N.getMsg("mdm.apps.status.dist_ondemand", new Object[0]));
            }
        }
        if (columnAlias.equals("ResourceToProfileSummary.MEMBER_COUNT")) {
            if (data == null || data != "--") {
                data = 0;
            }
            columnProperties.put("VALUE", data);
        }
        if (columnAlias.equalsIgnoreCase("PublishedAppDetails.APP_VERSION")) {
            if (data == null || (data != null && data.equals(""))) {
                data = "--";
            }
            data = AppsUtil.getValidVersion(data.toString());
            final Long groupId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            if (reportType == 4) {
                final JSONObject payloadJson = new JSONObject();
                final Integer associatedAppStatus = (Integer)tableContext.getAssociatedPropertyValue("MdAppCatalogToGroup.APPROVED_VERSION_STATUS");
                payloadJson.put("updateApp", this.isAppUpgradeAvailable(tableContext));
                payloadJson.put("groupId", (Object)groupId.toString());
                payloadJson.put("version", data);
                payloadJson.put("approvedVersionStatus", (associatedAppStatus == null) ? 1 : ((int)associatedAppStatus));
                columnProperties.put("PAYLOAD", payloadJson);
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
        if (columnAlias.equalsIgnoreCase("next_execution_time")) {
            final Boolean isUpgradeAvailable = this.isAppUpgradeAvailable(tableContext);
            final Integer associatedAppStatus2 = (Integer)tableContext.getAssociatedPropertyValue("MdAppCatalogToGroup.APPROVED_VERSION_STATUS");
            if (isUpgradeAvailable && associatedAppStatus2 != null && associatedAppStatus2 == 2) {
                columnProperties.put("VALUE", Utils.getTime((Long)data));
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
    }
    
    private boolean isAppUpgradeAvailable(final TransformerContext tableContext) throws Exception {
        final Object isUpgrade = tableContext.getAssociatedPropertyValue("MdAppCatalogToGroup.IS_UPDATE_AVAILABLE");
        if (isUpgrade != null) {
            return (boolean)isUpgrade;
        }
        return Boolean.FALSE;
    }
}
