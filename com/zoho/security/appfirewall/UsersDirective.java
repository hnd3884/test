package com.zoho.security.appfirewall;

import com.adventnet.iam.AppAccount;
import com.adventnet.iam.ServiceOrg;
import com.adventnet.iam.User;
import com.adventnet.iam.IAMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UsersDirective extends AppFirewallDirective
{
    private static final Logger LOGGER;
    List<UserDirective> userDirectiveList;
    
    public UsersDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        super(configuredDirectives, directive);
        this.loadDirective(configuredDirectives, directive);
    }
    
    public void loadDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        this.userDirectiveList = new ArrayList<UserDirective>();
        for (final DirectiveConfiguration configuredDirective : configuredDirectives) {
            this.userDirectiveList.add(new UserDirective(configuredDirective, directive));
        }
    }
    
    public JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
        JSONArray usersCollectiveJSON = null;
        for (final UserDirective userDirective : this.userDirectiveList) {
            final JSONArray userDirectiveErrorJSON = userDirective.findBlackListComponent(request);
            if (userDirectiveErrorJSON == null) {
                return null;
            }
            if (usersCollectiveJSON == null) {
                usersCollectiveJSON = new JSONArray();
            }
            usersCollectiveJSON.put((Object)userDirectiveErrorJSON);
        }
        return usersCollectiveJSON;
    }
    
    @Override
    public JSONArray toJSON() {
        final JSONArray headersJSON = new JSONArray();
        try {
            for (final UserDirective headerDirective : this.userDirectiveList) {
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
            UsersDirective.LOGGER.log(Level.SEVERE, "Exception Occurred while generating ComponentJSON :: Exception :: {0}", e.getMessage());
        }
        return headersJSON;
    }
    
    static {
        LOGGER = Logger.getLogger(UsersDirective.class.getName());
    }
    
    class UserDirective extends AppFirewallDirective
    {
        public UserDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
            super(configuredDirective, directive);
            this.loadDirective(configuredDirective, directive);
        }
        
        public void loadDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
            this.initDirectiveComponent(configuredDirective, directive);
        }
        
        public JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
            JSONArray array = null;
            boolean isBlackListeduser = true;
            for (final AppFirewallComponent component : this.getComponentList()) {
                final String componentName = component.getComponentName();
                String valueFromRequest = null;
                User user = null;
                final String s = componentName;
                switch (s) {
                    case "zuid": {
                        user = IAMUtil.getCurrentUser();
                        if (user != null) {
                            final long zuid = user.getZUID();
                            valueFromRequest = Long.toString(zuid);
                            isBlackListeduser = (zuid != -1L && (isBlackListeduser && component.isBlackListed(valueFromRequest)));
                            break;
                        }
                        isBlackListeduser = false;
                        break;
                    }
                    case "email": {
                        user = IAMUtil.getCurrentUser();
                        if (user != null) {
                            valueFromRequest = user.getPrimaryEmail();
                            isBlackListeduser = (isBlackListeduser && component.isBlackListed(valueFromRequest));
                            break;
                        }
                        isBlackListeduser = false;
                        break;
                    }
                    case "zsoid": {
                        final ServiceOrg serviceOrg = IAMUtil.getCurrentServiceOrg();
                        if (serviceOrg != null) {
                            final long zsoid = serviceOrg.getZSOID();
                            valueFromRequest = Long.toString(zsoid);
                            isBlackListeduser = (zsoid != -1L && (isBlackListeduser && component.isBlackListed(valueFromRequest)));
                            break;
                        }
                        isBlackListeduser = false;
                        break;
                    }
                    case "zaaid": {
                        final AppAccount appAccount = IAMUtil.getCurrentAppAccount();
                        if (appAccount != null) {
                            valueFromRequest = appAccount.getZaaid();
                            isBlackListeduser = (isBlackListeduser && component.isBlackListed(valueFromRequest));
                            break;
                        }
                        isBlackListeduser = false;
                        break;
                    }
                }
                if (!isBlackListeduser) {
                    break;
                }
                array = this.getComponentErrorJSON(array, component, valueFromRequest);
            }
            if (isBlackListeduser) {
                return array;
            }
            return null;
        }
    }
}
