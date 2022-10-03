package org.apache.xerces.impl.dv.xs;

import java.math.BigInteger;
import java.util.AbstractList;
import java.util.Locale;
import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.impl.xs.util.ShortListImpl;
import java.util.StringTokenizer;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.xpath.regex.RegularExpression;
import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.impl.dv.DatatypeException;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.impl.dv.ValidatedInfo;
import java.util.Vector;
import org.apache.xerces.impl.dv.ValidationContext;
import org.w3c.dom.TypeInfo;
import org.apache.xerces.impl.dv.XSSimpleType;

public class XSSimpleTypeDecl implements XSSimpleType, TypeInfo
{
    protected static final short DV_STRING = 1;
    protected static final short DV_BOOLEAN = 2;
    protected static final short DV_DECIMAL = 3;
    protected static final short DV_FLOAT = 4;
    protected static final short DV_DOUBLE = 5;
    protected static final short DV_DURATION = 6;
    protected static final short DV_DATETIME = 7;
    protected static final short DV_TIME = 8;
    protected static final short DV_DATE = 9;
    protected static final short DV_GYEARMONTH = 10;
    protected static final short DV_GYEAR = 11;
    protected static final short DV_GMONTHDAY = 12;
    protected static final short DV_GDAY = 13;
    protected static final short DV_GMONTH = 14;
    protected static final short DV_HEXBINARY = 15;
    protected static final short DV_BASE64BINARY = 16;
    protected static final short DV_ANYURI = 17;
    protected static final short DV_QNAME = 18;
    protected static final short DV_PRECISIONDECIMAL = 19;
    protected static final short DV_NOTATION = 20;
    protected static final short DV_ANYSIMPLETYPE = 0;
    protected static final short DV_ID = 21;
    protected static final short DV_IDREF = 22;
    protected static final short DV_ENTITY = 23;
    protected static final short DV_INTEGER = 24;
    protected static final short DV_LIST = 25;
    protected static final short DV_UNION = 26;
    protected static final short DV_YEARMONTHDURATION = 27;
    protected static final short DV_DAYTIMEDURATION = 28;
    protected static final short DV_ANYATOMICTYPE = 29;
    protected static final short DV_ERROR = 30;
    protected static final short DV_DATETIMESTAMP = 31;
    private static final TypeValidator[] gDVs;
    static final short NORMALIZE_NONE = 0;
    static final short NORMALIZE_TRIM = 1;
    static final short NORMALIZE_FULL = 2;
    static final short[] fDVNormalizeType;
    static final short SPECIAL_PATTERN_NONE = 0;
    static final short SPECIAL_PATTERN_NMTOKEN = 1;
    static final short SPECIAL_PATTERN_NAME = 2;
    static final short SPECIAL_PATTERN_NCNAME = 3;
    static final String[] SPECIAL_PATTERN_STRING;
    static final String[] WS_FACET_STRING;
    static final String[] ET_FACET_STRING;
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static final String ANY_TYPE = "anyType";
    public static final short YEARMONTHDURATION_DT = 46;
    public static final short DAYTIMEDURATION_DT = 47;
    public static final short PRECISIONDECIMAL_DT = 48;
    public static final short ANYATOMICTYPE_DT = 49;
    public static final short ERROR_DT = 50;
    public static final short DATETIMESTAMP_DT = 51;
    static final int DERIVATION_ANY = 0;
    static final int DERIVATION_RESTRICTION = 1;
    static final int DERIVATION_EXTENSION = 2;
    static final int DERIVATION_UNION = 4;
    static final int DERIVATION_LIST = 8;
    static final ValidationContext fEmptyContext;
    private TypeValidator[] fDVs;
    private static TypeValidatorHelper fDefaultTypeValidatorHelper;
    private boolean fIsImmutable;
    private XSSimpleTypeDecl fItemType;
    private XSSimpleTypeDecl[] fMemberTypes;
    private short fBuiltInKind;
    private String fTypeName;
    private String fTargetNamespace;
    private short fFinalSet;
    private XSSimpleTypeDecl fBase;
    private short fVariety;
    private short fValidationDV;
    private short fFacetsDefined;
    private short fFixedFacet;
    private short fWhiteSpace;
    private short fExplicitTimezone;
    private int fLength;
    private int fMinLength;
    private int fMaxLength;
    private int fTotalDigits;
    private int fFractionDigits;
    private int fMaxScale;
    private int fMinScale;
    private Vector fPattern;
    private Vector fPatternStr;
    private ValidatedInfo[] fEnumeration;
    private int fEnumerationSize;
    private ShortList fEnumerationTypeList;
    private ObjectList fEnumerationItemTypeList;
    private StringList fLexicalPattern;
    private StringList fLexicalEnumeration;
    private ObjectList fActualEnumeration;
    private Object fMaxInclusive;
    private Object fMaxExclusive;
    private Object fMinExclusive;
    private Object fMinInclusive;
    private Vector fAssertion;
    public XSAnnotation lengthAnnotation;
    public XSAnnotation minLengthAnnotation;
    public XSAnnotation maxLengthAnnotation;
    public XSAnnotation whiteSpaceAnnotation;
    public XSAnnotation totalDigitsAnnotation;
    public XSAnnotation fractionDigitsAnnotation;
    public XSObjectListImpl patternAnnotations;
    public XSObjectList enumerationAnnotations;
    public XSAnnotation maxInclusiveAnnotation;
    public XSAnnotation maxExclusiveAnnotation;
    public XSAnnotation minInclusiveAnnotation;
    public XSAnnotation minExclusiveAnnotation;
    public XSAnnotation maxScaleAnnotation;
    public XSAnnotation minScaleAnnotation;
    public XSAnnotation explicitTimezoneAnnotation;
    private XSObjectListImpl fFacets;
    private XSObjectListImpl fMultiValueFacets;
    private XSObjectList fAnnotations;
    private short fPatternType;
    private short fOrdered;
    private boolean fFinite;
    private boolean fBounded;
    private boolean fNumeric;
    private XSNamespaceItem fNamespaceItem;
    XSObject fContext;
    static final XSSimpleTypeDecl fAnySimpleType;
    static final XSSimpleTypeDecl fAnyAtomicType;
    static final XSSimpleTypeDecl fError;
    static final ValidationContext fDummyContext;
    private boolean fAnonymous;
    
    protected static TypeValidator[] getGDVs() {
        return XSSimpleTypeDecl.gDVs.clone();
    }
    
    protected void setDVs(final TypeValidator[] fdVs) {
        this.fDVs = fdVs;
    }
    
    public XSSimpleTypeDecl() {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fExplicitTimezone = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fContext = null;
        this.fAnonymous = false;
    }
    
    protected XSSimpleTypeDecl(final XSSimpleTypeDecl fBase, final String fTypeName, final short fValidationDV, final short fOrdered, final boolean fBounded, final boolean fFinite, final boolean fNumeric, final boolean fIsImmutable, final short fBuiltInKind) {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fExplicitTimezone = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fContext = null;
        this.fAnonymous = false;
        this.fIsImmutable = fIsImmutable;
        this.fBase = fBase;
        this.fTypeName = fTypeName;
        this.fTargetNamespace = "http://www.w3.org/2001/XMLSchema";
        this.fVariety = 1;
        this.fValidationDV = fValidationDV;
        this.fFacetsDefined = 16;
        if (fValidationDV == 0 || fValidationDV == 29 || fValidationDV == 1) {
            this.fWhiteSpace = 0;
        }
        else {
            this.fWhiteSpace = 2;
            this.fFixedFacet = 16;
        }
        this.fOrdered = fOrdered;
        this.fBounded = fBounded;
        this.fFinite = fFinite;
        this.fNumeric = fNumeric;
        this.fAnnotations = null;
        this.fBuiltInKind = fBuiltInKind;
    }
    
    protected XSSimpleTypeDecl(final XSSimpleTypeDecl xsSimpleTypeDecl, final String s, final String s2, final short n, final boolean b, final XSObjectList list, final short fBuiltInKind) {
        this(xsSimpleTypeDecl, s, s2, n, b, list);
        this.fBuiltInKind = fBuiltInKind;
    }
    
    protected XSSimpleTypeDecl(final XSSimpleTypeDecl fBase, final String fTypeName, final String fTargetNamespace, final short fFinalSet, final boolean fIsImmutable, final XSObjectList fAnnotations) {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fExplicitTimezone = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fContext = null;
        this.fAnonymous = false;
        this.fBase = fBase;
        this.fTypeName = fTypeName;
        this.fTargetNamespace = fTargetNamespace;
        this.fFinalSet = fFinalSet;
        this.fAnnotations = fAnnotations;
        this.fVariety = this.fBase.fVariety;
        this.fValidationDV = this.fBase.fValidationDV;
        switch (this.fVariety) {
            case 2: {
                this.fItemType = this.fBase.fItemType;
                break;
            }
            case 3: {
                this.fMemberTypes = this.fBase.fMemberTypes;
                break;
            }
        }
        this.fLength = this.fBase.fLength;
        this.fMinLength = this.fBase.fMinLength;
        this.fMaxLength = this.fBase.fMaxLength;
        this.fPattern = this.fBase.fPattern;
        this.fPatternStr = this.fBase.fPatternStr;
        this.fEnumeration = this.fBase.fEnumeration;
        this.fEnumerationSize = this.fBase.fEnumerationSize;
        this.fAssertion = this.fBase.fAssertion;
        this.fWhiteSpace = this.fBase.fWhiteSpace;
        this.fMaxExclusive = this.fBase.fMaxExclusive;
        this.fMaxInclusive = this.fBase.fMaxInclusive;
        this.fMinExclusive = this.fBase.fMinExclusive;
        this.fMinInclusive = this.fBase.fMinInclusive;
        this.fTotalDigits = this.fBase.fTotalDigits;
        this.fFractionDigits = this.fBase.fFractionDigits;
        this.fPatternType = this.fBase.fPatternType;
        this.fFixedFacet = this.fBase.fFixedFacet;
        this.fFacetsDefined = this.fBase.fFacetsDefined;
        this.fMaxScale = this.fBase.fMaxScale;
        this.fMinScale = this.fBase.fMinScale;
        this.fExplicitTimezone = this.fBase.fExplicitTimezone;
        this.lengthAnnotation = this.fBase.lengthAnnotation;
        this.minLengthAnnotation = this.fBase.minLengthAnnotation;
        this.maxLengthAnnotation = this.fBase.maxLengthAnnotation;
        this.patternAnnotations = this.fBase.patternAnnotations;
        this.enumerationAnnotations = this.fBase.enumerationAnnotations;
        this.whiteSpaceAnnotation = this.fBase.whiteSpaceAnnotation;
        this.maxExclusiveAnnotation = this.fBase.maxExclusiveAnnotation;
        this.maxInclusiveAnnotation = this.fBase.maxInclusiveAnnotation;
        this.minExclusiveAnnotation = this.fBase.minExclusiveAnnotation;
        this.minInclusiveAnnotation = this.fBase.minInclusiveAnnotation;
        this.totalDigitsAnnotation = this.fBase.totalDigitsAnnotation;
        this.fractionDigitsAnnotation = this.fBase.fractionDigitsAnnotation;
        this.maxScaleAnnotation = this.fBase.maxScaleAnnotation;
        this.minScaleAnnotation = this.fBase.minScaleAnnotation;
        this.explicitTimezoneAnnotation = this.fBase.explicitTimezoneAnnotation;
        this.calcFundamentalFacets();
        this.fIsImmutable = fIsImmutable;
        this.fBuiltInKind = fBase.fBuiltInKind;
    }
    
    protected XSSimpleTypeDecl(final String fTypeName, final String fTargetNamespace, final short fFinalSet, final XSSimpleTypeDecl fItemType, final boolean fIsImmutable, final XSObjectList fAnnotations) {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fExplicitTimezone = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fContext = null;
        this.fAnonymous = false;
        this.fBase = XSSimpleTypeDecl.fAnySimpleType;
        this.fTypeName = fTypeName;
        this.fTargetNamespace = fTargetNamespace;
        this.fFinalSet = fFinalSet;
        this.fAnnotations = fAnnotations;
        this.fVariety = 2;
        this.fItemType = fItemType;
        this.fValidationDV = 25;
        this.fFacetsDefined = 16;
        this.fFixedFacet = 16;
        this.fWhiteSpace = 2;
        this.calcFundamentalFacets();
        this.fIsImmutable = fIsImmutable;
        this.fBuiltInKind = 44;
    }
    
    protected XSSimpleTypeDecl(final String fTypeName, final String fTargetNamespace, final short fFinalSet, final XSSimpleTypeDecl[] fMemberTypes, final XSObjectList fAnnotations) {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fExplicitTimezone = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fContext = null;
        this.fAnonymous = false;
        this.fBase = XSSimpleTypeDecl.fAnySimpleType;
        this.fTypeName = fTypeName;
        this.fTargetNamespace = fTargetNamespace;
        this.fFinalSet = fFinalSet;
        this.fAnnotations = fAnnotations;
        this.fVariety = 3;
        this.fMemberTypes = fMemberTypes;
        this.fValidationDV = 26;
        this.fFacetsDefined = 16;
        this.fWhiteSpace = 2;
        this.calcFundamentalFacets();
        this.fIsImmutable = false;
        this.fBuiltInKind = 45;
    }
    
