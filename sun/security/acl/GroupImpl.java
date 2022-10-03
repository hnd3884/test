package sun.security.acl;

import java.util.Enumeration;
import java.security.Principal;
import java.util.Vector;
import java.security.acl.Group;

public class GroupImpl implements Group
{
    private Vector<Principal> groupMembers;
    private String group;
    
    public GroupImpl(final String group) {
        this.groupMembers = new Vector<Principal>(50, 100);
        this.group = group;
    }
    
    @Override
    public boolean addMember(final Principal principal) {
        if (this.groupMembers.contains(principal)) {
            return false;
        }
        if (this.group.equals(principal.toString())) {
            throw new IllegalArgumentException();
        }
        this.groupMembers.addElement(principal);
        return true;
    }
    
    @Override
    public boolean removeMember(final Principal principal) {
        return this.groupMembers.removeElement(principal);
    }
    
    @Override
    public Enumeration<? extends Principal> members() {
        return this.groupMembers.elements();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof Group && this.group.equals(o.toString()));
    }
    
    public boolean equals(final Group group) {
        return this.equals((Object)group);
    }
    
    @Override
    public String toString() {
        return this.group;
    }
    
    @Override
    public int hashCode() {
        return this.group.hashCode();
    }
    
    @Override
    public boolean isMember(final Principal principal) {
        return this.groupMembers.contains(principal) || this.isMemberRecurse(principal, new Vector<Group>(10));
    }
    
    @Override
    public String getName() {
        return this.group;
    }
    
    boolean isMemberRecurse(final Principal principal, final Vector<Group> vector) {
        final Enumeration<? extends Principal> members = this.members();
        while (members.hasMoreElements()) {
            boolean b = false;
            final Principal principal2 = (Principal)members.nextElement();
            if (principal2.equals(principal)) {
                return true;
            }
            if (principal2 instanceof GroupImpl) {
                final GroupImpl groupImpl = (GroupImpl)principal2;
                vector.addElement(this);
                if (!vector.contains(groupImpl)) {
                    b = groupImpl.isMemberRecurse(principal, vector);
                }
            }
            else if (principal2 instanceof Group) {
                final Group group = (Group)principal2;
                if (!vector.contains(group)) {
                    b = group.isMember(principal);
                }
            }
            if (b) {
                return b;
            }
        }
        return false;
    }
}
