package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import java.text.ParseException;
import com.unboundid.util.StaticUtils;
import java.util.Date;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordPolicyStateOperation implements Serializable
{
    public static final int OP_TYPE_GET_PW_POLICY_DN = 0;
    public static final int OP_TYPE_GET_ACCOUNT_DISABLED_STATE = 1;
    public static final int OP_TYPE_SET_ACCOUNT_DISABLED_STATE = 2;
    public static final int OP_TYPE_CLEAR_ACCOUNT_DISABLED_STATE = 3;
    public static final int OP_TYPE_GET_ACCOUNT_EXPIRATION_TIME = 4;
    public static final int OP_TYPE_SET_ACCOUNT_EXPIRATION_TIME = 5;
    public static final int OP_TYPE_CLEAR_ACCOUNT_EXPIRATION_TIME = 6;
    public static final int OP_TYPE_GET_SECONDS_UNTIL_ACCOUNT_EXPIRATION = 7;
    public static final int OP_TYPE_GET_PW_CHANGED_TIME = 8;
    public static final int OP_TYPE_SET_PW_CHANGED_TIME = 9;
    public static final int OP_TYPE_CLEAR_PW_CHANGED_TIME = 10;
    public static final int OP_TYPE_GET_PW_EXPIRATION_WARNED_TIME = 11;
    public static final int OP_TYPE_SET_PW_EXPIRATION_WARNED_TIME = 12;
    public static final int OP_TYPE_CLEAR_PW_EXPIRATION_WARNED_TIME = 13;
    public static final int OP_TYPE_GET_SECONDS_UNTIL_PW_EXPIRATION = 14;
    public static final int OP_TYPE_GET_SECONDS_UNTIL_PW_EXPIRATION_WARNING = 15;
    public static final int OP_TYPE_GET_AUTH_FAILURE_TIMES = 16;
    public static final int OP_TYPE_ADD_AUTH_FAILURE_TIME = 17;
    public static final int OP_TYPE_SET_AUTH_FAILURE_TIMES = 18;
    public static final int OP_TYPE_CLEAR_AUTH_FAILURE_TIMES = 19;
    public static final int OP_TYPE_GET_SECONDS_UNTIL_AUTH_FAILURE_UNLOCK = 20;
    public static final int OP_TYPE_GET_REMAINING_AUTH_FAILURE_COUNT = 21;
    public static final int OP_TYPE_GET_LAST_LOGIN_TIME = 22;
    public static final int OP_TYPE_SET_LAST_LOGIN_TIME = 23;
    public static final int OP_TYPE_CLEAR_LAST_LOGIN_TIME = 24;
    public static final int OP_TYPE_GET_SECONDS_UNTIL_IDLE_LOCKOUT = 25;
    public static final int OP_TYPE_GET_PW_RESET_STATE = 26;
    public static final int OP_TYPE_SET_PW_RESET_STATE = 27;
    public static final int OP_TYPE_CLEAR_PW_RESET_STATE = 28;
    public static final int OP_TYPE_GET_SECONDS_UNTIL_PW_RESET_LOCKOUT = 29;
    public static final int OP_TYPE_GET_GRACE_LOGIN_USE_TIMES = 30;
    public static final int OP_TYPE_ADD_GRACE_LOGIN_USE_TIME = 31;
    public static final int OP_TYPE_SET_GRACE_LOGIN_USE_TIMES = 32;
    public static final int OP_TYPE_CLEAR_GRACE_LOGIN_USE_TIMES = 33;
    public static final int OP_TYPE_GET_REMAINING_GRACE_LOGIN_COUNT = 34;
    public static final int OP_TYPE_GET_PW_CHANGED_BY_REQUIRED_TIME = 35;
    public static final int OP_TYPE_SET_PW_CHANGED_BY_REQUIRED_TIME = 36;
    public static final int OP_TYPE_CLEAR_PW_CHANGED_BY_REQUIRED_TIME = 37;
    public static final int OP_TYPE_GET_SECONDS_UNTIL_REQUIRED_CHANGE_TIME = 38;
    @Deprecated
    public static final int OP_TYPE_GET_PW_HISTORY = 39;
    public static final int OP_TYPE_CLEAR_PW_HISTORY = 40;
    public static final int OP_TYPE_HAS_RETIRED_PASSWORD = 41;
    public static final int OP_TYPE_GET_PASSWORD_RETIRED_TIME = 42;
    public static final int OP_TYPE_GET_RETIRED_PASSWORD_EXPIRATION_TIME = 43;
    public static final int OP_TYPE_PURGE_RETIRED_PASSWORD = 44;
    public static final int OP_TYPE_GET_ACCOUNT_ACTIVATION_TIME = 45;
    public static final int OP_TYPE_SET_ACCOUNT_ACTIVATION_TIME = 46;
    public static final int OP_TYPE_CLEAR_ACCOUNT_ACTIVATION_TIME = 47;
    public static final int OP_TYPE_GET_SECONDS_UNTIL_ACCOUNT_ACTIVATION = 48;
    public static final int OP_TYPE_GET_LAST_LOGIN_IP_ADDRESS = 49;
    public static final int OP_TYPE_SET_LAST_LOGIN_IP_ADDRESS = 50;
    public static final int OP_TYPE_CLEAR_LAST_LOGIN_IP_ADDRESS = 51;
    public static final int OP_TYPE_GET_ACCOUNT_USABILITY_NOTICES = 52;
    public static final int OP_TYPE_GET_ACCOUNT_USABILITY_WARNINGS = 53;
    public static final int OP_TYPE_GET_ACCOUNT_USABILITY_ERRORS = 54;
    public static final int OP_TYPE_GET_ACCOUNT_IS_USABLE = 55;
    public static final int OP_TYPE_GET_ACCOUNT_IS_NOT_YET_ACTIVE = 56;
    public static final int OP_TYPE_GET_ACCOUNT_IS_EXPIRED = 57;
    public static final int OP_TYPE_GET_PW_EXPIRATION_TIME = 58;
    public static final int OP_TYPE_GET_ACCOUNT_IS_FAILURE_LOCKED = 59;
    public static final int OP_TYPE_SET_ACCOUNT_IS_FAILURE_LOCKED = 60;
    public static final int OP_TYPE_GET_FAILURE_LOCKOUT_TIME = 61;
    public static final int OP_TYPE_GET_ACCOUNT_IS_IDLE_LOCKED = 62;
    public static final int OP_TYPE_GET_IDLE_LOCKOUT_TIME = 63;
    public static final int OP_TYPE_GET_ACCOUNT_IS_RESET_LOCKED = 64;
    public static final int OP_TYPE_GET_RESET_LOCKOUT_TIME = 65;
    public static final int OP_TYPE_GET_PW_HISTORY_COUNT = 66;
    public static final int OP_TYPE_GET_PW_IS_EXPIRED = 67;
    public static final int OP_TYPE_GET_AVAILABLE_SASL_MECHANISMS = 68;
    public static final int OP_TYPE_GET_AVAILABLE_OTP_DELIVERY_MECHANISMS = 69;
    public static final int OP_TYPE_HAS_TOTP_SHARED_SECRET = 70;
    public static final int OP_TYPE_GET_REGISTERED_YUBIKEY_PUBLIC_IDS = 71;
    public static final int OP_TYPE_ADD_REGISTERED_YUBIKEY_PUBLIC_ID = 72;
    public static final int OP_TYPE_REMOVE_REGISTERED_YUBIKEY_PUBLIC_ID = 73;
    public static final int OP_TYPE_SET_REGISTERED_YUBIKEY_PUBLIC_IDS = 74;
    public static final int OP_TYPE_CLEAR_REGISTERED_YUBIKEY_PUBLIC_IDS = 75;
    public static final int OP_TYPE_ADD_TOTP_SHARED_SECRET = 76;
    public static final int OP_TYPE_REMOVE_TOTP_SHARED_SECRET = 77;
    public static final int OP_TYPE_SET_TOTP_SHARED_SECRETS = 78;
    public static final int OP_TYPE_CLEAR_TOTP_SHARED_SECRETS = 79;
    public static final int OP_TYPE_HAS_REGISTERED_YUBIKEY_PUBLIC_ID = 80;
    public static final int OP_TYPE_HAS_STATIC_PASSWORD = 81;
    private static final ASN1OctetString[] NO_VALUES;
    private static final long serialVersionUID = -7004621958353828598L;
    private final ASN1OctetString[] values;
    private final int opType;
    
    public PasswordPolicyStateOperation(final int opType) {
        this(opType, PasswordPolicyStateOperation.NO_VALUES);
    }
    
    public PasswordPolicyStateOperation(final int opType, final ASN1OctetString[] values) {
        this.opType = opType;
        if (values == null) {
            this.values = PasswordPolicyStateOperation.NO_VALUES;
        }
        else {
            this.values = values;
        }
    }
    
    public static PasswordPolicyStateOperation createGetPasswordPolicyDNOperation() {
        return new PasswordPolicyStateOperation(0);
    }
    
    public static PasswordPolicyStateOperation createGetAccountDisabledStateOperation() {
        return new PasswordPolicyStateOperation(1);
    }
    
    public static PasswordPolicyStateOperation createSetAccountDisabledStateOperation(final boolean isDisabled) {
        final ASN1OctetString[] values = { new ASN1OctetString(String.valueOf(isDisabled)) };
        return new PasswordPolicyStateOperation(2, values);
    }
    
    public static PasswordPolicyStateOperation createClearAccountDisabledStateOperation() {
        return new PasswordPolicyStateOperation(3);
    }
    
    public static PasswordPolicyStateOperation createGetAccountActivationTimeOperation() {
        return new PasswordPolicyStateOperation(45);
    }
    
    public static PasswordPolicyStateOperation createSetAccountActivationTimeOperation(final Date expirationTime) {
        return new PasswordPolicyStateOperation(46, createValues(expirationTime));
    }
    
    public static PasswordPolicyStateOperation createClearAccountActivationTimeOperation() {
        return new PasswordPolicyStateOperation(47);
    }
    
    public static PasswordPolicyStateOperation createGetSecondsUntilAccountActivationOperation() {
        return new PasswordPolicyStateOperation(48);
    }
    
    public static PasswordPolicyStateOperation createGetAccountExpirationTimeOperation() {
        return new PasswordPolicyStateOperation(4);
    }
    
    public static PasswordPolicyStateOperation createSetAccountExpirationTimeOperation(final Date expirationTime) {
        return new PasswordPolicyStateOperation(5, createValues(expirationTime));
    }
    
    public static PasswordPolicyStateOperation createClearAccountExpirationTimeOperation() {
        return new PasswordPolicyStateOperation(6);
    }
    
    public static PasswordPolicyStateOperation createGetSecondsUntilAccountExpirationOperation() {
        return new PasswordPolicyStateOperation(7);
    }
    
    public static PasswordPolicyStateOperation createGetPasswordChangedTimeOperation() {
        return new PasswordPolicyStateOperation(8);
    }
    
    public static PasswordPolicyStateOperation createSetPasswordChangedTimeOperation(final Date passwordChangedTime) {
        return new PasswordPolicyStateOperation(9, createValues(passwordChangedTime));
    }
    
    public static PasswordPolicyStateOperation createClearPasswordChangedTimeOperation() {
        return new PasswordPolicyStateOperation(10);
    }
    
    public static PasswordPolicyStateOperation createGetPasswordExpirationWarnedTimeOperation() {
        return new PasswordPolicyStateOperation(11);
    }
    
    public static PasswordPolicyStateOperation createSetPasswordExpirationWarnedTimeOperation(final Date passwordExpirationWarnedTime) {
        return new PasswordPolicyStateOperation(12, createValues(passwordExpirationWarnedTime));
    }
    
    public static PasswordPolicyStateOperation createClearPasswordExpirationWarnedTimeOperation() {
        return new PasswordPolicyStateOperation(13);
    }
    
    public static PasswordPolicyStateOperation createGetSecondsUntilPasswordExpirationOperation() {
        return new PasswordPolicyStateOperation(14);
    }
    
    public static PasswordPolicyStateOperation createGetSecondsUntilPasswordExpirationWarningOperation() {
        return new PasswordPolicyStateOperation(15);
    }
    
    public static PasswordPolicyStateOperation createGetAuthenticationFailureTimesOperation() {
        return new PasswordPolicyStateOperation(16);
    }
    
    public static PasswordPolicyStateOperation createAddAuthenticationFailureTimeOperation() {
        return createAddAuthenticationFailureTimeOperation(null);
    }
    
    public static PasswordPolicyStateOperation createAddAuthenticationFailureTimeOperation(final Date[] authFailureTimes) {
        return new PasswordPolicyStateOperation(17, createValues(authFailureTimes));
    }
    
    public static PasswordPolicyStateOperation createSetAuthenticationFailureTimesOperation(final Date[] authFailureTimes) {
        return new PasswordPolicyStateOperation(18, createValues(authFailureTimes));
    }
    
    public static PasswordPolicyStateOperation createClearAuthenticationFailureTimesOperation() {
        return new PasswordPolicyStateOperation(19);
    }
    
    public static PasswordPolicyStateOperation createGetSecondsUntilAuthenticationFailureUnlockOperation() {
        return new PasswordPolicyStateOperation(20);
    }
    
    public static PasswordPolicyStateOperation createGetRemainingAuthenticationFailureCountOperation() {
        return new PasswordPolicyStateOperation(21);
    }
    
    public static PasswordPolicyStateOperation createGetLastLoginTimeOperation() {
        return new PasswordPolicyStateOperation(22);
    }
    
    public static PasswordPolicyStateOperation createSetLastLoginTimeOperation(final Date lastLoginTime) {
        return new PasswordPolicyStateOperation(23, createValues(lastLoginTime));
    }
    
    public static PasswordPolicyStateOperation createClearLastLoginTimeOperation() {
        return new PasswordPolicyStateOperation(24);
    }
    
    public static PasswordPolicyStateOperation createGetLastLoginIPAddressOperation() {
        return new PasswordPolicyStateOperation(49);
    }
    
    public static PasswordPolicyStateOperation createSetLastLoginIPAddressOperation(final String lastLoginIPAddress) {
        final ASN1OctetString[] values = { new ASN1OctetString(lastLoginIPAddress) };
        return new PasswordPolicyStateOperation(50, values);
    }
    
    public static PasswordPolicyStateOperation createClearLastLoginIPAddressOperation() {
        return new PasswordPolicyStateOperation(51);
    }
    
    public static PasswordPolicyStateOperation createGetSecondsUntilIdleLockoutOperation() {
        return new PasswordPolicyStateOperation(25);
    }
    
    public static PasswordPolicyStateOperation createGetPasswordResetStateOperation() {
        return new PasswordPolicyStateOperation(26);
    }
    
    public static PasswordPolicyStateOperation createSetPasswordResetStateOperation(final boolean isReset) {
        final ASN1OctetString[] values = { new ASN1OctetString(String.valueOf(isReset)) };
        return new PasswordPolicyStateOperation(27, values);
    }
    
    public static PasswordPolicyStateOperation createClearPasswordResetStateOperation() {
        return new PasswordPolicyStateOperation(28);
    }
    
    public static PasswordPolicyStateOperation createGetSecondsUntilPasswordResetLockoutOperation() {
        return new PasswordPolicyStateOperation(29);
    }
    
    public static PasswordPolicyStateOperation createGetGraceLoginUseTimesOperation() {
        return new PasswordPolicyStateOperation(30);
    }
    
    public static PasswordPolicyStateOperation createAddGraceLoginUseTimeOperation() {
        return createAddGraceLoginUseTimeOperation(null);
    }
    
    public static PasswordPolicyStateOperation createAddGraceLoginUseTimeOperation(final Date[] graceLoginUseTimes) {
        return new PasswordPolicyStateOperation(31, createValues(graceLoginUseTimes));
    }
    
    public static PasswordPolicyStateOperation createSetGraceLoginUseTimesOperation(final Date[] graceLoginUseTimes) {
        return new PasswordPolicyStateOperation(32, createValues(graceLoginUseTimes));
    }
    
    public static PasswordPolicyStateOperation createClearGraceLoginUseTimesOperation() {
        return new PasswordPolicyStateOperation(33);
    }
    
    public static PasswordPolicyStateOperation createGetRemainingGraceLoginCountOperation() {
        return new PasswordPolicyStateOperation(34);
    }
    
    public static PasswordPolicyStateOperation createGetPasswordChangedByRequiredTimeOperation() {
        return new PasswordPolicyStateOperation(35);
    }
    
    public static PasswordPolicyStateOperation createSetPasswordChangedByRequiredTimeOperation() {
        return createSetPasswordChangedByRequiredTimeOperation(null);
    }
    
    public static PasswordPolicyStateOperation createSetPasswordChangedByRequiredTimeOperation(final Date requiredTime) {
        return new PasswordPolicyStateOperation(36, createValues(requiredTime));
    }
    
    public static PasswordPolicyStateOperation createClearPasswordChangedByRequiredTimeOperation() {
        return new PasswordPolicyStateOperation(37);
    }
    
    public static PasswordPolicyStateOperation createGetSecondsUntilRequiredChangeTimeOperation() {
        return new PasswordPolicyStateOperation(38);
    }
    
    @Deprecated
    public static PasswordPolicyStateOperation createGetPasswordHistoryOperation() {
        return new PasswordPolicyStateOperation(39);
    }
    
    public static PasswordPolicyStateOperation createClearPasswordHistoryOperation() {
        return new PasswordPolicyStateOperation(40);
    }
    
    public static PasswordPolicyStateOperation createHasRetiredPasswordOperation() {
        return new PasswordPolicyStateOperation(41);
    }
    
    public static PasswordPolicyStateOperation createGetPasswordRetiredTimeOperation() {
        return new PasswordPolicyStateOperation(42);
    }
    
    public static PasswordPolicyStateOperation createGetRetiredPasswordExpirationTimeOperation() {
        return new PasswordPolicyStateOperation(43);
    }
    
    public static PasswordPolicyStateOperation createPurgeRetiredPasswordOperation() {
        return new PasswordPolicyStateOperation(44);
    }
    
    public static PasswordPolicyStateOperation createGetAccountUsabilityNoticesOperation() {
        return new PasswordPolicyStateOperation(52);
    }
    
    public static PasswordPolicyStateOperation createGetAccountUsabilityWarningsOperation() {
        return new PasswordPolicyStateOperation(53);
    }
    
    public static PasswordPolicyStateOperation createGetAccountUsabilityErrorsOperation() {
        return new PasswordPolicyStateOperation(54);
    }
    
    public static PasswordPolicyStateOperation createGetAccountIsUsableOperation() {
        return new PasswordPolicyStateOperation(55);
    }
    
    public static PasswordPolicyStateOperation createGetAccountIsNotYetActiveOperation() {
        return new PasswordPolicyStateOperation(56);
    }
    
    public static PasswordPolicyStateOperation createGetAccountIsExpiredOperation() {
        return new PasswordPolicyStateOperation(57);
    }
    
    public static PasswordPolicyStateOperation createGetPasswordExpirationTimeOperation() {
        return new PasswordPolicyStateOperation(58);
    }
    
    public static PasswordPolicyStateOperation createGetAccountIsFailureLockedOperation() {
        return new PasswordPolicyStateOperation(59);
    }
    
    public static PasswordPolicyStateOperation createSetAccountIsFailureLockedOperation(final boolean isFailureLocked) {
        final ASN1OctetString[] values = { new ASN1OctetString(String.valueOf(isFailureLocked)) };
        return new PasswordPolicyStateOperation(60, values);
    }
    
    public static PasswordPolicyStateOperation createGetFailureLockoutTimeOperation() {
        return new PasswordPolicyStateOperation(61);
    }
    
    public static PasswordPolicyStateOperation createGetAccountIsIdleLockedOperation() {
        return new PasswordPolicyStateOperation(62);
    }
    
    public static PasswordPolicyStateOperation createGetIdleLockoutTimeOperation() {
        return new PasswordPolicyStateOperation(63);
    }
    
    public static PasswordPolicyStateOperation createGetAccountIsResetLockedOperation() {
        return new PasswordPolicyStateOperation(64);
    }
    
    public static PasswordPolicyStateOperation createGetResetLockoutTimeOperation() {
        return new PasswordPolicyStateOperation(65);
    }
    
    public static PasswordPolicyStateOperation createGetPasswordHistoryCountOperation() {
        return new PasswordPolicyStateOperation(66);
    }
    
    public static PasswordPolicyStateOperation createGetPasswordIsExpiredOperation() {
        return new PasswordPolicyStateOperation(67);
    }
    
    public static PasswordPolicyStateOperation createGetAvailableSASLMechanismsOperation() {
        return new PasswordPolicyStateOperation(68);
    }
    
    public static PasswordPolicyStateOperation createGetAvailableOTPDeliveryMechanismsOperation() {
        return new PasswordPolicyStateOperation(69);
    }
    
    public static PasswordPolicyStateOperation createHasTOTPSharedSecret() {
        return new PasswordPolicyStateOperation(70);
    }
    
    public static PasswordPolicyStateOperation createAddTOTPSharedSecretOperation(final String... totpSharedSecrets) {
        final ASN1OctetString[] values = new ASN1OctetString[totpSharedSecrets.length];
        for (int i = 0; i < totpSharedSecrets.length; ++i) {
            values[i] = new ASN1OctetString(totpSharedSecrets[i]);
        }
        return new PasswordPolicyStateOperation(76, values);
    }
    
    public static PasswordPolicyStateOperation createRemoveTOTPSharedSecretOperation(final String... totpSharedSecrets) {
        final ASN1OctetString[] values = new ASN1OctetString[totpSharedSecrets.length];
        for (int i = 0; i < totpSharedSecrets.length; ++i) {
            values[i] = new ASN1OctetString(totpSharedSecrets[i]);
        }
        return new PasswordPolicyStateOperation(77, values);
    }
    
    public static PasswordPolicyStateOperation createSetTOTPSharedSecretsOperation(final String... totpSharedSecrets) {
        final ASN1OctetString[] values = new ASN1OctetString[totpSharedSecrets.length];
        for (int i = 0; i < totpSharedSecrets.length; ++i) {
            values[i] = new ASN1OctetString(totpSharedSecrets[i]);
        }
        return new PasswordPolicyStateOperation(78, values);
    }
    
    public static PasswordPolicyStateOperation createClearTOTPSharedSecretsOperation() {
        return new PasswordPolicyStateOperation(79);
    }
    
    public static PasswordPolicyStateOperation createHasYubiKeyPublicIDOperation() {
        return new PasswordPolicyStateOperation(80);
    }
    
    public static PasswordPolicyStateOperation createGetRegisteredYubiKeyPublicIDsOperation() {
        return new PasswordPolicyStateOperation(71);
    }
    
    public static PasswordPolicyStateOperation createAddRegisteredYubiKeyPublicIDOperation(final String... publicIDs) {
        final ASN1OctetString[] values = new ASN1OctetString[publicIDs.length];
        for (int i = 0; i < publicIDs.length; ++i) {
            values[i] = new ASN1OctetString(publicIDs[i]);
        }
        return new PasswordPolicyStateOperation(72, values);
    }
    
    public static PasswordPolicyStateOperation createRemoveRegisteredYubiKeyPublicIDOperation(final String... publicIDs) {
        final ASN1OctetString[] values = new ASN1OctetString[publicIDs.length];
        for (int i = 0; i < publicIDs.length; ++i) {
            values[i] = new ASN1OctetString(publicIDs[i]);
        }
        return new PasswordPolicyStateOperation(73, values);
    }
    
    public static PasswordPolicyStateOperation createSetRegisteredYubiKeyPublicIDsOperation(final String... publicIDs) {
        final ASN1OctetString[] values = new ASN1OctetString[publicIDs.length];
        for (int i = 0; i < publicIDs.length; ++i) {
            values[i] = new ASN1OctetString(publicIDs[i]);
        }
        return new PasswordPolicyStateOperation(74, values);
    }
    
    public static PasswordPolicyStateOperation createClearRegisteredYubiKeyPublicIDsOperation() {
        return new PasswordPolicyStateOperation(75);
    }
    
    public static PasswordPolicyStateOperation createHasStaticPasswordOperation() {
        return new PasswordPolicyStateOperation(81);
    }
    
    public int getOperationType() {
        return this.opType;
    }
    
    public ASN1OctetString[] getRawValues() {
        return this.values;
    }
    
    public String getStringValue() {
        if (this.values.length == 0) {
            return null;
        }
        return this.values[0].stringValue();
    }
    
    public String[] getStringValues() {
        final String[] stringValues = new String[this.values.length];
        for (int i = 0; i < this.values.length; ++i) {
            stringValues[i] = this.values[i].stringValue();
        }
        return stringValues;
    }
    
    public boolean getBooleanValue() throws IllegalStateException {
        if (this.values.length != 1) {
            throw new IllegalStateException(ExtOpMessages.ERR_PWP_STATE_INVALID_BOOLEAN_VALUE_COUNT.get(this.values.length));
        }
        final String valueString = StaticUtils.toLowerCase(this.values[0].stringValue());
        if (valueString.equals("true")) {
            return true;
        }
        if (valueString.equals("false")) {
            return false;
        }
        throw new IllegalStateException(ExtOpMessages.ERR_PWP_STATE_VALUE_NOT_BOOLEAN.get(this.values[0].stringValue()));
    }
    
    public int getIntValue() throws IllegalStateException, NumberFormatException {
        if (this.values.length == 0) {
            throw new IllegalStateException(ExtOpMessages.ERR_PWP_STATE_NO_VALUES.get());
        }
        return Integer.parseInt(this.values[0].stringValue());
    }
    
    public Date getGeneralizedTimeValue() throws ParseException {
        if (this.values.length == 0) {
            return null;
        }
        return StaticUtils.decodeGeneralizedTime(this.values[0].stringValue());
    }
    
    public Date[] getGeneralizedTimeValues() throws ParseException {
        final Date[] dateValues = new Date[this.values.length];
        for (int i = 0; i < this.values.length; ++i) {
            dateValues[i] = StaticUtils.decodeGeneralizedTime(this.values[i].stringValue());
        }
        return dateValues;
    }
    
    private static ASN1OctetString[] createValues(final Date... dates) {
        if (dates == null || dates.length == 0) {
            return PasswordPolicyStateOperation.NO_VALUES;
        }
        final ArrayList<ASN1OctetString> valueList = new ArrayList<ASN1OctetString>(dates.length);
        for (final Date d : dates) {
            if (d != null) {
                valueList.add(new ASN1OctetString(StaticUtils.encodeGeneralizedTime(d)));
            }
        }
        return valueList.toArray(PasswordPolicyStateOperation.NO_VALUES);
    }
    
    public ASN1Element encode() {
        ASN1Element[] elements;
        if (this.values.length > 0) {
            elements = new ASN1Element[] { new ASN1Enumerated(this.opType), new ASN1Sequence((ASN1Element[])this.values) };
        }
        else {
            elements = new ASN1Element[] { new ASN1Enumerated(this.opType) };
        }
        return new ASN1Sequence(elements);
    }
    
    public static PasswordPolicyStateOperation decode(final ASN1Element element) throws LDAPException {
        ASN1Element[] elements;
        try {
            elements = ASN1Sequence.decodeAsSequence(element).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_ELEMENT_NOT_SEQUENCE.get(e), e);
        }
        if (elements.length < 1 || elements.length > 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_INVALID_ELEMENT_COUNT.get(elements.length));
        }
        int opType;
        try {
            opType = ASN1Enumerated.decodeAsEnumerated(elements[0]).intValue();
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_OP_TYPE_NOT_INTEGER.get(e2), e2);
        }
        if (elements.length == 2) {
            try {
                final ASN1Element[] valueElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
                final ASN1OctetString[] values = new ASN1OctetString[valueElements.length];
                for (int i = 0; i < valueElements.length; ++i) {
                    values[i] = ASN1OctetString.decodeAsOctetString(valueElements[i]);
                }
                return new PasswordPolicyStateOperation(opType, values);
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_CANNOT_DECODE_VALUES.get(e3), e3);
            }
        }
        final ASN1OctetString[] values = PasswordPolicyStateOperation.NO_VALUES;
        return new PasswordPolicyStateOperation(opType, values);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordPolicyStateOperation(opType=");
        buffer.append(this.opType);
        if (this.values.length > 0) {
            buffer.append(", values={");
            for (int i = 0; i < this.values.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(this.values[i].stringValue());
                buffer.append('\'');
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
    
    static {
        NO_VALUES = new ASN1OctetString[0];
    }
}
