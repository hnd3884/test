package com.me.mdm.server.factory;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public interface MDMGDPRSettingAPI
{
    JSONObject getGDPRSettings();
    
    JSONArray getIntegratedAppsProtocol();
    
    void disableTracking();
    
    void enableTracking();
    
    boolean isTwoFactorEnabled();
    
    boolean getDefaultDblocksUploadSettings();
    
    Map getSecureSettings(final Long p0);
    
    long setSecureSettings(final JSONObject p0);
    
    long calculatePercentageSecure(final int p0);
    
    long findScore(final long p0);
    
    void setAutomaticUpload(final boolean p0);
}
