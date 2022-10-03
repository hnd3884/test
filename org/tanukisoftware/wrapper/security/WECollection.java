package org.tanukisoftware.wrapper.security;

import java.util.Enumeration;
import org.tanukisoftware.wrapper.WrapperManager;
import java.security.Permission;
import java.util.Vector;
import java.security.PermissionCollection;

final class WECollection extends PermissionCollection
{
    private static final long serialVersionUID = -5183704982261198435L;
    private Vector m_permissions;
    
    public WECollection() {
        this.m_permissions = new Vector();
    }
    
    public void add(final Permission permission) {
        if (!(permission instanceof WrapperEventPermission)) {
            throw new IllegalArgumentException(WrapperManager.getRes().getString("invalid permission: {0}", permission));
        }
        if (this.isReadOnly()) {
            throw new SecurityException(WrapperManager.getRes().getString("Collection is read-only."));
        }
        this.m_permissions.add(permission);
    }
    
    public boolean implies(final Permission permission) {
        if (!(permission instanceof WrapperEventPermission)) {
            return false;
        }
        final WrapperEventPermission wsp = (WrapperEventPermission)permission;
        int pendingMask;
        final int desiredMask = pendingMask = wsp.getActionMask();
        int foundMask = 0;
        final Enumeration en = this.m_permissions.elements();
        while (en.hasMoreElements()) {
            final WrapperEventPermission p2 = en.nextElement();
            if ((pendingMask & p2.getActionMask()) != 0x0 && wsp.impliesIgnoreEventTypeMask(p2)) {
                foundMask |= (desiredMask & p2.getActionMask());
                if (foundMask == desiredMask) {
                    return true;
                }
                pendingMask = (desiredMask ^ foundMask);
            }
        }
        return false;
    }
    
    public Enumeration elements() {
        return this.m_permissions.elements();
    }
}
