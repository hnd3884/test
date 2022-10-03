package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ControlHelper
{
    private ControlHelper() {
    }
    
    @InternalUseOnly
    public static void registerDefaultResponseControls() {
        Control.registerDecodeableControl("1.2.840.113556.1.4.841", new ActiveDirectoryDirSyncControl());
    }
    
    @InternalUseOnly
    public static void registerNonCommercialResponseControls() {
        Control.registerDecodeableControl("1.3.6.1.4.1.42.2.27.8.5.1", new DraftBeheraLDAPPasswordPolicy10ResponseControl());
    }
}
