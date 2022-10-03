package com.me.mdm.server.migration;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Comparator;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.Arrays;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Collection;
import java.util.ArrayList;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class MspMigrationHandler
{
    private final Logger logger;
    
    public MspMigrationHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void migrateDevice(final List<Long> deviceIdList, final Long customerId, final Long newCustomerId, final String userName) throws Exception {
        this.logger.log(Level.INFO, "[MspDeviceMig] Entering into MSP device migration..");
        final List<Long> managedUserIDs = ManagedUserHandler.getInstance().getManagedUserIDsForDeviceIDs(deviceIdList);
        final List<Long> deviceIdsManagedByUsers = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(managedUserIDs);
        if (!this.isListsEqual(deviceIdList, deviceIdsManagedByUsers)) {
            this.logger.log(Level.INFO, "[MspDeviceMig] Managed user has more no. of devices assigned. So migration cannot be performed.");
            throw new APIHTTPException("COM0009", new Object[0]);
        }
        final List<Long> resourceList = new ArrayList<Long>();
        resourceList.addAll(deviceIdList);
        resourceList.addAll(managedUserIDs);
        this.removeDeviceAssociations(deviceIdList);
        this.removeUserAssociations(managedUserIDs);
        final Object[] resourceUdid = ManagedDeviceHandler.getInstance().getUDIDListForResourceIDList(deviceIdList).toArray();
        final Object[] userNamesList = MDMResourceDataProvider.getResourceNamesList(managedUserIDs).toArray();
        final String fromCustomerName = MDMCustomerInfoUtil.getInstance().getCustomerName(customerId);
        final String toCustomerName = MDMCustomerInfoUtil.getInstance().getCustomerName(newCustomerId);
        final String remarksArgs = Arrays.toString(resourceUdid) + "@@@" + Arrays.toString(userNamesList) + "@@@";
        this.changeAssociatedCustomerForResource(resourceList, newCustomerId);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2092, null, userName, "dc.mdm.actionlog.migration.device_moved", remarksArgs + toCustomerName, customerId);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2092, null, userName, "dc.mdm.actionlog.migration.device_added", remarksArgs + fromCustomerName, newCustomerId);
        this.logger.log(Level.INFO, "[MspDeviceMig] Successfully migrated devices: {0} and associated users: {1} from customer: {2} to customer{3}", new Object[] { deviceIdList, managedUserIDs, customerId, newCustomerId });
        this.logger.log(Level.INFO, "[MspDeviceMig] Exiting from MSP device migration..");
    }
    
    private boolean isListsEqual(final List one, final List two) {
        one.sort(Comparator.naturalOrder());
        two.sort(Comparator.naturalOrder());
        return one.equals(two);
    }
    
    private void changeAssociatedCustomerForResource(final List<Long> resourceIDs, final Long customerID) throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        sq.addJoin(new Join("Resource", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        sq.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sq.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
        sq.setCriteria(resourceCriteria);
        sq.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        sq.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        sq.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        sq.addSelectColumn(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(sq);
        if (!dataObject.isEmpty()) {
            Row rowTemp = null;
            Iterator<Row> it = dataObject.getRows("Resource");
            while (it.hasNext()) {
                rowTemp = it.next();
                rowTemp.set("CUSTOMER_ID", (Object)customerID);
                dataObject.updateRow(rowTemp);
            }
            it = dataObject.getRows("DeviceForEnrollment");
            while (it.hasNext()) {
                rowTemp = it.next();
                rowTemp.set("CUSTOMER_ID", (Object)customerID);
                dataObject.updateRow(rowTemp);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
    }
    
    private void removeDeviceAssociations(final List<Long> deviceIdList) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        sq.addJoin(new Join("ManagedDevice", "CollnToResources", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sq.addJoin(new Join("ManagedDevice", "RecentProfileForResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sq.addJoin(new Join("ManagedDevice", "MdAppCatalogToResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sq.addJoin(new Join("ManagedDevice", "DocumentManagedDeviceRel", new String[] { "RESOURCE_ID" }, new String[] { "MANAGEDDEVICE_ID" }, 1));
        sq.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
        sq.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        sq.addJoin(new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sq.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceIdList.toArray(), 8));
        sq.addSelectColumn(Column.getColumn("CollnToResources", "*"));
        sq.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
        sq.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "*"));
        sq.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "*"));
        sq.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
        sq.addSelectColumn(Column.getColumn("EnrollmentTemplateToRequest", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(sq);
        if (!dataObject.isEmpty()) {
            final List tableList = dataObject.getTableNames();
            for (final Object tableName : tableList) {
                final Iterator it = dataObject.getRows(tableName.toString());
                while (it.hasNext()) {
                    it.next();
                    it.remove();
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
    }
    
    private void removeUserAssociations(final List<Long> managedUserIDs) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        sq.addJoin(new Join("ManagedUser", "CustomGroupMemberRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
        sq.setCriteria(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)managedUserIDs.toArray(), 8));
        sq.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(sq);
        if (!dataObject.isEmpty()) {
            final List tableList = dataObject.getTableNames();
            for (final Object tableName : tableList) {
                final Iterator it = dataObject.getRows(tableName.toString());
                while (it.hasNext()) {
                    it.next();
                    it.remove();
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
    }
}
