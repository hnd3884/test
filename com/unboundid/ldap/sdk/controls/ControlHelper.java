package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ControlHelper
{
    private ControlHelper() {
    }
    
    @InternalUseOnly
    public static void registerDefaultResponseControls() {
        Control.registerDecodeableControl("2.16.840.1.113730.3.4.15", new AuthorizationIdentityResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.4203.1.9.1.3", new ContentSyncDoneControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.4203.1.9.1.2", new ContentSyncStateControl());
        Control.registerDecodeableControl("2.16.840.1.113730.3.4.7", new EntryChangeNotificationControl());
        Control.registerDecodeableControl("1.3.6.1.1.13.2", new PostReadResponseControl());
        Control.registerDecodeableControl("1.3.6.1.1.13.1", new PreReadResponseControl());
        Control.registerDecodeableControl("1.2.840.113556.1.4.474", new ServerSideSortResponseControl());
        Control.registerDecodeableControl("1.2.840.113556.1.4.319", new SimplePagedResultsControl());
        Control.registerDecodeableControl("2.16.840.1.113730.3.4.4", new PasswordExpiredControl());
        Control.registerDecodeableControl("2.16.840.1.113730.3.4.5", new PasswordExpiringControl());
        Control.registerDecodeableControl("2.16.840.1.113730.3.4.10", new VirtualListViewResponseControl());
    }
}
