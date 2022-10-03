package org.apache.xmlbeans.impl.schema;

import java.util.HashMap;
import java.math.BigInteger;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlShort;
import org.apache.xmlbeans.XmlByte;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.XmlPositiveInteger;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.regex.ParseException;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.XmlAnySimpleType;
import java.util.Arrays;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionDocument;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.xb.xsdschema.UnionDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;
import java.util.Iterator;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleType;
import java.util.Map;
import org.apache.xmlbeans.impl.regex.RegularExpression;

public class StscSimpleTypeResolver
{
    private static final RegularExpression[] EMPTY_REGEX_ARRAY;
    private static CodeForNameEntry[] facetCodes;
    private static final Map facetCodeMap;
    
    public static void resolveSimpleType(final SchemaTypeImpl sImpl) {
        final SimpleType parseSt = (SimpleType)sImpl.getParseObject();
        assert sImpl.isSimpleType();
        final SchemaDocument.Schema schema = StscComplexTypeResolver.getSchema(parseSt);
        final int count = (parseSt.isSetList() + parseSt.isSetUnion() + parseSt.isSetRestriction()) ? 1 : 0;
        if (count > 1) {
            StscState.get().error("A simple type must define either a list, a union, or a restriction: more than one found.", 52, parseSt);
        }
        else if (count < 1) {
            StscState.get().error("A simple type must define either a list, a union, or a restriction: none was found.", 52, parseSt);
            resolveErrorSimpleType(sImpl);
            return;
        }
        boolean finalRest = false;
        boolean finalList = false;
        boolean finalUnion = false;
        Object finalValue = null;
        if (parseSt.isSetFinal()) {
            finalValue = parseSt.getFinal();
        }
        else if (schema != null && schema.isSetFinalDefault()) {
            finalValue = schema.getFinalDefault();
        }
        if (finalValue != null) {
            if (finalValue instanceof String) {
                if ("#all".equals(finalValue)) {
                    finalList = (finalRest = (finalUnion = true));
                }
            }
            else if (finalValue instanceof List) {
                final List lFinalValue = (List)finalValue;
                if (lFinalValue.contains("restriction")) {
                    finalRest = true;
                }
                if (lFinalValue.contains("list")) {
                    finalList = true;
                }
                if (lFinalValue.contains("union")) {
                    finalUnion = true;
                }
            }
        }
        sImpl.setSimpleFinal(finalRest, finalList, finalUnion);
        final List anonTypes = new ArrayList();
        if (parseSt.getList() != null) {
            resolveListType(sImpl, parseSt.getList(), anonTypes);
        }
        else if (parseSt.getUnion() != null) {
            resolveUnionType(sImpl, parseSt.getUnion(), anonTypes);
        }
        else if (parseSt.getRestriction() != null) {
            resolveSimpleRestrictionType(sImpl, parseSt.getRestriction(), anonTypes);
        }
        sImpl.setAnonymousTypeRefs(makeRefArray(anonTypes));
    }
    
    private static SchemaType.Ref[] makeRefArray(final Collection typeList) {
        final SchemaType.Ref[] result = new SchemaType.Ref[typeList.size()];
        int j = 0;
        final Iterator i = typeList.iterator();
        while (i.hasNext()) {
            result[j] = i.next().getRef();
            ++j;
        }
        return result;
    }
    
    static void resolveErrorSimpleType(final SchemaTypeImpl sImpl) {
        sImpl.setSimpleTypeVariety(1);
        sImpl.setBaseTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
        sImpl.setBaseDepth(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getBaseDepth() + 1);
        sImpl.setPrimitiveTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
    }
    
