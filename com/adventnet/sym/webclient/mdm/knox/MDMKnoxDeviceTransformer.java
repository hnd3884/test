package com.adventnet.sym.webclient.mdm.knox;

import java.util.ArrayList;
import com.me.mdm.webclient.transformer.TransformerUtil;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.server.mdm.MDMEntrollment;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import java.text.DecimalFormat;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMKnoxDeviceTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMKnoxDeviceTransformer() {
        this.logger = Logger.getLogger(MDMKnoxDeviceTransformer.class.getName());
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (columnalias.equalsIgnoreCase("checkbox") || columnalias.equalsIgnoreCase("Resource.RESOURCE_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            final boolean hasSettingsWritePrivillage = request.isUserInRole("MDM_Settings_Write");
            final boolean hasConfigurationWritePrivillage = request.isUserInRole("MDM_Configurations_Write");
            final boolean hasAppMgmtWritePrivillage = request.isUserInRole("MDM_AppMgmt_Write");
            return hasSettingsWritePrivillage || hasConfigurationWritePrivillage || hasAppMgmtWritePrivillage;
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMDeviceTransformer renderHeader().....");
        super.renderHeader(tableContext);
        final ViewContext viewCtx = tableContext.getViewContext();
        final int renderType = viewCtx.getRenderType();
        final HttpServletRequest request = viewCtx.getRequest();
        final String isExport = request.getParameter("isExport");
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String head = tableContext.getDisplayName();
        if (head.equals("Action") && isExport != null) {
            headerProperties.put("VALUE", "");
        }
    }
    
    public void renderCell(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMDeviceTransformer renderCell...");
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            this.logger.log(Level.FINE, "Columnalais : ", columnalais);
            final long resourceIdforCheck = (long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            final ViewContext vc = tableContext.getViewContext();
            final String isExportString = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            boolean isExport = false;
            final DecimalFormat dec = new DecimalFormat("###.##");
            if (isExportString != null && !isExportString.equals("")) {
                isExport = true;
            }
            if (columnalais.equals("GROUP_COUNT")) {
                if (data == null) {
                    columnProperties.put("VALUE", "0");
                }
                else if (!isExport) {
                    final List groupList = this.getAssociatedGroupNames(vc, resourceIdforCheck);
                    final Iterator item = groupList.iterator();
                    String groupName = item.next();
                    while (item.hasNext()) {
                        groupName = groupName + " , " + item.next();
                    }
                    final String value = groupName;
                    columnProperties.put("VALUE", value);
                }
            }
            if (columnalais.equals("ManagedDeviceExtn.NAME") && !isExport) {
                final Long resourceID = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
                final String deviceName = (String)data;
                final Integer managedStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                if (deviceName != null && managedStatus != 4) {
                    final String actionStr = "<a ignorequickload=\"true\" href=\"#/uems/mdm/inventory/devicesList/" + resourceID + "/summary\" >" + deviceName + "</a>";
                    columnProperties.put("VALUE", actionStr);
                }
                else {
                    final String actionStr = deviceName;
                    columnProperties.put("VALUE", actionStr);
                }
            }
            if (columnalais.equals("ManagedKNOXContainer.CONTAINER_STATUS")) {
                final Integer iStatus = (Integer)data;
                final JSONObject contanierStatus = this.getContainerStatusCompliantWithWebclient(iStatus);
                final String sStatus = I18N.getMsg(contanierStatus.getString("statusKey"), new Object[0]);
                final String textClass = contanierStatus.getString("textClass");
                final JSONObject payload = new JSONObject();
                payload.put("scanStatus", (Object)sStatus);
                payload.put("textClass", (Object)textClass);
                columnProperties.put("PAYLOAD", payload);
                columnProperties.put("VALUE", sStatus);
            }
            if (columnalais.equals("ManagedKNOXContainer.CONTAINER_REMARKS")) {
                final Integer managedStatus2 = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                if (managedStatus2 == 4) {
                    final String managedstatus = (String)tableContext.getAssociatedPropertyValue("ManagedDevice.REMARKS");
                    columnProperties.put("VALUE", I18N.getMsg(managedstatus, new Object[0]));
                }
                else {
                    final String managedstatus = (String)tableContext.getAssociatedPropertyValue("ManagedKNOXContainer.CONTAINER_REMARKS");
                    columnProperties.put("VALUE", I18N.getMsg(managedstatus, new Object[0]));
                }
            }
            if (columnalais.equals("DeviceEnrollmentRequest.OWNED_BY")) {
                final Integer ownedBy = (Integer)data;
                final String sOwnedBy = MDMEntrollment.getInstance().getOwnedByAsString(ownedBy);
                columnProperties.put("VALUE", sOwnedBy);
            }
            if (columnalais.equals("checkbox")) {
                final String check = " ";
                boolean isDisabled = false;
                final Integer managedStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                final String UDID = (String)tableContext.getAssociatedPropertyValue("ManagedDevice.UDID");
                if (managedStatus == 4) {
                    isDisabled = true;
                }
                final JSONObject payload = new JSONObject();
                payload.put("isDisabled", isDisabled);
                payload.put("UDID", (Object)UDID);
                columnProperties.put("PAYLOAD", payload);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
    
    private List getAssociatedGroupNames(final ViewContext vc, final Long resourceID) throws Exception {
        final HashMap hashMap = (HashMap)TransformerUtil.getPreValuesForTransformer(vc, "ASSOCIATED_GROUP_NAMES");
        return (hashMap.get(resourceID) != null) ? ((List)hashMap.get(resourceID)) : new ArrayList();
    }
    
    public JSONObject getContainerStatusCompliantWithWebclient(final Integer iContainerStatus) throws Exception {
        String sContainerStatus = I18N.getMsg("dc.conf.mod_list.not_avail", new Object[0]);
        final JSONObject jsonHeaders = new JSONObject();
        String textClass = "ucs-table-status-text__not-applicable";
        if (iContainerStatus != null) {
            try {
                switch (iContainerStatus) {
                    case 20001: {
                        sContainerStatus = I18N.getMsg("desktopcentral.config.fileFolder.addConfig.Created", new Object[0]);
                        textClass = "ucs-table-status-text__success";
                        break;
                    }
                    case 20002: {
                        sContainerStatus = I18N.getMsg("dc.inv.common.REMOVED", new Object[0]);
                        textClass = "ucs-table-status-text__failed";
                        break;
                    }
                    case 20000: {
                        sContainerStatus = I18N.getMsg("dc.db.mdm.status.initiated", new Object[0]);
                        textClass = "ucs-table-status-text__in-progress";
                        break;
                    }
                    case 20003: {
                        sContainerStatus = I18N.getMsg("dc.common.status.failed", new Object[0]);
                        textClass = "ucs-table-status-text__failed";
                        break;
                    }
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception while getting License Status Value as String ", ex);
            }
        }
        jsonHeaders.put("statusKey", (Object)sContainerStatus);
        jsonHeaders.put("textClass", (Object)textClass);
        return jsonHeaders;
    }
}
