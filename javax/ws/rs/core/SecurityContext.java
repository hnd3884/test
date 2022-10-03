package javax.ws.rs.core;

import java.security.Principal;

public interface SecurityContext
{
    public static final String BASIC_AUTH = "BASIC";
    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";
    public static final String DIGEST_AUTH = "DIGEST";
    public static final String FORM_AUTH = "FORM";
    
    Principal getUserPrincipal();
    
    boolean isUserInRole(final String p0);
    
    boolean isSecure();
    
    String getAuthenticationScheme();
}
