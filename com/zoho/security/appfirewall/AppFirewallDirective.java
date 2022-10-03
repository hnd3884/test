package com.zoho.security.appfirewall;

import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;

public abstract class AppFirewallDirective
{
    private static final Logger LOGGER;
    public static final String OPERATOR = "operator";
    public static final String LENGTH = "length";
    public static final String VALUEFROMREQUEST = "valueFromRequest";
    private List<AppFirewallComponent> componentList;
    DirectiveConfiguration.Directive directive;
    String id;
    public static final String FILECONTENT = "";
    
    public AppFirewallDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
        this.id = null;
        this.setDirective(directive);
        this.setId(configuredDirective);
    }
    
    public AppFirewallDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        this.id = null;
        this.setDirective(directive);
        this.setId(configuredDirectives.get(0));
    }
    
    private void setId(final DirectiveConfiguration configuredDirective) {
        this.id = configuredDirective.getId();
    }
    
    public String getId() {
        return this.id;
    }
    
    abstract Object findBlackListComponent(final HttpServletRequest p0) throws JSONException;
    
    void initDirectiveComponent(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
        this.componentList = configuredDirective.getComponentList();
    }
    
    String contentShortner(final String valueFromRequest) {
        if (valueFromRequest.length() > 100) {
            return valueFromRequest.substring(0, 100) + " ...";
        }
        return valueFromRequest;
    }
    
    public void setDirective(final DirectiveConfiguration.Directive directive) {
        this.directive = directive;
    }
    
    public DirectiveConfiguration.Directive getDirective() {
        return this.directive;
    }
    
    public List<AppFirewallComponent> getComponentList() {
        return this.componentList;
    }
    
    public JSONArray toJSON() {
        final JSONArray directiveArray = new JSONArray();
        try {
            final JSONObject directiveJSON = new JSONObject();
            if (this.id != null) {
                directiveJSON.put("id", (Object)this.id);
            }
            for (final AppFirewallComponent component : this.componentList) {
                directiveJSON.put(component.getComponentName(), (Object)component.toJSON());
            }
            directiveArray.put((Object)directiveJSON);
        }
        catch (final JSONException e) {
            AppFirewallDirective.LOGGER.log(Level.SEVERE, " json Exception occurred while constructing the json for \"{0}\" error log : {1}", new Object[] { this.getDirective().getValue(), e.getMessage() });
        }
        return directiveArray;
    }
    
    protected JSONArray getComponentErrorJSON(JSONArray array, final AppFirewallComponent component, final String valueFromRequest) {
        if (array == null) {
            array = new JSONArray();
        }
        if (component.getId() != null) {
            final JSONObject object = new JSONObject();
            object.put("id", (Object)component.getId());
            object.put("value", (Object)this.contentShortner(valueFromRequest));
            array.put((Object)object);
        }
        else {
            final JSONObject object = new JSONObject();
            object.put("name", (Object)component.getComponentName());
            object.put("value", (Object)component.toJSON().put("valueFromRequest", (Object)this.contentShortner(valueFromRequest)));
            array.put((Object)object);
        }
        return array;
    }
    
    static {
        LOGGER = Logger.getLogger(AppFirewallDirective.class.getName());
    }
}
