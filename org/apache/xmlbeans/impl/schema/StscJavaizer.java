package org.apache.xmlbeans.impl.schema;

import java.math.BigInteger;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.SchemaStringEnumEntry;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.apache.xmlbeans.UserType;
import org.apache.xmlbeans.PrePostExtension;
import org.apache.xmlbeans.InterfaceExtension;
import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.XmlObject;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;

public class StscJavaizer
{
    private static final int MAX_ENUM_COUNT = 3668;
    private static final String[] PREFIXES;
    static String[] PROTECTED_PROPERTIES;
    static Set PROTECTED_PROPERTIES_SET;
    
    public static void javaizeAllTypes(final boolean javaize) {
        final StscState state = StscState.get();
        final List allSeenTypes = new ArrayList();
        allSeenTypes.addAll(Arrays.asList(state.documentTypes()));
        allSeenTypes.addAll(Arrays.asList(state.attributeTypes()));
        allSeenTypes.addAll(Arrays.asList(state.globalTypes()));
        if (javaize) {
            assignGlobalJavaNames(allSeenTypes);
        }
        for (int i = 0; i < allSeenTypes.size(); ++i) {
            final SchemaType gType = allSeenTypes.get(i);
            if (javaize) {
                javaizeType((SchemaTypeImpl)gType);
                final String className = gType.getFullJavaName();
                if (className != null) {
                    state.addClassname(className.replace('$', '.'), gType);
                }
            }
            else {
                skipJavaizingType((SchemaTypeImpl)gType);
            }
            allSeenTypes.addAll(Arrays.asList(gType.getAnonymousTypes()));
            addAnonymousTypesFromRedefinition(gType, allSeenTypes);
        }
    }
    
    static void assignGlobalJavaNames(final Collection schemaTypes) {
        final HashSet usedNames = new HashSet();
        final StscState state = StscState.get();
        for (final SchemaTypeImpl sImpl : schemaTypes) {
            final QName topName = findTopName(sImpl);
            final String pickedName = state.getJavaname(topName, sImpl.isDocumentType() ? 2 : 1);
            if (sImpl.isUnjavaized()) {
                sImpl.setFullJavaName(pickFullJavaClassName(usedNames, findTopName(sImpl), pickedName, sImpl.isDocumentType(), sImpl.isAttributeType()));
                sImpl.setFullJavaImplName(pickFullJavaImplName(usedNames, sImpl.getFullJavaName()));
                setUserTypes(sImpl, state);
                setExtensions(sImpl, state);
            }
        }
        verifyInterfaceNameCollisions(usedNames, state);
    }
    
    private static void verifyInterfaceNameCollisions(final Set usedNames, final StscState state) {
        final BindingConfig config = state.getBindingConfig();
        if (config == null) {
            return;
        }
        final InterfaceExtension[] exts = config.getInterfaceExtensions();
        for (int i = 0; i < exts.length; ++i) {
            if (usedNames.contains(exts[i].getInterface().toLowerCase())) {
                state.error("InterfaceExtension interface '" + exts[i].getInterface() + "' creates a name collision with one of the generated interfaces or classes.", 0, null);
            }
            final String handler = exts[i].getStaticHandler();
            if (handler != null && usedNames.contains(handler.toLowerCase())) {
                state.error("InterfaceExtension handler class '" + handler + "' creates a name collision with one of the generated interfaces or classes.", 0, null);
            }
        }
        final PrePostExtension[] prepost = config.getPrePostExtensions();
        for (int j = 0; j < prepost.length; ++j) {
            final String handler2 = prepost[j].getStaticHandler();
            if (handler2 != null && usedNames.contains(handler2.toLowerCase())) {
                state.error("PrePostExtension handler class '" + handler2 + "' creates a name collision with one of the generated interfaces or classes.", 0, null);
            }
        }
    }
    
    private static void setUserTypes(final SchemaTypeImpl sImpl, final StscState state) {
        final BindingConfig config = state.getBindingConfig();
        if (config != null) {
            final UserType utype = config.lookupUserTypeForQName(sImpl.getName());
            if (utype != null) {
                sImpl.setUserTypeName(utype.getJavaName());
                sImpl.setUserTypeHandlerName(utype.getStaticHandler());
            }
        }
    }
    
