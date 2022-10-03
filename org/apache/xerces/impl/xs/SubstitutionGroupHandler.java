package org.apache.xerces.impl.xs;

import java.util.Vector;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xni.QName;
import java.util.Hashtable;

public class SubstitutionGroupHandler
{
    private static final XSElementDecl[] EMPTY_GROUP;
    private final XSElementDeclHelper fXSElementDeclHelper;
    Hashtable fSubGroupsB;
    private static final OneSubGroup[] EMPTY_VECTOR;
    Hashtable fSubGroups;
    
    public SubstitutionGroupHandler(final XSElementDeclHelper fxsElementDeclHelper) {
        this.fSubGroupsB = new Hashtable();
        this.fSubGroups = new Hashtable();
        this.fXSElementDeclHelper = fxsElementDeclHelper;
    }
    
    public XSElementDecl getMatchingElemDecl(final QName qName, final XSElementDecl xsElementDecl, final short n) {
        if (qName.localpart == xsElementDecl.fName && qName.uri == xsElementDecl.fTargetNamespace) {
            return xsElementDecl;
        }
        if (xsElementDecl.fScope != 1) {
            return null;
        }
        if ((xsElementDecl.fBlock & 0x4) != 0x0) {
            return null;
        }
        final XSElementDecl globalElementDecl = this.fXSElementDeclHelper.getGlobalElementDecl(qName);
        if (globalElementDecl == null) {
            return null;
        }
        if (this.substitutionGroupOK(globalElementDecl, xsElementDecl, xsElementDecl.fBlock, n)) {
            return globalElementDecl;
        }
        return null;
    }
    
    protected boolean substitutionGroupOK(final XSElementDecl xsElementDecl, final XSElementDecl xsElementDecl2, final short n, final short n2) {
        if (xsElementDecl == xsElementDecl2) {
            return true;
        }
        if ((n & 0x4) != 0x0) {
            return false;
        }
        final XSElementDecl[] fSubGroup = xsElementDecl.fSubGroup;
        return fSubGroup != null && this.checkSubstitutionGroupAffil(fSubGroup, xsElementDecl2) && this.typeDerivationOK(xsElementDecl.fType, xsElementDecl2.fType, n, n2);
    }
    
