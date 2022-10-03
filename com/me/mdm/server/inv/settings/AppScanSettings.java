package com.me.mdm.server.inv.settings;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class AppScanSettings
{
    @SerializedName("show_system_apps")
    public boolean showSystemApps;
    @SerializedName("show_user_installed_apps")
    public boolean showUserInstalledApps;
    @SerializedName("show_managed_apps")
    public boolean showManagedApps;
    @SerializedName("enable_summary_alert")
    public Boolean enableSummaryAlert;
    @SerializedName("enable_app_discovery_alert")
    public Boolean enableAppDiscoveryAlert;
    @SerializedName("enable_blacklist_alert")
    public Boolean enableBlacklistAlert;
    @SerializedName("admin_email")
    public String adminEmail;
    public transient Long customerID;
    
    public AppScanSettings(final boolean showUserInstalledApps, final boolean showSystemApps, final boolean showManagedApps) {
        this.showManagedApps = showManagedApps;
        this.showSystemApps = showSystemApps;
        this.showUserInstalledApps = showUserInstalledApps;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson((Object)this);
    }
}
