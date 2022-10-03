package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum PasswordPolicyErrorType
{
    PASSWORD_EXPIRED("password expired", 0), 
    ACCOUNT_LOCKED("account locked", 1), 
    CHANGE_AFTER_RESET("change after reset", 2), 
    PASSWORD_MOD_NOT_ALLOWED("password mod not allowed", 3), 
    MUST_SUPPLY_OLD_PASSWORD("must supply old password", 4), 
    INSUFFICIENT_PASSWORD_QUALITY("insufficient password quality", 5), 
    PASSWORD_TOO_SHORT("password too short", 6), 
    PASSWORD_TOO_YOUNG("password too young", 7), 
    PASSWORD_IN_HISTORY("password in history", 8);
    
    private final int value;
    private final String name;
    
    private PasswordPolicyErrorType(final String name, final int value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int intValue() {
        return this.value;
    }
    
    public static PasswordPolicyErrorType valueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return PasswordPolicyErrorType.PASSWORD_EXPIRED;
            }
            case 1: {
                return PasswordPolicyErrorType.ACCOUNT_LOCKED;
            }
            case 2: {
                return PasswordPolicyErrorType.CHANGE_AFTER_RESET;
            }
            case 3: {
                return PasswordPolicyErrorType.PASSWORD_MOD_NOT_ALLOWED;
            }
            case 4: {
                return PasswordPolicyErrorType.MUST_SUPPLY_OLD_PASSWORD;
            }
            case 5: {
                return PasswordPolicyErrorType.INSUFFICIENT_PASSWORD_QUALITY;
            }
            case 6: {
                return PasswordPolicyErrorType.PASSWORD_TOO_SHORT;
            }
            case 7: {
                return PasswordPolicyErrorType.PASSWORD_TOO_YOUNG;
            }
            case 8: {
                return PasswordPolicyErrorType.PASSWORD_IN_HISTORY;
            }
            default: {
                return null;
            }
        }
    }
    
    public static PasswordPolicyErrorType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "passwordexpired":
            case "password-expired":
            case "password_expired":
            case "password expired": {
                return PasswordPolicyErrorType.PASSWORD_EXPIRED;
            }
            case "accountlocked":
            case "account-locked":
            case "account_locked":
            case "account locked": {
                return PasswordPolicyErrorType.ACCOUNT_LOCKED;
            }
            case "changeafterreset":
            case "change-after-reset":
            case "change_after_reset":
            case "change after reset": {
                return PasswordPolicyErrorType.CHANGE_AFTER_RESET;
            }
            case "passwordmodnotallowed":
            case "password-mod-not-allowed":
            case "password_mod_not_allowed":
            case "password mod not allowed": {
                return PasswordPolicyErrorType.PASSWORD_MOD_NOT_ALLOWED;
            }
            case "mustsupplyoldpassword":
            case "must-supply-old-password":
            case "must_supply_old_password":
            case "must supply old password": {
                return PasswordPolicyErrorType.MUST_SUPPLY_OLD_PASSWORD;
            }
            case "insufficientpasswordquality":
            case "insufficient-password-quality":
            case "insufficient_password_quality":
            case "insufficient password quality": {
                return PasswordPolicyErrorType.INSUFFICIENT_PASSWORD_QUALITY;
            }
            case "passwordtooshort":
            case "password-too-short":
            case "password_too_short":
            case "password too short": {
                return PasswordPolicyErrorType.PASSWORD_TOO_SHORT;
            }
            case "passwordtooyoung":
            case "password-too-young":
            case "password_too_young":
            case "password too young": {
                return PasswordPolicyErrorType.PASSWORD_TOO_YOUNG;
            }
            case "passwordinhistory":
            case "password-in-history":
            case "password_in_history":
            case "password in history": {
                return PasswordPolicyErrorType.PASSWORD_IN_HISTORY;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
