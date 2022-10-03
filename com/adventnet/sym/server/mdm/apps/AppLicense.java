package com.adventnet.sym.server.mdm.apps;

import java.util.List;

public class AppLicense
{
    List appLicenseStatus;
    String orderNumber;
    String purchasedDate;
    String purchaser;
    String licenseFileName;
    String productName;
    String identifier;
    int licenseCount;
    int licenseAlreadyUsedCount;
    int licenseRemainingCount;
    int licenseType;
    boolean isMigrated;
    Long licenseID;
    Long licenseDetailsID;
    Long customerID;
    Long businessStoreId;
    Long appGroupId;
    
    public AppLicense() {
        this.appLicenseStatus = null;
        this.orderNumber = null;
        this.purchasedDate = null;
        this.purchaser = null;
        this.licenseFileName = null;
        this.productName = null;
        this.identifier = null;
        this.licenseCount = 0;
        this.licenseAlreadyUsedCount = 0;
        this.licenseRemainingCount = 0;
        this.licenseType = 0;
        this.isMigrated = false;
        this.licenseID = null;
        this.licenseDetailsID = null;
        this.customerID = null;
        this.businessStoreId = null;
        this.appGroupId = null;
    }
}
