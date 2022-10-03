package org.owasp.esapi.reference.accesscontrol.policyloader;

import java.lang.reflect.Constructor;
import org.owasp.esapi.AccessControlRule;
import org.owasp.esapi.errors.AccessControlException;
import java.util.HashMap;
import java.util.Map;

public final class PolicyDTO
{
    private Map accessControlRules;
    
    public PolicyDTO() {
        this.accessControlRules = new HashMap();
    }
    
    public Map getAccessControlRules() {
        return this.accessControlRules;
    }
    
    public void addAccessControlRule(final String key, final String accessControlRuleClassName, final Object policyParameter) throws AccessControlException {
        if (this.accessControlRules.get(key) != null) {
            throw new AccessControlException("Duplicate keys are not allowed. Key: " + key, "");
        }
        try {
            final Class accessControlRuleClass = Class.forName(accessControlRuleClassName, false, this.getClass().getClassLoader());
            final Constructor accessControlRuleConstructor = accessControlRuleClass.getConstructor((Class[])new Class[0]);
            final AccessControlRule accessControlRule = accessControlRuleConstructor.newInstance(new Object[0]);
            accessControlRule.setPolicyParameters(policyParameter);
            this.accessControlRules.put(key, accessControlRule);
        }
        catch (final Exception e) {
            throw new AccessControlException("Unable to create Access Control Rule for key: \"" + key + "\" with policyParameters: \"" + policyParameter + "\"", "", e);
        }
    }
    
    @Override
    public String toString() {
        return this.accessControlRules.toString();
    }
}