    private static void setExtensions(final SchemaTypeImpl sImpl, final StscState state) {
        final String javaName = sImpl.getFullJavaName();
        final BindingConfig config = state.getBindingConfig();
        if (javaName != null && config != null) {
            sImpl.setInterfaceExtensions(config.getInterfaceExtensions(javaName));
            sImpl.setPrePostExtension(config.getPrePostExtension(javaName));
        }
    }
    
    private static boolean isStringType(final SchemaType type) {
        return type != null && type.getSimpleVariety() == 1 && type.getPrimitiveType().getBuiltinTypeCode() == 12;
    }
    
    static String pickConstantName(final Set usedNames, final String words) {
        String base = NameUtil.upperCaseUnderbar(words);
        if (base.length() == 0) {
            base = "X";
        }
        if (base.startsWith("INT_")) {
            base = "X_" + base;
        }
        int index;
        String uniqName;
        for (index = 1, uniqName = base; usedNames.contains(uniqName); uniqName = base + "_" + index) {
            ++index;
        }
        usedNames.add(uniqName);
        return uniqName;
    }
    
    static void skipJavaizingType(final SchemaTypeImpl sImpl) {
        if (sImpl.isJavaized()) {
            return;
        }
        final SchemaTypeImpl baseType = (SchemaTypeImpl)sImpl.getBaseType();
        if (baseType != null) {
            skipJavaizingType(baseType);
        }
        sImpl.startJavaizing();
        secondPassProcessType(sImpl);
        sImpl.finishJavaizing();
    }
    
    static void secondPassProcessType(SchemaTypeImpl sImpl) {
        if (isStringType(sImpl)) {
            final XmlAnySimpleType[] enumVals = sImpl.getEnumerationValues();
            if (enumVals != null) {
                if (enumVals.length > 3668) {
                    StscState.get().warning("SchemaType Enumeration found with too many enumeration values to create a Java enumeration. The base SchemaType \"" + sImpl.getBaseEnumType() + "\" will be used instead", 1, null);
                    sImpl = (SchemaTypeImpl)sImpl.getBaseEnumType();
                }
                else {
                    final SchemaStringEnumEntry[] entryArray = new SchemaStringEnumEntry[enumVals.length];
                    final SchemaType basedOn = sImpl.getBaseEnumType();
                    if (basedOn == sImpl) {
                        final Set usedNames = new HashSet();
                        for (int i = 0; i < enumVals.length; ++i) {
                            final String val = enumVals[i].getStringValue();
                            entryArray[i] = new SchemaStringEnumEntryImpl(val, i + 1, pickConstantName(usedNames, val));
                        }
                    }
                    else {
                        for (int j = 0; j < enumVals.length; ++j) {
                            final String val2 = enumVals[j].getStringValue();
                            entryArray[j] = basedOn.enumEntryForString(val2);
                        }
                    }
                    sImpl.setStringEnumEntries(entryArray);
                }
            }
        }
    }
    
    static void javaizeType(final SchemaTypeImpl sImpl) {
        if (sImpl.isJavaized()) {
            return;
        }
        final SchemaTypeImpl baseType = (SchemaTypeImpl)sImpl.getBaseType();
        if (baseType != null) {
            javaizeType(baseType);
        }
        if (sImpl.getContentBasedOnType() != null && sImpl.getContentBasedOnType() != baseType) {
            javaizeType((SchemaTypeImpl)sImpl.getContentBasedOnType());
        }
        sImpl.startJavaizing();
        sImpl.setCompiled(true);
        secondPassProcessType(sImpl);
        if (!sImpl.isSimpleType()) {
            final SchemaProperty[] eltProps = sImpl.getElementProperties();
            final SchemaProperty[] attrProps = sImpl.getAttributeProperties();
            final Set usedPropNames = new HashSet();
            final SchemaProperty[] baseProps = baseType.getProperties();
            for (int i = 0; i < baseProps.length; ++i) {
                final String name = baseProps[i].getJavaPropertyName();
                assert !usedPropNames.contains(name);
                usedPropNames.add(name);
            }
            avoidExtensionMethods(usedPropNames, sImpl);
            boolean doInherited = true;
            while (true) {
                if (eltProps.length > 0) {
                    assignJavaPropertyNames(usedPropNames, eltProps, baseType, doInherited);
                }
                assignJavaPropertyNames(usedPropNames, attrProps, baseType, doInherited);
                if (!doInherited) {
                    break;
                }
                doInherited = false;
            }
            final SchemaProperty[] allprops = sImpl.getProperties();
            final boolean insensitive = isPropertyModelOrderInsensitive(allprops);
            assignJavaTypeCodes(allprops);
            sImpl.setOrderSensitive(!insensitive);
        }
        if (sImpl.getFullJavaName() != null || sImpl.getOuterType() != null) {
            assignJavaAnonymousTypeNames(sImpl);
        }
        sImpl.finishJavaizing();
    }
    
