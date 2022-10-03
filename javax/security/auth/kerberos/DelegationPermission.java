package javax.security.auth.kerberos;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PermissionCollection;
import java.security.Permission;
import java.util.StringTokenizer;
import java.io.Serializable;
import java.security.BasicPermission;

public final class DelegationPermission extends BasicPermission implements Serializable
{
    private static final long serialVersionUID = 883133252142523922L;
    private transient String subordinate;
    private transient String service;
    
    public DelegationPermission(final String s) {
        super(s);
        this.init(s);
    }
    
    public DelegationPermission(final String s, final String s2) {
        super(s, s2);
        this.init(s);
    }
    
    private void init(final String s) {
        if (!s.startsWith("\"")) {
            throw new IllegalArgumentException("service principal [" + s + "] syntax invalid: improperly quoted");
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "\"", false);
        this.subordinate = stringTokenizer.nextToken();
        if (stringTokenizer.countTokens() == 2) {
            stringTokenizer.nextToken();
            this.service = stringTokenizer.nextToken();
        }
        else if (stringTokenizer.countTokens() > 0) {
            throw new IllegalArgumentException("service principal [" + stringTokenizer.nextToken() + "] syntax invalid: improperly quoted");
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof DelegationPermission)) {
            return false;
        }
        final DelegationPermission delegationPermission = (DelegationPermission)permission;
        return this.subordinate.equals(delegationPermission.subordinate) && this.service.equals(delegationPermission.service);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof DelegationPermission && this.implies((Permission)o));
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public PermissionCollection newPermissionCollection() {
        return new KrbDelegationPermissionCollection();
    }
    
    private synchronized void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
    }
    
    private synchronized void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(this.getName());
    }
}
