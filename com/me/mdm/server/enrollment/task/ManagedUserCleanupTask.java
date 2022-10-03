package com.me.mdm.server.enrollment.task;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ManagedUserCleanupTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties taskProps) {
        try {
            final EnrollmentCleanupTask cleanupTask = new EnrollmentCleanupTask();
            cleanupTask.executeTask();
            final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerIDs != null) {
                for (final Long customerID : customerIDs) {
                    final Long[] userList = ManagedUserHandler.getInstance().getAllUsersWhoCanBeCleaned(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0), false);
                    Logger.getLogger("MDMEnrollment").log(Level.INFO, "Debug log for ERID missing... deleting user:{0}", userList.toString());
                    if (userList != null && userList.length != 0) {
                        ManagedUserHandler.getInstance().removeUser(userList, customerID, MDMUtil.getAdminUserId());
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception while cleaning up users: {0}", ex);
        }
    }
}
