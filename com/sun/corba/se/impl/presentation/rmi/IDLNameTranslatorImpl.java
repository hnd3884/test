package com.sun.corba.se.impl.presentation.rmi;

import java.util.HashSet;
import java.util.StringTokenizer;
import com.sun.corba.se.impl.orbutil.ObjectUtility;
import java.util.Iterator;
import java.security.AccessController;
import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.security.Permission;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;

public class IDLNameTranslatorImpl implements IDLNameTranslator
{
    private static String[] IDL_KEYWORDS;
    private static char[] HEX_DIGITS;
    private static final String UNDERSCORE = "_";
    private static final String INNER_CLASS_SEPARATOR = "__";
    private static final String[] BASE_IDL_ARRAY_MODULE_TYPE;
    private static final String BASE_IDL_ARRAY_ELEMENT_TYPE = "seq";
    private static final String LEADING_UNDERSCORE_CHAR = "J";
    private static final String ID_CONTAINER_CLASH_CHAR = "_";
    private static final String OVERLOADED_TYPE_SEPARATOR = "__";
    private static final String ATTRIBUTE_METHOD_CLASH_MANGLE_CHARS = "__";
    private static final String GET_ATTRIBUTE_PREFIX = "_get_";
    private static final String SET_ATTRIBUTE_PREFIX = "_set_";
    private static final String IS_ATTRIBUTE_PREFIX = "_get_";
    private static Set idlKeywords_;
    private Class[] interf_;
    private Map methodToIDLNameMap_;
    private Map IDLNameToMethodMap_;
    private Method[] methods_;
    
    public static IDLNameTranslator get(final Class clazz) {
        return new IDLNameTranslatorImpl(new Class[] { clazz });
    }
    
    public static IDLNameTranslator get(final Class[] array) {
        return new IDLNameTranslatorImpl(array);
    }
    
    public static String getExceptionId(final Class clazz) {
        return classToIDLType(clazz).getExceptionName();
    }
    
    @Override
    public Class[] getInterfaces() {
        return this.interf_;
    }
    
    @Override
    public Method[] getMethods() {
        return this.methods_;
    }
    
    @Override
    public Method getMethod(final String s) {
        return this.IDLNameToMethodMap_.get(s);
    }
    
    @Override
    public String getIDLName(final Method method) {
        return this.methodToIDLNameMap_.get(method);
    }
    
