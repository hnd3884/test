package com.me.mdm.server.factory;

import org.json.JSONObject;

public interface PersonalizationAPI
{
    JSONObject getPersonalizeSettings();
    
    JSONObject updatePersonalisationSettings(final JSONObject p0);
}
