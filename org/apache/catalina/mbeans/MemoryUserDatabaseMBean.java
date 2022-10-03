package org.apache.catalina.mbeans;

import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import org.apache.catalina.User;
import org.apache.catalina.Role;
import java.util.Iterator;
import org.apache.catalina.Group;
import java.util.ArrayList;
import org.apache.catalina.UserDatabase;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.BaseModelMBean;

public class MemoryUserDatabaseMBean extends BaseModelMBean
{
    protected final Registry registry;
    protected final ManagedBean managed;
    protected final ManagedBean managedGroup;
    protected final ManagedBean managedRole;
    protected final ManagedBean managedUser;
    
    public MemoryUserDatabaseMBean() {
        this.registry = MBeanUtils.createRegistry();
        this.managed = this.registry.findManagedBean("MemoryUserDatabase");
        this.managedGroup = this.registry.findManagedBean("Group");
        this.managedRole = this.registry.findManagedBean("Role");
        this.managedUser = this.registry.findManagedBean("User");
    }
    
    public String[] getGroups() {
        final UserDatabase database = (UserDatabase)this.resource;
        final ArrayList<String> results = new ArrayList<String>();
        final Iterator<Group> groups = database.getGroups();
        while (groups.hasNext()) {
            final Group group = groups.next();
            results.add(this.findGroup(group.getGroupname()));
        }
        return results.toArray(new String[0]);
    }
    
    public String[] getRoles() {
        final UserDatabase database = (UserDatabase)this.resource;
        final ArrayList<String> results = new ArrayList<String>();
        final Iterator<Role> roles = database.getRoles();
        while (roles.hasNext()) {
            final Role role = roles.next();
            results.add(this.findRole(role.getRolename()));
        }
        return results.toArray(new String[0]);
    }
    
    public String[] getUsers() {
        final UserDatabase database = (UserDatabase)this.resource;
        final ArrayList<String> results = new ArrayList<String>();
        final Iterator<User> users = database.getUsers();
        while (users.hasNext()) {
            final User user = users.next();
            results.add(this.findUser(user.getUsername()));
        }
        return results.toArray(new String[0]);
    }
    
    public String createGroup(final String groupname, final String description) {
        final UserDatabase database = (UserDatabase)this.resource;
        final Group group = database.createGroup(groupname, description);
        try {
            MBeanUtils.createMBean(group);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception creating group [" + groupname + "] MBean", e);
        }
        return this.findGroup(groupname);
    }
    
    public String createRole(final String rolename, final String description) {
        final UserDatabase database = (UserDatabase)this.resource;
        final Role role = database.createRole(rolename, description);
        try {
            MBeanUtils.createMBean(role);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception creating role [" + rolename + "] MBean", e);
        }
        return this.findRole(rolename);
    }
    
    public String createUser(final String username, final String password, final String fullName) {
        final UserDatabase database = (UserDatabase)this.resource;
        final User user = database.createUser(username, password, fullName);
        try {
            MBeanUtils.createMBean(user);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception creating user [" + username + "] MBean", e);
        }
        return this.findUser(username);
    }
    
    public String findGroup(final String groupname) {
        final UserDatabase database = (UserDatabase)this.resource;
        final Group group = database.findGroup(groupname);
        if (group == null) {
            return null;
        }
        try {
            final ObjectName oname = MBeanUtils.createObjectName(this.managedGroup.getDomain(), group);
            return oname.toString();
        }
        catch (final MalformedObjectNameException e) {
            throw new IllegalArgumentException("Cannot create object name for group [" + groupname + "]", e);
        }
    }
    
    public String findRole(final String rolename) {
        final UserDatabase database = (UserDatabase)this.resource;
        final Role role = database.findRole(rolename);
        if (role == null) {
            return null;
        }
        try {
            final ObjectName oname = MBeanUtils.createObjectName(this.managedRole.getDomain(), role);
            return oname.toString();
        }
        catch (final MalformedObjectNameException e) {
            throw new IllegalArgumentException("Cannot create object name for role [" + rolename + "]", e);
        }
    }
    
    public String findUser(final String username) {
        final UserDatabase database = (UserDatabase)this.resource;
        final User user = database.findUser(username);
        if (user == null) {
            return null;
        }
        try {
            final ObjectName oname = MBeanUtils.createObjectName(this.managedUser.getDomain(), user);
            return oname.toString();
        }
        catch (final MalformedObjectNameException e) {
            throw new IllegalArgumentException("Cannot create object name for user [" + username + "]", e);
        }
    }
    
    public void removeGroup(final String groupname) {
        final UserDatabase database = (UserDatabase)this.resource;
        final Group group = database.findGroup(groupname);
        if (group == null) {
            return;
        }
        try {
            MBeanUtils.destroyMBean(group);
            database.removeGroup(group);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception destroying group [" + groupname + "] MBean", e);
        }
    }
    
    public void removeRole(final String rolename) {
        final UserDatabase database = (UserDatabase)this.resource;
        final Role role = database.findRole(rolename);
        if (role == null) {
            return;
        }
        try {
            MBeanUtils.destroyMBean(role);
            database.removeRole(role);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception destroying role [" + rolename + "] MBean", e);
        }
    }
    
    public void removeUser(final String username) {
        final UserDatabase database = (UserDatabase)this.resource;
        final User user = database.findUser(username);
        if (user == null) {
            return;
        }
        try {
            MBeanUtils.destroyMBean(user);
            database.removeUser(user);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception destroying user [" + username + "] MBean", e);
        }
    }
}
