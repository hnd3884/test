package com.zoho.authentication;

import java.util.List;
import java.util.Locale;

public interface AuthInterface
{
    Long getAccountID();
    
    String getLoginName();
    
    Long getUserID();
    
    Locale getLocale();
    
    boolean isUserExists(final String p0);
    
    boolean isUserAuthenticated();
    
    List<Long> getAccountIDs(final List<String> p0) throws Exception;
}
