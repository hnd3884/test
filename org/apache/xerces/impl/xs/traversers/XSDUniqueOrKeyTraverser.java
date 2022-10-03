package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.w3c.dom.Element;

class XSDUniqueOrKeyTraverser extends XSDAbstractIDConstraintTraverser
{
    public XSDUniqueOrKeyTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
    }
    
    void traverse(final Element element, final XSElementDecl xsElementDecl, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final String s = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAME];
        if (s == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { DOMUtil.getLocalName(element), SchemaSymbols.ATT_NAME }, element);
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return;
        }
        UniqueOrKey uniqueOrKey;
        if (DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_UNIQUE)) {
            uniqueOrKey = new UniqueOrKey(xsDocumentInfo.fTargetNamespace, s, (short)3);
        }
        else {
            uniqueOrKey = new UniqueOrKey(xsDocumentInfo.fTargetNamespace, s, (short)1);
        }
        if (this.traverseIdentityConstraint(uniqueOrKey, element, xsDocumentInfo, checkAttributes)) {
            if (schemaGrammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName()) == null) {
                schemaGrammar.addIDConstraintDecl(xsElementDecl, uniqueOrKey);
            }
            final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
            final IdentityConstraint idConstraintDecl = schemaGrammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName(), schemaDocument2SystemId);
            if (idConstraintDecl == null) {
                schemaGrammar.addIDConstraintDecl(xsElementDecl, uniqueOrKey, schemaDocument2SystemId);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (idConstraintDecl != null && idConstraintDecl instanceof UniqueOrKey) {
                    uniqueOrKey = uniqueOrKey;
                }
                this.fSchemaHandler.addIDConstraintDecl(uniqueOrKey);
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
    }
}
