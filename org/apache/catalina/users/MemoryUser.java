package org.apache.catalina.users;

import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.security.Escape;
import org.apache.catalina.UserDatabase;
import java.util.Iterator;
import org.apache.catalina.Role;
import org.apache.catalina.Group;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemoryUser extends AbstractUser
{
    protected final MemoryUserDatabase database;
    protected final CopyOnWriteArrayList<Group> groups;
    protected final CopyOnWriteArrayList<Role> roles;
    
    MemoryUser(final MemoryUserDatabase database, final String username, final String password, final String fullName) {
        this.groups = new CopyOnWriteArrayList<Group>();
        this.roles = new CopyOnWriteArrayList<Role>();
        this.database = database;
        this.setUsername(username);
        this.setPassword(password);
        this.setFullName(fullName);
    }
    
    @Override
    public Iterator<Group> getGroups() {
        return this.groups.iterator();
    }
    
    @Override
    public Iterator<Role> getRoles() {
        return this.roles.iterator();
    }
    
    @Override
    public UserDatabase getUserDatabase() {
        return this.database;
    }
    
    @Override
    public void addGroup(final Group group) {
        this.groups.addIfAbsent(group);
    }
    
    @Override
    public void addRole(final Role role) {
        this.roles.addIfAbsent(role);
    }
    
    @Override
    public boolean isInGroup(final Group group) {
        return this.groups.contains(group);
    }
    
    @Override
    public boolean isInRole(final Role role) {
        return this.roles.contains(role);
    }
    
    @Override
    public void removeGroup(final Group group) {
        this.groups.remove(group);
    }
    
    @Override
    public void removeGroups() {
        this.groups.clear();
    }
    
    @Override
    public void removeRole(final Role role) {
        this.roles.remove(role);
    }
    
    @Override
    public void removeRoles() {
        this.roles.clear();
    }
    
    public String toXml() {
        final StringBuilder sb = new StringBuilder("<user username=\"");
        sb.append(Escape.xml(this.username));
        sb.append("\" password=\"");
        sb.append(Escape.xml(this.password));
        sb.append("\"");
        if (this.fullName != null) {
            sb.append(" fullName=\"");
            sb.append(Escape.xml(this.fullName));
            sb.append("\"");
        }
        sb.append(" groups=\"");
        StringUtils.join((Iterable)this.groups, ',', (StringUtils.Function)new StringUtils.Function<Group>() {
            public String apply(final Group t) {
                return Escape.xml(t.getGroupname());
            }
        }, sb);
        sb.append("\"");
        sb.append(" roles=\"");
        StringUtils.join((Iterable)this.roles, ',', (StringUtils.Function)new StringUtils.Function<Role>() {
            public String apply(final Role t) {
                return Escape.xml(t.getRolename());
            }
        }, sb);
        sb.append("\"");
        sb.append("/>");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User username=\"");
        sb.append(Escape.xml(this.username));
        sb.append("\"");
        if (this.fullName != null) {
            sb.append(", fullName=\"");
            sb.append(Escape.xml(this.fullName));
            sb.append("\"");
        }
        sb.append(", groups=\"");
        StringUtils.join((Iterable)this.groups, ',', (StringUtils.Function)new StringUtils.Function<Group>() {
            public String apply(final Group t) {
                return Escape.xml(t.getGroupname());
            }
        }, sb);
        sb.append("\"");
        sb.append(", roles=\"");
        StringUtils.join((Iterable)this.roles, ',', (StringUtils.Function)new StringUtils.Function<Role>() {
            public String apply(final Role t) {
                return Escape.xml(t.getRolename());
            }
        }, sb);
        sb.append("\"");
        return sb.toString();
    }
}
