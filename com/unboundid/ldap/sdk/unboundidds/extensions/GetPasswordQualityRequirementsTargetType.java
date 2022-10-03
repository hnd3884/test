package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum GetPasswordQualityRequirementsTargetType
{
    ADD_WITH_DEFAULT_PASSWORD_POLICY((byte)(-128)), 
    ADD_WITH_SPECIFIED_PASSWORD_POLICY((byte)(-127)), 
    SELF_CHANGE_FOR_AUTHORIZATION_IDENTITY((byte)(-126)), 
    SELF_CHANGE_FOR_SPECIFIED_USER((byte)(-125)), 
    ADMINISTRATIVE_RESET_FOR_SPECIFIED_USER((byte)(-124));
    
    private final byte berType;
    
    private GetPasswordQualityRequirementsTargetType(final byte berType) {
        this.berType = berType;
    }
    
    public byte getBERType() {
        return this.berType;
    }
    
    public static GetPasswordQualityRequirementsTargetType forBERType(final byte berType) {
        for (final GetPasswordQualityRequirementsTargetType t : values()) {
            if (t.berType == berType) {
                return t;
            }
        }
        return null;
    }
    
    public static GetPasswordQualityRequirementsTargetType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "addwithdefaultpasswordpolicy":
            case "add-with-default-password-policy":
            case "add_with_default_password_policy": {
                return GetPasswordQualityRequirementsTargetType.ADD_WITH_DEFAULT_PASSWORD_POLICY;
            }
            case "addwithspecifiedpasswordpolicy":
            case "add-with-specified-password-policy":
            case "add_with_specified_password_policy": {
                return GetPasswordQualityRequirementsTargetType.ADD_WITH_SPECIFIED_PASSWORD_POLICY;
            }
            case "selfchangeforauthorizationidentity":
            case "self-change-for-authorization-identity":
            case "self_change_for_authorization_identity": {
                return GetPasswordQualityRequirementsTargetType.SELF_CHANGE_FOR_AUTHORIZATION_IDENTITY;
            }
            case "selfchangeforspecifieduser":
            case "self-change-for-specified-user":
            case "self_change_for_specified_user": {
                return GetPasswordQualityRequirementsTargetType.SELF_CHANGE_FOR_SPECIFIED_USER;
            }
            case "administrativeresetforspecifieduser":
            case "administrative-reset-for-specified-user":
            case "administrative_reset_for_specified_user": {
                return GetPasswordQualityRequirementsTargetType.ADMINISTRATIVE_RESET_FOR_SPECIFIED_USER;
            }
            default: {
                return null;
            }
        }
    }
}
