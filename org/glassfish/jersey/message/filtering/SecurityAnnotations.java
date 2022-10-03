package org.glassfish.jersey.message.filtering;

import org.glassfish.jersey.internal.inject.AnnotationLiteral;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.ArrayList;
import javax.annotation.security.RolesAllowed;

public final class SecurityAnnotations
{
    public static RolesAllowed rolesAllowed(final String... roles) {
        final List<String> list = new ArrayList<String>(roles.length);
        for (final String role : roles) {
            if (role != null) {
                list.add(role);
            }
        }
        return (RolesAllowed)new RolesAllowedImpl((String[])list.toArray(new String[list.size()]));
    }
    
    public static PermitAll permitAll() {
        return (PermitAll)new PermitAllImpl();
    }
    
    public static DenyAll denyAll() {
        return (DenyAll)new DenyAllImpl();
    }
    
    private SecurityAnnotations() {
    }
    
    private static final class RolesAllowedImpl extends AnnotationLiteral<RolesAllowed> implements RolesAllowed
    {
        private final String[] roles;
        
        private RolesAllowedImpl(final String[] roles) {
            this.roles = roles;
        }
        
        public String[] value() {
            return this.roles;
        }
    }
    
    private static final class DenyAllImpl extends AnnotationLiteral<DenyAll> implements DenyAll
    {
    }
    
    private static class PermitAllImpl extends AnnotationLiteral<PermitAll> implements PermitAll
    {
    }
}
