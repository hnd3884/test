package com.me.mdm.chrome.agent.core.communication;

import java.io.IOException;
import java.util.HashMap;
import org.json.JSONObject;

public abstract class CommunicationHandler
{
    public static final int HTTP_STATUS_SUCCESS = 0;
    public static final int HTTP_STATUS_FAILURE = 1;
    
    public abstract CommunicationStatus postData(final JSONObject p0, final HashMap<String, String> p1) throws IOException;
}
