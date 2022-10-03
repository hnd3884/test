package java.lang.management;

import java.security.BasicPermission;

public final class ManagementPermission extends BasicPermission
{
    private static final long serialVersionUID = 1897496590799378737L;
    
    public ManagementPermission(final String s) {
        super(s);
        if (!s.equals("control") && !s.equals("monitor")) {
            throw new IllegalArgumentException("name: " + s);
        }
    }
    
    public ManagementPermission(final String s, final String s2) throws IllegalArgumentException {
        super(s);
        if (!s.equals("control") && !s.equals("monitor")) {
            throw new IllegalArgumentException("name: " + s);
        }
        if (s2 != null && s2.length() > 0) {
            throw new IllegalArgumentException("actions: " + s2);
        }
    }
}
