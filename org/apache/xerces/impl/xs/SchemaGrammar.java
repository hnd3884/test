package org.apache.xerces.impl.xs;

import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSIDCDefinition;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.impl.xs.util.XSNamedMap4Types;
import org.apache.xerces.impl.xs.util.XSNamedMapImpl;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.impl.xs.alternative.XSTypeAlternativeImpl;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import java.util.Vector;
import java.lang.ref.SoftReference;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xni.grammars.XSGrammar;

public class SchemaGrammar implements XSGrammar, XSNamespaceItem
{
    String fTargetNamespace;
    SymbolHash fGlobalAttrDecls;
    SymbolHash fGlobalAttrGrpDecls;
    SymbolHash fGlobalElemDecls;
    SymbolHash fGlobalGroupDecls;
    SymbolHash fGlobalNotationDecls;
    SymbolHash fGlobalIDConstraintDecls;
    SymbolHash fGlobalTypeDecls;
    SymbolHash fGlobalAttrDeclsExt;
    SymbolHash fGlobalAttrGrpDeclsExt;
    SymbolHash fGlobalElemDeclsExt;
    SymbolHash fGlobalGroupDeclsExt;
    SymbolHash fGlobalNotationDeclsExt;
    SymbolHash fGlobalIDConstraintDeclsExt;
    SymbolHash fGlobalTypeDeclsExt;
    SymbolHash fAllGlobalElemDecls;
    XSDDescription fGrammarDescription;
    XSAnnotationImpl[] fAnnotations;
    int fNumAnnotations;
    private SymbolTable fSymbolTable;
    private SoftReference fSAXParser;
    private SoftReference fDOMParser;
    private boolean fIsImmutable;
    private static final int BASICSET_COUNT = 29;
    private static final int FULLSET_COUNT = 46;
    private static final int GRAMMAR_XS = 1;
    private static final int GRAMMAR_XSI = 2;
    Vector fImported;
    private static final int INITIAL_SIZE = 16;
    private static final int INC_SIZE = 16;
    private int fCTCount;
    private XSComplexTypeDecl[] fComplexTypeDecls;
    private SimpleLocator[] fCTLocators;
    private static final int REDEFINED_GROUP_INIT_SIZE = 2;
    private int fRGCount;
    private XSGroupDecl[] fRedefinedGroupDecls;
    private SimpleLocator[] fRGLocators;
    boolean fFullChecked;
    private int fSubGroupCount;
    private XSElementDecl[] fSubGroups;
    private static final XSComplexTypeDecl fAnyType;
    private static final XSComplexTypeDecl fAnyTypeExtended;
    private static final XSComplexTypeDecl fAnyType11;
    private static final BuiltinSchemaGrammar SG_SchemaNS;
    private static final BuiltinSchemaGrammar SG_SchemaNSExtended;
    private static final BuiltinSchemaGrammar SG_Schema11NS;
    public static final XSSimpleType fAnySimpleType;
    public static final XSSimpleType fAnyAtomicType;
    public static final BuiltinSchemaGrammar SG_XSI;
    private static final BuiltinSchemaGrammar SG_XSI11;
    private static final short MAX_COMP_IDX = 16;
    private static final boolean[] GLOBAL_COMP;
    private XSNamedMap[] fComponents;
    private ObjectList[] fComponentsExt;
    private Vector fDocuments;
    private Vector fLocations;
    
    protected SchemaGrammar() {
        this.fGrammarDescription = null;
        this.fAnnotations = null;
        this.fSymbolTable = null;
        this.fSAXParser = null;
        this.fDOMParser = null;
        this.fIsImmutable = false;
        this.fImported = null;
        this.fCTCount = 0;
        this.fComplexTypeDecls = new XSComplexTypeDecl[16];
        this.fCTLocators = new SimpleLocator[16];
        this.fRGCount = 0;
        this.fRedefinedGroupDecls = new XSGroupDecl[2];
        this.fRGLocators = new SimpleLocator[1];
        this.fFullChecked = false;
        this.fSubGroupCount = 0;
        this.fSubGroups = new XSElementDecl[16];
        this.fComponents = null;
        this.fComponentsExt = null;
        this.fDocuments = null;
        this.fLocations = null;
    }
    
    public SchemaGrammar(final String s, final XSDDescription xsdDescription, final SymbolTable symbolTable) {
        this(s, xsdDescription, symbolTable, (short)1);
    }
    
    public SchemaGrammar(final String fTargetNamespace, final XSDDescription fGrammarDescription, final SymbolTable fSymbolTable, final short n) {
        this.fGrammarDescription = null;
        this.fAnnotations = null;
        this.fSymbolTable = null;
        this.fSAXParser = null;
        this.fDOMParser = null;
        this.fIsImmutable = false;
        this.fImported = null;
        this.fCTCount = 0;
        this.fComplexTypeDecls = new XSComplexTypeDecl[16];
        this.fCTLocators = new SimpleLocator[16];
        this.fRGCount = 0;
        this.fRedefinedGroupDecls = new XSGroupDecl[2];
        this.fRGLocators = new SimpleLocator[1];
        this.fFullChecked = false;
        this.fSubGroupCount = 0;
        this.fSubGroups = new XSElementDecl[16];
        this.fComponents = null;
        this.fComponentsExt = null;
        this.fDocuments = null;
        this.fLocations = null;
        this.fTargetNamespace = fTargetNamespace;
        this.fGrammarDescription = fGrammarDescription;
        this.fSymbolTable = fSymbolTable;
        this.fGlobalAttrDecls = new SymbolHash(12);
        this.fGlobalAttrGrpDecls = new SymbolHash(5);
        this.fGlobalElemDecls = new SymbolHash(25);
        this.fGlobalGroupDecls = new SymbolHash(5);
        this.fGlobalNotationDecls = new SymbolHash(1);
        this.fGlobalIDConstraintDecls = new SymbolHash(3);
        this.fGlobalAttrDeclsExt = new SymbolHash(12);
        this.fGlobalAttrGrpDeclsExt = new SymbolHash(5);
        this.fGlobalElemDeclsExt = new SymbolHash(25);
        this.fGlobalGroupDeclsExt = new SymbolHash(5);
        this.fGlobalNotationDeclsExt = new SymbolHash(1);
        this.fGlobalIDConstraintDeclsExt = new SymbolHash(3);
        this.fGlobalTypeDeclsExt = new SymbolHash(25);
        this.fAllGlobalElemDecls = new SymbolHash(25);
        if (this.fTargetNamespace == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            this.fGlobalTypeDecls = getS4SGrammar(n).fGlobalTypeDecls.makeClone();
        }
        else {
            this.fGlobalTypeDecls = new SymbolHash(25);
        }
    }
    
