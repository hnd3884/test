package org.apache.catalina.mbeans;

import org.apache.catalina.User;
import javax.management.ObjectName;
import java.util.Iterator;
import javax.management.MalformedObjectNameException;
import org.apache.catalina.Role;
import java.util.ArrayList;
import org.apache.catalina.Group;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.BaseModelMBean;

public class GroupMBean extends BaseModelMBean
{
    protected final Registry registry;
    protected final ManagedBean managed;
    
    public GroupMBean() {
        this.registry = MBeanUtils.createRegistry();
        this.managed = this.registry.findManagedBean("Group");
    }
    
    public String[] getRoles() {
        final Group group = (Group)this.resource;
        final ArrayList<String> results = new ArrayList<String>();
        final Iterator<Role> roles = group.getRoles();
        while (roles.hasNext()) {
            Role role = null;
            try {
                role = roles.next();
                final ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), role);
                results.add(oname.toString());
            }
            catch (final MalformedObjectNameException e) {
                throw new IllegalArgumentException("Cannot create object name for role " + role, e);
            }
        }
        return results.toArray(new String[0]);
    }
    
    public String[] getUsers() {
        final Group group = (Group)this.resource;
        final ArrayList<String> results = new ArrayList<String>();
        final Iterator<User> users = group.getUsers();
        while (users.hasNext()) {
            User user = null;
            try {
                user = users.next();
                final ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), user);
                results.add(oname.toString());
            }
            catch (final MalformedObjectNameException e) {
                throw new IllegalArgumentException("Cannot create object name for user " + user, e);
            }
        }
        return results.toArray(new String[0]);
    }
    
    public void addRole(final String rolename) {
        final Group group = (Group)this.resource;
        if (group == null) {
            return;
        }
        final Role role = group.getUserDatabase().findRole(rolename);
        if (role == null) {
            throw new IllegalArgumentException("Invalid role name '" + rolename + "'");
        }
        group.addRole(role);
    }
    
    public void removeRole(final String rolename) {
        final Group group = (Group)this.resource;
        if (group == null) {
            return;
        }
        final Role role = group.getUserDatabase().findRole(rolename);
        if (role == null) {
            throw new IllegalArgumentException("Invalid role name [" + rolename + "]");
        }
        group.removeRole(role);
    }
}
