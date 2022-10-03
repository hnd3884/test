package org.apache.xerces.impl.xs.traversers;

import org.eclipse.wst.xml.xpath2.processor.StaticChecker;
import org.eclipse.wst.xml.xpath2.processor.XPathParser;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.apache.xerces.util.XMLSymbols;
import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.apache.xerces.impl.xpath.XPathException;
import org.apache.xerces.impl.xpath.XPath20;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.impl.xs.alternative.Test;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.XPathNode;
import org.eclipse.wst.xml.xpath2.processor.StaticContext;
import org.eclipse.wst.xml.xpath2.processor.StaticNameResolver;
import java.util.Map;
import org.w3c.dom.Document;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.impl.xs.AbstractPsychoPathXPath2Impl;
import java.util.HashMap;
import org.eclipse.wst.xml.xpath2.processor.JFlexCupParser;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.xs.alternative.XSTypeAlternativeImpl;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.w3c.dom.Element;
import org.apache.xerces.impl.dv.XSSimpleType;

class XSDTypeAlternativeTraverser extends XSDAbstractTraverser
{
    private static final XSSimpleType fErrorType;
    private boolean fIsFullXPathModeForCTA;
    private String[] fctaXPathModes;
    
    XSDTypeAlternativeTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
        this.fctaXPathModes = new String[] { "cta-subset", "cta-full" };
        this.fIsFullXPathModeForCTA = xsdHandler.fFullXPathForCTA;
    }
    
    public void traverse(final Element element, final XSElementDecl xsElementDecl, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final QName qName = (QName)checkAttributes[XSAttributeChecker.ATTIDX_TYPE];
        final String s = (String)checkAttributes[XSAttributeChecker.ATTIDX_XPATH];
        String xPathDefauleNamespace = (String)checkAttributes[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS];
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject xsObject = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xsObject = this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                xsObject = this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo);
            }
        }
        XSObjectListImpl empty_LIST;
        if (xsObject != null) {
            empty_LIST = new XSObjectListImpl();
            empty_LIST.addXSObject(xsObject);
        }
        else {
            empty_LIST = XSObjectListImpl.EMPTY_LIST;
        }
        Object o = null;
        boolean b = false;
        if (qName != null) {
            o = this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 7, qName, element);
        }
        if (element2 != null) {
            final String localName = DOMUtil.getLocalName(element2);
            Object o2 = null;
            if (localName.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                o2 = this.fSchemaHandler.fComplexTypeTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar, xsElementDecl);
                b = true;
                element2 = DOMUtil.getNextSiblingElement(element2);
            }
            else if (localName.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                o2 = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar, xsElementDecl);
                b = true;
                element2 = DOMUtil.getNextSiblingElement(element2);
            }
            if (o == null) {
                o = o2;
            }
            if (b && qName != null) {
                this.reportSchemaError("src-type-alternative.3.12.13.1", null, element);
            }
        }
        if (qName == null && !b) {
            this.reportSchemaError("src-type-alternative.3.12.13.2", null, element);
        }
        if (o == null) {
            o = xsElementDecl.fType;
        }
        else if (o != XSDTypeAlternativeTraverser.fErrorType) {
            short fBlock = xsElementDecl.fBlock;
            if (xsElementDecl.fType.getTypeCategory() == 15) {
                fBlock |= ((XSComplexTypeDecl)xsElementDecl.fType).getProhibitedSubstitutions();
            }
            if (!this.fSchemaHandler.fXSConstraints.checkTypeDerivationOk((XSTypeDefinition)o, xsElementDecl.fType, fBlock)) {
                this.reportSchemaError("e-props-correct.7", new Object[] { xsElementDecl.getName(), XS11TypeHelper.getSchemaTypeName((XSTypeDefinition)o), XS11TypeHelper.getSchemaTypeName(xsElementDecl.fType) }, element);
                o = xsElementDecl.fType;
            }
        }
        if (element2 != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "type alternative", "(annotation?, (simpleType | complexType)?)", DOMUtil.getLocalName(element2) }, element2);
        }
        final XSTypeAlternativeImpl xsTypeAlternativeImpl = new XSTypeAlternativeImpl(xsElementDecl.fName, (XSTypeDefinition)o, empty_LIST);
        if (s != null) {
            Test test;
            try {
                if (this.fIsFullXPathModeForCTA) {
                    final XPath parse = ((XPathParser)new JFlexCupParser()).parse("boolean(" + s + ")", true);
                    final HashMap hashMap = new HashMap();
                    hashMap.put("XPATH2_NS_CONTEXT", xsDocumentInfo.fNamespaceSupport);
                    ((StaticChecker)new StaticNameResolver((StaticContext)new AbstractPsychoPathXPath2Impl().initXPath2DynamicContext(null, null, hashMap))).check((XPathNode)parse);
                    test = new Test(parse, s, xsTypeAlternativeImpl, xsDocumentInfo.fNamespaceSupport);
                }
                else {
                    test = new Test(new XPath20(s, this.fSymbolTable, new NamespaceSupport(xsDocumentInfo.fNamespaceSupport)), xsTypeAlternativeImpl, new NamespaceSupport(xsDocumentInfo.fNamespaceSupport));
                }
            }
            catch (final XPathException ex) {
                test = new Test(null, xsTypeAlternativeImpl, new NamespaceSupport(xsDocumentInfo.fNamespaceSupport));
                this.reportSchemaError("c-cta-xpath", new Object[] { s, this.fctaXPathModes[0] }, element);
            }
            catch (final XPathParserException ex2) {
                test = new Test(null, xsTypeAlternativeImpl, new NamespaceSupport(xsDocumentInfo.fNamespaceSupport));
                if ("Expression starts with / or //".equals(ex2.getMessage())) {
                    this.fSchemaHandler.reportSchemaWarning("c-cta-xpath-b", new Object[] { s, this.fctaXPathModes[1] }, element);
                }
                else {
                    this.reportSchemaError("c-cta-xpath", new Object[] { s, this.fctaXPathModes[1] }, element);
                }
            }
            catch (final StaticError staticError) {
                test = new Test(null, xsTypeAlternativeImpl, new NamespaceSupport(xsDocumentInfo.fNamespaceSupport));
                this.reportSchemaError("c-cta-xpath-serr", new Object[] { s, this.fctaXPathModes[1], staticError.code() }, element);
            }
            xsTypeAlternativeImpl.setTest(test);
        }
        else {
            xsTypeAlternativeImpl.setNamespaceContext(new NamespaceSupport(xsDocumentInfo.fNamespaceSupport));
        }
        final String documentURI = this.fSchemaHandler.getDocumentURI(element);
        if (documentURI != null) {
            xsTypeAlternativeImpl.setBaseURI(documentURI);
        }
        if (xPathDefauleNamespace == null) {
            if (xsDocumentInfo.fXpathDefaultNamespaceIs2PoundDefault) {
                xPathDefauleNamespace = xsDocumentInfo.fValidationContext.getURI(XMLSymbols.EMPTY_STRING);
                if (xPathDefauleNamespace != null) {
                    xPathDefauleNamespace = this.fSymbolTable.addSymbol(xPathDefauleNamespace);
                }
            }
            else {
                xPathDefauleNamespace = xsDocumentInfo.fXpathDefaultNamespace;
            }
        }
        if (xPathDefauleNamespace != null) {
            xsTypeAlternativeImpl.setXPathDefauleNamespace(xPathDefauleNamespace);
        }
        schemaGrammar.addTypeAlternative(xsElementDecl, xsTypeAlternativeImpl);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
    }
    
    static {
        fErrorType = (XSSimpleType)SchemaGrammar.getS4SGrammar((short)4).getGlobalTypeDecl("error");
    }
}
