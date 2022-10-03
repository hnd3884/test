package sun.corba;

import com.sun.corba.se.impl.io.ValueUtility;
import sun.misc.JavaOISAccess;
import java.lang.reflect.Method;
import sun.misc.Unsafe;

public class SharedSecrets
{
    private static final Unsafe unsafe;
    private static JavaCorbaAccess javaCorbaAccess;
    private static final Method getJavaOISAccessMethod;
    private static JavaOISAccess javaOISAccess;
    
    public static JavaOISAccess getJavaOISAccess() {
        if (SharedSecrets.javaOISAccess == null) {
            try {
                SharedSecrets.javaOISAccess = (JavaOISAccess)SharedSecrets.getJavaOISAccessMethod.invoke(null, new Object[0]);
            }
            catch (final Exception ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }
        return SharedSecrets.javaOISAccess;
    }
    
    public static JavaCorbaAccess getJavaCorbaAccess() {
        if (SharedSecrets.javaCorbaAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(ValueUtility.class);
        }
        return SharedSecrets.javaCorbaAccess;
    }
    
    public static void setJavaCorbaAccess(final JavaCorbaAccess javaCorbaAccess) {
        SharedSecrets.javaCorbaAccess = javaCorbaAccess;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        try {
            getJavaOISAccessMethod = Class.forName("sun.misc.SharedSecrets").getMethod("getJavaOISAccess", (Class<?>[])new Class[0]);
        }
        catch (final Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}
