package sun.management;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import com.sun.management.VMOption;

class Flag
{
    private String name;
    private Object value;
    private VMOption.Origin origin;
    private boolean writeable;
    private boolean external;
    
    Flag(final String name, final Object o, final boolean writeable, final boolean external, final VMOption.Origin origin) {
        this.name = name;
        this.value = ((o == null) ? "" : o);
        this.origin = origin;
        this.writeable = writeable;
        this.external = external;
    }
    
    Object getValue() {
        return this.value;
    }
    
    boolean isWriteable() {
        return this.writeable;
    }
    
    boolean isExternal() {
        return this.external;
    }
    
    VMOption getVMOption() {
        return new VMOption(this.name, this.value.toString(), this.writeable, this.origin);
    }
    
    static Flag getFlag(final String s) {
        final List<Flag> flags = getFlags(new String[] { s }, 1);
        if (flags.isEmpty()) {
            return null;
        }
        return flags.get(0);
    }
    
    static List<Flag> getAllFlags() {
        return getFlags(null, getInternalFlagCount());
    }
    
    private static List<Flag> getFlags(final String[] array, final int n) {
        final Flag[] array2 = new Flag[n];
        getFlags(array, array2, n);
        final ArrayList list = new ArrayList();
        for (final Flag flag : array2) {
            if (flag != null) {
                list.add(flag);
            }
        }
        return list;
    }
    
    private static native String[] getAllFlagNames();
    
    private static native int getFlags(final String[] p0, final Flag[] p1, final int p2);
    
    private static native int getInternalFlagCount();
    
    static synchronized native void setLongValue(final String p0, final long p1);
    
    static synchronized native void setDoubleValue(final String p0, final double p1);
    
    static synchronized native void setBooleanValue(final String p0, final boolean p1);
    
    static synchronized native void setStringValue(final String p0, final String p1);
    
    private static native void initialize();
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("management");
                return null;
            }
        });
        initialize();
    }
}
