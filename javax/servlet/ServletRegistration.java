package javax.servlet;

import java.util.Collection;
import java.util.Set;

public interface ServletRegistration extends Registration
{
    Set<String> addMapping(final String... p0);
    
    Collection<String> getMappings();
    
    String getRunAsRole();
    
    public interface Dynamic extends ServletRegistration, Registration.Dynamic
    {
        void setLoadOnStartup(final int p0);
        
        Set<String> setServletSecurity(final ServletSecurityElement p0);
        
        void setMultipartConfig(final MultipartConfigElement p0);
        
        void setRunAsRole(final String p0);
    }
}
