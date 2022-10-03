package com.me.mdm.server.inv.actions;

public class CommandExpiryHandler
{
    public static BaseCommandExpiryTimeHandler getHandler(final int platformType) {
        BaseCommandExpiryTimeHandler handler = null;
        switch (platformType) {
            case 4: {
                handler = new ChromeCommandExpiryTimeHandler();
                break;
            }
            default: {
                handler = new BaseCommandExpiryTimeHandler();
                break;
            }
        }
        return handler;
    }
}