    private IDLNameTranslatorImpl(final Class[] interf_) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new DynamicAccessPermission("access"));
        }
        try {
            final IDLTypesUtil idlTypesUtil = new IDLTypesUtil();
            for (int i = 0; i < interf_.length; ++i) {
                idlTypesUtil.validateRemoteInterface(interf_[i]);
            }
            this.interf_ = interf_;
            this.buildNameTranslation();
        }
        catch (final IDLTypeException ex) {
            final IllegalStateException ex2 = new IllegalStateException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void buildNameTranslation() {
        final HashMap hashMap = new HashMap();
        for (int i = 0; i < this.interf_.length; ++i) {
            final Class clazz = this.interf_[i];
            final IDLTypesUtil idlTypesUtil = new IDLTypesUtil();
            final Method[] methods = clazz.getMethods();
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    AccessibleObject.setAccessible(methods, true);
                    return null;
                }
            });
            for (int j = 0; j < methods.length; ++j) {
                final Method method = methods[j];
                final IDLMethodInfo idlMethodInfo = new IDLMethodInfo();
                idlMethodInfo.method = method;
                if (idlTypesUtil.isPropertyAccessorMethod(method, clazz)) {
                    idlMethodInfo.isProperty = true;
                    final String attributeNameForProperty = idlTypesUtil.getAttributeNameForProperty(method.getName());
                    idlMethodInfo.originalName = attributeNameForProperty;
                    idlMethodInfo.mangledName = attributeNameForProperty;
                }
                else {
                    idlMethodInfo.isProperty = false;
                    idlMethodInfo.originalName = method.getName();
                    idlMethodInfo.mangledName = method.getName();
                }
                hashMap.put(method, idlMethodInfo);
            }
        }
        for (final IDLMethodInfo idlMethodInfo2 : hashMap.values()) {
            for (final IDLMethodInfo idlMethodInfo3 : hashMap.values()) {
                if (idlMethodInfo2 != idlMethodInfo3 && !idlMethodInfo2.originalName.equals(idlMethodInfo3.originalName) && idlMethodInfo2.originalName.equalsIgnoreCase(idlMethodInfo3.originalName)) {
                    idlMethodInfo2.mangledName = this.mangleCaseSensitiveCollision(idlMethodInfo2.originalName);
                    break;
                }
            }
        }
        for (final IDLMethodInfo idlMethodInfo4 : hashMap.values()) {
            idlMethodInfo4.mangledName = mangleIdentifier(idlMethodInfo4.mangledName, idlMethodInfo4.isProperty);
        }
        for (final IDLMethodInfo idlMethodInfo5 : hashMap.values()) {
            if (idlMethodInfo5.isProperty) {
                continue;
            }
            for (final IDLMethodInfo idlMethodInfo6 : hashMap.values()) {
                if (idlMethodInfo5 != idlMethodInfo6 && !idlMethodInfo6.isProperty && idlMethodInfo5.originalName.equals(idlMethodInfo6.originalName)) {
                    idlMethodInfo5.mangledName = mangleOverloadedMethod(idlMethodInfo5.mangledName, idlMethodInfo5.method);
                    break;
                }
            }
        }
        for (final IDLMethodInfo idlMethodInfo7 : hashMap.values()) {
            if (!idlMethodInfo7.isProperty) {
                continue;
            }
            for (final IDLMethodInfo idlMethodInfo8 : hashMap.values()) {
                if (idlMethodInfo7 != idlMethodInfo8 && !idlMethodInfo8.isProperty && idlMethodInfo7.mangledName.equals(idlMethodInfo8.mangledName)) {
                    idlMethodInfo7.mangledName += "__";
                    break;
                }
            }
        }
        for (int k = 0; k < this.interf_.length; ++k) {
            final String mappedContainerName = getMappedContainerName(this.interf_[k]);
            for (final IDLMethodInfo idlMethodInfo9 : hashMap.values()) {
                if (!idlMethodInfo9.isProperty && identifierClashesWithContainer(mappedContainerName, idlMethodInfo9.mangledName)) {
                    idlMethodInfo9.mangledName = mangleContainerClash(idlMethodInfo9.mangledName);
                }
            }
        }
        this.methodToIDLNameMap_ = new HashMap();
        this.IDLNameToMethodMap_ = new HashMap();
        this.methods_ = (Method[])hashMap.keySet().toArray(new Method[0]);
        for (final IDLMethodInfo idlMethodInfo10 : hashMap.values()) {
            String s = idlMethodInfo10.mangledName;
            if (idlMethodInfo10.isProperty) {
                final String name = idlMethodInfo10.method.getName();
                String s2;
                if (name.startsWith("get")) {
                    s2 = "_get_";
                }
                else if (name.startsWith("set")) {
                    s2 = "_set_";
                }
                else {
                    s2 = "_get_";
                }
                s = s2 + idlMethodInfo10.mangledName;
            }
            this.methodToIDLNameMap_.put(idlMethodInfo10.method, s);
            if (this.IDLNameToMethodMap_.containsKey(s)) {
                throw new IllegalStateException("Error : methods " + this.IDLNameToMethodMap_.get(s) + " and " + idlMethodInfo10.method + " both result in IDL name '" + s + "'");
            }
            this.IDLNameToMethodMap_.put(s, idlMethodInfo10.method);
        }
    }
    
    private static String mangleIdentifier(final String s) {
        return mangleIdentifier(s, false);
    }
    
    private static String mangleIdentifier(final String s, final boolean b) {
        String s2 = s;
        if (hasLeadingUnderscore(s2)) {
            s2 = mangleLeadingUnderscore(s2);
        }
        if (!b && isIDLKeyword(s2)) {
            s2 = mangleIDLKeywordClash(s2);
        }
        if (!isIDLIdentifier(s2)) {
            s2 = mangleUnicodeChars(s2);
        }
        return s2;
    }
    
    static boolean isIDLKeyword(final String s) {
        return IDLNameTranslatorImpl.idlKeywords_.contains(s.toUpperCase());
    }
    
    static String mangleIDLKeywordClash(final String s) {
        return "_" + s;
    }
    
    private static String mangleLeadingUnderscore(final String s) {
        return "J" + s;
    }
    
    private static boolean hasLeadingUnderscore(final String s) {
        return s.startsWith("_");
    }
    
    static String mangleUnicodeChars(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (isIDLIdentifierChar(char1)) {
                sb.append(char1);
            }
            else {
                sb.append(charToUnicodeRepresentation(char1));
            }
        }
        return sb.toString();
    }
    
    String mangleCaseSensitiveCollision(final String s) {
        final StringBuffer sb = new StringBuffer(s);
        sb.append("_");
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (Character.isUpperCase(s.charAt(i))) {
                if (n != 0) {
                    sb.append("_");
                }
                sb.append(i);
                n = 1;
            }
        }
        return sb.toString();
    }
    
    private static String mangleContainerClash(final String s) {
        return s + "_";
    }
    
    private static boolean identifierClashesWithContainer(final String s, final String s2) {
        return s2.equalsIgnoreCase(s);
    }
    
    public static String charToUnicodeRepresentation(final char c) {
        final StringBuffer sb = new StringBuffer();
        int n;
        for (int i = c; i > 0; i = n) {
            n = i / 16;
            sb.insert(0, IDLNameTranslatorImpl.HEX_DIGITS[i % 16]);
        }
        for (int n2 = 4 - sb.length(), j = 0; j < n2; ++j) {
            sb.insert(0, "0");
        }
        sb.insert(0, "U");
        return sb.toString();
    }
    
    private static boolean isIDLIdentifier(final String s) {
        boolean b = true;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            b = ((i == 0) ? isIDLAlphabeticChar(char1) : isIDLIdentifierChar(char1));
            if (!b) {
                break;
            }
        }
        return b;
    }
    
    private static boolean isIDLIdentifierChar(final char c) {
        return isIDLAlphabeticChar(c) || isIDLDecimalDigit(c) || isUnderscore(c);
    }
    
    private static boolean isIDLAlphabeticChar(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '\u00c0' && c <= '\u00ff' && c != '\u00d7' && c != '\u00f7');
    }
    
    private static boolean isIDLDecimalDigit(final char c) {
        return c >= '0' && c <= '9';
    }
    
    private static boolean isUnderscore(final char c) {
        return c == '_';
    }
    
    private static String mangleOverloadedMethod(final String s, final Method method) {
        final IDLTypesUtil idlTypesUtil = new IDLTypesUtil();
        String s2 = s + "__";
        final Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            final Class<?> clazz = parameterTypes[i];
            if (i > 0) {
                s2 += "__";
            }
            final IDLType classToIDLType = classToIDLType(clazz);
            final String moduleName = classToIDLType.getModuleName();
            final String memberName = classToIDLType.getMemberName();
            String mangleIDLKeywordClash = (moduleName.length() > 0) ? (moduleName + "_" + memberName) : memberName;
            if (!idlTypesUtil.isPrimitive(clazz) && idlTypesUtil.getSpecialCaseIDLTypeMapping(clazz) == null && isIDLKeyword(mangleIDLKeywordClash)) {
                mangleIDLKeywordClash = mangleIDLKeywordClash(mangleIDLKeywordClash);
            }
            s2 += mangleUnicodeChars(mangleIDLKeywordClash);
        }
        return s2;
    }
    
    private static IDLType classToIDLType(final Class clazz) {
        final IDLTypesUtil idlTypesUtil = new IDLTypesUtil();
        IDLType idlType;
        if (idlTypesUtil.isPrimitive(clazz)) {
            idlType = idlTypesUtil.getPrimitiveIDLTypeMapping(clazz);
        }
        else if (clazz.isArray()) {
            Class clazz2;
            int n;
            for (clazz2 = clazz.getComponentType(), n = 1; clazz2.isArray(); clazz2 = clazz2.getComponentType(), ++n) {}
            final IDLType classToIDLType = classToIDLType(clazz2);
            String[] base_IDL_ARRAY_MODULE_TYPE = IDLNameTranslatorImpl.BASE_IDL_ARRAY_MODULE_TYPE;
            if (classToIDLType.hasModule()) {
                base_IDL_ARRAY_MODULE_TYPE = (String[])ObjectUtility.concatenateArrays(base_IDL_ARRAY_MODULE_TYPE, classToIDLType.getModules());
            }
            idlType = new IDLType(clazz, base_IDL_ARRAY_MODULE_TYPE, "seq" + n + "_" + classToIDLType.getMemberName());
        }
        else {
            idlType = idlTypesUtil.getSpecialCaseIDLTypeMapping(clazz);
            if (idlType == null) {
                String s = getUnmappedContainerName(clazz).replaceAll("\\$", "__");
                if (hasLeadingUnderscore(s)) {
                    s = mangleLeadingUnderscore(s);
                }
                String s2 = getPackageName(clazz);
                if (s2 == null) {
                    idlType = new IDLType(clazz, s);
                }
                else {
                    if (idlTypesUtil.isEntity(clazz)) {
                        s2 = "org.omg.boxedIDL." + s2;
                    }
                    final StringTokenizer stringTokenizer = new StringTokenizer(s2, ".");
                    final String[] array = new String[stringTokenizer.countTokens()];
                    int n2 = 0;
                    while (stringTokenizer.hasMoreElements()) {
                        final String nextToken = stringTokenizer.nextToken();
                        array[n2++] = (hasLeadingUnderscore(nextToken) ? mangleLeadingUnderscore(nextToken) : nextToken);
                    }
                    idlType = new IDLType(clazz, array, s);
                }
            }
        }
        return idlType;
    }
    
    private static String getPackageName(final Class clazz) {
        final Package package1 = clazz.getPackage();
        String name;
        if (package1 != null) {
            name = package1.getName();
        }
        else {
            final String name2 = clazz.getName();
            final int index = name2.indexOf(46);
            name = ((index == -1) ? null : name2.substring(0, index));
        }
        return name;
    }
    
    private static String getMappedContainerName(final Class clazz) {
        return mangleIdentifier(getUnmappedContainerName(clazz));
    }
    
    private static String getUnmappedContainerName(final Class clazz) {
        final String packageName = getPackageName(clazz);
        final String name = clazz.getName();
        String substring;
        if (packageName != null) {
            substring = name.substring(packageName.length() + 1);
        }
        else {
            substring = name;
        }
        return substring;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("IDLNameTranslator[");
        for (int i = 0; i < this.interf_.length; ++i) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(this.interf_[i].getName());
        }
        sb.append("]\n");
        for (final Method method : this.methodToIDLNameMap_.keySet()) {
            sb.append((String)this.methodToIDLNameMap_.get(method) + ":" + method + "\n");
        }
        return sb.toString();
    }
    
    static {
        IDLNameTranslatorImpl.IDL_KEYWORDS = new String[] { "abstract", "any", "attribute", "boolean", "case", "char", "const", "context", "custom", "default", "double", "enum", "exception", "factory", "FALSE", "fixed", "float", "in", "inout", "interface", "long", "module", "native", "Object", "octet", "oneway", "out", "private", "public", "raises", "readonly", "sequence", "short", "string", "struct", "supports", "switch", "TRUE", "truncatable", "typedef", "unsigned", "union", "ValueBase", "valuetype", "void", "wchar", "wstring" };
        IDLNameTranslatorImpl.HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        BASE_IDL_ARRAY_MODULE_TYPE = new String[] { "org", "omg", "boxedRMI" };
        IDLNameTranslatorImpl.idlKeywords_ = new HashSet();
        for (int i = 0; i < IDLNameTranslatorImpl.IDL_KEYWORDS.length; ++i) {
            IDLNameTranslatorImpl.idlKeywords_.add(IDLNameTranslatorImpl.IDL_KEYWORDS[i].toUpperCase());
        }
    }
    
    private static class IDLMethodInfo
    {
        public Method method;
        public boolean isProperty;
        public String originalName;
        public String mangledName;
    }
}
