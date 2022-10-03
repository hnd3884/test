package org.apache.xerces.impl.xs.assertion;

import org.eclipse.wst.xml.xpath2.processor.PsychoPathXPathTypeHelper;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xs.XSObjectList;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyAtomicType;
import org.apache.xerces.dom.PSVIElementNSImpl;
import java.util.List;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.eclipse.wst.xml.xpath2.processor.internal.types.QName;
import org.eclipse.wst.xml.xpath2.processor.internal.types.SchemaTypeValueFactory;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.ElementPSVI;
import org.w3c.dom.Element;

public class XSAssertionXPath2ValueImpl implements XSAssertionXPath2Value
{
    private Element fRootNodeOfAssertTree;
    
    public XSAssertionXPath2ValueImpl() {
        this.fRootNodeOfAssertTree = null;
    }
    
    public String computeStringValueOf$value(final Element fRootNodeOfAssertTree, final ElementPSVI elementPSVI) throws DOMException {
        String s = "";
        final XSTypeDefinition typeDefinition = elementPSVI.getTypeDefinition();
        if (typeDefinition instanceof XSComplexTypeDefinition && ((XSComplexTypeDefinition)typeDefinition).getContentType() != 1) {
            s = null;
        }
        if (s != null) {
            this.fRootNodeOfAssertTree = fRootNodeOfAssertTree;
            final NodeList childNodes = fRootNodeOfAssertTree.getChildNodes();
            final StringBuffer sb = new StringBuffer();
            final int length = childNodes.getLength();
            int n = 0;
            int n2 = 0;
            for (int i = 0; i < length; ++i) {
                final Node item = childNodes.item(i);
                final short nodeType = item.getNodeType();
                if (nodeType == 3) {
                    ++n;
                    ++n2;
                    sb.append(item.getNodeValue());
                }
                else if (nodeType == 1) {
                    ++n2;
                }
            }
            if (n == n2) {
                if (elementPSVI.getTypeDefinition().derivedFrom(SchemaSymbols.URI_SCHEMAFORSCHEMA, "string", (short)2)) {
                    s = sb.toString();
                }
                else {
                    s = XMLChar.trim(sb.toString());
                }
            }
        }
        return s;
    }
    
    public void setXDMTypedValueOf$value(final Element element, final String s, final XSSimpleTypeDefinition xsSimpleTypeDefinition, final XSTypeDefinition xsTypeDefinition, final boolean b, final DynamicContext dynamicContext) throws Exception {
        if (xsSimpleTypeDefinition != null) {
            if (b || xsSimpleTypeDefinition.getVariety() == 2) {
                final StringTokenizer stringTokenizer = new StringTokenizer(s, " \n\t\r");
                final ArrayList list = new ArrayList();
                while (stringTokenizer.hasMoreTokens()) {
                    final String nextToken = stringTokenizer.nextToken();
                    if (xsSimpleTypeDefinition.getItemType() != null) {
                        list.add(SchemaTypeValueFactory.newSchemaTypeValue(xsSimpleTypeDefinition.getItemType().getBuiltInKind(), nextToken));
                    }
                    else {
                        list.add(SchemaTypeValueFactory.newSchemaTypeValue(xsSimpleTypeDefinition.getBuiltInKind(), nextToken));
                    }
                }
                dynamicContext.set_variable(new QName("value"), XS11TypeHelper.getXPath2ResultSequence(list));
            }
            else {
                this.setXDMTypedValueOf$valueForSTVarietyAtomic(s, this.getXercesXSDTypeCodeFor$value(xsSimpleTypeDefinition), dynamicContext);
            }
        }
        else if (xsTypeDefinition != null) {
            this.setXDMTypedValueOf$valueForSTVarietyAtomic(s, this.getXercesXSDTypeCodeFor$value(xsTypeDefinition), dynamicContext);
        }
        else {
            final XSTypeDefinition typeDefinition = ((PSVIElementNSImpl)element).getTypeDefinition();
            if (typeDefinition instanceof XSComplexTypeDefinition && ((XSComplexTypeDefinition)typeDefinition).getSimpleType() != null) {
                this.setXDMValueOf$valueForCTWithSimpleContent(s, (XSComplexTypeDefinition)typeDefinition, dynamicContext);
            }
            else if (typeDefinition instanceof XSComplexTypeDefinition && ((XSComplexTypeDefinition)typeDefinition).getSimpleType() == null) {
                dynamicContext.set_variable(new QName("value"), XS11TypeHelper.getXPath2ResultSequence(new ArrayList()));
            }
            else {
                this.setXDMTypedValueOf$valueForSTVarietyAtomic(s, this.getXercesXSDTypeCodeFor$value(typeDefinition), dynamicContext);
            }
        }
    }
    
    public void setXDMTypedValueOf$valueForSTVarietyAtomic(final String s, final short n, final DynamicContext dynamicContext) {
        dynamicContext.set_variable(new QName("value"), (AnyType)SchemaTypeValueFactory.newSchemaTypeValue(n, s));
    }
    
