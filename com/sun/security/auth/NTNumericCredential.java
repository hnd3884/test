package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;

@Exported
public class NTNumericCredential
{
    private long impersonationToken;
    
    public NTNumericCredential(final long impersonationToken) {
        this.impersonationToken = impersonationToken;
    }
    
    public long getToken() {
        return this.impersonationToken;
    }
    
    @Override
    public String toString() {
        return new MessageFormat(ResourcesMgr.getString("NTNumericCredential.name", "sun.security.util.AuthResources")).format(new Object[] { Long.toString(this.impersonationToken) });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof NTNumericCredential && this.impersonationToken == ((NTNumericCredential)o).getToken()));
    }
    
    @Override
    public int hashCode() {
        return (int)this.impersonationToken;
    }
}
