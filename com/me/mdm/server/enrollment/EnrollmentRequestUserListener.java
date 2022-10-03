package com.me.mdm.server.enrollment;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.UserEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;

public class EnrollmentRequestUserListener implements ManagedUserListener
{
    public static Logger logger;
    
    @Override
    public void userAdded(final UserEvent userEvent) {
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
    }
    
    @Override
    public void userDetailsModified(final UserEvent userEvent) {
    }
    
    @Override
    public void userTrashed(final UserEvent userEvent) {
        EnrollmentRequestUserListener.logger.log(Level.INFO, " inside user trashed in EnrollmentRequestUserListener for user {0}", userEvent.resourceID);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DeviceEnrollmentRequest");
        deleteQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        final Criteria userCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), (Object)userEvent.resourceID, 0);
        final Criteria statusCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 0, 1 }, 8);
        final Criteria noDevice = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)null, 0);
        deleteQuery.setCriteria(userCri.and(statusCri).and(noDevice));
        try {
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final DataAccessException e) {
            EnrollmentRequestUserListener.logger.log(Level.SEVERE, "error in deleting trashed user enrollment request", (Throwable)e);
        }
    }
    
    static {
        EnrollmentRequestUserListener.logger = Logger.getLogger("MDMEnrollment");
    }
}
