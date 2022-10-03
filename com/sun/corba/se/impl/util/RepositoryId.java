package com.sun.corba.se.impl.util;

import javax.rmi.CORBA.ClassDesc;
import java.io.Externalizable;
import org.omg.CORBA.portable.ValueBase;
import org.omg.CORBA.portable.IDLEntity;
import java.lang.reflect.InvocationTargetException;
import org.omg.CORBA.MARSHAL;
import com.sun.corba.se.impl.io.TypeMismatchException;
import com.sun.corba.se.impl.io.ObjectStreamClass;
import java.io.Serializable;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.rmi.CORBA.Util;
import java.rmi.Remote;
import java.util.Hashtable;

public class RepositoryId
{
    private static final byte[] IDL_IDENTIFIER_CHARS;
    private static final long serialVersionUID = 123456789L;
    private static String defaultServerURL;
    private static boolean useCodebaseOnly;
    private static IdentityHashtable classToRepStr;
    private static IdentityHashtable classIDLToRepStr;
    private static IdentityHashtable classSeqToRepStr;
    private static final IdentityHashtable repStrToByteArray;
    private static Hashtable repStrToClass;
    private String repId;
    private boolean isSupportedFormat;
    private String typeString;
    private String versionString;
    private boolean isSequence;
    private boolean isRMIValueType;
    private boolean isIDLType;
    private String completeClassName;
    private String unqualifiedName;
    private String definedInId;
    private Class clazz;
    private String suid;
    private String actualSuid;
    private long suidLong;
    private long actualSuidLong;
    private static final String kSequenceKeyword = "seq";
    private static final String kValuePrefix = "RMI:";
    private static final String kIDLPrefix = "IDL:";
    private static final String kIDLNamePrefix = "omg.org/";
    private static final String kIDLClassnamePrefix = "org.omg.";
    private static final String kSequencePrefix = "[";
    private static final String kCORBAPrefix = "CORBA/";
    private static final String kArrayPrefix = "RMI:[CORBA/";
    private static final int kValuePrefixLength;
    private static final int kIDLPrefixLength;
    private static final int kSequencePrefixLength;
    private static final String kInterfaceHashCode = ":0000000000000000";
    private static final String kInterfaceOnlyHashStr = "0000000000000000";
    private static final String kExternalizableHashStr = "0000000000000001";
    public static final int kInitialValueTag = 2147483392;
    public static final int kNoTypeInfo = 0;
    public static final int kSingleRepTypeInfo = 2;
    public static final int kPartialListTypeInfo = 6;
    public static final int kChunkedMask = 8;
    public static final int kPreComputed_StandardRMIUnchunked;
    public static final int kPreComputed_CodeBaseRMIUnchunked;
    public static final int kPreComputed_StandardRMIChunked;
    public static final int kPreComputed_CodeBaseRMIChunked;
    public static final int kPreComputed_StandardRMIUnchunked_NoRep;
    public static final int kPreComputed_CodeBaseRMIUnchunked_NoRep;
    public static final int kPreComputed_StandardRMIChunked_NoRep;
    public static final int kPreComputed_CodeBaseRMIChunked_NoRep;
    public static final String kWStringValueVersion = "1.0";
    public static final String kWStringValueHash = ":1.0";
    public static final String kWStringStubValue = "WStringValue";
    public static final String kWStringTypeStr = "omg.org/CORBA/WStringValue";
    public static final String kWStringValueRepID = "IDL:omg.org/CORBA/WStringValue:1.0";
    public static final String kAnyRepID = "IDL:omg.org/CORBA/Any";
    public static final String kClassDescValueHash;
    public static final String kClassDescStubValue = "ClassDesc";
    public static final String kClassDescTypeStr = "javax.rmi.CORBA.ClassDesc";
    public static final String kClassDescValueRepID;
    public static final String kObjectValueHash = ":1.0";
    public static final String kObjectStubValue = "Object";
    public static final String kSequenceValueHash = ":1.0";
    public static final String kPrimitiveSequenceValueHash = ":0000000000000000";
    public static final String kSerializableValueHash = ":1.0";
    public static final String kSerializableStubValue = "Serializable";
    public static final String kExternalizableValueHash = ":1.0";
    public static final String kExternalizableStubValue = "Externalizable";
    public static final String kRemoteValueHash = "";
    public static final String kRemoteStubValue = "";
    public static final String kRemoteTypeStr = "";
    public static final String kRemoteValueRepID = "";
    private static final Hashtable kSpecialArrayTypeStrings;
    private static final Hashtable kSpecialCasesRepIDs;
    private static final Hashtable kSpecialCasesStubValues;
    private static final Hashtable kSpecialCasesVersions;
    private static final Hashtable kSpecialCasesClasses;
    private static final Hashtable kSpecialCasesArrayPrefix;
    private static final Hashtable kSpecialPrimitives;
    private static final byte[] ASCII_HEX;
    public static final RepositoryIdCache cache;
    public static final String kjava_rmi_Remote;
    public static final String korg_omg_CORBA_Object;
    public static final Class[] kNoParamTypes;
    public static final Object[] kNoArgs;
    
