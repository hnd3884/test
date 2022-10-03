package com.zoho.authentication;

import com.adventnet.persistence.PersistenceInitializer;
import java.util.Locale;
import java.util.List;

public class AuthenticationUtil
{
    private static AuthInterface authObject;
    
    public static Long getAccountID() {
        checkNull();
        return AuthenticationUtil.authObject.getAccountID();
    }
    
    public static String getLoginName() {
        checkNull();
        return AuthenticationUtil.authObject.getLoginName();
    }
    
    public static Long getUserID() {
        checkNull();
        return AuthenticationUtil.authObject.getUserID();
    }
    
    public static boolean isUserExists(final String role) {
        checkNull();
        return AuthenticationUtil.authObject.isUserExists(role);
    }
    
    public static List<Long> getAccountIDs(final List<String> roles) throws Exception {
        checkNull();
        return AuthenticationUtil.authObject.getAccountIDs(roles);
    }
    
    public static Locale getLocale() {
        checkNull();
        return AuthenticationUtil.authObject.getLocale();
    }
    
    public static boolean isUserAuthenticated() {
        checkNull();
        return AuthenticationUtil.authObject.isUserAuthenticated();
    }
    
    private static void checkNull() {
        if (AuthenticationUtil.authObject == null) {
            throw new RuntimeException("The configuration property 'auth.interface' must be set before using AuthenticationUtil and its APIs. Refer the Wiki to know more.");
        }
    }
    
    static {
        final String authClassName = PersistenceInitializer.getConfigurationValue("auth.interface");
        if (authClassName == null || authClassName.isEmpty()) {
            throw new RuntimeException("The configuration property 'auth.interface' must be set before using AuthenticationUtil and its APIs. Refer the Wiki to know more.");
        }
        try {
            AuthenticationUtil.authObject = (AuthInterface)Class.forName(authClassName).newInstance();
        }
        catch (final Exception e) {
            final RuntimeException ex = new RuntimeException("couldn't initialize AuthInterface class - > " + authClassName);
        }
    }
}