    public void setXDMTypedValueOf$valueForSTVarietyList(final Element element, final String s, final XSSimpleTypeDefinition xsSimpleTypeDefinition, final boolean b, final DynamicContext dynamicContext) throws Exception {
        if (xsSimpleTypeDefinition.getVariety() == 3) {
            final ArrayList list = new ArrayList();
            final XSObjectList memberTypes = xsSimpleTypeDefinition.getMemberTypes();
            final StringTokenizer stringTokenizer = new StringTokenizer(s, " \n\t\r");
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                list.add(SchemaTypeValueFactory.newSchemaTypeValue(this.getActualXDMItemTypeForSTVarietyUnion(memberTypes, nextToken).getBuiltInKind(), nextToken));
            }
            dynamicContext.set_variable(new QName("value"), XS11TypeHelper.getXPath2ResultSequence(list));
        }
        else {
            this.setXDMTypedValueOf$value(element, s, xsSimpleTypeDefinition, null, b, dynamicContext);
        }
    }
    
    public void setXDMTypedValueOf$valueForSTVarietyUnion(final String s, final XSObjectList list, final DynamicContext dynamicContext) throws Exception {
        int i = 0;
        while (i < list.getLength()) {
            final XSSimpleType xsSimpleType = (XSSimpleType)list.item(i);
            if (XS11TypeHelper.isStrValueValidForASimpleType(s, xsSimpleType, (short)4)) {
                if (xsSimpleType.getVariety() == 2) {
                    this.setXDMTypedValueOf$valueForSTVarietyList(this.fRootNodeOfAssertTree, s, xsSimpleType, ((XSSimpleTypeDefinition)xsSimpleType.getBaseType()).getVariety() == 2, dynamicContext);
                    break;
                }
                this.setXDMTypedValueOf$valueForSTVarietyAtomic(s, this.getXercesXSDTypeCodeFor$value(xsSimpleType), dynamicContext);
                break;
            }
            else {
                ++i;
            }
        }
    }
    
    protected void setXDMValueOf$valueForCTWithSimpleContent(final String s, final XSComplexTypeDefinition xsComplexTypeDefinition, final DynamicContext dynamicContext) throws Exception {
        final XSSimpleTypeDefinition simpleType = xsComplexTypeDefinition.getSimpleType();
        if (simpleType.getVariety() == 2) {
            final XSSimpleTypeDefinition itemType = simpleType.getItemType();
            final StringTokenizer stringTokenizer = new StringTokenizer(s, " \n\t\r");
            final ArrayList list = new ArrayList();
            if (itemType.getVariety() == 3) {
                final XSObjectList memberTypes = itemType.getMemberTypes();
                while (stringTokenizer.hasMoreTokens()) {
                    final String nextToken = stringTokenizer.nextToken();
                    list.add(SchemaTypeValueFactory.newSchemaTypeValue(this.getActualXDMItemTypeForSTVarietyUnion(memberTypes, nextToken).getBuiltInKind(), nextToken));
                }
            }
            else {
                while (stringTokenizer.hasMoreTokens()) {
                    list.add(SchemaTypeValueFactory.newSchemaTypeValue(itemType.getBuiltInKind(), stringTokenizer.nextToken()));
                }
            }
            dynamicContext.set_variable(new QName("value"), XS11TypeHelper.getXPath2ResultSequence(list));
        }
        else if (simpleType.getVariety() == 3) {
            final XSSimpleTypeDefinition actualXDMItemTypeForSTVarietyUnion = this.getActualXDMItemTypeForSTVarietyUnion(simpleType.getMemberTypes(), s);
            if (actualXDMItemTypeForSTVarietyUnion.getVariety() == 2) {
                this.setXDMTypedValueOf$valueForSTVarietyList(this.fRootNodeOfAssertTree, s, actualXDMItemTypeForSTVarietyUnion, ((XSSimpleTypeDefinition)actualXDMItemTypeForSTVarietyUnion.getBaseType()).getVariety() == 2, dynamicContext);
            }
            else {
                this.setXDMTypedValueOf$valueForSTVarietyAtomic(s, this.getXercesXSDTypeCodeFor$value(actualXDMItemTypeForSTVarietyUnion), dynamicContext);
            }
        }
        else {
            this.setXDMTypedValueOf$valueForSTVarietyAtomic(s, this.getXercesXSDTypeCodeFor$value(xsComplexTypeDefinition.getSimpleType()), dynamicContext);
        }
    }
    
    private short getXercesXSDTypeCodeFor$value(final XSTypeDefinition xsTypeDefinition) {
        if (Constants.NS_XMLSCHEMA.equals(xsTypeDefinition.getNamespace())) {
            short n = -100;
            boolean b = false;
            final String name = xsTypeDefinition.getName();
            if ("dayTimeDuration".equals(name)) {
                n = PsychoPathXPathTypeHelper.DAYTIMEDURATION_DT;
                b = true;
            }
            else if ("yearMonthDuration".equals(name)) {
                n = PsychoPathXPathTypeHelper.YEARMONTHDURATION_DT;
                b = true;
            }
            return b ? n : ((XSSimpleTypeDefinition)xsTypeDefinition).getBuiltInKind();
        }
        return this.getXercesXSDTypeCodeFor$value(xsTypeDefinition.getBaseType());
    }
    
    private XSSimpleTypeDefinition getActualXDMItemTypeForSTVarietyUnion(final XSObjectList list, final String s) {
        XSSimpleTypeDefinition xsSimpleTypeDefinition = null;
        for (int length = list.getLength(), i = 0; i < length; ++i) {
            final XSSimpleType xsSimpleType = (XSSimpleType)list.item(i);
            if (XS11TypeHelper.isStrValueValidForASimpleType(s, xsSimpleType, (short)4)) {
                xsSimpleTypeDefinition = xsSimpleType;
                break;
            }
        }
        return xsSimpleTypeDefinition;
    }
}
