package org.apache.xerces.impl.scd;

import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSWildcard;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSIDCDefinition;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xs.XSObjectList;
import java.util.ArrayList;
import java.util.List;
import org.apache.xerces.xs.XSModel;

public class SCDResolver
{
    private XSModel xsModel;
    private List result;
    private List currentComponents;
    private SCDParser parser;
    private static final short NO_FILTER = -1;
    private static final boolean IS_SPEC_COMPLIANT = false;
    private static final short LIST_SIZE = 30;
    
    public SCDResolver(final XSModel xsModel) {
        this.xsModel = xsModel;
        this.result = new ArrayList(30);
        this.currentComponents = new ArrayList(30);
        this.parser = new SCDParser();
    }
    
    public XSObjectList resolve(final String s) throws SCDException {
        final List relativeSCD = this.parser.parseRelativeSCD(s, false);
        if (relativeSCD.size() == 1 && ((Step)relativeSCD.get(0)).getAxisType() == 100 && ((Step)relativeSCD.get(0)).getNametest() == null && ((Step)relativeSCD.get(0)).getPredicate() == 0) {
            throw new SCDException("Error in SCD: Schema step is not supported");
        }
        this.result.clear();
        this.applyFirstStep((Step)relativeSCD.get(0));
        return this.evaluate(relativeSCD, 1);
    }
    
    public XSObjectList resolve(final String s, final NamespaceContext namespaceContext) throws SCDException {
        final List scp = this.parser.parseSCP(s, namespaceContext, false);
        if (scp.size() == 1 && ((Step)scp.get(0)).getAxisType() == 100 && ((Step)scp.get(0)).getNametest() == null && ((Step)scp.get(0)).getPredicate() == 0) {
            throw new SCDException("Error in SCD: Schema step is not supported");
        }
        this.result.clear();
        this.applyFirstStep((Step)scp.get(0));
        return this.evaluate(scp, 1);
    }
    
    public XSObjectList resolve(final String s, final NamespaceContext namespaceContext, final XSObject xsObject) throws SCDException {
        final List scp = this.parser.parseSCP(s, namespaceContext, true);
        this.result.clear();
        this.result.add(xsObject);
        return this.evaluate(scp, 0);
    }
    
    public XSObjectList resolve(final String s, final XSObject xsObject) throws SCDException {
        final List relativeSCD = this.parser.parseRelativeSCD(s, true);
        this.result.clear();
        this.result.add(xsObject);
        return this.evaluate(relativeSCD, 0);
    }
    
    private XSObjectList evaluate(final List list, final int n) throws SCDException {
        for (int i = n; i < list.size(); ++i) {
            this.currentComponents.clear();
            final Step step = list.get(i);
            final short axisType = step.getAxisType();
            for (int j = 0; j < this.result.size(); ++j) {
                this.currentComponents.add(this.result.get(j));
            }
            if (axisType != 27) {
                for (int k = 0; k < this.currentComponents.size(); ++k) {
                    this.addElidedComponents((XSObject)this.currentComponents.get(k));
                }
            }
            this.result.clear();
            this.applyStep(step);
            if (axisType == 27) {
                final Step step2 = list.get(++i);
                final List currentComponents = this.currentComponents;
                this.currentComponents = this.result;
                (this.result = currentComponents).clear();
                this.applyStep(step2);
            }
        }
        final XSObjectListImpl xsObjectListImpl = new XSObjectListImpl();
        for (int l = 0; l < this.result.size(); ++l) {
            xsObjectListImpl.addXSObject((XSObject)this.result.get(l));
        }
        return xsObjectListImpl;
    }
    
