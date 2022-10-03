package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreDocumentTypeDeclarationSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreDocumentTypeDeclarationSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreDocumentTypeDeclarationSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$rootName(final CoreDocumentTypeDeclaration ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$publicId(final CoreDocumentTypeDeclaration ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$systemId(final CoreDocumentTypeDeclaration ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$internalSubset(final CoreDocumentTypeDeclaration ajc$this_) {
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetNodeType(final CoreDocumentTypeDeclaration ajc$this_) {
        return NodeType.DOCUMENT_TYPE_DECLARATION;
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetRootName(final CoreDocumentTypeDeclaration ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$rootName();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreSetRootName(final CoreDocumentTypeDeclaration ajc$this_, final String rootName) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$rootName(rootName);
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetPublicId(final CoreDocumentTypeDeclaration ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$publicId();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreSetPublicId(final CoreDocumentTypeDeclaration ajc$this_, final String publicId) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$publicId(publicId);
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetSystemId(final CoreDocumentTypeDeclaration ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$systemId();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreSetSystemId(final CoreDocumentTypeDeclaration ajc$this_, final String systemId) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$systemId(systemId);
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetInternalSubset(final CoreDocumentTypeDeclaration ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$internalSubset();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreSetInternalSubset(final CoreDocumentTypeDeclaration ajc$this_, final String internalSubset) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$internalSubset(internalSubset);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$init(final CoreDocumentTypeDeclaration ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        final CoreDocumentTypeDeclaration o = (CoreDocumentTypeDeclaration)other;
        ajc$this_.coreSetRootName(o.coreGetRootName());
        ajc$this_.coreSetPublicId(o.coreGetPublicId());
        ajc$this_.coreSetSystemId(o.coreGetSystemId());
        ajc$this_.coreSetInternalSubset(o.coreGetInternalSubset());
    }
    
    public static CoreDocumentTypeDeclarationSupport aspectOf() {
        if (CoreDocumentTypeDeclarationSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreDocumentTypeDeclarationSupport", CoreDocumentTypeDeclarationSupport.ajc$initFailureCause);
        }
        return CoreDocumentTypeDeclarationSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreDocumentTypeDeclarationSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreDocumentTypeDeclarationSupport();
    }
}
