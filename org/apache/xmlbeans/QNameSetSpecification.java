package org.apache.xmlbeans;

import java.util.Set;
import javax.xml.namespace.QName;

public interface QNameSetSpecification
{
    boolean contains(final QName p0);
    
    boolean isAll();
    
    boolean isEmpty();
    
    boolean containsAll(final QNameSetSpecification p0);
    
    boolean isDisjoint(final QNameSetSpecification p0);
    
    QNameSet intersect(final QNameSetSpecification p0);
    
    QNameSet union(final QNameSetSpecification p0);
    
    QNameSet inverse();
    
    Set excludedURIs();
    
    Set includedURIs();
    
    Set excludedQNamesInIncludedURIs();
    
    Set includedQNamesInExcludedURIs();
}
