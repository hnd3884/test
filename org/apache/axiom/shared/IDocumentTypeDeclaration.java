package org.apache.axiom.shared;

import org.apache.axiom.core.CoreDocumentTypeDeclaration;

public interface IDocumentTypeDeclaration extends CoreDocumentTypeDeclaration
{
    String getInternalSubset();
    
    String getPublicId();
    
    String getSystemId();
}
