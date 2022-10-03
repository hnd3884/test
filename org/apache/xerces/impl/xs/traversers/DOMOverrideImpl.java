package org.apache.xerces.impl.xs.traversers;

import org.w3c.dom.DocumentType;
import java.util.Iterator;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.util.HashMap;
import java.util.ArrayList;

public final class DOMOverrideImpl extends OverrideTransformer
{
    private final ArrayList fOverrideComponents;
    private final HashMap[] fOverrideComponentsMap;
    private Document fOverridenDoc;
    private Element fOverrideElem;
    private boolean hasPerformedTransformations;
    private DOMImplementation fDOMImpl;
    private final XSDHandler fSchemaHandler;
    
    public DOMOverrideImpl(final XSDHandler fSchemaHandler) {
        this.fOverrideComponents = new ArrayList();
        this.fOverrideComponentsMap = new HashMap[] { null, new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap() };
        this.hasPerformedTransformations = false;
        this.fSchemaHandler = fSchemaHandler;
        try {
            this.fDOMImpl = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
        }
        catch (final ClassCastException ex) {
            ex.printStackTrace();
        }
        catch (final ClassNotFoundException ex2) {
            ex2.printStackTrace();
        }
        catch (final InstantiationException ex3) {
            ex3.printStackTrace();
        }
        catch (final IllegalAccessException ex4) {
            ex4.printStackTrace();
        }
    }
    
    public void clearState() {
        this.fOverrideComponents.clear();
        for (int i = 1; i < this.fOverrideComponentsMap.length; ++i) {
            this.fOverrideComponentsMap[i].clear();
        }
        this.fOverridenDoc = null;
        this.fOverrideElem = null;
        this.hasPerformedTransformations = false;
    }
    
    public Element transform(final Element fOverrideElem, final Element element) throws OverrideTransformException {
        this.fOverridenDoc = this.cloneOverridenSchema(element);
        final Element documentElement = this.fOverridenDoc.getDocumentElement();
        this.fillOverrideElementMap(this.fOverrideElem = fOverrideElem);
        this.transform(documentElement, false);
        if (this.hasOverrideTransformations()) {
            this.clearState();
            return documentElement;
        }
        this.clearState();
        return null;
    }
    
    public boolean hasOverrideTransformations() {
        return this.hasPerformedTransformations;
    }
    
