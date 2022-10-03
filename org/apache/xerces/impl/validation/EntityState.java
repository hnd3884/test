package org.apache.xerces.impl.validation;

public interface EntityState
{
    boolean isEntityDeclared(final String p0);
    
    boolean isEntityUnparsed(final String p0);
}
