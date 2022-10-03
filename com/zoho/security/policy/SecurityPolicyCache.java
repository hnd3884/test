package com.zoho.security.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityPolicyCache
{
    private Map<String, List<SecurityPolicyRule>> map;
    
    public SecurityPolicyCache() {
        this.map = new HashMap<String, List<SecurityPolicyRule>>();
    }
    
    public List<SecurityPolicyRule> getPolicyRules(final String featureName) {
        return this.map.get(featureName);
    }
}
