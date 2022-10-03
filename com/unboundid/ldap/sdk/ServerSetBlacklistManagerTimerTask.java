package com.unboundid.ldap.sdk;

import java.util.TimerTask;

final class ServerSetBlacklistManagerTimerTask extends TimerTask
{
    private final ServerSetBlacklistManager blacklistManager;
    
    ServerSetBlacklistManagerTimerTask(final ServerSetBlacklistManager blacklistManager) {
        this.blacklistManager = blacklistManager;
    }
    
    @Override
    public void run() {
        this.blacklistManager.checkBlacklistedServers();
    }
}
