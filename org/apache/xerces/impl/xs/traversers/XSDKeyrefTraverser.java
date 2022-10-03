package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.identity.KeyRef;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.w3c.dom.Element;

class XSDKeyrefTraverser extends XSDAbstractIDConstraintTraverser
{
    public XSDKeyrefTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
    }
    
    void traverse(final Element element, final XSElementDecl xsElementDecl, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final String s = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAME];
        if (s == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_KEYREF, SchemaSymbols.ATT_NAME }, element);
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return;
        }
        final QName qName = (QName)checkAttributes[XSAttributeChecker.ATTIDX_REFER];
        if (qName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_KEYREF, SchemaSymbols.ATT_REFER }, element);
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return;
        }
        UniqueOrKey uniqueOrKey = null;
        final IdentityConstraint identityConstraint = (IdentityConstraint)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 5, qName, element);
        if (identityConstraint != null) {
            if (identityConstraint.getCategory() == 1 || identityConstraint.getCategory() == 3) {
                uniqueOrKey = (UniqueOrKey)identityConstraint;
            }
            else {
                this.reportSchemaError("src-resolve", new Object[] { qName.rawname, "identity constraint key/unique" }, element);
            }
        }
        if (uniqueOrKey == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return;
        }
        KeyRef keyRef = new KeyRef(xsDocumentInfo.fTargetNamespace, s, uniqueOrKey);
        if (this.traverseIdentityConstraint(keyRef, element, xsDocumentInfo, checkAttributes)) {
            if (uniqueOrKey.getFieldCount() != keyRef.getFieldCount()) {
                this.reportSchemaError("c-props-correct.2", new Object[] { s, uniqueOrKey.getIdentityConstraintName() }, element);
            }
            else {
                if (schemaGrammar.getIDConstraintDecl(keyRef.getIdentityConstraintName()) == null) {
                    schemaGrammar.addIDConstraintDecl(xsElementDecl, keyRef);
                }
                final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
                final IdentityConstraint idConstraintDecl = schemaGrammar.getIDConstraintDecl(keyRef.getIdentityConstraintName(), schemaDocument2SystemId);
                if (idConstraintDecl == null) {
                    schemaGrammar.addIDConstraintDecl(xsElementDecl, keyRef, schemaDocument2SystemId);
                }
                if (this.fSchemaHandler.fTolerateDuplicates) {
                    if (idConstraintDecl != null && idConstraintDecl instanceof KeyRef) {
                        keyRef = (KeyRef)idConstraintDecl;
                    }
                    this.fSchemaHandler.addIDConstraintDecl(keyRef);
                }
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
    }
}
