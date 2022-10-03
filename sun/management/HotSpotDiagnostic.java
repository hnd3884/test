package sun.management;

import javax.management.ObjectName;
import java.util.Iterator;
import java.util.ArrayList;
import com.sun.management.VMOption;
import java.util.List;
import java.io.IOException;
import java.security.AccessController;
import com.sun.management.HotSpotDiagnosticMXBean;

public class HotSpotDiagnostic implements HotSpotDiagnosticMXBean
{
    @Override
    public void dumpHeap(final String s, final boolean b) throws IOException {
        if (!AccessController.doPrivileged(() -> Boolean.parseBoolean(System.getProperty(s2, "false"))) && !s.endsWith(".hprof")) {
            throw new IllegalArgumentException("heapdump file must have .hprof extention");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkWrite(s);
            Util.checkControlAccess();
        }
        this.dumpHeap0(s, b);
    }
    
    private native void dumpHeap0(final String p0, final boolean p1) throws IOException;
    
    @Override
    public List<VMOption> getDiagnosticOptions() {
        final List<Flag> allFlags = Flag.getAllFlags();
        final ArrayList list = new ArrayList();
        for (final Flag flag : allFlags) {
            if (flag.isWriteable() && flag.isExternal()) {
                list.add(flag.getVMOption());
            }
        }
        return list;
    }
    
    @Override
    public VMOption getVMOption(final String s) {
        if (s == null) {
            throw new NullPointerException("name cannot be null");
        }
        final Flag flag = Flag.getFlag(s);
        if (flag == null) {
            throw new IllegalArgumentException("VM option \"" + s + "\" does not exist");
        }
        return flag.getVMOption();
    }
    
    @Override
    public void setVMOption(final String s, final String s2) {
        if (s == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (s2 == null) {
            throw new NullPointerException("value cannot be null");
        }
        Util.checkControlAccess();
        final Flag flag = Flag.getFlag(s);
        if (flag == null) {
            throw new IllegalArgumentException("VM option \"" + s + "\" does not exist");
        }
        if (!flag.isWriteable()) {
            throw new IllegalArgumentException("VM Option \"" + s + "\" is not writeable");
        }
        final Object value = flag.getValue();
        if (value instanceof Long) {
            try {
                Flag.setLongValue(s, Long.parseLong(s2));
                return;
            }
            catch (final NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid value: VM Option \"" + s + "\" expects numeric value", ex);
            }
        }
        if (value instanceof Double) {
            try {
                Flag.setDoubleValue(s, Double.parseDouble(s2));
                return;
            }
            catch (final NumberFormatException ex2) {
                throw new IllegalArgumentException("Invalid value: VM Option \"" + s + "\" expects numeric value", ex2);
            }
        }
        if (value instanceof Boolean) {
            if (!s2.equalsIgnoreCase("true") && !s2.equalsIgnoreCase("false")) {
                throw new IllegalArgumentException("Invalid value: VM Option \"" + s + "\" expects \"true\" or \"false\".");
            }
            Flag.setBooleanValue(s, Boolean.parseBoolean(s2));
        }
        else {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("VM Option \"" + s + "\" is of an unsupported type: " + value.getClass().getName());
            }
            Flag.setStringValue(s, s2);
        }
    }
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("com.sun.management:type=HotSpotDiagnostic");
    }
}
