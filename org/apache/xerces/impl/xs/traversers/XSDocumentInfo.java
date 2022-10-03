package org.apache.xerces.impl.xs.traversers;

import org.w3c.dom.Document;
import org.apache.xerces.impl.xs.opti.SchemaDOM;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.dv.xs.TypeValidatorHelper;
import org.apache.xerces.impl.xs.XSOpenContentDecl;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.validation.ValidationState;
import java.util.Vector;
import org.w3c.dom.Element;
import java.util.Stack;
import org.apache.xerces.impl.xs.SchemaNamespaceSupport;

class XSDocumentInfo
{
    protected SchemaNamespaceSupport fNamespaceSupport;
    protected SchemaNamespaceSupport fNamespaceSupportRoot;
    protected Stack SchemaNamespaceSupportStack;
    protected boolean fAreLocalAttributesQualified;
    protected boolean fAreLocalElementsQualified;
    protected short fBlockDefault;
    protected short fFinalDefault;
    String fTargetNamespace;
    String fXpathDefaultNamespace;
    boolean fXpathDefaultNamespaceIs2PoundDefault;
    protected boolean fIsChameleonSchema;
    protected Element fSchemaElement;
    Vector fImportedNS;
    protected ValidationState fValidationContext;
    SymbolTable fSymbolTable;
    protected XSAttributeChecker fAttrChecker;
    protected Object[] fSchemaAttrs;
    protected XSAnnotationInfo fAnnotations;
    QName fDefaultAttributes;
    XSAttributeGroupDecl fDefaultAGroup;
    XSOpenContentDecl fDefaultOpenContent;
    short fDatatypeXMLVersion;
    private Vector fReportedTNS;
    
    XSDocumentInfo(final Element fSchemaElement, final XSAttributeChecker fAttrChecker, final SymbolTable symbolTable, final TypeValidatorHelper typeValidatorHelper, final short n) throws XMLSchemaException {
        this.SchemaNamespaceSupportStack = new Stack();
        this.fImportedNS = new Vector();
        this.fValidationContext = new ValidationState();
        this.fSymbolTable = null;
        this.fAnnotations = null;
        this.fDefaultAttributes = null;
        this.fDefaultAGroup = null;
        this.fDefaultOpenContent = null;
        this.fReportedTNS = null;
        this.fSchemaElement = fSchemaElement;
        (this.fNamespaceSupport = new SchemaNamespaceSupport(fSchemaElement, symbolTable)).reset();
        this.fIsChameleonSchema = false;
        this.fSymbolTable = symbolTable;
        this.fAttrChecker = fAttrChecker;
        this.fDatatypeXMLVersion = n;
        if (fSchemaElement != null) {
            this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
            this.fValidationContext.setSymbolTable(symbolTable);
            this.fValidationContext.setTypeValidatorHelper(typeValidatorHelper);
            this.fValidationContext.setDatatypeXMLVersion(n);
            this.fTargetNamespace = fAttrChecker.checkTargetNamespace(fSchemaElement, this);
            this.fSchemaAttrs = fAttrChecker.checkAttributes(fSchemaElement, true, this);
            if (this.fSchemaAttrs == null) {
                throw new XMLSchemaException(null, (Object[])null);
            }
            this.fAreLocalAttributesQualified = (((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_AFORMDEFAULT]).intValue() == 1);
            this.fAreLocalElementsQualified = (((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_EFORMDEFAULT]).intValue() == 1);
            this.fBlockDefault = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_BLOCKDEFAULT]).shortValue();
            this.fFinalDefault = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_FINALDEFAULT]).shortValue();
            this.fXpathDefaultNamespace = (String)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS];
            this.fXpathDefaultNamespaceIs2PoundDefault = (boolean)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS_TWOPOUNDDFLT];
            this.fNamespaceSupportRoot = new SchemaNamespaceSupport(this.fNamespaceSupport);
            this.fDefaultAttributes = (QName)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_DEFAULTATTRIBUTES];
        }
    }
    
    void backupNSSupport(SchemaNamespaceSupport fNamespaceSupportRoot) {
        this.SchemaNamespaceSupportStack.push(this.fNamespaceSupport);
        if (fNamespaceSupportRoot == null) {
            fNamespaceSupportRoot = this.fNamespaceSupportRoot;
        }
        this.fNamespaceSupport = new SchemaNamespaceSupport(fNamespaceSupportRoot);
        this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
    }
    
    void restoreNSSupport() {
        this.fNamespaceSupport = this.SchemaNamespaceSupportStack.pop();
        this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.fTargetNamespace == null) {
            sb.append("no targetNamspace");
        }
        else {
            sb.append("targetNamespace is ");
            sb.append(this.fTargetNamespace);
        }
        final Document document = (this.fSchemaElement != null) ? this.fSchemaElement.getOwnerDocument() : null;
        if (document instanceof SchemaDOM) {
            final String documentURI = document.getDocumentURI();
            if (documentURI != null && documentURI.length() > 0) {
                sb.append(" :: schemaLocation is ");
                sb.append(documentURI);
            }
        }
        return sb.toString();
    }
    
    public void addAllowedNS(final String s) {
        this.fImportedNS.addElement((s == null) ? "" : s);
    }
    
    public boolean isAllowedNS(final String s) {
        return this.fImportedNS.contains((s == null) ? "" : s);
    }
    
    final boolean needReportTNSError(final String s) {
        if (this.fReportedTNS == null) {
            this.fReportedTNS = new Vector();
        }
        else if (this.fReportedTNS.contains(s)) {
            return false;
        }
        this.fReportedTNS.addElement(s);
        return true;
    }
    
    Object[] getSchemaAttrs() {
        return this.fSchemaAttrs;
    }
    
    void returnSchemaAttrs() {
        this.fAttrChecker.returnAttrArray(this.fSchemaAttrs, null);
        this.fSchemaAttrs = null;
    }
    
    void addAnnotation(final XSAnnotationInfo fAnnotations) {
        fAnnotations.next = this.fAnnotations;
        this.fAnnotations = fAnnotations;
    }
    
    XSAnnotationInfo getAnnotations() {
        return this.fAnnotations;
    }
    
    void removeAnnotations() {
        this.fAnnotations = null;
    }
}
