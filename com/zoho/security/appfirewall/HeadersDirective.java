package com.zoho.security.appfirewall;

import java.util.Enumeration;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HeadersDirective extends AppFirewallDirective
{
    private static final Logger LOGGER;
    List<HeaderDirective> headerDirectiveList;
    
    public HeadersDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        super(configuredDirectives, directive);
        this.loadDirective(configuredDirectives, directive);
    }
    
    public void loadDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        this.headerDirectiveList = new ArrayList<HeaderDirective>();
        for (final DirectiveConfiguration configuredDirective : configuredDirectives) {
            this.headerDirectiveList.add(new HeaderDirective(configuredDirective, directive));
        }
    }
    
    public JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
        JSONArray headersCollectiveJSON = null;
        for (final HeaderDirective headerDirective : this.headerDirectiveList) {
            final JSONArray headerDirectiveErrorJSON = headerDirective.findBlackListComponent(request);
            if (headerDirectiveErrorJSON == null) {
                return null;
            }
            if (headersCollectiveJSON == null) {
                headersCollectiveJSON = new JSONArray();
            }
            headersCollectiveJSON.put((Object)headerDirectiveErrorJSON);
        }
        return headersCollectiveJSON;
    }
    
    @Override
    public JSONArray toJSON() {
        final JSONArray headersJSON = new JSONArray();
        try {
            for (final HeaderDirective headerDirective : this.headerDirectiveList) {
                final JSONObject componentJSON = new JSONObject();
                if (this.id != null) {
                    componentJSON.put("id", (Object)this.id);
                }
                for (final AppFirewallComponent component : headerDirective.getComponentList()) {
                    componentJSON.put(component.getComponentName(), (Object)component.toJSON());
                }
                headersJSON.put((Object)componentJSON);
            }
        }
        catch (final JSONException e) {
            HeadersDirective.LOGGER.log(Level.SEVERE, "Exception Occurred while generating ComponentJSON :: Exception :: {0}", e.getMessage());
        }
        return headersJSON;
    }
    
    static {
        LOGGER = Logger.getLogger(HeadersDirective.class.getName());
    }
    
    class HeaderDirective extends AppFirewallDirective
    {
        public HeaderDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
            super(configuredDirective, directive);
            this.loadDirective(configuredDirective, directive);
        }
        
        public void loadDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
            this.initDirectiveComponent(configuredDirective, directive);
        }
        
        @Override
        JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
            final Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                boolean isBlackListedheader = false;
                while (headerNames.hasMoreElements()) {
                    JSONArray array = null;
                    final String headerName = headerNames.nextElement();
                    for (final AppFirewallComponent component : this.getComponentList()) {
                        String valueFromRequest = null;
                        final String componentName2;
                        final String componentName = componentName2 = component.getComponentName();
                        switch (componentName2) {
                            case "name": {
                                valueFromRequest = headerName;
                                isBlackListedheader = this.isBlackListedHeaderName(valueFromRequest, component);
                                break;
                            }
                            case "value": {
                                valueFromRequest = this.findBlackListedHeaderValue(request, headerName, component);
                                isBlackListedheader = (isBlackListedheader && valueFromRequest != null);
                                break;
                            }
                        }
                        if (!isBlackListedheader) {
                            break;
                        }
                        array = this.getComponentErrorJSON(array, component, valueFromRequest);
                    }
                    if (isBlackListedheader) {
                        return array;
                    }
                }
            }
            return null;
        }
        
        boolean isBlackListedHeaderName(final String headerName, final AppFirewallComponent component) {
            return component.isBlackListed(headerName);
        }
        
        String findBlackListedHeaderValue(final HttpServletRequest request, final String headerName, final AppFirewallComponent component) {
            boolean matchFound = false;
            final Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                final String headerValue = headerValues.nextElement();
                matchFound = (matchFound || component.isBlackListed(headerValue));
                if (matchFound) {
                    return headerValue;
                }
            }
            return null;
        }
    }
}
