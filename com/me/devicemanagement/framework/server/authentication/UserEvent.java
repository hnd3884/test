package com.me.devicemanagement.framework.server.authentication;

import java.util.List;
import java.util.HashMap;

public class UserEvent
{
    public Long userID;
    public Long loginID;
    public String userName;
    public boolean isAdminUser;
    public boolean isUserRoleChanged;
    public boolean isUserScopeChanged;
    public boolean isPreviouslyManagingAllComputers;
    public int scope;
    public HashMap userContactInfo;
    public List managedCustomers;
    public List<Long> scopeList;
}
