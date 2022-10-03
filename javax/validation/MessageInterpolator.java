package javax.validation;

import javax.validation.metadata.ConstraintDescriptor;
import java.util.Locale;

public interface MessageInterpolator
{
    String interpolate(final String p0, final Context p1);
    
    String interpolate(final String p0, final Context p1, final Locale p2);
    
    public interface Context
    {
        ConstraintDescriptor<?> getConstraintDescriptor();
        
        Object getValidatedValue();
        
         <T> T unwrap(final Class<T> p0);
    }
}