    protected XSSimpleTypeDecl setRestrictionValues(final XSSimpleTypeDecl fBase, final String fTypeName, final String fTargetNamespace, final short fFinalSet, final XSObjectList fAnnotations) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = fBase;
        this.fAnonymous = false;
        this.fTypeName = fTypeName;
        this.fTargetNamespace = fTargetNamespace;
        this.fFinalSet = fFinalSet;
        this.fAnnotations = fAnnotations;
        this.fVariety = this.fBase.fVariety;
        this.fValidationDV = this.fBase.fValidationDV;
        switch (this.fVariety) {
            case 2: {
                this.fItemType = this.fBase.fItemType;
                break;
            }
            case 3: {
                this.fMemberTypes = this.fBase.fMemberTypes;
                break;
            }
        }
        this.fLength = this.fBase.fLength;
        this.fMinLength = this.fBase.fMinLength;
        this.fMaxLength = this.fBase.fMaxLength;
        this.fPattern = this.fBase.fPattern;
        this.fPatternStr = this.fBase.fPatternStr;
        this.fEnumeration = this.fBase.fEnumeration;
        this.fEnumerationSize = this.fBase.fEnumerationSize;
        this.fWhiteSpace = this.fBase.fWhiteSpace;
        this.fMaxExclusive = this.fBase.fMaxExclusive;
        this.fMaxInclusive = this.fBase.fMaxInclusive;
        this.fMinExclusive = this.fBase.fMinExclusive;
        this.fMinInclusive = this.fBase.fMinInclusive;
        this.fTotalDigits = this.fBase.fTotalDigits;
        this.fFractionDigits = this.fBase.fFractionDigits;
        this.fPatternType = this.fBase.fPatternType;
        this.fFixedFacet = this.fBase.fFixedFacet;
        this.fFacetsDefined = this.fBase.fFacetsDefined;
        this.fMaxScale = this.fBase.fMaxScale;
        this.fMinScale = this.fBase.fMinScale;
        this.fExplicitTimezone = this.fBase.fExplicitTimezone;
        this.calcFundamentalFacets();
        this.fBuiltInKind = fBase.fBuiltInKind;
        return this;
    }
    
    protected XSSimpleTypeDecl setListValues(final String fTypeName, final String fTargetNamespace, final short fFinalSet, final XSSimpleTypeDecl fItemType, final XSObjectList fAnnotations) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = XSSimpleTypeDecl.fAnySimpleType;
        this.fAnonymous = false;
        this.fTypeName = fTypeName;
        this.fTargetNamespace = fTargetNamespace;
        this.fFinalSet = fFinalSet;
        this.fAnnotations = fAnnotations;
        this.fVariety = 2;
        this.fItemType = fItemType;
        this.fValidationDV = 25;
        this.fFacetsDefined = 16;
        this.fFixedFacet = 16;
        this.fWhiteSpace = 2;
        this.calcFundamentalFacets();
        this.fBuiltInKind = 44;
        return this;
    }
    
    protected XSSimpleTypeDecl setUnionValues(final String fTypeName, final String fTargetNamespace, final short fFinalSet, final XSSimpleTypeDecl[] fMemberTypes, final XSObjectList fAnnotations) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = XSSimpleTypeDecl.fAnySimpleType;
        this.fAnonymous = false;
        this.fTypeName = fTypeName;
        this.fTargetNamespace = fTargetNamespace;
        this.fFinalSet = fFinalSet;
        this.fAnnotations = fAnnotations;
        this.fVariety = 3;
        this.fMemberTypes = fMemberTypes;
        this.fValidationDV = 26;
        this.fFacetsDefined = 16;
        this.fWhiteSpace = 2;
        this.calcFundamentalFacets();
        this.fBuiltInKind = 45;
        return this;
    }
    
    public short getType() {
        return 3;
    }
    
    public short getTypeCategory() {
        return 16;
    }
    
    public String getName() {
        return this.getAnonymous() ? null : this.fTypeName;
    }
    
    public String getTypeName() {
        return this.fTypeName;
    }
    
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    public short getFinal() {
        return this.fFinalSet;
    }
    
    public TypeValidator getTypeValidator() {
        return this.fDVs[this.fValidationDV];
    }
    
    public boolean isFinal(final short n) {
        return (this.fFinalSet & n) != 0x0;
    }
    
    public XSTypeDefinition getBaseType() {
        return this.fBase;
    }
    
    public boolean getAnonymous() {
        return this.fAnonymous || this.fTypeName == null;
    }
    
    public short getVariety() {
        return (short)((this.fValidationDV == 0) ? 0 : this.fVariety);
    }
    
    public boolean isIDType() {
        switch (this.fVariety) {
            case 1: {
                return this.fValidationDV == 21;
            }
            case 2: {
                return this.fItemType.isIDType();
            }
            case 3: {
                for (int i = 0; i < this.fMemberTypes.length; ++i) {
                    if (this.fMemberTypes[i].isIDType()) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }
    
    public short getWhitespace() throws DatatypeException {
        if (this.fVariety == 3) {
            throw new DatatypeException("dt-whitespace", new Object[] { this.fTypeName });
        }
        return this.fWhiteSpace;
    }
    
    public short getPrimitiveKind() {
        if (this.fVariety != 1 || this.fValidationDV == 0 || this.fValidationDV == 29) {
            return 0;
        }
        if (this.fValidationDV == 21 || this.fValidationDV == 22 || this.fValidationDV == 23) {
            return 1;
        }
        if (this.fValidationDV == 24) {
            return 3;
        }
        return this.fValidationDV;
    }
    
    public short getBuiltInKind() {
        return this.fBuiltInKind;
    }
    
    public XSSimpleTypeDefinition getPrimitiveType() {
        if (this.fVariety == 1 && this.fValidationDV != 0 && this.fValidationDV != 29) {
            XSSimpleTypeDecl fBase;
            for (fBase = this; fBase.fBase != XSSimpleTypeDecl.fAnySimpleType && fBase.fBase != XSSimpleTypeDecl.fAnyAtomicType; fBase = fBase.fBase) {}
            return fBase;
        }
        return null;
    }
    
    public XSSimpleTypeDefinition getItemType() {
        if (this.fVariety == 2) {
            return this.fItemType;
        }
        return null;
    }
    
    public XSObjectList getMemberTypes() {
        if (this.fVariety == 3) {
            return new XSObjectListImpl(this.fMemberTypes, this.fMemberTypes.length);
        }
        return XSObjectListImpl.EMPTY_LIST;
    }
    
    public void applyFacets(final XSFacets xsFacets, final int n, final int n2, ValidationContext fEmptyContext) throws InvalidDatatypeFacetException {
        if (fEmptyContext == null) {
            fEmptyContext = XSSimpleTypeDecl.fEmptyContext;
        }
        this.applyFacets(xsFacets, n, n2, (short)0, fEmptyContext);
    }
    
    void applyFacets1(final XSFacets xsFacets, final int n, final int n2) {
        try {
            this.applyFacets(xsFacets, n, n2, (short)0, XSSimpleTypeDecl.fDummyContext);
        }
        catch (final InvalidDatatypeFacetException ex) {
            throw new RuntimeException("internal error");
        }
        this.fIsImmutable = true;
    }
    
    void applyFacets1(final XSFacets xsFacets, final int n, final int n2, final short n3) {
        try {
            this.applyFacets(xsFacets, n, n2, n3, XSSimpleTypeDecl.fDummyContext);
        }
        catch (final InvalidDatatypeFacetException ex) {
            throw new RuntimeException("internal error");
        }
        this.fIsImmutable = true;
    }
    
    void applyFacets(final XSFacets xsFacets, final int n, final int n2, final short fPatternType, final ValidationContext validationContext) throws InvalidDatatypeFacetException {
        if (this.fIsImmutable) {
            return;
        }
        final ValidatedInfo validatedInfo = new ValidatedInfo();
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        TypeValidatorHelper typeValidatorHelper = validationContext.getTypeValidatorHelper();
        if (typeValidatorHelper == null) {
            typeValidatorHelper = XSSimpleTypeDecl.fDefaultTypeValidatorHelper;
        }
        final int allowedFacets = typeValidatorHelper.getAllowedFacets(this.fValidationDV);
        if ((n & 0x1) != 0x0) {
            if ((allowedFacets & 0x1) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "length", this.fTypeName });
            }
            else {
                this.fLength = xsFacets.length;
                this.lengthAnnotation = xsFacets.lengthAnnotation;
                this.fFacetsDefined |= 0x1;
                if ((n2 & 0x1) != 0x0) {
                    this.fFixedFacet |= 0x1;
                }
            }
        }
        if ((n & 0x2) != 0x0) {
            if ((allowedFacets & 0x2) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "minLength", this.fTypeName });
            }
            else {
                this.fMinLength = xsFacets.minLength;
                this.minLengthAnnotation = xsFacets.minLengthAnnotation;
                this.fFacetsDefined |= 0x2;
                if ((n2 & 0x2) != 0x0) {
                    this.fFixedFacet |= 0x2;
                }
            }
        }
        if ((n & 0x4) != 0x0) {
            if ((allowedFacets & 0x4) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "maxLength", this.fTypeName });
            }
            else {
                this.fMaxLength = xsFacets.maxLength;
                this.maxLengthAnnotation = xsFacets.maxLengthAnnotation;
                this.fFacetsDefined |= 0x4;
                if ((n2 & 0x4) != 0x0) {
                    this.fFixedFacet |= 0x4;
                }
            }
        }
        if ((n & 0x8) != 0x0) {
            if ((allowedFacets & 0x8) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "pattern", this.fTypeName });
            }
            else {
                this.patternAnnotations = xsFacets.patternAnnotations;
                Object o = null;
                final String s = validationContext.getTypeValidatorHelper().isXMLSchema11() ? "Xbh" : "X";
                try {
                    o = new RegularExpression(xsFacets.pattern, s, validationContext.getLocale(), validationContext.getDatatypeXMLVersion());
                }
                catch (final Exception ex) {
                    this.reportError("InvalidRegex", new Object[] { xsFacets.pattern, ex.getLocalizedMessage() });
                }
                if (o != null) {
                    (this.fPattern = new Vector()).addElement(o);
                    (this.fPatternStr = new Vector()).addElement(xsFacets.pattern);
                    this.fFacetsDefined |= 0x8;
                    if ((n2 & 0x8) != 0x0) {
                        this.fFixedFacet |= 0x8;
                    }
                }
            }
        }
        if ((n & 0x10) != 0x0) {
            if ((allowedFacets & 0x10) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "whiteSpace", this.fTypeName });
            }
            else {
                this.fWhiteSpace = xsFacets.whiteSpace;
                this.whiteSpaceAnnotation = xsFacets.whiteSpaceAnnotation;
                this.fFacetsDefined |= 0x10;
                if ((n2 & 0x10) != 0x0) {
                    this.fFixedFacet |= 0x10;
                }
            }
        }
        if ((n & 0x800) != 0x0) {
            if ((allowedFacets & 0x800) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "enumeration", this.fTypeName });
            }
            else {
                final Vector enumeration = xsFacets.enumeration;
                final int size = enumeration.size();
                this.fEnumeration = new ValidatedInfo[size];
                final Vector enumNSDecls = xsFacets.enumNSDecls;
                final ValidationContextImpl validationContextImpl = new ValidationContextImpl(validationContext);
                this.enumerationAnnotations = xsFacets.enumAnnotations;
                this.fEnumerationSize = 0;
                for (int i = 0; i < size; ++i) {
                    if (enumNSDecls != null) {
                        validationContextImpl.setNSContext((NamespaceContext)enumNSDecls.elementAt(i));
                    }
                    try {
                        this.fEnumeration[this.fEnumerationSize++] = this.getActualEnumValue((String)enumeration.elementAt(i), validationContextImpl, null);
                    }
                    catch (final InvalidDatatypeValueException ex2) {
                        this.reportError("enumeration-valid-restriction", new Object[] { enumeration.elementAt(i), this.getBaseType().getName() });
                    }
                }
                this.fFacetsDefined |= 0x800;
                if ((n2 & 0x800) != 0x0) {
                    this.fFixedFacet |= 0x800;
                }
            }
        }
        if ((n & 0x4000) != 0x0) {
            this.fAssertion = new Vector();
            final Vector assertFacets = xsFacets.assertFacets;
            for (int j = 0; j < assertFacets.size(); ++j) {
                this.fAssertion.addElement(assertFacets.elementAt(j));
            }
            this.fFacetsDefined |= 0x4000;
            if ((n2 & 0x4000) != 0x0) {
                this.fFixedFacet |= 0x4000;
            }
        }
        if ((n & 0xFFFF8000) != 0x0) {
            if ((allowedFacets & 0xFFFF8000) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "explicitTimezone", this.fTypeName });
            }
            else {
                this.fExplicitTimezone = xsFacets.explicitTimezone;
                this.explicitTimezoneAnnotation = xsFacets.explicitTimezoneAnnotation;
                this.fFacetsDefined |= 0xFFFF8000;
                if ((n2 & 0xFFFF8000) != 0x0) {
                    this.fFixedFacet |= 0xFFFF8000;
                }
            }
        }
        if ((n & 0x20) != 0x0) {
            if ((allowedFacets & 0x20) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "maxInclusive", this.fTypeName });
            }
            else {
                this.maxInclusiveAnnotation = xsFacets.maxInclusiveAnnotation;
                try {
                    this.fMaxInclusive = this.fBase.getActualValue(xsFacets.maxInclusive, validationContext, validatedInfo, true, false);
                    this.fFacetsDefined |= 0x20;
                    if ((n2 & 0x20) != 0x0) {
                        this.fFixedFacet |= 0x20;
                    }
                }
                catch (final InvalidDatatypeValueException ex3) {
                    this.reportError(ex3.getKey(), ex3.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, xsFacets.maxInclusive, "maxInclusive", this.fBase.getName() });
                }
                if ((this.fBase.fFacetsDefined & 0x20) != 0x0 && (this.fBase.fFixedFacet & 0x20) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMaxInclusive, this.fBase.fMaxInclusive) != 0) {
                    this.reportError("FixedFacetValue", new Object[] { "maxInclusive", this.fMaxInclusive, this.fBase.fMaxInclusive, this.fTypeName });
                }
                try {
                    this.fBase.validate(validationContext, validatedInfo);
                }
                catch (final InvalidDatatypeValueException ex4) {
                    this.reportError(ex4.getKey(), ex4.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, xsFacets.maxInclusive, "maxInclusive", this.fBase.getName() });
                }
            }
        }
        boolean b = true;
        if ((n & 0x40) != 0x0) {
            if ((allowedFacets & 0x40) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "maxExclusive", this.fTypeName });
            }
            else {
                this.maxExclusiveAnnotation = xsFacets.maxExclusiveAnnotation;
                try {
                    this.fMaxExclusive = this.fBase.getActualValue(xsFacets.maxExclusive, validationContext, validatedInfo, true, false);
                    this.fFacetsDefined |= 0x40;
                    if ((n2 & 0x40) != 0x0) {
                        this.fFixedFacet |= 0x40;
                    }
                }
                catch (final InvalidDatatypeValueException ex5) {
                    this.reportError(ex5.getKey(), ex5.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, xsFacets.maxExclusive, "maxExclusive", this.fBase.getName() });
                }
                if ((this.fBase.fFacetsDefined & 0x40) != 0x0) {
                    final int compare = this.fDVs[this.fValidationDV].compare(this.fMaxExclusive, this.fBase.fMaxExclusive);
                    if ((this.fBase.fFixedFacet & 0x40) != 0x0 && compare != 0) {
                        this.reportError("FixedFacetValue", new Object[] { "maxExclusive", xsFacets.maxExclusive, this.fBase.fMaxExclusive, this.fTypeName });
                    }
                    if (compare == 0) {
                        b = false;
                    }
                }
                if (b) {
                    try {
                        this.fBase.validate(validationContext, validatedInfo);
                    }
                    catch (final InvalidDatatypeValueException ex6) {
                        this.reportError(ex6.getKey(), ex6.getArgs());
                        this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, xsFacets.maxExclusive, "maxExclusive", this.fBase.getName() });
                    }
                }
                else if ((this.fBase.fFacetsDefined & 0x20) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMaxExclusive, this.fBase.fMaxInclusive) > 0) {
                    this.reportError("maxExclusive-valid-restriction.2", new Object[] { xsFacets.maxExclusive, this.fBase.fMaxInclusive });
                }
            }
        }
        boolean b2 = true;
        if ((n & 0x80) != 0x0) {
            if ((allowedFacets & 0x80) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "minExclusive", this.fTypeName });
            }
            else {
                this.minExclusiveAnnotation = xsFacets.minExclusiveAnnotation;
                try {
                    this.fMinExclusive = this.fBase.getActualValue(xsFacets.minExclusive, validationContext, validatedInfo, true, false);
                    this.fFacetsDefined |= 0x80;
                    if ((n2 & 0x80) != 0x0) {
                        this.fFixedFacet |= 0x80;
                    }
                }
                catch (final InvalidDatatypeValueException ex7) {
                    this.reportError(ex7.getKey(), ex7.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, xsFacets.minExclusive, "minExclusive", this.fBase.getName() });
                }
                if ((this.fBase.fFacetsDefined & 0x80) != 0x0) {
                    final int compare2 = this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fBase.fMinExclusive);
                    if ((this.fBase.fFixedFacet & 0x80) != 0x0 && compare2 != 0) {
                        this.reportError("FixedFacetValue", new Object[] { "minExclusive", xsFacets.minExclusive, this.fBase.fMinExclusive, this.fTypeName });
                    }
                    if (compare2 == 0) {
                        b2 = false;
                    }
                }
                if (b2) {
                    try {
                        this.fBase.validate(validationContext, validatedInfo);
                    }
                    catch (final InvalidDatatypeValueException ex8) {
                        this.reportError(ex8.getKey(), ex8.getArgs());
                        this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, xsFacets.minExclusive, "minExclusive", this.fBase.getName() });
                    }
                }
                else if ((this.fBase.fFacetsDefined & 0x100) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fBase.fMinInclusive) < 0) {
                    this.reportError("minExclusive-valid-restriction.3", new Object[] { xsFacets.minExclusive, this.fBase.fMinInclusive });
                }
            }
        }
        if ((n & 0x100) != 0x0) {
            if ((allowedFacets & 0x100) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "minInclusive", this.fTypeName });
            }
            else {
                this.minInclusiveAnnotation = xsFacets.minInclusiveAnnotation;
                try {
                    this.fMinInclusive = this.fBase.getActualValue(xsFacets.minInclusive, validationContext, validatedInfo, true, false);
                    this.fFacetsDefined |= 0x100;
                    if ((n2 & 0x100) != 0x0) {
                        this.fFixedFacet |= 0x100;
                    }
                }
                catch (final InvalidDatatypeValueException ex9) {
                    this.reportError(ex9.getKey(), ex9.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, xsFacets.minInclusive, "minInclusive", this.fBase.getName() });
                }
                if ((this.fBase.fFacetsDefined & 0x100) != 0x0 && (this.fBase.fFixedFacet & 0x100) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMinInclusive, this.fBase.fMinInclusive) != 0) {
                    this.reportError("FixedFacetValue", new Object[] { "minInclusive", xsFacets.minInclusive, this.fBase.fMinInclusive, this.fTypeName });
                }
                try {
                    this.fBase.validate(validationContext, validatedInfo);
                }
                catch (final InvalidDatatypeValueException ex10) {
                    this.reportError(ex10.getKey(), ex10.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, xsFacets.minInclusive, "minInclusive", this.fBase.getName() });
                }
            }
        }
        if ((n & 0x200) != 0x0) {
            if ((allowedFacets & 0x200) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "totalDigits", this.fTypeName });
            }
            else {
                this.totalDigitsAnnotation = xsFacets.totalDigitsAnnotation;
                this.fTotalDigits = xsFacets.totalDigits;
                this.fFacetsDefined |= 0x200;
                if ((n2 & 0x200) != 0x0) {
                    this.fFixedFacet |= 0x200;
                }
            }
        }
        if ((n & 0x400) != 0x0) {
            if ((allowedFacets & 0x400) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "fractionDigits", this.fTypeName });
            }
            else {
                this.fFractionDigits = xsFacets.fractionDigits;
                this.fractionDigitsAnnotation = xsFacets.fractionDigitsAnnotation;
                this.fFacetsDefined |= 0x400;
                if ((n2 & 0x400) != 0x0) {
                    this.fFixedFacet |= 0x400;
                }
            }
        }
        if ((n & 0x1000) != 0x0) {
            if ((allowedFacets & 0x1000) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "maxScale", this.fTypeName });
            }
            else {
                this.maxScaleAnnotation = xsFacets.maxScaleAnnotation;
                this.fMaxScale = xsFacets.maxScale;
                this.fFacetsDefined |= 0x1000;
                if ((n2 & 0x1000) != 0x0) {
                    this.fFixedFacet |= 0x1000;
                }
            }
        }
        if ((n & 0x2000) != 0x0) {
            if ((allowedFacets & 0x2000) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "minScale", this.fTypeName });
            }
            else {
                this.minScaleAnnotation = xsFacets.minScaleAnnotation;
                this.fMinScale = xsFacets.minScale;
                this.fFacetsDefined |= 0x2000;
                if ((n2 & 0x2000) != 0x0) {
                    this.fFixedFacet |= 0x2000;
                }
            }
        }
        if (fPatternType != 0) {
            this.fPatternType = fPatternType;
        }
        if (this.fFacetsDefined != 0) {
            if ((this.fFacetsDefined & 0x2) != 0x0 && (this.fFacetsDefined & 0x4) != 0x0 && this.fMinLength > this.fMaxLength) {
                this.reportError("minLength-less-than-equal-to-maxLength", new Object[] { Integer.toString(this.fMinLength), Integer.toString(this.fMaxLength), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x40) != 0x0 && (this.fFacetsDefined & 0x20) != 0x0) {
                this.reportError("maxInclusive-maxExclusive", new Object[] { this.fMaxInclusive, this.fMaxExclusive, this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x80) != 0x0 && (this.fFacetsDefined & 0x100) != 0x0) {
                this.reportError("minInclusive-minExclusive", new Object[] { this.fMinInclusive, this.fMinExclusive, this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x20) != 0x0 && (this.fFacetsDefined & 0x100) != 0x0) {
                final int compare3 = this.fDVs[this.fValidationDV].compare(this.fMinInclusive, this.fMaxInclusive);
                if (compare3 != -1 && compare3 != 0) {
                    this.reportError("minInclusive-less-than-equal-to-maxInclusive", new Object[] { this.fMinInclusive, this.fMaxInclusive, this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0x40) != 0x0 && (this.fFacetsDefined & 0x80) != 0x0) {
                final int compare4 = this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fMaxExclusive);
                if (compare4 != -1 && compare4 != 0) {
                    this.reportError("minExclusive-less-than-equal-to-maxExclusive", new Object[] { this.fMinExclusive, this.fMaxExclusive, this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0x20) != 0x0 && (this.fFacetsDefined & 0x80) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fMaxInclusive) != -1) {
                this.reportError("minExclusive-less-than-maxInclusive", new Object[] { this.fMinExclusive, this.fMaxInclusive, this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x40) != 0x0 && (this.fFacetsDefined & 0x100) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMinInclusive, this.fMaxExclusive) != -1) {
                this.reportError("minInclusive-less-than-maxExclusive", new Object[] { this.fMinInclusive, this.fMaxExclusive, this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x400) != 0x0 && (this.fFacetsDefined & 0x200) != 0x0 && this.fFractionDigits > this.fTotalDigits) {
                this.reportError("fractionDigits-totalDigits", new Object[] { Integer.toString(this.fFractionDigits), Integer.toString(this.fTotalDigits), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x1000) != 0x0 && (this.fFacetsDefined & 0x2000) != 0x0 && this.fMinScale > this.fMaxScale) {
                this.reportError("minScale-totalDigits", new Object[] { Integer.toString(this.fMinScale), Integer.toString(this.fMaxScale), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x1) != 0x0) {
                if ((this.fBase.fFacetsDefined & 0x2) != 0x0 && this.fLength < this.fBase.fMinLength) {
                    this.reportError("length-minLength-maxLength.1.1", new Object[] { this.fTypeName, Integer.toString(this.fLength), Integer.toString(this.fBase.fMinLength) });
                }
                if ((this.fBase.fFacetsDefined & 0x4) != 0x0 && this.fLength > this.fBase.fMaxLength) {
                    this.reportError("length-minLength-maxLength.2.1", new Object[] { this.fTypeName, Integer.toString(this.fLength), Integer.toString(this.fBase.fMaxLength) });
                }
                if ((this.fBase.fFacetsDefined & 0x1) != 0x0 && this.fLength != this.fBase.fLength) {
                    this.reportError("length-valid-restriction", new Object[] { Integer.toString(this.fLength), Integer.toString(this.fBase.fLength), this.fTypeName });
                }
            }
            if ((this.fBase.fFacetsDefined & 0x1) != 0x0 || (this.fFacetsDefined & 0x1) != 0x0) {
                if ((this.fFacetsDefined & 0x2) != 0x0) {
                    if (this.fBase.fLength < this.fMinLength) {
                        this.reportError("length-minLength-maxLength.1.1", new Object[] { this.fTypeName, Integer.toString(this.fBase.fLength), Integer.toString(this.fMinLength) });
                    }
                    if ((this.fBase.fFacetsDefined & 0x2) == 0x0) {
                        this.reportError("length-minLength-maxLength.1.2.a", new Object[] { this.fTypeName });
                    }
                    if (this.fMinLength != this.fBase.fMinLength) {
                        this.reportError("length-minLength-maxLength.1.2.b", new Object[] { this.fTypeName, Integer.toString(this.fMinLength), Integer.toString(this.fBase.fMinLength) });
                    }
                }
                if ((this.fFacetsDefined & 0x4) != 0x0) {
                    if (this.fBase.fLength > this.fMaxLength) {
                        this.reportError("length-minLength-maxLength.2.1", new Object[] { this.fTypeName, Integer.toString(this.fBase.fLength), Integer.toString(this.fMaxLength) });
                    }
                    if ((this.fBase.fFacetsDefined & 0x4) == 0x0) {
                        this.reportError("length-minLength-maxLength.2.2.a", new Object[] { this.fTypeName });
                    }
                    if (this.fMaxLength != this.fBase.fMaxLength) {
                        this.reportError("length-minLength-maxLength.2.2.b", new Object[] { this.fTypeName, Integer.toString(this.fMaxLength), Integer.toString(this.fBase.fBase.fMaxLength) });
                    }
                }
            }
            if ((this.fFacetsDefined & 0x2) != 0x0) {
                if ((this.fBase.fFacetsDefined & 0x4) != 0x0) {
                    if (this.fMinLength > this.fBase.fMaxLength) {
                        this.reportError("minLength-less-than-equal-to-maxLength", new Object[] { Integer.toString(this.fMinLength), Integer.toString(this.fBase.fMaxLength), this.fTypeName });
                    }
                }
                else if ((this.fBase.fFacetsDefined & 0x2) != 0x0) {
                    if ((this.fBase.fFixedFacet & 0x2) != 0x0 && this.fMinLength != this.fBase.fMinLength) {
                        this.reportError("FixedFacetValue", new Object[] { "minLength", Integer.toString(this.fMinLength), Integer.toString(this.fBase.fMinLength), this.fTypeName });
                    }
                    if (this.fMinLength < this.fBase.fMinLength) {
                        this.reportError("minLength-valid-restriction", new Object[] { Integer.toString(this.fMinLength), Integer.toString(this.fBase.fMinLength), this.fTypeName });
                    }
                }
            }
            if ((this.fFacetsDefined & 0x4) != 0x0 && (this.fBase.fFacetsDefined & 0x2) != 0x0 && this.fMaxLength < this.fBase.fMinLength) {
                this.reportError("minLength-less-than-equal-to-maxLength", new Object[] { Integer.toString(this.fBase.fMinLength), Integer.toString(this.fMaxLength) });
            }
            if ((this.fFacetsDefined & 0x4) != 0x0 && (this.fBase.fFacetsDefined & 0x4) != 0x0) {
                if ((this.fBase.fFixedFacet & 0x4) != 0x0 && this.fMaxLength != this.fBase.fMaxLength) {
                    this.reportError("FixedFacetValue", new Object[] { "maxLength", Integer.toString(this.fMaxLength), Integer.toString(this.fBase.fMaxLength), this.fTypeName });
                }
                if (this.fMaxLength > this.fBase.fMaxLength) {
                    this.reportError("maxLength-valid-restriction", new Object[] { Integer.toString(this.fMaxLength), Integer.toString(this.fBase.fMaxLength), this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0x200) != 0x0 && (this.fBase.fFacetsDefined & 0x200) != 0x0) {
                if ((this.fBase.fFixedFacet & 0x200) != 0x0 && this.fTotalDigits != this.fBase.fTotalDigits) {
                    this.reportError("FixedFacetValue", new Object[] { "totalDigits", Integer.toString(this.fTotalDigits), Integer.toString(this.fBase.fTotalDigits), this.fTypeName });
                }
                if (this.fTotalDigits > this.fBase.fTotalDigits) {
                    this.reportError("totalDigits-valid-restriction", new Object[] { Integer.toString(this.fTotalDigits), Integer.toString(this.fBase.fTotalDigits), this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0x1000) != 0x0 && (this.fBase.fFacetsDefined & 0x1000) != 0x0) {
                if ((this.fBase.fFixedFacet & 0x1000) != 0x0 && this.fMaxScale != this.fBase.fMaxScale) {
                    this.reportError("FixedFacetValue", new Object[] { "maxScale", Integer.toString(this.fMaxScale), Integer.toString(this.fBase.fMaxScale), this.fTypeName });
                }
                if (this.fMaxScale > this.fBase.fMaxScale) {
                    this.reportError("maxScale-valid-restriction", new Object[] { Integer.toString(this.fMaxScale), Integer.toString(this.fBase.fMaxScale), this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0x2000) != 0x0 && (this.fBase.fFacetsDefined & 0x2000) != 0x0) {
                if ((this.fBase.fFixedFacet & 0x2000) != 0x0 && this.fMinScale != this.fBase.fMinScale) {
                    this.reportError("FixedFacetValue", new Object[] { "minScale", Integer.toString(this.fMinScale), Integer.toString(this.fBase.fMinScale), this.fTypeName });
                }
                if (this.fMinScale < this.fBase.fMinScale) {
                    this.reportError("minScale-valid-restriction", new Object[] { Integer.toString(this.fMinScale), Integer.toString(this.fBase.fMinScale), this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0x1000) != 0x0 && (this.fBase.fFacetsDefined & 0x2000) != 0x0 && this.fMaxScale < this.fBase.fMinScale) {
                this.reportError("minScale-totalDigits", new Object[] { Integer.toString(this.fBase.fMinScale), Integer.toString(this.fMaxScale), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x2000) != 0x0 && (this.fBase.fFacetsDefined & 0x1000) != 0x0 && this.fMinScale > this.fBase.fMaxScale) {
                this.reportError("minScale-totalDigits", new Object[] { Integer.toString(this.fMinScale), Integer.toString(this.fBase.fMaxScale), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x400) != 0x0 && (this.fBase.fFacetsDefined & 0x200) != 0x0 && this.fFractionDigits > this.fBase.fTotalDigits) {
                this.reportError("fractionDigits-totalDigits", new Object[] { Integer.toString(this.fFractionDigits), Integer.toString(this.fTotalDigits), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x400) != 0x0) {
                if ((this.fBase.fFacetsDefined & 0x400) != 0x0) {
                    if (((this.fBase.fFixedFacet & 0x400) != 0x0 && this.fFractionDigits != this.fBase.fFractionDigits) || (this.fValidationDV == 24 && this.fFractionDigits != 0)) {
                        this.reportError("FixedFacetValue", new Object[] { "fractionDigits", Integer.toString(this.fFractionDigits), Integer.toString(this.fBase.fFractionDigits), this.fTypeName });
                    }
                    if (this.fFractionDigits > this.fBase.fFractionDigits) {
                        this.reportError("fractionDigits-valid-restriction", new Object[] { Integer.toString(this.fFractionDigits), Integer.toString(this.fBase.fFractionDigits), this.fTypeName });
                    }
                }
                else if (this.fValidationDV == 24 && this.fFractionDigits != 0) {
                    this.reportError("FixedFacetValue", new Object[] { "fractionDigits", Integer.toString(this.fFractionDigits), "0", this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0x10) != 0x0 && (this.fBase.fFacetsDefined & 0x10) != 0x0) {
                if ((this.fBase.fFixedFacet & 0x10) != 0x0 && this.fWhiteSpace != this.fBase.fWhiteSpace) {
                    this.reportError("FixedFacetValue", new Object[] { "whiteSpace", this.whiteSpaceValue(this.fWhiteSpace), this.whiteSpaceValue(this.fBase.fWhiteSpace), this.fTypeName });
                }
                if (this.fWhiteSpace == 0 && this.fBase.fWhiteSpace == 2) {
                    this.reportError("whiteSpace-valid-restriction.1", new Object[] { this.fTypeName, "preserve" });
                }
                if (this.fWhiteSpace == 1 && this.fBase.fWhiteSpace == 2) {
                    this.reportError("whiteSpace-valid-restriction.1", new Object[] { this.fTypeName, "replace" });
                }
                if (this.fWhiteSpace == 0 && this.fBase.fWhiteSpace == 1) {
                    this.reportError("whiteSpace-valid-restriction.2", new Object[] { this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0xFFFF8000) != 0x0) {
                if ((this.fBase.fFacetsDefined & 0xFFFF8000) != 0x0 && this.fExplicitTimezone != this.fBase.fExplicitTimezone) {
                    final String explicitTimezoneValue = this.explicitTimezoneValue(this.fExplicitTimezone);
                    final String explicitTimezoneValue2 = this.explicitTimezoneValue(this.fBase.fExplicitTimezone);
                    if ((this.fBase.fFixedFacet & 0xFFFF8000) != 0x0) {
                        this.reportError("FixedFacetValue", new Object[] { "explicitTimezone", explicitTimezoneValue, explicitTimezoneValue2, this.fTypeName });
                    }
                    if (this.fBase.fExplicitTimezone != 0) {
                        this.reportError("timezone-valid-restriction", new Object[] { this.fTypeName, explicitTimezoneValue, explicitTimezoneValue2 });
                    }
                }
                if (this.fValidationDV == 31 && this.fExplicitTimezone != 1) {
                    this.reportError("FixedFacetValue", new Object[] { "explicitTimezone", this.explicitTimezoneValue(this.fExplicitTimezone), this.explicitTimezoneValue((short)1), this.fTypeName });
                }
            }
        }
        if ((this.fFacetsDefined & 0x1) == 0x0 && (this.fBase.fFacetsDefined & 0x1) != 0x0) {
            this.fFacetsDefined |= 0x1;
            this.fLength = this.fBase.fLength;
            this.lengthAnnotation = this.fBase.lengthAnnotation;
        }
        if ((this.fFacetsDefined & 0x2) == 0x0 && (this.fBase.fFacetsDefined & 0x2) != 0x0) {
            this.fFacetsDefined |= 0x2;
            this.fMinLength = this.fBase.fMinLength;
            this.minLengthAnnotation = this.fBase.minLengthAnnotation;
        }
        if ((this.fFacetsDefined & 0x4) == 0x0 && (this.fBase.fFacetsDefined & 0x4) != 0x0) {
            this.fFacetsDefined |= 0x4;
            this.fMaxLength = this.fBase.fMaxLength;
            this.maxLengthAnnotation = this.fBase.maxLengthAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x8) != 0x0) {
            if ((this.fFacetsDefined & 0x8) == 0x0) {
                this.fFacetsDefined |= 0x8;
                this.fPattern = this.fBase.fPattern;
                this.fPatternStr = this.fBase.fPatternStr;
                this.patternAnnotations = this.fBase.patternAnnotations;
            }
            else {
                for (int k = this.fBase.fPattern.size() - 1; k >= 0; --k) {
                    this.fPattern.addElement(this.fBase.fPattern.elementAt(k));
                    this.fPatternStr.addElement(this.fBase.fPatternStr.elementAt(k));
                }
                if (this.fBase.patternAnnotations != null) {
                    if (this.patternAnnotations != null) {
                        for (int l = this.fBase.patternAnnotations.getLength() - 1; l >= 0; --l) {
                            this.patternAnnotations.addXSObject(this.fBase.patternAnnotations.item(l));
                        }
                    }
                    else {
                        this.patternAnnotations = this.fBase.patternAnnotations;
                    }
                }
            }
        }
        if ((this.fFacetsDefined & 0x10) == 0x0 && (this.fBase.fFacetsDefined & 0x10) != 0x0) {
            this.fFacetsDefined |= 0x10;
            this.fWhiteSpace = this.fBase.fWhiteSpace;
            this.whiteSpaceAnnotation = this.fBase.whiteSpaceAnnotation;
        }
        if ((this.fFacetsDefined & 0xFFFF8000) == 0x0 && (this.fBase.fFacetsDefined & 0xFFFF8000) != 0x0) {
            this.fFacetsDefined |= 0xFFFF8000;
            this.fExplicitTimezone = this.fBase.fExplicitTimezone;
            this.explicitTimezoneAnnotation = this.fBase.explicitTimezoneAnnotation;
        }
        if ((this.fFacetsDefined & 0x800) == 0x0 && (this.fBase.fFacetsDefined & 0x800) != 0x0) {
            this.fFacetsDefined |= 0x800;
            this.fEnumeration = this.fBase.fEnumeration;
            this.fEnumerationSize = this.fBase.fEnumerationSize;
            this.enumerationAnnotations = this.fBase.enumerationAnnotations;
        }
        if ((this.fBase.fFacetsDefined & 0x40) != 0x0 && (this.fFacetsDefined & 0x40) == 0x0 && (this.fFacetsDefined & 0x20) == 0x0) {
            this.fFacetsDefined |= 0x40;
            this.fMaxExclusive = this.fBase.fMaxExclusive;
            this.maxExclusiveAnnotation = this.fBase.maxExclusiveAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x20) != 0x0 && (this.fFacetsDefined & 0x40) == 0x0 && (this.fFacetsDefined & 0x20) == 0x0) {
            this.fFacetsDefined |= 0x20;
            this.fMaxInclusive = this.fBase.fMaxInclusive;
            this.maxInclusiveAnnotation = this.fBase.maxInclusiveAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x80) != 0x0 && (this.fFacetsDefined & 0x80) == 0x0 && (this.fFacetsDefined & 0x100) == 0x0) {
            this.fFacetsDefined |= 0x80;
            this.fMinExclusive = this.fBase.fMinExclusive;
            this.minExclusiveAnnotation = this.fBase.minExclusiveAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x100) != 0x0 && (this.fFacetsDefined & 0x80) == 0x0 && (this.fFacetsDefined & 0x100) == 0x0) {
            this.fFacetsDefined |= 0x100;
            this.fMinInclusive = this.fBase.fMinInclusive;
            this.minInclusiveAnnotation = this.fBase.minInclusiveAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x1000) != 0x0 && (this.fFacetsDefined & 0x1000) == 0x0) {
            this.fFacetsDefined |= 0x1000;
            this.fMaxScale = this.fBase.fMaxScale;
            this.maxScaleAnnotation = this.fBase.maxScaleAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x2000) != 0x0 && (this.fFacetsDefined & 0x2000) == 0x0) {
            this.fFacetsDefined |= 0x2000;
            this.fMinScale = this.fBase.fMinScale;
            this.minScaleAnnotation = this.fBase.minScaleAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x200) != 0x0 && (this.fFacetsDefined & 0x200) == 0x0) {
            this.fFacetsDefined |= 0x200;
            this.fTotalDigits = this.fBase.fTotalDigits;
            this.totalDigitsAnnotation = this.fBase.totalDigitsAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x400) != 0x0 && (this.fFacetsDefined & 0x400) == 0x0) {
            this.fFacetsDefined |= 0x400;
            this.fFractionDigits = this.fBase.fFractionDigits;
            this.fractionDigitsAnnotation = this.fBase.fractionDigitsAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x4000) != 0x0 && (this.fFacetsDefined & 0x4000) == 0x0) {
            this.fFacetsDefined |= 0x4000;
            this.fAssertion = this.fBase.fAssertion;
        }
        if (this.fPatternType == 0 && this.fBase.fPatternType != 0) {
            this.fPatternType = this.fBase.fPatternType;
        }
        this.fFixedFacet |= this.fBase.fFixedFacet;
        this.calcFundamentalFacets();
    }
    
    public Object validate(final String s, ValidationContext fEmptyContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (fEmptyContext == null) {
            fEmptyContext = XSSimpleTypeDecl.fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        }
        else {
            validatedInfo.memberType = null;
        }
        final Object actualValue = this.getActualValue(s, fEmptyContext, validatedInfo, fEmptyContext == null || fEmptyContext.needToNormalize(), false);
        this.validate(fEmptyContext, validatedInfo);
        return actualValue;
    }
    
    protected ValidatedInfo getActualEnumValue(final String s, final ValidationContext validationContext, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.fBase.validateWithInfo(s, validationContext, validatedInfo, true);
    }
    
    public ValidatedInfo validateWithInfo(final String s, final ValidationContext validationContext, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.validateWithInfo(s, validationContext, validatedInfo, false);
    }
    
    private ValidatedInfo validateWithInfo(final String s, ValidationContext fEmptyContext, ValidatedInfo validatedInfo, final boolean b) throws InvalidDatatypeValueException {
        if (fEmptyContext == null) {
            fEmptyContext = XSSimpleTypeDecl.fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        }
        else {
            validatedInfo.memberType = null;
        }
        this.getActualValue(s, fEmptyContext, validatedInfo, fEmptyContext == null || fEmptyContext.needToNormalize(), b);
        this.validate(fEmptyContext, validatedInfo);
        return validatedInfo;
    }
    
    public Object validate(final Object o, ValidationContext fEmptyContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (fEmptyContext == null) {
            fEmptyContext = XSSimpleTypeDecl.fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        }
        else {
            validatedInfo.memberType = null;
        }
        final Object actualValue = this.getActualValue(o, fEmptyContext, validatedInfo, fEmptyContext == null || fEmptyContext.needToNormalize(), false);
        this.validate(fEmptyContext, validatedInfo);
        return actualValue;
    }
    
    public void validate(ValidationContext fEmptyContext, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (fEmptyContext == null) {
            fEmptyContext = XSSimpleTypeDecl.fEmptyContext;
        }
        if (fEmptyContext.needFacetChecking() && this.fFacetsDefined != 0 && this.fFacetsDefined != 16) {
            this.checkFacets(validatedInfo, fEmptyContext);
        }
        if (fEmptyContext.needExtraChecking()) {
            this.checkExtraRules(fEmptyContext, validatedInfo);
        }
    }
    
    private void checkFacets(final ValidatedInfo validatedInfo, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        final Object actualValue = validatedInfo.actualValue;
        final String normalizedValue = validatedInfo.normalizedValue;
        final short n = (short)(validationContext.getTypeValidatorHelper().isXMLSchema11() ? 4 : 1);
        if (this.fValidationDV != 18 && this.fValidationDV != 20) {
            final int dataLength = this.fDVs[this.fValidationDV].getDataLength(actualValue);
            if ((this.fFacetsDefined & 0x4) != 0x0 && dataLength > this.fMaxLength) {
                throw new InvalidDatatypeValueException("cvc-maxLength-valid", new Object[] { normalizedValue, Integer.toString(dataLength), Integer.toString(this.fMaxLength), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x2) != 0x0 && dataLength < this.fMinLength) {
                throw new InvalidDatatypeValueException("cvc-minLength-valid", new Object[] { normalizedValue, Integer.toString(dataLength), Integer.toString(this.fMinLength), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x1) != 0x0 && dataLength != this.fLength) {
                throw new InvalidDatatypeValueException("cvc-length-valid", new Object[] { normalizedValue, Integer.toString(dataLength), Integer.toString(this.fLength), this.fTypeName });
            }
        }
        if ((this.fFacetsDefined & 0x800) != 0x0) {
            boolean b = false;
            for (int fEnumerationSize = this.fEnumerationSize, i = 0; i < fEnumerationSize; ++i) {
                if (EqualityHelper.isEqual(validatedInfo, this.fEnumeration[i], n)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                final StringBuffer sb = new StringBuffer();
                this.appendEnumString(sb);
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { normalizedValue, sb.toString() });
            }
        }
        if ((this.fFacetsDefined & 0x400) != 0x0) {
            final int fractionDigits = this.fDVs[this.fValidationDV].getFractionDigits(actualValue);
            if (fractionDigits > this.fFractionDigits) {
                throw new InvalidDatatypeValueException("cvc-fractionDigits-valid", new Object[] { normalizedValue, Integer.toString(fractionDigits), Integer.toString(this.fFractionDigits) });
            }
        }
        if ((this.fFacetsDefined & 0x200) != 0x0) {
            final int totalDigits = this.fDVs[this.fValidationDV].getTotalDigits(actualValue);
            if (totalDigits > this.fTotalDigits) {
                throw new InvalidDatatypeValueException("cvc-totalDigits-valid", new Object[] { normalizedValue, Integer.toString(totalDigits), Integer.toString(this.fTotalDigits) });
            }
        }
        if ((this.fFacetsDefined & 0x1000) != 0x0 && this.fDVs[this.fValidationDV].hasPrecision(actualValue)) {
            final int precision = this.fDVs[this.fValidationDV].getPrecision(actualValue);
            if (precision > this.fMaxScale) {
                throw new InvalidDatatypeValueException("cvc-maxScale-valid", new Object[] { normalizedValue, Integer.toString(this.fMaxScale), this.fTypeName, Integer.toString(precision) });
            }
        }
        if ((this.fFacetsDefined & 0x2000) != 0x0 && this.fDVs[this.fValidationDV].hasPrecision(actualValue)) {
            final int precision2 = this.fDVs[this.fValidationDV].getPrecision(actualValue);
            if (precision2 < this.fMinScale) {
                throw new InvalidDatatypeValueException("cvc-minScale-valid", new Object[] { normalizedValue, Integer.toString(this.fMinScale), this.fTypeName, Integer.toString(precision2) });
            }
        }
        if ((this.fFacetsDefined & 0xFFFF8000) != 0x0) {
            if (this.fDVs[this.fValidationDV].hasTimeZone(actualValue)) {
                if (this.fExplicitTimezone == 2) {
                    throw new InvalidDatatypeValueException("cvc-explicitTimezone-valid", new Object[] { normalizedValue, "prohibited", this.fTypeName });
                }
            }
            else if (this.fExplicitTimezone == 1) {
                throw new InvalidDatatypeValueException("cvc-explicitTimezone-valid", new Object[] { normalizedValue, "required", this.fTypeName });
            }
        }
        if ((this.fFacetsDefined & 0x20) != 0x0) {
            final int compare = this.fDVs[this.fValidationDV].compare(actualValue, this.fMaxInclusive);
            if (compare != -1 && compare != 0) {
                throw new InvalidDatatypeValueException("cvc-maxInclusive-valid", new Object[] { normalizedValue, this.fMaxInclusive, this.fTypeName });
            }
        }
        if ((this.fFacetsDefined & 0x40) != 0x0 && this.fDVs[this.fValidationDV].compare(actualValue, this.fMaxExclusive) != -1) {
            throw new InvalidDatatypeValueException("cvc-maxExclusive-valid", new Object[] { normalizedValue, this.fMaxExclusive, this.fTypeName });
        }
        if ((this.fFacetsDefined & 0x100) != 0x0) {
            final int compare2 = this.fDVs[this.fValidationDV].compare(actualValue, this.fMinInclusive);
            if (compare2 != 1 && compare2 != 0) {
                throw new InvalidDatatypeValueException("cvc-minInclusive-valid", new Object[] { normalizedValue, this.fMinInclusive, this.fTypeName });
            }
        }
        if ((this.fFacetsDefined & 0x80) != 0x0 && this.fDVs[this.fValidationDV].compare(actualValue, this.fMinExclusive) != 1) {
            throw new InvalidDatatypeValueException("cvc-minExclusive-valid", new Object[] { normalizedValue, this.fMinExclusive, this.fTypeName });
        }
    }
    
    private void checkExtraRules(final ValidationContext validationContext, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        final Object actualValue = validatedInfo.actualValue;
        if (this.fVariety == 1) {
            this.fDVs[this.fValidationDV].checkExtraRules(actualValue, validationContext);
        }
        else {
            if (this.fVariety == 2) {
                final ListDV.ListData actualValue2 = (ListDV.ListData)actualValue;
                final XSSimpleType memberType = validatedInfo.memberType;
                final int length = actualValue2.getLength();
                try {
                    if (this.fItemType.fVariety == 3) {
                        final XSSimpleTypeDecl[] array = (XSSimpleTypeDecl[])validatedInfo.memberTypes;
                        for (int i = length - 1; i >= 0; --i) {
                            validatedInfo.actualValue = actualValue2.item(i);
                            validatedInfo.memberType = array[i];
                            this.fItemType.checkExtraRules(validationContext, validatedInfo);
                        }
                    }
                    else {
                        for (int j = length - 1; j >= 0; --j) {
                            validatedInfo.actualValue = actualValue2.item(j);
                            this.fItemType.checkExtraRules(validationContext, validatedInfo);
                        }
                    }
                    return;
                }
                finally {
                    validatedInfo.actualValue = actualValue2;
                    validatedInfo.memberType = memberType;
                }
            }
            ((XSSimpleTypeDecl)validatedInfo.memberType).checkExtraRules(validationContext, validatedInfo);
        }
    }
    
    private Object getActualValue(final Object o, final ValidationContext validationContext, final ValidatedInfo validatedInfo, final boolean b, final boolean b2) throws InvalidDatatypeValueException {
        String s;
        if (b) {
            s = this.normalize(o, this.fWhiteSpace);
        }
        else {
            s = o.toString();
        }
        if ((this.fFacetsDefined & 0x8) != 0x0) {
            for (int i = this.fPattern.size() - 1; i >= 0; --i) {
                if (!((RegularExpression)this.fPattern.elementAt(i)).matches(s)) {
                    throw new InvalidDatatypeValueException("cvc-pattern-valid", new Object[] { o, this.fPatternStr.elementAt(i), this.fTypeName });
                }
            }
        }
        if (this.fVariety == 1) {
            if (this.fPatternType != 0) {
                int n = 0;
                final boolean b3 = validationContext.getDatatypeXMLVersion() == 2;
                if (this.fPatternType == 1) {
                    n = ((b3 ? (XML11Char.isXML11ValidNmtoken(s) ? false : true) : (XMLChar.isValidNmtoken(s) ? false : true)) ? 1 : 0);
                }
                else if (this.fPatternType == 2) {
                    n = ((b3 ? (XML11Char.isXML11ValidName(s) ? false : true) : (XMLChar.isValidName(s) ? false : true)) ? 1 : 0);
                }
                else if (this.fPatternType == 3) {
                    n = ((b3 ? (XML11Char.isXML11ValidNCName(s) ? false : true) : (XMLChar.isValidNCName(s) ? false : true)) ? 1 : 0);
                }
                if (n != 0) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, XSSimpleTypeDecl.SPECIAL_PATTERN_STRING[this.fPatternType] });
                }
            }
            validatedInfo.normalizedValue = s;
            if (validationContext.getTypeValidatorHelper().isXMLSchema11() && this.fValidationDV == 20 && (this.getDefinedFacets() & 0x800) == 0x0 && !b2) {
                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.4", new Object[] { this.fTypeName, o });
            }
            final Object actualValue = this.fDVs[this.fValidationDV].getActualValue(s, validationContext);
            validatedInfo.actualValue = actualValue;
            validatedInfo.actualValueType = this.fBuiltInKind;
            validatedInfo.actualType = this;
            return actualValue;
        }
        else {
            if (this.fVariety == 2) {
                final StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
                final int countTokens = stringTokenizer.countTokens();
                final Object[] array = new Object[countTokens];
                final boolean b4 = this.fItemType.getVariety() == 3;
                final short[] array2 = new short[b4 ? countTokens : 1];
                if (!b4) {
                    array2[0] = this.fItemType.fBuiltInKind;
                }
                final XSSimpleTypeDecl[] memberTypes = new XSSimpleTypeDecl[countTokens];
                for (int j = 0; j < countTokens; ++j) {
                    array[j] = this.fItemType.getActualValue(stringTokenizer.nextToken(), validationContext, validatedInfo, false, b2);
                    if (validationContext.needFacetChecking() && this.fItemType.fFacetsDefined != 0 && this.fItemType.fFacetsDefined != 16) {
                        this.fItemType.checkFacets(validatedInfo, validationContext);
                    }
                    memberTypes[j] = (XSSimpleTypeDecl)validatedInfo.memberType;
                    if (b4) {
                        array2[j] = memberTypes[j].fBuiltInKind;
                    }
                }
                final ListDV.ListData actualValue2 = new ListDV.ListData(array);
                validatedInfo.actualValue = actualValue2;
                validatedInfo.actualValueType = (short)(b4 ? 43 : 44);
                validatedInfo.memberType = null;
                validatedInfo.memberTypes = memberTypes;
                validatedInfo.itemValueTypes = new ShortListImpl(array2, array2.length);
                validatedInfo.normalizedValue = s;
                validatedInfo.actualType = this;
                return actualValue2;
            }
            final Object o2 = (this.fMemberTypes.length > 1 && o != null) ? o.toString() : o;
            int k = 0;
            while (k < this.fMemberTypes.length) {
                try {
                    final Object actualValue3 = this.fMemberTypes[k].getActualValue(o2, validationContext, validatedInfo, true, b2);
                    if (validationContext.needFacetChecking() && this.fMemberTypes[k].fFacetsDefined != 0 && this.fMemberTypes[k].fFacetsDefined != 16) {
                        this.fMemberTypes[k].checkFacets(validatedInfo, validationContext);
                    }
                    if (this.fMemberTypes[k].fVariety != 3) {
                        validatedInfo.memberType = this.fMemberTypes[k];
                    }
                    validatedInfo.actualType = this;
                    return actualValue3;
                }
                catch (final InvalidDatatypeValueException ex) {
                    ++k;
                    continue;
                }
                break;
            }
            final StringBuffer sb = new StringBuffer();
            for (int l = 0; l < this.fMemberTypes.length; ++l) {
                if (l != 0) {
                    sb.append(" | ");
                }
                final XSSimpleTypeDecl xsSimpleTypeDecl = this.fMemberTypes[l];
                if (xsSimpleTypeDecl.fTargetNamespace != null) {
                    sb.append('{');
                    sb.append(xsSimpleTypeDecl.fTargetNamespace);
                    sb.append('}');
                }
                sb.append(xsSimpleTypeDecl.fTypeName);
                if (xsSimpleTypeDecl.fEnumeration != null) {
                    sb.append(" : ");
                    xsSimpleTypeDecl.appendEnumString(sb);
                }
            }
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { o, this.fTypeName, sb.toString() });
        }
    }
    
    public boolean isEqual(final Object o, final Object o2) {
        return o != null && o.equals(o2);
    }
    
    public boolean isIdentical(final Object o, final Object o2) {
        return o != null && this.fDVs[this.fValidationDV].isIdentical(o, o2);
    }
    
    public static String normalize(final String s, final short n) {
        final int n2 = (s == null) ? 0 : s.length();
        if (n2 == 0 || n == 0) {
            return s;
        }
        final StringBuffer sb = new StringBuffer();
        if (n == 1) {
            for (int i = 0; i < n2; ++i) {
                final char char1 = s.charAt(i);
                if (char1 != '\t' && char1 != '\n' && char1 != '\r') {
                    sb.append(char1);
                }
                else {
                    sb.append(' ');
                }
            }
        }
        else {
            boolean b = true;
            for (int j = 0; j < n2; ++j) {
                final char char2 = s.charAt(j);
                if (char2 != '\t' && char2 != '\n' && char2 != '\r' && char2 != ' ') {
                    sb.append(char2);
                    b = false;
                }
                else {
                    while (j < n2 - 1) {
                        final char char3 = s.charAt(j + 1);
                        if (char3 != '\t' && char3 != '\n' && char3 != '\r' && char3 != ' ') {
                            break;
                        }
                        ++j;
                    }
                    if (j < n2 - 1 && !b) {
                        sb.append(' ');
                    }
                }
            }
        }
        return sb.toString();
    }
    
    protected String normalize(final Object o, final short n) {
        if (o == null) {
            return null;
        }
        if ((this.fFacetsDefined & 0x8) == 0x0) {
            final short n2 = XSSimpleTypeDecl.fDVNormalizeType[this.fValidationDV];
            if (n2 == 0) {
                return o.toString();
            }
            if (n2 == 1) {
                return XMLChar.trim(o.toString());
            }
        }
        if (!(o instanceof StringBuffer)) {
            return normalize(o.toString(), n);
        }
        final StringBuffer sb = (StringBuffer)o;
        final int length = sb.length();
        if (length == 0) {
            return "";
        }
        if (n == 0) {
            return sb.toString();
        }
        if (n == 1) {
            for (int i = 0; i < length; ++i) {
                final char char1 = sb.charAt(i);
                if (char1 == '\t' || char1 == '\n' || char1 == '\r') {
                    sb.setCharAt(i, ' ');
                }
            }
        }
        else {
            int length2 = 0;
            boolean b = true;
            for (int j = 0; j < length; ++j) {
                final char char2 = sb.charAt(j);
                if (char2 != '\t' && char2 != '\n' && char2 != '\r' && char2 != ' ') {
                    sb.setCharAt(length2++, char2);
                    b = false;
                }
                else {
                    while (j < length - 1) {
                        final char char3 = sb.charAt(j + 1);
                        if (char3 != '\t' && char3 != '\n' && char3 != '\r' && char3 != ' ') {
                            break;
                        }
                        ++j;
                    }
                    if (j < length - 1 && !b) {
                        sb.setCharAt(length2++, ' ');
                    }
                }
            }
            sb.setLength(length2);
        }
        return sb.toString();
    }
    
    void reportError(final String s, final Object[] array) throws InvalidDatatypeFacetException {
        throw new InvalidDatatypeFacetException(s, array);
    }
    
    private String whiteSpaceValue(final short n) {
        return XSSimpleTypeDecl.WS_FACET_STRING[n];
    }
    
    private String explicitTimezoneValue(final short n) {
        return XSSimpleTypeDecl.ET_FACET_STRING[n];
    }
    
    public short getOrdered() {
        return this.fOrdered;
    }
    
    public boolean getBounded() {
        return this.fBounded;
    }
    
    public boolean getFinite() {
        return this.fFinite;
    }
    
    public boolean getNumeric() {
        return this.fNumeric;
    }
    
    public boolean isDefinedFacet(final short n) {
        if (this.fValidationDV == 0 || this.fValidationDV == 29) {
            return false;
        }
        if ((this.fFacetsDefined & n) != 0x0) {
            return true;
        }
        if (this.fPatternType != 0) {
            return n == 8;
        }
        return this.fValidationDV == 24 && (n == 8 || n == 1024);
    }
    
    public short getDefinedFacets() {
        if (this.fValidationDV == 0 || this.fValidationDV == 29) {
            return 0;
        }
        if (this.fPatternType != 0) {
            return (short)(this.fFacetsDefined | 0x8);
        }
        if (this.fValidationDV == 24) {
            return (short)(this.fFacetsDefined | 0x8 | 0x400);
        }
        return this.fFacetsDefined;
    }
    
    public boolean isFixedFacet(final short n) {
        return (this.fFixedFacet & n) != 0x0 || (this.fValidationDV == 24 && n == 1024);
    }
    
    public short getFixedFacets() {
        if (this.fValidationDV == 24) {
            return (short)(this.fFixedFacet | 0x400);
        }
        return this.fFixedFacet;
    }
    
    public String getLexicalFacetValue(final short n) {
        switch (n) {
            case 1: {
                return (this.fLength == -1) ? null : Integer.toString(this.fLength);
            }
            case 2: {
                return (this.fMinLength == -1) ? null : Integer.toString(this.fMinLength);
            }
            case 4: {
                return (this.fMaxLength == -1) ? null : Integer.toString(this.fMaxLength);
            }
            case 16: {
                if (this.fValidationDV == 0 || this.fValidationDV == 29) {
                    return null;
                }
                return XSSimpleTypeDecl.WS_FACET_STRING[this.fWhiteSpace];
            }
            case 32: {
                return (this.fMaxInclusive == null) ? null : this.fMaxInclusive.toString();
            }
            case 64: {
                return (this.fMaxExclusive == null) ? null : this.fMaxExclusive.toString();
            }
            case 128: {
                return (this.fMinExclusive == null) ? null : this.fMinExclusive.toString();
            }
            case 256: {
                return (this.fMinInclusive == null) ? null : this.fMinInclusive.toString();
            }
            case 512: {
                return (this.fTotalDigits == -1) ? null : Integer.toString(this.fTotalDigits);
            }
            case 4096: {
                return ((this.fFacetsDefined & 0x1000) == 0x0) ? null : Integer.toString(this.fMaxScale);
            }
            case 8192: {
                return ((this.fFacetsDefined & 0x2000) == 0x0) ? null : Integer.toString(this.fMinScale);
            }
            case Short.MIN_VALUE: {
                return XSSimpleTypeDecl.ET_FACET_STRING[this.fExplicitTimezone];
            }
            case 1024: {
                if (this.fValidationDV == 24) {
                    return "0";
                }
                return (this.fFractionDigits == -1) ? null : Integer.toString(this.fFractionDigits);
            }
            default: {
                return null;
            }
        }
    }
    
    public StringList getLexicalEnumeration() {
        if (this.fLexicalEnumeration == null) {
            if (this.fEnumeration == null) {
                return StringListImpl.EMPTY_LIST;
            }
            final int fEnumerationSize = this.fEnumerationSize;
            final String[] array = new String[fEnumerationSize];
            for (int i = 0; i < fEnumerationSize; ++i) {
                array[i] = this.fEnumeration[i].normalizedValue;
            }
            this.fLexicalEnumeration = new StringListImpl(array, fEnumerationSize);
        }
        return this.fLexicalEnumeration;
    }
    
    public ObjectList getActualEnumeration() {
        if (this.fActualEnumeration == null) {
            this.fActualEnumeration = new AbstractObjectList() {
                public int getLength() {
                    return (XSSimpleTypeDecl.this.fEnumeration != null) ? XSSimpleTypeDecl.this.fEnumerationSize : 0;
                }
                
                public boolean contains(final Object o) {
                    if (XSSimpleTypeDecl.this.fEnumeration == null) {
                        return false;
                    }
                    for (int i = 0; i < XSSimpleTypeDecl.this.fEnumerationSize; ++i) {
                        if (XSSimpleTypeDecl.this.fEnumeration[i].getActualValue().equals(o)) {
                            return true;
                        }
                    }
                    return false;
                }
                
                public Object item(final int n) {
                    if (n < 0 || n >= this.getLength()) {
                        return null;
                    }
                    return XSSimpleTypeDecl.this.fEnumeration[n].getActualValue();
                }
            };
        }
        return this.fActualEnumeration;
    }
    
    public ObjectList getEnumerationItemTypeList() {
        if (this.fEnumerationItemTypeList == null) {
            if (this.fEnumeration == null) {
                return null;
            }
            this.fEnumerationItemTypeList = new AbstractObjectList() {
                public int getLength() {
                    return (XSSimpleTypeDecl.this.fEnumeration != null) ? XSSimpleTypeDecl.this.fEnumerationSize : 0;
                }
                
                public boolean contains(final Object o) {
                    if (XSSimpleTypeDecl.this.fEnumeration == null || !(o instanceof ShortList)) {
                        return false;
                    }
                    for (int i = 0; i < XSSimpleTypeDecl.this.fEnumerationSize; ++i) {
                        if (XSSimpleTypeDecl.this.fEnumeration[i].itemValueTypes == o) {
                            return true;
                        }
                    }
                    return false;
                }
                
                public Object item(final int n) {
                    if (n < 0 || n >= this.getLength()) {
                        return null;
                    }
                    return XSSimpleTypeDecl.this.fEnumeration[n].itemValueTypes;
                }
            };
        }
        return this.fEnumerationItemTypeList;
    }
    
    public ShortList getEnumerationTypeList() {
        if (this.fEnumerationTypeList == null) {
            if (this.fEnumeration == null) {
                return ShortListImpl.EMPTY_LIST;
            }
            final short[] array = new short[this.fEnumerationSize];
            for (int i = 0; i < this.fEnumerationSize; ++i) {
                array[i] = this.fEnumeration[i].actualValueType;
            }
            this.fEnumerationTypeList = new ShortListImpl(array, this.fEnumerationSize);
        }
        return this.fEnumerationTypeList;
    }
    
    public StringList getLexicalPattern() {
        if (this.fPatternType == 0 && this.fValidationDV != 24 && this.fPatternStr == null) {
            return StringListImpl.EMPTY_LIST;
        }
        if (this.fLexicalPattern == null) {
            final int n = (this.fPatternStr == null) ? 0 : this.fPatternStr.size();
            String[] array;
            if (this.fPatternType == 1) {
                array = new String[n + 1];
                array[n] = "\\c+";
            }
            else if (this.fPatternType == 2) {
                array = new String[n + 1];
                array[n] = "\\i\\c*";
            }
            else if (this.fPatternType == 3) {
                array = new String[n + 2];
                array[n] = "\\i\\c*";
                array[n + 1] = "[\\i-[:]][\\c-[:]]*";
            }
            else if (this.fValidationDV == 24) {
                array = new String[n + 1];
                array[n] = "[\\-+]?[0-9]+";
            }
            else {
                array = new String[n];
            }
            for (int i = 0; i < n; ++i) {
                array[i] = (String)this.fPatternStr.elementAt(i);
            }
            this.fLexicalPattern = new StringListImpl(array, array.length);
        }
        return this.fLexicalPattern;
    }
    
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    private void calcFundamentalFacets() {
        this.setOrdered();
        this.setNumeric();
        this.setBounded();
        this.setCardinality();
    }
    
    private void setOrdered() {
        if (this.fVariety == 1) {
            this.fOrdered = this.fBase.fOrdered;
        }
        else if (this.fVariety == 2) {
            this.fOrdered = 0;
        }
        else if (this.fVariety == 3) {
            if (this.fMemberTypes.length == 0) {
                this.fOrdered = 1;
                return;
            }
            final short fOrdered = this.fMemberTypes[0].fOrdered;
            if (fOrdered == 1) {
                this.fOrdered = 1;
                return;
            }
            final short primitiveDV = this.getPrimitiveDV(this.getFirstExpandedSimpleTypeValidationDV(this.fMemberTypes[0]));
            boolean b = true;
            for (int i = 1; i < this.fMemberTypes.length; ++i) {
                if (this.fMemberTypes[i].fOrdered != fOrdered) {
                    this.fOrdered = 1;
                    return;
                }
                if (b) {
                    b = (this.getPrimitiveDV(this.getFirstExpandedSimpleTypeValidationDV(this.fMemberTypes[i])) == primitiveDV);
                }
            }
            if (fOrdered == 0) {
                this.fOrdered = 0;
                return;
            }
            this.fOrdered = (short)(b ? 2 : 1);
        }
    }
    
    private void setNumeric() {
        if (this.fVariety == 1) {
            this.fNumeric = this.fBase.fNumeric;
        }
        else if (this.fVariety == 2) {
            this.fNumeric = false;
        }
        else if (this.fVariety == 3) {
            final XSSimpleTypeDecl[] fMemberTypes = this.fMemberTypes;
            for (int i = 0; i < fMemberTypes.length; ++i) {
                if (!fMemberTypes[i].getNumeric()) {
                    this.fNumeric = false;
                    return;
                }
            }
            this.fNumeric = true;
        }
    }
    
    private void setBounded() {
        if (this.fVariety == 1) {
            if (((this.fFacetsDefined & 0x100) != 0x0 || (this.fFacetsDefined & 0x80) != 0x0) && ((this.fFacetsDefined & 0x20) != 0x0 || (this.fFacetsDefined & 0x40) != 0x0)) {
                this.fBounded = true;
            }
            else {
                this.fBounded = false;
            }
        }
        else if (this.fVariety == 2) {
            if ((this.fFacetsDefined & 0x1) != 0x0 || ((this.fFacetsDefined & 0x2) != 0x0 && (this.fFacetsDefined & 0x4) != 0x0)) {
                this.fBounded = true;
            }
            else {
                this.fBounded = false;
            }
        }
        else if (this.fVariety == 3) {
            final XSSimpleTypeDecl[] fMemberTypes = this.fMemberTypes;
            short primitiveDV = 0;
            if (fMemberTypes.length > 0) {
                primitiveDV = this.getPrimitiveDV(this.getFirstExpandedSimpleTypeValidationDV(fMemberTypes[0]));
            }
            for (int i = 0; i < fMemberTypes.length; ++i) {
                if (!fMemberTypes[i].getBounded() || primitiveDV != this.getPrimitiveDV(this.getFirstExpandedSimpleTypeValidationDV(fMemberTypes[i]))) {
                    this.fBounded = false;
                    return;
                }
            }
            this.fBounded = true;
        }
    }
    
    private short getFirstExpandedSimpleTypeValidationDV(final XSSimpleTypeDecl xsSimpleTypeDecl) {
        if (xsSimpleTypeDecl.fVariety == 3) {
            for (int i = 0; i < xsSimpleTypeDecl.fMemberTypes.length; ++i) {
                final short firstExpandedSimpleTypeValidationDV = this.getFirstExpandedSimpleTypeValidationDV(xsSimpleTypeDecl.fMemberTypes[i]);
                if (firstExpandedSimpleTypeValidationDV != -1) {
                    return firstExpandedSimpleTypeValidationDV;
                }
            }
            return -1;
        }
        return xsSimpleTypeDecl.fValidationDV;
    }
    
    private boolean specialCardinalityCheck() {
        return this.fBase.fValidationDV == 9 || this.fBase.fValidationDV == 10 || this.fBase.fValidationDV == 11 || this.fBase.fValidationDV == 12 || this.fBase.fValidationDV == 13 || this.fBase.fValidationDV == 14;
    }
    
    private void setCardinality() {
        if (this.fVariety == 1) {
            if (this.fBase.fFinite) {
                this.fFinite = true;
            }
            else if ((this.fFacetsDefined & 0x1) != 0x0 || (this.fFacetsDefined & 0x4) != 0x0 || (this.fFacetsDefined & 0x200) != 0x0) {
                this.fFinite = true;
            }
            else if (((this.fFacetsDefined & 0x100) != 0x0 || (this.fFacetsDefined & 0x80) != 0x0) && ((this.fFacetsDefined & 0x20) != 0x0 || (this.fFacetsDefined & 0x40) != 0x0)) {
                if ((this.fFacetsDefined & 0x400) != 0x0 || this.specialCardinalityCheck()) {
                    this.fFinite = true;
                }
                else {
                    this.fFinite = false;
                }
            }
            else {
                this.fFinite = false;
            }
        }
        else if (this.fVariety == 2) {
            if ((this.fFacetsDefined & 0x1) != 0x0 || ((this.fFacetsDefined & 0x2) != 0x0 && (this.fFacetsDefined & 0x4) != 0x0)) {
                this.fFinite = true;
            }
            else {
                this.fFinite = false;
            }
        }
        else if (this.fVariety == 3) {
            final XSSimpleTypeDecl[] fMemberTypes = this.fMemberTypes;
            for (int i = 0; i < fMemberTypes.length; ++i) {
                if (!fMemberTypes[i].getFinite()) {
                    this.fFinite = false;
                    return;
                }
            }
            this.fFinite = true;
        }
    }
    
    private short getPrimitiveDV(final short n) {
        if (n == 21 || n == 22 || n == 23) {
            return 1;
        }
        if (n == 24) {
            return 3;
        }
        return n;
    }
    
    public boolean derivedFromType(XSTypeDefinition type, final short n) {
        if (type == null) {
            return false;
        }
        while (type instanceof XSSimpleTypeDelegate) {
            type = ((XSSimpleTypeDelegate)type).type;
        }
        if (type.getBaseType() == type) {
            return true;
        }
        XSTypeDefinition baseType;
        for (baseType = this; baseType != type && baseType != XSSimpleTypeDecl.fAnySimpleType; baseType = baseType.getBaseType()) {}
        return baseType == type;
    }
    
    public boolean derivedFrom(final String s, final String s2, final short n) {
        if (s2 == null) {
            return false;
        }
        if ("http://www.w3.org/2001/XMLSchema".equals(s) && "anyType".equals(s2)) {
            return true;
        }
        XSTypeDefinition baseType;
        for (baseType = this; (!s2.equals(baseType.getName()) || ((s != null || baseType.getNamespace() != null) && (s == null || !s.equals(baseType.getNamespace())))) && baseType != XSSimpleTypeDecl.fAnySimpleType; baseType = baseType.getBaseType()) {}
        return baseType != XSSimpleTypeDecl.fAnySimpleType;
    }
    
    public boolean isDOMDerivedFrom(final String s, final String s2, final int n) {
        return s2 != null && ((SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(s) && "anyType".equals(s2) && ((n & 0x1) != 0x0 || n == 0)) || ((n & 0x1) != 0x0 && this.isDerivedByRestriction(s, s2, this)) || ((n & 0x8) != 0x0 && this.isDerivedByList(s, s2, this)) || ((n & 0x4) != 0x0 && this.isDerivedByUnion(s, s2, this)) || (((n & 0x2) == 0x0 || (n & 0x1) != 0x0 || (n & 0x8) != 0x0 || (n & 0x4) != 0x0) && ((n & 0x2) == 0x0 && (n & 0x1) == 0x0 && (n & 0x8) == 0x0 && (n & 0x4) == 0x0) && this.isDerivedByAny(s, s2, this)));
    }
    
    private boolean isDerivedByAny(final String s, final String s2, XSTypeDefinition xsTypeDefinition) {
        boolean b = false;
        XSTypeDefinition xsTypeDefinition2 = null;
        while (xsTypeDefinition != null && xsTypeDefinition != xsTypeDefinition2) {
            if (s2.equals(xsTypeDefinition.getName()) && ((s == null && xsTypeDefinition.getNamespace() == null) || (s != null && s.equals(xsTypeDefinition.getNamespace())))) {
                b = true;
                break;
            }
            if (this.isDerivedByRestriction(s, s2, xsTypeDefinition)) {
                return true;
            }
            if (this.isDerivedByList(s, s2, xsTypeDefinition)) {
                return true;
            }
            if (this.isDerivedByUnion(s, s2, xsTypeDefinition)) {
                return true;
            }
            xsTypeDefinition2 = xsTypeDefinition;
            if (((XSSimpleTypeDecl)xsTypeDefinition).getVariety() == 0 || ((XSSimpleTypeDecl)xsTypeDefinition).getVariety() == 1) {
                xsTypeDefinition = xsTypeDefinition.getBaseType();
            }
            else if (((XSSimpleTypeDecl)xsTypeDefinition).getVariety() == 3) {
                final int n = 0;
                if (n < ((XSSimpleTypeDecl)xsTypeDefinition).getMemberTypes().getLength()) {
                    return this.isDerivedByAny(s, s2, (XSTypeDefinition)((XSSimpleTypeDecl)xsTypeDefinition).getMemberTypes().item(n));
                }
                continue;
            }
            else {
                if (((XSSimpleTypeDecl)xsTypeDefinition).getVariety() != 2) {
                    continue;
                }
                xsTypeDefinition = ((XSSimpleTypeDecl)xsTypeDefinition).getItemType();
            }
        }
        return b;
    }
    
    private boolean isDerivedByRestriction(final String s, final String s2, XSTypeDefinition baseType) {
        for (XSTypeDefinition xsTypeDefinition = null; baseType != null && baseType != xsTypeDefinition; xsTypeDefinition = baseType, baseType = baseType.getBaseType()) {
            if (s2.equals(baseType.getName()) && ((s != null && s.equals(baseType.getNamespace())) || (baseType.getNamespace() == null && s == null))) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isDerivedByList(final String s, final String s2, final XSTypeDefinition xsTypeDefinition) {
        if (xsTypeDefinition != null && ((XSSimpleTypeDefinition)xsTypeDefinition).getVariety() == 2) {
            final XSSimpleTypeDefinition itemType = ((XSSimpleTypeDefinition)xsTypeDefinition).getItemType();
            if (itemType != null && this.isDerivedByRestriction(s, s2, itemType)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isDerivedByUnion(final String s, final String s2, final XSTypeDefinition xsTypeDefinition) {
        if (xsTypeDefinition != null && ((XSSimpleTypeDefinition)xsTypeDefinition).getVariety() == 3) {
            final XSObjectList memberTypes = ((XSSimpleTypeDefinition)xsTypeDefinition).getMemberTypes();
            for (int i = 0; i < memberTypes.getLength(); ++i) {
                if (memberTypes.item(i) != null && this.isDerivedByRestriction(s, s2, (XSTypeDefinition)memberTypes.item(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void reset() {
        if (this.fIsImmutable) {
            return;
        }
        this.fItemType = null;
        this.fMemberTypes = null;
        this.fTypeName = null;
        this.fTargetNamespace = null;
        this.fFinalSet = 0;
        this.fBase = null;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fExplicitTimezone = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fPattern = null;
        this.fPatternStr = null;
        this.fEnumeration = null;
        this.fLexicalPattern = null;
        this.fLexicalEnumeration = null;
        this.fActualEnumeration = null;
        this.fEnumerationTypeList = null;
        this.fEnumerationItemTypeList = null;
        this.fMaxInclusive = null;
        this.fMaxExclusive = null;
        this.fMinExclusive = null;
        this.fMinInclusive = null;
        this.fMaxScale = 0;
        this.fMinScale = 0;
        this.lengthAnnotation = null;
        this.minLengthAnnotation = null;
        this.maxLengthAnnotation = null;
        this.whiteSpaceAnnotation = null;
        this.totalDigitsAnnotation = null;
        this.fractionDigitsAnnotation = null;
        this.patternAnnotations = null;
        this.enumerationAnnotations = null;
        this.maxInclusiveAnnotation = null;
        this.maxExclusiveAnnotation = null;
        this.minInclusiveAnnotation = null;
        this.minExclusiveAnnotation = null;
        this.maxScaleAnnotation = null;
        this.minScaleAnnotation = null;
        this.explicitTimezoneAnnotation = null;
        this.fPatternType = 0;
        this.fAnnotations = null;
        this.fFacets = null;
        this.fContext = null;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }
    
    public void setNamespaceItem(final XSNamespaceItem fNamespaceItem) {
        this.fNamespaceItem = fNamespaceItem;
    }
    
    public void setContext(final XSObject fContext) {
        this.fContext = fContext;
    }
    
    public XSObject getContext() {
        return this.fContext;
    }
    
    public String toString() {
        return this.fTargetNamespace + "," + this.fTypeName;
    }
    
    public XSObjectList getFacets() {
        if (this.fFacets == null && (this.fFacetsDefined != 0 || this.fValidationDV == 24)) {
            final XSFacetImpl[] array = new XSFacetImpl[13];
            int n = 0;
            if ((this.fFacetsDefined & 0x10) != 0x0 && this.fValidationDV != 0 && this.fValidationDV != 29) {
                array[n] = new XSFacetImpl((short)16, XSSimpleTypeDecl.WS_FACET_STRING[this.fWhiteSpace], 0, null, (this.fFixedFacet & 0x10) != 0x0, this.whiteSpaceAnnotation);
                ++n;
            }
            if (this.fLength != -1) {
                array[n] = new XSFacetImpl((short)1, Integer.toString(this.fLength), this.fLength, null, (this.fFixedFacet & 0x1) != 0x0, this.lengthAnnotation);
                ++n;
            }
            if (this.fMinLength != -1) {
                array[n] = new XSFacetImpl((short)2, Integer.toString(this.fMinLength), this.fMinLength, null, (this.fFixedFacet & 0x2) != 0x0, this.minLengthAnnotation);
                ++n;
            }
            if (this.fMaxLength != -1) {
                array[n] = new XSFacetImpl((short)4, Integer.toString(this.fMaxLength), this.fMaxLength, null, (this.fFixedFacet & 0x4) != 0x0, this.maxLengthAnnotation);
                ++n;
            }
            if (this.fTotalDigits != -1) {
                array[n] = new XSFacetImpl((short)512, Integer.toString(this.fTotalDigits), this.fTotalDigits, null, (this.fFixedFacet & 0x200) != 0x0, this.totalDigitsAnnotation);
                ++n;
            }
            if (this.fValidationDV == 24) {
                array[n] = new XSFacetImpl((short)1024, "0", 0, null, true, this.fractionDigitsAnnotation);
                ++n;
            }
            else if (this.fFractionDigits != -1) {
                array[n] = new XSFacetImpl((short)1024, Integer.toString(this.fFractionDigits), this.fFractionDigits, null, (this.fFixedFacet & 0x400) != 0x0, this.fractionDigitsAnnotation);
                ++n;
            }
            if (this.fMaxInclusive != null) {
                array[n] = new XSFacetImpl((short)32, this.fMaxInclusive.toString(), 0, this.fMaxInclusive, (this.fFixedFacet & 0x20) != 0x0, this.maxInclusiveAnnotation);
                ++n;
            }
            if (this.fMaxExclusive != null) {
                array[n] = new XSFacetImpl((short)64, this.fMaxExclusive.toString(), 0, this.fMaxExclusive, (this.fFixedFacet & 0x40) != 0x0, this.maxExclusiveAnnotation);
                ++n;
            }
            if (this.fMinExclusive != null) {
                array[n] = new XSFacetImpl((short)128, this.fMinExclusive.toString(), 0, this.fMinExclusive, (this.fFixedFacet & 0x80) != 0x0, this.minExclusiveAnnotation);
                ++n;
            }
            if (this.fMinInclusive != null) {
                array[n] = new XSFacetImpl((short)256, this.fMinInclusive.toString(), 0, this.fMinInclusive, (this.fFixedFacet & 0x100) != 0x0, this.minInclusiveAnnotation);
                ++n;
            }
            if ((this.fFacetsDefined & 0xFFFF8000) != 0x0) {
                array[n] = new XSFacetImpl((short)(-32768), XSSimpleTypeDecl.ET_FACET_STRING[this.fExplicitTimezone], 0, null, (this.fFixedFacet & 0xFFFF8000) != 0x0, this.explicitTimezoneAnnotation);
                ++n;
            }
            if ((this.fFacetsDefined & 0x1000) != 0x0) {
                array[n] = new XSFacetImpl((short)4096, Integer.toString(this.fMaxScale), this.fMaxScale, null, (this.fFixedFacet & 0x1000) != 0x0, this.maxScaleAnnotation);
                ++n;
            }
            if ((this.fFacetsDefined & 0x2000) != 0x0) {
                array[n] = new XSFacetImpl((short)8192, Integer.toString(this.fMinScale), this.fMinScale, null, (this.fFixedFacet & 0x2000) != 0x0, this.minScaleAnnotation);
                ++n;
            }
            this.fFacets = ((n > 0) ? new XSObjectListImpl(array, n) : XSObjectListImpl.EMPTY_LIST);
        }
        return (this.fFacets != null) ? this.fFacets : XSObjectListImpl.EMPTY_LIST;
    }
    
    public XSObject getFacet(final int n) {
        if (n == 2048 || n == 8) {
            final XSObjectList multiValueFacets = this.getMultiValueFacets();
            for (int i = 0; i < multiValueFacets.getLength(); ++i) {
                final XSMultiValueFacet xsMultiValueFacet = (XSMultiValueFacet)multiValueFacets.item(i);
                if (xsMultiValueFacet.getFacetKind() == n) {
                    return xsMultiValueFacet;
                }
            }
        }
        else {
            final XSObjectList facets = this.getFacets();
            for (int j = 0; j < facets.getLength(); ++j) {
                final XSFacet xsFacet = (XSFacet)facets.item(j);
                if (xsFacet.getFacetKind() == n) {
                    return xsFacet;
                }
            }
        }
        return null;
    }
    
    public XSObjectList getMultiValueFacets() {
        if (this.fMultiValueFacets == null && ((this.fFacetsDefined & 0x800) != 0x0 || (this.fFacetsDefined & 0x4000) != 0x0 || (this.fFacetsDefined & 0x8) != 0x0 || this.fPatternType != 0 || this.fValidationDV == 24)) {
            final XSMVFacetImpl[] array = new XSMVFacetImpl[3];
            int n = 0;
            if ((this.fFacetsDefined & 0x8) != 0x0 || this.fPatternType != 0 || this.fValidationDV == 24) {
                array[n] = new XSMVFacetImpl((short)8, this.getLexicalPattern(), null, this.patternAnnotations);
                ++n;
            }
            if (this.fEnumeration != null) {
                array[n] = new XSMVFacetImpl((short)2048, this.getLexicalEnumeration(), new ObjectListImpl(this.fEnumeration, this.fEnumerationSize), this.enumerationAnnotations);
                ++n;
            }
            if (this.fAssertion != null) {
                array[n] = new XSMVFacetImpl((short)16384, this.fAssertion);
                ++n;
            }
            this.fMultiValueFacets = new XSObjectListImpl(array, n);
        }
        return (this.fMultiValueFacets != null) ? this.fMultiValueFacets : XSObjectListImpl.EMPTY_LIST;
    }
    
    public Object getMinInclusiveValue() {
        return this.fMinInclusive;
    }
    
    public Object getMinExclusiveValue() {
        return this.fMinExclusive;
    }
    
    public Object getMaxInclusiveValue() {
        return this.fMaxInclusive;
    }
    
    public Object getMaxExclusiveValue() {
        return this.fMaxExclusive;
    }
    
    public void setAnonymous(final boolean fAnonymous) {
        this.fAnonymous = fAnonymous;
    }
    
    public String getTypeNamespace() {
        return this.getNamespace();
    }
    
    public boolean isDerivedFrom(final String s, final String s2, final int n) {
        return this.isDOMDerivedFrom(s, s2, n);
    }
    
    private void appendEnumString(final StringBuffer sb) {
        sb.append('[');
        for (int i = 0; i < this.fEnumerationSize; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(this.fEnumeration[i].actualValue);
        }
        sb.append(']');
    }
    
    static {
        gDVs = new TypeValidator[] { new AnySimpleDV(), new StringDV(), new BooleanDV(), new DecimalDV(), new FloatDV(), new DoubleDV(), new DurationDV(), new DateTimeDV(), new TimeDV(), new DateDV(), new YearMonthDV(), new YearDV(), new MonthDayDV(), new DayDV(), new MonthDV(), new HexBinaryDV(), new Base64BinaryDV(), new AnyURIDV(), new QNameDV(), new PrecisionDecimalDV(), new QNameDV(), new IDDV(), new IDREFDV(), new EntityDV(), new IntegerDV(), new ListDV(), new UnionDV(), new YearMonthDurationDV(), new DayTimeDurationDV(), new AnyAtomicDV(), new ErrorDV(), new DateTimeStampDV() };
        fDVNormalizeType = new short[] { 0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 0, 0, 1 };
        SPECIAL_PATTERN_STRING = new String[] { "NONE", "NMTOKEN", "Name", "NCName" };
        WS_FACET_STRING = new String[] { "preserve", "replace", "collapse" };
        ET_FACET_STRING = new String[] { "optional", "required", "prohibited" };
        fEmptyContext = new ValidationContext() {
            public boolean needFacetChecking() {
                return true;
            }
            
            public boolean needExtraChecking() {
                return false;
            }
            
            public boolean needToNormalize() {
                return true;
            }
            
            public boolean useNamespaces() {
                return true;
            }
            
            public boolean isEntityDeclared(final String s) {
                return false;
            }
            
            public boolean isEntityUnparsed(final String s) {
                return false;
            }
            
            public boolean isIdDeclared(final String s) {
                return false;
            }
            
            public void addId(final String s) {
            }
            
            public void addIdRef(final String s) {
            }
            
            public String getSymbol(final String s) {
                return s.intern();
            }
            
            public String getURI(final String s) {
                return null;
            }
            
            public Locale getLocale() {
                return Locale.getDefault();
            }
            
            public TypeValidatorHelper getTypeValidatorHelper() {
                return TypeValidatorHelper.getInstance((short)1);
            }
            
            public short getDatatypeXMLVersion() {
                return 1;
            }
        };
        XSSimpleTypeDecl.fDefaultTypeValidatorHelper = TypeValidatorHelper.getInstance((short)1);
        fAnySimpleType = new XSSimpleTypeDecl(null, "anySimpleType", (short)0, (short)0, false, true, false, true, (short)1);
        fAnyAtomicType = new XSSimpleTypeDecl(XSSimpleTypeDecl.fAnySimpleType, "anyAtomicType", (short)29, (short)0, false, true, false, true, (short)49);
        fError = new XSSimpleTypeDecl(XSSimpleTypeDecl.fAnySimpleType, "error", (short)30, (short)0, false, true, false, true, (short)50);
        fDummyContext = new ValidationContext() {
            public boolean needFacetChecking() {
                return true;
            }
            
            public boolean needExtraChecking() {
                return false;
            }
            
            public boolean needToNormalize() {
                return false;
            }
            
            public boolean useNamespaces() {
                return true;
            }
            
            public boolean isEntityDeclared(final String s) {
                return false;
            }
            
            public boolean isEntityUnparsed(final String s) {
                return false;
            }
            
            public boolean isIdDeclared(final String s) {
                return false;
            }
            
            public void addId(final String s) {
            }
            
            public void addIdRef(final String s) {
            }
            
            public String getSymbol(final String s) {
                return s.intern();
            }
            
            public String getURI(final String s) {
                return null;
            }
            
            public Locale getLocale() {
                return Locale.getDefault();
            }
            
            public TypeValidatorHelper getTypeValidatorHelper() {
                return TypeValidatorHelper.getInstance((short)1);
            }
            
            public short getDatatypeXMLVersion() {
                return 1;
            }
        };
    }
    
    private abstract static class AbstractObjectList extends AbstractList implements ObjectList
    {
        public Object get(final int n) {
            if (n >= 0 && n < this.getLength()) {
                return this.item(n);
            }
            throw new IndexOutOfBoundsException("Index: " + n);
        }
        
        public int size() {
            return this.getLength();
        }
    }
    
    static final class ValidationContextImpl implements ValidationContext
    {
        final ValidationContext fExternal;
        NamespaceContext fNSContext;
        
        ValidationContextImpl(final ValidationContext fExternal) {
            this.fExternal = fExternal;
        }
        
        void setNSContext(final NamespaceContext fnsContext) {
            this.fNSContext = fnsContext;
        }
        
        public boolean needFacetChecking() {
            return this.fExternal.needFacetChecking();
        }
        
        public boolean needExtraChecking() {
            return this.fExternal.needExtraChecking();
        }
        
        public boolean needToNormalize() {
            return this.fExternal.needToNormalize();
        }
        
        public boolean useNamespaces() {
            return true;
        }
        
        public boolean isEntityDeclared(final String s) {
            return this.fExternal.isEntityDeclared(s);
        }
        
        public boolean isEntityUnparsed(final String s) {
            return this.fExternal.isEntityUnparsed(s);
        }
        
        public boolean isIdDeclared(final String s) {
            return this.fExternal.isIdDeclared(s);
        }
        
        public void addId(final String s) {
            this.fExternal.addId(s);
        }
        
        public void addIdRef(final String s) {
            this.fExternal.addIdRef(s);
        }
        
        public String getSymbol(final String s) {
            return this.fExternal.getSymbol(s);
        }
        
        public String getURI(final String s) {
            if (this.fNSContext == null) {
                return this.fExternal.getURI(s);
            }
            return this.fNSContext.getURI(s);
        }
        
        public Locale getLocale() {
            return this.fExternal.getLocale();
        }
        
        public TypeValidatorHelper getTypeValidatorHelper() {
            return this.fExternal.getTypeValidatorHelper();
        }
        
        public short getDatatypeXMLVersion() {
            return this.fExternal.getDatatypeXMLVersion();
        }
    }
    
    private static final class XSFacetImpl implements XSFacet
    {
        final short kind;
        final String svalue;
        final int ivalue;
        Object avalue;
        final boolean fixed;
        final XSObjectList annotations;
        
        public XSFacetImpl(final short kind, final String svalue, final int ivalue, final Object avalue, final boolean fixed, final XSAnnotation xsAnnotation) {
            this.kind = kind;
            this.svalue = svalue;
            this.ivalue = ivalue;
            this.avalue = avalue;
            this.fixed = fixed;
            if (xsAnnotation != null) {
                this.annotations = new XSObjectListImpl();
                ((XSObjectListImpl)this.annotations).addXSObject(xsAnnotation);
            }
            else {
                this.annotations = XSObjectListImpl.EMPTY_LIST;
            }
        }
        
        public XSAnnotation getAnnotation() {
            return (XSAnnotation)this.annotations.item(0);
        }
        
        public XSObjectList getAnnotations() {
            return this.annotations;
        }
        
        public short getFacetKind() {
            return this.kind;
        }
        
        public String getLexicalFacetValue() {
            return this.svalue;
        }
        
        public Object getActualFacetValue() {
            if (this.avalue == null) {
                if (this.kind == 16 || this.kind == -32768) {
                    this.avalue = this.svalue;
                }
                else {
                    this.avalue = BigInteger.valueOf(this.ivalue);
                }
            }
            return this.avalue;
        }
        
        public int getIntFacetValue() {
            return this.ivalue;
        }
        
        public boolean getFixed() {
            return this.fixed;
        }
        
        public String getName() {
            return null;
        }
        
        public String getNamespace() {
            return null;
        }
        
        public XSNamespaceItem getNamespaceItem() {
            return null;
        }
        
        public short getType() {
            return 13;
        }
    }
    
    private static final class XSMVFacetImpl implements XSMultiValueFacet
    {
        final short kind;
        final XSObjectList annotations;
        final StringList svalues;
        final ObjectList avalues;
        final Vector asserts;
        
        public XSMVFacetImpl(final short kind, final StringList svalues, final ObjectList avalues, final XSObjectList list) {
            this.kind = kind;
            this.svalues = svalues;
            this.avalues = avalues;
            this.annotations = ((list != null) ? list : XSObjectListImpl.EMPTY_LIST);
            this.asserts = null;
        }
        
        public XSMVFacetImpl(final short kind, final Vector asserts) {
            this.kind = kind;
            this.asserts = asserts;
            this.svalues = null;
            this.avalues = null;
            this.annotations = null;
        }
        
        public short getFacetKind() {
            return this.kind;
        }
        
        public XSObjectList getAnnotations() {
            return this.annotations;
        }
        
        public StringList getLexicalFacetValues() {
            return this.svalues;
        }
        
        public ObjectList getEnumerationValues() {
            return this.avalues;
        }
        
        public String getName() {
            return null;
        }
        
        public String getNamespace() {
            return null;
        }
        
        public XSNamespaceItem getNamespaceItem() {
            return null;
        }
        
        public short getType() {
            return 14;
        }
        
        public Vector getAsserts() {
            return this.asserts;
        }
    }
}