    private void addElidedComponents(final XSObject xsObject) {
        for (int i = this.currentComponents.size() - 1; i < this.currentComponents.size(); ++i) {
            this.term((XSObject)this.currentComponents.get(i), (short)7, SCDParser.WILDCARD, this.currentComponents);
        }
        switch (xsObject.getType()) {
            case 2: {
                final XSTypeDefinition typeDefinition = ((XSElementDeclaration)xsObject).getTypeDefinition();
                if (typeDefinition != null && !this.currentComponents.contains(typeDefinition)) {
                    this.currentComponents.add(typeDefinition);
                    break;
                }
                break;
            }
            case 1: {
                final XSSimpleTypeDefinition typeDefinition2 = ((XSAttributeDeclaration)xsObject).getTypeDefinition();
                if (typeDefinition2 != null && !this.currentComponents.contains(typeDefinition2)) {
                    this.currentComponents.add(typeDefinition2);
                    break;
                }
                break;
            }
        }
    }
    
    private void applyFirstStep(final Step step) throws SCDException {
        XSNamedMap xsNamedMap = null;
        switch (step.getAxisType()) {
            case 21: {
                final XSObjectList annotations = this.xsModel.getAnnotations();
                for (int i = 0; i < annotations.size(); ++i) {
                    this.addComponent(annotations.item(i), step.getNametest(), this.result);
                }
                break;
            }
            case 1: {
                xsNamedMap = this.xsModel.getComponents((short)2);
                break;
            }
            case 2: {
                xsNamedMap = this.xsModel.getComponents((short)3);
                break;
            }
            case 0: {
                xsNamedMap = this.xsModel.getComponents((short)1);
                break;
            }
            case 3: {
                xsNamedMap = this.xsModel.getComponents((short)5);
                break;
            }
            case 4: {
                xsNamedMap = this.xsModel.getComponents((short)6);
                break;
            }
            case 8: {
                xsNamedMap = this.xsModel.getComponents((short)11);
                break;
            }
            case 22:
            case 27: {
                this.currentComponents.clear();
                this.addTopLevelComponents(step.getNametest());
                final int size = this.currentComponents.size();
                for (int j = 0; j < this.currentComponents.size(); ++j) {
                    this.componentChildren((XSObject)this.currentComponents.get(j), (short)(-1), SCDParser.WILDCARD, this.currentComponents);
                }
                for (int k = (step.getAxisType() == 27) ? 0 : size; k < this.currentComponents.size(); ++k) {
                    this.addComponent((XSObject)this.currentComponents.get(k), step.getNametest(), this.result);
                }
                break;
            }
            default: {
                throw new SCDException("Error in SCD: Unsupported top level component type " + step.getAxisName());
            }
        }
        if (xsNamedMap != null && !xsNamedMap.isEmpty()) {
            for (int l = 0; l < xsNamedMap.size(); ++l) {
                this.addComponent(xsNamedMap.item(l), step.getNametest(), this.result);
            }
        }
        this.applyPredicate(step.getPredicate());
    }
    
