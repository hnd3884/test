package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import java.util.HashSet;
import org.apache.xmlbeans.QNameSetSpecification;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.impl.common.XBeanDebug;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.XmlAnySimpleType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaLocalAttribute;
import org.apache.xmlbeans.SchemaAttributeModel;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlNOTATION;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.XmlID;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;

public class StscChecker
{
    public static void checkAll() {
        final StscState state = StscState.get();
        final List allSeenTypes = new ArrayList();
        allSeenTypes.addAll(Arrays.asList(state.documentTypes()));
        allSeenTypes.addAll(Arrays.asList(state.attributeTypes()));
        allSeenTypes.addAll(Arrays.asList(state.redefinedGlobalTypes()));
        allSeenTypes.addAll(Arrays.asList(state.globalTypes()));
        for (int i = 0; i < allSeenTypes.size(); ++i) {
            final SchemaType gType = allSeenTypes.get(i);
            if (!state.noPvr() && !gType.isDocumentType()) {
                checkRestriction((SchemaTypeImpl)gType);
            }
            checkFields((SchemaTypeImpl)gType);
            allSeenTypes.addAll(Arrays.asList(gType.getAnonymousTypes()));
        }
        checkSubstitutionGroups(state.globalElements());
    }
    
    public static void checkFields(final SchemaTypeImpl sType) {
        if (sType.isSimpleType()) {
            return;
        }
        final XmlObject location = sType.getParseObject();
        final SchemaAttributeModel sAttrModel = sType.getAttributeModel();
        if (sAttrModel != null) {
            final SchemaLocalAttribute[] sAttrs = sAttrModel.getAttributes();
            QName idAttr = null;
            for (int i = 0; i < sAttrs.length; ++i) {
                final XmlObject attrLocation = ((SchemaLocalAttributeImpl)sAttrs[i])._parseObject;
                if (XmlID.type.isAssignableFrom(sAttrs[i].getType())) {
                    if (idAttr == null) {
                        idAttr = sAttrs[i].getName();
                    }
                    else {
                        StscState.get().error("ag-props-correct.3", new Object[] { QNameHelper.pretty(idAttr), sAttrs[i].getName() }, (attrLocation != null) ? attrLocation : location);
                    }
                    if (sAttrs[i].getDefaultText() != null) {
                        StscState.get().error("a-props-correct.3", null, (attrLocation != null) ? attrLocation : location);
                    }
                }
                else if (XmlNOTATION.type.isAssignableFrom(sAttrs[i].getType())) {
                    if (sAttrs[i].getType().getBuiltinTypeCode() == 8) {
                        StscState.get().recover("enumeration-required-notation-attr", new Object[] { QNameHelper.pretty(sAttrs[i].getName()) }, (attrLocation != null) ? attrLocation : location);
                    }
                    else {
                        if (sAttrs[i].getType().getSimpleVariety() == 2) {
                            final SchemaType[] members = sAttrs[i].getType().getUnionConstituentTypes();
                            for (int j = 0; j < members.length; ++j) {
                                if (members[j].getBuiltinTypeCode() == 8) {
                                    StscState.get().recover("enumeration-required-notation-attr", new Object[] { QNameHelper.pretty(sAttrs[i].getName()) }, (attrLocation != null) ? attrLocation : location);
                                }
                            }
                        }
                        boolean hasNS;
                        if (sType.isAttributeType()) {
                            hasNS = (sAttrs[i].getName().getNamespaceURI().length() > 0);
                        }
                        else {
                            SchemaType t;
                            for (t = sType; t.getOuterType() != null; t = t.getOuterType()) {}
                            if (t.isDocumentType()) {
                                hasNS = (t.getDocumentElementName().getNamespaceURI().length() > 0);
                            }
                            else {
                                hasNS = (t.getName().getNamespaceURI().length() > 0);
                            }
                        }
                        if (hasNS) {
                            StscState.get().warning("notation-targetns-attr", new Object[] { QNameHelper.pretty(sAttrs[i].getName()) }, (attrLocation != null) ? attrLocation : location);
                        }
                    }
                }
                else {
                    final String valueConstraint = sAttrs[i].getDefaultText();
                    if (valueConstraint != null) {
                        try {
                            final XmlAnySimpleType val = sAttrs[i].getDefaultValue();
                            if (!val.validate()) {
                                throw new Exception();
                            }
                            final SchemaPropertyImpl sProp = (SchemaPropertyImpl)sType.getAttributeProperty(sAttrs[i].getName());
                            if (sProp != null && sProp.getDefaultText() != null) {
                                sProp.setDefaultValue(new XmlValueRef(val));
                            }
                        }
                        catch (final Exception e) {
                            final String constraintName = sAttrs[i].isFixed() ? "fixed" : "default";
                            XmlObject constraintLocation = location;
                            if (attrLocation != null) {
                                constraintLocation = attrLocation.selectAttribute("", constraintName);
                                if (constraintLocation == null) {
                                    constraintLocation = attrLocation;
                                }
                            }
                            StscState.get().error("a-props-correct.2", new Object[] { QNameHelper.pretty(sAttrs[i].getName()), constraintName, valueConstraint, QNameHelper.pretty(sAttrs[i].getType().getName()) }, constraintLocation);
                        }
                    }
                }
            }
        }
        checkElementDefaults(sType.getContentModel(), location, sType);
    }
    
