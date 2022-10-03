package org.omg.CORBA;

import org.omg.CORBA.ORBPackage.InconsistentTypeCode;
import org.omg.CORBA.portable.OutputStream;
import java.lang.reflect.InvocationTargetException;
import org.omg.CORBA.ORBPackage.InvalidName;
import java.applet.Applet;
import com.sun.corba.se.impl.orb.ORBImpl;
import sun.reflect.misc.ReflectUtil;
import com.sun.corba.se.impl.orb.ORBSingleton;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class ORB
{
    private static final String ORBClassKey = "org.omg.CORBA.ORBClass";
    private static final String ORBSingletonClassKey = "org.omg.CORBA.ORBSingletonClass";
    private static ORB singleton;
    
    private static String getSystemProperty(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperty(s);
            }
        });
    }
    
    private static String getPropertyFromFile(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            private Properties getFileProperties(final String s) {
                try {
                    final File file = new File(s);
                    if (!file.exists()) {
                        return null;
                    }
                    final Properties properties = new Properties();
                    final FileInputStream fileInputStream = new FileInputStream(file);
                    try {
                        properties.load(fileInputStream);
                    }
                    finally {
                        fileInputStream.close();
                    }
                    return properties;
                }
                catch (final Exception ex) {
                    return null;
                }
            }
            
            @Override
            public Object run() {
                final Properties fileProperties = this.getFileProperties(System.getProperty("user.home") + File.separator + "orb.properties");
                if (fileProperties != null) {
                    final String property = fileProperties.getProperty(s);
                    if (property != null) {
                        return property;
                    }
                }
                final Properties fileProperties2 = this.getFileProperties(System.getProperty("java.home") + File.separator + "lib" + File.separator + "orb.properties");
                if (fileProperties2 == null) {
                    return null;
                }
                return fileProperties2.getProperty(s);
            }
        });
    }
    
    public static synchronized ORB init() {
        if (ORB.singleton == null) {
            String s = getSystemProperty("org.omg.CORBA.ORBSingletonClass");
            if (s == null) {
                s = getPropertyFromFile("org.omg.CORBA.ORBSingletonClass");
            }
            if (s == null || s.equals("com.sun.corba.se.impl.orb.ORBSingleton")) {
                ORB.singleton = new ORBSingleton();
            }
            else {
                ORB.singleton = create_impl(s);
            }
        }
        return ORB.singleton;
    }
    
    private static ORB create_impl(final String s) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        try {
            ReflectUtil.checkPackageAccess(s);
            return (ORB)Class.forName(s, true, classLoader).asSubclass(ORB.class).newInstance();
        }
        catch (final Throwable t) {
            final INITIALIZE initialize = new INITIALIZE("can't instantiate default ORB implementation " + s);
            initialize.initCause(t);
            throw initialize;
        }
    }
    
    public static ORB init(final String[] array, final Properties properties) {
        String s = null;
        if (properties != null) {
            s = properties.getProperty("org.omg.CORBA.ORBClass");
        }
        if (s == null) {
            s = getSystemProperty("org.omg.CORBA.ORBClass");
        }
        if (s == null) {
            s = getPropertyFromFile("org.omg.CORBA.ORBClass");
        }
        ORB create_impl;
        if (s == null || s.equals("com.sun.corba.se.impl.orb.ORBImpl")) {
            create_impl = new ORBImpl();
        }
        else {
            create_impl = create_impl(s);
        }
        create_impl.set_parameters(array, properties);
        return create_impl;
    }
    
    public static ORB init(final Applet applet, final Properties properties) {
        String s = applet.getParameter("org.omg.CORBA.ORBClass");
        if (s == null && properties != null) {
            s = properties.getProperty("org.omg.CORBA.ORBClass");
        }
        if (s == null) {
            s = getSystemProperty("org.omg.CORBA.ORBClass");
        }
        if (s == null) {
            s = getPropertyFromFile("org.omg.CORBA.ORBClass");
        }
        ORB create_impl;
        if (s == null || s.equals("com.sun.corba.se.impl.orb.ORBImpl")) {
            create_impl = new ORBImpl();
        }
        else {
            create_impl = create_impl(s);
        }
        create_impl.set_parameters(applet, properties);
        return create_impl;
    }
    
    protected abstract void set_parameters(final String[] p0, final Properties p1);
    
    protected abstract void set_parameters(final Applet p0, final Properties p1);
    
    public void connect(final org.omg.CORBA.Object object) {
        throw new NO_IMPLEMENT();
    }
    
    public void destroy() {
        throw new NO_IMPLEMENT();
    }
    
    public void disconnect(final org.omg.CORBA.Object object) {
        throw new NO_IMPLEMENT();
    }
    
    public abstract String[] list_initial_services();
    
    public abstract org.omg.CORBA.Object resolve_initial_references(final String p0) throws InvalidName;
    
    public abstract String object_to_string(final org.omg.CORBA.Object p0);
    
    public abstract org.omg.CORBA.Object string_to_object(final String p0);
    
    public abstract NVList create_list(final int p0);
    
    public NVList create_operation_list(final org.omg.CORBA.Object object) {
        try {
            final String s = "org.omg.CORBA.OperationDef";
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            return (NVList)this.getClass().getMethod("create_operation_list", Class.forName(s, true, classLoader)).invoke(this, object);
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            if (targetException instanceof Error) {
                throw (Error)targetException;
            }
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException)targetException;
            }
            throw new NO_IMPLEMENT();
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new NO_IMPLEMENT();
        }
    }
    
    public abstract NamedValue create_named_value(final String p0, final Any p1, final int p2);
    
    public abstract ExceptionList create_exception_list();
    
    public abstract ContextList create_context_list();
    
    public abstract Context get_default_context();
    
    public abstract Environment create_environment();
    
    public abstract OutputStream create_output_stream();
    
    public abstract void send_multiple_requests_oneway(final Request[] p0);
    
    public abstract void send_multiple_requests_deferred(final Request[] p0);
    
    public abstract boolean poll_next_response();
    
    public abstract Request get_next_response() throws WrongTransaction;
    
    public abstract TypeCode get_primitive_tc(final TCKind p0);
    
    public abstract TypeCode create_struct_tc(final String p0, final String p1, final StructMember[] p2);
    
    public abstract TypeCode create_union_tc(final String p0, final String p1, final TypeCode p2, final UnionMember[] p3);
    
    public abstract TypeCode create_enum_tc(final String p0, final String p1, final String[] p2);
    
    public abstract TypeCode create_alias_tc(final String p0, final String p1, final TypeCode p2);
    
    public abstract TypeCode create_exception_tc(final String p0, final String p1, final StructMember[] p2);
    
    public abstract TypeCode create_interface_tc(final String p0, final String p1);
    
    public abstract TypeCode create_string_tc(final int p0);
    
    public abstract TypeCode create_wstring_tc(final int p0);
    
    public abstract TypeCode create_sequence_tc(final int p0, final TypeCode p1);
    
    @Deprecated
    public abstract TypeCode create_recursive_sequence_tc(final int p0, final int p1);
    
    public abstract TypeCode create_array_tc(final int p0, final TypeCode p1);
    
    public TypeCode create_native_tc(final String s, final String s2) {
        throw new NO_IMPLEMENT();
    }
    
    public TypeCode create_abstract_interface_tc(final String s, final String s2) {
        throw new NO_IMPLEMENT();
    }
    
    public TypeCode create_fixed_tc(final short n, final short n2) {
        throw new NO_IMPLEMENT();
    }
    
    public TypeCode create_value_tc(final String s, final String s2, final short n, final TypeCode typeCode, final ValueMember[] array) {
        throw new NO_IMPLEMENT();
    }
    
    public TypeCode create_recursive_tc(final String s) {
        throw new NO_IMPLEMENT();
    }
    
    public TypeCode create_value_box_tc(final String s, final String s2, final TypeCode typeCode) {
        throw new NO_IMPLEMENT();
    }
    
    public abstract Any create_any();
    
    @Deprecated
    public Current get_current() {
        throw new NO_IMPLEMENT();
    }
    
    public void run() {
        throw new NO_IMPLEMENT();
    }
    
    public void shutdown(final boolean b) {
        throw new NO_IMPLEMENT();
    }
    
    public boolean work_pending() {
        throw new NO_IMPLEMENT();
    }
    
    public void perform_work() {
        throw new NO_IMPLEMENT();
    }
    
    public boolean get_service_information(final short n, final ServiceInformationHolder serviceInformationHolder) {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public DynAny create_dyn_any(final Any any) {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public DynAny create_basic_dyn_any(final TypeCode typeCode) throws InconsistentTypeCode {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public DynStruct create_dyn_struct(final TypeCode typeCode) throws InconsistentTypeCode {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public DynSequence create_dyn_sequence(final TypeCode typeCode) throws InconsistentTypeCode {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public DynArray create_dyn_array(final TypeCode typeCode) throws InconsistentTypeCode {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public DynUnion create_dyn_union(final TypeCode typeCode) throws InconsistentTypeCode {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public DynEnum create_dyn_enum(final TypeCode typeCode) throws InconsistentTypeCode {
        throw new NO_IMPLEMENT();
    }
    
    public Policy create_policy(final int n, final Any any) throws PolicyError {
        throw new NO_IMPLEMENT();
    }
}
