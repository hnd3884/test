package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;

public class XSDDescription extends XMLResourceIdentifierImpl implements XMLSchemaDescription
{
    public static final short CONTEXT_INITIALIZE = -1;
    public static final short CONTEXT_INCLUDE = 0;
    public static final short CONTEXT_REDEFINE = 1;
    public static final short CONTEXT_IMPORT = 2;
    public static final short CONTEXT_PREPARSE = 3;
    public static final short CONTEXT_INSTANCE = 4;
    public static final short CONTEXT_ELEMENT = 5;
    public static final short CONTEXT_ATTRIBUTE = 6;
    public static final short CONTEXT_XSITYPE = 7;
    protected short fContextType;
    protected String[] fLocationHints;
    protected QName fTriggeringComponent;
    protected QName fEnclosedElementName;
    protected XMLAttributes fAttributes;
    
    @Override
    public String getGrammarType() {
        return "http://www.w3.org/2001/XMLSchema";
    }
    
    @Override
    public short getContextType() {
        return this.fContextType;
    }
    
    @Override
    public String getTargetNamespace() {
        return this.fNamespace;
    }
    
    @Override
    public String[] getLocationHints() {
        return this.fLocationHints;
    }
    
    @Override
    public QName getTriggeringComponent() {
        return this.fTriggeringComponent;
    }
    
    @Override
    public QName getEnclosingElementName() {
        return this.fEnclosedElementName;
    }
    
    @Override
    public XMLAttributes getAttributes() {
        return this.fAttributes;
    }
    
    public boolean fromInstance() {
        return this.fContextType == 6 || this.fContextType == 5 || this.fContextType == 4 || this.fContextType == 7;
    }
    
    public boolean isExternal() {
        return this.fContextType == 0 || this.fContextType == 1 || this.fContextType == 2 || this.fContextType == 5 || this.fContextType == 6 || this.fContextType == 7;
    }
    
    @Override
    public boolean equals(final Object descObj) {
        if (!(descObj instanceof XMLSchemaDescription)) {
            return false;
        }
        final XMLSchemaDescription desc = (XMLSchemaDescription)descObj;
        if (this.fNamespace != null) {
            return this.fNamespace.equals(desc.getTargetNamespace());
        }
        return desc.getTargetNamespace() == null;
    }
    
    @Override
    public int hashCode() {
        return (this.fNamespace == null) ? 0 : this.fNamespace.hashCode();
    }
    
    public void setContextType(final short contextType) {
        this.fContextType = contextType;
    }
    
    public void setTargetNamespace(final String targetNamespace) {
        this.fNamespace = targetNamespace;
    }
    
    public void setLocationHints(final String[] locationHints) {
        final int length = locationHints.length;
        System.arraycopy(locationHints, 0, this.fLocationHints = new String[length], 0, length);
    }
    
    public void setTriggeringComponent(final QName triggeringComponent) {
        this.fTriggeringComponent = triggeringComponent;
    }
    
    public void setEnclosingElementName(final QName enclosedElementName) {
        this.fEnclosedElementName = enclosedElementName;
    }
    
    public void setAttributes(final XMLAttributes attributes) {
        this.fAttributes = attributes;
    }
    
    public void reset() {
        super.clear();
        this.fContextType = -1;
        this.fLocationHints = null;
        this.fTriggeringComponent = null;
        this.fEnclosedElementName = null;
        this.fAttributes = null;
    }
    
    public XSDDescription makeClone() {
        final XSDDescription desc = new XSDDescription();
        desc.fAttributes = this.fAttributes;
        desc.fBaseSystemId = this.fBaseSystemId;
        desc.fContextType = this.fContextType;
        desc.fEnclosedElementName = this.fEnclosedElementName;
        desc.fExpandedSystemId = this.fExpandedSystemId;
        desc.fLiteralSystemId = this.fLiteralSystemId;
        desc.fLocationHints = this.fLocationHints;
        desc.fPublicId = this.fPublicId;
        desc.fNamespace = this.fNamespace;
        desc.fTriggeringComponent = this.fTriggeringComponent;
        return desc;
    }
}
