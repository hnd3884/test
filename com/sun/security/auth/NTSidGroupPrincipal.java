package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;

@Exported
public class NTSidGroupPrincipal extends NTSid
{
    private static final long serialVersionUID = -1373347438636198229L;
    
    public NTSidGroupPrincipal(final String s) {
        super(s);
    }
    
    @Override
    public String toString() {
        return new MessageFormat(ResourcesMgr.getString("NTSidGroupPrincipal.name", "sun.security.util.AuthResources")).format(new Object[] { this.getName() });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof NTSidGroupPrincipal && super.equals(o)));
    }
}
