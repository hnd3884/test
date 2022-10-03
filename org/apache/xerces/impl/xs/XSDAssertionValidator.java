package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.util.XMLAttributesImpl;
import java.util.Collection;
import org.apache.xerces.xs.XSMultiValueFacet;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.assertion.XSAssert;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.util.NamespaceSupport;
import java.util.List;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.impl.xs.assertion.XSAssertConstants;
import org.apache.xerces.util.AugmentationsImpl;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import org.apache.xerces.impl.xs.assertion.XSAssertImpl;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.impl.xs.assertion.XMLAssertHandler;

public class XSDAssertionValidator
{
    XMLSchemaValidator fXmlSchemaValidator;
    XMLAssertHandler fAssertionProcessor;
    boolean fAttributesHaveAsserts;
    
    public XSDAssertionValidator(final XMLSchemaValidator fXmlSchemaValidator) {
        this.fXmlSchemaValidator = null;
        this.fAssertionProcessor = null;
        this.fAttributesHaveAsserts = false;
        this.fXmlSchemaValidator = fXmlSchemaValidator;
    }
    
    public void characterDataHandler(final XMLString xmlString) {
        if (this.fAssertionProcessor != null) {
            this.fAssertionProcessor.characters(xmlString);
        }
    }
    
    public void handleStartElement(final QName qName, final XMLAttributes xmlAttributes) throws Exception {
        final List assertsForEvaluation = this.getAssertsForEvaluation(qName, xmlAttributes);
        if (assertsForEvaluation != null && this.fAssertionProcessor == null) {
            NamespaceSupport namespaceSupport;
            if (assertsForEvaluation instanceof XSObjectList) {
                namespaceSupport = ((XSAssertImpl)((XSObjectList)assertsForEvaluation).item(0)).getXPath2NamespaceContext();
            }
            else {
                namespaceSupport = ((XSAssertImpl)((Vector)assertsForEvaluation).get(0)).getXPath2NamespaceContext();
            }
            final HashMap hashMap = new HashMap();
            hashMap.put("XPATH2_NS_CONTEXT", namespaceSupport);
            this.initializeAssertProcessor(hashMap);
        }
        if (this.fAssertionProcessor != null) {
            final AugmentationsImpl augmentationsImpl = new AugmentationsImpl();
            augmentationsImpl.putItem(XSAssertConstants.assertList, assertsForEvaluation);
            augmentationsImpl.putItem(XSAssertConstants.isAttrHaveAsserts, this.fAttributesHaveAsserts);
            this.fAttributesHaveAsserts = false;
            this.fAssertionProcessor.startElement(qName, xmlAttributes, augmentationsImpl);
        }
    }
    
    public void handleEndElement(final QName qName, final Augmentations augmentations) {
        if (this.fAssertionProcessor != null) {
            try {
                this.fAssertionProcessor.endElement(qName, augmentations);
            }
            catch (final Exception ex) {
                throw new XNIException(ex.getMessage(), ex);
            }
        }
    }
    
