package com.me.devicemanagement.onpremise.webclient.admin;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class QActionTransformer extends DefaultTransformer
{
    Logger logger;
    
    public QActionTransformer() {
        this.logger = Logger.getLogger("QueueCountLog");
    }
    
    public void renderCell(final TransformerContext context) {
        try {
            final HttpServletRequest request = context.getRequest();
            final String qName = (String)context.getAssociatedPropertyValue("DCQueueMetaData.QUEUE_NAME");
            final String qTabName = (String)context.getAssociatedPropertyValue("DCQueueMetaData.QUEUE_TABLE_NAME");
            final long qTabId = Long.valueOf(context.getAssociatedPropertyValue("DCQueueMetaData.Q_METADATA_ID").toString());
            final String columnalias = context.getPropertyName();
            super.renderCell(context);
            final HashMap columnProperties = context.getRenderedAttributes();
            if (context.getViewContext().getUniqueId().equalsIgnoreCase("QueueCountView")) {
                if (columnalias.equalsIgnoreCase("DCQueueMetaData.Q_AUTO_START")) {
                    final String value = "" + columnProperties.get("VALUE");
                    if (Boolean.parseBoolean(value)) {
                        columnProperties.put("VALUE", "Enabled");
                    }
                    else {
                        columnProperties.put("VALUE", "Disabled");
                    }
                }
                if (columnalias.equalsIgnoreCase("Status")) {
                    final DCQueue queueObj = DCQueueHandler.getQueue(qName);
                    final boolean qSuspend = queueObj.isQueueSuspended();
                    if (qSuspend) {
                        columnProperties.put("VALUE", "Suspended");
                        request.setAttribute("status", (Object)"Suspended");
                    }
                    else {
                        columnProperties.put("VALUE", "Running");
                        request.setAttribute("status", (Object)"Running");
                    }
                }
                if (columnalias.equalsIgnoreCase("Action")) {
                    final String status = (String)request.getAttribute("status");
                    if (status.equalsIgnoreCase("Suspended")) {
                        columnProperties.put("VALUE", "<a href=\"javascript:refreshFun('/dcqueueCount.do?actionToCall=refreshqCount&qTabName=" + qTabName + "&qTabId=" + qTabId + "&qName=" + qName + "');\"><img title=\"Refresh\" src=\"/images/refresh_icon.gif\"/></a>&nbsp;&nbsp;&nbsp;<a href=\"javascript:refreshFun('/dcqueueCount.do?actionToCall=resumeqCount&qName=" + qName + "');\"><img title=\"Resume\" src=\"/images/resume.gif\"/></a>");
                    }
                    else if (status.equalsIgnoreCase("Running")) {
                        columnProperties.put("VALUE", "<a href=\"javascript:refreshFun('/dcqueueCount.do?actionToCall=refreshqCount&qTabName=" + qTabName + "&qTabId=" + qTabId + "&qName=" + qName + "');\"><img title=\"Refresh\" src=\"/images/refresh_icon.gif\"/></a>&nbsp;&nbsp;&nbsp;<a href=\"javascript:refreshFun('/dcqueueCount.do?actionToCall=suspendqCount&qName=" + qName + "');\"><img title=\"Suspend\" src=\"/images/suspend.png\"/></a>");
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            this.logger.log(Level.WARNING, "QAutoStartTransformer Exception" + ex);
        }
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        String head = tableContext.getDisplayName();
        final boolean isRedis = Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"));
        if (head.equalsIgnoreCase("Queue Size(db)") && isRedis) {
            head = "Queue Size<b>(redis)</b>";
        }
        if (head.equalsIgnoreCase("Queue Size Pending (db)") && isRedis) {
            head = "Queue Size Pending<b>(redis)</b>";
        }
        headerProperties.put("VALUE", head);
    }
}
