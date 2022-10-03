package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import java.util.Vector;
import org.w3c.dom.Element;
import java.util.Stack;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;

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
    protected boolean fIsChameleonSchema;
    protected Element fSchemaElement;
    Vector fImportedNS;
    protected ValidationState fValidationContext;
    SymbolTable fSymbolTable;
    protected XSAttributeChecker fAttrChecker;
    protected Object[] fSchemaAttrs;
    protected XSAnnotationInfo fAnnotations;
    private Vector fReportedTNS;
    
    XSDocumentInfo(final Element schemaRoot, final XSAttributeChecker attrChecker, final SymbolTable symbolTable) throws XMLSchemaException {
        this.SchemaNamespaceSupportStack = new Stack();
        this.fImportedNS = new Vector();
        this.fValidationContext = new ValidationState();
        this.fSymbolTable = null;
        this.fAnnotations = null;
        this.fReportedTNS = null;
        this.initNamespaceSupport(this.fSchemaElement = schemaRoot);
        this.fIsChameleonSchema = false;
        this.fSymbolTable = symbolTable;
        this.fAttrChecker = attrChecker;
        if (schemaRoot != null) {
            final Element root = schemaRoot;
            this.fSchemaAttrs = attrChecker.checkAttributes(root, true, this);
            if (this.fSchemaAttrs == null) {
                throw new XMLSchemaException(null, (Object[])null);
            }
            this.fAreLocalAttributesQualified = (((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_AFORMDEFAULT]).intValue() == 1);
            this.fAreLocalElementsQualified = (((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_EFORMDEFAULT]).intValue() == 1);
            this.fBlockDefault = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_BLOCKDEFAULT]).shortValue();
            this.fFinalDefault = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_FINALDEFAULT]).shortValue();
            this.fTargetNamespace = (String)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_TARGETNAMESPACE];
            if (this.fTargetNamespace != null) {
                this.fTargetNamespace = symbolTable.addSymbol(this.fTargetNamespace);
            }
            this.fNamespaceSupportRoot = new SchemaNamespaceSupport(this.fNamespaceSupport);
            this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
            this.fValidationContext.setSymbolTable(symbolTable);
        }
    }
    
    private void initNamespaceSupport(final Element schemaRoot) {
        (this.fNamespaceSupport = new SchemaNamespaceSupport()).reset();
        for (Node parent = schemaRoot.getParentNode(); parent != null && parent.getNodeType() == 1 && !parent.getNodeName().equals("DOCUMENT_NODE"); parent = parent.getParentNode()) {
            final Element eparent = (Element)parent;
            final NamedNodeMap map = eparent.getAttributes();
            for (int length = (map != null) ? map.getLength() : 0, i = 0; i < length; ++i) {
                final Attr attr = (Attr)map.item(i);
                final String uri = attr.getNamespaceURI();
                if (uri != null && uri.equals("http://www.w3.org/2000/xmlns/")) {
                    String prefix = attr.getLocalName().intern();
                    if (prefix == "xmlns") {
                        prefix = "";
                    }
                    if (this.fNamespaceSupport.getURI(prefix) == null) {
                        this.fNamespaceSupport.declarePrefix(prefix, attr.getValue().intern());
                    }
                }
            }
        }
    }
    
    void backupNSSupport(SchemaNamespaceSupport nsSupport) {
        this.SchemaNamespaceSupportStack.push(this.fNamespaceSupport);
        if (nsSupport == null) {
            nsSupport = this.fNamespaceSupportRoot;
        }
        this.fNamespaceSupport = new SchemaNamespaceSupport(nsSupport);
        this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
    }
    
    void restoreNSSupport() {
        this.fNamespaceSupport = this.SchemaNamespaceSupportStack.pop();
        this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
    }
    
    @Override
    public String toString() {
        return (this.fTargetNamespace == null) ? "no targetNamspace" : ("targetNamespace is " + this.fTargetNamespace);
    }
    
    public void addAllowedNS(final String namespace) {
        this.fImportedNS.addElement((namespace == null) ? "" : namespace);
    }
    
    public boolean isAllowedNS(final String namespace) {
        return this.fImportedNS.contains((namespace == null) ? "" : namespace);
    }
    
    final boolean needReportTNSError(final String uri) {
        if (this.fReportedTNS == null) {
            this.fReportedTNS = new Vector();
        }
        else if (this.fReportedTNS.contains(uri)) {
            return false;
        }
        this.fReportedTNS.addElement(uri);
        return true;
    }
    
    Object[] getSchemaAttrs() {
        return this.fSchemaAttrs;
    }
    
    void returnSchemaAttrs() {
        this.fAttrChecker.returnAttrArray(this.fSchemaAttrs, null);
        this.fSchemaAttrs = null;
    }
    
    void addAnnotation(final XSAnnotationInfo info) {
        info.next = this.fAnnotations;
        this.fAnnotations = info;
    }
    
    XSAnnotationInfo getAnnotations() {
        return this.fAnnotations;
    }
    
    void removeAnnotations() {
        this.fAnnotations = null;
    }
}
