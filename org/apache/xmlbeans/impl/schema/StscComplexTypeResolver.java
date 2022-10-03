package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaLocalElement;
import java.util.Collections;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.Element;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalElement;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.NamespaceList;
import org.apache.xmlbeans.SchemaLocalAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import java.util.HashSet;
import org.apache.xmlbeans.QNameSetSpecification;
import org.apache.xmlbeans.QNameSetBuilder;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleExtensionType;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import java.util.Arrays;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleRestrictionType;
import org.apache.xmlbeans.SchemaProperty;
import java.util.HashMap;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaAttributeModel;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.xmlbeans.SchemaType;
import java.util.Collection;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleContentDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexContentDocument;
import java.util.List;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.ExtensionType;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexRestrictionType;
import org.apache.xmlbeans.impl.xb.xsdschema.Group;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;
import java.util.Map;

public class StscComplexTypeResolver
{
    private static final int MODEL_GROUP_CODE = 100;
    private static CodeForNameEntry[] particleCodes;
    private static Map particleCodeMap;
    private static final int ATTRIBUTE_CODE = 100;
    private static final int ATTRIBUTE_GROUP_CODE = 101;
    private static final int ANY_ATTRIBUTE_CODE = 102;
    private static CodeForNameEntry[] attributeCodes;
    private static Map attributeCodeMap;
    
    public static Group getContentModel(final ComplexType parseCt) {
        if (parseCt.getAll() != null) {
            return parseCt.getAll();
        }
        if (parseCt.getSequence() != null) {
            return parseCt.getSequence();
        }
        if (parseCt.getChoice() != null) {
            return parseCt.getChoice();
        }
        if (parseCt.getGroup() != null) {
            return parseCt.getGroup();
        }
        return null;
    }
    
    public static Group getContentModel(final ComplexRestrictionType parseRest) {
        if (parseRest.getAll() != null) {
            return parseRest.getAll();
        }
        if (parseRest.getSequence() != null) {
            return parseRest.getSequence();
        }
        if (parseRest.getChoice() != null) {
            return parseRest.getChoice();
        }
        if (parseRest.getGroup() != null) {
            return parseRest.getGroup();
        }
        return null;
    }
    
    public static Group getContentModel(final ExtensionType parseExt) {
        if (parseExt.getAll() != null) {
            return parseExt.getAll();
        }
        if (parseExt.getSequence() != null) {
            return parseExt.getSequence();
        }
        if (parseExt.getChoice() != null) {
            return parseExt.getChoice();
        }
        if (parseExt.getGroup() != null) {
            return parseExt.getGroup();
        }
        return null;
    }
    
    static SchemaDocument.Schema getSchema(XmlObject o) {
        final XmlCursor c = o.newCursor();
        try {
            while (c.toParent()) {
                o = c.getObject();
                if (o.schemaType().equals(SchemaDocument.Schema.type)) {
                    return (SchemaDocument.Schema)o;
                }
            }
        }
        finally {
            c.dispose();
        }
        return null;
    }
    
    public static void resolveComplexType(final SchemaTypeImpl sImpl) {
        final ComplexType parseCt = (ComplexType)sImpl.getParseObject();
        final StscState state = StscState.get();
        final SchemaDocument.Schema schema = getSchema(parseCt);
        final boolean abs = parseCt.isSetAbstract() && parseCt.getAbstract();
        boolean finalExt = false;
        boolean finalRest = false;
        boolean finalList = false;
        boolean finalUnion = false;
        Object ds = null;
        if (parseCt.isSetFinal()) {
            ds = parseCt.getFinal();
        }
        else if (schema != null && schema.isSetFinalDefault()) {
            ds = schema.getFinalDefault();
        }
        if (ds != null) {
            if (ds instanceof String && ds.equals("#all")) {
                finalRest = (finalExt = (finalList = (finalUnion = true)));
            }
            else if (ds instanceof List) {
                if (((List)ds).contains("extension")) {
                    finalExt = true;
                }
                if (((List)ds).contains("restriction")) {
                    finalRest = true;
                }
            }
        }
        sImpl.setAbstractFinal(abs, finalExt, finalRest, finalList, finalUnion);
        boolean blockExt = false;
        boolean blockRest = false;
        Object block = null;
        if (parseCt.isSetBlock()) {
            block = parseCt.getBlock();
        }
        else if (schema != null && schema.isSetBlockDefault()) {
            block = schema.getBlockDefault();
        }
        if (block != null) {
            if (block instanceof String && block.equals("#all")) {
                blockRest = (blockExt = true);
            }
            else if (block instanceof List) {
                if (((List)block).contains("extension")) {
                    blockExt = true;
                }
                if (((List)block).contains("restriction")) {
                    blockRest = true;
                }
            }
        }
        sImpl.setBlock(blockExt, blockRest);
        final ComplexContentDocument.ComplexContent parseCc = parseCt.getComplexContent();
        SimpleContentDocument.SimpleContent parseSc = parseCt.getSimpleContent();
        Group parseGroup = getContentModel(parseCt);
        final int count = ((parseCc != null) + (parseSc != null) + (parseGroup != null)) ? 1 : 0;
        if (count > 1) {
            state.error("A complex type must define either a content model, or a simpleContent or complexContent derivation: more than one found.", 26, parseCt);
            parseGroup = null;
            if (parseCc != null && parseSc != null) {
                parseSc = null;
            }
        }
        if (parseCc != null) {
            if (parseCc.getExtension() != null && parseCc.getRestriction() != null) {
                state.error("Restriction conflicts with extension", 26, parseCc.getRestriction());
            }
            final boolean mixed = parseCc.isSetMixed() ? parseCc.getMixed() : parseCt.getMixed();
            if (parseCc.getExtension() != null) {
                resolveCcExtension(sImpl, parseCc.getExtension(), mixed);
            }
            else if (parseCc.getRestriction() != null) {
                resolveCcRestriction(sImpl, parseCc.getRestriction(), mixed);
            }
            else {
                state.error("Missing restriction or extension", 27, parseCc);
                resolveErrorType(sImpl);
            }
            return;
        }
        if (parseSc != null) {
            if (parseSc.getExtension() != null && parseSc.getRestriction() != null) {
                state.error("Restriction conflicts with extension", 26, parseSc.getRestriction());
            }
            if (parseSc.getExtension() != null) {
                resolveScExtension(sImpl, parseSc.getExtension());
            }
            else if (parseSc.getRestriction() != null) {
                resolveScRestriction(sImpl, parseSc.getRestriction());
            }
            else {
                state.error("Missing restriction or extension", 27, parseSc);
                resolveErrorType(sImpl);
            }
            return;
        }
        resolveBasicComplexType(sImpl);
    }
    
