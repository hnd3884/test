package com.zoho.security.appfirewall;

import org.json.JSONException;
import java.util.Iterator;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;

public class InputStreamDirective extends AppFirewallDirective
{
    public InputStreamDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
        super(configuredDirective, directive);
        this.loadDirective(configuredDirective, directive);
    }
    
    public void loadDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
        this.initDirectiveComponent(configuredDirective, directive);
    }
    
    public JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
        boolean isBlackListedComponent = true;
        JSONArray array = null;
        for (final AppFirewallComponent component : this.getComponentList()) {
            String valueFromRequest = null;
            final String componentName2;
            final String componentName = componentName2 = component.getComponentName();
            switch (componentName2) {
                case "content": {
                    valueFromRequest = SecurityFrameworkUtil.getInputStreamContentforScanning(request);
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
