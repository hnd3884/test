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

public class ParametersDirective extends AppFirewallDirective
{
    private static final Logger LOGGER;
    static final String PARAMNODE = "param";
    List<ParameterDirective> parameterDirectiveList;
    
    public ParametersDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        super(configuredDirectives, directive);
        this.loadDirective(configuredDirectives, directive);
    }
    
    public void loadDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        this.parameterDirectiveList = new ArrayList<ParameterDirective>();
        for (final DirectiveConfiguration configuredDirective : configuredDirectives) {
            this.parameterDirectiveList.add(new ParameterDirective(configuredDirective, directive));
        }
    }
    
    public JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
        JSONArray parametersCollectiveJSON = null;
        for (final ParameterDirective parameterDirective : this.parameterDirectiveList) {
            final JSONArray parameterDirectiveErrorJSON = parameterDirective.findBlackListComponent(request);
            if (parameterDirectiveErrorJSON == null) {
                return null;
            }
            if (parametersCollectiveJSON == null) {
                parametersCollectiveJSON = new JSONArray();
            }
            parametersCollectiveJSON.put((Object)parameterDirectiveErrorJSON);
        }
        return parametersCollectiveJSON;
    }
    
    @Override
    public JSONArray toJSON() {
        final JSONArray parameterJSONArray = new JSONArray();
        try {
            for (final ParameterDirective parameterDirective : this.parameterDirectiveList) {
                final JSONObject componentJSON = new JSONObject();
                if (this.id != null) {
                    componentJSON.put("id", (Object)this.id);
                }
                for (final AppFirewallComponent component : parameterDirective.getComponentList()) {
                    componentJSON.put(component.getComponentName(), (Object)component.toJSON());
                }
                parameterJSONArray.put((Object)componentJSON);
            }
        }
        catch (final JSONException e) {
            ParametersDirective.LOGGER.log(Level.SEVERE, "Exception Occurred while generating ComponentJSON :: Exception :: {0}", e.getMessage());
        }
        return parameterJSONArray;
    }
    
    static {
        LOGGER = Logger.getLogger(ParameterDirective.class.getName());
    }
    
    class ParameterDirective extends AppFirewallDirective
    {
        public ParameterDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
            super(configuredDirective, directive);
            this.loadDirective(configuredDirective, directive);
        }
        
        public void loadDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
            this.initDirectiveComponent(configuredDirective, directive);
        }
        
        @Override
        JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
            final Enumeration<String> parameterNames = request.getParameterNames();
            if (parameterNames != null) {
                boolean isBlackListedParameter = false;
                while (parameterNames.hasMoreElements()) {
                    JSONArray array = null;
                    final String parameterName = parameterNames.nextElement();
                    for (final AppFirewallComponent component : this.getComponentList()) {
                        String valueFromRequest = null;
                        final String componentName2;
                        final String componentName = componentName2 = component.getComponentName();
                        switch (componentName2) {
                            case "name": {
                                valueFromRequest = parameterName;
                                isBlackListedParameter = this.isBlackListedParameterName(valueFromRequest, component);
                                break;
                            }
                            case "value": {
                                valueFromRequest = this.findBlackListedParameterValue(request, parameterName, component);
                                isBlackListedParameter = (isBlackListedParameter && valueFromRequest != null);
                                break;
                            }
                        }
                        if (!isBlackListedParameter) {
                            break;
                        }
                        array = this.getComponentErrorJSON(array, component, valueFromRequest);
                    }
                    if (isBlackListedParameter) {
                        return array;
                    }
                }
            }
            return null;
        }
        
        boolean isBlackListedParameterName(final String parameterName, final AppFirewallComponent component) {
            return component.isBlackListed(parameterName);
        }
        
        String findBlackListedParameterValue(final HttpServletRequest request, final String parameterName, final AppFirewallComponent component) {
            boolean matchFound = false;
            final String[] parameterValues2;
            final String[] parameterValues = parameterValues2 = request.getParameterValues(parameterName);
            for (final String parameterValue : parameterValues2) {
                matchFound = (matchFound || component.isBlackListed(parameterValue));
                if (matchFound) {
                    return parameterValue;
                }
            }
            return null;
        }
    }
}
