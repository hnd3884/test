package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.impl.xs.util.ObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMap4Types;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import java.util.Vector;
import java.lang.ref.SoftReference;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;

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
    public static final XSComplexTypeDecl fAnyType;
    public static final BuiltinSchemaGrammar SG_SchemaNS;
    private static final BuiltinSchemaGrammar SG_SchemaNSExtended;
    public static final XSSimpleType fAnySimpleType;
    public static final BuiltinSchemaGrammar SG_XSI;
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
    
    public SchemaGrammar(final String targetNamespace, final XSDDescription grammarDesc, final SymbolTable symbolTable) {
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
        this.fTargetNamespace = targetNamespace;
        this.fGrammarDescription = grammarDesc;
        this.fSymbolTable = symbolTable;
        this.fGlobalAttrDecls = new SymbolHash();
        this.fGlobalAttrGrpDecls = new SymbolHash();
        this.fGlobalElemDecls = new SymbolHash();
        this.fGlobalGroupDecls = new SymbolHash();
        this.fGlobalNotationDecls = new SymbolHash();
        this.fGlobalIDConstraintDecls = new SymbolHash();
        this.fGlobalAttrDeclsExt = new SymbolHash();
        this.fGlobalAttrGrpDeclsExt = new SymbolHash();
        this.fGlobalElemDeclsExt = new SymbolHash();
        this.fGlobalGroupDeclsExt = new SymbolHash();
        this.fGlobalNotationDeclsExt = new SymbolHash();
        this.fGlobalIDConstraintDeclsExt = new SymbolHash();
        this.fGlobalTypeDeclsExt = new SymbolHash();
        this.fAllGlobalElemDecls = new SymbolHash();
        if (this.fTargetNamespace == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            this.fGlobalTypeDecls = SchemaGrammar.SG_SchemaNS.fGlobalTypeDecls.makeClone();
        }
        else {
            this.fGlobalTypeDecls = new SymbolHash();
        }
    }
    
    public SchemaGrammar(final SchemaGrammar grammar) {
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
        this.fTargetNamespace = grammar.fTargetNamespace;
        this.fGrammarDescription = grammar.fGrammarDescription.makeClone();
        this.fSymbolTable = grammar.fSymbolTable;
        this.fGlobalAttrDecls = grammar.fGlobalAttrDecls.makeClone();
        this.fGlobalAttrGrpDecls = grammar.fGlobalAttrGrpDecls.makeClone();
        this.fGlobalElemDecls = grammar.fGlobalElemDecls.makeClone();
        this.fGlobalGroupDecls = grammar.fGlobalGroupDecls.makeClone();
        this.fGlobalNotationDecls = grammar.fGlobalNotationDecls.makeClone();
        this.fGlobalIDConstraintDecls = grammar.fGlobalIDConstraintDecls.makeClone();
        this.fGlobalTypeDecls = grammar.fGlobalTypeDecls.makeClone();
        this.fGlobalAttrDeclsExt = grammar.fGlobalAttrDeclsExt.makeClone();
        this.fGlobalAttrGrpDeclsExt = grammar.fGlobalAttrGrpDeclsExt.makeClone();
        this.fGlobalElemDeclsExt = grammar.fGlobalElemDeclsExt.makeClone();
        this.fGlobalGroupDeclsExt = grammar.fGlobalGroupDeclsExt.makeClone();
        this.fGlobalNotationDeclsExt = grammar.fGlobalNotationDeclsExt.makeClone();
        this.fGlobalIDConstraintDeclsExt = grammar.fGlobalIDConstraintDeclsExt.makeClone();
        this.fGlobalTypeDeclsExt = grammar.fGlobalTypeDeclsExt.makeClone();
        this.fAllGlobalElemDecls = grammar.fAllGlobalElemDecls.makeClone();
        this.fNumAnnotations = grammar.fNumAnnotations;
        if (this.fNumAnnotations > 0) {
            this.fAnnotations = new XSAnnotationImpl[grammar.fAnnotations.length];
            System.arraycopy(grammar.fAnnotations, 0, this.fAnnotations, 0, this.fNumAnnotations);
        }
        this.fSubGroupCount = grammar.fSubGroupCount;
        if (this.fSubGroupCount > 0) {
            this.fSubGroups = new XSElementDecl[grammar.fSubGroups.length];
            System.arraycopy(grammar.fSubGroups, 0, this.fSubGroups, 0, this.fSubGroupCount);
        }
        this.fCTCount = grammar.fCTCount;
        if (this.fCTCount > 0) {
            this.fComplexTypeDecls = new XSComplexTypeDecl[grammar.fComplexTypeDecls.length];
            this.fCTLocators = new SimpleLocator[grammar.fCTLocators.length];
            System.arraycopy(grammar.fComplexTypeDecls, 0, this.fComplexTypeDecls, 0, this.fCTCount);
            System.arraycopy(grammar.fCTLocators, 0, this.fCTLocators, 0, this.fCTCount);
        }
        this.fRGCount = grammar.fRGCount;
        if (this.fRGCount > 0) {
            this.fRedefinedGroupDecls = new XSGroupDecl[grammar.fRedefinedGroupDecls.length];
            this.fRGLocators = new SimpleLocator[grammar.fRGLocators.length];
            System.arraycopy(grammar.fRedefinedGroupDecls, 0, this.fRedefinedGroupDecls, 0, this.fRGCount);
            System.arraycopy(grammar.fRGLocators, 0, this.fRGLocators, 0, this.fRGCount);
        }
        if (grammar.fImported != null) {
            this.fImported = new Vector();
            for (int i = 0; i < grammar.fImported.size(); ++i) {
                this.fImported.add(grammar.fImported.elementAt(i));
            }
        }
        if (grammar.fLocations != null) {
            for (int k = 0; k < grammar.fLocations.size(); ++k) {
                this.addDocument(null, grammar.fLocations.elementAt(k));
            }
        }
    }
    
    @Override
    public XMLGrammarDescription getGrammarDescription() {
        return this.fGrammarDescription;
    }
    
    public boolean isNamespaceAware() {
        return true;
    }
    
    public void setImportedGrammars(final Vector importedGrammars) {
        this.fImported = importedGrammars;
    }
    
    public Vector getImportedGrammars() {
        return this.fImported;
    }
    
    public final String getTargetNamespace() {
        return this.fTargetNamespace;
    }
    
    public void addGlobalAttributeDecl(final XSAttributeDecl decl) {
        this.fGlobalAttrDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }
    
    public void addGlobalAttributeDecl(final XSAttributeDecl decl, final String location) {
        this.fGlobalAttrDeclsExt.put(((location != null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl decl) {
        this.fGlobalAttrGrpDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }
    
    public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl decl, final String location) {
        this.fGlobalAttrGrpDeclsExt.put(((location != null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalElementDeclAll(final XSElementDecl decl) {
        if (this.fAllGlobalElemDecls.get(decl) == null) {
            this.fAllGlobalElemDecls.put(decl, decl);
            if (decl.fSubGroup != null) {
                if (this.fSubGroupCount == this.fSubGroups.length) {
                    this.fSubGroups = resize(this.fSubGroups, this.fSubGroupCount + 16);
                }
                this.fSubGroups[this.fSubGroupCount++] = decl;
            }
        }
    }
    
    public void addGlobalElementDecl(final XSElementDecl decl) {
        this.fGlobalElemDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }
    
    public void addGlobalElementDecl(final XSElementDecl decl, final String location) {
        this.fGlobalElemDeclsExt.put(((location != null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalGroupDecl(final XSGroupDecl decl) {
        this.fGlobalGroupDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }
    
    public void addGlobalGroupDecl(final XSGroupDecl decl, final String location) {
        this.fGlobalGroupDeclsExt.put(((location != null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalNotationDecl(final XSNotationDecl decl) {
        this.fGlobalNotationDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }
    
    public void addGlobalNotationDecl(final XSNotationDecl decl, final String location) {
        this.fGlobalNotationDeclsExt.put(((location != null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalTypeDecl(final XSTypeDefinition decl) {
        this.fGlobalTypeDecls.put(decl.getName(), decl);
        if (decl instanceof XSComplexTypeDecl) {
            ((XSComplexTypeDecl)decl).setNamespaceItem(this);
        }
        else if (decl instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)decl).setNamespaceItem(this);
        }
    }
    
    public void addGlobalTypeDecl(final XSTypeDefinition decl, final String location) {
        this.fGlobalTypeDeclsExt.put(((location != null) ? location : "") + "," + decl.getName(), decl);
        if (decl.getNamespaceItem() == null) {
            if (decl instanceof XSComplexTypeDecl) {
                ((XSComplexTypeDecl)decl).setNamespaceItem(this);
            }
            else if (decl instanceof XSSimpleTypeDecl) {
                ((XSSimpleTypeDecl)decl).setNamespaceItem(this);
            }
        }
    }
    
    public void addGlobalComplexTypeDecl(final XSComplexTypeDecl decl) {
        this.fGlobalTypeDecls.put(decl.getName(), decl);
        decl.setNamespaceItem(this);
    }
    
    public void addGlobalComplexTypeDecl(final XSComplexTypeDecl decl, final String location) {
        this.fGlobalTypeDeclsExt.put(((location != null) ? location : "") + "," + decl.getName(), decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }
    
    public void addGlobalSimpleTypeDecl(final XSSimpleType decl) {
        this.fGlobalTypeDecls.put(decl.getName(), decl);
        if (decl instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)decl).setNamespaceItem(this);
        }
    }
    
    public void addGlobalSimpleTypeDecl(final XSSimpleType decl, final String location) {
        this.fGlobalTypeDeclsExt.put(((location != null) ? location : "") + "," + decl.getName(), decl);
        if (decl.getNamespaceItem() == null && decl instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)decl).setNamespaceItem(this);
        }
    }
    
    public final void addIDConstraintDecl(final XSElementDecl elmDecl, final IdentityConstraint decl) {
        elmDecl.addIDConstraint(decl);
        this.fGlobalIDConstraintDecls.put(decl.getIdentityConstraintName(), decl);
    }
    
    public final void addIDConstraintDecl(final XSElementDecl elmDecl, final IdentityConstraint decl, final String location) {
        this.fGlobalIDConstraintDeclsExt.put(((location != null) ? location : "") + "," + decl.getIdentityConstraintName(), decl);
    }
    
    public final XSAttributeDecl getGlobalAttributeDecl(final String declName) {
        return (XSAttributeDecl)this.fGlobalAttrDecls.get(declName);
    }
    
    public final XSAttributeDecl getGlobalAttributeDecl(final String declName, final String location) {
        return (XSAttributeDecl)this.fGlobalAttrDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }
    
    public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(final String declName) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(declName);
    }
    
    public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(final String declName, final String location) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }
    
    public final XSElementDecl getGlobalElementDecl(final String declName) {
        return (XSElementDecl)this.fGlobalElemDecls.get(declName);
    }
    
    public final XSElementDecl getGlobalElementDecl(final String declName, final String location) {
        return (XSElementDecl)this.fGlobalElemDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }
    
    public final XSGroupDecl getGlobalGroupDecl(final String declName) {
        return (XSGroupDecl)this.fGlobalGroupDecls.get(declName);
    }
    
    public final XSGroupDecl getGlobalGroupDecl(final String declName, final String location) {
        return (XSGroupDecl)this.fGlobalGroupDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }
    
    public final XSNotationDecl getGlobalNotationDecl(final String declName) {
        return (XSNotationDecl)this.fGlobalNotationDecls.get(declName);
    }
    
    public final XSNotationDecl getGlobalNotationDecl(final String declName, final String location) {
        return (XSNotationDecl)this.fGlobalNotationDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }
    
    public final XSTypeDefinition getGlobalTypeDecl(final String declName) {
        return (XSTypeDefinition)this.fGlobalTypeDecls.get(declName);
    }
    
    public final XSTypeDefinition getGlobalTypeDecl(final String declName, final String location) {
        return (XSTypeDefinition)this.fGlobalTypeDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }
    
    public final IdentityConstraint getIDConstraintDecl(final String declName) {
        return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(declName);
    }
    
    public final IdentityConstraint getIDConstraintDecl(final String declName, final String location) {
        return (IdentityConstraint)this.fGlobalIDConstraintDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }
    
    public final boolean hasIDConstraints() {
        return this.fGlobalIDConstraintDecls.getLength() > 0;
    }
    
    public void addComplexTypeDecl(final XSComplexTypeDecl decl, final SimpleLocator locator) {
        if (this.fCTCount == this.fComplexTypeDecls.length) {
            this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount + 16);
            this.fCTLocators = resize(this.fCTLocators, this.fCTCount + 16);
        }
        this.fCTLocators[this.fCTCount] = locator;
        this.fComplexTypeDecls[this.fCTCount++] = decl;
    }
    
    public void addRedefinedGroupDecl(final XSGroupDecl derived, final XSGroupDecl base, final SimpleLocator locator) {
        if (this.fRGCount == this.fRedefinedGroupDecls.length) {
            this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount << 1);
            this.fRGLocators = resize(this.fRGLocators, this.fRGCount);
        }
        this.fRGLocators[this.fRGCount / 2] = locator;
        this.fRedefinedGroupDecls[this.fRGCount++] = derived;
        this.fRedefinedGroupDecls[this.fRGCount++] = base;
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
    
    final void setUncheckedTypeNum(final int newSize) {
        this.fCTCount = newSize;
        this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
        this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
    }
    
    final XSElementDecl[] getSubstitutionGroups() {
        if (this.fSubGroupCount < this.fSubGroups.length) {
            this.fSubGroups = resize(this.fSubGroups, this.fSubGroupCount);
        }
        return this.fSubGroups;
    }
    
    public static SchemaGrammar getS4SGrammar(final short schemaVersion) {
        if (schemaVersion == 1) {
            return SchemaGrammar.SG_SchemaNS;
        }
        return SchemaGrammar.SG_SchemaNSExtended;
    }
    
    static final XSComplexTypeDecl[] resize(final XSComplexTypeDecl[] oldArray, final int newSize) {
        final XSComplexTypeDecl[] newArray = new XSComplexTypeDecl[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }
    
    static final XSGroupDecl[] resize(final XSGroupDecl[] oldArray, final int newSize) {
        final XSGroupDecl[] newArray = new XSGroupDecl[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }
    
    static final XSElementDecl[] resize(final XSElementDecl[] oldArray, final int newSize) {
        final XSElementDecl[] newArray = new XSElementDecl[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }
    
    static final SimpleLocator[] resize(final SimpleLocator[] oldArray, final int newSize) {
        final SimpleLocator[] newArray = new SimpleLocator[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }
    
    public synchronized void addDocument(final Object document, final String location) {
        if (this.fDocuments == null) {
            this.fDocuments = new Vector();
            this.fLocations = new Vector();
        }
        this.fDocuments.addElement(document);
        this.fLocations.addElement(location);
    }
    
    public synchronized void removeDocument(final int index) {
        if (this.fDocuments != null && index >= 0 && index < this.fDocuments.size()) {
            this.fDocuments.removeElementAt(index);
            this.fLocations.removeElementAt(index);
        }
    }
    
    @Override
    public String getSchemaNamespace() {
        return this.fTargetNamespace;
    }
    
    synchronized DOMParser getDOMParser() {
        if (this.fDOMParser != null) {
            final DOMParser parser = this.fDOMParser.get();
            if (parser != null) {
                return parser;
            }
        }
        final XML11Configuration config = new XML11Configuration(this.fSymbolTable);
        config.setFeature("http://xml.org/sax/features/namespaces", true);
        config.setFeature("http://xml.org/sax/features/validation", false);
        final DOMParser parser2 = new DOMParser(config);
        try {
            parser2.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
        }
        catch (final SAXException ex) {}
        this.fDOMParser = new SoftReference((T)parser2);
        return parser2;
    }
    
    synchronized SAXParser getSAXParser() {
        if (this.fSAXParser != null) {
            final SAXParser parser = this.fSAXParser.get();
            if (parser != null) {
                return parser;
            }
        }
        final XML11Configuration config = new XML11Configuration(this.fSymbolTable);
        config.setFeature("http://xml.org/sax/features/namespaces", true);
        config.setFeature("http://xml.org/sax/features/validation", false);
        final SAXParser parser2 = new SAXParser(config);
        this.fSAXParser = new SoftReference((T)parser2);
        return parser2;
    }
    
    @Override
    public synchronized XSNamedMap getComponents(final short objectType) {
        if (objectType <= 0 || objectType > 16 || !SchemaGrammar.GLOBAL_COMP[objectType]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        if (this.fComponents == null) {
            this.fComponents = new XSNamedMap[17];
        }
        if (this.fComponents[objectType] == null) {
            SymbolHash table = null;
            switch (objectType) {
                case 3:
                case 15:
                case 16: {
                    table = this.fGlobalTypeDecls;
                    break;
                }
                case 1: {
                    table = this.fGlobalAttrDecls;
                    break;
                }
                case 2: {
                    table = this.fGlobalElemDecls;
                    break;
                }
                case 5: {
                    table = this.fGlobalAttrGrpDecls;
                    break;
                }
                case 6: {
                    table = this.fGlobalGroupDecls;
                    break;
                }
                case 11: {
                    table = this.fGlobalNotationDecls;
                    break;
                }
            }
            if (objectType == 15 || objectType == 16) {
                this.fComponents[objectType] = new XSNamedMap4Types(this.fTargetNamespace, table, objectType);
            }
            else {
                this.fComponents[objectType] = new XSNamedMapImpl(this.fTargetNamespace, table);
            }
        }
        return this.fComponents[objectType];
    }
    
    public synchronized ObjectList getComponentsExt(final short objectType) {
        if (objectType <= 0 || objectType > 16 || !SchemaGrammar.GLOBAL_COMP[objectType]) {
            return ObjectListImpl.EMPTY_LIST;
        }
        if (this.fComponentsExt == null) {
            this.fComponentsExt = new ObjectList[17];
        }
        if (this.fComponentsExt[objectType] == null) {
            SymbolHash table = null;
            switch (objectType) {
                case 3:
                case 15:
                case 16: {
                    table = this.fGlobalTypeDeclsExt;
                    break;
                }
                case 1: {
                    table = this.fGlobalAttrDeclsExt;
                    break;
                }
                case 2: {
                    table = this.fGlobalElemDeclsExt;
                    break;
                }
                case 5: {
                    table = this.fGlobalAttrGrpDeclsExt;
                    break;
                }
                case 6: {
                    table = this.fGlobalGroupDeclsExt;
                    break;
                }
                case 11: {
                    table = this.fGlobalNotationDeclsExt;
                    break;
                }
            }
            final Object[] entries = table.getEntries();
            this.fComponentsExt[objectType] = new ObjectListImpl(entries, entries.length);
        }
        return this.fComponentsExt[objectType];
    }
    
    public synchronized void resetComponents() {
        this.fComponents = null;
        this.fComponentsExt = null;
    }
    
    @Override
    public XSTypeDefinition getTypeDefinition(final String name) {
        return this.getGlobalTypeDecl(name);
    }
    
    @Override
    public XSAttributeDeclaration getAttributeDeclaration(final String name) {
        return this.getGlobalAttributeDecl(name);
    }
    
    @Override
    public XSElementDeclaration getElementDeclaration(final String name) {
        return this.getGlobalElementDecl(name);
    }
    
    @Override
    public XSAttributeGroupDefinition getAttributeGroup(final String name) {
        return this.getGlobalAttributeGroupDecl(name);
    }
    
    @Override
    public XSModelGroupDefinition getModelGroupDefinition(final String name) {
        return this.getGlobalGroupDecl(name);
    }
    
    @Override
    public XSNotationDeclaration getNotationDeclaration(final String name) {
        return this.getGlobalNotationDecl(name);
    }
    
    @Override
    public StringList getDocumentLocations() {
        return new StringListImpl(this.fLocations);
    }
    
    @Override
    public XSModel toXSModel() {
        return new XSModelImpl(new SchemaGrammar[] { this });
    }
    
    @Override
    public XSModel toXSModel(final XSGrammar[] grammars) {
        if (grammars == null || grammars.length == 0) {
            return this.toXSModel();
        }
        final int len = grammars.length;
        boolean hasSelf = false;
        for (int i = 0; i < len; ++i) {
            if (grammars[i] == this) {
                hasSelf = true;
                break;
            }
        }
        final SchemaGrammar[] gs = new SchemaGrammar[hasSelf ? len : (len + 1)];
        for (int j = 0; j < len; ++j) {
            gs[j] = (SchemaGrammar)grammars[j];
        }
        if (!hasSelf) {
            gs[len] = this;
        }
        return new XSModelImpl(gs);
    }
    
    @Override
    public XSObjectList getAnnotations() {
        if (this.fNumAnnotations == 0) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        return new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations);
    }
    
    public void addAnnotation(final XSAnnotationImpl annotation) {
        if (annotation == null) {
            return;
        }
        if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[2];
        }
        else if (this.fNumAnnotations == this.fAnnotations.length) {
            final XSAnnotationImpl[] newArray = new XSAnnotationImpl[this.fNumAnnotations << 1];
            System.arraycopy(this.fAnnotations, 0, newArray, 0, this.fNumAnnotations);
            this.fAnnotations = newArray;
        }
        this.fAnnotations[this.fNumAnnotations++] = annotation;
    }
    
    public void setImmutable(final boolean isImmutable) {
        this.fIsImmutable = isImmutable;
    }
    
    public boolean isImmutable() {
        return this.fIsImmutable;
    }
    
    static {
        fAnyType = new XSAnyType();
        SG_SchemaNS = new BuiltinSchemaGrammar(1, (short)1);
        SG_SchemaNSExtended = new BuiltinSchemaGrammar(1, (short)2);
        fAnySimpleType = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("anySimpleType");
        SG_XSI = new BuiltinSchemaGrammar(2, (short)1);
        GLOBAL_COMP = new boolean[] { false, true, true, true, false, true, true, false, false, false, false, true, false, false, false, true, true };
    }
    
    public static class BuiltinSchemaGrammar extends SchemaGrammar
    {
        private static final String EXTENDED_SCHEMA_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.ExtendedSchemaDVFactoryImpl";
        
        public BuiltinSchemaGrammar(final int grammar, final short schemaVersion) {
            SchemaDVFactory schemaFactory;
            if (schemaVersion == 1) {
                schemaFactory = SchemaDVFactory.getInstance();
            }
            else {
                schemaFactory = SchemaDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.xs.ExtendedSchemaDVFactoryImpl");
            }
            if (grammar == 1) {
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
                this.fGlobalTypeDecls = schemaFactory.getBuiltInTypes();
                final int length = this.fGlobalTypeDecls.getLength();
                final XSTypeDefinition[] typeDefinitions = new XSTypeDefinition[length];
                this.fGlobalTypeDecls.getValues(typeDefinitions, 0);
                for (final XSTypeDefinition xtd : typeDefinitions) {
                    if (xtd instanceof XSSimpleTypeDecl) {
                        ((XSSimpleTypeDecl)xtd).setNamespaceItem(this);
                    }
                }
                this.fGlobalTypeDecls.put(BuiltinSchemaGrammar.fAnyType.getName(), BuiltinSchemaGrammar.fAnyType);
            }
            else if (grammar == 2) {
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
                String name = null;
                String tns = null;
                XSSimpleType type = null;
                final short scope = 1;
                name = SchemaSymbols.XSI_TYPE;
                tns = SchemaSymbols.URI_XSI;
                type = schemaFactory.getBuiltInType("QName");
                this.fGlobalAttrDecls.put(name, new BuiltinAttrDecl(name, tns, type, scope));
                name = SchemaSymbols.XSI_NIL;
                tns = SchemaSymbols.URI_XSI;
                type = schemaFactory.getBuiltInType("boolean");
                this.fGlobalAttrDecls.put(name, new BuiltinAttrDecl(name, tns, type, scope));
                final XSSimpleType anyURI = schemaFactory.getBuiltInType("anyURI");
                name = SchemaSymbols.XSI_SCHEMALOCATION;
                tns = SchemaSymbols.URI_XSI;
                type = schemaFactory.createTypeList("#AnonType_schemaLocation", SchemaSymbols.URI_XSI, (short)0, anyURI, null);
                if (type instanceof XSSimpleTypeDecl) {
                    ((XSSimpleTypeDecl)type).setAnonymous(true);
                }
                this.fGlobalAttrDecls.put(name, new BuiltinAttrDecl(name, tns, type, scope));
                name = SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION;
                tns = SchemaSymbols.URI_XSI;
                type = anyURI;
                this.fGlobalAttrDecls.put(name, new BuiltinAttrDecl(name, tns, type, scope));
            }
        }
        
        @Override
        public XMLGrammarDescription getGrammarDescription() {
            return this.fGrammarDescription.makeClone();
        }
        
        @Override
        public void setImportedGrammars(final Vector importedGrammars) {
        }
        
        @Override
        public void addGlobalAttributeDecl(final XSAttributeDecl decl) {
        }
        
        @Override
        public void addGlobalAttributeDecl(final XSAttributeDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl decl) {
        }
        
        @Override
        public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalElementDecl(final XSElementDecl decl) {
        }
        
        @Override
        public void addGlobalElementDecl(final XSElementDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalElementDeclAll(final XSElementDecl decl) {
        }
        
        @Override
        public void addGlobalGroupDecl(final XSGroupDecl decl) {
        }
        
        @Override
        public void addGlobalGroupDecl(final XSGroupDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalNotationDecl(final XSNotationDecl decl) {
        }
        
        @Override
        public void addGlobalNotationDecl(final XSNotationDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalTypeDecl(final XSTypeDefinition decl) {
        }
        
        @Override
        public void addGlobalTypeDecl(final XSTypeDefinition decl, final String location) {
        }
        
        @Override
        public void addGlobalComplexTypeDecl(final XSComplexTypeDecl decl) {
        }
        
        @Override
        public void addGlobalComplexTypeDecl(final XSComplexTypeDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalSimpleTypeDecl(final XSSimpleType decl) {
        }
        
        @Override
        public void addGlobalSimpleTypeDecl(final XSSimpleType decl, final String location) {
        }
        
        @Override
        public void addComplexTypeDecl(final XSComplexTypeDecl decl, final SimpleLocator locator) {
        }
        
        @Override
        public void addRedefinedGroupDecl(final XSGroupDecl derived, final XSGroupDecl base, final SimpleLocator locator) {
        }
        
        @Override
        public synchronized void addDocument(final Object document, final String location) {
        }
        
        @Override
        synchronized DOMParser getDOMParser() {
            return null;
        }
        
        @Override
        synchronized SAXParser getSAXParser() {
            return null;
        }
    }
    
    public static final class Schema4Annotations extends SchemaGrammar
    {
        public static final Schema4Annotations INSTANCE;
        
        private Schema4Annotations() {
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
            this.fGlobalTypeDecls = Schema4Annotations.SG_SchemaNS.fGlobalTypeDecls;
            final XSElementDecl annotationDecl = this.createAnnotationElementDecl(SchemaSymbols.ELT_ANNOTATION);
            final XSElementDecl documentationDecl = this.createAnnotationElementDecl(SchemaSymbols.ELT_DOCUMENTATION);
            final XSElementDecl appinfoDecl = this.createAnnotationElementDecl(SchemaSymbols.ELT_APPINFO);
            this.fGlobalElemDecls.put(annotationDecl.fName, annotationDecl);
            this.fGlobalElemDecls.put(documentationDecl.fName, documentationDecl);
            this.fGlobalElemDecls.put(appinfoDecl.fName, appinfoDecl);
            this.fGlobalElemDeclsExt.put("," + annotationDecl.fName, annotationDecl);
            this.fGlobalElemDeclsExt.put("," + documentationDecl.fName, documentationDecl);
            this.fGlobalElemDeclsExt.put("," + appinfoDecl.fName, appinfoDecl);
            this.fAllGlobalElemDecls.put(annotationDecl, annotationDecl);
            this.fAllGlobalElemDecls.put(documentationDecl, documentationDecl);
            this.fAllGlobalElemDecls.put(appinfoDecl, appinfoDecl);
            final XSComplexTypeDecl annotationType = new XSComplexTypeDecl();
            final XSComplexTypeDecl documentationType = new XSComplexTypeDecl();
            final XSComplexTypeDecl appinfoType = new XSComplexTypeDecl();
            annotationDecl.fType = annotationType;
            documentationDecl.fType = documentationType;
            appinfoDecl.fType = appinfoType;
            final XSAttributeGroupDecl annotationAttrs = new XSAttributeGroupDecl();
            final XSAttributeGroupDecl documentationAttrs = new XSAttributeGroupDecl();
            final XSAttributeGroupDecl appinfoAttrs = new XSAttributeGroupDecl();
            final XSAttributeUseImpl annotationIDAttr = new XSAttributeUseImpl();
            (annotationIDAttr.fAttrDecl = new XSAttributeDecl()).setValues(SchemaSymbols.ATT_ID, null, (XSSimpleType)this.fGlobalTypeDecls.get("ID"), (short)0, (short)2, null, annotationType, null);
            annotationIDAttr.fUse = 0;
            annotationIDAttr.fConstraintType = 0;
            final XSAttributeUseImpl documentationSourceAttr = new XSAttributeUseImpl();
            (documentationSourceAttr.fAttrDecl = new XSAttributeDecl()).setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, null, documentationType, null);
            documentationSourceAttr.fUse = 0;
            documentationSourceAttr.fConstraintType = 0;
            final XSAttributeUseImpl documentationLangAttr = new XSAttributeUseImpl();
            (documentationLangAttr.fAttrDecl = new XSAttributeDecl()).setValues("lang".intern(), NamespaceContext.XML_URI, (XSSimpleType)this.fGlobalTypeDecls.get("language"), (short)0, (short)2, null, documentationType, null);
            documentationLangAttr.fUse = 0;
            documentationLangAttr.fConstraintType = 0;
            final XSAttributeUseImpl appinfoSourceAttr = new XSAttributeUseImpl();
            (appinfoSourceAttr.fAttrDecl = new XSAttributeDecl()).setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, null, appinfoType, null);
            appinfoSourceAttr.fUse = 0;
            appinfoSourceAttr.fConstraintType = 0;
            final XSWildcardDecl otherAttrs = new XSWildcardDecl();
            otherAttrs.fNamespaceList = new String[] { this.fTargetNamespace, null };
            otherAttrs.fType = 2;
            otherAttrs.fProcessContents = 3;
            annotationAttrs.addAttributeUse(annotationIDAttr);
            annotationAttrs.fAttributeWC = otherAttrs;
            documentationAttrs.addAttributeUse(documentationSourceAttr);
            documentationAttrs.addAttributeUse(documentationLangAttr);
            documentationAttrs.fAttributeWC = otherAttrs;
            appinfoAttrs.addAttributeUse(appinfoSourceAttr);
            appinfoAttrs.fAttributeWC = otherAttrs;
            final XSParticleDecl annotationParticle = this.createUnboundedModelGroupParticle();
            final XSModelGroupImpl annotationChoice = new XSModelGroupImpl();
            annotationChoice.fCompositor = 101;
            annotationChoice.fParticleCount = 2;
            (annotationChoice.fParticles = new XSParticleDecl[2])[0] = this.createChoiceElementParticle(appinfoDecl);
            annotationChoice.fParticles[1] = this.createChoiceElementParticle(documentationDecl);
            annotationParticle.fValue = annotationChoice;
            final XSParticleDecl anyWCSequenceParticle = this.createUnboundedAnyWildcardSequenceParticle();
            annotationType.setValues("#AnonType_" + SchemaSymbols.ELT_ANNOTATION, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)2, false, annotationAttrs, null, annotationParticle, new XSObjectListImpl(null, 0));
            annotationType.setName("#AnonType_" + SchemaSymbols.ELT_ANNOTATION);
            annotationType.setIsAnonymous();
            documentationType.setValues("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)3, false, documentationAttrs, null, anyWCSequenceParticle, new XSObjectListImpl(null, 0));
            documentationType.setName("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION);
            documentationType.setIsAnonymous();
            appinfoType.setValues("#AnonType_" + SchemaSymbols.ELT_APPINFO, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)3, false, appinfoAttrs, null, anyWCSequenceParticle, new XSObjectListImpl(null, 0));
            appinfoType.setName("#AnonType_" + SchemaSymbols.ELT_APPINFO);
            appinfoType.setIsAnonymous();
        }
        
        @Override
        public XMLGrammarDescription getGrammarDescription() {
            return this.fGrammarDescription.makeClone();
        }
        
        @Override
        public void setImportedGrammars(final Vector importedGrammars) {
        }
        
        @Override
        public void addGlobalAttributeDecl(final XSAttributeDecl decl) {
        }
        
        public void addGlobalAttributeDecl(final XSAttributeGroupDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl decl) {
        }
        
        @Override
        public void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalElementDecl(final XSElementDecl decl) {
        }
        
        @Override
        public void addGlobalElementDecl(final XSElementDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalElementDeclAll(final XSElementDecl decl) {
        }
        
        @Override
        public void addGlobalGroupDecl(final XSGroupDecl decl) {
        }
        
        @Override
        public void addGlobalGroupDecl(final XSGroupDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalNotationDecl(final XSNotationDecl decl) {
        }
        
        @Override
        public void addGlobalNotationDecl(final XSNotationDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalTypeDecl(final XSTypeDefinition decl) {
        }
        
        @Override
        public void addGlobalTypeDecl(final XSTypeDefinition decl, final String location) {
        }
        
        @Override
        public void addGlobalComplexTypeDecl(final XSComplexTypeDecl decl) {
        }
        
        @Override
        public void addGlobalComplexTypeDecl(final XSComplexTypeDecl decl, final String location) {
        }
        
        @Override
        public void addGlobalSimpleTypeDecl(final XSSimpleType decl) {
        }
        
        @Override
        public void addGlobalSimpleTypeDecl(final XSSimpleType decl, final String location) {
        }
        
        @Override
        public void addComplexTypeDecl(final XSComplexTypeDecl decl, final SimpleLocator locator) {
        }
        
        @Override
        public void addRedefinedGroupDecl(final XSGroupDecl derived, final XSGroupDecl base, final SimpleLocator locator) {
        }
        
        @Override
        public synchronized void addDocument(final Object document, final String location) {
        }
        
        @Override
        synchronized DOMParser getDOMParser() {
            return null;
        }
        
        @Override
        synchronized SAXParser getSAXParser() {
            return null;
        }
        
        private XSElementDecl createAnnotationElementDecl(final String localName) {
            final XSElementDecl eDecl = new XSElementDecl();
            eDecl.fName = localName;
            eDecl.fTargetNamespace = this.fTargetNamespace;
            eDecl.setIsGlobal();
            eDecl.fBlock = 7;
            eDecl.setConstraintType((short)0);
            return eDecl;
        }
        
        private XSParticleDecl createUnboundedModelGroupParticle() {
            final XSParticleDecl particle = new XSParticleDecl();
            particle.fMinOccurs = 0;
            particle.fMaxOccurs = -1;
            particle.fType = 3;
            return particle;
        }
        
        private XSParticleDecl createChoiceElementParticle(final XSElementDecl ref) {
            final XSParticleDecl particle = new XSParticleDecl();
            particle.fMinOccurs = 1;
            particle.fMaxOccurs = 1;
            particle.fType = 1;
            particle.fValue = ref;
            return particle;
        }
        
        private XSParticleDecl createUnboundedAnyWildcardSequenceParticle() {
            final XSParticleDecl particle = this.createUnboundedModelGroupParticle();
            final XSModelGroupImpl sequence = new XSModelGroupImpl();
            sequence.fCompositor = 102;
            sequence.fParticleCount = 1;
            (sequence.fParticles = new XSParticleDecl[1])[0] = this.createAnyLaxWildcardParticle();
            particle.fValue = sequence;
            return particle;
        }
        
        private XSParticleDecl createAnyLaxWildcardParticle() {
            final XSParticleDecl particle = new XSParticleDecl();
            particle.fMinOccurs = 1;
            particle.fMaxOccurs = 1;
            particle.fType = 2;
            final XSWildcardDecl anyWC = new XSWildcardDecl();
            anyWC.fNamespaceList = null;
            anyWC.fType = 1;
            anyWC.fProcessContents = 3;
            particle.fValue = anyWC;
            return particle;
        }
        
        static {
            INSTANCE = new Schema4Annotations();
        }
    }
    
    private static class XSAnyType extends XSComplexTypeDecl
    {
        public XSAnyType() {
            this.fName = "anyType";
            super.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            this.fBaseType = this;
            this.fDerivedBy = 2;
            this.fContentType = 3;
            this.fParticle = null;
            this.fAttrGrp = null;
        }
        
        public void setValues(final String name, final String targetNamespace, final XSTypeDefinition baseType, final short derivedBy, final short schemaFinal, final short block, final short contentType, final boolean isAbstract, final XSAttributeGroupDecl attrGrp, final XSSimpleType simpleType, final XSParticleDecl particle) {
        }
        
        @Override
        public void setName(final String name) {
        }
        
        @Override
        public void setIsAbstractType() {
        }
        
        @Override
        public void setContainsTypeID() {
        }
        
        @Override
        public void setIsAnonymous() {
        }
        
        @Override
        public void reset() {
        }
        
        @Override
        public XSObjectList getAttributeUses() {
            return XSObjectListImpl.EMPTY_LIST;
        }
        
        @Override
        public XSAttributeGroupDecl getAttrGrp() {
            final XSWildcardDecl wildcard = new XSWildcardDecl();
            wildcard.fProcessContents = 3;
            final XSAttributeGroupDecl attrGrp = new XSAttributeGroupDecl();
            attrGrp.fAttributeWC = wildcard;
            return attrGrp;
        }
        
        @Override
        public XSWildcard getAttributeWildcard() {
            final XSWildcardDecl wildcard = new XSWildcardDecl();
            wildcard.fProcessContents = 3;
            return wildcard;
        }
        
        @Override
        public XSParticle getParticle() {
            final XSWildcardDecl wildcard = new XSWildcardDecl();
            wildcard.fProcessContents = 3;
            final XSParticleDecl particleW = new XSParticleDecl();
            particleW.fMinOccurs = 0;
            particleW.fMaxOccurs = -1;
            particleW.fType = 2;
            particleW.fValue = wildcard;
            final XSModelGroupImpl group = new XSModelGroupImpl();
            group.fCompositor = 102;
            group.fParticleCount = 1;
            (group.fParticles = new XSParticleDecl[1])[0] = particleW;
            final XSParticleDecl particleG = new XSParticleDecl();
            particleG.fType = 3;
            particleG.fValue = group;
            return particleG;
        }
        
        @Override
        public XSObjectList getAnnotations() {
            return XSObjectListImpl.EMPTY_LIST;
        }
        
        @Override
        public XSNamespaceItem getNamespaceItem() {
            return SchemaGrammar.SG_SchemaNS;
        }
    }
    
    private static class BuiltinAttrDecl extends XSAttributeDecl
    {
        public BuiltinAttrDecl(final String name, final String tns, final XSSimpleType type, final short scope) {
            this.fName = name;
            super.fTargetNamespace = tns;
            this.fType = type;
            this.fScope = scope;
        }
        
        public void setValues(final String name, final String targetNamespace, final XSSimpleType simpleType, final short constraintType, final short scope, final ValidatedInfo valInfo, final XSComplexTypeDecl enclosingCT) {
        }
        
        @Override
        public void reset() {
        }
        
        @Override
        public XSAnnotation getAnnotation() {
            return null;
        }
        
        @Override
        public XSNamespaceItem getNamespaceItem() {
            return SchemaGrammar.SG_XSI;
        }
    }
}
