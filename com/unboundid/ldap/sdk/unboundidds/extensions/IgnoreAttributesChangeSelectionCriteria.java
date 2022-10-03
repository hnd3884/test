package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.util.Validator;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IgnoreAttributesChangeSelectionCriteria extends ChangelogBatchChangeSelectionCriteria
{
    static final byte TYPE_SELECTION_CRITERIA_IGNORE_ATTRIBUTES = -93;
    private final boolean ignoreOperationalAttributes;
    private final List<String> attributeNames;
    
    public IgnoreAttributesChangeSelectionCriteria(final boolean ignoreOperationalAttributes, final String... attributeNames) {
        this(ignoreOperationalAttributes, StaticUtils.toList(attributeNames));
    }
    
    public IgnoreAttributesChangeSelectionCriteria(final boolean ignoreOperationalAttributes, final Collection<String> attributeNames) {
        if (attributeNames == null || attributeNames.isEmpty()) {
            Validator.ensureTrue(ignoreOperationalAttributes);
            this.attributeNames = Collections.emptyList();
        }
        else {
            this.attributeNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(attributeNames));
        }
        this.ignoreOperationalAttributes = ignoreOperationalAttributes;
    }
    
    static IgnoreAttributesChangeSelectionCriteria decodeInnerElement(final ASN1Element innerElement) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(innerElement).elements();
            final ASN1Element[] attrElements = ASN1Sequence.decodeAsSequence(elements[0]).elements();
            final ArrayList<String> attrNames = new ArrayList<String>(attrElements.length);
            for (final ASN1Element e : attrElements) {
                attrNames.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
            }
            return new IgnoreAttributesChangeSelectionCriteria(ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue(), attrNames);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_IGNORE_ATTRS_CHANGE_SELECTION_CRITERIA_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public boolean ignoreOperationalAttributes() {
        return this.ignoreOperationalAttributes;
    }
    
    public List<String> getAttributeNames() {
        return this.attributeNames;
    }
    
    public ASN1Element encodeInnerElement() {
        final ArrayList<ASN1Element> attrNameElements = new ArrayList<ASN1Element>(this.attributeNames.size());
        for (final String s : this.attributeNames) {
            attrNameElements.add(new ASN1OctetString(s));
        }
        return new ASN1Sequence((byte)(-93), new ASN1Element[] { new ASN1Sequence(attrNameElements), new ASN1Boolean(this.ignoreOperationalAttributes) });
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IgnoreAttributesChangeSelectionCriteria(attributeNames={");
        final Iterator<String> iterator = this.attributeNames.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append("}, ignoreOperationalAttributes=");
        buffer.append(this.ignoreOperationalAttributes);
        buffer.append(')');
    }
}
