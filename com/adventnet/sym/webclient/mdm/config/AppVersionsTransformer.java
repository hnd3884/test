package com.adventnet.sym.webclient.mdm.config;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AppVersionsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (!columnalias.equalsIgnoreCase("AppGroupToCollection.COLLECTION_ID")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        final int platformType = (int)tableContext.getAssociatedPropertyValue("MdAppDetails.PLATFORM_TYPE");
        final boolean portalApp = (boolean)tableContext.getAssociatedPropertyValue("MdPackageToAppGroup.IS_PURCHASED_FROM_PORTAL");
        final int privateAppType = (int)tableContext.getAssociatedPropertyValue("MdPackageToAppGroup.PRIVATE_APP_TYPE");
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = Boolean.FALSE;
        if (reportType != 4) {
            isExport = Boolean.TRUE;
        }
        if (platformType == 2 && portalApp && privateAppType == 1) {
            return false;
        }
        if (isExport) {
            return false;
        }
        final List roles = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
        final boolean hasAppMgmtWritePrivillage = roles.contains("MDM_AppMgmt_Write") || roles.contains("ModernMgmt_AppMgmt_Write");
        return hasAppMgmtWritePrivillage;
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnAlias = tableContext.getPropertyName();
        Object data = tableContext.getPropertyValue();
        final Long packageID = (Long)tableContext.getAssociatedPropertyValue("MdPackage.packageId");
        final Long releaseLabelId = (Long)tableContext.getAssociatedPropertyValue("AppGroupToCollection.RELEASE_LABEL_ID");
        final Integer versionStatus = (Integer)tableContext.getAssociatedPropertyValue("AppGroupToCollection.APP_VERSION_STATUS");
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = Boolean.FALSE;
        if (reportType != 4) {
            isExport = Boolean.TRUE;
        }
        if (columnAlias.equals("MdAppDetails.APP_VERSION") || columnAlias.equals("PublishedAppVersionToDeviceCount") || columnAlias.equals("PublishedAppVersionToGroupCount") || columnAlias.equals("AppGroupToCollection.COLLECTION_ID")) {
            if (columnAlias.equals("MdAppDetails.APP_VERSION") && data != null) {
                data = AppsUtil.getValidVersion(data.toString());
            }
            if (!isExport) {
                final Boolean isForAllCustomers = (Boolean)TransformerUtil.getPreValuesForTransformer(tableContext.getViewContext(), "is_for_all_customers");
                final Boolean isUserInRole = (Boolean)TransformerUtil.getPreValuesForTransformer(tableContext.getViewContext(), "isUserInRole");
                String viewContent = "details";
                if (columnAlias.equals("PublishedAppVersionToDeviceCount")) {
                    viewContent = "devices";
                }
                else if (columnAlias.equals("PublishedAppVersionToGroupCount")) {
                    viewContent = "groups";
                }
                boolean isApproved = false;
                if (versionStatus != null && versionStatus == 1) {
                    isApproved = true;
                }
                if (data == null) {
                    data = '0';
                }
                final Integer groupCount = (Integer)tableContext.getAssociatedPropertyValue("PublishedAppVersionToGroupCount");
                final Integer deviceCount = (Integer)tableContext.getAssociatedPropertyValue("PublishedAppVersionToDeviceCount");
                final JSONObject payloadData = new JSONObject();
                payloadData.put("viewContent", (Object)viewContent);
                payloadData.put("groupCount", (groupCount == null) ? 0 : ((int)groupCount));
                payloadData.put("deviceCount", (deviceCount == null) ? 0 : ((int)deviceCount));
                payloadData.put("isApproved", isApproved);
                payloadData.put("cellValue", (Object)data.toString());
                payloadData.put("labelId", (Object)releaseLabelId.toString());
                payloadData.put("is_for_all_customers", (Object)isForAllCustomers);
                payloadData.put("isUserInRole", (Object)isUserInRole);
                payloadData.put("isDistributable", true);
                columnProperties.put("PAYLOAD", payloadData);
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
        if (columnAlias.equals("AppReleaseLabel.RELEASE_LABEL_DISPLAY_NAME")) {
            final String versionLabel = I18N.getMsg(String.valueOf(data), new Object[0]);
            columnProperties.put("VALUE", versionLabel);
        }
    }
}
