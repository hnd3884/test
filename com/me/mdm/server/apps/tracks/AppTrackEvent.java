package com.me.mdm.server.apps.tracks;

import java.util.List;
import org.json.JSONArray;

public class AppTrackEvent
{
    public Long customerId;
    public Long appGroupId;
    public JSONArray applicableVersions;
    public String trackId;
    public String trackName;
    public String appName;
    public String version;
    public String versionCode;
    public List<String> trackIds;
    public static final int TRACK_ADDED = 1;
    public static final int TRACK_DELETED = 2;
    public static final int TRACK_UPDATED = 3;
    
    public AppTrackEvent(final Long customerId, final Long appGroupId, final String trackId) {
        this.customerId = customerId;
        this.trackId = trackId;
        this.appGroupId = appGroupId;
    }
    
    public AppTrackEvent(final Long customerId, final Long appGroupId) {
        this.customerId = customerId;
        this.appGroupId = appGroupId;
    }
    
    public AppTrackEvent(final Long customerId, final List<String> trackIds, final Long appGroupId) {
        this.customerId = customerId;
        this.trackIds = trackIds;
        this.appGroupId = appGroupId;
    }
    
    public AppTrackEvent() {
    }
}
