package com.adventnet.iam.security;

import com.zoho.security.appfirewall.AppFirewallPolicyLoader;

public class RequestFirewall extends AppFirewallPolicyLoader
{
    public static void resetReqFirewallPatternInitializationFlag() {
        RequestFirewall.isAppFirewallPolicyInitialized = false;
    }
}
