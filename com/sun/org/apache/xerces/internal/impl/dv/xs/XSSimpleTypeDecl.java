package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.util.AbstractList;
import com.sun.org.apache.xerces.internal.xs.XSMultiValueFacet;
import com.sun.org.apache.xerces.internal.xs.XSFacet;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.ShortListImpl;
import java.util.StringTokenizer;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import org.w3c.dom.TypeInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;

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
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static final String ANY_TYPE = "anyType";
    public static final short YEARMONTHDURATION_DT = 46;
    public static final short DAYTIMEDURATION_DT = 47;
    public static final short PRECISIONDECIMAL_DT = 48;
    public static final short ANYATOMICTYPE_DT = 49;
    static final int DERIVATION_ANY = 0;
    static final int DERIVATION_RESTRICTION = 1;
    static final int DERIVATION_EXTENSION = 2;
    static final int DERIVATION_UNION = 4;
    static final int DERIVATION_LIST = 8;
    static final ValidationContext fEmptyContext;
    private TypeValidator[] fDVs;
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
    private int fLength;
    private int fMinLength;
    private int fMaxLength;
    private int fTotalDigits;
    private int fFractionDigits;
    private Vector fPattern;
    private Vector fPatternStr;
    private Vector fEnumeration;
    private short[] fEnumerationType;
    private ShortList[] fEnumerationItemType;
    private ShortList fEnumerationTypeList;
    private ObjectList fEnumerationItemTypeList;
    private StringList fLexicalPattern;
    private StringList fLexicalEnumeration;
    private ObjectList fActualEnumeration;
    private Object fMaxInclusive;
    private Object fMaxExclusive;
    private Object fMinExclusive;
    private Object fMinInclusive;
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
    private XSObjectListImpl fFacets;
    private XSObjectListImpl fMultiValueFacets;
    private XSObjectList fAnnotations;
    private short fPatternType;
    private short fOrdered;
    private boolean fFinite;
    private boolean fBounded;
    private boolean fNumeric;
    private XSNamespaceItem fNamespaceItem;
    static final XSSimpleTypeDecl fAnySimpleType;
    static final XSSimpleTypeDecl fAnyAtomicType;
    static final ValidationContext fDummyContext;
    private boolean fAnonymous;
    
    protected static TypeValidator[] getGDVs() {
        return XSSimpleTypeDecl.gDVs.clone();
    }
    
    protected void setDVs(final TypeValidator[] dvs) {
        this.fDVs = dvs;
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
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fAnonymous = false;
    }
    
    protected XSSimpleTypeDecl(final XSSimpleTypeDecl base, final String name, final short validateDV, final short ordered, final boolean bounded, final boolean finite, final boolean numeric, final boolean isImmutable, final short builtInKind) {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fAnonymous = false;
        this.fIsImmutable = isImmutable;
        this.fBase = base;
        this.fTypeName = name;
        this.fTargetNamespace = "http://www.w3.org/2001/XMLSchema";
        this.fVariety = 1;
        this.fValidationDV = validateDV;
        this.fFacetsDefined = 16;
        if (validateDV == 0 || validateDV == 29 || validateDV == 1) {
            this.fWhiteSpace = 0;
        }
        else {
            this.fWhiteSpace = 2;
            this.fFixedFacet = 16;
        }
        this.fOrdered = ordered;
        this.fBounded = bounded;
        this.fFinite = finite;
        this.fNumeric = numeric;
        this.fAnnotations = null;
        this.fBuiltInKind = builtInKind;
    }
    
    protected XSSimpleTypeDecl(final XSSimpleTypeDecl base, final String name, final String uri, final short finalSet, final boolean isImmutable, final XSObjectList annotations, final short builtInKind) {
        this(base, name, uri, finalSet, isImmutable, annotations);
        this.fBuiltInKind = builtInKind;
    }
    
    protected XSSimpleTypeDecl(final XSSimpleTypeDecl base, final String name, final String uri, final short finalSet, final boolean isImmutable, final XSObjectList annotations) {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fAnonymous = false;
        this.fBase = base;
        this.fTypeName = name;
        this.fTargetNamespace = uri;
        this.fFinalSet = finalSet;
        this.fAnnotations = annotations;
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
        this.fEnumerationType = this.fBase.fEnumerationType;
        this.fEnumerationItemType = this.fBase.fEnumerationItemType;
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
        this.calcFundamentalFacets();
        this.fIsImmutable = isImmutable;
        this.fBuiltInKind = base.fBuiltInKind;
    }
    
    protected XSSimpleTypeDecl(final String name, final String uri, final short finalSet, final XSSimpleTypeDecl itemType, final boolean isImmutable, final XSObjectList annotations) {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fAnonymous = false;
        this.fBase = XSSimpleTypeDecl.fAnySimpleType;
        this.fTypeName = name;
        this.fTargetNamespace = uri;
        this.fFinalSet = finalSet;
        this.fAnnotations = annotations;
        this.fVariety = 2;
        this.fItemType = itemType;
        this.fValidationDV = 25;
        this.fFacetsDefined = 16;
        this.fFixedFacet = 16;
        this.fWhiteSpace = 2;
        this.calcFundamentalFacets();
        this.fIsImmutable = isImmutable;
        this.fBuiltInKind = 44;
    }
    
    protected XSSimpleTypeDecl(final String name, final String uri, final short finalSet, final XSSimpleTypeDecl[] memberTypes, final XSObjectList annotations) {
        this.fDVs = XSSimpleTypeDecl.gDVs;
        this.fIsImmutable = false;
        this.fFinalSet = 0;
        this.fVariety = -1;
        this.fValidationDV = -1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fAnnotations = null;
        this.fPatternType = 0;
        this.fNamespaceItem = null;
        this.fAnonymous = false;
        this.fBase = XSSimpleTypeDecl.fAnySimpleType;
        this.fTypeName = name;
        this.fTargetNamespace = uri;
        this.fFinalSet = finalSet;
        this.fAnnotations = annotations;
        this.fVariety = 3;
        this.fMemberTypes = memberTypes;
        this.fValidationDV = 26;
        this.fFacetsDefined = 16;
        this.fWhiteSpace = 2;
        this.calcFundamentalFacets();
        this.fIsImmutable = false;
        this.fBuiltInKind = 45;
    }
    
    protected XSSimpleTypeDecl setRestrictionValues(final XSSimpleTypeDecl base, final String name, final String uri, final short finalSet, final XSObjectList annotations) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = base;
        this.fAnonymous = false;
        this.fTypeName = name;
        this.fTargetNamespace = uri;
        this.fFinalSet = finalSet;
        this.fAnnotations = annotations;
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
        this.fEnumerationType = this.fBase.fEnumerationType;
        this.fEnumerationItemType = this.fBase.fEnumerationItemType;
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
        this.calcFundamentalFacets();
        this.fBuiltInKind = base.fBuiltInKind;
        return this;
    }
    
    protected XSSimpleTypeDecl setListValues(final String name, final String uri, final short finalSet, final XSSimpleTypeDecl itemType, final XSObjectList annotations) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = XSSimpleTypeDecl.fAnySimpleType;
        this.fAnonymous = false;
        this.fTypeName = name;
        this.fTargetNamespace = uri;
        this.fFinalSet = finalSet;
        this.fAnnotations = annotations;
        this.fVariety = 2;
        this.fItemType = itemType;
        this.fValidationDV = 25;
        this.fFacetsDefined = 16;
        this.fFixedFacet = 16;
        this.fWhiteSpace = 2;
        this.calcFundamentalFacets();
        this.fBuiltInKind = 44;
        return this;
    }
    
    protected XSSimpleTypeDecl setUnionValues(final String name, final String uri, final short finalSet, final XSSimpleTypeDecl[] memberTypes, final XSObjectList annotations) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = XSSimpleTypeDecl.fAnySimpleType;
        this.fAnonymous = false;
        this.fTypeName = name;
        this.fTargetNamespace = uri;
        this.fFinalSet = finalSet;
        this.fAnnotations = annotations;
        this.fVariety = 3;
        this.fMemberTypes = memberTypes;
        this.fValidationDV = 26;
        this.fFacetsDefined = 16;
        this.fWhiteSpace = 2;
        this.calcFundamentalFacets();
        this.fBuiltInKind = 45;
        return this;
    }
    
    @Override
    public short getType() {
        return 3;
    }
    
    @Override
    public short getTypeCategory() {
        return 16;
    }
    
    @Override
    public String getName() {
        return this.getAnonymous() ? null : this.fTypeName;
    }
    
    @Override
    public String getTypeName() {
        return this.fTypeName;
    }
    
    @Override
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    @Override
    public short getFinal() {
        return this.fFinalSet;
    }
    
    @Override
    public boolean isFinal(final short derivation) {
        return (this.fFinalSet & derivation) != 0x0;
    }
    
    @Override
    public XSTypeDefinition getBaseType() {
        return this.fBase;
    }
    
    @Override
    public boolean getAnonymous() {
        return this.fAnonymous || this.fTypeName == null;
    }
    
    @Override
    public short getVariety() {
        return (short)((this.fValidationDV == 0) ? 0 : this.fVariety);
    }
    
    @Override
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
    
    @Override
    public short getWhitespace() throws DatatypeException {
        if (this.fVariety == 3) {
            throw new DatatypeException("dt-whitespace", new Object[] { this.fTypeName });
        }
        return this.fWhiteSpace;
    }
    
    @Override
    public short getPrimitiveKind() {
        if (this.fVariety != 1 || this.fValidationDV == 0) {
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
    
    @Override
    public short getBuiltInKind() {
        return this.fBuiltInKind;
    }
    
    @Override
    public XSSimpleTypeDefinition getPrimitiveType() {
        if (this.fVariety == 1 && this.fValidationDV != 0) {
            XSSimpleTypeDecl pri;
            for (pri = this; pri.fBase != XSSimpleTypeDecl.fAnySimpleType; pri = pri.fBase) {}
            return pri;
        }
        return null;
    }
    
    @Override
    public XSSimpleTypeDefinition getItemType() {
        if (this.fVariety == 2) {
            return this.fItemType;
        }
        return null;
    }
    
    @Override
    public XSObjectList getMemberTypes() {
        if (this.fVariety == 3) {
            return new XSObjectListImpl(this.fMemberTypes, this.fMemberTypes.length);
        }
        return XSObjectListImpl.EMPTY_LIST;
    }
    
    @Override
    public void applyFacets(final XSFacets facets, final short presentFacet, final short fixedFacet, ValidationContext context) throws InvalidDatatypeFacetException {
        if (context == null) {
            context = XSSimpleTypeDecl.fEmptyContext;
        }
        this.applyFacets(facets, presentFacet, fixedFacet, (short)0, context);
    }
    
    void applyFacets1(final XSFacets facets, final short presentFacet, final short fixedFacet) {
        try {
            this.applyFacets(facets, presentFacet, fixedFacet, (short)0, XSSimpleTypeDecl.fDummyContext);
        }
        catch (final InvalidDatatypeFacetException e) {
            throw new RuntimeException("internal error");
        }
        this.fIsImmutable = true;
    }
    
    void applyFacets1(final XSFacets facets, final short presentFacet, final short fixedFacet, final short patternType) {
        try {
            this.applyFacets(facets, presentFacet, fixedFacet, patternType, XSSimpleTypeDecl.fDummyContext);
        }
        catch (final InvalidDatatypeFacetException e) {
            throw new RuntimeException("internal error");
        }
        this.fIsImmutable = true;
    }
    
    void applyFacets(final XSFacets facets, final short presentFacet, final short fixedFacet, final short patternType, final ValidationContext context) throws InvalidDatatypeFacetException {
        if (this.fIsImmutable) {
            return;
        }
        final ValidatedInfo tempInfo = new ValidatedInfo();
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        int result = 0;
        final short allowedFacet = this.fDVs[this.fValidationDV].getAllowedFacets();
        if ((presentFacet & 0x1) != 0x0) {
            if ((allowedFacet & 0x1) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "length", this.fTypeName });
            }
            else {
                this.fLength = facets.length;
                this.lengthAnnotation = facets.lengthAnnotation;
                this.fFacetsDefined |= 0x1;
                if ((fixedFacet & 0x1) != 0x0) {
                    this.fFixedFacet |= 0x1;
                }
            }
        }
        if ((presentFacet & 0x2) != 0x0) {
            if ((allowedFacet & 0x2) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "minLength", this.fTypeName });
            }
            else {
                this.fMinLength = facets.minLength;
                this.minLengthAnnotation = facets.minLengthAnnotation;
                this.fFacetsDefined |= 0x2;
                if ((fixedFacet & 0x2) != 0x0) {
                    this.fFixedFacet |= 0x2;
                }
            }
        }
        if ((presentFacet & 0x4) != 0x0) {
            if ((allowedFacet & 0x4) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "maxLength", this.fTypeName });
            }
            else {
                this.fMaxLength = facets.maxLength;
                this.maxLengthAnnotation = facets.maxLengthAnnotation;
                this.fFacetsDefined |= 0x4;
                if ((fixedFacet & 0x4) != 0x0) {
                    this.fFixedFacet |= 0x4;
                }
            }
        }
        if ((presentFacet & 0x8) != 0x0) {
            if ((allowedFacet & 0x8) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "pattern", this.fTypeName });
            }
            else {
                this.patternAnnotations = facets.patternAnnotations;
                RegularExpression regex = null;
                try {
                    regex = new RegularExpression(facets.pattern, "X", context.getLocale());
                }
                catch (final Exception e) {
                    this.reportError("InvalidRegex", new Object[] { facets.pattern, e.getLocalizedMessage() });
                }
                if (regex != null) {
                    (this.fPattern = new Vector()).addElement(regex);
                    (this.fPatternStr = new Vector()).addElement(facets.pattern);
                    this.fFacetsDefined |= 0x8;
                    if ((fixedFacet & 0x8) != 0x0) {
                        this.fFixedFacet |= 0x8;
                    }
                }
            }
        }
        if ((presentFacet & 0x10) != 0x0) {
            if ((allowedFacet & 0x10) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "whiteSpace", this.fTypeName });
            }
            else {
                this.fWhiteSpace = facets.whiteSpace;
                this.whiteSpaceAnnotation = facets.whiteSpaceAnnotation;
                this.fFacetsDefined |= 0x10;
                if ((fixedFacet & 0x10) != 0x0) {
                    this.fFixedFacet |= 0x10;
                }
            }
        }
        if ((presentFacet & 0x800) != 0x0) {
            if ((allowedFacet & 0x800) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "enumeration", this.fTypeName });
            }
            else {
                this.fEnumeration = new Vector();
                final Vector enumVals = facets.enumeration;
                this.fEnumerationType = new short[enumVals.size()];
                this.fEnumerationItemType = new ShortList[enumVals.size()];
                final Vector enumNSDecls = facets.enumNSDecls;
                final ValidationContextImpl ctx = new ValidationContextImpl(context);
                this.enumerationAnnotations = facets.enumAnnotations;
                for (int i = 0; i < enumVals.size(); ++i) {
                    if (enumNSDecls != null) {
                        ctx.setNSContext(enumNSDecls.elementAt(i));
                    }
                    try {
                        final ValidatedInfo info = this.getActualEnumValue(enumVals.elementAt(i), ctx, tempInfo);
                        this.fEnumeration.addElement(info.actualValue);
                        this.fEnumerationType[i] = info.actualValueType;
                        this.fEnumerationItemType[i] = info.itemValueTypes;
                    }
                    catch (final InvalidDatatypeValueException ide) {
                        this.reportError("enumeration-valid-restriction", new Object[] { enumVals.elementAt(i), this.getBaseType().getName() });
                    }
                }
                this.fFacetsDefined |= 0x800;
                if ((fixedFacet & 0x800) != 0x0) {
                    this.fFixedFacet |= 0x800;
                }
            }
        }
        if ((presentFacet & 0x20) != 0x0) {
            if ((allowedFacet & 0x20) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "maxInclusive", this.fTypeName });
            }
            else {
                this.maxInclusiveAnnotation = facets.maxInclusiveAnnotation;
                try {
                    this.fMaxInclusive = this.fBase.getActualValue(facets.maxInclusive, context, tempInfo, true);
                    this.fFacetsDefined |= 0x20;
                    if ((fixedFacet & 0x20) != 0x0) {
                        this.fFixedFacet |= 0x20;
                    }
                }
                catch (final InvalidDatatypeValueException ide2) {
                    this.reportError(ide2.getKey(), ide2.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, facets.maxInclusive, "maxInclusive", this.fBase.getName() });
                }
                if ((this.fBase.fFacetsDefined & 0x20) != 0x0 && (this.fBase.fFixedFacet & 0x20) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMaxInclusive, this.fBase.fMaxInclusive) != 0) {
                    this.reportError("FixedFacetValue", new Object[] { "maxInclusive", this.fMaxInclusive, this.fBase.fMaxInclusive, this.fTypeName });
                }
                try {
                    this.fBase.validate(context, tempInfo);
                }
                catch (final InvalidDatatypeValueException ide2) {
                    this.reportError(ide2.getKey(), ide2.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, facets.maxInclusive, "maxInclusive", this.fBase.getName() });
                }
            }
        }
        boolean needCheckBase = true;
        if ((presentFacet & 0x40) != 0x0) {
            if ((allowedFacet & 0x40) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "maxExclusive", this.fTypeName });
            }
            else {
                this.maxExclusiveAnnotation = facets.maxExclusiveAnnotation;
                try {
                    this.fMaxExclusive = this.fBase.getActualValue(facets.maxExclusive, context, tempInfo, true);
                    this.fFacetsDefined |= 0x40;
                    if ((fixedFacet & 0x40) != 0x0) {
                        this.fFixedFacet |= 0x40;
                    }
                }
                catch (final InvalidDatatypeValueException ide3) {
                    this.reportError(ide3.getKey(), ide3.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, facets.maxExclusive, "maxExclusive", this.fBase.getName() });
                }
                if ((this.fBase.fFacetsDefined & 0x40) != 0x0) {
                    result = this.fDVs[this.fValidationDV].compare(this.fMaxExclusive, this.fBase.fMaxExclusive);
                    if ((this.fBase.fFixedFacet & 0x40) != 0x0 && result != 0) {
                        this.reportError("FixedFacetValue", new Object[] { "maxExclusive", facets.maxExclusive, this.fBase.fMaxExclusive, this.fTypeName });
                    }
                    if (result == 0) {
                        needCheckBase = false;
                    }
                }
                if (needCheckBase) {
                    try {
                        this.fBase.validate(context, tempInfo);
                    }
                    catch (final InvalidDatatypeValueException ide3) {
                        this.reportError(ide3.getKey(), ide3.getArgs());
                        this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, facets.maxExclusive, "maxExclusive", this.fBase.getName() });
                    }
                }
                else if ((this.fBase.fFacetsDefined & 0x20) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMaxExclusive, this.fBase.fMaxInclusive) > 0) {
                    this.reportError("maxExclusive-valid-restriction.2", new Object[] { facets.maxExclusive, this.fBase.fMaxInclusive });
                }
            }
        }
        needCheckBase = true;
        if ((presentFacet & 0x80) != 0x0) {
            if ((allowedFacet & 0x80) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "minExclusive", this.fTypeName });
            }
            else {
                this.minExclusiveAnnotation = facets.minExclusiveAnnotation;
                try {
                    this.fMinExclusive = this.fBase.getActualValue(facets.minExclusive, context, tempInfo, true);
                    this.fFacetsDefined |= 0x80;
                    if ((fixedFacet & 0x80) != 0x0) {
                        this.fFixedFacet |= 0x80;
                    }
                }
                catch (final InvalidDatatypeValueException ide3) {
                    this.reportError(ide3.getKey(), ide3.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, facets.minExclusive, "minExclusive", this.fBase.getName() });
                }
                if ((this.fBase.fFacetsDefined & 0x80) != 0x0) {
                    result = this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fBase.fMinExclusive);
                    if ((this.fBase.fFixedFacet & 0x80) != 0x0 && result != 0) {
                        this.reportError("FixedFacetValue", new Object[] { "minExclusive", facets.minExclusive, this.fBase.fMinExclusive, this.fTypeName });
                    }
                    if (result == 0) {
                        needCheckBase = false;
                    }
                }
                if (needCheckBase) {
                    try {
                        this.fBase.validate(context, tempInfo);
                    }
                    catch (final InvalidDatatypeValueException ide3) {
                        this.reportError(ide3.getKey(), ide3.getArgs());
                        this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, facets.minExclusive, "minExclusive", this.fBase.getName() });
                    }
                }
                else if ((this.fBase.fFacetsDefined & 0x100) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fBase.fMinInclusive) < 0) {
                    this.reportError("minExclusive-valid-restriction.3", new Object[] { facets.minExclusive, this.fBase.fMinInclusive });
                }
            }
        }
        if ((presentFacet & 0x100) != 0x0) {
            if ((allowedFacet & 0x100) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "minInclusive", this.fTypeName });
            }
            else {
                this.minInclusiveAnnotation = facets.minInclusiveAnnotation;
                try {
                    this.fMinInclusive = this.fBase.getActualValue(facets.minInclusive, context, tempInfo, true);
                    this.fFacetsDefined |= 0x100;
                    if ((fixedFacet & 0x100) != 0x0) {
                        this.fFixedFacet |= 0x100;
                    }
                }
                catch (final InvalidDatatypeValueException ide3) {
                    this.reportError(ide3.getKey(), ide3.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, facets.minInclusive, "minInclusive", this.fBase.getName() });
                }
                if ((this.fBase.fFacetsDefined & 0x100) != 0x0 && (this.fBase.fFixedFacet & 0x100) != 0x0 && this.fDVs[this.fValidationDV].compare(this.fMinInclusive, this.fBase.fMinInclusive) != 0) {
                    this.reportError("FixedFacetValue", new Object[] { "minInclusive", facets.minInclusive, this.fBase.fMinInclusive, this.fTypeName });
                }
                try {
                    this.fBase.validate(context, tempInfo);
                }
                catch (final InvalidDatatypeValueException ide3) {
                    this.reportError(ide3.getKey(), ide3.getArgs());
                    this.reportError("FacetValueFromBase", new Object[] { this.fTypeName, facets.minInclusive, "minInclusive", this.fBase.getName() });
                }
            }
        }
        if ((presentFacet & 0x200) != 0x0) {
            if ((allowedFacet & 0x200) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "totalDigits", this.fTypeName });
            }
            else {
                this.totalDigitsAnnotation = facets.totalDigitsAnnotation;
                this.fTotalDigits = facets.totalDigits;
                this.fFacetsDefined |= 0x200;
                if ((fixedFacet & 0x200) != 0x0) {
                    this.fFixedFacet |= 0x200;
                }
            }
        }
        if ((presentFacet & 0x400) != 0x0) {
            if ((allowedFacet & 0x400) == 0x0) {
                this.reportError("cos-applicable-facets", new Object[] { "fractionDigits", this.fTypeName });
            }
            else {
                this.fFractionDigits = facets.fractionDigits;
                this.fractionDigitsAnnotation = facets.fractionDigitsAnnotation;
                this.fFacetsDefined |= 0x400;
                if ((fixedFacet & 0x400) != 0x0) {
                    this.fFixedFacet |= 0x400;
                }
            }
        }
        if (patternType != 0) {
            this.fPatternType = patternType;
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
                result = this.fDVs[this.fValidationDV].compare(this.fMinInclusive, this.fMaxInclusive);
                if (result != -1 && result != 0) {
                    this.reportError("minInclusive-less-than-equal-to-maxInclusive", new Object[] { this.fMinInclusive, this.fMaxInclusive, this.fTypeName });
                }
            }
            if ((this.fFacetsDefined & 0x40) != 0x0 && (this.fFacetsDefined & 0x80) != 0x0) {
                result = this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fMaxExclusive);
                if (result != -1 && result != 0) {
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
                for (int j = this.fBase.fPattern.size() - 1; j >= 0; --j) {
                    this.fPattern.addElement(this.fBase.fPattern.elementAt(j));
                    this.fPatternStr.addElement(this.fBase.fPatternStr.elementAt(j));
                }
                if (this.fBase.patternAnnotations != null) {
                    if (this.patternAnnotations != null) {
                        for (int j = this.fBase.patternAnnotations.getLength() - 1; j >= 0; --j) {
                            this.patternAnnotations.addXSObject(this.fBase.patternAnnotations.item(j));
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
        if ((this.fFacetsDefined & 0x800) == 0x0 && (this.fBase.fFacetsDefined & 0x800) != 0x0) {
            this.fFacetsDefined |= 0x800;
            this.fEnumeration = this.fBase.fEnumeration;
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
        if (this.fPatternType == 0 && this.fBase.fPatternType != 0) {
            this.fPatternType = this.fBase.fPatternType;
        }
        this.fFixedFacet |= this.fBase.fFixedFacet;
        this.calcFundamentalFacets();
    }
    
    @Override
    public Object validate(final String content, ValidationContext context, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (context == null) {
            context = XSSimpleTypeDecl.fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        }
        else {
            validatedInfo.memberType = null;
        }
        final boolean needNormalize = context == null || context.needToNormalize();
        final Object ob = this.getActualValue(content, context, validatedInfo, needNormalize);
        this.validate(context, validatedInfo);
        return ob;
    }
    
    protected ValidatedInfo getActualEnumValue(final String lexical, final ValidationContext ctx, final ValidatedInfo info) throws InvalidDatatypeValueException {
        return this.fBase.validateWithInfo(lexical, ctx, info);
    }
    
    public ValidatedInfo validateWithInfo(final String content, ValidationContext context, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (context == null) {
            context = XSSimpleTypeDecl.fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        }
        else {
            validatedInfo.memberType = null;
        }
        final boolean needNormalize = context == null || context.needToNormalize();
        this.getActualValue(content, context, validatedInfo, needNormalize);
        this.validate(context, validatedInfo);
        return validatedInfo;
    }
    
    @Override
    public Object validate(final Object content, ValidationContext context, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (context == null) {
            context = XSSimpleTypeDecl.fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        }
        else {
            validatedInfo.memberType = null;
        }
        final boolean needNormalize = context == null || context.needToNormalize();
        final Object ob = this.getActualValue(content, context, validatedInfo, needNormalize);
        this.validate(context, validatedInfo);
        return ob;
    }
    
    @Override
    public void validate(ValidationContext context, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (context == null) {
            context = XSSimpleTypeDecl.fEmptyContext;
        }
        if (context.needFacetChecking() && this.fFacetsDefined != 0 && this.fFacetsDefined != 16) {
            this.checkFacets(validatedInfo);
        }
        if (context.needExtraChecking()) {
            this.checkExtraRules(context, validatedInfo);
        }
    }
    
    private void checkFacets(final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        final Object ob = validatedInfo.actualValue;
        final String content = validatedInfo.normalizedValue;
        final short type = validatedInfo.actualValueType;
        final ShortList itemType = validatedInfo.itemValueTypes;
        if (this.fValidationDV != 18 && this.fValidationDV != 20) {
            final int length = this.fDVs[this.fValidationDV].getDataLength(ob);
            if ((this.fFacetsDefined & 0x4) != 0x0 && length > this.fMaxLength) {
                throw new InvalidDatatypeValueException("cvc-maxLength-valid", new Object[] { content, Integer.toString(length), Integer.toString(this.fMaxLength), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x2) != 0x0 && length < this.fMinLength) {
                throw new InvalidDatatypeValueException("cvc-minLength-valid", new Object[] { content, Integer.toString(length), Integer.toString(this.fMinLength), this.fTypeName });
            }
            if ((this.fFacetsDefined & 0x1) != 0x0 && length != this.fLength) {
                throw new InvalidDatatypeValueException("cvc-length-valid", new Object[] { content, Integer.toString(length), Integer.toString(this.fLength), this.fTypeName });
            }
        }
        if ((this.fFacetsDefined & 0x800) != 0x0) {
            boolean present = false;
            final int enumSize = this.fEnumeration.size();
            final short primitiveType1 = this.convertToPrimitiveKind(type);
            for (int i = 0; i < enumSize; ++i) {
                final short primitiveType2 = this.convertToPrimitiveKind(this.fEnumerationType[i]);
                if ((primitiveType1 == primitiveType2 || (primitiveType1 == 1 && primitiveType2 == 2) || (primitiveType1 == 2 && primitiveType2 == 1)) && this.fEnumeration.elementAt(i).equals(ob)) {
                    if (primitiveType1 != 44 && primitiveType1 != 43) {
                        present = true;
                        break;
                    }
                    final ShortList enumItemType = this.fEnumerationItemType[i];
                    final int typeList1Length = (itemType != null) ? itemType.getLength() : 0;
                    final int typeList2Length = (enumItemType != null) ? enumItemType.getLength() : 0;
                    if (typeList1Length == typeList2Length) {
                        int j;
                        for (j = 0; j < typeList1Length; ++j) {
                            final short primitiveItem1 = this.convertToPrimitiveKind(itemType.item(j));
                            final short primitiveItem2 = this.convertToPrimitiveKind(enumItemType.item(j));
                            if (primitiveItem1 != primitiveItem2 && (primitiveItem1 != 1 || primitiveItem2 != 2) && (primitiveItem1 != 2 || primitiveItem2 != 1)) {
                                break;
                            }
                        }
                        if (j == typeList1Length) {
                            present = true;
                            break;
                        }
                    }
                }
            }
            if (!present) {
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { content, this.fEnumeration.toString() });
            }
        }
        if ((this.fFacetsDefined & 0x400) != 0x0) {
            final int scale = this.fDVs[this.fValidationDV].getFractionDigits(ob);
            if (scale > this.fFractionDigits) {
                throw new InvalidDatatypeValueException("cvc-fractionDigits-valid", new Object[] { content, Integer.toString(scale), Integer.toString(this.fFractionDigits) });
            }
        }
        if ((this.fFacetsDefined & 0x200) != 0x0) {
            final int totalDigits = this.fDVs[this.fValidationDV].getTotalDigits(ob);
            if (totalDigits > this.fTotalDigits) {
                throw new InvalidDatatypeValueException("cvc-totalDigits-valid", new Object[] { content, Integer.toString(totalDigits), Integer.toString(this.fTotalDigits) });
            }
        }
        if ((this.fFacetsDefined & 0x20) != 0x0) {
            final int compare = this.fDVs[this.fValidationDV].compare(ob, this.fMaxInclusive);
            if (compare != -1 && compare != 0) {
                throw new InvalidDatatypeValueException("cvc-maxInclusive-valid", new Object[] { content, this.fMaxInclusive, this.fTypeName });
            }
        }
        if ((this.fFacetsDefined & 0x40) != 0x0) {
            final int compare = this.fDVs[this.fValidationDV].compare(ob, this.fMaxExclusive);
            if (compare != -1) {
                throw new InvalidDatatypeValueException("cvc-maxExclusive-valid", new Object[] { content, this.fMaxExclusive, this.fTypeName });
            }
        }
        if ((this.fFacetsDefined & 0x100) != 0x0) {
            final int compare = this.fDVs[this.fValidationDV].compare(ob, this.fMinInclusive);
            if (compare != 1 && compare != 0) {
                throw new InvalidDatatypeValueException("cvc-minInclusive-valid", new Object[] { content, this.fMinInclusive, this.fTypeName });
            }
        }
        if ((this.fFacetsDefined & 0x80) != 0x0) {
            final int compare = this.fDVs[this.fValidationDV].compare(ob, this.fMinExclusive);
            if (compare != 1) {
                throw new InvalidDatatypeValueException("cvc-minExclusive-valid", new Object[] { content, this.fMinExclusive, this.fTypeName });
            }
        }
    }
    
    private void checkExtraRules(final ValidationContext context, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        final Object ob = validatedInfo.actualValue;
        if (this.fVariety == 1) {
            this.fDVs[this.fValidationDV].checkExtraRules(ob, context);
        }
        else if (this.fVariety == 2) {
            final ListDV.ListData values = (ListDV.ListData)ob;
            final XSSimpleType memberType = validatedInfo.memberType;
            final int len = values.getLength();
            try {
                if (this.fItemType.fVariety == 3) {
                    final XSSimpleTypeDecl[] memberTypes = (XSSimpleTypeDecl[])validatedInfo.memberTypes;
                    for (int i = len - 1; i >= 0; --i) {
                        validatedInfo.actualValue = values.item(i);
                        validatedInfo.memberType = memberTypes[i];
                        this.fItemType.checkExtraRules(context, validatedInfo);
                    }
                }
                else {
                    for (int j = len - 1; j >= 0; --j) {
                        validatedInfo.actualValue = values.item(j);
                        this.fItemType.checkExtraRules(context, validatedInfo);
                    }
                }
            }
            finally {
                validatedInfo.actualValue = values;
                validatedInfo.memberType = memberType;
            }
        }
        else {
            ((XSSimpleTypeDecl)validatedInfo.memberType).checkExtraRules(context, validatedInfo);
        }
    }
    
    private Object getActualValue(final Object content, final ValidationContext context, final ValidatedInfo validatedInfo, final boolean needNormalize) throws InvalidDatatypeValueException {
        String nvalue;
        if (needNormalize) {
            nvalue = this.normalize(content, this.fWhiteSpace);
        }
        else {
            nvalue = content.toString();
        }
        if ((this.fFacetsDefined & 0x8) != 0x0) {
            if (this.fPattern.size() == 0 && nvalue.length() > 0) {
                throw new InvalidDatatypeValueException("cvc-pattern-valid", new Object[] { content, "(empty string)", this.fTypeName });
            }
            for (int idx = this.fPattern.size() - 1; idx >= 0; --idx) {
                final RegularExpression regex = this.fPattern.elementAt(idx);
                if (!regex.matches(nvalue)) {
                    throw new InvalidDatatypeValueException("cvc-pattern-valid", new Object[] { content, this.fPatternStr.elementAt(idx), this.fTypeName });
                }
            }
        }
        if (this.fVariety == 1) {
            if (this.fPatternType != 0) {
                boolean seenErr = false;
                if (this.fPatternType == 1) {
                    seenErr = !XMLChar.isValidNmtoken(nvalue);
                }
                else if (this.fPatternType == 2) {
                    seenErr = !XMLChar.isValidName(nvalue);
                }
                else if (this.fPatternType == 3) {
                    seenErr = !XMLChar.isValidNCName(nvalue);
                }
                if (seenErr) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { nvalue, XSSimpleTypeDecl.SPECIAL_PATTERN_STRING[this.fPatternType] });
                }
            }
            validatedInfo.normalizedValue = nvalue;
            final Object avalue = this.fDVs[this.fValidationDV].getActualValue(nvalue, context);
            validatedInfo.actualValue = avalue;
            validatedInfo.actualValueType = this.fBuiltInKind;
            return avalue;
        }
        if (this.fVariety == 2) {
            final StringTokenizer parsedList = new StringTokenizer(nvalue, " ");
            final int countOfTokens = parsedList.countTokens();
            final Object[] avalue2 = new Object[countOfTokens];
            final boolean isUnion = this.fItemType.getVariety() == 3;
            final short[] itemTypes = new short[isUnion ? countOfTokens : 1];
            if (!isUnion) {
                itemTypes[0] = this.fItemType.fBuiltInKind;
            }
            final XSSimpleTypeDecl[] memberTypes = new XSSimpleTypeDecl[countOfTokens];
            for (int i = 0; i < countOfTokens; ++i) {
                avalue2[i] = this.fItemType.getActualValue(parsedList.nextToken(), context, validatedInfo, false);
                if (context.needFacetChecking() && this.fItemType.fFacetsDefined != 0 && this.fItemType.fFacetsDefined != 16) {
                    this.fItemType.checkFacets(validatedInfo);
                }
                memberTypes[i] = (XSSimpleTypeDecl)validatedInfo.memberType;
                if (isUnion) {
                    itemTypes[i] = memberTypes[i].fBuiltInKind;
                }
            }
            final ListDV.ListData v = new ListDV.ListData(avalue2);
            validatedInfo.actualValue = v;
            validatedInfo.actualValueType = (short)(isUnion ? 43 : 44);
            validatedInfo.memberType = null;
            validatedInfo.memberTypes = memberTypes;
            validatedInfo.itemValueTypes = new ShortListImpl(itemTypes, itemTypes.length);
            validatedInfo.normalizedValue = nvalue;
            return v;
        }
        final Object _content = (this.fMemberTypes.length > 1 && content != null) ? content.toString() : content;
        int j = 0;
        while (j < this.fMemberTypes.length) {
            try {
                final Object aValue = this.fMemberTypes[j].getActualValue(_content, context, validatedInfo, true);
                if (context.needFacetChecking() && this.fMemberTypes[j].fFacetsDefined != 0 && this.fMemberTypes[j].fFacetsDefined != 16) {
                    this.fMemberTypes[j].checkFacets(validatedInfo);
                }
                validatedInfo.memberType = this.fMemberTypes[j];
                return aValue;
            }
            catch (final InvalidDatatypeValueException ex) {
                ++j;
                continue;
            }
            break;
        }
        final StringBuffer typesBuffer = new StringBuffer();
        for (int k = 0; k < this.fMemberTypes.length; ++k) {
            if (k != 0) {
                typesBuffer.append(" | ");
            }
            final XSSimpleTypeDecl decl = this.fMemberTypes[k];
            if (decl.fTargetNamespace != null) {
                typesBuffer.append('{');
                typesBuffer.append(decl.fTargetNamespace);
                typesBuffer.append('}');
            }
            typesBuffer.append(decl.fTypeName);
            if (decl.fEnumeration != null) {
                final Vector v2 = decl.fEnumeration;
                typesBuffer.append(" : [");
                for (int l = 0; l < v2.size(); ++l) {
                    if (l != 0) {
                        typesBuffer.append(',');
                    }
                    typesBuffer.append(v2.elementAt(l));
                }
                typesBuffer.append(']');
            }
        }
        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { content, this.fTypeName, typesBuffer.toString() });
    }
    
    @Override
    public boolean isEqual(final Object value1, final Object value2) {
        return value1 != null && value1.equals(value2);
    }
    
    public boolean isIdentical(final Object value1, final Object value2) {
        return value1 != null && this.fDVs[this.fValidationDV].isIdentical(value1, value2);
    }
    
    public static String normalize(final String content, final short ws) {
        final int len = (content == null) ? 0 : content.length();
        if (len == 0 || ws == 0) {
            return content;
        }
        final StringBuffer sb = new StringBuffer();
        if (ws == 1) {
            for (int i = 0; i < len; ++i) {
                final char ch = content.charAt(i);
                if (ch != '\t' && ch != '\n' && ch != '\r') {
                    sb.append(ch);
                }
                else {
                    sb.append(' ');
                }
            }
        }
        else {
            boolean isLeading = true;
            for (int i = 0; i < len; ++i) {
                char ch = content.charAt(i);
                if (ch != '\t' && ch != '\n' && ch != '\r' && ch != ' ') {
                    sb.append(ch);
                    isLeading = false;
                }
                else {
                    while (i < len - 1) {
                        ch = content.charAt(i + 1);
                        if (ch != '\t' && ch != '\n' && ch != '\r' && ch != ' ') {
                            break;
                        }
                        ++i;
                    }
                    if (i < len - 1 && !isLeading) {
                        sb.append(' ');
                    }
                }
            }
        }
        return sb.toString();
    }
    
    protected String normalize(final Object content, final short ws) {
        if (content == null) {
            return null;
        }
        if ((this.fFacetsDefined & 0x8) == 0x0) {
            final short norm_type = XSSimpleTypeDecl.fDVNormalizeType[this.fValidationDV];
            if (norm_type == 0) {
                return content.toString();
            }
            if (norm_type == 1) {
                return XMLChar.trim(content.toString());
            }
        }
        if (!(content instanceof StringBuffer)) {
            final String strContent = content.toString();
            return normalize(strContent, ws);
        }
        final StringBuffer sb = (StringBuffer)content;
        final int len = sb.length();
        if (len == 0) {
            return "";
        }
        if (ws == 0) {
            return sb.toString();
        }
        if (ws == 1) {
            for (int i = 0; i < len; ++i) {
                final char ch = sb.charAt(i);
                if (ch == '\t' || ch == '\n' || ch == '\r') {
                    sb.setCharAt(i, ' ');
                }
            }
        }
        else {
            int j = 0;
            boolean isLeading = true;
            for (int i = 0; i < len; ++i) {
                char ch = sb.charAt(i);
                if (ch != '\t' && ch != '\n' && ch != '\r' && ch != ' ') {
                    sb.setCharAt(j++, ch);
                    isLeading = false;
                }
                else {
                    while (i < len - 1) {
                        ch = sb.charAt(i + 1);
                        if (ch != '\t' && ch != '\n' && ch != '\r' && ch != ' ') {
                            break;
                        }
                        ++i;
                    }
                    if (i < len - 1 && !isLeading) {
                        sb.setCharAt(j++, ' ');
                    }
                }
            }
            sb.setLength(j);
        }
        return sb.toString();
    }
    
    void reportError(final String key, final Object[] args) throws InvalidDatatypeFacetException {
        throw new InvalidDatatypeFacetException(key, args);
    }
    
    private String whiteSpaceValue(final short ws) {
        return XSSimpleTypeDecl.WS_FACET_STRING[ws];
    }
    
    @Override
    public short getOrdered() {
        return this.fOrdered;
    }
    
    @Override
    public boolean getBounded() {
        return this.fBounded;
    }
    
    @Override
    public boolean getFinite() {
        return this.fFinite;
    }
    
    @Override
    public boolean getNumeric() {
        return this.fNumeric;
    }
    
    @Override
    public boolean isDefinedFacet(final short facetName) {
        if (this.fValidationDV == 0 || this.fValidationDV == 29) {
            return false;
        }
        if ((this.fFacetsDefined & facetName) != 0x0) {
            return true;
        }
        if (this.fPatternType != 0) {
            return facetName == 8;
        }
        return this.fValidationDV == 24 && (facetName == 8 || facetName == 1024);
    }
    
    @Override
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
    
    @Override
    public boolean isFixedFacet(final short facetName) {
        return (this.fFixedFacet & facetName) != 0x0 || (this.fValidationDV == 24 && facetName == 1024);
    }
    
    @Override
    public short getFixedFacets() {
        if (this.fValidationDV == 24) {
            return (short)(this.fFixedFacet | 0x400);
        }
        return this.fFixedFacet;
    }
    
    @Override
    public String getLexicalFacetValue(final short facetName) {
        switch (facetName) {
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
    
    @Override
    public StringList getLexicalEnumeration() {
        if (this.fLexicalEnumeration == null) {
            if (this.fEnumeration == null) {
                return StringListImpl.EMPTY_LIST;
            }
            final int size = this.fEnumeration.size();
            final String[] strs = new String[size];
            for (int i = 0; i < size; ++i) {
                strs[i] = this.fEnumeration.elementAt(i).toString();
            }
            this.fLexicalEnumeration = new StringListImpl(strs, size);
        }
        return this.fLexicalEnumeration;
    }
    
    public ObjectList getActualEnumeration() {
        if (this.fActualEnumeration == null) {
            this.fActualEnumeration = new AbstractObjectList() {
                @Override
                public int getLength() {
                    return (XSSimpleTypeDecl.this.fEnumeration != null) ? XSSimpleTypeDecl.this.fEnumeration.size() : 0;
                }
                
                @Override
                public boolean contains(final Object item) {
                    return XSSimpleTypeDecl.this.fEnumeration != null && XSSimpleTypeDecl.this.fEnumeration.contains(item);
                }
                
                @Override
                public Object item(final int index) {
                    if (index < 0 || index >= this.getLength()) {
                        return null;
                    }
                    return XSSimpleTypeDecl.this.fEnumeration.elementAt(index);
                }
            };
        }
        return this.fActualEnumeration;
    }
    
    public ObjectList getEnumerationItemTypeList() {
        if (this.fEnumerationItemTypeList == null) {
            if (this.fEnumerationItemType == null) {
                return null;
            }
            this.fEnumerationItemTypeList = new AbstractObjectList() {
                @Override
                public int getLength() {
                    return (XSSimpleTypeDecl.this.fEnumerationItemType != null) ? XSSimpleTypeDecl.this.fEnumerationItemType.length : 0;
                }
                
                @Override
                public boolean contains(final Object item) {
                    if (XSSimpleTypeDecl.this.fEnumerationItemType == null || !(item instanceof ShortList)) {
                        return false;
                    }
                    for (int i = 0; i < XSSimpleTypeDecl.this.fEnumerationItemType.length; ++i) {
                        if (XSSimpleTypeDecl.this.fEnumerationItemType[i] == item) {
                            return true;
                        }
                    }
                    return false;
                }
                
                @Override
                public Object item(final int index) {
                    if (index < 0 || index >= this.getLength()) {
                        return null;
                    }
                    return XSSimpleTypeDecl.this.fEnumerationItemType[index];
                }
            };
        }
        return this.fEnumerationItemTypeList;
    }
    
    public ShortList getEnumerationTypeList() {
        if (this.fEnumerationTypeList == null) {
            if (this.fEnumerationType == null) {
                return ShortListImpl.EMPTY_LIST;
            }
            this.fEnumerationTypeList = new ShortListImpl(this.fEnumerationType, this.fEnumerationType.length);
        }
        return this.fEnumerationTypeList;
    }
    
    @Override
    public StringList getLexicalPattern() {
        if (this.fPatternType == 0 && this.fValidationDV != 24 && this.fPatternStr == null) {
            return StringListImpl.EMPTY_LIST;
        }
        if (this.fLexicalPattern == null) {
            final int size = (this.fPatternStr == null) ? 0 : this.fPatternStr.size();
            String[] strs;
            if (this.fPatternType == 1) {
                strs = new String[size + 1];
                strs[size] = "\\c+";
            }
            else if (this.fPatternType == 2) {
                strs = new String[size + 1];
                strs[size] = "\\i\\c*";
            }
            else if (this.fPatternType == 3) {
                strs = new String[size + 2];
                strs[size] = "\\i\\c*";
                strs[size + 1] = "[\\i-[:]][\\c-[:]]*";
            }
            else if (this.fValidationDV == 24) {
                strs = new String[size + 1];
                strs[size] = "[\\-+]?[0-9]+";
            }
            else {
                strs = new String[size];
            }
            for (int i = 0; i < size; ++i) {
                strs[i] = this.fPatternStr.elementAt(i);
            }
            this.fLexicalPattern = new StringListImpl(strs, strs.length);
        }
        return this.fLexicalPattern;
    }
    
    @Override
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
            final int length = this.fMemberTypes.length;
            if (length == 0) {
                this.fOrdered = 1;
                return;
            }
            final short ancestorId = this.getPrimitiveDV(this.fMemberTypes[0].fValidationDV);
            boolean commonAnc = ancestorId != 0;
            boolean allFalse = this.fMemberTypes[0].fOrdered == 0;
            for (int i = 1; i < this.fMemberTypes.length && (commonAnc || allFalse); ++i) {
                if (commonAnc) {
                    commonAnc = (ancestorId == this.getPrimitiveDV(this.fMemberTypes[i].fValidationDV));
                }
                if (allFalse) {
                    allFalse = (this.fMemberTypes[i].fOrdered == 0);
                }
            }
            if (commonAnc) {
                this.fOrdered = this.fMemberTypes[0].fOrdered;
            }
            else if (allFalse) {
                this.fOrdered = 0;
            }
            else {
                this.fOrdered = 1;
            }
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
            final XSSimpleType[] memberTypes = this.fMemberTypes;
            for (int i = 0; i < memberTypes.length; ++i) {
                if (!memberTypes[i].getNumeric()) {
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
            final XSSimpleTypeDecl[] memberTypes = this.fMemberTypes;
            short ancestorId = 0;
            if (memberTypes.length > 0) {
                ancestorId = this.getPrimitiveDV(memberTypes[0].fValidationDV);
            }
            for (int i = 0; i < memberTypes.length; ++i) {
                if (!memberTypes[i].getBounded() || ancestorId != this.getPrimitiveDV(memberTypes[i].fValidationDV)) {
                    this.fBounded = false;
                    return;
                }
            }
            this.fBounded = true;
        }
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
            final XSSimpleType[] memberTypes = this.fMemberTypes;
            for (int i = 0; i < memberTypes.length; ++i) {
                if (!memberTypes[i].getFinite()) {
                    this.fFinite = false;
                    return;
                }
            }
            this.fFinite = true;
        }
    }
    
    private short getPrimitiveDV(final short validationDV) {
        if (validationDV == 21 || validationDV == 22 || validationDV == 23) {
            return 1;
        }
        if (validationDV == 24) {
            return 3;
        }
        return validationDV;
    }
    
    @Override
    public boolean derivedFromType(XSTypeDefinition ancestor, final short derivation) {
        if (ancestor == null) {
            return false;
        }
        while (ancestor instanceof XSSimpleTypeDelegate) {
            ancestor = ((XSSimpleTypeDelegate)ancestor).type;
        }
        if (ancestor.getBaseType() == ancestor) {
            return true;
        }
        XSTypeDefinition type;
        for (type = this; type != ancestor && type != XSSimpleTypeDecl.fAnySimpleType; type = type.getBaseType()) {}
        return type == ancestor;
    }
    
    @Override
    public boolean derivedFrom(final String ancestorNS, final String ancestorName, final short derivation) {
        if (ancestorName == null) {
            return false;
        }
        if ("http://www.w3.org/2001/XMLSchema".equals(ancestorNS) && "anyType".equals(ancestorName)) {
            return true;
        }
        XSTypeDefinition type;
        for (type = this; (!ancestorName.equals(type.getName()) || ((ancestorNS != null || type.getNamespace() != null) && (ancestorNS == null || !ancestorNS.equals(type.getNamespace())))) && type != XSSimpleTypeDecl.fAnySimpleType; type = type.getBaseType()) {}
        return type != XSSimpleTypeDecl.fAnySimpleType;
    }
    
    public boolean isDOMDerivedFrom(final String ancestorNS, final String ancestorName, final int derivationMethod) {
        return ancestorName != null && ((SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(ancestorNS) && "anyType".equals(ancestorName) && ((derivationMethod & 0x1) != 0x0 || derivationMethod == 0)) || ((derivationMethod & 0x1) != 0x0 && this.isDerivedByRestriction(ancestorNS, ancestorName, this)) || ((derivationMethod & 0x8) != 0x0 && this.isDerivedByList(ancestorNS, ancestorName, this)) || ((derivationMethod & 0x4) != 0x0 && this.isDerivedByUnion(ancestorNS, ancestorName, this)) || (((derivationMethod & 0x2) == 0x0 || (derivationMethod & 0x1) != 0x0 || (derivationMethod & 0x8) != 0x0 || (derivationMethod & 0x4) != 0x0) && ((derivationMethod & 0x2) == 0x0 && (derivationMethod & 0x1) == 0x0 && (derivationMethod & 0x8) == 0x0 && (derivationMethod & 0x4) == 0x0) && this.isDerivedByAny(ancestorNS, ancestorName, this)));
    }
    
    private boolean isDerivedByAny(final String ancestorNS, final String ancestorName, XSTypeDefinition type) {
        boolean derivedFrom = false;
        XSTypeDefinition oldType = null;
        while (type != null && type != oldType) {
            if (ancestorName.equals(type.getName()) && ((ancestorNS == null && type.getNamespace() == null) || (ancestorNS != null && ancestorNS.equals(type.getNamespace())))) {
                derivedFrom = true;
                break;
            }
            if (this.isDerivedByRestriction(ancestorNS, ancestorName, type)) {
                return true;
            }
            if (this.isDerivedByList(ancestorNS, ancestorName, type)) {
                return true;
            }
            if (this.isDerivedByUnion(ancestorNS, ancestorName, type)) {
                return true;
            }
            oldType = type;
            if (((XSSimpleTypeDecl)type).getVariety() == 0 || ((XSSimpleTypeDecl)type).getVariety() == 1) {
                type = type.getBaseType();
            }
            else if (((XSSimpleTypeDecl)type).getVariety() == 3) {
                final int i = 0;
                if (i < ((XSSimpleTypeDecl)type).getMemberTypes().getLength()) {
                    return this.isDerivedByAny(ancestorNS, ancestorName, (XSTypeDefinition)((XSSimpleTypeDecl)type).getMemberTypes().item(i));
                }
                continue;
            }
            else {
                if (((XSSimpleTypeDecl)type).getVariety() != 2) {
                    continue;
                }
                type = ((XSSimpleTypeDecl)type).getItemType();
            }
        }
        return derivedFrom;
    }
    
    private boolean isDerivedByRestriction(final String ancestorNS, final String ancestorName, XSTypeDefinition type) {
        for (XSTypeDefinition oldType = null; type != null && type != oldType; oldType = type, type = type.getBaseType()) {
            if (ancestorName.equals(type.getName()) && ((ancestorNS != null && ancestorNS.equals(type.getNamespace())) || (type.getNamespace() == null && ancestorNS == null))) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isDerivedByList(final String ancestorNS, final String ancestorName, final XSTypeDefinition type) {
        if (type != null && ((XSSimpleTypeDefinition)type).getVariety() == 2) {
            final XSTypeDefinition itemType = ((XSSimpleTypeDefinition)type).getItemType();
            if (itemType != null && this.isDerivedByRestriction(ancestorNS, ancestorName, itemType)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isDerivedByUnion(final String ancestorNS, final String ancestorName, final XSTypeDefinition type) {
        if (type != null && ((XSSimpleTypeDefinition)type).getVariety() == 3) {
            final XSObjectList memberTypes = ((XSSimpleTypeDefinition)type).getMemberTypes();
            for (int i = 0; i < memberTypes.getLength(); ++i) {
                if (memberTypes.item(i) != null && this.isDerivedByRestriction(ancestorNS, ancestorName, (XSTypeDefinition)memberTypes.item(i))) {
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
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fPattern = null;
        this.fPatternStr = null;
        this.fEnumeration = null;
        this.fEnumerationType = null;
        this.fEnumerationItemType = null;
        this.fLexicalPattern = null;
        this.fLexicalEnumeration = null;
        this.fMaxInclusive = null;
        this.fMaxExclusive = null;
        this.fMinExclusive = null;
        this.fMinInclusive = null;
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
        this.fPatternType = 0;
        this.fAnnotations = null;
        this.fFacets = null;
    }
    
    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }
    
    public void setNamespaceItem(final XSNamespaceItem namespaceItem) {
        this.fNamespaceItem = namespaceItem;
    }
    
    @Override
    public String toString() {
        return this.fTargetNamespace + "," + this.fTypeName;
    }
    
    @Override
    public XSObjectList getFacets() {
        if (this.fFacets == null && (this.fFacetsDefined != 0 || this.fValidationDV == 24)) {
            final XSFacetImpl[] facets = new XSFacetImpl[10];
            int count = 0;
            if ((this.fFacetsDefined & 0x10) != 0x0 && this.fValidationDV != 0 && this.fValidationDV != 29) {
                facets[count] = new XSFacetImpl((short)16, XSSimpleTypeDecl.WS_FACET_STRING[this.fWhiteSpace], (this.fFixedFacet & 0x10) != 0x0, this.whiteSpaceAnnotation);
                ++count;
            }
            if (this.fLength != -1) {
                facets[count] = new XSFacetImpl((short)1, Integer.toString(this.fLength), (this.fFixedFacet & 0x1) != 0x0, this.lengthAnnotation);
                ++count;
            }
            if (this.fMinLength != -1) {
                facets[count] = new XSFacetImpl((short)2, Integer.toString(this.fMinLength), (this.fFixedFacet & 0x2) != 0x0, this.minLengthAnnotation);
                ++count;
            }
            if (this.fMaxLength != -1) {
                facets[count] = new XSFacetImpl((short)4, Integer.toString(this.fMaxLength), (this.fFixedFacet & 0x4) != 0x0, this.maxLengthAnnotation);
                ++count;
            }
            if (this.fTotalDigits != -1) {
                facets[count] = new XSFacetImpl((short)512, Integer.toString(this.fTotalDigits), (this.fFixedFacet & 0x200) != 0x0, this.totalDigitsAnnotation);
                ++count;
            }
            if (this.fValidationDV == 24) {
                facets[count] = new XSFacetImpl((short)1024, "0", true, this.fractionDigitsAnnotation);
                ++count;
            }
            else if (this.fFractionDigits != -1) {
                facets[count] = new XSFacetImpl((short)1024, Integer.toString(this.fFractionDigits), (this.fFixedFacet & 0x400) != 0x0, this.fractionDigitsAnnotation);
                ++count;
            }
            if (this.fMaxInclusive != null) {
                facets[count] = new XSFacetImpl((short)32, this.fMaxInclusive.toString(), (this.fFixedFacet & 0x20) != 0x0, this.maxInclusiveAnnotation);
                ++count;
            }
            if (this.fMaxExclusive != null) {
                facets[count] = new XSFacetImpl((short)64, this.fMaxExclusive.toString(), (this.fFixedFacet & 0x40) != 0x0, this.maxExclusiveAnnotation);
                ++count;
            }
            if (this.fMinExclusive != null) {
                facets[count] = new XSFacetImpl((short)128, this.fMinExclusive.toString(), (this.fFixedFacet & 0x80) != 0x0, this.minExclusiveAnnotation);
                ++count;
            }
            if (this.fMinInclusive != null) {
                facets[count] = new XSFacetImpl((short)256, this.fMinInclusive.toString(), (this.fFixedFacet & 0x100) != 0x0, this.minInclusiveAnnotation);
                ++count;
            }
            this.fFacets = ((count > 0) ? new XSObjectListImpl(facets, count) : XSObjectListImpl.EMPTY_LIST);
        }
        return (this.fFacets != null) ? this.fFacets : XSObjectListImpl.EMPTY_LIST;
    }
    
    @Override
    public XSObjectList getMultiValueFacets() {
        if (this.fMultiValueFacets == null && ((this.fFacetsDefined & 0x800) != 0x0 || (this.fFacetsDefined & 0x8) != 0x0 || this.fPatternType != 0 || this.fValidationDV == 24)) {
            final XSMVFacetImpl[] facets = new XSMVFacetImpl[2];
            int count = 0;
            if ((this.fFacetsDefined & 0x8) != 0x0 || this.fPatternType != 0 || this.fValidationDV == 24) {
                facets[count] = new XSMVFacetImpl((short)8, this.getLexicalPattern(), this.patternAnnotations);
                ++count;
            }
            if (this.fEnumeration != null) {
                facets[count] = new XSMVFacetImpl((short)2048, this.getLexicalEnumeration(), this.enumerationAnnotations);
                ++count;
            }
            this.fMultiValueFacets = new XSObjectListImpl(facets, count);
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
    
    public void setAnonymous(final boolean anon) {
        this.fAnonymous = anon;
    }
    
    @Override
    public String getTypeNamespace() {
        return this.getNamespace();
    }
    
    @Override
    public boolean isDerivedFrom(final String typeNamespaceArg, final String typeNameArg, final int derivationMethod) {
        return this.isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
    }
    
    private short convertToPrimitiveKind(final short valueType) {
        if (valueType <= 20) {
            return valueType;
        }
        if (valueType <= 29) {
            return 2;
        }
        if (valueType <= 42) {
            return 4;
        }
        return valueType;
    }
    
    static {
        gDVs = new TypeValidator[] { new AnySimpleDV(), new StringDV(), new BooleanDV(), new DecimalDV(), new FloatDV(), new DoubleDV(), new DurationDV(), new DateTimeDV(), new TimeDV(), new DateDV(), new YearMonthDV(), new YearDV(), new MonthDayDV(), new DayDV(), new MonthDV(), new HexBinaryDV(), new Base64BinaryDV(), new AnyURIDV(), new QNameDV(), new PrecisionDecimalDV(), new QNameDV(), new IDDV(), new IDREFDV(), new EntityDV(), new IntegerDV(), new ListDV(), new UnionDV(), new YearMonthDurationDV(), new DayTimeDurationDV(), new AnyAtomicDV() };
        fDVNormalizeType = new short[] { 0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 0 };
        SPECIAL_PATTERN_STRING = new String[] { "NONE", "NMTOKEN", "Name", "NCName" };
        WS_FACET_STRING = new String[] { "preserve", "replace", "collapse" };
        fEmptyContext = new ValidationContext() {
            @Override
            public boolean needFacetChecking() {
                return true;
            }
            
            @Override
            public boolean needExtraChecking() {
                return false;
            }
            
            @Override
            public boolean needToNormalize() {
                return true;
            }
            
            @Override
            public boolean useNamespaces() {
                return true;
            }
            
            @Override
            public boolean isEntityDeclared(final String name) {
                return false;
            }
            
            @Override
            public boolean isEntityUnparsed(final String name) {
                return false;
            }
            
            @Override
            public boolean isIdDeclared(final String name) {
                return false;
            }
            
            @Override
            public void addId(final String name) {
            }
            
            @Override
            public void addIdRef(final String name) {
            }
            
            @Override
            public String getSymbol(final String symbol) {
                return symbol.intern();
            }
            
            @Override
            public String getURI(final String prefix) {
                return null;
            }
            
            @Override
            public Locale getLocale() {
                return Locale.getDefault();
            }
        };
        fAnySimpleType = new XSSimpleTypeDecl(null, "anySimpleType", (short)0, (short)0, false, true, false, true, (short)1);
        fAnyAtomicType = new XSSimpleTypeDecl(XSSimpleTypeDecl.fAnySimpleType, "anyAtomicType", (short)29, (short)0, false, true, false, true, (short)49);
        fDummyContext = new ValidationContext() {
            @Override
            public boolean needFacetChecking() {
                return true;
            }
            
            @Override
            public boolean needExtraChecking() {
                return false;
            }
            
            @Override
            public boolean needToNormalize() {
                return false;
            }
            
            @Override
            public boolean useNamespaces() {
                return true;
            }
            
            @Override
            public boolean isEntityDeclared(final String name) {
                return false;
            }
            
            @Override
            public boolean isEntityUnparsed(final String name) {
                return false;
            }
            
            @Override
            public boolean isIdDeclared(final String name) {
                return false;
            }
            
            @Override
            public void addId(final String name) {
            }
            
            @Override
            public void addIdRef(final String name) {
            }
            
            @Override
            public String getSymbol(final String symbol) {
                return symbol.intern();
            }
            
            @Override
            public String getURI(final String prefix) {
                return null;
            }
            
            @Override
            public Locale getLocale() {
                return Locale.getDefault();
            }
        };
    }
    
    static final class ValidationContextImpl implements ValidationContext
    {
        final ValidationContext fExternal;
        NamespaceContext fNSContext;
        
        ValidationContextImpl(final ValidationContext external) {
            this.fExternal = external;
        }
        
        void setNSContext(final NamespaceContext nsContext) {
            this.fNSContext = nsContext;
        }
        
        @Override
        public boolean needFacetChecking() {
            return this.fExternal.needFacetChecking();
        }
        
        @Override
        public boolean needExtraChecking() {
            return this.fExternal.needExtraChecking();
        }
        
        @Override
        public boolean needToNormalize() {
            return this.fExternal.needToNormalize();
        }
        
        @Override
        public boolean useNamespaces() {
            return true;
        }
        
        @Override
        public boolean isEntityDeclared(final String name) {
            return this.fExternal.isEntityDeclared(name);
        }
        
        @Override
        public boolean isEntityUnparsed(final String name) {
            return this.fExternal.isEntityUnparsed(name);
        }
        
        @Override
        public boolean isIdDeclared(final String name) {
            return this.fExternal.isIdDeclared(name);
        }
        
        @Override
        public void addId(final String name) {
            this.fExternal.addId(name);
        }
        
        @Override
        public void addIdRef(final String name) {
            this.fExternal.addIdRef(name);
        }
        
        @Override
        public String getSymbol(final String symbol) {
            return this.fExternal.getSymbol(symbol);
        }
        
        @Override
        public String getURI(final String prefix) {
            if (this.fNSContext == null) {
                return this.fExternal.getURI(prefix);
            }
            return this.fNSContext.getURI(prefix);
        }
        
        @Override
        public Locale getLocale() {
            return this.fExternal.getLocale();
        }
    }
    
    private static final class XSFacetImpl implements XSFacet
    {
        final short kind;
        final String value;
        final boolean fixed;
        final XSObjectList annotations;
        
        public XSFacetImpl(final short kind, final String value, final boolean fixed, final XSAnnotation annotation) {
            this.kind = kind;
            this.value = value;
            this.fixed = fixed;
            if (annotation != null) {
                this.annotations = new XSObjectListImpl();
                ((XSObjectListImpl)this.annotations).addXSObject(annotation);
            }
            else {
                this.annotations = XSObjectListImpl.EMPTY_LIST;
            }
        }
        
        @Override
        public XSAnnotation getAnnotation() {
            return (XSAnnotation)this.annotations.item(0);
        }
        
        @Override
        public XSObjectList getAnnotations() {
            return this.annotations;
        }
        
        @Override
        public short getFacetKind() {
            return this.kind;
        }
        
        @Override
        public String getLexicalFacetValue() {
            return this.value;
        }
        
        @Override
        public boolean getFixed() {
            return this.fixed;
        }
        
        @Override
        public String getName() {
            return null;
        }
        
        @Override
        public String getNamespace() {
            return null;
        }
        
        @Override
        public XSNamespaceItem getNamespaceItem() {
            return null;
        }
        
        @Override
        public short getType() {
            return 13;
        }
    }
    
    private static final class XSMVFacetImpl implements XSMultiValueFacet
    {
        final short kind;
        final XSObjectList annotations;
        final StringList values;
        
        public XSMVFacetImpl(final short kind, final StringList values, final XSObjectList annotations) {
            this.kind = kind;
            this.values = values;
            this.annotations = ((annotations != null) ? annotations : XSObjectListImpl.EMPTY_LIST);
        }
        
        @Override
        public short getFacetKind() {
            return this.kind;
        }
        
        @Override
        public XSObjectList getAnnotations() {
            return this.annotations;
        }
        
        @Override
        public StringList getLexicalFacetValues() {
            return this.values;
        }
        
        @Override
        public String getName() {
            return null;
        }
        
        @Override
        public String getNamespace() {
            return null;
        }
        
        @Override
        public XSNamespaceItem getNamespaceItem() {
            return null;
        }
        
        @Override
        public short getType() {
            return 14;
        }
    }
    
    private abstract static class AbstractObjectList extends AbstractList implements ObjectList
    {
        @Override
        public Object get(final int index) {
            if (index >= 0 && index < this.getLength()) {
                return this.item(index);
            }
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        
        @Override
        public int size() {
            return this.getLength();
        }
    }
}
