package com.me.mdm.server.inv.actions;

public class InvActionUtilProvider
{
    public static InvActionUtil getInvActionUtil(final int platform) {
        switch (platform) {
            case 2: {
                return new AndroidInvActionUtil();
            }
            case 1: {
                return new IOSInvActionUtil();
            }
            case 3: {
                return new WindowsInvActionUtil();
            }
            case 4: {
                return new ChromeInvActionUtil();
            }
            default: {
                return null;
            }
        }
    }
}
