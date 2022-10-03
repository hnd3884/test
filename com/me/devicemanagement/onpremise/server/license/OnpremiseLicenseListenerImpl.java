package com.me.devicemanagement.onpremise.server.license;

import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.license.LicenseEvent;
import com.me.devicemanagement.framework.server.license.LicenseListener;

public class OnpremiseLicenseListenerImpl implements LicenseListener
{
    public void licenseChanged(final LicenseEvent licenseEvent) {
        final String licenseRegisteredDateInMillis = SyMUtil.getSyMParameter("licenseRegisteredDate");
        if (licenseRegisteredDateInMillis == null && LicenseProvider.getInstance().getLicenseType().equals("R")) {
            SyMUtil.updateSyMParameter("licenseRegisteredDate", Long.toString(System.currentTimeMillis()));
        }
        DCQueueHandler.addApplicableQueues();
    }
}
