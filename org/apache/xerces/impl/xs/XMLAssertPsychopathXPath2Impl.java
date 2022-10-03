package org.apache.xerces.impl.xs;

import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import java.util.Iterator;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import java.util.ArrayList;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import java.util.StringTokenizer;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import java.util.Vector;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.impl.xs.assertion.XSAssertImpl;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.impl.xs.assertion.XSAssertConstants;
import java.util.List;
import org.w3c.dom.Attr;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.w3c.dom.Node;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.dom.PSVIDocumentImpl;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.assertion.XSAssert;
import java.util.Map;
import java.util.Stack;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.impl.xs.assertion.XMLAssertAdapter;

public class XMLAssertPsychopathXPath2Impl extends XMLAssertAdapter
{
    private XSModel fSchemaXSmodel;
    private DynamicContext fXpath2DynamicContext;
    private AbstractPsychoPathXPath2Impl fAbstrPsychopathImpl;
    private Document fAssertDocument;
    private Element fCurrentAssertDomNode;
    private Stack fAssertRootStack;
    private Stack fAssertListStack;
    private XMLSchemaValidator fXmlSchemaValidator;
    private Map fAssertParams;
    private XSAssert[] fFailedAssertions;
    private XSTypeDefinition fAssertRootTypeDef;
    
    public XMLAssertPsychopathXPath2Impl(final Map fAssertParams) {
        this.fSchemaXSmodel = null;
        this.fAbstrPsychopathImpl = null;
        this.fAssertDocument = null;
        this.fCurrentAssertDomNode = null;
        this.fAssertRootStack = null;
        this.fAssertListStack = null;
        this.fXmlSchemaValidator = null;
        this.fAssertParams = null;
        this.fFailedAssertions = null;
        this.fAssertRootTypeDef = null;
        this.fAssertDocument = new PSVIDocumentImpl();
        this.fAssertRootStack = new Stack();
        this.fAssertListStack = new Stack();
        this.fAssertParams = fAssertParams;
    }
    
    private void initXPathProcessor() throws Exception {
        this.fXmlSchemaValidator = (XMLSchemaValidator)this.getProperty("http://apache.org/xml/properties/assert/validator");
        this.fAbstrPsychopathImpl = new AbstractPsychoPathXPath2Impl();
        this.fXpath2DynamicContext = this.fAbstrPsychopathImpl.initXPath2DynamicContext(this.fSchemaXSmodel, this.fAssertDocument, this.fAssertParams);
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws Exception {
        if (this.fCurrentAssertDomNode == null) {
            this.fCurrentAssertDomNode = new PSVIElementNSImpl((CoreDocumentImpl)this.fAssertDocument, qName.uri, qName.rawname);
            this.fAssertDocument.appendChild(this.fCurrentAssertDomNode);
        }
        else {
            final PSVIElementNSImpl fCurrentAssertDomNode = new PSVIElementNSImpl((CoreDocumentImpl)this.fAssertDocument, qName.uri, qName.rawname);
            this.fCurrentAssertDomNode.appendChild(fCurrentAssertDomNode);
            this.fCurrentAssertDomNode = fCurrentAssertDomNode;
        }
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            final PSVIAttrNSImpl attributeNode = new PSVIAttrNSImpl((CoreDocumentImpl)this.fAssertDocument, xmlAttributes.getURI(i), xmlAttributes.getQName(i), xmlAttributes.getLocalName(i));
            attributeNode.setNodeValue(xmlAttributes.getValue(i));
            final AttributePSVImpl psvi = (AttributePSVImpl)xmlAttributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
            if (psvi != null) {
                attributeNode.setPSVI(psvi);
            }
            this.fCurrentAssertDomNode.setAttributeNode(attributeNode);
        }
        final List list = (List)augmentations.getItem(XSAssertConstants.assertList);
        if (list != null) {
            this.fAssertRootStack.push(this.fCurrentAssertDomNode);
            this.fAssertListStack.push(list);
            this.initXPathProcessor();
        }
        if (augmentations.getItem(XSAssertConstants.isAttrHaveAsserts)) {
            this.evaluateAssertsFromAttributes(qName, xmlAttributes);
        }
    }
    
