package org.apache.catalina.users;

import org.apache.juli.logging.LogFactory;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URI;
import java.io.IOException;
import org.apache.tomcat.util.digester.ObjectCreationFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.file.ConfigFileLoader;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.catalina.User;
import org.apache.catalina.Role;
import org.apache.catalina.Group;
import java.util.Map;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.UserDatabase;

public class MemoryUserDatabase implements UserDatabase
{
    private static final Log log;
    private static final StringManager sm;
    protected final Map<String, Group> groups;
    protected final String id;
    protected String pathname;
    protected String pathnameOld;
    protected String pathnameNew;
    protected boolean readonly;
    protected final Map<String, Role> roles;
    protected final Map<String, User> users;
    private final ReentrantReadWriteLock dbLock;
    private final Lock readLock;
    private final Lock writeLock;
    private volatile long lastModified;
    private boolean watchSource;
    
    public MemoryUserDatabase() {
        this(null);
    }
    
    public MemoryUserDatabase(final String id) {
        this.groups = new ConcurrentHashMap<String, Group>();
        this.pathname = "conf/tomcat-users.xml";
        this.pathnameOld = this.pathname + ".old";
        this.pathnameNew = this.pathname + ".new";
        this.readonly = true;
        this.roles = new ConcurrentHashMap<String, Role>();
        this.users = new ConcurrentHashMap<String, User>();
        this.dbLock = new ReentrantReadWriteLock();
        this.readLock = this.dbLock.readLock();
        this.writeLock = this.dbLock.writeLock();
        this.lastModified = 0L;
        this.watchSource = true;
        this.id = id;
    }
    