    RepositoryId() {
        this.repId = null;
        this.isSupportedFormat = true;
        this.typeString = null;
        this.versionString = null;
        this.isSequence = false;
        this.isRMIValueType = false;
        this.isIDLType = false;
        this.completeClassName = null;
        this.unqualifiedName = null;
        this.definedInId = null;
        this.clazz = null;
        this.suid = null;
        this.actualSuid = null;
        this.suidLong = -1L;
        this.actualSuidLong = -1L;
    }
    
    RepositoryId(final String s) {
        this.repId = null;
        this.isSupportedFormat = true;
        this.typeString = null;
        this.versionString = null;
        this.isSequence = false;
        this.isRMIValueType = false;
        this.isIDLType = false;
        this.completeClassName = null;
        this.unqualifiedName = null;
        this.definedInId = null;
        this.clazz = null;
        this.suid = null;
        this.actualSuid = null;
        this.suidLong = -1L;
        this.actualSuidLong = -1L;
        this.init(s);
    }
    
    RepositoryId init(final String repId) {
        this.repId = repId;
        if (repId.length() == 0) {
            this.clazz = Remote.class;
            this.typeString = "";
            this.isRMIValueType = true;
            this.suid = "0000000000000000";
            return this;
        }
        if (repId.equals("IDL:omg.org/CORBA/WStringValue:1.0")) {
            this.clazz = String.class;
            this.typeString = "omg.org/CORBA/WStringValue";
            this.isIDLType = true;
            this.completeClassName = "java.lang.String";
            this.versionString = "1.0";
            return this;
        }
        final String convertFromISOLatin1 = convertFromISOLatin1(repId);
        final int index = convertFromISOLatin1.indexOf(58);
        if (index == -1) {
            throw new IllegalArgumentException("RepsitoryId must have the form <type>:<body>");
        }
        final int index2 = convertFromISOLatin1.indexOf(58, index + 1);
        if (index2 == -1) {
            this.versionString = "";
        }
        else {
            this.versionString = convertFromISOLatin1.substring(index2);
        }
        if (convertFromISOLatin1.startsWith("IDL:")) {
            this.typeString = convertFromISOLatin1.substring(RepositoryId.kIDLPrefixLength, convertFromISOLatin1.indexOf(58, RepositoryId.kIDLPrefixLength));
            this.isIDLType = true;
            if (this.typeString.startsWith("omg.org/")) {
                this.completeClassName = "org.omg." + this.typeString.substring("omg.org/".length()).replace('/', '.');
            }
            else {
                this.completeClassName = this.typeString.replace('/', '.');
            }
        }
        else if (convertFromISOLatin1.startsWith("RMI:")) {
            this.typeString = convertFromISOLatin1.substring(RepositoryId.kValuePrefixLength, convertFromISOLatin1.indexOf(58, RepositoryId.kValuePrefixLength));
            this.isRMIValueType = true;
            if (this.versionString.indexOf(46) == -1) {
                this.actualSuid = this.versionString.substring(1);
                this.suid = this.actualSuid;
                if (this.actualSuid.indexOf(58) != -1) {
                    final int n = this.actualSuid.indexOf(58) + 1;
                    this.suid = this.actualSuid.substring(n);
                    this.actualSuid = this.actualSuid.substring(0, n - 1);
                }
            }
        }
        else {
            this.isSupportedFormat = false;
            this.typeString = "";
        }
        if (this.typeString.startsWith("[")) {
            this.isSequence = true;
        }
        return this;
    }
    