    private void applyStep(final Step step) throws SCDException {
        switch (step.getAxisType()) {
            case 1: {
                for (int i = 0; i < this.currentComponents.size(); ++i) {
                    this.term((XSObject)this.currentComponents.get(i), (short)2, step.getNametest(), this.result);
                }
                break;
            }
            case 0: {
                for (int j = 0; j < this.currentComponents.size(); ++j) {
                    this.componentLinked((XSObject)this.currentComponents.get(j), (short)1, step.getNametest(), this.result);
                }
                break;
            }
            case 2: {
                for (int k = 0; k < this.currentComponents.size(); ++k) {
                    this.componentChildren((XSObject)this.currentComponents.get(k), (short)3, step.getNametest(), this.result);
                }
                break;
            }
            case 23: {
                for (int l = 0; l < this.currentComponents.size(); ++l) {
                    this.result.add(this.currentComponents.get(l));
                }
                return;
            }
            case 22:
            case 27: {
                final int size = this.currentComponents.size();
                for (int n = 0; n < this.currentComponents.size(); ++n) {
                    this.componentChildren((XSObject)this.currentComponents.get(n), (short)(-1), SCDParser.WILDCARD, this.currentComponents);
                }
                for (int n2 = (step.getAxisType() == 27) ? 0 : size; n2 < this.currentComponents.size(); ++n2) {
                    this.addComponent((XSObject)this.currentComponents.get(n2), step.getNametest(), this.result);
                }
                break;
            }
            case 3: {
                for (int n3 = 0; n3 < this.currentComponents.size(); ++n3) {
                    this.componentLinked((XSObject)this.currentComponents.get(n3), (short)5, step.getNametest(), this.result);
                }
                break;
            }
            case 4: {
                for (int n4 = 0; n4 < this.currentComponents.size(); ++n4) {
                    this.componentLinked((XSObject)this.currentComponents.get(n4), (short)6, step.getNametest(), this.result);
                }
                break;
            }
            case 5: {
                for (int n5 = 0; n5 < this.currentComponents.size(); ++n5) {
                    this.componentLinked((XSObject)this.currentComponents.get(n5), (short)10, step.getNametest(), this.result);
                }
                break;
            }
            case 6: {
                throw new SCDException("Error in SCD: Assertion axis is not supported");
            }
            case 7: {
                throw new SCDException("Error in SCD: Alternative axis is not supported");
            }
            case 8: {
                for (int n6 = 0; n6 < this.currentComponents.size(); ++n6) {
                    this.componentLinked((XSObject)this.currentComponents.get(n6), (short)11, step.getNametest(), this.result);
                }
                break;
            }
            case 9: {
                for (int n7 = 0; n7 < this.currentComponents.size(); ++n7) {
                    this.term((XSObject)this.currentComponents.get(n7), (short)7, step.getNametest(), this.result);
                }
                break;
            }
            case 10: {
                for (int n8 = 0; n8 < this.currentComponents.size(); ++n8) {
                    final XSObject xsObject = this.currentComponents.get(n8);
                    final short type = xsObject.getType();
                    if (type == 3) {
                        if (((XSTypeDefinition)xsObject).getTypeCategory() == 15) {
                            this.addComponent(((XSComplexTypeDefinition)xsObject).getAttributeWildcard(), step.getNametest(), this.result);
                        }
                    }
                    else if (type == 5) {
                        this.addComponent(((XSAttributeGroupDefinition)xsObject).getAttributeWildcard(), step.getNametest(), this.result);
                    }
                }
                break;
            }
            case 11: {
                for (int n9 = 0; n9 < this.currentComponents.size(); ++n9) {
                    this.term((XSObject)this.currentComponents.get(n9), (short)9, step.getNametest(), this.result);
                }
                break;
            }
            case 12: {
                for (int n10 = 0; n10 < this.currentComponents.size(); ++n10) {
                    this.componentLinked((XSObject)this.currentComponents.get(n10), (short)13, step.getNametest(), this.result);
                }
                break;
            }
            case 13: {
                for (int n11 = 0; n11 < this.currentComponents.size(); ++n11) {
                    this.componentScope((XSObject)this.currentComponents.get(n11), this.result);
                }
                break;
            }
            case 14: {
                throw new SCDException("Error in SCD: Context axis is not supported");
            }
            case 15: {
                for (int n12 = 0; n12 < this.currentComponents.size(); ++n12) {
                    final XSObject xsObject2 = this.currentComponents.get(n12);
                    if (xsObject2.getType() == 2) {
                        this.addComponent(((XSElementDeclaration)xsObject2).getSubstitutionGroupAffiliation(), step.getNametest(), this.result);
                    }
                }
                break;
            }
            case 16: {
                for (int n13 = 0; n13 < this.currentComponents.size(); ++n13) {
                    if (((XSObject)this.currentComponents.get(n13)).getType() == 3) {
                        this.addComponent(((XSTypeDefinition)this.currentComponents.get(n13)).getBaseType(), step.getNametest(), this.result);
                    }
                }
                break;
            }
            case 17: {
                for (int n14 = 0; n14 < this.currentComponents.size(); ++n14) {
                    if (((XSObject)this.currentComponents.get(n14)).getType() == 3) {
                        final XSObject xsObject3 = this.currentComponents.get(n14);
                        if (((XSTypeDefinition)xsObject3).getTypeCategory() == 16) {
                            this.addComponent(((XSSimpleTypeDefinition)xsObject3).getItemType(), step.getNametest(), this.result);
                        }
                    }
                }
                break;
            }
            case 18: {
                for (int n15 = 0; n15 < this.currentComponents.size(); ++n15) {
                    if (((XSObject)this.currentComponents.get(n15)).getType() == 3) {
                        final XSObject xsObject4 = this.currentComponents.get(n15);
                        if (((XSTypeDefinition)xsObject4).getTypeCategory() == 16) {
                            final XSObjectList memberTypes = ((XSSimpleTypeDefinition)xsObject4).getMemberTypes();
                            for (int n16 = 0; n16 < memberTypes.size(); ++n16) {
                                this.addComponent((XSObject)memberTypes.get(n16), step.getNametest(), this.result);
                            }
                        }
                    }
                }
                break;
            }
            case 19: {
                for (int n17 = 0; n17 < this.currentComponents.size(); ++n17) {
                    if (((XSObject)this.currentComponents.get(n17)).getType() == 3) {
                        final XSObject xsObject5 = this.currentComponents.get(n17);
                        if (((XSTypeDefinition)xsObject5).getTypeCategory() == 16) {
                            this.addComponent(((XSSimpleTypeDefinition)xsObject5).getPrimitiveType(), step.getNametest(), this.result);
                        }
                    }
                }
                break;
            }
            case 20: {
                for (int n18 = 0; n18 < this.currentComponents.size(); ++n18) {
                    if (((XSObject)this.currentComponents.get(n18)).getType() == 10) {
                        this.addComponent(((XSIDCDefinition)this.currentComponents.get(n18)).getRefKey(), step.getNametest(), this.result);
                    }
                }
                break;
            }
            case 21: {
                for (int n19 = 0; n19 < this.currentComponents.size(); ++n19) {
                    this.annotations((XSObject)this.currentComponents.get(n19), this.result);
                }
                break;
            }
            case 24: {
                List list = null;
                for (int n20 = 0; n20 < this.currentComponents.size(); ++n20) {
                    final XSObject xsObject6 = this.currentComponents.get(n20);
                    if (xsObject6.getType() == 3) {
                        if (((XSTypeDefinition)xsObject6).getTypeCategory() == 15) {
                            list = ((XSComplexTypeDefinition)xsObject6).getAttributeUses();
                        }
                    }
                    else if (xsObject6.getType() == 5) {
                        list = ((XSAttributeGroupDefinition)xsObject6).getAttributeUses();
                    }
                    if (list != null) {
                        for (int n21 = 0; n21 < list.size(); ++n21) {
                            this.addComponent((XSObject)list.get(n21), step.getNametest(), this.result);
                        }
                    }
                }
                break;
            }
            case 25: {
                for (int n22 = 0; n22 < this.currentComponents.size(); ++n22) {
                    final XSObject xsObject7 = this.currentComponents.get(n22);
                    if (xsObject7.getType() == 7) {
                        final XSObjectList particles = ((XSModelGroup)xsObject7).getParticles();
                        for (int n23 = 0; n23 < particles.size(); ++n23) {
                            this.addComponent((XSObject)particles.get(n23), step.getNametest(), this.result);
                        }
                    }
                }
                break;
            }
            case 26: {
                throw new SCDException("Error in SCD: Extension axis is not supported");
            }
            default: {
                throw new SCDException("Error in SCD: Unsupported axis type " + step.getAxisName());
            }
        }
        this.applyPredicate(step.getPredicate());
    }
    
