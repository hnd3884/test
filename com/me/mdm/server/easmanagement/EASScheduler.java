package com.me.mdm.server.easmanagement;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import org.json.simple.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class EASScheduler implements SchedulerExecutionInterface
{
    public void executeTask(final Properties props) {
        EASMgmt.logger.log(Level.INFO, "executing easscheduler");
        try {
            final DataObject dObj = MDMUtil.getPersistence().get("EASServerDetails", (Criteria)null);
            final Iterator iterator = dObj.getRows("EASServerDetails");
            while (iterator != null && iterator.hasNext()) {
                final Row row = iterator.next();
                final Long easServerID = (Long)row.get("EAS_SERVER_ID");
                final JSONObject exServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
                if (exServerDetails != null && exServerDetails.containsKey((Object)"SYNC_STATUS")) {
                    int ongoingSyncStatus = 1;
                    final String syncStatusStr = String.valueOf(exServerDetails.get((Object)"SYNC_STATUS"));
                    try {
                        ongoingSyncStatus = Integer.valueOf(syncStatusStr);
                    }
                    catch (final Exception ex) {
                        EASMgmt.logger.log(Level.INFO, "could not parse " + ongoingSyncStatus, ex);
                    }
                    if (ongoingSyncStatus != 1) {
                        EASMgmt.logger.log(Level.INFO, "adding sync task from daily scheduler");
                        final JSONObject syncRequestDetails = new JSONObject();
                        syncRequestDetails.put((Object)"SEND_GRACE_MAILS", (Object)Boolean.TRUE);
                        syncRequestDetails.put((Object)"EAS_SERVER_ID", (Object)String.valueOf(easServerID));
                        EASMgmt.getInstance().handleSyncRequest(syncRequestDetails);
                    }
                    else {
                        EASMgmt.logger.log(Level.INFO, "skiping daily scheduled sync because existing sync has not completed");
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            EASMgmt.logger.log(Level.SEVERE, null, (Throwable)e);
        }
    }
}
