package sun.security.acl;

import java.security.acl.Group;
import java.util.Enumeration;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Vector;
import java.security.acl.AclEntry;
import java.security.Principal;
import java.util.Hashtable;
import java.security.acl.Acl;

public class AclImpl extends OwnerImpl implements Acl
{
    private Hashtable<Principal, AclEntry> allowedUsersTable;
    private Hashtable<Principal, AclEntry> allowedGroupsTable;
    private Hashtable<Principal, AclEntry> deniedUsersTable;
    private Hashtable<Principal, AclEntry> deniedGroupsTable;
    private String aclName;
    private Vector<Permission> zeroSet;
    
    public AclImpl(final Principal principal, final String s) {
        super(principal);
        this.allowedUsersTable = new Hashtable<Principal, AclEntry>(23);
        this.allowedGroupsTable = new Hashtable<Principal, AclEntry>(23);
        this.deniedUsersTable = new Hashtable<Principal, AclEntry>(23);
        this.deniedGroupsTable = new Hashtable<Principal, AclEntry>(23);
        this.aclName = null;
        this.zeroSet = new Vector<Permission>(1, 1);
        try {
            this.setName(principal, s);
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public void setName(final Principal principal, final String aclName) throws NotOwnerException {
        if (!this.isOwner(principal)) {
            throw new NotOwnerException();
        }
        this.aclName = aclName;
    }
    
    @Override
    public String getName() {
        return this.aclName;
    }
    
    @Override
    public synchronized boolean addEntry(final Principal principal, final AclEntry aclEntry) throws NotOwnerException {
        if (!this.isOwner(principal)) {
            throw new NotOwnerException();
        }
        final Hashtable<Principal, AclEntry> table = this.findTable(aclEntry);
        final Principal principal2 = aclEntry.getPrincipal();
        if (table.get(principal2) != null) {
            return false;
        }
        table.put(principal2, aclEntry);
        return true;
    }
    
    @Override
    public synchronized boolean removeEntry(final Principal principal, final AclEntry aclEntry) throws NotOwnerException {
        if (!this.isOwner(principal)) {
            throw new NotOwnerException();
        }
        return this.findTable(aclEntry).remove(aclEntry.getPrincipal()) != null;
    }
    
    @Override
    public synchronized Enumeration<Permission> getPermissions(final Principal principal) {
        return this.subtract(union(this.subtract(this.getIndividualPositive(principal), this.getIndividualNegative(principal)), this.subtract(this.subtract(this.getGroupPositive(principal), this.getGroupNegative(principal)), this.subtract(this.getIndividualNegative(principal), this.getIndividualPositive(principal)))), union(this.subtract(this.getIndividualNegative(principal), this.getIndividualPositive(principal)), this.subtract(this.subtract(this.getGroupNegative(principal), this.getGroupPositive(principal)), this.subtract(this.getIndividualPositive(principal), this.getIndividualNegative(principal)))));
    }
    
    @Override
    public boolean checkPermission(final Principal principal, final Permission permission) {
        final Enumeration<Permission> permissions = this.getPermissions(principal);
        while (permissions.hasMoreElements()) {
            if (permissions.nextElement().equals(permission)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public synchronized Enumeration<AclEntry> entries() {
        return new AclEnumerator(this, this.allowedUsersTable, this.allowedGroupsTable, this.deniedUsersTable, this.deniedGroupsTable);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final Enumeration<AclEntry> entries = this.entries();
        while (entries.hasMoreElements()) {
            sb.append(entries.nextElement().toString().trim());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private Hashtable<Principal, AclEntry> findTable(final AclEntry aclEntry) {
        Hashtable<Principal, AclEntry> hashtable;
        if (aclEntry.getPrincipal() instanceof Group) {
            if (aclEntry.isNegative()) {
                hashtable = this.deniedGroupsTable;
            }
            else {
                hashtable = this.allowedGroupsTable;
            }
        }
        else if (aclEntry.isNegative()) {
            hashtable = this.deniedUsersTable;
        }
        else {
            hashtable = this.allowedUsersTable;
        }
        return hashtable;
    }
    
    private static Enumeration<Permission> union(final Enumeration<Permission> enumeration, final Enumeration<Permission> enumeration2) {
        final Vector vector = new Vector(20, 20);
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
        while (enumeration2.hasMoreElements()) {
            final Permission permission = enumeration2.nextElement();
            if (!vector.contains(permission)) {
                vector.addElement(permission);
            }
        }
        return vector.elements();
    }
    
    private Enumeration<Permission> subtract(final Enumeration<Permission> enumeration, final Enumeration<Permission> enumeration2) {
        final Vector vector = new Vector(20, 20);
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
        while (enumeration2.hasMoreElements()) {
            final Permission permission = enumeration2.nextElement();
            if (vector.contains(permission)) {
                vector.removeElement(permission);
            }
        }
        return vector.elements();
    }
    
    private Enumeration<Permission> getGroupPositive(final Principal principal) {
        Enumeration<Permission> enumeration = this.zeroSet.elements();
        final Enumeration<Principal> keys = this.allowedGroupsTable.keys();
        while (keys.hasMoreElements()) {
            final Group group = keys.nextElement();
            if (group.isMember(principal)) {
                enumeration = union(this.allowedGroupsTable.get(group).permissions(), enumeration);
            }
        }
        return enumeration;
    }
    
    private Enumeration<Permission> getGroupNegative(final Principal principal) {
        Enumeration<Permission> enumeration = this.zeroSet.elements();
        final Enumeration<Principal> keys = this.deniedGroupsTable.keys();
        while (keys.hasMoreElements()) {
            final Group group = keys.nextElement();
            if (group.isMember(principal)) {
                enumeration = union(this.deniedGroupsTable.get(group).permissions(), enumeration);
            }
        }
        return enumeration;
    }
    
    private Enumeration<Permission> getIndividualPositive(final Principal principal) {
        Enumeration<Permission> enumeration = this.zeroSet.elements();
        final AclEntry aclEntry = this.allowedUsersTable.get(principal);
        if (aclEntry != null) {
            enumeration = aclEntry.permissions();
        }
        return enumeration;
    }
    
    private Enumeration<Permission> getIndividualNegative(final Principal principal) {
        Enumeration<Permission> enumeration = this.zeroSet.elements();
        final AclEntry aclEntry = this.deniedUsersTable.get(principal);
        if (aclEntry != null) {
            enumeration = aclEntry.permissions();
        }
        return enumeration;
    }
}
