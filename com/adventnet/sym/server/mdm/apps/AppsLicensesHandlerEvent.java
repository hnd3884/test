package com.adventnet.sym.server.mdm.apps;

import java.util.Properties;

public class AppsLicensesHandlerEvent
{
    private Long customerID;
    private Properties licenseDetails;
    
    public AppsLicensesHandlerEvent(final Long customerID, final Properties licenseDetails) {
        this.customerID = null;
        this.licenseDetails = new Properties();
        this.customerID = customerID;
        this.licenseDetails = licenseDetails;
    }
    
    public Properties getLicenseDetails() {
        return this.licenseDetails;
    }
    
    public Long getCustomerID() {
        return this.customerID;
    }
}
