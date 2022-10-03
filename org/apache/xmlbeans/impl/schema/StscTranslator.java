package org.apache.xmlbeans.impl.schema;

import java.util.Collections;
import org.apache.xmlbeans.SchemaBookmark;
import org.apache.xmlbeans.impl.values.XmlPositiveIntegerImpl;
import org.apache.xmlbeans.XmlPositiveInteger;
import org.apache.xmlbeans.impl.values.XmlNonNegativeIntegerImpl;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaAttributeModel;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import java.util.Map;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
import org.apache.xmlbeans.impl.common.XPath;
import java.util.HashMap;
import org.apache.xmlbeans.impl.xb.xsdschema.KeyrefDocument;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.impl.values.NamespaceContext;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.QNameSetSpecification;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.QNameSetBuilder;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalElement;
import org.apache.xmlbeans.impl.xb.xsdschema.Element;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleType;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.common.XMLChar;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import java.util.Iterator;
import java.util.List;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.RedefineDocument;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.regex.RegularExpression;
import javax.xml.namespace.QName;

public class StscTranslator
{
    private static final QName WSDL_ARRAYTYPE_NAME;
    private static final String FORM_QUALIFIED = "qualified";
    public static final RegularExpression XPATH_REGEXP;
    
    public static void addAllDefinitions(final StscImporter.SchemaToProcess[] schemasAndChameleons) {
        final List redefinitions = new ArrayList();
        for (int i = 0; i < schemasAndChameleons.length; ++i) {
            final List redefines = schemasAndChameleons[i].getRedefines();
            if (redefines != null) {
                final List redefineObjects = schemasAndChameleons[i].getRedefineObjects();
                final Iterator it = redefines.iterator();
                final Iterator ito = redefineObjects.iterator();
                while (it.hasNext()) {
                    assert ito.hasNext() : "The array of redefines and redefine objects have to have the same length";
                    redefinitions.add(new RedefinitionHolder(it.next(), ito.next()));
                }
            }
        }
        final RedefinitionMaster globalRedefinitions = new RedefinitionMaster(redefinitions.toArray(new RedefinitionHolder[redefinitions.size()]));
        final StscState state = StscState.get();
        for (int j = 0; j < schemasAndChameleons.length; ++j) {
            final SchemaDocument.Schema schema = schemasAndChameleons[j].getSchema();
            final String givenTargetNamespace = schemasAndChameleons[j].getChameleonNamespace();
            if (schema.sizeOfNotationArray() > 0) {
                state.warning("Schema <notation> is not yet supported for this release.", 51, schema.getNotationArray(0));
            }
            String targetNamespace = schema.getTargetNamespace();
            boolean chameleon = false;
            if (givenTargetNamespace != null && targetNamespace == null) {
                targetNamespace = givenTargetNamespace;
                chameleon = true;
            }
            if (targetNamespace == null) {
                targetNamespace = "";
            }
            if (targetNamespace.length() > 0 || !isEmptySchema(schema)) {
                state.registerContribution(targetNamespace, schema.documentProperties().getSourceName());
                state.addNewContainer(targetNamespace);
            }
            final List redefChain = new ArrayList();
            final TopLevelComplexType[] complexTypes = schema.getComplexTypeArray();
            for (int k = 0; k < complexTypes.length; ++k) {
                TopLevelComplexType type = complexTypes[k];
                final RedefinitionHolder[] rhArray = globalRedefinitions.getComplexTypeRedefinitions(type.getName(), schemasAndChameleons[j]);
                for (int l = 0; l < rhArray.length; ++l) {
                    if (rhArray[l] != null) {
                        final TopLevelComplexType redef = rhArray[l].redefineComplexType(type.getName());
                        assert redef != null;
                        redefChain.add(type);
                        type = redef;
                    }
                }
                SchemaTypeImpl t = translateGlobalComplexType(type, targetNamespace, chameleon, redefChain.size() > 0);
                state.addGlobalType(t, null);
                for (int m = redefChain.size() - 1; m >= 0; --m) {
                    final TopLevelComplexType redef = redefChain.remove(m);
                    final SchemaTypeImpl r = translateGlobalComplexType(redef, targetNamespace, chameleon, m > 0);
                    state.addGlobalType(r, t);
                    t = r;
                }
            }
            final TopLevelSimpleType[] simpleTypes = schema.getSimpleTypeArray();
            for (int i2 = 0; i2 < simpleTypes.length; ++i2) {
                TopLevelSimpleType type2 = simpleTypes[i2];
                final RedefinitionHolder[] rhArray2 = globalRedefinitions.getSimpleTypeRedefinitions(type2.getName(), schemasAndChameleons[j]);
                for (int k2 = 0; k2 < rhArray2.length; ++k2) {
                    if (rhArray2[k2] != null) {
                        final TopLevelSimpleType redef2 = rhArray2[k2].redefineSimpleType(type2.getName());
                        assert redef2 != null;
                        redefChain.add(type2);
                        type2 = redef2;
                    }
                }
                SchemaTypeImpl t2 = translateGlobalSimpleType(type2, targetNamespace, chameleon, redefChain.size() > 0);
                state.addGlobalType(t2, null);
                for (int k3 = redefChain.size() - 1; k3 >= 0; --k3) {
                    final TopLevelSimpleType redef2 = redefChain.remove(k3);
                    final SchemaTypeImpl r2 = translateGlobalSimpleType(redef2, targetNamespace, chameleon, k3 > 0);
                    state.addGlobalType(r2, t2);
                    t2 = r2;
                }
            }
            final TopLevelElement[] elements = schema.getElementArray();
            for (int i3 = 0; i3 < elements.length; ++i3) {
                final TopLevelElement element = elements[i3];
                state.addDocumentType(translateDocumentType(element, targetNamespace, chameleon), QNameHelper.forLNS(element.getName(), targetNamespace));
            }
            final TopLevelAttribute[] attributes = schema.getAttributeArray();
            for (int i4 = 0; i4 < attributes.length; ++i4) {
                final TopLevelAttribute attribute = attributes[i4];
                state.addAttributeType(translateAttributeType(attribute, targetNamespace, chameleon), QNameHelper.forLNS(attribute.getName(), targetNamespace));
            }
            final NamedGroup[] modelgroups = schema.getGroupArray();
            for (int i5 = 0; i5 < modelgroups.length; ++i5) {
                NamedGroup group = modelgroups[i5];
                final RedefinitionHolder[] rhArray3 = globalRedefinitions.getModelGroupRedefinitions(group.getName(), schemasAndChameleons[j]);
                for (int k4 = 0; k4 < rhArray3.length; ++k4) {
                    if (rhArray3[k4] != null) {
                        final NamedGroup redef3 = rhArray3[k4].redefineModelGroup(group.getName());
                        assert redef3 != null;
                        redefChain.add(group);
                        group = redef3;
                    }
                }
                SchemaModelGroupImpl g = translateModelGroup(group, targetNamespace, chameleon, redefChain.size() > 0);
                state.addModelGroup(g, null);
                for (int k5 = redefChain.size() - 1; k5 >= 0; --k5) {
                    final NamedGroup redef3 = redefChain.remove(k5);
                    final SchemaModelGroupImpl r3 = translateModelGroup(redef3, targetNamespace, chameleon, k5 > 0);
                    state.addModelGroup(r3, g);
                    g = r3;
                }
            }
            final NamedAttributeGroup[] attrgroups = schema.getAttributeGroupArray();
            for (int i6 = 0; i6 < attrgroups.length; ++i6) {
                NamedAttributeGroup group2 = attrgroups[i6];
                final RedefinitionHolder[] rhArray4 = globalRedefinitions.getAttributeGroupRedefinitions(group2.getName(), schemasAndChameleons[j]);
                for (int k6 = 0; k6 < rhArray4.length; ++k6) {
                    if (rhArray4[k6] != null) {
                        final NamedAttributeGroup redef4 = rhArray4[k6].redefineAttributeGroup(group2.getName());
                        assert redef4 != null;
                        redefChain.add(group2);
                        group2 = redef4;
                    }
                }
                SchemaAttributeGroupImpl g2 = translateAttributeGroup(group2, targetNamespace, chameleon, redefChain.size() > 0);
                state.addAttributeGroup(g2, null);
                for (int k7 = redefChain.size() - 1; k7 >= 0; --k7) {
                    final NamedAttributeGroup redef4 = redefChain.remove(k7);
                    final SchemaAttributeGroupImpl r4 = translateAttributeGroup(redef4, targetNamespace, chameleon, k7 > 0);
                    state.addAttributeGroup(r4, g2);
                    g2 = r4;
                }
            }
            final AnnotationDocument.Annotation[] annotations = schema.getAnnotationArray();
            for (int i7 = 0; i7 < annotations.length; ++i7) {
                state.addAnnotation(SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), schema, annotations[i7]), targetNamespace);
            }
        }
        for (int i8 = 0; i8 < redefinitions.size(); ++i8) {
            redefinitions.get(i8).complainAboutMissingDefinitions();
        }
    }
    
    private static String findFilename(final XmlObject xobj) {
        return StscState.get().sourceNameForUri(xobj.documentProperties().getSourceName());
    }
    
    private static SchemaTypeImpl translateDocumentType(final TopLevelElement xsdType, final String targetNamespace, final boolean chameleon) {
        final SchemaTypeImpl sType = new SchemaTypeImpl(StscState.get().getContainer(targetNamespace));
        sType.setDocumentType(true);
        sType.setParseContext(xsdType, targetNamespace, chameleon, null, null, false);
        sType.setFilename(findFilename(xsdType));
        return sType;
    }
    
    private static SchemaTypeImpl translateAttributeType(final TopLevelAttribute xsdType, final String targetNamespace, final boolean chameleon) {
        final SchemaTypeImpl sType = new SchemaTypeImpl(StscState.get().getContainer(targetNamespace));
        sType.setAttributeType(true);
        sType.setParseContext(xsdType, targetNamespace, chameleon, null, null, false);
        sType.setFilename(findFilename(xsdType));
        return sType;
    }
    
    private static SchemaTypeImpl translateGlobalComplexType(final TopLevelComplexType xsdType, final String targetNamespace, final boolean chameleon, final boolean redefinition) {
        final StscState state = StscState.get();
        final String localname = xsdType.getName();
        if (localname == null) {
            state.error("missing-name", new Object[] { "global type" }, xsdType);
            return null;
        }
        if (!XMLChar.isValidNCName(localname)) {
            state.error("invalid-value", new Object[] { localname, "name" }, xsdType.xgetName());
        }
        final QName name = QNameHelper.forLNS(localname, targetNamespace);
        if (isReservedTypeName(name)) {
            state.warning("reserved-type-name", new Object[] { QNameHelper.pretty(name) }, xsdType);
            return null;
        }
        final SchemaTypeImpl sType = new SchemaTypeImpl(state.getContainer(targetNamespace));
        sType.setParseContext(xsdType, targetNamespace, chameleon, null, null, redefinition);
        sType.setFilename(findFilename(xsdType));
        sType.setName(QNameHelper.forLNS(localname, targetNamespace));
        sType.setAnnotation(SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), xsdType));
        sType.setUserData(getUserData(xsdType));
        return sType;
    }
    
    private static SchemaTypeImpl translateGlobalSimpleType(final TopLevelSimpleType xsdType, final String targetNamespace, final boolean chameleon, final boolean redefinition) {
        final StscState state = StscState.get();
        final String localname = xsdType.getName();
        if (localname == null) {
            state.error("missing-name", new Object[] { "global type" }, xsdType);
            return null;
        }
        if (!XMLChar.isValidNCName(localname)) {
            state.error("invalid-value", new Object[] { localname, "name" }, xsdType.xgetName());
        }
        final QName name = QNameHelper.forLNS(localname, targetNamespace);
        if (isReservedTypeName(name)) {
            state.warning("reserved-type-name", new Object[] { QNameHelper.pretty(name) }, xsdType);
            return null;
        }
        final SchemaTypeImpl sType = new SchemaTypeImpl(state.getContainer(targetNamespace));
        sType.setSimpleType(true);
        sType.setParseContext(xsdType, targetNamespace, chameleon, null, null, redefinition);
        sType.setFilename(findFilename(xsdType));
        sType.setName(name);
        sType.setAnnotation(SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), xsdType));
        sType.setUserData(getUserData(xsdType));
        return sType;
    }
    
    static SchemaTypeImpl translateAnonymousSimpleType(final SimpleType typedef, final String targetNamespace, final boolean chameleon, final String elemFormDefault, final String attFormDefault, final List anonymousTypes, final SchemaType outerType) {
        final StscState state = StscState.get();
        final SchemaTypeImpl sType = new SchemaTypeImpl(state.getContainer(targetNamespace));
        sType.setSimpleType(true);
        sType.setParseContext(typedef, targetNamespace, chameleon, elemFormDefault, attFormDefault, false);
        sType.setOuterSchemaTypeRef(outerType.getRef());
        sType.setAnnotation(SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), typedef));
        sType.setUserData(getUserData(typedef));
        anonymousTypes.add(sType);
        return sType;
    }
    
    static FormChoice findElementFormDefault(final XmlObject obj) {
        final XmlCursor cur = obj.newCursor();
        while (cur.getObject().schemaType() != SchemaDocument.Schema.type) {
            if (!cur.toParent()) {
                return null;
            }
        }
        return ((SchemaDocument.Schema)cur.getObject()).xgetElementFormDefault();
    }
    
    public static boolean uriMatch(final String s1, final String s2) {
        if (s1 == null) {
            return s2 == null || s2.equals("");
        }
        if (s2 == null) {
            return s1.equals("");
        }
        return s1.equals(s2);
    }
    
    public static void copyGlobalElementToLocalElement(final SchemaGlobalElement referenced, final SchemaLocalElementImpl target) {
        target.setNameAndTypeRef(referenced.getName(), referenced.getType().getRef());
        target.setNillable(referenced.isNillable());
        target.setDefault(referenced.getDefaultText(), referenced.isFixed(), ((SchemaGlobalElementImpl)referenced).getParseObject());
        target.setIdentityConstraints(((SchemaLocalElementImpl)referenced).getIdentityConstraintRefs());
        target.setBlock(referenced.blockExtension(), referenced.blockRestriction(), referenced.blockSubstitution());
        target.setAbstract(referenced.isAbstract());
        target.setTransitionRules(((SchemaParticle)referenced).acceptedStartNames(), ((SchemaParticle)referenced).isSkippable());
        target.setAnnotation(referenced.getAnnotation());
    }
    
    public static void copyGlobalAttributeToLocalAttribute(final SchemaGlobalAttributeImpl referenced, final SchemaLocalAttributeImpl target) {
        target.init(referenced.getName(), referenced.getTypeRef(), referenced.getUse(), referenced.getDefaultText(), referenced.getParseObject(), referenced._defaultValue, referenced.isFixed(), referenced.getWSDLArrayType(), referenced.getAnnotation(), null);
    }
    
    public static SchemaLocalElementImpl translateElement(final Element xsdElt, final String targetNamespace, final boolean chameleon, final String elemFormDefault, final String attFormDefault, final List anonymousTypes, final SchemaType outerType) {
        final StscState state = StscState.get();
        SchemaTypeImpl sgHead = null;
        if (xsdElt.isSetSubstitutionGroup()) {
            sgHead = state.findDocumentType(xsdElt.getSubstitutionGroup(), ((SchemaTypeImpl)outerType).getChameleonNamespace(), targetNamespace);
            if (sgHead != null) {
                StscResolver.resolveType(sgHead);
            }
        }
        String name = xsdElt.getName();
        final QName ref = xsdElt.getRef();
        if (ref != null && name != null) {
            state.error("src-element.2.1a", new Object[] { name }, xsdElt.xgetRef());
            name = null;
        }
        if (ref == null && name == null) {
            state.error("src-element.2.1b", null, xsdElt);
            return null;
        }
        if (name != null && !XMLChar.isValidNCName(name)) {
            state.error("invalid-value", new Object[] { name, "name" }, xsdElt.xgetName());
        }
        if (ref == null) {
            SchemaType sType = null;
            SchemaLocalElementImpl impl;
            QName qname;
            if (xsdElt instanceof LocalElement) {
                impl = new SchemaLocalElementImpl();
                boolean qualified = false;
                FormChoice form = xsdElt.xgetForm();
                if (form != null) {
                    qualified = form.getStringValue().equals("qualified");
                }
                else if (elemFormDefault != null) {
                    qualified = elemFormDefault.equals("qualified");
                }
                else {
                    form = findElementFormDefault(xsdElt);
                    qualified = (form != null && form.getStringValue().equals("qualified"));
                }
                qname = (qualified ? QNameHelper.forLNS(name, targetNamespace) : QNameHelper.forLN(name));
            }
            else {
                final SchemaGlobalElementImpl gelt = (SchemaGlobalElementImpl)(impl = new SchemaGlobalElementImpl(state.getContainer(targetNamespace)));
                if (sgHead != null) {
                    final SchemaGlobalElementImpl head = state.findGlobalElement(xsdElt.getSubstitutionGroup(), chameleon ? targetNamespace : null, targetNamespace);
                    if (head != null) {
                        gelt.setSubstitutionGroup(head.getRef());
                    }
                }
                qname = QNameHelper.forLNS(name, targetNamespace);
                final SchemaTypeImpl docType = (SchemaTypeImpl)outerType;
                final QName[] sgMembers = docType.getSubstitutionGroupMembers();
                final QNameSetBuilder transitionRules = new QNameSetBuilder();
                transitionRules.add(qname);
                for (int i = 0; i < sgMembers.length; ++i) {
                    gelt.addSubstitutionGroupMember(sgMembers[i]);
                    transitionRules.add(sgMembers[i]);
                }
                impl.setTransitionRules(QNameSet.forSpecification(transitionRules), false);
                impl.setTransitionNotes(QNameSet.EMPTY, true);
                boolean finalExt = false;
                boolean finalRest = false;
                final Object ds = xsdElt.getFinal();
                if (ds != null) {
                    if (ds instanceof String && ds.equals("#all")) {
                        finalRest = (finalExt = true);
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
                gelt.setFinal(finalExt, finalRest);
                gelt.setAbstract(xsdElt.getAbstract());
                gelt.setFilename(findFilename(xsdElt));
                gelt.setParseContext(xsdElt, targetNamespace, chameleon);
            }
            final SchemaAnnotationImpl ann = SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), xsdElt);
            impl.setAnnotation(ann);
            impl.setUserData(getUserData(xsdElt));
            if (xsdElt.getType() != null) {
                sType = state.findGlobalType(xsdElt.getType(), chameleon ? targetNamespace : null, targetNamespace);
                if (sType == null) {
                    state.notFoundError(xsdElt.getType(), 0, xsdElt.xgetType(), true);
                }
            }
            boolean simpleTypedef = false;
            Annotated typedef = xsdElt.getComplexType();
            if (typedef == null) {
                typedef = xsdElt.getSimpleType();
                simpleTypedef = true;
            }
            if (sType != null && typedef != null) {
                state.error("src-element.3", null, typedef);
                typedef = null;
            }
            if (typedef != null) {
                final Object[] grps = state.getCurrentProcessing();
                final QName[] context = new QName[grps.length];
                for (int j = 0; j < context.length; ++j) {
                    if (grps[j] instanceof SchemaModelGroupImpl) {
                        context[j] = ((SchemaModelGroupImpl)grps[j]).getName();
                    }
                }
                final SchemaType repeat = checkRecursiveGroupReference(context, qname, (SchemaTypeImpl)outerType);
                if (repeat != null) {
                    sType = repeat;
                }
                else {
                    final SchemaTypeImpl sTypeImpl = (SchemaTypeImpl)(sType = new SchemaTypeImpl(state.getContainer(targetNamespace)));
                    sTypeImpl.setContainerField(impl);
                    sTypeImpl.setOuterSchemaTypeRef((outerType == null) ? null : outerType.getRef());
                    sTypeImpl.setGroupReferenceContext(context);
                    anonymousTypes.add(sType);
                    sTypeImpl.setSimpleType(simpleTypedef);
                    sTypeImpl.setParseContext(typedef, targetNamespace, chameleon, elemFormDefault, attFormDefault, false);
                    sTypeImpl.setAnnotation(SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), typedef));
                    sTypeImpl.setUserData(getUserData(typedef));
                }
            }
            if (sType == null && sgHead != null) {
                final SchemaGlobalElement head2 = state.findGlobalElement(xsdElt.getSubstitutionGroup(), chameleon ? targetNamespace : null, targetNamespace);
                if (head2 != null) {
                    sType = head2.getType();
                }
            }
            if (sType == null) {
                sType = BuiltinSchemaTypeSystem.ST_ANY_TYPE;
            }
            SOAPArrayType wat = null;
            final XmlCursor c = xsdElt.newCursor();
            final String arrayType = c.getAttributeText(StscTranslator.WSDL_ARRAYTYPE_NAME);
            c.dispose();
            if (arrayType != null) {
                try {
                    wat = new SOAPArrayType(arrayType, new NamespaceContext(xsdElt));
                }
                catch (final XmlValueOutOfRangeException e) {
                    state.error("soaparray", new Object[] { arrayType }, xsdElt);
                }
            }
            impl.setWsdlArrayType(wat);
            boolean isFixed = xsdElt.isSetFixed();
            if (xsdElt.isSetDefault() && isFixed) {
                state.error("src-element.1", null, xsdElt.xgetFixed());
                isFixed = false;
            }
            impl.setParticleType(4);
            impl.setNameAndTypeRef(qname, sType.getRef());
            impl.setNillable(xsdElt.getNillable());
            impl.setDefault(isFixed ? xsdElt.getFixed() : xsdElt.getDefault(), isFixed, xsdElt);
            final Object block = xsdElt.getBlock();
            boolean blockExt = false;
            boolean blockRest = false;
            boolean blockSubst = false;
            if (block != null) {
                if (block instanceof String && block.equals("#all")) {
                    blockRest = (blockExt = (blockSubst = true));
                }
                else if (block instanceof List) {
                    if (((List)block).contains("extension")) {
                        blockExt = true;
                    }
                    if (((List)block).contains("restriction")) {
                        blockRest = true;
                    }
                    if (((List)block).contains("substitution")) {
                        blockSubst = true;
                    }
                }
            }
            impl.setBlock(blockExt, blockRest, blockSubst);
            boolean constraintFailed = false;
            final int length = xsdElt.sizeOfKeyArray() + xsdElt.sizeOfKeyrefArray() + xsdElt.sizeOfUniqueArray();
            final SchemaIdentityConstraintImpl[] constraints = new SchemaIdentityConstraintImpl[length];
            int cur = 0;
            final Keybase[] keys = xsdElt.getKeyArray();
            for (int k = 0; k < keys.length; ++k, ++cur) {
                constraints[cur] = translateIdentityConstraint(keys[k], targetNamespace, chameleon);
                if (constraints[cur] != null) {
                    constraints[cur].setConstraintCategory(1);
                }
                else {
                    constraintFailed = true;
                }
            }
            final Keybase[] uc = xsdElt.getUniqueArray();
            for (int l = 0; l < uc.length; ++l, ++cur) {
                constraints[cur] = translateIdentityConstraint(uc[l], targetNamespace, chameleon);
                if (constraints[cur] != null) {
                    constraints[cur].setConstraintCategory(3);
                }
                else {
                    constraintFailed = true;
                }
            }
            final KeyrefDocument.Keyref[] krs = xsdElt.getKeyrefArray();
            for (int m = 0; m < krs.length; ++m, ++cur) {
                constraints[cur] = translateIdentityConstraint(krs[m], targetNamespace, chameleon);
                if (constraints[cur] != null) {
                    constraints[cur].setConstraintCategory(2);
                }
                else {
                    constraintFailed = true;
                }
            }
            if (!constraintFailed) {
                final SchemaIdentityConstraint.Ref[] refs = new SchemaIdentityConstraint.Ref[length];
                for (int i2 = 0; i2 < refs.length; ++i2) {
                    refs[i2] = constraints[i2].getRef();
                }
                impl.setIdentityConstraints(refs);
            }
            return impl;
        }
        if (xsdElt.getType() != null) {
            state.error("src-element.2.2", new Object[] { "type" }, xsdElt.xgetType());
        }
        if (xsdElt.getSimpleType() != null) {
            state.error("src-element.2.2", new Object[] { "<simpleType>" }, xsdElt.getSimpleType());
        }
        if (xsdElt.getComplexType() != null) {
            state.error("src-element.2.2", new Object[] { "<complexType>" }, xsdElt.getComplexType());
        }
        if (xsdElt.getForm() != null) {
            state.error("src-element.2.2", new Object[] { "form" }, xsdElt.xgetForm());
        }
        if (xsdElt.sizeOfKeyArray() > 0) {
            state.warning("src-element.2.2", new Object[] { "<key>" }, xsdElt);
        }
        if (xsdElt.sizeOfKeyrefArray() > 0) {
            state.warning("src-element.2.2", new Object[] { "<keyref>" }, xsdElt);
        }
        if (xsdElt.sizeOfUniqueArray() > 0) {
            state.warning("src-element.2.2", new Object[] { "<unique>" }, xsdElt);
        }
        if (xsdElt.isSetDefault()) {
            state.warning("src-element.2.2", new Object[] { "default" }, xsdElt.xgetDefault());
        }
        if (xsdElt.isSetFixed()) {
            state.warning("src-element.2.2", new Object[] { "fixed" }, xsdElt.xgetFixed());
        }
        if (xsdElt.isSetBlock()) {
            state.warning("src-element.2.2", new Object[] { "block" }, xsdElt.xgetBlock());
        }
        if (xsdElt.isSetNillable()) {
            state.warning("src-element.2.2", new Object[] { "nillable" }, xsdElt.xgetNillable());
        }
        assert xsdElt instanceof LocalElement;
        final SchemaGlobalElement referenced = state.findGlobalElement(ref, chameleon ? targetNamespace : null, targetNamespace);
        if (referenced == null) {
            state.notFoundError(ref, 1, xsdElt.xgetRef(), true);
            return null;
        }
        final SchemaLocalElementImpl target = new SchemaLocalElementImpl();
        target.setParticleType(4);
        target.setUserData(getUserData(xsdElt));
        copyGlobalElementToLocalElement(referenced, target);
        return target;
    }
    
    private static SchemaType checkRecursiveGroupReference(final QName[] context, final QName containingElement, final SchemaTypeImpl outerType) {
        if (context.length < 1) {
            return null;
        }
        for (SchemaTypeImpl type = outerType; type != null; type = (SchemaTypeImpl)type.getOuterType()) {
            if (type.getName() != null || type.isDocumentType()) {
                return null;
            }
            if (containingElement.equals(type.getContainerField().getName())) {
                final QName[] outerContext = type.getGroupReferenceContext();
                if (outerContext != null && outerContext.length == context.length) {
                    boolean equal = true;
                    for (int i = 0; i < context.length; ++i) {
                        if ((context[i] != null || outerContext[i] != null) && (context[i] == null || !context[i].equals(outerContext[i]))) {
                            equal = false;
                            break;
                        }
                    }
                    if (equal) {
                        return type;
                    }
                }
            }
        }
        return null;
    }
    
    private static String removeWhitespace(final String xpath) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < xpath.length(); ++i) {
            final char ch = xpath.charAt(i);
            if (!XMLChar.isSpace(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    private static boolean checkXPathSyntax(String xpath) {
        if (xpath == null) {
            return false;
        }
        xpath = removeWhitespace(xpath);
        synchronized (StscTranslator.XPATH_REGEXP) {
            return StscTranslator.XPATH_REGEXP.matches(xpath);
        }
    }
    
    private static SchemaIdentityConstraintImpl translateIdentityConstraint(final Keybase parseIC, final String targetNamespace, final boolean chameleon) {
        final StscState state = StscState.get();
        final String selector = (parseIC.getSelector() == null) ? null : parseIC.getSelector().getXpath();
        if (!checkXPathSyntax(selector)) {
            state.error("c-selector-xpath", new Object[] { selector }, parseIC.getSelector().xgetXpath());
            return null;
        }
        final FieldDocument.Field[] fieldElts = parseIC.getFieldArray();
        for (int j = 0; j < fieldElts.length; ++j) {
            if (!checkXPathSyntax(fieldElts[j].getXpath())) {
                state.error("c-fields-xpaths", new Object[] { fieldElts[j].getXpath() }, fieldElts[j].xgetXpath());
                return null;
            }
        }
        final SchemaIdentityConstraintImpl ic = new SchemaIdentityConstraintImpl(state.getContainer(targetNamespace));
        ic.setName(QNameHelper.forLNS(parseIC.getName(), targetNamespace));
        ic.setSelector(parseIC.getSelector().getXpath());
        ic.setParseContext(parseIC, targetNamespace, chameleon);
        final SchemaAnnotationImpl ann = SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), parseIC);
        ic.setAnnotation(ann);
        ic.setUserData(getUserData(parseIC));
        final XmlCursor c = parseIC.newCursor();
        final Map nsMap = new HashMap();
        c.getAllNamespaces(nsMap);
        nsMap.remove("");
        ic.setNSMap(nsMap);
        c.dispose();
        final String[] fields = new String[fieldElts.length];
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = fieldElts[i].getXpath();
        }
        ic.setFields(fields);
        try {
            ic.buildPaths();
        }
        catch (final XPath.XPathCompileException e) {
            state.error("invalid-xpath", new Object[] { e.getMessage() }, parseIC);
            return null;
        }
        state.addIdConstraint(ic);
        ic.setFilename(findFilename(parseIC));
        return state.findIdConstraint(ic.getName(), targetNamespace, null);
    }
    
    public static SchemaModelGroupImpl translateModelGroup(final NamedGroup namedGroup, final String targetNamespace, final boolean chameleon, final boolean redefinition) {
        final String name = namedGroup.getName();
        if (name == null) {
            StscState.get().error("missing-name", new Object[] { "model group" }, namedGroup);
            return null;
        }
        final SchemaContainer c = StscState.get().getContainer(targetNamespace);
        final SchemaModelGroupImpl result = new SchemaModelGroupImpl(c);
        final SchemaAnnotationImpl ann = SchemaAnnotationImpl.getAnnotation(c, namedGroup);
        final FormChoice elemFormDefault = findElementFormDefault(namedGroup);
        final FormChoice attFormDefault = findAttributeFormDefault(namedGroup);
        result.init(QNameHelper.forLNS(name, targetNamespace), targetNamespace, chameleon, (elemFormDefault == null) ? null : elemFormDefault.getStringValue(), (attFormDefault == null) ? null : attFormDefault.getStringValue(), redefinition, namedGroup, ann, getUserData(namedGroup));
        result.setFilename(findFilename(namedGroup));
        return result;
    }
    
    public static SchemaAttributeGroupImpl translateAttributeGroup(final AttributeGroup attrGroup, final String targetNamespace, final boolean chameleon, final boolean redefinition) {
        final String name = attrGroup.getName();
        if (name == null) {
            StscState.get().error("missing-name", new Object[] { "attribute group" }, attrGroup);
            return null;
        }
        final SchemaContainer c = StscState.get().getContainer(targetNamespace);
        final SchemaAttributeGroupImpl result = new SchemaAttributeGroupImpl(c);
        final SchemaAnnotationImpl ann = SchemaAnnotationImpl.getAnnotation(c, attrGroup);
        final FormChoice formDefault = findAttributeFormDefault(attrGroup);
        result.init(QNameHelper.forLNS(name, targetNamespace), targetNamespace, chameleon, (formDefault == null) ? null : formDefault.getStringValue(), redefinition, attrGroup, ann, getUserData(attrGroup));
        result.setFilename(findFilename(attrGroup));
        return result;
    }
    
    static FormChoice findAttributeFormDefault(final XmlObject obj) {
        final XmlCursor cur = obj.newCursor();
        while (cur.getObject().schemaType() != SchemaDocument.Schema.type) {
            if (!cur.toParent()) {
                return null;
            }
        }
        return ((SchemaDocument.Schema)cur.getObject()).xgetAttributeFormDefault();
    }
    
    static SchemaLocalAttributeImpl translateAttribute(final Attribute xsdAttr, final String targetNamespace, final String formDefault, final boolean chameleon, final List anonymousTypes, final SchemaType outerType, final SchemaAttributeModel baseModel, final boolean local) {
        final StscState state = StscState.get();
        String name = xsdAttr.getName();
        final QName ref = xsdAttr.getRef();
        if (ref != null && name != null) {
            if (name.equals(ref.getLocalPart()) && uriMatch(targetNamespace, ref.getNamespaceURI())) {
                state.warning("src-attribute.3.1a", new Object[] { name }, xsdAttr.xgetRef());
            }
            else {
                state.error("src-attribute.3.1a", new Object[] { name }, xsdAttr.xgetRef());
            }
            name = null;
        }
        if (ref == null && name == null) {
            state.error("src-attribute.3.1b", null, xsdAttr);
            return null;
        }
        if (name != null && !XMLChar.isValidNCName(name)) {
            state.error("invalid-value", new Object[] { name, "name" }, xsdAttr.xgetName());
        }
        boolean isFixed = false;
        String deftext = null;
        String fmrfixedtext = null;
        SchemaType sType = null;
        int use = 2;
        SchemaLocalAttributeImpl sAttr;
        if (local) {
            sAttr = new SchemaLocalAttributeImpl();
        }
        else {
            sAttr = new SchemaGlobalAttributeImpl(StscState.get().getContainer(targetNamespace));
            ((SchemaGlobalAttributeImpl)sAttr).setParseContext(xsdAttr, targetNamespace, chameleon);
        }
        QName qname;
        if (ref != null) {
            if (xsdAttr.getType() != null) {
                state.error("src-attribute.3.2", new Object[] { "type" }, xsdAttr.xgetType());
            }
            if (xsdAttr.getSimpleType() != null) {
                state.error("src-attribute.3.2", new Object[] { "<simpleType>" }, xsdAttr.getSimpleType());
            }
            if (xsdAttr.getForm() != null) {
                state.error("src-attribute.3.2", new Object[] { "form" }, xsdAttr.xgetForm());
            }
            final SchemaGlobalAttribute referenced = state.findGlobalAttribute(ref, chameleon ? targetNamespace : null, targetNamespace);
            if (referenced == null) {
                state.notFoundError(ref, 3, xsdAttr.xgetRef(), true);
                return null;
            }
            qname = ref;
            use = referenced.getUse();
            sType = referenced.getType();
            deftext = referenced.getDefaultText();
            if (deftext != null) {
                isFixed = referenced.isFixed();
                if (isFixed) {
                    fmrfixedtext = deftext;
                }
            }
        }
        else {
            if (local) {
                boolean qualified = false;
                FormChoice form = xsdAttr.xgetForm();
                if (form != null) {
                    qualified = form.getStringValue().equals("qualified");
                }
                else if (formDefault != null) {
                    qualified = formDefault.equals("qualified");
                }
                else {
                    form = findAttributeFormDefault(xsdAttr);
                    qualified = (form != null && form.getStringValue().equals("qualified"));
                }
                qname = (qualified ? QNameHelper.forLNS(name, targetNamespace) : QNameHelper.forLN(name));
            }
            else {
                qname = QNameHelper.forLNS(name, targetNamespace);
            }
            if (xsdAttr.getType() != null) {
                sType = state.findGlobalType(xsdAttr.getType(), chameleon ? targetNamespace : null, targetNamespace);
                if (sType == null) {
                    state.notFoundError(xsdAttr.getType(), 0, xsdAttr.xgetType(), true);
                }
            }
            if (qname.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema-instance")) {
                state.error("no-xsi", new Object[] { "http://www.w3.org/2001/XMLSchema-instance" }, xsdAttr.xgetName());
            }
            if (qname.getNamespaceURI().length() == 0 && qname.getLocalPart().equals("xmlns")) {
                state.error("no-xmlns", null, xsdAttr.xgetName());
            }
            LocalSimpleType typedef = xsdAttr.getSimpleType();
            if (sType != null && typedef != null) {
                state.error("src-attribute.4", null, typedef);
                typedef = null;
            }
            if (typedef != null) {
                final SchemaTypeImpl sTypeImpl = (SchemaTypeImpl)(sType = new SchemaTypeImpl(state.getContainer(targetNamespace)));
                sTypeImpl.setContainerField(sAttr);
                sTypeImpl.setOuterSchemaTypeRef((outerType == null) ? null : outerType.getRef());
                anonymousTypes.add(sType);
                sTypeImpl.setSimpleType(true);
                sTypeImpl.setParseContext(typedef, targetNamespace, chameleon, null, null, false);
                sTypeImpl.setAnnotation(SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), typedef));
                sTypeImpl.setUserData(getUserData(typedef));
            }
            if (sType == null && baseModel != null && baseModel.getAttribute(qname) != null) {
                sType = baseModel.getAttribute(qname).getType();
            }
        }
        if (sType == null) {
            sType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        if (!sType.isSimpleType()) {
            state.error("Attributes must have a simple type (not complex).", 46, xsdAttr);
            sType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        if (xsdAttr.isSetUse()) {
            use = translateUseCode(xsdAttr.xgetUse());
            if (use != 2 && !isFixed) {
                deftext = null;
            }
        }
        if (xsdAttr.isSetDefault() || xsdAttr.isSetFixed()) {
            if (isFixed && !xsdAttr.isSetFixed()) {
                state.error("A use of a fixed attribute definition must also be fixed", 9, xsdAttr.xgetFixed());
            }
            isFixed = xsdAttr.isSetFixed();
            if (xsdAttr.isSetDefault() && isFixed) {
                state.error("src-attribute.1", null, xsdAttr.xgetFixed());
                isFixed = false;
            }
            deftext = (isFixed ? xsdAttr.getFixed() : xsdAttr.getDefault());
            if (fmrfixedtext != null && !fmrfixedtext.equals(deftext)) {
                state.error("au-value_constraint", null, xsdAttr.xgetFixed());
                deftext = fmrfixedtext;
            }
        }
        if (!local) {
            ((SchemaGlobalAttributeImpl)sAttr).setFilename(findFilename(xsdAttr));
        }
        SOAPArrayType wat = null;
        final XmlCursor c = xsdAttr.newCursor();
        final String arrayType = c.getAttributeText(StscTranslator.WSDL_ARRAYTYPE_NAME);
        c.dispose();
        if (arrayType != null) {
            try {
                wat = new SOAPArrayType(arrayType, new NamespaceContext(xsdAttr));
            }
            catch (final XmlValueOutOfRangeException e) {
                state.error("soaparray", new Object[] { arrayType }, xsdAttr);
            }
        }
        final SchemaAnnotationImpl ann = SchemaAnnotationImpl.getAnnotation(state.getContainer(targetNamespace), xsdAttr);
        sAttr.init(qname, sType.getRef(), use, deftext, xsdAttr, null, isFixed, wat, ann, getUserData(xsdAttr));
        return sAttr;
    }
    
    static int translateUseCode(final Attribute.Use attruse) {
        if (attruse == null) {
            return 2;
        }
        final String val = attruse.getStringValue();
        if (val.equals("optional")) {
            return 2;
        }
        if (val.equals("required")) {
            return 3;
        }
        if (val.equals("prohibited")) {
            return 1;
        }
        return 2;
    }
    
    static BigInteger buildBigInt(final XmlAnySimpleType value) {
        if (value == null) {
            return null;
        }
        final String text = value.getStringValue();
        BigInteger bigInt;
        try {
            bigInt = new BigInteger(text);
        }
        catch (final NumberFormatException e) {
            StscState.get().error("invalid-value-detail", new Object[] { text, "nonNegativeInteger", e.getMessage() }, value);
            return null;
        }
        if (bigInt.signum() < 0) {
            StscState.get().error("invalid-value", new Object[] { text, "nonNegativeInteger" }, value);
            return null;
        }
        return bigInt;
    }
    
    static XmlNonNegativeInteger buildNnInteger(final XmlAnySimpleType value) {
        final BigInteger bigInt = buildBigInt(value);
        try {
            final XmlNonNegativeIntegerImpl i = new XmlNonNegativeIntegerImpl();
            i.set(bigInt);
            i.setImmutable();
            return i;
        }
        catch (final XmlValueOutOfRangeException e) {
            StscState.get().error("Internal error processing number", 21, value);
            return null;
        }
    }
    
    static XmlPositiveInteger buildPosInteger(final XmlAnySimpleType value) {
        final BigInteger bigInt = buildBigInt(value);
        try {
            final XmlPositiveIntegerImpl i = new XmlPositiveIntegerImpl();
            i.set(bigInt);
            i.setImmutable();
            return i;
        }
        catch (final XmlValueOutOfRangeException e) {
            StscState.get().error("Internal error processing number", 21, value);
            return null;
        }
    }
    
    private static Object getUserData(final XmlObject pos) {
        final XmlCursor.XmlBookmark b = pos.newCursor().getBookmark(SchemaBookmark.class);
        if (b != null && b instanceof SchemaBookmark) {
            return ((SchemaBookmark)b).getValue();
        }
        return null;
    }
    
    private static boolean isEmptySchema(final SchemaDocument.Schema schema) {
        final XmlCursor cursor = schema.newCursor();
        final boolean result = !cursor.toFirstChild();
        cursor.dispose();
        return result;
    }
    
    private static boolean isReservedTypeName(final QName name) {
        return BuiltinSchemaTypeSystem.get().findType(name) != null;
    }
    
    static {
        WSDL_ARRAYTYPE_NAME = QNameHelper.forLNS("arrayType", "http://schemas.xmlsoap.org/wsdl/");
        XPATH_REGEXP = new RegularExpression("(\\.//)?((((child::)?((\\i\\c*:)?(\\i\\c*|\\*)))|\\.)/)*((((child::)?((\\i\\c*:)?(\\i\\c*|\\*)))|\\.)|((attribute::|@)((\\i\\c*:)?(\\i\\c*|\\*))))(\\|(\\.//)?((((child::)?((\\i\\c*:)?(\\i\\c*|\\*)))|\\.)/)*((((child::)?((\\i\\c*:)?(\\i\\c*|\\*)))|\\.)|((attribute::|@)((\\i\\c*:)?(\\i\\c*|\\*)))))*", "X");
    }
    
    private static class RedefinitionHolder
    {
        private Map stRedefinitions;
        private Map ctRedefinitions;
        private Map agRedefinitions;
        private Map mgRedefinitions;
        private String schemaLocation;
        private StscImporter.SchemaToProcess schemaRedefined;
        
        RedefinitionHolder(final StscImporter.SchemaToProcess schemaToProcess, final RedefineDocument.Redefine redefine) {
            this.stRedefinitions = Collections.EMPTY_MAP;
            this.ctRedefinitions = Collections.EMPTY_MAP;
            this.agRedefinitions = Collections.EMPTY_MAP;
            this.mgRedefinitions = Collections.EMPTY_MAP;
            this.schemaLocation = "";
            this.schemaRedefined = schemaToProcess;
            if (redefine != null) {
                final StscState state = StscState.get();
                this.stRedefinitions = new HashMap();
                this.ctRedefinitions = new HashMap();
                this.agRedefinitions = new HashMap();
                this.mgRedefinitions = new HashMap();
                if (redefine.getSchemaLocation() != null) {
                    this.schemaLocation = redefine.getSchemaLocation();
                }
                final TopLevelComplexType[] complexTypes = redefine.getComplexTypeArray();
                for (int i = 0; i < complexTypes.length; ++i) {
                    if (complexTypes[i].getName() != null) {
                        if (this.ctRedefinitions.containsKey(complexTypes[i].getName())) {
                            state.error("Duplicate type redefinition: " + complexTypes[i].getName(), 49, null);
                        }
                        else {
                            this.ctRedefinitions.put(complexTypes[i].getName(), complexTypes[i]);
                        }
                    }
                }
                final TopLevelSimpleType[] simpleTypes = redefine.getSimpleTypeArray();
                for (int j = 0; j < simpleTypes.length; ++j) {
                    if (simpleTypes[j].getName() != null) {
                        if (this.stRedefinitions.containsKey(simpleTypes[j].getName())) {
                            state.error("Duplicate type redefinition: " + simpleTypes[j].getName(), 49, null);
                        }
                        else {
                            this.stRedefinitions.put(simpleTypes[j].getName(), simpleTypes[j]);
                        }
                    }
                }
                final NamedGroup[] modelgroups = redefine.getGroupArray();
                for (int k = 0; k < modelgroups.length; ++k) {
                    if (modelgroups[k].getName() != null) {
                        if (this.mgRedefinitions.containsKey(modelgroups[k].getName())) {
                            state.error("Duplicate type redefinition: " + modelgroups[k].getName(), 49, null);
                        }
                        else {
                            this.mgRedefinitions.put(modelgroups[k].getName(), modelgroups[k]);
                        }
                    }
                }
                final NamedAttributeGroup[] attrgroups = redefine.getAttributeGroupArray();
                for (int l = 0; l < attrgroups.length; ++l) {
                    if (attrgroups[l].getName() != null) {
                        if (this.agRedefinitions.containsKey(attrgroups[l].getName())) {
                            state.error("Duplicate type redefinition: " + attrgroups[l].getName(), 49, null);
                        }
                        else {
                            this.agRedefinitions.put(attrgroups[l].getName(), attrgroups[l]);
                        }
                    }
                }
            }
        }
        
        public TopLevelSimpleType redefineSimpleType(final String name) {
            if (name == null || !this.stRedefinitions.containsKey(name)) {
                return null;
            }
            return this.stRedefinitions.remove(name);
        }
        
        public TopLevelComplexType redefineComplexType(final String name) {
            if (name == null || !this.ctRedefinitions.containsKey(name)) {
                return null;
            }
            return this.ctRedefinitions.remove(name);
        }
        
        public NamedGroup redefineModelGroup(final String name) {
            if (name == null || !this.mgRedefinitions.containsKey(name)) {
                return null;
            }
            return this.mgRedefinitions.remove(name);
        }
        
        public NamedAttributeGroup redefineAttributeGroup(final String name) {
            if (name == null || !this.agRedefinitions.containsKey(name)) {
                return null;
            }
            return this.agRedefinitions.remove(name);
        }
        
        public void complainAboutMissingDefinitions() {
            if (this.stRedefinitions.isEmpty() && this.ctRedefinitions.isEmpty() && this.agRedefinitions.isEmpty() && this.mgRedefinitions.isEmpty()) {
                return;
            }
            final StscState state = StscState.get();
            for (final String name : this.stRedefinitions.keySet()) {
                state.error("Redefined simple type " + name + " not found in " + this.schemaLocation, 60, this.stRedefinitions.get(name));
            }
            for (final String name : this.ctRedefinitions.keySet()) {
                state.error("Redefined complex type " + name + " not found in " + this.schemaLocation, 60, this.ctRedefinitions.get(name));
            }
            for (final String name : this.agRedefinitions.keySet()) {
                state.error("Redefined attribute group " + name + " not found in " + this.schemaLocation, 60, this.agRedefinitions.get(name));
            }
            for (final String name : this.mgRedefinitions.keySet()) {
                state.error("Redefined model group " + name + " not found in " + this.schemaLocation, 60, this.mgRedefinitions.get(name));
            }
        }
    }
    
    private static class RedefinitionMaster
    {
        private Map stRedefinitions;
        private Map ctRedefinitions;
        private Map agRedefinitions;
        private Map mgRedefinitions;
        private static final RedefinitionHolder[] EMPTY_REDEFINTION_HOLDER_ARRAY;
        private static final short SIMPLE_TYPE = 1;
        private static final short COMPLEX_TYPE = 2;
        private static final short MODEL_GROUP = 3;
        private static final short ATTRIBUTE_GROUP = 4;
        
        RedefinitionMaster(final RedefinitionHolder[] redefHolders) {
            this.stRedefinitions = Collections.EMPTY_MAP;
            this.ctRedefinitions = Collections.EMPTY_MAP;
            this.agRedefinitions = Collections.EMPTY_MAP;
            this.mgRedefinitions = Collections.EMPTY_MAP;
            if (redefHolders.length > 0) {
                this.stRedefinitions = new HashMap();
                this.ctRedefinitions = new HashMap();
                this.agRedefinitions = new HashMap();
                this.mgRedefinitions = new HashMap();
                for (int i = 0; i < redefHolders.length; ++i) {
                    final RedefinitionHolder redefHolder = redefHolders[i];
                    for (final Object key : redefHolder.stRedefinitions.keySet()) {
                        List redefinedIn = this.stRedefinitions.get(key);
                        if (redefinedIn == null) {
                            redefinedIn = new ArrayList();
                            this.stRedefinitions.put(key, redefinedIn);
                        }
                        redefinedIn.add(redefHolders[i]);
                    }
                    for (final Object key : redefHolder.ctRedefinitions.keySet()) {
                        List redefinedIn = this.ctRedefinitions.get(key);
                        if (redefinedIn == null) {
                            redefinedIn = new ArrayList();
                            this.ctRedefinitions.put(key, redefinedIn);
                        }
                        redefinedIn.add(redefHolders[i]);
                    }
                    for (final Object key : redefHolder.agRedefinitions.keySet()) {
                        List redefinedIn = this.agRedefinitions.get(key);
                        if (redefinedIn == null) {
                            redefinedIn = new ArrayList();
                            this.agRedefinitions.put(key, redefinedIn);
                        }
                        redefinedIn.add(redefHolders[i]);
                    }
                    for (final Object key : redefHolder.mgRedefinitions.keySet()) {
                        List redefinedIn = this.mgRedefinitions.get(key);
                        if (redefinedIn == null) {
                            redefinedIn = new ArrayList();
                            this.mgRedefinitions.put(key, redefinedIn);
                        }
                        redefinedIn.add(redefHolders[i]);
                    }
                }
            }
        }
        
        RedefinitionHolder[] getSimpleTypeRedefinitions(final String name, final StscImporter.SchemaToProcess schema) {
            final List redefines = this.stRedefinitions.get(name);
            if (redefines == null) {
                return RedefinitionMaster.EMPTY_REDEFINTION_HOLDER_ARRAY;
            }
            return this.doTopologicalSort(redefines, schema, name, (short)1);
        }
        
        RedefinitionHolder[] getComplexTypeRedefinitions(final String name, final StscImporter.SchemaToProcess schema) {
            final List redefines = this.ctRedefinitions.get(name);
            if (redefines == null) {
                return RedefinitionMaster.EMPTY_REDEFINTION_HOLDER_ARRAY;
            }
            return this.doTopologicalSort(redefines, schema, name, (short)2);
        }
        
        RedefinitionHolder[] getAttributeGroupRedefinitions(final String name, final StscImporter.SchemaToProcess schema) {
            final List redefines = this.agRedefinitions.get(name);
            if (redefines == null) {
                return RedefinitionMaster.EMPTY_REDEFINTION_HOLDER_ARRAY;
            }
            return this.doTopologicalSort(redefines, schema, name, (short)4);
        }
        
        RedefinitionHolder[] getModelGroupRedefinitions(final String name, final StscImporter.SchemaToProcess schema) {
            final List redefines = this.mgRedefinitions.get(name);
            if (redefines == null) {
                return RedefinitionMaster.EMPTY_REDEFINTION_HOLDER_ARRAY;
            }
            return this.doTopologicalSort(redefines, schema, name, (short)3);
        }
        
        private RedefinitionHolder[] doTopologicalSort(final List genericRedefines, final StscImporter.SchemaToProcess schema, final String name, final short componentType) {
            final RedefinitionHolder[] specificRedefines = new RedefinitionHolder[genericRedefines.size()];
            int n = 0;
            for (int i = 0; i < genericRedefines.size(); ++i) {
                final RedefinitionHolder h = genericRedefines.get(i);
                if (h.schemaRedefined == schema || h.schemaRedefined.indirectIncludes(schema)) {
                    specificRedefines[n++] = h;
                }
            }
            final RedefinitionHolder[] sortedRedefines = new RedefinitionHolder[n];
            final int[] numberOfIncludes = new int[n];
            for (int j = 0; j < n - 1; ++j) {
                final RedefinitionHolder current = specificRedefines[j];
                for (int k = j + 1; k < n; ++k) {
                    if (current.schemaRedefined.indirectIncludes(specificRedefines[k].schemaRedefined)) {
                        final int[] array = numberOfIncludes;
                        final int n2 = j;
                        ++array[n2];
                    }
                    if (specificRedefines[k].schemaRedefined.indirectIncludes(current.schemaRedefined)) {
                        final int[] array2 = numberOfIncludes;
                        final int n3 = k;
                        ++array2[n3];
                    }
                }
            }
            int position = 0;
            boolean errorReported = false;
            while (position < n) {
                int index = -1;
                for (int l = 0; l < numberOfIncludes.length; ++l) {
                    if (numberOfIncludes[l] == 0 && index < 0) {
                        index = l;
                    }
                }
                if (index < 0) {
                    if (!errorReported) {
                        final StringBuffer fileNameList = new StringBuffer();
                        XmlObject location = null;
                        for (int m = 0; m < n; ++m) {
                            if (specificRedefines[m] != null) {
                                fileNameList.append(specificRedefines[m].schemaLocation).append(',').append(' ');
                                if (location == null) {
                                    location = this.locationFromRedefinitionAndCode(specificRedefines[m], name, componentType);
                                }
                            }
                        }
                        StscState.get().error("Detected circular redefinition of " + this.componentNameFromCode(componentType) + " \"" + name + "\"; Files involved: " + fileNameList.toString(), 60, location);
                        errorReported = true;
                    }
                    int min = n;
                    for (int i2 = 0; i2 < n; ++i2) {
                        if (numberOfIncludes[i2] > 0 && numberOfIncludes[i2] < min) {
                            min = numberOfIncludes[i2];
                            index = i2;
                        }
                    }
                    final int[] array3 = numberOfIncludes;
                    final int n4 = index;
                    --array3[n4];
                }
                else {
                    assert specificRedefines[index] != null;
                    sortedRedefines[position++] = specificRedefines[index];
                    for (int l = 0; l < n; ++l) {
                        if (specificRedefines[l] != null && specificRedefines[l].schemaRedefined.indirectIncludes(specificRedefines[index].schemaRedefined)) {
                            final int[] array4 = numberOfIncludes;
                            final int n5 = l;
                            --array4[n5];
                        }
                    }
                    specificRedefines[index] = null;
                    final int[] array5 = numberOfIncludes;
                    final int n6 = index;
                    --array5[n6];
                }
            }
            for (int i3 = 1; i3 < n; ++i3) {
                int j2;
                for (j2 = i3 - 1; j2 >= 0 && sortedRedefines[j2] == null; --j2) {}
                if (!sortedRedefines[i3].schemaRedefined.indirectIncludes(sortedRedefines[j2].schemaRedefined)) {
                    StscState.get().error("Detected multiple redefinitions of " + this.componentNameFromCode(componentType) + " \"" + name + "\"; Files involved: " + sortedRedefines[j2].schemaRedefined.getSourceName() + ", " + sortedRedefines[i3].schemaRedefined.getSourceName(), 49, this.locationFromRedefinitionAndCode(sortedRedefines[i3], name, componentType));
                    switch (componentType) {
                        case 1: {
                            sortedRedefines[i3].redefineSimpleType(name);
                            break;
                        }
                        case 2: {
                            sortedRedefines[i3].redefineComplexType(name);
                            break;
                        }
                        case 4: {
                            sortedRedefines[i3].redefineAttributeGroup(name);
                            break;
                        }
                        case 3: {
                            sortedRedefines[i3].redefineModelGroup(name);
                            break;
                        }
                    }
                    sortedRedefines[i3] = null;
                }
            }
            return sortedRedefines;
        }
        
        private String componentNameFromCode(final short code) {
            String componentName = null;
            switch (code) {
                case 1: {
                    componentName = "simple type";
                    break;
                }
                case 2: {
                    componentName = "complex type";
                    break;
                }
                case 3: {
                    componentName = "model group";
                    break;
                }
                case 4: {
                    componentName = "attribute group";
                    break;
                }
                default: {
                    componentName = "";
                    break;
                }
            }
            return componentName;
        }
        
        private XmlObject locationFromRedefinitionAndCode(final RedefinitionHolder redefinition, final String name, final short code) {
            XmlObject location = null;
            switch (code) {
                case 1: {
                    location = redefinition.stRedefinitions.get(name);
                    break;
                }
                case 2: {
                    location = redefinition.ctRedefinitions.get(name);
                    break;
                }
                case 3: {
                    location = redefinition.mgRedefinitions.get(name);
                    break;
                }
                case 4: {
                    location = redefinition.agRedefinitions.get(name);
                    break;
                }
                default: {
                    location = null;
                    break;
                }
            }
            return location;
        }
        
        static {
            EMPTY_REDEFINTION_HOLDER_ARRAY = new RedefinitionHolder[0];
        }
    }
}
