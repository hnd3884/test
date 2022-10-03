package com.me.ems.framework.reports.core;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class ScheduleReportViewTransformer extends DefaultTransformer
{
    private static Logger out;
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final String columnAlias = tableContext.getPropertyName();
        if (columnAlias.equals("Action")) {
            return request.isUserInRole("Report_Write") || request.isUserInRole("PatchMgmt_Write") || request.isUserInRole("MDM_Report_Write");
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void initCellRendering(final TransformerContext context) throws Exception {
        final ViewContext viewCtx = context.getViewContext();
        final String viewID = viewCtx.getUniqueId();
        if (viewID.equalsIgnoreCase("ScheduledReportTasks")) {
            final HashMap nextExecTimeList = (HashMap)viewCtx.getRequest().getAttribute("nextExecTimeMap");
            if (nextExecTimeList == null || nextExecTimeList.isEmpty()) {
                final DMWebClientCommonUtil webClientUtil = new DMWebClientCommonUtil();
                webClientUtil.setNextExecTimeinViewContext(viewCtx);
            }
        }
        super.initCellRendering(context);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final String viewName = tableContext.getViewContext().getUniqueId();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final HttpServletRequest request = tableContext.getViewContext().getRequest();
            final String isExport = request.getParameter("isExport");
            final String displyColumn = tableContext.getPropertyName();
            if ("SCHEDULE_TIME".equals(displyColumn)) {
                final DMWebClientCommonUtil webClientUtil = new DMWebClientCommonUtil();
                final String value = webClientUtil.getNextExecTime(tableContext);
                columnProperties.put("VALUE", value);
            }
            if ("TaskDetails.STATUS".equals(displyColumn) && "ScheduledReportTasks".equals(viewName)) {
                final String status_str = columnProperties.get("VALUE");
                columnProperties.put("VALUE", status_str);
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
        }
        catch (final Exception ex) {
            ScheduleReportViewTransformer.out.log(Level.WARNING, "Schedule Shutdown : Exception occured while rendering cell value for schedule shutdown views ", ex);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            columnProperties.put("VALUE", "--");
        }
    }
    
    static {
        ScheduleReportViewTransformer.out = Logger.getLogger("QueryExecutorLogger");
    }
}
