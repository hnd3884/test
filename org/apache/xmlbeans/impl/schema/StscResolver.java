package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.impl.xb.xsdschema.KeyrefDocument;
import java.util.Iterator;
import org.apache.xmlbeans.SchemaLocalAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import java.util.Map;
import org.apache.xmlbeans.SchemaAttributeModel;
import java.util.Collections;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.QNameSet;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.impl.xb.xsdschema.Element;
import org.apache.xmlbeans.XmlObject;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;

public class StscResolver
{
    public static void resolveAll() {
        final StscState state = StscState.get();
        final SchemaType[] documentTypes = state.documentTypes();
        for (int i = 0; i < documentTypes.length; ++i) {
            resolveSubstitutionGroup((SchemaTypeImpl)documentTypes[i]);
        }
        final List allSeenTypes = new ArrayList();
        allSeenTypes.addAll(Arrays.asList(state.documentTypes()));
        allSeenTypes.addAll(Arrays.asList(state.attributeTypes()));
        allSeenTypes.addAll(Arrays.asList(state.redefinedGlobalTypes()));
        allSeenTypes.addAll(Arrays.asList(state.globalTypes()));
        for (int j = 0; j < allSeenTypes.size(); ++j) {
            final SchemaType gType = allSeenTypes.get(j);
            resolveType((SchemaTypeImpl)gType);
            allSeenTypes.addAll(Arrays.asList(gType.getAnonymousTypes()));
        }
        resolveIdentityConstraints();
    }
    
    public static boolean resolveType(final SchemaTypeImpl sImpl) {
        if (sImpl.isResolved()) {
            return true;
        }
        if (sImpl.isResolving()) {
            StscState.get().error("Cyclic dependency error", 13, sImpl.getParseObject());
            return false;
        }
        sImpl.startResolving();
        if (sImpl.isDocumentType()) {
            resolveDocumentType(sImpl);
        }
        else if (sImpl.isAttributeType()) {
            resolveAttributeType(sImpl);
        }
        else if (sImpl.isSimpleType()) {
            StscSimpleTypeResolver.resolveSimpleType(sImpl);
        }
        else {
            StscComplexTypeResolver.resolveComplexType(sImpl);
        }
        sImpl.finishResolving();
        return true;
    }
    
    public static boolean resolveSubstitutionGroup(final SchemaTypeImpl sImpl) {
        assert sImpl.isDocumentType();
        if (sImpl.isSGResolved()) {
            return true;
        }
        if (sImpl.isSGResolving()) {
            StscState.get().error("Cyclic dependency error", 13, sImpl.getParseObject());
            return false;
        }
        sImpl.startResolvingSGs();
        final TopLevelElement elt = (TopLevelElement)sImpl.getParseObject();
        SchemaTypeImpl substitutionGroup = null;
        final QName eltName = new QName(sImpl.getTargetNamespace(), elt.getName());
        if (elt.isSetSubstitutionGroup()) {
            substitutionGroup = StscState.get().findDocumentType(elt.getSubstitutionGroup(), sImpl.getChameleonNamespace(), sImpl.getTargetNamespace());
            if (substitutionGroup == null) {
                StscState.get().notFoundError(elt.getSubstitutionGroup(), 1, elt.xgetSubstitutionGroup(), true);
            }
            else if (!resolveSubstitutionGroup(substitutionGroup)) {
                substitutionGroup = null;
            }
            else {
                sImpl.setSubstitutionGroup(elt.getSubstitutionGroup());
            }
        }
        while (substitutionGroup != null) {
            substitutionGroup.addSubstitutionGroupMember(eltName);
            if (substitutionGroup.getSubstitutionGroup() == null) {
                break;
            }
            substitutionGroup = StscState.get().findDocumentType(substitutionGroup.getSubstitutionGroup(), substitutionGroup.getChameleonNamespace(), null);
            assert substitutionGroup != null : "Could not find document type for: " + substitutionGroup.getSubstitutionGroup();
            if (resolveSubstitutionGroup(substitutionGroup)) {
                continue;
            }
            substitutionGroup = null;
        }
        sImpl.finishResolvingSGs();
        return true;
    }
    
