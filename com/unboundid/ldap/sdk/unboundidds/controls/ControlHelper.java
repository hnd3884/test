package com.unboundid.ldap.sdk.unboundidds.controls;

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
        Control.registerDecodeableControl("1.3.6.1.4.1.42.2.27.9.5.8", new AccountUsableResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.29", new AssuredReplicationResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.59", new GeneratePasswordResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.6", new GetAuthorizationEntryResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.34", new GetBackendSetIDResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.47", new GetPasswordPolicyStateIssuesResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.15", new GetServerIDResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.26", new GetUserResourceLimitsResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.2", new IntermediateClientResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.4", new InteractiveTransactionSpecificationResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.9", new JoinResultControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.37", new MatchingEntryCountResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.42.2.27.8.5.1", new PasswordPolicyResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.41", new PasswordValidationDetailsResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.21", new SoftDeleteResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.39", new TransactionSettingsResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.7", new UnsolicitedCancelResponseControl());
        Control.registerDecodeableControl("1.3.6.1.4.1.30221.2.5.53", new UniquenessResponseControl());
    }
}
