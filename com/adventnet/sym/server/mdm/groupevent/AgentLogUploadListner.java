package com.adventnet.sym.server.mdm.groupevent;

import java.util.List;
import com.adventnet.sym.server.devicemanagement.framework.groupevent.GroupEventNotifier;
import java.util.logging.Level;
import com.me.mdm.server.support.SupportFileCreation;
import java.util.logging.Logger;
import com.adventnet.sym.server.devicemanagement.framework.groupevent.GroupEventProperties;
import java.io.Serializable;
import com.adventnet.sym.server.devicemanagement.framework.groupevent.GroupEventListener;

public class AgentLogUploadListner implements GroupEventListener, Serializable
{
    public void onGroupEventCompleted(final GroupEventProperties groupEventProperties) throws Exception {
        final Logger logger = Logger.getLogger(AgentLogUploadListner.class.getName());
        final SupportFileCreation supportObj = SupportFileCreation.getInstance();
        logger.log(Level.INFO, "Log upload group event completed");
        try {
            if (supportObj.getMdmAgentLogInitiatedCount() == supportObj.getMdmAgentLogUplodedCount()) {
                logger.log(Level.INFO, "ALL Device Log uploades sucessfully");
            }
            supportObj.onUpload();
        }
        catch (final Exception e) {
            logger.log(Level.SEVERE, "Exception : ", e);
            GroupEventNotifier.setGroupEventNotifierStatus("MDM_AGENT_LOG_UPLOAD", "failed");
        }
    }
    
    public void onGroupEventTimeOut(final GroupEventProperties groupEventProperties) throws Exception {
        final Logger logger = Logger.getLogger(AgentLogUploadListner.class.getName());
        final SupportFileCreation supportObj = SupportFileCreation.getInstance();
        try {
            logger.log(Level.INFO, "Log upload group event timed out");
            supportObj.onUpload();
        }
        catch (final Exception e) {
            logger.log(Level.SEVERE, "Exception : ", e);
            GroupEventNotifier.setGroupEventNotifierStatus("MDM_AGENT_LOG_UPLOAD", "failed");
        }
    }
    
    public void onActionCompleted(final GroupEventProperties groupEventProperties) throws Exception {
    }
    
    public void onActionCleaned(final List<GroupEventProperties> groupEventPropertiesList) throws Exception {
    }
}
