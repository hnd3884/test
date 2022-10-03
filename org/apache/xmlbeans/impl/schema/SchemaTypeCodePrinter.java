package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SystemProperties;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.PrePostExtension;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.apache.xmlbeans.SchemaStringEnumEntry;
import java.util.HashSet;
import org.apache.xmlbeans.InterfaceExtension;
import org.apache.xmlbeans.SchemaProperty;
import javax.xml.namespace.QName;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharacterCodingException;
import org.apache.xmlbeans.SchemaTypeSystem;
import java.io.IOException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaType;
import java.io.Writer;
import org.apache.xmlbeans.SchemaCodePrinter;

public final class SchemaTypeCodePrinter implements SchemaCodePrinter
{
    Writer _writer;
    int _indent;
    boolean _useJava15;
    static final String LINE_SEPARATOR;
    static final String MAX_SPACES = "                                        ";
    static final int INDENT_INCREMENT = 4;
    public static final String INDEX_CLASSNAME = "TypeSystemHolder";
    private static final int NOTHING = 1;
    private static final int ADD_NEW_VALUE = 3;
    private static final int THROW_EXCEPTION = 4;
    
    public static void printTypeImpl(final Writer writer, final SchemaType sType, final XmlOptions opt) throws IOException {
        getPrinter(opt).printTypeImpl(writer, sType);
    }
    
    public static void printType(final Writer writer, final SchemaType sType, final XmlOptions opt) throws IOException {
        getPrinter(opt).printType(writer, sType);
    }
    
    @Deprecated
    public static void printLoader(final Writer writer, final SchemaTypeSystem system, final XmlOptions opt) throws IOException {
        getPrinter(opt).printLoader(writer, system);
    }
    
    private static SchemaCodePrinter getPrinter(final XmlOptions opt) {
        Object printer = XmlOptions.safeGet(opt, "SCHEMA_CODE_PRINTER");
        if (printer == null || !(printer instanceof SchemaCodePrinter)) {
            printer = new SchemaTypeCodePrinter(opt);
        }
        return (SchemaCodePrinter)printer;
    }
    
    public SchemaTypeCodePrinter(final XmlOptions opt) {
        this._indent = 0;
        String genversion = null;
        if (opt != null && XmlOptions.hasOption(opt, "GENERATE_JAVA_VERSION")) {
            genversion = (String)opt.get("GENERATE_JAVA_VERSION");
        }
        if (genversion == null) {
            genversion = "1.4";
        }
        this._useJava15 = "1.5".equals(genversion);
    }
    
    void indent() {
        this._indent += 4;
    }
    
    void outdent() {
        this._indent -= 4;
    }
    
