package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.SearchResultReference;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchResultReferenceProtocolOp implements ProtocolOp
{
    private static final long serialVersionUID = -1526778443581862609L;
    private final List<String> referralURLs;
    
    public SearchResultReferenceProtocolOp(final List<String> referralURLs) {
        this.referralURLs = Collections.unmodifiableList((List<? extends String>)referralURLs);
    }
    
    public SearchResultReferenceProtocolOp(final SearchResultReference reference) {
        this.referralURLs = StaticUtils.toList(reference.getReferralURLs());
    }
    
    SearchResultReferenceProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            final ArrayList<String> refs = new ArrayList<String>(5);
            final ASN1StreamReaderSequence refSequence = reader.beginSequence();
            while (refSequence.hasMoreElements()) {
                refs.add(reader.readString());
            }
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)refs);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_SEARCH_REFERENCE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public List<String> getReferralURLs() {
        return this.referralURLs;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 115;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ArrayList<ASN1Element> urlElements = new ArrayList<ASN1Element>(this.referralURLs.size());
        for (final String url : this.referralURLs) {
            urlElements.add(new ASN1OctetString(url));
        }
        return new ASN1Sequence((byte)115, urlElements);
    }
    
    public static SearchResultReferenceProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] urlElements = ASN1Sequence.decodeAsSequence(element).elements();
            final ArrayList<String> referralURLs = new ArrayList<String>(urlElements.length);
            for (final ASN1Element e : urlElements) {
                referralURLs.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
            }
            return new SearchResultReferenceProtocolOp(referralURLs);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_SEARCH_REFERENCE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)115);
        for (final String s : this.referralURLs) {
            buffer.addOctetString(s);
        }
        opSequence.end();
    }
    
    public SearchResultReference toSearchResultReference(final Control... controls) {
        final String[] referralArray = new String[this.referralURLs.size()];
        this.referralURLs.toArray(referralArray);
        return new SearchResultReference(referralArray, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SearchResultReferenceProtocolOp(referralURLs={");
        final Iterator<String> iterator = this.referralURLs.iterator();
        while (iterator.hasNext()) {
            buffer.append('\'');
            buffer.append(iterator.next());
            buffer.append('\'');
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append("})");
    }
}
