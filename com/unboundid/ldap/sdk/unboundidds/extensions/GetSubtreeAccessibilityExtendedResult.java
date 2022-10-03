package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.ldap.sdk.Control;
import java.util.Collection;
import java.util.Date;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import java.util.Collections;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetSubtreeAccessibilityExtendedResult extends ExtendedResult
{
    public static final String GET_SUBTREE_ACCESSIBILITY_RESULT_OID = "1.3.6.1.4.1.30221.1.6.21";
    private static final byte TYPE_BASE_DN = Byte.MIN_VALUE;
    private static final byte TYPE_STATE = -127;
    private static final byte TYPE_BYPASS_USER = -126;
    private static final byte TYPE_EFFECTIVE_TIME = -125;
    private static final long serialVersionUID = -3163306122775326749L;
    private final List<SubtreeAccessibilityRestriction> accessibilityRestrictions;
    
    public GetSubtreeAccessibilityExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.accessibilityRestrictions = null;
            return;
        }
        try {
            final ASN1Element[] restrictionElements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            final ArrayList<SubtreeAccessibilityRestriction> restrictionList = new ArrayList<SubtreeAccessibilityRestriction>(restrictionElements.length);
            for (final ASN1Element e : restrictionElements) {
                String baseDN = null;
                SubtreeAccessibilityState state = null;
                String bypassDN = null;
                Date effectiveTime = null;
                for (final ASN1Element re : ASN1Sequence.decodeAsSequence(e).elements()) {
                    switch (re.getType()) {
                        case Byte.MIN_VALUE: {
                            baseDN = ASN1OctetString.decodeAsOctetString(re).stringValue();
                            break;
                        }
                        case -127: {
                            state = SubtreeAccessibilityState.valueOf(ASN1Enumerated.decodeAsEnumerated(re).intValue());
                            if (state == null) {
                                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_UNEXPECTED_STATE.get(ASN1Enumerated.decodeAsEnumerated(re).intValue()));
                            }
                            break;
                        }
                        case -126: {
                            bypassDN = ASN1OctetString.decodeAsOctetString(re).stringValue();
                            break;
                        }
                        case -125: {
                            effectiveTime = StaticUtils.decodeGeneralizedTime(ASN1OctetString.decodeAsOctetString(re).stringValue());
                            break;
                        }
                        default: {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_UNEXPECTED_TYPE.get(StaticUtils.toHex(re.getType())));
                        }
                    }
                }
                if (baseDN == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_MISSING_BASE.get());
                }
                if (state == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_MISSING_STATE.get());
                }
                if (effectiveTime == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_MISSING_TIME.get());
                }
                restrictionList.add(new SubtreeAccessibilityRestriction(baseDN, state, bypassDN, effectiveTime));
            }
            this.accessibilityRestrictions = Collections.unmodifiableList((List<? extends SubtreeAccessibilityRestriction>)restrictionList);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public GetSubtreeAccessibilityExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Collection<SubtreeAccessibilityRestriction> restrictions, final Control... responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, null, encodeValue(restrictions), responseControls);
        if (restrictions == null) {
            this.accessibilityRestrictions = null;
        }
        else {
            this.accessibilityRestrictions = Collections.unmodifiableList((List<? extends SubtreeAccessibilityRestriction>)new ArrayList<SubtreeAccessibilityRestriction>(restrictions));
        }
    }
    
    private static ASN1OctetString encodeValue(final Collection<SubtreeAccessibilityRestriction> restrictions) {
        if (restrictions == null) {
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(restrictions.size());
        for (final SubtreeAccessibilityRestriction r : restrictions) {
            final ArrayList<ASN1Element> restrictionElements = new ArrayList<ASN1Element>(4);
            restrictionElements.add(new ASN1OctetString((byte)(-128), r.getSubtreeBaseDN()));
            restrictionElements.add(new ASN1Enumerated((byte)(-127), r.getAccessibilityState().intValue()));
            if (r.getBypassUserDN() != null) {
                restrictionElements.add(new ASN1OctetString((byte)(-126), r.getBypassUserDN()));
            }
            restrictionElements.add(new ASN1OctetString((byte)(-125), StaticUtils.encodeGeneralizedTime(r.getEffectiveTime())));
            elements.add(new ASN1Sequence(restrictionElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public List<SubtreeAccessibilityRestriction> getAccessibilityRestrictions() {
        return this.accessibilityRestrictions;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_GET_SUBTREE_ACCESSIBILITY.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetSubtreeAccessibilityExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(diagnosticMessage);
            buffer.append('\'');
        }
        final String matchedDN = this.getMatchedDN();
        if (matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(matchedDN);
            buffer.append('\'');
        }
        final String[] referralURLs = this.getReferralURLs();
        if (referralURLs != null && referralURLs.length > 0) {
            buffer.append(", referralURLs={ '");
            for (int i = 0; i < referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append("', '");
                }
                buffer.append(referralURLs[i]);
            }
            buffer.append("' }");
        }
        if (this.accessibilityRestrictions != null) {
            buffer.append(", accessibilityRestrictions={");
            final Iterator<SubtreeAccessibilityRestriction> iterator = this.accessibilityRestrictions.iterator();
            while (iterator.hasNext()) {
                iterator.next().toString(buffer);
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        final Control[] controls = this.getResponseControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int j = 0; j < controls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
