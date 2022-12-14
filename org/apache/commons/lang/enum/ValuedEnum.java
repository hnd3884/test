package org.apache.commons.lang.enum;

import org.apache.commons.lang.ClassUtils;
import java.util.Iterator;
import java.util.List;

public abstract class ValuedEnum extends Enum
{
    private static final long serialVersionUID = -7129650521543789085L;
    private final int iValue;
    
    protected ValuedEnum(final String name, final int value) {
        super(name);
        this.iValue = value;
    }
    
    protected static Enum getEnum(final Class enumClass, final int value) {
        if (enumClass == null) {
            throw new IllegalArgumentException("The Enum Class must not be null");
        }
        final List list = Enum.getEnumList(enumClass);
        final Iterator it = list.iterator();
        while (it.hasNext()) {
            final ValuedEnum enum1 = it.next();
            if (enum1.getValue() == value) {
                return enum1;
            }
        }
        return null;
    }
    
    public final int getValue() {
        return this.iValue;
    }
    
    public int compareTo(final Object other) {
        return this.iValue - ((ValuedEnum)other).iValue;
    }
    
    public String toString() {
        if (super.iToString == null) {
            final String shortName = ClassUtils.getShortClassName(this.getEnumClass());
            super.iToString = shortName + "[" + this.getName() + "=" + this.getValue() + "]";
        }
        return super.iToString;
    }
}
