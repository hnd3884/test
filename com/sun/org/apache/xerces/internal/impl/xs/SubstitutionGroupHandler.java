package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.Vector;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.HashMap;
import java.util.Map;

public class SubstitutionGroupHandler
{
    private static final XSElementDecl[] EMPTY_GROUP;
    XSGrammarBucket fGrammarBucket;
    Map<XSElementDecl, Object> fSubGroupsB;
    private static final OneSubGroup[] EMPTY_VECTOR;
    Map<XSElementDecl, XSElementDecl[]> fSubGroups;
    
    public SubstitutionGroupHandler(final XSGrammarBucket grammarBucket) {
        this.fSubGroupsB = new HashMap<XSElementDecl, Object>();
        this.fSubGroups = new HashMap<XSElementDecl, XSElementDecl[]>();
        this.fGrammarBucket = grammarBucket;
    }
    
    public XSElementDecl getMatchingElemDecl(final QName element, final XSElementDecl exemplar) {
        if (element.localpart == exemplar.fName && element.uri == exemplar.fTargetNamespace) {
            return exemplar;
        }
        if (exemplar.fScope != 1) {
            return null;
        }
        if ((exemplar.fBlock & 0x4) != 0x0) {
            return null;
        }
        final SchemaGrammar sGrammar = this.fGrammarBucket.getGrammar(element.uri);
        if (sGrammar == null) {
            return null;
        }
        final XSElementDecl eDecl = sGrammar.getGlobalElementDecl(element.localpart);
        if (eDecl == null) {
            return null;
        }
        if (this.substitutionGroupOK(eDecl, exemplar, exemplar.fBlock)) {
            return eDecl;
        }
        return null;
    }
    
    protected boolean substitutionGroupOK(final XSElementDecl element, final XSElementDecl exemplar, final short blockingConstraint) {
        if (element == exemplar) {
            return true;
        }
        if ((blockingConstraint & 0x4) != 0x0) {
            return false;
        }
        XSElementDecl subGroup;
        for (subGroup = element.fSubGroup; subGroup != null && subGroup != exemplar; subGroup = subGroup.fSubGroup) {}
        return subGroup != null && this.typeDerivationOK(element.fType, exemplar.fType, blockingConstraint);
    }
    
