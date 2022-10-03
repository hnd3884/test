package sun.security.krb5.internal.rcache;

import java.util.Objects;

public class AuthTimeWithHash extends AuthTime implements Comparable<AuthTimeWithHash>
{
    final String hash;
    
    public AuthTimeWithHash(final String s, final String s2, final int n, final int n2, final String hash) {
        super(s, s2, n, n2);
        this.hash = hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthTimeWithHash)) {
            return false;
        }
        final AuthTimeWithHash authTimeWithHash = (AuthTimeWithHash)o;
        return Objects.equals(this.hash, authTimeWithHash.hash) && Objects.equals(this.client, authTimeWithHash.client) && Objects.equals(this.server, authTimeWithHash.server) && this.ctime == authTimeWithHash.ctime && this.cusec == authTimeWithHash.cusec;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.hash);
    }
    
    @Override
    public String toString() {
        return String.format("%d/%06d/%s/%s", this.ctime, this.cusec, this.hash, this.client);
    }
    
    @Override
    public int compareTo(final AuthTimeWithHash authTimeWithHash) {
        int n;
        if (this.ctime != authTimeWithHash.ctime) {
            n = Integer.compare(this.ctime, authTimeWithHash.ctime);
        }
        else if (this.cusec != authTimeWithHash.cusec) {
            n = Integer.compare(this.cusec, authTimeWithHash.cusec);
        }
        else {
            n = this.hash.compareTo(authTimeWithHash.hash);
        }
        return n;
    }
    
    public boolean isSameIgnoresHash(final AuthTime authTime) {
        return this.client.equals(authTime.client) && this.server.equals(authTime.server) && this.ctime == authTime.ctime && this.cusec == authTime.cusec;
    }
    
    @Override
    public byte[] encode(final boolean b) {
        String client;
        String s;
        if (b) {
            client = "";
            s = String.format("HASH:%s %d:%s %d:%s", this.hash, this.client.length(), this.client, this.server.length(), this.server);
        }
        else {
            client = this.client;
            s = this.server;
        }
        return this.encode0(client, s);
    }
}
