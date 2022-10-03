package com.me.ems.onpremise.summaryserver.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;
import com.me.ems.summaryserver.common.ProbeActionAPI;

public class ProbeActionImpl implements ProbeActionAPI
{
    private static Logger logger;
    private static List<Integer> allowedEventIds;
    private static ProbeActionImpl probeAction;
    
    public static ProbeActionImpl getInstance() {
        if (ProbeActionImpl.probeAction == null) {
            ProbeActionImpl.probeAction = new ProbeActionImpl();
        }
        return ProbeActionImpl.probeAction;
    }
    
    public void addToProbeActionQueue(final Long probeID, final Integer actionModule, final Integer actionType, final Integer actionStatus, final Object actionData) throws Exception {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("actionModule", (Object)actionModule);
            jsonObject.put("actionType", (Object)actionType);
            jsonObject.put("actionStatus", (Object)actionStatus);
            jsonObject.put("actionData", actionData);
            final Long currentTime = System.currentTimeMillis();
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final DCQueueData dcQueueData = new DCQueueData();
            dcQueueData.queueData = jsonObject.toString();
            dcQueueData.fileName = customerID + "-" + probeID + "-" + "ProbeAction" + "-" + currentTime + "-" + actionModule + "-" + actionType + "-" + actionStatus + ".json";
            dcQueueData.postTime = currentTime;
            if (probeID != null) {
                final Map<String, Object> mappingTable = new Hashtable<String, Object>();
                mappingTable.put("SYNC_TIME", currentTime);
                mappingTable.put("PROBE_ID", probeID);
                dcQueueData.queueExtnTableData = mappingTable;
            }
            DCQueueHandler.addToQueue("probe-action-data", dcQueueData, jsonObject.toString());
            ProbeActionImpl.logger.log(Level.INFO, "Data added to probe-action-data queue");
        }
        catch (final Exception e) {
            ProbeActionImpl.logger.log(Level.WARNING, "Exception while addToProbeActionQueue", e);
            throw e;
        }
    }
    
    public void addToProbeActionEventIDs(final List eventIDList) {
        ProbeActionImpl.allowedEventIds.addAll(eventIDList);
    }
    
    public List<Integer> getProbeEventIDs() {
        return ProbeActionImpl.allowedEventIds;
    }
    
    static {
        ProbeActionImpl.logger = Logger.getLogger("ProbeActionUpdateLogger");
        ProbeActionImpl.allowedEventIds = new ArrayList<Integer>();
        ProbeActionImpl.probeAction = null;
    }
}
