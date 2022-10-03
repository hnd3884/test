package com.zoho.security.appfirewall;

import org.json.JSONException;
import java.util.Iterator;
import com.adventnet.iam.security.SecurityUtil;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;

public class ServerDirective extends AppFirewallDirective
{
    public ServerDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
        super(configuredDirective, directive);
        this.loadDirective(configuredDirective, directive);
    }
    
    public void loadDirective(final DirectiveConfiguration configuredDirectives, final DirectiveConfiguration.Directive directive) {
        this.initDirectiveComponent(configuredDirectives, directive);
    }
    
    public JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
        boolean isBlackListedComponent = true;
        JSONArray array = null;
        for (final AppFirewallComponent component : this.getComponentList()) {
            String valueFromRequest = null;
            final String componentName2;
            final String componentName = componentName2 = component.getComponentName();
            switch (componentName2) {
                case "name": {
                    valueFromRequest = SecurityUtil.getServerName(request);
                    isBlackListedComponent = (isBlackListedComponent && component.isBlackListed(valueFromRequest));
                    break;
                }
                case "port": {
                    valueFromRequest = Integer.toString(request.getServerPort());
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
