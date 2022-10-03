package com.me.mdm.server.security.profile.task;

import java.util.Hashtable;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashSet;
import java.util.Set;
import com.me.mdm.server.security.profile.PayloadSecretFieldsMigrationHandler;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class PayloadSecretFieldsMigrationTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties props) {
        try {
            final PayloadSecretFieldsMigrationHandler payloadSecretFieldsMigrationHandler = new PayloadSecretFieldsMigrationHandler();
            final Set<Long> collectionIds = props.containsKey("collectionIds") ? ((Hashtable<K, Set<Long>>)props).get("collectionIds") : new HashSet<Long>();
            payloadSecretFieldsMigrationHandler.resetSecretFieldColumns(collectionIds);
            payloadSecretFieldsMigrationHandler.rePublishProfilesWithSecretFields(collectionIds);
            MDMUtil.deleteSyMParameter("resetSecretFieldsAndRepublishProfileTask");
        }
        catch (final Exception ex) {
            PayloadSecretFieldsMigrationTask.logger.log(Level.SEVERE, "Exception while executing task PayloadSecretFieldsMigrationTask", ex);
        }
    }
    
    static {
        PayloadSecretFieldsMigrationTask.logger = Logger.getLogger("MDMDeviceSecurityLogger");
    }
}
