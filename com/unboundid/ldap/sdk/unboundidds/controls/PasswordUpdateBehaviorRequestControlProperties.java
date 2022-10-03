package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PasswordUpdateBehaviorRequestControlProperties implements Serializable
{
    private static final long serialVersionUID = -529840713192839805L;
    private Boolean allowPreEncodedPassword;
    private Boolean ignoreMinimumPasswordAge;
    private Boolean ignorePasswordHistory;
    private Boolean isSelfChange;
    private Boolean mustChangePassword;
    private Boolean skipPasswordValidation;
    private String passwordStorageScheme;
    
    public PasswordUpdateBehaviorRequestControlProperties() {
        this.isSelfChange = null;
        this.allowPreEncodedPassword = null;
        this.skipPasswordValidation = null;
        this.ignorePasswordHistory = null;
        this.ignoreMinimumPasswordAge = null;
        this.passwordStorageScheme = null;
        this.mustChangePassword = null;
    }
    
    public PasswordUpdateBehaviorRequestControlProperties(final PasswordUpdateBehaviorRequestControl control) {
        this.isSelfChange = control.getIsSelfChange();
        this.allowPreEncodedPassword = control.getAllowPreEncodedPassword();
        this.skipPasswordValidation = control.getSkipPasswordValidation();
        this.ignorePasswordHistory = control.getIgnorePasswordHistory();
        this.ignoreMinimumPasswordAge = control.getIgnoreMinimumPasswordAge();
        this.passwordStorageScheme = control.getPasswordStorageScheme();
        this.mustChangePassword = control.getMustChangePassword();
    }
    
    public Boolean getIsSelfChange() {
        return this.isSelfChange;
    }
    
    public void setIsSelfChange(final Boolean isSelfChange) {
        this.isSelfChange = isSelfChange;
    }
    
    public Boolean getAllowPreEncodedPassword() {
        return this.allowPreEncodedPassword;
    }
    
    public void setAllowPreEncodedPassword(final Boolean allowPreEncodedPassword) {
        this.allowPreEncodedPassword = allowPreEncodedPassword;
    }
    
    public Boolean getSkipPasswordValidation() {
        return this.skipPasswordValidation;
    }
    
    public void setSkipPasswordValidation(final Boolean skipPasswordValidation) {
        this.skipPasswordValidation = skipPasswordValidation;
    }
    
    public Boolean getIgnorePasswordHistory() {
        return this.ignorePasswordHistory;
    }
    
    public void setIgnorePasswordHistory(final Boolean ignorePasswordHistory) {
        this.ignorePasswordHistory = ignorePasswordHistory;
    }
    
    public Boolean getIgnoreMinimumPasswordAge() {
        return this.ignoreMinimumPasswordAge;
    }
    
    public void setIgnoreMinimumPasswordAge(final Boolean ignoreMinimumPasswordAge) {
        this.ignoreMinimumPasswordAge = ignoreMinimumPasswordAge;
    }
    
    public String getPasswordStorageScheme() {
        return this.passwordStorageScheme;
    }
    
    public void setPasswordStorageScheme(final String passwordStorageScheme) {
        this.passwordStorageScheme = passwordStorageScheme;
    }
    
    public Boolean getMustChangePassword() {
        return this.mustChangePassword;
    }
    
    public void setMustChangePassword(final Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordUpdateBehaviorRequestControlProperties(");
        boolean appended = appendNameValuePair(buffer, "isSelfChange", this.isSelfChange, false);
        appended = appendNameValuePair(buffer, "allowPreEncodedPassword", this.allowPreEncodedPassword, appended);
        appended = appendNameValuePair(buffer, "skipPasswordValidation", this.skipPasswordValidation, appended);
        appended = appendNameValuePair(buffer, "ignorePasswordHistory", this.ignorePasswordHistory, appended);
        appended = appendNameValuePair(buffer, "ignoreMinimumPasswordAge", this.ignoreMinimumPasswordAge, appended);
        appended = appendNameValuePair(buffer, "passwordStorageScheme", this.passwordStorageScheme, appended);
        appendNameValuePair(buffer, "mustChangePassword", this.mustChangePassword, appended);
        buffer.append(')');
    }
    
    private static boolean appendNameValuePair(final StringBuilder buffer, final String propertyName, final Object propertyValue, final boolean appendedPreviousPair) {
        if (propertyValue == null) {
            return appendedPreviousPair;
        }
        if (appendedPreviousPair) {
            buffer.append(", ");
        }
        buffer.append(propertyName);
        buffer.append('=');
        if (propertyValue instanceof Boolean) {
            buffer.append((boolean)propertyValue);
        }
        else {
            buffer.append('\"');
            buffer.append(propertyValue);
            buffer.append('\"');
        }
        return true;
    }
}
