package com.me.mdm.server.apps.blacklist;

import org.json.JSONObject;
import java.util.HashMap;

public interface BlacklistProcessorInterface
{
    Object processBlackListRequest(final HashMap p0) throws Exception;
    
    JSONObject processResponse(final Object p0) throws Exception;
}
