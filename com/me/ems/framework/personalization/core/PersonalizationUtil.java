package com.me.ems.framework.personalization.core;

import com.me.devicemanagement.framework.server.util.SyMUtil;

public class PersonalizationUtil
{
    private static final String SESSION_API_CALLS = "session_api_calls";
    
    public void incrementActiveSessionCalls() {
        SyMUtil.updateSyMParameter("session_api_calls", String.valueOf(this.getActiveSessionCalls() + 1));
    }
    
    public int getActiveSessionCalls() {
        try {
            final String callsStr = SyMUtil.getSyMParameter("session_api_calls");
            return (callsStr == null) ? 0 : Integer.parseInt(callsStr);
        }
        catch (final Exception e) {
            return 0;
        }
    }
}
