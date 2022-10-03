package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreNSUnawareNamedNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreNSUnawareNamedNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreNSUnawareNamedNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreNSUnawareNamedNodeSupport$org_apache_axiom_core_CoreNSUnawareNamedNode$name(final CoreNSUnawareNamedNode ajc$this_) {
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreNSUnawareNamedNodeSupport$org_apache_axiom_core_CoreNSUnawareNamedNode$coreGetName(final CoreNSUnawareNamedNode ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreNSUnawareNamedNodeSupport$org_apache_axiom_core_CoreNSUnawareNamedNode$name();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreNSUnawareNamedNodeSupport$org_apache_axiom_core_CoreNSUnawareNamedNode$coreSetName(final CoreNSUnawareNamedNode ajc$this_, final String name) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreNSUnawareNamedNodeSupport$org_apache_axiom_core_CoreNSUnawareNamedNode$name(name);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreNSUnawareNamedNodeSupport$org_apache_axiom_core_CoreNSUnawareNamedNode$initName(final CoreNSUnawareNamedNode ajc$this_, final CoreNamedNode other) {
        ajc$this_.coreSetName(((CoreNSUnawareNamedNode)other).coreGetName());
    }
    
    public static CoreNSUnawareNamedNodeSupport aspectOf() {
        if (CoreNSUnawareNamedNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreNSUnawareNamedNodeSupport", CoreNSUnawareNamedNodeSupport.ajc$initFailureCause);
        }
        return CoreNSUnawareNamedNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreNSUnawareNamedNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreNSUnawareNamedNodeSupport();
    }
}
