package com.me.mdm.server.profiles.ios;

import java.util.HashMap;
import com.me.mdm.server.profiles.SingletonRestrictionHandler;

public class IOSPasscodeSingletonRestrictionHandler extends SingletonRestrictionHandler
{
    public static final HashMap<String, Object> PASSCODE_RESTRICTION;
    private static final HashMap<String, String> DEVICE_RESTRICTION;
    private static final HashMap<String, String> MAPPED_RESTRICTION;
    
    public IOSPasscodeSingletonRestrictionHandler() {
        this.request_type = "RestrictPasscode";
    }
    
    @Override
    protected String getRestrictionTable() {
        return "PasscodePolicy";
    }
    
    @Override
    protected String getDeviceRestrictionTable() {
        return "MdIOSRestriction";
    }
    
    @Override
    protected HashMap getRestrictionHash() {
        return IOSPasscodeSingletonRestrictionHandler.PASSCODE_RESTRICTION;
    }
    
    @Override
    protected HashMap getDeviceRestrictionHash() {
        return IOSPasscodeSingletonRestrictionHandler.DEVICE_RESTRICTION;
    }
    
    @Override
    protected String getSeqHandler() {
        return null;
    }
    
    @Override
    protected String getRemoveSeqHandler() {
        return null;
    }
    
    @Override
    protected HashMap<String, String> getMappedRestrictionHash() {
        return IOSPasscodeSingletonRestrictionHandler.MAPPED_RESTRICTION;
    }
    
    static {
        PASSCODE_RESTRICTION = new HashMap<String, Object>() {
            {
                ((HashMap<String, Boolean>)this).put("RESTRICT_PASSCODE", Boolean.TRUE);
            }
        };
        DEVICE_RESTRICTION = new HashMap<String, String>() {
            {
                this.put("ALLOW_MODIFI_PASSCODE", "2");
            }
        };
        MAPPED_RESTRICTION = new HashMap<String, String>() {
            {
                this.put("RESTRICT_PASSCODE", "ALLOW_MODIFI_PASSCODE");
            }
        };
    }
}
