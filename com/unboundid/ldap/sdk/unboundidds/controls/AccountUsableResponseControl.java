package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.Collection;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AccountUsableResponseControl extends Control implements DecodeableControl
{
    public static final String ACCOUNT_USABLE_RESPONSE_OID = "1.3.6.1.4.1.42.2.27.9.5.8";
    private static final byte TYPE_SECONDS_UNTIL_EXPIRATION = Byte.MIN_VALUE;
    private static final byte TYPE_MORE_INFO = -95;
    private static final byte TYPE_IS_INACTIVE = Byte.MIN_VALUE;
    private static final byte TYPE_MUST_CHANGE = -127;
    private static final byte TYPE_IS_EXPIRED = -126;
    private static final byte TYPE_REMAINING_GRACE_LOGINS = -125;
    private static final byte TYPE_SECONDS_UNTIL_UNLOCK = -124;
    private static final long serialVersionUID = -9150988495337467770L;
    private final boolean isInactive;
    private final boolean isUsable;
    private final boolean mustChangePassword;
    private final boolean passwordIsExpired;
    private final List<String> unusableReasons;
    private final int remainingGraceLogins;
    private final int secondsUntilExpiration;
    private final int secondsUntilUnlock;
    
    AccountUsableResponseControl() {
        this.isUsable = false;
        this.secondsUntilExpiration = 0;
        this.isInactive = false;
        this.mustChangePassword = false;
        this.passwordIsExpired = false;
        this.remainingGraceLogins = 0;
        this.secondsUntilUnlock = 0;
        this.unusableReasons = Collections.emptyList();
    }
    
    public AccountUsableResponseControl(final int secondsUntilExpiration) {
        super("1.3.6.1.4.1.42.2.27.9.5.8", false, encodeValue(secondsUntilExpiration));
        this.isUsable = true;
        this.secondsUntilExpiration = secondsUntilExpiration;
        this.isInactive = false;
        this.mustChangePassword = false;
        this.passwordIsExpired = false;
        this.remainingGraceLogins = -1;
        this.secondsUntilUnlock = -1;
        this.unusableReasons = Collections.emptyList();
    }
    
    public AccountUsableResponseControl(final boolean isInactive, final boolean mustChangePassword, final boolean passwordIsExpired, final int remainingGraceLogins, final int secondsUntilUnlock) {
        super("1.3.6.1.4.1.42.2.27.9.5.8", false, encodeValue(isInactive, mustChangePassword, passwordIsExpired, remainingGraceLogins, secondsUntilUnlock));
        this.isUsable = false;
        this.secondsUntilExpiration = -1;
        this.isInactive = isInactive;
        this.mustChangePassword = mustChangePassword;
        this.passwordIsExpired = passwordIsExpired;
        this.remainingGraceLogins = remainingGraceLogins;
        this.secondsUntilUnlock = secondsUntilUnlock;
        final ArrayList<String> unusableList = new ArrayList<String>(5);
        if (isInactive) {
            unusableList.add(ControlMessages.ERR_ACCT_UNUSABLE_INACTIVE.get());
        }
        if (mustChangePassword) {
            unusableList.add(ControlMessages.ERR_ACCT_UNUSABLE_MUST_CHANGE_PW.get());
        }
        if (passwordIsExpired) {
            unusableList.add(ControlMessages.ERR_ACCT_UNUSABLE_PW_EXPIRED.get());
        }
        if (remainingGraceLogins >= 0) {
            switch (remainingGraceLogins) {
                case 0: {
                    unusableList.add(ControlMessages.ERR_ACCT_UNUSABLE_REMAINING_GRACE_NONE.get());
                    break;
                }
                case 1: {
                    unusableList.add(ControlMessages.ERR_ACCT_UNUSABLE_REMAINING_GRACE_ONE.get());
                    break;
                }
                default: {
                    unusableList.add(ControlMessages.ERR_ACCT_UNUSABLE_REMAINING_GRACE_MULTIPLE.get(remainingGraceLogins));
                    break;
                }
            }
        }
        if (secondsUntilUnlock > 0) {
            unusableList.add(ControlMessages.ERR_ACCT_UNUSABLE_SECONDS_UNTIL_UNLOCK.get(secondsUntilUnlock));
        }
        this.unusableReasons = Collections.unmodifiableList((List<? extends String>)unusableList);
    }
    
    public AccountUsableResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_NO_VALUE.get());
        }
        ASN1Element valueElement;
        try {
            valueElement = ASN1Element.decode(value.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_VALUE_NOT_ELEMENT.get(e), e);
        }
        boolean decodedIsInactive = false;
        boolean decodedMustChangePassword = false;
        boolean decodedPasswordIsExpired = false;
        int decodedRemainingGraceLogins = -1;
        int decodedSecondsUntilExpiration = -1;
        int decodedSecondsUntilUnlock = -1;
        final List<String> decodedUnusableReasons = new ArrayList<String>(5);
        final byte type = valueElement.getType();
        boolean decodedIsUsable = false;
        Label_0794: {
            if (type == -128) {
                decodedIsUsable = true;
                try {
                    decodedSecondsUntilExpiration = ASN1Integer.decodeAsInteger(valueElement).intValue();
                    if (decodedSecondsUntilExpiration < 0) {
                        decodedSecondsUntilExpiration = -1;
                    }
                    break Label_0794;
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_STE_NOT_INT.get(e2), e2);
                }
            }
            if (type != -95) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_INVALID_TYPE.get(StaticUtils.toHex(type)));
            }
            decodedIsUsable = false;
            ASN1Element[] elements;
            try {
                elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_VALUE_NOT_SEQUENCE.get(e3), e3);
            }
            for (final ASN1Element element : elements) {
                switch (element.getType()) {
                    case Byte.MIN_VALUE: {
                        try {
                            decodedIsInactive = ASN1Boolean.decodeAsBoolean(element).booleanValue();
                            decodedUnusableReasons.add(ControlMessages.ERR_ACCT_UNUSABLE_INACTIVE.get());
                            break;
                        }
                        catch (final Exception e4) {
                            Debug.debugException(e4);
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_INACTIVE_NOT_BOOLEAN.get(e4), e4);
                        }
                    }
                    case -127: {
                        try {
                            decodedMustChangePassword = ASN1Boolean.decodeAsBoolean(element).booleanValue();
                            decodedUnusableReasons.add(ControlMessages.ERR_ACCT_UNUSABLE_MUST_CHANGE_PW.get());
                            break;
                        }
                        catch (final Exception e4) {
                            Debug.debugException(e4);
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_MUST_CHANGE_NOT_BOOLEAN.get(e4), e4);
                        }
                    }
                    case -126: {
                        try {
                            decodedPasswordIsExpired = ASN1Boolean.decodeAsBoolean(element).booleanValue();
                            decodedUnusableReasons.add(ControlMessages.ERR_ACCT_UNUSABLE_PW_EXPIRED.get());
                            break;
                        }
                        catch (final Exception e4) {
                            Debug.debugException(e4);
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_IS_EXP_NOT_BOOLEAN.get(e4), e4);
                        }
                    }
                    case -125: {
                        try {
                            decodedRemainingGraceLogins = ASN1Integer.decodeAsInteger(element).intValue();
                            if (decodedRemainingGraceLogins < 0) {
                                decodedRemainingGraceLogins = -1;
                            }
                            else {
                                switch (decodedRemainingGraceLogins) {
                                    case 0: {
                                        decodedUnusableReasons.add(ControlMessages.ERR_ACCT_UNUSABLE_REMAINING_GRACE_NONE.get());
                                        break;
                                    }
                                    case 1: {
                                        decodedUnusableReasons.add(ControlMessages.ERR_ACCT_UNUSABLE_REMAINING_GRACE_ONE.get());
                                        break;
                                    }
                                    default: {
                                        decodedUnusableReasons.add(ControlMessages.ERR_ACCT_UNUSABLE_REMAINING_GRACE_MULTIPLE.get(decodedRemainingGraceLogins));
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        catch (final Exception e4) {
                            Debug.debugException(e4);
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_GRACE_LOGINS_NOT_INT.get(e4), e4);
                        }
                    }
                    case -124: {
                        try {
                            decodedSecondsUntilUnlock = ASN1Integer.decodeAsInteger(element).intValue();
                            if (decodedSecondsUntilUnlock < 0) {
                                decodedSecondsUntilUnlock = -1;
                            }
                            else if (decodedSecondsUntilUnlock > 0) {
                                decodedUnusableReasons.add(ControlMessages.ERR_ACCT_UNUSABLE_SECONDS_UNTIL_UNLOCK.get(decodedSecondsUntilUnlock));
                            }
                            break;
                        }
                        catch (final Exception e4) {
                            Debug.debugException(e4);
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_STU_NOT_INT.get(e4), e4);
                        }
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_RESPONSE_MORE_INFO_INVALID_TYPE.get(StaticUtils.toHex(element.getType())));
                    }
                }
            }
        }
        this.isUsable = decodedIsUsable;
        this.secondsUntilExpiration = decodedSecondsUntilExpiration;
        this.isInactive = decodedIsInactive;
        this.mustChangePassword = decodedMustChangePassword;
        this.passwordIsExpired = decodedPasswordIsExpired;
        this.remainingGraceLogins = decodedRemainingGraceLogins;
        this.secondsUntilUnlock = decodedSecondsUntilUnlock;
        this.unusableReasons = Collections.unmodifiableList((List<? extends String>)decodedUnusableReasons);
    }
    
    private static ASN1OctetString encodeValue(final int secondsUntilExpiration) {
        final ASN1Integer sueInteger = new ASN1Integer((byte)(-128), secondsUntilExpiration);
        return new ASN1OctetString(sueInteger.encode());
    }
    
    private static ASN1OctetString encodeValue(final boolean isInactive, final boolean mustChangePassword, final boolean passwordIsExpired, final int remainingGraceLogins, final int secondsUntilUnlock) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(5);
        if (isInactive) {
            elements.add(new ASN1Boolean((byte)(-128), true));
        }
        if (mustChangePassword) {
            elements.add(new ASN1Boolean((byte)(-127), true));
        }
        if (passwordIsExpired) {
            elements.add(new ASN1Boolean((byte)(-126), true));
        }
        if (remainingGraceLogins >= 0) {
            elements.add(new ASN1Integer((byte)(-125), remainingGraceLogins));
        }
        if (secondsUntilUnlock >= 0) {
            elements.add(new ASN1Integer((byte)(-124), secondsUntilUnlock));
        }
        final ASN1Sequence valueSequence = new ASN1Sequence((byte)(-95), elements);
        return new ASN1OctetString(valueSequence.encode());
    }
    
    @Override
    public AccountUsableResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new AccountUsableResponseControl(oid, isCritical, value);
    }
    
    public static AccountUsableResponseControl get(final SearchResultEntry entry) throws LDAPException {
        final Control c = entry.getControl("1.3.6.1.4.1.42.2.27.9.5.8");
        if (c == null) {
            return null;
        }
        if (c instanceof AccountUsableResponseControl) {
            return (AccountUsableResponseControl)c;
        }
        return new AccountUsableResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public boolean isUsable() {
        return this.isUsable;
    }
    
    public List<String> getUnusableReasons() {
        return this.unusableReasons;
    }
    
    public int getSecondsUntilExpiration() {
        return this.secondsUntilExpiration;
    }
    
    public boolean isInactive() {
        return this.isInactive;
    }
    
    public boolean mustChangePassword() {
        return this.mustChangePassword;
    }
    
    public boolean passwordIsExpired() {
        return this.passwordIsExpired;
    }
    
    public int getRemainingGraceLogins() {
        return this.remainingGraceLogins;
    }
    
    public int getSecondsUntilUnlock() {
        return this.secondsUntilUnlock;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ACCOUNT_USABLE_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AccountUsableResponseControl(isUsable=");
        buffer.append(this.isUsable);
        if (this.isUsable) {
            if (this.secondsUntilExpiration >= 0) {
                buffer.append(", secondsUntilExpiration=");
                buffer.append(this.secondsUntilExpiration);
            }
        }
        else {
            buffer.append(", isInactive=");
            buffer.append(this.isInactive);
            buffer.append(", mustChangePassword=");
            buffer.append(this.mustChangePassword);
            buffer.append(", passwordIsExpired=");
            buffer.append(this.passwordIsExpired);
            if (this.remainingGraceLogins >= 0) {
                buffer.append(", remainingGraceLogins=");
                buffer.append(this.remainingGraceLogins);
            }
            if (this.secondsUntilUnlock >= 0) {
                buffer.append(", secondsUntilUnlock=");
                buffer.append(this.secondsUntilUnlock);
            }
        }
        buffer.append(')');
    }
}