    private void transform(final Element element, final boolean b) {
        for (Element element2 = DOMUtil.getFirstChildElement(element); element2 != null; element2 = DOMUtil.getNextSiblingElement(element2)) {
            final String localName = this.getLocalName(element2);
            if (!localName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                if (localName.equals(SchemaSymbols.ELT_INCLUDE)) {
                    final Element fOverrideElem = this.fOverrideElem;
                    final String attribute = element2.getAttribute(SchemaSymbols.ATT_SCHEMALOCATION);
                    element2 = this.performDOMOverride(element, fOverrideElem, element2);
                    element2.setAttribute(SchemaSymbols.ATT_SCHEMALOCATION, attribute);
                    this.hasPerformedTransformations = true;
                }
                else if (localName.equals(SchemaSymbols.ELT_REDEFINE)) {
                    this.transform(element2, false);
                }
                else if (localName.equals(SchemaSymbols.ELT_OVERRIDE)) {
                    this.transform(element2, true);
                    this.mergeOverride(element2);
                }
                else {
                    final String attrValue = DOMUtil.getAttrValue(element2, SchemaSymbols.ATT_NAME);
                    if (attrValue.length() != 0) {
                        final int overrideType = this.getOverrideType(localName);
                        if (overrideType != 0) {
                            final OverrideElement matchingOverrideElement = this.getMatchingOverrideElement(overrideType, attrValue);
                            final Element element3 = element2;
                            if (matchingOverrideElement != null) {
                                element2 = this.performDOMOverride(element, matchingOverrideElement.originalElement, element3);
                                if (!element2.isEqualNode(element3)) {
                                    this.hasPerformedTransformations = true;
                                }
                                if (b) {
                                    matchingOverrideElement.overrideCloned = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void fillOverrideElementMap(final Element element) {
        for (Element element2 = DOMUtil.getFirstChildElement(element); element2 != null; element2 = DOMUtil.getNextSiblingElement(element2)) {
            final String localName = this.getLocalName(element2);
            if (!localName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                final int overrideType = this.getOverrideType(localName);
                if (overrideType != 0) {
                    this.addOverrideElement(overrideType, element2);
                }
                else {
                    this.fSchemaHandler.reportSchemaError("s4s-elt-must-match.1", new Object[] { "override", "(annotation | (simpleType | complexType | group | attributeGroup | element | attribute | notation))*", DOMUtil.getLocalName(element2) }, element2);
                }
            }
        }
    }
    
    private void addOverrideElement(final int n, final Element element) {
        final String attrValue = DOMUtil.getAttrValue(element, SchemaSymbols.ATT_NAME);
        final HashMap hashMap = this.fOverrideComponentsMap[n];
        if (hashMap.get(attrValue) != null) {
            this.fSchemaHandler.reportSchemaError("sch-props-correct.2", new Object[] { attrValue }, element);
        }
        else {
            final OverrideElement overrideElement = new OverrideElement(n, element, attrValue);
            this.fOverrideComponents.add(overrideElement);
            hashMap.put(attrValue, overrideElement);
        }
    }
    
    private OverrideElement getMatchingOverrideElement(final int n, final String s) {
        final Iterator iterator = this.fOverrideComponents.iterator();
        while (iterator.hasNext()) {
            final OverrideElement overrideElement = (OverrideElement)iterator.next();
            if (overrideElement.componentType == n && overrideElement.cName.equals(s)) {
                return overrideElement;
            }
        }
        return null;
    }
    
    private int getOverrideType(final String s) {
        if (s.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            return 1;
        }
        if (s.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
            return 2;
        }
        if (s.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
            return 3;
        }
        if (s.equals(SchemaSymbols.ELT_GROUP)) {
            return 4;
        }
        if (s.equals(SchemaSymbols.ELT_ELEMENT)) {
            return 5;
        }
        if (s.equals(SchemaSymbols.ELT_NOTATION)) {
            return 6;
        }
        if (s.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
            return 7;
        }
        return 0;
    }
    
    private Document cloneOverridenSchema(final Element element) {
        final Document document = this.fDOMImpl.createDocument(null, null, null);
        final Node importNode = document.importNode(element, true);
        document.setDocumentURI(element.getOwnerDocument().getDocumentURI());
        document.appendChild(importNode);
        return document;
    }
    
    private Node getChildClone(final Node node) {
        return this.fOverridenDoc.importNode(node, true);
    }
    
    private void mergeOverride(final Element element) {
        final Iterator iterator = this.fOverrideComponents.iterator();
        while (iterator.hasNext()) {
            final OverrideElement overrideElement = (OverrideElement)iterator.next();
            if (!overrideElement.overrideCloned) {
                element.appendChild(this.getChildClone(overrideElement.originalElement));
                this.hasPerformedTransformations = true;
            }
            else {
                overrideElement.overrideCloned = false;
            }
        }
    }
    
    private Element performDOMOverride(final Element element, final Element element2, final Element element3) {
        final Element element4 = (Element)this.getChildClone(element2);
        element.replaceChild(element4, element3);
        return element4;
    }
    
    private String getLocalName(final Node node) {
        final String localName = DOMUtil.getLocalName(node);
        if (localName.indexOf(":") > -1) {
            return localName.split(":")[1];
        }
        return localName;
    }
    
    private static final class OverrideElement
    {
        final int componentType;
        final Element originalElement;
        final String cName;
        boolean overrideCloned;
        
        OverrideElement(final int componentType, final Element originalElement, final String cName) {
            this.overrideCloned = false;
            this.componentType = componentType;
            this.originalElement = originalElement;
            this.cName = cName;
        }
    }
}
