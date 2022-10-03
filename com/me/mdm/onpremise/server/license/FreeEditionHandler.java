package com.me.mdm.onpremise.server.license;

public class FreeEditionHandler extends com.me.devicemanagement.framework.server.license.FreeEditionHandler
{
    private static FreeEditionHandler freeedition;
    
    private FreeEditionHandler() {
    }
    
    public static synchronized FreeEditionHandler getInstance() {
        if (FreeEditionHandler.freeedition == null) {
            FreeEditionHandler.freeedition = new FreeEditionHandler();
        }
        return FreeEditionHandler.freeedition;
    }
    
    static {
        FreeEditionHandler.freeedition = null;
    }
}