    public final String getUnqualifiedName() {
        if (this.unqualifiedName == null) {
            final String className = this.getClassName();
            final int lastIndex = className.lastIndexOf(46);
            if (lastIndex == -1) {
                this.unqualifiedName = className;
                this.definedInId = "IDL::1.0";
            }
            else {
                this.unqualifiedName = className.substring(lastIndex);
                this.definedInId = "IDL:" + className.substring(0, lastIndex).replace('.', '/') + ":1.0";
            }
        }
        return this.unqualifiedName;
    }
    
    public final String getDefinedInId() {
        if (this.definedInId == null) {
            this.getUnqualifiedName();
        }
        return this.definedInId;
    }
    
    public final String getTypeString() {
        return this.typeString;
    }
    
    public final String getVersionString() {
        return this.versionString;
    }
    
    public final String getSerialVersionUID() {
        return this.suid;
    }
    
    public final String getActualSerialVersionUID() {
        return this.actualSuid;
    }
    
    public final long getSerialVersionUIDAsLong() {
        return this.suidLong;
    }
    
    public final long getActualSerialVersionUIDAsLong() {
        return this.actualSuidLong;
    }
    
    public final boolean isRMIValueType() {
        return this.isRMIValueType;
    }
    
    public final boolean isIDLType() {
        return this.isIDLType;
    }
    
    public final String getRepositoryId() {
        return this.repId;
    }
    
    public static byte[] getByteArray(final String s) {
        synchronized (RepositoryId.repStrToByteArray) {
            return (byte[])RepositoryId.repStrToByteArray.get(s);
        }
    }
    
    public static void setByteArray(final String s, final byte[] array) {
        synchronized (RepositoryId.repStrToByteArray) {
            RepositoryId.repStrToByteArray.put(s, array);
        }
    }
    
    public final boolean isSequence() {
        return this.isSequence;
    }
    
    public final boolean isSupportedFormat() {
        return this.isSupportedFormat;
    }
    
    public final String getClassName() {
        if (this.isRMIValueType) {
            return this.typeString;
        }
        if (this.isIDLType) {
            return this.completeClassName;
        }
        return null;
    }
    
    public final Class getAnyClassFromType() throws ClassNotFoundException {
        try {
            return this.getClassFromType();
        }
        catch (final ClassNotFoundException ex) {
            final Class clazz = RepositoryId.repStrToClass.get(this.repId);
            if (clazz != null) {
                return clazz;
            }
            throw ex;
        }
    }
    
    public final Class getClassFromType() throws ClassNotFoundException {
        if (this.clazz != null) {
            return this.clazz;
        }
        final Class clazz = RepositoryId.kSpecialCasesClasses.get(this.getClassName());
        if (clazz != null) {
            return this.clazz = clazz;
        }
        try {
            return Util.loadClass(this.getClassName(), null, null);
        }
        catch (final ClassNotFoundException ex) {
            if (RepositoryId.defaultServerURL != null) {
                try {
                    return this.getClassFromType(RepositoryId.defaultServerURL);
                }
                catch (final MalformedURLException ex2) {
                    throw ex;
                }
            }
            throw ex;
        }
    }
    
    public final Class getClassFromType(final Class clazz, final String s) throws ClassNotFoundException {
        if (this.clazz != null) {
            return this.clazz;
        }
        final Class clazz2 = RepositoryId.kSpecialCasesClasses.get(this.getClassName());
        if (clazz2 != null) {
            return this.clazz = clazz2;
        }
        final ClassLoader classLoader = (clazz == null) ? null : clazz.getClassLoader();
        return Utility.loadClassOfType(this.getClassName(), s, classLoader, clazz, classLoader);
    }
    
