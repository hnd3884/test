package org.glassfish.jersey.server.filter;

import java.io.IOException;
import javax.ws.rs.ForbiddenException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.ws.rs.container.ContainerRequestContext;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.lang.annotation.Annotation;
import javax.annotation.security.DenyAll;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.container.DynamicFeature;

public class RolesAllowedDynamicFeature implements DynamicFeature
{
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {
        final AnnotatedMethod am = new AnnotatedMethod(resourceInfo.getResourceMethod());
        if (am.isAnnotationPresent((Class<? extends Annotation>)DenyAll.class)) {
            configuration.register((Object)new RolesAllowedRequestFilter());
            return;
        }
        RolesAllowed ra = am.getAnnotation(RolesAllowed.class);
        if (ra != null) {
            configuration.register((Object)new RolesAllowedRequestFilter(ra.value()));
            return;
        }
        if (am.isAnnotationPresent((Class<? extends Annotation>)PermitAll.class)) {
            return;
        }
        ra = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
        if (ra != null) {
            configuration.register((Object)new RolesAllowedRequestFilter(ra.value()));
        }
    }
    
    @Priority(2000)
    private static class RolesAllowedRequestFilter implements ContainerRequestFilter
    {
        private final boolean denyAll;
        private final String[] rolesAllowed;
        
        RolesAllowedRequestFilter() {
            this.denyAll = true;
            this.rolesAllowed = null;
        }
        
        RolesAllowedRequestFilter(final String[] rolesAllowed) {
            this.denyAll = false;
            this.rolesAllowed = ((rolesAllowed != null) ? rolesAllowed : new String[0]);
        }
        
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            if (!this.denyAll) {
                if (this.rolesAllowed.length > 0 && !isAuthenticated(requestContext)) {
                    throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
                }
                for (final String role : this.rolesAllowed) {
                    if (requestContext.getSecurityContext().isUserInRole(role)) {
                        return;
                    }
                }
            }
            throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
        }
        
        private static boolean isAuthenticated(final ContainerRequestContext requestContext) {
            return requestContext.getSecurityContext().getUserPrincipal() != null;
        }
    }
}
