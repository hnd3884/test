package com.me.devicemanagement.framework.server.authentication;

import java.util.ArrayList;

public class ScopeEvent
{
    public ArrayList<Long> loginIds;
    public Long cgId;
    public Long roId;
    public boolean isCGScopeChanged;
    public boolean isROScopeChanged;
    public boolean isUserScopeChanged;
    
    public ScopeEvent() {
        this.loginIds = null;
        this.cgId = null;
        this.roId = null;
    }
}
