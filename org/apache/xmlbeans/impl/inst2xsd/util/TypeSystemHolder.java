package org.apache.xmlbeans.impl.inst2xsd.util;

import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.DocumentationDocument;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleContentDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleExtensionType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionDocument;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;
import java.math.BigInteger;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalElement;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import java.util.Iterator;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import java.util.Collection;
import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeSystemHolder
{
    Map _globalElements;
    Map _globalAttributes;
    Map _globalTypes;
    
    public TypeSystemHolder() {
        this._globalElements = new LinkedHashMap();
        this._globalAttributes = new LinkedHashMap();
        this._globalTypes = new LinkedHashMap();
    }
    
    public void addGlobalElement(final Element element) {
        assert element.isGlobal() && !element.isRef();
        this._globalElements.put(element.getName(), element);
    }
    
    public Element getGlobalElement(final QName name) {
        return this._globalElements.get(name);
    }
    
    public Element[] getGlobalElements() {
        final Collection col = this._globalElements.values();
        return col.toArray(new Element[col.size()]);
    }
    
    public void addGlobalAttribute(final Attribute attribute) {
        assert attribute.isGlobal() && !attribute.isRef();
        this._globalAttributes.put(attribute.getName(), attribute);
    }
    
    public Attribute getGlobalAttribute(final QName name) {
        return this._globalAttributes.get(name);
    }
    
    public Attribute[] getGlobalAttributes() {
        final Collection col = this._globalAttributes.values();
        return col.toArray(new Attribute[col.size()]);
    }
    
    public void addGlobalType(final Type type) {
        assert type.isGlobal() && type.getName() != null : "type must be a global type before being added.";
        this._globalTypes.put(type.getName(), type);
    }
    
    public Type getGlobalType(final QName name) {
        return this._globalTypes.get(name);
    }
    
    public Type[] getGlobalTypes() {
        final Collection col = this._globalTypes.values();
        return col.toArray(new Type[col.size()]);
    }
    
    public SchemaDocument[] getSchemaDocuments() {
        final Map nsToSchemaDocs = new LinkedHashMap();
        for (final QName globalElemName : this._globalElements.keySet()) {
            final String tns = globalElemName.getNamespaceURI();
            final SchemaDocument schDoc = getSchemaDocumentForTNS(nsToSchemaDocs, tns);
            this.fillUpGlobalElement(this._globalElements.get(globalElemName), schDoc, tns);
        }
        for (final QName globalAttName : this._globalAttributes.keySet()) {
            final String tns = globalAttName.getNamespaceURI();
            final SchemaDocument schDoc = getSchemaDocumentForTNS(nsToSchemaDocs, tns);
            this.fillUpGlobalAttribute(this._globalAttributes.get(globalAttName), schDoc, tns);
        }
        for (final QName globalTypeName : this._globalTypes.keySet()) {
            final String tns = globalTypeName.getNamespaceURI();
            final SchemaDocument schDoc = getSchemaDocumentForTNS(nsToSchemaDocs, tns);
            this.fillUpGlobalType(this._globalTypes.get(globalTypeName), schDoc, tns);
        }
        final Collection schDocColl = nsToSchemaDocs.values();
        return schDocColl.toArray(new SchemaDocument[schDocColl.size()]);
    }
    
    private static SchemaDocument getSchemaDocumentForTNS(final Map nsToSchemaDocs, final String tns) {
        SchemaDocument schDoc = nsToSchemaDocs.get(tns);
        if (schDoc == null) {
            schDoc = SchemaDocument.Factory.newInstance();
            nsToSchemaDocs.put(tns, schDoc);
        }
        return schDoc;
    }
    
    private static SchemaDocument.Schema getTopLevelSchemaElement(final SchemaDocument schDoc, final String tns) {
        SchemaDocument.Schema sch = schDoc.getSchema();
        if (sch == null) {
            sch = schDoc.addNewSchema();
            sch.setAttributeFormDefault(FormChoice.Enum.forString("unqualified"));
            sch.setElementFormDefault(FormChoice.Enum.forString("qualified"));
            if (!tns.equals("")) {
                sch.setTargetNamespace(tns);
            }
        }
        return sch;
    }
    
    private void fillUpGlobalElement(final Element globalElement, final SchemaDocument schDoc, final String tns) {
        assert tns.equals(globalElement.getName().getNamespaceURI());
        final SchemaDocument.Schema sch = getTopLevelSchemaElement(schDoc, tns);
        final TopLevelElement topLevelElem = sch.addNewElement();
        topLevelElem.setName(globalElement.getName().getLocalPart());
        if (globalElement.isNillable()) {
            topLevelElem.setNillable(globalElement.isNillable());
        }
        fillUpElementDocumentation(topLevelElem, globalElement.getComment());
        final Type elemType = globalElement.getType();
        this.fillUpTypeOnElement(elemType, topLevelElem, tns);
    }
    
    protected void fillUpLocalElement(final Element element, final LocalElement localSElement, final String tns) {
        fillUpElementDocumentation(localSElement, element.getComment());
        if (!element.isRef()) {
            assert element.getName().getNamespaceURI().length() == 0;
            this.fillUpTypeOnElement(element.getType(), localSElement, tns);
            localSElement.setName(element.getName().getLocalPart());
        }
        else {
            localSElement.setRef(element.getName());
            assert !element.isNillable();
        }
        if (element.getMaxOccurs() == -1) {
            localSElement.setMaxOccurs("unbounded");
        }
        if (element.getMinOccurs() != 1) {
            localSElement.setMinOccurs(new BigInteger("" + element.getMinOccurs()));
        }
        if (element.isNillable()) {
            localSElement.setNillable(element.isNillable());
        }
    }
    
    private void fillUpTypeOnElement(final Type elemType, final org.apache.xmlbeans.impl.xb.xsdschema.Element parentSElement, final String tns) {
        if (elemType.isGlobal()) {
            assert elemType.getName() != null : "Global type must have a name.";
            parentSElement.setType(elemType.getName());
        }
        else if (elemType.getContentType() == 1) {
            if (elemType.isEnumeration()) {
                this.fillUpEnumeration(elemType, parentSElement);
            }
            else {
                parentSElement.setType(elemType.getName());
            }
        }
        else {
            final LocalComplexType localComplexType = parentSElement.addNewComplexType();
            this.fillUpContentForComplexType(elemType, localComplexType, tns);
        }
    }
    
    private void fillUpEnumeration(final Type type, final org.apache.xmlbeans.impl.xb.xsdschema.Element parentSElement) {
        assert type.isEnumeration() && !type.isComplexType() : "Enumerations must be on simple types only.";
        final RestrictionDocument.Restriction restriction = parentSElement.addNewSimpleType().addNewRestriction();
        restriction.setBase(type.getName());
        if (type.isQNameEnumeration()) {
            for (int i = 0; i < type.getEnumerationQNames().size(); ++i) {
                final QName value = type.getEnumerationQNames().get(i);
                final XmlQName xqname = XmlQName.Factory.newValue(value);
                final NoFixedFacet enumSElem = restriction.addNewEnumeration();
                final XmlCursor xc = enumSElem.newCursor();
                final String newPrefix = xc.prefixForNamespace(value.getNamespaceURI());
                xc.dispose();
                enumSElem.setValue(XmlQName.Factory.newValue(new QName(value.getNamespaceURI(), value.getLocalPart(), newPrefix)));
            }
        }
        else {
            for (int i = 0; i < type.getEnumerationValues().size(); ++i) {
                final String value2 = type.getEnumerationValues().get(i);
                restriction.addNewEnumeration().setValue(XmlString.Factory.newValue(value2));
            }
        }
    }
    
    private void fillUpAttributesInComplexTypesSimpleContent(final Type elemType, final SimpleExtensionType sExtension, final String tns) {
        for (int i = 0; i < elemType.getAttributes().size(); ++i) {
            final Attribute att = elemType.getAttributes().get(i);
            final org.apache.xmlbeans.impl.xb.xsdschema.Attribute sAttribute = sExtension.addNewAttribute();
            this.fillUpLocalAttribute(att, sAttribute, tns);
        }
    }
    
    private void fillUpAttributesInComplexTypesComplexContent(final Type elemType, final ComplexType localSComplexType, final String tns) {
        for (int i = 0; i < elemType.getAttributes().size(); ++i) {
            final Attribute att = elemType.getAttributes().get(i);
            final org.apache.xmlbeans.impl.xb.xsdschema.Attribute sAttribute = localSComplexType.addNewAttribute();
            this.fillUpLocalAttribute(att, sAttribute, tns);
        }
    }
    
    protected void fillUpLocalAttribute(final Attribute att, final org.apache.xmlbeans.impl.xb.xsdschema.Attribute sAttribute, final String tns) {
        if (att.isRef()) {
            sAttribute.setRef(att.getRef().getName());
        }
        else {
            assert !(!att.getName().getNamespaceURI().equals(""));
            sAttribute.setType(att.getType().getName());
            sAttribute.setName(att.getName().getLocalPart());
            if (att.isOptional()) {
                sAttribute.setUse(org.apache.xmlbeans.impl.xb.xsdschema.Attribute.Use.OPTIONAL);
            }
        }
    }
    
    protected void fillUpContentForComplexType(final Type type, final ComplexType sComplexType, final String tns) {
        if (type.getContentType() == 2) {
            final SimpleContentDocument.SimpleContent simpleContent = sComplexType.addNewSimpleContent();
            assert type.getExtensionType() != null && type.getExtensionType().getName() != null : "Extension type must exist and be named for a COMPLEX_TYPE_SIMPLE_CONTENT";
            final SimpleExtensionType ext = simpleContent.addNewExtension();
            ext.setBase(type.getExtensionType().getName());
            this.fillUpAttributesInComplexTypesSimpleContent(type, ext, tns);
        }
        else {
            if (type.getContentType() == 4) {
                sComplexType.setMixed(true);
            }
            ExplicitGroup explicitGroup;
            if (type.getContentType() == 5) {
                explicitGroup = null;
            }
            else if (type.getTopParticleForComplexOrMixedContent() == 1) {
                explicitGroup = sComplexType.addNewSequence();
            }
            else {
                if (type.getTopParticleForComplexOrMixedContent() != 2) {
                    throw new IllegalStateException("Unknown particle type in complex and mixed content");
                }
                explicitGroup = sComplexType.addNewChoice();
                explicitGroup.setMaxOccurs("unbounded");
                explicitGroup.setMinOccurs(new BigInteger("0"));
            }
            for (int i = 0; i < type.getElements().size(); ++i) {
                final Element child = type.getElements().get(i);
                assert !child.isGlobal();
                final LocalElement childLocalElement = explicitGroup.addNewElement();
                this.fillUpLocalElement(child, childLocalElement, tns);
            }
            this.fillUpAttributesInComplexTypesComplexContent(type, sComplexType, tns);
        }
    }
    
    private void fillUpGlobalAttribute(final Attribute globalAttribute, final SchemaDocument schDoc, final String tns) {
        assert tns.equals(globalAttribute.getName().getNamespaceURI());
        final SchemaDocument.Schema sch = getTopLevelSchemaElement(schDoc, tns);
        final TopLevelAttribute topLevelAtt = sch.addNewAttribute();
        topLevelAtt.setName(globalAttribute.getName().getLocalPart());
        final Type elemType = globalAttribute.getType();
        if (elemType.getContentType() == 1) {
            topLevelAtt.setType(elemType.getName());
            return;
        }
        throw new IllegalStateException();
    }
    
    private static void fillUpElementDocumentation(final org.apache.xmlbeans.impl.xb.xsdschema.Element element, final String comment) {
        if (comment != null && comment.length() > 0) {
            final DocumentationDocument.Documentation documentation = element.addNewAnnotation().addNewDocumentation();
            documentation.set(XmlString.Factory.newValue(comment));
        }
    }
    
    private void fillUpGlobalType(final Type globalType, final SchemaDocument schDoc, final String tns) {
        assert tns.equals(globalType.getName().getNamespaceURI());
        final SchemaDocument.Schema sch = getTopLevelSchemaElement(schDoc, tns);
        final TopLevelComplexType topLevelComplexType = sch.addNewComplexType();
        topLevelComplexType.setName(globalType.getName().getLocalPart());
        this.fillUpContentForComplexType(globalType, topLevelComplexType, tns);
    }
    
    @Override
    public String toString() {
        return "TypeSystemHolder{\n\n_globalElements=" + this._globalElements + "\n\n_globalAttributes=" + this._globalAttributes + "\n\n_globalTypes=" + this._globalTypes + "\n}";
    }
}
