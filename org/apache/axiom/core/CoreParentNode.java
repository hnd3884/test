package org.apache.axiom.core;

import org.apache.axiom.om.OMXMLParserWrapper;

public interface CoreParentNode extends CoreNode
{
    public static final int COMPLETE = 0;
    public static final int INCOMPLETE = 1;
    public static final int DISCARDED = 2;
    public static final int COMPACT = 3;
    
    OMXMLParserWrapper getBuilder();
    
    void coreSetBuilder(final OMXMLParserWrapper p0);
    
    void build();
    
    void buildNext();
    
     <T> void cloneChildrenIfNecessary(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
    
    void coreAppendChild(final CoreChildNode p0, final boolean p1);
    
    void coreAppendChildren(final CoreDocumentFragment p0);
    
     <T extends CoreElement> NodeIterator<T> coreGetElements(final Axis p0, final Class<T> p1, final ElementMatcher<? super T> p2, final String p3, final String p4, final Semantics p5);
    
    CoreChildNode coreGetFirstChild();
    
    CoreChildNode coreGetFirstChild(final NodeFilter p0);
    
    CoreChildNode coreGetFirstChildIfAvailable();
    
    CoreChildNode coreGetLastChild();
    
    CoreChildNode coreGetLastChild(final NodeFilter p0);
    
    CoreChildNode coreGetLastKnownChild();
    
     <T> NodeIterator<T> coreGetNodes(final Axis p0, final Class<T> p1, final Semantics p2);
    
    void coreRemoveChildren(final Semantics p0);
    
    void coreSetCharacterData(final Object p0, final Semantics p1);
    
    void coreSetState(final int p0);
    
    void forceExpand();
    
    int getState();
    
    boolean isExpanded();
}
