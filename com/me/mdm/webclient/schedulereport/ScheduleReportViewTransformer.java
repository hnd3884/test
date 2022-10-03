package com.me.mdm.webclient.schedulereport;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class ScheduleReportViewTransformer extends DefaultTransformer
{
    private static Logger out;
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        boolean isExport = false;
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = true;
        }
        if (columnalias.equalsIgnoreCase("Action")) {
            final boolean hasReportsWritePrivillage = request.isUserInRole("MDM_Report_Write");
            if (isExport || !hasReportsWritePrivillage) {
                return false;
            }
        }
        if (vc.getUniqueId().equalsIgnoreCase("DeviceLocationHistoryList")) {
            final String isScheduleReportStr = tableContext.getViewContext().getRequest().getParameter("isScheduledReport");
            boolean isScheduleReport = false;
            if (isScheduleReportStr != null) {
                isScheduleReport = Boolean.valueOf(isScheduleReportStr);
            }
            if (columnalias.equalsIgnoreCase("CustomGroup.RESOURCE_ID")) {
                return isScheduleReport || isExport;
            }
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            final String viewName = tableContext.getViewContext().getUniqueId();
            final ViewContext vc = tableContext.getViewContext();
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String displyColumn = tableContext.getPropertyName();
            if ("SCHEDULE_TIME".equals(displyColumn)) {
                final DMWebClientCommonUtil webClientUtil = new DMWebClientCommonUtil();
                webClientUtil.setNextExecTimeinViewContext(vc);
                final String value = webClientUtil.getNextExecTime(tableContext);
                columnProperties.put("VALUE", value);
            }
            if ("TaskDetails.STATUS".equals(displyColumn) && "ScheduledReportTasks".equals(viewName)) {
                final String status_str = columnProperties.get("VALUE");
                final JSONObject json = new JSONObject();
                json.put("status", (Object)status_str);
                columnProperties.put("PAYLOAD", json);
            }
            if ("Action".equals(displyColumn.trim())) {
                if ("ScheduledReportTasks".equals(viewName)) {
                    final Long id = (Long)tableContext.getAssociatedPropertyValue("TaskDetails.TASK_ID");
                    final JSONObject json = new JSONObject();
                    json.put("task_id", (Object)String.valueOf(id));
                    columnProperties.put("PAYLOAD", json);
                }
                columnProperties.put("VALUE", "--");
            }
            if ("ScheduleRepTask.REPORT_FORMAT".equals(displyColumn)) {
                String value2 = columnProperties.get("VALUE");
                if ("ScheduledReportTasks".equals(viewName)) {
                    if (value2.equalsIgnoreCase("1")) {
                        value2 = "pdf";
                    }
                    else if (value2.equalsIgnoreCase("2")) {
                        value2 = "xls";
                    }
                    else if (value2.equalsIgnoreCase("3")) {
                        value2 = "csv";
                    }
                    columnProperties.put("VALUE", value2);
                }
            }
            if (displyColumn.equals("ScheduledTaskDetails.REPEAT_FREQUENCY")) {
                final String frequency = (String)tableContext.getAssociatedPropertyValue("ScheduledTaskDetails.REPEAT_FREQUENCY");
                final String repeatFreq_i18n = SchedulerInfo.getI18nValforRepeatFreq(frequency);
                columnProperties.put("VALUE", repeatFreq_i18n);
            }
            if (displyColumn.equals("CustomGroup.RESOURCE_ID")) {
                final HashMap<Long, String> groupMap = (HashMap<Long, String>)tableContext.getViewContext().getRequest().getAttribute("CUSTOM_GROUPS");
                final Long resourceID = (Long)tableContext.getAssociatedPropertyValue("ManagedDevice.RESOURCE_ID");
                String grpNameString = groupMap.get(resourceID);
                if (viewName.equalsIgnoreCase("DeviceLocationList")) {
                    final Long userID = (Long)tableContext.getAssociatedPropertyValue("ManagedUser.MANAGED_USER_ID");
                    final String userGroupName = groupMap.get(userID);
                    if (userGroupName != null) {
                        grpNameString = ((grpNameString == null) ? userGroupName : (grpNameString + "," + userGroupName));
                    }
                }
                grpNameString = ((grpNameString == null) ? "--" : grpNameString);
                columnProperties.put("VALUE", grpNameString);
            }
            if (displyColumn.equals("TaskDetails.TASKNAME")) {
                final String taskName = columnProperties.get("VALUE").toString();
                if (taskName != null && !taskName.equalsIgnoreCase("")) {
                    columnProperties.put("VALUE", DMIAMEncoder.encodeHTML(taskName));
                }
                else if (columnProperties.get("TRIMMED_VALUE").toString() != null) {
                    final String trimmedValue = columnProperties.get("TRIMMED_VALUE").toString();
                    columnProperties.put("TRIMMED_VALUE", DMIAMEncoder.encodeHTML(trimmedValue));
                }
            }
            if (displyColumn.equals("CRSaveViewDetails.DISPLAY_CRVIEWNAME")) {
                final String displayName = columnProperties.get("VALUE").toString();
                columnProperties.put("VALUE", DMIAMEncoder.encodeHTML(displayName));
            }
            if (displyColumn.equals("AaaUser.FIRST_NAME")) {
                final String displayName = columnProperties.get("VALUE").toString();
                columnProperties.put("VALUE", DMIAMEncoder.encodeHTML(displayName));
            }
            if (displyColumn.equals("ScheduleRepTask.EMAIL_ADDRESS")) {
                final String emailAddress = columnProperties.get("VALUE").toString();
                columnProperties.put("VALUE", DMIAMEncoder.encodeHTML(emailAddress));
            }
        }
        catch (final Exception ex) {
            ScheduleReportViewTransformer.out.log(Level.WARNING, "Schedule Shutdown : Exception occured while rendering cell value for schedule shutdown views ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "--");
        }
    }
    
    static {
        ScheduleReportViewTransformer.out = Logger.getLogger("QueryExecutorLogger");
    }
}
