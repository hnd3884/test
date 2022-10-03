package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported
public class NTSid implements Principal, Serializable
{
    private static final long serialVersionUID = 4412290580770249885L;
    private String sid;
    
    public NTSid(final String s) {
        if (s == null) {
            throw new NullPointerException(new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources")).format(new Object[] { "stringSid" }));
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException(ResourcesMgr.getString("Invalid.NTSid.value", "sun.security.util.AuthResources"));
        }
        this.sid = new String(s);
    }
    
    @Override
    public String getName() {
        return this.sid;
    }
    
    @Override
    public String toString() {
        return new MessageFormat(ResourcesMgr.getString("NTSid.name", "sun.security.util.AuthResources")).format(new Object[] { this.sid });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof NTSid && this.sid.equals(((NTSid)o).sid)));
    }
    
    @Override
    public int hashCode() {
        return this.sid.hashCode();
    }
}
