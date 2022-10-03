package sun.net.ftp;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Date;

public class FtpDirEntry
{
    private final String name;
    private String user;
    private String group;
    private long size;
    private Date created;
    private Date lastModified;
    private Type type;
    private boolean[][] permissions;
    private HashMap<String, String> facts;
    
    private FtpDirEntry() {
        this.user = null;
        this.group = null;
        this.size = -1L;
        this.created = null;
        this.lastModified = null;
        this.type = Type.FILE;
        this.permissions = null;
        this.facts = new HashMap<String, String>();
        this.name = null;
    }
    
    public FtpDirEntry(final String name) {
        this.user = null;
        this.group = null;
        this.size = -1L;
        this.created = null;
        this.lastModified = null;
        this.type = Type.FILE;
        this.permissions = null;
        this.facts = new HashMap<String, String>();
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public FtpDirEntry setUser(final String user) {
        this.user = user;
        return this;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public FtpDirEntry setGroup(final String group) {
        this.group = group;
        return this;
    }
    
    public long getSize() {
        return this.size;
    }
    
    public FtpDirEntry setSize(final long size) {
        this.size = size;
        return this;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public FtpDirEntry setType(final Type type) {
        this.type = type;
        return this;
    }
    
    public Date getLastModified() {
        return this.lastModified;
    }
    
    public FtpDirEntry setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }
    
    public boolean canRead(final Permission permission) {
        return this.permissions != null && this.permissions[permission.value][0];
    }
    
    public boolean canWrite(final Permission permission) {
        return this.permissions != null && this.permissions[permission.value][1];
    }
    
    public boolean canExexcute(final Permission permission) {
        return this.permissions != null && this.permissions[permission.value][2];
    }
    
    public FtpDirEntry setPermissions(final boolean[][] permissions) {
        this.permissions = permissions;
        return this;
    }
    
    public FtpDirEntry addFact(final String s, final String s2) {
        this.facts.put(s.toLowerCase(), s2);
        return this;
    }
    
    public String getFact(final String s) {
        return this.facts.get(s.toLowerCase());
    }
    
    public Date getCreated() {
        return this.created;
    }
    
    public FtpDirEntry setCreated(final Date created) {
        this.created = created;
        return this;
    }
    
    @Override
    public String toString() {
        if (this.lastModified == null) {
            return this.name + " [" + this.type + "] (" + this.user + " / " + this.group + ") " + this.size;
        }
        return this.name + " [" + this.type + "] (" + this.user + " / " + this.group + ") {" + this.size + "} " + DateFormat.getDateInstance().format(this.lastModified);
    }
    
    public enum Type
    {
        FILE, 
        DIR, 
        PDIR, 
        CDIR, 
        LINK;
    }
    
    public enum Permission
    {
        USER(0), 
        GROUP(1), 
        OTHERS(2);
        
        int value;
        
        private Permission(final int value) {
            this.value = value;
        }
    }
}
