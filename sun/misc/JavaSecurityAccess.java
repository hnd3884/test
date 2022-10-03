package sun.misc;

import java.security.AccessControlContext;
import java.security.PrivilegedAction;

public interface JavaSecurityAccess
{
     <T> T doIntersectionPrivilege(final PrivilegedAction<T> p0, final AccessControlContext p1, final AccessControlContext p2);
    
     <T> T doIntersectionPrivilege(final PrivilegedAction<T> p0, final AccessControlContext p1);
}
