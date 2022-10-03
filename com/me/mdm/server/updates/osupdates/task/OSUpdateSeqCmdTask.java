package com.me.mdm.server.updates.osupdates.task;

import com.me.mdm.server.updates.osupdates.ios.IOSOSUpdateHandler;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class OSUpdateSeqCmdTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    public void executeTask(final Properties props) {
        try {
            final String isNeedToExecute = MDMUtil.getSyMParameter("OSUpdateSeqCmdRedistributeNeeded");
            if (!MDMStringUtils.isEmpty(isNeedToExecute) && Boolean.valueOf(isNeedToExecute)) {
                OSUpdateSeqCmdTask.LOGGER.log(Level.INFO, "Entered the OS Update Seq Task");
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
                selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                selectQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, 2));
                final Criteria osUpdateCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)3, 0);
                final Criteria platformCriteria = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)1, 0);
                selectQuery.setCriteria(osUpdateCriteria.and(platformCriteria));
                selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
                selectQuery.addSelectColumn(new Column("Profile", "LAST_MODIFIED_BY"));
                selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "*"));
                selectQuery.addSelectColumn(new Column("RecentProfileToColln", "PROFILE_ID"));
                selectQuery.addSelectColumn(new Column("RecentProfileToColln", "COLLECTION_ID"));
                selectQuery.addSelectColumn(new Column("AaaUser", "USER_ID"));
                selectQuery.addSelectColumn(new Column("AaaUser", "FIRST_NAME"));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("Profile");
                    while (iterator.hasNext()) {
                        final Row profileRow = iterator.next();
                        final Long profileId = (Long)profileRow.get("PROFILE_ID");
                        final Long userId = (Long)profileRow.get("LAST_MODIFIED_BY");
                        final Row customerRow = dataObject.getRow("ProfileToCustomerRel", new Criteria(new Column("ProfileToCustomerRel", "PROFILE_ID"), (Object)profileId, 0));
                        final Long customerId = (Long)customerRow.get("CUSTOMER_ID");
                        final Row userRow = dataObject.getRow("AaaUser", new Criteria(new Column("AaaUser", "USER_ID"), (Object)userId, 0));
                        final String loggedOnName = (String)userRow.get("FIRST_NAME");
                        final Row collectionRow = dataObject.getRow("RecentProfileToColln", new Criteria(new Column("RecentProfileToColln", "PROFILE_ID"), (Object)profileId, 0));
                        final Long collectionId = (Long)collectionRow.get("COLLECTION_ID");
                        this.createSeqCommandForCollection(collectionId, customerId);
                        final JSONObject msgHeaderJSON = new JSONObject();
                        msgHeaderJSON.put("loggedOnUserName", (Object)loggedOnName);
                        msgHeaderJSON.put("CUSTOMER_ID", (Object)customerId);
                        msgHeaderJSON.put("USER_ID", (Object)userId);
                        final JSONObject distributeOSUpdatePolicyJSON = new JSONObject();
                        distributeOSUpdatePolicyJSON.put("PROFILE_ID", (Object)profileId);
                        distributeOSUpdatePolicyJSON.put("COLLECTION_ID", (Object)collectionId);
                        distributeOSUpdatePolicyJSON.put("isNewProfileDetected", false);
                        OSUpdateSeqCmdTask.LOGGER.log(Level.INFO, "Going to publish the profile from Os update Seq Task. MsgHeaderJSon:{0} & distributeOSUpdatePolicyJSON:{1}", new Object[] { msgHeaderJSON, distributeOSUpdatePolicyJSON });
                        new OSUpdatePolicyHandler().publishOSUpdatePolicy(msgHeaderJSON, distributeOSUpdatePolicyJSON);
                    }
                    MDMUtil.updateSyMParameter("OSUpdateSeqCmdRedistributeNeeded", "false");
                }
                OSUpdateSeqCmdTask.LOGGER.log(Level.INFO, "Completed the OS Update Seq Cmd Task");
            }
        }
        catch (final Exception ex) {
            OSUpdateSeqCmdTask.LOGGER.log(Level.SEVERE, "Exception in OSUpdate SeqCMd Task", ex);
        }
    }
    
    private void createSeqCommandForCollection(final Long collectionId, final Long customerId) {
        final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerId, "profiles", collectionId);
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerId, collectionId);
        final IOSOSUpdateHandler iososUpdateHandler = new IOSOSUpdateHandler();
        OSUpdateSeqCmdTask.LOGGER.log(Level.FINE, "Going to create OS Update Sequential XMl from seq task. Collection Id:{0} & CustomerID:{1}", new Object[] { collectionId, customerId });
        final JSONObject iosCmdObject = iososUpdateHandler.addIOSOSUpdatePolicyXML(collectionId, mdmProfileDir, mdmProfileRelativeDirPath);
        iososUpdateHandler.addOSUpdateCommand(collectionId, iosCmdObject);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
