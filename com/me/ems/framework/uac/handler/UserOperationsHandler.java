package com.me.ems.framework.uac.handler;

import java.util.ArrayList;
import java.util.List;

public class UserOperationsHandler
{
    private static UserOperationsHandler userOperationsHandler;
    static List<String> userOperationImplClassNames;
    
    private UserOperationsHandler() {
    }
    
    public static synchronized UserOperationsHandler getInstance() {
        if (UserOperationsHandler.userOperationsHandler == null) {
            UserOperationsHandler.userOperationsHandler = new UserOperationsHandler();
        }
        return UserOperationsHandler.userOperationsHandler;
    }
    
    public static void registerUserOperationsImpl(final String implClassName) {
        UserOperationsHandler.userOperationImplClassNames.add(implClassName);
    }
    
    public static void deregisterUserOperationsImpl(final String implClassName) {
        UserOperationsHandler.userOperationImplClassNames.remove(implClassName);
    }
    
    public static List<String> getUserOperationsImplClassNames() {
        return UserOperationsHandler.userOperationImplClassNames;
    }
    
    static {
        UserOperationsHandler.userOperationsHandler = null;
        UserOperationsHandler.userOperationImplClassNames = new ArrayList<String>();
    }
}
