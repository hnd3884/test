package com.me.devicemanagement.onpremise.webclient.admin;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import org.json.simple.JSONObject;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EmberQActionTransformer extends DefaultTransformer
{
    Logger logger;
    
    public EmberQActionTransformer() {
        this.logger = Logger.getLogger("QueueCountLog");
    }
    
    public void renderCell(final TransformerContext context) {
        try {
            final String qName = (String)context.getAssociatedPropertyValue("DCQueueMetaData.QUEUE_NAME");
            final String columnalias = context.getPropertyName();
            final JSONObject payload = new JSONObject();
            super.renderCell(context);
            final HashMap columnProperties = context.getRenderedAttributes();
            final DCQueue queueObj = DCQueueHandler.getQueue(qName);
            final boolean qSuspend = queueObj.isQueueSuspended();
            if (context.getViewContext().getUniqueId().equalsIgnoreCase("QueueCountView")) {
                if ("DCQueueMetaData.Q_AUTO_START".equalsIgnoreCase(columnalias)) {
                    final String value = "" + columnProperties.get("VALUE");
                    if (Boolean.parseBoolean(value)) {
                        columnProperties.put("VALUE", "Enabled");
                    }
                    else {
                        columnProperties.put("VALUE", "Disabled");
                    }
                }
                if ("Status".equalsIgnoreCase(columnalias)) {
                    if (qSuspend) {
                        columnProperties.put("VALUE", "Suspended");
                    }
                    else {
                        columnProperties.put("VALUE", "Running");
                    }
                }
                if ("Action".equalsIgnoreCase(columnalias)) {
                    if (qSuspend) {
                        payload.put((Object)"status", (Object)"Suspended");
                    }
                    else {
                        payload.put((Object)"status", (Object)"Running");
                    }
                    columnProperties.put("PAYLOAD", payload);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "QAutoStartTransformer Exception", ex);
        }
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        String head = tableContext.getDisplayName();
        final boolean isRedis = Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"));
        if ("Queue Size(db)".equalsIgnoreCase(head) && isRedis) {
            head = "Queue Size<b>(redis)</b>";
        }
        if ("Queue Size Pending (db)".equalsIgnoreCase(head) && isRedis) {
            head = "Queue Size Pending<b>(redis)</b>";
        }
        headerProperties.put("VALUE", head);
    }
}
