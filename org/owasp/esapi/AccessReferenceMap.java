package org.owasp.esapi;

import java.util.Set;
import org.owasp.esapi.errors.AccessControlException;
import java.util.Iterator;
import java.io.Serializable;

public interface AccessReferenceMap<K> extends Serializable
{
    Iterator iterator();
    
     <T> K getIndirectReference(final T p0);
    
     <T> T getDirectReference(final K p0) throws AccessControlException;
    
     <T> K addDirectReference(final T p0);
    
     <T> K removeDirectReference(final T p0) throws AccessControlException;
    
    void update(final Set p0);
}
