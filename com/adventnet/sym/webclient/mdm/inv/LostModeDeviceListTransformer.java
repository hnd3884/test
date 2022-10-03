package com.adventnet.sym.webclient.mdm.inv;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class LostModeDeviceListTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        String isExport = "false";
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = "true";
        }
        final boolean hasInvWritePrivillage = request.isUserInRole("MDM_Inventory_Write");
        return ((!columnalias.equalsIgnoreCase("DeviceToActionHistory.DEVICE_ACTION_ID") && !columnalias.equalsIgnoreCase("GroupToActionHistory.GROUP_ACTION_ID")) || ((isExport == null || !isExport.equalsIgnoreCase("true")) && hasInvWritePrivillage)) && super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        Object data = tableContext.getPropertyValue();
        Integer lostmodeStatus = (Integer)tableContext.getAssociatedPropertyValue("LostModeTrackInfo.TRACKING_STATUS");
        final Long lostmodeStartTime = (Long)tableContext.getAssociatedPropertyValue("CommandHistory.ADDED_TIME");
        final Long lostmodeEndTime = (Long)tableContext.getAssociatedPropertyValue("CommandHistory.UPDATED_TIME");
        final Long resourceId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
        final boolean isSupervised = tableContext.getAssociatedPropertyValue("MdDeviceInfo.IS_SUPERVISED") != null && (boolean)tableContext.getAssociatedPropertyValue("MdDeviceInfo.IS_SUPERVISED");
        final String osVersion = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.OS_VERSION");
        final int platform = (int)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
        final Integer errorCode = (Integer)tableContext.getAssociatedPropertyValue("CommandError.ERROR_CODE");
        if (columnalais.equals("LostModeTrackInfo.TRACKING_STATUS")) {
            lostmodeStatus = (Integer)data;
            final JSONObject payload = new JSONObject();
            String lmStatus = "NA";
            String textClass = "";
            if (lostmodeStatus != null) {
                if (lostmodeStatus == 2) {
                    lmStatus = I18N.getMsg("dc.db.config.status.succeeded", new Object[0]);
                    textClass = "ucs-table-status-text__success";
                }
                else if (lostmodeStatus == 1 || lostmodeStatus == 4) {
                    lmStatus = I18N.getMsg("dc.common.status.in_progress", new Object[0]);
                    textClass = "ucs-table-status-text__in-progress";
                }
                else if (lostmodeStatus == 3 || lostmodeStatus == 6) {
                    lmStatus = I18N.getMsg("dc.common.status.failed", new Object[0]);
                    textClass = "ucs-table-status-text__failed";
                }
            }
            payload.put("scanStatus", (Object)lmStatus);
            payload.put("textClass", (Object)textClass);
            columnProperties.put("PAYLOAD", payload);
            columnProperties.put("VALUE", lmStatus);
        }
        if (columnalais.equals("CommandHistory.REMARKS")) {
            final JSONObject payload = new JSONObject();
            if (lostmodeStatus != null) {
                if (lostmodeStatus == 2) {
                    String msg = "";
                    if (platform == 1 && (!isSupervised || (!osVersion.equals("9.3") && !new VersionChecker().isGreater(osVersion, "9.3")))) {
                        msg = "mdm.command.lostmode_remote_lock";
                    }
                    else {
                        msg = "mdm.command.lostmode_completed_successfully";
                    }
                    final String date_time = Utils.getEventTime(lostmodeEndTime);
                    data = I18N.getMsg(msg, new Object[] { date_time });
                }
                else if (lostmodeStatus == 1) {
                    data = I18N.getMsg("mdm.command.lostmode.activation_in_progress", new Object[0]);
                }
                else if (lostmodeStatus == 3) {
                    data = I18N.getMsg("mdm.command.lostmode.activation_failed", new Object[0]);
                }
                else if (lostmodeStatus == 4) {
                    data = I18N.getMsg("mdm.command.lostmode.deactivation_in_progress", new Object[0]);
                }
                else if (lostmodeStatus == 6) {
                    data = I18N.getMsg("mdm.command.lostmode.deactivation_failed", new Object[0]);
                }
                if (errorCode != null && errorCode.equals(35001)) {
                    data = I18N.getMsg("mdm.command.lostmode.not_applicable_android", new Object[0]);
                }
            }
            payload.put("remarks", data);
            columnProperties.put("PAYLOAD", payload);
            columnProperties.put("VALUE", data);
        }
        if (columnalais.equals("Resource.RESOURCE_ID") && resourceId != null) {
            columnProperties.put("VALUE", resourceId);
        }
    }
}