    private void evaluateAssertsFromAttributes(final QName qName, final XMLAttributes xmlAttributes) throws Exception {
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            final QName qName2 = new QName();
            xmlAttributes.getName(i, qName2);
            final String value = xmlAttributes.getValue(i);
            final Augmentations augmentations = xmlAttributes.getAugmentations(i);
            final XSSimpleTypeDefinition xsSimpleTypeDefinition = (XSSimpleTypeDefinition)((AttributePSVImpl)augmentations.getItem("ATTRIBUTE_PSVI")).getTypeDefinition();
            if (xsSimpleTypeDefinition != null) {
                final List assertsFromSimpleType = this.fXmlSchemaValidator.getAssertionValidator().getAssertsFromSimpleType(xsSimpleTypeDefinition);
                if (assertsFromSimpleType != null) {
                    final boolean typeDerivedFromSTList = this.isTypeDerivedFromSTList(xsSimpleTypeDefinition);
                    final boolean typeDerivedFromSTUnion = this.isTypeDerivedFromSTUnion(xsSimpleTypeDefinition);
                    for (int j = 0; j < assertsFromSimpleType.size(); ++j) {
                        final XSAssertImpl xsAssertImpl = assertsFromSimpleType.get(j);
                        xsAssertImpl.setAttrName(qName2.localpart);
                        this.evaluateOneAssertionFromSimpleType(qName, value, augmentations, xsSimpleTypeDefinition, typeDerivedFromSTList, typeDerivedFromSTUnion, xsAssertImpl, true, qName2);
                        final XSSimpleTypeDefinition itemType = xsSimpleTypeDefinition.getItemType();
                        if (typeDerivedFromSTList && itemType != null) {
                            this.evaluateAssertsFromItemTypeOfSTList(qName, itemType, value);
                        }
                    }
                }
            }
        }
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws Exception {
        if (this.fCurrentAssertDomNode != null) {
            final ElementPSVI elementPSVI = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
            ((PSVIElementNSImpl)this.fCurrentAssertDomNode).setPSVI(elementPSVI);
            final XSElementDecl xsElementDecl = (XSElementDecl)elementPSVI.getElementDeclaration();
            if (xsElementDecl != null && xsElementDecl.fDefault != null && !this.fCurrentAssertDomNode.hasChildNodes()) {
                this.fCurrentAssertDomNode.appendChild(this.fAssertDocument.createTextNode(xsElementDecl.fDefault.normalizedValue));
            }
            if (!this.fAssertRootStack.empty() && this.fCurrentAssertDomNode == this.fAssertRootStack.peek()) {
                this.fSchemaXSmodel = ((PSVIElementNSImpl)this.fCurrentAssertDomNode).getSchemaInformation();
                this.fAssertRootStack.pop();
                this.processAllAssertionsOnElement(qName, this.fAssertListStack.pop(), augmentations);
                if (this.fFailedAssertions != null && elementPSVI != null) {
                    this.setFailedAssertionsPSVIResult(elementPSVI);
                    this.fFailedAssertions = null;
                }
            }
            if (this.fCurrentAssertDomNode.getParentNode() instanceof Element) {
                this.fCurrentAssertDomNode = (Element)this.fCurrentAssertDomNode.getParentNode();
            }
        }
    }
    
