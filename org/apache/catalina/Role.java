package org.apache.catalina;

import java.security.Principal;

public interface Role extends Principal
{
    String getDescription();
    
    void setDescription(final String p0);
    
    String getRolename();
    
    void setRolename(final String p0);
    
    UserDatabase getUserDatabase();
}
