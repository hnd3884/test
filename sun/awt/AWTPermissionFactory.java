package sun.awt;

import java.security.Permission;
import java.awt.AWTPermission;
import sun.security.util.PermissionFactory;

public class AWTPermissionFactory implements PermissionFactory<AWTPermission>
{
    @Override
    public AWTPermission newPermission(final String s) {
        return new AWTPermission(s);
    }
}