    @Override
    public Iterator<Group> getGroups() {
        this.readLock.lock();
        try {
            return new ArrayList<Group>(this.groups.values()).iterator();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    public String getPathname() {
        return this.pathname;
    }
    
    public void setPathname(final String pathname) {
        this.pathname = pathname;
        this.pathnameOld = pathname + ".old";
        this.pathnameNew = pathname + ".new";
    }
    
    public boolean getReadonly() {
        return this.readonly;
    }
    
    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
    }
    
    public boolean getWatchSource() {
        return this.watchSource;
    }
    
    public void setWatchSource(final boolean watchSource) {
        this.watchSource = watchSource;
    }
    
    @Override
    public Iterator<Role> getRoles() {
        this.readLock.lock();
        try {
            return new ArrayList<Role>(this.roles.values()).iterator();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Iterator<User> getUsers() {
        this.readLock.lock();
        try {
            return new ArrayList<User>(this.users.values()).iterator();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void close() throws Exception {
        this.writeLock.lock();
        try {
            this.save();
            this.users.clear();
            this.groups.clear();
            this.roles.clear();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Group createGroup(final String groupname, final String description) {
        if (groupname == null || groupname.length() == 0) {
            final String msg = MemoryUserDatabase.sm.getString("memoryUserDatabase.nullGroup");
            MemoryUserDatabase.log.warn((Object)msg);
            throw new IllegalArgumentException(msg);
        }
        final MemoryGroup group = new MemoryGroup(this, groupname, description);
        this.readLock.lock();
        try {
            this.groups.put(group.getGroupname(), group);
        }
        finally {
            this.readLock.unlock();
        }
        return group;
    }
    
    @Override
    public Role createRole(final String rolename, final String description) {
        if (rolename == null || rolename.length() == 0) {
            final String msg = MemoryUserDatabase.sm.getString("memoryUserDatabase.nullRole");
            MemoryUserDatabase.log.warn((Object)msg);
            throw new IllegalArgumentException(msg);
        }
        final MemoryRole role = new MemoryRole(this, rolename, description);
        this.readLock.lock();
        try {
            this.roles.put(role.getRolename(), role);
        }
        finally {
            this.readLock.unlock();
        }
        return role;
    }
    
    @Override
    public User createUser(final String username, final String password, final String fullName) {
        if (username == null || username.length() == 0) {
            final String msg = MemoryUserDatabase.sm.getString("memoryUserDatabase.nullUser");
            MemoryUserDatabase.log.warn((Object)msg);
            throw new IllegalArgumentException(msg);
        }
        final MemoryUser user = new MemoryUser(this, username, password, fullName);
        this.readLock.lock();
        try {
            this.users.put(user.getUsername(), user);
        }
        finally {
            this.readLock.unlock();
        }
        return user;
    }
    
    @Override
    public Group findGroup(final String groupname) {
        this.readLock.lock();
        try {
            return this.groups.get(groupname);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Role findRole(final String rolename) {
        this.readLock.lock();
        try {
            return this.roles.get(rolename);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public User findUser(final String username) {
        this.readLock.lock();
        try {
            return this.users.get(username);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void open() throws Exception {
        this.writeLock.lock();
        try {
            this.users.clear();
            this.groups.clear();
            this.roles.clear();
            final String pathName = this.getPathname();
            final URI uri = ConfigFileLoader.getURI(pathName);
            URLConnection uConn = null;
            try {
                final URL url = uri.toURL();
                uConn = url.openConnection();
                final InputStream is = uConn.getInputStream();
                this.lastModified = uConn.getLastModified();
                final Digester digester = new Digester();
                try {
                    digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
                }
                catch (final Exception e) {
                    MemoryUserDatabase.log.warn((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.xmlFeatureEncoding"), (Throwable)e);
                }
                digester.addFactoryCreate("tomcat-users/group", (ObjectCreationFactory)new MemoryGroupCreationFactory(this), true);
                digester.addFactoryCreate("tomcat-users/role", (ObjectCreationFactory)new MemoryRoleCreationFactory(this), true);
                digester.addFactoryCreate("tomcat-users/user", (ObjectCreationFactory)new MemoryUserCreationFactory(this), true);
                digester.parse(is);
            }
            catch (final IOException ioe) {
                MemoryUserDatabase.log.error((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.fileNotFound", new Object[] { pathName }));
            }
            catch (final Exception e2) {
                this.users.clear();
                this.groups.clear();
                this.roles.clear();
                throw e2;
            }
            finally {
                if (uConn != null) {
                    try {
                        uConn.getInputStream().close();
                    }
                    catch (final IOException ioe2) {
                        MemoryUserDatabase.log.warn((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.fileClose", new Object[] { this.pathname }), (Throwable)ioe2);
                    }
                }
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void removeGroup(final Group group) {
        this.readLock.lock();
        try {
            final Iterator<User> users = this.getUsers();
            while (users.hasNext()) {
                final User user = users.next();
                user.removeGroup(group);
            }
            this.groups.remove(group.getGroupname());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void removeRole(final Role role) {
        this.readLock.lock();
        try {
            final Iterator<Group> groups = this.getGroups();
            while (groups.hasNext()) {
                final Group group = groups.next();
                group.removeRole(role);
            }
            final Iterator<User> users = this.getUsers();
            while (users.hasNext()) {
                final User user = users.next();
                user.removeRole(role);
            }
            this.roles.remove(role.getRolename());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void removeUser(final User user) {
        this.readLock.lock();
        try {
            this.users.remove(user.getUsername());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public boolean isWriteable() {
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("catalina.base"), this.pathname);
        }
        final File dir = file.getParentFile();
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }
    
    @Override
    public void save() throws Exception {
        if (this.getReadonly()) {
            MemoryUserDatabase.log.error((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.readOnly"));
            return;
        }
        if (!this.isWriteable()) {
            MemoryUserDatabase.log.warn((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.notPersistable"));
            return;
        }
        File fileNew = new File(this.pathnameNew);
        if (!fileNew.isAbsolute()) {
            fileNew = new File(System.getProperty("catalina.base"), this.pathnameNew);
        }
        this.writeLock.lock();
        try {
            try (final FileOutputStream fos = new FileOutputStream(fileNew);
                 final OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 final PrintWriter writer = new PrintWriter(osw)) {
                writer.println("<?xml version='1.0' encoding='utf-8'?>");
                writer.println("<tomcat-users xmlns=\"http://tomcat.apache.org/xml\"");
                writer.print("              ");
                writer.println("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
                writer.print("              ");
                writer.println("xsi:schemaLocation=\"http://tomcat.apache.org/xml tomcat-users.xsd\"");
                writer.println("              version=\"1.0\">");
                Iterator<?> values = null;
                values = this.getRoles();
                while (values.hasNext()) {
                    writer.print("  ");
                    writer.println(values.next());
                }
                values = this.getGroups();
                while (values.hasNext()) {
                    writer.print("  ");
                    writer.println(values.next());
                }
                values = this.getUsers();
                while (values.hasNext()) {
                    writer.print("  ");
                    writer.println(((MemoryUser)values.next()).toXml());
                }
                writer.println("</tomcat-users>");
                if (writer.checkError()) {
                    throw new IOException(MemoryUserDatabase.sm.getString("memoryUserDatabase.writeException", new Object[] { fileNew.getAbsolutePath() }));
                }
            }
            catch (final IOException e) {
                if (fileNew.exists() && !fileNew.delete()) {
                    MemoryUserDatabase.log.warn((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.fileDelete", new Object[] { fileNew }));
                }
                throw e;
            }
            this.lastModified = fileNew.lastModified();
        }
        finally {
            this.writeLock.unlock();
        }
        File fileOld = new File(this.pathnameOld);
        if (!fileOld.isAbsolute()) {
            fileOld = new File(System.getProperty("catalina.base"), this.pathnameOld);
        }
        if (fileOld.exists() && !fileOld.delete()) {
            throw new IOException(MemoryUserDatabase.sm.getString("memoryUserDatabase.fileDelete", new Object[] { fileOld }));
        }
        File fileOrig = new File(this.pathname);
        if (!fileOrig.isAbsolute()) {
            fileOrig = new File(System.getProperty("catalina.base"), this.pathname);
        }
        if (fileOrig.exists() && !fileOrig.renameTo(fileOld)) {
            throw new IOException(MemoryUserDatabase.sm.getString("memoryUserDatabase.renameOld", new Object[] { fileOld.getAbsolutePath() }));
        }
        if (!fileNew.renameTo(fileOrig)) {
            if (fileOld.exists() && !fileOld.renameTo(fileOrig)) {
                MemoryUserDatabase.log.warn((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.restoreOrig", new Object[] { fileOld }));
            }
            throw new IOException(MemoryUserDatabase.sm.getString("memoryUserDatabase.renameNew", new Object[] { fileOrig.getAbsolutePath() }));
        }
        if (fileOld.exists() && !fileOld.delete()) {
            throw new IOException(MemoryUserDatabase.sm.getString("memoryUserDatabase.fileDelete", new Object[] { fileOld }));
        }
    }
    
    public void backgroundProcess() {
        if (!this.watchSource) {
            return;
        }
        final URI uri = ConfigFileLoader.getURI(this.getPathname());
        URLConnection uConn = null;
        try {
            final URL url = uri.toURL();
            uConn = url.openConnection();
            if (this.lastModified != uConn.getLastModified()) {
                this.writeLock.lock();
                try {
                    final long detectedLastModified = uConn.getLastModified();
                    if (this.lastModified != detectedLastModified && detectedLastModified + 2000L < System.currentTimeMillis()) {
                        MemoryUserDatabase.log.info((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.reload", new Object[] { this.id, uri }));
                        this.open();
                    }
                }
                finally {
                    this.writeLock.unlock();
                }
            }
        }
        catch (final Exception ioe) {
            MemoryUserDatabase.log.error((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.reloadError", new Object[] { this.id, uri }), (Throwable)ioe);
            if (uConn != null) {
                try {
                    uConn.getInputStream().close();
                }
                catch (final FileNotFoundException fnfe) {
                    this.lastModified = 0L;
                }
                catch (final IOException ioe2) {
                    MemoryUserDatabase.log.warn((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.fileClose", new Object[] { this.pathname }), (Throwable)ioe2);
                }
            }
        }
        finally {
            if (uConn != null) {
                try {
                    uConn.getInputStream().close();
                }
                catch (final FileNotFoundException fnfe2) {
                    this.lastModified = 0L;
                }
                catch (final IOException ioe3) {
                    MemoryUserDatabase.log.warn((Object)MemoryUserDatabase.sm.getString("memoryUserDatabase.fileClose", new Object[] { this.pathname }), (Throwable)ioe3);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MemoryUserDatabase[id=");
        sb.append(this.id);
        sb.append(",pathname=");
        sb.append(this.pathname);
        sb.append(",groupCount=");
        sb.append(this.groups.size());
        sb.append(",roleCount=");
        sb.append(this.roles.size());
        sb.append(",userCount=");
        sb.append(this.users.size());
        sb.append(']');
        return sb.toString();
    }
    
    static {
        log = LogFactory.getLog((Class)MemoryUserDatabase.class);
        sm = StringManager.getManager((Class)MemoryUserDatabase.class);
    }
}