    private boolean checkSubstitutionGroupAffil(final XSElementDecl[] array, final XSElementDecl xsElementDecl) {
        for (int i = 0; i < array.length; ++i) {
            final XSElementDecl xsElementDecl2 = array[i];
            if (xsElementDecl2 == xsElementDecl) {
                return true;
            }
            if (xsElementDecl2.fSubGroup != null && this.checkSubstitutionGroupAffil(xsElementDecl2.fSubGroup, xsElementDecl)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean typeDerivationOK(final XSTypeDefinition xsTypeDefinition, final XSTypeDefinition xsTypeDefinition2, final short n, final short n2) {
        short n3 = 0;
        short n4 = n;
        XSTypeDefinition xsTypeDefinition3 = xsTypeDefinition;
        while (xsTypeDefinition3 != xsTypeDefinition2 && !SchemaGrammar.isAnyType(xsTypeDefinition3)) {
            if (xsTypeDefinition3.getTypeCategory() == 15) {
                n3 |= ((XSComplexTypeDecl)xsTypeDefinition3).fDerivedBy;
            }
            else {
                n3 |= 0x2;
            }
            xsTypeDefinition3 = xsTypeDefinition3.getBaseType();
            if (xsTypeDefinition3 == null) {
                xsTypeDefinition3 = SchemaGrammar.getXSAnyType(n2);
            }
            if (xsTypeDefinition3.getTypeCategory() == 15) {
                n4 |= ((XSComplexTypeDecl)xsTypeDefinition3).fBlock;
            }
        }
        if (xsTypeDefinition3 != xsTypeDefinition2) {
            if (xsTypeDefinition2.getTypeCategory() == 16) {
                final XSSimpleTypeDefinition xsSimpleTypeDefinition = (XSSimpleTypeDefinition)xsTypeDefinition2;
                if (xsSimpleTypeDefinition.getVariety() == 3) {
                    final XSObjectList memberTypes = xsSimpleTypeDefinition.getMemberTypes();
                    for (int length = memberTypes.getLength(), i = 0; i < length; ++i) {
                        if (this.typeDerivationOK(xsTypeDefinition, (XSTypeDefinition)memberTypes.item(i), n, n2)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return (n3 & n4) == 0x0;
    }
    
    public boolean inSubstitutionGroup(final XSElementDecl xsElementDecl, final XSElementDecl xsElementDecl2, final short n) {
        return this.substitutionGroupOK(xsElementDecl, xsElementDecl2, xsElementDecl2.fBlock, n);
    }
    
    public void reset() {
        this.fSubGroupsB.clear();
        this.fSubGroups.clear();
    }
    
    public void addSubstitutionGroup(final XSElementDecl[] array) {
        for (int i = array.length - 1; i >= 0; --i) {
            final XSElementDecl xsElementDecl = array[i];
            final XSElementDecl[] fSubGroup = xsElementDecl.fSubGroup;
            for (int j = 0; j < fSubGroup.length; ++j) {
                final XSElementDecl xsElementDecl2 = fSubGroup[j];
                Vector vector = this.fSubGroupsB.get(xsElementDecl2);
                if (vector == null) {
                    vector = new Vector();
                    this.fSubGroupsB.put(xsElementDecl2, vector);
                }
                vector.addElement(xsElementDecl);
            }
        }
    }
    
    public XSElementDecl[] getSubstitutionGroup(final XSElementDecl xsElementDecl, final short n) {
        final XSElementDecl[] value = this.fSubGroups.get(xsElementDecl);
        if (value != null) {
            return value;
        }
        if ((xsElementDecl.fBlock & 0x4) != 0x0) {
            this.fSubGroups.put(xsElementDecl, SubstitutionGroupHandler.EMPTY_GROUP);
            return SubstitutionGroupHandler.EMPTY_GROUP;
        }
        final OneSubGroup[] subGroupB = this.getSubGroupB(xsElementDecl, new OneSubGroup(), n);
        final int length = subGroupB.length;
        int n2 = 0;
        XSElementDecl[] array = new XSElementDecl[length];
        for (int i = 0; i < length; ++i) {
            if ((xsElementDecl.fBlock & subGroupB[i].dMethod) == 0x0) {
                array[n2++] = subGroupB[i].sub;
            }
        }
        if (n2 < length) {
            final XSElementDecl[] array2 = new XSElementDecl[n2];
            System.arraycopy(array, 0, array2, 0, n2);
            array = array2;
        }
        this.fSubGroups.put(xsElementDecl, array);
        return array;
    }
    
    private OneSubGroup[] getSubGroupB(final XSElementDecl xsElementDecl, final OneSubGroup oneSubGroup, final short n) {
        final Vector value = this.fSubGroupsB.get(xsElementDecl);
        if (value == null) {
            this.fSubGroupsB.put(xsElementDecl, SubstitutionGroupHandler.EMPTY_VECTOR);
            return SubstitutionGroupHandler.EMPTY_VECTOR;
        }
        if (value instanceof OneSubGroup[]) {
            return (OneSubGroup[])(Object)value;
        }
        final Vector vector = value;
        final Vector vector2 = new Vector();
        for (int i = vector.size() - 1; i >= 0; --i) {
            final XSElementDecl xsElementDecl2 = (XSElementDecl)vector.elementAt(i);
            if (this.getDBMethods(xsElementDecl2.fType, xsElementDecl.fType, oneSubGroup, n)) {
                final short dMethod = oneSubGroup.dMethod;
                final short bMethod = oneSubGroup.bMethod;
                vector2.addElement(new OneSubGroup(xsElementDecl2, oneSubGroup.dMethod, oneSubGroup.bMethod));
                final OneSubGroup[] subGroupB = this.getSubGroupB(xsElementDecl2, oneSubGroup, n);
                for (int j = subGroupB.length - 1; j >= 0; --j) {
                    final short n2 = (short)(dMethod | subGroupB[j].dMethod);
                    final short n3 = (short)(bMethod | subGroupB[j].bMethod);
                    if ((n2 & n3) == 0x0) {
                        vector2.addElement(new OneSubGroup(subGroupB[j].sub, n2, n3));
                    }
                }
            }
        }
        final OneSubGroup[] array = new OneSubGroup[vector2.size()];
        for (int k = vector2.size() - 1; k >= 0; --k) {
            array[k] = (OneSubGroup)vector2.elementAt(k);
        }
        this.fSubGroupsB.put(xsElementDecl, array);
        return array;
    }
    
    private boolean getDBMethods(XSTypeDefinition xsTypeDefinition, final XSTypeDefinition xsTypeDefinition2, final OneSubGroup oneSubGroup, final short n) {
        short dMethod = 0;
        short bMethod = 0;
        while (xsTypeDefinition != xsTypeDefinition2 && !SchemaGrammar.isAnyType(xsTypeDefinition)) {
            if (xsTypeDefinition.getTypeCategory() == 15) {
                dMethod |= ((XSComplexTypeDecl)xsTypeDefinition).fDerivedBy;
            }
            else {
                dMethod |= 0x2;
            }
            xsTypeDefinition = xsTypeDefinition.getBaseType();
            if (xsTypeDefinition == null) {
                xsTypeDefinition = SchemaGrammar.getXSAnyType(n);
            }
            if (xsTypeDefinition.getTypeCategory() == 15) {
                bMethod |= ((XSComplexTypeDecl)xsTypeDefinition).fBlock;
            }
        }
        if (xsTypeDefinition != xsTypeDefinition2 || (dMethod & bMethod) != 0x0) {
            return false;
        }
        oneSubGroup.dMethod = dMethod;
        oneSubGroup.bMethod = bMethod;
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