    public final Class getClassFromType(final String s) throws ClassNotFoundException, MalformedURLException {
        return Util.loadClass(this.getClassName(), s, null);
    }
    
    @Override
    public final String toString() {
        return this.repId;
    }
    
    public static boolean useFullValueDescription(final Class clazz, final String s) throws IOException {
        final String forAnyType = createForAnyType(clazz);
        if (forAnyType.equals(s)) {
            return false;
        }
        final RepositoryId id;
        final RepositoryId id2;
        synchronized (RepositoryId.cache) {
            id = RepositoryId.cache.getId(s);
            id2 = RepositoryId.cache.getId(forAnyType);
        }
        if (!id.isRMIValueType() || !id2.isRMIValueType()) {
            throw new IOException("The repository ID is not of an RMI value type (Expected ID = " + forAnyType + "; Received ID = " + s + ")");
        }
        if (!id.getSerialVersionUID().equals(id2.getSerialVersionUID())) {
            throw new IOException("Mismatched serialization UIDs : Source (Rep. ID" + id2 + ") = " + id2.getSerialVersionUID() + " whereas Target (Rep. ID " + s + ") = " + id.getSerialVersionUID());
        }
        return true;
    }
    
    private static String createHashString(final Serializable s) {
        return createHashString(s.getClass());
    }
    
    private static String createHashString(final Class clazz) {
        if (clazz.isInterface() || !Serializable.class.isAssignableFrom(clazz)) {
            return ":0000000000000000";
        }
        final long actualSerialVersionUID = ObjectStreamClass.getActualSerialVersionUID(clazz);
        String s;
        if (actualSerialVersionUID == 0L) {
            s = "0000000000000000";
        }
        else if (actualSerialVersionUID == 1L) {
            s = "0000000000000001";
        }
        else {
            s = Long.toHexString(actualSerialVersionUID).toUpperCase();
        }
        while (s.length() < 16) {
            s = "0" + s;
        }
        final long serialVersionUID = ObjectStreamClass.getSerialVersionUID(clazz);
        String s2;
        if (serialVersionUID == 0L) {
            s2 = "0000000000000000";
        }
        else if (serialVersionUID == 1L) {
            s2 = "0000000000000001";
        }
        else {
            s2 = Long.toHexString(serialVersionUID).toUpperCase();
        }
        while (s2.length() < 16) {
            s2 = "0" + s2;
        }
        return ":" + (s + ":" + s2);
    }
    
    public static String createSequenceRepID(final Object o) {
        return createSequenceRepID(o.getClass());
    }
    
    public static String createSequenceRepID(Class clazz) {
        synchronized (RepositoryId.classSeqToRepStr) {
            final String s = (String)RepositoryId.classSeqToRepStr.get(clazz);
            if (s != null) {
                return s;
            }
            final Class clazz2 = clazz;
            int n = 0;
            Class componentType;
            while ((componentType = clazz.getComponentType()) != null) {
                ++n;
                clazz = componentType;
            }
            String s2;
            if (clazz.isPrimitive()) {
                s2 = "RMI:" + clazz2.getName() + ":0000000000000000";
            }
            else {
                final StringBuffer sb = new StringBuffer();
                sb.append("RMI:");
                while (n-- > 0) {
                    sb.append("[");
                }
                sb.append("L");
                sb.append(convertToISOLatin1(clazz.getName()));
                sb.append(";");
                sb.append(createHashString(clazz));
                s2 = sb.toString();
            }
            RepositoryId.classSeqToRepStr.put(clazz2, s2);
            return s2;
        }
    }
    
    public static String createForSpecialCase(final Class clazz) {
        if (clazz.isArray()) {
            return createSequenceRepID(clazz);
        }
        return RepositoryId.kSpecialCasesRepIDs.get(clazz);
    }
    
    public static String createForSpecialCase(final Serializable s) {
        final Class<? extends Serializable> class1 = s.getClass();
        if (class1.isArray()) {
            return createSequenceRepID(s);
        }
        return createForSpecialCase(class1);
    }
    
