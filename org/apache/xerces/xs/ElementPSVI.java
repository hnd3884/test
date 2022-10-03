package org.apache.xerces.xs;

import org.apache.xerces.xs.datatypes.ObjectList;

public interface ElementPSVI extends ItemPSVI
{
    XSElementDeclaration getElementDeclaration();
    
    XSNotationDeclaration getNotation();
    
    boolean getNil();
    
    XSModel getSchemaInformation();
    
    ObjectList getInheritedAttributes();
    
    ObjectList getFailedAssertions();
    
    XSTypeAlternative getTypeAlternative();
}
