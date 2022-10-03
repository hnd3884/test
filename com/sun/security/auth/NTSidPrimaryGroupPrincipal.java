package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;

@Exported
public class NTSidPrimaryGroupPrincipal extends NTSid
{
    private static final long serialVersionUID = 8011978367305190527L;
    
    public NTSidPrimaryGroupPrincipal(final String s) {
        super(s);
    }
    
    @Override
    public String toString() {
        return new MessageFormat(ResourcesMgr.getString("NTSidPrimaryGroupPrincipal.name", "sun.security.util.AuthResources")).format(new Object[] { this.getName() });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof NTSidPrimaryGroupPrincipal && super.equals(o)));
    }
}