    public static void resolveDocumentType(final SchemaTypeImpl sImpl) {
        assert sImpl.isResolving();
        assert sImpl.isDocumentType();
        final List anonTypes = new ArrayList();
        final SchemaGlobalElementImpl element = (SchemaGlobalElementImpl)StscTranslator.translateElement((Element)sImpl.getParseObject(), sImpl.getTargetNamespace(), sImpl.isChameleon(), null, null, anonTypes, sImpl);
        SchemaLocalElementImpl contentModel = null;
        if (element != null) {
            StscState.get().addGlobalElement(element);
            contentModel = new SchemaLocalElementImpl();
            contentModel.setParticleType(4);
            StscTranslator.copyGlobalElementToLocalElement(element, contentModel);
            contentModel.setMinOccurs(BigInteger.ONE);
            contentModel.setMaxOccurs(BigInteger.ONE);
            contentModel.setTransitionNotes(QNameSet.EMPTY, true);
        }
        final Map elementPropertyModel = StscComplexTypeResolver.buildContentPropertyModelByQName(contentModel, sImpl);
        final SchemaTypeImpl baseType = (sImpl.getSubstitutionGroup() == null) ? BuiltinSchemaTypeSystem.ST_ANY_TYPE : StscState.get().findDocumentType(sImpl.getSubstitutionGroup(), sImpl.isChameleon() ? sImpl.getTargetNamespace() : null, null);
        sImpl.setBaseTypeRef(baseType.getRef());
        sImpl.setBaseDepth(baseType.getBaseDepth() + 1);
        sImpl.setDerivationType(1);
        sImpl.setComplexTypeVariety(3);
        sImpl.setContentModel(contentModel, new SchemaAttributeModelImpl(), elementPropertyModel, Collections.EMPTY_MAP, false);
        sImpl.setWildcardSummary(QNameSet.EMPTY, false, QNameSet.EMPTY, false);
        sImpl.setAnonymousTypeRefs(makeRefArray(anonTypes));
    }
    
    public static void resolveAttributeType(final SchemaTypeImpl sImpl) {
        assert sImpl.isResolving();
        assert sImpl.isAttributeType();
        final List anonTypes = new ArrayList();
        final SchemaGlobalAttributeImpl attribute = (SchemaGlobalAttributeImpl)StscTranslator.translateAttribute((Attribute)sImpl.getParseObject(), sImpl.getTargetNamespace(), null, sImpl.isChameleon(), anonTypes, sImpl, null, false);
        final SchemaAttributeModelImpl attributeModel = new SchemaAttributeModelImpl();
        if (attribute != null) {
            StscState.get().addGlobalAttribute(attribute);
            final SchemaLocalAttributeImpl attributeCopy = new SchemaLocalAttributeImpl();
            StscTranslator.copyGlobalAttributeToLocalAttribute(attribute, attributeCopy);
            attributeModel.addAttribute(attributeCopy);
        }
        sImpl.setBaseTypeRef(BuiltinSchemaTypeSystem.ST_ANY_TYPE.getRef());
        sImpl.setBaseDepth(sImpl.getBaseDepth() + 1);
        sImpl.setDerivationType(1);
        sImpl.setComplexTypeVariety(1);
        final Map attributePropertyModel = StscComplexTypeResolver.buildAttributePropertyModelByQName(attributeModel, sImpl);
        sImpl.setContentModel(null, attributeModel, Collections.EMPTY_MAP, attributePropertyModel, false);
        sImpl.setWildcardSummary(QNameSet.EMPTY, false, QNameSet.EMPTY, false);
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
    
    public static void resolveIdentityConstraints() {
        final StscState state = StscState.get();
        final SchemaIdentityConstraintImpl[] idcs = state.idConstraints();
        for (int i = 0; i < idcs.length; ++i) {
            if (!idcs[i].isResolved()) {
                final KeyrefDocument.Keyref xsdkr = (KeyrefDocument.Keyref)idcs[i].getParseObject();
                final QName keyName = xsdkr.getRefer();
                SchemaIdentityConstraintImpl key = null;
                key = state.findIdConstraint(keyName, idcs[i].getChameleonNamespace(), idcs[i].getTargetNamespace());
                if (key == null) {
                    state.notFoundError(keyName, 5, xsdkr, true);
                }
                else {
                    if (key.getConstraintCategory() == 2) {
                        state.error("c-props-correct.1", null, idcs[i].getParseObject());
                    }
                    if (key.getFields().length != idcs[i].getFields().length) {
                        state.error("c-props-correct.2", null, idcs[i].getParseObject());
                    }
                    idcs[i].setReferencedKey(key.getRef());
                }
            }
        }
    }
}