    private void addTopLevelComponents(final QName qName) {
        final XSObjectList annotations = this.xsModel.getAnnotations();
        for (int i = 0; i < annotations.size(); ++i) {
            this.addComponent(annotations.item(i), qName, this.result);
        }
        final short[] array = { 2, 3, 1, 5, 6, 11 };
        for (int j = 0; j < array.length; ++j) {
            final XSNamedMap components = this.xsModel.getComponents(array[j]);
            if (!components.isEmpty()) {
                for (int k = 0; k < components.size(); ++k) {
                    this.addComponent(components.item(j), qName, this.result);
                }
            }
        }
    }
    
    private void applyPredicate(final int n) throws SCDException {
        if (n == 0) {
            return;
        }
        if (n > 0 && n <= this.result.size()) {
            final XSObject xsObject = this.result.get(n - 1);
            this.result.clear();
            this.result.add(xsObject);
            return;
        }
        throw new SCDException("Error in SCD: Invalid predicate value (" + n + ") detected");
    }
    
    private void term(final XSObject xsObject, final short n, final QName qName, final List list) {
        switch (xsObject.getType()) {
            case 6: {
                if (-1 == n || 7 == n) {
                    this.addComponent(((XSModelGroupDefinition)xsObject).getModelGroup(), qName, list);
                    break;
                }
                break;
            }
            case 3: {
                if (((XSTypeDefinition)xsObject).getTypeCategory() != 15) {
                    break;
                }
                final XSParticle particle = ((XSComplexTypeDefinition)xsObject).getParticle();
                if (particle == null) {
                    break;
                }
                final XSTerm term = particle.getTerm();
                if (-1 == n || (term != null && term.getType() == n)) {
                    this.addComponent(term, qName, list);
                    break;
                }
                break;
            }
            case 8: {
                final XSTerm term2 = ((XSParticle)xsObject).getTerm();
                if (-1 == n || (term2 != null && term2.getType() == n)) {
                    this.addComponent(term2, qName, list);
                    break;
                }
                break;
            }
            case 7: {
                final XSObjectList particles = ((XSModelGroup)xsObject).getParticles();
                for (int i = 0; i < particles.size(); ++i) {
                    final XSTerm term3 = ((XSParticle)particles.item(i)).getTerm();
                    if (-1 == n || (term3 != null && term3.getType() == n)) {
                        this.addComponent(term3, qName, list);
                    }
                }
                break;
            }
        }
    }
    
