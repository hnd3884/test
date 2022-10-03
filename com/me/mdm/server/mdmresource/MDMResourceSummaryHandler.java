package com.me.mdm.server.mdmresource;

import com.adventnet.sym.server.mdm.config.ResourceSummaryHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class MDMResourceSummaryHandler extends DCQueueDataProcessor
{
    private static Logger logger;
    
    public long getPartitionFeedId(final DCQueueData qData) {
        long schemaID = 0L;
        try {
            final String schemaName = IdpsFactoryProvider.getIdpsProdEnvAPI().getSchemaName();
            schemaID = Long.valueOf(schemaName.replaceAll("[^0-9]", ""));
        }
        catch (final Exception ex) {
            schemaID = 0L;
            MDMResourceSummaryHandler.logger.log(Level.FINE, "could not get schemaID", ex);
        }
        return schemaID;
    }
    
    public boolean isParallelProcessingQueue() {
        return false;
    }
    
    public void processData(final DCQueueData qData) {
        try {
            final JSONObject qNode = new JSONObject(String.valueOf(qData.queueData));
            final String value;
            final String taskType = value = String.valueOf(qNode.get("Task"));
            switch (value) {
                case "ResourceToProfileSummary": {
                    ResourceSummaryHandler.getInstance().updateResSummary(qNode);
                    break;
                }
                default: {
                    MDMResourceSummaryHandler.logger.log(Level.INFO, "processing task Details {0}", new Object[] { qNode });
                    break;
                }
            }
        }
        catch (final Exception ex) {
            MDMResourceSummaryHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    static {
        MDMResourceSummaryHandler.logger = Logger.getLogger("MDMLogger");
    }
}
