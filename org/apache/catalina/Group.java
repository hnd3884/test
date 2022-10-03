package org.apache.catalina;

import java.util.Iterator;
import java.security.Principal;

public interface Group extends Principal
{
    String getDescription();
    
    void setDescription(final String p0);
    
    String getGroupname();
    
    void setGroupname(final String p0);
    
    Iterator<Role> getRoles();
    
    UserDatabase getUserDatabase();
    
    Iterator<User> getUsers();
    
    void addRole(final Role p0);
    
    boolean isInRole(final Role p0);
    
    void removeRole(final Role p0);
    
    void removeRoles();
}
