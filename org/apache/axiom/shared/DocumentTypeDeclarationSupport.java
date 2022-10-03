package org.apache.axiom.shared;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class DocumentTypeDeclarationSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ DocumentTypeDeclarationSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            DocumentTypeDeclarationSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static String ajc$interMethod$org_apache_axiom_shared_DocumentTypeDeclarationSupport$org_apache_axiom_shared_IDocumentTypeDeclaration$getPublicId(final IDocumentTypeDeclaration ajc$this_) {
        return ajc$this_.coreGetPublicId();
    }
    
    public static String ajc$interMethod$org_apache_axiom_shared_DocumentTypeDeclarationSupport$org_apache_axiom_shared_IDocumentTypeDeclaration$getSystemId(final IDocumentTypeDeclaration ajc$this_) {
        return ajc$this_.coreGetSystemId();
    }
    
    public static String ajc$interMethod$org_apache_axiom_shared_DocumentTypeDeclarationSupport$org_apache_axiom_shared_IDocumentTypeDeclaration$getInternalSubset(final IDocumentTypeDeclaration ajc$this_) {
        return ajc$this_.coreGetInternalSubset();
    }
    
    public static DocumentTypeDeclarationSupport aspectOf() {
        if (DocumentTypeDeclarationSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_shared_DocumentTypeDeclarationSupport", DocumentTypeDeclarationSupport.ajc$initFailureCause);
        }
        return DocumentTypeDeclarationSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return DocumentTypeDeclarationSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new DocumentTypeDeclarationSupport();
    }
}
