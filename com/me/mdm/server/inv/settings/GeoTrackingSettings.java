package com.me.mdm.server.inv.settings;

import com.me.mdm.server.customgroup.resource.Group;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GeoTrackingSettings
{
    @SerializedName("tracking_mode")
    public Integer trackingMode;
    @SerializedName("location_radius")
    public Integer locationRadius;
    @SerializedName("location_tracking_interval")
    public Integer locationTrackingInterval;
    @SerializedName("apply_to_all")
    public Boolean applyToAll;
    @SerializedName("enable_location_history")
    public Boolean enableLocationHistory;
    @SerializedName("groups")
    public List<Long> groups;
    @SerializedName("groups_info")
    public List<Group> groupList;
    
    public GeoTrackingSettings() {
        this.enableLocationHistory = false;
    }
}
