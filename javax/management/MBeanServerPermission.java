package javax.management;

import java.security.PermissionCollection;
import java.security.Permission;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;

public class MBeanServerPermission extends BasicPermission
{
    private static final long serialVersionUID = -5661980843569388590L;
    private static final int CREATE = 0;
    private static final int FIND = 1;
    private static final int NEW = 2;
    private static final int RELEASE = 3;
    private static final int N_NAMES = 4;
    private static final String[] names;
    private static final int CREATE_MASK = 1;
    private static final int FIND_MASK = 2;
    private static final int NEW_MASK = 4;
    private static final int RELEASE_MASK = 8;
    private static final int ALL_MASK = 15;
    private static final String[] canonicalNames;
    transient int mask;
    
    public MBeanServerPermission(final String s) {
        this(s, null);
    }
    
    public MBeanServerPermission(final String s, final String s2) {
        super(getCanonicalName(parseMask(s)), s2);
        this.mask = parseMask(s);
        if (s2 != null && s2.length() > 0) {
            throw new IllegalArgumentException("MBeanServerPermission actions must be null: " + s2);
        }
    }
    
    MBeanServerPermission(final int n) {
        super(getCanonicalName(n));
        this.mask = impliedMask(n);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.mask = parseMask(this.getName());
    }
    
    static int simplifyMask(int n) {
        if ((n & 0x1) != 0x0) {
            n &= 0xFFFFFFFB;
        }
        return n;
    }
    
    static int impliedMask(int n) {
        if ((n & 0x1) != 0x0) {
            n |= 0x4;
        }
        return n;
    }
    
    static String getCanonicalName(int simplifyMask) {
        if (simplifyMask == 15) {
            return "*";
        }
        simplifyMask = simplifyMask(simplifyMask);
        synchronized (MBeanServerPermission.canonicalNames) {
            if (MBeanServerPermission.canonicalNames[simplifyMask] == null) {
                MBeanServerPermission.canonicalNames[simplifyMask] = makeCanonicalName(simplifyMask);
            }
        }
        return MBeanServerPermission.canonicalNames[simplifyMask];
    }
    
    private static String makeCanonicalName(final int n) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; ++i) {
            if ((n & 1 << i) != 0x0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(MBeanServerPermission.names[i]);
            }
        }
        return sb.toString().intern();
    }
    
    private static int parseMask(String trim) {
        if (trim == null) {
            throw new NullPointerException("MBeanServerPermission: target name can't be null");
        }
        trim = trim.trim();
        if (trim.equals("*")) {
            return 15;
        }
        if (trim.indexOf(44) < 0) {
            return impliedMask(1 << nameIndex(trim.trim()));
        }
        int n = 0;
        final StringTokenizer stringTokenizer = new StringTokenizer(trim, ",");
        while (stringTokenizer.hasMoreTokens()) {
            n |= 1 << nameIndex(stringTokenizer.nextToken().trim());
        }
        return impliedMask(n);
    }
    
    private static int nameIndex(final String s) throws IllegalArgumentException {
        for (int i = 0; i < 4; ++i) {
            if (MBeanServerPermission.names[i].equals(s)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid MBeanServerPermission name: \"" + s + "\"");
    }
    
    @Override
    public int hashCode() {
        return this.mask;
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof MBeanServerPermission)) {
            return false;
        }
        final MBeanServerPermission mBeanServerPermission = (MBeanServerPermission)permission;
        return (this.mask & mBeanServerPermission.mask) == mBeanServerPermission.mask;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof MBeanServerPermission && this.mask == ((MBeanServerPermission)o).mask);
    }
    
    @Override
    public PermissionCollection newPermissionCollection() {
        return new MBeanServerPermissionCollection();
    }
    
    static {
        names = new String[] { "createMBeanServer", "findMBeanServer", "newMBeanServer", "releaseMBeanServer" };
        canonicalNames = new String[16];
    }
}
