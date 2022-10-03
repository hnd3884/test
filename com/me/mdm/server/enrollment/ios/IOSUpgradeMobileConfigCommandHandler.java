package com.me.mdm.server.enrollment.ios;

import java.util.Date;
import com.me.mdm.server.util.CalendarUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSUpgradeMobileConfigCommandHandler
{
    public static Logger logger;
    private static IOSUpgradeMobileConfigCommandHandler iosUpgradeMobileConfigCommandHandler;
    
    public static IOSUpgradeMobileConfigCommandHandler getInstance() {
        if (IOSUpgradeMobileConfigCommandHandler.iosUpgradeMobileConfigCommandHandler == null) {
            IOSUpgradeMobileConfigCommandHandler.iosUpgradeMobileConfigCommandHandler = new IOSUpgradeMobileConfigCommandHandler();
        }
        return IOSUpgradeMobileConfigCommandHandler.iosUpgradeMobileConfigCommandHandler;
    }
    
    public void addIosUpgradeMobileConfigCommand(final Long customerId, final boolean isApnsRenewal, final boolean isIdentityCertRenewal) {
        try {
            IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "IOSUpgradeMobileConfigCommandHandler: Going to add upgrade mobile config command for devices belonging to CustomerID: {0}", new Object[] { customerId });
            IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "IOSUpgradeMobileConfigCommandHandler: DATA-IN: CustomerID: {0}, isAPNSRenewal: {1}, isIdentityCertRenewal: {2}", new Object[] { customerId, isApnsRenewal, isIdentityCertRenewal });
            final DataObject devicesEligibleForUpgradeDO = this.getDevicesEligibleForUpgradeDO(customerId, isApnsRenewal, isIdentityCertRenewal);
            this.addIosUpgradeMobileConfigCommandForEligibleDevices(devicesEligibleForUpgradeDO);
            IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "IOSUpgradeMobileConfigCommandHandler: Added upgrade mobile config command for eligible devices belonging to CustomerID: {0}", new Object[] { customerId });
        }
        catch (final Exception e) {
            IOSUpgradeMobileConfigCommandHandler.logger.log(Level.SEVERE, "IOSUpgradeMobileConfigCommandHandler: Exception while adding renew APNS certificate command", e);
        }
    }
    
    private void addIosUpgradeMobileConfigCommandForEligibleDevices(final DataObject devicesEligibleForUpgradeDO) throws Exception {
        IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "IOSUpgradeMobileConfigCommandHandler: Going to send mobile config upgrade command to eligible devices.");
        if (!devicesEligibleForUpgradeDO.isEmpty()) {
            final Criteria os4CategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 2);
            Iterator devicesIter = devicesEligibleForUpgradeDO.getRows("MdDeviceInfo", os4CategoryCriteria);
            this.addIosUpgradeMobileConfigCommandToCommandRepo("InstallProfile;Collection=UpgradeMobileConfig4", devicesIter);
            final Criteria os5CategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
            devicesIter = devicesEligibleForUpgradeDO.getRows("MdDeviceInfo", os5CategoryCriteria);
            this.addIosUpgradeMobileConfigCommandToCommandRepo("InstallProfile;Collection=UpgradeMobileConfig5", devicesIter);
        }
    }
    
    private void addIosUpgradeMobileConfigCommandToCommandRepo(final String commandName, final Iterator devicesIter) throws Exception {
        final List devicesList = DBUtil.getColumnValuesAsList(devicesIter, "RESOURCE_ID");
        IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "IOSUpgradeMobileConfigCommandHandler: Sending {0} command", new Object[] { commandName });
        if (!devicesList.isEmpty()) {
            IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "IOSUpgradeMobileConfigCommandHandler: Command name: {0}, Devices List: {1}", new Object[] { commandName, devicesList });
            DeviceInvCommandHandler.getInstance().scanDevice(devicesList, null);
            DeviceCommandRepository.getInstance().addUpgradeMobileConfigCommand(commandName, devicesList);
            NotificationHandler.getInstance().SendNotification(devicesList, 1);
        }
        IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "IOSUpgradeMobileConfigCommandHandler: Successfully sent {0} command", new Object[] { commandName });
    }
    
    private DataObject getDevicesEligibleForUpgradeDO(final Long customerId, final boolean isApnsRenewal, final boolean isIdentityCertRenewal) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addSelectColumn(new Column("ManagedDevice", "*"));
        sQuery.addSelectColumn(new Column("MdDeviceInfo", "*"));
        final Criteria eligibleDevicesCriteria = this.qualifyAndAddCriteriaToQuery(customerId, isApnsRenewal, isIdentityCertRenewal, sQuery);
        sQuery.setCriteria(eligibleDevicesCriteria);
        IOSUpgradeMobileConfigCommandHandler.logger.log(Level.FINE, "IOSUpgradeMobileConfigCommandHandler: Select Query for getting eligible device for upgrade: {0} ", new Object[] { sQuery });
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        return DO;
    }
    
    private Criteria qualifyAndAddCriteriaToQuery(final Long customerId, final boolean isApnsRenewal, final boolean isIdentityCertRenewal, final SelectQuery sQuery) {
        final Criteria iosPlatformCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria managedDevicesCriteria = this.getManagedDevicesCriteria();
        Criteria overAllCriteria = iosPlatformCriteria.and(managedDevicesCriteria);
        if (customerId != null && !isApnsRenewal) {
            IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "IOSUpgradeMobileConfigCommandHandler: Adding only the devices belonging to the customer {0}", new Object[] { customerId });
            final Criteria specificCustomerCriteria = this.addCustomerSpecificCriteriaToQuery(sQuery, customerId);
            overAllCriteria = overAllCriteria.and(specificCustomerCriteria);
        }
        if (isIdentityCertRenewal) {
            IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "Scheduler - IOSMobileConfigUpgradeTask: Identity cert renewal, getting devices eligible for upgrade mobile config.");
            final Criteria identityCertExpiryCriteria = this.getIdentityCertExpiryCriteria(sQuery);
            overAllCriteria = overAllCriteria.and(identityCertExpiryCriteria);
        }
        return overAllCriteria;
    }
    
    private Criteria getManagedDevicesCriteria() {
        final Criteria enrollSuccessCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria excludeUnmanagedCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)4, 1);
        final Criteria excludeRepairCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)9, 1);
        final Criteria excludeRetiredCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)11, 1);
        final Criteria excludeOldDeviceCrteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)10, 1);
        final Criteria deviceStatusCriteria = enrollSuccessCriteria.or(excludeUnmanagedCriteria.and(excludeRepairCriteria).and(excludeRetiredCriteria).and(excludeOldDeviceCrteria));
        return deviceStatusCriteria;
    }
    
    private Criteria addCustomerSpecificCriteriaToQuery(final SelectQuery sQuery, final Long customerId) {
        sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        return customerCriteria;
    }
    
    private Criteria getIdentityCertExpiryCriteria(final SelectQuery sQuery) {
        final Date currentDay = CalendarUtil.getInstance().getStartTimeOfTheDay(new Date().getTime());
        final long expiryMillis = CalendarUtil.getInstance().addDays(currentDay, 90).getTime();
        sQuery.addJoin(new Join("ManagedDevice", "MdCertificateResourceRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("MdCertificateResourceRel", "MdCertificateInfo", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 2));
        final Criteria identityCertExpiryCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_NAME"), (Object)"MDM-IDENTITY*", 2);
        final Criteria expiryTimeCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_EXPIRE"), (Object)expiryMillis, 7);
        IOSUpgradeMobileConfigCommandHandler.logger.log(Level.INFO, "Scheduler - IOSMobileConfigUpgradeTask: Getting devices eligible for upgrade mobile config. Expiring on {0}", new Object[] { expiryMillis });
        return identityCertExpiryCriteria.and(expiryTimeCriteria);
    }
    
    static {
        IOSUpgradeMobileConfigCommandHandler.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        IOSUpgradeMobileConfigCommandHandler.iosUpgradeMobileConfigCommandHandler = null;
    }
}