    static void resolveListType(SchemaTypeImpl sImpl, final ListDocument.List parseList, final List anonTypes) {
        final StscState state = StscState.get();
        sImpl.setSimpleTypeVariety(3);
        sImpl.setBaseTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
        sImpl.setBaseDepth(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getBaseDepth() + 1);
        sImpl.setDerivationType(1);
        if (sImpl.isRedefinition()) {
            state.error("src-redefine.5a", new Object[] { "list" }, parseList);
        }
        final QName itemName = parseList.getItemType();
        LocalSimpleType parseInner = parseList.getSimpleType();
        if (itemName != null && parseInner != null) {
            state.error("src-simple-type.3a", null, parseList);
            parseInner = null;
        }
        SchemaTypeImpl itemImpl;
        XmlObject errorLoc;
        if (itemName != null) {
            itemImpl = state.findGlobalType(itemName, sImpl.getChameleonNamespace(), sImpl.getTargetNamespace());
            errorLoc = parseList.xgetItemType();
            if (itemImpl == null) {
                state.notFoundError(itemName, 0, parseList.xgetItemType(), true);
                itemImpl = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
            }
        }
        else {
            if (parseInner == null) {
                state.error("src-simple-type.3b", null, parseList);
                resolveErrorSimpleType(sImpl);
                return;
            }
            itemImpl = StscTranslator.translateAnonymousSimpleType(parseInner, sImpl.getTargetNamespace(), sImpl.getChameleonNamespace() != null, sImpl.getElemFormDefault(), sImpl.getAttFormDefault(), anonTypes, sImpl);
            errorLoc = parseInner;
        }
        if (itemImpl.finalList()) {
            state.error("st-props-correct.4.2.1", null, parseList);
        }
        StscResolver.resolveType(itemImpl);
        if (!itemImpl.isSimpleType()) {
            state.error("cos-st-restricts.2.1a", null, errorLoc);
            sImpl = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        switch (itemImpl.getSimpleVariety()) {
            case 3: {
                state.error("cos-st-restricts.2.1b", null, errorLoc);
                resolveErrorSimpleType(sImpl);
                return;
            }
            case 2: {
                if (itemImpl.isUnionOfLists()) {
                    state.error("cos-st-restricts.2.1c", null, errorLoc);
                    resolveErrorSimpleType(sImpl);
                    return;
                }
            }
            case 1: {
                sImpl.setListItemTypeRef(itemImpl.getRef());
                if (sImpl.getBuiltinTypeCode() == 8) {
                    state.recover("enumeration-required-notation", null, errorLoc);
                    break;
                }
                break;
            }
            default: {
                assert false;
                sImpl.setListItemTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
                break;
            }
        }
        sImpl.setBasicFacets(StscState.FACETS_LIST, StscState.FIXED_FACETS_LIST);
        sImpl.setWhiteSpaceRule(3);
        resolveFundamentalFacets(sImpl);
    }
    
    static void resolveUnionType(final SchemaTypeImpl sImpl, final UnionDocument.Union parseUnion, final List anonTypes) {
        sImpl.setSimpleTypeVariety(2);
        sImpl.setBaseTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
        sImpl.setBaseDepth(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getBaseDepth() + 1);
        sImpl.setDerivationType(1);
        final StscState state = StscState.get();
        if (sImpl.isRedefinition()) {
            state.error("src-redefine.5a", new Object[] { "union" }, parseUnion);
        }
        final List memberTypes = parseUnion.getMemberTypes();
        final SimpleType[] simpleTypes = parseUnion.getSimpleTypeArray();
        final List memberImplList = new ArrayList();
        if (simpleTypes.length == 0 && (memberTypes == null || memberTypes.size() == 0)) {
            state.error("src-union-memberTypes-or-simpleTypes", null, parseUnion);
        }
        if (memberTypes != null) {
            for (final QName mName : memberTypes) {
                final SchemaTypeImpl memberImpl = state.findGlobalType(mName, sImpl.getChameleonNamespace(), sImpl.getTargetNamespace());
                if (memberImpl == null) {
                    state.notFoundError(mName, 0, parseUnion.xgetMemberTypes(), true);
                }
                else {
                    memberImplList.add(memberImpl);
                }
            }
        }
        for (int i = 0; i < simpleTypes.length; ++i) {
            final SchemaTypeImpl mImpl = StscTranslator.translateAnonymousSimpleType(simpleTypes[i], sImpl.getTargetNamespace(), sImpl.getChameleonNamespace() != null, sImpl.getElemFormDefault(), sImpl.getAttFormDefault(), anonTypes, sImpl);
            memberImplList.add(mImpl);
            mImpl.setAnonymousUnionMemberOrdinal(i + 1);
        }
        final Iterator mImpls = memberImplList.iterator();
        while (mImpls.hasNext()) {
            final SchemaTypeImpl mImpl = mImpls.next();
            if (!StscResolver.resolveType(mImpl)) {
                String memberName = "";
                XmlObject errorLoc;
                if (mImpl.getOuterType().equals(sImpl)) {
                    errorLoc = mImpl.getParseObject();
                }
                else {
                    memberName = QNameHelper.pretty(mImpl.getName()) + " ";
                    errorLoc = parseUnion.xgetMemberTypes();
                }
                state.error("src-simple-type.4", new Object[] { memberName }, errorLoc);
                mImpls.remove();
            }
        }
        boolean isUnionOfLists = false;
        final Iterator mImpls2 = memberImplList.iterator();
        while (mImpls2.hasNext()) {
            final SchemaTypeImpl mImpl2 = mImpls2.next();
            if (!mImpl2.isSimpleType()) {
                String memberName2 = "";
                XmlObject errorLoc2;
                if (mImpl2.getOuterType() != null && mImpl2.getOuterType().equals(sImpl)) {
                    errorLoc2 = mImpl2.getParseObject();
                }
                else {
                    memberName2 = QNameHelper.pretty(mImpl2.getName()) + " ";
                    errorLoc2 = parseUnion.xgetMemberTypes();
                }
                state.error("cos-st-restricts.3.1", new Object[] { memberName2 }, errorLoc2);
                mImpls2.remove();
            }
            else {
                if (mImpl2.getSimpleVariety() != 3 && (mImpl2.getSimpleVariety() != 2 || !mImpl2.isUnionOfLists())) {
                    continue;
                }
                isUnionOfLists = true;
            }
        }
        for (int j = 0; j < memberImplList.size(); ++j) {
            final SchemaTypeImpl mImpl2 = memberImplList.get(j);
            if (mImpl2.finalUnion()) {
                state.error("st-props-correct.4.2.2", null, parseUnion);
            }
        }
        sImpl.setUnionOfLists(isUnionOfLists);
        sImpl.setUnionMemberTypeRefs(makeRefArray(memberImplList));
        sImpl.setBasicFacets(StscState.FACETS_UNION, StscState.FIXED_FACETS_UNION);
        resolveFundamentalFacets(sImpl);
    }
    
    static void resolveSimpleRestrictionType(final SchemaTypeImpl sImpl, final RestrictionDocument.Restriction parseRestr, final List anonTypes) {
        final QName baseName = parseRestr.getBase();
        SimpleType parseInner = parseRestr.getSimpleType();
        final StscState state = StscState.get();
        if (baseName != null && parseInner != null) {
            state.error("src-simple-type.2a", null, parseRestr);
            parseInner = null;
        }
        SchemaTypeImpl baseImpl;
        if (baseName != null) {
            if (sImpl.isRedefinition()) {
                baseImpl = state.findRedefinedGlobalType(parseRestr.getBase(), sImpl.getChameleonNamespace(), sImpl);
                if (baseImpl != null && !baseImpl.getName().equals(sImpl.getName())) {
                    state.error("src-redefine.5b", new Object[] { "<simpleType>", QNameHelper.pretty(baseName), QNameHelper.pretty(sImpl.getName()) }, parseRestr);
                }
            }
            else {
                baseImpl = state.findGlobalType(baseName, sImpl.getChameleonNamespace(), sImpl.getTargetNamespace());
            }
            if (baseImpl == null) {
                state.notFoundError(baseName, 0, parseRestr.xgetBase(), true);
                baseImpl = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
            }
        }
        else if (parseInner != null) {
            if (sImpl.isRedefinition()) {
                StscState.get().error("src-redefine.5a", new Object[] { "<simpleType>" }, parseInner);
            }
            baseImpl = StscTranslator.translateAnonymousSimpleType(parseInner, sImpl.getTargetNamespace(), sImpl.getChameleonNamespace() != null, sImpl.getElemFormDefault(), sImpl.getAttFormDefault(), anonTypes, sImpl);
        }
        else {
            state.error("src-simple-type.2b", null, parseRestr);
            baseImpl = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        if (!StscResolver.resolveType(baseImpl)) {
            baseImpl = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        if (baseImpl.finalRestriction()) {
            state.error("st-props-correct.3", null, parseRestr);
        }
        sImpl.setBaseTypeRef(baseImpl.getRef());
        sImpl.setBaseDepth(baseImpl.getBaseDepth() + 1);
        sImpl.setDerivationType(1);
        if (!baseImpl.isSimpleType()) {
            state.error("cos-st-restricts.1.1", null, parseRestr.xgetBase());
            resolveErrorSimpleType(sImpl);
            return;
        }
        sImpl.setSimpleTypeVariety(baseImpl.getSimpleVariety());
        switch (baseImpl.getSimpleVariety()) {
            case 1: {
                sImpl.setPrimitiveTypeRef(baseImpl.getPrimitiveType().getRef());
                break;
            }
            case 2: {
                sImpl.setUnionOfLists(baseImpl.isUnionOfLists());
                sImpl.setUnionMemberTypeRefs(makeRefArray(Arrays.asList(baseImpl.getUnionMemberTypes())));
                break;
            }
            case 3: {
                sImpl.setListItemTypeRef(baseImpl.getListItemType().getRef());
                break;
            }
        }
        resolveFacets(sImpl, parseRestr, baseImpl);
        resolveFundamentalFacets(sImpl);
    }
    
    static int translateWhitespaceCode(final XmlAnySimpleType value) {
        final String textval = value.getStringValue();
        if (textval.equals("collapse")) {
            return 3;
        }
        if (textval.equals("preserve")) {
            return 1;
        }
        if (textval.equals("replace")) {
            return 2;
        }
        StscState.get().error("Unrecognized whitespace value \"" + textval + "\"", 20, value);
        return 0;
    }
    
    static boolean isMultipleFacet(final int facetcode) {
        return facetcode == 11 || facetcode == 10;
    }
    
    static boolean facetAppliesToType(final int facetCode, final SchemaTypeImpl baseImpl) {
        switch (baseImpl.getSimpleVariety()) {
            case 3: {
                switch (facetCode) {
                    case 0:
                    case 1:
                    case 2:
                    case 9:
                    case 10:
                    case 11: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            case 2: {
                switch (facetCode) {
                    case 10:
                    case 11: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            default: {
                switch (baseImpl.getPrimitiveType().getBuiltinTypeCode()) {
                    case 2: {
                        return false;
                    }
                    case 3: {
                        switch (facetCode) {
                            case 9:
                            case 10: {
                                return true;
                            }
                            default: {
                                return false;
                            }
                        }
                        break;
                    }
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
                        switch (facetCode) {
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 9:
                            case 10:
                            case 11: {
                                return true;
                            }
                            default: {
                                return false;
                            }
                        }
                        break;
                    }
                    case 11: {
                        switch (facetCode) {
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11: {
                                return true;
                            }
                            default: {
                                return false;
                            }
                        }
                        break;
                    }
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 12: {
                        switch (facetCode) {
                            case 0:
                            case 1:
                            case 2:
                            case 9:
                            case 10:
                            case 11: {
                                return true;
                            }
                            default: {
                                return false;
                            }
                        }
                        break;
                    }
                    default: {
                        assert false;
                        return false;
                    }
                }
                break;
            }
        }
    }
    
    private static int other_similar_limit(final int facetcode) {
        switch (facetcode) {
            case 3: {
                return 4;
            }
            case 4: {
                return 3;
            }
            case 5: {
                return 6;
            }
            case 6: {
                return 5;
            }
            default: {
                assert false;
                throw new IllegalStateException();
            }
        }
    }
    
    static void resolveFacets(final SchemaTypeImpl sImpl, final XmlObject restriction, final SchemaTypeImpl baseImpl) {
        final StscState state = StscState.get();
        final boolean[] seenFacet = new boolean[12];
        final XmlAnySimpleType[] myFacets = baseImpl.getBasicFacets();
        final boolean[] fixedFacets = baseImpl.getFixedFacets();
        int wsr = 0;
        List enumeratedValues = null;
        List patterns = null;
        if (restriction != null) {
            final XmlCursor cur = restriction.newCursor();
            for (boolean more = cur.toFirstChild(); more; more = cur.toNextSibling()) {
                final QName facetQName = cur.getName();
                final String facetName = facetQName.getLocalPart();
                final int code = translateFacetCode(facetQName);
                if (code != -1) {
                    final Facet facet = (Facet)cur.getObject();
                    if (!facetAppliesToType(code, baseImpl)) {
                        state.error("cos-applicable-facets", new Object[] { facetName, QNameHelper.pretty(baseImpl.getName()) }, facet);
                    }
                    else {
                        if (baseImpl.getSimpleVariety() == 1 && baseImpl.getPrimitiveType().getBuiltinTypeCode() == 8 && (code == 0 || code == 1 || code == 2)) {
                            state.warning("notation-facets", new Object[] { facetName, QNameHelper.pretty(baseImpl.getName()) }, facet);
                        }
                        if (seenFacet[code] && !isMultipleFacet(code)) {
                            state.error("src-single-facet-value", null, facet);
                        }
                        else {
                            seenFacet[code] = true;
                            switch (code) {
                                case 0: {
                                    final XmlInteger len = StscTranslator.buildNnInteger(facet.getValue());
                                    if (len == null) {
                                        state.error("Must be a nonnegative integer", 20, facet);
                                        continue;
                                    }
                                    if (fixedFacets[code] && !myFacets[code].valueEquals(len)) {
                                        state.error("facet-fixed", new Object[] { facetName }, facet);
                                        continue;
                                    }
                                    if (myFacets[1] != null) {
                                        final XmlAnySimpleType baseMinLength = baseImpl.getFacet(1);
                                        if (baseMinLength == null || !baseMinLength.valueEquals(myFacets[1]) || baseMinLength.compareValue(len) > 0) {
                                            state.error("length-minLength-maxLength", null, facet);
                                            continue;
                                        }
                                    }
                                    if (myFacets[2] != null) {
                                        final XmlAnySimpleType baseMaxLength = baseImpl.getFacet(2);
                                        if (baseMaxLength == null || !baseMaxLength.valueEquals(myFacets[2]) || baseMaxLength.compareValue(len) < 0) {
                                            state.error("length-minLength-maxLength", null, facet);
                                            continue;
                                        }
                                    }
                                    myFacets[code] = len;
                                    break;
                                }
                                case 1:
                                case 2: {
                                    final XmlInteger mlen = StscTranslator.buildNnInteger(facet.getValue());
                                    if (mlen == null) {
                                        state.error("Must be a nonnegative integer", 20, facet);
                                        continue;
                                    }
                                    if (fixedFacets[code] && !myFacets[code].valueEquals(mlen)) {
                                        state.error("facet-fixed", new Object[] { facetName }, facet);
                                        continue;
                                    }
                                    Label_0655: {
                                        if (myFacets[0] != null) {
                                            final XmlAnySimpleType baseMinMaxLength = baseImpl.getFacet(code);
                                            if (baseMinMaxLength != null && baseMinMaxLength.valueEquals(mlen)) {
                                                if (code == 1) {
                                                    if (baseMinMaxLength.compareTo(myFacets[0]) <= 0) {
                                                        break Label_0655;
                                                    }
                                                }
                                                else if (baseMinMaxLength.compareTo(myFacets[0]) >= 0) {
                                                    break Label_0655;
                                                }
                                            }
                                            state.error("length-minLength-maxLength", null, facet);
                                            continue;
                                        }
                                    }
                                    if (myFacets[2] != null && mlen.compareValue(myFacets[2]) > 0) {
                                        state.error("maxLength-valid-restriction", null, facet);
                                        continue;
                                    }
                                    if (myFacets[1] != null && mlen.compareValue(myFacets[1]) < 0) {
                                        state.error("minLength-valid-restriction", null, facet);
                                        continue;
                                    }
                                    myFacets[code] = mlen;
                                    break;
                                }
                                case 7: {
                                    final XmlPositiveInteger dig = StscTranslator.buildPosInteger(facet.getValue());
                                    if (dig == null) {
                                        state.error("Must be a positive integer", 20, facet);
                                        break;
                                    }
                                    if (fixedFacets[code] && !myFacets[code].valueEquals(dig)) {
                                        state.error("facet-fixed", new Object[] { facetName }, facet);
                                        continue;
                                    }
                                    if (myFacets[7] != null && dig.compareValue(myFacets[7]) > 0) {
                                        state.error("totalDigits-valid-restriction", null, facet);
                                    }
                                    myFacets[code] = dig;
                                    break;
                                }
                                case 8: {
                                    final XmlNonNegativeInteger fdig = StscTranslator.buildNnInteger(facet.getValue());
                                    if (fdig == null) {
                                        state.error("Must be a nonnegative integer", 20, facet);
                                        break;
                                    }
                                    if (fixedFacets[code] && !myFacets[code].valueEquals(fdig)) {
                                        state.error("facet-fixed", new Object[] { facetName }, facet);
                                        continue;
                                    }
                                    if (myFacets[8] != null && fdig.compareValue(myFacets[8]) > 0) {
                                        state.error("fractionDigits-valid-restriction", null, facet);
                                    }
                                    if (myFacets[7] != null && fdig.compareValue(myFacets[7]) > 0) {
                                        state.error("fractionDigits-totalDigits", null, facet);
                                    }
                                    myFacets[code] = fdig;
                                    break;
                                }
                                case 3:
                                case 4:
                                case 5:
                                case 6: {
                                    if (seenFacet[other_similar_limit(code)]) {
                                        state.error("Cannot define both inclusive and exclusive limit in the same restriciton", 19, facet);
                                        continue;
                                    }
                                    final boolean ismin = code == 3 || code == 4;
                                    final boolean isexclusive = code == 3 || code == 6;
                                    XmlAnySimpleType limit;
                                    try {
                                        limit = baseImpl.newValue(facet.getValue(), true);
                                    }
                                    catch (final XmlValueOutOfRangeException e) {
                                        switch (code) {
                                            case 3: {
                                                state.error("minExclusive-valid-restriction", new Object[] { e.getMessage() }, facet);
                                                break;
                                            }
                                            case 4: {
                                                state.error("minInclusive-valid-restriction", new Object[] { e.getMessage() }, facet);
                                                break;
                                            }
                                            case 5: {
                                                state.error("maxInclusive-valid-restriction", new Object[] { e.getMessage() }, facet);
                                                break;
                                            }
                                            case 6: {
                                                state.error("maxExclusive-valid-restriction", new Object[] { e.getMessage() }, facet);
                                                break;
                                            }
                                        }
                                        continue;
                                    }
                                    if (fixedFacets[code] && !myFacets[code].valueEquals(limit)) {
                                        state.error("facet-fixed", new Object[] { facetName }, facet);
                                        continue;
                                    }
                                    if (myFacets[code] != null) {
                                        final SchemaType limitSType = limit.schemaType();
                                        if (limitSType != null && !limitSType.isSimpleType() && limitSType.getContentType() == 2) {
                                            limit = baseImpl.getContentBasedOnType().newValue(facet.getValue());
                                        }
                                        final int comparison = limit.compareValue(myFacets[code]);
                                        if (comparison == 2 || comparison == (ismin ? -1 : 1)) {
                                            state.error(ismin ? (isexclusive ? "Must be greater than or equal to previous minExclusive" : "Must be greater than or equal to previous minInclusive") : (isexclusive ? "Must be less than or equal to previous maxExclusive" : "Must be less than or equal to previous maxInclusive"), 20, facet);
                                            continue;
                                        }
                                    }
                                    myFacets[code] = limit;
                                    myFacets[other_similar_limit(code)] = null;
                                    break;
                                }
                                case 9: {
                                    wsr = translateWhitespaceCode(facet.getValue());
                                    if (baseImpl.getWhiteSpaceRule() > wsr) {
                                        wsr = 0;
                                        state.error("whiteSpace-valid-restriction", null, facet);
                                        continue;
                                    }
                                    myFacets[code] = StscState.build_wsstring(wsr).get();
                                    break;
                                }
                                case 11: {
                                    XmlObject enumval;
                                    try {
                                        enumval = baseImpl.newValue(facet.getValue(), true);
                                    }
                                    catch (final XmlValueOutOfRangeException e2) {
                                        state.error("enumeration-valid-restriction", new Object[] { facet.getValue().getStringValue(), e2.getMessage() }, facet);
                                        continue;
                                    }
                                    if (enumeratedValues == null) {
                                        enumeratedValues = new ArrayList();
                                    }
                                    enumeratedValues.add(enumval);
                                    break;
                                }
                                case 10: {
                                    RegularExpression p;
                                    try {
                                        p = new RegularExpression(facet.getValue().getStringValue(), "X");
                                    }
                                    catch (final ParseException e3) {
                                        state.error("pattern-regex", new Object[] { facet.getValue().getStringValue(), e3.getMessage() }, facet);
                                        continue;
                                    }
                                    if (patterns == null) {
                                        patterns = new ArrayList();
                                    }
                                    patterns.add(p);
                                    break;
                                }
                            }
                            if (facet.getFixed()) {
                                fixedFacets[code] = true;
                            }
                        }
                    }
                }
            }
        }
        sImpl.setBasicFacets(makeValueRefArray(myFacets), fixedFacets);
        if (wsr == 0) {
            wsr = baseImpl.getWhiteSpaceRule();
        }
        sImpl.setWhiteSpaceRule(wsr);
        if (enumeratedValues != null) {
            sImpl.setEnumerationValues(makeValueRefArray(enumeratedValues.toArray(new XmlAnySimpleType[enumeratedValues.size()])));
            SchemaType beType = sImpl;
            if (sImpl.isRedefinition()) {
                beType = sImpl.getBaseType().getBaseEnumType();
                if (beType == null || sImpl.getBaseType() == beType) {
                    beType = sImpl;
                }
            }
            else if (sImpl.getBaseType().getBaseEnumType() != null) {
                beType = sImpl.getBaseType().getBaseEnumType();
            }
            sImpl.setBaseEnumTypeRef(beType.getRef());
        }
        else {
            sImpl.copyEnumerationValues(baseImpl);
        }
        RegularExpression[] patternArray;
        if (patterns != null) {
            patternArray = patterns.toArray(StscSimpleTypeResolver.EMPTY_REGEX_ARRAY);
        }
        else {
            patternArray = StscSimpleTypeResolver.EMPTY_REGEX_ARRAY;
        }
        sImpl.setPatternFacet(patternArray.length > 0 || baseImpl.hasPatternFacet());
        sImpl.setPatterns(patternArray);
        if (baseImpl.getBuiltinTypeCode() == 8 && sImpl.getEnumerationValues() == null) {
            state.recover("enumeration-required-notation", null, restriction);
        }
    }
    
    private static XmlValueRef[] makeValueRefArray(final XmlAnySimpleType[] source) {
        final XmlValueRef[] result = new XmlValueRef[source.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = ((source[i] == null) ? null : new XmlValueRef(source[i]));
        }
        return result;
    }
    
    private static boolean isDiscreteType(final SchemaTypeImpl sImpl) {
        if (sImpl.getFacet(8) != null) {
            return true;
        }
        switch (sImpl.getPrimitiveType().getBuiltinTypeCode()) {
            case 3:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean isNumericPrimitive(final SchemaType sImpl) {
        switch (sImpl.getBuiltinTypeCode()) {
            case 9:
            case 10:
            case 11: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static int decimalSizeOfType(final SchemaTypeImpl sImpl) {
        int size = mathematicalSizeOfType(sImpl);
        if (size == 8 && !XmlByte.type.isAssignableFrom(sImpl)) {
            size = 16;
        }
        if (size == 16 && !XmlShort.type.isAssignableFrom(sImpl) && !XmlUnsignedByte.type.isAssignableFrom(sImpl)) {
            size = 32;
        }
        return size;
    }
    
    private static int mathematicalSizeOfType(final SchemaTypeImpl sImpl) {
        if (sImpl.getPrimitiveType().getBuiltinTypeCode() != 11) {
            return 0;
        }
        if (sImpl.getFacet(8) == null || ((SimpleValue)sImpl.getFacet(8)).getBigIntegerValue().signum() != 0) {
            return 1000001;
        }
        BigInteger min = null;
        BigInteger max = null;
        if (sImpl.getFacet(3) != null) {
            min = ((SimpleValue)sImpl.getFacet(3)).getBigIntegerValue();
        }
        if (sImpl.getFacet(4) != null) {
            min = ((SimpleValue)sImpl.getFacet(4)).getBigIntegerValue();
        }
        if (sImpl.getFacet(5) != null) {
            max = ((SimpleValue)sImpl.getFacet(5)).getBigIntegerValue();
        }
        if (sImpl.getFacet(6) != null) {
            max = ((SimpleValue)sImpl.getFacet(6)).getBigIntegerValue();
        }
        if (sImpl.getFacet(7) != null) {
            BigInteger peg = null;
            try {
                final BigInteger totalDigits = ((SimpleValue)sImpl.getFacet(7)).getBigIntegerValue();
                switch (totalDigits.intValue()) {
                    case 0:
                    case 1:
                    case 2: {
                        peg = BigInteger.valueOf(99L);
                        break;
                    }
                    case 3:
                    case 4: {
                        peg = BigInteger.valueOf(9999L);
                        break;
                    }
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9: {
                        peg = BigInteger.valueOf(999999999L);
                        break;
                    }
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18: {
                        peg = BigInteger.valueOf(999999999999999999L);
                        break;
                    }
                }
            }
            catch (final XmlValueOutOfRangeException ex) {}
            if (peg != null) {
                min = ((min == null) ? peg.negate() : min.max(peg.negate()));
                max = ((max == null) ? peg : max.min(peg));
            }
        }
        if (min != null && max != null) {
            if (min.signum() < 0) {
                min = min.negate().subtract(BigInteger.ONE);
            }
            if (max.signum() < 0) {
                max = max.negate().subtract(BigInteger.ONE);
            }
            max = max.max(min);
            if (max.compareTo(BigInteger.valueOf(127L)) <= 0) {
                return 8;
            }
            if (max.compareTo(BigInteger.valueOf(32767L)) <= 0) {
                return 16;
            }
            if (max.compareTo(BigInteger.valueOf(2147483647L)) <= 0) {
                return 32;
            }
            if (max.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0) {
                return 64;
            }
        }
        return 1000000;
    }
    
    static void resolveFundamentalFacets(final SchemaTypeImpl sImpl) {
        switch (sImpl.getSimpleVariety()) {
            case 1: {
                final SchemaTypeImpl baseImpl = (SchemaTypeImpl)sImpl.getBaseType();
                sImpl.setOrdered(baseImpl.ordered());
                sImpl.setBounded((sImpl.getFacet(3) != null || sImpl.getFacet(4) != null) && (sImpl.getFacet(5) != null || sImpl.getFacet(6) != null));
                sImpl.setFinite(baseImpl.isFinite() || (sImpl.isBounded() && isDiscreteType(sImpl)));
                sImpl.setNumeric(baseImpl.isNumeric() || isNumericPrimitive(sImpl.getPrimitiveType()));
                sImpl.setDecimalSize(decimalSizeOfType(sImpl));
                break;
            }
            case 2: {
                final SchemaType[] mTypes = sImpl.getUnionMemberTypes();
                int ordered = 0;
                boolean isBounded = true;
                boolean isFinite = true;
                boolean isNumeric = true;
                for (int i = 0; i < mTypes.length; ++i) {
                    if (mTypes[i].ordered() != 0) {
                        ordered = 1;
                    }
                    if (!mTypes[i].isBounded()) {
                        isBounded = false;
                    }
                    if (!mTypes[i].isFinite()) {
                        isFinite = false;
                    }
                    if (!mTypes[i].isNumeric()) {
                        isNumeric = false;
                    }
                }
                sImpl.setOrdered(ordered);
                sImpl.setBounded(isBounded);
                sImpl.setFinite(isFinite);
                sImpl.setNumeric(isNumeric);
                sImpl.setDecimalSize(0);
                break;
            }
            case 3: {
                sImpl.setOrdered(0);
                sImpl.setBounded(sImpl.getFacet(0) != null || sImpl.getFacet(2) != null);
                sImpl.setFinite(sImpl.getListItemType().isFinite() && sImpl.isBounded());
                sImpl.setNumeric(false);
                sImpl.setDecimalSize(0);
                break;
            }
        }
    }
    
    private static Map buildFacetCodeMap() {
        final Map result = new HashMap();
        for (int i = 0; i < StscSimpleTypeResolver.facetCodes.length; ++i) {
            result.put(StscSimpleTypeResolver.facetCodes[i].name, new Integer(StscSimpleTypeResolver.facetCodes[i].code));
        }
        return result;
    }
    
    private static int translateFacetCode(final QName name) {
        final Integer result = StscSimpleTypeResolver.facetCodeMap.get(name);
        if (result == null) {
            return -1;
        }
        return result;
    }
    
    static {
        EMPTY_REGEX_ARRAY = new RegularExpression[0];
        StscSimpleTypeResolver.facetCodes = new CodeForNameEntry[] { new CodeForNameEntry(QNameHelper.forLNS("length", "http://www.w3.org/2001/XMLSchema"), 0), new CodeForNameEntry(QNameHelper.forLNS("minLength", "http://www.w3.org/2001/XMLSchema"), 1), new CodeForNameEntry(QNameHelper.forLNS("maxLength", "http://www.w3.org/2001/XMLSchema"), 2), new CodeForNameEntry(QNameHelper.forLNS("pattern", "http://www.w3.org/2001/XMLSchema"), 10), new CodeForNameEntry(QNameHelper.forLNS("enumeration", "http://www.w3.org/2001/XMLSchema"), 11), new CodeForNameEntry(QNameHelper.forLNS("whiteSpace", "http://www.w3.org/2001/XMLSchema"), 9), new CodeForNameEntry(QNameHelper.forLNS("maxInclusive", "http://www.w3.org/2001/XMLSchema"), 5), new CodeForNameEntry(QNameHelper.forLNS("maxExclusive", "http://www.w3.org/2001/XMLSchema"), 6), new CodeForNameEntry(QNameHelper.forLNS("minInclusive", "http://www.w3.org/2001/XMLSchema"), 4), new CodeForNameEntry(QNameHelper.forLNS("minExclusive", "http://www.w3.org/2001/XMLSchema"), 3), new CodeForNameEntry(QNameHelper.forLNS("totalDigits", "http://www.w3.org/2001/XMLSchema"), 7), new CodeForNameEntry(QNameHelper.forLNS("fractionDigits", "http://www.w3.org/2001/XMLSchema"), 8) };
        facetCodeMap = buildFacetCodeMap();
    }
    
    private static class CodeForNameEntry
    {
        public QName name;
        public int code;
        
        CodeForNameEntry(final QName name, final int code) {
            this.name = name;
            this.code = code;
        }
    }
}
