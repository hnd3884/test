package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreNamespaceDeclarationSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreNamespaceDeclarationSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreNamespaceDeclarationSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreNamespaceDeclarationSupport$org_apache_axiom_core_CoreNamespaceDeclaration$coreGetNodeType(final CoreNamespaceDeclaration ajc$this_) {
        return NodeType.NAMESPACE_DECLARATION;
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreNamespaceDeclarationSupport$org_apache_axiom_core_CoreNamespaceDeclaration$init(final CoreNamespaceDeclaration ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        ajc$this_.coreSetDeclaredNamespace(((CoreNamespaceDeclaration)other).coreGetDeclaredPrefix(), "");
    }
    
    public static CoreNamespaceDeclarationSupport aspectOf() {
        if (CoreNamespaceDeclarationSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreNamespaceDeclarationSupport", CoreNamespaceDeclarationSupport.ajc$initFailureCause);
        }
        return CoreNamespaceDeclarationSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreNamespaceDeclarationSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreNamespaceDeclarationSupport();
    }
}
