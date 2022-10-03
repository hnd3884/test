package com.unboundid.ldap.sdk.unboundidds.tools;

import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

public enum ManageAccountSubCommandType
{
    GET_ALL("get-all", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_ALL.get(), -1, new String[0]), 
    GET_PASSWORD_POLICY_DN("get-password-policy-dn", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_POLICY_DN.get(), 0, new String[0]), 
    GET_ACCOUNT_IS_USABLE("get-account-is-usable", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_IS_USABLE.get(), 55, new String[0]), 
    GET_ACCOUNT_USABILITY_NOTICES("get-account-usability-notice-messages", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_USABILITY_NOTICES.get(), 52, new String[0]), 
    GET_ACCOUNT_USABILITY_WARNINGS("get-account-usability-warning-messages", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_USABILITY_WARNINGS.get(), 53, new String[0]), 
    GET_ACCOUNT_USABILITY_ERRORS("get-account-usability-error-messages", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_USABILITY_ERRORS.get(), 54, new String[0]), 
    GET_PASSWORD_CHANGED_TIME("get-password-changed-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_PW_CHANGED_TIME.get(), 8, new String[0]), 
    SET_PASSWORD_CHANGED_TIME("set-password-changed-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_PW_CHANGED_TIME.get(), 9, new String[0]), 
    CLEAR_PASSWORD_CHANGED_TIME("clear-password-changed-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_PW_CHANGED_TIME.get(), 10, new String[0]), 
    GET_ACCOUNT_IS_DISABLED("get-account-is-disabled", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_IS_DISABLED.get(), 1, new String[0]), 
    SET_ACCOUNT_IS_DISABLED("set-account-is-disabled", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_IS_DISABLED.get(), 2, new String[0]), 
    CLEAR_ACCOUNT_IS_DISABLED("clear-account-is-disabled", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_IS_DISABLED.get(ManageAccountSubCommandType.SET_ACCOUNT_IS_DISABLED.primaryName, "accountIsDisabled"), 3, new String[0]), 
    GET_ACCOUNT_ACTIVATION_TIME("get-account-activation-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_ACCT_ACT_TIME.get(), 45, new String[0]), 
    SET_ACCOUNT_ACTIVATION_TIME("set-account-activation-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_ACCT_ACT_TIME.get(), 46, new String[0]), 
    CLEAR_ACCOUNT_ACTIVATION_TIME("clear-account-activation-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_ACCT_ACT_TIME.get(), 47, new String[0]), 
    GET_SECONDS_UNTIL_ACCOUNT_ACTIVATION("get-seconds-until-account-activation", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_SECONDS_UNTIL_ACCT_ACT.get(), 48, new String[0]), 
    GET_ACCOUNT_IS_NOT_YET_ACTIVE("get-account-is-not-yet-active", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_ACCT_NOT_YET_ACTIVE.get(), 56, new String[0]), 
    GET_ACCOUNT_EXPIRATION_TIME("get-account-expiration-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_ACCT_EXP_TIME.get(), 4, new String[0]), 
    SET_ACCOUNT_EXPIRATION_TIME("set-account-expiration-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_ACCT_EXP_TIME.get(), 5, new String[0]), 
    CLEAR_ACCOUNT_EXPIRATION_TIME("clear-account-expiration-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_ACCT_EXP_TIME.get(), 6, new String[0]), 
    GET_SECONDS_UNTIL_ACCOUNT_EXPIRATION("get-seconds-until-account-expiration", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_SECONDS_UNTIL_ACCT_EXP.get(), 7, new String[0]), 
    GET_ACCOUNT_IS_EXPIRED("get-account-is-expired", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_ACCT_IS_EXPIRED.get(), 57, new String[0]), 
    GET_PASSWORD_EXPIRATION_WARNED_TIME("get-password-expiration-warned-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_PW_EXP_WARNED_TIME.get(), 11, new String[0]), 
    SET_PASSWORD_EXPIRATION_WARNED_TIME("set-password-expiration-warned-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_PW_EXP_WARNED_TIME.get(), 12, new String[0]), 
    CLEAR_PASSWORD_EXPIRATION_WARNED_TIME("clear-password-expiration-warned-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_PW_EXP_WARNED_TIME.get(), 13, new String[0]), 
    GET_SECONDS_UNTIL_PASSWORD_EXPIRATION_WARNING("get-seconds-until-password-expiration-warning", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_SECONDS_UNTIL_PW_EXP_WARNING.get(), 15, new String[0]), 
    GET_PASSWORD_EXPIRATION_TIME("get-password-expiration-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_PW_EXP_TIME.get(), 58, new String[0]), 
    GET_SECONDS_UNTIL_PASSWORD_EXPIRATION("get-seconds-until-password-expiration", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_SECONDS_UNTIL_PW_EXP.get(), 14, new String[0]), 
    GET_PASSWORD_IS_EXPIRED("get-password-is-expired", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_PW_IS_EXPIRED.get(), 67, new String[0]), 
    GET_ACCOUNT_IS_FAILURE_LOCKED("get-account-is-failure-locked", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_ACCT_FAILURE_LOCKED.get(), 59, new String[0]), 
    SET_ACCOUNT_IS_FAILURE_LOCKED("set-account-is-failure-locked", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_ACCT_FAILURE_LOCKED.get(), 60, new String[0]), 
    GET_FAILURE_LOCKOUT_TIME("get-failure-lockout-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_FAILURE_LOCKED_TIME.get(), 61, new String[] { "get-failure-locked-time" }), 
    GET_SECONDS_UNTIL_AUTHENTICATION_FAILURE_UNLOCK("get-seconds-until-authentication-failure-unlock", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_SECONDS_UNTIL_FAILURE_UNLOCK.get(), 20, new String[0]), 
    GET_AUTHENTICATION_FAILURE_TIMES("get-authentication-failure-times", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_AUTH_FAILURE_TIMES.get(), 16, new String[0]), 
    ADD_AUTHENTICATION_FAILURE_TIME("add-authentication-failure-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_ADD_AUTH_FAILURE_TIME.get(), 17, new String[0]), 
    SET_AUTHENTICATION_FAILURE_TIMES("set-authentication-failure-times", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_AUTH_FAILURE_TIMES.get(), 18, new String[0]), 
    CLEAR_AUTHENTICATION_FAILURE_TIMES("clear-authentication-failure-times", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_AUTH_FAILURE_TIMES.get(), 19, new String[0]), 
    GET_REMAINING_AUTHENTICATION_FAILURE_COUNT("get-remaining-authentication-failure-count", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_REMAINING_FAILURE_COUNT.get(), 21, new String[0]), 
    GET_ACCOUNT_IS_IDLE_LOCKED("get-account-is-idle-locked", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_ACCT_IDLE_LOCKED.get(), 62, new String[0]), 
    GET_SECONDS_UNTIL_IDLE_LOCKOUT("get-seconds-until-idle-lockout", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_SECONDS_UNTIL_IDLE_LOCKOUT.get(), 25, new String[0]), 
    GET_IDLE_LOCKOUT_TIME("get-idle-lockout-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_IDLE_LOCKOUT_TIME.get(), 63, new String[] { "get-idle-locked-time" }), 
    GET_MUST_CHANGE_PASSWORD("get-must-change-password", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_MUST_CHANGE_PW.get(), 26, new String[] { "get-password-is-reset" }), 
    SET_MUST_CHANGE_PASSWORD("set-must-change-password", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_MUST_CHANGE_PW.get(), 27, new String[] { "set-password-is-reset" }), 
    CLEAR_MUST_CHANGE_PASSWORD("clear-must-change-password", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_MUST_CHANGE_PW.get(), 28, new String[] { "clear-password-is-reset" }), 
    GET_ACCOUNT_IS_PASSWORD_RESET_LOCKED("get-account-is-password-reset-locked", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_ACCT_IS_RESET_LOCKED.get(), 64, new String[0]), 
    GET_SECONDS_UNTIL_PASSWORD_RESET_LOCKOUT("get-seconds-until-password-reset-lockout", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_SECONDS_UNTIL_RESET_LOCKOUT.get(), 29, new String[0]), 
    GET_PASSWORD_RESET_LOCKOUT_TIME("get-password-reset-lockout-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_RESET_LOCKOUT_TIME.get(), 65, new String[] { "get-password-reset-locked-time" }), 
    GET_LAST_LOGIN_TIME("get-last-login-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_LAST_LOGIN_TIME.get(), 22, new String[0]), 
    SET_LAST_LOGIN_TIME("set-last-login-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_LAST_LOGIN_TIME.get(), 23, new String[0]), 
    CLEAR_LAST_LOGIN_TIME("clear-last-login-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_LAST_LOGIN_TIME.get(), 24, new String[0]), 
    GET_LAST_LOGIN_IP_ADDRESS("get-last-login-ip-address", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_LAST_LOGIN_IP.get(), 49, new String[0]), 
    SET_LAST_LOGIN_IP_ADDRESS("set-last-login-ip-address", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_LAST_LOGIN_IP.get(), 50, new String[0]), 
    CLEAR_LAST_LOGIN_IP_ADDRESS("clear-last-login-ip-address", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_LAST_LOGIN_IP.get(), 51, new String[0]), 
    GET_GRACE_LOGIN_USE_TIMES("get-grace-login-use-times", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_GRACE_LOGIN_TIMES.get(), 30, new String[0]), 
    ADD_GRACE_LOGIN_USE_TIME("add-grace-login-use-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_ADD_GRACE_LOGIN_TIME.get(), 31, new String[0]), 
    SET_GRACE_LOGIN_USE_TIMES("set-grace-login-use-times", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_GRACE_LOGIN_TIMES.get(), 32, new String[0]), 
    CLEAR_GRACE_LOGIN_USE_TIMES("clear-grace-login-use-times", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_GRACE_LOGIN_TIMES.get(), 33, new String[0]), 
    GET_REMAINING_GRACE_LOGIN_COUNT("get-remaining-grace-login-count", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_REMAINING_GRACE_LOGIN_COUNT.get(), 34, new String[0]), 
    GET_PASSWORD_CHANGED_BY_REQUIRED_TIME("get-password-changed-by-required-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_PW_CHANGED_BY_REQ_TIME.get(), 35, new String[0]), 
    SET_PASSWORD_CHANGED_BY_REQUIRED_TIME("set-password-changed-by-required-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_PW_CHANGED_BY_REQ_TIME.get(), 36, new String[0]), 
    CLEAR_PASSWORD_CHANGED_BY_REQUIRED_TIME("clear-password-changed-by-required-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_PW_CHANGED_BY_REQ_TIME.get(), 37, new String[0]), 
    GET_SECONDS_UNTIL_REQUIRED_PASSWORD_CHANGE_TIME("get-seconds-until-required-password-change-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_SECS_UNTIL_REQ_CHANGE_TIME.get(), 38, new String[] { "get-seconds-until-required-change-time" }), 
    GET_PASSWORD_HISTORY_COUNT("get-password-history-count", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_PW_HISTORY_COUNT.get(), 66, new String[] { "get-password-history" }), 
    CLEAR_PASSWORD_HISTORY("clear-password-history", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_PW_HISTORY.get(), 40, new String[0]), 
    GET_HAS_RETIRED_PASSWORD("get-has-retired-password", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_HAS_RETIRED_PW.get(), 41, new String[0]), 
    GET_PASSWORD_RETIRED_TIME("get-password-retired-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_PW_RETIRED_TIME.get(), 42, new String[0]), 
    GET_RETIRED_PASSWORD_EXPIRATION_TIME("get-retired-password-expiration-time", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_RETIRED_PW_EXP_TIME.get(), 43, new String[0]), 
    CLEAR_RETIRED_PASSWORD("clear-retired-password", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_PURGE_RETIRED_PW.get(), 44, new String[] { "purge-retired-password" }), 
    GET_AVAILABLE_SASL_MECHANISMS("get-available-sasl-mechanisms", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_AVAILABLE_SASL_MECHS.get(), 68, new String[0]), 
    GET_AVAILABLE_OTP_DELIVERY_MECHANISMS("get-available-otp-delivery-mechanisms", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_AVAILABLE_OTP_MECHS.get(), 69, new String[0]), 
    GET_HAS_TOTP_SHARED_SECRET("get-has-totp-shared-secret", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_HAS_TOTP_SHARED_SECRET.get(), 70, new String[0]), 
    ADD_TOTP_SHARED_SECRET("add-totp-shared-secret", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_ADD_TOTP_SHARED_SECRET.get(), 76, new String[0]), 
    REMOVE_TOTP_SHARED_SECRET("remove-totp-shared-secret", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_REMOVE_TOTP_SHARED_SECRET.get(), 77, new String[0]), 
    SET_TOTP_SHARED_SECRETS("set-totp-shared-secrets", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_TOTP_SHARED_SECRETS.get(), 78, new String[0]), 
    CLEAR_TOTP_SHARED_SECRETS("clear-totp-shared-secrets", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_TOTP_SHARED_SECRETS.get(), 79, new String[0]), 
    GET_HAS_REGISTERED_YUBIKEY_PUBLIC_ID("get-has-registered-yubikey-public-id", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_HAS_YUBIKEY_ID.get(), 80, new String[0]), 
    GET_REGISTERED_YUBIKEY_PUBLIC_IDS("get-registered-yubikey-public-ids", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_YUBIKEY_IDS.get(), 71, new String[0]), 
    ADD_REGISTERED_YUBIKEY_PUBLIC_ID("add-registered-yubikey-public-id", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_ADD_YUBIKEY_ID.get(), 72, new String[0]), 
    REMOVE_REGISTERED_YUBIKEY_PUBLIC_ID("remove-registered-yubikey-public-id", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_REMOVE_YUBIKEY_ID.get(), 73, new String[0]), 
    SET_REGISTERED_YUBIKEY_PUBLIC_IDS("set-registered-yubikey-public-ids", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_SET_YUBIKEY_IDS.get(), 74, new String[0]), 
    CLEAR_REGISTERED_YUBIKEY_PUBLIC_IDS("clear-registered-yubikey-public-ids", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_CLEAR_YUBIKEY_IDS.get(), 75, new String[0]), 
    GET_HAS_STATIC_PASSWORD("get-has-static-password", ToolMessages.INFO_MANAGE_ACCT_SC_DESC_GET_HAS_STATIC_PW.get(), 81, new String[0]);
    
    private static HashMap<Integer, ManageAccountSubCommandType> typesByOpType;
    private static HashMap<String, ManageAccountSubCommandType> typesByName;
    private final int operationType;
    private final List<String> allNames;
    private final List<String> alternateNames;
    private final String description;
    private final String primaryName;
    
    private ManageAccountSubCommandType(final String primaryName, final String description, final int operationType, final String[] alternateNames) {
        this.primaryName = primaryName;
        this.description = description;
        this.operationType = operationType;
        this.alternateNames = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])alternateNames));
        final ArrayList<String> allNamesList = new ArrayList<String>(alternateNames.length + 1);
        allNamesList.add(primaryName);
        allNamesList.addAll(this.alternateNames);
        this.allNames = Collections.unmodifiableList((List<? extends String>)allNamesList);
    }
    
    public String getPrimaryName() {
        return this.primaryName;
    }
    
    public List<String> getAlternateNames() {
        return this.alternateNames;
    }
    
    public List<String> getAllNames() {
        return this.allNames;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public int getPasswordPolicyStateOperationType() {
        return this.operationType;
    }
    
    public static ManageAccountSubCommandType forName(final String name) {
        ensureMapsPopulated();
        return ManageAccountSubCommandType.typesByName.get(StaticUtils.toLowerCase(name));
    }
    
    public static ManageAccountSubCommandType forOperationType(final int opType) {
        ensureMapsPopulated();
        return ManageAccountSubCommandType.typesByOpType.get(opType);
    }
    
    private static synchronized void ensureMapsPopulated() {
        if (ManageAccountSubCommandType.typesByName == null) {
            final ManageAccountSubCommandType[] values = values();
            ManageAccountSubCommandType.typesByName = new HashMap<String, ManageAccountSubCommandType>(StaticUtils.computeMapCapacity(2 * values.length));
            ManageAccountSubCommandType.typesByOpType = new HashMap<Integer, ManageAccountSubCommandType>(StaticUtils.computeMapCapacity(values.length));
            for (final ManageAccountSubCommandType t : values) {
                ManageAccountSubCommandType.typesByName.put(StaticUtils.toLowerCase(t.primaryName), t);
                for (final String altName : t.alternateNames) {
                    ManageAccountSubCommandType.typesByName.put(StaticUtils.toLowerCase(altName), t);
                }
                if (t.operationType >= 0) {
                    ManageAccountSubCommandType.typesByOpType.put(t.operationType, t);
                }
            }
        }
    }
    
    static {
        ManageAccountSubCommandType.typesByOpType = null;
        ManageAccountSubCommandType.typesByName = null;
    }
}
