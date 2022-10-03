package com.zoho.security.agent.notification;

import org.json.JSONArray;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_APPSENSE_NOTIFICATION;
import com.zoho.security.appfirewall.AppFirewallInitializer;
import com.zoho.security.appfirewall.AppFirewallPolicyLoader;
import com.zoho.security.agent.AppSenseAgent;
import com.adventnet.iam.security.SecurityFilterProperties;
import org.json.JSONObject;
import com.zoho.security.agent.Components;

public class AppFirewallNotification extends DefaultNotificationReceiver
{
    @Override
    public boolean receive(final Components.COMPONENT component, final Components.COMPONENT_NAME subComponent, final JSONObject dataObj) {
        try {
            final String actualServiceName = SecurityFilterProperties.getServiceName();
            final JSONArray ruleAutoID = dataObj.getJSONArray("VALUE");
            JSONObject ruleObject = null;
            switch (subComponent) {
                case RULEADDITION: {
                    ruleObject = AppFirewallPolicyLoader.getAppFireWallJSONObject(AppSenseAgent.getFireWallRulesLoaderURL(), actualServiceName, ruleAutoID);
                    AppFirewallInitializer.addAppFirewallRuleOnNotification((Object)ruleObject);
                    break;
                }
                case RULEUPDATION: {
                    ruleObject = AppFirewallPolicyLoader.getAppFireWallJSONObject(AppSenseAgent.getFireWallRulesLoaderURL(), actualServiceName, ruleAutoID);
                    AppFirewallInitializer.removeFireWallRulesByID(ruleAutoID);
                    AppFirewallInitializer.addAppFirewallRuleOnNotification((Object)ruleObject);
                    break;
                }
                case RULEDELETION: {
                    AppFirewallInitializer.removeFireWallRulesByID(ruleAutoID);
                    break;
                }
            }
            ZSEC_APPSENSE_NOTIFICATION.pushSuccess("APPFIREWALL", ruleAutoID.toString(), (ExecutionTimer)null);
            return true;
        }
        catch (final Exception e) {
            ZSEC_APPSENSE_NOTIFICATION.pushExceptionWithComponent("APPFIREWALL", e.getMessage(), (ExecutionTimer)null);
            return false;
        }
    }
}