    public static String createForJavaType(final Serializable s) throws TypeMismatchException {
        synchronized (RepositoryId.classToRepStr) {
            final String forSpecialCase = createForSpecialCase(s);
            if (forSpecialCase != null) {
                return forSpecialCase;
            }
            final Class<? extends Serializable> class1 = s.getClass();
            final String s2 = (String)RepositoryId.classToRepStr.get(class1);
            if (s2 != null) {
                return s2;
            }
            final String string = "RMI:" + convertToISOLatin1(class1.getName()) + createHashString(class1);
            RepositoryId.classToRepStr.put(class1, string);
            RepositoryId.repStrToClass.put(string, class1);
            return string;
        }
    }
    
    public static String createForJavaType(final Class clazz) throws TypeMismatchException {
        synchronized (RepositoryId.classToRepStr) {
            final String forSpecialCase = createForSpecialCase(clazz);
            if (forSpecialCase != null) {
                return forSpecialCase;
            }
            final String s = (String)RepositoryId.classToRepStr.get(clazz);
            if (s != null) {
                return s;
            }
            final String string = "RMI:" + convertToISOLatin1(clazz.getName()) + createHashString(clazz);
            RepositoryId.classToRepStr.put(clazz, string);
            RepositoryId.repStrToClass.put(string, clazz);
            return string;
        }
    }
    
    public static String createForIDLType(final Class clazz, final int n, final int n2) throws TypeMismatchException {
        synchronized (RepositoryId.classIDLToRepStr) {
            final String s = (String)RepositoryId.classIDLToRepStr.get(clazz);
            if (s != null) {
                return s;
            }
            final String string = "IDL:" + convertToISOLatin1(clazz.getName()).replace('.', '/') + ":" + n + "." + n2;
            RepositoryId.classIDLToRepStr.put(clazz, string);
            return string;
        }
    }
    
    private static String getIdFromHelper(final Class clazz) {
        try {
            return (String)Utility.loadClassForClass(clazz.getName() + "Helper", null, clazz.getClassLoader(), clazz, clazz.getClassLoader()).getDeclaredMethod("id", (Class[])RepositoryId.kNoParamTypes).invoke(null, RepositoryId.kNoArgs);
        }
        catch (final ClassNotFoundException ex) {
            throw new MARSHAL(ex.toString());
        }
        catch (final NoSuchMethodException ex2) {
            throw new MARSHAL(ex2.toString());
        }
        catch (final InvocationTargetException ex3) {
            throw new MARSHAL(ex3.toString());
        }
        catch (final IllegalAccessException ex4) {
            throw new MARSHAL(ex4.toString());
        }
    }
    
    public static String createForAnyType(final Class clazz) {
        try {
            if (clazz.isArray()) {
                return createSequenceRepID(clazz);
            }
            if (IDLEntity.class.isAssignableFrom(clazz)) {
                try {
                    return getIdFromHelper(clazz);
                }
                catch (final Throwable t) {
                    return createForIDLType(clazz, 1, 0);
                }
            }
            return createForJavaType(clazz);
        }
        catch (final TypeMismatchException ex) {
            return null;
        }
    }
    
    public static boolean isAbstractBase(final Class clazz) {
        return clazz.isInterface() && IDLEntity.class.isAssignableFrom(clazz) && !ValueBase.class.isAssignableFrom(clazz) && !org.omg.CORBA.Object.class.isAssignableFrom(clazz);
    }
    
    public static boolean isAnyRequired(final Class clazz) {
        return clazz == Object.class || clazz == Serializable.class || clazz == Externalizable.class;
    }
    
    public static long fromHex(final String s) {
        if (s.startsWith("0x")) {
            return Long.valueOf(s.substring(2), 16);
        }
        return Long.valueOf(s, 16);
    }
    
