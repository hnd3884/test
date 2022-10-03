package org.aopalliance.reflect;

public interface ProgramUnit
{
    UnitLocator getLocator();
    
    Metadata getMetadata(final Object p0);
    
    Metadata[] getMetadatas();
    
    void addMetadata(final Metadata p0);
    
    void removeMetadata(final Object p0);
}
