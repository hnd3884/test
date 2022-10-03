package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.SearchRequest;
import java.util.Collections;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.List;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchRequestProtocolOp implements ProtocolOp
{
    private static final long serialVersionUID = -8521750809606744181L;
    private final boolean typesOnly;
    private final DereferencePolicy derefPolicy;
    private final Filter filter;
    private final int sizeLimit;
    private final int timeLimit;
    private final List<String> attributes;
    private final SearchScope scope;
    private final String baseDN;
    
    public SearchRequestProtocolOp(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final List<String> attributes) {
        this.scope = scope;
        this.derefPolicy = derefPolicy;
        this.typesOnly = typesOnly;
        this.filter = filter;
        if (baseDN == null) {
            this.baseDN = "";
        }
        else {
            this.baseDN = baseDN;
        }
        if (sizeLimit > 0) {
            this.sizeLimit = sizeLimit;
        }
        else {
            this.sizeLimit = 0;
        }
        if (timeLimit > 0) {
            this.timeLimit = timeLimit;
        }
        else {
            this.timeLimit = 0;
        }
        if (attributes == null) {
            this.attributes = Collections.emptyList();
        }
        else {
            this.attributes = Collections.unmodifiableList((List<? extends String>)attributes);
        }
    }
    
    public SearchRequestProtocolOp(final SearchRequest request) {
        this.baseDN = request.getBaseDN();
        this.scope = request.getScope();
        this.derefPolicy = request.getDereferencePolicy();
        this.sizeLimit = request.getSizeLimit();
        this.timeLimit = request.getTimeLimitSeconds();
        this.typesOnly = request.typesOnly();
        this.filter = request.getFilter();
        this.attributes = request.getAttributeList();
    }
    
    SearchRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            reader.beginSequence();
            this.baseDN = reader.readString();
            this.scope = SearchScope.valueOf(reader.readEnumerated());
            this.derefPolicy = DereferencePolicy.valueOf(reader.readEnumerated());
            this.sizeLimit = reader.readInteger();
            this.timeLimit = reader.readInteger();
            this.typesOnly = reader.readBoolean();
            this.filter = Filter.readFrom(reader);
            final ArrayList<String> attrs = new ArrayList<String>(5);
            final ASN1StreamReaderSequence attrSequence = reader.beginSequence();
            while (attrSequence.hasMoreElements()) {
                attrs.add(reader.readString());
            }
            this.attributes = Collections.unmodifiableList((List<? extends String>)attrs);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_SEARCH_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public SearchScope getScope() {
        return this.scope;
    }
    
    public DereferencePolicy getDerefPolicy() {
        return this.derefPolicy;
    }
    
    public int getSizeLimit() {
        return this.sizeLimit;
    }
    
    public int getTimeLimit() {
        return this.timeLimit;
    }
    
    public boolean typesOnly() {
        return this.typesOnly;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public List<String> getAttributes() {
        return this.attributes;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 99;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(this.attributes.size());
        for (final String attribute : this.attributes) {
            attrElements.add(new ASN1OctetString(attribute));
        }
        return new ASN1Sequence((byte)99, new ASN1Element[] { new ASN1OctetString(this.baseDN), new ASN1Enumerated(this.scope.intValue()), new ASN1Enumerated(this.derefPolicy.intValue()), new ASN1Integer(this.sizeLimit), new ASN1Integer(this.timeLimit), new ASN1Boolean(this.typesOnly), this.filter.encode(), new ASN1Sequence(attrElements) });
    }
    
    public static SearchRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final String baseDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            final SearchScope scope = SearchScope.valueOf(ASN1Enumerated.decodeAsEnumerated(elements[1]).intValue());
            final DereferencePolicy derefPolicy = DereferencePolicy.valueOf(ASN1Enumerated.decodeAsEnumerated(elements[2]).intValue());
            final int sizeLimit = ASN1Integer.decodeAsInteger(elements[3]).intValue();
            final int timeLimit = ASN1Integer.decodeAsInteger(elements[4]).intValue();
            final boolean typesOnly = ASN1Boolean.decodeAsBoolean(elements[5]).booleanValue();
            final Filter filter = Filter.decode(elements[6]);
            final ASN1Element[] attrElements = ASN1Sequence.decodeAsSequence(elements[7]).elements();
            final ArrayList<String> attributes = new ArrayList<String>(attrElements.length);
            for (final ASN1Element e : attrElements) {
                attributes.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
            }
            return new SearchRequestProtocolOp(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_SEARCH_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)99);
        buffer.addOctetString(this.baseDN);
        buffer.addEnumerated(this.scope.intValue());
        buffer.addEnumerated(this.derefPolicy.intValue());
        buffer.addInteger(this.sizeLimit);
        buffer.addInteger(this.timeLimit);
        buffer.addBoolean(this.typesOnly);
        this.filter.writeTo(buffer);
        final ASN1BufferSequence attrSequence = buffer.beginSequence();
        for (final String s : this.attributes) {
            buffer.addOctetString(s);
        }
        attrSequence.end();
        opSequence.end();
    }
    
    public SearchRequest toSearchRequest(final Control... controls) {
        final String[] attrArray = new String[this.attributes.size()];
        this.attributes.toArray(attrArray);
        return new SearchRequest(null, controls, this.baseDN, this.scope, this.derefPolicy, this.sizeLimit, this.timeLimit, this.typesOnly, this.filter, attrArray);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SearchRequestProtocolOp(baseDN='");
        buffer.append(this.baseDN);
        buffer.append("', scope='");
        buffer.append(this.scope.toString());
        buffer.append("', derefPolicy='");
        buffer.append(this.derefPolicy.toString());
        buffer.append("', sizeLimit=");
        buffer.append(this.sizeLimit);
        buffer.append(", timeLimit=");
        buffer.append(this.timeLimit);
        buffer.append(", typesOnly=");
        buffer.append(this.typesOnly);
        buffer.append(", filter='");
        this.filter.toString(buffer);
        buffer.append("', attributes={");
        final Iterator<String> iterator = this.attributes.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append("})");
    }
}
