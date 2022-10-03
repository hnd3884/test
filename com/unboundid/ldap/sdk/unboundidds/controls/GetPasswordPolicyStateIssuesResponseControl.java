package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.BindResult;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityWarning;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityNotice;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityError;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetPasswordPolicyStateIssuesResponseControl extends Control implements DecodeableControl
{
    public static final String GET_PASSWORD_POLICY_STATE_ISSUES_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.47";
    private static final byte TYPE_NOTICES = -96;
    private static final byte TYPE_WARNINGS = -95;
    private static final byte TYPE_ERRORS = -94;
    private static final byte TYPE_AUTH_FAILURE_REASON = -93;
    private static final long serialVersionUID = 7509027658735069270L;
    private final AuthenticationFailureReason authFailureReason;
    private final List<PasswordPolicyStateAccountUsabilityError> errors;
    private final List<PasswordPolicyStateAccountUsabilityNotice> notices;
    private final List<PasswordPolicyStateAccountUsabilityWarning> warnings;
    
    GetPasswordPolicyStateIssuesResponseControl() {
        this.authFailureReason = null;
        this.notices = Collections.emptyList();
        this.warnings = Collections.emptyList();
        this.errors = Collections.emptyList();
    }
    
    public GetPasswordPolicyStateIssuesResponseControl(final List<PasswordPolicyStateAccountUsabilityNotice> notices, final List<PasswordPolicyStateAccountUsabilityWarning> warnings, final List<PasswordPolicyStateAccountUsabilityError> errors) {
        this(notices, warnings, errors, null);
    }
    
    public GetPasswordPolicyStateIssuesResponseControl(final List<PasswordPolicyStateAccountUsabilityNotice> notices, final List<PasswordPolicyStateAccountUsabilityWarning> warnings, final List<PasswordPolicyStateAccountUsabilityError> errors, final AuthenticationFailureReason authFailureReason) {
        super("1.3.6.1.4.1.30221.2.5.47", false, encodeValue(notices, warnings, errors, authFailureReason));
        this.authFailureReason = authFailureReason;
        if (notices == null) {
            this.notices = Collections.emptyList();
        }
        else {
            this.notices = Collections.unmodifiableList((List<? extends PasswordPolicyStateAccountUsabilityNotice>)new ArrayList<PasswordPolicyStateAccountUsabilityNotice>(notices));
        }
        if (warnings == null) {
            this.warnings = Collections.emptyList();
        }
        else {
            this.warnings = Collections.unmodifiableList((List<? extends PasswordPolicyStateAccountUsabilityWarning>)new ArrayList<PasswordPolicyStateAccountUsabilityWarning>(warnings));
        }
        if (errors == null) {
            this.errors = Collections.emptyList();
        }
        else {
            this.errors = Collections.unmodifiableList((List<? extends PasswordPolicyStateAccountUsabilityError>)new ArrayList<PasswordPolicyStateAccountUsabilityError>(errors));
        }
    }
    
    public GetPasswordPolicyStateIssuesResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_PWP_STATE_ISSUES_RESPONSE_NO_VALUE.get());
        }
        AuthenticationFailureReason afr = null;
        List<PasswordPolicyStateAccountUsabilityNotice> nList = Collections.emptyList();
        List<PasswordPolicyStateAccountUsabilityWarning> wList = Collections.emptyList();
        List<PasswordPolicyStateAccountUsabilityError> eList = Collections.emptyList();
        try {
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case -96: {
                        nList = new ArrayList<PasswordPolicyStateAccountUsabilityNotice>(10);
                        for (final ASN1Element ne : ASN1Sequence.decodeAsSequence(e).elements()) {
                            final ASN1Element[] noticeElements = ASN1Sequence.decodeAsSequence(ne).elements();
                            final int type = ASN1Integer.decodeAsInteger(noticeElements[0]).intValue();
                            final String name = ASN1OctetString.decodeAsOctetString(noticeElements[1]).stringValue();
                            String message;
                            if (noticeElements.length == 3) {
                                message = ASN1OctetString.decodeAsOctetString(noticeElements[2]).stringValue();
                            }
                            else {
                                message = null;
                            }
                            nList.add(new PasswordPolicyStateAccountUsabilityNotice(type, name, message));
                        }
                        nList = Collections.unmodifiableList((List<? extends PasswordPolicyStateAccountUsabilityNotice>)nList);
                        break;
                    }
                    case -95: {
                        wList = new ArrayList<PasswordPolicyStateAccountUsabilityWarning>(10);
                        for (final ASN1Element we : ASN1Sequence.decodeAsSequence(e).elements()) {
                            final ASN1Element[] warningElements = ASN1Sequence.decodeAsSequence(we).elements();
                            final int type = ASN1Integer.decodeAsInteger(warningElements[0]).intValue();
                            final String name = ASN1OctetString.decodeAsOctetString(warningElements[1]).stringValue();
                            String message;
                            if (warningElements.length == 3) {
                                message = ASN1OctetString.decodeAsOctetString(warningElements[2]).stringValue();
                            }
                            else {
                                message = null;
                            }
                            wList.add(new PasswordPolicyStateAccountUsabilityWarning(type, name, message));
                        }
                        wList = Collections.unmodifiableList((List<? extends PasswordPolicyStateAccountUsabilityWarning>)wList);
                        break;
                    }
                    case -94: {
                        eList = new ArrayList<PasswordPolicyStateAccountUsabilityError>(10);
                        for (final ASN1Element ee : ASN1Sequence.decodeAsSequence(e).elements()) {
                            final ASN1Element[] errorElements = ASN1Sequence.decodeAsSequence(ee).elements();
                            final int type = ASN1Integer.decodeAsInteger(errorElements[0]).intValue();
                            final String name = ASN1OctetString.decodeAsOctetString(errorElements[1]).stringValue();
                            String message;
                            if (errorElements.length == 3) {
                                message = ASN1OctetString.decodeAsOctetString(errorElements[2]).stringValue();
                            }
                            else {
                                message = null;
                            }
                            eList.add(new PasswordPolicyStateAccountUsabilityError(type, name, message));
                        }
                        eList = Collections.unmodifiableList((List<? extends PasswordPolicyStateAccountUsabilityError>)eList);
                        break;
                    }
                    case -93: {
                        final ASN1Element[] afrElements = ASN1Sequence.decodeAsSequence(e).elements();
                        final int afrType = ASN1Integer.decodeAsInteger(afrElements[0]).intValue();
                        final String afrName = ASN1OctetString.decodeAsOctetString(afrElements[1]).stringValue();
                        String afrMessage;
                        if (afrElements.length == 3) {
                            afrMessage = ASN1OctetString.decodeAsOctetString(afrElements[2]).stringValue();
                        }
                        else {
                            afrMessage = null;
                        }
                        afr = new AuthenticationFailureReason(afrType, afrName, afrMessage);
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_PWP_STATE_ISSUES_RESPONSE_UNEXPECTED_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_PWP_STATE_ISSUES_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        this.authFailureReason = afr;
        this.notices = nList;
        this.warnings = wList;
        this.errors = eList;
    }
    
    private static ASN1OctetString encodeValue(final List<PasswordPolicyStateAccountUsabilityNotice> notices, final List<PasswordPolicyStateAccountUsabilityWarning> warnings, final List<PasswordPolicyStateAccountUsabilityError> errors, final AuthenticationFailureReason authFailureReason) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        if (notices != null && !notices.isEmpty()) {
            final ArrayList<ASN1Element> noticeElements = new ArrayList<ASN1Element>(notices.size());
            for (final PasswordPolicyStateAccountUsabilityNotice n : notices) {
                if (n.getMessage() == null) {
                    noticeElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Integer(n.getIntValue()), new ASN1OctetString(n.getName()) }));
                }
                else {
                    noticeElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Integer(n.getIntValue()), new ASN1OctetString(n.getName()), new ASN1OctetString(n.getMessage()) }));
                }
            }
            elements.add(new ASN1Sequence((byte)(-96), noticeElements));
        }
        if (warnings != null && !warnings.isEmpty()) {
            final ArrayList<ASN1Element> warningElements = new ArrayList<ASN1Element>(warnings.size());
            for (final PasswordPolicyStateAccountUsabilityWarning w : warnings) {
                if (w.getMessage() == null) {
                    warningElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Integer(w.getIntValue()), new ASN1OctetString(w.getName()) }));
                }
                else {
                    warningElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Integer(w.getIntValue()), new ASN1OctetString(w.getName()), new ASN1OctetString(w.getMessage()) }));
                }
            }
            elements.add(new ASN1Sequence((byte)(-95), warningElements));
        }
        if (errors != null && !errors.isEmpty()) {
            final ArrayList<ASN1Element> errorElements = new ArrayList<ASN1Element>(errors.size());
            for (final PasswordPolicyStateAccountUsabilityError e : errors) {
                if (e.getMessage() == null) {
                    errorElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Integer(e.getIntValue()), new ASN1OctetString(e.getName()) }));
                }
                else {
                    errorElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1Integer(e.getIntValue()), new ASN1OctetString(e.getName()), new ASN1OctetString(e.getMessage()) }));
                }
            }
            elements.add(new ASN1Sequence((byte)(-94), errorElements));
        }
        if (authFailureReason != null) {
            if (authFailureReason.getMessage() == null) {
                elements.add(new ASN1Sequence((byte)(-93), new ASN1Element[] { new ASN1Integer(authFailureReason.getIntValue()), new ASN1OctetString(authFailureReason.getName()) }));
            }
            else {
                elements.add(new ASN1Sequence((byte)(-93), new ASN1Element[] { new ASN1Integer(authFailureReason.getIntValue()), new ASN1OctetString(authFailureReason.getName()), new ASN1OctetString(authFailureReason.getMessage()) }));
            }
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    @Override
    public GetPasswordPolicyStateIssuesResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new GetPasswordPolicyStateIssuesResponseControl(oid, isCritical, value);
    }
    
    public List<PasswordPolicyStateAccountUsabilityNotice> getNotices() {
        return this.notices;
    }
    
    public List<PasswordPolicyStateAccountUsabilityWarning> getWarnings() {
        return this.warnings;
    }
    
    public List<PasswordPolicyStateAccountUsabilityError> getErrors() {
        return this.errors;
    }
    
    public AuthenticationFailureReason getAuthenticationFailureReason() {
        return this.authFailureReason;
    }
    
    public static GetPasswordPolicyStateIssuesResponseControl get(final BindResult bindResult) throws LDAPException {
        final Control c = bindResult.getResponseControl("1.3.6.1.4.1.30221.2.5.47");
        if (c == null) {
            return null;
        }
        if (c instanceof GetPasswordPolicyStateIssuesResponseControl) {
            return (GetPasswordPolicyStateIssuesResponseControl)c;
        }
        return new GetPasswordPolicyStateIssuesResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static GetPasswordPolicyStateIssuesResponseControl get(final LDAPException ldapException) throws LDAPException {
        final Control c = ldapException.getResponseControl("1.3.6.1.4.1.30221.2.5.47");
        if (c == null) {
            return null;
        }
        if (c instanceof GetPasswordPolicyStateIssuesResponseControl) {
            return (GetPasswordPolicyStateIssuesResponseControl)c;
        }
        return new GetPasswordPolicyStateIssuesResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_PWP_STATE_ISSUES_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetPasswordPolicyStateIssuesResponseControl(notices={ ");
        final Iterator<PasswordPolicyStateAccountUsabilityNotice> noticeIterator = this.notices.iterator();
        while (noticeIterator.hasNext()) {
            buffer.append(noticeIterator.next().toString());
            if (noticeIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, warnings={ ");
        final Iterator<PasswordPolicyStateAccountUsabilityWarning> warningIterator = this.warnings.iterator();
        while (warningIterator.hasNext()) {
            buffer.append(warningIterator.next().toString());
            if (warningIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, errors={ ");
        final Iterator<PasswordPolicyStateAccountUsabilityError> errorIterator = this.errors.iterator();
        while (errorIterator.hasNext()) {
            buffer.append(errorIterator.next().toString());
            if (errorIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        if (this.authFailureReason != null) {
            buffer.append(", authFailureReason=");
            buffer.append(this.authFailureReason.toString());
        }
        buffer.append(')');
    }
}
