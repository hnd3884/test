package com.me.mdm.webclient.remote;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class RemoteUtil
{
    private static final Logger LOGGER;
    
    public int remoteNonEligibleDevices() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        query.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        final Criteria androidPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer(2), 0);
        final Criteria android4OsVersion = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 2);
        final Criteria android3OsVersion = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"3.*", 2);
        final Criteria android2OsVersion = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"2.*", 2);
        query.setCriteria(managedCriteria.and(androidPlatform).and(android4OsVersion.or(android3OsVersion).or(android2OsVersion)));
        try {
            final DataObject dO = DataAccess.get(query);
            if (dO.isEmpty()) {
                return 0;
            }
            return dO.size("Resource");
        }
        catch (final DataAccessException e) {
            RemoteUtil.LOGGER.log(Level.FINE, "Exception while remoteNonEligibleDevices ");
            return 0;
        }
    }
    
    public int iOSEligibleDevices() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        query.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        final Criteria iOSPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer(1), 0);
        final Criteria iOS7Version = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"7*", 3);
        final Criteria iOS8Version = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8*", 3);
        final Criteria iOS9Version = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"9*", 3);
        final Criteria iOS10Version = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"10*", 3);
        query.setCriteria(managedCriteria.and(iOSPlatform).and(iOS7Version.and(iOS8Version.and(iOS9Version.and(iOS10Version)))));
        try {
            final DataObject dO = DataAccess.get(query);
            if (dO.isEmpty()) {
                return 0;
            }
            return dO.size("ManagedDevice");
        }
        catch (final DataAccessException e) {
            RemoteUtil.LOGGER.log(Level.FINE, "Exception while ioSEligibleDevices ");
            return 0;
        }
    }
    
    public Boolean areDevicesEnrolled() {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            query.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
            final Criteria androidPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria iOSPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            query.setCriteria(managedCriteria.and(androidPlatform).or(iOSPlatform));
            final DataObject dO = DataAccess.get(query);
            if (dO.isEmpty()) {
                return false;
            }
            return true;
        }
        catch (final Exception e) {
            RemoteUtil.LOGGER.log(Level.FINE, "Exception while areDevicesEnrolled ");
            return false;
        }
    }
    
    public int remoteNonEligibleDevicesWithCustId(final Long customerId) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        query.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        final Criteria androidPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer(2), 0);
        final Criteria android4OsVersion = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 2);
        final Criteria android3OsVersion = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"3.*", 2);
        final Criteria android2OsVersion = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"2.*", 2);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        query.setCriteria(managedCriteria.and(androidPlatform).and(android4OsVersion.or(android3OsVersion).or(android2OsVersion)).and(customerCriteria));
        try {
            final DataObject dO = DataAccess.get(query);
            if (dO.isEmpty()) {
                return 0;
            }
            return dO.size("Resource");
        }
        catch (final DataAccessException e) {
            RemoteUtil.LOGGER.log(Level.FINE, "Exception while remoteNonEligibleDevices ");
            return 0;
        }
    }
    
    public int iOSEligibleDevicesWithCustId(final Long customerId) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        query.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        final Criteria iOSPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer(1), 0);
        final Criteria iOS7Version = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"7*", 3);
        final Criteria iOS8Version = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8*", 3);
        final Criteria iOS9Version = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"9*", 3);
        final Criteria iOS10Version = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"10*", 3);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        query.setCriteria(managedCriteria.and(iOSPlatform).and(iOS7Version.and(iOS8Version.and(iOS9Version.and(iOS10Version)))).and(customerCriteria));
        try {
            final DataObject dO = DataAccess.get(query);
            if (dO.isEmpty()) {
                return 0;
            }
            return dO.size("ManagedDevice");
        }
        catch (final DataAccessException e) {
            RemoteUtil.LOGGER.log(Level.FINE, "Exception while ioSEligibleDevices ");
            return 0;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMRemoteControlLogger");
    }
}
