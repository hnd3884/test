package com.zoho.security.policy;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class SecurityPolicyRule
{
    private static final Logger LOGGER;
    protected String principal;
    protected String principalValue;
    protected String access;
    protected String operation;
    protected List<SecurityPolicyHandler> handlers;
    
    public SecurityPolicyRule(final String principal, final String principalValue, final String access, final String operation) {
        this.principal = null;
        this.principalValue = null;
        this.access = null;
        this.operation = null;
        this.handlers = null;
        this.principal = principal;
        this.principalValue = principalValue;
        this.access = access;
        this.operation = operation;
    }
    
    public SecurityPolicyRule(final JSONObject json) {
        this(json, true);
    }
    
    public SecurityPolicyRule(final JSONObject json, final boolean defaultStruct) {
        this.principal = null;
        this.principalValue = null;
        this.access = null;
        this.operation = null;
        this.handlers = null;
        if (defaultStruct) {
            try {
                this.principal = json.getString("PRINCIPAL");
                this.principalValue = json.getString("VALUE");
                this.operation = json.getString("OPERATION");
                this.access = json.getString("ACCESS");
                final JSONObject policy = json.getJSONObject("POLICY");
                if (policy == null || policy.length() == 0) {
                    throw new SecurityPolicyException("INVALID_JSON_FORMAT");
                }
                for (final Object type : policy.keySet()) {
                    final String policyType = (String)type;
                    this.addHandler(SecurityPolicyHandler.getInstance(policyType, policy.getString(policyType)));
                }
            }
            catch (final JSONException e) {
                SecurityPolicyRule.LOGGER.log(Level.SEVERE, "Exception occured while creating SecurityPolicyRule instance for the JSONObject : {0}, Message : {1} ", new Object[] { json, e.getMessage() });
                throw new SecurityPolicyException("INVALID_JSON_FORMAT");
            }
        }
    }
    
    public String getPrincipal() {
        return this.principal;
    }
    
    public String getPrincipalValue() {
        return this.principalValue;
    }
    
    public String getAccess() {
        return this.access;
    }
    
    public String getOperation() {
        return this.operation;
    }
    
    protected void addHandler(final SecurityPolicyHandler handler) {
        if (this.handlers == null) {
            this.handlers = new ArrayList<SecurityPolicyHandler>();
        }
        this.handlers.add(handler);
    }
    
    public List<SecurityPolicyHandler> getHandlers() {
        return this.handlers;
    }
    
    public boolean isAccessAllowed(final HttpServletRequest request) {
        if (this.handlers == null) {
            return false;
        }
        boolean match = true;
        for (final SecurityPolicyHandler handler : this.handlers) {
            if (!match) {
                break;
            }
            match = handler.isAccessAllowed(request);
        }
        if ("allow".equals(this.access)) {
            return match;
        }
        return !match;
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityPolicyRule.class.getName());
    }
}
