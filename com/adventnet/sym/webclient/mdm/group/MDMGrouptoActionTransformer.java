package com.adventnet.sym.webclient.mdm.group;

import java.util.List;
import com.me.mdm.server.inv.actions.InvActionUtilProvider;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.me.mdm.api.command.schedule.GroupActionScheduleUtils;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.inv.actions.ClearAppDataHandler;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMGrouptoActionTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        String isExport = "false";
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = "true";
        }
        final boolean hasInvWritePrivillage = request.isUserInRole("MDM_Inventory_Write") || request.isUserInRole("ModernMgmt_Inventory_Write");
        return (!columnalias.equalsIgnoreCase("Resource.RESOURCE_ID") || ((isExport == null || !isExport.equalsIgnoreCase("true")) && hasInvWritePrivillage)) && super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final Object data = tableContext.getPropertyValue();
        final String columnalias = tableContext.getPropertyName();
        final String viewname = tableContext.getViewContext().getUniqueId();
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = Boolean.FALSE;
        if (reportType != 4) {
            isExport = Boolean.TRUE;
        }
        final Long group_action_id = (Long)tableContext.getAssociatedPropertyValue("GroupActionHistory.GROUP_ACTION_ID");
        final Long device_action_id = (Long)tableContext.getAssociatedPropertyValue("DeviceActionHistory.DEVICE_ACTION_ID");
        final Integer total_count = (Integer)tableContext.getAssociatedPropertyValue("GroupActionHistory.INITIATED_COUNT");
        final Integer inprogress_count = (Integer)tableContext.getAssociatedPropertyValue("GroupActionHistory.INPROGRESS_COUNT");
        final Integer success_count = (Integer)tableContext.getAssociatedPropertyValue("GroupActionHistory.SUCCESS_COUNT");
        final Integer failure_count = (Integer)tableContext.getAssociatedPropertyValue("GroupActionHistory.FAILURE_COUNT");
        final Integer suspend_count = (Integer)tableContext.getAssociatedPropertyValue("GroupActionHistory.SUSPEND_COUNT");
        final Integer action_status = (Integer)tableContext.getAssociatedPropertyValue("GroupActionHistory.ACTION_STATUS");
        final String grp_reason_msg = (String)tableContext.getAssociatedPropertyValue("GroupActionHistory.REASON_MESSAGE");
        final String action_remarks = (String)tableContext.getAssociatedPropertyValue("GroupActionHistory.ACTION_REMARKS");
        final Long commandHistoryID = (Long)tableContext.getAssociatedPropertyValue("DeviceActionHistory.COMMAND_HISTORY_ID");
        final String device_reason_msg = (String)tableContext.getAssociatedPropertyValue("ReasonForCommandHistory.REASON_MESSAGE");
        Integer cmd_status = (Integer)tableContext.getAssociatedPropertyValue("CommandHistory.COMMAND_STATUS");
        final String cmd_remarks = (String)tableContext.getAssociatedPropertyValue("CommandHistory.REMARKS");
        final Integer platform_type = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
        if ((columnalias.equals("DeviceActionHistory.ACTION_ID") || columnalias.equals("GroupActionHistory.ACTION_ID")) && data != null) {
            final int action = Integer.valueOf(data.toString());
            final JSONObject payload = new JSONObject();
            if (action == 2) {
                List AppsList = null;
                if (columnalias.equals("DeviceActionHistory.ACTION_ID")) {
                    AppsList = ClearAppDataHandler.getInstance().getDeviceActionClearedApps(device_action_id);
                }
                if (columnalias.equals("GroupActionHistory.ACTION_ID")) {
                    AppsList = ClearAppDataHandler.getInstance().getGroupActionClearedApps(group_action_id);
                }
                final String appsString = MDMStringUtils.getCommaSeparatedStringFromStrList(AppsList);
                payload.put("clearAppsString", (Object)appsString);
                payload.put("clearAppsCount", AppsList.size());
            }
            else if (group_action_id == null) {
                final Long groupActionID = GroupActionScheduleUtils.getGroupActionIDForCommandHistoryID(commandHistoryID);
                columnProperties.put("VALUE", this.getActionType(action, groupActionID));
                if (groupActionID != null) {
                    payload.put("group_action_id", (Object)groupActionID);
                }
            }
            else {
                final Boolean isScheduled = GroupActionScheduleUtils.isGroupActionScheduled(group_action_id);
                payload.put("group_action_id", (Object)group_action_id);
                payload.put("is_scheduled", (Object)isScheduled);
            }
            columnProperties.put("PAYLOAD", payload);
            columnProperties.put("VALUE", this.getActionType(action, group_action_id));
        }
        if (columnalias.equals("GroupActionHistory.ACTION_STATUS")) {
            if (!isExport) {
                final Boolean isScheduled2 = GroupActionScheduleUtils.isGroupActionScheduled(group_action_id);
                final JSONObject payload = new JSONObject();
                final HashMap hashMap = (HashMap)tableContext.getViewContext().getRequest().getAttribute("ACTION_ID_MAP");
                final HashMap timeZoneMap = (HashMap)tableContext.getViewContext().getRequest().getAttribute("TIME_ZONE_MAP");
                final HashMap scheduleTypeMap = (HashMap)tableContext.getViewContext().getRequest().getAttribute("SCHEDULE_TYPE_MAP");
                if (isScheduled2) {
                    final Long nextExecutionTime = hashMap.get(group_action_id);
                    final String timeZone = timeZoneMap.get(group_action_id);
                    final Integer scheduleType = scheduleTypeMap.get(group_action_id);
                    payload.put("is_scheduled", (Object)isScheduled2);
                    payload.put("next_execution_time", (Object)nextExecutionTime);
                    payload.put("timeZone", (Object)timeZone);
                    payload.put("scheduleType", (Object)scheduleType);
                }
                payload.put("inprogress_count", (Object)inprogress_count);
                payload.put("total_count", (Object)total_count);
                payload.put("success_count", (Object)success_count);
                payload.put("suspend_count", (Object)suspend_count);
                payload.put("failure_count", (Object)failure_count);
                payload.put("action_status", (Object)action_status);
                columnProperties.put("PAYLOAD", payload);
            }
            else {
                columnProperties.put("VALUE", this.getStatusMsg(action_status));
            }
        }
        if (columnalias.equals("CommandHistory.REMARKS")) {
            String remarks = "--";
            if (viewname.equals("mdmGroupActions")) {
                cmd_status = action_status;
                if (cmd_status != null) {
                    if (cmd_status == 4) {
                        remarks = I18N.getMsg(action_remarks, new Object[] { inprogress_count });
                    }
                    else if (cmd_status == 0) {
                        remarks = I18N.getMsg(action_remarks, new Object[] { failure_count });
                    }
                    else {
                        remarks = I18N.getMsg(action_remarks, new Object[0]);
                    }
                }
            }
            else {
                final Integer group_action_type = (Integer)tableContext.getAssociatedPropertyValue("GroupActionHistory.ACTION_ID");
                final Integer device_action_type = (Integer)tableContext.getAssociatedPropertyValue("DeviceActionHistory.ACTION_ID");
                Integer actionTypeParam = null;
                if (viewname.equals("mdmGroupActionDevices")) {
                    actionTypeParam = Integer.parseInt(tableContext.getViewContext().getRequest().getParameter("actionType"));
                }
                final Integer actionType = (actionTypeParam != null) ? actionTypeParam : ((device_action_type != null) ? device_action_type : group_action_type);
                if (cmd_status != null && actionType == 2) {
                    if (cmd_status == 2) {
                        final String remarksArg = "mdm.bulkaction.resetapps.successful_remarks@@@<l>$(mdmUrl)/help/asset_management/mdm_bulk_actions.html#clear_app_data";
                        remarks = MDMI18N.getMsg(remarksArg, false, true);
                    }
                    else if (cmd_status == 4) {
                        remarks = I18N.getMsg("mdm.bulkaction.device_inProgress_remarks", new Object[0]);
                    }
                    else if (cmd_status == 0) {
                        remarks = I18N.getMsg("mdm.bulkaction.resetapps.device_failure_remarks", new Object[0]);
                    }
                    else if (cmd_status == 1) {
                        remarks = I18N.getMsg("mdm.bulkaction.resetapps.device_yetToApply_remarks", new Object[0]);
                    }
                    else if (cmd_status == 6) {
                        remarks = I18N.getMsg("mdm.bulkaction.device.manual_suspend", new Object[0]);
                    }
                }
                else if (cmd_status != null) {
                    if (cmd_status == 4) {
                        remarks = I18N.getMsg("mdm.bulkaction.device_inProgress_remarks", new Object[0]);
                    }
                    else if (cmd_status == 2) {
                        remarks = I18N.getMsg("mdm.bulkaction.device_successful_remarks", new Object[0]);
                    }
                    else if (cmd_status == 0) {
                        remarks = I18N.getMsg("mdm.bulkaction.device_failure_remarks", new Object[0]);
                    }
                    else if (cmd_status == 1) {
                        remarks = I18N.getMsg("mdm.bulkaction.device_yetToApply_remarks", new Object[0]);
                    }
                    else if (cmd_status == 7) {
                        remarks = I18N.getMsg("mdm.bulkaction.device_schedule_remarks", new Object[0]);
                    }
                    else if (cmd_status == 6) {
                        remarks = I18N.getMsg(cmd_remarks, new Object[0]);
                    }
                }
                else if (viewname.equals("mdmGroupActionDevices")) {
                    final JSONObject json = new JSONObject();
                    final Boolean isSupervised = (Boolean)tableContext.getAssociatedPropertyValue("MdDeviceInfo.IS_SUPERVISED");
                    final String osversion = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.OS_VERSION");
                    final Integer modelType = (Integer)tableContext.getAssociatedPropertyValue("MdModelInfo.MODEL_TYPE");
                    json.put("IS_SUPERVISED", (Object)isSupervised);
                    json.put("OS_VERSION", (Object)osversion);
                    json.put("MODEL_TYPE", (Object)modelType);
                    final String commandName = this.getActionName(actionType);
                    if (InvActionUtilProvider.getInvActionUtil(platform_type).isCommandApplicable(json, commandName)) {
                        remarks = I18N.getMsg("mdm.bulkaction.addedLater.notApplicable_remarks", new Object[0]);
                    }
                    else {
                        remarks = I18N.getMsg("mdm.bulkaction.notApplicable_remarks", new Object[0]);
                    }
                }
            }
            if (isExport) {
                columnProperties.put("VALUE", remarks);
            }
            else {
                final JSONObject payload = new JSONObject();
                payload.put("VALUE", (Object)remarks);
                payload.put("data", (Object)remarks);
                payload.put("hoverText", (Object)remarks);
                columnProperties.put("PAYLOAD", payload);
            }
        }
        if (columnalias.equals("CommandHistory.COMMAND_STATUS")) {
            Integer actionId = -1;
            if (viewname.equals("mdmGroupActionDevices")) {
                actionId = (Integer)tableContext.getAssociatedPropertyValue("GroupActionHistory.ACTION_ID");
            }
            else if (viewname.equals("mdmDeviceActions")) {
                actionId = (Integer)tableContext.getAssociatedPropertyValue("DeviceActionHistory.ACTION_ID");
            }
            if (cmd_status == null) {
                cmd_status = 1000;
            }
            if (cmd_status != null) {
                if (isExport) {
                    columnProperties.put("VALUE", this.getStatusMsg(cmd_status));
                }
                else {
                    final JSONObject payload = new JSONObject();
                    payload.put("VALUE", (Object)cmd_status);
                    payload.put("ACTION_TYPE", (Object)actionId);
                    columnProperties.put("PAYLOAD", payload);
                }
            }
        }
        if (columnalias.equals("ReasonForCommandHistory.REASON_MESSAGE") && device_reason_msg != null) {
            if (isExport) {
                columnProperties.put("VALUE", device_reason_msg);
            }
            else {
                final JSONObject payload2 = new JSONObject();
                payload2.put("data", (Object)device_reason_msg);
                payload2.put("hoverText", (Object)device_reason_msg);
                columnProperties.put("PAYLOAD", payload2);
            }
        }
        if (columnalias.equals("GroupActionHistory.REASON_MESSAGE") && grp_reason_msg != null) {
            if (isExport) {
                columnProperties.put("VALUE", grp_reason_msg);
            }
            else {
                final JSONObject payload2 = new JSONObject();
                final Boolean isScheduled3 = GroupActionScheduleUtils.isGroupActionScheduled(group_action_id);
                payload2.put("is_scheduled", (Object)isScheduled3);
                payload2.put("data", (Object)grp_reason_msg);
                payload2.put("hoverText", (Object)grp_reason_msg);
                columnProperties.put("PAYLOAD", payload2);
            }
        }
        if (columnalias.equals("GroupActionHistory.GROUP_ACTION_ID") && group_action_id != null) {
            final JSONObject payload2 = new JSONObject();
            final Boolean isScheduled3 = GroupActionScheduleUtils.isGroupActionScheduled(group_action_id);
            if (isScheduled3) {
                final HashMap scheduleTypeMap2 = (HashMap)tableContext.getViewContext().getRequest().getAttribute("SCHEDULE_TYPE_MAP");
                final Integer scheduleType2 = scheduleTypeMap2.get(group_action_id);
                payload2.put("schedule_type", (Object)scheduleType2);
            }
            payload2.put("status", (Object)action_status);
            payload2.put("is_scheduled", (Object)isScheduled3);
            columnProperties.put("PAYLOAD", payload2);
            columnProperties.put("VALUE", group_action_id);
        }
        if (columnalias.equals("ScheduleRepository.SCHEDULE_TYPE")) {
            final HashMap scheduleTypeMap3 = (HashMap)tableContext.getViewContext().getRequest().getAttribute("SCHEDULE_TYPE_MAP");
            Integer scheduleType3 = null;
            if (scheduleTypeMap3 != null) {
                scheduleType3 = scheduleTypeMap3.get(group_action_id);
            }
            if (scheduleType3 == null) {
                columnProperties.put("VALUE", I18N.getMsg("dc.mdm.device_mgmt.immediate", new Object[0]));
            }
            else if (scheduleType3 == 1) {
                columnProperties.put("VALUE", I18N.getMsg("mdm.bulkaction.repeat", new Object[0]));
            }
            else {
                columnProperties.put("VALUE", I18N.getMsg("mdm.bulkaction.later", new Object[0]));
            }
        }
    }
    
    private String getActionType(final int action, final Long groupActionID) throws Exception {
        String actionType = "--";
        if (action == 1) {
            actionType = I18N.getMsg("mdm.common.remote_restart", new Object[0]);
        }
        else if (action == 0) {
            actionType = I18N.getMsg("mdm.common.remote_shutdown", new Object[0]);
        }
        else if (action == 2) {
            actionType = I18N.getMsg("dc.mdm.inv.clear_app_data", new Object[0]);
        }
        return actionType;
    }
    
    private String getActionName(final int action) throws Exception {
        String actionName = "--";
        if (action == 1) {
            actionName = "restart";
        }
        else if (action == 0) {
            actionName = "shutdown";
        }
        else if (action == 2) {
            actionName = "clear_app_data";
        }
        return actionName;
    }
    
    private String getStatusMsg(final Integer action_status) throws Exception {
        String statusStr = "NA";
        if (action_status != null) {
            if (action_status == 1 || action_status == 4) {
                statusStr = I18N.getMsg("dc.common.status.in_progress", new Object[0]);
            }
            else if (action_status == 2) {
                statusStr = I18N.getMsg("dc.db.config.status.succeeded", new Object[0]);
            }
            else if (action_status == 0) {
                statusStr = I18N.getMsg("dc.common.status.failed", new Object[0]);
            }
            else if (action_status == 6) {
                statusStr = I18N.getMsg("dc.db.config.status.suspended", new Object[0]);
            }
            else if (action_status == 7) {
                statusStr = I18N.getMsg("dc.db.som.status.scheduled", new Object[0]);
            }
        }
        return statusStr;
    }
}
