package org.apache.catalina.realm;

import java.io.ObjectStreamException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import java.util.HashSet;
import java.util.List;
import org.apache.catalina.LifecycleException;
import org.apache.tomcat.util.ExceptionUtils;
import javax.naming.Context;
import org.apache.naming.ContextBindings;
import java.security.Principal;
import org.apache.catalina.User;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.catalina.UserDatabase;

public class UserDatabaseRealm extends RealmBase
{
    protected volatile UserDatabase database;
    private final Object databaseLock;
    @Deprecated
    protected static final String name = "UserDatabaseRealm";
    protected String resourceName;
    private boolean localJndiResource;
    
    public UserDatabaseRealm() {
        this.database = null;
        this.databaseLock = new Object();
        this.resourceName = "UserDatabase";
        this.localJndiResource = false;
    }
    
    public String getResourceName() {
        return this.resourceName;
    }
    
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }
    
    public boolean getLocalJndiResource() {
        return this.localJndiResource;
    }
    
    public void setLocalJndiResource(final boolean localJndiResource) {
        this.localJndiResource = localJndiResource;
    }
    
    @Deprecated
    @Override
    protected String getName() {
        return "UserDatabaseRealm";
    }
    
    @Override
    public void backgroundProcess() {
        final UserDatabase database = this.getUserDatabase();
        if (database instanceof MemoryUserDatabase) {
            ((MemoryUserDatabase)database).backgroundProcess();
        }
    }
    
    @Override
    protected String getPassword(final String username) {
        final UserDatabase database = this.getUserDatabase();
        if (database == null) {
            return null;
        }
        final User user = database.findUser(username);
        if (user == null) {
            return null;
        }
        return user.getPassword();
    }
    
    @Override
    protected Principal getPrincipal(final String username) {
        final UserDatabase database = this.getUserDatabase();
        if (database == null) {
            return null;
        }
        final User user = database.findUser(username);
        if (user == null) {
            return null;
        }
        return new UserDatabasePrincipal(user, database);
    }
    
    private UserDatabase getUserDatabase() {
        if (this.database == null) {
            synchronized (this.databaseLock) {
                if (this.database == null) {
                    try {
                        Context context = null;
                        if (this.localJndiResource) {
                            context = ContextBindings.getClassLoader();
                            context = (Context)context.lookup("comp/env");
                        }
                        else {
                            context = this.getServer().getGlobalNamingContext();
                        }
                        this.database = (UserDatabase)context.lookup(this.resourceName);
                    }
                    catch (final Throwable e) {
                        ExceptionUtils.handleThrowable(e);
                        if (this.containerLog != null) {
                            this.containerLog.error((Object)UserDatabaseRealm.sm.getString("userDatabaseRealm.lookup", new Object[] { this.resourceName }), e);
                        }
                        this.database = null;
                    }
                }
            }
        }
        return this.database;
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        if (!this.localJndiResource) {
            final UserDatabase database = this.getUserDatabase();
            if (database == null) {
                throw new LifecycleException(UserDatabaseRealm.sm.getString("userDatabaseRealm.noDatabase", new Object[] { this.resourceName }));
            }
        }
        super.startInternal();
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.database = null;
    }
    
    public static final class UserDatabasePrincipal extends GenericPrincipal
    {
        private static final long serialVersionUID = 1L;
        private final transient User user;
        private final transient UserDatabase database;
        
        public UserDatabasePrincipal(final User user, final UserDatabase database) {
            super(user.getName(), null, null);
            this.user = user;
            this.database = database;
        }
        
        @Override
        public String[] getRoles() {
            final Set<String> roles = new HashSet<String>();
            Iterator<Role> uroles = this.user.getRoles();
            while (uroles.hasNext()) {
                final Role role = uroles.next();
                roles.add(role.getName());
            }
            final Iterator<Group> groups = this.user.getGroups();
            while (groups.hasNext()) {
                final Group group = groups.next();
                uroles = group.getRoles();
                while (uroles.hasNext()) {
                    final Role role2 = uroles.next();
                    roles.add(role2.getName());
                }
            }
            return roles.toArray(new String[0]);
        }
        
        @Override
        public boolean hasRole(final String role) {
            if ("*".equals(role)) {
                return true;
            }
            if (role == null) {
                return false;
            }
            if (this.database == null) {
                return super.hasRole(role);
            }
            final Role dbrole = this.database.findRole(role);
            if (dbrole == null) {
                return false;
            }
            if (this.user.isInRole(dbrole)) {
                return true;
            }
            final Iterator<Group> groups = this.user.getGroups();
            while (groups.hasNext()) {
                final Group group = groups.next();
                if (group.isInRole(dbrole)) {
                    return true;
                }
            }
            return false;
        }
        
        private Object writeReplace() throws ObjectStreamException {
            return new GenericPrincipal(this.getName(), null, Arrays.asList(this.getRoles()));
        }
    }
}
