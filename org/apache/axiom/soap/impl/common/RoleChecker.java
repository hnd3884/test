package org.apache.axiom.soap.impl.common;

import org.apache.axiom.soap.SOAPHeaderBlock;

public class RoleChecker implements Checker
{
    String role;
    
    public RoleChecker(final String role) {
        this.role = role;
    }
    
    public boolean checkHeader(final SOAPHeaderBlock header) {
        if (this.role == null) {
            return true;
        }
        final String thisRole = header.getRole();
        return this.role.equals(thisRole);
    }
}
