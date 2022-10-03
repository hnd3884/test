package org.apache.catalina.users;

import org.apache.tomcat.util.buf.StringUtils;
import java.util.List;
import org.apache.catalina.Group;
import java.util.ArrayList;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import java.util.Iterator;
import org.apache.catalina.Role;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemoryGroup extends AbstractGroup
{
    protected final MemoryUserDatabase database;
    protected final CopyOnWriteArrayList<Role> roles;
    
    MemoryGroup(final MemoryUserDatabase database, final String groupname, final String description) {
        this.roles = new CopyOnWriteArrayList<Role>();
        this.database = database;
        this.setGroupname(groupname);
        this.setDescription(description);
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
    public Iterator<User> getUsers() {
        final List<User> results = new ArrayList<User>();
        final Iterator<User> users = this.database.getUsers();
        while (users.hasNext()) {
            final User user = users.next();
            if (user.isInGroup(this)) {
                results.add(user);
            }
        }
        return results.iterator();
    }
    
    @Override
    public void addRole(final Role role) {
        this.roles.addIfAbsent(role);
    }
    
    @Override
    public boolean isInRole(final Role role) {
        return this.roles.contains(role);
    }
    
    @Override
    public void removeRole(final Role role) {
        this.roles.remove(role);
    }
    
    @Override
    public void removeRoles() {
        this.roles.clear();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("<group groupname=\"");
        sb.append(this.groupname);
        sb.append("\"");
        if (this.description != null) {
            sb.append(" description=\"");
            sb.append(this.description);
            sb.append("\"");
        }
        sb.append(" roles=\"");
        StringUtils.join((Iterable)this.roles, ',', (StringUtils.Function)new StringUtils.Function<Role>() {
            public String apply(final Role t) {
                return t.getRolename();
            }
        }, sb);
        sb.append("\"");
        sb.append("/>");
        return sb.toString();
    }
}