    public void comment(final XMLString xmlString) {
        if (this.fCurrentAssertDomNode != null) {
            this.fCurrentAssertDomNode.appendChild(this.fAssertDocument.createComment(new String(xmlString.ch, xmlString.offset, xmlString.length)));
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString) {
        if (this.fCurrentAssertDomNode != null) {
            this.fCurrentAssertDomNode.appendChild(this.fAssertDocument.createProcessingInstruction(s, new String(xmlString.ch, xmlString.offset, xmlString.length)));
        }
    }
    
    private void setFailedAssertionsPSVIResult(final ElementPSVI elementPSVI) {
        final XSAssert[] array = new XSAssert[this.fFailedAssertions.length];
        System.arraycopy(this.fFailedAssertions, 0, array, 0, this.fFailedAssertions.length);
        ((ElementPSVImpl)elementPSVI).fFailedAssertions = new ObjectListImpl(array, array.length);
    }
    
    private void processAllAssertionsOnElement(final QName qName, final List list, final Augmentations augmentations) throws Exception {
        this.initXPathProcessor();
        final ElementPSVI elementPSVI = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
        final String computeStringValueOf$value = this.computeStringValueOf$value(this.fCurrentAssertDomNode, elementPSVI);
        if (list instanceof XSObjectList) {
            final ElementPSVImpl savePsviInfoWithUntypingOfAssertRoot = this.savePsviInfoWithUntypingOfAssertRoot(elementPSVI, true);
            this.evaluateAssertionsFromAComplexType(qName, list, computeStringValueOf$value, augmentations);
            this.restorePsviInfoForXPathContext(savePsviInfoWithUntypingOfAssertRoot);
        }
        else if (list instanceof Vector) {
            this.evaluateAssertionsFromASimpleType(qName, list, computeStringValueOf$value, augmentations);
        }
    }
    
    private ElementPSVImpl savePsviInfoWithUntypingOfAssertRoot(final ElementPSVI elementPSVI, final boolean b) {
        final ElementPSVImpl psvi = new ElementPSVImpl(true, elementPSVI);
        this.fAssertRootTypeDef = psvi.getTypeDefinition();
        if (b) {
            psvi.fTypeDecl = new SchemaGrammar.XSAnyType();
            ((PSVIElementNSImpl)this.fCurrentAssertDomNode).setPSVI(psvi);
        }
        return psvi;
    }
    
    private void restorePsviInfoForXPathContext(final ElementPSVI elementPSVI) {
        final ElementPSVImpl psvi = (ElementPSVImpl)elementPSVI;
        if (this.fAssertRootTypeDef != null) {
            psvi.fTypeDecl = this.fAssertRootTypeDef;
            ((PSVIElementNSImpl)this.fCurrentAssertDomNode).setPSVI(psvi);
        }
    }
    
    private void evaluateAssertionsFromASimpleType(final QName qName, final List list, final String s, final Augmentations augmentations) throws Exception {
        final XSSimpleTypeDefinition xsSimpleTypeDefinition = (XSSimpleTypeDefinition)((ElementPSVI)augmentations.getItem("ELEMENT_PSVI")).getTypeDefinition();
        final boolean typeDerivedFromSTList = this.isTypeDerivedFromSTList(xsSimpleTypeDefinition);
        final boolean typeDerivedFromSTUnion = this.isTypeDerivedFromSTUnion(xsSimpleTypeDefinition);
        final Vector vector = (Vector)list;
        for (int i = 0; i < vector.size(); ++i) {
            final XSAssertImpl xsAssertImpl = vector.get(i);
            final String xPathDefaultNamespace = xsAssertImpl.getXPathDefaultNamespace();
            if (xPathDefaultNamespace != null) {
                this.fXpath2DynamicContext.add_namespace((String)null, xPathDefaultNamespace);
            }
            this.evaluateOneAssertionFromSimpleType(qName, s, augmentations, xsSimpleTypeDefinition, typeDerivedFromSTList, typeDerivedFromSTUnion, xsAssertImpl, false, null);
        }
        if (typeDerivedFromSTList && xsSimpleTypeDefinition.getItemType() != null) {
            this.evaluateAssertsFromItemTypeOfSTList(qName, xsSimpleTypeDefinition.getItemType(), s);
        }
    }
    
    private void evaluateOneAssertionFromSimpleType(final QName qName, final String s, final Augmentations augmentations, final XSSimpleTypeDefinition xsSimpleTypeDefinition, final boolean b, final boolean b2, final XSAssertImpl xsAssertImpl, final boolean b3, final QName qName2) throws Exception {
        if (xsSimpleTypeDefinition.getVariety() == 1) {
            if (b3) {
                this.setXDMTypedValueOf$value(this.fCurrentAssertDomNode, s, null, xsSimpleTypeDefinition, false, this.fXpath2DynamicContext);
            }
            else {
                this.setXDMTypedValueOf$value(this.fCurrentAssertDomNode, s, null, null, false, this.fXpath2DynamicContext);
            }
            final AssertionError evaluateOneAssertion = this.evaluateOneAssertion(qName, xsAssertImpl, s, false, false);
            if (evaluateOneAssertion != null) {
                this.reportAssertionsError(evaluateOneAssertion);
            }
        }
        else if (xsSimpleTypeDefinition.getVariety() == 2) {
            this.evaluateAssertionOnSTListValue(qName, s, xsAssertImpl, false, xsSimpleTypeDefinition.getItemType(), b);
        }
        else if (!b3 && (boolean)augmentations.getItem(XSAssertConstants.isAssertProcNeededForUnionElem)) {
            if (!this.evaluateAssertionOnSTUnion(qName, xsSimpleTypeDefinition, b2, xsAssertImpl, s, augmentations)) {
                this.fXmlSchemaValidator.reportSchemaError("cvc-type.3.1.3", new Object[] { qName.rawname, s });
            }
        }
        else if (b3 && (boolean)augmentations.getItem(XSAssertConstants.isAssertProcNeededForUnionAttr) && !this.evaluateAssertionOnSTUnion(qName, xsSimpleTypeDefinition, b2, xsAssertImpl, s, augmentations)) {
            this.fXmlSchemaValidator.reportSchemaError("cvc-attribute.3", new Object[] { qName.rawname, qName2.localpart, s, ((XSSimpleTypeDecl)xsSimpleTypeDefinition).getTypeName() });
        }
    }
    
    private void evaluateAssertsFromItemTypeOfSTList(final QName qName, final XSSimpleTypeDefinition xsSimpleTypeDefinition, final String s) throws Exception {
        final Vector assertsFromSimpleType = XS11TypeHelper.getAssertsFromSimpleType(xsSimpleTypeDefinition);
        if (xsSimpleTypeDefinition.getVariety() == 1 && assertsFromSimpleType.size() > 0) {
            for (int i = 0; i < assertsFromSimpleType.size(); ++i) {
                final XSAssertImpl xsAssertImpl = assertsFromSimpleType.get(i);
                final StringTokenizer stringTokenizer = new StringTokenizer(s, " \n\t\r");
                while (stringTokenizer.hasMoreTokens()) {
                    final String nextToken = stringTokenizer.nextToken();
                    this.setXDMTypedValueOf$valueForSTVarietyList(this.fCurrentAssertDomNode, nextToken, xsSimpleTypeDefinition, false, this.fXpath2DynamicContext);
                    final AssertionError evaluateOneAssertion = this.evaluateOneAssertion(qName, xsAssertImpl, nextToken, false, true);
                    if (evaluateOneAssertion != null) {
                        evaluateOneAssertion.setIsTypeDerivedFromList(false);
                        this.reportAssertionsError(evaluateOneAssertion);
                    }
                }
            }
        }
    }
    
    private void evaluateAssertionOnSTListValue(final QName qName, final String s, final XSAssertImpl xsAssertImpl, final boolean b, final XSSimpleTypeDefinition xsSimpleTypeDefinition, final boolean isTypeDerivedFromList) throws Exception {
        if (isTypeDerivedFromList) {
            this.setXDMTypedValueOf$valueForSTVarietyList(this.fCurrentAssertDomNode, s, xsSimpleTypeDefinition, isTypeDerivedFromList, this.fXpath2DynamicContext);
            final AssertionError evaluateOneAssertion = this.evaluateOneAssertion(qName, xsAssertImpl, s, b, true);
            if (evaluateOneAssertion != null) {
                evaluateOneAssertion.setIsTypeDerivedFromList(isTypeDerivedFromList);
                this.reportAssertionsError(evaluateOneAssertion);
            }
        }
        else {
            final StringTokenizer stringTokenizer = new StringTokenizer(s, " \n\t\r");
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                this.setXDMTypedValueOf$valueForSTVarietyList(this.fCurrentAssertDomNode, nextToken, xsSimpleTypeDefinition, isTypeDerivedFromList, this.fXpath2DynamicContext);
                final AssertionError evaluateOneAssertion2 = this.evaluateOneAssertion(qName, xsAssertImpl, nextToken, b, true);
                if (evaluateOneAssertion2 != null) {
                    this.reportAssertionsError(evaluateOneAssertion2);
                }
            }
        }
    }
    
