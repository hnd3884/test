package com.me.mdm.server.updates.osupdates;

import java.util.Collection;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Logger;

public class OSUpdatesProcessor
{
    private static final Logger LOGGER;
    private ExtendedOSDetailsDataHandler extnDataHandler;
    private OSUpdatesEventListener listener;
    private ResourcesMissingUpdatesListener resMissingListener;
    private ArrayList<Long> resourceListToCheck;
    
    public OSUpdatesProcessor() {
        this.extnDataHandler = null;
        this.listener = null;
        this.resMissingListener = null;
        this.resourceListToCheck = new ArrayList<Long>();
    }
    
    public void processOSUpdateDetails(final JSONObject updateDetails, final OSUpdateCriteriaEvaluator evaluator) throws Exception {
        final OSUpdatesDataHandler dataHandler = new OSUpdatesDataHandler();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedUpdates"));
        final Join osUpdateJoin = new Join("ManagedUpdates", "OSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
        selectQuery.addJoin(osUpdateJoin);
        if (this.extnDataHandler != null) {
            selectQuery.addJoin(this.extnDataHandler.getOSUpdateDetailsExtnJoin());
        }
        final Criteria uniqueOSCriteria = evaluator.addedCriteria(updateDetails);
        if (uniqueOSCriteria != null) {
            selectQuery.setCriteria(uniqueOSCriteria);
        }
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        final int type = evaluator.evaluateAvailableDataForGivenCriteria(updateDetails, dO);
        OSUpdatesProcessor.LOGGER.log(Level.INFO, "OS update type is {0} for resources: {1}", new Object[] { type, this.resourceListToCheck });
        Long updateID = null;
        if (type == 1) {
            updateID = dataHandler.addNewOSUpdate(updateDetails, this.extnDataHandler);
            this.notifyNewUpdate(updateID);
        }
        else {
            if (type != 2) {
                throw new UnsupportedOperationException("OSUpdateCriteriaEvaluator returned unknown integer type = " + type);
            }
            updateID = (Long)dO.getFirstRow("ManagedUpdates").get("UPDATE_ID");
        }
        this.processResources(updateID);
    }
    
    private void processResources(final Long updateID) throws Exception {
        final ResourceOSUpdateDataHandler resDataHandler = new ResourceOSUpdateDataHandler();
        if (!this.resourceListToCheck.isEmpty()) {
            final ArrayList<Long> resources = resDataHandler.getResourcesMissingUpdate(this.resourceListToCheck, updateID);
            OSUpdatesProcessor.LOGGER.log(Level.INFO, "Processing resources for update. UpdateID - {0} | Resources: {1}", new Object[] { updateID, resources });
            if (!resources.isEmpty()) {
                this.resMissingListener.onResourcesMissingUpdate(resources, updateID);
            }
        }
    }
    
    public void setResourcesToCheckMissingUpdate(final ArrayList<Long> resourceList, final ResourcesMissingUpdatesListener resMissingListener) {
        this.resourceListToCheck.addAll(resourceList);
        this.resMissingListener = resMissingListener;
    }
    
    private void notifyNewUpdate(final Long updateID) {
        if (this.listener != null) {
            this.listener.onNewOSUpdateAdded(updateID);
        }
    }
    
    public void setNewUpdateEventsListener(final OSUpdatesEventListener listener) {
        this.listener = listener;
    }
    
    public void setExtnOSDetailsDataHandler(final ExtendedOSDetailsDataHandler extnDataHandler) {
        this.extnDataHandler = extnDataHandler;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
