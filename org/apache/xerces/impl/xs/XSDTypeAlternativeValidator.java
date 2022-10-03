package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.impl.xs.alternative.Test;
import org.apache.xerces.impl.xs.alternative.XSTypeAlternativeImpl;
import org.apache.xerces.xs.XSTypeAlternative;
import org.apache.xerces.xni.NamespaceContext;
import java.util.Vector;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

public class XSDTypeAlternativeValidator
{
    XMLSchemaValidator fXmlSchemaValidator;
    
    public XSDTypeAlternativeValidator(final XMLSchemaValidator fXmlSchemaValidator) {
        this.fXmlSchemaValidator = null;
        this.fXmlSchemaValidator = fXmlSchemaValidator;
    }
    
    public XSTypeAlternative getTypeAlternative(final XSElementDecl xsElementDecl, final QName qName, final XMLAttributes xmlAttributes, final Vector vector, final NamespaceContext namespaceContext, final String s) {
        XSTypeAlternativeImpl defaultTypeDefinition = null;
        final XSTypeAlternativeImpl[] typeAlternatives = xsElementDecl.getTypeAlternatives();
        if (typeAlternatives != null) {
            final XMLAttributes attributesForCTA = this.getAttributesForCTA(xmlAttributes, vector);
            for (int i = 0; i < typeAlternatives.length; ++i) {
                final Test test = typeAlternatives[i].getTest();
                if (test != null && test.evaluateTest(qName, attributesForCTA, namespaceContext, s)) {
                    defaultTypeDefinition = typeAlternatives[i];
                    break;
                }
            }
            if (defaultTypeDefinition == null) {
                defaultTypeDefinition = xsElementDecl.getDefaultTypeDefinition();
            }
        }
        return defaultTypeDefinition;
    }
    
    private XMLAttributes getAttributesForCTA(final XMLAttributes xmlAttributes, final Vector vector) {
        final XMLAttributesImpl xmlAttributesImpl = new XMLAttributesImpl();
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            final QName qName = new QName();
            xmlAttributes.getName(i, qName);
            xmlAttributesImpl.addAttribute(qName, xmlAttributes.getType(i), xmlAttributes.getValue(i));
        }
        for (int j = vector.size() - 1; j > -1; --j) {
            final AttributePSVI attributePSVI = vector.elementAt(j);
            final XSAttributeDeclaration attributeDeclaration = attributePSVI.getAttributeDeclaration();
            if (!this.isInheritedAttributeOverridden(xmlAttributesImpl, attributeDeclaration)) {
                final QName qName2 = new QName();
                qName2.setValues(null, attributeDeclaration.getName(), attributeDeclaration.getName(), attributeDeclaration.getNamespace());
                xmlAttributesImpl.addAttribute(qName2, null, attributePSVI.getSchemaValue().getNormalizedValue());
            }
        }
        return xmlAttributesImpl;
    }
    
    private boolean isInheritedAttributeOverridden(final XMLAttributes xmlAttributes, final XSAttributeDeclaration xsAttributeDeclaration) {
        boolean b = false;
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            if (xmlAttributes.getLocalName(i).equals(xsAttributeDeclaration.getName()) && XS11TypeHelper.isURIEqual(xmlAttributes.getURI(i), xsAttributeDeclaration.getNamespace())) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    void saveInheritableAttributes(final XSElementDecl xsElementDecl, final XMLAttributes xmlAttributes) {
        if (xsElementDecl != null && xsElementDecl.fType instanceof XSComplexTypeDecl) {
            final XSObjectListImpl xsObjectListImpl = (XSObjectListImpl)((XSComplexTypeDecl)xsElementDecl.fType).getAttributeUses();
            for (int i = 0; i < xsObjectListImpl.getLength(); ++i) {
                final XSAttributeUse xsAttributeUse = (XSAttributeUse)xsObjectListImpl.get(i);
                if (xsAttributeUse.getInheritable()) {
                    final XSAttributeDeclaration attrDeclaration = xsAttributeUse.getAttrDeclaration();
                    final Augmentations augmentations = xmlAttributes.getAugmentations(attrDeclaration.getNamespace(), attrDeclaration.getName());
                    if (augmentations != null) {
                        this.fXmlSchemaValidator.getInheritableAttrList().add(augmentations.getItem("ATTRIBUTE_PSVI"));
                    }
                }
            }
        }
    }
    
    ObjectList getInheritedAttributesForPSVI() {
        ObjectList list = null;
        final Vector inheritableAttrList = this.fXmlSchemaValidator.getInheritableAttrList();
        if (inheritableAttrList.size() > 0) {
            final Object[] array = new Object[inheritableAttrList.size()];
            for (int i = 0; i < inheritableAttrList.size(); ++i) {
                array[i] = inheritableAttrList.get(i);
            }
            list = new ObjectListImpl(array, array.length);
        }
        return list;
    }
}
