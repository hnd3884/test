package org.apache.axiom.shared;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class NSAwareNamedNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ NSAwareNamedNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            NSAwareNamedNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static String ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getLocalName(final INSAwareNamedNode ajc$this_) {
        return ajc$this_.coreGetLocalName();
    }
    
    public static String ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getNamespaceURI(final INSAwareNamedNode ajc$this_) {
        final String namespaceURI = ajc$this_.coreGetNamespaceURI();
        return (namespaceURI.length() == 0) ? null : namespaceURI;
    }
    
    public static String ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getPrefix(final INSAwareNamedNode ajc$this_) {
        final String prefix = ajc$this_.coreGetPrefix();
        return (prefix.length() == 0) ? null : prefix;
    }
    
    public static NSAwareNamedNodeSupport aspectOf() {
        if (NSAwareNamedNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_shared_NSAwareNamedNodeSupport", NSAwareNamedNodeSupport.ajc$initFailureCause);
        }
        return NSAwareNamedNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return NSAwareNamedNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new NSAwareNamedNodeSupport();
    }
}
