package com.zoho.security.policy;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityFilterProperties;
import com.adventnet.iam.security.ActionRule;
import com.adventnet.iam.security.SecurityRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.iam.security.Authenticator;
import java.util.logging.Logger;

public class SecurityPolicyValidator
{
    private static final Logger LOGGER;
    private static Authenticator authenticationImpl;
    static SecurityPolicyCache cacheImpl;
    
    public static void validatePolicy(final HttpServletRequest request) {
        if (request instanceof SecurityRequestWrapper) {
            final SecurityRequestWrapper secureRequest = (SecurityRequestWrapper)request;
            final ActionRule rule = secureRequest.getURLActionRule();
            if (secureRequest.isIntegrationRequest()) {
                return;
            }
            if (rule != null) {
                validatePolicy(secureRequest, rule);
            }
        }
    }
    
    public static void validatePolicy(final SecurityRequestWrapper request, final ActionRule rule) {
        if (SecurityPolicyValidator.authenticationImpl == null) {
            SecurityPolicyValidator.authenticationImpl = SecurityFilterProperties.getInstance((HttpServletRequest)request).getAuthenticationProvider();
        }
        final String featureName = getFeatureName(rule);
        if (featureName == null) {
            return;
        }
        final List<SecurityPolicyRule> policyRules = SecurityPolicyValidator.cacheImpl.getPolicyRules(featureName);
        if (policyRules == null) {
            return;
        }
        final String requestedOperation = getOperation(rule);
        if (requestedOperation == null) {
            return;
        }
        for (final SecurityPolicyRule policyRule : policyRules) {
            final String principal = policyRule.getPrincipal();
            final PRINCIPAL object = PRINCIPAL.valueOfString(principal);
            if (object == null) {
                SecurityPolicyValidator.LOGGER.log(Level.WARNING, "Principal {0} is not supported", principal);
            }
            else {
                if (policyRule.getOperation() != null && !policyRule.getOperation().equals(requestedOperation)) {
                    continue;
                }
                boolean check = false;
                switch (object) {
                    case USER: {
                        final String user = SecurityPolicyValidator.authenticationImpl.getCurrentUserId(request);
                        if (policyRule.getPrincipalValue().equals(user)) {
                            check = true;
                            break;
                        }
                        break;
                    }
                    case PORTAL_USER: {
                        final String portalUser = SecurityPolicyValidator.authenticationImpl.getCurrentUserId(request);
                        if (policyRule.getPrincipalValue().equals(portalUser)) {
                            check = true;
                            break;
                        }
                        break;
                    }
                    case ROLE: {
                        if (SecurityPolicyValidator.authenticationImpl.isUserInRole(request, policyRule.getPrincipalValue())) {
                            check = true;
                            break;
                        }
                        break;
                    }
                    case GROUP: {
                        final String group = policyRule.getPrincipalValue();
                        if (SecurityPolicyValidator.authenticationImpl.isUserInGroup(request, group)) {
                            check = true;
                            break;
                        }
                        break;
                    }
                }
                if (!check) {
                    continue;
                }
                if (!policyRule.isAccessAllowed((HttpServletRequest)request)) {
                    SecurityPolicyValidator.LOGGER.log(Level.SEVERE, "#-- Access Denied for the principal : {0} --#", principal);
                    throw new SecurityPolicyException("ACCESS_DENIED", policyRule, request.getRequestURI(), request.getRemoteAddr());
                }
                return;
            }
        }
        throw new SecurityPolicyException("ACCESS_DENIED");
    }
    
    private static String getFeatureName(final ActionRule rule) {
        String featureName = rule.getModule();
        if (rule.getSubModule() != null) {
            featureName = featureName + "." + rule.getSubModule();
        }
        return featureName;
    }
    
    private static String getOperation(final ActionRule rule) {
        final String operation = rule.getOperation();
        if (operation == null) {
            final String lowerCase;
            final String method = lowerCase = rule.getMethod().toLowerCase();
            switch (lowerCase) {
                case "get": {
                    return "read";
                }
                case "post": {
                    return "create";
                }
                case "put":
                case "patch": {
                    return "update";
                }
                case "delete": {
                    return "delete";
                }
            }
        }
        return operation;
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityPolicyValidator.class.getName());
        SecurityPolicyValidator.authenticationImpl = null;
        SecurityPolicyValidator.cacheImpl = new SecurityPolicyCache();
    }
}
