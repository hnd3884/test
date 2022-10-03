package com.zoho.security.appfirewall;

import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class AppFirewallDirectiveFactory
{
    private static final Logger LOGGER;
    
    static AppFirewallDirective createAppFirewallDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) throws AppFirewallException {
        switch (directive) {
            case URL: {
                return new UrlDirective(configuredDirectives.get(0), directive);
            }
            case METHOD: {
                return new MethodDirective(configuredDirectives.get(0), directive);
            }
            case HEADERS: {
                return new HeadersDirective(configuredDirectives, directive);
            }
            case PARAMS: {
                return new ParametersDirective(configuredDirectives, directive);
            }
            case INPUTSTREAM: {
                return new InputStreamDirective(configuredDirectives.get(0), directive);
            }
            case FILES: {
                return new FilesDirective(configuredDirectives, directive);
            }
            case IP: {
                return new IpDirective(configuredDirectives.get(0), directive);
            }
            case SERVER: {
                return new ServerDirective(configuredDirectives.get(0), directive);
            }
            case USERS: {
                return new UsersDirective(configuredDirectives, directive);
            }
            default: {
                AppFirewallDirectiveFactory.LOGGER.log(Level.SEVERE, "Found a non-registered/invalid directive : <{0}> in the policy rule , register it with schema and AppFirewallDirectoryFactory ", directive.getValue());
                throw new AppFirewallException("INVALID_APPFIREWALL_CONFIGURATION");
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AppFirewallDirectiveFactory.class.getName());
    }
}
