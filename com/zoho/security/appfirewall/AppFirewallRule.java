package com.zoho.security.appfirewall;

import java.util.Iterator;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONObject;
import java.util.List;

public class AppFirewallRule
{
    private List<AppFirewallDirective> validFirewallDirectives;
    private boolean isPostStage;
    private boolean isPostAuthenticationStage;
    private boolean validAppFirewallRule;
    private String description;
    private JSONObject ruleJSON;
    private String id;
    private Map<String, String> actions;
    private long expiryTime;
    
    public AppFirewallRule() {
        this.validFirewallDirectives = new ArrayList<AppFirewallDirective>();
        this.isPostStage = false;
        this.isPostAuthenticationStage = false;
        this.validAppFirewallRule = true;
        this.ruleJSON = new JSONObject();
        this.id = null;
        this.expiryTime = -1L;
    }
    
    public void setValidFirewallDirectives(final List<AppFirewallDirective> validFirewallDirectives) {
        this.validFirewallDirectives = validFirewallDirectives;
    }
    
    public List<AppFirewallDirective> getValidFirewallDirectives() {
        return this.validFirewallDirectives;
    }
    
    public void setPostStage(final boolean isPostStage) {
        this.isPostStage = isPostStage;
    }
    
    public void setPostAuthenticationStage(final boolean isPostStage) {
        this.isPostAuthenticationStage = isPostStage;
    }
    
    public boolean isPostStage() {
        return this.isPostStage;
    }
    
    public boolean isPostAuthenticationStage() {
        return this.isPostAuthenticationStage;
    }
    
    public void setValidAppFirewallRule(final boolean validAppFirewallRule) {
        this.validAppFirewallRule = validAppFirewallRule;
    }
    
    public boolean isValidAppFirewallRule() {
        return this.validAppFirewallRule;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public JSONObject getRuleJSON() {
        return this.ruleJSON;
    }
    
    public JSONObject toJSON() {
        final JSONObject directiveJSON = new JSONObject();
        try {
            if (this.id != null) {
                this.ruleJSON.put("id", (Object)this.getId());
            }
            this.ruleJSON.put("description", (Object)this.getDescription());
            this.ruleJSON.put("expiry_time", this.getExpiryTime());
            if (this.actions != null) {
                final JSONObject actionObj = new JSONObject();
                for (final Map.Entry<String, String> action : this.actions.entrySet()) {
                    actionObj.put((String)action.getKey(), (Object)action.getValue());
                }
                this.ruleJSON.put("actions", (Object)actionObj);
            }
            for (final AppFirewallDirective appFirewallDirective : this.getValidFirewallDirectives()) {
                directiveJSON.put(appFirewallDirective.getDirective().toString(), (Object)appFirewallDirective.toJSON());
            }
            this.ruleJSON.put("directive", (Object)directiveJSON);
            return this.ruleJSON;
        }
        catch (final JSONException e) {
            Logger.getLogger(AppFirewallRule.class.getName()).log(Level.SEVERE, " json Exception occurred while constructing the json , error log : {1}", new Object[] { e.getMessage() });
            return null;
        }
    }
    
    public void setID(final String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setActions(final Map<String, String> actionsMap) {
        this.actions = actionsMap;
    }
    
    public Map<String, String> getActions() {
        return this.actions;
    }
    
    public long getExpiryTime() {
        return this.expiryTime;
    }
    
    public void setExpiryTime(final long time) {
        this.expiryTime = time;
    }
    
    public boolean isExpired() {
        return this.expiryTime != -1L && this.expiryTime < System.currentTimeMillis();
    }
    
    public enum ACTIONS
    {
        BLOCK(0, "BLOCK"), 
        REDIRECTION(1, "REDIRECTION");
        
        private int index;
        private String action;
        
        private ACTIONS(final int val, final String actionStr) {
            this.index = val;
            this.action = actionStr;
        }
        
        public int index() {
            return this.index;
        }
        
        public String action() {
            return this.action;
        }
        
        public static String getAction(final int actionInt) {
            switch (actionInt) {
                case 1: {
                    return ACTIONS.REDIRECTION.action();
                }
                default: {
                    return ACTIONS.BLOCK.action();
                }
            }
        }
    }
}
