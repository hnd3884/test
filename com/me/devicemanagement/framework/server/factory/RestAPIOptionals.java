package com.me.devicemanagement.framework.server.factory;

import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import org.json.JSONObject;
import java.util.HashMap;

public abstract class RestAPIOptionals
{
    public int preInvoker(final HashMap parameterList, final JSONObject apiDetails) {
        return FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
    }
}