    private boolean evaluateAssertionOnSTUnion(final QName qName, final XSSimpleTypeDefinition xsSimpleTypeDefinition, final boolean b, final XSAssertImpl xsAssertImpl, final String s, final Augmentations augmentations) throws Exception {
        boolean b2 = true;
        final XSObjectList memberTypes = xsSimpleTypeDefinition.getMemberTypes();
        if (memberTypes != null && memberTypes.getLength() > 0 && !b) {
            if (this.isValidationFailedForSTUnion(memberTypes, qName, s, augmentations)) {
                b2 = false;
                if (xsAssertImpl.getAttrName() == null) {
                    this.fXmlSchemaValidator.reportSchemaError("cvc-assertions-valid-union-elem", new Object[] { s, qName.rawname, ((XSSimpleTypeDecl)xsSimpleTypeDefinition).getTypeName() });
                }
                else {
                    this.fXmlSchemaValidator.reportSchemaError("cvc-assertions-valid-union-attr", new Object[] { s, xsAssertImpl.getAttrName(), qName.rawname, ((XSSimpleTypeDecl)xsSimpleTypeDefinition).getTypeName() });
                }
                this.fXmlSchemaValidator.reportSchemaError("cvc-datatype-valid.1.2.3", new Object[] { s, ((XSSimpleTypeDecl)xsSimpleTypeDefinition).getTypeName() });
            }
        }
        else if (b) {
            this.setXDMTypedValueOf$valueForSTVarietyUnion(s, memberTypes, this.fXpath2DynamicContext);
            final AssertionError evaluateOneAssertion = this.evaluateOneAssertion(qName, xsAssertImpl, s, false, false);
            if (evaluateOneAssertion != null) {
                b2 = false;
                this.reportAssertionsError(evaluateOneAssertion);
            }
        }
        return b2;
    }
    
