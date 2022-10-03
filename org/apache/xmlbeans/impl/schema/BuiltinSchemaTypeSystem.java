package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaAttributeModel;
import org.apache.xmlbeans.SchemaParticle;
import java.util.Collections;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.impl.regex.RegularExpression;
import org.apache.xmlbeans.impl.regex.ParseException;
import org.apache.xmlbeans.impl.regex.SchemaRegularExpression;
import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.values.XmlStringImpl;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.values.XmlIntegerImpl;
import java.math.BigInteger;
import org.apache.xmlbeans.Filer;
import java.io.File;
import java.io.InputStream;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;

public class BuiltinSchemaTypeSystem extends SchemaTypeLoaderBase implements SchemaTypeSystem
{
    private static final SchemaType[] EMPTY_SCHEMATYPE_ARRAY;
    private static final SchemaType.Ref[] EMPTY_SCHEMATYPEREF_ARRAY;
    private static final SchemaGlobalElement[] EMPTY_SCHEMAELEMENT_ARRAY;
    private static final SchemaGlobalAttribute[] EMPTY_SCHEMAATTRIBUTE_ARRAY;
    private static final SchemaModelGroup[] EMPTY_SCHEMAMODELGROUP_ARRAY;
    private static final SchemaAttributeGroup[] EMPTY_SCHEMAATTRIBUTEGROUP_ARRAY;
    private static final SchemaAnnotation[] EMPTY_SCHEMAANNOTATION_ARRAY;
    private static BuiltinSchemaTypeSystem _global;
    public static final SchemaTypeImpl ST_ANY_TYPE;
    public static final SchemaTypeImpl ST_ANY_SIMPLE;
    public static final SchemaTypeImpl ST_BOOLEAN;
    public static final SchemaTypeImpl ST_BASE_64_BINARY;
    public static final SchemaTypeImpl ST_HEX_BINARY;
    public static final SchemaTypeImpl ST_ANY_URI;
    public static final SchemaTypeImpl ST_QNAME;
    public static final SchemaTypeImpl ST_NOTATION;
    public static final SchemaTypeImpl ST_FLOAT;
    public static final SchemaTypeImpl ST_DOUBLE;
    public static final SchemaTypeImpl ST_DECIMAL;
    public static final SchemaTypeImpl ST_STRING;
    public static final SchemaTypeImpl ST_DURATION;
    public static final SchemaTypeImpl ST_DATE_TIME;
    public static final SchemaTypeImpl ST_TIME;
    public static final SchemaTypeImpl ST_DATE;
    public static final SchemaTypeImpl ST_G_YEAR_MONTH;
    public static final SchemaTypeImpl ST_G_YEAR;
    public static final SchemaTypeImpl ST_G_MONTH_DAY;
    public static final SchemaTypeImpl ST_G_DAY;
    public static final SchemaTypeImpl ST_G_MONTH;
    public static final SchemaTypeImpl ST_INTEGER;
    public static final SchemaTypeImpl ST_LONG;
    public static final SchemaTypeImpl ST_INT;
    public static final SchemaTypeImpl ST_SHORT;
    public static final SchemaTypeImpl ST_BYTE;
    public static final SchemaTypeImpl ST_NON_POSITIVE_INTEGER;
    public static final SchemaTypeImpl ST_NEGATIVE_INTEGER;
    public static final SchemaTypeImpl ST_NON_NEGATIVE_INTEGER;
    public static final SchemaTypeImpl ST_POSITIVE_INTEGER;
    public static final SchemaTypeImpl ST_UNSIGNED_LONG;
    public static final SchemaTypeImpl ST_UNSIGNED_INT;
    public static final SchemaTypeImpl ST_UNSIGNED_SHORT;
    public static final SchemaTypeImpl ST_UNSIGNED_BYTE;
    public static final SchemaTypeImpl ST_NORMALIZED_STRING;
    public static final SchemaTypeImpl ST_TOKEN;
    public static final SchemaTypeImpl ST_NAME;
    public static final SchemaTypeImpl ST_NCNAME;
    public static final SchemaTypeImpl ST_LANGUAGE;
    public static final SchemaTypeImpl ST_ID;
    public static final SchemaTypeImpl ST_IDREF;
    public static final SchemaTypeImpl ST_IDREFS;
    public static final SchemaTypeImpl ST_ENTITY;
    public static final SchemaTypeImpl ST_ENTITIES;
    public static final SchemaTypeImpl ST_NMTOKEN;
    public static final SchemaTypeImpl ST_NMTOKENS;
    public static final SchemaTypeImpl ST_NO_TYPE;
    private static final XmlValueRef XMLSTR_PRESERVE;
    private static final XmlValueRef XMLSTR_REPLACE;
    private static final XmlValueRef XMLSTR_COLLAPSE;
    private static final XmlValueRef[] FACETS_NONE;
    private static final boolean[] FIXED_FACETS_NONE;
    private static final XmlValueRef[] FACETS_WS_COLLAPSE;
    private static final XmlValueRef[] FACETS_WS_REPLACE;
    private static final XmlValueRef[] FACETS_WS_PRESERVE;
    private static final XmlValueRef[] FACETS_INTEGER;
    private static final XmlValueRef[] FACETS_LONG;
    private static final XmlValueRef[] FACETS_INT;
    private static final XmlValueRef[] FACETS_SHORT;
    private static final XmlValueRef[] FACETS_BYTE;
    private static final XmlValueRef[] FACETS_NONNEGATIVE;
    private static final XmlValueRef[] FACETS_POSITIVE;
    private static final XmlValueRef[] FACETS_NONPOSITIVE;
    private static final XmlValueRef[] FACETS_NEGATIVE;
    private static final XmlValueRef[] FACETS_UNSIGNED_LONG;
    private static final XmlValueRef[] FACETS_UNSIGNED_INT;
    private static final XmlValueRef[] FACETS_UNSIGNED_SHORT;
    private static final XmlValueRef[] FACETS_UNSIGNED_BYTE;
    private static final XmlValueRef[] FACETS_BUILTIN_LIST;
    private static final boolean[] FIXED_FACETS_WS;
    private static final boolean[] FIXED_FACETS_INTEGER;
    static final XmlValueRef[] FACETS_UNION;
    static final boolean[] FIXED_FACETS_UNION;
    static final XmlValueRef[] FACETS_LIST;
    static final boolean[] FIXED_FACETS_LIST;
    private Map _typeMap;
    private SchemaTypeImpl[] _typeArray;
    private Map _handlesToObjects;
    private Map _objectsToHandles;
    private Map _typesByClassname;
    private SchemaContainer _container;
    