    static void resolveErrorType(final SchemaTypeImpl sImpl) {
        throw new RuntimeException("This type of error recovery not yet implemented.");
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
    
    static void resolveBasicComplexType(final SchemaTypeImpl sImpl) {
        final List anonymousTypes = new ArrayList();
        final ComplexType parseTree = (ComplexType)sImpl.getParseObject();
        final String targetNamespace = sImpl.getTargetNamespace();
        final boolean chameleon = sImpl.getChameleonNamespace() != null;
        final Group parseGroup = getContentModel(parseTree);
        if (sImpl.isRedefinition()) {
            StscState.get().error("src-redefine.5a", new Object[] { "<complexType>" }, parseTree);
        }
        final int particleCode = translateParticleCode(parseGroup);
        final Map elementModel = new LinkedHashMap();
        final SchemaParticle contentModel = translateContentModel(sImpl, parseGroup, targetNamespace, chameleon, sImpl.getElemFormDefault(), sImpl.getAttFormDefault(), particleCode, anonymousTypes, elementModel, false, null);
        final boolean isAll = contentModel != null && contentModel.getParticleType() == 1;
        final SchemaAttributeModelImpl attrModel = new SchemaAttributeModelImpl();
        translateAttributeModel(parseTree, targetNamespace, chameleon, sImpl.getAttFormDefault(), anonymousTypes, sImpl, null, attrModel, null, true, null);
        final WildcardResult wcElt = summarizeEltWildcards(contentModel);
        final WildcardResult wcAttr = summarizeAttrWildcards(attrModel);
        if (contentModel != null) {
            buildStateMachine(contentModel);
            if (!StscState.get().noUpa() && !((SchemaParticleImpl)contentModel).isDeterministic()) {
                StscState.get().error("cos-nonambig", null, parseGroup);
            }
        }
        final Map elementPropertyModel = buildContentPropertyModelByQName(contentModel, sImpl);
        final Map attributePropertyModel = buildAttributePropertyModelByQName(attrModel, sImpl);
        final int complexVariety = parseTree.getMixed() ? 4 : ((contentModel == null) ? 1 : 3);
        sImpl.setBaseTypeRef(BuiltinSchemaTypeSystem.ST_ANY_TYPE.getRef());
        sImpl.setBaseDepth(BuiltinSchemaTypeSystem.ST_ANY_TYPE.getBaseDepth() + 1);
        sImpl.setDerivationType(2);
        sImpl.setComplexTypeVariety(complexVariety);
        sImpl.setContentModel(contentModel, attrModel, elementPropertyModel, attributePropertyModel, isAll);
        sImpl.setAnonymousTypeRefs(makeRefArray(anonymousTypes));
        sImpl.setWildcardSummary(wcElt.typedWildcards, wcElt.hasWildcards, wcAttr.typedWildcards, wcAttr.hasWildcards);
    }
    
    static void resolveCcRestriction(final SchemaTypeImpl sImpl, final ComplexRestrictionType parseTree, final boolean mixed) {
        final StscState state = StscState.get();
        final String targetNamespace = sImpl.getTargetNamespace();
        final boolean chameleon = sImpl.getChameleonNamespace() != null;
        SchemaType baseType;
        if (parseTree.getBase() == null) {
            state.error("A complexContent must define a base type", 28, parseTree);
            baseType = null;
        }
        else {
            if (sImpl.isRedefinition()) {
                baseType = state.findRedefinedGlobalType(parseTree.getBase(), sImpl.getChameleonNamespace(), sImpl);
                if (baseType != null && !baseType.getName().equals(sImpl.getName())) {
                    state.error("src-redefine.5b", new Object[] { "<complexType>", QNameHelper.pretty(baseType.getName()), QNameHelper.pretty(sImpl.getName()) }, parseTree);
                }
            }
            else {
                baseType = state.findGlobalType(parseTree.getBase(), sImpl.getChameleonNamespace(), targetNamespace);
            }
            if (baseType == null) {
                state.notFoundError(parseTree.getBase(), 0, parseTree.xgetBase(), true);
            }
        }
        if (baseType == null) {
            baseType = BuiltinSchemaTypeSystem.ST_ANY_TYPE;
        }
        if (baseType != null && baseType.finalRestriction()) {
            state.error("derivation-ok-restriction.1", new Object[] { QNameHelper.pretty(baseType.getName()), QNameHelper.pretty(sImpl.getName()) }, parseTree.xgetBase());
        }
        if (baseType != null && !StscResolver.resolveType((SchemaTypeImpl)baseType)) {
            baseType = null;
        }
        final List anonymousTypes = new ArrayList();
        final Group parseEg = getContentModel(parseTree);
        final int particleCode = translateParticleCode(parseEg);
        final Map elementModel = new LinkedHashMap();
        final SchemaParticle contentModel = translateContentModel(sImpl, parseEg, targetNamespace, chameleon, sImpl.getElemFormDefault(), sImpl.getAttFormDefault(), particleCode, anonymousTypes, elementModel, false, null);
        final boolean isAll = contentModel != null && contentModel.getParticleType() == 1;
        SchemaAttributeModelImpl attrModel;
        if (baseType == null) {
            attrModel = new SchemaAttributeModelImpl();
        }
        else {
            attrModel = new SchemaAttributeModelImpl(baseType.getAttributeModel());
        }
        translateAttributeModel(parseTree, targetNamespace, chameleon, sImpl.getAttFormDefault(), anonymousTypes, sImpl, null, attrModel, baseType, false, null);
        final WildcardResult wcElt = summarizeEltWildcards(contentModel);
        final WildcardResult wcAttr = summarizeAttrWildcards(attrModel);
        if (contentModel != null) {
            buildStateMachine(contentModel);
            if (!StscState.get().noUpa() && !((SchemaParticleImpl)contentModel).isDeterministic()) {
                StscState.get().error("cos-nonambig", null, parseEg);
            }
        }
        final Map elementPropertyModel = buildContentPropertyModelByQName(contentModel, sImpl);
        final Map attributePropertyModel = buildAttributePropertyModelByQName(attrModel, sImpl);
        final int complexVariety = mixed ? 4 : ((contentModel == null) ? 1 : 3);
        sImpl.setBaseTypeRef(baseType.getRef());
        sImpl.setBaseDepth(((SchemaTypeImpl)baseType).getBaseDepth() + 1);
        sImpl.setDerivationType(1);
        sImpl.setComplexTypeVariety(complexVariety);
        sImpl.setContentModel(contentModel, attrModel, elementPropertyModel, attributePropertyModel, isAll);
        sImpl.setAnonymousTypeRefs(makeRefArray(anonymousTypes));
        sImpl.setWildcardSummary(wcElt.typedWildcards, wcElt.hasWildcards, wcAttr.typedWildcards, wcAttr.hasWildcards);
    }
    
    static Map extractElementModel(final SchemaType sType) {
        final Map elementModel = new HashMap();
        if (sType != null) {
            final SchemaProperty[] sProps = sType.getProperties();
            for (int i = 0; i < sProps.length; ++i) {
                if (!sProps[i].isAttribute()) {
                    elementModel.put(sProps[i].getName(), sProps[i].getType());
                }
            }
        }
        return elementModel;
    }
    
    static void resolveCcExtension(final SchemaTypeImpl sImpl, final ExtensionType parseTree, boolean mixed) {
        final StscState state = StscState.get();
        final String targetNamespace = sImpl.getTargetNamespace();
        final boolean chameleon = sImpl.getChameleonNamespace() != null;
        SchemaType baseType;
        if (parseTree.getBase() == null) {
            state.error("A complexContent must define a base type", 28, parseTree);
            baseType = null;
        }
        else {
            if (sImpl.isRedefinition()) {
                baseType = state.findRedefinedGlobalType(parseTree.getBase(), sImpl.getChameleonNamespace(), sImpl);
                if (baseType != null && !baseType.getName().equals(sImpl.getName())) {
                    state.error("src-redefine.5b", new Object[] { "<complexType>", QNameHelper.pretty(baseType.getName()), QNameHelper.pretty(sImpl.getName()) }, parseTree);
                }
            }
            else {
                baseType = state.findGlobalType(parseTree.getBase(), sImpl.getChameleonNamespace(), targetNamespace);
            }
            if (baseType == null) {
                state.notFoundError(parseTree.getBase(), 0, parseTree.xgetBase(), true);
            }
        }
        if (baseType != null && !StscResolver.resolveType((SchemaTypeImpl)baseType)) {
            baseType = null;
        }
        if (baseType != null && baseType.isSimpleType()) {
            state.recover("src-ct.1", new Object[] { QNameHelper.pretty(baseType.getName()) }, parseTree.xgetBase());
            baseType = null;
        }
        if (baseType != null && baseType.finalExtension()) {
            state.error("cos-ct-extends.1.1", new Object[] { QNameHelper.pretty(baseType.getName()), QNameHelper.pretty(sImpl.getName()) }, parseTree.xgetBase());
        }
        final SchemaParticle baseContentModel = (baseType == null) ? null : baseType.getContentModel();
        final List anonymousTypes = new ArrayList();
        final Map baseElementModel = extractElementModel(baseType);
        final Group parseEg = getContentModel(parseTree);
        if (baseType != null && baseType.getContentType() == 2) {
            if (parseEg == null) {
                resolveScExtensionPart2(sImpl, baseType, parseTree, targetNamespace, chameleon);
                return;
            }
            state.recover("cos-ct-extends.1.4.1", new Object[] { QNameHelper.pretty(baseType.getName()) }, parseTree.xgetBase());
            baseType = null;
        }
        SchemaParticle extensionModel = translateContentModel(sImpl, parseEg, targetNamespace, chameleon, sImpl.getElemFormDefault(), sImpl.getAttFormDefault(), translateParticleCode(parseEg), anonymousTypes, baseElementModel, false, null);
        if (extensionModel == null && !mixed) {
            mixed = (baseType != null && baseType.getContentType() == 4);
        }
        if (baseType != null && baseType.getContentType() != 1 && baseType.getContentType() == 4 != mixed) {
            state.error("cos-ct-extends.1.4.2.2", null, parseTree.xgetBase());
        }
        if (baseType != null && baseType.hasAllContent() && extensionModel != null) {
            state.error("Cannot extend a type with 'all' content model", 42, parseTree.xgetBase());
            extensionModel = null;
        }
        final SchemaParticle contentModel = extendContentModel(baseContentModel, extensionModel, parseTree);
        final boolean isAll = contentModel != null && contentModel.getParticleType() == 1;
        SchemaAttributeModelImpl attrModel;
        if (baseType == null) {
            attrModel = new SchemaAttributeModelImpl();
        }
        else {
            attrModel = new SchemaAttributeModelImpl(baseType.getAttributeModel());
        }
        translateAttributeModel(parseTree, targetNamespace, chameleon, sImpl.getAttFormDefault(), anonymousTypes, sImpl, null, attrModel, baseType, true, null);
        final WildcardResult wcElt = summarizeEltWildcards(contentModel);
        final WildcardResult wcAttr = summarizeAttrWildcards(attrModel);
        if (contentModel != null) {
            buildStateMachine(contentModel);
            if (!StscState.get().noUpa() && !((SchemaParticleImpl)contentModel).isDeterministic()) {
                StscState.get().error("cos-nonambig", null, parseEg);
            }
        }
        final Map elementPropertyModel = buildContentPropertyModelByQName(contentModel, sImpl);
        final Map attributePropertyModel = buildAttributePropertyModelByQName(attrModel, sImpl);
        int complexVariety;
        if (contentModel == null && baseType != null && baseType.getContentType() == 2) {
            complexVariety = 2;
            sImpl.setContentBasedOnTypeRef(baseType.getContentBasedOnType().getRef());
        }
        else {
            complexVariety = (mixed ? 4 : ((contentModel == null) ? 1 : 3));
        }
        if (baseType == null) {
            baseType = XmlObject.type;
        }
        sImpl.setBaseTypeRef(baseType.getRef());
        sImpl.setBaseDepth(((SchemaTypeImpl)baseType).getBaseDepth() + 1);
        sImpl.setDerivationType(2);
        sImpl.setComplexTypeVariety(complexVariety);
        sImpl.setContentModel(contentModel, attrModel, elementPropertyModel, attributePropertyModel, isAll);
        sImpl.setAnonymousTypeRefs(makeRefArray(anonymousTypes));
        sImpl.setWildcardSummary(wcElt.typedWildcards, wcElt.hasWildcards, wcAttr.typedWildcards, wcAttr.hasWildcards);
    }
    
    static void resolveScRestriction(final SchemaTypeImpl sImpl, final SimpleRestrictionType parseTree) {
        SchemaType contentType = null;
        final StscState state = StscState.get();
        final String targetNamespace = sImpl.getTargetNamespace();
        final boolean chameleon = sImpl.getChameleonNamespace() != null;
        final List anonymousTypes = new ArrayList();
        if (parseTree.getSimpleType() != null) {
            final LocalSimpleType typedef = parseTree.getSimpleType();
            final SchemaTypeImpl anonType = (SchemaTypeImpl)(contentType = StscTranslator.translateAnonymousSimpleType(typedef, targetNamespace, chameleon, sImpl.getElemFormDefault(), sImpl.getAttFormDefault(), anonymousTypes, sImpl));
        }
        SchemaType baseType;
        if (parseTree.getBase() == null) {
            state.error("A simpleContent restriction must define a base type", 28, parseTree);
            baseType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        else {
            if (sImpl.isRedefinition()) {
                baseType = state.findRedefinedGlobalType(parseTree.getBase(), sImpl.getChameleonNamespace(), sImpl);
                if (baseType != null && !baseType.getName().equals(sImpl.getName())) {
                    state.error("src-redefine.5b", new Object[] { "<simpleType>", QNameHelper.pretty(baseType.getName()), QNameHelper.pretty(sImpl.getName()) }, parseTree);
                }
            }
            else {
                baseType = state.findGlobalType(parseTree.getBase(), sImpl.getChameleonNamespace(), targetNamespace);
            }
            if (baseType == null) {
                state.notFoundError(parseTree.getBase(), 0, parseTree.xgetBase(), true);
                baseType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
            }
        }
        StscResolver.resolveType((SchemaTypeImpl)baseType);
        if (contentType != null) {
            StscResolver.resolveType((SchemaTypeImpl)contentType);
        }
        else {
            contentType = baseType;
        }
        if (baseType.isSimpleType()) {
            state.recover("ct-props-correct.2", new Object[] { QNameHelper.pretty(baseType.getName()) }, parseTree);
            baseType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        else if (baseType.getContentType() != 2 && contentType == null) {
            baseType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        if (baseType != null && baseType.finalRestriction()) {
            state.error("derivation-ok-restriction.1", new Object[] { QNameHelper.pretty(baseType.getName()), QNameHelper.pretty(sImpl.getName()) }, parseTree.xgetBase());
        }
        SchemaAttributeModelImpl attrModel;
        if (baseType == null) {
            attrModel = new SchemaAttributeModelImpl();
        }
        else {
            attrModel = new SchemaAttributeModelImpl(baseType.getAttributeModel());
        }
        translateAttributeModel(parseTree, targetNamespace, chameleon, sImpl.getAttFormDefault(), anonymousTypes, sImpl, null, attrModel, baseType, false, null);
        final WildcardResult wcAttr = summarizeAttrWildcards(attrModel);
        final Map attributePropertyModel = buildAttributePropertyModelByQName(attrModel, sImpl);
        sImpl.setBaseTypeRef(baseType.getRef());
        sImpl.setBaseDepth(((SchemaTypeImpl)baseType).getBaseDepth() + 1);
        sImpl.setContentBasedOnTypeRef(contentType.getRef());
        sImpl.setDerivationType(1);
        sImpl.setAnonymousTypeRefs(makeRefArray(anonymousTypes));
        sImpl.setWildcardSummary(QNameSet.EMPTY, false, wcAttr.typedWildcards, wcAttr.hasWildcards);
        sImpl.setComplexTypeVariety(2);
        sImpl.setContentModel(null, attrModel, null, attributePropertyModel, false);
        sImpl.setSimpleTypeVariety(contentType.getSimpleVariety());
        sImpl.setPrimitiveTypeRef((contentType.getPrimitiveType() == null) ? null : contentType.getPrimitiveType().getRef());
        switch (sImpl.getSimpleVariety()) {
            case 3: {
                sImpl.setListItemTypeRef(contentType.getListItemType().getRef());
                break;
            }
            case 2: {
                sImpl.setUnionMemberTypeRefs(makeRefArray(Arrays.asList(contentType.getUnionMemberTypes())));
                break;
            }
        }
        StscSimpleTypeResolver.resolveFacets(sImpl, parseTree, (SchemaTypeImpl)contentType);
        StscSimpleTypeResolver.resolveFundamentalFacets(sImpl);
    }
    
    static void resolveScExtension(final SchemaTypeImpl sImpl, final SimpleExtensionType parseTree) {
        final StscState state = StscState.get();
        final String targetNamespace = sImpl.getTargetNamespace();
        final boolean chameleon = sImpl.getChameleonNamespace() != null;
        SchemaType baseType;
        if (parseTree.getBase() == null) {
            state.error("A simpleContent extension must define a base type", 28, parseTree);
            baseType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        else {
            if (sImpl.isRedefinition()) {
                baseType = state.findRedefinedGlobalType(parseTree.getBase(), sImpl.getChameleonNamespace(), sImpl);
                if (baseType != null && !baseType.getName().equals(sImpl.getName())) {
                    state.error("src-redefine.5b", new Object[] { "<simpleType>", QNameHelper.pretty(baseType.getName()), QNameHelper.pretty(sImpl.getName()) }, parseTree);
                }
            }
            else {
                baseType = state.findGlobalType(parseTree.getBase(), sImpl.getChameleonNamespace(), targetNamespace);
            }
            if (baseType == null) {
                state.notFoundError(parseTree.getBase(), 0, parseTree.xgetBase(), true);
                baseType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
            }
        }
        StscResolver.resolveType((SchemaTypeImpl)baseType);
        if (!baseType.isSimpleType() && baseType.getContentType() != 2) {
            state.error("src-ct.2", new Object[] { QNameHelper.pretty(baseType.getName()) }, parseTree);
            baseType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        if (baseType != null && baseType.finalExtension()) {
            state.error("cos-ct-extends.1.1", new Object[] { QNameHelper.pretty(baseType.getName()), QNameHelper.pretty(sImpl.getName()) }, parseTree.xgetBase());
        }
        resolveScExtensionPart2(sImpl, baseType, parseTree, targetNamespace, chameleon);
    }
    
    static void resolveScExtensionPart2(final SchemaTypeImpl sImpl, final SchemaType baseType, final ExtensionType parseTree, final String targetNamespace, final boolean chameleon) {
        final List anonymousTypes = new ArrayList();
        final SchemaAttributeModelImpl attrModel = new SchemaAttributeModelImpl(baseType.getAttributeModel());
        translateAttributeModel(parseTree, targetNamespace, chameleon, sImpl.getAttFormDefault(), anonymousTypes, sImpl, null, attrModel, baseType, true, null);
        final WildcardResult wcAttr = summarizeAttrWildcards(attrModel);
        final Map attributePropertyModel = buildAttributePropertyModelByQName(attrModel, sImpl);
        sImpl.setBaseTypeRef(baseType.getRef());
        sImpl.setBaseDepth(((SchemaTypeImpl)baseType).getBaseDepth() + 1);
        sImpl.setContentBasedOnTypeRef(baseType.getRef());
        sImpl.setDerivationType(2);
        sImpl.setAnonymousTypeRefs(makeRefArray(anonymousTypes));
        sImpl.setWildcardSummary(QNameSet.EMPTY, false, wcAttr.typedWildcards, wcAttr.hasWildcards);
        sImpl.setComplexTypeVariety(2);
        sImpl.setContentModel(null, attrModel, null, attributePropertyModel, false);
        sImpl.setSimpleTypeVariety(baseType.getSimpleVariety());
        sImpl.setPrimitiveTypeRef((baseType.getPrimitiveType() == null) ? null : baseType.getPrimitiveType().getRef());
        switch (sImpl.getSimpleVariety()) {
            case 3: {
                sImpl.setListItemTypeRef(baseType.getListItemType().getRef());
                break;
            }
            case 2: {
                sImpl.setUnionMemberTypeRefs(makeRefArray(Arrays.asList(baseType.getUnionMemberTypes())));
                break;
            }
        }
        StscSimpleTypeResolver.resolveFacets(sImpl, null, (SchemaTypeImpl)baseType);
        StscSimpleTypeResolver.resolveFundamentalFacets(sImpl);
    }
    
    static WildcardResult summarizeAttrWildcards(final SchemaAttributeModel attrModel) {
        if (attrModel.getWildcardProcess() == 0) {
            return new WildcardResult(QNameSet.EMPTY, false);
        }
        if (attrModel.getWildcardProcess() == 3) {
            return new WildcardResult(QNameSet.EMPTY, true);
        }
        return new WildcardResult(attrModel.getWildcardSet(), true);
    }
    
    static WildcardResult summarizeEltWildcards(final SchemaParticle contentModel) {
        if (contentModel == null) {
            return new WildcardResult(QNameSet.EMPTY, false);
        }
        switch (contentModel.getParticleType()) {
            case 1:
            case 2:
            case 3: {
                final QNameSetBuilder set = new QNameSetBuilder();
                boolean hasWildcards = false;
                for (int i = 0; i < contentModel.countOfParticleChild(); ++i) {
                    final WildcardResult inner = summarizeEltWildcards(contentModel.getParticleChild(i));
                    set.addAll(inner.typedWildcards);
                    hasWildcards |= inner.hasWildcards;
                }
                return new WildcardResult(set.toQNameSet(), hasWildcards);
            }
            case 5: {
                return new WildcardResult((contentModel.getWildcardProcess() == 3) ? QNameSet.EMPTY : contentModel.getWildcardSet(), true);
            }
            default: {
                return new WildcardResult(QNameSet.EMPTY, false);
            }
        }
    }
    
    static void translateAttributeModel(final XmlObject parseTree, final String targetNamespace, boolean chameleon, final String formDefault, final List anonymousTypes, final SchemaType outerType, Set seenAttributes, final SchemaAttributeModelImpl result, final SchemaType baseType, final boolean extension, final SchemaAttributeGroupImpl redefinitionFor) {
        final StscState state = StscState.get();
        if (seenAttributes == null) {
            seenAttributes = new HashSet();
        }
        boolean seenWildcard = false;
        boolean seenRedefinition = false;
        SchemaAttributeModel baseModel = null;
        if (baseType != null) {
            baseModel = baseType.getAttributeModel();
        }
        final XmlCursor cur = parseTree.newCursor();
        for (boolean more = cur.toFirstChild(); more; more = cur.toNextSibling()) {
            switch (translateAttributeCode(cur.getName())) {
                case 100: {
                    final Attribute xsdattr = (Attribute)cur.getObject();
                    final SchemaLocalAttribute sAttr = StscTranslator.translateAttribute(xsdattr, targetNamespace, formDefault, chameleon, anonymousTypes, outerType, baseModel, true);
                    if (sAttr == null) {
                        break;
                    }
                    if (seenAttributes.contains(sAttr.getName())) {
                        state.error("ct-props-correct.4", new Object[] { QNameHelper.pretty(sAttr.getName()), QNameHelper.pretty(outerType.getName()) }, xsdattr.xgetName());
                        break;
                    }
                    seenAttributes.add(sAttr.getName());
                    if (baseModel != null) {
                        final SchemaLocalAttribute baseAttr = baseModel.getAttribute(sAttr.getName());
                        if (baseAttr == null) {
                            if (!extension && !baseModel.getWildcardSet().contains(sAttr.getName())) {
                                state.error("derivation-ok-restriction.2.2", new Object[] { QNameHelper.pretty(sAttr.getName()), QNameHelper.pretty(outerType.getName()) }, xsdattr);
                            }
                        }
                        else if (extension) {
                            if (sAttr.getUse() == 1) {
                                state.error("An extension cannot prohibit an attribute from the base type; use restriction instead.", 37, xsdattr.xgetUse());
                            }
                        }
                        else if (sAttr.getUse() != 3) {
                            if (baseAttr.getUse() == 3) {
                                state.error("derivation-ok-restriction.2.1.1", new Object[] { QNameHelper.pretty(sAttr.getName()), QNameHelper.pretty(outerType.getName()) }, xsdattr);
                            }
                            if (sAttr.getUse() == 1) {
                                result.removeProhibitedAttribute(sAttr.getName());
                            }
                        }
                    }
                    if (sAttr.getUse() != 1) {
                        result.addAttribute(sAttr);
                    }
                    else {
                        final SchemaType attrType = sAttr.getType();
                        if (anonymousTypes != null && anonymousTypes.contains(attrType)) {
                            anonymousTypes.remove(attrType);
                        }
                    }
                    if (sAttr.getDefaultText() != null && !sAttr.isFixed() && sAttr.getUse() != 2) {
                        state.error("src-attribute.2", new Object[] { QNameHelper.pretty(sAttr.getName()) }, xsdattr);
                        break;
                    }
                    break;
                }
                case 102: {
                    final Wildcard xsdwc = (Wildcard)cur.getObject();
                    if (seenWildcard) {
                        state.error("Only one attribute wildcard allowed", 38, xsdwc);
                        break;
                    }
                    seenWildcard = true;
                    final NamespaceList nsList = xsdwc.xgetNamespace();
                    String nsText;
                    if (nsList == null) {
                        nsText = "##any";
                    }
                    else {
                        nsText = nsList.getStringValue();
                    }
                    final QNameSet wcset = QNameSet.forWildcardNamespaceString(nsText, targetNamespace);
                    if (baseModel != null && !extension) {
                        if (baseModel.getWildcardSet() == null) {
                            state.error("derivation-ok-restriction.4.1", null, xsdwc);
                            break;
                        }
                        if (!baseModel.getWildcardSet().containsAll(wcset)) {
                            state.error("derivation-ok-restriction.4.2", new Object[] { nsText }, xsdwc);
                            break;
                        }
                    }
                    final int wcprocess = translateWildcardProcess(xsdwc.xgetProcessContents());
                    if (result.getWildcardProcess() == 0) {
                        result.setWildcardSet(wcset);
                        result.setWildcardProcess(wcprocess);
                        break;
                    }
                    if (extension) {
                        result.setWildcardSet(wcset.union(result.getWildcardSet()));
                        result.setWildcardProcess(wcprocess);
                        break;
                    }
                    result.setWildcardSet(wcset.intersect(result.getWildcardSet()));
                    break;
                }
                case 101: {
                    final AttributeGroupRef xsdag = (AttributeGroupRef)cur.getObject();
                    final QName ref = xsdag.getRef();
                    if (ref == null) {
                        state.error("Attribute group reference must have a ref attribute", 39, xsdag);
                        break;
                    }
                    SchemaAttributeGroupImpl group;
                    if (redefinitionFor != null) {
                        group = state.findRedefinedAttributeGroup(ref, chameleon ? targetNamespace : null, redefinitionFor);
                        if (group != null && redefinitionFor.getName().equals(group.getName())) {
                            if (seenRedefinition) {
                                state.error("src-redefine.7.1", new Object[] { QNameHelper.pretty(redefinitionFor.getName()) }, xsdag);
                            }
                            seenRedefinition = true;
                        }
                    }
                    else {
                        group = state.findAttributeGroup(ref, chameleon ? targetNamespace : null, targetNamespace);
                    }
                    if (group == null) {
                        state.notFoundError(ref, 4, xsdag.xgetRef(), true);
                        break;
                    }
                    if (state.isProcessing(group)) {
                        state.error("src-attribute_group.3", new Object[] { QNameHelper.pretty(group.getName()) }, group.getParseObject());
                        break;
                    }
                    String subTargetNamespace = targetNamespace;
                    if (group.getTargetNamespace() != null) {
                        subTargetNamespace = group.getTargetNamespace();
                        chameleon = (group.getChameleonNamespace() != null);
                    }
                    state.startProcessing(group);
                    SchemaAttributeGroupImpl nestedRedefinitionFor = null;
                    if (group.isRedefinition()) {
                        nestedRedefinitionFor = group;
                    }
                    translateAttributeModel(group.getParseObject(), subTargetNamespace, chameleon, group.getFormDefault(), anonymousTypes, outerType, seenAttributes, result, baseType, extension, nestedRedefinitionFor);
                    state.finishProcessing(group);
                    break;
                }
            }
        }
        if (!extension && !seenWildcard) {
            result.setWildcardSet(null);
            result.setWildcardProcess(0);
        }
    }
    
    static SchemaParticle extendContentModel(final SchemaParticle baseContentModel, final SchemaParticle extendedContentModel, final XmlObject parseTree) {
        if (extendedContentModel == null) {
            return baseContentModel;
        }
        if (baseContentModel == null) {
            return extendedContentModel;
        }
        final SchemaParticleImpl sPart = new SchemaParticleImpl();
        sPart.setParticleType(3);
        final List accumulate = new ArrayList();
        addMinusPointlessParticles(accumulate, baseContentModel, 3);
        addMinusPointlessParticles(accumulate, extendedContentModel, 3);
        sPart.setMinOccurs(BigInteger.ONE);
        sPart.setMaxOccurs(BigInteger.ONE);
        sPart.setParticleChildren(accumulate.toArray(new SchemaParticle[accumulate.size()]));
        return filterPointlessParticlesAndVerifyAllParticles(sPart, parseTree);
    }
    
    static BigInteger extractMinOccurs(final XmlNonNegativeInteger nni) {
        if (nni == null) {
            return BigInteger.ONE;
        }
        final BigInteger result = nni.getBigIntegerValue();
        if (result == null) {
            return BigInteger.ONE;
        }
        return result;
    }
    
    static BigInteger extractMaxOccurs(final AllNNI allNNI) {
        if (allNNI == null) {
            return BigInteger.ONE;
        }
        if (allNNI.instanceType().getPrimitiveType().getBuiltinTypeCode() == 11) {
            return ((XmlInteger)allNNI).getBigIntegerValue();
        }
        return null;
    }
    
    static SchemaParticle translateContentModel(final SchemaType outerType, XmlObject parseTree, String targetNamespace, boolean chameleon, String elemFormDefault, String attFormDefault, int particleCode, final List anonymousTypes, final Map elementModel, final boolean allowElt, RedefinitionForGroup redefinitionFor) {
        if (parseTree == null || particleCode == 0) {
            return null;
        }
        final StscState state = StscState.get();
        assert particleCode != 0;
        boolean hasChildren = false;
        SchemaModelGroupImpl group = null;
        SchemaParticleImpl sPart;
        BigInteger minOccurs;
        BigInteger maxOccurs;
        if (particleCode == 4) {
            if (!allowElt) {
                state.error("Must be a sequence, choice or all here", 32, parseTree);
            }
            final LocalElement parseElt = (LocalElement)parseTree;
            sPart = StscTranslator.translateElement(parseElt, targetNamespace, chameleon, elemFormDefault, attFormDefault, anonymousTypes, outerType);
            if (sPart == null) {
                return null;
            }
            minOccurs = extractMinOccurs(parseElt.xgetMinOccurs());
            maxOccurs = extractMaxOccurs(parseElt.xgetMaxOccurs());
            final SchemaType oldType = elementModel.get(sPart.getName());
            if (oldType == null) {
                elementModel.put(sPart.getName(), sPart.getType());
            }
            else if (!sPart.getType().equals(oldType)) {
                state.error("cos-element-consistent", new Object[] { QNameHelper.pretty(sPart.getName()) }, parseTree);
                return null;
            }
        }
        else if (particleCode == 5) {
            if (!allowElt) {
                state.error("Must be a sequence, choice or all here", 32, parseTree);
            }
            final AnyDocument.Any parseAny = (AnyDocument.Any)parseTree;
            sPart = new SchemaParticleImpl();
            sPart.setParticleType(5);
            final NamespaceList nslist = parseAny.xgetNamespace();
            QNameSet wcset;
            if (nslist == null) {
                wcset = QNameSet.ALL;
            }
            else {
                wcset = QNameSet.forWildcardNamespaceString(nslist.getStringValue(), targetNamespace);
            }
            sPart.setWildcardSet(wcset);
            sPart.setWildcardProcess(translateWildcardProcess(parseAny.xgetProcessContents()));
            minOccurs = extractMinOccurs(parseAny.xgetMinOccurs());
            maxOccurs = extractMaxOccurs(parseAny.xgetMaxOccurs());
        }
        else {
            Group parseGroup = (Group)parseTree;
            sPart = new SchemaParticleImpl();
            minOccurs = extractMinOccurs(parseGroup.xgetMinOccurs());
            maxOccurs = extractMaxOccurs(parseGroup.xgetMaxOccurs());
            if (particleCode == 100) {
                final QName ref = parseGroup.getRef();
                if (ref == null) {
                    state.error("Group reference must have a ref attribute", 33, parseTree);
                    return null;
                }
                if (redefinitionFor != null) {
                    group = state.findRedefinedModelGroup(ref, chameleon ? targetNamespace : null, redefinitionFor.getGroup());
                    if (group != null && group.getName().equals(redefinitionFor.getGroup().getName())) {
                        if (redefinitionFor.isSeenRedefinition()) {
                            state.error("src-redefine.6.1.1", new Object[] { QNameHelper.pretty(group.getName()) }, parseTree);
                        }
                        if (!BigInteger.ONE.equals(maxOccurs) || !BigInteger.ONE.equals(minOccurs)) {
                            state.error("src-redefine.6.1.2", new Object[] { QNameHelper.pretty(group.getName()) }, parseTree);
                        }
                        redefinitionFor.setSeenRedefinition(true);
                    }
                }
                else {
                    group = state.findModelGroup(ref, chameleon ? targetNamespace : null, targetNamespace);
                }
                if (group == null) {
                    state.notFoundError(ref, 6, ((Group)parseTree).xgetRef(), true);
                    return null;
                }
                if (state.isProcessing(group)) {
                    state.error("mg-props-correct.2", new Object[] { QNameHelper.pretty(group.getName()) }, group.getParseObject());
                    return null;
                }
                final XmlCursor cur = group.getParseObject().newCursor();
                for (boolean more = cur.toFirstChild(); more; more = cur.toNextSibling()) {
                    particleCode = translateParticleCode(cur.getName());
                    if (particleCode != 0) {
                        parseGroup = (Group)(parseTree = cur.getObject());
                        break;
                    }
                }
                if (particleCode == 0) {
                    state.error("Model group " + QNameHelper.pretty(group.getName()) + " is empty", 32, group.getParseObject());
                    return null;
                }
                if (particleCode != 1 && particleCode != 3 && particleCode != 2) {
                    state.error("Model group " + QNameHelper.pretty(group.getName()) + " is not a sequence, all, or choice", 32, group.getParseObject());
                }
                final String newTargetNamespace = group.getTargetNamespace();
                if (newTargetNamespace != null) {
                    targetNamespace = newTargetNamespace;
                }
                elemFormDefault = group.getElemFormDefault();
                attFormDefault = group.getAttFormDefault();
                chameleon = (group.getChameleonNamespace() != null);
            }
            switch (particleCode) {
                case 1:
                case 2:
                case 3: {
                    sPart.setParticleType(particleCode);
                    hasChildren = true;
                    break;
                }
                default: {
                    assert false;
                    throw new IllegalStateException();
                }
            }
        }
        if (maxOccurs != null && minOccurs.compareTo(maxOccurs) > 0) {
            state.error("p-props-correct.2.1", null, parseTree);
            maxOccurs = minOccurs;
        }
        if (maxOccurs != null && maxOccurs.compareTo(BigInteger.ONE) < 0) {
            state.warning("p-props-correct.2.2", null, parseTree);
            anonymousTypes.remove(sPart.getType());
            return null;
        }
        sPart.setMinOccurs(minOccurs);
        sPart.setMaxOccurs(maxOccurs);
        if (group != null) {
            state.startProcessing(group);
            redefinitionFor = null;
            if (group.isRedefinition()) {
                redefinitionFor = new RedefinitionForGroup(group);
            }
        }
        if (hasChildren) {
            final XmlCursor cur2 = parseTree.newCursor();
            final List accumulate = new ArrayList();
            for (boolean more2 = cur2.toFirstChild(); more2; more2 = cur2.toNextSibling()) {
                final int code = translateParticleCode(cur2.getName());
                if (code != 0) {
                    addMinusPointlessParticles(accumulate, translateContentModel(outerType, cur2.getObject(), targetNamespace, chameleon, elemFormDefault, attFormDefault, code, anonymousTypes, elementModel, true, redefinitionFor), sPart.getParticleType());
                }
            }
            sPart.setParticleChildren(accumulate.toArray(new SchemaParticle[accumulate.size()]));
            cur2.dispose();
        }
        final SchemaParticle result = filterPointlessParticlesAndVerifyAllParticles(sPart, parseTree);
        if (group != null) {
            state.finishProcessing(group);
        }
        return result;
    }
    
    static int translateWildcardProcess(final Wildcard.ProcessContents process) {
        if (process == null) {
            return 1;
        }
        final String processValue = process.getStringValue();
        if ("lax".equals(processValue)) {
            return 2;
        }
        if ("skip".equals(processValue)) {
            return 3;
        }
        return 1;
    }
    
    static SchemaParticle filterPointlessParticlesAndVerifyAllParticles(final SchemaParticle part, final XmlObject parseTree) {
        if (part.getMaxOccurs() != null && part.getMaxOccurs().signum() == 0) {
            return null;
        }
        switch (part.getParticleType()) {
            case 1:
            case 3: {
                if (part.getParticleChildren().length == 0) {
                    return null;
                }
                if (part.isSingleton() && part.countOfParticleChild() == 1) {
                    return part.getParticleChild(0);
                }
                break;
            }
            case 2: {
                if (part.getParticleChildren().length == 0 && part.getMinOccurs().compareTo(BigInteger.ZERO) == 0) {
                    return null;
                }
                if (part.isSingleton() && part.countOfParticleChild() == 1) {
                    return part.getParticleChild(0);
                }
                break;
            }
            case 4:
            case 5: {
                return part;
            }
            default: {
                assert false;
                throw new IllegalStateException();
            }
        }
        final boolean isAll = part.getParticleType() == 1;
        if (isAll && (part.getMaxOccurs() == null || part.getMaxOccurs().compareTo(BigInteger.ONE) > 0)) {
            StscState.get().error("cos-all-limited.1.2a", null, parseTree);
        }
        for (int i = 0; i < part.countOfParticleChild(); ++i) {
            final SchemaParticle child = part.getParticleChild(i);
            if (child.getParticleType() == 1) {
                StscState.get().error("cos-all-limited.1.2b", null, parseTree);
            }
            else if (isAll && (child.getParticleType() != 4 || child.getMaxOccurs() == null || child.getMaxOccurs().compareTo(BigInteger.ONE) > 0)) {
                StscState.get().error("cos-all-limited.2", null, parseTree);
            }
        }
        return part;
    }
    
    static void addMinusPointlessParticles(final List list, final SchemaParticle part, final int parentParticleType) {
        if (part == null) {
            return;
        }
        switch (part.getParticleType()) {
            case 3: {
                if (parentParticleType == 3 && part.isSingleton()) {
                    list.addAll(Arrays.asList(part.getParticleChildren()));
                    return;
                }
                break;
            }
            case 2: {
                if (parentParticleType == 2 && part.isSingleton()) {
                    list.addAll(Arrays.asList(part.getParticleChildren()));
                    return;
                }
                break;
            }
        }
        list.add(part);
    }
    
    static Map buildAttributePropertyModelByQName(final SchemaAttributeModel attrModel, final SchemaType owner) {
        final Map result = new LinkedHashMap();
        final SchemaLocalAttribute[] attruses = attrModel.getAttributes();
        for (int i = 0; i < attruses.length; ++i) {
            result.put(attruses[i].getName(), buildUseProperty(attruses[i], owner));
        }
        return result;
    }
    
    static Map buildContentPropertyModelByQName(final SchemaParticle part, final SchemaType owner) {
        if (part == null) {
            return Collections.EMPTY_MAP;
        }
        boolean asSequence = false;
        Map model = null;
        switch (part.getParticleType()) {
            case 1:
            case 3: {
                asSequence = true;
                break;
            }
            case 2: {
                asSequence = false;
                break;
            }
            case 4: {
                model = buildElementPropertyModel((SchemaLocalElement)part, owner);
                break;
            }
            case 5: {
                model = Collections.EMPTY_MAP;
                break;
            }
            default: {
                assert false;
                throw new IllegalStateException();
            }
        }
        if (model == null) {
            model = new LinkedHashMap();
            final SchemaParticle[] children = part.getParticleChildren();
            for (int i = 0; i < children.length; ++i) {
                final Map childModel = buildContentPropertyModelByQName(children[i], owner);
                for (final SchemaProperty iProp : childModel.values()) {
                    final SchemaPropertyImpl oProp = model.get(iProp.getName());
                    if (oProp == null) {
                        if (!asSequence) {
                            ((SchemaPropertyImpl)iProp).setMinOccurs(BigInteger.ZERO);
                        }
                        model.put(iProp.getName(), iProp);
                    }
                    else {
                        assert oProp.getType().equals(iProp.getType());
                        mergeProperties(oProp, iProp, asSequence);
                    }
                }
            }
            final BigInteger min = part.getMinOccurs();
            final BigInteger max = part.getMaxOccurs();
            for (final SchemaProperty oProp2 : model.values()) {
                BigInteger minOccurs = oProp2.getMinOccurs();
                BigInteger maxOccurs = oProp2.getMaxOccurs();
                minOccurs = minOccurs.multiply(min);
                if (max != null && max.equals(BigInteger.ZERO)) {
                    maxOccurs = BigInteger.ZERO;
                }
                else if (maxOccurs != null && !maxOccurs.equals(BigInteger.ZERO)) {
                    maxOccurs = ((max == null) ? null : maxOccurs.multiply(max));
                }
                ((SchemaPropertyImpl)oProp2).setMinOccurs(minOccurs);
                ((SchemaPropertyImpl)oProp2).setMaxOccurs(maxOccurs);
            }
        }
        return model;
    }
    
    static Map buildElementPropertyModel(final SchemaLocalElement epart, final SchemaType owner) {
        final Map result = new HashMap(1);
        final SchemaProperty sProp = buildUseProperty(epart, owner);
        result.put(sProp.getName(), sProp);
        return result;
    }
    
    static SchemaProperty buildUseProperty(final SchemaField use, final SchemaType owner) {
        final SchemaPropertyImpl sPropImpl = new SchemaPropertyImpl();
        sPropImpl.setName(use.getName());
        sPropImpl.setContainerTypeRef(owner.getRef());
        sPropImpl.setTypeRef(use.getType().getRef());
        sPropImpl.setAttribute(use.isAttribute());
        sPropImpl.setDefault(use.isDefault() ? 2 : 0);
        sPropImpl.setFixed(use.isFixed() ? 2 : 0);
        sPropImpl.setNillable(use.isNillable() ? 2 : 0);
        sPropImpl.setDefaultText(use.getDefaultText());
        sPropImpl.setMinOccurs(use.getMinOccurs());
        sPropImpl.setMaxOccurs(use.getMaxOccurs());
        if (use instanceof SchemaLocalElementImpl) {
            final SchemaLocalElementImpl elt = (SchemaLocalElementImpl)use;
            sPropImpl.setAcceptedNames(elt.acceptedStartNames());
        }
        return sPropImpl;
    }
    
    static void mergeProperties(final SchemaPropertyImpl into, final SchemaProperty from, final boolean asSequence) {
        BigInteger minOccurs = into.getMinOccurs();
        BigInteger maxOccurs = into.getMaxOccurs();
        if (asSequence) {
            minOccurs = minOccurs.add(from.getMinOccurs());
            if (maxOccurs != null) {
                maxOccurs = ((from.getMaxOccurs() == null) ? null : maxOccurs.add(from.getMaxOccurs()));
            }
        }
        else {
            minOccurs = minOccurs.min(from.getMinOccurs());
            if (maxOccurs != null) {
                maxOccurs = ((from.getMaxOccurs() == null) ? null : maxOccurs.max(from.getMaxOccurs()));
            }
        }
        into.setMinOccurs(minOccurs);
        into.setMaxOccurs(maxOccurs);
        if (from.hasNillable() != into.hasNillable()) {
            into.setNillable(1);
        }
        if (from.hasDefault() != into.hasDefault()) {
            into.setDefault(1);
        }
        if (from.hasFixed() != into.hasFixed()) {
            into.setFixed(1);
        }
        if (into.getDefaultText() != null && (from.getDefaultText() == null || !into.getDefaultText().equals(from.getDefaultText()))) {
            into.setDefaultText(null);
        }
    }
    
    static SchemaParticle[] ensureStateMachine(final SchemaParticle[] children) {
        for (int i = 0; i < children.length; ++i) {
            buildStateMachine(children[i]);
        }
        return children;
    }
    
    static void buildStateMachine(final SchemaParticle contentModel) {
        if (contentModel == null) {
            return;
        }
        final SchemaParticleImpl partImpl = (SchemaParticleImpl)contentModel;
        if (partImpl.hasTransitionNotes()) {
            return;
        }
        final QNameSetBuilder start = new QNameSetBuilder();
        final QNameSetBuilder excludenext = new QNameSetBuilder();
        boolean deterministic = true;
        SchemaParticle[] children = null;
        boolean canskip = partImpl.getMinOccurs().signum() == 0;
        switch (partImpl.getParticleType()) {
            case 4: {
                if (partImpl.hasTransitionRules()) {
                    start.addAll(partImpl.acceptedStartNames());
                    break;
                }
                start.add(partImpl.getName());
                break;
            }
            case 5: {
                start.addAll(partImpl.getWildcardSet());
                break;
            }
            case 3: {
                children = ensureStateMachine(partImpl.getParticleChildren());
                canskip = true;
                for (int i = 0; canskip && i < children.length; ++i) {
                    if (!children[i].isSkippable()) {
                        canskip = false;
                    }
                }
                for (int i = 0; deterministic && i < children.length; ++i) {
                    if (!((SchemaParticleImpl)children[i]).isDeterministic()) {
                        deterministic = false;
                    }
                }
                for (int i = 1; i < children.length; ++i) {
                    excludenext.addAll(((SchemaParticleImpl)children[i - 1]).getExcludeNextSet());
                    if (deterministic && !excludenext.isDisjoint(children[i].acceptedStartNames())) {
                        deterministic = false;
                    }
                    if (children[i].isSkippable()) {
                        excludenext.addAll(children[i].acceptedStartNames());
                    }
                    else {
                        excludenext.clear();
                    }
                }
                for (int i = 0; i < children.length; ++i) {
                    start.addAll(children[i].acceptedStartNames());
                    if (!children[i].isSkippable()) {
                        break;
                    }
                }
                break;
            }
            case 2: {
                children = ensureStateMachine(partImpl.getParticleChildren());
                canskip = false;
                for (int i = 0; !canskip && i < children.length; ++i) {
                    if (children[i].isSkippable()) {
                        canskip = true;
                    }
                }
                for (int i = 0; deterministic && i < children.length; ++i) {
                    if (!((SchemaParticleImpl)children[i]).isDeterministic()) {
                        deterministic = false;
                    }
                }
                for (int i = 0; i < children.length; ++i) {
                    if (deterministic && !start.isDisjoint(children[i].acceptedStartNames())) {
                        deterministic = false;
                    }
                    start.addAll(children[i].acceptedStartNames());
                    excludenext.addAll(((SchemaParticleImpl)children[i]).getExcludeNextSet());
                }
                break;
            }
            case 1: {
                children = ensureStateMachine(partImpl.getParticleChildren());
                canskip = true;
                for (int i = 0; !canskip && i < children.length; ++i) {
                    if (!children[i].isSkippable()) {
                        canskip = false;
                    }
                }
                for (int i = 0; deterministic && i < children.length; ++i) {
                    if (!((SchemaParticleImpl)children[i]).isDeterministic()) {
                        deterministic = false;
                    }
                }
                for (int i = 0; i < children.length; ++i) {
                    if (deterministic && !start.isDisjoint(children[i].acceptedStartNames())) {
                        deterministic = false;
                    }
                    start.addAll(children[i].acceptedStartNames());
                    excludenext.addAll(((SchemaParticleImpl)children[i]).getExcludeNextSet());
                }
                if (canskip) {
                    excludenext.addAll(start);
                    break;
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unrecognized schema particle");
            }
        }
        final BigInteger minOccurs = partImpl.getMinOccurs();
        final BigInteger maxOccurs = partImpl.getMaxOccurs();
        final boolean canloop = maxOccurs == null || maxOccurs.compareTo(BigInteger.ONE) > 0;
        final boolean varloop = maxOccurs == null || minOccurs.compareTo(maxOccurs) < 0;
        if (canloop && deterministic && !excludenext.isDisjoint(start)) {
            final QNameSet suspectSet = excludenext.intersect(start);
            final Map startMap = new HashMap();
            particlesMatchingStart(partImpl, suspectSet, startMap, new QNameSetBuilder());
            final Map afterMap = new HashMap();
            particlesMatchingAfter(partImpl, suspectSet, afterMap, new QNameSetBuilder(), true);
            deterministic = afterMapSubsumedByStartMap(startMap, afterMap);
        }
        if (varloop) {
            excludenext.addAll(start);
        }
        canskip = (canskip || minOccurs.signum() == 0);
        partImpl.setTransitionRules(start.toQNameSet(), canskip);
        partImpl.setTransitionNotes(excludenext.toQNameSet(), deterministic);
    }
    
    private static boolean afterMapSubsumedByStartMap(final Map startMap, final Map afterMap) {
        if (afterMap.size() > startMap.size()) {
            return false;
        }
        if (afterMap.isEmpty()) {
            return true;
        }
        for (final SchemaParticle part : startMap.keySet()) {
            if (part.getParticleType() == 5 && afterMap.containsKey(part)) {
                final QNameSet startSet = startMap.get(part);
                final QNameSet afterSet = afterMap.get(part);
                if (!startSet.containsAll(afterSet)) {
                    return false;
                }
            }
            afterMap.remove(part);
            if (afterMap.isEmpty()) {
                return true;
            }
        }
        return afterMap.isEmpty();
    }
    
    private static void particlesMatchingStart(final SchemaParticle part, final QNameSetSpecification suspectSet, final Map result, final QNameSetBuilder eliminate) {
        switch (part.getParticleType()) {
            case 4: {
                if (!suspectSet.contains(part.getName())) {
                    return;
                }
                result.put(part, null);
                eliminate.add(part.getName());
                return;
            }
            case 5: {
                if (suspectSet.isDisjoint(part.getWildcardSet())) {
                    return;
                }
                result.put(part, part.getWildcardSet().intersect(suspectSet));
                eliminate.addAll(part.getWildcardSet());
                return;
            }
            case 1:
            case 2: {
                final SchemaParticle[] children = part.getParticleChildren();
                for (int i = 0; i < children.length; ++i) {
                    particlesMatchingStart(children[i], suspectSet, result, eliminate);
                }
                return;
            }
            case 3: {
                final SchemaParticle[] children = part.getParticleChildren();
                if (children.length == 0) {
                    return;
                }
                if (!children[0].isSkippable()) {
                    particlesMatchingStart(children[0], suspectSet, result, eliminate);
                    return;
                }
                final QNameSetBuilder remainingSuspects = new QNameSetBuilder(suspectSet);
                final QNameSetBuilder suspectsToEliminate = new QNameSetBuilder();
                for (int j = 0; j < children.length; ++j) {
                    particlesMatchingStart(children[j], remainingSuspects, result, suspectsToEliminate);
                    eliminate.addAll(suspectsToEliminate);
                    if (!children[j].isSkippable()) {
                        return;
                    }
                    remainingSuspects.removeAll(suspectsToEliminate);
                    if (remainingSuspects.isEmpty()) {
                        return;
                    }
                    suspectsToEliminate.clear();
                }
            }
            default: {}
        }
    }
    
    private static void particlesMatchingAfter(final SchemaParticle part, final QNameSetSpecification suspectSet, final Map result, final QNameSetBuilder eliminate, final boolean top) {
        switch (part.getParticleType()) {
            case 1:
            case 2: {
                final SchemaParticle[] children = part.getParticleChildren();
                for (int i = 0; i < children.length; ++i) {
                    particlesMatchingAfter(children[i], suspectSet, result, eliminate, false);
                }
                break;
            }
            case 3: {
                final SchemaParticle[] children = part.getParticleChildren();
                if (children.length == 0) {
                    break;
                }
                if (!children[children.length - 1].isSkippable()) {
                    particlesMatchingAfter(children[0], suspectSet, result, eliminate, false);
                    break;
                }
                final QNameSetBuilder remainingSuspects = new QNameSetBuilder(suspectSet);
                final QNameSetBuilder suspectsToEliminate = new QNameSetBuilder();
                for (int j = children.length - 1; j >= 0; --j) {
                    particlesMatchingAfter(children[j], remainingSuspects, result, suspectsToEliminate, false);
                    eliminate.addAll(suspectsToEliminate);
                    if (!children[j].isSkippable()) {
                        break;
                    }
                    remainingSuspects.removeAll(suspectsToEliminate);
                    if (remainingSuspects.isEmpty()) {
                        break;
                    }
                    suspectsToEliminate.clear();
                }
                break;
            }
        }
        if (!top) {
            final BigInteger minOccurs = part.getMinOccurs();
            final BigInteger maxOccurs = part.getMaxOccurs();
            final boolean varloop = maxOccurs == null || minOccurs.compareTo(maxOccurs) < 0;
            if (varloop) {
                particlesMatchingStart(part, suspectSet, result, eliminate);
            }
        }
    }
    
    private static Map buildParticleCodeMap() {
        final Map result = new HashMap();
        for (int i = 0; i < StscComplexTypeResolver.particleCodes.length; ++i) {
            result.put(StscComplexTypeResolver.particleCodes[i].name, new Integer(StscComplexTypeResolver.particleCodes[i].code));
        }
        return result;
    }
    
    private static int translateParticleCode(final Group parseEg) {
        if (parseEg == null) {
            return 0;
        }
        return translateParticleCode(parseEg.newCursor().getName());
    }
    
    private static int translateParticleCode(final QName name) {
        final Integer result = StscComplexTypeResolver.particleCodeMap.get(name);
        if (result == null) {
            return 0;
        }
        return result;
    }
    
    private static Map buildAttributeCodeMap() {
        final Map result = new HashMap();
        for (int i = 0; i < StscComplexTypeResolver.attributeCodes.length; ++i) {
            result.put(StscComplexTypeResolver.attributeCodes[i].name, new Integer(StscComplexTypeResolver.attributeCodes[i].code));
        }
        return result;
    }
    
    static int translateAttributeCode(final QName currentName) {
        final Integer result = StscComplexTypeResolver.attributeCodeMap.get(currentName);
        if (result == null) {
            return 0;
        }
        return result;
    }
    
    static {
        StscComplexTypeResolver.particleCodes = new CodeForNameEntry[] { new CodeForNameEntry(QNameHelper.forLNS("all", "http://www.w3.org/2001/XMLSchema"), 1), new CodeForNameEntry(QNameHelper.forLNS("sequence", "http://www.w3.org/2001/XMLSchema"), 3), new CodeForNameEntry(QNameHelper.forLNS("choice", "http://www.w3.org/2001/XMLSchema"), 2), new CodeForNameEntry(QNameHelper.forLNS("element", "http://www.w3.org/2001/XMLSchema"), 4), new CodeForNameEntry(QNameHelper.forLNS("any", "http://www.w3.org/2001/XMLSchema"), 5), new CodeForNameEntry(QNameHelper.forLNS("group", "http://www.w3.org/2001/XMLSchema"), 100) };
        StscComplexTypeResolver.particleCodeMap = buildParticleCodeMap();
        StscComplexTypeResolver.attributeCodes = new CodeForNameEntry[] { new CodeForNameEntry(QNameHelper.forLNS("attribute", "http://www.w3.org/2001/XMLSchema"), 100), new CodeForNameEntry(QNameHelper.forLNS("attributeGroup", "http://www.w3.org/2001/XMLSchema"), 101), new CodeForNameEntry(QNameHelper.forLNS("anyAttribute", "http://www.w3.org/2001/XMLSchema"), 102) };
        StscComplexTypeResolver.attributeCodeMap = buildAttributeCodeMap();
    }
    
    static class WildcardResult
    {
        QNameSet typedWildcards;
        boolean hasWildcards;
        
        WildcardResult(final QNameSet typedWildcards, final boolean hasWildcards) {
            this.typedWildcards = typedWildcards;
            this.hasWildcards = hasWildcards;
        }
    }
    
    private static class RedefinitionForGroup
    {
        private SchemaModelGroupImpl group;
        private boolean seenRedefinition;
        
        public RedefinitionForGroup(final SchemaModelGroupImpl group) {
            this.seenRedefinition = false;
            this.group = group;
        }
        
        public SchemaModelGroupImpl getGroup() {
            return this.group;
        }
        
        public boolean isSeenRedefinition() {
            return this.seenRedefinition;
        }
        
        public void setSeenRedefinition(final boolean seenRedefinition) {
            this.seenRedefinition = seenRedefinition;
        }
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
