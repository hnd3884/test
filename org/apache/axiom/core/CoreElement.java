package org.apache.axiom.core;

import java.util.Iterator;

public interface CoreElement extends CoreChildNode, CoreMixedContentContainer, CoreNamedNode, DeferringParentNode
{
    void coreAppendAttribute(final CoreAttribute p0);
    
    CoreAttribute coreGetAttribute(final AttributeMatcher p0, final String p1, final String p2);
    
     <T extends CoreAttribute, S> Iterator<S> coreGetAttributesByType(final Class<T> p0, final Mapper<T, S> p1, final Semantics p2);
    
    CoreAttribute coreGetFirstAttribute();
    
    CoreAttribute coreGetLastAttribute();
    
    String coreLookupNamespaceURI(final String p0, final Semantics p1);
    
    String coreLookupPrefix(final String p0, final Semantics p1);
    
    boolean coreRemoveAttribute(final AttributeMatcher p0, final String p1, final String p2, final Semantics p3);
    
    void coreSetAttribute(final AttributeMatcher p0, final String p1, final String p2, final String p3, final String p4);
    
    CoreAttribute coreSetAttribute(final AttributeMatcher p0, final CoreAttribute p1, final Semantics p2);
    
    String getImplicitNamespaceURI(final String p0);
    
    String getImplicitPrefix(final String p0);
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
    
     <T> void initSource(final ClonePolicy<T> p0, final T p1, final CoreElement p2);
}
