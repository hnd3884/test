package org.apache.axiom.soap.impl.common;

import org.apache.axiom.soap.SOAPHeaderBlock;

public class MURoleChecker extends RoleChecker
{
    public MURoleChecker(final String role) {
        super(role);
    }
    
    @Override
    public boolean checkHeader(final SOAPHeaderBlock header) {
        return header.getMustUnderstand() && super.checkHeader(header);
    }
}
