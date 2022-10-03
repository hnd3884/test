package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Enumerated;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPResult;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CompareResponseProtocolOp extends GenericResponseProtocolOp
{
    private static final long serialVersionUID = 3237778285010810669L;
    
    public CompareResponseProtocolOp(final int resultCode, final String matchedDN, final String diagnosticMessage, final List<String> referralURLs) {
        super((byte)111, resultCode, matchedDN, diagnosticMessage, referralURLs);
    }
    
    public CompareResponseProtocolOp(final LDAPResult result) {
        super((byte)111, result.getResultCode().intValue(), result.getMatchedDN(), result.getDiagnosticMessage(), StaticUtils.toList(result.getReferralURLs()));
    }
    
    CompareResponseProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        super(reader);
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1Enumerated(this.getResultCode()));
        final String matchedDN = this.getMatchedDN();
        if (matchedDN == null) {
            elements.add(new ASN1OctetString());
        }
        else {
            elements.add(new ASN1OctetString(matchedDN));
        }
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (diagnosticMessage == null) {
            elements.add(new ASN1OctetString());
        }
        else {
            elements.add(new ASN1OctetString(diagnosticMessage));
        }
        final List<String> referralURLs = this.getReferralURLs();
        if (!referralURLs.isEmpty()) {
            final ArrayList<ASN1Element> refElements = new ArrayList<ASN1Element>(referralURLs.size());
            for (final String r : referralURLs) {
                refElements.add(new ASN1OctetString(r));
            }
            elements.add(new ASN1Sequence((byte)(-93), refElements));
        }
        return new ASN1Sequence((byte)111, elements);
    }
    
    public static CompareResponseProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final int resultCode = ASN1Enumerated.decodeAsEnumerated(elements[0]).intValue();
            final String md = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            String matchedDN;
            if (!md.isEmpty()) {
                matchedDN = md;
            }
            else {
                matchedDN = null;
            }
            final String dm = ASN1OctetString.decodeAsOctetString(elements[2]).stringValue();
            String diagnosticMessage;
            if (!dm.isEmpty()) {
                diagnosticMessage = dm;
            }
            else {
                diagnosticMessage = null;
            }
            List<String> referralURLs;
            if (elements.length == 4) {
                final ASN1Element[] refElements = ASN1Sequence.decodeAsSequence(elements[3]).elements();
                referralURLs = new ArrayList<String>(refElements.length);
                for (final ASN1Element e : refElements) {
                    referralURLs.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                }
            }
            else {
                referralURLs = null;
            }
            return new CompareResponseProtocolOp(resultCode, matchedDN, diagnosticMessage, referralURLs);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_COMPARE_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
}
