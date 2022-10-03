package com.me.mdm.server.profiles.ios;

import java.util.HashMap;
import com.me.mdm.server.profiles.SingletonRestrictionHandler;

public class IOSSingletonRestrictionHandler extends SingletonRestrictionHandler
{
    public static final String SINGLETON_RESTRICTION_HANDLER = "com.me.mdm.server.profiles.ios.IOSSingletonRestrictSeqCmdResHandler";
    public static final String SINGLETON_REMOVE_RESTRICTION_HANDLER = "com.me.mdm.server.profiles.ios.IOSSingletonRemRestrictSeqCmdResHandler";
    public static final String IOS_SINGLETON_RESTRICTION = "com.mdm.singleton.restriction";
    public static final HashMap<String, Object> SINGLETON_RESTRICTION_HASH;
    public static final HashMap<String, String> DEVICE_RESTRICTION_HASH;
    
    @Override
    protected String getRestrictionTable() {
        return "RestrictionsPolicy";
    }
    
    @Override
    protected String getDeviceRestrictionTable() {
        return "MdIOSRestriction";
    }
    
    @Override
    protected HashMap getRestrictionHash() {
        return IOSSingletonRestrictionHandler.SINGLETON_RESTRICTION_HASH;
    }
    
    @Override
    protected HashMap getDeviceRestrictionHash() {
        return IOSSingletonRestrictionHandler.DEVICE_RESTRICTION_HASH;
    }
    
    @Override
    protected String getSeqHandler() {
        return "com.me.mdm.server.profiles.ios.IOSSingletonRestrictSeqCmdResHandler";
    }
    
    @Override
    protected String getRemoveSeqHandler() {
        return "com.me.mdm.server.profiles.ios.IOSSingletonRemRestrictSeqCmdResHandler";
    }
    
    @Override
    protected HashMap<String, String> getMappedRestrictionHash() {
        return new HashMap<String, String>();
    }
    
    static {
        SINGLETON_RESTRICTION_HASH = new HashMap<String, Object>() {
            {
                ((HashMap<String, Boolean>)this).put("ALLOW_MODIFI_DEVICE_NAME", Boolean.FALSE);
            }
        };
        DEVICE_RESTRICTION_HASH = new HashMap<String, String>() {
            {
                this.put("ALLOW_MODIFI_DEVICE_NAME", "2");
            }
        };
    }
}
