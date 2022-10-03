package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreDocumentSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreDocumentSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreDocumentSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$inputEncoding(final CoreDocument ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlVersion(final CoreDocument ajc$this_) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlVersion("1.0");
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlEncoding(final CoreDocument ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$standalone(final CoreDocument ajc$this_) {
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetNodeType(final CoreDocument ajc$this_) {
        return NodeType.DOCUMENT;
    }
    
    public static CoreNode ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$getRootOrOwnerDocument(final CoreDocument ajc$this_) {
        return ajc$this_;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetOwnerDocument(final CoreDocument ajc$this_, final CoreDocument document) {
        if (document != ajc$this_) {
            throw new IllegalArgumentException();
        }
    }
    
    public static CoreElement ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetDocumentElement(final CoreDocument ajc$this_) {
        for (CoreChildNode child = CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(ajc$this_); child != null; child = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling(child)) {
            if (child instanceof CoreElement) {
                return (CoreElement)child;
            }
        }
        return null;
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetInputEncoding(final CoreDocument ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$inputEncoding();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetInputEncoding(final CoreDocument ajc$this_, final String inputEncoding) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$inputEncoding(inputEncoding);
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetXmlVersion(final CoreDocument ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlVersion();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetXmlVersion(final CoreDocument ajc$this_, final String xmlVersion) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlVersion(xmlVersion);
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetXmlEncoding(final CoreDocument ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlEncoding();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetXmlEncoding(final CoreDocument ajc$this_, final String xmlEncoding) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlEncoding(xmlEncoding);
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreIsStandalone(final CoreDocument ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$standalone();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetStandalone(final CoreDocument ajc$this_, final boolean standalone) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$standalone(standalone);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$init(final CoreDocument ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        final CoreDocument o = (CoreDocument)other;
        ajc$this_.coreSetXmlVersion(o.coreGetXmlVersion());
        ajc$this_.coreSetXmlEncoding(o.coreGetXmlEncoding());
        ajc$this_.coreSetStandalone(o.coreIsStandalone());
        ajc$this_.coreSetInputEncoding(o.coreGetInputEncoding());
    }
    
    public static CoreDocumentSupport aspectOf() {
        if (CoreDocumentSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreDocumentSupport", CoreDocumentSupport.ajc$initFailureCause);
        }
        return CoreDocumentSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreDocumentSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreDocumentSupport();
    }
}
