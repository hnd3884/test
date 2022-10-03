package com.sun.corba.se.impl.orbutil;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.Policy;
import java.security.PrivilegedAction;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.BAD_OPERATION;
import java.rmi.RemoteException;
import com.sun.corba.se.pept.transport.ContactInfoList;
import org.omg.CORBA.INTERNAL;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import org.omg.CORBA.portable.Delegate;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import java.util.StringTokenizer;
import java.util.Iterator;
import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.IOR;
import sun.corba.SharedSecrets;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import javax.rmi.CORBA.Util;
import java.security.PrivilegedExceptionAction;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Hashtable;
import org.omg.CORBA.StructMember;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public final class ORBUtility
{
    private static ORBUtilSystemException wrapper;
    private static OMGSystemException omgWrapper;
    private static StructMember[] members;
    private static final Hashtable exceptionClassNames;
    private static final Hashtable exceptionRepositoryIds;
    
    private ORBUtility() {
    }
    
    private static StructMember[] systemExceptionMembers(final ORB orb) {
        if (ORBUtility.members == null) {
            (ORBUtility.members = new StructMember[3])[0] = new StructMember("id", orb.create_string_tc(0), null);
            ORBUtility.members[1] = new StructMember("minor", orb.get_primitive_tc(TCKind.tk_long), null);
            ORBUtility.members[2] = new StructMember("completed", orb.get_primitive_tc(TCKind.tk_long), null);
        }
        return ORBUtility.members;
    }
    
    private static TypeCode getSystemExceptionTypeCode(final ORB orb, final String s, final String s2) {
        synchronized (TypeCode.class) {
            return orb.create_exception_tc(s, s2, systemExceptionMembers(orb));
        }
    }
    
    private static boolean isSystemExceptionTypeCode(final TypeCode typeCode, final ORB orb) {
        final StructMember[] systemExceptionMembers = systemExceptionMembers(orb);
        try {
            return typeCode.kind().value() == 22 && typeCode.member_count() == 3 && typeCode.member_type(0).equal(systemExceptionMembers[0].type) && typeCode.member_type(1).equal(systemExceptionMembers[1].type) && typeCode.member_type(2).equal(systemExceptionMembers[2].type);
        }
        catch (final BadKind badKind) {
            return false;
        }
        catch (final Bounds bounds) {
            return false;
        }
    }
    
    public static void insertSystemException(final SystemException ex, final Any any) {
        final OutputStream create_output_stream = any.create_output_stream();
        final ORB orb = (ORB)create_output_stream.orb();
        final String name = ex.getClass().getName();
        final String repositoryId = repositoryIdOf(name);
        create_output_stream.write_string(repositoryId);
        create_output_stream.write_long(ex.minor);
        create_output_stream.write_long(ex.completed.value());
        any.read_value(create_output_stream.create_input_stream(), getSystemExceptionTypeCode(orb, repositoryId, name));
    }
    
    public static SystemException extractSystemException(final Any any) {
        final InputStream create_input_stream = any.create_input_stream();
        if (!isSystemExceptionTypeCode(any.type(), (ORB)create_input_stream.orb())) {
            throw ORBUtility.wrapper.unknownDsiSysex(CompletionStatus.COMPLETED_MAYBE);
        }
        return readSystemException(create_input_stream);
    }
    
    public static ValueHandler createValueHandler() {
        ValueHandler valueHandler;
        try {
            valueHandler = AccessController.doPrivileged((PrivilegedExceptionAction<ValueHandler>)new PrivilegedExceptionAction<ValueHandler>() {
                @Override
                public ValueHandler run() throws Exception {
                    return Util.createValueHandler();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new InternalError(ex.getMessage());
        }
        return valueHandler;
    }
    
    public static boolean isForeignORB(final ORB orb) {
        if (orb == null) {
            return false;
        }
        try {
            return orb.getORBVersion().equals(ORBVersionFactory.getFOREIGN());
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
    
    public static int bytesToInt(final byte[] array, int n) {
        return (array[n++] << 24 & 0xFF000000) | (array[n++] << 16 & 0xFF0000) | (array[n++] << 8 & 0xFF00) | (array[n++] << 0 & 0xFF);
    }
    
    public static void intToBytes(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >>> 24 & 0xFF);
        array[n2++] = (byte)(n >>> 16 & 0xFF);
        array[n2++] = (byte)(n >>> 8 & 0xFF);
        array[n2++] = (byte)(n >>> 0 & 0xFF);
    }
    
    public static int hexOf(final char c) {
        final int n = c - '0';
        if (n >= 0 && n <= 9) {
            return n;
        }
        final int n2 = c - 'a' + 10;
        if (n2 >= 10 && n2 <= 15) {
            return n2;
        }
        final int n3 = c - 'A' + 10;
        if (n3 >= 10 && n3 <= 15) {
            return n3;
        }
        throw ORBUtility.wrapper.badHexDigit();
    }
    
    public static void writeSystemException(final SystemException ex, final OutputStream outputStream) {
        outputStream.write_string(repositoryIdOf(ex.getClass().getName()));
        outputStream.write_long(ex.minor);
        outputStream.write_long(ex.completed.value());
    }
    
    public static SystemException readSystemException(final InputStream inputStream) {
        try {
            final SystemException ex = (SystemException)SharedSecrets.getJavaCorbaAccess().loadClass(classNameOf(inputStream.read_string())).newInstance();
            ex.minor = inputStream.read_long();
            ex.completed = CompletionStatus.from_int(inputStream.read_long());
            return ex;
        }
        catch (final Exception ex2) {
            throw ORBUtility.wrapper.unknownSysex(CompletionStatus.COMPLETED_MAYBE, ex2);
        }
    }
    
    public static String classNameOf(final String s) {
        String s2 = ORBUtility.exceptionClassNames.get(s);
        if (s2 == null) {
            s2 = "org.omg.CORBA.UNKNOWN";
        }
        return s2;
    }
    
    public static boolean isSystemException(final String s) {
        return ORBUtility.exceptionClassNames.get(s) != null;
    }
    
    public static byte getEncodingVersion(final ORB orb, final IOR ior) {
        if (orb.getORBData().isJavaSerializationEnabled()) {
            final Iterator iteratorById = ior.getProfile().getTaggedProfileTemplate().iteratorById(1398099458);
            if (iteratorById.hasNext()) {
                final JavaSerializationComponent javaSerializationComponent = iteratorById.next();
                final byte javaSerializationVersion = javaSerializationComponent.javaSerializationVersion();
                if (javaSerializationVersion >= 1) {
                    return 1;
                }
                if (javaSerializationVersion > 0) {
                    return javaSerializationComponent.javaSerializationVersion();
                }
            }
        }
        return 0;
    }
    
    public static String repositoryIdOf(final String s) {
        String s2 = ORBUtility.exceptionRepositoryIds.get(s);
        if (s2 == null) {
            s2 = "IDL:omg.org/CORBA/UNKNOWN:1.0";
        }
        return s2;
    }
    
    public static int[] parseVersion(final String s) {
        if (s == null) {
            return new int[0];
        }
        char[] charArray;
        int n;
        for (charArray = s.toCharArray(), n = 0; n < charArray.length && (charArray[n] < '0' || charArray[n] > '9'); ++n) {
            if (n == charArray.length) {
                return new int[0];
            }
        }
        int i = n + 1;
        int n2 = 1;
        while (i < charArray.length) {
            if (charArray[i] == '.') {
                ++n2;
            }
            else {
                if (charArray[i] < '0') {
                    break;
                }
                if (charArray[i] > '9') {
                    break;
                }
            }
            ++i;
        }
        final int[] array = new int[n2];
        for (int j = 0; j < n2; ++j) {
            int index = s.indexOf(46, n);
            if (index == -1 || index > i) {
                index = i;
            }
            if (n >= index) {
                array[j] = 0;
            }
            else {
                array[j] = Integer.parseInt(s.substring(n, index));
            }
            n = index + 1;
        }
        return array;
    }
    
    public static int compareVersion(int[] array, int[] array2) {
        if (array == null) {
            array = new int[0];
        }
        if (array2 == null) {
            array2 = new int[0];
        }
        for (int i = 0; i < array.length; ++i) {
            if (i >= array2.length || array[i] > array2[i]) {
                return 1;
            }
            if (array[i] < array2[i]) {
                return -1;
            }
        }
        return (array.length == array2.length) ? 0 : -1;
    }
    
    public static synchronized int compareVersion(final String s, final String s2) {
        return compareVersion(parseVersion(s), parseVersion(s2));
    }
    
    private static String compressClassName(final String s) {
        final String s2 = "com.sun.corba.se.";
        if (s.startsWith(s2)) {
            return "(ORB)." + s.substring(s2.length());
        }
        return s;
    }
    
    public static String getThreadName(final Thread thread) {
        if (thread == null) {
            return "null";
        }
        final String name = thread.getName();
        final StringTokenizer stringTokenizer = new StringTokenizer(name);
        final int countTokens = stringTokenizer.countTokens();
        if (countTokens != 5) {
            return name;
        }
        final String[] array = new String[countTokens];
        for (int i = 0; i < countTokens; ++i) {
            array[i] = stringTokenizer.nextToken();
        }
        if (!array[0].equals("SelectReaderThread")) {
            return name;
        }
        return "SelectReaderThread[" + array[2] + ":" + array[3] + "]";
    }
    
    private static String formatStackTraceElement(final StackTraceElement stackTraceElement) {
        return compressClassName(stackTraceElement.getClassName()) + "." + stackTraceElement.getMethodName() + (stackTraceElement.isNativeMethod() ? "(Native Method)" : ((stackTraceElement.getFileName() != null && stackTraceElement.getLineNumber() >= 0) ? ("(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")") : ((stackTraceElement.getFileName() != null) ? ("(" + stackTraceElement.getFileName() + ")") : "(Unknown Source)")));
    }
    
    private static void printStackTrace(final StackTraceElement[] array) {
        System.out.println("    Stack Trace:");
        for (int i = 1; i < array.length; ++i) {
            System.out.print("        >");
            System.out.println(formatStackTraceElement(array[i]));
        }
    }
    
    public static synchronized void dprint(final Object o, final String s) {
        System.out.println(compressClassName(o.getClass().getName()) + "(" + getThreadName(Thread.currentThread()) + "): " + s);
    }
    
    public static synchronized void dprint(final String s, final String s2) {
        System.out.println(compressClassName(s) + "(" + getThreadName(Thread.currentThread()) + "): " + s2);
    }
    
    public synchronized void dprint(final String s) {
        dprint(this, s);
    }
    
    public static synchronized void dprintTrace(final Object o, final String s) {
        dprint(o, s);
        printStackTrace(new Throwable().getStackTrace());
    }
    
    public static synchronized void dprint(final Object o, final String s, final Throwable t) {
        System.out.println(compressClassName(o.getClass().getName()) + '(' + Thread.currentThread() + "): " + s);
        if (t != null) {
            printStackTrace(t.getStackTrace());
        }
    }
    
    public static String[] concatenateStringArrays(final String[] array, final String[] array2) {
        final String[] array3 = new String[array.length + array2.length];
        for (int i = 0; i < array.length; ++i) {
            array3[i] = array[i];
        }
        for (int j = 0; j < array2.length; ++j) {
            array3[j + array.length] = array2[j];
        }
        return array3;
    }
    
    public static void throwNotSerializableForCorba(final String s) {
        throw ORBUtility.omgWrapper.notSerializable(CompletionStatus.COMPLETED_MAYBE, s);
    }
    
    public static byte getMaxStreamFormatVersion() {
        ValueHandler valueHandler;
        try {
            valueHandler = AccessController.doPrivileged((PrivilegedExceptionAction<ValueHandler>)new PrivilegedExceptionAction<ValueHandler>() {
                @Override
                public ValueHandler run() throws Exception {
                    return Util.createValueHandler();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new InternalError(ex.getMessage());
        }
        if (!(valueHandler instanceof ValueHandlerMultiFormat)) {
            return 1;
        }
        return ((ValueHandlerMultiFormat)valueHandler).getMaximumStreamFormatVersion();
    }
    
    public static CorbaClientDelegate makeClientDelegate(final IOR ior) {
        final ORB orb = ior.getORB();
        return orb.getClientDelegateFactory().create(orb.getCorbaContactInfoListFactory().create(ior));
    }
    
    public static org.omg.CORBA.Object makeObjectReference(final IOR ior) {
        final CorbaClientDelegate clientDelegate = makeClientDelegate(ior);
        final CORBAObjectImpl corbaObjectImpl = new CORBAObjectImpl();
        StubAdapter.setDelegate(corbaObjectImpl, clientDelegate);
        return corbaObjectImpl;
    }
    
    public static IOR getIOR(final org.omg.CORBA.Object object) {
        if (object == null) {
            throw ORBUtility.wrapper.nullObjectReference();
        }
        if (!StubAdapter.isStub(object)) {
            throw ORBUtility.wrapper.localObjectNotAllowed();
        }
        final Delegate delegate = StubAdapter.getDelegate(object);
        if (!(delegate instanceof CorbaClientDelegate)) {
            throw ORBUtility.wrapper.objrefFromForeignOrb();
        }
        final ContactInfoList contactInfoList = ((CorbaClientDelegate)delegate).getContactInfoList();
        if (!(contactInfoList instanceof CorbaContactInfoList)) {
            throw new INTERNAL();
        }
        final IOR targetIOR = ((CorbaContactInfoList)contactInfoList).getTargetIOR();
        if (targetIOR == null) {
            throw ORBUtility.wrapper.nullIor();
        }
        return targetIOR;
    }
    
    public static IOR connectAndGetIOR(final ORB orb, final org.omg.CORBA.Object object) {
        IOR ior;
        try {
            ior = getIOR(object);
        }
        catch (final BAD_OPERATION bad_OPERATION) {
            Label_0040: {
                if (StubAdapter.isStub(object)) {
                    try {
                        StubAdapter.connect(object, orb);
                        break Label_0040;
                    }
                    catch (final RemoteException ex) {
                        throw ORBUtility.wrapper.connectingServant(ex);
                    }
                }
                orb.connect(object);
            }
            ior = getIOR(object);
        }
        return ior;
    }
    
    public static String operationNameAndRequestId(final CorbaMessageMediator corbaMessageMediator) {
        return "op/" + corbaMessageMediator.getOperationName() + " id/" + corbaMessageMediator.getRequestId();
    }
    
    public static boolean isPrintable(final char c) {
        if (Character.isJavaIdentifierStart(c)) {
            return true;
        }
        if (Character.isDigit(c)) {
            return true;
        }
        switch (Character.getType(c)) {
            case 27: {
                return true;
            }
            case 20: {
                return true;
            }
            case 25: {
                return true;
            }
            case 24: {
                return true;
            }
            case 21: {
                return true;
            }
            case 22: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static String getClassSecurityInfo(final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            @Override
            public Object run() {
                final StringBuffer sb = new StringBuffer(500);
                final ProtectionDomain protectionDomain = clazz.getProtectionDomain();
                final PermissionCollection permissions = Policy.getPolicy().getPermissions(protectionDomain);
                sb.append("\nPermissionCollection ");
                sb.append(permissions.toString());
                sb.append(protectionDomain.toString());
                return sb.toString();
            }
        });
    }
    
    static {
        ORBUtility.wrapper = ORBUtilSystemException.get("util");
        ORBUtility.omgWrapper = OMGSystemException.get("util");
        ORBUtility.members = null;
        exceptionClassNames = new Hashtable();
        exceptionRepositoryIds = new Hashtable();
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/BAD_CONTEXT:1.0", "org.omg.CORBA.BAD_CONTEXT");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/BAD_INV_ORDER:1.0", "org.omg.CORBA.BAD_INV_ORDER");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/BAD_OPERATION:1.0", "org.omg.CORBA.BAD_OPERATION");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/BAD_PARAM:1.0", "org.omg.CORBA.BAD_PARAM");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/BAD_TYPECODE:1.0", "org.omg.CORBA.BAD_TYPECODE");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/COMM_FAILURE:1.0", "org.omg.CORBA.COMM_FAILURE");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/DATA_CONVERSION:1.0", "org.omg.CORBA.DATA_CONVERSION");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/IMP_LIMIT:1.0", "org.omg.CORBA.IMP_LIMIT");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INTF_REPOS:1.0", "org.omg.CORBA.INTF_REPOS");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INTERNAL:1.0", "org.omg.CORBA.INTERNAL");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INV_FLAG:1.0", "org.omg.CORBA.INV_FLAG");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INV_IDENT:1.0", "org.omg.CORBA.INV_IDENT");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INV_OBJREF:1.0", "org.omg.CORBA.INV_OBJREF");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/MARSHAL:1.0", "org.omg.CORBA.MARSHAL");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/NO_MEMORY:1.0", "org.omg.CORBA.NO_MEMORY");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/FREE_MEM:1.0", "org.omg.CORBA.FREE_MEM");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/NO_IMPLEMENT:1.0", "org.omg.CORBA.NO_IMPLEMENT");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/NO_PERMISSION:1.0", "org.omg.CORBA.NO_PERMISSION");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESOURCES:1.0", "org.omg.CORBA.NO_RESOURCES");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESPONSE:1.0", "org.omg.CORBA.NO_RESPONSE");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/OBJ_ADAPTER:1.0", "org.omg.CORBA.OBJ_ADAPTER");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INITIALIZE:1.0", "org.omg.CORBA.INITIALIZE");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/PERSIST_STORE:1.0", "org.omg.CORBA.PERSIST_STORE");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/TRANSIENT:1.0", "org.omg.CORBA.TRANSIENT");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/UNKNOWN:1.0", "org.omg.CORBA.UNKNOWN");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0", "org.omg.CORBA.OBJECT_NOT_EXIST");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0", "org.omg.CORBA.INVALID_TRANSACTION");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0", "org.omg.CORBA.TRANSACTION_REQUIRED");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0", "org.omg.CORBA.TRANSACTION_ROLLEDBACK");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INV_POLICY:1.0", "org.omg.CORBA.INV_POLICY");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0", "org.omg.CORBA.TRANSACTION_UNAVAILABLE");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_MODE:1.0", "org.omg.CORBA.TRANSACTION_MODE");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/CODESET_INCOMPATIBLE:1.0", "org.omg.CORBA.CODESET_INCOMPATIBLE");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/REBIND:1.0", "org.omg.CORBA.REBIND");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/TIMEOUT:1.0", "org.omg.CORBA.TIMEOUT");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/BAD_QOS:1.0", "org.omg.CORBA.BAD_QOS");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_ACTIVITY:1.0", "org.omg.CORBA.INVALID_ACTIVITY");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/ACTIVITY_COMPLETED:1.0", "org.omg.CORBA.ACTIVITY_COMPLETED");
        ORBUtility.exceptionClassNames.put("IDL:omg.org/CORBA/ACTIVITY_REQUIRED:1.0", "org.omg.CORBA.ACTIVITY_REQUIRED");
        final Enumeration keys = ORBUtility.exceptionClassNames.keys();
        try {
            while (keys.hasMoreElements()) {
                final String s = (String)keys.nextElement();
                ORBUtility.exceptionRepositoryIds.put(ORBUtility.exceptionClassNames.get(s), s);
            }
        }
        catch (final NoSuchElementException ex) {}
    }
}
