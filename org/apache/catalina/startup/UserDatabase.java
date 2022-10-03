package org.apache.catalina.startup;

import java.util.Enumeration;

public interface UserDatabase
{
    UserConfig getUserConfig();
    
    void setUserConfig(final UserConfig p0);
    
    String getHome(final String p0);
    
    Enumeration<String> getUsers();
}