    public SchemaGrammar(final SchemaGrammar schemaGrammar) {
        this.fGrammarDescription = null;
        this.fAnnotations = null;
        this.fSymbolTable = null;
        this.fSAXParser = null;
        this.fDOMParser = null;
        this.fIsImmutable = false;
        this.fImported = null;
        this.fCTCount = 0;
        this.fComplexTypeDecls = new XSComplexTypeDecl[16];
        this.fCTLocators = new SimpleLocator[16];
        this.fRGCount = 0;
        this.fRedefinedGroupDecls = new XSGroupDecl[2];
        this.fRGLocators = new SimpleLocator[1];
        this.fFullChecked = false;
        this.fSubGroupCount = 0;
        this.fSubGroups = new XSElementDecl[16];
        this.fComponents = null;
        this.fComponentsExt = null;
        this.fDocuments = null;
        this.fLocations = null;
        this.fTargetNamespace = schemaGrammar.fTargetNamespace;
        this.fGrammarDescription = schemaGrammar.fGrammarDescription.makeClone();
        this.fSymbolTable = schemaGrammar.fSymbolTable;
        this.fGlobalAttrDecls = schemaGrammar.fGlobalAttrDecls.makeClone();
        this.fGlobalAttrGrpDecls = schemaGrammar.fGlobalAttrGrpDecls.makeClone();
        this.fGlobalElemDecls = schemaGrammar.fGlobalElemDecls.makeClone();
        this.fGlobalGroupDecls = schemaGrammar.fGlobalGroupDecls.makeClone();
        this.fGlobalNotationDecls = schemaGrammar.fGlobalNotationDecls.makeClone();
        this.fGlobalIDConstraintDecls = schemaGrammar.fGlobalIDConstraintDecls.makeClone();
        this.fGlobalTypeDecls = schemaGrammar.fGlobalTypeDecls.makeClone();
        this.fGlobalAttrDeclsExt = schemaGrammar.fGlobalAttrDeclsExt.makeClone();
        this.fGlobalAttrGrpDeclsExt = schemaGrammar.fGlobalAttrGrpDeclsExt.makeClone();
        this.fGlobalElemDeclsExt = schemaGrammar.fGlobalElemDeclsExt.makeClone();
        this.fGlobalGroupDeclsExt = schemaGrammar.fGlobalGroupDeclsExt.makeClone();
        this.fGlobalNotationDeclsExt = schemaGrammar.fGlobalNotationDeclsExt.makeClone();
        this.fGlobalIDConstraintDeclsExt = schemaGrammar.fGlobalIDConstraintDeclsExt.makeClone();
        this.fGlobalTypeDeclsExt = schemaGrammar.fGlobalTypeDeclsExt.makeClone();
        this.fAllGlobalElemDecls = schemaGrammar.fAllGlobalElemDecls.makeClone();
        this.fNumAnnotations = schemaGrammar.fNumAnnotations;
        if (this.fNumAnnotations > 0) {
            this.fAnnotations = new XSAnnotationImpl[schemaGrammar.fAnnotations.length];
            System.arraycopy(schemaGrammar.fAnnotations, 0, this.fAnnotations, 0, this.fNumAnnotations);
        }
        this.fSubGroupCount = schemaGrammar.fSubGroupCount;
        if (this.fSubGroupCount > 0) {
            this.fSubGroups = new XSElementDecl[schemaGrammar.fSubGroups.length];
            System.arraycopy(schemaGrammar.fSubGroups, 0, this.fSubGroups, 0, this.fSubGroupCount);
        }
        this.fCTCount = schemaGrammar.fCTCount;
        if (this.fCTCount > 0) {
            this.fComplexTypeDecls = new XSComplexTypeDecl[schemaGrammar.fComplexTypeDecls.length];
            this.fCTLocators = new SimpleLocator[schemaGrammar.fCTLocators.length];
            System.arraycopy(schemaGrammar.fComplexTypeDecls, 0, this.fComplexTypeDecls, 0, this.fCTCount);
            System.arraycopy(schemaGrammar.fCTLocators, 0, this.fCTLocators, 0, this.fCTCount);
        }
        this.fRGCount = schemaGrammar.fRGCount;
        if (this.fRGCount > 0) {
            this.fRedefinedGroupDecls = new XSGroupDecl[schemaGrammar.fRedefinedGroupDecls.length];
            this.fRGLocators = new SimpleLocator[schemaGrammar.fRGLocators.length];
            System.arraycopy(schemaGrammar.fRedefinedGroupDecls, 0, this.fRedefinedGroupDecls, 0, this.fRGCount);
            System.arraycopy(schemaGrammar.fRGLocators, 0, this.fRGLocators, 0, this.fRGCount / 2);
        }
        if (schemaGrammar.fImported != null) {
            this.fImported = new Vector();
            for (int i = 0; i < schemaGrammar.fImported.size(); ++i) {
                this.fImported.add(schemaGrammar.fImported.elementAt(i));
            }
        }
        if (schemaGrammar.fLocations != null) {
            for (int j = 0; j < schemaGrammar.fLocations.size(); ++j) {
                this.addDocument(null, (String)schemaGrammar.fLocations.elementAt(j));
            }
        }
    }
    
    public XMLGrammarDescription getGrammarDescription() {
        return this.fGrammarDescription;
    }
    
    public boolean isNamespaceAware() {
        return true;
    }
    
    public void setImportedGrammars(final Vector fImported) {
        this.fImported = fImported;
    }
    
    public Vector getImportedGrammars() {
        return this.fImported;
    }
    
    public final String getTargetNamespace() {
        return this.fTargetNamespace;
    }
    
    public void addGlobalAttributeDecl(final XSAttributeDecl xsAttributeDecl) {
        this.fGlobalAttrDecls.put(xsAttributeDecl.fName, xsAttributeDecl);
        xsAttributeDecl.setNamespaceItem(this);
    }
    