    public static SchemaTypeSystem get() {
        return BuiltinSchemaTypeSystem._global;
    }
    
    private SchemaTypeImpl getBuiltinType(final int btc) {
        return this._typeArray[btc];
    }
    
    private BuiltinSchemaTypeSystem() {
        this._typeMap = new HashMap();
        this._typeArray = new SchemaTypeImpl[47];
        this._handlesToObjects = new HashMap();
        this._objectsToHandles = new HashMap();
        this._typesByClassname = new HashMap();
        (this._container = new SchemaContainer("http://www.w3.org/2001/XMLSchema")).setTypeSystem(this);
        this.setupBuiltin(1, "anyType", "org.apache.xmlbeans.XmlObject");
        this.setupBuiltin(2, "anySimpleType", "org.apache.xmlbeans.XmlAnySimpleType");
        this.setupBuiltin(3, "boolean", "org.apache.xmlbeans.XmlBoolean");
        this.setupBuiltin(4, "base64Binary", "org.apache.xmlbeans.XmlBase64Binary");
        this.setupBuiltin(5, "hexBinary", "org.apache.xmlbeans.XmlHexBinary");
        this.setupBuiltin(6, "anyURI", "org.apache.xmlbeans.XmlAnyURI");
        this.setupBuiltin(7, "QName", "org.apache.xmlbeans.XmlQName");
        this.setupBuiltin(8, "NOTATION", "org.apache.xmlbeans.XmlNOTATION");
        this.setupBuiltin(9, "float", "org.apache.xmlbeans.XmlFloat");
        this.setupBuiltin(10, "double", "org.apache.xmlbeans.XmlDouble");
        this.setupBuiltin(11, "decimal", "org.apache.xmlbeans.XmlDecimal");
        this.setupBuiltin(12, "string", "org.apache.xmlbeans.XmlString");
        this.setupBuiltin(13, "duration", "org.apache.xmlbeans.XmlDuration");
        this.setupBuiltin(14, "dateTime", "org.apache.xmlbeans.XmlDateTime");
        this.setupBuiltin(15, "time", "org.apache.xmlbeans.XmlTime");
        this.setupBuiltin(16, "date", "org.apache.xmlbeans.XmlDate");
        this.setupBuiltin(17, "gYearMonth", "org.apache.xmlbeans.XmlGYearMonth");
        this.setupBuiltin(18, "gYear", "org.apache.xmlbeans.XmlGYear");
        this.setupBuiltin(19, "gMonthDay", "org.apache.xmlbeans.XmlGMonthDay");
        this.setupBuiltin(20, "gDay", "org.apache.xmlbeans.XmlGDay");
        this.setupBuiltin(21, "gMonth", "org.apache.xmlbeans.XmlGMonth");
        this.setupBuiltin(22, "integer", "org.apache.xmlbeans.XmlInteger");
        this.setupBuiltin(23, "long", "org.apache.xmlbeans.XmlLong");
        this.setupBuiltin(24, "int", "org.apache.xmlbeans.XmlInt");
        this.setupBuiltin(25, "short", "org.apache.xmlbeans.XmlShort");
        this.setupBuiltin(26, "byte", "org.apache.xmlbeans.XmlByte");
        this.setupBuiltin(27, "nonPositiveInteger", "org.apache.xmlbeans.XmlNonPositiveInteger");
        this.setupBuiltin(28, "negativeInteger", "org.apache.xmlbeans.XmlNegativeInteger");
        this.setupBuiltin(29, "nonNegativeInteger", "org.apache.xmlbeans.XmlNonNegativeInteger");
        this.setupBuiltin(30, "positiveInteger", "org.apache.xmlbeans.XmlPositiveInteger");
        this.setupBuiltin(31, "unsignedLong", "org.apache.xmlbeans.XmlUnsignedLong");
        this.setupBuiltin(32, "unsignedInt", "org.apache.xmlbeans.XmlUnsignedInt");
        this.setupBuiltin(33, "unsignedShort", "org.apache.xmlbeans.XmlUnsignedShort");
        this.setupBuiltin(34, "unsignedByte", "org.apache.xmlbeans.XmlUnsignedByte");
        this.setupBuiltin(35, "normalizedString", "org.apache.xmlbeans.XmlNormalizedString");
        this.setupBuiltin(36, "token", "org.apache.xmlbeans.XmlToken");
        this.setupBuiltin(37, "Name", "org.apache.xmlbeans.XmlName");
        this.setupBuiltin(38, "NCName", "org.apache.xmlbeans.XmlNCName");
        this.setupBuiltin(39, "language", "org.apache.xmlbeans.XmlLanguage");
        this.setupBuiltin(40, "ID", "org.apache.xmlbeans.XmlID");
        this.setupBuiltin(41, "IDREF", "org.apache.xmlbeans.XmlIDREF");
        this.setupBuiltin(42, "IDREFS", "org.apache.xmlbeans.XmlIDREFS");
        this.setupBuiltin(43, "ENTITY", "org.apache.xmlbeans.XmlENTITY");
        this.setupBuiltin(44, "ENTITIES", "org.apache.xmlbeans.XmlENTITIES");
        this.setupBuiltin(45, "NMTOKEN", "org.apache.xmlbeans.XmlNMTOKEN");
        this.setupBuiltin(46, "NMTOKENS", "org.apache.xmlbeans.XmlNMTOKENS");
        this.setupBuiltin(0, null, null);
        this._container.setImmutable();
    }
    
