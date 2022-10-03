package org.apache.xerces.dom3.as;

import org.w3c.dom.DOMException;

public interface DocumentAS
{
    ASModel getActiveASModel();
    
    void setActiveASModel(final ASModel p0);
    
    ASObjectList getBoundASModels();
    
    void setBoundASModels(final ASObjectList p0);
    
    ASModel getInternalAS();
    
    void setInternalAS(final ASModel p0);
    
    void addAS(final ASModel p0);
    
    void removeAS(final ASModel p0);
    
    ASElementDeclaration getElementDeclaration() throws DOMException;
    
    void validate() throws DOMASException;
}
