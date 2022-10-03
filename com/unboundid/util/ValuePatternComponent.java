package com.unboundid.util;

import java.io.Serializable;

@NotExtensible
abstract class ValuePatternComponent implements Serializable
{
    private static final long serialVersionUID = -5740038096026337244L;
    
    abstract void append(final StringBuilder p0);
    
    abstract boolean supportsBackReference();
}
