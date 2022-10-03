package com.me.mdm.onpremise.remotesession;

import java.util.HashMap;
import org.json.JSONObject;

public interface AssistApiHandlerImpl
{
    HashMap getAssistHeaderMap(final JSONObject p0);
    
    String getAssistSessionUrl(final String p0);
}
