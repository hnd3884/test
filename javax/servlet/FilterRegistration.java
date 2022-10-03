package javax.servlet;

import java.util.Collection;
import java.util.EnumSet;

public interface FilterRegistration extends Registration
{
    void addMappingForServletNames(final EnumSet<DispatcherType> p0, final boolean p1, final String... p2);
    
    Collection<String> getServletNameMappings();
    
    void addMappingForUrlPatterns(final EnumSet<DispatcherType> p0, final boolean p1, final String... p2);
    
    Collection<String> getUrlPatternMappings();
    
    public interface Dynamic extends FilterRegistration, Registration.Dynamic
    {
    }
}