    private static void avoidExtensionMethods(final Set usedPropNames, final SchemaTypeImpl sImpl) {
        final InterfaceExtension[] exts = sImpl.getInterfaceExtensions();
        if (exts != null) {
            for (int i = 0; i < exts.length; ++i) {
                final InterfaceExtension ext = exts[i];
                final InterfaceExtension.MethodSignature[] methods = ext.getMethods();
                for (int j = 0; j < methods.length; ++j) {
                    final String methodName = methods[j].getName();
                    for (int k = 0; k < StscJavaizer.PREFIXES.length; ++k) {
                        final String prefix = StscJavaizer.PREFIXES[k];
                        if (methodName.startsWith(prefix)) {
                            usedPropNames.add(methodName.substring(prefix.length()));
                        }
                    }
                }
            }
        }
    }
    
    static void assignJavaAnonymousTypeNames(final SchemaTypeImpl outerType) {
        final Set usedTypeNames = new HashSet();
        SchemaType[] anonymousTypes = outerType.getAnonymousTypes();
        final StscState state = StscState.get();
        final int nrOfAnonTypes = anonymousTypes.length;
        if (outerType.isRedefinition()) {
            final ArrayList list = new ArrayList();
            addAnonymousTypesFromRedefinition(outerType, list);
            if (list.size() > 0) {
                final SchemaType[] temp = new SchemaType[nrOfAnonTypes + list.size()];
                list.toArray(temp);
                System.arraycopy(anonymousTypes, 0, temp, list.size(), nrOfAnonTypes);
                anonymousTypes = temp;
            }
        }
        for (SchemaType scanOuterType = outerType; scanOuterType != null; scanOuterType = scanOuterType.getOuterType()) {
            usedTypeNames.add(scanOuterType.getShortJavaName());
        }
        for (SchemaType scanOuterType = outerType; scanOuterType != null; scanOuterType = scanOuterType.getOuterType()) {
            usedTypeNames.add(scanOuterType.getShortJavaImplName());
        }
        usedTypeNames.add(getOutermostPackage(outerType.getFullJavaName()));
        for (int i = 0; i < anonymousTypes.length; ++i) {
            final SchemaTypeImpl sImpl = (SchemaTypeImpl)anonymousTypes[i];
            if (sImpl != null) {
                if (!sImpl.isSkippedAnonymousType()) {
                    String localname = null;
                    String javaname = null;
                    final SchemaField containerField = sImpl.getContainerField();
                    if (containerField != null) {
                        final QName qname = sImpl.getContainerField().getName();
                        localname = qname.getLocalPart();
                        javaname = state.getJavaname(sImpl.getContainerField().getName(), 1);
                    }
                    else {
                        switch (sImpl.getOuterType().getSimpleVariety()) {
                            case 2: {
                                javaname = "Member";
                                break;
                            }
                            case 3: {
                                javaname = "Item";
                                break;
                            }
                            default: {
                                assert false : "Weird type " + sImpl.toString();
                                javaname = "Base";
                                break;
                            }
                        }
                    }
                    if (i < nrOfAnonTypes) {
                        sImpl.setShortJavaName(pickInnerJavaClassName(usedTypeNames, localname, javaname));
                        sImpl.setShortJavaImplName(pickInnerJavaImplName(usedTypeNames, localname, (javaname == null) ? null : (javaname + "Impl")));
                    }
                    else {
                        sImpl.setFullJavaName(outerType.getFullJavaName() + "$" + pickInnerJavaClassName(usedTypeNames, localname, javaname));
                        sImpl.setFullJavaImplName(outerType.getFullJavaImplName() + "$" + pickInnerJavaImplName(usedTypeNames, localname, (javaname == null) ? null : (javaname + "Impl")));
                    }
                    setExtensions(sImpl, state);
                }
            }
        }
    }
    
