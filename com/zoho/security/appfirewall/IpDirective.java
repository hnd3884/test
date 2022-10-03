package com.zoho.security.appfirewall;

import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;

public class IpDirective extends AppFirewallDirective
{
    public static final String LB_ADDED_REMOTE_IP_HEADER = "LB_SSL_REMOTE_IP";
    
    public IpDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
        super(configuredDirective, directive);
        this.loadDirective(configuredDirective, directive);
    }
    
    public void loadDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
        this.initDirectiveComponent(configuredDirective, directive);
    }
    
    @Override
    JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
        boolean isBlackListedComponent = true;
        JSONArray array = null;
        for (final AppFirewallComponent component : this.getComponentList()) {
            String valueFromRequest = null;
            final String componentName = component.getComponentName();
            final String ipAddr = request.getHeader("LB_SSL_REMOTE_IP");
            final String s = componentName;
            switch (s) {
                case "value": {
                    valueFromRequest = ((ipAddr == null) ? request.getRemoteAddr() : ipAddr);
                    isBlackListedComponent = (isBlackListedComponent && component.isBlackListed(valueFromRequest));
                    break;
                }
            }
            if (!isBlackListedComponent) {
                break;
            }
            array = this.getComponentErrorJSON(array, component, valueFromRequest);
        }
        return isBlackListedComponent ? new JSONArray().put((Object)array) : null;
    }
}
