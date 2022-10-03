package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class NonDeferringParentNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ NonDeferringParentNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            NonDeferringParentNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static OMXMLParserWrapper ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$getBuilder(final NonDeferringParentNode ajc$this_) {
        return null;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$coreSetBuilder(final NonDeferringParentNode ajc$this_, final OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$build(final NonDeferringParentNode ajc$this_) {
    }
    
    public static NonDeferringParentNodeSupport aspectOf() {
        if (NonDeferringParentNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_NonDeferringParentNodeSupport", NonDeferringParentNodeSupport.ajc$initFailureCause);
        }
        return NonDeferringParentNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return NonDeferringParentNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new NonDeferringParentNodeSupport();
    }
}
