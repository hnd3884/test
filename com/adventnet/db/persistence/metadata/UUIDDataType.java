package com.adventnet.db.persistence.metadata;

import java.util.List;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Operation;
import com.adventnet.ds.query.Function;
import java.util.UUID;
import java.lang.reflect.Array;

public class UUIDDataType implements DataTypeMetaInfo
{
    @Override
    public void validate(final Object value, final int maxLength, final int precision) {
        this.validate(value);
    }
    
    @Override
    public void validate(final Object value) {
        if (value != null) {
            if (value.getClass().isArray() && (value.getClass().getSimpleName().equals("String[]") || value.getClass().getSimpleName().equals("UUID[]"))) {
                for (int length = Array.getLength(value), i = 0; i < length; ++i) {
                    this.validate(Array.get(value, i));
                }
            }
            else if (value instanceof String) {
                final String str = String.valueOf(value);
                final String pattern = "[0-9A-Fa-f]{8}-([0-9A-Fa-f]{4}-){3}[0-9A-Fa-f]{12}";
                if (!str.matches(pattern)) {
                    throw new IllegalArgumentException("Value is not in UUID format");
                }
                UUID.fromString(str);
            }
            else {
                if (value instanceof Function) {
                    throw new IllegalArgumentException("UUID is not supported for Functions");
                }
                if (value instanceof Operation) {
                    throw new IllegalArgumentException("UUID is not supported for Operations");
                }
                if (value instanceof Column) {
                    return;
                }
                if (!(value instanceof UUID)) {
                    throw new IllegalArgumentException("The value for UUID type column should be of String type or UUID type");
                }
            }
        }
    }
    
    @Override
    public Object convert(final String value) {
        return UUID.fromString(value);
    }
    
    @Override
    public void validateCriteriaInput(final Column column, final Object value, final int comparator, final boolean caseSensitive) {
        final List<Integer> compList = new ArrayList<Integer>();
        compList.add(0);
        compList.add(1);
        compList.add(14);
        compList.add(15);
        compList.add(8);
        compList.add(9);
        compList.add(4);
        compList.add(5);
        compList.add(6);
        compList.add(7);
        if (!compList.contains(comparator)) {
            throw new IllegalArgumentException("Comparator is not supported");
        }
        if (column instanceof Function) {
            throw new IllegalArgumentException("UUID Column is not supported for Functions");
        }
        if (column instanceof Operation) {
            throw new IllegalArgumentException("UUID Column is not supported for Operations");
        }
        this.validate(value);
    }
    
    @Override
    public boolean matches(final int comparator, final Object lhsValue, final Object rhsValue, final boolean caseSensitive) {
        if (lhsValue == null) {
            throw new IllegalArgumentException("lhsValue should'nt be null");
        }
        if (rhsValue == null) {
            throw new IllegalArgumentException("rhsValue should'nt be null");
        }
        switch (comparator) {
            case 0: {
                return UUID.fromString(lhsValue.toString()).equals(UUID.fromString(rhsValue.toString()));
            }
            default: {
                throw new IllegalArgumentException("For UUID ,matches is supported only for QueryConstant EQUAL");
            }
        }
    }
    
    @Override
    public boolean isEncrypted() {
        return true;
    }
    
    @Override
    public boolean processInput() {
        return true;
    }
    
    @Override
    public boolean processCheckConstraint() {
        return false;
    }
    
    @Override
    public int getMaxLength(final int maxLength) {
        return maxLength;
    }
    
    @Override
    public int getPrecision(final int precision) {
        return precision;
    }
    
    @Override
    public boolean matches(final int comparator, final Object lhsValue, final Object rhsValue) {
        return false;
    }
    
    @Override
    public void validateAllowedValues(final AllowedValues allowedValue) {
        throw new IllegalArgumentException("Allowed Values are not supported for column type UUID");
    }
    
    @Override
    public void validateValueForAllowedValues(final AllowedValues allowedValue, final Object value) {
        throw new IllegalArgumentException("Allowed Values are not supported for column type UUID");
    }
    
    @Override
    public Object getValueHolder() {
        return null;
    }
    
    @Override
    public boolean isReferenceable() {
        return true;
    }
    
    @Override
    public List<String> referenceableTypes() {
        return null;
    }
    
    @Override
    public boolean isPartialIndexSupported() {
        return false;
    }
}
