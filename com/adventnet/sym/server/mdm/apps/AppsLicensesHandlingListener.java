package com.adventnet.sym.server.mdm.apps;

public interface AppsLicensesHandlingListener
{
    void appLicenseDirectRemoval(final AppsLicensesHandlerEvent p0);
    
    void appReassignedLicenseRemoval(final AppsLicensesHandlerEvent p0);
}
