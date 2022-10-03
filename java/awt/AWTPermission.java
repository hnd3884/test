package java.awt;

import java.security.BasicPermission;

public final class AWTPermission extends BasicPermission
{
    private static final long serialVersionUID = 8890392402588814465L;
    
    public AWTPermission(final String s) {
        super(s);
    }
    
    public AWTPermission(final String s, final String s2) {
        super(s, s2);
    }
}
