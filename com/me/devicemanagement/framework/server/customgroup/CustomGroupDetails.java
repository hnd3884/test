package com.me.devicemanagement.framework.server.customgroup;

public class CustomGroupDetails
{
    public static final int CUSTOM_GROUP_ADDED = 1;
    public static final int CUSTOM_GROUP_MODIFIED = 2;
    public static final int CUSTOM_GROUP_DELETED = 3;
    public static final int CG_MODIFIED_FOR_COMP_DELETED_EVENT = 4;
    public String domainName;
    public boolean isCriteriaCG;
    public boolean isStaticUniqueCG;
    public String groupName;
    public Long customerId;
    public int groupType;
    public int platformType;
    public boolean isEditable;
    public String groupDescription;
    public String description;
    public Long[] resourceIds;
    public String criteriaParams;
    public String resourceIdStr;
    public Long resourceId;
    public int groupPlatformType;
    public boolean isDummyDomain;
    public boolean isAllComputerGroup;
    public boolean isSecurityAddonFreeEditionCG;
    public boolean isDiscoveryAndRemediationCG;
    
    public CustomGroupDetails() {
        this.domainName = null;
        this.isCriteriaCG = false;
        this.isStaticUniqueCG = false;
        this.groupName = null;
        this.customerId = null;
        this.groupType = -1;
        this.platformType = -1;
        this.isEditable = false;
        this.groupDescription = null;
        this.description = null;
        this.resourceIds = null;
        this.criteriaParams = null;
        this.resourceIdStr = null;
        this.resourceId = null;
        this.groupPlatformType = -1;
        this.isDummyDomain = false;
        this.isAllComputerGroup = false;
        this.isSecurityAddonFreeEditionCG = false;
        this.isDiscoveryAndRemediationCG = false;
    }
}
