package com.me.mdm.server.enrollment.task;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Range;
import java.util.ArrayList;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class InactiveDevicePolicyTask
{
    private static Logger logger;
    
    public void executeTask() {
        final Long startTime = MDMUtil.getCurrentTimeInMillis();
        InactiveDevicePolicyTask.logger.log(Level.INFO, "Entering Inactive Device Policy Task in {0}", startTime);
        try {
            final Long[] msgCustArr = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            int deviceCount = 0;
            if (msgCustArr != null) {
                for (int i = 0; i < msgCustArr.length; ++i) {
                    deviceCount = this.getInactiveDeviceCounts(msgCustArr[i]);
                    if (deviceCount > 0) {
                        MessageProvider.getInstance().unhideMessage("INACTIVE_DEVICE_FOUND", msgCustArr[i]);
                        this.updateInactiveDeviceRemarksForCustomer(msgCustArr[i]);
                    }
                    else {
                        MessageProvider.getInstance().hideMessage("INACTIVE_DEVICE_FOUND", msgCustArr[i]);
                    }
                }
            }
            Long customerID = null;
            final SelectQuery allQuery = this.getSelectQuery();
            final Criteria enrollSuccessCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            allQuery.setCriteria(enrollSuccessCri);
            final DataObject criDO = MDMUtil.getPersistence().get(allQuery);
            final Iterator<Row> customerIter = criDO.getRows("InactiveDevicePolicyDetails");
            while (customerIter.hasNext()) {
                final Row custRow = customerIter.next();
                customerID = (Long)custRow.get("CUSTOMER_ID");
                final Criteria custCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "CUSTOMER_ID"), (Object)customerID, 0);
                final Long currentTime = MDMUtil.getCurrentTimeInMillis();
                final Long inactiveThreshold = (Long)criDO.getValue("InactiveDevicePolicyDetails", "INACTIVE_THRESHOLD", custCri);
                final int actionType = (int)criDO.getValue("InactiveDevicePolicyDetails", "ACTION_TYPE", custCri);
                if (actionType > 0 && actionType <= 3) {
                    final Long idpActionThreshold = (Long)criDO.getValue("InactiveDevicePolicyDetails", "IDP_ACTION_THRESHOLD", custCri);
                    final Long actionTime = currentTime - inactiveThreshold - idpActionThreshold;
                    final Criteria idpThresholdCri = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)actionTime, 6);
                    this.inactiveDevicePolicyActionOnDevices(allQuery, custCri, idpThresholdCri, enrollSuccessCri, actionType);
                }
            }
        }
        catch (final Exception ex) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in InactiveDevicePolicyTask :{0}", ex);
        }
        final Long endTime = MDMUtil.getCurrentTimeInMillis();
        InactiveDevicePolicyTask.logger.log(Level.INFO, "Exiting Inactive Device Policy Task in {0}", endTime);
        InactiveDevicePolicyTask.logger.log(Level.INFO, "Time taken for Inactive Device Policy Task is {0}", endTime - startTime);
    }
    
    public SelectQuery getSelectQuery() {
        final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "POLICY_ID"));
        squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "ACTION_TYPE"));
        squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "CUSTOMER_ID"));
        squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "IDP_ACTION_THRESHOLD"));
        squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "INACTIVE_THRESHOLD"));
        squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "CREATED_BY"));
        squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "UPDATED_TIME"));
        squery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        squery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
        squery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        squery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        squery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        squery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        squery.addSelectColumn(Column.getColumn("ManagedDevice", "OWNED_BY"));
        squery.addSelectColumn(Column.getColumn("ManagedDevice", "REMARKS"));
        squery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
        squery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
        squery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        squery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        squery.addJoin(new Join("Resource", "InactiveDevicePolicyDetails", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        squery.addSortColumn(new SortColumn("ManagedDevice", "RESOURCE_ID", true));
        return squery;
    }
    
    public void inactiveDevicePolicyActionOnDevices(final SelectQuery squery, final Criteria commonCri, final Criteria idpThresholdCri, final Criteria enrollSuccessCri, final int actionType) {
        try {
            switch (actionType) {
                case 1: {
                    final Criteria removeDeviceCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "ACTION_TYPE"), (Object)1, 0);
                    this.removeInactiveDevicePolicyDevices(squery, commonCri, idpThresholdCri, removeDeviceCri, enrollSuccessCri);
                    break;
                }
                case 2: {
                    final Criteria retireDeviceCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "ACTION_TYPE"), (Object)2, 0);
                    this.retireInactiveDevicePolicyDevices(squery, commonCri, idpThresholdCri, retireDeviceCri, enrollSuccessCri);
                    break;
                }
                case 3: {
                    final Criteria unassignLicenseCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "ACTION_TYPE"), (Object)3, 0);
                    this.unassignMDMLicenseForDevices(squery, commonCri, idpThresholdCri, unassignLicenseCri, enrollSuccessCri);
                    break;
                }
            }
        }
        catch (final Exception ex) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in idpActionOnDevices :{0}", ex);
        }
    }
    
    private List<Long> getResourceIDs(final DataObject dObj, final List<Long> resourceList) {
        try {
            final Iterator<Row> resourceIter = dObj.getRows("Resource");
            while (resourceIter.hasNext()) {
                final Row resRow = resourceIter.next();
                resourceList.add((Long)resRow.get("RESOURCE_ID"));
            }
        }
        catch (final DataAccessException e) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in getResourceIDs :{0}", (Throwable)e);
        }
        return resourceList;
    }
    
    private void removeInactiveDevicePolicyDevices(final SelectQuery squery, final Criteria commonCri, final Criteria idpThresholdCri, final Criteria removeDeviceCri, final Criteria enrollSuccessCri) {
        DataObject idpActionDO = null;
        final EnrollmentFacade enrollObj = new EnrollmentFacade();
        int startRange = 0;
        final int deviceDORange = 50;
        int dObjSize = 50;
        final Long currentTime = MDMUtil.getCurrentTimeInMillis();
        List<Long> resourceList = new ArrayList<Long>();
        try {
            squery.setCriteria(commonCri.and(idpThresholdCri.and(removeDeviceCri).and(enrollSuccessCri)));
            while (startRange < dObjSize) {
                squery.setRange(new Range(startRange, deviceDORange));
                idpActionDO = MDMUtil.getPersistence().get(squery);
                dObjSize = idpActionDO.size("Resource");
                resourceList = this.getResourceIDs(idpActionDO, resourceList);
                final ArrayList deviceNameList = new ArrayList();
                for (final Long resource : resourceList) {
                    deviceNameList.add(ManagedDeviceHandler.getInstance().getDeviceName(resource));
                }
                if (!idpActionDO.isEmpty()) {
                    final Long custID = (Long)idpActionDO.getFirstValue("InactiveDevicePolicyDetails", "CUSTOMER_ID");
                    final Long userID = (Long)idpActionDO.getFirstValue("InactiveDevicePolicyDetails", "CREATED_BY");
                    enrollObj.removeDevicesUtil(new MDMEnrollmentRequestHandler().getEnrollmentRequestIdsFromManagedDeviceIDs(resourceList.toArray(new Long[resourceList.size()])), custID, DMUserHandler.getUserNameFromUserID(userID), true);
                    MDMEventLogHandler.getInstance().addEvent(2003, resourceList, DMUserHandler.getUserNameFromUserID(userID), "mdm.actionlog.enrollment.inactive_device_removal", deviceNameList, custID, currentTime);
                }
                final int nextRange = startRange + deviceDORange;
                if (nextRange > dObjSize) {
                    startRange += nextRange - dObjSize;
                }
                else {
                    startRange = nextRange;
                }
            }
        }
        catch (final Exception ex) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in removeIDPDevices :{0}", ex);
        }
    }
    
    private void retireInactiveDevicePolicyDevices(final SelectQuery squery, final Criteria commonCri, final Criteria idpThresholdCri, final Criteria retireDeviceCri, final Criteria enrollSuccessCri) {
        DataObject idpActionDO = null;
        int startRange = 0;
        final int deviceDORange = 50;
        int dObjSize = 50;
        List<Long> resourceList = new ArrayList<Long>();
        final JSONArray successArray = new JSONArray();
        final JSONArray failedArray = new JSONArray();
        JSONObject deviceListJSON = new JSONObject();
        final MDMEnrollmentUtil enrollUtilObj = new MDMEnrollmentUtil();
        final Long currentTime = MDMUtil.getCurrentTimeInMillis();
        try {
            squery.setCriteria(commonCri.and(idpThresholdCri.and(retireDeviceCri).and(enrollSuccessCri)));
            while (startRange < dObjSize) {
                squery.setRange(new Range(startRange, deviceDORange));
                idpActionDO = MDMUtil.getPersistence().get(squery);
                dObjSize = idpActionDO.size("Resource");
                resourceList = this.getResourceIDs(idpActionDO, resourceList);
                final ArrayList deviceNameList = new ArrayList();
                for (final Long resource : resourceList) {
                    deviceNameList.add(ManagedDeviceHandler.getInstance().getDeviceName(resource));
                }
                if (!idpActionDO.isEmpty()) {
                    final Long custID = (Long)idpActionDO.getFirstValue("InactiveDevicePolicyDetails", "CUSTOMER_ID");
                    final Long userID = (Long)idpActionDO.getFirstValue("InactiveDevicePolicyDetails", "CREATED_BY");
                    final Long inactiveThreshold = (Long)idpActionDO.getFirstValue("InactiveDevicePolicyDetails", "INACTIVE_THRESHOLD");
                    final Long idpActionThreshold = (Long)idpActionDO.getFirstValue("InactiveDevicePolicyDetails", "IDP_ACTION_THRESHOLD");
                    deviceListJSON = enrollUtilObj.retireInactiveDevices(resourceList, inactiveThreshold, idpActionThreshold, userID, custID);
                    successArray.put(deviceListJSON.get("SuccessList"));
                    failedArray.put(deviceListJSON.get("FailureList"));
                    MDMEventLogHandler.getInstance().addEvent(2004, resourceList, DMUserHandler.getUserNameFromUserID(userID), "mdm.actionlog.enrollment.inactive_device_retire", deviceNameList, custID, currentTime);
                }
                final int nextRange = startRange + deviceDORange;
                if (nextRange > dObjSize) {
                    startRange += nextRange - dObjSize;
                }
                else {
                    startRange = nextRange;
                }
            }
        }
        catch (final Exception ex) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in retireIDPDevices :{0}", ex);
        }
    }
    
    private void unassignMDMLicenseForDevices(final SelectQuery squery, final Criteria commonCri, final Criteria idpThresholdCri, final Criteria unassignLicenseCri, final Criteria enrollSuccessCri) {
        int startRange = 0;
        final int deviceDORange = 50;
        int dObjSize = 50;
        final JSONObject resourceDetails = new JSONObject();
        List<Long> resourceList = new ArrayList<Long>();
        final ArrayList deviceNameList = new ArrayList();
        final Long currentTime = MDMUtil.getCurrentTimeInMillis();
        try {
            resourceDetails.put("MANAGED_STATUS", 4);
            resourceDetails.put("REMARKS", (Object)"mdm.enroll.unassign_mdm_license_for_device");
            squery.setCriteria(commonCri.and(idpThresholdCri.and(unassignLicenseCri.and(enrollSuccessCri))));
            while (startRange < dObjSize) {
                squery.setRange(new Range(startRange, deviceDORange));
                final DataObject idpActionDO = MDMUtil.getPersistence().get(squery);
                dObjSize = idpActionDO.size("Resource");
                resourceList = this.getResourceIDs(idpActionDO, resourceList);
                for (final Long resource : resourceList) {
                    deviceNameList.add(ManagedDeviceHandler.getInstance().getDeviceName(resource));
                }
                if (!idpActionDO.isEmpty()) {
                    final Long custID = (Long)idpActionDO.getFirstValue("InactiveDevicePolicyDetails", "CUSTOMER_ID");
                    final Long userID = (Long)idpActionDO.getFirstValue("InactiveDevicePolicyDetails", "CREATED_BY");
                    ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(resourceList, resourceDetails);
                    MDMEventLogHandler.getInstance().addEvent(2005, resourceList, DMUserHandler.getUserNameFromUserID(userID), "mdm.actionlog.enrollment.idp_license_unassign", deviceNameList, custID, currentTime);
                }
                final int nextRange = startRange + deviceDORange;
                if (nextRange > dObjSize) {
                    startRange += nextRange - dObjSize;
                }
                else {
                    startRange = nextRange;
                }
            }
        }
        catch (final Exception ex) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in unassignMDMLicenseForDevices :{0}", ex);
        }
    }
    
    public JSONObject getInactiveDevicePolicyThresholdValues(final Long customerID) {
        final JSONObject thresholdJson = new JSONObject();
        Long inactiveThreshold = null;
        Long idpActionThreshold = null;
        try {
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("InactiveDevicePolicyDetails"));
            squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "POLICY_ID"));
            squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "INACTIVE_THRESHOLD"));
            squery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "IDP_ACTION_THRESHOLD"));
            final Criteria custCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            squery.setCriteria(custCri);
            final DataObject thresholdDO = MDMUtil.getPersistence().get(squery);
            if (!thresholdDO.isEmpty()) {
                inactiveThreshold = (Long)thresholdDO.getFirstValue("InactiveDevicePolicyDetails", "INACTIVE_THRESHOLD");
                idpActionThreshold = (Long)thresholdDO.getFirstValue("InactiveDevicePolicyDetails", "IDP_ACTION_THRESHOLD");
            }
            if (inactiveThreshold == null || inactiveThreshold < 1L) {
                inactiveThreshold = 604800000L;
            }
            if (idpActionThreshold == null || idpActionThreshold < 1L) {
                idpActionThreshold = 7776000000L;
            }
            thresholdJson.put("InactiveThreshold", (Object)inactiveThreshold);
            thresholdJson.put("IDPActionThreshold", (Object)idpActionThreshold);
        }
        catch (final Exception e) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in getThresholdValues ", e);
        }
        return thresholdJson;
    }
    
    public int getInactiveDeviceCounts(final Long customerID) {
        int deviceCount = 0;
        final Long currentTime = MDMUtil.getCurrentTimeInMillis();
        try {
            final JSONObject thresholdJson = this.getInactiveDevicePolicyThresholdValues(customerID);
            final Long inactiveThreshold = (Long)thresholdJson.get("InactiveThreshold");
            final Long inactiveTime = currentTime - inactiveThreshold;
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            squery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            squery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria inactiveThresholdCri = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)inactiveTime, 6);
            final Criteria managedCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            squery.setCriteria(custCri.and(inactiveThresholdCri).and(managedCri));
            deviceCount = DBUtil.getRecordCount(squery, "ManagedDevice", "RESOURCE_ID");
        }
        catch (final Exception ex) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in getInactiveDeviceCounts :{0}", ex);
        }
        return deviceCount;
    }
    
    public void updateInactiveDeviceRemarksForCustomer(final Long customerID) {
        final Long currentTime = MDMUtil.getCurrentTimeInMillis();
        int startRange = 0;
        final int deviceDORange = 50;
        int dObjSize = 50;
        final JSONObject resourceDetails = new JSONObject();
        try {
            resourceDetails.put("MANAGED_STATUS", 2);
            resourceDetails.put("REMARKS", (Object)"mdm.enroll.inactive_device_remarks");
            final JSONObject thresholdJson = this.getInactiveDevicePolicyThresholdValues(customerID);
            final Long inactiveThreshold = (Long)thresholdJson.get("InactiveThreshold");
            final Long inactiveTime = currentTime - inactiveThreshold;
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            squery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
            squery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            squery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            squery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            squery.addSelectColumn(Column.getColumn("ManagedDevice", "OWNED_BY"));
            squery.addSelectColumn(Column.getColumn("ManagedDevice", "REMARKS"));
            squery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
            squery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            squery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            squery.addSortColumn(new SortColumn("ManagedDevice", "RESOURCE_ID", true));
            final Criteria enrollSuccessCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria inactiveThresholdCri = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)inactiveTime, 6);
            squery.setCriteria(enrollSuccessCri.and(inactiveThresholdCri));
            while (startRange < dObjSize) {
                squery.setRange(new Range(startRange, deviceDORange));
                final DataObject idpActionDO = MDMUtil.getPersistence().get(squery);
                dObjSize = idpActionDO.size("Resource");
                List<Long> resourceList = new ArrayList<Long>();
                resourceList = this.getResourceIDs(idpActionDO, resourceList);
                if (!idpActionDO.isEmpty()) {
                    ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(resourceList, resourceDetails);
                }
                final int nextRange = startRange + deviceDORange;
                if (nextRange > dObjSize) {
                    startRange += nextRange - dObjSize;
                }
                else {
                    startRange = nextRange;
                }
            }
        }
        catch (final Exception ex) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in updateInactiveDeviceRemarksForCustomer :{0}", ex);
        }
    }
    
    public void updateInactiveDeviceRemarksAfterContact(final Long resourceId) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
            final Criteria remarksCri = new Criteria(Column.getColumn("ManagedDevice", "REMARKS"), (Object)"mdm.enroll.inactive_device_remarks", 0);
            final Criteria resourceIDCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            updateQuery.setCriteria(resourceIDCri.and(remarksCri));
            updateQuery.setUpdateColumn("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
            MDMUtil.getPersistenceLite().update(updateQuery);
        }
        catch (final Exception ex) {
            InactiveDevicePolicyTask.logger.log(Level.SEVERE, "Exception in updateInactiveDeviceRemarksAfterContact :{0}", ex);
        }
    }
    
    static {
        InactiveDevicePolicyTask.logger = Logger.getLogger("MDMEnrollment");
    }
}
