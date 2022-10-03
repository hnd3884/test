package com.me.mdm.onpremise.server.license;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseAPI;
import com.me.devicemanagement.onpremise.server.license.LicenseImpl;

public class MDMGeneralLicenseImpl extends LicenseImpl implements LicenseAPI
{
    private Logger logger;
    
    public MDMGeneralLicenseImpl() {
        this.logger = Logger.getLogger(MDMGeneralLicenseImpl.class.getName());
    }
    
    public boolean isFreeEditionForwardRequired() {
        if (SyMUtil.getSyMParameterFromDB("free_edition_computer_defined") == null || SyMUtil.getSyMParameterFromDB("free_edition_computer_defined").equals("false")) {
            final int mobileDevicesCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
            if (mobileDevicesCount > 0) {
                this.logger.log(Level.INFO, "Mobile Devices Count is > 0 && free edition not defined already so moving to free edition page");
                return true;
            }
        }
        return false;
    }
    
    public void setFreeEditionConfiguredStatus() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final int mobileDevicesCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
        if (licenseType.equals("F") && (SyMUtil.getSyMParameter("free_edition_computer_defined") == null || SyMUtil.getSyMParameter("free_edition_computer_defined").equals("false")) && mobileDevicesCount > 0) {
            ApiFactoryProvider.getCacheAccessAPI().putCache("FREE_LICENSE_NOT_CONFIGURED", (Object)true);
        }
        else {
            ApiFactoryProvider.getCacheAccessAPI().putCache("FREE_LICENSE_NOT_CONFIGURED", (Object)false);
        }
        this.logger.log(Level.INFO, "Value of FREE_LICENSE_NOT_CONFIGURED in cache is  : ", ApiFactoryProvider.getCacheAccessAPI().getCache("FREE_LICENSE_NOT_CONFIGURED"));
    }
    
    public boolean isLicenseExpiryMsgRequired() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final int defaultFreeMobileDevicecount = FreeEditionHandler.getInstance().getFreeEditionMobileDevices();
        if (licenseType != null && licenseType.equals("T")) {
            return true;
        }
        final String devices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
        if (devices.equalsIgnoreCase("unlimited")) {
            return true;
        }
        final int numberOfMobileDevices = Integer.parseInt(devices);
        return numberOfMobileDevices > defaultFreeMobileDevicecount;
    }
    
    public boolean isClearedDetailsForFreeEdition(final HttpServletRequest request) {
        final String mobileDeviceIds = request.getParameter("mobileDeviceIds");
        return this.isClearedDetailsForFreeEdition(mobileDeviceIds);
    }
    
    private boolean isClearedDetailsForFreeEdition(final String mobileDeviceIds) {
        try {
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final boolean mobileUpdated = MDMUtil.getInstance().updateFreeEditionDetails(mobileDeviceIds);
            final Long currentlyLoggedInUserLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            DMOnPremiseUserHandler.deleteDCUsersExceptCurrent(currentlyLoggedInUserLoginId);
            this.logger.log(Level.INFO, "Additional Technicians Deleted(Except currently logged in user) successfully");
            if (mobileUpdated) {
                DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, "mdm.license.moved_to_free_edition", (Object)null, true);
                return true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in isClearedDetailsForFreeEdition ", e);
        }
        return false;
    }
}