    @Override
    public String getName() {
        return "schema.typesystem.builtin";
    }
    
    @Override
    public boolean isNamespaceDefined(final String namespace) {
        return namespace.equals("http://www.w3.org/2001/XMLSchema");
    }
    
    @Override
    public SchemaType findType(final QName name) {
        return this._typeMap.get(name);
    }
    
    @Override
    public SchemaType findDocumentType(final QName name) {
        return null;
    }
    
    @Override
    public SchemaType findAttributeType(final QName name) {
        return null;
    }
    
    @Override
    public SchemaGlobalElement findElement(final QName name) {
        return null;
    }
    
    @Override
    public SchemaGlobalAttribute findAttribute(final QName name) {
        return null;
    }
    
    @Override
    public SchemaType.Ref findTypeRef(final QName name) {
        final SchemaType type = this.findType(name);
        return (type == null) ? null : type.getRef();
    }
    
    @Override
    public SchemaType.Ref findDocumentTypeRef(final QName name) {
        return null;
    }
    
    @Override
    public SchemaType.Ref findAttributeTypeRef(final QName name) {
        return null;
    }
    
    @Override
    public SchemaGlobalElement.Ref findElementRef(final QName name) {
        return null;
    }
    
    @Override
    public SchemaGlobalAttribute.Ref findAttributeRef(final QName name) {
        return null;
    }
    
    @Override
    public SchemaModelGroup.Ref findModelGroupRef(final QName name) {
        return null;
    }
    
    @Override
    public SchemaAttributeGroup.Ref findAttributeGroupRef(final QName name) {
        return null;
    }
    
    @Override
    public SchemaIdentityConstraint.Ref findIdentityConstraintRef(final QName name) {
        return null;
    }
    
    @Override
    public SchemaType typeForClassname(final String classname) {
        return this._typesByClassname.get(classname);
    }
    
    @Override
    public InputStream getSourceAsStream(final String sourceName) {
        return null;
    }
    
    @Override
    public SchemaType[] globalTypes() {
        final SchemaType[] result = new SchemaType[this._typeArray.length - 1];
        System.arraycopy(this._typeArray, 1, result, 0, result.length);
        return result;
    }
    
    @Override
    public SchemaType[] documentTypes() {
        return BuiltinSchemaTypeSystem.EMPTY_SCHEMATYPE_ARRAY;
    }
    
    @Override
    public SchemaType[] attributeTypes() {
        return BuiltinSchemaTypeSystem.EMPTY_SCHEMATYPE_ARRAY;
    }
    