    static void assignJavaPropertyNames(final Set usedNames, final SchemaProperty[] props, final SchemaType baseType, final boolean doInherited) {
        final StscState state = StscState.get();
        for (int i = 0; i < props.length; ++i) {
            final SchemaPropertyImpl sImpl = (SchemaPropertyImpl)props[i];
            final SchemaProperty baseProp = sImpl.isAttribute() ? baseType.getAttributeProperty(sImpl.getName()) : baseType.getElementProperty(sImpl.getName());
            if (baseProp != null == doInherited) {
                final QName propQName = sImpl.getName();
                String theName;
                if (baseProp == null) {
                    theName = pickJavaPropertyName(usedNames, propQName.getLocalPart(), state.getJavaname(propQName, sImpl.isAttribute() ? 4 : 3));
                }
                else {
                    theName = baseProp.getJavaPropertyName();
                }
                sImpl.setJavaPropertyName(theName);
                boolean isArray = sImpl.getMaxOccurs() == null || sImpl.getMaxOccurs().compareTo(BigInteger.ONE) > 0;
                boolean isSingleton = !isArray && sImpl.getMaxOccurs().signum() > 0;
                boolean isOption = isSingleton && sImpl.getMinOccurs().signum() == 0;
                SchemaType javaBasedOnType = sImpl.getType();
                if (baseProp != null) {
                    if (baseProp.extendsJavaArray()) {
                        isSingleton = false;
                        isOption = false;
                        isArray = true;
                    }
                    if (baseProp.extendsJavaSingleton()) {
                        isSingleton = true;
                    }
                    if (baseProp.extendsJavaOption()) {
                        isOption = true;
                    }
                    javaBasedOnType = baseProp.javaBasedOnType();
                }
                sImpl.setExtendsJava(javaBasedOnType.getRef(), isSingleton, isOption, isArray);
            }
        }
    }
    
    static void assignJavaTypeCodes(final SchemaProperty[] properties) {
        for (int i = 0; i < properties.length; ++i) {
            final SchemaPropertyImpl sImpl = (SchemaPropertyImpl)properties[i];
            final SchemaType sType = sImpl.javaBasedOnType();
            sImpl.setJavaTypeCode(javaTypeCodeForType(sType));
        }
    }
    
    static int javaTypeCodeInCommon(final SchemaType[] types) {
        if (types == null || types.length == 0) {
            return 0;
        }
        final int code = javaTypeCodeForType(types[0]);
        if (code == 19) {
            return code;
        }
        for (int i = 1; i < types.length; ++i) {
            if (code != javaTypeCodeForType(types[i])) {
                return 19;
            }
        }
        return code;
    }
    
