package com.me.mdm.server.enrollment.task;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResendEnrollmentRequestTask
{
    private static Logger logger;
    
    public void executeTask() {
        ResendEnrollmentRequestTask.logger.log(Level.INFO, "Entering Resend Enrollment Request Task");
        try {
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ResendEnrollRequest")) {
                final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
                squery.addJoin(new Join("DeviceEnrollmentRequest", "OTPPassword", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
                squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
                squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "AUTH_MODE"));
                squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "USER_ID"));
                final Hashtable ht = DateTimeUtil.determine_From_To_Times("today");
                final Long tommorrow = ht.get("date2") + 86400000L;
                final Criteria invitation = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)1, 0);
                final Criteria pendingRequest = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 1, 0 }, 8);
                final Criteria expiryTime = new Criteria(Column.getColumn("OTPPassword", "EXPIRE_TIME"), (Object)tommorrow, 6);
                final Criteria criteria = invitation.and(pendingRequest).and(expiryTime);
                squery.setCriteria(criteria);
                final DataObject dobj = MDMUtil.getReadOnlyPersistence().get(squery);
                final Iterator<Row> iterator = dobj.getRows("DeviceEnrollmentRequest");
                while (iterator.hasNext()) {
                    final Properties properties = new Properties();
                    final Row row = iterator.next();
                    ((Hashtable<String, Object>)properties).put("ENROLLMENT_REQUEST_ID", row.get("ENROLLMENT_REQUEST_ID"));
                    ((Hashtable<String, Object>)properties).put("USER_ID", row.get("USER_ID"));
                    ((Hashtable<String, Object>)properties).put("AUTH_MODE", row.get("AUTH_MODE"));
                    MDMEnrollmentRequestHandler.getInstance().resendEnrollmentRequest(properties);
                }
            }
        }
        catch (final Exception ex) {
            ResendEnrollmentRequestTask.logger.log(Level.SEVERE, "Exception in ResendEnrollmentRequestTask :{0}", ex);
        }
    }
    
    static {
        ResendEnrollmentRequestTask.logger = Logger.getLogger("MDMEnrollment");
    }
}