    private boolean typeDerivationOK(final XSTypeDefinition derived, final XSTypeDefinition base, final short blockingConstraint) {
        short devMethod = 0;
        short blockConstraint = blockingConstraint;
        XSTypeDefinition type = derived;
        while (type != base && type != SchemaGrammar.fAnyType) {
            if (type.getTypeCategory() == 15) {
                devMethod |= ((XSComplexTypeDecl)type).fDerivedBy;
            }
            else {
                devMethod |= 0x2;
            }
            type = type.getBaseType();
            if (type == null) {
                type = SchemaGrammar.fAnyType;
            }
            if (type.getTypeCategory() == 15) {
                blockConstraint |= ((XSComplexTypeDecl)type).fBlock;
            }
        }
        if (type != base) {
            if (base.getTypeCategory() == 16) {
                final XSSimpleTypeDefinition st = (XSSimpleTypeDefinition)base;
                if (st.getVariety() == 3) {
                    final XSObjectList memberTypes = st.getMemberTypes();
                    for (int length = memberTypes.getLength(), i = 0; i < length; ++i) {
                        if (this.typeDerivationOK(derived, (XSTypeDefinition)memberTypes.item(i), blockingConstraint)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return (devMethod & blockConstraint) == 0x0;
    }
    
    public boolean inSubstitutionGroup(final XSElementDecl element, final XSElementDecl exemplar) {
        return this.substitutionGroupOK(element, exemplar, exemplar.fBlock);
    }
    
    public void reset() {
        this.fSubGroupsB.clear();
        this.fSubGroups.clear();
    }
    
    public void addSubstitutionGroup(final XSElementDecl[] elements) {
        for (int i = elements.length - 1; i >= 0; --i) {
            final XSElementDecl element = elements[i];
            final XSElementDecl subHead = element.fSubGroup;
            Vector subGroup = this.fSubGroupsB.get(subHead);
            if (subGroup == null) {
                subGroup = new Vector();
                this.fSubGroupsB.put(subHead, subGroup);
            }
            subGroup.addElement(element);
        }
    }
    
    public XSElementDecl[] getSubstitutionGroup(final XSElementDecl element) {
        final XSElementDecl[] subGroup = this.fSubGroups.get(element);
        if (subGroup != null) {
            return subGroup;
        }
        if ((element.fBlock & 0x4) != 0x0) {
            this.fSubGroups.put(element, SubstitutionGroupHandler.EMPTY_GROUP);
            return SubstitutionGroupHandler.EMPTY_GROUP;
        }
        final OneSubGroup[] groupB = this.getSubGroupB(element, new OneSubGroup());
        final int len = groupB.length;
        int rlen = 0;
        XSElementDecl[] ret = new XSElementDecl[len];
        for (int i = 0; i < len; ++i) {
            if ((element.fBlock & groupB[i].dMethod) == 0x0) {
                ret[rlen++] = groupB[i].sub;
            }
        }
        if (rlen < len) {
            final XSElementDecl[] ret2 = new XSElementDecl[rlen];
            System.arraycopy(ret, 0, ret2, 0, rlen);
            ret = ret2;
        }
        this.fSubGroups.put(element, ret);
        return ret;
    }
    
    private OneSubGroup[] getSubGroupB(final XSElementDecl element, final OneSubGroup methods) {
        final Object subGroup = this.fSubGroupsB.get(element);
        if (subGroup == null) {
            this.fSubGroupsB.put(element, SubstitutionGroupHandler.EMPTY_VECTOR);
            return SubstitutionGroupHandler.EMPTY_VECTOR;
        }
        if (subGroup instanceof OneSubGroup[]) {
            return (OneSubGroup[])subGroup;
        }
        final Vector group = (Vector)subGroup;
        final Vector newGroup = new Vector();
        for (int i = group.size() - 1; i >= 0; --i) {
            final XSElementDecl sub = group.elementAt(i);
            if (this.getDBMethods(sub.fType, element.fType, methods)) {
                final short dMethod = methods.dMethod;
                final short bMethod = methods.bMethod;
                newGroup.addElement(new OneSubGroup(sub, methods.dMethod, methods.bMethod));
                final OneSubGroup[] group2 = this.getSubGroupB(sub, methods);
                for (int j = group2.length - 1; j >= 0; --j) {
                    final short dSubMethod = (short)(dMethod | group2[j].dMethod);
                    final short bSubMethod = (short)(bMethod | group2[j].bMethod);
                    if ((dSubMethod & bSubMethod) == 0x0) {
                        newGroup.addElement(new OneSubGroup(group2[j].sub, dSubMethod, bSubMethod));
                    }
                }
            }
        }
        final OneSubGroup[] ret = new OneSubGroup[newGroup.size()];
        for (int k = newGroup.size() - 1; k >= 0; --k) {
            ret[k] = newGroup.elementAt(k);
        }
        this.fSubGroupsB.put(element, ret);
        return ret;
    }
    
    private boolean getDBMethods(XSTypeDefinition typed, final XSTypeDefinition typeb, final OneSubGroup methods) {
        short dMethod = 0;
        short bMethod = 0;
        while (typed != typeb && typed != SchemaGrammar.fAnyType) {
            if (typed.getTypeCategory() == 15) {
                dMethod |= ((XSComplexTypeDecl)typed).fDerivedBy;
            }
            else {
                dMethod |= 0x2;
            }
            typed = typed.getBaseType();
            if (typed == null) {
                typed = SchemaGrammar.fAnyType;
            }
            if (typed.getTypeCategory() == 15) {
                bMethod |= ((XSComplexTypeDecl)typed).fBlock;
            }
        }
        if (typed != typeb || (dMethod & bMethod) != 0x0) {
            return false;
        }
        methods.dMethod = dMethod;
        methods.bMethod = bMethod;
        return true;
    }
    
    static {
        EMPTY_GROUP = new XSElementDecl[0];
        EMPTY_VECTOR = new OneSubGroup[0];
    }
    
    private static final class OneSubGroup
    {
        XSElementDecl sub;
        short dMethod;
        short bMethod;
        
        OneSubGroup() {
        }
        
        OneSubGroup(final XSElementDecl sub, final short dMethod, final short bMethod) {
            this.sub = sub;
            this.dMethod = dMethod;
            this.bMethod = bMethod;
        }
    }
}