    @Override
    public SchemaGlobalElement[] globalElements() {
        return BuiltinSchemaTypeSystem.EMPTY_SCHEMAELEMENT_ARRAY;
    }
    
    @Override
    public SchemaGlobalAttribute[] globalAttributes() {
        return BuiltinSchemaTypeSystem.EMPTY_SCHEMAATTRIBUTE_ARRAY;
    }
    
    @Override
    public SchemaModelGroup[] modelGroups() {
        return BuiltinSchemaTypeSystem.EMPTY_SCHEMAMODELGROUP_ARRAY;
    }
    
    @Override
    public SchemaAttributeGroup[] attributeGroups() {
        return BuiltinSchemaTypeSystem.EMPTY_SCHEMAATTRIBUTEGROUP_ARRAY;
    }
    
    @Override
    public SchemaAnnotation[] annotations() {
        return BuiltinSchemaTypeSystem.EMPTY_SCHEMAANNOTATION_ARRAY;
    }
    
    public String handleForType(final SchemaType type) {
        return this._objectsToHandles.get(type);
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return BuiltinSchemaTypeSystem.class.getClassLoader();
    }
    
    @Override
    public void saveToDirectory(final File classDir) {
        throw new UnsupportedOperationException("The builtin schema type system cannot be saved.");
    }
    
    @Override
    public void save(final Filer filer) {
        throw new UnsupportedOperationException("The builtin schema type system cannot be saved.");
    }
    
    private static XmlValueRef build_wsstring(final int wsr) {
        switch (wsr) {
            case 1: {
                return BuiltinSchemaTypeSystem.XMLSTR_PRESERVE;
            }
            case 2: {
                return BuiltinSchemaTypeSystem.XMLSTR_REPLACE;
            }
            case 3: {
                return BuiltinSchemaTypeSystem.XMLSTR_COLLAPSE;
            }
            default: {
                return null;
            }
        }
    }
    
    private static XmlValueRef buildNnInteger(final BigInteger bigInt) {
        if (bigInt == null) {
            return null;
        }
        if (bigInt.signum() < 0) {
            return null;
        }
        try {
            final XmlIntegerImpl i = new XmlIntegerImpl();
            i.set(bigInt);
            i.setImmutable();
            return new XmlValueRef(i);
        }
        catch (final XmlValueOutOfRangeException e) {
            return null;
        }
    }
    
    private static XmlValueRef buildInteger(final BigInteger bigInt) {
        if (bigInt == null) {
            return null;
        }
        try {
            final XmlIntegerImpl i = new XmlIntegerImpl();
            i.set(bigInt);
            i.setImmutable();
            return new XmlValueRef(i);
        }
        catch (final XmlValueOutOfRangeException e) {
            return null;
        }
    }
    
    private static XmlValueRef buildString(final String str) {
        if (str == null) {
            return null;
        }
        try {
            final XmlStringImpl i = new XmlStringImpl();
            i.set(str);
            i.setImmutable();
            return new XmlValueRef(i);
        }
        catch (final XmlValueOutOfRangeException e) {
            return null;
        }
    }
    
    private void setupBuiltin(final int btc, final String localname, final String classname) {
        final SchemaTypeImpl result = new SchemaTypeImpl(this._container, true);
        this._container.addGlobalType(result.getRef());
        final QName name = (localname == null) ? null : QNameHelper.forLNS(localname, "http://www.w3.org/2001/XMLSchema");
        final String handle = "_BI_" + ((localname == null) ? "NO_TYPE" : localname);
        result.setName(name);
        result.setBuiltinTypeCode(btc);
        if (classname != null) {
            result.setFullJavaName(classname);
        }
        this._typeArray[btc] = result;
        this._typeMap.put(name, result);
        this._handlesToObjects.put(handle, result);
        this._objectsToHandles.put(result, handle);
        if (classname != null) {
            this._typesByClassname.put(classname, result);
        }
    }
    
    @Override
    public void resolve() {
    }
    
    @Override
    public SchemaType typeForHandle(final String handle) {
        return this._handlesToObjects.get(handle);
    }
    
    @Override
    public SchemaComponent resolveHandle(final String handle) {
        return this._handlesToObjects.get(handle);
    }
    
