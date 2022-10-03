package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;

@Exported
public class NTSidUserPrincipal extends NTSid
{
    private static final long serialVersionUID = -5573239889517749525L;
    
    public NTSidUserPrincipal(final String s) {
        super(s);
    }
    
    @Override
    public String toString() {
        return new MessageFormat(ResourcesMgr.getString("NTSidUserPrincipal.name", "sun.security.util.AuthResources")).format(new Object[] { this.getName() });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof NTSidUserPrincipal && super.equals(o)));
    }
}
