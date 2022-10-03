package com.sun.org.apache.xpath.internal.jaxp;

import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPath;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import jdk.xml.internal.JdkXmlFeatures;
import javax.xml.xpath.XPathVariableResolver;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathFactory;

public class XPathFactoryImpl extends XPathFactory
{
    private static final String CLASS_NAME = "XPathFactoryImpl";
    private XPathFunctionResolver xPathFunctionResolver;
    private XPathVariableResolver xPathVariableResolver;
    private boolean _isNotSecureProcessing;
    private boolean _isSecureMode;
    private final JdkXmlFeatures _featureManager;
    
    public XPathFactoryImpl() {
        this.xPathFunctionResolver = null;
        this.xPathVariableResolver = null;
        this._isNotSecureProcessing = true;
        this._isSecureMode = false;
        if (System.getSecurityManager() != null) {
            this._isSecureMode = true;
            this._isNotSecureProcessing = false;
        }
        this._featureManager = new JdkXmlFeatures(!this._isNotSecureProcessing);
    }
    
    @Override
    public boolean isObjectModelSupported(final String objectModel) {
        if (objectModel == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_OBJECT_MODEL_NULL", new Object[] { this.getClass().getName() });
            throw new NullPointerException(fmsg);
        }
        if (objectModel.length() == 0) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_OBJECT_MODEL_EMPTY", new Object[] { this.getClass().getName() });
            throw new IllegalArgumentException(fmsg);
        }
        return objectModel.equals("http://java.sun.com/jaxp/xpath/dom");
    }
    
    @Override
    public XPath newXPath() {
        return new XPathImpl(this.xPathVariableResolver, this.xPathFunctionResolver, !this._isNotSecureProcessing, this._featureManager);
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws XPathFactoryConfigurationException {
        if (name == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_FEATURE_NAME_NULL", new Object[] { "XPathFactoryImpl", new Boolean(value) });
            throw new NullPointerException(fmsg);
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            if (this._isSecureMode && !value) {
                final String fmsg = XPATHMessages.createXPATHMessage("ER_SECUREPROCESSING_FEATURE", new Object[] { name, "XPathFactoryImpl", new Boolean(value) });
                throw new XPathFactoryConfigurationException(fmsg);
            }
            this._isNotSecureProcessing = !value;
            if (value && this._featureManager != null) {
                this._featureManager.setFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION, JdkXmlFeatures.State.FSP, false);
            }
        }
        else {
            if (name.equals("http://www.oracle.com/feature/use-service-mechanism") && this._isSecureMode) {
                return;
            }
            if (this._featureManager != null && this._featureManager.setFeature(name, JdkXmlFeatures.State.APIPROPERTY, value)) {
                return;
            }
            final String fmsg = XPATHMessages.createXPATHMessage("ER_FEATURE_UNKNOWN", new Object[] { name, "XPathFactoryImpl", value });
            throw new XPathFactoryConfigurationException(fmsg);
        }
    }
    
    @Override
    public boolean getFeature(final String name) throws XPathFactoryConfigurationException {
        if (name == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_GETTING_NULL_FEATURE", new Object[] { "XPathFactoryImpl" });
            throw new NullPointerException(fmsg);
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return !this._isNotSecureProcessing;
        }
        final int index = this._featureManager.getIndex(name);
        if (index > -1) {
            return this._featureManager.getFeature(index);
        }
        final String fmsg2 = XPATHMessages.createXPATHMessage("ER_GETTING_UNKNOWN_FEATURE", new Object[] { name, "XPathFactoryImpl" });
        throw new XPathFactoryConfigurationException(fmsg2);
    }
    
    @Override
    public void setXPathFunctionResolver(final XPathFunctionResolver resolver) {
        if (resolver == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_NULL_XPATH_FUNCTION_RESOLVER", new Object[] { "XPathFactoryImpl" });
            throw new NullPointerException(fmsg);
        }
        this.xPathFunctionResolver = resolver;
    }
    
    @Override
    public void setXPathVariableResolver(final XPathVariableResolver resolver) {
        if (resolver == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_NULL_XPATH_VARIABLE_RESOLVER", new Object[] { "XPathFactoryImpl" });
            throw new NullPointerException(fmsg);
        }
        this.xPathVariableResolver = resolver;
    }
}