    public void fillInType(final int btc) {
        final SchemaTypeImpl result = this.getBuiltinType(btc);
        SchemaType item = null;
        int variety = 1;
        int derivationType = 1;
        SchemaType base = null;
        switch (btc) {
            case 0: {
                variety = 0;
                base = BuiltinSchemaTypeSystem.ST_ANY_TYPE;
                break;
            }
            case 1: {
                variety = 0;
                base = null;
                derivationType = 1;
                break;
            }
            default: {
                assert false;
            }
            case 2: {
                base = BuiltinSchemaTypeSystem.ST_ANY_TYPE;
                break;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                base = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
                break;
            }
            case 22: {
                base = BuiltinSchemaTypeSystem.ST_DECIMAL;
                break;
            }
            case 23: {
                base = BuiltinSchemaTypeSystem.ST_INTEGER;
                break;
            }
            case 24: {
                base = BuiltinSchemaTypeSystem.ST_LONG;
                break;
            }
            case 25: {
                base = BuiltinSchemaTypeSystem.ST_INT;
                break;
            }
            case 26: {
                base = BuiltinSchemaTypeSystem.ST_SHORT;
                break;
            }
            case 27: {
                base = BuiltinSchemaTypeSystem.ST_INTEGER;
                break;
            }
            case 28: {
                base = BuiltinSchemaTypeSystem.ST_NON_POSITIVE_INTEGER;
                break;
            }
            case 29: {
                base = BuiltinSchemaTypeSystem.ST_INTEGER;
                break;
            }
            case 30: {
                base = BuiltinSchemaTypeSystem.ST_NON_NEGATIVE_INTEGER;
                break;
            }
            case 31: {
                base = BuiltinSchemaTypeSystem.ST_NON_NEGATIVE_INTEGER;
                break;
            }
            case 32: {
                base = BuiltinSchemaTypeSystem.ST_UNSIGNED_LONG;
                break;
            }
            case 33: {
                base = BuiltinSchemaTypeSystem.ST_UNSIGNED_INT;
                break;
            }
            case 34: {
                base = BuiltinSchemaTypeSystem.ST_UNSIGNED_SHORT;
                break;
            }
            case 35: {
                base = BuiltinSchemaTypeSystem.ST_STRING;
                break;
            }
            case 36: {
                base = BuiltinSchemaTypeSystem.ST_NORMALIZED_STRING;
                break;
            }
            case 37: {
                base = BuiltinSchemaTypeSystem.ST_TOKEN;
                break;
            }
            case 38: {
                base = BuiltinSchemaTypeSystem.ST_NAME;
                break;
            }
            case 40:
            case 41:
            case 43: {
                base = BuiltinSchemaTypeSystem.ST_NCNAME;
                break;
            }
            case 39:
            case 45: {
                base = BuiltinSchemaTypeSystem.ST_TOKEN;
                break;
            }
            case 42:
            case 44:
            case 46: {
                variety = 3;
                base = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
                if (btc == 42) {
                    item = BuiltinSchemaTypeSystem.ST_IDREF;
                    break;
                }
                if (btc == 44) {
                    item = BuiltinSchemaTypeSystem.ST_ENTITY;
                    break;
                }
                item = BuiltinSchemaTypeSystem.ST_NMTOKEN;
                break;
            }
        }
        result.setDerivationType(derivationType);
        result.setSimpleTypeVariety(variety);
        if (variety != 0) {
            result.setSimpleType(true);
        }
        else {
            assert btc == 0;
        }
        result.setBaseTypeRef((base == null) ? null : base.getRef());
        result.setBaseDepth((base == null) ? 0 : (((SchemaTypeImpl)base).getBaseDepth() + 1));
        result.setListItemTypeRef((item == null) ? null : item.getRef());
        if (btc >= 2 && btc <= 21) {
            result.setPrimitiveTypeRef(result.getRef());
        }
        else if (variety == 1) {
            if (base == null) {
                throw new IllegalStateException("Base was null for " + btc);
            }
            if (base.getPrimitiveType() == null) {
                throw new IllegalStateException("Base.gpt was null for " + btc);
            }
            result.setPrimitiveTypeRef(base.getPrimitiveType().getRef());
        }
        int wsr = 3;
        int decimalSize = 0;
        XmlValueRef[] facets = null;
        boolean[] fixedf = null;
        switch (btc) {
            default: {
                assert false;
            }
            case 0:
            case 1:
            case 2: {
                facets = BuiltinSchemaTypeSystem.FACETS_NONE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_NONE;
                wsr = 0;
                break;
            }
            case 12: {
                facets = BuiltinSchemaTypeSystem.FACETS_WS_PRESERVE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_NONE;
                wsr = 1;
                break;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                facets = BuiltinSchemaTypeSystem.FACETS_WS_COLLAPSE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_WS;
                break;
            }
            case 11: {
                facets = BuiltinSchemaTypeSystem.FACETS_WS_COLLAPSE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_WS;
                decimalSize = 1000001;
                break;
            }
            case 22: {
                facets = BuiltinSchemaTypeSystem.FACETS_INTEGER;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 1000000;
                break;
            }
            case 23: {
                facets = BuiltinSchemaTypeSystem.FACETS_LONG;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 64;
                break;
            }
            case 24: {
                facets = BuiltinSchemaTypeSystem.FACETS_INT;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 32;
                break;
            }
            case 25: {
                facets = BuiltinSchemaTypeSystem.FACETS_SHORT;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 16;
                break;
            }
            case 26: {
                facets = BuiltinSchemaTypeSystem.FACETS_BYTE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 8;
                break;
            }
            case 27: {
                facets = BuiltinSchemaTypeSystem.FACETS_NONPOSITIVE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 1000000;
                break;
            }
            case 28: {
                facets = BuiltinSchemaTypeSystem.FACETS_NEGATIVE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 1000000;
                break;
            }
            case 29: {
                facets = BuiltinSchemaTypeSystem.FACETS_NONNEGATIVE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 1000000;
                break;
            }
            case 30: {
                facets = BuiltinSchemaTypeSystem.FACETS_POSITIVE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 1000000;
                break;
            }
            case 31: {
                facets = BuiltinSchemaTypeSystem.FACETS_UNSIGNED_LONG;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 1000000;
                break;
            }
            case 32: {
                facets = BuiltinSchemaTypeSystem.FACETS_UNSIGNED_INT;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 64;
                break;
            }
            case 33: {
                facets = BuiltinSchemaTypeSystem.FACETS_UNSIGNED_SHORT;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 32;
                break;
            }
            case 34: {
                facets = BuiltinSchemaTypeSystem.FACETS_UNSIGNED_BYTE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_INTEGER;
                decimalSize = 16;
                break;
            }
            case 35: {
                facets = BuiltinSchemaTypeSystem.FACETS_WS_REPLACE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_NONE;
                wsr = 2;
                break;
            }
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 45: {
                facets = BuiltinSchemaTypeSystem.FACETS_WS_COLLAPSE;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_NONE;
                wsr = 3;
                break;
            }
            case 44:
            case 46: {
                facets = BuiltinSchemaTypeSystem.FACETS_BUILTIN_LIST;
                fixedf = BuiltinSchemaTypeSystem.FIXED_FACETS_NONE;
                wsr = 0;
                break;
            }
        }
        int ordered = 0;
        boolean isNumeric = false;
        boolean isFinite = false;
        boolean isBounded = false;
        switch (btc) {
            default: {
                assert false;
                break;
            }
            case 0:
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 12:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46: {
                break;
            }
            case 3: {
                isFinite = true;
                break;
            }
            case 9:
            case 10:
            case 11:
            case 22: {
                isNumeric = true;
                ordered = 2;
                break;
            }
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                ordered = 1;
                break;
            }
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34: {
                isNumeric = true;
                ordered = 2;
                isFinite = true;
                isBounded = true;
                break;
            }
        }
        result.setBasicFacets(facets, fixedf);
        result.setWhiteSpaceRule(wsr);
        result.setOrdered(ordered);
        result.setBounded(isBounded);
        result.setNumeric(isNumeric);
        result.setFinite(isFinite);
        result.setDecimalSize(decimalSize);
        result.setAnonymousTypeRefs(BuiltinSchemaTypeSystem.EMPTY_SCHEMATYPEREF_ARRAY);
        String pattern = null;
        boolean hasPattern = false;
        switch (btc) {
            case 39: {
                pattern = "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*";
                hasPattern = true;
                break;
            }
            case 45: {
                pattern = "\\c+";
                hasPattern = true;
                break;
            }
            case 37: {
                pattern = "\\i\\c*";
                hasPattern = true;
                break;
            }
            case 38: {
                pattern = "[\\i-[:]][\\c-[:]]*";
                hasPattern = true;
                break;
            }
            case 40:
            case 41:
            case 43: {
                hasPattern = true;
                break;
            }
        }
        if (pattern != null) {
            RegularExpression p = null;
            try {
                p = SchemaRegularExpression.forPattern(pattern);
            }
            catch (final ParseException e) {
                assert false;
            }
            result.setPatterns(new RegularExpression[] { p });
        }
        result.setPatternFacet(hasPattern);
        if (btc == 1) {
            final SchemaParticleImpl contentModel = new SchemaParticleImpl();
            contentModel.setParticleType(5);
            contentModel.setWildcardSet(QNameSet.ALL);
            contentModel.setWildcardProcess(2);
            contentModel.setMinOccurs(BigInteger.ZERO);
            contentModel.setMaxOccurs(null);
            contentModel.setTransitionRules(QNameSet.ALL, true);
            contentModel.setTransitionNotes(QNameSet.ALL, true);
            final SchemaAttributeModelImpl attrModel = new SchemaAttributeModelImpl();
            attrModel.setWildcardProcess(2);
            attrModel.setWildcardSet(QNameSet.ALL);
            result.setComplexTypeVariety(4);
            result.setContentModel(contentModel, attrModel, Collections.EMPTY_MAP, Collections.EMPTY_MAP, false);
            result.setAnonymousTypeRefs(BuiltinSchemaTypeSystem.EMPTY_SCHEMATYPEREF_ARRAY);
            result.setWildcardSummary(QNameSet.ALL, true, QNameSet.ALL, true);
        }
        else if (btc == 0) {
            final SchemaParticleImpl contentModel = null;
            final SchemaAttributeModelImpl attrModel = new SchemaAttributeModelImpl();
            result.setComplexTypeVariety(1);
            result.setContentModel(contentModel, attrModel, Collections.EMPTY_MAP, Collections.EMPTY_MAP, false);
            result.setAnonymousTypeRefs(BuiltinSchemaTypeSystem.EMPTY_SCHEMATYPEREF_ARRAY);
            result.setWildcardSummary(QNameSet.EMPTY, false, QNameSet.EMPTY, false);
        }
        result.setOrderSensitive(false);
    }
    
    public static SchemaType getNoType() {
        return BuiltinSchemaTypeSystem.ST_NO_TYPE;
    }
    
    static {
        EMPTY_SCHEMATYPE_ARRAY = new SchemaType[0];
        EMPTY_SCHEMATYPEREF_ARRAY = new SchemaType.Ref[0];
        EMPTY_SCHEMAELEMENT_ARRAY = new SchemaGlobalElement[0];
        EMPTY_SCHEMAATTRIBUTE_ARRAY = new SchemaGlobalAttribute[0];
        EMPTY_SCHEMAMODELGROUP_ARRAY = new SchemaModelGroup[0];
        EMPTY_SCHEMAATTRIBUTEGROUP_ARRAY = new SchemaAttributeGroup[0];
        EMPTY_SCHEMAANNOTATION_ARRAY = new SchemaAnnotation[0];
        BuiltinSchemaTypeSystem._global = new BuiltinSchemaTypeSystem();
        ST_ANY_TYPE = BuiltinSchemaTypeSystem._global.getBuiltinType(1);
        ST_ANY_SIMPLE = BuiltinSchemaTypeSystem._global.getBuiltinType(2);
        ST_BOOLEAN = BuiltinSchemaTypeSystem._global.getBuiltinType(3);
        ST_BASE_64_BINARY = BuiltinSchemaTypeSystem._global.getBuiltinType(4);
        ST_HEX_BINARY = BuiltinSchemaTypeSystem._global.getBuiltinType(5);
        ST_ANY_URI = BuiltinSchemaTypeSystem._global.getBuiltinType(6);
        ST_QNAME = BuiltinSchemaTypeSystem._global.getBuiltinType(7);
        ST_NOTATION = BuiltinSchemaTypeSystem._global.getBuiltinType(8);
        ST_FLOAT = BuiltinSchemaTypeSystem._global.getBuiltinType(9);
        ST_DOUBLE = BuiltinSchemaTypeSystem._global.getBuiltinType(10);
        ST_DECIMAL = BuiltinSchemaTypeSystem._global.getBuiltinType(11);
        ST_STRING = BuiltinSchemaTypeSystem._global.getBuiltinType(12);
        ST_DURATION = BuiltinSchemaTypeSystem._global.getBuiltinType(13);
        ST_DATE_TIME = BuiltinSchemaTypeSystem._global.getBuiltinType(14);
        ST_TIME = BuiltinSchemaTypeSystem._global.getBuiltinType(15);
        ST_DATE = BuiltinSchemaTypeSystem._global.getBuiltinType(16);
        ST_G_YEAR_MONTH = BuiltinSchemaTypeSystem._global.getBuiltinType(17);
        ST_G_YEAR = BuiltinSchemaTypeSystem._global.getBuiltinType(18);
        ST_G_MONTH_DAY = BuiltinSchemaTypeSystem._global.getBuiltinType(19);
        ST_G_DAY = BuiltinSchemaTypeSystem._global.getBuiltinType(20);
        ST_G_MONTH = BuiltinSchemaTypeSystem._global.getBuiltinType(21);
        ST_INTEGER = BuiltinSchemaTypeSystem._global.getBuiltinType(22);
        ST_LONG = BuiltinSchemaTypeSystem._global.getBuiltinType(23);
        ST_INT = BuiltinSchemaTypeSystem._global.getBuiltinType(24);
        ST_SHORT = BuiltinSchemaTypeSystem._global.getBuiltinType(25);
        ST_BYTE = BuiltinSchemaTypeSystem._global.getBuiltinType(26);
        ST_NON_POSITIVE_INTEGER = BuiltinSchemaTypeSystem._global.getBuiltinType(27);
        ST_NEGATIVE_INTEGER = BuiltinSchemaTypeSystem._global.getBuiltinType(28);
        ST_NON_NEGATIVE_INTEGER = BuiltinSchemaTypeSystem._global.getBuiltinType(29);
        ST_POSITIVE_INTEGER = BuiltinSchemaTypeSystem._global.getBuiltinType(30);
        ST_UNSIGNED_LONG = BuiltinSchemaTypeSystem._global.getBuiltinType(31);
        ST_UNSIGNED_INT = BuiltinSchemaTypeSystem._global.getBuiltinType(32);
        ST_UNSIGNED_SHORT = BuiltinSchemaTypeSystem._global.getBuiltinType(33);
        ST_UNSIGNED_BYTE = BuiltinSchemaTypeSystem._global.getBuiltinType(34);
        ST_NORMALIZED_STRING = BuiltinSchemaTypeSystem._global.getBuiltinType(35);
        ST_TOKEN = BuiltinSchemaTypeSystem._global.getBuiltinType(36);
        ST_NAME = BuiltinSchemaTypeSystem._global.getBuiltinType(37);
        ST_NCNAME = BuiltinSchemaTypeSystem._global.getBuiltinType(38);
        ST_LANGUAGE = BuiltinSchemaTypeSystem._global.getBuiltinType(39);
        ST_ID = BuiltinSchemaTypeSystem._global.getBuiltinType(40);
        ST_IDREF = BuiltinSchemaTypeSystem._global.getBuiltinType(41);
        ST_IDREFS = BuiltinSchemaTypeSystem._global.getBuiltinType(42);
        ST_ENTITY = BuiltinSchemaTypeSystem._global.getBuiltinType(43);
        ST_ENTITIES = BuiltinSchemaTypeSystem._global.getBuiltinType(44);
        ST_NMTOKEN = BuiltinSchemaTypeSystem._global.getBuiltinType(45);
        ST_NMTOKENS = BuiltinSchemaTypeSystem._global.getBuiltinType(46);
        ST_NO_TYPE = BuiltinSchemaTypeSystem._global.getBuiltinType(0);
        XMLSTR_PRESERVE = buildString("preserve");
        XMLSTR_REPLACE = buildString("preserve");
        XMLSTR_COLLAPSE = buildString("preserve");
        FACETS_NONE = new XmlValueRef[] { null, null, null, null, null, null, null, null, null, null, null, null };
        FIXED_FACETS_NONE = new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false };
        FACETS_WS_COLLAPSE = new XmlValueRef[] { null, null, null, null, null, null, null, null, null, build_wsstring(3), null, null };
        FACETS_WS_REPLACE = new XmlValueRef[] { null, null, null, null, null, null, null, null, null, build_wsstring(2), null, null };
        FACETS_WS_PRESERVE = new XmlValueRef[] { null, null, null, null, null, null, null, null, null, build_wsstring(1), null, null };
        FACETS_INTEGER = new XmlValueRef[] { null, null, null, null, null, null, null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_LONG = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.valueOf(Long.MIN_VALUE)), buildInteger(BigInteger.valueOf(Long.MAX_VALUE)), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_INT = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.valueOf(-2147483648L)), buildInteger(BigInteger.valueOf(2147483647L)), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_SHORT = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.valueOf(-32768L)), buildInteger(BigInteger.valueOf(32767L)), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_BYTE = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.valueOf(-128L)), buildInteger(BigInteger.valueOf(127L)), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_NONNEGATIVE = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.ZERO), null, null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_POSITIVE = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.ONE), null, null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_NONPOSITIVE = new XmlValueRef[] { null, null, null, null, null, buildInteger(BigInteger.ZERO), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_NEGATIVE = new XmlValueRef[] { null, null, null, null, null, buildInteger(BigInteger.ONE.negate()), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_UNSIGNED_LONG = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.ZERO), buildInteger(new BigInteger("18446744073709551615")), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_UNSIGNED_INT = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.ZERO), buildInteger(BigInteger.valueOf(4294967295L)), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_UNSIGNED_SHORT = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.ZERO), buildInteger(BigInteger.valueOf(65535L)), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_UNSIGNED_BYTE = new XmlValueRef[] { null, null, null, null, buildInteger(BigInteger.ZERO), buildInteger(BigInteger.valueOf(255L)), null, null, buildNnInteger(BigInteger.ZERO), build_wsstring(3), null, null };
        FACETS_BUILTIN_LIST = new XmlValueRef[] { null, buildNnInteger(BigInteger.ONE), null, null, null, null, null, null, null, build_wsstring(3), null, null };
        FIXED_FACETS_WS = new boolean[] { false, false, false, false, false, false, false, false, false, true, false, false };
        FIXED_FACETS_INTEGER = new boolean[] { false, false, false, false, false, false, false, false, true, true, false, false };
        FACETS_UNION = BuiltinSchemaTypeSystem.FACETS_NONE;
        FIXED_FACETS_UNION = BuiltinSchemaTypeSystem.FIXED_FACETS_NONE;
        FACETS_LIST = BuiltinSchemaTypeSystem.FACETS_WS_COLLAPSE;
        FIXED_FACETS_LIST = BuiltinSchemaTypeSystem.FIXED_FACETS_WS;
        for (int i = 0; i <= 46; ++i) {
            BuiltinSchemaTypeSystem._global.fillInType(i);
        }
    }
}