    private static void checkElementDefaults(final SchemaParticle model, final XmlObject location, final SchemaType parentType) {
        if (model == null) {
            return;
        }
        switch (model.getParticleType()) {
            case 1:
            case 2:
            case 3: {
                final SchemaParticle[] children = model.getParticleChildren();
                for (int i = 0; i < children.length; ++i) {
                    checkElementDefaults(children[i], location, parentType);
                }
                break;
            }
            case 4: {
                final String valueConstraint = model.getDefaultText();
                Label_0619: {
                    if (valueConstraint != null) {
                        if (!model.getType().isSimpleType()) {
                            if (model.getType().getContentType() != 2) {
                                if (model.getType().getContentType() == 4) {
                                    if (!model.getType().getContentModel().isSkippable()) {
                                        final String constraintName = model.isFixed() ? "fixed" : "default";
                                        final XmlObject constraintLocation = location.selectAttribute("", constraintName);
                                        StscState.get().error("cos-valid-default.2.2.2", new Object[] { QNameHelper.pretty(model.getName()), constraintName, valueConstraint }, (constraintLocation == null) ? location : constraintLocation);
                                        break Label_0619;
                                    }
                                    final SchemaPropertyImpl sProp = (SchemaPropertyImpl)parentType.getElementProperty(model.getName());
                                    if (sProp != null && sProp.getDefaultText() != null) {
                                        sProp.setDefaultValue(new XmlValueRef(XmlString.type.newValue(valueConstraint)));
                                    }
                                    break Label_0619;
                                }
                                else {
                                    if (model.getType().getContentType() == 3) {
                                        final XmlObject constraintLocation2 = location.selectAttribute("", "default");
                                        StscState.get().error("cos-valid-default.2.1", new Object[] { QNameHelper.pretty(model.getName()), valueConstraint, "element" }, (constraintLocation2 == null) ? location : constraintLocation2);
                                        break Label_0619;
                                    }
                                    if (model.getType().getContentType() == 1) {
                                        final XmlObject constraintLocation2 = location.selectAttribute("", "default");
                                        StscState.get().error("cos-valid-default.2.1", new Object[] { QNameHelper.pretty(model.getName()), valueConstraint, "empty" }, (constraintLocation2 == null) ? location : constraintLocation2);
                                    }
                                    break Label_0619;
                                }
                            }
                        }
                        try {
                            final XmlAnySimpleType val = model.getDefaultValue();
                            final XmlOptions opt = new XmlOptions();
                            opt.put("VALIDATE_TEXT_ONLY");
                            if (!val.validate(opt)) {
                                throw new Exception();
                            }
                            final SchemaPropertyImpl sProp2 = (SchemaPropertyImpl)parentType.getElementProperty(model.getName());
                            if (sProp2 != null && sProp2.getDefaultText() != null) {
                                sProp2.setDefaultValue(new XmlValueRef(val));
                            }
                        }
                        catch (final Exception e) {
                            final String constraintName2 = model.isFixed() ? "fixed" : "default";
                            final XmlObject constraintLocation3 = location.selectAttribute("", constraintName2);
                            StscState.get().error("e-props-correct.2", new Object[] { QNameHelper.pretty(model.getName()), constraintName2, valueConstraint, QNameHelper.pretty(model.getType().getName()) }, (constraintLocation3 == null) ? location : constraintLocation3);
                        }
                    }
                }
                String warningType = null;
                if (BuiltinSchemaTypeSystem.ST_ID.isAssignableFrom(model.getType())) {
                    warningType = BuiltinSchemaTypeSystem.ST_ID.getName().getLocalPart();
                }
                else if (BuiltinSchemaTypeSystem.ST_IDREF.isAssignableFrom(model.getType())) {
                    warningType = BuiltinSchemaTypeSystem.ST_IDREF.getName().getLocalPart();
                }
                else if (BuiltinSchemaTypeSystem.ST_IDREFS.isAssignableFrom(model.getType())) {
                    warningType = BuiltinSchemaTypeSystem.ST_IDREFS.getName().getLocalPart();
                }
                else if (BuiltinSchemaTypeSystem.ST_ENTITY.isAssignableFrom(model.getType())) {
                    warningType = BuiltinSchemaTypeSystem.ST_ENTITY.getName().getLocalPart();
                }
                else if (BuiltinSchemaTypeSystem.ST_ENTITIES.isAssignableFrom(model.getType())) {
                    warningType = BuiltinSchemaTypeSystem.ST_ENTITIES.getName().getLocalPart();
                }
                else if (BuiltinSchemaTypeSystem.ST_NOTATION.isAssignableFrom(model.getType())) {
                    if (model.getType().getBuiltinTypeCode() == 8) {
                        StscState.get().recover("enumeration-required-notation-elem", new Object[] { QNameHelper.pretty(model.getName()) }, (((SchemaLocalElementImpl)model)._parseObject == null) ? location : ((SchemaLocalElementImpl)model)._parseObject.selectAttribute("", "type"));
                    }
                    else {
                        if (model.getType().getSimpleVariety() == 2) {
                            final SchemaType[] members = model.getType().getUnionConstituentTypes();
                            for (int j = 0; j < members.length; ++j) {
                                if (members[j].getBuiltinTypeCode() == 8) {
                                    StscState.get().recover("enumeration-required-notation-elem", new Object[] { QNameHelper.pretty(model.getName()) }, (((SchemaLocalElementImpl)model)._parseObject == null) ? location : ((SchemaLocalElementImpl)model)._parseObject.selectAttribute("", "type"));
                                }
                            }
                        }
                        warningType = BuiltinSchemaTypeSystem.ST_NOTATION.getName().getLocalPart();
                    }
                    SchemaType t;
                    for (t = parentType; t.getOuterType() != null; t = t.getOuterType()) {}
                    boolean hasNS;
                    if (t.isDocumentType()) {
                        hasNS = (t.getDocumentElementName().getNamespaceURI().length() > 0);
                    }
                    else {
                        hasNS = (t.getName().getNamespaceURI().length() > 0);
                    }
                    if (hasNS) {
                        StscState.get().warning("notation-targetns-elem", new Object[] { QNameHelper.pretty(model.getName()) }, (((SchemaLocalElementImpl)model)._parseObject == null) ? location : ((SchemaLocalElementImpl)model)._parseObject);
                    }
                }
                if (warningType != null) {
                    StscState.get().warning("id-idref-idrefs-entity-entities-notation", new Object[] { QNameHelper.pretty(model.getName()), warningType }, (((SchemaLocalElementImpl)model)._parseObject == null) ? location : ((SchemaLocalElementImpl)model)._parseObject.selectAttribute("", "type"));
                    break;
                }
                break;
            }
        }
    }
    
