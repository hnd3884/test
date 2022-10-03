package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import org.w3c.dom.Element;

class XSDUniqueOrKeyTraverser extends XSDAbstractIDConstraintTraverser
{
    public XSDUniqueOrKeyTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }
    
    void traverse(final Element uElem, final XSElementDecl element, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(uElem, false, schemaDoc);
        final String uName = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        if (uName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { DOMUtil.getLocalName(uElem), SchemaSymbols.ATT_NAME }, uElem);
            this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }
        UniqueOrKey uniqueOrKey = null;
        if (DOMUtil.getLocalName(uElem).equals(SchemaSymbols.ELT_UNIQUE)) {
            uniqueOrKey = new UniqueOrKey(schemaDoc.fTargetNamespace, uName, element.fName, (short)3);
        }
        else {
            uniqueOrKey = new UniqueOrKey(schemaDoc.fTargetNamespace, uName, element.fName, (short)1);
        }
        if (this.traverseIdentityConstraint(uniqueOrKey, uElem, schemaDoc, attrValues)) {
            if (grammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName()) == null) {
                grammar.addIDConstraintDecl(element, uniqueOrKey);
            }
            final String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
            final IdentityConstraint idc = grammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName(), loc);
            if (idc == null) {
                grammar.addIDConstraintDecl(element, uniqueOrKey, loc);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (idc != null && idc instanceof UniqueOrKey) {
                    uniqueOrKey = uniqueOrKey;
                }
                this.fSchemaHandler.addIDConstraintDecl(uniqueOrKey);
            }
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
    }
}
