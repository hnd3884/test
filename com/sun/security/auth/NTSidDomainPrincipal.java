package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;

@Exported
public class NTSidDomainPrincipal extends NTSid
{
    private static final long serialVersionUID = 5247810785821650912L;
    
    public NTSidDomainPrincipal(final String s) {
        super(s);
    }
    
    @Override
    public String toString() {
        return new MessageFormat(ResourcesMgr.getString("NTSidDomainPrincipal.name", "sun.security.util.AuthResources")).format(new Object[] { this.getName() });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof NTSidDomainPrincipal && super.equals(o)));
    }
}
