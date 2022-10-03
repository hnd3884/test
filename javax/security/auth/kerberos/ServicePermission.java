package javax.security.auth.kerberos;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PermissionCollection;
import java.io.Serializable;
import java.security.Permission;

public final class ServicePermission extends Permission implements Serializable
{
    private static final long serialVersionUID = -1227585031618624935L;
    private static final int INITIATE = 1;
    private static final int ACCEPT = 2;
    private static final int ALL = 3;
    private static final int NONE = 0;
    private transient int mask;
    private String actions;
    
    public ServicePermission(final String s, final String s2) {
        super(s);
        this.init(s, getMask(s2));
    }
    
    private void init(final String s, final int mask) {
        if (s == null) {
            throw new NullPointerException("service principal can't be null");
        }
        if ((mask & 0x3) != mask) {
            throw new IllegalArgumentException("invalid actions mask");
        }
        this.mask = mask;
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof ServicePermission)) {
            return false;
        }
        final ServicePermission servicePermission = (ServicePermission)permission;
        return (this.mask & servicePermission.mask) == servicePermission.mask && this.impliesIgnoreMask(servicePermission);
    }
    
    boolean impliesIgnoreMask(final ServicePermission servicePermission) {
        return this.getName().equals("*") || this.getName().equals(servicePermission.getName()) || (servicePermission.getName().startsWith("@") && this.getName().endsWith(servicePermission.getName()));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServicePermission)) {
            return false;
        }
        final ServicePermission servicePermission = (ServicePermission)o;
        return (this.mask & servicePermission.mask) == servicePermission.mask && this.getName().equals(servicePermission.getName());
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode() ^ this.mask;
    }
    
    private static String getActions(final int n) {
        final StringBuilder sb = new StringBuilder();
        int n2 = 0;
        if ((n & 0x1) == 0x1) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("initiate");
        }
        if ((n & 0x2) == 0x2) {
            if (n2 != 0) {
                sb.append(',');
            }
            sb.append("accept");
        }
        return sb.toString();
    }
    
    @Override
    public String getActions() {
        if (this.actions == null) {
            this.actions = getActions(this.mask);
        }
        return this.actions;
    }
    
    @Override
    public PermissionCollection newPermissionCollection() {
        return new KrbServicePermissionCollection();
    }
    
    int getMask() {
        return this.mask;
    }
    
    private static int getMask(final String s) {
        if (s == null) {
            throw new NullPointerException("action can't be null");
        }
        if (s.equals("")) {
            throw new IllegalArgumentException("action can't be empty");
        }
        int n = 0;
        final char[] charArray = s.toCharArray();
        if (charArray.length == 1 && charArray[0] == '-') {
            return n;
        }
        int n2;
        for (int i = charArray.length - 1; i != -1; i -= n2) {
            char c;
            while (i != -1 && ((c = charArray[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t')) {
                --i;
            }
            if (i >= 7 && (charArray[i - 7] == 'i' || charArray[i - 7] == 'I') && (charArray[i - 6] == 'n' || charArray[i - 6] == 'N') && (charArray[i - 5] == 'i' || charArray[i - 5] == 'I') && (charArray[i - 4] == 't' || charArray[i - 4] == 'T') && (charArray[i - 3] == 'i' || charArray[i - 3] == 'I') && (charArray[i - 2] == 'a' || charArray[i - 2] == 'A') && (charArray[i - 1] == 't' || charArray[i - 1] == 'T') && (charArray[i] == 'e' || charArray[i] == 'E')) {
                n2 = 8;
                n |= 0x1;
            }
            else {
                if (i < 5 || (charArray[i - 5] != 'a' && charArray[i - 5] != 'A') || (charArray[i - 4] != 'c' && charArray[i - 4] != 'C') || (charArray[i - 3] != 'c' && charArray[i - 3] != 'C') || (charArray[i - 2] != 'e' && charArray[i - 2] != 'E') || (charArray[i - 1] != 'p' && charArray[i - 1] != 'P') || (charArray[i] != 't' && charArray[i] != 'T')) {
                    throw new IllegalArgumentException("invalid permission: " + s);
                }
                n2 = 6;
                n |= 0x2;
            }
            for (int n3 = 0; i >= n2 && n3 == 0; --i) {
                switch (charArray[i - n2]) {
                    case ',': {
                        n3 = 1;
                        break;
                    }
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ': {
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("invalid permission: " + s);
                    }
                }
            }
        }
        return n;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.actions == null) {
            this.getActions();
        }
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(this.getName(), getMask(this.actions));
    }
}
