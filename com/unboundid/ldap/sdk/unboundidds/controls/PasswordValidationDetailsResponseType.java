package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum PasswordValidationDetailsResponseType
{
    VALIDATION_DETAILS((byte)(-96)), 
    NO_PASSWORD_PROVIDED((byte)(-127)), 
    MULTIPLE_PASSWORDS_PROVIDED((byte)(-126)), 
    NO_VALIDATION_ATTEMPTED((byte)(-125));
    
    private final byte berType;
    
    private PasswordValidationDetailsResponseType(final byte berType) {
        this.berType = berType;
    }
    
    public byte getBERType() {
        return this.berType;
    }
    
    public static PasswordValidationDetailsResponseType forBERType(final byte berType) {
        for (final PasswordValidationDetailsResponseType t : values()) {
            if (t.berType == berType) {
                return t;
            }
        }
        return null;
    }
    
    public static PasswordValidationDetailsResponseType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "validationdetails":
            case "validation-details":
            case "validation_details": {
                return PasswordValidationDetailsResponseType.VALIDATION_DETAILS;
            }
            case "nopasswordprovided":
            case "no-password-provided":
            case "no_password_provided": {
                return PasswordValidationDetailsResponseType.NO_PASSWORD_PROVIDED;
            }
            case "multiplepasswordsprovided":
            case "multiple-passwords-provided":
            case "multiple_passwords_provided": {
                return PasswordValidationDetailsResponseType.MULTIPLE_PASSWORDS_PROVIDED;
            }
            case "novalidationattempted":
            case "no-validation-attempted":
            case "no_validation_attempted": {
                return PasswordValidationDetailsResponseType.NO_VALIDATION_ATTEMPTED;
            }
            default: {
                return null;
            }
        }
    }
}
