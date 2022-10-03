package javax.management.remote;

import java.security.BasicPermission;

public final class SubjectDelegationPermission extends BasicPermission
{
    private static final long serialVersionUID = 1481618113008682343L;
    
    public SubjectDelegationPermission(final String s) {
        super(s);
    }
    
    public SubjectDelegationPermission(final String s, final String s2) {
        super(s, s2);
        if (s2 != null) {
            throw new IllegalArgumentException("Non-null actions");
        }
    }
}
