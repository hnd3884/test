package org.glassfish.jersey.server.internal.process;

import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.security.Principal;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

class SecurityContextInjectee implements SecurityContext
{
    private final ContainerRequestContext requestContext;
    
    @Inject
    public SecurityContextInjectee(final ContainerRequestContext requestContext) {
        this.requestContext = requestContext;
    }
    
    public Principal getUserPrincipal() {
        this.checkState();
        return this.requestContext.getSecurityContext().getUserPrincipal();
    }
    
    public boolean isUserInRole(final String role) {
        this.checkState();
        return this.requestContext.getSecurityContext().isUserInRole(role);
    }
    
    public boolean isSecure() {
        this.checkState();
        return this.requestContext.getSecurityContext().isSecure();
    }
    
    public String getAuthenticationScheme() {
        this.checkState();
        return this.requestContext.getSecurityContext().getAuthenticationScheme();
    }
    
    @Override
    public int hashCode() {
        this.checkState();
        return 7 * this.requestContext.getSecurityContext().hashCode();
    }
    
    @Override
    public boolean equals(final Object that) {
        this.checkState();
        return that instanceof SecurityContext && that.equals(this.requestContext.getSecurityContext());
    }
    
    private void checkState() {
        if (this.requestContext == null) {
            throw new IllegalStateException(LocalizationMessages.SECURITY_CONTEXT_WAS_NOT_SET());
        }
    }
}
