package com.adventnet.beans.criteriatable;

import java.util.Hashtable;

public class ComparatorsTable extends Hashtable
{
    public ComparatorsTable() {
        final String[] array = { "contains", "does not contain", "equals", "does not equals", "begins with", "ends with" };
        final String[] array2 = { "is", "is not", "is before", "is after" };
        final String[] array3 = { "is", "is greater than", "is less than" };
        final String[] array4 = { "is", "is not" };
        this.put(Attribute.STRING_TYPE, array);
        this.put(Attribute.DATE_TYPE, array2);
        this.put(Attribute.INTEGER_TYPE, array3);
        this.put(Attribute.FLOAT_TYPE, array3);
        this.put(Attribute.DOUBLE_TYPE, array3);
        this.put(Attribute.LONG_TYPE, array3);
        this.put(Attribute.BOOLEAN_TYPE, array4);
        this.put(Attribute.OBJECT_TYPE, array);
    }
    
    public void setDefaultComparatorsForType(final Class clazz, final String[] array) {
        if (clazz != null && array != null) {
            this.put(clazz, array);
        }
    }
    
    public String[] getDefaultComparatorsByType(final Class clazz) {
        return this.get(clazz);
    }
}