    public static boolean checkRestriction(final SchemaTypeImpl sType) {
        Label_0571: {
            if (sType.getDerivationType() == 1 && !sType.isSimpleType()) {
                final StscState state = StscState.get();
                final XmlObject location = sType.getParseObject();
                final SchemaType baseType = sType.getBaseType();
                if (baseType.isSimpleType()) {
                    state.error("src-ct.1", new Object[] { QNameHelper.pretty(baseType.getName()) }, location);
                    return false;
                }
                switch (sType.getContentType()) {
                    case 2: {
                        switch (baseType.getContentType()) {
                            case 2: {
                                final SchemaType cType = sType.getContentBasedOnType();
                                if (cType == baseType) {
                                    break;
                                }
                                SchemaType bType;
                                for (bType = baseType; bType != null && !bType.isSimpleType(); bType = bType.getContentBasedOnType()) {}
                                if (bType != null && !bType.isAssignableFrom(cType)) {
                                    state.error("derivation-ok-restriction.5.2.2.1", null, location);
                                    return false;
                                }
                                break;
                            }
                            case 4: {
                                if (baseType.getContentModel() != null && !baseType.getContentModel().isSkippable()) {
                                    state.error("derivation-ok-restriction.5.1.2", null, location);
                                    return false;
                                }
                                break;
                            }
                            default: {
                                state.error("derivation-ok-restriction.5.1", null, location);
                                return false;
                            }
                        }
                        break;
                    }
                    case 1: {
                        switch (baseType.getContentType()) {
                            case 1: {
                                break Label_0571;
                            }
                            case 3:
                            case 4: {
                                if (baseType.getContentModel() != null && !baseType.getContentModel().isSkippable()) {
                                    state.error("derivation-ok-restriction.5.2.2", null, location);
                                    return false;
                                }
                                break Label_0571;
                            }
                            default: {
                                state.error("derivation-ok-restriction.5.2", null, location);
                                return false;
                            }
                        }
                        break;
                    }
                    case 4: {
                        if (baseType.getContentType() != 4) {
                            state.error("derivation-ok-restriction.5.3a", null, location);
                            return false;
                        }
                    }
                    case 3: {
                        if (baseType.getContentType() == 1) {
                            state.error("derivation-ok-restriction.5.3b", null, location);
                            return false;
                        }
                        if (baseType.getContentType() == 2) {
                            state.error("derivation-ok-restriction.5.3c", null, location);
                            return false;
                        }
                        final SchemaParticle baseModel = baseType.getContentModel();
                        final SchemaParticle derivedModel = sType.getContentModel();
                        if (derivedModel == null && sType.getDerivationType() == 1) {
                            return true;
                        }
                        if (baseModel == null || derivedModel == null) {
                            XBeanDebug.logStackTrace("Null models that weren't caught by EMPTY_CONTENT: " + baseType + " (" + baseModel + "), " + sType + " (" + derivedModel + ")");
                            state.error("derivation-ok-restriction.5.3", null, location);
                            return false;
                        }
                        final List errors = new ArrayList();
                        final boolean isValid = isParticleValidRestriction(baseModel, derivedModel, errors, location);
                        if (!isValid) {
                            if (errors.size() == 0) {
                                state.error("derivation-ok-restriction.5.3", null, location);
                            }
                            else {
                                state.getErrorListener().add(errors.get(errors.size() - 1));
                            }
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean isParticleValidRestriction(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        boolean restrictionValid = false;
        Label_0699: {
            if (baseModel.equals(derivedModel)) {
                restrictionValid = true;
            }
            else {
                switch (baseModel.getParticleType()) {
                    case 4: {
                        switch (derivedModel.getParticleType()) {
                            case 4: {
                                restrictionValid = nameAndTypeOK((SchemaLocalElement)baseModel, (SchemaLocalElement)derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 1:
                            case 2:
                            case 3:
                            case 5: {
                                errors.add(XmlError.forObject("cos-particle-restrict.2", new Object[] { printParticle(derivedModel), printParticle(baseModel) }, context));
                                restrictionValid = false;
                                break Label_0699;
                            }
                            default: {
                                assert false : XBeanDebug.logStackTrace("Unknown schema type for Derived Type");
                                break Label_0699;
                            }
                        }
                        break;
                    }
                    case 5: {
                        switch (derivedModel.getParticleType()) {
                            case 4: {
                                restrictionValid = nsCompat(baseModel, (SchemaLocalElement)derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 5: {
                                restrictionValid = nsSubset(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 1: {
                                restrictionValid = nsRecurseCheckCardinality(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 2: {
                                restrictionValid = nsRecurseCheckCardinality(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 3: {
                                restrictionValid = nsRecurseCheckCardinality(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            default: {
                                assert false : XBeanDebug.logStackTrace("Unknown schema type for Derived Type");
                                break Label_0699;
                            }
                        }
                        break;
                    }
                    case 1: {
                        switch (derivedModel.getParticleType()) {
                            case 4: {
                                restrictionValid = recurseAsIfGroup(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 2:
                            case 5: {
                                errors.add(XmlError.forObject("cos-particle-restrict.2", new Object[] { printParticle(derivedModel), printParticle(baseModel) }, context));
                                restrictionValid = false;
                                break Label_0699;
                            }
                            case 1: {
                                restrictionValid = recurse(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 3: {
                                restrictionValid = recurseUnordered(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            default: {
                                assert false : XBeanDebug.logStackTrace("Unknown schema type for Derived Type");
                                break Label_0699;
                            }
                        }
                        break;
                    }
                    case 2: {
                        switch (derivedModel.getParticleType()) {
                            case 4: {
                                restrictionValid = recurseAsIfGroup(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 1:
                            case 5: {
                                errors.add(XmlError.forObject("cos-particle-restrict.2", new Object[] { printParticle(derivedModel), printParticle(baseModel) }, context));
                                restrictionValid = false;
                                break Label_0699;
                            }
                            case 2: {
                                restrictionValid = recurseLax(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 3: {
                                restrictionValid = mapAndSum(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            default: {
                                assert false : XBeanDebug.logStackTrace("Unknown schema type for Derived Type");
                                break Label_0699;
                            }
                        }
                        break;
                    }
                    case 3: {
                        switch (derivedModel.getParticleType()) {
                            case 4: {
                                restrictionValid = recurseAsIfGroup(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            case 1:
                            case 2:
                            case 5: {
                                errors.add(XmlError.forObject("cos-particle-restrict.2", new Object[] { printParticle(derivedModel), printParticle(baseModel) }, context));
                                restrictionValid = false;
                                break Label_0699;
                            }
                            case 3: {
                                restrictionValid = recurse(baseModel, derivedModel, errors, context);
                                break Label_0699;
                            }
                            default: {
                                assert false : XBeanDebug.logStackTrace("Unknown schema type for Derived Type");
                                break Label_0699;
                            }
                        }
                        break;
                    }
                    default: {
                        assert false : XBeanDebug.logStackTrace("Unknown schema type for Base Type");
                        break;
                    }
                }
            }
        }
        return restrictionValid;
    }
    
    private static boolean mapAndSum(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        assert baseModel.getParticleType() == 2;
        assert derivedModel.getParticleType() == 3;
        boolean mapAndSumValid = true;
        final SchemaParticle[] derivedParticleArray = derivedModel.getParticleChildren();
        final SchemaParticle[] baseParticleArray = baseModel.getParticleChildren();
        for (int i = 0; i < derivedParticleArray.length; ++i) {
            final SchemaParticle derivedParticle = derivedParticleArray[i];
            boolean foundMatch = false;
            for (int j = 0; j < baseParticleArray.length; ++j) {
                final SchemaParticle baseParticle = baseParticleArray[j];
                if (isParticleValidRestriction(baseParticle, derivedParticle, errors, context)) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                mapAndSumValid = false;
                errors.add(XmlError.forObject("rcase-MapAndSum.1", new Object[] { printParticle(derivedParticle) }, context));
                return false;
            }
        }
        final BigInteger derivedRangeMin = derivedModel.getMinOccurs().multiply(BigInteger.valueOf(derivedModel.getParticleChildren().length));
        BigInteger derivedRangeMax = null;
        final BigInteger UNBOUNDED = null;
        if (derivedModel.getMaxOccurs() == UNBOUNDED) {
            derivedRangeMax = null;
        }
        else {
            derivedRangeMax = derivedModel.getMaxOccurs().multiply(BigInteger.valueOf(derivedModel.getParticleChildren().length));
        }
        if (derivedRangeMin.compareTo(baseModel.getMinOccurs()) < 0) {
            mapAndSumValid = false;
            errors.add(XmlError.forObject("rcase-MapAndSum.2a", new Object[] { derivedRangeMin.toString(), baseModel.getMinOccurs().toString() }, context));
        }
        else if (baseModel.getMaxOccurs() != UNBOUNDED && (derivedRangeMax == UNBOUNDED || derivedRangeMax.compareTo(baseModel.getMaxOccurs()) > 0)) {
            mapAndSumValid = false;
            errors.add(XmlError.forObject("rcase-MapAndSum.2b", new Object[] { (derivedRangeMax == UNBOUNDED) ? "unbounded" : derivedRangeMax.toString(), baseModel.getMaxOccurs().toString() }, context));
        }
        return mapAndSumValid;
    }
    
    private static boolean recurseAsIfGroup(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        assert baseModel.getParticleType() == 3 && derivedModel.getParticleType() == 4;
        final SchemaParticleImpl asIfPart = new SchemaParticleImpl();
        asIfPart.setParticleType(baseModel.getParticleType());
        asIfPart.setMinOccurs(BigInteger.ONE);
        asIfPart.setMaxOccurs(BigInteger.ONE);
        asIfPart.setParticleChildren(new SchemaParticle[] { derivedModel });
        return isParticleValidRestriction(baseModel, asIfPart, errors, context);
    }
    
    private static boolean recurseLax(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        assert baseModel.getParticleType() == 2 && derivedModel.getParticleType() == 2;
        boolean recurseLaxValid = true;
        if (!occurrenceRangeOK(baseModel, derivedModel, errors, context)) {
            return false;
        }
        final SchemaParticle[] derivedParticleArray = derivedModel.getParticleChildren();
        final SchemaParticle[] baseParticleArray = baseModel.getParticleChildren();
        int i = 0;
        for (int j = 0; i < derivedParticleArray.length && j < baseParticleArray.length; ++j) {
            final SchemaParticle derivedParticle = derivedParticleArray[i];
            final SchemaParticle baseParticle = baseParticleArray[j];
            if (isParticleValidRestriction(baseParticle, derivedParticle, errors, context)) {
                ++i;
            }
            else {}
        }
        if (i < derivedParticleArray.length) {
            recurseLaxValid = false;
            errors.add(XmlError.forObject("rcase-RecurseLax.2", new Object[] { printParticles(baseParticleArray, i) }, context));
        }
        return recurseLaxValid;
    }
    
    private static boolean recurseUnordered(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        assert baseModel.getParticleType() == 1 && derivedModel.getParticleType() == 3;
        boolean recurseUnorderedValid = true;
        if (!occurrenceRangeOK(baseModel, derivedModel, errors, context)) {
            return false;
        }
        final SchemaParticle[] baseParticles = baseModel.getParticleChildren();
        final HashMap baseParticleMap = new HashMap(10);
        final Object MAPPED = new Object();
        for (int i = 0; i < baseParticles.length; ++i) {
            baseParticleMap.put(baseParticles[i].getName(), baseParticles[i]);
        }
        final SchemaParticle[] derivedParticles = derivedModel.getParticleChildren();
        for (int j = 0; j < derivedParticles.length; ++j) {
            final Object baseParticle = baseParticleMap.get(derivedParticles[j].getName());
            if (baseParticle == null) {
                recurseUnorderedValid = false;
                errors.add(XmlError.forObject("rcase-RecurseUnordered.2", new Object[] { printParticle(derivedParticles[j]) }, context));
                break;
            }
            if (baseParticle == MAPPED) {
                recurseUnorderedValid = false;
                errors.add(XmlError.forObject("rcase-RecurseUnordered.2.1", new Object[] { printParticle(derivedParticles[j]) }, context));
                break;
            }
            final SchemaParticle matchedBaseParticle = (SchemaParticle)baseParticle;
            if (derivedParticles[j].getMaxOccurs() == null || derivedParticles[j].getMaxOccurs().compareTo(BigInteger.ONE) > 0) {
                recurseUnorderedValid = false;
                errors.add(XmlError.forObject("rcase-RecurseUnordered.2.2a", new Object[] { printParticle(derivedParticles[j]), printMaxOccurs(derivedParticles[j].getMinOccurs()) }, context));
                break;
            }
            if (!isParticleValidRestriction(matchedBaseParticle, derivedParticles[j], errors, context)) {
                recurseUnorderedValid = false;
                break;
            }
            baseParticleMap.put(derivedParticles[j].getName(), MAPPED);
        }
        if (recurseUnorderedValid) {
            final Set baseParticleCollection = baseParticleMap.keySet();
            for (final QName baseParticleQName : baseParticleCollection) {
                if (baseParticleMap.get(baseParticleQName) != MAPPED && !baseParticleMap.get(baseParticleQName).isSkippable()) {
                    recurseUnorderedValid = false;
                    errors.add(XmlError.forObject("rcase-RecurseUnordered.2.3", new Object[] { printParticle(baseParticleMap.get(baseParticleQName)) }, context));
                }
            }
        }
        return recurseUnorderedValid;
    }
    
    private static boolean recurse(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        boolean recurseValid = true;
        if (!occurrenceRangeOK(baseModel, derivedModel, errors, context)) {
            return false;
        }
        SchemaParticle[] derivedParticleArray;
        SchemaParticle[] baseParticleArray;
        int i;
        int j;
        SchemaParticle derivedParticle;
        SchemaParticle baseParticle;
        for (derivedParticleArray = derivedModel.getParticleChildren(), baseParticleArray = baseModel.getParticleChildren(), i = 0, j = 0; i < derivedParticleArray.length && j < baseParticleArray.length; ++j) {
            derivedParticle = derivedParticleArray[i];
            baseParticle = baseParticleArray[j];
            if (isParticleValidRestriction(baseParticle, derivedParticle, errors, context)) {
                ++i;
            }
            else if (!baseParticle.isSkippable()) {
                recurseValid = false;
                errors.add(XmlError.forObject("rcase-Recurse.2.1", new Object[] { printParticle(derivedParticle), printParticle(derivedModel), printParticle(baseParticle), printParticle(baseModel) }, context));
                break;
            }
        }
        if (i < derivedParticleArray.length) {
            recurseValid = false;
            errors.add(XmlError.forObject("rcase-Recurse.2", new Object[] { printParticle(derivedModel), printParticle(baseModel), printParticles(derivedParticleArray, i) }, context));
        }
        else if (j < baseParticleArray.length) {
            final ArrayList particles = new ArrayList(baseParticleArray.length);
            for (int k = j; k < baseParticleArray.length; ++k) {
                if (!baseParticleArray[k].isSkippable()) {
                    particles.add(baseParticleArray[k]);
                }
            }
            if (particles.size() > 0) {
                recurseValid = false;
                errors.add(XmlError.forObject("rcase-Recurse.2.2", new Object[] { printParticle(baseModel), printParticle(derivedModel), printParticles(particles) }, context));
            }
        }
        return recurseValid;
    }
    
    private static boolean nsRecurseCheckCardinality(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        assert baseModel.getParticleType() == 5;
        assert derivedModel.getParticleType() == 3;
        boolean nsRecurseCheckCardinality = true;
        final SchemaParticleImpl asIfPart = new SchemaParticleImpl();
        asIfPart.setParticleType(baseModel.getParticleType());
        asIfPart.setWildcardProcess(baseModel.getWildcardProcess());
        asIfPart.setWildcardSet(baseModel.getWildcardSet());
        asIfPart.setMinOccurs(BigInteger.ZERO);
        asIfPart.setMaxOccurs(null);
        asIfPart.setTransitionRules(baseModel.getWildcardSet(), true);
        asIfPart.setTransitionNotes(baseModel.getWildcardSet(), true);
        final SchemaParticle[] particleChildren = derivedModel.getParticleChildren();
        for (int i = 0; i < particleChildren.length; ++i) {
            final SchemaParticle particle = particleChildren[i];
            switch (particle.getParticleType()) {
                case 4: {
                    nsRecurseCheckCardinality = nsCompat(asIfPart, (SchemaLocalElement)particle, errors, context);
                    break;
                }
                case 5: {
                    nsRecurseCheckCardinality = nsSubset(asIfPart, particle, errors, context);
                    break;
                }
                case 1:
                case 2:
                case 3: {
                    nsRecurseCheckCardinality = nsRecurseCheckCardinality(asIfPart, particle, errors, context);
                    break;
                }
            }
            if (!nsRecurseCheckCardinality) {
                break;
            }
        }
        if (nsRecurseCheckCardinality) {
            nsRecurseCheckCardinality = checkGroupOccurrenceOK(baseModel, derivedModel, errors, context);
        }
        return nsRecurseCheckCardinality;
    }
    
    private static boolean checkGroupOccurrenceOK(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        boolean groupOccurrenceOK = true;
        BigInteger minRange = BigInteger.ZERO;
        BigInteger maxRange = BigInteger.ZERO;
        switch (derivedModel.getParticleType()) {
            case 1:
            case 3: {
                minRange = getEffectiveMinRangeAllSeq(derivedModel);
                maxRange = getEffectiveMaxRangeAllSeq(derivedModel);
                break;
            }
            case 2: {
                minRange = getEffectiveMinRangeChoice(derivedModel);
                maxRange = getEffectiveMaxRangeChoice(derivedModel);
                break;
            }
        }
        if (minRange.compareTo(baseModel.getMinOccurs()) < 0) {
            groupOccurrenceOK = false;
            errors.add(XmlError.forObject("range-ok.1", new Object[] { printParticle(derivedModel), printParticle(baseModel) }, context));
        }
        final BigInteger UNBOUNDED = null;
        if (baseModel.getMaxOccurs() != UNBOUNDED) {
            if (maxRange == UNBOUNDED) {
                groupOccurrenceOK = false;
                errors.add(XmlError.forObject("range-ok.2", new Object[] { printParticle(derivedModel), printParticle(baseModel) }, context));
            }
            else if (maxRange.compareTo(baseModel.getMaxOccurs()) > 0) {
                groupOccurrenceOK = false;
                errors.add(XmlError.forObject("range-ok.2", new Object[] { printParticle(derivedModel), printParticle(baseModel) }, context));
            }
        }
        return groupOccurrenceOK;
    }
    
    private static BigInteger getEffectiveMaxRangeChoice(final SchemaParticle derivedModel) {
        BigInteger maxRange = BigInteger.ZERO;
        final BigInteger UNBOUNDED = null;
        boolean nonZeroParticleChildFound = false;
        BigInteger maxOccursInWildCardOrElement = BigInteger.ZERO;
        BigInteger maxOccursInGroup = BigInteger.ZERO;
        final SchemaParticle[] particleChildren = derivedModel.getParticleChildren();
        for (int i = 0; i < particleChildren.length; ++i) {
            final SchemaParticle particle = particleChildren[i];
            switch (particle.getParticleType()) {
                case 4:
                case 5: {
                    if (particle.getMaxOccurs() == UNBOUNDED) {
                        maxRange = UNBOUNDED;
                        break;
                    }
                    if (particle.getIntMaxOccurs() <= 0) {
                        break;
                    }
                    nonZeroParticleChildFound = true;
                    if (particle.getMaxOccurs().compareTo(maxOccursInWildCardOrElement) > 0) {
                        maxOccursInWildCardOrElement = particle.getMaxOccurs();
                        break;
                    }
                    break;
                }
                case 1:
                case 3: {
                    maxRange = getEffectiveMaxRangeAllSeq(particle);
                    if (maxRange != UNBOUNDED && maxRange.compareTo(maxOccursInGroup) > 0) {
                        maxOccursInGroup = maxRange;
                        break;
                    }
                    break;
                }
                case 2: {
                    maxRange = getEffectiveMaxRangeChoice(particle);
                    if (maxRange != UNBOUNDED && maxRange.compareTo(maxOccursInGroup) > 0) {
                        maxOccursInGroup = maxRange;
                        break;
                    }
                    break;
                }
            }
            if (maxRange == UNBOUNDED) {
                break;
            }
        }
        if (maxRange != UNBOUNDED) {
            if (nonZeroParticleChildFound && derivedModel.getMaxOccurs() == UNBOUNDED) {
                maxRange = UNBOUNDED;
            }
            else {
                maxRange = derivedModel.getMaxOccurs().multiply(maxOccursInWildCardOrElement.add(maxOccursInGroup));
            }
        }
        return maxRange;
    }
    
    private static BigInteger getEffectiveMaxRangeAllSeq(final SchemaParticle derivedModel) {
        BigInteger maxRange = BigInteger.ZERO;
        final BigInteger UNBOUNDED = null;
        boolean nonZeroParticleChildFound = false;
        BigInteger maxOccursTotal = BigInteger.ZERO;
        BigInteger maxOccursInGroup = BigInteger.ZERO;
        final SchemaParticle[] particleChildren = derivedModel.getParticleChildren();
        for (int i = 0; i < particleChildren.length; ++i) {
            final SchemaParticle particle = particleChildren[i];
            switch (particle.getParticleType()) {
                case 4:
                case 5: {
                    if (particle.getMaxOccurs() == UNBOUNDED) {
                        maxRange = UNBOUNDED;
                        break;
                    }
                    if (particle.getIntMaxOccurs() > 0) {
                        nonZeroParticleChildFound = true;
                        maxOccursTotal = maxOccursTotal.add(particle.getMaxOccurs());
                        break;
                    }
                    break;
                }
                case 1:
                case 3: {
                    maxRange = getEffectiveMaxRangeAllSeq(particle);
                    if (maxRange != UNBOUNDED && maxRange.compareTo(maxOccursInGroup) > 0) {
                        maxOccursInGroup = maxRange;
                        break;
                    }
                    break;
                }
                case 2: {
                    maxRange = getEffectiveMaxRangeChoice(particle);
                    if (maxRange != UNBOUNDED && maxRange.compareTo(maxOccursInGroup) > 0) {
                        maxOccursInGroup = maxRange;
                        break;
                    }
                    break;
                }
            }
            if (maxRange == UNBOUNDED) {
                break;
            }
        }
        if (maxRange != UNBOUNDED) {
            if (nonZeroParticleChildFound && derivedModel.getMaxOccurs() == UNBOUNDED) {
                maxRange = UNBOUNDED;
            }
            else {
                maxRange = derivedModel.getMaxOccurs().multiply(maxOccursTotal.add(maxOccursInGroup));
            }
        }
        return maxRange;
    }
    
    private static BigInteger getEffectiveMinRangeChoice(final SchemaParticle derivedModel) {
        final SchemaParticle[] particleChildren = derivedModel.getParticleChildren();
        if (particleChildren.length == 0) {
            return BigInteger.ZERO;
        }
        BigInteger minRange = null;
        for (int i = 0; i < particleChildren.length; ++i) {
            final SchemaParticle particle = particleChildren[i];
            switch (particle.getParticleType()) {
                case 4:
                case 5: {
                    if (minRange == null || minRange.compareTo(particle.getMinOccurs()) > 0) {
                        minRange = particle.getMinOccurs();
                        break;
                    }
                    break;
                }
                case 1:
                case 3: {
                    final BigInteger mrs = getEffectiveMinRangeAllSeq(particle);
                    if (minRange == null || minRange.compareTo(mrs) > 0) {
                        minRange = mrs;
                        break;
                    }
                    break;
                }
                case 2: {
                    final BigInteger mrc = getEffectiveMinRangeChoice(particle);
                    if (minRange == null || minRange.compareTo(mrc) > 0) {
                        minRange = mrc;
                        break;
                    }
                    break;
                }
            }
        }
        if (minRange == null) {
            minRange = BigInteger.ZERO;
        }
        minRange = derivedModel.getMinOccurs().multiply(minRange);
        return minRange;
    }
    
    private static BigInteger getEffectiveMinRangeAllSeq(final SchemaParticle derivedModel) {
        BigInteger minRange = BigInteger.ZERO;
        final SchemaParticle[] particleChildren = derivedModel.getParticleChildren();
        BigInteger particleTotalMinOccurs = BigInteger.ZERO;
        for (int i = 0; i < particleChildren.length; ++i) {
            final SchemaParticle particle = particleChildren[i];
            switch (particle.getParticleType()) {
                case 4:
                case 5: {
                    particleTotalMinOccurs = particleTotalMinOccurs.add(particle.getMinOccurs());
                    break;
                }
                case 1:
                case 3: {
                    particleTotalMinOccurs = particleTotalMinOccurs.add(getEffectiveMinRangeAllSeq(particle));
                    break;
                }
                case 2: {
                    particleTotalMinOccurs = particleTotalMinOccurs.add(getEffectiveMinRangeChoice(particle));
                    break;
                }
            }
        }
        minRange = derivedModel.getMinOccurs().multiply(particleTotalMinOccurs);
        return minRange;
    }
    
    private static boolean nsSubset(final SchemaParticle baseModel, final SchemaParticle derivedModel, final Collection errors, final XmlObject context) {
        assert baseModel.getParticleType() == 5;
        assert derivedModel.getParticleType() == 5;
        boolean nsSubset = false;
        if (occurrenceRangeOK(baseModel, derivedModel, errors, context)) {
            if (baseModel.getWildcardSet().inverse().isDisjoint(derivedModel.getWildcardSet())) {
                nsSubset = true;
            }
            else {
                nsSubset = false;
                errors.add(XmlError.forObject("rcase-NSSubset.2", new Object[] { printParticle(derivedModel), printParticle(baseModel) }, context));
            }
        }
        else {
            nsSubset = false;
        }
        return nsSubset;
    }
    
    private static boolean nsCompat(final SchemaParticle baseModel, final SchemaLocalElement derivedElement, final Collection errors, final XmlObject context) {
        assert baseModel.getParticleType() == 5;
        boolean nsCompat = false;
        if (baseModel.getWildcardSet().contains(derivedElement.getName())) {
            if (occurrenceRangeOK(baseModel, (SchemaParticle)derivedElement, errors, context)) {
                nsCompat = true;
            }
        }
        else {
            nsCompat = false;
            errors.add(XmlError.forObject("rcase-NSCompat.1", new Object[] { printParticle((SchemaParticle)derivedElement), printParticle(baseModel) }, context));
        }
        return nsCompat;
    }
    
    private static boolean nameAndTypeOK(final SchemaLocalElement baseElement, final SchemaLocalElement derivedElement, final Collection errors, final XmlObject context) {
        if (!((SchemaParticle)baseElement).canStartWithElement(derivedElement.getName())) {
            errors.add(XmlError.forObject("rcase-NameAndTypeOK.1", new Object[] { printParticle((SchemaParticle)derivedElement), printParticle((SchemaParticle)baseElement) }, context));
            return false;
        }
        if (!baseElement.isNillable() && derivedElement.isNillable()) {
            errors.add(XmlError.forObject("rcase-NameAndTypeOK.2", new Object[] { printParticle((SchemaParticle)derivedElement), printParticle((SchemaParticle)baseElement) }, context));
            return false;
        }
        return occurrenceRangeOK((SchemaParticle)baseElement, (SchemaParticle)derivedElement, errors, context) && checkFixed(baseElement, derivedElement, errors, context) && checkIdentityConstraints(baseElement, derivedElement, errors, context) && typeDerivationOK(baseElement.getType(), derivedElement.getType(), errors, context) && blockSetOK(baseElement, derivedElement, errors, context);
    }
    
    private static boolean blockSetOK(final SchemaLocalElement baseElement, final SchemaLocalElement derivedElement, final Collection errors, final XmlObject context) {
        if (baseElement.blockRestriction() && !derivedElement.blockRestriction()) {
            errors.add(XmlError.forObject("rcase-NameAndTypeOK.6", new Object[] { printParticle((SchemaParticle)derivedElement), "restriction", printParticle((SchemaParticle)baseElement) }, context));
            return false;
        }
        if (baseElement.blockExtension() && !derivedElement.blockExtension()) {
            errors.add(XmlError.forObject("rcase-NameAndTypeOK.6", new Object[] { printParticle((SchemaParticle)derivedElement), "extension", printParticle((SchemaParticle)baseElement) }, context));
            return false;
        }
        if (baseElement.blockSubstitution() && !derivedElement.blockSubstitution()) {
            errors.add(XmlError.forObject("rcase-NameAndTypeOK.6", new Object[] { printParticle((SchemaParticle)derivedElement), "substitution", printParticle((SchemaParticle)baseElement) }, context));
            return false;
        }
        return true;
    }
    
    private static boolean typeDerivationOK(final SchemaType baseType, final SchemaType derivedType, final Collection errors, final XmlObject context) {
        boolean typeDerivationOK = false;
        if (baseType.isAssignableFrom(derivedType)) {
            typeDerivationOK = checkAllDerivationsForRestriction(baseType, derivedType, errors, context);
        }
        else {
            typeDerivationOK = false;
            errors.add(XmlError.forObject("rcase-NameAndTypeOK.7a", new Object[] { printType(derivedType), printType(baseType) }, context));
        }
        return typeDerivationOK;
    }
    
    private static boolean checkAllDerivationsForRestriction(final SchemaType baseType, final SchemaType derivedType, final Collection errors, final XmlObject context) {
        boolean allDerivationsAreRestrictions = true;
        SchemaType currentType = derivedType;
        Set possibleTypes = null;
        if (baseType.getSimpleVariety() == 2) {
            possibleTypes = new HashSet(Arrays.asList(baseType.getUnionConstituentTypes()));
        }
        while (!baseType.equals(currentType) && possibleTypes != null && !possibleTypes.contains(currentType)) {
            if (currentType.getDerivationType() != 1) {
                allDerivationsAreRestrictions = false;
                errors.add(XmlError.forObject("rcase-NameAndTypeOK.7b", new Object[] { printType(derivedType), printType(baseType), printType(currentType) }, context));
                break;
            }
            currentType = currentType.getBaseType();
        }
        return allDerivationsAreRestrictions;
    }
    
    private static boolean checkIdentityConstraints(final SchemaLocalElement baseElement, final SchemaLocalElement derivedElement, final Collection errors, final XmlObject context) {
        boolean identityConstraintsOK = true;
        final SchemaIdentityConstraint[] baseConstraints = baseElement.getIdentityConstraints();
        final SchemaIdentityConstraint[] derivedConstraints = derivedElement.getIdentityConstraints();
        for (int i = 0; i < derivedConstraints.length; ++i) {
            final SchemaIdentityConstraint derivedConstraint = derivedConstraints[i];
            if (checkForIdentityConstraintExistence(baseConstraints, derivedConstraint)) {
                identityConstraintsOK = false;
                errors.add(XmlError.forObject("rcase-NameAndTypeOK.5", new Object[] { printParticle((SchemaParticle)derivedElement), printParticle((SchemaParticle)baseElement) }, context));
                break;
            }
        }
        return identityConstraintsOK;
    }
    
    private static boolean checkForIdentityConstraintExistence(final SchemaIdentityConstraint[] baseConstraints, final SchemaIdentityConstraint derivedConstraint) {
        boolean identityConstraintExists = false;
        for (int i = 0; i < baseConstraints.length; ++i) {
            final SchemaIdentityConstraint baseConstraint = baseConstraints[i];
            if (baseConstraint.getName().equals(derivedConstraint.getName())) {
                identityConstraintExists = true;
                break;
            }
        }
        return identityConstraintExists;
    }
    
    private static boolean checkFixed(final SchemaLocalElement baseModel, final SchemaLocalElement derivedModel, final Collection errors, final XmlObject context) {
        boolean checkFixed = false;
        if (baseModel.isFixed()) {
            if (baseModel.getDefaultText().equals(derivedModel.getDefaultText())) {
                checkFixed = true;
            }
            else {
                errors.add(XmlError.forObject("rcase-NameAndTypeOK.4", new Object[] { printParticle((SchemaParticle)derivedModel), derivedModel.getDefaultText(), printParticle((SchemaParticle)baseModel), baseModel.getDefaultText() }, context));
                checkFixed = false;
            }
        }
        else {
            checkFixed = true;
        }
        return checkFixed;
    }
    
    private static boolean occurrenceRangeOK(final SchemaParticle baseParticle, final SchemaParticle derivedParticle, final Collection errors, final XmlObject context) {
        boolean occurrenceRangeOK = false;
        if (derivedParticle.getMinOccurs().compareTo(baseParticle.getMinOccurs()) >= 0) {
            if (baseParticle.getMaxOccurs() == null) {
                occurrenceRangeOK = true;
            }
            else if (derivedParticle.getMaxOccurs() != null && baseParticle.getMaxOccurs() != null && derivedParticle.getMaxOccurs().compareTo(baseParticle.getMaxOccurs()) <= 0) {
                occurrenceRangeOK = true;
            }
            else {
                occurrenceRangeOK = false;
                errors.add(XmlError.forObject("range-ok.2", new Object[] { printParticle(derivedParticle), printMaxOccurs(derivedParticle.getMaxOccurs()), printParticle(baseParticle), printMaxOccurs(baseParticle.getMaxOccurs()) }, context));
            }
        }
        else {
            occurrenceRangeOK = false;
            errors.add(XmlError.forObject("range-ok.1", new Object[] { printParticle(derivedParticle), derivedParticle.getMinOccurs().toString(), printParticle(baseParticle), baseParticle.getMinOccurs().toString() }, context));
        }
        return occurrenceRangeOK;
    }
    
    private static String printParticles(final List parts) {
        return printParticles(parts.toArray(new SchemaParticle[parts.size()]));
    }
    
    private static String printParticles(final SchemaParticle[] parts) {
        return printParticles(parts, 0, parts.length);
    }
    
    private static String printParticles(final SchemaParticle[] parts, final int start) {
        return printParticles(parts, start, parts.length);
    }
    
    private static String printParticles(final SchemaParticle[] parts, final int start, final int end) {
        final StringBuffer buf = new StringBuffer(parts.length * 30);
        int i = start;
        while (i < end) {
            buf.append(printParticle(parts[i]));
            if (++i != end) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }
    
    private static String printParticle(final SchemaParticle part) {
        switch (part.getParticleType()) {
            case 1: {
                return "<all>";
            }
            case 2: {
                return "<choice>";
            }
            case 4: {
                return "<element name=\"" + QNameHelper.pretty(part.getName()) + "\">";
            }
            case 3: {
                return "<sequence>";
            }
            case 5: {
                return "<any>";
            }
            default: {
                return "??";
            }
        }
    }
    
    private static String printMaxOccurs(final BigInteger bi) {
        if (bi == null) {
            return "unbounded";
        }
        return bi.toString();
    }
    
    private static String printType(final SchemaType type) {
        if (type.getName() != null) {
            return QNameHelper.pretty(type.getName());
        }
        return type.toString();
    }
    
    private static void checkSubstitutionGroups(final SchemaGlobalElement[] elts) {
        final StscState state = StscState.get();
        for (int i = 0; i < elts.length; ++i) {
            final SchemaGlobalElement elt = elts[i];
            final SchemaGlobalElement head = elt.substitutionGroup();
            if (head != null) {
                final SchemaType headType = head.getType();
                final SchemaType tailType = elt.getType();
                final XmlObject parseTree = ((SchemaGlobalElementImpl)elt)._parseObject;
                if (!headType.isAssignableFrom(tailType)) {
                    state.error("e-props-correct.4", new Object[] { QNameHelper.pretty(elt.getName()), QNameHelper.pretty(head.getName()) }, parseTree);
                }
                else if (head.finalExtension() && head.finalRestriction()) {
                    state.error("e-props-correct.4a", new Object[] { QNameHelper.pretty(elt.getName()), QNameHelper.pretty(head.getName()), "#all" }, parseTree);
                }
                else if (!headType.equals(tailType)) {
                    if (head.finalExtension() && tailType.getDerivationType() == 2) {
                        state.error("e-props-correct.4a", new Object[] { QNameHelper.pretty(elt.getName()), QNameHelper.pretty(head.getName()), "extension" }, parseTree);
                    }
                    else if (head.finalRestriction() && tailType.getDerivationType() == 1) {
                        state.error("e-props-correct.4a", new Object[] { QNameHelper.pretty(elt.getName()), QNameHelper.pretty(head.getName()), "restriction" }, parseTree);
                    }
                }
            }
        }
    }
}
