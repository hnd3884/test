package javax.accessibility;

public interface AccessibleValue
{
    Number getCurrentAccessibleValue();
    
    boolean setCurrentAccessibleValue(final Number p0);
    
    Number getMinimumAccessibleValue();
    
    Number getMaximumAccessibleValue();
}
