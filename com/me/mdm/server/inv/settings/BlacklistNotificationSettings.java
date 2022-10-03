package com.me.mdm.server.inv.settings;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class BlacklistNotificationSettings
{
    @SerializedName("blacklist_mode")
    public long blacklistMode;
    @SerializedName("days_to_notify_for")
    public int daysToNotifyFor;
    @SerializedName("notify_user")
    public boolean notifyUser;
    public transient Long customerID;
    
    public BlacklistNotificationSettings(final long blacklistMode, final int daysToNotifyFor, final boolean notifyUser) {
        this.blacklistMode = blacklistMode;
        this.daysToNotifyFor = daysToNotifyFor;
        this.notifyUser = notifyUser;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson((Object)this);
    }
}
