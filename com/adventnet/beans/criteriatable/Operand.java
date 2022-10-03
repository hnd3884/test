package com.adventnet.beans.criteriatable;

import java.io.Serializable;

public interface Operand extends Cloneable, Serializable
{
    Operand and(final Operand p0);
    
    Operand or(final Operand p0);
    
    Operand negate();
    
    boolean isNegated();
    
    String getString();
    
    boolean isGroupStartsBeforeThis();
    
    boolean isGroupEndsAfterThis();
    
    void setGroupStartsBeforeThis(final boolean p0);
    
    void setGroupEndsAfterThis(final boolean p0);
}
