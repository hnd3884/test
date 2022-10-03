package com.adventnet.sym.server.mdm.command;

import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ServerCertificateHandler implements SchedulerExecutionInterface
{
    private static Logger logger;
    
    public void executeTask(final Properties props) {
        ServerCertificateHandler.logger.log(Level.INFO, "Received certificate fetch command");
        this.publishCertificate();
    }
    
    private void publishCertificate() {
        ServerCertificateHandler.logger.log(Level.INFO, "Publishing the certificate to the enrolled devices");
        try {
            final List managedAndroidDevicesList = ManagedDeviceHandler.getInstance().getAndroidManagedDeviceResourceIDs();
            final List notifyDevicesList = new ArrayList();
            for (final Object resourceId : managedAndroidDevicesList) {
                ServerCertificateHandler.logger.log(Level.INFO, "The device resource ID is {0}", resourceId.toString());
                try {
                    final Row row = DBUtil.getRowFromDB("ManagedDevice", "RESOURCE_ID", resourceId);
                    if (row != null) {
                        final Long currentVersionCode = (Long)row.get("AGENT_VERSION_CODE");
                        if (currentVersionCode % 10000L <= 330L) {
                            continue;
                        }
                        ServerCertificateHandler.logger.log(Level.INFO, "Device is found to have a version greater than 330");
                        DeviceCommandRepository.getInstance().addCertificateDistributeCommand((Long)resourceId);
                        notifyDevicesList.add(resourceId);
                    }
                    else {
                        ServerCertificateHandler.logger.log(Level.INFO, "No devices enrolled");
                    }
                }
                catch (final Exception exp) {
                    ServerCertificateHandler.logger.log(Level.SEVERE, "Error while publishing the certificate {0}", exp.toString());
                }
            }
            try {
                NotificationHandler.getInstance().SendNotification(notifyDevicesList, 2);
            }
            catch (final Exception exp2) {
                ServerCertificateHandler.logger.log(Level.SEVERE, "Exception in pushing commands to devices : {0}", exp2.getMessage());
            }
        }
        catch (final Exception e) {
            ServerCertificateHandler.logger.log(Level.SEVERE, "Cannot push certificate to devices ", e);
        }
    }
    
    static {
        ServerCertificateHandler.logger = Logger.getLogger(ServerCertificateHandler.class.getName());
    }
}
