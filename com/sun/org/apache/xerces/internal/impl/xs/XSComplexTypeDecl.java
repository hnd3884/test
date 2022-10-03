package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.TypeInfo;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;

public class XSComplexTypeDecl implements XSComplexTypeDefinition, TypeInfo
{
    String fName;
    String fTargetNamespace;
    XSTypeDefinition fBaseType;
    short fDerivedBy;
    short fFinal;
    short fBlock;
    short fMiscFlags;
    XSAttributeGroupDecl fAttrGrp;
    short fContentType;
    XSSimpleType fXSSimpleType;
    XSParticleDecl fParticle;
    volatile XSCMValidator fCMValidator;
    XSCMValidator fUPACMValidator;
    XSObjectListImpl fAnnotations;
    private XSNamespaceItem fNamespaceItem;
    static final int DERIVATION_ANY = 0;
    static final int DERIVATION_RESTRICTION = 1;
    static final int DERIVATION_EXTENSION = 2;
    static final int DERIVATION_UNION = 4;
    static final int DERIVATION_LIST = 8;
    private static final short CT_IS_ABSTRACT = 1;
    private static final short CT_HAS_TYPE_ID = 2;
    private static final short CT_IS_ANONYMOUS = 4;
    
    public XSComplexTypeDecl() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fBaseType = null;
        this.fDerivedBy = 2;
        this.fFinal = 0;
        this.fBlock = 0;
        this.fMiscFlags = 0;
        this.fAttrGrp = null;
        this.fContentType = 0;
        this.fXSSimpleType = null;
        this.fParticle = null;
        this.fCMValidator = null;
        this.fUPACMValidator = null;
        this.fAnnotations = null;
        this.fNamespaceItem = null;
    }
    
    public void setValues(final String name, final String targetNamespace, final XSTypeDefinition baseType, final short derivedBy, final short schemaFinal, final short block, final short contentType, final boolean isAbstract, final XSAttributeGroupDecl attrGrp, final XSSimpleType simpleType, final XSParticleDecl particle, final XSObjectListImpl annotations) {
        this.fTargetNamespace = targetNamespace;
        this.fBaseType = baseType;
        this.fDerivedBy = derivedBy;
        this.fFinal = schemaFinal;
        this.fBlock = block;
        this.fContentType = contentType;
        if (isAbstract) {
            this.fMiscFlags |= 0x1;
        }
        this.fAttrGrp = attrGrp;
        this.fXSSimpleType = simpleType;
        this.fParticle = particle;
        this.fAnnotations = annotations;
    }
    
    public void setName(final String name) {
        this.fName = name;
    }
    
    @Override
    public short getTypeCategory() {
        return 15;
    }
    
    @Override
    public String getTypeName() {
        return this.fName;
    }
    
    public short getFinalSet() {
        return this.fFinal;
    }
    
    public String getTargetNamespace() {
        return this.fTargetNamespace;
    }
    
    public boolean containsTypeID() {
        return (this.fMiscFlags & 0x2) != 0x0;
    }
    
    public void setIsAbstractType() {
        this.fMiscFlags |= 0x1;
    }
    
    public void setContainsTypeID() {
        this.fMiscFlags |= 0x2;
    }
    
    public void setIsAnonymous() {
        this.fMiscFlags |= 0x4;
    }
    
    public XSCMValidator getContentModel(final CMBuilder cmBuilder) {
        if (this.fContentType == 1 || this.fContentType == 0) {
            return null;
        }
        if (this.fCMValidator == null) {
            synchronized (this) {
                if (this.fCMValidator == null) {
                    this.fCMValidator = cmBuilder.getContentModel(this);
                }
            }
        }
        return this.fCMValidator;
    }
    
    public XSAttributeGroupDecl getAttrGrp() {
        return this.fAttrGrp;
    }
    
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder(192);
        this.appendTypeInfo(str);
        return str.toString();
    }
    
    void appendTypeInfo(final StringBuilder str) {
        final String[] contentType = { "EMPTY", "SIMPLE", "ELEMENT", "MIXED" };
        final String[] derivedBy = { "EMPTY", "EXTENSION", "RESTRICTION" };
        str.append("Complex type name='").append(this.fTargetNamespace).append(',').append(this.getTypeName()).append("', ");
        if (this.fBaseType != null) {
            str.append(" base type name='").append(this.fBaseType.getName()).append("', ");
        }
        str.append(" content type='").append(contentType[this.fContentType]).append("', ");
        str.append(" isAbstract='").append(this.getAbstract()).append("', ");
        str.append(" hasTypeId='").append(this.containsTypeID()).append("', ");
        str.append(" final='").append(this.fFinal).append("', ");
        str.append(" block='").append(this.fBlock).append("', ");
        if (this.fParticle != null) {
            str.append(" particle='").append(this.fParticle.toString()).append("', ");
        }
        str.append(" derivedBy='").append(derivedBy[this.fDerivedBy]).append("'. ");
    }
    
    @Override
    public boolean derivedFromType(final XSTypeDefinition ancestor, final short derivationMethod) {
        if (ancestor == null) {
            return false;
        }
        if (ancestor == SchemaGrammar.fAnyType) {
            return true;
        }
        XSTypeDefinition type;
        for (type = this; type != ancestor && type != SchemaGrammar.fAnySimpleType && type != SchemaGrammar.fAnyType; type = type.getBaseType()) {}
        return type == ancestor;
    }
    
    @Override
    public boolean derivedFrom(final String ancestorNS, final String ancestorName, final short derivationMethod) {
        if (ancestorName == null) {
            return false;
        }
        if (ancestorNS != null && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType")) {
            return true;
        }
        XSTypeDefinition type;
        for (type = this; (!ancestorName.equals(type.getName()) || ((ancestorNS != null || type.getNamespace() != null) && (ancestorNS == null || !ancestorNS.equals(type.getNamespace())))) && type != SchemaGrammar.fAnySimpleType && type != SchemaGrammar.fAnyType; type = type.getBaseType()) {}
        return type != SchemaGrammar.fAnySimpleType && type != SchemaGrammar.fAnyType;
    }
    
    public boolean isDOMDerivedFrom(final String ancestorNS, String ancestorName, final int derivationMethod) {
        if (ancestorName == null) {
            return false;
        }
        if (ancestorNS != null && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType") && derivationMethod == 1 && derivationMethod == 2) {
            return true;
        }
        if ((derivationMethod & 0x1) != 0x0 && this.isDerivedByRestriction(ancestorNS, ancestorName, derivationMethod, this)) {
            return true;
        }
        if ((derivationMethod & 0x2) != 0x0 && this.isDerivedByExtension(ancestorNS, ancestorName, derivationMethod, this)) {
            return true;
        }
        if (((derivationMethod & 0x8) != 0x0 || (derivationMethod & 0x4) != 0x0) && (derivationMethod & 0x1) == 0x0 && (derivationMethod & 0x2) == 0x0) {
            if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType")) {
                ancestorName = "anySimpleType";
            }
            if (!this.fName.equals("anyType") || !this.fTargetNamespace.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
                if (this.fBaseType != null && this.fBaseType instanceof XSSimpleTypeDecl) {
                    return ((XSSimpleTypeDecl)this.fBaseType).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod);
                }
                if (this.fBaseType != null && this.fBaseType instanceof XSComplexTypeDecl) {
                    return ((XSComplexTypeDecl)this.fBaseType).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod);
                }
            }
        }
        return (derivationMethod & 0x2) == 0x0 && (derivationMethod & 0x1) == 0x0 && (derivationMethod & 0x8) == 0x0 && (derivationMethod & 0x4) == 0x0 && this.isDerivedByAny(ancestorNS, ancestorName, derivationMethod, this);
    }
    
    private boolean isDerivedByAny(final String ancestorNS, final String ancestorName, final int derivationMethod, XSTypeDefinition type) {
        XSTypeDefinition oldType = null;
        boolean derivedFrom = false;
        while (type != null && type != oldType) {
            if (ancestorName.equals(type.getName()) && ((ancestorNS == null && type.getNamespace() == null) || (ancestorNS != null && ancestorNS.equals(type.getNamespace())))) {
                derivedFrom = true;
                break;
            }
            if (this.isDerivedByRestriction(ancestorNS, ancestorName, derivationMethod, type)) {
                return true;
            }
            if (!this.isDerivedByExtension(ancestorNS, ancestorName, derivationMethod, type)) {
                return true;
            }
            oldType = type;
            type = type.getBaseType();
        }
        return derivedFrom;
    }
    
    private boolean isDerivedByRestriction(final String ancestorNS, String ancestorName, final int derivationMethod, XSTypeDefinition type) {
        for (XSTypeDefinition oldType = null; type != null && type != oldType; oldType = type, type = type.getBaseType()) {
            if (ancestorNS != null && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anySimpleType")) {
                return false;
            }
            if ((ancestorName.equals(type.getName()) && ancestorNS != null && ancestorNS.equals(type.getNamespace())) || (type.getNamespace() == null && ancestorNS == null)) {
                return true;
            }
            if (type instanceof XSSimpleTypeDecl) {
                if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType")) {
                    ancestorName = "anySimpleType";
                }
                return ((XSSimpleTypeDecl)type).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod);
            }
            if (((XSComplexTypeDecl)type).getDerivationMethod() != 2) {
                return false;
            }
        }
        return false;
    }
    
    private boolean isDerivedByExtension(final String ancestorNS, String ancestorName, final int derivationMethod, XSTypeDefinition type) {
        boolean extension = false;
        XSTypeDefinition oldType = null;
        while (type != null && type != oldType && (ancestorNS == null || !ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) || !ancestorName.equals("anySimpleType") || !SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(type.getNamespace()) || !"anyType".equals(type.getName()))) {
            if (ancestorName.equals(type.getName()) && ((ancestorNS == null && type.getNamespace() == null) || (ancestorNS != null && ancestorNS.equals(type.getNamespace())))) {
                return extension;
            }
            if (type instanceof XSSimpleTypeDecl) {
                if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType")) {
                    ancestorName = "anySimpleType";
                }
                if ((derivationMethod & 0x2) != 0x0) {
                    return extension & ((XSSimpleTypeDecl)type).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod & 0x1);
                }
                return extension & ((XSSimpleTypeDecl)type).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod);
            }
            else {
                if (((XSComplexTypeDecl)type).getDerivationMethod() == 1) {
                    extension |= true;
                }
                oldType = type;
                type = type.getBaseType();
            }
        }
        return false;
    }
    
    public void reset() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fBaseType = null;
        this.fDerivedBy = 2;
        this.fFinal = 0;
        this.fBlock = 0;
        this.fMiscFlags = 0;
        this.fAttrGrp.reset();
        this.fContentType = 0;
        this.fXSSimpleType = null;
        this.fParticle = null;
        this.fCMValidator = null;
        this.fUPACMValidator = null;
        if (this.fAnnotations != null) {
            this.fAnnotations.clearXSObjectList();
        }
        this.fAnnotations = null;
    }
    
    @Override
    public short getType() {
        return 3;
    }
    
    @Override
    public String getName() {
        return this.getAnonymous() ? null : this.fName;
    }
    
    @Override
    public boolean getAnonymous() {
        return (this.fMiscFlags & 0x4) != 0x0;
    }
    
    @Override
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    @Override
    public XSTypeDefinition getBaseType() {
        return this.fBaseType;
    }
    
    @Override
    public short getDerivationMethod() {
        return this.fDerivedBy;
    }
    
    @Override
    public boolean isFinal(final short derivation) {
        return (this.fFinal & derivation) != 0x0;
    }
    
    @Override
    public short getFinal() {
        return this.fFinal;
    }
    
    @Override
    public boolean getAbstract() {
        return (this.fMiscFlags & 0x1) != 0x0;
    }
    
    @Override
    public XSObjectList getAttributeUses() {
        return this.fAttrGrp.getAttributeUses();
    }
    
    @Override
    public XSWildcard getAttributeWildcard() {
        return this.fAttrGrp.getAttributeWildcard();
    }
    
    @Override
    public short getContentType() {
        return this.fContentType;
    }
    
    @Override
    public XSSimpleTypeDefinition getSimpleType() {
        return this.fXSSimpleType;
    }
    
    @Override
    public XSParticle getParticle() {
        return this.fParticle;
    }
    
    @Override
    public boolean isProhibitedSubstitution(final short prohibited) {
        return (this.fBlock & prohibited) != 0x0;
    }
    
    @Override
    public short getProhibitedSubstitutions() {
        return this.fBlock;
    }
    
    @Override
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }
    
    void setNamespaceItem(final XSNamespaceItem namespaceItem) {
        this.fNamespaceItem = namespaceItem;
    }
    
    public XSAttributeUse getAttributeUse(final String namespace, final String name) {
        return this.fAttrGrp.getAttributeUse(namespace, name);
    }
    
    @Override
    public String getTypeNamespace() {
        return this.getNamespace();
    }
    
    @Override
    public boolean isDerivedFrom(final String typeNamespaceArg, final String typeNameArg, final int derivationMethod) {
        return this.isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
    }
}