    public void addGlobalAttributeDecl(final XSAttributeDecl xsAttributeDecl, final String s) {
        this.fGlobalAttrDeclsExt.put(((s != null) ? s : "") + "," + xsAttributeDecl.fName, xsAttributeDecl);
        if (xsAttributeDecl.getNamespaceItem() == null) {
            xsAttributeDecl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl xsAttributeGroupDecl) {
        this.fGlobalAttrGrpDecls.put(xsAttributeGroupDecl.fName, xsAttributeGroupDecl);
        xsAttributeGroupDecl.setNamespaceItem(this);
    }
    
    public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl xsAttributeGroupDecl, final String s) {
        this.fGlobalAttrGrpDeclsExt.put(((s != null) ? s : "") + "," + xsAttributeGroupDecl.fName, xsAttributeGroupDecl);
        if (xsAttributeGroupDecl.getNamespaceItem() == null) {
            xsAttributeGroupDecl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalElementDeclAll(final XSElementDecl xsElementDecl) {
        if (this.fAllGlobalElemDecls.get(xsElementDecl) == null) {
            this.fAllGlobalElemDecls.put(xsElementDecl, xsElementDecl);
            if (xsElementDecl.fSubGroup != null) {
                if (this.fSubGroupCount == this.fSubGroups.length) {
                    this.fSubGroups = resize(this.fSubGroups, this.fSubGroupCount + 16);
                }
                this.fSubGroups[this.fSubGroupCount++] = xsElementDecl;
            }
        }
    }
    
    public void addGlobalElementDecl(final XSElementDecl xsElementDecl) {
        this.fGlobalElemDecls.put(xsElementDecl.fName, xsElementDecl);
        xsElementDecl.setNamespaceItem(this);
    }
    
    public void addGlobalElementDecl(final XSElementDecl xsElementDecl, final String s) {
        this.fGlobalElemDeclsExt.put(((s != null) ? s : "") + "," + xsElementDecl.fName, xsElementDecl);
        if (xsElementDecl.getNamespaceItem() == null) {
            xsElementDecl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalGroupDecl(final XSGroupDecl xsGroupDecl) {
        this.fGlobalGroupDecls.put(xsGroupDecl.fName, xsGroupDecl);
        xsGroupDecl.setNamespaceItem(this);
    }
    
    public void addGlobalGroupDecl(final XSGroupDecl xsGroupDecl, final String s) {
        this.fGlobalGroupDeclsExt.put(((s != null) ? s : "") + "," + xsGroupDecl.fName, xsGroupDecl);
        if (xsGroupDecl.getNamespaceItem() == null) {
            xsGroupDecl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalNotationDecl(final XSNotationDecl xsNotationDecl) {
        this.fGlobalNotationDecls.put(xsNotationDecl.fName, xsNotationDecl);
        xsNotationDecl.setNamespaceItem(this);
    }
    
    public void addGlobalNotationDecl(final XSNotationDecl xsNotationDecl, final String s) {
        this.fGlobalNotationDeclsExt.put(((s != null) ? s : "") + "," + xsNotationDecl.fName, xsNotationDecl);
        if (xsNotationDecl.getNamespaceItem() == null) {
            xsNotationDecl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalTypeDecl(final XSTypeDefinition xsTypeDefinition) {
        this.fGlobalTypeDecls.put(xsTypeDefinition.getName(), xsTypeDefinition);
        if (xsTypeDefinition instanceof XSComplexTypeDecl) {
            ((XSComplexTypeDecl)xsTypeDefinition).setNamespaceItem(this);
        }
        else if (xsTypeDefinition instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)xsTypeDefinition).setNamespaceItem(this);
        }
    }
    
    public void addGlobalTypeDecl(final XSTypeDefinition xsTypeDefinition, final String s) {
        this.fGlobalTypeDeclsExt.put(((s != null) ? s : "") + "," + xsTypeDefinition.getName(), xsTypeDefinition);
        if (xsTypeDefinition.getNamespaceItem() == null) {
            if (xsTypeDefinition instanceof XSComplexTypeDecl) {
                ((XSComplexTypeDecl)xsTypeDefinition).setNamespaceItem(this);
            }
            else if (xsTypeDefinition instanceof XSSimpleTypeDecl) {
                ((XSSimpleTypeDecl)xsTypeDefinition).setNamespaceItem(this);
            }
        }
    }
    
    public void addGlobalComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl) {
        this.fGlobalTypeDecls.put(xsComplexTypeDecl.getName(), xsComplexTypeDecl);
        xsComplexTypeDecl.setNamespaceItem(this);
    }
    
    public void addGlobalComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl, final String s) {
        this.fGlobalTypeDeclsExt.put(((s != null) ? s : "") + "," + xsComplexTypeDecl.getName(), xsComplexTypeDecl);
        if (xsComplexTypeDecl.getNamespaceItem() == null) {
            xsComplexTypeDecl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalSimpleTypeDecl(final XSSimpleType xsSimpleType) {
        this.fGlobalTypeDecls.put(xsSimpleType.getName(), xsSimpleType);
        if (xsSimpleType instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)xsSimpleType).setNamespaceItem(this);
        }
    }
    
    public void addGlobalSimpleTypeDecl(final XSSimpleType xsSimpleType, final String s) {
        this.fGlobalTypeDeclsExt.put(((s != null) ? s : "") + "," + xsSimpleType.getName(), xsSimpleType);
        if (xsSimpleType.getNamespaceItem() == null && xsSimpleType instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)xsSimpleType).setNamespaceItem(this);
        }
    }
    
    public final void addIDConstraintDecl(final XSElementDecl xsElementDecl, final IdentityConstraint identityConstraint) {
        xsElementDecl.addIDConstraint(identityConstraint);
        this.fGlobalIDConstraintDecls.put(identityConstraint.getIdentityConstraintName(), identityConstraint);
    }
    
    public final void addIDConstraintDecl(final XSElementDecl xsElementDecl, final IdentityConstraint identityConstraint, final String s) {
        this.fGlobalIDConstraintDeclsExt.put(((s != null) ? s : "") + "," + identityConstraint.getIdentityConstraintName(), identityConstraint);
    }
    
    public final void addTypeAlternative(final XSElementDecl xsElementDecl, final XSTypeAlternativeImpl xsTypeAlternativeImpl) {
        xsElementDecl.addTypeAlternative(xsTypeAlternativeImpl);
    }
    
    public final XSAttributeDecl getGlobalAttributeDecl(final String s) {
        return (XSAttributeDecl)this.fGlobalAttrDecls.get(s);
    }
    
    public final XSAttributeDecl getGlobalAttributeDecl(final String s, final String s2) {
        return (XSAttributeDecl)this.fGlobalAttrDeclsExt.get(((s2 != null) ? s2 : "") + "," + s);
    }
    
    public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(final String s) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(s);
    }
    
    public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(final String s, final String s2) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDeclsExt.get(((s2 != null) ? s2 : "") + "," + s);
    }
    
    public final XSElementDecl getGlobalElementDecl(final String s) {
        return (XSElementDecl)this.fGlobalElemDecls.get(s);
    }
    
    public final XSElementDecl getGlobalElementDecl(final String s, final String s2) {
        return (XSElementDecl)this.fGlobalElemDeclsExt.get(((s2 != null) ? s2 : "") + "," + s);
    }
    
    public final XSGroupDecl getGlobalGroupDecl(final String s) {
        return (XSGroupDecl)this.fGlobalGroupDecls.get(s);
    }
    
    public final XSGroupDecl getGlobalGroupDecl(final String s, final String s2) {
        return (XSGroupDecl)this.fGlobalGroupDeclsExt.get(((s2 != null) ? s2 : "") + "," + s);
    }
    
    public final XSNotationDecl getGlobalNotationDecl(final String s) {
        return (XSNotationDecl)this.fGlobalNotationDecls.get(s);
    }
    
    public final XSNotationDecl getGlobalNotationDecl(final String s, final String s2) {
        return (XSNotationDecl)this.fGlobalNotationDeclsExt.get(((s2 != null) ? s2 : "") + "," + s);
    }
    
    public final XSTypeDefinition getGlobalTypeDecl(final String s) {
        return (XSTypeDefinition)this.fGlobalTypeDecls.get(s);
    }
    
    public final XSTypeDefinition getGlobalTypeDecl(final String s, final String s2) {
        return (XSTypeDefinition)this.fGlobalTypeDeclsExt.get(((s2 != null) ? s2 : "") + "," + s);
    }
    
    public final IdentityConstraint getIDConstraintDecl(final String s) {
        return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(s);
    }
    
    public final IdentityConstraint getIDConstraintDecl(final String s, final String s2) {
        return (IdentityConstraint)this.fGlobalIDConstraintDeclsExt.get(((s2 != null) ? s2 : "") + "," + s);
    }
    
    public final boolean hasIDConstraints() {
        return this.fGlobalIDConstraintDecls.getLength() > 0;
    }
    
