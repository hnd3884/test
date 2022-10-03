package com.adventnet.client.util.web;

public interface AuthInterface
{
    Long getAccountID();
    
    String getLoginName();
    
    Long getUserID();
    
    boolean userExists(final String p0);
}
