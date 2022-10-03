package com.me.mdm.api;

import java.util.List;
import java.util.LinkedList;

public class APIActionsHandler
{
    private static APIActionsHandler apiActionsHandler;
    public static final int ER_ADDED = 1;
    public static final int ER_PRE_REMOVE = 2;
    public static final int RESOURCE_ADDED = 3;
    public static final int APP_ADDED = 4;
    public static final int PROFILE_ADDED = 5;
    public static final int RESOURCE_REMOVED = 6;
    public static final int APP_REMOVED = 7;
    public static final int PROFILE_REMOVED = 8;
    private LinkedList<APIActionsListener> apiActionsListenersList;
    
    private APIActionsHandler() {
        this.apiActionsListenersList = null;
        this.apiActionsListenersList = new LinkedList<APIActionsListener>();
    }
    
    public static synchronized APIActionsHandler getInstance() {
        if (APIActionsHandler.apiActionsHandler == null) {
            APIActionsHandler.apiActionsHandler = new APIActionsHandler();
        }
        return APIActionsHandler.apiActionsHandler;
    }
    
    public void addAPIActionsListener(final APIActionsListener apiActionsListener) {
        this.apiActionsListenersList.add(apiActionsListener);
    }
    
    public String invokeAPIActionResourceListener(final List<Long> resIDs, final List<Long> sourceResIds, final Long serviceID, final int operation) {
        final int l = this.apiActionsListenersList.size();
        if (operation == 3) {
            for (int s = 0; s < l; ++s) {
                final APIActionsListener listener = this.apiActionsListenersList.get(s);
                listener.addResourceServiceMapping(resIDs, sourceResIds, serviceID);
            }
        }
        else if (operation == 6) {
            for (int s = 0; s < l; ++s) {
                final APIActionsListener listener = this.apiActionsListenersList.get(s);
                listener.removeResourceServiceMapping(resIDs, serviceID);
            }
        }
        return null;
    }
    
    public String invokeAPIActionProfileListener(final List<Long> profileIDs, final List<Long> sourceProfileIDs, final Long serviceID, final int operation) {
        final int l = this.apiActionsListenersList.size();
        if (operation == 4 || operation == 5) {
            for (int s = 0; s < l; ++s) {
                final APIActionsListener listener = this.apiActionsListenersList.get(s);
                listener.addProfileServiceMapping(profileIDs, sourceProfileIDs, serviceID);
            }
        }
        else if (operation == 7 || operation == 8) {
            for (int s = 0; s < l; ++s) {
                final APIActionsListener listener = this.apiActionsListenersList.get(s);
                listener.removeProfileServiceMapping(profileIDs, serviceID);
            }
        }
        return null;
    }
    
    public String invokeAPIActionERIDListener(final List<Long> erIDs, final Long serviceID, final int operation) {
        final int l = this.apiActionsListenersList.size();
        if (operation == 1) {
            for (int s = 0; s < l; ++s) {
                final APIActionsListener listener = this.apiActionsListenersList.get(s);
                listener.addERIDServiceMapping(erIDs, serviceID);
            }
        }
        else if (operation == 2) {
            for (int s = 0; s < l; ++s) {
                final APIActionsListener listener = this.apiActionsListenersList.get(s);
                listener.removeERIDServiceMapping(erIDs, serviceID);
            }
        }
        return null;
    }
    
    static {
        APIActionsHandler.apiActionsHandler = null;
    }
}
