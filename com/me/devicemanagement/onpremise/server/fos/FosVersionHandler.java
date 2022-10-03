package com.me.devicemanagement.onpremise.server.fos;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.mfw.VersionHandler;

public class FosVersionHandler implements VersionHandler
{
    public String getCurrentVersion() {
        return SyMUtil.getProductProperty("productversion");
    }
    
    public boolean isCompatible(final String string) {
        return true;
    }
    
    public long getCurrentBuildNumber() {
        return new Long(SyMUtil.getProductProperty("buildnumber"));
    }
}