    private String componentVariety(final XSObject xsObject) {
        final short type = xsObject.getType();
        if (type == 7) {
            switch (((XSModelGroup)xsObject).getCompositor()) {
                case 1: {
                    return "sequence";
                }
                case 3: {
                    return "all";
                }
                case 2: {
                    return "choice";
                }
            }
        }
        else if (type == 13 || type == 14) {
            short n;
            if (type == 13) {
                n = ((XSFacet)xsObject).getFacetKind();
            }
            else {
                n = ((XSMultiValueFacet)xsObject).getFacetKind();
            }
            switch (n) {
                case 2048: {
                    return "enumeration";
                }
                case 1024: {
                    return "fractionDigits";
                }
                case 1: {
                    return "length";
                }
                case 64: {
                    return "maxExclusive";
                }
                case 32: {
                    return "maxInclusive";
                }
                case 4: {
                    return "maxLength";
                }
                case 128: {
                    return "minExclusive";
                }
                case 256: {
                    return "minInclusive";
                }
                case 2: {
                    return "minLength";
                }
                case 8: {
                    return "pattern";
                }
                case 512: {
                    return "totalDigits";
                }
                case 16: {
                    return "whiteSpace";
                }
            }
        }
        return null;
    }
    
    private void componentLinked(final XSObject xsObject, final short n, final QName qName, final List list) throws SCDException {
        switch (xsObject.getType()) {
            case 1: {
                this.componentChildren(xsObject, n, qName, list);
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                }
                if ((-1 == n || 3 == n) && ((XSAttributeDeclaration)xsObject).getScope() == 2) {
                    this.addComponent(((XSAttributeDeclaration)xsObject).getEnclosingCTDefinition(), qName, list);
                    break;
                }
                break;
            }
            case 2: {
                this.componentChildren(xsObject, n, qName, list);
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                }
                if ((-1 == n || 3 == n) && ((XSElementDeclaration)xsObject).getScope() == 2) {
                    this.addComponent(((XSElementDeclaration)xsObject).getEnclosingCTDefinition(), qName, list);
                }
                if (-1 == n || 10 == n) {
                    final XSNamedMap identityConstraints = ((XSElementDeclaration)xsObject).getIdentityConstraints();
                    for (int i = 0; i < identityConstraints.size(); ++i) {
                        this.addComponent(identityConstraints.item(i), qName, list);
                    }
                }
                if (-1 == n || 2 == n) {
                    this.addComponent(((XSElementDeclaration)xsObject).getSubstitutionGroupAffiliation(), qName, list);
                    break;
                }
                break;
            }
            case 3: {
                if (((XSTypeDefinition)xsObject).getTypeCategory() == 16) {
                    this.componentChildren(xsObject, n, qName, list);
                    if (-1 == n || 12 == n) {
                        this.annotations(xsObject, list);
                    }
                    if (-1 == n || 3 == n) {
                        this.addComponent(((XSSimpleTypeDefinition)xsObject).getBaseType(), qName, list);
                        this.addComponent(((XSSimpleTypeDefinition)xsObject).getPrimitiveType(), qName, list);
                        this.addComponent(((XSSimpleTypeDefinition)xsObject).getItemType(), qName, list);
                        final XSObjectList memberTypes = ((XSSimpleTypeDefinition)xsObject).getMemberTypes();
                        for (int j = 0; j < memberTypes.size(); ++j) {
                            this.addComponent(memberTypes.item(j), qName, list);
                        }
                        break;
                    }
                    break;
                }
                else {
                    this.componentChildren(xsObject, n, qName, list);
                    if (-1 == n || 12 == n) {
                        this.annotations(xsObject, list);
                    }
                    if (-1 == n || 3 == n) {
                        this.addComponent(((XSComplexTypeDefinition)xsObject).getBaseType(), qName, list);
                    }
                    if (-1 == n || 9 == n) {
                        this.addComponent(((XSComplexTypeDefinition)xsObject).getAttributeWildcard(), qName, list);
                        break;
                    }
                    break;
                }
                break;
            }
            case 4: {
                this.componentChildren(xsObject, n, qName, list);
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                    break;
                }
                break;
            }
            case 5: {
                this.componentChildren(xsObject, n, qName, list);
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                }
                if (-1 == n || 9 == n) {
                    this.addComponent(((XSAttributeGroupDefinition)xsObject).getAttributeWildcard(), qName, list);
                    break;
                }
                break;
            }
            case 6: {
                this.componentChildren(xsObject, n, qName, list);
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                    break;
                }
                break;
            }
            case 7: {
                this.componentChildren(xsObject, n, qName, list);
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                    break;
                }
                break;
            }
            case 8: {
                this.componentChildren(xsObject, n, qName, list);
                break;
            }
            case 9: {
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                    break;
                }
                break;
            }
            case 10: {
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                }
                if (-1 == n || 10 == n) {
                    this.addComponent(((XSIDCDefinition)xsObject).getRefKey(), qName, list);
                    break;
                }
                break;
            }
            case 11: {
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                    break;
                }
                break;
            }
            case 13: {
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                    break;
                }
                break;
            }
            case 14: {
                if (-1 == n || 12 == n) {
                    this.annotations(xsObject, list);
                    break;
                }
                break;
            }
        }
    }
    
    private void componentChildren(final XSObject xsObject, final short n, final QName qName, final List list) {
        Label_0635: {
            switch (xsObject.getType()) {
                case 1: {
                    if (-1 == n || 3 == n) {
                        this.addComponent(((XSAttributeDeclaration)xsObject).getTypeDefinition(), qName, list);
                        break;
                    }
                    break;
                }
                case 2: {
                    if (-1 == n || 3 == n) {
                        this.addComponent(((XSElementDeclaration)xsObject).getTypeDefinition(), qName, list);
                        break;
                    }
                    break;
                }
                case 3: {
                    if (((XSTypeDefinition)xsObject).getTypeCategory() == 16) {
                        if (-1 == n || 13 == n) {
                            final XSObjectList facets = ((XSSimpleTypeDefinition)xsObject).getFacets();
                            for (int i = 0; i < facets.size(); ++i) {
                                this.addComponent(facets.item(i), qName, list);
                            }
                            final XSObjectList multiValueFacets = ((XSSimpleTypeDefinition)xsObject).getMultiValueFacets();
                            for (int j = 0; j < multiValueFacets.size(); ++j) {
                                this.addComponent(multiValueFacets.item(j), qName, list);
                            }
                            break;
                        }
                        break;
                    }
                    else {
                        final XSComplexTypeDefinition xsComplexTypeDefinition = (XSComplexTypeDefinition)xsObject;
                        if (-1 == n || 4 == n) {
                            final XSObjectList attributeUses = xsComplexTypeDefinition.getAttributeUses();
                            for (int k = 0; k < attributeUses.size(); ++k) {
                                this.addComponent(attributeUses.item(k), qName, list);
                            }
                        }
                        switch (xsComplexTypeDefinition.getContentType()) {
                            case 0: {
                                break Label_0635;
                            }
                            case 1: {
                                if (-1 == n || 3 == n) {
                                    this.addComponent(xsComplexTypeDefinition.getSimpleType(), qName, list);
                                    break Label_0635;
                                }
                                break Label_0635;
                            }
                            default: {
                                this.term(xsComplexTypeDefinition, n, qName, list);
                                break Label_0635;
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    if (-1 == n || 1 == n) {
                        this.addComponent(((XSAttributeUse)xsObject).getAttrDeclaration(), qName, list);
                        break;
                    }
                    break;
                }
                case 5: {
                    if (-1 == n || 1 == n) {
                        final XSObjectList attributeUses2 = ((XSAttributeGroupDefinition)xsObject).getAttributeUses();
                        for (int l = 0; l < attributeUses2.size(); ++l) {
                            this.addComponent(((XSAttributeUse)attributeUses2.item(l)).getAttrDeclaration(), qName, list);
                        }
                        break;
                    }
                    break;
                }
                case 6: {
                    if (-1 == n || 7 == n) {
                        this.addComponent(((XSModelGroupDefinition)xsObject).getModelGroup(), qName, list);
                        break;
                    }
                    break;
                }
                case 7: {
                    final XSObjectList particles = ((XSModelGroup)xsObject).getParticles();
                    for (int n2 = 0; n2 < particles.size(); ++n2) {
                        final XSTerm term = ((XSParticle)particles.item(n2)).getTerm();
                        if (-1 == n || term.getType() == n) {
                            this.addComponent(term, qName, list);
                        }
                    }
                    break;
                }
                case 8: {
                    final XSTerm term2 = ((XSParticle)xsObject).getTerm();
                    if (-1 == n || term2.getType() == n) {
                        this.addComponent(term2, qName, list);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void annotations(final XSObject xsObject, final List list) throws SCDException {
        XSObjectList list2 = null;
        switch (xsObject.getType()) {
            case 1: {
                list2 = ((XSAttributeDeclaration)xsObject).getAnnotations();
                break;
            }
            case 2: {
                list2 = ((XSElementDeclaration)xsObject).getAnnotations();
                break;
            }
            case 3: {
                if (((XSTypeDefinition)xsObject).getTypeCategory() == 15) {
                    list2 = ((XSComplexTypeDefinition)xsObject).getAnnotations();
                    break;
                }
                list2 = ((XSSimpleTypeDefinition)xsObject).getAnnotations();
                break;
            }
            case 4: {
                list2 = ((XSAttributeUse)xsObject).getAnnotations();
                break;
            }
            case 5: {
                list2 = ((XSAttributeGroupDefinition)xsObject).getAnnotations();
                break;
            }
            case 7: {
                list2 = ((XSModelGroup)xsObject).getAnnotations();
                break;
            }
            case 6: {
                list2 = ((XSModelGroupDefinition)xsObject).getAnnotations();
                break;
            }
            case 8: {
                list2 = ((XSParticle)xsObject).getAnnotations();
                break;
            }
            case 9: {
                list2 = ((XSWildcard)xsObject).getAnnotations();
                break;
            }
            case 10: {
                list2 = ((XSIDCDefinition)xsObject).getAnnotations();
                break;
            }
            case 11: {
                list2 = ((XSNotationDeclaration)xsObject).getAnnotations();
                break;
            }
            case 13: {
                list2 = ((XSFacet)xsObject).getAnnotations();
                break;
            }
            case 14: {
                list2 = ((XSMultiValueFacet)xsObject).getAnnotations();
                break;
            }
            default: {
                throw new SCDException("Error in SCD: annotations accessor is not supported for the component type " + xsObject.getType());
            }
        }
        if (list2 != null) {
            for (int i = 0; i < list2.size(); ++i) {
                final XSObject item = list2.item(i);
                if (item != null && !list.contains(item)) {
                    list.add(item);
                }
            }
        }
    }
    
    private void componentScope(final XSObject xsObject, final List list) {
        switch (xsObject.getType()) {
            case 1: {
                if (((XSAttributeDeclaration)xsObject).getScope() == 1) {
                    break;
                }
                final XSComplexTypeDefinition enclosingCTDefinition = ((XSAttributeDeclaration)xsObject).getEnclosingCTDefinition();
                if (enclosingCTDefinition != null && !list.contains(enclosingCTDefinition)) {
                    list.add(enclosingCTDefinition);
                    break;
                }
                break;
            }
            case 2: {
                if (((XSElementDeclaration)xsObject).getScope() == 1) {
                    break;
                }
                final XSComplexTypeDefinition enclosingCTDefinition2 = ((XSElementDeclaration)xsObject).getEnclosingCTDefinition();
                if (enclosingCTDefinition2 != null && !list.contains(enclosingCTDefinition2)) {
                    list.add(enclosingCTDefinition2);
                    break;
                }
                break;
            }
        }
    }
    
    private void addComponent(final XSObject xsObject, final QName qName, final List list) {
        if (xsObject == null || list.contains(xsObject)) {
            return;
        }
        if (qName == SCDParser.ZERO) {
            if (xsObject.getType() == 3 && xsObject.getName() == null) {
                list.add(xsObject);
            }
        }
        else if (qName == SCDParser.WILDCARD) {
            list.add(xsObject);
        }
        else {
            final String name = xsObject.getName();
            final String namespace = xsObject.getNamespace();
            if (namespace != null && name != null) {
                if (namespace.equals(qName.uri) && name.equals(qName.localpart)) {
                    list.add(xsObject);
                }
            }
            else if (namespace == null && name != null) {
                if (qName.uri == null && name.equals(qName.localpart)) {
                    list.add(xsObject);
                }
            }
            else if (namespace == null && name == null) {
                final short type = xsObject.getType();
                if (type == 7 || type == 13 || type == 14) {
                    final String componentVariety = this.componentVariety(xsObject);
                    if (qName.uri == null && qName.localpart.equals(componentVariety)) {
                        list.add(xsObject);
                    }
                }
            }
        }
    }
    
    public String toString() {
        return "(current components=" + this.currentComponents.toString() + ", result=" + this.result.toString() + ")";
    }
}
