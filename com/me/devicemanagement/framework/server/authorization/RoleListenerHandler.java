package com.me.devicemanagement.framework.server.authorization;

import java.util.ArrayList;
import java.util.List;

public class RoleListenerHandler
{
    private List roleListenerList;
    private static RoleListenerHandler rolelistenerhandler;
    public static final int ROLE_ADDED = 1000;
    public static final int ROLE_DELETED = 1001;
    public static final int ROLE_UPDATED = 1003;
    
    private RoleListenerHandler() {
        this.roleListenerList = null;
        this.roleListenerList = new ArrayList();
    }
    
    public static synchronized RoleListenerHandler getInstance() {
        if (RoleListenerHandler.rolelistenerhandler == null) {
            RoleListenerHandler.rolelistenerhandler = new RoleListenerHandler();
        }
        return RoleListenerHandler.rolelistenerhandler;
    }
    
    public void addRoleListener(final RoleListener listener) {
        this.roleListenerList.add(listener);
    }
    
    public void removeRoleListener(final RoleListener listener) {
        this.roleListenerList.remove(listener);
    }
    
    public void invokeRoleListeners(final RoleEvent roleEvent, final int operation) throws Exception {
        final int l = this.roleListenerList.size();
        if (operation == 1000) {
            for (int s = 0; s < l; ++s) {
                final RoleListener listener = this.roleListenerList.get(s);
                listener.roleAdded(roleEvent);
            }
        }
        else if (operation == 1001) {
            for (int s = 0; s < l; ++s) {
                final RoleListener listener = this.roleListenerList.get(s);
                listener.roleDeleted(roleEvent);
            }
        }
        else if (operation == 1003) {
            for (int s = 0; s < l; ++s) {
                final RoleListener listener = this.roleListenerList.get(s);
                listener.roleUpdated(roleEvent);
            }
        }
    }
    
    static {
        RoleListenerHandler.rolelistenerhandler = null;
    }
}
