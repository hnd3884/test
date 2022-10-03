package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AllAttributesChangeSelectionCriteria extends ChangelogBatchChangeSelectionCriteria
{
    static final byte TYPE_SELECTION_CRITERIA_ALL_ATTRIBUTES = -94;
    private final List<String> attributeNames;
    
    public AllAttributesChangeSelectionCriteria(final String... attributeNames) {
        this(StaticUtils.toList(attributeNames));
    }
    
    public AllAttributesChangeSelectionCriteria(final Collection<String> attributeNames) {
        Validator.ensureNotNull(attributeNames);
        Validator.ensureFalse(attributeNames.isEmpty());
        this.attributeNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(attributeNames));
    }
    
    static AllAttributesChangeSelectionCriteria decodeInnerElement(final ASN1Element innerElement) throws LDAPException {
        try {
            final ASN1Element[] attrElements = ASN1Sequence.decodeAsSequence(innerElement).elements();
            final ArrayList<String> attrNames = new ArrayList<String>(attrElements.length);
            for (final ASN1Element e : attrElements) {
                attrNames.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
            }
            return new AllAttributesChangeSelectionCriteria(attrNames);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_ALL_ATTRS_CHANGE_SELECTION_CRITERIA_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public List<String> getAttributeNames() {
        return this.attributeNames;
    }
    
    public ASN1Element encodeInnerElement() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(this.attributeNames.size());
        for (final String s : this.attributeNames) {
            elements.add(new ASN1OctetString(s));
        }
        return new ASN1Sequence((byte)(-94), elements);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AllAttributesChangeSelectionCriteria(attributeNames={");
        final Iterator<String> iterator = this.attributeNames.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append("})");
    }
}
