package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkDataSecurityModel
{
    @JsonAlias({ "allow_share_doc_to_work_profile" })
    private boolean allowShareDocsToWorkProfile;
    @JsonAlias({ "allow_share_doc_to_personal_apps" })
    private boolean allowShareDocsToPersonalApps;
    @JsonAlias({ "allow_profile_content_to_other_apps" })
    private boolean allowProfileContentToOtherApps;
    @JsonAlias({ "allow_profile_contact_over_bluetooth" })
    private boolean allowProfileContactOverBluetooth;
    @JsonAlias({ "allow_profile_app_widgets_to_home_screen" })
    private boolean allowProfileAppWidgetsToHomescreen;
    @JsonAlias({ "allow_contact_in_personal_profile" })
    private boolean allowContactInPersonalProfile;
    @JsonAlias({ "allow_contact_access_to_personal_apps" })
    private boolean allowContactAccessToPersonalApps;
    
    public boolean isAllowShareDocsToWorkProfile() {
        return this.allowShareDocsToWorkProfile;
    }
    
    public void setAllowShareDocsToWorkProfile(final boolean allowShareDocsToWorkProfile) {
        this.allowShareDocsToWorkProfile = allowShareDocsToWorkProfile;
    }
    
    public boolean isAllowShareDocsToPersonalApps() {
        return this.allowShareDocsToPersonalApps;
    }
    
    public void setAllowShareDocsToPersonalApps(final boolean allowShareDocsToPersonalApps) {
        this.allowShareDocsToPersonalApps = allowShareDocsToPersonalApps;
    }
    
    public boolean isAllowProfileContentToOtherApps() {
        return this.allowProfileContentToOtherApps;
    }
    
    public void setAllowProfileContentToOtherApps(final boolean allowProfileContentToOtherApps) {
        this.allowProfileContentToOtherApps = allowProfileContentToOtherApps;
    }
    
    public boolean isAllowProfileContactOverBluetooth() {
        return this.allowProfileContactOverBluetooth;
    }
    
    public void setAllowProfileContactOverBluetooth(final boolean allowProfileContactOverBluetooth) {
        this.allowProfileContactOverBluetooth = allowProfileContactOverBluetooth;
    }
    
    public boolean isAllowProfileAppWidgetsToHomescreen() {
        return this.allowProfileAppWidgetsToHomescreen;
    }
    
    public void setAllowProfileAppWidgetsToHomescreen(final boolean allowProfileAppWidgetsToHomescreen) {
        this.allowProfileAppWidgetsToHomescreen = allowProfileAppWidgetsToHomescreen;
    }
    
    public boolean isAllowContactInPersonalProfile() {
        return this.allowContactInPersonalProfile;
    }
    
    public void setAllowContactInPersonalProfile(final boolean allowContactInPersonalProfile) {
        this.allowContactInPersonalProfile = allowContactInPersonalProfile;
    }
    
    public boolean isAllowContactAccessToPersonalApps() {
        return this.allowContactAccessToPersonalApps;
    }
    
    public void setAllowContactAccessToPersonalApps(final boolean allowContactAccessToPersonalApps) {
        this.allowContactAccessToPersonalApps = allowContactAccessToPersonalApps;
    }
}
