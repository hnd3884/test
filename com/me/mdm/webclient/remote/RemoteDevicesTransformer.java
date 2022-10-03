package com.me.mdm.webclient.remote;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.web.TransformerContext;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class RemoteDevicesTransformer extends RolecheckerTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        String isExport = "false";
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = "true";
        }
        if (columnalias.equalsIgnoreCase("RemoteAction")) {
            final boolean hasRemoteWritePrivillage = request.isUserInRole("MDM_RemoteControl_Write");
            if ((isExport != null && isExport.equalsIgnoreCase("true")) || !hasRemoteWritePrivillage) {
                return false;
            }
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        String isExport = "false";
        final String eligibilityOfDevice = tableContext.getViewContext().getRequest().getParameter("eligibility");
        tableContext.getViewContext().getRequest().setAttribute("eligibility", (Object)eligibilityOfDevice);
        final Object data = tableContext.getPropertyValue();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = "true";
        }
        String commandName = (String)tableContext.getAssociatedPropertyValue("MdCommands.COMMAND_UUID");
        if (commandName == null) {
            commandName = "nonRemoteCommand";
        }
        if (columnalais.equalsIgnoreCase("RemoteAction")) {
            String actionStr = "";
            String failureCause = null;
            boolean isRemoteControlCapable = false;
            final Long resourceId = (Long)tableContext.getAssociatedPropertyValue("ManagedDevice.RESOURCE_ID");
            final Long customerId = (Long)tableContext.getAssociatedPropertyValue("CustomerInfo.Customer_ID");
            final String osVersion = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.OS_VERSION");
            final int platformType = (int)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
            int locationEnabled = 0;
            if (tableContext.getAssociatedPropertyValue("MDMPrivacySettings.DISABLE_REMOTE_CONTROL") != null) {
                locationEnabled = (int)tableContext.getAssociatedPropertyValue("MDMPrivacySettings.DISABLE_REMOTE_CONTROL");
            }
            final int deviceAgentType = (int)tableContext.getAssociatedPropertyValue("ManagedDevice.AGENT_TYPE");
            final int notificationService = MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(platformType, customerId);
            if (platformType == 2) {
                isRemoteControlCapable = InventoryUtil.getInstance().getRemoteControlCapability(resourceId);
            }
            if (locationEnabled != 2) {
                if ((platformType == 2 && !osVersion.startsWith("3") && !osVersion.startsWith("4")) || (platformType == 1 && ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 11.0f))) {
                    if (MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(customerId)) {
                        actionStr = "initiateRemote";
                    }
                    else {
                        actionStr = "accountNotLoggedIn";
                    }
                }
                else {
                    failureCause = (actionStr = I18N.getMsg("dc.mdm.inv.remoteTroubleshoot.os_not_supported", new Object[0]));
                }
            }
            else {
                failureCause = (actionStr = I18N.getMsg("mdm.privacy.remotecontrol.privacy_error", new Object[0]));
            }
            String buttonText = I18N.getMsg("dc.mdm.inv.remote_troubleshoot", new Object[0]);
            if (isRemoteControlCapable || deviceAgentType == 3) {
                buttonText = I18N.getMsg("dc.mdm.inv.remote_troubleshoot", new Object[0]);
            }
            else if (deviceAgentType == 2 || deviceAgentType == 1) {
                buttonText = I18N.getMsg("dc.mdm.inv.remote_view", new Object[0]);
            }
            final JSONObject payload = new JSONObject();
            payload.put("buttonText", (Object)buttonText);
            payload.put("buttonAction", (Object)actionStr);
            payload.put("resourceId", (Object)resourceId);
            payload.put("notificationService", notificationService);
            columnProperties.put("PAYLOAD", payload);
            columnProperties.put("VALUE", actionStr);
        }
        if (columnalais.equalsIgnoreCase("CommandHistory.ADDED_BY")) {
            String addedby = null;
            try {
                addedby = (String)tableContext.getAssociatedPropertyValue("AaaUser.FIRST_NAME");
                if (addedby == null) {
                    columnProperties.put("VALUE", "--");
                }
                else if (commandName.equalsIgnoreCase("RemoteSession")) {
                    columnProperties.put("VALUE", addedby);
                }
                else {
                    columnProperties.put("VALUE", "--");
                }
            }
            catch (final Exception e) {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("ManagedDevice.PLATFORM_TYPE")) {
            int platformType2 = platformType2 = (int)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
            if (isExport == null || !isExport.equalsIgnoreCase("true")) {
                if (platformType2 == 2) {
                    columnProperties.put("VALUE", " <img src=\"/images/androidlogo.png\" style=\"height:13px; width:13px;align:\"/>&nbsp;&nbsp; " + I18N.getMsg("dc.mdm.android", new Object[0]));
                }
                else if (platformType2 == 1) {
                    columnProperties.put("VALUE", "<img src=\"/images/ios_grey_logo.png\" style=\"height:13px; width:12px;\"/>&nbsp;&nbsp;&nbsp;" + I18N.getMsg("dc.mdm.ios", new Object[0]));
                }
            }
            else if (platformType2 == 2) {
                columnProperties.put("VALUE", I18N.getMsg("dc.mdm.android", new Object[0]));
            }
            else if (platformType2 == 1) {
                columnProperties.put("VALUE", I18N.getMsg("dc.mdm.ios", new Object[0]));
            }
        }
        if (columnalais.equalsIgnoreCase("CommandHistory.ADDED_TIME")) {
            String actionStr = "";
            try {
                if (commandName.equalsIgnoreCase("RemoteSession")) {
                    final Long addedTime = (Long)tableContext.getAssociatedPropertyValue("CommandHistory.ADDED_TIME");
                    final DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(addedTime);
                    actionStr = formatter.format(calendar.getTime());
                    columnProperties.put("VALUE", actionStr);
                }
                else {
                    columnProperties.put("VALUE", "--");
                }
            }
            catch (final Exception e) {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("RemoteSessionCommandHistory.STATUS")) {
            int status = 0;
            String statusText = null;
            try {
                if (commandName.equalsIgnoreCase("RemoteSession")) {
                    status = (int)tableContext.getAssociatedPropertyValue("RemoteSessionCommandHistory.STATUS");
                    final String osVersion2 = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.OS_VERSION");
                    final int platformType3 = (int)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
                    if (platformType3 == 2 && (osVersion2.startsWith("2.") || osVersion2.startsWith("3.") || osVersion2.startsWith("4."))) {
                        status = 200;
                    }
                    if (platformType3 == 1 && (osVersion2.startsWith("4.") || osVersion2.startsWith("5.") || osVersion2.startsWith("6.") || osVersion2.startsWith("7.") || osVersion2.startsWith("8.") || osVersion2.startsWith("9.") || osVersion2.startsWith("10."))) {
                        status = 200;
                    }
                    switch (status) {
                        case 1: {
                            statusText = I18N.getMsg("dc.db.mdm.status.initiated", new Object[0]);
                            break;
                        }
                        case 2: {
                            statusText = I18N.getMsg("dc.db.mdm.status.initiated", new Object[0]);
                            break;
                        }
                        case 3: {
                            statusText = I18N.getMsg("dc.db.config.status.in_progress", new Object[0]);
                            break;
                        }
                        case 4: {
                            statusText = I18N.getMsg("dc.common.status.failed", new Object[0]);
                            break;
                        }
                        case 5: {
                            statusText = I18N.getMsg("dc.common.status.failed", new Object[0]);
                            break;
                        }
                        case 6: {
                            statusText = I18N.getMsg("mdm.remote.session_ended", new Object[0]);
                            break;
                        }
                        case 7: {
                            statusText = I18N.getMsg("dc.common.status.failed", new Object[0]);
                            break;
                        }
                        case 8: {
                            statusText = I18N.getMsg("mdm.remote.session_ended", new Object[0]);
                            break;
                        }
                        case 10: {
                            statusText = I18N.getMsg("dc.common.status.failed", new Object[0]);
                            break;
                        }
                        case 11: {
                            statusText = I18N.getMsg("dc.common.status.failed", new Object[0]);
                            break;
                        }
                        case 12: {
                            statusText = I18N.getMsg("dc.common.status.failed", new Object[0]);
                            break;
                        }
                        case 200: {
                            statusText = I18N.getMsg("dc.mdm.inv.remoteTroubleshoot.os_not_supported", new Object[0]);
                            break;
                        }
                    }
                }
                else {
                    status = 0;
                }
            }
            catch (final Exception e2) {
                status = 0;
            }
            final JSONObject payload2 = new JSONObject();
            payload2.put("status", status);
            columnProperties.put("PAYLOAD", payload2);
            columnProperties.put("VALUE", isExport.equalsIgnoreCase("true") ? statusText : Integer.valueOf(status));
        }
        if (columnalais.equalsIgnoreCase("Remarks")) {
            try {
                if (commandName.equalsIgnoreCase("RemoteSession")) {
                    int status = (int)tableContext.getAssociatedPropertyValue("RemoteSessionCommandHistory.STATUS");
                    final String osVersion3 = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.OS_VERSION");
                    final int platformType4 = (int)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
                    String statusText2 = null;
                    if (platformType4 == 2 && (osVersion3.startsWith("2.") || osVersion3.startsWith("3.") || osVersion3.startsWith("4."))) {
                        status = 200;
                    }
                    if (platformType4 == 1 && (osVersion3.startsWith("4.") || osVersion3.startsWith("5.") || osVersion3.startsWith("6.") || osVersion3.startsWith("7.") || osVersion3.startsWith("8.") || osVersion3.startsWith("9.") || osVersion3.startsWith("10."))) {
                        status = 200;
                    }
                    switch (status) {
                        case 1: {
                            statusText2 = I18N.getMsg("mdm.remote.initiated_remarks", new Object[0]);
                            break;
                        }
                        case 2: {
                            statusText2 = I18N.getMsg("mdm.remote.agent_received_remarks", new Object[0]);
                            break;
                        }
                        case 3: {
                            statusText2 = I18N.getMsg("mdm.remote.inprogress_remarks", new Object[0]);
                            break;
                        }
                        case 4: {
                            statusText2 = I18N.getMsg("mdm.remote.rejected_remarks", new Object[0]);
                            break;
                        }
                        case 5: {
                            statusText2 = I18N.getMsg("mdm.remote.not_supported_remarks", new Object[0]);
                            break;
                        }
                        case 6: {
                            statusText2 = I18N.getMsg("mdm.remote.session_ended_by_admin_remarks", new Object[0]);
                            break;
                        }
                        case 7: {
                            statusText2 = I18N.getMsg("mdm.remote.error_remarks", new Object[0]);
                            break;
                        }
                        case 8: {
                            statusText2 = I18N.getMsg("mdm.remote.session_ended_by_user_remarks", new Object[0]);
                            break;
                        }
                        case 10: {
                            statusText2 = I18N.getMsg("mdm.remote.no_participant", new Object[0]);
                            break;
                        }
                        case 11: {
                            final String viewer = (String)tableContext.getAssociatedPropertyValue("UserResource.NAME");
                            statusText2 = I18N.getMsg("mdm.remote.admin_closed_viewer", new Object[0]) + " " + viewer;
                            break;
                        }
                        case 12: {
                            final String viewer2 = (String)tableContext.getAssociatedPropertyValue("UserResource.NAME");
                            statusText2 = I18N.getMsg("mdm.remote.admin_closed_viewer", new Object[0]) + " " + viewer2;
                            break;
                        }
                        case 200: {
                            statusText2 = I18N.getMsg("mdm.remote.only5andabove", new Object[0]);
                            break;
                        }
                    }
                    columnProperties.put("VALUE", statusText2);
                }
                else {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.remote.ready_remarks", new Object[0]));
                }
            }
            catch (final Exception e3) {
                columnProperties.put("VALUE", I18N.getMsg("mdm.remote.ready_remarks", new Object[0]));
            }
        }
        if (columnalais.equalsIgnoreCase("SESSIONDURATION")) {
            try {
                if (commandName.equalsIgnoreCase("RemoteSession")) {
                    final Long startTime = (Long)tableContext.getAssociatedPropertyValue("RemoteSessionReport.SESSION_START_TIME");
                    final Long endTime = (Long)tableContext.getAssociatedPropertyValue("RemoteSessionReport.SESSION_END_TIME");
                    final Long mins = (endTime - startTime) / 60000L;
                    final Long secs = (endTime - startTime) / 1000L % 60L;
                    if (startTime != -1L && endTime != -1L) {
                        if (mins > 0L) {
                            columnProperties.put("VALUE", mins + " " + I18N.getMsg("dc.common.MINUTES", new Object[0]) + " " + secs + " " + I18N.getMsg("dc.common.SECONDS", new Object[0]));
                        }
                        else {
                            columnProperties.put("VALUE", secs + " " + I18N.getMsg("dc.common.SECONDS", new Object[0]));
                        }
                    }
                    else {
                        columnProperties.put("VALUE", "--");
                    }
                }
                else {
                    columnProperties.put("VALUE", "--");
                }
            }
            catch (final Exception e3) {
                columnProperties.put("VALUE", "--");
            }
        }
    }
}