    public static String convertToISOLatin1(String string) {
        final int length = string.length();
        if (length == 0) {
            return string;
        }
        StringBuffer sb = null;
        for (int i = 0; i < length; ++i) {
            final char char1 = string.charAt(i);
            if (char1 > '\u00ff' || RepositoryId.IDL_IDENTIFIER_CHARS[char1] == 0) {
                if (sb == null) {
                    sb = new StringBuffer(string.substring(0, i));
                }
                sb.append("\\U" + (char)RepositoryId.ASCII_HEX[(char1 & '\uf000') >>> 12] + (char)RepositoryId.ASCII_HEX[(char1 & '\u0f00') >>> 8] + (char)RepositoryId.ASCII_HEX[(char1 & '\u00f0') >>> 4] + (char)RepositoryId.ASCII_HEX[char1 & '\u000f']);
            }
            else if (sb != null) {
                sb.append(char1);
            }
        }
        if (sb != null) {
            string = sb.toString();
        }
        return string;
    }
    
    private static String convertFromISOLatin1(final String s) {
        StringBuffer sb = new StringBuffer(s);
        int index;
        while ((index = sb.toString().indexOf("\\U")) != -1) {
            final String string = "0000" + sb.toString().substring(index + 2, index + 6);
            final byte[] array = new byte[(string.length() - 4) / 2];
            for (int i = 4, n = 0; i < string.length(); i += 2, ++n) {
                array[n] = (byte)(Utility.hexOf(string.charAt(i)) << 4 & 0xF0);
                final byte[] array2 = array;
                final int n2 = n;
                array2[n2] |= (byte)(Utility.hexOf(string.charAt(i + 1)) << 0 & 0xF);
            }
            sb = new StringBuffer(delete(sb.toString(), index, index + 6));
            sb.insert(index, (char)array[1]);
        }
        return sb.toString();
    }
    
    private static String delete(final String s, final int n, final int n2) {
        return s.substring(0, n) + s.substring(n2, s.length());
    }
    
    private static String replace(String s, final String s2, final String s3) {
        for (int i = s.indexOf(s2); i != -1; i = s.indexOf(s2)) {
            s = new String(s.substring(0, i) + s3 + s.substring(i + s2.length()));
        }
        return s;
    }
    
    public static int computeValueTag(final boolean b, final int n, final boolean b2) {
        int n2 = 2147483392;
        if (b) {
            n2 |= 0x1;
        }
        int n3 = n2 | n;
        if (b2) {
            n3 |= 0x8;
        }
        return n3;
    }
    
    public static boolean isCodeBasePresent(final int n) {
        return (n & 0x1) == 0x1;
    }
    
    public static int getTypeInfo(final int n) {
        return n & 0x6;
    }
    
    public static boolean isChunkedEncoding(final int n) {
        return (n & 0x8) != 0x0;
    }
    
    public static String getServerURL() {
        return RepositoryId.defaultServerURL;
    }
    
