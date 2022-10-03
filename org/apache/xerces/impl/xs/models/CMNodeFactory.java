package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.impl.XMLErrorReporter;

public class CMNodeFactory
{
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final boolean DEBUG = false;
    private static final int MULTIPLICITY = 1;
    private int nodeCount;
    private int maxNodeLimit;
    private XMLErrorReporter fErrorReporter;
    private SecurityManager fSecurityManager;
    
    public CMNodeFactory() {
        this.nodeCount = 0;
        this.fSecurityManager = null;
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) {
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        try {
            this.fSecurityManager = (SecurityManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
            this.reset();
        }
        catch (final XMLConfigurationException ex) {
            this.fSecurityManager = null;
        }
    }
    
    public void reset() {
        if (this.fSecurityManager != null) {
            this.maxNodeLimit = this.fSecurityManager.getMaxOccurNodeLimit() * 1;
        }
    }
    
    public CMNode getCMLeafNode(final int n, final Object o, final int n2, final int n3) {
        this.nodeCountCheck();
        return new XSCMLeaf(n, o, n2, n3);
    }
    
    public CMNode getCMRepeatingLeafNode(final int n, final Object o, final int n2, final int n3, final int n4, final int n5) {
        this.nodeCountCheck();
        return new XSCMRepeatingLeaf(n, o, n2, n3, n4, n5);
    }
    
    public CMNode getCMUniOpNode(final int n, final CMNode cmNode) {
        this.nodeCountCheck();
        return new XSCMUniOp(n, cmNode);
    }
    
    public CMNode getCMBinOpNode(final int n, final CMNode cmNode, final CMNode cmNode2) {
        this.nodeCountCheck();
        return new XSCMBinOp(n, cmNode, cmNode2);
    }
    
    public void nodeCountCheck() {
        if (this.fSecurityManager != null && this.nodeCount++ > this.maxNodeLimit) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "maxOccurLimit", new Object[] { new Integer(this.maxNodeLimit) }, (short)2);
            this.nodeCount = 0;
        }
    }
    
    public void resetNodeCount() {
        this.nodeCount = 0;
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/properties/")) {
            final int n = s.length() - "http://apache.org/xml/properties/".length();
            if (n == "security-manager".length() && s.endsWith("security-manager")) {
                this.fSecurityManager = (SecurityManager)o;
                this.maxNodeLimit = ((this.fSecurityManager != null) ? (this.fSecurityManager.getMaxOccurNodeLimit() * 1) : 0);
                return;
            }
            if (n == "internal/error-reporter".length() && s.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)o;
            }
        }
    }
    
    void testOccurrences(final int n) {
        if (this.fSecurityManager != null && (n < 0 || n > this.maxNodeLimit)) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "maxOccurLimit", new Object[] { new Integer(this.maxNodeLimit) }, (short)2);
        }
    }
}