    public void addComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl, final SimpleLocator simpleLocator) {
        if (this.fCTCount == this.fComplexTypeDecls.length) {
            this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount + 16);
            this.fCTLocators = resize(this.fCTLocators, this.fCTCount + 16);
        }
        this.fCTLocators[this.fCTCount] = simpleLocator;
        this.fComplexTypeDecls[this.fCTCount++] = xsComplexTypeDecl;
    }
    
    public void addRedefinedGroupDecl(final XSGroupDecl xsGroupDecl, final XSGroupDecl xsGroupDecl2, final SimpleLocator simpleLocator) {
        if (this.fRGCount == this.fRedefinedGroupDecls.length) {
            this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount << 1);
            this.fRGLocators = resize(this.fRGLocators, this.fRGCount);
        }
        this.fRGLocators[this.fRGCount / 2] = simpleLocator;
        this.fRedefinedGroupDecls[this.fRGCount++] = xsGroupDecl;
        this.fRedefinedGroupDecls[this.fRGCount++] = xsGroupDecl2;
    }
    
    final XSComplexTypeDecl[] getUncheckedComplexTypeDecls() {
        if (this.fCTCount < this.fComplexTypeDecls.length) {
            this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
            this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
        }
        return this.fComplexTypeDecls;
    }
    
    final SimpleLocator[] getUncheckedCTLocators() {
        if (this.fCTCount < this.fCTLocators.length) {
            this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
            this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
        }
        return this.fCTLocators;
    }
    
    final XSGroupDecl[] getRedefinedGroupDecls() {
        if (this.fRGCount < this.fRedefinedGroupDecls.length) {
            this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount);
            this.fRGLocators = resize(this.fRGLocators, this.fRGCount / 2);
        }
        return this.fRedefinedGroupDecls;
    }
    
    final SimpleLocator[] getRGLocators() {
        if (this.fRGCount < this.fRedefinedGroupDecls.length) {
            this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount);
            this.fRGLocators = resize(this.fRGLocators, this.fRGCount / 2);
        }
        return this.fRGLocators;
    }
    
    final void setUncheckedTypeNum(final int fctCount) {
        this.fCTCount = fctCount;
        this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
        this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
    }
    
    final XSElementDecl[] getSubstitutionGroups() {
        if (this.fSubGroupCount < this.fSubGroups.length) {
            this.fSubGroups = resize(this.fSubGroups, this.fSubGroupCount);
        }
        return this.fSubGroups;
    }
    
    public static XSComplexTypeDecl getXSAnyType(final short n) {
        if (n == 1) {
            return SchemaGrammar.fAnyType;
        }
        if (n == 4) {
            return SchemaGrammar.fAnyType11;
        }
        return SchemaGrammar.fAnyTypeExtended;
    }
    
    public static boolean isAnyType(final XSTypeDefinition xsTypeDefinition) {
        return xsTypeDefinition == SchemaGrammar.fAnyType || xsTypeDefinition == SchemaGrammar.fAnyType11 || xsTypeDefinition == SchemaGrammar.fAnyTypeExtended;
    }
    
    public static SchemaGrammar getS4SGrammar(final short n) {
        if (n == 1) {
            return SchemaGrammar.SG_SchemaNS;
        }
        if (n == 4) {
            return SchemaGrammar.SG_Schema11NS;
        }
        return SchemaGrammar.SG_SchemaNSExtended;
    }
    
    public static SchemaGrammar getXSIGrammar(final short n) {
        if (n == 4) {
            return SchemaGrammar.SG_XSI11;
        }
        return SchemaGrammar.SG_XSI;
    }
    
    static final XSComplexTypeDecl[] resize(final XSComplexTypeDecl[] array, final int n) {
        final XSComplexTypeDecl[] array2 = new XSComplexTypeDecl[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    static final XSGroupDecl[] resize(final XSGroupDecl[] array, final int n) {
        final XSGroupDecl[] array2 = new XSGroupDecl[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    static final XSElementDecl[] resize(final XSElementDecl[] array, final int n) {
        final XSElementDecl[] array2 = new XSElementDecl[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    static final SimpleLocator[] resize(final SimpleLocator[] array, final int n) {
        final SimpleLocator[] array2 = new SimpleLocator[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    public synchronized void addDocument(final Object o, final String s) {
        if (this.fDocuments == null) {
            this.fDocuments = new Vector();
            this.fLocations = new Vector();
        }
        this.fDocuments.addElement(o);
        this.fLocations.addElement(s);
    }
    
    public synchronized void removeDocument(final int n) {
        if (this.fDocuments != null && n >= 0 && n < this.fDocuments.size()) {
            this.fDocuments.removeElementAt(n);
            this.fLocations.removeElementAt(n);
        }
    }
    
    public String getSchemaNamespace() {
        return this.fTargetNamespace;
    }
    
    synchronized DOMParser getDOMParser() {
        if (this.fDOMParser != null) {
            final DOMParser domParser = this.fDOMParser.get();
            if (domParser != null) {
                return domParser;
            }
        }
        final XML11Configuration xml11Configuration = new XML11Configuration(this.fSymbolTable);
        xml11Configuration.setFeature("http://xml.org/sax/features/namespaces", true);
        xml11Configuration.setFeature("http://xml.org/sax/features/validation", false);
        final DOMParser domParser2 = new DOMParser(xml11Configuration);
        try {
            domParser2.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
        }
        catch (final SAXException ex) {}
        this.fDOMParser = new SoftReference(domParser2);
        return domParser2;
    }
    
    synchronized SAXParser getSAXParser() {
        if (this.fSAXParser != null) {
            final SAXParser saxParser = this.fSAXParser.get();
            if (saxParser != null) {
                return saxParser;
            }
        }
        final XML11Configuration xml11Configuration = new XML11Configuration(this.fSymbolTable);
        xml11Configuration.setFeature("http://xml.org/sax/features/namespaces", true);
        xml11Configuration.setFeature("http://xml.org/sax/features/validation", false);
        final SAXParser saxParser2 = new SAXParser(xml11Configuration);
        this.fSAXParser = new SoftReference(saxParser2);
        return saxParser2;
    }
    
    public synchronized XSNamedMap getComponents(final short n) {
        if (n <= 0 || n > 16 || !SchemaGrammar.GLOBAL_COMP[n]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        if (this.fComponents == null) {
            this.fComponents = new XSNamedMap[17];
        }
        if (this.fComponents[n] == null) {
            SymbolHash symbolHash = null;
            switch (n) {
                case 3:
                case 15:
                case 16: {
                    symbolHash = this.fGlobalTypeDecls;
                    break;
                }
                case 1: {
                    symbolHash = this.fGlobalAttrDecls;
                    break;
                }
                case 2: {
                    symbolHash = this.fGlobalElemDecls;
                    break;
                }
                case 5: {
                    symbolHash = this.fGlobalAttrGrpDecls;
                    break;
                }
                case 6: {
                    symbolHash = this.fGlobalGroupDecls;
                    break;
                }
                case 11: {
                    symbolHash = this.fGlobalNotationDecls;
                    break;
                }
                case 10: {
                    symbolHash = this.fGlobalIDConstraintDecls;
                    break;
                }
            }
            if (n == 15 || n == 16) {
                this.fComponents[n] = new XSNamedMap4Types(this.fTargetNamespace, symbolHash, n);
            }
            else {
                this.fComponents[n] = new XSNamedMapImpl(this.fTargetNamespace, symbolHash);
            }
        }
        return this.fComponents[n];
    }
    
    public synchronized ObjectList getComponentsExt(final short n) {
        if (n <= 0 || n > 16 || !SchemaGrammar.GLOBAL_COMP[n]) {
            return ObjectListImpl.EMPTY_LIST;
        }
        if (this.fComponentsExt == null) {
            this.fComponentsExt = new ObjectList[17];
        }
        if (this.fComponentsExt[n] == null) {
            SymbolHash symbolHash = null;
            switch (n) {
                case 3:
                case 15:
                case 16: {
                    symbolHash = this.fGlobalTypeDeclsExt;
                    break;
                }
                case 1: {
                    symbolHash = this.fGlobalAttrDeclsExt;
                    break;
                }
                case 2: {
                    symbolHash = this.fGlobalElemDeclsExt;
                    break;
                }
                case 5: {
                    symbolHash = this.fGlobalAttrGrpDeclsExt;
                    break;
                }
                case 6: {
                    symbolHash = this.fGlobalGroupDeclsExt;
                    break;
                }
                case 11: {
                    symbolHash = this.fGlobalNotationDeclsExt;
                    break;
                }
                case 10: {
                    symbolHash = this.fGlobalIDConstraintDeclsExt;
                    break;
                }
            }
            final Object[] entries = symbolHash.getEntries();
            this.fComponentsExt[n] = new ObjectListImpl(entries, entries.length);
        }
        return this.fComponentsExt[n];
    }
    
    public synchronized void resetComponents() {
        this.fComponents = null;
        this.fComponentsExt = null;
    }
    
    public XSTypeDefinition getTypeDefinition(final String s) {
        return this.getGlobalTypeDecl(s);
    }
    
    public XSAttributeDeclaration getAttributeDeclaration(final String s) {
        return this.getGlobalAttributeDecl(s);
    }
    
    public XSElementDeclaration getElementDeclaration(final String s) {
        return this.getGlobalElementDecl(s);
    }
    
    public XSAttributeGroupDefinition getAttributeGroup(final String s) {
        return this.getGlobalAttributeGroupDecl(s);
    }
    
    public XSModelGroupDefinition getModelGroupDefinition(final String s) {
        return this.getGlobalGroupDecl(s);
    }
    
    public XSNotationDeclaration getNotationDeclaration(final String s) {
        return this.getGlobalNotationDecl(s);
    }
    
    public XSIDCDefinition getIDCDefinition(final String s) {
        return this.getIDConstraintDecl(s);
    }
    
    public StringList getDocumentLocations() {
        return new StringListImpl(this.fLocations);
    }
    
    public XSModel toXSModel() {
        return new XSModelImpl(new SchemaGrammar[] { this });
    }
    
    public XSModel toXSModel(final XSGrammar[] array) {
        if (array == null || array.length == 0) {
            return this.toXSModel();
        }
        final int length = array.length;
        boolean b = false;
        for (int i = 0; i < length; ++i) {
            if (array[i] == this) {
                b = true;
                break;
            }
        }
        final SchemaGrammar[] array2 = new SchemaGrammar[b ? length : (length + 1)];
        for (int j = 0; j < length; ++j) {
            array2[j] = (SchemaGrammar)array[j];
        }
        if (!b) {
            array2[length] = this;
        }
        return new XSModelImpl(array2);
    }
    
    public XSObjectList getAnnotations() {
        if (this.fNumAnnotations == 0) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        return new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations);
    }
    
    public void addAnnotation(final XSAnnotationImpl xsAnnotationImpl) {
        if (xsAnnotationImpl == null) {
            return;
        }
        if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[2];
        }
        else if (this.fNumAnnotations == this.fAnnotations.length) {
            final XSAnnotationImpl[] fAnnotations = new XSAnnotationImpl[this.fNumAnnotations << 1];
            System.arraycopy(this.fAnnotations, 0, fAnnotations, 0, this.fNumAnnotations);
            this.fAnnotations = fAnnotations;
        }
        this.fAnnotations[this.fNumAnnotations++] = xsAnnotationImpl;
    }
    
    public void setImmutable(final boolean fIsImmutable) {
        this.fIsImmutable = fIsImmutable;
    }
    
    public boolean isImmutable() {
        return this.fIsImmutable;
    }
    
    static {
        fAnyType = new XSAnyType();
        fAnyTypeExtended = new XSAnyTypeExtended();
        fAnyType11 = new XS11AnyType();
        SG_SchemaNS = new BuiltinSchemaGrammar(1, (short)1);
        SG_SchemaNSExtended = new BuiltinSchemaGrammar(1, (short)2);
        SG_Schema11NS = new BuiltinSchemaGrammar(1, (short)4);
        fAnySimpleType = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("anySimpleType");
        fAnyAtomicType = (XSSimpleType)SchemaGrammar.SG_Schema11NS.getGlobalTypeDecl("anyAtomicType");
        SG_XSI = new BuiltinSchemaGrammar(2, (short)1);
        SG_XSI11 = new BuiltinSchemaGrammar(2, (short)4);
        GLOBAL_COMP = new boolean[] { false, true, true, true, false, true, true, false, false, false, true, true, false, false, false, true, true };
    }
    
    private static class BuiltinAttrDecl extends XSAttributeDecl
    {
        public BuiltinAttrDecl(final String fName, final String fTargetNamespace, final XSSimpleType fType, final short fScope) {
            this.fName = fName;
            super.fTargetNamespace = fTargetNamespace;
            this.fType = fType;
            this.fScope = fScope;
        }
        
        public void setValues(final String s, final String s2, final XSSimpleType xsSimpleType, final short n, final short n2, final ValidatedInfo validatedInfo, final XSComplexTypeDecl xsComplexTypeDecl) {
        }
        
        public void reset() {
        }
        
        public XSAnnotation getAnnotation() {
            return null;
        }
        
        public XSNamespaceItem getNamespaceItem() {
            return SchemaGrammar.SG_XSI;
        }
    }
    
    public static class BuiltinSchemaGrammar extends SchemaGrammar
    {
        private static final String EXTENDED_SCHEMA_FACTORY_CLASS = "org.apache.xerces.impl.dv.xs.ExtendedSchemaDVFactoryImpl";
        private static final String SCHEMA11_FACTORY_CLASS = "org.apache.xerces.impl.dv.xs.Schema11DVFactoryImpl";
        
        public BuiltinSchemaGrammar(final int n, final short n2) {
            SchemaDVFactory schemaDVFactory;
            if (n2 == 1) {
                schemaDVFactory = SchemaDVFactory.getInstance();
            }
            else if (n2 == 4) {
                schemaDVFactory = SchemaDVFactory.getInstance("org.apache.xerces.impl.dv.xs.Schema11DVFactoryImpl");
            }
            else {
                schemaDVFactory = SchemaDVFactory.getInstance("org.apache.xerces.impl.dv.xs.ExtendedSchemaDVFactoryImpl");
            }
            if (n == 1) {
                this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
                this.fGrammarDescription = new XSDDescription();
                this.fGrammarDescription.fContextType = 3;
                this.fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);
                this.fGlobalAttrDecls = new SymbolHash(1);
                this.fGlobalAttrGrpDecls = new SymbolHash(1);
                this.fGlobalElemDecls = new SymbolHash(1);
                this.fGlobalGroupDecls = new SymbolHash(1);
                this.fGlobalNotationDecls = new SymbolHash(1);
                this.fGlobalIDConstraintDecls = new SymbolHash(1);
                this.fGlobalAttrDeclsExt = new SymbolHash(1);
                this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
                this.fGlobalElemDeclsExt = new SymbolHash(1);
                this.fGlobalGroupDeclsExt = new SymbolHash(1);
                this.fGlobalNotationDeclsExt = new SymbolHash(1);
                this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
                this.fGlobalTypeDeclsExt = new SymbolHash(1);
                this.fAllGlobalElemDecls = new SymbolHash(1);
                this.fGlobalTypeDecls = schemaDVFactory.getBuiltInTypes();
                final int length = this.fGlobalTypeDecls.getLength();
                final XSTypeDefinition[] array = new XSTypeDefinition[length];
                this.fGlobalTypeDecls.getValues(array, 0);
                for (final XSTypeDefinition xsTypeDefinition : array) {
                    if (xsTypeDefinition instanceof XSSimpleTypeDecl) {
                        ((XSSimpleTypeDecl)xsTypeDefinition).setNamespaceItem(this);
                    }
                }
                final XSComplexTypeDecl xsAnyType = SchemaGrammar.getXSAnyType(n2);
                this.fGlobalTypeDecls.put(xsAnyType.getName(), xsAnyType);
            }
            else if (n == 2) {
                this.fTargetNamespace = SchemaSymbols.URI_XSI;
                this.fGrammarDescription = new XSDDescription();
                this.fGrammarDescription.fContextType = 3;
                this.fGrammarDescription.setNamespace(SchemaSymbols.URI_XSI);
                this.fGlobalAttrGrpDecls = new SymbolHash(1);
                this.fGlobalElemDecls = new SymbolHash(1);
                this.fGlobalGroupDecls = new SymbolHash(1);
                this.fGlobalNotationDecls = new SymbolHash(1);
                this.fGlobalIDConstraintDecls = new SymbolHash(1);
                this.fGlobalTypeDecls = new SymbolHash(1);
                this.fGlobalAttrDeclsExt = new SymbolHash(1);
                this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
                this.fGlobalElemDeclsExt = new SymbolHash(1);
                this.fGlobalGroupDeclsExt = new SymbolHash(1);
                this.fGlobalNotationDeclsExt = new SymbolHash(1);
                this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
                this.fGlobalTypeDeclsExt = new SymbolHash(1);
                this.fAllGlobalElemDecls = new SymbolHash(1);
                this.fGlobalAttrDecls = new SymbolHash(8);
                final short n3 = 1;
                final String xsi_TYPE = SchemaSymbols.XSI_TYPE;
                this.fGlobalAttrDecls.put(xsi_TYPE, new BuiltinAttrDecl(xsi_TYPE, SchemaSymbols.URI_XSI, schemaDVFactory.getBuiltInType("QName"), n3));
                final String xsi_NIL = SchemaSymbols.XSI_NIL;
                this.fGlobalAttrDecls.put(xsi_NIL, new BuiltinAttrDecl(xsi_NIL, SchemaSymbols.URI_XSI, schemaDVFactory.getBuiltInType("boolean"), n3));
                final XSSimpleType builtInType = schemaDVFactory.getBuiltInType("anyURI");
                final String xsi_SCHEMALOCATION = SchemaSymbols.XSI_SCHEMALOCATION;
                final String uri_XSI = SchemaSymbols.URI_XSI;
                final XSSimpleType typeList = schemaDVFactory.createTypeList("#AnonType_schemaLocation", SchemaSymbols.URI_XSI, (short)0, builtInType, null);
                if (typeList instanceof XSSimpleTypeDecl) {
                    ((XSSimpleTypeDecl)typeList).setAnonymous(true);
                }
                this.fGlobalAttrDecls.put(xsi_SCHEMALOCATION, new BuiltinAttrDecl(xsi_SCHEMALOCATION, uri_XSI, typeList, n3));
                final String xsi_NONAMESPACESCHEMALOCATION = SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION;
                this.fGlobalAttrDecls.put(xsi_NONAMESPACESCHEMALOCATION, new BuiltinAttrDecl(xsi_NONAMESPACESCHEMALOCATION, SchemaSymbols.URI_XSI, builtInType, n3));
            }
        }
        
        public XMLGrammarDescription getGrammarDescription() {
            return this.fGrammarDescription.makeClone();
        }
        
        public void setImportedGrammars(final Vector vector) {
        }
        
        public void addGlobalAttributeDecl(final XSAttributeDecl xsAttributeDecl) {
        }
        
        public void addGlobalAttributeDecl(final XSAttributeDecl xsAttributeDecl, final String s) {
        }
        
        public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl xsAttributeGroupDecl) {
        }
        
        public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl xsAttributeGroupDecl, final String s) {
        }
        
        public void addGlobalElementDecl(final XSElementDecl xsElementDecl) {
        }
        
        public void addGlobalElementDecl(final XSElementDecl xsElementDecl, final String s) {
        }
        
        public void addGlobalElementDeclAll(final XSElementDecl xsElementDecl) {
        }
        
        public void addGlobalGroupDecl(final XSGroupDecl xsGroupDecl) {
        }
        
        public void addGlobalGroupDecl(final XSGroupDecl xsGroupDecl, final String s) {
        }
        
        public void addGlobalNotationDecl(final XSNotationDecl xsNotationDecl) {
        }
        
        public void addGlobalNotationDecl(final XSNotationDecl xsNotationDecl, final String s) {
        }
        
        public void addGlobalTypeDecl(final XSTypeDefinition xsTypeDefinition) {
        }
        
        public void addGlobalTypeDecl(final XSTypeDefinition xsTypeDefinition, final String s) {
        }
        
        public void addGlobalComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl) {
        }
        
        public void addGlobalComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl, final String s) {
        }
        
        public void addGlobalSimpleTypeDecl(final XSSimpleType xsSimpleType) {
        }
        
        public void addGlobalSimpleTypeDecl(final XSSimpleType xsSimpleType, final String s) {
        }
        
        public void addComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl, final SimpleLocator simpleLocator) {
        }
        
        public void addRedefinedGroupDecl(final XSGroupDecl xsGroupDecl, final XSGroupDecl xsGroupDecl2, final SimpleLocator simpleLocator) {
        }
        
        public synchronized void addDocument(final Object o, final String s) {
        }
        
        synchronized DOMParser getDOMParser() {
            return null;
        }
        
        synchronized SAXParser getSAXParser() {
            return null;
        }
    }
    
    public static final class Schema4Annotations extends SchemaGrammar
    {
        private static final Schema4Annotations INSTANCE_10;
        private static final Schema4Annotations INSTANCE_Extended;
        private static final Schema4Annotations INSTANCE_11;
        
        public static Schema4Annotations getSchema4Annotations(final short n) {
            if (n == 1) {
                return Schema4Annotations.INSTANCE_10;
            }
            if (n == 4) {
                return Schema4Annotations.INSTANCE_11;
            }
            return Schema4Annotations.INSTANCE_Extended;
        }
        
        private Schema4Annotations(final short n) {
            this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            this.fGrammarDescription = new XSDDescription();
            this.fGrammarDescription.fContextType = 3;
            this.fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);
            this.fGlobalAttrDecls = new SymbolHash(1);
            this.fGlobalAttrGrpDecls = new SymbolHash(1);
            this.fGlobalElemDecls = new SymbolHash(6);
            this.fGlobalGroupDecls = new SymbolHash(1);
            this.fGlobalNotationDecls = new SymbolHash(1);
            this.fGlobalIDConstraintDecls = new SymbolHash(1);
            this.fGlobalAttrDeclsExt = new SymbolHash(1);
            this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
            this.fGlobalElemDeclsExt = new SymbolHash(6);
            this.fGlobalGroupDeclsExt = new SymbolHash(1);
            this.fGlobalNotationDeclsExt = new SymbolHash(1);
            this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
            this.fGlobalTypeDeclsExt = new SymbolHash(1);
            this.fAllGlobalElemDecls = new SymbolHash(6);
            this.fGlobalTypeDecls = SchemaGrammar.getS4SGrammar(n).fGlobalTypeDecls;
            final XSElementDecl annotationElementDecl = this.createAnnotationElementDecl(SchemaSymbols.ELT_ANNOTATION);
            final XSElementDecl annotationElementDecl2 = this.createAnnotationElementDecl(SchemaSymbols.ELT_DOCUMENTATION);
            final XSElementDecl annotationElementDecl3 = this.createAnnotationElementDecl(SchemaSymbols.ELT_APPINFO);
            this.fGlobalElemDecls.put(annotationElementDecl.fName, annotationElementDecl);
            this.fGlobalElemDecls.put(annotationElementDecl2.fName, annotationElementDecl2);
            this.fGlobalElemDecls.put(annotationElementDecl3.fName, annotationElementDecl3);
            this.fGlobalElemDeclsExt.put("," + annotationElementDecl.fName, annotationElementDecl);
            this.fGlobalElemDeclsExt.put("," + annotationElementDecl2.fName, annotationElementDecl2);
            this.fGlobalElemDeclsExt.put("," + annotationElementDecl3.fName, annotationElementDecl3);
            this.fAllGlobalElemDecls.put(annotationElementDecl, annotationElementDecl);
            this.fAllGlobalElemDecls.put(annotationElementDecl2, annotationElementDecl2);
            this.fAllGlobalElemDecls.put(annotationElementDecl3, annotationElementDecl3);
            final XSComplexTypeDecl fType = new XSComplexTypeDecl();
            final XSComplexTypeDecl fType2 = new XSComplexTypeDecl();
            final XSComplexTypeDecl fType3 = new XSComplexTypeDecl();
            annotationElementDecl.fType = fType;
            annotationElementDecl2.fType = fType2;
            annotationElementDecl3.fType = fType3;
            final XSAttributeGroupDecl xsAttributeGroupDecl = new XSAttributeGroupDecl();
            final XSAttributeGroupDecl xsAttributeGroupDecl2 = new XSAttributeGroupDecl();
            final XSAttributeGroupDecl xsAttributeGroupDecl3 = new XSAttributeGroupDecl();
            final XSAttributeUseImpl xsAttributeUseImpl = new XSAttributeUseImpl();
            (xsAttributeUseImpl.fAttrDecl = new XSAttributeDecl()).setValues(SchemaSymbols.ATT_ID, null, (XSSimpleType)this.fGlobalTypeDecls.get("ID"), (short)0, (short)2, null, fType, null, false);
            xsAttributeUseImpl.fUse = 0;
            xsAttributeUseImpl.fConstraintType = 0;
            final XSAttributeUseImpl xsAttributeUseImpl2 = new XSAttributeUseImpl();
            (xsAttributeUseImpl2.fAttrDecl = new XSAttributeDecl()).setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, null, fType2, null, false);
            xsAttributeUseImpl2.fUse = 0;
            xsAttributeUseImpl2.fConstraintType = 0;
            final XSAttributeUseImpl xsAttributeUseImpl3 = new XSAttributeUseImpl();
            (xsAttributeUseImpl3.fAttrDecl = new XSAttributeDecl()).setValues("lang".intern(), NamespaceContext.XML_URI, (XSSimpleType)this.fGlobalTypeDecls.get("language"), (short)0, (short)2, null, fType2, null, false);
            xsAttributeUseImpl3.fUse = 0;
            xsAttributeUseImpl3.fConstraintType = 0;
            final XSAttributeUseImpl xsAttributeUseImpl4 = new XSAttributeUseImpl();
            (xsAttributeUseImpl4.fAttrDecl = new XSAttributeDecl()).setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, null, fType3, null, false);
            xsAttributeUseImpl4.fUse = 0;
            xsAttributeUseImpl4.fConstraintType = 0;
            final XSWildcardDecl fAttributeWC = new XSWildcardDecl();
            fAttributeWC.fNamespaceList = new String[] { this.fTargetNamespace, null };
            fAttributeWC.fType = 2;
            fAttributeWC.fProcessContents = 3;
            xsAttributeGroupDecl.addAttributeUse(xsAttributeUseImpl);
            xsAttributeGroupDecl.fAttributeWC = fAttributeWC;
            xsAttributeGroupDecl2.addAttributeUse(xsAttributeUseImpl2);
            xsAttributeGroupDecl2.addAttributeUse(xsAttributeUseImpl3);
            xsAttributeGroupDecl2.fAttributeWC = fAttributeWC;
            xsAttributeGroupDecl3.addAttributeUse(xsAttributeUseImpl4);
            xsAttributeGroupDecl3.fAttributeWC = fAttributeWC;
            final XSParticleDecl unboundedModelGroupParticle = this.createUnboundedModelGroupParticle();
            final XSModelGroupImpl fValue = new XSModelGroupImpl();
            fValue.fCompositor = 101;
            fValue.fParticleCount = 2;
            (fValue.fParticles = new XSParticleDecl[2])[0] = this.createChoiceElementParticle(annotationElementDecl3);
            fValue.fParticles[1] = this.createChoiceElementParticle(annotationElementDecl2);
            unboundedModelGroupParticle.fValue = fValue;
            final XSParticleDecl unboundedAnyWildcardSequenceParticle = this.createUnboundedAnyWildcardSequenceParticle();
            final XSComplexTypeDecl xsAnyType = SchemaGrammar.getXSAnyType(n);
            fType.setValues("#AnonType_" + SchemaSymbols.ELT_ANNOTATION, this.fTargetNamespace, xsAnyType, (short)2, (short)0, (short)3, (short)2, false, xsAttributeGroupDecl, null, unboundedModelGroupParticle, XSObjectListImpl.EMPTY_LIST, null);
            fType.setName("#AnonType_" + SchemaSymbols.ELT_ANNOTATION);
            fType.setIsAnonymous();
            fType2.setValues("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION, this.fTargetNamespace, xsAnyType, (short)2, (short)0, (short)3, (short)3, false, xsAttributeGroupDecl2, null, unboundedAnyWildcardSequenceParticle, XSObjectListImpl.EMPTY_LIST, null);
            fType2.setName("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION);
            fType2.setIsAnonymous();
            fType3.setValues("#AnonType_" + SchemaSymbols.ELT_APPINFO, this.fTargetNamespace, xsAnyType, (short)2, (short)0, (short)3, (short)3, false, xsAttributeGroupDecl3, null, unboundedAnyWildcardSequenceParticle, XSObjectListImpl.EMPTY_LIST, null);
            fType3.setName("#AnonType_" + SchemaSymbols.ELT_APPINFO);
            fType3.setIsAnonymous();
        }
        
        public XMLGrammarDescription getGrammarDescription() {
            return this.fGrammarDescription.makeClone();
        }
        
        public void setImportedGrammars(final Vector vector) {
        }
        
        public void addGlobalAttributeDecl(final XSAttributeDecl xsAttributeDecl) {
        }
        
        public void addGlobalAttributeDecl(final XSAttributeGroupDecl xsAttributeGroupDecl, final String s) {
        }
        
        public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl xsAttributeGroupDecl) {
        }
        
        public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl xsAttributeGroupDecl, final String s) {
        }
        
        public void addGlobalElementDecl(final XSElementDecl xsElementDecl) {
        }
        
        public void addGlobalElementDecl(final XSElementDecl xsElementDecl, final String s) {
        }
        
        public void addGlobalElementDeclAll(final XSElementDecl xsElementDecl) {
        }
        
        public void addGlobalGroupDecl(final XSGroupDecl xsGroupDecl) {
        }
        
        public void addGlobalGroupDecl(final XSGroupDecl xsGroupDecl, final String s) {
        }
        
        public void addGlobalNotationDecl(final XSNotationDecl xsNotationDecl) {
        }
        
        public void addGlobalNotationDecl(final XSNotationDecl xsNotationDecl, final String s) {
        }
        
        public void addGlobalTypeDecl(final XSTypeDefinition xsTypeDefinition) {
        }
        
        public void addGlobalTypeDecl(final XSTypeDefinition xsTypeDefinition, final String s) {
        }
        
        public void addGlobalComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl) {
        }
        
        public void addGlobalComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl, final String s) {
        }
        
        public void addGlobalSimpleTypeDecl(final XSSimpleType xsSimpleType) {
        }
        
        public void addGlobalSimpleTypeDecl(final XSSimpleType xsSimpleType, final String s) {
        }
        
        public void addComplexTypeDecl(final XSComplexTypeDecl xsComplexTypeDecl, final SimpleLocator simpleLocator) {
        }
        
        public void addRedefinedGroupDecl(final XSGroupDecl xsGroupDecl, final XSGroupDecl xsGroupDecl2, final SimpleLocator simpleLocator) {
        }
        
        public synchronized void addDocument(final Object o, final String s) {
        }
        
        synchronized DOMParser getDOMParser() {
            return null;
        }
        
        synchronized SAXParser getSAXParser() {
            return null;
        }
        
        private XSElementDecl createAnnotationElementDecl(final String fName) {
            final XSElementDecl xsElementDecl = new XSElementDecl();
            xsElementDecl.fName = fName;
            xsElementDecl.fTargetNamespace = this.fTargetNamespace;
            xsElementDecl.setIsGlobal();
            xsElementDecl.fBlock = 7;
            xsElementDecl.setConstraintType((short)0);
            return xsElementDecl;
        }
        
        private XSParticleDecl createUnboundedModelGroupParticle() {
            final XSParticleDecl xsParticleDecl = new XSParticleDecl();
            xsParticleDecl.fMinOccurs = 0;
            xsParticleDecl.fMaxOccurs = -1;
            xsParticleDecl.fType = 3;
            return xsParticleDecl;
        }
        
        private XSParticleDecl createChoiceElementParticle(final XSElementDecl fValue) {
            final XSParticleDecl xsParticleDecl = new XSParticleDecl();
            xsParticleDecl.fMinOccurs = 1;
            xsParticleDecl.fMaxOccurs = 1;
            xsParticleDecl.fType = 1;
            xsParticleDecl.fValue = fValue;
            return xsParticleDecl;
        }
        
        private XSParticleDecl createUnboundedAnyWildcardSequenceParticle() {
            final XSParticleDecl unboundedModelGroupParticle = this.createUnboundedModelGroupParticle();
            final XSModelGroupImpl fValue = new XSModelGroupImpl();
            fValue.fCompositor = 102;
            fValue.fParticleCount = 1;
            (fValue.fParticles = new XSParticleDecl[1])[0] = this.createAnyLaxWildcardParticle();
            unboundedModelGroupParticle.fValue = fValue;
            return unboundedModelGroupParticle;
        }
        
        private XSParticleDecl createAnyLaxWildcardParticle() {
            final XSParticleDecl xsParticleDecl = new XSParticleDecl();
            xsParticleDecl.fMinOccurs = 1;
            xsParticleDecl.fMaxOccurs = 1;
            xsParticleDecl.fType = 2;
            final XSWildcardDecl fValue = new XSWildcardDecl();
            fValue.fNamespaceList = null;
            fValue.fType = 1;
            fValue.fProcessContents = 3;
            xsParticleDecl.fValue = fValue;
            return xsParticleDecl;
        }
        
        static {
            INSTANCE_10 = new Schema4Annotations((short)1);
            INSTANCE_Extended = new Schema4Annotations((short)2);
            INSTANCE_11 = new Schema4Annotations((short)4);
        }
    }
    
    private static class XS11AnyType extends XSAnyType
    {
        public XSNamespaceItem getNamespaceItem() {
            return SchemaGrammar.SG_Schema11NS;
        }
    }
    
    public static class XSAnyType extends XSComplexTypeDecl
    {
        public XSAnyType() {
            this.fName = "anyType";
            super.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            this.fBaseType = this;
            this.fDerivedBy = 2;
            this.fContentType = 3;
            this.fParticle = this.createParticle();
            this.fAttrGrp = this.createAttrGrp();
        }
        
        public void setValues(final String s, final String s2, final XSTypeDefinition xsTypeDefinition, final short n, final short n2, final short n3, final short n4, final boolean b, final XSAttributeGroupDecl xsAttributeGroupDecl, final XSSimpleType xsSimpleType, final XSParticleDecl xsParticleDecl) {
        }
        
        public void setName(final String s) {
        }
        
        public void setIsAbstractType() {
        }
        
        public void setContainsTypeID() {
        }
        
        public void setIsAnonymous() {
        }
        
        public void reset() {
        }
        
        public XSObjectList getAnnotations() {
            return XSObjectListImpl.EMPTY_LIST;
        }
        
        public XSNamespaceItem getNamespaceItem() {
            return SchemaGrammar.SG_SchemaNS;
        }
        
        private XSAttributeGroupDecl createAttrGrp() {
            final XSWildcardDecl fAttributeWC = new XSWildcardDecl();
            fAttributeWC.fProcessContents = 3;
            final XSAttributeGroupDecl xsAttributeGroupDecl = new XSAttributeGroupDecl();
            xsAttributeGroupDecl.fAttributeWC = fAttributeWC;
            return xsAttributeGroupDecl;
        }
        
        private XSParticleDecl createParticle() {
            final XSWildcardDecl fValue = new XSWildcardDecl();
            fValue.fProcessContents = 3;
            final XSParticleDecl xsParticleDecl = new XSParticleDecl();
            xsParticleDecl.fMinOccurs = 0;
            xsParticleDecl.fMaxOccurs = -1;
            xsParticleDecl.fType = 2;
            xsParticleDecl.fValue = fValue;
            final XSModelGroupImpl fValue2 = new XSModelGroupImpl();
            fValue2.fCompositor = 102;
            fValue2.fParticleCount = 1;
            (fValue2.fParticles = new XSParticleDecl[1])[0] = xsParticleDecl;
            final XSParticleDecl xsParticleDecl2 = new XSParticleDecl();
            xsParticleDecl2.fType = 3;
            xsParticleDecl2.fValue = fValue2;
            return xsParticleDecl2;
        }
    }
    
    private static class XSAnyTypeExtended extends XSAnyType
    {
        public XSNamespaceItem getNamespaceItem() {
            return SchemaGrammar.SG_SchemaNSExtended;
        }
    }
}
