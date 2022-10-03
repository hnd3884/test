package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import java.util.Collections;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collection;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ReadOnlyEntry;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JoinedEntry extends ReadOnlyEntry
{
    private static final long serialVersionUID = -6519864521813773703L;
    private final List<JoinedEntry> nestedJoinResults;
    
    public JoinedEntry(final Entry entry, final List<JoinedEntry> nestedJoinResults) {
        this(entry.getDN(), entry.getAttributes(), nestedJoinResults);
    }
    
    public JoinedEntry(final String dn, final Collection<Attribute> attributes, final List<JoinedEntry> nestedJoinResults) {
        super(dn, attributes);
        if (nestedJoinResults == null) {
            this.nestedJoinResults = Collections.emptyList();
        }
        else {
            this.nestedJoinResults = Collections.unmodifiableList((List<? extends JoinedEntry>)nestedJoinResults);
        }
    }
    
    ASN1Element encode() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1OctetString(this.getDN()));
        final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(20);
        for (final Attribute a : this.getAttributes()) {
            attrElements.add(a.encode());
        }
        elements.add(new ASN1Sequence(attrElements));
        if (!this.nestedJoinResults.isEmpty()) {
            final ArrayList<ASN1Element> nestedElements = new ArrayList<ASN1Element>(this.nestedJoinResults.size());
            for (final JoinedEntry je : this.nestedJoinResults) {
                nestedElements.add(je.encode());
            }
            elements.add(new ASN1Sequence(nestedElements));
        }
        return new ASN1Sequence(elements);
    }
    
    static JoinedEntry decode(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final String dn = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            final ASN1Element[] attrElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
            final ArrayList<Attribute> attrs = new ArrayList<Attribute>(attrElements.length);
            for (final ASN1Element e : attrElements) {
                attrs.add(Attribute.decode(ASN1Sequence.decodeAsSequence(e)));
            }
            ArrayList<JoinedEntry> nestedJoinResults;
            if (elements.length == 3) {
                final ASN1Element[] nestedElements = ASN1Sequence.decodeAsSequence(elements[2]).elements();
                nestedJoinResults = new ArrayList<JoinedEntry>(nestedElements.length);
                for (final ASN1Element e2 : nestedElements) {
                    nestedJoinResults.add(decode(e2));
                }
            }
            else {
                nestedJoinResults = new ArrayList<JoinedEntry>(0);
            }
            return new JoinedEntry(dn, attrs, nestedJoinResults);
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOINED_ENTRY_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
    }
    
    public List<JoinedEntry> getNestedJoinResults() {
        return this.nestedJoinResults;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("JoinedEntry(dn='");
        buffer.append(this.getDN());
        buffer.append("', attributes={");
        final Iterator<Attribute> attrIterator = this.getAttributes().iterator();
        while (attrIterator.hasNext()) {
            attrIterator.next().toString(buffer);
            if (attrIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, nestedJoinResults={");
        final Iterator<JoinedEntry> entryIterator = this.nestedJoinResults.iterator();
        while (entryIterator.hasNext()) {
            entryIterator.next().toString(buffer);
            if (entryIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
