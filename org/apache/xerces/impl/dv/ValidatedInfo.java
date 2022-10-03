package org.apache.xerces.impl.dv;

import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.xs.util.ShortListImpl;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSValue;

public class ValidatedInfo implements XSValue
{
    public String normalizedValue;
    public Object actualValue;
    public short actualValueType;
    public XSSimpleType actualType;
    public XSSimpleType memberType;
    public XSSimpleType[] memberTypes;
    public ShortList itemValueTypes;
    
    public void reset() {
        this.normalizedValue = null;
        this.actualValue = null;
        this.actualValueType = 45;
        this.actualType = null;
        this.memberType = null;
        this.memberTypes = null;
        this.itemValueTypes = null;
    }
    
    public String stringValue() {
        if (this.actualValue == null) {
            return this.normalizedValue;
        }
        return this.actualValue.toString();
    }
    
    public static boolean isComparable(final ValidatedInfo validatedInfo, final ValidatedInfo validatedInfo2) {
        final short convertToPrimitiveKind = convertToPrimitiveKind(validatedInfo.actualValueType);
        final short convertToPrimitiveKind2 = convertToPrimitiveKind(validatedInfo2.actualValueType);
        if (convertToPrimitiveKind != convertToPrimitiveKind2) {
            return (convertToPrimitiveKind == 1 && convertToPrimitiveKind2 == 2) || (convertToPrimitiveKind == 2 && convertToPrimitiveKind2 == 1);
        }
        if (convertToPrimitiveKind == 44 || convertToPrimitiveKind == 43) {
            final ShortList itemValueTypes = validatedInfo.itemValueTypes;
            final ShortList itemValueTypes2 = validatedInfo2.itemValueTypes;
            final int n = (itemValueTypes != null) ? itemValueTypes.getLength() : 0;
            if (n != ((itemValueTypes2 != null) ? itemValueTypes2.getLength() : 0)) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                final short convertToPrimitiveKind3 = convertToPrimitiveKind(itemValueTypes.item(i));
                final short convertToPrimitiveKind4 = convertToPrimitiveKind(itemValueTypes2.item(i));
                if (convertToPrimitiveKind3 != convertToPrimitiveKind4 && (convertToPrimitiveKind3 != 1 || convertToPrimitiveKind4 != 2) && (convertToPrimitiveKind3 != 2 || convertToPrimitiveKind4 != 1)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static short convertToPrimitiveKind(final short n) {
        if (n <= 20) {
            return n;
        }
        if (n <= 29) {
            return 2;
        }
        if (n <= 42) {
            return 4;
        }
        return n;
    }
    
    public Object getActualValue() {
        return this.actualValue;
    }
    
    public short getActualValueType() {
        return this.actualValueType;
    }
    
    public ShortList getListValueTypes() {
        return (this.itemValueTypes == null) ? ShortListImpl.EMPTY_LIST : this.itemValueTypes;
    }
    
    public XSObjectList getMemberTypeDefinitions() {
        if (this.memberTypes == null) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        return new XSObjectListImpl(this.memberTypes, this.memberTypes.length);
    }
    
    public String getNormalizedValue() {
        return this.normalizedValue;
    }
    
    public XSSimpleTypeDefinition getTypeDefinition() {
        return this.actualType;
    }
    
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return this.memberType;
    }
    
    public void copyFrom(final XSValue xsValue) {
        if (xsValue == null) {
            this.reset();
        }
        else if (xsValue instanceof ValidatedInfo) {
            final ValidatedInfo validatedInfo = (ValidatedInfo)xsValue;
            this.normalizedValue = validatedInfo.normalizedValue;
            this.actualValue = validatedInfo.actualValue;
            this.actualValueType = validatedInfo.actualValueType;
            this.actualType = validatedInfo.actualType;
            this.memberType = validatedInfo.memberType;
            this.memberTypes = validatedInfo.memberTypes;
            this.itemValueTypes = validatedInfo.itemValueTypes;
        }
        else {
            this.normalizedValue = xsValue.getNormalizedValue();
            this.actualValue = xsValue.getActualValue();
            this.actualValueType = xsValue.getActualValueType();
            this.actualType = (XSSimpleType)xsValue.getTypeDefinition();
            this.memberType = (XSSimpleType)xsValue.getMemberTypeDefinition();
            final XSSimpleType xsSimpleType = (this.memberType == null) ? this.actualType : this.memberType;
            if (xsSimpleType != null && xsSimpleType.getBuiltInKind() == 43) {
                final XSObjectList memberTypeDefinitions = xsValue.getMemberTypeDefinitions();
                this.memberTypes = new XSSimpleType[memberTypeDefinitions.getLength()];
                for (int i = 0; i < memberTypeDefinitions.getLength(); ++i) {
                    this.memberTypes[i] = (XSSimpleType)memberTypeDefinitions.get(i);
                }
            }
            else {
                this.memberTypes = null;
            }
            this.itemValueTypes = xsValue.getListValueTypes();
        }
    }
}
