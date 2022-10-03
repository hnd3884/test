package com.me.mdm.server.updates.osupdates.android;

import org.json.JSONObject;
import com.me.mdm.server.updates.osupdates.OSUpdateProfileValidator;

public class OSUpdateProfileAndroidValidator extends OSUpdateProfileValidator
{
    private static Long days;
    
    @Override
    protected boolean checkWindowTime(final JSONObject deploymentTemplateJSON) {
        final Long startTime = 60L * deploymentTemplateJSON.optLong("WINDOW_START_TIME");
        final Long endTime = 60L * deploymentTemplateJSON.optLong("WINDOW_END_TIME");
        if (endTime >= startTime) {
            if (endTime - startTime < 10800L) {
                return false;
            }
        }
        else if (endTime * OSUpdateProfileAndroidValidator.days - startTime < 10800L) {
            return false;
        }
        return true;
    }
    
    static {
        OSUpdateProfileAndroidValidator.days = 86400L;
    }
}
