package com.me.mdm.server.tracker.mics;

import org.json.JSONObject;
import com.me.mdm.api.APIRequest;

public interface MICSDataAPI
{
    void addData(final APIRequest p0);
    
    void postDataToMicsForMailer(final JSONObject p0);
}
