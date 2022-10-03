package org.apache.xerces.impl.xs;

import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.grammars.XMLSchemaDescription;
import org.apache.xerces.util.XMLResourceIdentifierImpl;

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
    public static final short CONTEXT_OVERRIDE = 8;
    protected short fContextType;
    protected String[] fLocationHints;
    protected QName fTriggeringComponent;
    protected QName fEnclosedElementName;
    protected XMLAttributes fAttributes;
    
    public String getGrammarType() {
        return "http://www.w3.org/2001/XMLSchema";
    }
    
    public short getContextType() {
        return this.fContextType;
    }
    
    public String getTargetNamespace() {
        return this.fNamespace;
    }
    
    public String[] getLocationHints() {
        return this.fLocationHints;
    }
    
    public QName getTriggeringComponent() {
        return this.fTriggeringComponent;
    }
    
    public QName getEnclosingElementName() {
        return this.fEnclosedElementName;
    }
    
    public XMLAttributes getAttributes() {
        return this.fAttributes;
    }
    
    public boolean fromInstance() {
        return this.fContextType == 6 || this.fContextType == 5 || this.fContextType == 4 || this.fContextType == 7;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof XMLSchemaDescription)) {
            return false;
        }
        final XMLSchemaDescription xmlSchemaDescription = (XMLSchemaDescription)o;
        if (this.fNamespace != null) {
            return this.fNamespace.equals(xmlSchemaDescription.getTargetNamespace());
        }
        return xmlSchemaDescription.getTargetNamespace() == null;
    }
    
    public int hashCode() {
        return (this.fNamespace == null) ? 0 : this.fNamespace.hashCode();
    }
    
    public void setContextType(final short fContextType) {
        this.fContextType = fContextType;
    }
    
    public void setTargetNamespace(final String fNamespace) {
        this.fNamespace = fNamespace;
    }
    
    public void setLocationHints(final String[] array) {
        final int length = array.length;
        System.arraycopy(array, 0, this.fLocationHints = new String[length], 0, length);
    }
    
    public void setTriggeringComponent(final QName fTriggeringComponent) {
        this.fTriggeringComponent = fTriggeringComponent;
    }
    
    public void setEnclosingElementName(final QName fEnclosedElementName) {
        this.fEnclosedElementName = fEnclosedElementName;
    }
    
    public void setAttributes(final XMLAttributes fAttributes) {
        this.fAttributes = fAttributes;
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
        final XSDDescription xsdDescription = new XSDDescription();
        xsdDescription.fAttributes = this.fAttributes;
        xsdDescription.fBaseSystemId = this.fBaseSystemId;
        xsdDescription.fContextType = this.fContextType;
        xsdDescription.fEnclosedElementName = this.fEnclosedElementName;
        xsdDescription.fExpandedSystemId = this.fExpandedSystemId;
        xsdDescription.fLiteralSystemId = this.fLiteralSystemId;
        xsdDescription.fLocationHints = this.fLocationHints;
        xsdDescription.fPublicId = this.fPublicId;
        xsdDescription.fNamespace = this.fNamespace;
        xsdDescription.fTriggeringComponent = this.fTriggeringComponent;
        return xsdDescription;
    }
}
