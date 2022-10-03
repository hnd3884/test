package sun.security.acl;

import java.util.Enumeration;
import java.security.acl.LastOwnerException;
import java.security.acl.NotOwnerException;
import java.security.Principal;
import java.security.acl.Group;
import java.security.acl.Owner;

public class OwnerImpl implements Owner
{
    private Group ownerGroup;
    
    public OwnerImpl(final Principal principal) {
        (this.ownerGroup = new GroupImpl("AclOwners")).addMember(principal);
    }
    
    @Override
    public synchronized boolean addOwner(final Principal principal, final Principal principal2) throws NotOwnerException {
        if (!this.isOwner(principal)) {
            throw new NotOwnerException();
        }
        this.ownerGroup.addMember(principal2);
        return false;
    }
    
    @Override
    public synchronized boolean deleteOwner(final Principal principal, final Principal principal2) throws NotOwnerException, LastOwnerException {
        if (!this.isOwner(principal)) {
            throw new NotOwnerException();
        }
        final Enumeration<? extends Principal> members = this.ownerGroup.members();
        members.nextElement();
        if (members.hasMoreElements()) {
            return this.ownerGroup.removeMember(principal2);
        }
        throw new LastOwnerException();
    }
    
    @Override
    public synchronized boolean isOwner(final Principal principal) {
        return this.ownerGroup.isMember(principal);
    }
}
