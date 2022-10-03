package org.apache.catalina;

import java.util.Iterator;
import java.security.Principal;

public interface User extends Principal
{
    String getFullName();
    
    void setFullName(final String p0);
    
    Iterator<Group> getGroups();
    
    String getPassword();
    
    void setPassword(final String p0);
    
    Iterator<Role> getRoles();
    
    UserDatabase getUserDatabase();
    
    String getUsername();
    
    void setUsername(final String p0);
    
    void addGroup(final Group p0);
    
    void addRole(final Role p0);
    
    boolean isInGroup(final Group p0);
    
    boolean isInRole(final Role p0);
    
    void removeGroup(final Group p0);
    
    void removeGroups();
    
    void removeRole(final Role p0);
    
    void removeRoles();
}
