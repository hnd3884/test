package com.me.mdm.core.enrollment;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.UnionQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DEPAdminEnrollmentHandler extends AdminEnrollmentHandler
{
    public static Logger logger;
    
    public DEPAdminEnrollmentHandler() {
        super(10, "AppleDEPDeviceForEnrollment", "DEPEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        DEPAdminEnrollmentHandler.logger.log(Level.INFO, "Not performing any task for DEPEnrollmentTemplate(During New role added , modified , user created ,user modified)");
    }
    
    public static List<Long> getDepDevicesInResList(final List<Long> resIdList) {
        List<Long> depRescList = new ArrayList<Long>();
        try {
            final SelectQuery sq = getManagedResourceToDEPTokenQuery();
            if (resIdList != null && !resIdList.isEmpty()) {
                final Criteria rescIdCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resIdList.toArray(), 8);
                sq.setCriteria(rescIdCriteria);
            }
            sq.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            final DataObject DO = MDMUtil.getPersistence().get(sq);
            depRescList = DBUtil.getColumnValuesAsList(DO.getRows("ManagedDevice"), "RESOURCE_ID");
        }
        catch (final Exception ex) {
            DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in get DepDevices In ResourceList", ex);
        }
        return depRescList;
    }
    
    public int getUnenrolledDeviceCount(final Long customerID, final Long deptokenID) throws Exception {
        int count = 0;
        try {
            final SelectQuery leftQuery = getManagedResourceToDEPTokenQuery();
            Criteria leftQueryCri = new Criteria(Column.getColumn("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer[] { 9, 10 }, 8));
            leftQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID", "DEVICE_ID"));
            leftQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER", "SERIAL_NUMBER"));
            final SelectQuery rightQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleDEPDeviceForEnrollment"));
            rightQuery.addJoin(new Join("AppleDEPDeviceForEnrollment", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            Criteria rightQueryCri = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
            rightQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "DEVICE_ID"));
            rightQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER", "SERIAL_NUMBER"));
            if (deptokenID != null) {
                leftQueryCri = leftQueryCri.and(new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)deptokenID, 0));
                rightQueryCri = rightQueryCri.and(new Criteria(Column.getColumn("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"), (Object)deptokenID, 0));
            }
            leftQuery.setCriteria(leftQueryCri);
            rightQuery.setCriteria(rightQueryCri);
            final UnionQuery uq = (UnionQuery)new UnionQueryImpl((Query)leftQuery, (Query)rightQuery, false);
            final String uqString = RelationalAPI.getInstance().getSelectSQL((Query)uq);
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)uqString);
            while (dmDataSetWrapper.next()) {
                ++count;
            }
        }
        catch (final Exception e) {
            DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getUnassignedDeviceCount", e);
        }
        return count;
    }
    
    public int getAdminEnrolledDeviceCount(final Long customerID, final Long depTokenID) throws Exception {
        final SelectQuery sQuery = getManagedResourceToDEPTokenQuery();
        final Criteria enrollStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria depTokenCriteria = new Criteria(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)depTokenID, 0);
        sQuery.setCriteria(enrollStatus.and(customerCriteria));
        if (depTokenID != null) {
            Criteria cri = sQuery.getCriteria();
            cri = cri.and(depTokenCriteria);
            sQuery.setCriteria(cri);
        }
        final int enrolledDeviceCount = DBUtil.getRecordCount(sQuery, "Resource", "RESOURCE_ID");
        return enrolledDeviceCount;
    }
    
    public int getDevicesWithoutDepCount(final Long customerID, final Long depTokenID) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        sQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("MdDeviceInfo", "DEPDevicesSyncData", new String[] { "SERIAL_NUMBER" }, new String[] { "SERIAL_NUMBER" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 2, 5 }, 8);
        final Criteria depTokenCriteria = new Criteria(Column.getColumn("DEPDevicesSyncData", "DEP_TOKEN_ID"), (Object)depTokenID, 0);
        final Criteria depStatusCriteria = new Criteria(Column.getColumn("DEPDevicesSyncData", "DEVICE_STATUS"), (Object)1, 0);
        final Criteria notDepCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)10, 1);
        final Criteria nullTemplateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)null, 0);
        Criteria cri = customerCriteria.and(managedCriteria.and(depStatusCriteria.and(notDepCriteria.or(nullTemplateCriteria))));
        if (depTokenID != null) {
            cri = cri.and(depTokenCriteria);
        }
        sQuery.setCriteria(cri);
        final int withoutDepCount = DBUtil.getRecordCount(sQuery, "Resource", "RESOURCE_ID");
        return withoutDepCount;
    }
    
    public int getDevicesEnrolledAndNotAssignedUserCount(final Long customerID, final Long depTokenID) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplateToGroupRel", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplateToGroupRel", "DEPTokenToGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
        final Criteria depTokenCriteria = new Criteria(Column.getColumn("DEPTokenToGroup", "DEP_TOKEN_ID"), (Object)depTokenID, 0);
        Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)5, 0);
        if (depTokenID != null) {
            managedCriteria = managedCriteria.and(depTokenCriteria);
        }
        sQuery.setCriteria(managedCriteria);
        final int enrolledButNotUserAssigned = DBUtil.getRecordCount(sQuery, "ManagedDevice", "RESOURCE_ID");
        return enrolledButNotUserAssigned;
    }
    
    public boolean getDEPDeviceStatus(final List<JSONObject> list) {
        boolean enrolled = false;
        if (!list.isEmpty()) {
            try {
                final String serialNo = list.iterator().next().optString("SerialNumber");
                final Criteria criteria = new Criteria(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNo, 0);
                final int count = DBUtil.getRecordCount("DeviceForEnrollment", "SERIAL_NUMBER", criteria);
                if (count == 0) {
                    enrolled = true;
                }
            }
            catch (final Exception e) {
                DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getDEPDeviceStatus", e);
            }
        }
        return enrolled;
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
    
    public static SelectQuery getDEPTokenToEnrollmentTemplateQuery() {
        final SelectQuery deviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
        deviceQuery.addJoin(new Join("DEPTokenDetails", "DEPTokenToGroup", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
        deviceQuery.addJoin(new Join("DEPTokenToGroup", "EnrollmentTemplateToGroupRel", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
        deviceQuery.addJoin(new Join("EnrollmentTemplateToGroupRel", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        deviceQuery.addJoin(new Join("DEPEnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        deviceQuery.addJoin(new Join("EnrollmentTemplate", "EnrollmentTemplateToRequest", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        deviceQuery.addJoin(new Join("EnrollmentTemplate", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        return deviceQuery;
    }
    
    public static SelectQuery getManagedResourceToDEPTokenQuery() {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        sQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplate", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("DEPEnrollmentTemplate", "EnrollmentTemplateToGroupRel", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplateToGroupRel", "DEPTokenToGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("DEPTokenToGroup", "DEPTokenDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
        return sQuery;
    }
    
    public boolean isNewTechnicianAssignmentNeededForAbmServer(final Long abmServerId) {
        boolean isNewTechnicianAssignmentNeeded = false;
        try {
            DEPAdminEnrollmentHandler.logger.log(Level.INFO, "Checking whether new technician assignment is needed for abm server: {0}", new Object[] { abmServerId });
            final SelectQuery selectQuery = getDEPTokenToEnrollmentTemplateQuery();
            final Join join = new Join("EnrollmentTemplate", "AaaLogin", new String[] { "ADDED_USER" }, new String[] { "USER_ID" }, 2);
            final Criteria criteria = new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)abmServerId, 0);
            selectQuery.addJoin(join);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Row aaaloginRow = dataObject.getRow("AaaLogin");
            if (aaaloginRow == null) {
                DEPAdminEnrollmentHandler.logger.log(Level.INFO, "AAALogin row is empty, so new technician assignment is needed for {0}", new Object[] { abmServerId });
                isNewTechnicianAssignmentNeeded = true;
            }
            DEPAdminEnrollmentHandler.logger.log(Level.INFO, "Is new technician assignment needed for abm server: {0} - {1}", new Object[] { abmServerId, isNewTechnicianAssignmentNeeded });
        }
        catch (final DataAccessException e) {
            DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception while checking whether new technician assignment is needed", (Throwable)e);
        }
        return isNewTechnicianAssignmentNeeded;
    }
    
    static {
        DEPAdminEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