    private void evaluateAssertionsFromAComplexType(final QName qName, final List list, final String s, final Augmentations augmentations) throws Exception {
        final ElementPSVI elementPSVI = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
        if (s != null) {
            this.restorePsviInfoForXPathContext(elementPSVI);
            this.setXDMValueOf$valueForCTWithSimpleContent(s, (XSComplexTypeDefinition)elementPSVI.getTypeDefinition(), this.fXpath2DynamicContext);
            this.savePsviInfoWithUntypingOfAssertRoot(elementPSVI, true);
        }
        else {
            this.fXpath2DynamicContext.set_variable(new org.eclipse.wst.xml.xpath2.processor.internal.types.QName("value"), XS11TypeHelper.getXPath2ResultSequence(new ArrayList()));
        }
        final XSObjectList list2 = (XSObjectList)list;
        for (int i = 0; i < list2.size(); ++i) {
            final XSAssertImpl xsAssertImpl = list2.get(i);
            final String xPathDefaultNamespace = xsAssertImpl.getXPathDefaultNamespace();
            if (xPathDefaultNamespace != null) {
                this.fXpath2DynamicContext.add_namespace((String)null, xPathDefaultNamespace);
            }
            if (xsAssertImpl.getType() == 16) {
                final AssertionError evaluateOneAssertion = this.evaluateOneAssertion(qName, xsAssertImpl, s, true, false);
                if (evaluateOneAssertion != null) {
                    this.reportAssertionsError(evaluateOneAssertion);
                }
            }
            else if (xsAssertImpl.getAttrName() == null) {
                final XSTypeDefinition typeDefinition = xsAssertImpl.getTypeDefinition();
                XSSimpleTypeDefinition simpleType;
                if (typeDefinition instanceof XSComplexTypeDefinition) {
                    simpleType = ((XSComplexTypeDefinition)typeDefinition).getSimpleType();
                }
                else {
                    simpleType = (XSSimpleTypeDefinition)typeDefinition;
                }
                final XSComplexTypeDefinition xsComplexTypeDefinition = (XSComplexTypeDefinition)elementPSVI.getTypeDefinition();
                if (XS11TypeHelper.isComplexTypeDerivedFromSTList(xsComplexTypeDefinition, (short)1)) {
                    simpleType = (XSSimpleTypeDefinition)xsComplexTypeDefinition.getBaseType();
                }
                final boolean typeDerivedFromSTList = this.isTypeDerivedFromSTList(simpleType);
                final boolean typeDerivedFromSTUnion = this.isTypeDerivedFromSTUnion(simpleType);
                this.restorePsviInfoForXPathContext(elementPSVI);
                this.evaluateOneAssertionFromSimpleType(qName, s, augmentations, simpleType, typeDerivedFromSTList, typeDerivedFromSTUnion, xsAssertImpl, false, null);
                this.savePsviInfoWithUntypingOfAssertRoot(elementPSVI, true);
                final XSSimpleTypeDefinition itemType = simpleType.getItemType();
                if (typeDerivedFromSTList && itemType != null) {
                    this.restorePsviInfoForXPathContext(elementPSVI);
                    this.evaluateAssertsFromItemTypeOfSTList(qName, itemType, s);
                    this.savePsviInfoWithUntypingOfAssertRoot(elementPSVI, true);
                }
            }
        }
    }
    
