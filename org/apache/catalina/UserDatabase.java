package org.apache.catalina;

import java.util.Iterator;

public interface UserDatabase
{
    Iterator<Group> getGroups();
    
    String getId();
    
    Iterator<Role> getRoles();
    
    Iterator<User> getUsers();
    
    void close() throws Exception;
    
    Group createGroup(final String p0, final String p1);
    
    Role createRole(final String p0, final String p1);
    
    User createUser(final String p0, final String p1, final String p2);
    
    Group findGroup(final String p0);
    
    Role findRole(final String p0);
    
    User findUser(final String p0);
    
    void open() throws Exception;
    
    void removeGroup(final Group p0);
    
    void removeRole(final Role p0);
    
    void removeUser(final User p0);
    
    void save() throws Exception;
}
