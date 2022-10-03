package org.tanukisoftware.wrapper.security;

import org.tanukisoftware.wrapper.WrapperManager;
import java.util.StringTokenizer;
import java.security.PermissionCollection;
import java.security.Permission;

public class WrapperEventPermission extends Permission
{
    private static final long serialVersionUID = 8916489326587298168L;
    public static String EVENT_TYPE_SERVICE;
    public static String EVENT_TYPE_CONTROL;
    public static String EVENT_TYPE_CORE;
    public static String EVENT_TYPE_REMOTE_CONTROL;
    private static int MASK_SERVICE;
    private static int MASK_CONTROL;
    private static int MASK_REMOTE_CONTROL;
    private static int MASK_CORE;
    private static int MASK_ALL;
    private int m_eventTypeMask;
    
    public WrapperEventPermission(final String eventTypes) {
        super("*");
        this.m_eventTypeMask = this.buildEventTypeMask(eventTypes);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof WrapperEventPermission)) {
            return false;
        }
        final WrapperEventPermission wsp = (WrapperEventPermission)obj;
        return this.m_eventTypeMask == wsp.m_eventTypeMask && this.getName().equals(wsp.getName());
    }
    
    public String getActions() {
        final StringBuffer sb = new StringBuffer();
        boolean first = true;
        if ((this.m_eventTypeMask & WrapperEventPermission.MASK_SERVICE) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperEventPermission.EVENT_TYPE_SERVICE);
        }
        if ((this.m_eventTypeMask & WrapperEventPermission.MASK_CONTROL) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperEventPermission.EVENT_TYPE_CONTROL);
        }
        if ((this.m_eventTypeMask & WrapperEventPermission.MASK_CORE) != 0x0) {
            if (first) {
                sb.append(',');
            }
            else {
                first = false;
            }
            sb.append(WrapperEventPermission.EVENT_TYPE_CORE);
        }
        return sb.toString();
    }
    
    public boolean implies(final Permission p2) {
        if (!(p2 instanceof WrapperEventPermission)) {
            return false;
        }
        final WrapperEventPermission wsp = (WrapperEventPermission)p2;
        return (this.m_eventTypeMask & wsp.m_eventTypeMask) == wsp.m_eventTypeMask && this.impliesIgnoreEventTypeMask(wsp);
    }
    
    public PermissionCollection newPermissionCollection() {
        return new WECollection();
    }
    
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    int getActionMask() {
        return this.m_eventTypeMask;
    }
    
    boolean impliesIgnoreEventTypeMask(final WrapperEventPermission p2) {
        return this.getName().equals(p2.getName()) || (p2.getName().endsWith("*") && this.getName().startsWith(p2.getName().substring(0, p2.getName().length() - 1)));
    }
    
    private int buildEventTypeMask(final String eventTypes) {
        if (eventTypes == WrapperEventPermission.EVENT_TYPE_SERVICE) {
            return WrapperEventPermission.MASK_SERVICE;
        }
        if (eventTypes == WrapperEventPermission.EVENT_TYPE_CONTROL) {
            return WrapperEventPermission.MASK_CONTROL;
        }
        if (eventTypes == WrapperEventPermission.EVENT_TYPE_CORE) {
            return WrapperEventPermission.MASK_CORE;
        }
        if (eventTypes.equals("*")) {
            return WrapperEventPermission.MASK_ALL;
        }
        int mask = 0;
        final StringTokenizer st = new StringTokenizer(eventTypes, ",");
        while (st.hasMoreTokens()) {
            final String eventType = st.nextToken();
            if (eventType.equals(WrapperEventPermission.EVENT_TYPE_SERVICE)) {
                mask |= WrapperEventPermission.MASK_SERVICE;
            }
            else if (eventType.equals(WrapperEventPermission.EVENT_TYPE_CONTROL)) {
                mask |= WrapperEventPermission.MASK_CONTROL;
            }
            else {
                if (!eventType.equals(WrapperEventPermission.EVENT_TYPE_CORE)) {
                    throw new IllegalArgumentException(WrapperManager.getRes().getString("Invalid permission eventType: \"{0}\"", eventType));
                }
                mask |= WrapperEventPermission.MASK_CORE;
            }
        }
        return mask;
    }
    
    static {
        WrapperEventPermission.EVENT_TYPE_SERVICE = "service";
        WrapperEventPermission.EVENT_TYPE_CONTROL = "control";
        WrapperEventPermission.EVENT_TYPE_CORE = "core";
        WrapperEventPermission.EVENT_TYPE_REMOTE_CONTROL = "remote control";
        WrapperEventPermission.MASK_SERVICE = 1;
        WrapperEventPermission.MASK_CONTROL = 2;
        WrapperEventPermission.MASK_REMOTE_CONTROL = 8;
        WrapperEventPermission.MASK_CORE = 65536;
        WrapperEventPermission.MASK_ALL = (WrapperEventPermission.MASK_SERVICE | WrapperEventPermission.MASK_CONTROL | WrapperEventPermission.MASK_REMOTE_CONTROL | WrapperEventPermission.MASK_CORE);
    }
}
