package javax.management.openmbean;

import java.util.Set;

public interface OpenMBeanParameterInfo
{
    String getDescription();
    
    String getName();
    
    OpenType<?> getOpenType();
    
    Object getDefaultValue();
    
    Set<?> getLegalValues();
    
    Comparable<?> getMinValue();
    
    Comparable<?> getMaxValue();
    
    boolean hasDefaultValue();
    
    boolean hasLegalValues();
    
    boolean hasMinValue();
    
    boolean hasMaxValue();
    
    boolean isValue(final Object p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
