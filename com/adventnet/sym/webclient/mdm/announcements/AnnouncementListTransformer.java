package com.adventnet.sym.webclient.mdm.announcements;

import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import com.adventnet.client.view.web.ViewContext;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AnnouncementListTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public AnnouncementListTransformer() {
        this.logger = Logger.getLogger("AnnouncementHandler");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalais = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (!columnalais.equals("checkbox") && !columnalais.equals("Action")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (reportType != 4) {
            return Boolean.FALSE;
        }
        final boolean hasAdminPrivilage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Announcement_Write");
        boolean isRestrictedProfileListProfileWriteRole = Boolean.FALSE;
        if (!hasAdminPrivilage) {
            final Boolean showOnlyUserCreatedProfilesApps = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("showOnlyUserCreatedProfilesApps");
            if (showOnlyUserCreatedProfilesApps != null && showOnlyUserCreatedProfilesApps) {
                isRestrictedProfileListProfileWriteRole = (ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Announcement_Write") && showOnlyUserCreatedProfilesApps);
            }
        }
        return hasAdminPrivilage || isRestrictedProfileListProfileWriteRole;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            Object data = tableContext.getPropertyValue();
            if (data == "" || data == null) {
                data = 0;
            }
            final int reportType = tableContext.getViewContext().getRenderType();
            boolean isExport = Boolean.FALSE;
            if (reportType != 4) {
                isExport = Boolean.TRUE;
            }
            if (columnalais.equals("StatusLabel")) {
                final String statusName = (String)data;
                String statusStr = " -- ";
                if (statusName != null) {
                    if (reportType != 4) {
                        statusStr = I18N.getMsg(statusName, new Object[0]);
                    }
                    else {
                        final String statusImage = (String)tableContext.getAssociatedPropertyValue("StatusImage");
                        final JSONObject payload = new JSONObject();
                        payload.put("statusImage", (Object)statusImage);
                        payload.put("statusName", (Object)I18N.getMsg(statusName, new Object[0]));
                        columnProperties.put("PAYLOAD", payload);
                    }
                }
                columnProperties.put("VALUE", statusStr);
            }
            if (columnalais.equals("Action")) {
                final Integer profileStatus = (Integer)tableContext.getAssociatedPropertyValue("CollectionStatus.PROFILE_COLLECTION_STATUS");
                final Integer profileType = (Integer)tableContext.getAssociatedPropertyValue("Profile.PROFILE_TYPE");
                final JSONObject payload2 = new JSONObject();
                if (profileStatus != null && profileStatus == 1) {
                    payload2.put("yetToPublish", true);
                }
                else {
                    payload2.put("yetToPublish", false);
                }
                payload2.put("profile_type", (Object)profileType);
                columnProperties.put("PAYLOAD", payload2);
            }
            if (columnalais.equalsIgnoreCase("Announcement.ANNOUNCEMENT_NAME") || columnalais.equalsIgnoreCase("GroupQuery.GROUPCOUNT") || columnalais.equalsIgnoreCase("DevQuery.DEVCOUNT")) {
                if (!isExport) {
                    final Integer groupType = (Integer)tableContext.getAssociatedPropertyValue("CustomGroup.GROUP_TYPE");
                    String viewContent = "";
                    final long announcementIdforCheck = (long)tableContext.getAssociatedPropertyValue("Announcement.ANNOUNCEMENT_ID");
                    if (columnalais.equalsIgnoreCase("Announcement.ANNOUNCEMENT_NAME")) {
                        viewContent = "details";
                    }
                    else if (columnalais.equalsIgnoreCase("GroupQuery.GROUPCOUNT")) {
                        viewContent = "groups";
                    }
                    else if (columnalais.equalsIgnoreCase("DevQuery.DEVCOUNT")) {
                        viewContent = "devices";
                    }
                    final JSONObject payloadData = new JSONObject();
                    payloadData.put("cellValue", data);
                    payloadData.put("viewContent", (Object)viewContent);
                    payloadData.put("announcementId", (Object)String.valueOf(announcementIdforCheck));
                    columnProperties.put("PAYLOAD", payloadData);
                }
                else {
                    columnProperties.put("VALUE", data);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in ProfileListTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;--");
        }
    }
}
