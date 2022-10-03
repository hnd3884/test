package org.apache.xerces.xni.grammars;

import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

public interface XMLSchemaDescription extends XMLGrammarDescription
{
    public static final short CONTEXT_INCLUDE = 0;
    public static final short CONTEXT_REDEFINE = 1;
    public static final short CONTEXT_IMPORT = 2;
    public static final short CONTEXT_PREPARSE = 3;
    public static final short CONTEXT_INSTANCE = 4;
    public static final short CONTEXT_ELEMENT = 5;
    public static final short CONTEXT_ATTRIBUTE = 6;
    public static final short CONTEXT_XSITYPE = 7;
    
    short getContextType();
    
    String getTargetNamespace();
    
    String[] getLocationHints();
    
    QName getTriggeringComponent();
    
    QName getEnclosingElementName();
    
    XMLAttributes getAttributes();
}
