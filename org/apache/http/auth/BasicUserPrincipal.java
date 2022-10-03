package org.apache.http.auth;

import org.apache.http.util.LangUtils;
import org.apache.http.util.Args;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import java.io.Serializable;
import java.security.Principal;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class BasicUserPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -2266305184969850467L;
    private final String username;
    
    public BasicUserPrincipal(final String username) {
        Args.notNull((Object)username, "User name");
        this.username = username;
    }
    
    @Override
    public String getName() {
        return this.username;
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, (Object)this.username);
        return hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BasicUserPrincipal) {
            final BasicUserPrincipal that = (BasicUserPrincipal)o;
            if (LangUtils.equals((Object)this.username, (Object)that.username)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[principal: ");
        buffer.append(this.username);
        buffer.append("]");
        return buffer.toString();
    }
}
