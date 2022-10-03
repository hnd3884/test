package com.me.devicemanagement.framework.server.authentication;

import java.time.temporal.Temporal;
import java.time.Duration;
import java.util.logging.Level;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;

public class UserListenerHandler
{
    protected List<UserListener> userListenerList;
    private static UserListenerHandler userListenerHandler;
    private static Logger logger;
    
    public UserListenerHandler() {
        this.userListenerList = new ArrayList<UserListener>();
    }
    
    public static synchronized UserListenerHandler getInstance() {
        if (UserListenerHandler.userListenerHandler == null) {
            UserListenerHandler.userListenerHandler = new UserListenerHandler();
        }
        return UserListenerHandler.userListenerHandler;
    }
    
    public void addUserListener(final UserListener listener) {
        this.userListenerList.add(listener);
    }
    
    public void removeUserListener(final UserListener listener) {
        this.userListenerList.remove(listener);
    }
    
    public void invokeUsedAddedListeners(final UserEvent userEvent) {
        for (int linstenerInt = 0; linstenerInt < this.userListenerList.size(); ++linstenerInt) {
            final UserListener listener = this.userListenerList.get(linstenerInt);
            listener.userAdded(userEvent);
        }
    }
    
    public void invokeuserDeletedListeners(final UserEvent userEvent) {
        for (int linstenerInt = 0; linstenerInt < this.userListenerList.size(); ++linstenerInt) {
            final UserListener listener = this.userListenerList.get(linstenerInt);
            listener.userDeleted(userEvent);
        }
    }
    
    public void invokeAdminUserDeletedListeners(final UserEvent userEvent) {
        for (int linstenerInt = 0; linstenerInt < this.userListenerList.size(); ++linstenerInt) {
            final UserListener listener = this.userListenerList.get(linstenerInt);
            listener.adminUserDeleted(userEvent);
        }
    }
    
    public void invokeuserModifiedListeners(final UserEvent userEvent) {
        for (int linstenerInt = 0; linstenerInt < this.userListenerList.size(); ++linstenerInt) {
            final UserListener listener = this.userListenerList.get(linstenerInt);
            listener.userModified(userEvent);
        }
    }
    
    public void invokeUserManagedComputersChangedListeners(final ScopeEvent scopeEvent) {
        for (int i = 0; i < this.userListenerList.size(); ++i) {
            final UserListener listener = this.userListenerList.get(i);
            final LocalDateTime start = LocalDateTime.now();
            listener.userManagedComputerChanged(scopeEvent);
            final LocalDateTime end = LocalDateTime.now();
            UserListenerHandler.logger.log(Level.FINE, "UMListener: took {0} millis", Duration.between(start, end).toMillis());
        }
    }
    
    static {
        UserListenerHandler.userListenerHandler = null;
        UserListenerHandler.logger = Logger.getLogger("UserManagementLogger");
    }
}
