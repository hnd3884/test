package com.me.mdm.server.easmanagement;

import com.me.mdm.server.easmanagement.pss.PSSException;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.mdm.server.easmanagement.pss.PSSMgmt;
import com.adventnet.ds.query.SelectQuery;
import org.json.simple.JSONObject;
import com.me.mdm.server.factory.ConditionalExchangeAccessAPI;

public class CEAopImpl implements ConditionalExchangeAccessAPI
{
    public void addTaskToQueue(final JSONObject qData) {
        EASMgmt.getInstance().addTaskToQueue(qData);
    }
    
    public void customizeQuery(final SelectQuery selectQuery, final Long serverID) {
    }
    
    public int getPSSstate(final Long esServerID) {
        return PSSMgmt.getInstance().getPSSstate(esServerID);
    }
    
    public boolean getReadFromFile() {
        return true;
    }
    
    private void initiateSessionMonitor(final Long easServerID) {
        if (EASSessionMonitor.getInstance().getSessionMonitorThreadCount() < 2) {
            final DCQueueData queueData = new DCQueueData();
            queueData.postTime = System.currentTimeMillis();
            queueData.queueData = easServerID;
            try {
                final DCQueue queue = DCQueueHandler.getQueue("eas-session");
                queue.addToQueue(queueData);
                EASSessionMonitor.getInstance().increaseSessionMonitorThreadCount();
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean installEXOV2Module(final Long esServerID) {
        final JSONObject ceaSyncDetails = new JSONObject();
        ceaSyncDetails.put((Object)"EAS_Sync_Status_ID", (Object)esServerID);
        ceaSyncDetails.put((Object)"REMARKS", (Object)MDMI18N.getI18Nmsg("mdm.cea.install.exo.v2.init", new Object[] { "0" }));
        EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(ceaSyncDetails);
        final boolean installedEXOV2Module = PSSMgmt.getInstance().installEXOV2Module(esServerID);
        return installedEXOV2Module;
    }
    
    public void incrementTaskList(final Long easServerID) {
        PSSMgmt.getInstance().incrementTaskList(easServerID);
    }
    
    public void initiateSession(final JSONObject easTaskProps, final JSONObject exServerDetails) throws PSSException {
        final String taskType = (String)easTaskProps.get((Object)"TASK_TYPE");
        final Long easServerID = (Long)easTaskProps.get((Object)"EAS_SERVER_ID");
        if (!taskType.equalsIgnoreCase("CEA_REMOVAL") && !taskType.equalsIgnoreCase("ACKNOWLEDGEMENT") && !taskType.equalsIgnoreCase("CEA_POLICY_REMOVAL")) {
            final boolean installEXOV2 = Boolean.valueOf(String.valueOf(easTaskProps.getOrDefault((Object)"install_exo_v2", (Object)false)));
            if (installEXOV2) {
                final boolean installed = this.installEXOV2Module(easServerID);
                if (installed) {
                    easTaskProps.remove((Object)"install_exo_v2");
                }
            }
            PSSMgmt.getInstance().startSession(exServerDetails);
            PSSMgmt.getInstance().updateSessionActivity(easServerID);
            this.initiateSessionMonitor(easServerID);
        }
    }
    
    public void decrementTaskList(final Long easServerID) {
        PSSMgmt.getInstance().decrementTaskList(easServerID);
    }
    
    public void closeSession(final Long easServerID) {
        PSSMgmt.getInstance().closeSession(easServerID);
    }
    
    public void closeAllSessions() {
        PSSMgmt.getInstance().closeAllSessions();
    }
}
