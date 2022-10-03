package com.me.mdm.server.dep;

import java.util.logging.Level;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;
import com.me.devicemanagement.framework.server.authorization.RoleEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authorization.RoleListener;

public class AdminEnrollmentRoleListener implements RoleListener
{
    public Logger logger;
    
    public AdminEnrollmentRoleListener() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public void roleAdded(final RoleEvent re) {
    }
    
    public void roleDeleted(final RoleEvent re) {
    }
    
    public void roleUpdated(final RoleEvent re) {
        ManagedDeviceListener.mdmlogger.info("Entering AdminEnrollmentRoleListener:roleUpdated");
        try {
            EnrollmentTemplateHandler.updateAdminEnrollmentTemplate(re.roleID);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, exp, () -> "Class: MDMPRoleManagaement | Exception in UpdateRole , Role ID: " + roleEvent.roleID + " | ");
        }
    }
}