    public void comment(final XMLString xmlString) {
        if (this.fAssertionProcessor != null) {
            this.fAssertionProcessor.comment(xmlString);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString) {
        if (this.fAssertionProcessor != null) {
            this.fAssertionProcessor.processingInstruction(s, xmlString);
        }
    }
    
    private List getAssertsForEvaluation(final QName qName, final XMLAttributes xmlAttributes) {
        List assertsFromSimpleType = null;
        final XSTypeDefinition typeDefinition = this.fXmlSchemaValidator.fCurrentPSVI.getTypeDefinition();
        if (typeDefinition != null) {
            if (typeDefinition.getTypeCategory() == 15) {
                final XSObjectListImpl assertsFromComplexType = this.getAssertsFromComplexType((XSComplexTypeDefinition)typeDefinition, xmlAttributes);
                if (assertsFromComplexType.size() > 0) {
                    assertsFromSimpleType = assertsFromComplexType;
                }
            }
            else {
                assertsFromSimpleType = this.getAssertsFromSimpleType((XSSimpleTypeDefinition)typeDefinition);
            }
        }
        return assertsFromSimpleType;
    }
    
    private XSObjectListImpl getAssertsFromComplexType(final XSComplexTypeDefinition xsComplexTypeDefinition, final XMLAttributes xmlAttributes) {
        final XSObjectListImpl xsObjectListImpl = new XSObjectListImpl();
        final XSObjectList assertions = xsComplexTypeDefinition.getAssertions();
        if (assertions.getLength() > 0) {
            for (int i = 0; i < assertions.getLength(); ++i) {
                xsObjectListImpl.addXSObject((XSObject)assertions.get(i));
            }
        }
        final XSSimpleTypeDefinition simpleType = xsComplexTypeDefinition.getSimpleType();
        if (simpleType != null) {
            if (xsComplexTypeDefinition.getDerivationMethod() == 2) {
                final Vector assertsFromSimpleType = XS11TypeHelper.getAssertsFromSimpleType(simpleType);
                for (int j = 0; j < assertsFromSimpleType.size(); ++j) {
                    xsObjectListImpl.addXSObject((XSObject)assertsFromSimpleType.get(j));
                }
            }
            else if (XS11TypeHelper.isComplexTypeDerivedFromSTList(xsComplexTypeDefinition, (short)1)) {
                final Vector assertsFromSimpleType2 = XS11TypeHelper.getAssertsFromSimpleType(((XSSimpleTypeDefinition)xsComplexTypeDefinition.getBaseType()).getItemType());
                for (int k = 0; k < assertsFromSimpleType2.size(); ++k) {
                    xsObjectListImpl.addXSObject((XSObject)assertsFromSimpleType2.get(k));
                }
            }
        }
        final XSObjectListImpl assertsFromAttributes = this.getAssertsFromAttributes(xmlAttributes);
        final int length = assertsFromAttributes.getLength();
        for (int l = 0; l < length; ++l) {
            xsObjectListImpl.addXSObject(assertsFromAttributes.item(l));
        }
        if (length > 0) {
            this.fAttributesHaveAsserts = true;
        }
        return xsObjectListImpl;
    }
    
    private XSObjectListImpl getAssertsFromAttributes(final XMLAttributes xmlAttributes) {
        final XSObjectListImpl xsObjectListImpl = new XSObjectListImpl();
        final ArrayList list = new ArrayList();
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            final XSSimpleTypeDefinition xsSimpleTypeDefinition = (XSSimpleTypeDefinition)((AttributePSVImpl)xmlAttributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI")).getTypeDefinition();
            if (xsSimpleTypeDefinition != null && !XS11TypeHelper.isListContainsType(list, xsSimpleTypeDefinition)) {
                list.add(xsSimpleTypeDefinition);
                final List assertsFromSimpleType = this.getAssertsFromSimpleType(xsSimpleTypeDefinition);
                if (assertsFromSimpleType != null) {
                    final Iterator iterator = assertsFromSimpleType.iterator();
                    while (iterator.hasNext()) {
                        xsObjectListImpl.addXSObject((XSObject)iterator.next());
                    }
                }
            }
        }
        return xsObjectListImpl;
    }
    
    public List getAssertsFromSimpleType(final XSSimpleTypeDefinition xsSimpleTypeDefinition) {
        List<XSAssertImpl> list = null;
        XSObjectListImpl xsObjectListImpl = (XSObjectListImpl)xsSimpleTypeDefinition.getMultiValueFacets();
        if (xsObjectListImpl.getLength() == 0 && xsSimpleTypeDefinition.getItemType() != null) {
            xsObjectListImpl = (XSObjectListImpl)xsSimpleTypeDefinition.getItemType().getMultiValueFacets();
        }
        else if (xsSimpleTypeDefinition.getVariety() == 3) {
            final XSAssertImpl firstAssertFromUnionMemberTypes = this.getFirstAssertFromUnionMemberTypes(xsSimpleTypeDefinition.getMemberTypes());
            if (firstAssertFromUnionMemberTypes != null) {
                list = new Vector<XSAssertImpl>();
                list.add(firstAssertFromUnionMemberTypes);
            }
        }
        for (int i = 0; i < xsObjectListImpl.getLength(); ++i) {
            final XSMultiValueFacet xsMultiValueFacet = (XSMultiValueFacet)xsObjectListImpl.item(i);
            if (xsMultiValueFacet.getFacetKind() == 16384) {
                if (list == null) {
                    list = new Vector<XSAssertImpl>();
                }
                list.addAll(xsMultiValueFacet.getAsserts());
            }
        }
        return list;
    }
    
    private XSAssertImpl getFirstAssertFromUnionMemberTypes(final XSObjectList list) {
        XSAssertImpl xsAssertImpl = null;
        for (int i = 0; i < list.getLength(); ++i) {
            final XSSimpleTypeDefinition xsSimpleTypeDefinition = (XSSimpleTypeDefinition)list.item(i);
            if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsSimpleTypeDefinition.getNamespace())) {
                final Vector assertsFromSimpleType = XS11TypeHelper.getAssertsFromSimpleType(xsSimpleTypeDefinition);
                if (!assertsFromSimpleType.isEmpty()) {
                    xsAssertImpl = (XSAssertImpl)assertsFromSimpleType.get(0);
                    break;
                }
            }
        }
        return xsAssertImpl;
    }
    
    private void initializeAssertProcessor(final Map map) {
        String systemProperty;
        try {
            systemProperty = SecuritySupport.getSystemProperty("org.apache.xerces.assertProcessor");
        }
        catch (final SecurityException ex) {
            systemProperty = null;
        }
        if (systemProperty == null || systemProperty.length() == 0) {
            this.fAssertionProcessor = new XMLAssertPsychopathXPath2Impl(map);
        }
        else {
            try {
                this.fAssertionProcessor = (XMLAssertHandler)ObjectFactory.findProviderClass(systemProperty, ObjectFactory.findClassLoader(), true).newInstance();
            }
            catch (final ClassNotFoundException ex2) {
                throw new XNIException(ex2.getMessage(), ex2);
            }
            catch (final InstantiationException ex3) {
                throw new XNIException(ex3.getMessage(), ex3);
            }
            catch (final IllegalAccessException ex4) {
                throw new XNIException(ex4.getMessage(), ex4);
            }
        }
        this.fAssertionProcessor.setProperty("http://apache.org/xml/properties/assert/validator", this.fXmlSchemaValidator);
    }
    
    void extraCheckForSTUnionAssertsAttrs(final XMLAttributes xmlAttributes) {
        final XMLAttributesImpl xmlAttributesImpl = (XMLAttributesImpl)xmlAttributes;
        for (int i = 0; i < xmlAttributesImpl.getLength(); ++i) {
            final AttributePSVImpl attributePSVImpl = (AttributePSVImpl)xmlAttributesImpl.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
            final XSSimpleTypeDefinition xsSimpleTypeDefinition = (XSSimpleTypeDefinition)attributePSVImpl.getTypeDefinition();
            final List isAssertProcessingNeededForSTUnionAttrs = this.fXmlSchemaValidator.getIsAssertProcessingNeededForSTUnionAttrs();
            if (xsSimpleTypeDefinition != null && xsSimpleTypeDefinition.getVariety() == 3 && ((XSSimpleType)xsSimpleTypeDefinition.getBaseType()).getVariety() != 3) {
                if (XS11TypeHelper.isAtomicStrValueValidForSTUnion(xsSimpleTypeDefinition.getMemberTypes(), xmlAttributesImpl.getValue(i), attributePSVImpl.fValue, (short)4)) {
                    isAssertProcessingNeededForSTUnionAttrs.add(false);
                }
                else {
                    isAssertProcessingNeededForSTUnionAttrs.add(true);
                }
            }
            else {
                isAssertProcessingNeededForSTUnionAttrs.add(true);
            }
        }
    }
    
    void extraCheckForSTUnionAssertsElem(final XSSimpleType xsSimpleType, final String s, final ValidatedInfo validatedInfo) {
        if (xsSimpleType.getVariety() == 3 && ((XSSimpleType)xsSimpleType.getBaseType()).getVariety() != 3 && XS11TypeHelper.isAtomicStrValueValidForSTUnion(xsSimpleType.getMemberTypes(), s, validatedInfo, (short)4)) {
            this.fXmlSchemaValidator.setIsAssertProcessingNeededForSTUnionElem(false);
        }
    }
}