    static int javaTypeCodeForType(SchemaType sType) {
        if (!sType.isSimpleType()) {
            return 0;
        }
        if (((SchemaTypeImpl)sType).getUserTypeHandlerName() != null) {
            return 20;
        }
        if (sType.getSimpleVariety() == 2) {
            final SchemaType baseType = sType.getUnionCommonBaseType();
            if (baseType == null || baseType.isURType()) {
                return javaTypeCodeInCommon(sType.getUnionConstituentTypes());
            }
            sType = baseType;
        }
        if (sType.getSimpleVariety() == 3) {
            return 16;
        }
        if (sType.isURType()) {
            return 0;
        }
        switch (sType.getPrimitiveType().getBuiltinTypeCode()) {
            case 2: {
                return 10;
            }
            case 3: {
                return 1;
            }
            case 4: {
                return 11;
            }
            case 5: {
                return 11;
            }
            case 6: {
                return 10;
            }
            case 7: {
                return 15;
            }
            case 8: {
                return 0;
            }
            case 9: {
                return 2;
            }
            case 10: {
                return 3;
            }
            case 11: {
                switch (sType.getDecimalSize()) {
                    case 8: {
                        return 4;
                    }
                    case 16: {
                        return 5;
                    }
                    case 32: {
                        return 6;
                    }
                    case 64: {
                        return 7;
                    }
                    case 1000000: {
                        return 9;
                    }
                    default: {
                        return 8;
                    }
                }
                break;
            }
            case 12: {
                if (!isStringType(sType.getBaseEnumType())) {
                    return 10;
                }
                if (sType.getEnumerationValues() != null && sType.getEnumerationValues().length > 3668) {
                    return 10;
                }
                return 18;
            }
            case 13: {
                return 13;
            }
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                return 17;
            }
            default: {
                assert false : "unrecognized code " + sType.getPrimitiveType().getBuiltinTypeCode();
                throw new IllegalStateException("unrecognized code " + sType.getPrimitiveType().getBuiltinTypeCode() + " of " + sType.getPrimitiveType().getName());
            }
        }
    }
    
    static boolean isPropertyModelOrderInsensitive(final SchemaProperty[] properties) {
        for (int i = 0; i < properties.length; ++i) {
            final SchemaProperty prop = properties[i];
            if (prop.hasNillable() == 1) {
                return false;
            }
            if (prop.hasDefault() == 1) {
                return false;
            }
            if (prop.hasFixed() == 1) {
                return false;
            }
            if (prop.hasDefault() != 0 && prop.getDefaultText() == null) {
                return false;
            }
        }
        return true;
    }
    
    static boolean protectReservedGlobalClassNames(final String name) {
        final int i = name.lastIndexOf(46);
        final String lastSegment = name.substring(i + 1);
        return lastSegment.endsWith("Document") && !lastSegment.equals("Document");
    }
    
    static boolean protectReservedInnerClassNames(final String name) {
        return name.equals("Enum") || name.equals("Factory");
    }
    
    static boolean protectReservedPropertyNames(final String name) {
        return StscJavaizer.PROTECTED_PROPERTIES_SET.contains(name) || (name.endsWith("Array") && !name.equals("Array"));
    }
    
    static String pickFullJavaClassName(final Set usedNames, final QName qName, final String configname, final boolean isDocument, final boolean isAttrType) {
        String base;
        boolean protect;
        if (configname != null && configname.indexOf(46) >= 0) {
            base = configname;
            protect = protectReservedGlobalClassNames(base);
        }
        else {
            final StscState state = StscState.get();
            final String uri = qName.getNamespaceURI();
            base = NameUtil.getClassNameFromQName(qName);
            final String pkgPrefix = state.getPackageOverride(uri);
            if (pkgPrefix != null) {
                base = pkgPrefix + "." + base.substring(base.lastIndexOf(46) + 1);
            }
            final String javaPrefix = state.getJavaPrefix(uri);
            if (javaPrefix != null) {
                base = base.substring(0, base.lastIndexOf(46) + 1) + javaPrefix + base.substring(base.lastIndexOf(46) + 1);
            }
            if (configname != null) {
                base = base.substring(0, base.lastIndexOf(46) + 1) + configname;
            }
            protect = protectReservedGlobalClassNames(base);
            if (configname == null) {
                if (isDocument) {
                    base += "Document";
                }
                else if (isAttrType) {
                    base += "Attribute";
                }
                final String javaSuffix = state.getJavaSuffix(uri);
                if (javaSuffix != null) {
                    base += javaSuffix;
                }
            }
        }
        final String outermostPkg = getOutermostPackage(base);
        int index = 1;
        String uniqName;
        if (protect) {
            uniqName = base + index;
        }
        else {
            uniqName = base;
        }
        while (usedNames.contains(uniqName.toLowerCase()) || uniqName.equals(outermostPkg)) {
            ++index;
            uniqName = base + index;
        }
        usedNames.add(uniqName.toLowerCase());
        return uniqName;
    }
    
    static String getOutermostPackage(final String fqcn) {
        if (fqcn == null) {
            return "";
        }
        final int lastdot = fqcn.indexOf(46);
        if (lastdot < 0) {
            return "";
        }
        return fqcn.substring(0, lastdot);
    }
    
    static String pickFullJavaImplName(final Set usedNames, final String intfName) {
        String className = intfName;
        String pkgName = null;
        int index = intfName.lastIndexOf(46);
        if (index >= 0) {
            className = intfName.substring(index + 1);
            pkgName = intfName.substring(0, index);
        }
        String base;
        String uniqName;
        for (base = pkgName + ".impl." + className + "Impl", index = 1, uniqName = base; usedNames.contains(uniqName.toLowerCase()); uniqName = base + index) {
            ++index;
        }
        usedNames.add(uniqName.toLowerCase());
        return uniqName;
    }
    
    static String pickJavaPropertyName(final Set usedNames, final String localName, String javaName) {
        if (javaName == null) {
            javaName = NameUtil.upperCamelCase(localName);
        }
        final boolean protect = protectReservedPropertyNames(javaName);
        int index = 1;
        String uniqName;
        if (protect) {
            uniqName = javaName + index;
        }
        else {
            uniqName = javaName;
        }
        while (usedNames.contains(uniqName)) {
            ++index;
            uniqName = javaName + index;
        }
        usedNames.add(uniqName);
        return uniqName;
    }
    
    static String pickInnerJavaClassName(final Set usedNames, final String localName, String javaName) {
        if (javaName == null) {
            javaName = NameUtil.upperCamelCase(localName);
        }
        final boolean protect = protectReservedInnerClassNames(javaName);
        int index = 1;
        String uniqName;
        if (protect) {
            uniqName = javaName + index;
        }
        else {
            uniqName = javaName;
        }
        while (usedNames.contains(uniqName)) {
            ++index;
            uniqName = javaName + index;
        }
        usedNames.add(uniqName);
        return uniqName;
    }
    
    static String pickInnerJavaImplName(final Set usedNames, final String localName, String javaName) {
        if (javaName == null) {
            javaName = NameUtil.upperCamelCase(localName) + "Impl";
        }
        String uniqName = javaName;
        for (int index = 1; usedNames.contains(uniqName); uniqName = javaName + index) {
            ++index;
        }
        usedNames.add(uniqName);
        return uniqName;
    }
    
    static QName findTopName(final SchemaType sType) {
        if (sType.getName() != null) {
            return sType.getName();
        }
        if (sType.isDocumentType()) {
            if (sType.getContentModel() == null || sType.getContentModel().getParticleType() != 4) {
                throw new IllegalStateException();
            }
            return sType.getDocumentElementName();
        }
        else if (sType.isAttributeType()) {
            if (sType.getAttributeModel() == null || sType.getAttributeModel().getAttributes().length != 1) {
                throw new IllegalStateException();
            }
            return sType.getAttributeTypeAttributeName();
        }
        else {
            final SchemaField sElt = sType.getContainerField();
            assert sElt != null;
            assert sType.getOuterType() == null;
            return sElt.getName();
        }
    }
    
    static void addAnonymousTypesFromRedefinition(SchemaType sType, final List result) {
        while (((SchemaTypeImpl)sType).isRedefinition() && (sType.getDerivationType() == 2 || sType.isSimpleType())) {
            sType = sType.getBaseType();
            final SchemaType[] newAnonTypes = sType.getAnonymousTypes();
            if (newAnonTypes.length > 0) {
                result.addAll(Arrays.asList(newAnonTypes));
            }
        }
    }
    
    static {
        PREFIXES = new String[] { "get", "xget", "isNil", "isSet", "sizeOf", "set", "xset", "addNew", "setNil", "unset", "insert", "add", "insertNew", "addNew", "remove" };
        StscJavaizer.PROTECTED_PROPERTIES = new String[] { "StringValue", "BooleanValue", "ByteValue", "ShortValue", "IntValue", "LongValue", "BigIntegerValue", "BigDecimalValue", "FloatValue", "DoubleValue", "ByteArrayValue", "EnumValue", "CalendarValue", "DateValue", "GDateValue", "GDurationValue", "QNameValue", "ListValue", "ObjectValue", "Class" };
        StscJavaizer.PROTECTED_PROPERTIES_SET = new HashSet(Arrays.asList(StscJavaizer.PROTECTED_PROPERTIES));
    }
}
