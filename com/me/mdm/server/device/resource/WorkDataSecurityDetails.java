package com.me.mdm.server.device.resource;

import com.google.gson.annotations.SerializedName;

public class WorkDataSecurityDetails
{
    @SerializedName("allow_share_doc_to_work_profile")
    private boolean allowShareDocsToWorkProfile;
    @SerializedName("allow_share_doc_to_personal_apps")
    private boolean allowShareDocsToPersonalApps;
    @SerializedName("allow_profile_content_to_other_apps")
    private boolean allowProfileContentToOtherApps;
    @SerializedName("allow_share_work_profile_contact_over_bluetooth")
    private boolean allowShareProfileContactOverBluetooth;
    @SerializedName("allow_work_profile_app_widgets_to_home_screen")
    private boolean allowWorkProfileAppWidgetsToHomeScreen;
    @SerializedName("allow_work_contact_details_in_personal_profile")
    private boolean allowWorkContactDetailsInPersonalProfile;
    @SerializedName("allow_work_contact_access_to_personal_apps")
    private boolean allowWorkContactAccessToPersonalApps;
    
    public WorkDataSecurityDetails() {
        this.allowShareDocsToWorkProfile = false;
        this.allowShareDocsToPersonalApps = false;
        this.allowProfileContentToOtherApps = false;
        this.allowShareProfileContactOverBluetooth = false;
        this.allowWorkProfileAppWidgetsToHomeScreen = false;
        this.allowWorkContactDetailsInPersonalProfile = false;
        this.allowWorkContactAccessToPersonalApps = false;
    }
    
    public boolean getAllowShareDocsToWorkProfile() {
        return this.allowShareDocsToWorkProfile;
    }
    
    public void setAllowShareDocsToWorkProfile(final boolean allowShareDocsToWorkProfile) {
        this.allowShareDocsToWorkProfile = allowShareDocsToWorkProfile;
    }
    
    public boolean getAllowShareDocsToPersonalApps() {
        return this.allowShareDocsToPersonalApps;
    }
    
    public void setAllowShareDocsToPersonalApps(final boolean allowShareDocsToPersonalApps) {
        this.allowShareDocsToPersonalApps = allowShareDocsToPersonalApps;
    }
    
    public boolean getAllowProfileContentToOtherApps() {
        return this.allowProfileContentToOtherApps;
    }
    
    public void setAllowProfileContentToOtherApps(final boolean allowProfileContentToOtherApps) {
        this.allowProfileContentToOtherApps = allowProfileContentToOtherApps;
    }
    
    public boolean getAllowShareProfileContactOverBluetooth() {
        return this.allowShareProfileContactOverBluetooth;
    }
    
    public void setAllowShareProfileContactOverBluetooth(final boolean allowShareProfileContactOverBluetooth) {
        this.allowShareProfileContactOverBluetooth = allowShareProfileContactOverBluetooth;
    }
    
    public boolean getAllowWorkProfileAppWidgetsToHomeScreen() {
        return this.allowWorkProfileAppWidgetsToHomeScreen;
    }
    
    public void setAllowWorkProfileAppWidgetsToHomeScreen(final boolean allowWorkProfileAppWidgetsToHomeScreen) {
        this.allowWorkProfileAppWidgetsToHomeScreen = allowWorkProfileAppWidgetsToHomeScreen;
    }
    
    public boolean getAllowWorkContactDetailsInPersonalProfile() {
        return this.allowWorkContactDetailsInPersonalProfile;
    }
    
    public void setAllowWorkContactDetailsInPersonalProfile(final boolean allowWorkContactDetailsInPersonalProfile) {
        this.allowWorkContactDetailsInPersonalProfile = allowWorkContactDetailsInPersonalProfile;
    }
    
    public boolean getAllowWorkContactAccessToPersonalApps() {
        return this.allowWorkContactAccessToPersonalApps;
    }
    
    public void setAllowWorkContactAccessToPersonalApps(final boolean allowWorkContactAccessToPersonalApps) {
        this.allowWorkContactAccessToPersonalApps = allowWorkContactAccessToPersonalApps;
    }
}