    private AssertionError evaluateOneAssertion(final QName qName, final XSAssertImpl xsAssertImpl, final String s, final boolean b, final boolean b2) {
        AssertionError assertionError = null;
        try {
            final XPath compiledXPathExpr = xsAssertImpl.getCompiledXPathExpr();
            boolean b3;
            if (s == null || b) {
                b3 = this.fAbstrPsychopathImpl.evaluateXPathExpr(compiledXPathExpr, this.fCurrentAssertDomNode);
            }
            else {
                b3 = this.fAbstrPsychopathImpl.evaluateXPathExpr(compiledXPathExpr, null);
            }
            if (!b3) {
                assertionError = new AssertionError("cvc-assertion", qName, xsAssertImpl, s, b2, null);
            }
        }
        catch (final Exception ex) {
            assertionError = new AssertionError("cvc-assertion", qName, xsAssertImpl, s, b2, ex);
        }
        if (assertionError != null && xsAssertImpl.getAttrName() == null) {
            if (this.fFailedAssertions == null) {
                this.fFailedAssertions = new XSAssertImpl[1];
            }
            else {
                final XSAssertImpl[] fFailedAssertions = new XSAssertImpl[this.fFailedAssertions.length + 1];
                System.arraycopy(this.fFailedAssertions, 0, fFailedAssertions, 0, this.fFailedAssertions.length);
                this.fFailedAssertions = fFailedAssertions;
            }
            this.fFailedAssertions[this.fFailedAssertions.length - 1] = xsAssertImpl;
        }
        return assertionError;
    }
    
