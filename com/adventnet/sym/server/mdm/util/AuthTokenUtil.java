package com.adventnet.sym.server.mdm.util;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.adventnet.sym.server.mdm.queue.MDMDataQueueUtil;
import com.adventnet.sym.server.mdm.queue.QueueName;
import java.util.Map;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthTokenUtil
{
    public static final Logger LOGGER;
    protected static final Logger QUEUE_LOGGER;
    public static final String ERID = "erid";
    protected static final String SEPARATOR = "\t";
    
    public static void addToQueue(Long erid, final Long customerId, final String udid) {
        try {
            Long resID = null;
            if (erid == null) {
                AuthTokenUtil.LOGGER.log(Level.INFO, "Erid is null. So going to fetch using UDID..");
                if (udid.isEmpty()) {
                    AuthTokenUtil.LOGGER.log(Level.INFO, "Udid is also empty. So going to return directly..");
                    return;
                }
                resID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
                if (resID == null) {
                    AuthTokenUtil.LOGGER.log(Level.WARNING, "Resource ID is NULL for the UDID {0}", udid);
                    return;
                }
                erid = MDMEnrollmentUtil.getInstance().getEnrollRequestIDFromManagedDeviceID(resID);
            }
            final long postTime = System.currentTimeMillis();
            final String qFileName = customerId + "-" + erid + "-" + postTime + ".txt";
            AuthTokenUtil.LOGGER.log(Level.INFO, "Device checkIn with authtoken and going to add to the queue. erid {0} udid {1} cxId {2}", new Object[] { erid, udid, customerId });
            final HashMap<String, Object> qDataMap = new HashMap<String, Object>();
            qDataMap.put("CUSTOMER_ID", customerId);
            qDataMap.put("RESOURCE_ID", (resID != null) ? resID : ManagedDeviceHandler.getInstance().getManagedDeviceIDFromEnrollRequestID(erid));
            final DCQueueData queueData = new DCQueueData();
            queueData.fileName = qFileName;
            queueData.postTime = postTime;
            queueData.customerID = customerId;
            queueData.queueDataType = 900;
            queueData.queueExtnTableData = (Map)qDataMap.clone();
            qDataMap.put("erid", erid);
            queueData.queueData = qDataMap;
            final String queueName = QueueName.OTHERS.getQueueName();
            AuthTokenUtil.QUEUE_LOGGER.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}{6}{7}", new Object[] { queueName, "\t", "\t", queueData.fileName, "\t", MDMDataQueueUtil.getInstance().getPlatformNameForLogging(queueData.queueDataType), "\t", String.valueOf(postTime) });
            final DCQueue queue = DCQueueHandler.getQueue(queueName);
            queue.addToQueue(queueData);
        }
        catch (final Exception ex) {
            AuthTokenUtil.LOGGER.log(Level.WARNING, "Exception in AuthToken util addtoqueue: ", ex);
        }
    }
    
    public static void processAuthTokenCheckin(final DCQueueData qData) {
        try {
            final HashMap qDataMap = (HashMap)qData.queueData;
            final Long erid = qDataMap.get("erid");
            addEridToEnrollmentRequestWithAuthToken(erid);
        }
        catch (final Exception e) {
            AuthTokenUtil.LOGGER.log(Level.SEVERE, "Exception in AuthToken util: ", e);
        }
    }
    
    private static void addEridToEnrollmentRequestWithAuthToken(final Long erid) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentRequestWithAuthToken"));
            final Criteria criteria = new Criteria(Column.getColumn("EnrollmentRequestWithAuthToken", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
            sq.setCriteria(criteria);
            sq.addSelectColumn(Column.getColumn("EnrollmentRequestWithAuthToken", "ENROLLMENT_REQUEST_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(sq);
            if (dataObject.isEmpty()) {
                final Row row = new Row("EnrollmentRequestWithAuthToken");
                row.set("ENROLLMENT_REQUEST_ID", (Object)erid);
                dataObject.addRow(row);
                MDMUtil.getPersistenceLite().update(dataObject);
                AuthTokenUtil.LOGGER.log(Level.INFO, "Erid added addEridToEnrollmentRequestWithAuthToken {0}..", erid);
            }
            else {
                AuthTokenUtil.LOGGER.log(Level.INFO, "Erid already present addEridToEnrollmentRequestWithAuthToken {0}..", erid);
            }
        }
        catch (final Exception e) {
            AuthTokenUtil.LOGGER.log(Level.SEVERE, "Exception in AuthToken util add erid to DB: ", e);
        }
    }
    
    public static void checkForAuthTokenDevices() {
        try {
            AuthTokenUtil.LOGGER.log(Level.INFO, "Going to run checkForAuthTokenDevices and open the msg box if necessary");
            final Long[] custIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (custIds != null) {
                for (final Long custID : custIds) {
                    final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentRequestWithAuthToken"));
                    sq.addJoin(new Join("EnrollmentRequestWithAuthToken", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
                    sq.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                    sq.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                    final Criteria custCri = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)custID, 0);
                    final Criteria managedStatusCri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer[] { 2, 5 }, 8);
                    sq.setCriteria(custCri.and(managedStatusCri));
                    sq.addSelectColumn(Column.getColumn("EnrollmentRequestWithAuthToken", "ENROLLMENT_REQUEST_ID"));
                    final DataObject dataObject = MDMUtil.getPersistenceLite().get(sq);
                    if (!dataObject.isEmpty()) {
                        AuthTokenUtil.LOGGER.log(Level.INFO, "Number of Auth devices: {0}, for customer: {1}", new Object[] { dataObject.size("EnrollmentRequestWithAuthToken"), custID });
                        MessageProvider.getInstance().unhideMessage("REENROLL_AUTH_TOKEN_DEVICE", custID);
                    }
                    else {
                        AuthTokenUtil.LOGGER.log(Level.INFO, "Number of Auth devices: Zero, for customer: {0}", new Object[] { custID });
                        MessageProvider.getInstance().hideMessage("REENROLL_AUTH_TOKEN_DEVICE", custID);
                    }
                }
            }
        }
        catch (final Exception ex) {
            AuthTokenUtil.LOGGER.log(Level.SEVERE, "Exception in AuthToken util check for authTokenDevices: ", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        QUEUE_LOGGER = Logger.getLogger("MDMQueueBriefLogger");
    }
}
