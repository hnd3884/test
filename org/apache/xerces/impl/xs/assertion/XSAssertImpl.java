package org.apache.xerces.impl.xs.assertion;

import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.xs.XSNamespaceItem;
import org.w3c.dom.Element;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.xs.XSObjectList;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.AbstractPsychoPathXPath2Impl;

public class XSAssertImpl extends AbstractPsychoPathXPath2Impl implements XSAssert
{
    private short fAssertKind;
    private XSTypeDefinition fTypeDefinition;
    private Test fTestExpr;
    private XPath fCompiledXPathExpr;
    private XSObjectList fAnnotations;
    private String fXPathDefaultNamespace;
    private NamespaceSupport fXPath2NamespaceContext;
    private String fAttrName;
    private String fAttrValue;
    private XSDHandler fSchemaHandler;
    private String fMessage;
    private short fVariety;
    
    public XSAssertImpl(final XSTypeDefinition fTypeDefinition, final XSObjectList fAnnotations, final XSDHandler fSchemaHandler) {
        this.fAssertKind = 16;
        this.fTestExpr = null;
        this.fCompiledXPathExpr = null;
        this.fAnnotations = null;
        this.fXPathDefaultNamespace = null;
        this.fXPath2NamespaceContext = null;
        this.fAttrName = null;
        this.fAttrValue = null;
        this.fSchemaHandler = null;
        this.fMessage = null;
        this.fVariety = 0;
        this.fTypeDefinition = fTypeDefinition;
        this.fSchemaHandler = fSchemaHandler;
        this.fAnnotations = fAnnotations;
    }
    
    public void setTest(final Test fTestExpr, final Element element) {
        this.fTestExpr = fTestExpr;
        this.setCompiledExpr(this.compileXPathStr(fTestExpr.getXPathStr(), this, this.fSchemaHandler, element));
    }
    
    public void setCompiledExpr(final XPath fCompiledXPathExpr) {
        this.fCompiledXPathExpr = fCompiledXPathExpr;
    }
    
    public void setAnnotations(final XSObjectList fAnnotations) {
        this.fAnnotations = fAnnotations;
    }
    
    public void setXPathDefaultNamespace(final String fxPathDefaultNamespace) {
        this.fXPathDefaultNamespace = fxPathDefaultNamespace;
    }
    
    public void setXPath2NamespaceContext(final NamespaceSupport fxPath2NamespaceContext) {
        this.fXPath2NamespaceContext = fxPath2NamespaceContext;
    }
    
    public void setAssertKind(final short fAssertKind) {
        this.fAssertKind = fAssertKind;
    }
    
    public void setAttrName(final String fAttrName) {
        this.fAttrName = fAttrName;
    }
    
    public void setAttrValue(final String fAttrValue) {
        this.fAttrValue = fAttrValue;
    }
    
    public void setTypeDefinition(final XSTypeDefinition fTypeDefinition) {
        this.fTypeDefinition = fTypeDefinition;
    }
    
    public void setMessage(final String fMessage) {
        this.fMessage = fMessage;
    }
    
    public void setVariety(final short fVariety) {
        this.fVariety = fVariety;
    }
    
    public XSObjectList getAnnotations() {
        return this.fAnnotations;
    }
    
    public String getTestStr() {
        return this.fTestExpr.getXPathStr();
    }
    
    public XPath getCompiledXPathExpr() {
        return this.fCompiledXPathExpr;
    }
    
    public Test getTest() {
        return this.fTestExpr;
    }
    
    public XSTypeDefinition getTypeDefinition() {
        return this.fTypeDefinition;
    }
    
    public String getXPathDefaultNamespace() {
        return this.fXPathDefaultNamespace;
    }
    
    public String getName() {
        return null;
    }
    
    public String getNamespace() {
        return null;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
    
    public short getType() {
        return this.fAssertKind;
    }
    
    public String getAttrName() {
        return this.fAttrName;
    }
    
    public String getAttrValue() {
        return this.fAttrValue;
    }
    
    public NamespaceSupport getXPath2NamespaceContext() {
        return this.fXPath2NamespaceContext;
    }
    
    public String getMessage() {
        return this.fMessage;
    }
    
    public short getVariety() {
        return this.fVariety;
    }
    
    public XSDHandler getSchemaHandler() {
        return this.fSchemaHandler;
    }
    
    public short getAssertKind() {
        return this.fAssertKind;
    }
    
    public boolean equals(final XSAssertImpl xsAssertImpl) {
        boolean b = false;
        final String xPathStr = xsAssertImpl.getTest().getXPathStr();
        final String xPathStr2 = this.getTest().getXPathStr();
        if (XS11TypeHelper.isSchemaTypesIdentical(xsAssertImpl.getTypeDefinition(), this.fTypeDefinition) && xPathStr2.equals(xPathStr)) {
            b = true;
        }
        return b;
    }
}
