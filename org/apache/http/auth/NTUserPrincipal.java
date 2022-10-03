package org.apache.http.auth;

import org.apache.http.util.LangUtils;
import java.util.Locale;
import org.apache.http.util.Args;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import java.io.Serializable;
import java.security.Principal;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NTUserPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -6870169797924406894L;
    private final String username;
    private final String domain;
    private final String ntname;
    
    public NTUserPrincipal(final String domain, final String username) {
        Args.notNull((Object)username, "User name");
        this.username = username;
        if (domain != null) {
            this.domain = domain.toUpperCase(Locale.ROOT);
        }
        else {
            this.domain = null;
        }
        if (this.domain != null && !this.domain.isEmpty()) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(this.domain);
            buffer.append('\\');
            buffer.append(this.username);
            this.ntname = buffer.toString();
        }
        else {
            this.ntname = this.username;
        }
    }
    
    @Override
    public String getName() {
        return this.ntname;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, (Object)this.username);
        hash = LangUtils.hashCode(hash, (Object)this.domain);
        return hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof NTUserPrincipal) {
            final NTUserPrincipal that = (NTUserPrincipal)o;
            if (LangUtils.equals((Object)this.username, (Object)that.username) && LangUtils.equals((Object)this.domain, (Object)that.domain)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.ntname;
    }
}