    private boolean isValidationFailedForSTUnion(final XSObjectList list, final QName qName, final String s, final Augmentations augmentations) {
        boolean b = true;
        for (int i = 0; i < list.getLength(); ++i) {
            final XSSimpleTypeDefinition xsSimpleTypeDefinition = (XSSimpleTypeDefinition)list.item(i);
            if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsSimpleTypeDefinition.getNamespace())) {
                if (!XS11TypeHelper.simpleTypeHasAsserts(xsSimpleTypeDefinition) && XS11TypeHelper.isStrValueValidForASimpleType(s, (XSSimpleType)xsSimpleTypeDefinition, (short)4)) {
                    b = false;
                    break;
                }
                final XSObjectList multiValueFacets = xsSimpleTypeDefinition.getMultiValueFacets();
                for (int j = 0; j < multiValueFacets.getLength(); ++j) {
                    final XSMultiValueFacet xsMultiValueFacet = (XSMultiValueFacet)multiValueFacets.item(j);
                    if (xsMultiValueFacet.getFacetKind() == 16384) {
                        final Vector asserts = xsMultiValueFacet.getAsserts();
                        int n = 0;
                        final Iterator iterator = asserts.iterator();
                        while (iterator.hasNext()) {
                            final XSAssertImpl xsAssertImpl = (XSAssertImpl)iterator.next();
                            try {
                                this.setXDMTypedValueOf$value(this.fCurrentAssertDomNode, s, xsSimpleTypeDefinition, null, false, this.fXpath2DynamicContext);
                                if (this.evaluateOneAssertion(qName, xsAssertImpl, s, false, false) != null) {
                                    continue;
                                }
                                ++n;
                            }
                            catch (final Exception ex) {}
                        }
                        if (n == asserts.size()) {
                            final ItemPSVI itemPSVI = (ItemPSVI)augmentations.getItem("ELEMENT_PSVI");
                            final ItemPSVI itemPSVI2 = (ItemPSVI)augmentations.getItem("ATTRIBUTE_PSVI");
                            if (itemPSVI != null) {
                                ((ElementPSVImpl)itemPSVI).fValue.memberType = (XSSimpleType)xsSimpleTypeDefinition;
                            }
                            else {
                                ((AttributePSVImpl)itemPSVI2).fValue.memberType = (XSSimpleType)xsSimpleTypeDefinition;
                            }
                            b = false;
                            break;
                        }
                    }
                }
                if (!b) {
                    break;
                }
            }
        }
        return b;
    }
    
    public void characters(final XMLString xmlString) {
        if (this.fCurrentAssertDomNode != null) {
            this.fCurrentAssertDomNode.appendChild(this.fAssertDocument.createTextNode(new String(xmlString.ch, xmlString.offset, xmlString.length)));
        }
    }
    
    private void reportAssertionsError(final AssertionError assertionError) {
        final String errorCode = assertionError.getErrorCode();
        final QName element = assertionError.getElement();
        final XSAssertImpl assertion = assertionError.getAssertion();
        final boolean list = assertionError.isList();
        final String value = assertionError.getValue();
        String s = "";
        final Exception exception = assertionError.getException();
        if (exception instanceof DynamicError) {
            s = ((DynamicError)exception).code() + " : " + ((DynamicError)exception).getMessage();
        }
        else if (exception instanceof StaticError) {
            s = ((StaticError)exception).code() + " : " + ((StaticError)exception).getMessage();
        }
        if (!"".equals(s) && !s.endsWith(".")) {
            s += ".";
        }
        final String schemaTypeName = XS11TypeHelper.getSchemaTypeName(assertion.getTypeDefinition());
        String s2 = element.rawname;
        if (assertion.getAttrName() != null) {
            s2 = element.rawname + " (attribute => " + assertion.getAttrName() + ")";
        }
        String s3 = "";
        if (list) {
            if (assertionError.getIsTypeDerivedFromList()) {
                s3 = "Assertion failed for xs:list instance '" + value + "'.";
            }
            else {
                s3 = "Assertion failed for an xs:list member value '" + value + "'.";
            }
        }
        final String s4 = "".equals(s3) ? s : (s3 + ("".equals(s) ? "" : (" " + s)));
        String s5 = assertion.getMessage();
        if (s5 != null) {
            if (value != null && !"".equals(value)) {
                s5 = s5.replaceAll("\\{\\$value\\}", value);
            }
            if (!s5.endsWith(".")) {
                s5 += ".";
            }
            this.fXmlSchemaValidator.reportSchemaError("cvc-assertion-failure-mesg", new Object[] { "Assertion failed for schema type '" + schemaTypeName + "'. " + s5, s4 });
        }
        else if (assertion.getAssertKind() == 16) {
            this.fXmlSchemaValidator.reportSchemaError(errorCode, new Object[] { s2, assertion.getTest().getXPathStr(), schemaTypeName, s4 });
        }
        else {
            this.fXmlSchemaValidator.reportSchemaError("cvc-assertions-valid", new Object[] { value, assertion.getTest().getXPathStr(), s });
            this.fXmlSchemaValidator.reportSchemaError(errorCode, new Object[] { s2, assertion.getTest().getXPathStr(), schemaTypeName, s4 });
        }
    }
    
    private boolean isTypeDerivedFromSTList(final XSSimpleTypeDefinition xsSimpleTypeDefinition) {
        return ((XSSimpleType)xsSimpleTypeDefinition.getBaseType()).getVariety() == 2;
    }
    
    private boolean isTypeDerivedFromSTUnion(final XSSimpleTypeDefinition xsSimpleTypeDefinition) {
        return ((XSSimpleType)xsSimpleTypeDefinition.getBaseType()).getVariety() == 3;
    }
    
    final class AssertionError
    {
        private final String errorCode;
        private final QName element;
        private final XSAssertImpl assertImpl;
        private final String value;
        private final boolean isList;
        private boolean isTypeDerivedFromList;
        Exception ex;
        
        public AssertionError(final String errorCode, final QName element, final XSAssertImpl assertImpl, final String value, final boolean isList, final Exception ex) {
            this.isTypeDerivedFromList = false;
            this.ex = null;
            this.errorCode = errorCode;
            this.element = element;
            this.assertImpl = assertImpl;
            this.value = value;
            this.isList = isList;
            this.ex = ex;
        }
        
        public void setIsTypeDerivedFromList(final boolean isTypeDerivedFromList) {
            this.isTypeDerivedFromList = isTypeDerivedFromList;
        }
        
        public String getErrorCode() {
            return this.errorCode;
        }
        
        public QName getElement() {
            return this.element;
        }
        
        public XSAssertImpl getAssertion() {
            return this.assertImpl;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public boolean isList() {
            return this.isList;
        }
        
        public boolean getIsTypeDerivedFromList() {
            return this.isTypeDerivedFromList;
        }
        
        public Exception getException() {
            return this.ex;
        }
    }
}
