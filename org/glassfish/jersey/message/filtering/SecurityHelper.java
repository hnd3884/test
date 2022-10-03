package org.glassfish.jersey.message.filtering;

import javax.annotation.security.DenyAll;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;
import javax.annotation.security.PermitAll;
import java.util.HashSet;
import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import javax.ws.rs.core.SecurityContext;
import java.lang.annotation.Annotation;
import java.util.Set;

final class SecurityHelper
{
    private static final Set<String> roles;
    
    static Set<String> getFilteringScopes(final Annotation[] annotations) {
        return getFilteringScopes(null, annotations);
    }
    
    static Set<String> getFilteringScopes(final SecurityContext securityContext, final Annotation[] annotations) {
        if (annotations.length == 0) {
            return Collections.emptySet();
        }
        for (final Annotation annotation : annotations) {
            if (annotation instanceof RolesAllowed) {
                final Set<String> bindings = new HashSet<String>();
                for (final String role : ((RolesAllowed)annotation).value()) {
                    if (securityContext == null || securityContext.isUserInRole(role)) {
                        bindings.add(getRolesAllowedScope(role));
                    }
                }
                return bindings;
            }
            if (annotation instanceof PermitAll) {
                return FilteringHelper.getDefaultFilteringScope();
            }
            if (annotation instanceof DenyAll) {
                return null;
            }
        }
        return Collections.emptySet();
    }
    
    static String getRolesAllowedScope(final String role) {
        SecurityHelper.roles.add(role);
        return RolesAllowed.class.getName() + "_" + role;
    }
    
    static Set<String> getProcessedRoles() {
        return SecurityHelper.roles;
    }
    
    private SecurityHelper() {
    }
    
    static {
        roles = new HashSet<String>();
    }
}
