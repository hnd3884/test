package com.zoho.security.appfirewall;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.Matcher;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import java.util.logging.Level;
import com.zoho.security.appfirewall.operator.range.IpRangeMatcher;
import com.zoho.security.appfirewall.operator.range.RangeMatcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class AppFirewallComponent
{
    private static final Logger LOGGER;
    private String componentName;
    private Operator operator;
    private String value;
    private Pattern blackListPattern;
    private long length;
    private String id;
    private RangeMatcher rangeMatcher;
    
    public AppFirewallComponent(final Operator operator, final String componentName, final String value, final long length, final DirectiveConfiguration.Directive directive) {
        this.componentName = componentName;
        this.operator = operator;
        if (operator == Operator.REGEXFIND || operator == Operator.REGEXMATCHES) {
            this.blackListPattern = Pattern.compile(value);
        }
        if (operator == Operator.RANGEMATCHES) {
            switch (directive) {
                case IP: {
                    this.rangeMatcher = IpRangeMatcher.newInstance(value);
                    if (this.rangeMatcher == null) {
                        AppFirewallComponent.LOGGER.log(Level.SEVERE, "Invalid operator value \"{0}\" for the rangematcher. directive name: {1} ", new Object[] { value, directive.getValue() });
                        throw new AppFirewallException("INVALID_APPFIREWALL_CONFIGURATION");
                    }
                    break;
                }
                default: {
                    AppFirewallComponent.LOGGER.log(Level.SEVERE, "Range matcher operator is not supported for \"{0}\" directive", directive.getValue());
                    throw new AppFirewallException("INVALID_APPFIREWALL_CONFIGURATION");
                }
            }
        }
        this.value = value;
        this.length = length;
    }
    
    public AppFirewallComponent(final String component_autoID, final Operator operator, final String componentName, final String value, final long length, final DirectiveConfiguration.Directive directive) {
        this(operator, componentName, value, length, directive);
        this.id = component_autoID;
    }
    
    public boolean isBlackListed(final String valueFromRequest) {
        if (!SecurityFrameworkUtil.isValid(valueFromRequest)) {
            return false;
        }
        if (valueFromRequest.length() > this.length) {
            AppFirewallComponent.LOGGER.log(Level.INFO, " \n\n MAX-LENGTH LIMIT HAS BEEN CROSSED FOR COMPONENT :: {2} , CALCULATED-LENGTH = {0} ... MAX-LENGTH LIMIT = {1}   ", new Object[] { valueFromRequest.length(), this.length, this.componentName });
            return true;
        }
        boolean matchFound = false;
        Matcher blackListMatcher = null;
        switch (this.operator) {
            case STRINGMATCHES: {
                matchFound = valueFromRequest.equalsIgnoreCase(this.value);
                break;
            }
            case STRINGCONTAINS: {
                matchFound = valueFromRequest.contains(this.value);
                break;
            }
            case REGEXMATCHES: {
                blackListMatcher = this.blackListPattern.matcher(valueFromRequest);
                matchFound = blackListMatcher.matches();
                break;
            }
            case REGEXFIND: {
                blackListMatcher = this.blackListPattern.matcher(valueFromRequest);
                matchFound = blackListMatcher.find();
                break;
            }
            case STARTSWITHPREFIX: {
                matchFound = valueFromRequest.startsWith(this.value);
                break;
            }
            case ENDSWITHSUFFIX: {
                matchFound = valueFromRequest.endsWith(this.value);
                break;
            }
            case RANGEMATCHES: {
                matchFound = this.rangeMatcher.matches(valueFromRequest);
                break;
            }
        }
        if (matchFound) {
            AppFirewallComponent.LOGGER.log(Level.FINE, "\n\n BLACKLISTED COMPONENT HAS BEEN DETECTED FOR COMPONENT :: \"{0}\", AS PER THE BLACKLISTED_SIGNATURE \"{1}\" ", new Object[] { this.componentName, (this.value == null) ? this.blackListPattern.toString() : this.value });
            return true;
        }
        return false;
    }
    
    public String getComponentName() {
        return this.componentName;
    }
    
    public JSONObject toJSON() {
        final JSONObject componentJSON = new JSONObject();
        try {
            if (this.id != null) {
                componentJSON.put("id", (Object)this.id);
            }
            componentJSON.put("operator", (Object)this.operator.getOperator().toString());
            componentJSON.put("value", (Object)this.value);
            if (this.length != Long.MAX_VALUE) {
                componentJSON.put("length", this.length);
            }
        }
        catch (final JSONException e) {
            AppFirewallComponent.LOGGER.log(Level.SEVERE, "Exception Occurred while generating ComponentJSON :: Exception :: {0}", e.getMessage());
        }
        return componentJSON;
    }
    
    public String getId() {
        return this.id;
    }
    
    static {
        LOGGER = Logger.getLogger(AppFirewallComponent.class.getName());
    }
}
