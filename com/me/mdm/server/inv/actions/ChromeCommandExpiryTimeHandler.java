package com.me.mdm.server.inv.actions;

public class ChromeCommandExpiryTimeHandler extends BaseCommandExpiryTimeHandler
{
    @Override
    public int getExpiryTime() {
        return 600;
    }
}
