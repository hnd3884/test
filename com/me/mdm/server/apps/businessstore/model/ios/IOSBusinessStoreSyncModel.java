package com.me.mdm.server.apps.businessstore.model.ios;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.server.apps.businessstore.model.BusinessStoresSyncModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IOSBusinessStoreSyncModel extends BusinessStoresSyncModel
{
    @JsonProperty("apps_with_insufficient_licenses")
    private Integer appWithInsufficientLicenses;
    @JsonProperty("if_licenses_insufficient")
    private boolean ifLicensesInsufficient;
    @JsonProperty("other_mdm_hostname")
    private String otherMDMHostName;
    @JsonProperty("free_to_vpp_apps_count")
    private Integer freeToVPPAppsCount;
    
    public void setFreeToVPPAppsCount(final int freeToVPPAppsCount) {
        this.freeToVPPAppsCount = freeToVPPAppsCount;
    }
    
    public void setOtherMDMHostName(final String otherMDMHostName) {
        this.otherMDMHostName = otherMDMHostName;
    }
    
    public void setAppWithInsufficientLicenses(final int appWithInsufficientLicenses) {
        this.appWithInsufficientLicenses = appWithInsufficientLicenses;
    }
    
    public void setIfLicensesInsufficient(final boolean ifLicensesInsufficient) {
        this.ifLicensesInsufficient = ifLicensesInsufficient;
    }
}
