package com.adventnet.db.persistence.metadata;

import java.util.List;
import java.lang.reflect.Array;
import com.adventnet.ds.query.QueryConstants;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;

public class SerialDataType implements DataTypeMetaInfo
{
    @Override
    public Object convert(final String value) {
        if (value == null) {
            return null;
        }
        return Long.valueOf(value.toString());
    }
    
    @Override
    public void validateCriteriaInput(final Column column, final Object value, final int comparator, final boolean caseSensitive) {
        if (null == value) {
            return;
        }
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
            throw new IllegalArgumentException("Comparator " + comparator + " is not supported for SERIAL datatype.");
        }
        if (value == QueryConstants.PREPARED_STMT_CONST) {
            return;
        }
        if (value.getClass().isArray()) {
            for (int length = Array.getLength(value), i = 0; i < length; ++i) {
                this.validate(Array.get(value, i));
            }
        }
        else {
            this.validate(value);
        }
    }
    
    @Override
    public void validate(final Object value) {
        if (value instanceof Column) {
            return;
        }
        if (!(value instanceof Long) && !(value instanceof Integer)) {
            try {
                Long.valueOf(value.toString());
            }
            catch (final Exception e) {
                throw new IllegalArgumentException("Unable to convert given value " + value + " to long type.");
            }
        }
    }
    
    @Override
    public boolean matches(final int comparator, final Object lhsValue, final Object rhsValue, final boolean caseSensitive) {
        if (lhsValue == null) {
            throw new IllegalArgumentException("lhsValue shouldn't be null");
        }
        if (rhsValue == null) {
            throw new IllegalArgumentException("rhsValue shouldn't be null");
        }
        long rhsVal = 0L;
        long startVal = 0L;
        long endVal = 0L;
        if (comparator == 14 || comparator == 15) {
            if (!rhsValue.getClass().isArray()) {
                throw new IllegalArgumentException("rhsValue should be an array for comparator: " + comparator);
            }
            final Object[] rhsVals = (Object[])rhsValue;
            startVal = Long.valueOf(rhsVals[0].toString());
            endVal = Long.valueOf(rhsVals[1].toString());
        }
        else if (comparator == 9 || comparator == 8) {
            if (!rhsValue.getClass().isArray()) {
                throw new IllegalArgumentException("rhsValue should be an array for comparator: " + comparator);
            }
        }
        else {
            rhsVal = Long.valueOf(rhsValue.toString());
        }
        final long lhsVal = Long.valueOf(lhsValue.toString());
        switch (comparator) {
            case 0: {
                return lhsVal == rhsVal;
            }
            case 1: {
                return lhsVal != rhsVal;
            }
            case 14: {
                return startVal <= lhsVal && lhsVal <= endVal;
            }
            case 15: {
                return startVal > lhsVal || lhsVal > endVal;
            }
            case 8: {
                for (final Object rhsObject : (Object[])rhsValue) {
                    if (Long.valueOf(rhsObject.toString()) == lhsVal) {
                        return true;
                    }
                }
                return false;
            }
            case 9: {
                for (final Object rhsObject : (Object[])rhsValue) {
                    if (Long.valueOf(rhsObject.toString()) == lhsVal) {
                        return false;
                    }
                }
                return true;
            }
            case 5: {
                return lhsVal > rhsVal;
            }
            case 4: {
                return lhsVal >= rhsVal;
            }
            case 7: {
                return lhsVal < rhsVal;
            }
            case 6: {
                return lhsVal <= rhsVal;
            }
            default: {
                throw new IllegalArgumentException("For SERIAL ,matches is not supported for " + comparator);
            }
        }
    }
    
    @Override
    public boolean isEncrypted() {
        return false;
    }
    
    @Override
    public boolean processInput() {
        return false;
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
    public Object getValueHolder() {
        return new SerialValueHolder();
    }
    
    @Override
    public List<String> referenceableTypes() {
        final List<String> dataTypes = new ArrayList<String>();
        dataTypes.add("BIGINT");
        return dataTypes;
    }
    
    @Override
    public void validate(final Object value, final int maxLength, final int precision) {
        if (value != null && !(value instanceof SerialValueHolder)) {
            throw new IllegalArgumentException("Values for SERIAL datatype column are autogenerated.So, values cannot be set.");
        }
    }
    
    @Override
    public void validateAllowedValues(final AllowedValues allowedValue) {
        throw new IllegalArgumentException("Allowed values not supported for Serial DataType!");
    }
    
    @Override
    public void validateValueForAllowedValues(final AllowedValues allowedValue, final Object value) {
        throw new IllegalArgumentException("Allowed values not supported for Serial DataType!");
    }
    
    public class SerialValueHolder
    {
    }
}