    String encodeString(final String s) {
        final StringBuffer sb = new StringBuffer();
        sb.append('\"');
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (ch == '\"') {
                sb.append('\\');
                sb.append('\"');
            }
            else if (ch == '\\') {
                sb.append('\\');
                sb.append('\\');
            }
            else if (ch == '\r') {
                sb.append('\\');
                sb.append('r');
            }
            else if (ch == '\n') {
                sb.append('\\');
                sb.append('n');
            }
            else if (ch == '\t') {
                sb.append('\\');
                sb.append('t');
            }
            else {
                sb.append(ch);
            }
        }
        sb.append('\"');
        return sb.toString();
    }
    
    void emit(final String s) throws IOException {
        int indent = this._indent;
        if (indent > "                                        ".length() / 2) {
            indent = "                                        ".length() / 4 + indent / 2;
        }
        if (indent > "                                        ".length()) {
            indent = "                                        ".length();
        }
        this._writer.write("                                        ".substring(0, indent));
        try {
            this._writer.write(s);
        }
        catch (final CharacterCodingException cce) {
            this._writer.write(makeSafe(s));
        }
        this._writer.write(SchemaTypeCodePrinter.LINE_SEPARATOR);
    }
    
    private static String makeSafe(final String s) {
        final Charset charset = Charset.forName(System.getProperty("file.encoding"));
        if (charset == null) {
            throw new IllegalStateException("Default character set is null!");
        }
        final CharsetEncoder cEncoder = charset.newEncoder();
        final StringBuffer result = new StringBuffer();
        int i;
        for (i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (!cEncoder.canEncode(c)) {
                break;
            }
        }
        while (i < s.length()) {
            final char c = s.charAt(i);
            if (cEncoder.canEncode(c)) {
                result.append(c);
            }
            else {
                final String hexValue = Integer.toHexString(c);
                switch (hexValue.length()) {
                    case 1: {
                        result.append("\\u000").append(hexValue);
                        break;
                    }
                    case 2: {
                        result.append("\\u00").append(hexValue);
                        break;
                    }
                    case 3: {
                        result.append("\\u0").append(hexValue);
                        break;
                    }
                    case 4: {
                        result.append("\\u").append(hexValue);
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
            }
            ++i;
        }
        return result.toString();
    }
    
    @Override
    public void printType(final Writer writer, final SchemaType sType) throws IOException {
        this._writer = writer;
        this.printTopComment(sType);
        this.printPackage(sType, true);
        this.emit("");
        this.printInnerType(sType, sType.getTypeSystem());
        this._writer.flush();
    }
    
    @Override
    public void printTypeImpl(final Writer writer, final SchemaType sType) throws IOException {
        this._writer = writer;
        this.printTopComment(sType);
        this.printPackage(sType, false);
        this.printInnerTypeImpl(sType, sType.getTypeSystem(), false);
    }
    
    String findJavaType(SchemaType sType) {
        while (sType.getFullJavaName() == null) {
            sType = sType.getBaseType();
        }
        return sType.getFullJavaName();
    }
    
    static String prettyQName(final QName qname) {
        String result = qname.getLocalPart();
        if (qname.getNamespaceURI() != null) {
            result = result + "(@" + qname.getNamespaceURI() + ")";
        }
        return result;
    }
    
    void printInnerTypeJavaDoc(final SchemaType sType) throws IOException {
        QName name = sType.getName();
        if (name == null) {
            if (sType.isDocumentType()) {
                name = sType.getDocumentElementName();
            }
            else if (sType.isAttributeType()) {
                name = sType.getAttributeTypeAttributeName();
            }
            else if (sType.getContainerField() != null) {
                name = sType.getContainerField().getName();
            }
        }
        this.emit("/**");
        if (sType.isDocumentType()) {
            this.emit(" * A document containing one " + prettyQName(name) + " element.");
        }
        else if (sType.isAttributeType()) {
            this.emit(" * A document containing one " + prettyQName(name) + " attribute.");
        }
        else if (name != null) {
            this.emit(" * An XML " + prettyQName(name) + ".");
        }
        else {
            this.emit(" * An anonymous inner XML type.");
        }
        this.emit(" *");
        switch (sType.getSimpleVariety()) {
            case 0: {
                this.emit(" * This is a complex type.");
                break;
            }
            case 1: {
                this.emit(" * This is an atomic type that is a restriction of " + this.getFullJavaName(sType) + ".");
                break;
            }
            case 3: {
                this.emit(" * This is a list type whose items are " + sType.getListItemType().getFullJavaName() + ".");
                break;
            }
            case 2: {
                this.emit(" * This is a union type. Instances are of one of the following types:");
                final SchemaType[] members = sType.getUnionConstituentTypes();
                for (int i = 0; i < members.length; ++i) {
                    this.emit(" *     " + members[i].getFullJavaName());
                }
                break;
            }
        }
        this.emit(" */");
    }
    
    private String getFullJavaName(final SchemaType sType) {
        SchemaTypeImpl sTypeI = (SchemaTypeImpl)sType;
        String ret = sTypeI.getFullJavaName();
        while (sTypeI.isRedefinition()) {
            ret = sTypeI.getFullJavaName();
            sTypeI = (SchemaTypeImpl)sTypeI.getBaseType();
        }
        return ret;
    }
    
    private String getUserTypeStaticHandlerMethod(final boolean encode, final SchemaTypeImpl stype) {
        String unqualifiedName = stype.getName().getLocalPart();
        if (unqualifiedName.length() < 2) {
            unqualifiedName = unqualifiedName.toUpperCase();
        }
        else {
            unqualifiedName = unqualifiedName.substring(0, 1).toUpperCase() + unqualifiedName.substring(1);
        }
        if (encode) {
            return stype.getUserTypeHandlerName() + ".encode" + unqualifiedName;
        }
        return stype.getUserTypeHandlerName() + ".decode" + unqualifiedName;
    }
    
    public static String indexClassForSystem(final SchemaTypeSystem system) {
        final String name = system.getName();
        return name + "." + "TypeSystemHolder";
    }
    
    static String shortIndexClassForSystem(final SchemaTypeSystem system) {
        return "TypeSystemHolder";
    }
    
    void printStaticTypeDeclaration(final SchemaType sType, final SchemaTypeSystem system) throws IOException {
        final String interfaceShortName = sType.getShortJavaName();
        this.emit("public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)");
        this.indent();
        this.emit("org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(" + interfaceShortName + ".class.getClassLoader(), \"" + system.getName() + "\")" + ".resolveHandle(\"" + ((SchemaTypeSystemImpl)system).handleForType(sType) + "\");");
        this.outdent();
    }
    
    @Override
    @Deprecated
    public void printLoader(final Writer writer, final SchemaTypeSystem system) throws IOException {
    }
    
    void printInnerType(final SchemaType sType, final SchemaTypeSystem system) throws IOException {
        this.emit("");
        this.printInnerTypeJavaDoc(sType);
        this.startInterface(sType);
        this.printStaticTypeDeclaration(sType, system);
        if (sType.isSimpleType()) {
            if (sType.hasStringEnumValues()) {
                this.printStringEnumeration(sType);
            }
        }
        else {
            if (sType.getContentType() == 2 && sType.hasStringEnumValues()) {
                this.printStringEnumeration(sType);
            }
            final SchemaProperty[] props = this.getDerivedProperties(sType);
            for (int i = 0; i < props.length; ++i) {
                final SchemaProperty prop = props[i];
                this.printPropertyGetters(prop.getName(), prop.isAttribute(), prop.getJavaPropertyName(), prop.getJavaTypeCode(), this.javaTypeForProperty(prop), this.xmlTypeForProperty(prop), prop.hasNillable() != 0, prop.extendsJavaOption(), prop.extendsJavaArray(), prop.extendsJavaSingleton());
                if (!prop.isReadOnly()) {
                    this.printPropertySetters(prop.getName(), prop.isAttribute(), prop.getJavaPropertyName(), prop.getJavaTypeCode(), this.javaTypeForProperty(prop), this.xmlTypeForProperty(prop), prop.hasNillable() != 0, prop.extendsJavaOption(), prop.extendsJavaArray(), prop.extendsJavaSingleton());
                }
            }
        }
        this.printNestedInnerTypes(sType, system);
        this.printFactory(sType);
        this.endBlock();
    }
    
    void printFactory(final SchemaType sType) throws IOException {
        boolean fullFactory = true;
        if (sType.isAnonymousType() && !sType.isDocumentType() && !sType.isAttributeType()) {
            fullFactory = false;
        }
        final String fullName = sType.getFullJavaName().replace('$', '.');
        this.emit("");
        this.emit("/**");
        this.emit(" * A factory class with static methods for creating instances");
        this.emit(" * of this type.");
        this.emit(" */");
        this.emit("");
        this.emit("public static final class Factory");
        this.emit("{");
        this.indent();
        if (sType.isSimpleType()) {
            this.emit("public static " + fullName + " newValue(java.lang.Object obj) {");
            this.emit("  return (" + fullName + ") type.newValue( obj ); }");
            this.emit("");
        }
        if (sType.isAbstract()) {
            this.emit("/** @deprecated No need to be able to create instances of abstract types */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
        }
        this.emit("public static " + fullName + " newInstance() {");
        this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }");
        this.emit("");
        if (sType.isAbstract()) {
            this.emit("/** @deprecated No need to be able to create instances of abstract types */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
        }
        this.emit("public static " + fullName + " newInstance(org.apache.xmlbeans.XmlOptions options) {");
        this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }");
        this.emit("");
        if (fullFactory) {
            this.emit("/** @param xmlAsString the string value to parse */");
            this.emit("public static " + fullName + " parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }");
            this.emit("");
            this.emit("/** @param file the file from which to load an xml document */");
            this.emit("public static " + fullName + " parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }");
            this.emit("");
            this.emit("public static " + fullName + " parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }");
            this.emit("");
            this.emit("/** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("public static " + fullName + " parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }");
            this.emit("");
            this.emit("/** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("public static " + fullName + " parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {");
            this.emit("  return (" + fullName + ") org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }");
            this.emit("");
            this.emit("/** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {");
            this.emit("  return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }");
            this.emit("");
            this.emit("/** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {");
            this.emit("  return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }");
            this.emit("");
        }
        this.emit("private Factory() { } // No instance of this class allowed");
        this.outdent();
        this.emit("}");
    }
    
    void printNestedInnerTypes(SchemaType sType, final SchemaTypeSystem system) throws IOException {
        final boolean redefinition = sType.getName() != null && sType.getName().equals(sType.getBaseType().getName());
        while (sType != null) {
            final SchemaType[] anonTypes = sType.getAnonymousTypes();
            for (int i = 0; i < anonTypes.length; ++i) {
                if (anonTypes[i].isSkippedAnonymousType()) {
                    this.printNestedInnerTypes(anonTypes[i], system);
                }
                else {
                    this.printInnerType(anonTypes[i], system);
                }
            }
            if (!redefinition) {
                break;
            }
            if (sType.getDerivationType() != 2 && !sType.isSimpleType()) {
                break;
            }
            sType = sType.getBaseType();
        }
    }
    
    void printTopComment(final SchemaType sType) throws IOException {
        this.emit("/*");
        if (sType.getName() != null) {
            this.emit(" * XML Type:  " + sType.getName().getLocalPart());
            this.emit(" * Namespace: " + sType.getName().getNamespaceURI());
        }
        else {
            QName thename = null;
            if (sType.isDocumentType()) {
                thename = sType.getDocumentElementName();
                this.emit(" * An XML document type.");
            }
            else if (sType.isAttributeType()) {
                thename = sType.getAttributeTypeAttributeName();
                this.emit(" * An XML attribute type.");
            }
            else {
                assert false;
            }
            assert thename != null;
            this.emit(" * Localname: " + thename.getLocalPart());
            this.emit(" * Namespace: " + thename.getNamespaceURI());
        }
        this.emit(" * Java type: " + sType.getFullJavaName());
        this.emit(" *");
        this.emit(" * Automatically generated - do not modify.");
        this.emit(" */");
    }
    
    void printPackage(final SchemaType sType, final boolean intf) throws IOException {
        String fqjn;
        if (intf) {
            fqjn = sType.getFullJavaName();
        }
        else {
            fqjn = sType.getFullJavaImplName();
        }
        final int lastdot = fqjn.lastIndexOf(46);
        if (lastdot < 0) {
            return;
        }
        final String pkg = fqjn.substring(0, lastdot);
        this.emit("package " + pkg + ";");
    }
    
    void startInterface(final SchemaType sType) throws IOException {
        final String shortName = sType.getShortJavaName();
        final String baseInterface = this.findJavaType(sType.getBaseType());
        this.emit("public interface " + shortName + " extends " + baseInterface + getExtensionInterfaces(sType));
        this.emit("{");
        this.indent();
        this.emitSpecializedAccessors(sType);
    }
    
    private static String getExtensionInterfaces(final SchemaType sType) {
        final SchemaTypeImpl sImpl = getImpl(sType);
        if (sImpl == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        final InterfaceExtension[] exts = sImpl.getInterfaceExtensions();
        if (exts != null) {
            for (int i = 0; i < exts.length; ++i) {
                sb.append(", " + exts[i].getInterface());
            }
        }
        return sb.toString();
    }
    
    private static SchemaTypeImpl getImpl(final SchemaType sType) {
        if (sType instanceof SchemaTypeImpl) {
            return (SchemaTypeImpl)sType;
        }
        return null;
    }
    
    private void emitSpecializedAccessors(final SchemaType sType) throws IOException {
        if (sType.getSimpleVariety() == 1 && sType.getPrimitiveType().getBuiltinTypeCode() == 11) {
            final int bits = sType.getDecimalSize();
            final int parentBits = sType.getBaseType().getDecimalSize();
            if (bits != parentBits || sType.getBaseType().getFullJavaName() == null) {
                if (bits == 1000000) {
                    this.emit("java.math.BigInteger getBigIntegerValue();");
                    this.emit("void setBigIntegerValue(java.math.BigInteger bi);");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("java.math.BigInteger bigIntegerValue();");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("void set(java.math.BigInteger bi);");
                }
                else if (bits == 64) {
                    this.emit("long getLongValue();");
                    this.emit("void setLongValue(long l);");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("long longValue();");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("void set(long l);");
                }
                else if (bits == 32) {
                    this.emit("int getIntValue();");
                    this.emit("void setIntValue(int i);");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("int intValue();");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("void set(int i);");
                }
                else if (bits == 16) {
                    this.emit("short getShortValue();");
                    this.emit("void setShortValue(short s);");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("short shortValue();");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("void set(short s);");
                }
                else if (bits == 8) {
                    this.emit("byte getByteValue();");
                    this.emit("void setByteValue(byte b);");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("byte byteValue();");
                    this.emit("/** @deprecated */");
                    if (this._useJava15) {
                        this.emit("@Deprecated");
                    }
                    this.emit("void set(byte b);");
                }
            }
        }
        if (sType.getSimpleVariety() == 2) {
            this.emit("java.lang.Object getObjectValue();");
            this.emit("void setObjectValue(java.lang.Object val);");
            this.emit("/** @deprecated */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("java.lang.Object objectValue();");
            this.emit("/** @deprecated */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("void objectSet(java.lang.Object val);");
            this.emit("org.apache.xmlbeans.SchemaType instanceType();");
            final SchemaType ctype = sType.getUnionCommonBaseType();
            if (ctype == null || ctype.getSimpleVariety() != 2) {}
            this.emitSpecializedAccessors(ctype);
        }
        if (sType.getSimpleVariety() == 3) {
            this.emit("java.util.List getListValue();");
            this.emit("java.util.List xgetListValue();");
            this.emit("void setListValue(java.util.List list);");
            this.emit("/** @deprecated */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("java.util.List listValue();");
            this.emit("/** @deprecated */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("java.util.List xlistValue();");
            this.emit("/** @deprecated */");
            if (this._useJava15) {
                this.emit("@Deprecated");
            }
            this.emit("void set(java.util.List list);");
        }
    }
    
    void startBlock() throws IOException {
        this.emit("{");
        this.indent();
    }
    
    void endBlock() throws IOException {
        this.outdent();
        this.emit("}");
    }
    
    void printJavaDoc(final String sentence) throws IOException {
        this.emit("");
        this.emit("/**");
        this.emit(" * " + sentence);
        this.emit(" */");
    }
    
    void printShortJavaDoc(final String sentence) throws IOException {
        this.emit("/** " + sentence + " */");
    }
    
    public static String javaStringEscape(final String str) {
        int i = 0;
        while (i < str.length()) {
            switch (str.charAt(i)) {
                case '\n':
                case '\r':
                case '\"':
                case '\\': {
                    final StringBuffer sb = new StringBuffer();
                    for (int j = 0; j < str.length(); ++j) {
                        final char ch = str.charAt(j);
                        switch (ch) {
                            default: {
                                sb.append(ch);
                                break;
                            }
                            case '\n': {
                                sb.append("\\n");
                                break;
                            }
                            case '\r': {
                                sb.append("\\r");
                                break;
                            }
                            case '\"': {
                                sb.append("\\\"");
                                break;
                            }
                            case '\\': {
                                sb.append("\\\\");
                                break;
                            }
                        }
                    }
                    return sb.toString();
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        return str;
    }
    
    void printStringEnumeration(final SchemaType sType) throws IOException {
        final SchemaType baseEnumType = sType.getBaseEnumType();
        final String baseEnumClass = baseEnumType.getFullJavaName();
        final boolean hasBase = this.hasBase(sType);
        if (!hasBase) {
            this.emit("");
            this.emit("org.apache.xmlbeans.StringEnumAbstractBase enumValue();");
            this.emit("void set(org.apache.xmlbeans.StringEnumAbstractBase e);");
        }
        this.emit("");
        final SchemaStringEnumEntry[] entries = sType.getStringEnumEntries();
        final HashSet seenValues = new HashSet();
        final HashSet repeatValues = new HashSet();
        for (int i = 0; i < entries.length; ++i) {
            final String enumValue = entries[i].getString();
            if (seenValues.contains(enumValue)) {
                repeatValues.add(enumValue);
            }
            else {
                seenValues.add(enumValue);
                final String constName = entries[i].getEnumName();
                if (hasBase) {
                    this.emit("static final " + baseEnumClass + ".Enum " + constName + " = " + baseEnumClass + "." + constName + ";");
                }
                else {
                    this.emit("static final Enum " + constName + " = Enum.forString(\"" + javaStringEscape(enumValue) + "\");");
                }
            }
        }
        this.emit("");
        for (int i = 0; i < entries.length; ++i) {
            if (!repeatValues.contains(entries[i].getString())) {
                final String constName2 = "INT_" + entries[i].getEnumName();
                if (hasBase) {
                    this.emit("static final int " + constName2 + " = " + baseEnumClass + "." + constName2 + ";");
                }
                else {
                    this.emit("static final int " + constName2 + " = Enum." + constName2 + ";");
                }
            }
        }
        if (!hasBase) {
            this.emit("");
            this.emit("/**");
            this.emit(" * Enumeration value class for " + baseEnumClass + ".");
            this.emit(" * These enum values can be used as follows:");
            this.emit(" * <pre>");
            this.emit(" * enum.toString(); // returns the string value of the enum");
            this.emit(" * enum.intValue(); // returns an int value, useful for switches");
            if (entries.length > 0) {
                this.emit(" * // e.g., case Enum.INT_" + entries[0].getEnumName());
            }
            this.emit(" * Enum.forString(s); // returns the enum value for a string");
            this.emit(" * Enum.forInt(i); // returns the enum value for an int");
            this.emit(" * </pre>");
            this.emit(" * Enumeration objects are immutable singleton objects that");
            this.emit(" * can be compared using == object equality. They have no");
            this.emit(" * public constructor. See the constants defined within this");
            this.emit(" * class for all the valid values.");
            this.emit(" */");
            this.emit("static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase");
            this.emit("{");
            this.indent();
            this.emit("/**");
            this.emit(" * Returns the enum value for a string, or null if none.");
            this.emit(" */");
            this.emit("public static Enum forString(java.lang.String s)");
            this.emit("    { return (Enum)table.forString(s); }");
            this.emit("/**");
            this.emit(" * Returns the enum value corresponding to an int, or null if none.");
            this.emit(" */");
            this.emit("public static Enum forInt(int i)");
            this.emit("    { return (Enum)table.forInt(i); }");
            this.emit("");
            this.emit("private Enum(java.lang.String s, int i)");
            this.emit("    { super(s, i); }");
            this.emit("");
            for (int i = 0; i < entries.length; ++i) {
                final String constName2 = "INT_" + entries[i].getEnumName();
                final int intValue = entries[i].getIntValue();
                this.emit("static final int " + constName2 + " = " + intValue + ";");
            }
            this.emit("");
            this.emit("public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =");
            this.emit("    new org.apache.xmlbeans.StringEnumAbstractBase.Table");
            this.emit("(");
            this.indent();
            this.emit("new Enum[]");
            this.emit("{");
            this.indent();
            for (int i = 0; i < entries.length; ++i) {
                final String enumValue = entries[i].getString();
                final String constName = "INT_" + entries[i].getEnumName();
                this.emit("new Enum(\"" + javaStringEscape(enumValue) + "\", " + constName + "),");
            }
            this.outdent();
            this.emit("}");
            this.outdent();
            this.emit(");");
            this.emit("private static final long serialVersionUID = 1L;");
            this.emit("private java.lang.Object readResolve() { return forInt(intValue()); } ");
            this.outdent();
            this.emit("}");
        }
    }
    
    private boolean hasBase(final SchemaType sType) {
        final SchemaType baseEnumType = sType.getBaseEnumType();
        boolean hasBase;
        if (baseEnumType.isAnonymousType() && baseEnumType.isSkippedAnonymousType()) {
            if (sType.getContentBasedOnType() != null) {
                hasBase = (sType.getContentBasedOnType().getBaseType() != baseEnumType);
            }
            else {
                hasBase = (sType.getBaseType() != baseEnumType);
            }
        }
        else {
            hasBase = (baseEnumType != sType);
        }
        return hasBase;
    }
    
    String xmlTypeForProperty(final SchemaProperty sProp) {
        final SchemaType sType = sProp.javaBasedOnType();
        return this.findJavaType(sType).replace('$', '.');
    }
    
    static boolean xmlTypeForPropertyIsUnion(final SchemaProperty sProp) {
        final SchemaType sType = sProp.javaBasedOnType();
        return sType.isSimpleType() && sType.getSimpleVariety() == 2;
    }
    
    static boolean isJavaPrimitive(final int javaType) {
        return javaType >= 1 && javaType <= 7;
    }
    
    static String javaWrappedType(final int javaType) {
        switch (javaType) {
            case 1: {
                return "java.lang.Boolean";
            }
            case 2: {
                return "java.lang.Float";
            }
            case 3: {
                return "java.lang.Double";
            }
            case 4: {
                return "java.lang.Byte";
            }
            case 5: {
                return "java.lang.Short";
            }
            case 6: {
                return "java.lang.Integer";
            }
            case 7: {
                return "java.lang.Long";
            }
            default: {
                assert false;
                throw new IllegalStateException();
            }
        }
    }
    
    String javaTypeForProperty(final SchemaProperty sProp) {
        if (sProp.getJavaTypeCode() == 0) {
            final SchemaType sType = sProp.javaBasedOnType();
            return this.findJavaType(sType).replace('$', '.');
        }
        if (sProp.getJavaTypeCode() == 20) {
            return ((SchemaTypeImpl)sProp.getType()).getUserTypeName();
        }
        switch (sProp.getJavaTypeCode()) {
            case 1: {
                return "boolean";
            }
            case 2: {
                return "float";
            }
            case 3: {
                return "double";
            }
            case 4: {
                return "byte";
            }
            case 5: {
                return "short";
            }
            case 6: {
                return "int";
            }
            case 7: {
                return "long";
            }
            case 8: {
                return "java.math.BigDecimal";
            }
            case 9: {
                return "java.math.BigInteger";
            }
            case 10: {
                return "java.lang.String";
            }
            case 11: {
                return "byte[]";
            }
            case 12: {
                return "org.apache.xmlbeans.GDate";
            }
            case 13: {
                return "org.apache.xmlbeans.GDuration";
            }
            case 14: {
                return "java.util.Date";
            }
            case 15: {
                return "javax.xml.namespace.QName";
            }
            case 16: {
                return "java.util.List";
            }
            case 17: {
                return "java.util.Calendar";
            }
            case 18: {
                SchemaType sType = sProp.javaBasedOnType();
                if (sType.getSimpleVariety() == 2) {
                    sType = sType.getUnionCommonBaseType();
                }
                assert sType.getBaseEnumType() != null;
                if (this.hasBase(sType)) {
                    return this.findJavaType(sType.getBaseEnumType()).replace('$', '.') + ".Enum";
                }
                return this.findJavaType(sType).replace('$', '.') + ".Enum";
            }
            case 19: {
                return "java.lang.Object";
            }
            default: {
                assert false;
                throw new IllegalStateException();
            }
        }
    }
    
    void printPropertyGetters(final QName qName, final boolean isAttr, final String propertyName, final int javaType, final String type, final String xtype, final boolean nillable, final boolean optional, final boolean several, final boolean singleton) throws IOException {
        final String propdesc = "\"" + qName.getLocalPart() + "\"" + (isAttr ? " attribute" : " element");
        final boolean xmltype = javaType == 0;
        if (singleton) {
            this.printJavaDoc((several ? "Gets first " : "Gets the ") + propdesc);
            this.emit(type + " get" + propertyName + "();");
            if (!xmltype) {
                this.printJavaDoc((several ? "Gets (as xml) first " : "Gets (as xml) the ") + propdesc);
                this.emit(xtype + " xget" + propertyName + "();");
            }
            if (nillable) {
                this.printJavaDoc((several ? "Tests for nil first " : "Tests for nil ") + propdesc);
                this.emit("boolean isNil" + propertyName + "();");
            }
        }
        if (optional) {
            this.printJavaDoc((several ? "True if has at least one " : "True if has ") + propdesc);
            this.emit("boolean isSet" + propertyName + "();");
        }
        if (several) {
            final String arrayName = propertyName + "Array";
            if (this._useJava15) {
                String wrappedType = type;
                if (isJavaPrimitive(javaType)) {
                    wrappedType = javaWrappedType(javaType);
                }
                this.printJavaDoc("Gets a List of " + propdesc + "s");
                this.emit("java.util.List<" + wrappedType + "> get" + propertyName + "List();");
            }
            if (this._useJava15) {
                this.emit("");
                this.emit("/**");
                this.emit(" * Gets array of all " + propdesc + "s");
                this.emit(" * @deprecated");
                this.emit(" */");
                this.emit("@Deprecated");
            }
            else {
                this.printJavaDoc("Gets array of all " + propdesc + "s");
            }
            this.emit(type + "[] get" + arrayName + "();");
            this.printJavaDoc("Gets ith " + propdesc);
            this.emit(type + " get" + arrayName + "(int i);");
            if (!xmltype) {
                if (this._useJava15) {
                    this.printJavaDoc("Gets (as xml) a List of " + propdesc + "s");
                    this.emit("java.util.List<" + xtype + "> xget" + propertyName + "List();");
                }
                if (this._useJava15) {
                    this.emit("");
                    this.emit("/**");
                    this.emit(" * Gets (as xml) array of all " + propdesc + "s");
                    this.emit(" * @deprecated");
                    this.emit(" */");
                    this.emit("@Deprecated");
                }
                else {
                    this.printJavaDoc("Gets (as xml) array of all " + propdesc + "s");
                }
                this.emit(xtype + "[] xget" + arrayName + "();");
                this.printJavaDoc("Gets (as xml) ith " + propdesc);
                this.emit(xtype + " xget" + arrayName + "(int i);");
            }
            if (nillable) {
                this.printJavaDoc("Tests for nil ith " + propdesc);
                this.emit("boolean isNil" + arrayName + "(int i);");
            }
            this.printJavaDoc("Returns number of " + propdesc);
            this.emit("int sizeOf" + arrayName + "();");
        }
    }
    
    void printPropertySetters(final QName qName, final boolean isAttr, final String propertyName, final int javaType, final String type, final String xtype, final boolean nillable, final boolean optional, final boolean several, final boolean singleton) throws IOException {
        String safeVarName = NameUtil.nonJavaKeyword(NameUtil.lowerCamelCase(propertyName));
        if (safeVarName.equals("i")) {
            safeVarName = "iValue";
        }
        final boolean xmltype = javaType == 0;
        final String propdesc = "\"" + qName.getLocalPart() + "\"" + (isAttr ? " attribute" : " element");
        if (singleton) {
            this.printJavaDoc((several ? "Sets first " : "Sets the ") + propdesc);
            this.emit("void set" + propertyName + "(" + type + " " + safeVarName + ");");
            if (!xmltype) {
                this.printJavaDoc((several ? "Sets (as xml) first " : "Sets (as xml) the ") + propdesc);
                this.emit("void xset" + propertyName + "(" + xtype + " " + safeVarName + ");");
            }
            if (xmltype && !several) {
                this.printJavaDoc("Appends and returns a new empty " + propdesc);
                this.emit(xtype + " addNew" + propertyName + "();");
            }
            if (nillable) {
                this.printJavaDoc((several ? "Nils the first " : "Nils the ") + propdesc);
                this.emit("void setNil" + propertyName + "();");
            }
        }
        if (optional) {
            this.printJavaDoc((several ? "Removes first " : "Unsets the ") + propdesc);
            this.emit("void unset" + propertyName + "();");
        }
        if (several) {
            final String arrayName = propertyName + "Array";
            this.printJavaDoc("Sets array of all " + propdesc);
            this.emit("void set" + arrayName + "(" + type + "[] " + safeVarName + "Array);");
            this.printJavaDoc("Sets ith " + propdesc);
            this.emit("void set" + arrayName + "(int i, " + type + " " + safeVarName + ");");
            if (!xmltype) {
                this.printJavaDoc("Sets (as xml) array of all " + propdesc);
                this.emit("void xset" + arrayName + "(" + xtype + "[] " + safeVarName + "Array);");
                this.printJavaDoc("Sets (as xml) ith " + propdesc);
                this.emit("void xset" + arrayName + "(int i, " + xtype + " " + safeVarName + ");");
            }
            if (nillable) {
                this.printJavaDoc("Nils the ith " + propdesc);
                this.emit("void setNil" + arrayName + "(int i);");
            }
            if (!xmltype) {
                this.printJavaDoc("Inserts the value as the ith " + propdesc);
                this.emit("void insert" + propertyName + "(int i, " + type + " " + safeVarName + ");");
                this.printJavaDoc("Appends the value as the last " + propdesc);
                this.emit("void add" + propertyName + "(" + type + " " + safeVarName + ");");
            }
            this.printJavaDoc("Inserts and returns a new empty value (as xml) as the ith " + propdesc);
            this.emit(xtype + " insertNew" + propertyName + "(int i);");
            this.printJavaDoc("Appends and returns a new empty value (as xml) as the last " + propdesc);
            this.emit(xtype + " addNew" + propertyName + "();");
            this.printJavaDoc("Removes the ith " + propdesc);
            this.emit("void remove" + propertyName + "(int i);");
        }
    }
    
    String getAtomicRestrictionType(final SchemaType sType) {
        final SchemaType pType = sType.getPrimitiveType();
        switch (pType.getBuiltinTypeCode()) {
            case 2: {
                return "org.apache.xmlbeans.impl.values.XmlAnySimpleTypeImpl";
            }
            case 3: {
                return "org.apache.xmlbeans.impl.values.JavaBooleanHolderEx";
            }
            case 4: {
                return "org.apache.xmlbeans.impl.values.JavaBase64HolderEx";
            }
            case 5: {
                return "org.apache.xmlbeans.impl.values.JavaHexBinaryHolderEx";
            }
            case 6: {
                return "org.apache.xmlbeans.impl.values.JavaUriHolderEx";
            }
            case 7: {
                return "org.apache.xmlbeans.impl.values.JavaQNameHolderEx";
            }
            case 8: {
                return "org.apache.xmlbeans.impl.values.JavaNotationHolderEx";
            }
            case 9: {
                return "org.apache.xmlbeans.impl.values.JavaFloatHolderEx";
            }
            case 10: {
                return "org.apache.xmlbeans.impl.values.JavaDoubleHolderEx";
            }
            case 11: {
                switch (sType.getDecimalSize()) {
                    default: {
                        assert false;
                        return "org.apache.xmlbeans.impl.values.JavaDecimalHolderEx";
                    }
                    case 1000001: {
                        return "org.apache.xmlbeans.impl.values.JavaDecimalHolderEx";
                    }
                    case 1000000: {
                        return "org.apache.xmlbeans.impl.values.JavaIntegerHolderEx";
                    }
                    case 64: {
                        return "org.apache.xmlbeans.impl.values.JavaLongHolderEx";
                    }
                    case 8:
                    case 16:
                    case 32: {
                        return "org.apache.xmlbeans.impl.values.JavaIntHolderEx";
                    }
                }
                break;
            }
            case 12: {
                if (sType.hasStringEnumValues()) {
                    return "org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx";
                }
                return "org.apache.xmlbeans.impl.values.JavaStringHolderEx";
            }
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                return "org.apache.xmlbeans.impl.values.JavaGDateHolderEx";
            }
            case 13: {
                return "org.apache.xmlbeans.impl.values.JavaGDurationHolderEx";
            }
            default: {
                assert false : "unrecognized primitive type";
                return null;
            }
        }
    }
    
    static SchemaType findBaseType(SchemaType sType) {
        while (sType.getFullJavaName() == null) {
            sType = sType.getBaseType();
        }
        return sType;
    }
    
    String getBaseClass(final SchemaType sType) {
        final SchemaType baseType = findBaseType(sType.getBaseType());
        switch (sType.getSimpleVariety()) {
            case 0: {
                if (!XmlObject.type.equals(baseType)) {
                    return baseType.getFullJavaImplName();
                }
                return "org.apache.xmlbeans.impl.values.XmlComplexContentImpl";
            }
            case 1: {
                assert !sType.isBuiltinType();
                return this.getAtomicRestrictionType(sType);
            }
            case 3: {
                return "org.apache.xmlbeans.impl.values.XmlListImpl";
            }
            case 2: {
                return "org.apache.xmlbeans.impl.values.XmlUnionImpl";
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    void printConstructor(final SchemaType sType, final String shortName) throws IOException {
        this.emit("");
        this.emit("public " + shortName + "(org.apache.xmlbeans.SchemaType sType)");
        this.startBlock();
        this.emit("super(sType" + ((sType.getSimpleVariety() == 0) ? "" : (", " + !sType.isSimpleType())) + ");");
        this.endBlock();
        if (sType.getSimpleVariety() != 0) {
            this.emit("");
            this.emit("protected " + shortName + "(org.apache.xmlbeans.SchemaType sType, boolean b)");
            this.startBlock();
            this.emit("super(sType, b);");
            this.endBlock();
        }
    }
    
    void startClass(final SchemaType sType, final boolean isInner) throws IOException {
        final String shortName = sType.getShortJavaImplName();
        final String baseClass = this.getBaseClass(sType);
        final StringBuffer interfaces = new StringBuffer();
        interfaces.append(sType.getFullJavaName().replace('$', '.'));
        if (sType.getSimpleVariety() == 2) {
            final SchemaType[] memberTypes = sType.getUnionMemberTypes();
            for (int i = 0; i < memberTypes.length; ++i) {
                interfaces.append(", " + memberTypes[i].getFullJavaName().replace('$', '.'));
            }
        }
        this.emit("public " + (isInner ? "static " : "") + "class " + shortName + " extends " + baseClass + " implements " + interfaces.toString());
        this.startBlock();
        this.emit("private static final long serialVersionUID = 1L;");
    }
    
    void makeAttributeDefaultValue(final String jtargetType, final SchemaProperty prop, final String identifier) throws IOException {
        String fullName = jtargetType;
        if (fullName == null) {
            fullName = prop.javaBasedOnType().getFullJavaName().replace('$', '.');
        }
        this.emit("target = (" + fullName + ")get_default_attribute_value(" + identifier + ");");
    }
    
    void makeMissingValue(final int javaType) throws IOException {
        switch (javaType) {
            case 1: {
                this.emit("return false;");
                break;
            }
            case 2: {
                this.emit("return 0.0f;");
                break;
            }
            case 3: {
                this.emit("return 0.0;");
                break;
            }
            case 4:
            case 5:
            case 6: {
                this.emit("return 0;");
                break;
            }
            case 7: {
                this.emit("return 0L;");
                break;
            }
            default: {
                this.emit("return null;");
                break;
            }
        }
    }
    
    void printJGetArrayValue(final int javaType, final String type, final SchemaTypeImpl stype) throws IOException {
        switch (javaType) {
            case 0: {
                this.emit(type + "[] result = new " + type + "[targetList.size()];");
                this.emit("targetList.toArray(result);");
                break;
            }
            case 18: {
                this.emit(type + "[] result = new " + type + "[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = (" + type + ")((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getEnumValue();");
                break;
            }
            case 1: {
                this.emit("boolean[] result = new boolean[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getBooleanValue();");
                break;
            }
            case 2: {
                this.emit("float[] result = new float[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getFloatValue();");
                break;
            }
            case 3: {
                this.emit("double[] result = new double[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getDoubleValue();");
                break;
            }
            case 4: {
                this.emit("byte[] result = new byte[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getByteValue();");
                break;
            }
            case 5: {
                this.emit("short[] result = new short[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getShortValue();");
                break;
            }
            case 6: {
                this.emit("int[] result = new int[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getIntValue();");
                break;
            }
            case 7: {
                this.emit("long[] result = new long[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getLongValue();");
                break;
            }
            case 8: {
                this.emit("java.math.BigDecimal[] result = new java.math.BigDecimal[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getBigDecimalValue();");
                break;
            }
            case 9: {
                this.emit("java.math.BigInteger[] result = new java.math.BigInteger[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getBigIntegerValue();");
                break;
            }
            case 10: {
                this.emit("java.lang.String[] result = new java.lang.String[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getStringValue();");
                break;
            }
            case 11: {
                this.emit("byte[][] result = new byte[targetList.size()][];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getByteArrayValue();");
                break;
            }
            case 17: {
                this.emit("java.util.Calendar[] result = new java.util.Calendar[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getCalendarValue();");
                break;
            }
            case 14: {
                this.emit("java.util.Date[] result = new java.util.Date[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getDateValue();");
                break;
            }
            case 12: {
                this.emit("org.apache.xmlbeans.GDate[] result = new org.apache.xmlbeans.GDate[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getGDateValue();");
                break;
            }
            case 13: {
                this.emit("org.apache.xmlbeans.GDuration[] result = new org.apache.xmlbeans.GDuration[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getGDurationValue();");
                break;
            }
            case 15: {
                this.emit("javax.xml.namespace.QName[] result = new javax.xml.namespace.QName[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getQNameValue();");
                break;
            }
            case 16: {
                this.emit("java.util.List[] result = new java.util.List[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getListValue();");
                break;
            }
            case 19: {
                this.emit("java.lang.Object[] result = new java.lang.Object[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getObjectValue();");
                break;
            }
            case 20: {
                this.emit(stype.getUserTypeName() + "[] result = new " + stype.getUserTypeName() + "[targetList.size()];");
                this.emit("for (int i = 0, len = targetList.size() ; i < len ; i++)");
                this.emit("    result[i] = " + this.getUserTypeStaticHandlerMethod(false, stype) + "((org.apache.xmlbeans.SimpleValue)targetList.get(i));");
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        this.emit("return result;");
    }
    
    void printJGetValue(final int javaType, final String type, final SchemaTypeImpl stype) throws IOException {
        switch (javaType) {
            case 0: {
                this.emit("return target;");
                break;
            }
            case 1: {
                this.emit("return target.getBooleanValue();");
                break;
            }
            case 2: {
                this.emit("return target.getFloatValue();");
                break;
            }
            case 3: {
                this.emit("return target.getDoubleValue();");
                break;
            }
            case 4: {
                this.emit("return target.getByteValue();");
                break;
            }
            case 5: {
                this.emit("return target.getShortValue();");
                break;
            }
            case 6: {
                this.emit("return target.getIntValue();");
                break;
            }
            case 7: {
                this.emit("return target.getLongValue();");
                break;
            }
            case 8: {
                this.emit("return target.getBigDecimalValue();");
                break;
            }
            case 9: {
                this.emit("return target.getBigIntegerValue();");
                break;
            }
            case 10: {
                this.emit("return target.getStringValue();");
                break;
            }
            case 11: {
                this.emit("return target.getByteArrayValue();");
                break;
            }
            case 12: {
                this.emit("return target.getGDateValue();");
                break;
            }
            case 13: {
                this.emit("return target.getGDurationValue();");
                break;
            }
            case 17: {
                this.emit("return target.getCalendarValue();");
                break;
            }
            case 14: {
                this.emit("return target.getDateValue();");
                break;
            }
            case 15: {
                this.emit("return target.getQNameValue();");
                break;
            }
            case 16: {
                this.emit("return target.getListValue();");
                break;
            }
            case 18: {
                this.emit("return (" + type + ")target.getEnumValue();");
                break;
            }
            case 19: {
                this.emit("return target.getObjectValue();");
                break;
            }
            case 20: {
                this.emit("return " + this.getUserTypeStaticHandlerMethod(false, stype) + "(target);");
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    void printJSetValue(final int javaType, final String safeVarName, final SchemaTypeImpl stype) throws IOException {
        switch (javaType) {
            case 0: {
                this.emit("target.set(" + safeVarName + ");");
                break;
            }
            case 1: {
                this.emit("target.setBooleanValue(" + safeVarName + ");");
                break;
            }
            case 2: {
                this.emit("target.setFloatValue(" + safeVarName + ");");
                break;
            }
            case 3: {
                this.emit("target.setDoubleValue(" + safeVarName + ");");
                break;
            }
            case 4: {
                this.emit("target.setByteValue(" + safeVarName + ");");
                break;
            }
            case 5: {
                this.emit("target.setShortValue(" + safeVarName + ");");
                break;
            }
            case 6: {
                this.emit("target.setIntValue(" + safeVarName + ");");
                break;
            }
            case 7: {
                this.emit("target.setLongValue(" + safeVarName + ");");
                break;
            }
            case 8: {
                this.emit("target.setBigDecimalValue(" + safeVarName + ");");
                break;
            }
            case 9: {
                this.emit("target.setBigIntegerValue(" + safeVarName + ");");
                break;
            }
            case 10: {
                this.emit("target.setStringValue(" + safeVarName + ");");
                break;
            }
            case 11: {
                this.emit("target.setByteArrayValue(" + safeVarName + ");");
                break;
            }
            case 12: {
                this.emit("target.setGDateValue(" + safeVarName + ");");
                break;
            }
            case 13: {
                this.emit("target.setGDurationValue(" + safeVarName + ");");
                break;
            }
            case 17: {
                this.emit("target.setCalendarValue(" + safeVarName + ");");
                break;
            }
            case 14: {
                this.emit("target.setDateValue(" + safeVarName + ");");
                break;
            }
            case 15: {
                this.emit("target.setQNameValue(" + safeVarName + ");");
                break;
            }
            case 16: {
                this.emit("target.setListValue(" + safeVarName + ");");
                break;
            }
            case 18: {
                this.emit("target.setEnumValue(" + safeVarName + ");");
                break;
            }
            case 19: {
                this.emit("target.setObjectValue(" + safeVarName + ");");
                break;
            }
            case 20: {
                this.emit(this.getUserTypeStaticHandlerMethod(true, stype) + "(" + safeVarName + ", target);");
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    String getIdentifier(final Map qNameMap, final QName qName) {
        return ((String[])qNameMap.get(qName))[0];
    }
    
    String getSetIdentifier(final Map qNameMap, final QName qName) {
        final String[] identifiers = qNameMap.get(qName);
        return (identifiers[1] == null) ? identifiers[0] : identifiers[1];
    }
    
    Map printStaticFields(final SchemaProperty[] properties) throws IOException {
        final Map results = new HashMap();
        this.emit("");
        for (int i = 0; i < properties.length; ++i) {
            final String[] identifiers = new String[2];
            final SchemaProperty prop = properties[i];
            final QName name = prop.getName();
            results.put(name, identifiers);
            final String javaName = prop.getJavaPropertyName();
            identifiers[0] = (javaName + "$" + i * 2).toUpperCase();
            final String uriString = "\"" + name.getNamespaceURI() + "\"";
            this.emit("private static final javax.xml.namespace.QName " + identifiers[0] + " = ");
            this.indent();
            this.emit("new javax.xml.namespace.QName(" + uriString + ", \"" + name.getLocalPart() + "\");");
            this.outdent();
            if (properties[i].acceptedNames() != null) {
                final QName[] qnames = properties[i].acceptedNames();
                if (qnames.length > 1) {
                    identifiers[1] = (javaName + "$" + (i * 2 + 1)).toUpperCase();
                    this.emit("private static final org.apache.xmlbeans.QNameSet " + identifiers[1] + " = org.apache.xmlbeans.QNameSet.forArray( new javax.xml.namespace.QName[] { ");
                    this.indent();
                    for (int j = 0; j < qnames.length; ++j) {
                        this.emit("new javax.xml.namespace.QName(\"" + qnames[j].getNamespaceURI() + "\", \"" + qnames[j].getLocalPart() + "\"),");
                    }
                    this.outdent();
                    this.emit("});");
                }
            }
        }
        this.emit("");
        return results;
    }
    
    void emitImplementationPreamble() throws IOException {
        this.emit("synchronized (monitor())");
        this.emit("{");
        this.indent();
        this.emit("check_orphaned();");
    }
    
    void emitImplementationPostamble() throws IOException {
        this.outdent();
        this.emit("}");
    }
    
    void emitDeclareTarget(final boolean declareTarget, final String xtype) throws IOException {
        if (declareTarget) {
            this.emit(xtype + " target = null;");
        }
    }
    
    void emitAddTarget(final String identifier, final boolean isAttr, final boolean declareTarget, final String xtype) throws IOException {
        if (isAttr) {
            this.emit("target = (" + xtype + ")get_store().add_attribute_user(" + identifier + ");");
        }
        else {
            this.emit("target = (" + xtype + ")get_store().add_element_user(" + identifier + ");");
        }
    }
    
    void emitPre(final SchemaType sType, final int opType, final String identifier, final boolean isAttr) throws IOException {
        this.emitPre(sType, opType, identifier, isAttr, "-1");
    }
    
    void emitPre(final SchemaType sType, final int opType, final String identifier, final boolean isAttr, final String index) throws IOException {
        final SchemaTypeImpl sImpl = getImpl(sType);
        if (sImpl == null) {
            return;
        }
        final PrePostExtension ext = sImpl.getPrePostExtension();
        if (ext != null && ext.hasPreCall()) {
            this.emit("if ( " + ext.getStaticHandler() + ".preSet(" + this.prePostOpString(opType) + ", this, " + identifier + ", " + isAttr + ", " + index + "))");
            this.startBlock();
        }
    }
    
    void emitPost(final SchemaType sType, final int opType, final String identifier, final boolean isAttr) throws IOException {
        this.emitPost(sType, opType, identifier, isAttr, "-1");
    }
    
    void emitPost(final SchemaType sType, final int opType, final String identifier, final boolean isAttr, final String index) throws IOException {
        final SchemaTypeImpl sImpl = getImpl(sType);
        if (sImpl == null) {
            return;
        }
        final PrePostExtension ext = sImpl.getPrePostExtension();
        if (ext != null) {
            if (ext.hasPreCall()) {
                this.endBlock();
            }
            if (ext.hasPostCall()) {
                this.emit(ext.getStaticHandler() + ".postSet(" + this.prePostOpString(opType) + ", this, " + identifier + ", " + isAttr + ", " + index + ");");
            }
        }
    }
    
    String prePostOpString(final int opType) {
        switch (opType) {
            default: {
                assert false;
                return "org.apache.xmlbeans.PrePostExtension.OPERATION_SET";
            }
            case 1: {
                return "org.apache.xmlbeans.PrePostExtension.OPERATION_SET";
            }
            case 2: {
                return "org.apache.xmlbeans.PrePostExtension.OPERATION_INSERT";
            }
            case 3: {
                return "org.apache.xmlbeans.PrePostExtension.OPERATION_REMOVE";
            }
        }
    }
    
    void emitGetTarget(final String setIdentifier, final String identifier, final boolean isAttr, final String index, final int nullBehaviour, final String xtype) throws IOException {
        assert setIdentifier != null && identifier != null;
        this.emit(xtype + " target = null;");
        if (isAttr) {
            this.emit("target = (" + xtype + ")get_store().find_attribute_user(" + identifier + ");");
        }
        else {
            this.emit("target = (" + xtype + ")get_store().find_element_user(" + setIdentifier + ", " + index + ");");
        }
        if (nullBehaviour == 1) {
            return;
        }
        this.emit("if (target == null)");
        this.startBlock();
        switch (nullBehaviour) {
            case 3: {
                this.emitAddTarget(identifier, isAttr, false, xtype);
                break;
            }
            case 4: {
                this.emit("throw new IndexOutOfBoundsException();");
                break;
            }
            case 1: {
                break;
            }
            default: {
                assert false : "Bad behaviour type: " + nullBehaviour;
                break;
            }
        }
        this.endBlock();
    }
    
    void printListGetter15Impl(final String parentJavaName, final String propdesc, final String propertyName, final String wrappedType, final String xtype, final boolean xmltype, final boolean xget) throws IOException {
        final String arrayName = propertyName + "Array";
        final String listName = propertyName + "List";
        final String parentThis = parentJavaName + ".this.";
        final String xgetMethod = (xget ? "x" : "") + "get";
        final String xsetMethod = (xget ? "x" : "") + "set";
        this.printJavaDoc("Gets " + (xget ? "(as xml) " : "") + "a List of " + propdesc + "s");
        this.emit("public java.util.List<" + wrappedType + "> " + xgetMethod + listName + "()");
        this.startBlock();
        this.emit("final class " + listName + " extends java.util.AbstractList<" + wrappedType + ">");
        this.startBlock();
        if (this._useJava15) {
            this.emit("@Override");
        }
        this.emit("public " + wrappedType + " get(int i)");
        this.emit("    { return " + parentThis + xgetMethod + arrayName + "(i); }");
        this.emit("");
        if (this._useJava15) {
            this.emit("@Override");
        }
        this.emit("public " + wrappedType + " set(int i, " + wrappedType + " o)");
        this.startBlock();
        this.emit(wrappedType + " old = " + parentThis + xgetMethod + arrayName + "(i);");
        this.emit(parentThis + xsetMethod + arrayName + "(i, o);");
        this.emit("return old;");
        this.endBlock();
        this.emit("");
        if (this._useJava15) {
            this.emit("@Override");
        }
        this.emit("public void add(int i, " + wrappedType + " o)");
        if (xmltype || xget) {
            this.emit("    { " + parentThis + "insertNew" + propertyName + "(i).set(o); }");
        }
        else {
            this.emit("    { " + parentThis + "insert" + propertyName + "(i, o); }");
        }
        this.emit("");
        if (this._useJava15) {
            this.emit("@Override");
        }
        this.emit("public " + wrappedType + " remove(int i)");
        this.startBlock();
        this.emit(wrappedType + " old = " + parentThis + xgetMethod + arrayName + "(i);");
        this.emit(parentThis + "remove" + propertyName + "(i);");
        this.emit("return old;");
        this.endBlock();
        this.emit("");
        if (this._useJava15) {
            this.emit("@Override");
        }
        this.emit("public int size()");
        this.emit("    { return " + parentThis + "sizeOf" + arrayName + "(); }");
        this.emit("");
        this.endBlock();
        this.emit("");
        this.emitImplementationPreamble();
        this.emit("return new " + listName + "();");
        this.emitImplementationPostamble();
        this.endBlock();
    }
    
    void printGetterImpls(final String parentJavaName, final SchemaProperty prop, final QName qName, final boolean isAttr, final String propertyName, final int javaType, final String type, final String xtype, final boolean nillable, final boolean optional, final boolean several, final boolean singleton, final boolean isunion, final String identifier, final String setIdentifier) throws IOException {
        final String propdesc = "\"" + qName.getLocalPart() + "\"" + (isAttr ? " attribute" : " element");
        final boolean xmltype = javaType == 0;
        final String jtargetType = (isunion || !xmltype) ? "org.apache.xmlbeans.SimpleValue" : xtype;
        if (singleton) {
            this.printJavaDoc((several ? "Gets first " : "Gets the ") + propdesc);
            this.emit("public " + type + " get" + propertyName + "()");
            this.startBlock();
            this.emitImplementationPreamble();
            this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 1, jtargetType);
            if (isAttr && (prop.hasDefault() == 2 || prop.hasFixed() == 2)) {
                this.emit("if (target == null)");
                this.startBlock();
                this.makeAttributeDefaultValue(jtargetType, prop, identifier);
                this.endBlock();
            }
            this.emit("if (target == null)");
            this.startBlock();
            this.makeMissingValue(javaType);
            this.endBlock();
            this.printJGetValue(javaType, type, (SchemaTypeImpl)prop.getType());
            this.emitImplementationPostamble();
            this.endBlock();
            if (!xmltype) {
                this.printJavaDoc((several ? "Gets (as xml) first " : "Gets (as xml) the ") + propdesc);
                this.emit("public " + xtype + " xget" + propertyName + "()");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 1, xtype);
                if (isAttr && (prop.hasDefault() == 2 || prop.hasFixed() == 2)) {
                    this.emit("if (target == null)");
                    this.startBlock();
                    this.makeAttributeDefaultValue(xtype, prop, identifier);
                    this.endBlock();
                }
                this.emit("return target;");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (nillable) {
                this.printJavaDoc((several ? "Tests for nil first " : "Tests for nil ") + propdesc);
                this.emit("public boolean isNil" + propertyName + "()");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 1, xtype);
                this.emit("if (target == null) return false;");
                this.emit("return target.isNil();");
                this.emitImplementationPostamble();
                this.endBlock();
            }
        }
        if (optional) {
            this.printJavaDoc((several ? "True if has at least one " : "True if has ") + propdesc);
            this.emit("public boolean isSet" + propertyName + "()");
            this.startBlock();
            this.emitImplementationPreamble();
            if (isAttr) {
                this.emit("return get_store().find_attribute_user(" + identifier + ") != null;");
            }
            else {
                this.emit("return get_store().count_elements(" + setIdentifier + ") != 0;");
            }
            this.emitImplementationPostamble();
            this.endBlock();
        }
        if (several) {
            final String arrayName = propertyName + "Array";
            if (this._useJava15) {
                String wrappedType = type;
                if (isJavaPrimitive(javaType)) {
                    wrappedType = javaWrappedType(javaType);
                }
                this.printListGetter15Impl(parentJavaName, propdesc, propertyName, wrappedType, xtype, xmltype, false);
            }
            if (this._useJava15) {
                this.emit("");
                this.emit("/**");
                this.emit(" * Gets array of all " + propdesc + "s");
                this.emit(" * @deprecated");
                this.emit(" */");
                this.emit("@Deprecated");
            }
            else {
                this.printJavaDoc("Gets array of all " + propdesc + "s");
            }
            this.emit("public " + type + "[] get" + arrayName + "()");
            this.startBlock();
            this.emitImplementationPreamble();
            if (this._useJava15) {
                this.emit("java.util.List<" + xtype + "> targetList = new java.util.ArrayList<" + xtype + ">();");
            }
            else {
                this.emit("java.util.List targetList = new java.util.ArrayList();");
            }
            this.emit("get_store().find_all_element_users(" + setIdentifier + ", targetList);");
            this.printJGetArrayValue(javaType, type, (SchemaTypeImpl)prop.getType());
            this.emitImplementationPostamble();
            this.endBlock();
            this.printJavaDoc("Gets ith " + propdesc);
            this.emit("public " + type + " get" + arrayName + "(int i)");
            this.startBlock();
            this.emitImplementationPreamble();
            this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, jtargetType);
            this.printJGetValue(javaType, type, (SchemaTypeImpl)prop.getType());
            this.emitImplementationPostamble();
            this.endBlock();
            if (!xmltype) {
                if (this._useJava15) {
                    this.printListGetter15Impl(parentJavaName, propdesc, propertyName, xtype, xtype, xmltype, true);
                }
                if (this._useJava15) {
                    this.emit("");
                    this.emit("/**");
                    this.emit(" * Gets array of all " + propdesc + "s");
                    this.emit(" * @deprecated");
                    this.emit(" */");
                    this.emit("@Deprecated");
                }
                else {
                    this.printJavaDoc("Gets (as xml) array of all " + propdesc + "s");
                }
                this.emit("public " + xtype + "[] xget" + arrayName + "()");
                this.startBlock();
                this.emitImplementationPreamble();
                if (this._useJava15) {
                    this.emit("java.util.List<" + xtype + "> targetList = new java.util.ArrayList<" + xtype + ">();");
                }
                else {
                    this.emit("java.util.List targetList = new java.util.ArrayList();");
                }
                this.emit("get_store().find_all_element_users(" + setIdentifier + ", targetList);");
                this.emit(xtype + "[] result = new " + xtype + "[targetList.size()];");
                this.emit("targetList.toArray(result);");
                this.emit("return result;");
                this.emitImplementationPostamble();
                this.endBlock();
                this.printJavaDoc("Gets (as xml) ith " + propdesc);
                this.emit("public " + xtype + " xget" + arrayName + "(int i)");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, xtype);
                this.emit("return target;");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (nillable) {
                this.printJavaDoc("Tests for nil ith " + propdesc);
                this.emit("public boolean isNil" + arrayName + "(int i)");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, xtype);
                this.emit("return target.isNil();");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            this.printJavaDoc("Returns number of " + propdesc);
            this.emit("public int sizeOf" + arrayName + "()");
            this.startBlock();
            this.emitImplementationPreamble();
            this.emit("return get_store().count_elements(" + setIdentifier + ");");
            this.emitImplementationPostamble();
            this.endBlock();
        }
    }
    
    void printSetterImpls(final QName qName, final SchemaProperty prop, final boolean isAttr, final String propertyName, final int javaType, final String type, final String xtype, final boolean nillable, final boolean optional, final boolean several, final boolean singleton, final boolean isunion, final String identifier, final String setIdentifier, final SchemaType sType) throws IOException {
        String safeVarName = NameUtil.nonJavaKeyword(NameUtil.lowerCamelCase(propertyName));
        safeVarName = NameUtil.nonExtraKeyword(safeVarName);
        final boolean xmltype = javaType == 0;
        final boolean isobj = javaType == 19;
        final boolean isSubstGroup = identifier != setIdentifier;
        final String jtargetType = (isunion || !xmltype) ? "org.apache.xmlbeans.SimpleValue" : xtype;
        final String propdesc = "\"" + qName.getLocalPart() + "\"" + (isAttr ? " attribute" : " element");
        if (singleton) {
            this.printJavaDoc((several ? "Sets first " : "Sets the ") + propdesc);
            this.emit("public void set" + propertyName + "(" + type + " " + safeVarName + ")");
            this.startBlock();
            if (xmltype && !isSubstGroup && !isAttr) {
                this.emitPre(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emit("generatedSetterHelperImpl(" + safeVarName + ", " + setIdentifier + ", 0, " + "org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);");
                this.emitPost(sType, 1, identifier, isAttr, several ? "0" : "-1");
            }
            else {
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 3, jtargetType);
                this.printJSetValue(javaType, safeVarName, (SchemaTypeImpl)prop.getType());
                this.emitPost(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitImplementationPostamble();
            }
            this.endBlock();
            if (!xmltype) {
                this.printJavaDoc((several ? "Sets (as xml) first " : "Sets (as xml) the ") + propdesc);
                this.emit("public void xset" + propertyName + "(" + xtype + " " + safeVarName + ")");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 3, xtype);
                this.emit("target.set(" + safeVarName + ");");
                this.emitPost(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (xmltype && !several) {
                this.printJavaDoc("Appends and returns a new empty " + propdesc);
                this.emit("public " + xtype + " addNew" + propertyName + "()");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitDeclareTarget(true, xtype);
                this.emitPre(sType, 2, identifier, isAttr);
                this.emitAddTarget(identifier, isAttr, true, xtype);
                this.emitPost(sType, 2, identifier, isAttr);
                this.emit("return target;");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (nillable) {
                this.printJavaDoc((several ? "Nils the first " : "Nils the ") + propdesc);
                this.emit("public void setNil" + propertyName + "()");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 3, xtype);
                this.emit("target.setNil();");
                this.emitPost(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitImplementationPostamble();
                this.endBlock();
            }
        }
        if (optional) {
            this.printJavaDoc((several ? "Removes first " : "Unsets the ") + propdesc);
            this.emit("public void unset" + propertyName + "()");
            this.startBlock();
            this.emitImplementationPreamble();
            this.emitPre(sType, 3, identifier, isAttr, several ? "0" : "-1");
            if (isAttr) {
                this.emit("get_store().remove_attribute(" + identifier + ");");
            }
            else {
                this.emit("get_store().remove_element(" + setIdentifier + ", 0);");
            }
            this.emitPost(sType, 3, identifier, isAttr, several ? "0" : "-1");
            this.emitImplementationPostamble();
            this.endBlock();
        }
        if (several) {
            final String arrayName = propertyName + "Array";
            if (xmltype) {
                this.printJavaDoc("Sets array of all " + propdesc + "  WARNING: This method is not atomicaly synchronized.");
                this.emit("public void set" + arrayName + "(" + type + "[] " + safeVarName + "Array)");
                this.startBlock();
                this.emit("check_orphaned();");
                this.emitPre(sType, 1, identifier, isAttr);
                if (isobj) {
                    if (!isSubstGroup) {
                        this.emit("unionArraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ");");
                    }
                    else {
                        this.emit("unionArraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ", " + setIdentifier + ");");
                    }
                }
                else if (!isSubstGroup) {
                    this.emit("arraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ");");
                }
                else {
                    this.emit("arraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ", " + setIdentifier + ");");
                }
                this.emitPost(sType, 1, identifier, isAttr);
                this.endBlock();
            }
            else {
                this.printJavaDoc("Sets array of all " + propdesc);
                this.emit("public void set" + arrayName + "(" + type + "[] " + safeVarName + "Array)");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr);
                if (isobj) {
                    if (!isSubstGroup) {
                        this.emit("unionArraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ");");
                    }
                    else {
                        this.emit("unionArraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ", " + setIdentifier + ");");
                    }
                }
                else if (prop.getJavaTypeCode() == 20) {
                    if (!isSubstGroup) {
                        this.emit("org.apache.xmlbeans.SimpleValue[] dests = arraySetterHelper(" + safeVarName + "Array.length" + ", " + identifier + ");");
                        this.emit("for ( int i = 0 ; i < dests.length ; i++ ) {");
                        this.emit("    " + this.getUserTypeStaticHandlerMethod(true, (SchemaTypeImpl)prop.getType()) + "(" + safeVarName + "Array[i], dests[i]);");
                        this.emit("}");
                    }
                    else {
                        this.emit("org.apache.xmlbeans.SimpleValue[] dests = arraySetterHelper(" + safeVarName + "Array.length" + ", " + identifier + ", " + setIdentifier + ");");
                        this.emit("for ( int i = 0 ; i < dests.length ; i++ ) {");
                        this.emit("    " + this.getUserTypeStaticHandlerMethod(true, (SchemaTypeImpl)prop.getType()) + "(" + safeVarName + "Array[i], dests[i]);");
                        this.emit("}");
                    }
                }
                else if (!isSubstGroup) {
                    this.emit("arraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ");");
                }
                else {
                    this.emit("arraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ", " + setIdentifier + ");");
                }
                this.emitPost(sType, 1, identifier, isAttr);
                this.emitImplementationPostamble();
                this.endBlock();
            }
            this.printJavaDoc("Sets ith " + propdesc);
            this.emit("public void set" + arrayName + "(int i, " + type + " " + safeVarName + ")");
            this.startBlock();
            if (xmltype && !isSubstGroup) {
                this.emitPre(sType, 1, identifier, isAttr, "i");
                this.emit("generatedSetterHelperImpl(" + safeVarName + ", " + setIdentifier + ", i, " + "org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);");
                this.emitPost(sType, 1, identifier, isAttr, "i");
            }
            else {
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, "i");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, jtargetType);
                this.printJSetValue(javaType, safeVarName, (SchemaTypeImpl)prop.getType());
                this.emitPost(sType, 1, identifier, isAttr, "i");
                this.emitImplementationPostamble();
            }
            this.endBlock();
            if (!xmltype) {
                this.printJavaDoc("Sets (as xml) array of all " + propdesc);
                this.emit("public void xset" + arrayName + "(" + xtype + "[]" + safeVarName + "Array)");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr);
                this.emit("arraySetterHelper(" + safeVarName + "Array" + ", " + identifier + ");");
                this.emitPost(sType, 1, identifier, isAttr);
                this.emitImplementationPostamble();
                this.endBlock();
                this.printJavaDoc("Sets (as xml) ith " + propdesc);
                this.emit("public void xset" + arrayName + "(int i, " + xtype + " " + safeVarName + ")");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, "i");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, xtype);
                this.emit("target.set(" + safeVarName + ");");
                this.emitPost(sType, 1, identifier, isAttr, "i");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (nillable) {
                this.printJavaDoc("Nils the ith " + propdesc);
                this.emit("public void setNil" + arrayName + "(int i)");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, "i");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, xtype);
                this.emit("target.setNil();");
                this.emitPost(sType, 1, identifier, isAttr, "i");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (!xmltype) {
                this.printJavaDoc("Inserts the value as the ith " + propdesc);
                this.emit("public void insert" + propertyName + "(int i, " + type + " " + safeVarName + ")");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 2, identifier, isAttr, "i");
                this.emit(jtargetType + " target = ");
                this.indent();
                if (!isSubstGroup) {
                    this.emit("(" + jtargetType + ")get_store().insert_element_user(" + identifier + ", i);");
                }
                else {
                    this.emit("(" + jtargetType + ")get_store().insert_element_user(" + setIdentifier + ", " + identifier + ", i);");
                }
                this.outdent();
                this.printJSetValue(javaType, safeVarName, (SchemaTypeImpl)prop.getType());
                this.emitPost(sType, 2, identifier, isAttr, "i");
                this.emitImplementationPostamble();
                this.endBlock();
                this.printJavaDoc("Appends the value as the last " + propdesc);
                this.emit("public void add" + propertyName + "(" + type + " " + safeVarName + ")");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitDeclareTarget(true, jtargetType);
                this.emitPre(sType, 2, identifier, isAttr);
                this.emitAddTarget(identifier, isAttr, true, jtargetType);
                this.printJSetValue(javaType, safeVarName, (SchemaTypeImpl)prop.getType());
                this.emitPost(sType, 2, identifier, isAttr);
                this.emitImplementationPostamble();
                this.endBlock();
            }
            this.printJavaDoc("Inserts and returns a new empty value (as xml) as the ith " + propdesc);
            this.emit("public " + xtype + " insertNew" + propertyName + "(int i)");
            this.startBlock();
            this.emitImplementationPreamble();
            this.emitDeclareTarget(true, xtype);
            this.emitPre(sType, 2, identifier, isAttr, "i");
            if (!isSubstGroup) {
                this.emit("target = (" + xtype + ")get_store().insert_element_user(" + identifier + ", i);");
            }
            else {
                this.emit("target = (" + xtype + ")get_store().insert_element_user(" + setIdentifier + ", " + identifier + ", i);");
            }
            this.emitPost(sType, 2, identifier, isAttr, "i");
            this.emit("return target;");
            this.emitImplementationPostamble();
            this.endBlock();
            this.printJavaDoc("Appends and returns a new empty value (as xml) as the last " + propdesc);
            this.emit("public " + xtype + " addNew" + propertyName + "()");
            this.startBlock();
            this.emitImplementationPreamble();
            this.emitDeclareTarget(true, xtype);
            this.emitPre(sType, 2, identifier, isAttr);
            this.emitAddTarget(identifier, isAttr, true, xtype);
            this.emitPost(sType, 2, identifier, isAttr);
            this.emit("return target;");
            this.emitImplementationPostamble();
            this.endBlock();
            this.printJavaDoc("Removes the ith " + propdesc);
            this.emit("public void remove" + propertyName + "(int i)");
            this.startBlock();
            this.emitImplementationPreamble();
            this.emitPre(sType, 3, identifier, isAttr, "i");
            this.emit("get_store().remove_element(" + setIdentifier + ", i);");
            this.emitPost(sType, 3, identifier, isAttr, "i");
            this.emitImplementationPostamble();
            this.endBlock();
        }
    }
    
    static void getTypeName(Class c, final StringBuffer sb) {
        int arrayCount;
        for (arrayCount = 0; c.isArray(); c = c.getComponentType(), ++arrayCount) {}
        sb.append(c.getName());
        for (int i = 0; i < arrayCount; ++i) {
            sb.append("[]");
        }
    }
    
    void printInnerTypeImpl(final SchemaType sType, final SchemaTypeSystem system, final boolean isInner) throws IOException {
        final String shortName = sType.getShortJavaImplName();
        this.printInnerTypeJavaDoc(sType);
        this.startClass(sType, isInner);
        this.printConstructor(sType, shortName);
        this.printExtensionImplMethods(sType);
        if (!sType.isSimpleType()) {
            SchemaProperty[] properties;
            if (sType.getContentType() == 2) {
                SchemaType baseType = sType.getBaseType();
                List extraProperties = null;
                while (!baseType.isSimpleType() && !baseType.isBuiltinType()) {
                    final SchemaProperty[] baseProperties = baseType.getDerivedProperties();
                    for (int i = 0; i < baseProperties.length; ++i) {
                        if (!baseProperties[i].isAttribute() || sType.getAttributeProperty(baseProperties[i].getName()) == null) {
                            if (extraProperties == null) {
                                extraProperties = new ArrayList();
                            }
                            extraProperties.add(baseProperties[i]);
                        }
                    }
                    baseType = baseType.getBaseType();
                }
                properties = sType.getProperties();
                if (extraProperties != null) {
                    for (int j = 0; j < properties.length; ++j) {
                        extraProperties.add(properties[j]);
                    }
                    properties = extraProperties.toArray(new SchemaProperty[extraProperties.size()]);
                }
            }
            else {
                properties = this.getDerivedProperties(sType);
            }
            final Map qNameMap = this.printStaticFields(properties);
            for (int k = 0; k < properties.length; ++k) {
                final SchemaProperty prop = properties[k];
                final QName name = prop.getName();
                final String xmlType = this.xmlTypeForProperty(prop);
                this.printGetterImpls(shortName, prop, name, prop.isAttribute(), prop.getJavaPropertyName(), prop.getJavaTypeCode(), this.javaTypeForProperty(prop), xmlType, prop.hasNillable() != 0, prop.extendsJavaOption(), prop.extendsJavaArray(), prop.extendsJavaSingleton(), xmlTypeForPropertyIsUnion(prop), this.getIdentifier(qNameMap, name), this.getSetIdentifier(qNameMap, name));
                if (!prop.isReadOnly()) {
                    this.printSetterImpls(name, prop, prop.isAttribute(), prop.getJavaPropertyName(), prop.getJavaTypeCode(), this.javaTypeForProperty(prop), xmlType, prop.hasNillable() != 0, prop.extendsJavaOption(), prop.extendsJavaArray(), prop.extendsJavaSingleton(), xmlTypeForPropertyIsUnion(prop), this.getIdentifier(qNameMap, name), this.getSetIdentifier(qNameMap, name), sType);
                }
            }
        }
        this.printNestedTypeImpls(sType, system);
        this.endBlock();
    }
    
    private SchemaProperty[] getDerivedProperties(final SchemaType sType) {
        final QName name = sType.getName();
        if (name != null && name.equals(sType.getBaseType().getName())) {
            SchemaType sType2 = sType.getBaseType();
            SchemaProperty[] props = sType.getDerivedProperties();
            final Map propsByName = new LinkedHashMap();
            for (int i = 0; i < props.length; ++i) {
                propsByName.put(props[i].getName(), props[i]);
            }
            while (sType2 != null && name.equals(sType2.getName())) {
                props = sType2.getDerivedProperties();
                for (int i = 0; i < props.length; ++i) {
                    if (!propsByName.containsKey(props[i].getName())) {
                        propsByName.put(props[i].getName(), props[i]);
                    }
                }
                sType2 = sType2.getBaseType();
            }
            return (SchemaProperty[])propsByName.values().toArray(new SchemaProperty[0]);
        }
        return sType.getDerivedProperties();
    }
    
    private void printExtensionImplMethods(final SchemaType sType) throws IOException {
        final SchemaTypeImpl sImpl = getImpl(sType);
        if (sImpl == null) {
            return;
        }
        final InterfaceExtension[] exts = sImpl.getInterfaceExtensions();
        if (exts != null) {
            for (int i = 0; i < exts.length; ++i) {
                final InterfaceExtension.MethodSignature[] methods = exts[i].getMethods();
                if (methods != null) {
                    for (int j = 0; j < methods.length; ++j) {
                        this.printJavaDoc("Implementation method for interface " + exts[i].getStaticHandler());
                        this.printInterfaceMethodDecl(methods[j]);
                        this.startBlock();
                        this.printInterfaceMethodImpl(exts[i].getStaticHandler(), methods[j]);
                        this.endBlock();
                    }
                }
            }
        }
    }
    
    void printInterfaceMethodDecl(final InterfaceExtension.MethodSignature method) throws IOException {
        final StringBuffer decl = new StringBuffer(60);
        decl.append("public ").append(method.getReturnType());
        decl.append(" ").append(method.getName()).append("(");
        final String[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; ++i) {
            if (i != 0) {
                decl.append(", ");
            }
            decl.append(paramTypes[i]).append(" p").append(i);
        }
        decl.append(")");
        final String[] exceptions = method.getExceptionTypes();
        for (int j = 0; j < exceptions.length; ++j) {
            decl.append(((j == 0) ? " throws " : ", ") + exceptions[j]);
        }
        this.emit(decl.toString());
    }
    
    void printInterfaceMethodImpl(final String handler, final InterfaceExtension.MethodSignature method) throws IOException {
        final StringBuffer impl = new StringBuffer(60);
        if (!method.getReturnType().equals("void")) {
            impl.append("return ");
        }
        impl.append(handler).append(".").append(method.getName()).append("(this");
        final String[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            impl.append(", p" + i);
        }
        impl.append(");");
        this.emit(impl.toString());
    }
    
    void printNestedTypeImpls(SchemaType sType, final SchemaTypeSystem system) throws IOException {
        final boolean redefinition = sType.getName() != null && sType.getName().equals(sType.getBaseType().getName());
        while (sType != null) {
            final SchemaType[] anonTypes = sType.getAnonymousTypes();
            for (int i = 0; i < anonTypes.length; ++i) {
                if (anonTypes[i].isSkippedAnonymousType()) {
                    this.printNestedTypeImpls(anonTypes[i], system);
                }
                else {
                    this.printInnerTypeImpl(anonTypes[i], system, true);
                }
            }
            if (!redefinition) {
                break;
            }
            if (sType.getDerivationType() != 2 && !sType.isSimpleType()) {
                break;
            }
            sType = sType.getBaseType();
        }
    }
    
    static {
        LINE_SEPARATOR = ((SystemProperties.getProperty("line.separator") == null) ? "\n" : SystemProperties.getProperty("line.separator"));
    }
}