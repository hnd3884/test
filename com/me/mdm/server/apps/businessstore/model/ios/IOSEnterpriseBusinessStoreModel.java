package com.me.mdm.server.apps.businessstore.model.ios;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.server.apps.businessstore.model.BusinessStoreModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IOSEnterpriseBusinessStoreModel extends BusinessStoreModel
{
    @JsonProperty("license_assign_type")
    private Integer licenseType;
    @JsonProperty("organization_name")
    private String organizationName;
    @JsonProperty("location_name")
    private String locationName;
    @JsonProperty("notification_mail")
    private String notificationMail;
    @JsonProperty("expiry_date")
    private String expiryDate;
    @JsonProperty("total_apps_count")
    private Integer totalAppsCount;
    @JsonProperty("org_type")
    private String orgType;
    @JsonProperty("non_vpp_apps_count")
    private Integer nonVppAppsCount;
    
    public void setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
    }
    
    public void setTotalAppsCount(final int totalAppsCount) {
        this.totalAppsCount = totalAppsCount;
    }
    
    public void setNotificationMail(final String notificationMail) {
        this.notificationMail = notificationMail;
    }
    
    public void setLocationName(final String locationName) {
        this.locationName = locationName;
    }
    
    public void setLicenseType(final int licenseType) {
        this.licenseType = licenseType;
    }
    
    public void setExpiryDate(final String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public void setOrgType(final String orgType) {
        this.orgType = orgType;
    }
    
    public void setNonVppAppsCount(final int nonVppAppsCount) {
        this.nonVppAppsCount = nonVppAppsCount;
    }
}