    static {
        IDL_IDENTIFIER_CHARS = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1 };
        RepositoryId.defaultServerURL = null;
        RepositoryId.useCodebaseOnly = false;
        if (RepositoryId.defaultServerURL == null) {
            RepositoryId.defaultServerURL = JDKBridge.getLocalCodebase();
        }
        RepositoryId.useCodebaseOnly = JDKBridge.useCodebaseOnly();
        RepositoryId.classToRepStr = new IdentityHashtable();
        RepositoryId.classIDLToRepStr = new IdentityHashtable();
        RepositoryId.classSeqToRepStr = new IdentityHashtable();
        repStrToByteArray = new IdentityHashtable();
        RepositoryId.repStrToClass = new Hashtable();
        kValuePrefixLength = "RMI:".length();
        kIDLPrefixLength = "IDL:".length();
        kSequencePrefixLength = "[".length();
        kPreComputed_StandardRMIUnchunked = computeValueTag(false, 2, false);
        kPreComputed_CodeBaseRMIUnchunked = computeValueTag(true, 2, false);
        kPreComputed_StandardRMIChunked = computeValueTag(false, 2, true);
        kPreComputed_CodeBaseRMIChunked = computeValueTag(true, 2, true);
        kPreComputed_StandardRMIUnchunked_NoRep = computeValueTag(false, 0, false);
        kPreComputed_CodeBaseRMIUnchunked_NoRep = computeValueTag(true, 0, false);
        kPreComputed_StandardRMIChunked_NoRep = computeValueTag(false, 0, true);
        kPreComputed_CodeBaseRMIChunked_NoRep = computeValueTag(true, 0, true);
        kClassDescValueHash = ":" + Long.toHexString(ObjectStreamClass.getActualSerialVersionUID(ClassDesc.class)).toUpperCase() + ":" + Long.toHexString(ObjectStreamClass.getSerialVersionUID(ClassDesc.class)).toUpperCase();
        kClassDescValueRepID = "RMI:javax.rmi.CORBA.ClassDesc" + RepositoryId.kClassDescValueHash;
        (kSpecialArrayTypeStrings = new Hashtable()).put("CORBA.WStringValue", new StringBuffer(String.class.getName()));
        RepositoryId.kSpecialArrayTypeStrings.put("javax.rmi.CORBA.ClassDesc", new StringBuffer(Class.class.getName()));
        RepositoryId.kSpecialArrayTypeStrings.put("CORBA.Object", new StringBuffer(Remote.class.getName()));
        (kSpecialCasesRepIDs = new Hashtable()).put(String.class, "IDL:omg.org/CORBA/WStringValue:1.0");
        RepositoryId.kSpecialCasesRepIDs.put(Class.class, RepositoryId.kClassDescValueRepID);
        RepositoryId.kSpecialCasesRepIDs.put(Remote.class, "");
        (kSpecialCasesStubValues = new Hashtable()).put(String.class, "WStringValue");
        RepositoryId.kSpecialCasesStubValues.put(Class.class, "ClassDesc");
        RepositoryId.kSpecialCasesStubValues.put(Object.class, "Object");
        RepositoryId.kSpecialCasesStubValues.put(Serializable.class, "Serializable");
        RepositoryId.kSpecialCasesStubValues.put(Externalizable.class, "Externalizable");
        RepositoryId.kSpecialCasesStubValues.put(Remote.class, "");
        (kSpecialCasesVersions = new Hashtable()).put(String.class, ":1.0");
        RepositoryId.kSpecialCasesVersions.put(Class.class, RepositoryId.kClassDescValueHash);
        RepositoryId.kSpecialCasesVersions.put(Object.class, ":1.0");
        RepositoryId.kSpecialCasesVersions.put(Serializable.class, ":1.0");
        RepositoryId.kSpecialCasesVersions.put(Externalizable.class, ":1.0");
        RepositoryId.kSpecialCasesVersions.put(Remote.class, "");
        (kSpecialCasesClasses = new Hashtable()).put("omg.org/CORBA/WStringValue", String.class);
        RepositoryId.kSpecialCasesClasses.put("javax.rmi.CORBA.ClassDesc", Class.class);
        RepositoryId.kSpecialCasesClasses.put("", Remote.class);
        RepositoryId.kSpecialCasesClasses.put("org.omg.CORBA.WStringValue", String.class);
        RepositoryId.kSpecialCasesClasses.put("javax.rmi.CORBA.ClassDesc", Class.class);
        (kSpecialCasesArrayPrefix = new Hashtable()).put(String.class, "RMI:[CORBA/");
        RepositoryId.kSpecialCasesArrayPrefix.put(Class.class, "RMI:[javax/rmi/CORBA/");
        RepositoryId.kSpecialCasesArrayPrefix.put(Object.class, "RMI:[java/lang/");
        RepositoryId.kSpecialCasesArrayPrefix.put(Serializable.class, "RMI:[java/io/");
        RepositoryId.kSpecialCasesArrayPrefix.put(Externalizable.class, "RMI:[java/io/");
        RepositoryId.kSpecialCasesArrayPrefix.put(Remote.class, "RMI:[CORBA/");
        (kSpecialPrimitives = new Hashtable()).put("int", "long");
        RepositoryId.kSpecialPrimitives.put("long", "longlong");
        RepositoryId.kSpecialPrimitives.put("byte", "octet");
        ASCII_HEX = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
        cache = new RepositoryIdCache();
        kjava_rmi_Remote = createForAnyType(Remote.class);
        korg_omg_CORBA_Object = createForAnyType(org.omg.CORBA.Object.class);
        kNoParamTypes = new Class[0];
        kNoArgs = new Object[0];
    }
}
