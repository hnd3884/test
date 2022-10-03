package com.me.mdm.server.audit.task;

import com.me.mdm.server.audit.AuditLogHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class AuditLogTask extends DCQueueDataProcessor
{
    private Logger profileLogger;
    
    public AuditLogTask() {
        this.profileLogger = Logger.getLogger("MDMProfileConfigLogger");
    }
    
    public void processData(final DCQueueData qData) {
        try {
            this.profileLogger.log(Level.INFO, "Execute task in Audit log task begins with props {0}", new Object[] { qData.queueData.toString() });
            AuditLogHandler.getInstance(qData).addEventLogEntry(qData);
        }
        catch (final Exception ex) {
            this.profileLogger.log(Level.SEVERE, "Exception in audit log task", ex);
        }
    }
}
