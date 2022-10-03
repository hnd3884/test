package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.Debug;
import java.util.LinkedHashSet;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Enumerated;
import java.util.Collection;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.UUID;
import java.util.Set;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UniquenessRequestControl extends Control
{
    public static final String UNIQUENESS_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.52";
    private static final byte TYPE_UNIQUENESS_ID = Byte.MIN_VALUE;
    private static final byte TYPE_ATTRIBUTE_TYPES = -95;
    private static final byte TYPE_MULTIPLE_ATTRIBUTE_BEHAVIOR = -126;
    private static final byte TYPE_BASE_DN = -125;
    private static final byte TYPE_FILTER = -92;
    private static final byte TYPE_PREVENT_CONFLICTS_WITH_SOFT_DELETED_ENTRIES = -123;
    private static final byte TYPE_PRE_COMMIT_VALIDATION_LEVEL = -122;
    private static final byte TYPE_POST_COMMIT_VALIDATION_LEVEL = -121;
    private static final long serialVersionUID = 7976218379635922852L;
    private final boolean preventConflictsWithSoftDeletedEntries;
    private final Filter filter;
    private final Set<String> attributeTypes;
    private final String baseDN;
    private final String uniquenessID;
    private final UniquenessMultipleAttributeBehavior multipleAttributeBehavior;
    private final UniquenessValidationLevel postCommitValidationLevel;
    private final UniquenessValidationLevel preCommitValidationLevel;
    
    public UniquenessRequestControl(final boolean isCritical, final String uniquenessID, final UniquenessRequestControlProperties properties) throws LDAPException {
        this((uniquenessID == null) ? UUID.randomUUID().toString() : uniquenessID, properties, isCritical);
    }
    
    private UniquenessRequestControl(final String uniquenessID, final UniquenessRequestControlProperties properties, final boolean isCritical) throws LDAPException {
        super("1.3.6.1.4.1.30221.2.5.52", isCritical, encodeValue(uniquenessID, properties));
        Validator.ensureNotNull(uniquenessID);
        this.uniquenessID = uniquenessID;
        this.attributeTypes = properties.getAttributeTypes();
        this.multipleAttributeBehavior = properties.getMultipleAttributeBehavior();
        this.baseDN = properties.getBaseDN();
        this.filter = properties.getFilter();
        this.preventConflictsWithSoftDeletedEntries = properties.preventConflictsWithSoftDeletedEntries();
        this.preCommitValidationLevel = properties.getPreCommitValidationLevel();
        this.postCommitValidationLevel = properties.getPostCommitValidationLevel();
        if (this.attributeTypes.isEmpty() && this.filter == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_NO_ATTRS_OR_FILTER.get());
        }
    }
    
    private static ASN1OctetString encodeValue(final String uniquenessID, final UniquenessRequestControlProperties properties) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(8);
        elements.add(new ASN1OctetString((byte)(-128), uniquenessID));
        final Set<String> attributeTypes = properties.getAttributeTypes();
        if (!attributeTypes.isEmpty()) {
            final ArrayList<ASN1Element> attributeTypeElements = new ArrayList<ASN1Element>(attributeTypes.size());
            for (final String attributeType : attributeTypes) {
                attributeTypeElements.add(new ASN1OctetString(attributeType));
            }
            elements.add(new ASN1Set((byte)(-95), attributeTypeElements));
        }
        final UniquenessMultipleAttributeBehavior multipleAttributeBehavior = properties.getMultipleAttributeBehavior();
        if (multipleAttributeBehavior != UniquenessMultipleAttributeBehavior.UNIQUE_WITHIN_EACH_ATTRIBUTE) {
            elements.add(new ASN1Enumerated((byte)(-126), multipleAttributeBehavior.intValue()));
        }
        final String baseDN = properties.getBaseDN();
        if (baseDN != null) {
            elements.add(new ASN1OctetString((byte)(-125), baseDN));
        }
        final Filter filter = properties.getFilter();
        if (filter != null) {
            elements.add(new ASN1Element((byte)(-92), filter.encode().encode()));
        }
        if (properties.preventConflictsWithSoftDeletedEntries()) {
            elements.add(new ASN1Boolean((byte)(-123), true));
        }
        final UniquenessValidationLevel preCommitValidationLevel = properties.getPreCommitValidationLevel();
        if (preCommitValidationLevel != UniquenessValidationLevel.ALL_SUBTREE_VIEWS) {
            elements.add(new ASN1Enumerated((byte)(-122), preCommitValidationLevel.intValue()));
        }
        final UniquenessValidationLevel postCommitValidationLevel = properties.getPostCommitValidationLevel();
        if (postCommitValidationLevel != UniquenessValidationLevel.ALL_SUBTREE_VIEWS) {
            elements.add(new ASN1Enumerated((byte)(-121), postCommitValidationLevel.intValue()));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public UniquenessRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_DECODE_NO_VALUE.get());
        }
        try {
            boolean decodedPreventSoftDeletedConflicts = false;
            Filter decodedFilter = null;
            Set<String> decodedAttributeTypes = Collections.emptySet();
            String decodedBaseDN = null;
            String decodedUniquenessID = null;
            UniquenessMultipleAttributeBehavior decodedMultipleAttributeBehavior = UniquenessMultipleAttributeBehavior.UNIQUE_WITHIN_EACH_ATTRIBUTE;
            UniquenessValidationLevel decodedPreCommitLevel = UniquenessValidationLevel.ALL_SUBTREE_VIEWS;
            UniquenessValidationLevel decodedPostCommitLevel = UniquenessValidationLevel.ALL_SUBTREE_VIEWS;
            final ASN1Element[] arr$;
            final ASN1Element[] elements = arr$ = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            for (final ASN1Element e : arr$) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        decodedUniquenessID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -95: {
                        final ASN1Element[] atElements = ASN1Set.decodeAsSet(e).elements();
                        final LinkedHashSet<String> atNames = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(atElements.length));
                        for (final ASN1Element atElement : atElements) {
                            atNames.add(ASN1OctetString.decodeAsOctetString(atElement).stringValue());
                        }
                        decodedAttributeTypes = Collections.unmodifiableSet((Set<? extends String>)atNames);
                        break;
                    }
                    case -126: {
                        final int mabIntValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        decodedMultipleAttributeBehavior = UniquenessMultipleAttributeBehavior.valueOf(mabIntValue);
                        if (decodedMultipleAttributeBehavior == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_DECODE_UNKNOWN_MULTIPLE_ATTR_BEHAVIOR.get(mabIntValue));
                        }
                        break;
                    }
                    case -125: {
                        decodedBaseDN = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -92: {
                        decodedFilter = Filter.decode(ASN1Element.decode(e.getValue()));
                        break;
                    }
                    case -123: {
                        decodedPreventSoftDeletedConflicts = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -122: {
                        final int preCommitIntValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        decodedPreCommitLevel = UniquenessValidationLevel.valueOf(preCommitIntValue);
                        if (decodedPreCommitLevel == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_DECODE_UNKNOWN_PRE_COMMIT_LEVEL.get(preCommitIntValue));
                        }
                        break;
                    }
                    case -121: {
                        final int postCommitIntValue = ASN1Enumerated.decodeAsEnumerated(e).intValue();
                        decodedPostCommitLevel = UniquenessValidationLevel.valueOf(postCommitIntValue);
                        if (decodedPostCommitLevel == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_DECODE_UNKNOWN_POST_COMMIT_LEVEL.get(postCommitIntValue));
                        }
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_DECODE_UNKNOWN_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            if (decodedUniquenessID == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_MISSING_UNIQUENESS_ID.get());
            }
            if (decodedAttributeTypes.isEmpty() && decodedFilter == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_NO_ATTRS_OR_FILTER.get());
            }
            this.uniquenessID = decodedUniquenessID;
            this.attributeTypes = decodedAttributeTypes;
            this.multipleAttributeBehavior = decodedMultipleAttributeBehavior;
            this.baseDN = decodedBaseDN;
            this.filter = decodedFilter;
            this.preventConflictsWithSoftDeletedEntries = decodedPreventSoftDeletedConflicts;
            this.preCommitValidationLevel = decodedPreCommitLevel;
            this.postCommitValidationLevel = decodedPostCommitLevel;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_REQ_DECODE_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public String getUniquenessID() {
        return this.uniquenessID;
    }
    
    public Set<String> getAttributeTypes() {
        return this.attributeTypes;
    }
    
    public UniquenessMultipleAttributeBehavior getMultipleAttributeBehavior() {
        return this.multipleAttributeBehavior;
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public boolean preventConflictsWithSoftDeletedEntries() {
        return this.preventConflictsWithSoftDeletedEntries;
    }
    
    public UniquenessValidationLevel getPreCommitValidationLevel() {
        return this.preCommitValidationLevel;
    }
    
    public UniquenessValidationLevel getPostCommitValidationLevel() {
        return this.postCommitValidationLevel;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_UNIQUENESS_REQ_CONTROL_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UniquenessRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", uniquenessID='");
        buffer.append(this.uniquenessID);
        buffer.append("', attributeTypes={");
        final Iterator<String> attributeTypesIterator = this.attributeTypes.iterator();
        while (attributeTypesIterator.hasNext()) {
            buffer.append('\'');
            buffer.append(attributeTypesIterator.next());
            buffer.append('\'');
            if (attributeTypesIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, multipleAttributeBehavior=");
        buffer.append(this.multipleAttributeBehavior);
        if (this.baseDN != null) {
            buffer.append(", baseDN='");
            buffer.append(this.baseDN);
            buffer.append('\'');
        }
        if (this.filter != null) {
            buffer.append(", filter='");
            buffer.append(this.filter);
            buffer.append('\'');
        }
        buffer.append(", preventConflictsWithSoftDeletedEntries=");
        buffer.append(this.preventConflictsWithSoftDeletedEntries);
        buffer.append(", preCommitValidationLevel=");
        buffer.append(this.preCommitValidationLevel);
        buffer.append(", postCommitValidationLevel=");
        buffer.append(this.postCommitValidationLevel);
        buffer.append(')');
    }
}
