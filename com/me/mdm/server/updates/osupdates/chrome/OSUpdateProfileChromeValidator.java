package com.me.mdm.server.updates.osupdates.chrome;

import org.json.JSONException;
import org.json.JSONObject;
import com.me.mdm.server.updates.osupdates.OSUpdateProfileValidator;

public class OSUpdateProfileChromeValidator extends OSUpdateProfileValidator
{
    @Override
    protected boolean checkDeferDays(final JSONObject osUpdatePolicyJSON) {
        try {
            final Integer policyType = osUpdatePolicyJSON.getInt("POLICY_TYPE");
            if (policyType == 3) {
                final Integer differDays = osUpdatePolicyJSON.getInt("DEFER_DAYS");
                if (differDays > 14) {
                    return false;
                }
            }
        }
        catch (final JSONException e) {
            return false;
        }
        return true;
    }
}
