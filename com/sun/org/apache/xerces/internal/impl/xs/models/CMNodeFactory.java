package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;

public class CMNodeFactory
{
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final boolean DEBUG = false;
    private static final int MULTIPLICITY = 1;
    private int nodeCount;
    private int maxNodeLimit;
    private XMLErrorReporter fErrorReporter;
    private XMLSecurityManager fSecurityManager;
    
    public CMNodeFactory() {
        this.nodeCount = 0;
        this.fSecurityManager = null;
    }
    
    public void reset(final XMLComponentManager componentManager) {
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        try {
            this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager");
            if (this.fSecurityManager != null) {
                this.maxNodeLimit = this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT) * 1;
            }
        }
        catch (final XMLConfigurationException e) {
            this.fSecurityManager = null;
        }
    }
    
    public CMNode getCMLeafNode(final int type, final Object leaf, final int id, final int position) {
        return new XSCMLeaf(type, leaf, id, position);
    }
    
    public CMNode getCMRepeatingLeafNode(final int type, final Object leaf, final int minOccurs, final int maxOccurs, final int id, final int position) {
        this.nodeCountCheck();
        return new XSCMRepeatingLeaf(type, leaf, minOccurs, maxOccurs, id, position);
    }
    
    public CMNode getCMUniOpNode(final int type, final CMNode childNode) {
        this.nodeCountCheck();
        return new XSCMUniOp(type, childNode);
    }
    
    public CMNode getCMBinOpNode(final int type, final CMNode leftNode, final CMNode rightNode) {
        return new XSCMBinOp(type, leftNode, rightNode);
    }
    
    public void nodeCountCheck() {
        if (this.fSecurityManager != null && !this.fSecurityManager.isNoLimit(this.maxNodeLimit) && this.nodeCount++ > this.maxNodeLimit) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "MaxOccurLimit", new Object[] { new Integer(this.maxNodeLimit) }, (short)2);
            this.nodeCount = 0;
        }
    }
    
    public void resetNodeCount() {
        this.nodeCount = 0;
    }
    
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
            if (suffixLength == "security-manager".length() && propertyId.endsWith("security-manager")) {
                this.fSecurityManager = (XMLSecurityManager)value;
                this.maxNodeLimit = ((this.fSecurityManager != null) ? (this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT) * 1) : 0);
                return;
            }
            if (suffixLength == "internal/error-reporter".length() && propertyId.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)value;
            }
        }
    }
}
