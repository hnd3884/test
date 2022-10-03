package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.identity.KeyRef;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import org.w3c.dom.Element;

class XSDKeyrefTraverser extends XSDAbstractIDConstraintTraverser
{
    public XSDKeyrefTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }
    
    void traverse(final Element krElem, final XSElementDecl element, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(krElem, false, schemaDoc);
        final String krName = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        if (krName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_KEYREF, SchemaSymbols.ATT_NAME }, krElem);
            this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }
        final QName kName = (QName)attrValues[XSAttributeChecker.ATTIDX_REFER];
        if (kName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_KEYREF, SchemaSymbols.ATT_REFER }, krElem);
            this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }
        UniqueOrKey key = null;
        final IdentityConstraint ret = (IdentityConstraint)this.fSchemaHandler.getGlobalDecl(schemaDoc, 5, kName, krElem);
        if (ret != null) {
            if (ret.getCategory() == 1 || ret.getCategory() == 3) {
                key = (UniqueOrKey)ret;
            }
            else {
                this.reportSchemaError("src-resolve", new Object[] { kName.rawname, "identity constraint key/unique" }, krElem);
            }
        }
        if (key == null) {
            this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }
        KeyRef keyRef = new KeyRef(schemaDoc.fTargetNamespace, krName, element.fName, key);
        if (this.traverseIdentityConstraint(keyRef, krElem, schemaDoc, attrValues)) {
            if (key.getFieldCount() != keyRef.getFieldCount()) {
                this.reportSchemaError("c-props-correct.2", new Object[] { krName, key.getIdentityConstraintName() }, krElem);
            }
            else {
                if (grammar.getIDConstraintDecl(keyRef.getIdentityConstraintName()) == null) {
                    grammar.addIDConstraintDecl(element, keyRef);
                }
                final String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
                final IdentityConstraint idc = grammar.getIDConstraintDecl(keyRef.getIdentityConstraintName(), loc);
                if (idc == null) {
                    grammar.addIDConstraintDecl(element, keyRef, loc);
                }
                if (this.fSchemaHandler.fTolerateDuplicates) {
                    if (idc != null && idc instanceof KeyRef) {
                        keyRef = (KeyRef)idc;
                    }
                    this.fSchemaHandler.addIDConstraintDecl(keyRef);
                }
            }
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
    }
}
