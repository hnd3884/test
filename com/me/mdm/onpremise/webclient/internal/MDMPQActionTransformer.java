package com.me.mdm.onpremise.webclient.internal;

import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.webclient.admin.QActionTransformer;

public class MDMPQActionTransformer extends QActionTransformer
{
    Logger logger;
    
    public MDMPQActionTransformer() {
        this.logger = Logger.getLogger("QueueCountLog");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (columnalias.equalsIgnoreCase("Action")) {
            final int reportType = tableContext.getViewContext().getRenderType();
            if (reportType != 4) {
                return false;
            }
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext context) {
        long qcount = 0L;
        long qsizememory = 0L;
        long qsizepending = 0L;
        try {
            super.renderCell(context);
            final String qName = (String)context.getAssociatedPropertyValue("DCQueueMetaData.QUEUE_NAME");
            final String qTabName = (String)context.getAssociatedPropertyValue("DCQueueMetaData.QUEUE_TABLE_NAME");
            final long qTabId = Long.valueOf(context.getAssociatedPropertyValue("DCQueueMetaData.Q_METADATA_ID").toString());
            if (context.getAssociatedPropertyValue("DCQueueSummary.Q_COUNT") != null) {
                qcount = Long.parseLong(context.getAssociatedPropertyValue("DCQueueSummary.Q_COUNT").toString());
            }
            if (context.getAssociatedPropertyValue("DCQueueSummary.Q_SIZE_IN_MEMORY") != null) {
                qsizememory = Long.parseLong(context.getAssociatedPropertyValue("DCQueueSummary.Q_SIZE_IN_MEMORY").toString());
            }
            if (context.getAssociatedPropertyValue("DCQueueSummary.QUEUE_SIZE_PENDING_IN_DB") != null) {
                qsizepending = Long.parseLong(context.getAssociatedPropertyValue("DCQueueSummary.QUEUE_SIZE_PENDING_IN_DB").toString());
            }
            final String columnalias = context.getPropertyName();
            final HashMap columnProperties = context.getRenderedAttributes();
            final boolean isExport = false;
            final int reportType = context.getViewContext().getRenderType();
            boolean export = false;
            if (reportType != 4) {
                export = true;
            }
            if (context.getViewContext().getUniqueId().equalsIgnoreCase("QueueCountView")) {
                if (columnalias.equalsIgnoreCase("Action")) {
                    final DCQueue queueObj = DCQueueHandler.getQueue(qName);
                    final boolean qSuspend = queueObj.isQueueSuspended();
                    String status;
                    if (qSuspend) {
                        status = "Suspended";
                    }
                    else {
                        status = "Running";
                    }
                    final JSONObject payload = new JSONObject();
                    if (status.equalsIgnoreCase("Suspended")) {
                        payload.put("status", (Object)"Suspended");
                    }
                    else if (status.equalsIgnoreCase("Running")) {
                        payload.put("status", (Object)"Running");
                    }
                    payload.put("qName", (Object)qName);
                    payload.put("qTabName", (Object)qTabName);
                    payload.put("qTabId", qTabId);
                    columnProperties.put("PAYLOAD", payload);
                }
                else if (columnalias.equalsIgnoreCase("DCQueueSummary.Q_COUNT")) {
                    columnProperties.put("VALUE", qcount);
                }
                else if (columnalias.equalsIgnoreCase("DCQueueSummary.Q_SIZE_IN_MEMORY")) {
                    columnProperties.put("VALUE", qsizememory);
                }
                else if (columnalias.equalsIgnoreCase("DCQueueSummary.QUEUE_SIZE_PENDING_IN_DB")) {
                    columnProperties.put("VALUE", qsizepending);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception ocurred in MDMPQActionTransformer", ex);
        }
    }
}
