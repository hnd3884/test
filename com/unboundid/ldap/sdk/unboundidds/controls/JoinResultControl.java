package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.util.Validator;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Collections;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JoinResultControl extends Control implements DecodeableControl
{
    public static final String JOIN_RESULT_OID = "1.3.6.1.4.1.30221.2.5.9";
    private static final byte TYPE_REFERRAL_URLS = -93;
    private static final byte TYPE_JOIN_RESULTS = -92;
    private static final long serialVersionUID = 681831114773253358L;
    private final List<JoinedEntry> joinResults;
    private final List<String> referralURLs;
    private final ResultCode resultCode;
    private final String diagnosticMessage;
    private final String matchedDN;
    
    JoinResultControl() {
        this.resultCode = null;
        this.diagnosticMessage = null;
        this.matchedDN = null;
        this.referralURLs = null;
        this.joinResults = null;
    }
    
    public JoinResultControl(final List<JoinedEntry> joinResults) {
        this(ResultCode.SUCCESS, null, null, null, joinResults);
    }
    
    public JoinResultControl(final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final List<String> referralURLs, final List<JoinedEntry> joinResults) {
        super("1.3.6.1.4.1.30221.2.5.9", false, encodeValue(resultCode, diagnosticMessage, matchedDN, referralURLs, joinResults));
        this.resultCode = resultCode;
        this.diagnosticMessage = diagnosticMessage;
        this.matchedDN = matchedDN;
        if (referralURLs == null) {
            this.referralURLs = Collections.emptyList();
        }
        else {
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)referralURLs);
        }
        if (joinResults == null) {
            this.joinResults = Collections.emptyList();
        }
        else {
            this.joinResults = Collections.unmodifiableList((List<? extends JoinedEntry>)joinResults);
        }
    }
    
    public JoinResultControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_RESULT_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            this.resultCode = ResultCode.valueOf(ASN1Enumerated.decodeAsEnumerated(elements[0]).intValue());
            final String matchedDNStr = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            if (matchedDNStr.isEmpty()) {
                this.matchedDN = null;
            }
            else {
                this.matchedDN = matchedDNStr;
            }
            final String diagnosticMessageStr = ASN1OctetString.decodeAsOctetString(elements[2]).stringValue();
            if (diagnosticMessageStr.isEmpty()) {
                this.diagnosticMessage = null;
            }
            else {
                this.diagnosticMessage = diagnosticMessageStr;
            }
            final ArrayList<String> refs = new ArrayList<String>(5);
            final ArrayList<JoinedEntry> entries = new ArrayList<JoinedEntry>(20);
            for (int i = 3; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case -93: {
                        final ASN1Element[] arr$;
                        final ASN1Element[] refElements = arr$ = ASN1Sequence.decodeAsSequence(elements[i]).elements();
                        for (final ASN1Element e : arr$) {
                            refs.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                        }
                        break;
                    }
                    case -92: {
                        final ASN1Element[] arr$2;
                        final ASN1Element[] entryElements = arr$2 = ASN1Sequence.decodeAsSequence(elements[i]).elements();
                        for (final ASN1Element e2 : arr$2) {
                            entries.add(JoinedEntry.decode(e2));
                        }
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_RESULT_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)refs);
            this.joinResults = Collections.unmodifiableList((List<? extends JoinedEntry>)entries);
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_RESULT_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
    }
    
    private static ASN1OctetString encodeValue(final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final List<String> referralURLs, final List<JoinedEntry> joinResults) {
        Validator.ensureNotNull(resultCode);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(5);
        elements.add(new ASN1Enumerated(resultCode.intValue()));
        if (matchedDN == null) {
            elements.add(new ASN1OctetString());
        }
        else {
            elements.add(new ASN1OctetString(matchedDN));
        }
        if (diagnosticMessage == null) {
            elements.add(new ASN1OctetString());
        }
        else {
            elements.add(new ASN1OctetString(diagnosticMessage));
        }
        if (referralURLs != null && !referralURLs.isEmpty()) {
            final ArrayList<ASN1Element> refElements = new ArrayList<ASN1Element>(referralURLs.size());
            for (final String s : referralURLs) {
                refElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-93), refElements));
        }
        if (joinResults == null || joinResults.isEmpty()) {
            elements.add(new ASN1Sequence((byte)(-92)));
        }
        else {
            final ArrayList<ASN1Element> entryElements = new ArrayList<ASN1Element>(joinResults.size());
            for (final JoinedEntry e : joinResults) {
                entryElements.add(e.encode());
            }
            elements.add(new ASN1Sequence((byte)(-92), entryElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public String getDiagnosticMessage() {
        return this.diagnosticMessage;
    }
    
    public String getMatchedDN() {
        return this.matchedDN;
    }
    
    public List<String> getReferralURLs() {
        return this.referralURLs;
    }
    
    public List<JoinedEntry> getJoinResults() {
        return this.joinResults;
    }
    
    @Override
    public JoinResultControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new JoinResultControl(oid, isCritical, value);
    }
    
    public static JoinResultControl get(final SearchResultEntry entry) throws LDAPException {
        final Control c = entry.getControl("1.3.6.1.4.1.30221.2.5.9");
        if (c == null) {
            return null;
        }
        if (c instanceof JoinResultControl) {
            return (JoinResultControl)c;
        }
        return new JoinResultControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_JOIN_RESULT.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("JoinResultControl(resultCode='");
        buffer.append(this.resultCode.getName());
        buffer.append("', diagnosticMessage='");
        if (this.diagnosticMessage != null) {
            buffer.append(this.diagnosticMessage);
        }
        buffer.append("', matchedDN='");
        if (this.matchedDN != null) {
            buffer.append(this.matchedDN);
        }
        buffer.append("', referralURLs={");
        final Iterator<String> refIterator = this.referralURLs.iterator();
        while (refIterator.hasNext()) {
            buffer.append(refIterator.next());
            if (refIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, joinResults={");
        final Iterator<JoinedEntry> entryIterator = this.joinResults.iterator();
        while (entryIterator.hasNext()) {
            entryIterator.next().toString(buffer);
            if (entryIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
