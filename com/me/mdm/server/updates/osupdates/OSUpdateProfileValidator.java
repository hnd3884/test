package com.me.mdm.server.updates.osupdates;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.server.updates.osupdates.chrome.OSUpdateProfileChromeValidator;
import com.me.mdm.server.updates.osupdates.android.OSUpdateProfileAndroidValidator;
import java.util.ArrayList;
import java.util.List;

public class OSUpdateProfileValidator
{
    List<String> failedKeys;
    
    public OSUpdateProfileValidator() {
        this.failedKeys = new ArrayList<String>();
    }
    
    public static OSUpdateProfileValidator getInstance(final Integer platformType) {
        switch (platformType) {
            case 2: {
                return new OSUpdateProfileAndroidValidator();
            }
            case 4: {
                return new OSUpdateProfileChromeValidator();
            }
            default: {
                return new OSUpdateProfileValidator();
            }
        }
    }
    
    public void validatePostData(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final JSONObject osupdatePolicyJSON = requestJSON.getJSONObject("OSUpdatePolicy");
            final JSONObject deploymentWindowTemplateJSON = requestJSON.optJSONObject("DeploymentWindowTemplate");
            final JSONObject deploymentNotifyTemplate = requestJSON.optJSONObject("DeploymentNotifTemplate");
            final JSONObject deploymentPolicySetting = requestJSON.optJSONObject("DeploymentPolicySettings");
            this.osUpdateJSONValidate(osupdatePolicyJSON);
            this.deploymentWindowTemplateValidate(deploymentWindowTemplateJSON);
            this.deploymentNotifyTemplateValidate(deploymentNotifyTemplate);
            this.deploymentSettingValidate(deploymentPolicySetting);
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[] { this.failedKeys });
        }
        if (!this.failedKeys.isEmpty()) {
            throw new APIHTTPException("COM0005", new Object[] { this.failedKeys });
        }
    }
    
    private void osUpdateJSONValidate(final JSONObject osUpdatePolicyJSON) {
        if (!this.checkPolicyType(osUpdatePolicyJSON)) {
            this.failedKeys.add("POLICY_TYPE");
        }
        if (!this.checkDeferDays(osUpdatePolicyJSON)) {
            this.failedKeys.add("DEFER_DAYS");
        }
    }
    
    private void deploymentWindowTemplateValidate(final JSONObject deploymentTemplateJSON) {
        if (deploymentTemplateJSON != null) {
            if (!this.checkWindowWeekMonth(deploymentTemplateJSON)) {
                this.failedKeys.add("WINDOW_WEEK_OF_MONTH");
            }
            if (!this.checkWindowDayWeek(deploymentTemplateJSON)) {
                this.failedKeys.add("WINDOW_DAY_OF_WEEK");
            }
            if (!this.checkWindowTime(deploymentTemplateJSON)) {
                this.failedKeys.add("WINDOW_START_TIME");
                this.failedKeys.add("WINDOW_END_TIME");
            }
        }
    }
    
    private void deploymentNotifyTemplateValidate(final JSONObject deploymentTemplateJSON) {
        if (deploymentTemplateJSON != null) {
            if (!this.checkNotifyTitle(deploymentTemplateJSON)) {
                this.failedKeys.add("NOTIFY_TITLE");
            }
            if (!this.checkNotifyMessage(deploymentTemplateJSON)) {
                this.failedKeys.add("NOTIFY_MESSAGE");
            }
            if (!this.checkNotifySkip(deploymentTemplateJSON)) {
                this.failedKeys.add("ALLOW_USERS_TO_SKIP");
            }
            if (!this.checkNotifyMaxSkip(deploymentTemplateJSON)) {
                this.failedKeys.add("MAX_SKIPS_ALLOWED");
            }
        }
    }
    
    private void deploymentSettingValidate(final JSONObject deploymentTemplateJSON) {
        if (deploymentTemplateJSON != null) {
            if (!this.checkSettingTarget(deploymentTemplateJSON)) {
                this.failedKeys.add("MAX_TARGET_PREFIX");
            }
            if (!this.checkSettingReboot(deploymentTemplateJSON)) {
                this.failedKeys.add("REBOOT_AFTER_UPDATE");
            }
        }
    }
    
    protected boolean checkPolicyType(final JSONObject osUpdatePolicyJSON) {
        try {
            osUpdatePolicyJSON.getInt("POLICY_TYPE");
        }
        catch (final JSONException e) {
            return false;
        }
        return true;
    }
    
    protected boolean checkDeferDays(final JSONObject osUpdatePolicyJSON) {
        try {
            final Integer policyType = osUpdatePolicyJSON.getInt("POLICY_TYPE");
            if (policyType == 3) {
                final Integer differDays = osUpdatePolicyJSON.getInt("DEFER_DAYS");
                if (differDays > 90) {
                    return false;
                }
            }
        }
        catch (final JSONException e) {
            return false;
        }
        return true;
    }
    
    protected boolean checkWindowWeekMonth(final JSONObject deploymentTemplateJSON) {
        return true;
    }
    
    protected boolean checkWindowDayWeek(final JSONObject deploymentTemplateJSON) {
        return true;
    }
    
    protected boolean checkWindowTime(final JSONObject deploymentTemplateJSON) {
        return true;
    }
    
    protected boolean checkNotifyTitle(final JSONObject deploymentTemplateJSON) {
        return true;
    }
    
    protected boolean checkNotifyMessage(final JSONObject deploymentTemplateJSON) {
        return true;
    }
    
    protected boolean checkNotifySkip(final JSONObject deploymentTemplateJSON) {
        return true;
    }
    
    protected boolean checkNotifyMaxSkip(final JSONObject deploymentTemplateJSON) {
        return true;
    }
    
    protected boolean checkSettingTarget(final JSONObject deploymentTemplateJSON) {
        return true;
    }
    
    protected boolean checkSettingReboot(final JSONObject deploymentTemplateJSON) {
        return true;
    }
}
