package org.apache.xerces.xs.datatypes;

import java.math.BigInteger;
import java.math.BigDecimal;

public interface XSDecimal
{
    BigDecimal getBigDecimal();
    
    BigInteger getBigInteger() throws NumberFormatException;
    
    long getLong() throws NumberFormatException;
    
    int getInt() throws NumberFormatException;
    
    short getShort() throws NumberFormatException;
    
    byte getByte() throws NumberFormatException;
}
