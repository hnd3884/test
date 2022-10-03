package org.tanukisoftware.wrapper.security;

import org.tanukisoftware.wrapper.WrapperManager;
import java.util.StringTokenizer;
import java.security.PermissionCollection;
import java.security.Permission;

public class WrapperServicePermission extends Permission
{
    private static final long serialVersionUID = -6520453688353960444L;
    public static String ACTION_START;
    public static String ACTION_STOP;
    public static String ACTION_PAUSE;
    public static String ACTION_CONTINUE;
    public static String ACTION_INTERROGATE;
    public static String ACTION_USER_CODE;
    private static int MASK_START;
    private static int MASK_STOP;
    private static int MASK_PAUSE;
    private static int MASK_CONTINUE;
    private static int MASK_INTERROGATE;
    private static int MASK_USER_CODE;
    private static int MASK_ALL;
    private int m_actionMask;
    
    public WrapperServicePermission(final String serviceName, final String actions) {
        super(serviceName);
        this.m_actionMask = this.buildActionMask(actions);
    }
    
    public WrapperServicePermission(final String serviceName) {
        this(serviceName, "*");
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof WrapperServicePermission)) {
            return false;
        }
        final WrapperServicePermission wsp = (WrapperServicePermission)obj;
        return this.m_actionMask == wsp.m_actionMask && this.getName().equals(wsp.getName());
    }
    
    public String getActions() {
        final StringBuffer sb = new StringBuffer();
        boolean first = true;
        if ((this.m_actionMask & WrapperServicePermission.MASK_START) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperServicePermission.ACTION_START);
        }
        if ((this.m_actionMask & WrapperServicePermission.MASK_STOP) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperServicePermission.ACTION_STOP);
        }
        if ((this.m_actionMask & WrapperServicePermission.MASK_PAUSE) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperServicePermission.ACTION_CONTINUE);
        }
        if ((this.m_actionMask & WrapperServicePermission.MASK_CONTINUE) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperServicePermission.ACTION_CONTINUE);
        }
        if ((this.m_actionMask & WrapperServicePermission.MASK_INTERROGATE) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperServicePermission.ACTION_INTERROGATE);
        }
        if ((this.m_actionMask & WrapperServicePermission.MASK_USER_CODE) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperServicePermission.ACTION_USER_CODE);
        }
        return sb.toString();
    }
    
    public boolean implies(final Permission p2) {
        if (!(p2 instanceof WrapperServicePermission)) {
            return false;
        }
        final WrapperServicePermission wsp = (WrapperServicePermission)p2;
        return (this.m_actionMask & wsp.m_actionMask) == wsp.m_actionMask && this.impliesIgnoreActionMask(wsp);
    }
    
    public PermissionCollection newPermissionCollection() {
        return new WSCollection();
    }
    
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    int getActionMask() {
        return this.m_actionMask;
    }
    
    boolean impliesIgnoreActionMask(final WrapperServicePermission p2) {
        return this.getName().equals(p2.getName()) || (p2.getName().endsWith("*") && this.getName().startsWith(p2.getName().substring(0, p2.getName().length() - 1)));
    }
    
    private int buildActionMask(final String actions) {
        if (actions == WrapperServicePermission.ACTION_START) {
            return WrapperServicePermission.MASK_START;
        }
        if (actions == WrapperServicePermission.ACTION_STOP) {
            return WrapperServicePermission.MASK_STOP;
        }
        if (actions == WrapperServicePermission.ACTION_PAUSE) {
            return WrapperServicePermission.MASK_PAUSE;
        }
        if (actions == WrapperServicePermission.ACTION_CONTINUE) {
            return WrapperServicePermission.MASK_CONTINUE;
        }
        if (actions == WrapperServicePermission.ACTION_INTERROGATE) {
            return WrapperServicePermission.MASK_INTERROGATE;
        }
        if (actions == WrapperServicePermission.ACTION_USER_CODE) {
            return WrapperServicePermission.MASK_USER_CODE;
        }
        if (actions.equals("*")) {
            return WrapperServicePermission.MASK_ALL;
        }
        int mask = 0;
        final StringTokenizer st = new StringTokenizer(actions, ",");
        while (st.hasMoreTokens()) {
            final String action = st.nextToken();
            if (action.equals(WrapperServicePermission.ACTION_START)) {
                mask |= WrapperServicePermission.MASK_START;
            }
            else if (action.equals(WrapperServicePermission.ACTION_STOP)) {
                mask |= WrapperServicePermission.MASK_STOP;
            }
            else if (action.equals(WrapperServicePermission.ACTION_PAUSE)) {
                mask |= WrapperServicePermission.MASK_PAUSE;
            }
            else if (action.equals(WrapperServicePermission.ACTION_CONTINUE)) {
                mask |= WrapperServicePermission.MASK_CONTINUE;
            }
            else if (action.equals(WrapperServicePermission.ACTION_INTERROGATE)) {
                mask |= WrapperServicePermission.MASK_INTERROGATE;
            }
            else {
                if (!action.equals(WrapperServicePermission.ACTION_USER_CODE)) {
                    throw new IllegalArgumentException(WrapperManager.getRes().getString("Invalid permission action: \"{0}\"", action));
                }
                mask |= WrapperServicePermission.MASK_USER_CODE;
            }
        }
        return mask;
    }
    
    static {
        WrapperServicePermission.ACTION_START = "start";
        WrapperServicePermission.ACTION_STOP = "stop";
        WrapperServicePermission.ACTION_PAUSE = "pause";
        WrapperServicePermission.ACTION_CONTINUE = "continue";
        WrapperServicePermission.ACTION_INTERROGATE = "interrogate";
        WrapperServicePermission.ACTION_USER_CODE = "userCode";
        WrapperServicePermission.MASK_START = 1;
        WrapperServicePermission.MASK_STOP = 2;
        WrapperServicePermission.MASK_PAUSE = 4;
        WrapperServicePermission.MASK_CONTINUE = 8;
        WrapperServicePermission.MASK_INTERROGATE = 16;
        WrapperServicePermission.MASK_USER_CODE = 32;
        WrapperServicePermission.MASK_ALL = (WrapperServicePermission.MASK_START | WrapperServicePermission.MASK_STOP | WrapperServicePermission.MASK_PAUSE | WrapperServicePermission.MASK_CONTINUE | WrapperServicePermission.MASK_INTERROGATE | WrapperServicePermission.MASK_USER_CODE);
    }
}
