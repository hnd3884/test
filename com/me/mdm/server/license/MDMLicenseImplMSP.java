package com.me.mdm.server.license;

import com.adventnet.ds.query.Column;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.logging.Logger;

public class MDMLicenseImplMSP
{
    private static final Logger logger;
    
    public String getNoOfMobileDevicesAllocated(final long customerID) {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType != null && licenseType.equalsIgnoreCase("F")) {
            return "unlimited";
        }
        String noOfDevices = "";
        try {
            noOfDevices = String.valueOf(DBUtil.getValueFromDB("DeviceLimitToCustomerMapping", "CUSTOMER_ID", (Object)customerID, "NO_OF_DEVICES"));
        }
        catch (final Exception exp) {
            MDMLicenseImplMSP.logger.log(Level.WARNING, "Exception while getting no of mobiledevices allocated ", exp);
        }
        return noOfDevices;
    }
    
    public boolean isMobileDeviceLicenseReached(final int managedDeviceCount, final long customerID) {
        final String allocatedDevices = this.getNoOfMobileDevicesAllocated(customerID);
        if (allocatedDevices == null || allocatedDevices.equalsIgnoreCase("unlimited")) {
            return false;
        }
        final int noOfAllocatedDevices = Integer.parseInt(allocatedDevices);
        final Boolean isAllocatedAndExceed = managedDeviceCount >= noOfAllocatedDevices && noOfAllocatedDevices != 0;
        if (isAllocatedAndExceed) {
            return isAllocatedAndExceed;
        }
        return LicenseProvider.getInstance().getMDMLicenseAPI().isModernManagementCapable() && LicenseProvider.getInstance().getDCLicenseAPI().isEndpointLimitReachedForMSP(Long.valueOf(customerID));
    }
    
    public boolean isMobileDeviceLicenseReached(final long customerID) {
        final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCountForCustomer(customerID);
        return this.isMobileDeviceLicenseReached(managedDeviceCount, customerID);
    }
    
    public boolean isMobileDeviceLicenseLimitExceed(final long customerID) {
        final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCountForCustomer(customerID);
        return this.isMobileDeviceLicenseLimitExceed(managedDeviceCount, customerID);
    }
    
    public boolean isMobileDeviceLicenseLimitExceed(final int managedDeviceCount, final long customerID) {
        final String allocatedDevices = this.getNoOfMobileDevicesAllocated(customerID);
        if (allocatedDevices == null || allocatedDevices.equalsIgnoreCase("unlimited")) {
            return false;
        }
        final int noOfAllocatedDevices = Integer.valueOf(allocatedDevices);
        return managedDeviceCount > noOfAllocatedDevices && noOfAllocatedDevices != 0;
    }
    
    public List getUnAllocatedDeviceLimitCustomers(final Criteria cri) {
        List customerList = null;
        try {
            Criteria unallocationCriteria = new Criteria(new Column("DeviceLimitToCustomerMapping", "NO_OF_DEVICES"), (Object)0, 6);
            if (cri != null) {
                unallocationCriteria = unallocationCriteria.and(cri);
            }
            customerList = DBUtil.getDistinctColumnValue("DeviceLimitToCustomerMapping", "CUSTOMER_ID", unallocationCriteria);
        }
        catch (final Exception ex) {
            MDMLicenseImplMSP.logger.log(Level.SEVERE, "Exception occured in getUnAllocatedDeviceLimitCustomers: {0}", ex);
        }
        return customerList;
    }
    
    public int getTotalDeviceAllocatedOrManaged(final Criteria criteria) {
        int totalAllocatedCount = 0;
        try {
            final Object allocatedCmptrs = DBUtil.getSumOfValue("DeviceLimitToCustomerMapping", "NO_OF_DEVICES", criteria);
            if (allocatedCmptrs != null) {
                totalAllocatedCount = (int)allocatedCmptrs;
            }
            final List<Long> customerList = this.getUnAllocatedDeviceLimitCustomers(criteria);
            if (customerList != null && !customerList.isEmpty()) {
                totalAllocatedCount += ManagedDeviceHandler.getInstance().getManagedDeviceCountForCustomers(customerList);
            }
        }
        catch (final Exception e) {
            MDMLicenseImplMSP.logger.log(Level.SEVERE, "Exception getTotalDevicesAllocated", e);
        }
        return totalAllocatedCount;
    }
    
    public int getTotalDeviceAllocated() {
        final int totalAllocatedCount = 0;
        try {
            final Object totalAllocatedDevicesObj = DBUtil.getSumOfValue("DeviceLimitToCustomerMapping", "NO_OF_DEVICES", (Criteria)null);
            final int totalAllocatedDevices = (int)((totalAllocatedDevicesObj == null) ? 0 : totalAllocatedDevicesObj);
            return totalAllocatedDevices;
        }
        catch (final Exception e) {
            MDMLicenseImplMSP.logger.log(Level.SEVERE, "Exception getTotalDevicesAllocated", e);
            return totalAllocatedCount;
        }
    }
    
    public int getTotalDeviceAllocatedOrManaged() {
        return this.getTotalDeviceAllocatedOrManaged(null);
    }
    
    public int getTotalDevicesAllocatedExcludingCustomer(final Long customerID) {
        return this.getTotalDeviceAllocatedOrManaged(new Criteria(new Column("DeviceLimitToCustomerMapping", "CUSTOMER_ID"), (Object)customerID, 1));
    }
    
    public boolean isLicenseDegradationAllowed(final int newDeviceCount) {
        final int totalDeviceAllocated = this.getTotalDeviceAllocatedOrManaged();
        return totalDeviceAllocated <= newDeviceCount;
    }
    
    static {
        logger = Logger.getLogger(MDMLicenseImplMSP.class.getName());
    }
}
