package java.security.acl;

import java.util.Enumeration;
import java.security.Principal;

public interface Group extends Principal
{
    boolean addMember(final Principal p0);
    
    boolean removeMember(final Principal p0);
    
    boolean isMember(final Principal p0);
    
    Enumeration<? extends Principal> members();
}
