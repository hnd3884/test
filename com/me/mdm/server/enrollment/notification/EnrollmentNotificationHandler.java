package com.me.mdm.server.enrollment.notification;

import com.adventnet.persistence.DataAccess;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.HashMap;
import java.util.logging.Logger;

public class EnrollmentNotificationHandler
{
    private static Logger logger;
    private static EnrollmentNotificationHandler notificationHandler;
    
    public static synchronized EnrollmentNotificationHandler getInstance() {
        if (EnrollmentNotificationHandler.notificationHandler == null) {
            EnrollmentNotificationHandler.notificationHandler = new EnrollmentNotificationHandler();
        }
        return EnrollmentNotificationHandler.notificationHandler;
    }
    
    public void addOrUpdateNotificationDetails(final HashMap hmap) throws SyMException {
        try {
            if (!hmap.containsKey("ENROLLMENT_REQUEST_ID") || !hmap.containsKey("IS_SOURCE_TOKEN_UPDATE") || !hmap.containsKey("STATUS")) {
                throw new SyMException(51200, "All inputs are mandatory!", (Throwable)null);
            }
            final Criteria cri = new Criteria(new Column("EnrollmentNotification", "ENROLLMENT_REQUEST_ID"), hmap.get("ENROLLMENT_REQUEST_ID"), 0);
            synchronized (this) {
                DataObject dobj = MDMUtil.getPersistence().get("EnrollmentNotification", cri);
                if (dobj.isEmpty()) {
                    dobj = (DataObject)new WritableDataObject();
                    final Row r = new Row("EnrollmentNotification");
                    r.set("ENROLLMENT_REQUEST_ID", hmap.get("ENROLLMENT_REQUEST_ID"));
                    r.set("IS_SOURCE_TOKEN_UPDATE", hmap.get("IS_SOURCE_TOKEN_UPDATE"));
                    r.set("STATUS", hmap.get("STATUS"));
                    dobj.addRow(r);
                }
                else {
                    final Row r = dobj.getRow("EnrollmentNotification");
                    r.set("IS_SOURCE_TOKEN_UPDATE", hmap.get("IS_SOURCE_TOKEN_UPDATE"));
                    r.set("STATUS", hmap.get("STATUS"));
                    dobj.updateRow(r);
                }
                MDMUtil.getPersistence().update(dobj);
            }
        }
        catch (final SyMException ex) {
            EnrollmentNotificationHandler.logger.log(Level.SEVERE, "Exception while adding/ updating the ios notification", (Throwable)ex);
            throw ex;
        }
        catch (final Exception ex2) {
            EnrollmentNotificationHandler.logger.log(Level.SEVERE, "Exception while adding/ updating the ios notification", ex2);
        }
    }
    
    public void updateNotificationStatus(final long erid, final int status) {
        try {
            final UpdateQuery uquery = (UpdateQuery)new UpdateQueryImpl("EnrollmentNotification");
            uquery.setUpdateColumn("STATUS", (Object)status);
            uquery.setCriteria(new Criteria(Column.getColumn("EnrollmentNotification", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
            synchronized (this) {
                MDMUtil.getPersistence().update(uquery);
            }
        }
        catch (final Exception ex) {
            EnrollmentNotificationHandler.logger.log(Level.SEVERE, "Exception while updating the ios notification status", ex);
        }
    }
    
    public int getNotificationStatus(final Long erid) {
        Integer status = null;
        try {
            status = (Integer)DBUtil.getValueFromDB("EnrollmentNotification", "ENROLLMENT_REQUEST_ID", (Object)erid, "STATUS");
        }
        catch (final Exception ex) {
            EnrollmentNotificationHandler.logger.log(Level.SEVERE, "Exception raised while getting status of the iOS notification erid : {0} {1}", new Object[] { erid, ex.getLocalizedMessage() });
            return -1;
        }
        if (status == null) {
            return -1;
        }
        return status;
    }
    
    public JSONObject getNotificationDetails(final Long erid) {
        try {
            final Row r = DBUtil.getRowFromDB("EnrollmentNotification", "ENROLLMENT_REQUEST_ID", (Object)erid);
            final JSONObject json = new JSONObject();
            if (r != null) {
                json.put("ENROLLMENT_REQUEST_ID", (Object)r.get("ENROLLMENT_REQUEST_ID"));
                json.put("IS_SOURCE_TOKEN_UPDATE", (Object)r.get("IS_SOURCE_TOKEN_UPDATE"));
                json.put("STATUS", (Object)r.get("STATUS"));
            }
            return json;
        }
        catch (final Exception ex) {
            EnrollmentNotificationHandler.logger.log(Level.SEVERE, "Exception raised while getting iOS notification roe for erid : {0} {1}", new Object[] { erid, ex.getLocalizedMessage() });
            return null;
        }
    }
    
    public void removeNotification(final Long erid) {
        final Criteria cri = new Criteria(new Column("EnrollmentNotification", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
        try {
            synchronized (this) {
                DataAccess.delete(cri);
            }
        }
        catch (final Exception ex) {
            EnrollmentNotificationHandler.logger.log(Level.SEVERE, "Exception while notification request", ex);
        }
    }
    
    static {
        EnrollmentNotificationHandler.logger = Logger.getLogger("MDMLogger");
        EnrollmentNotificationHandler.notificationHandler = null;
    }
}
