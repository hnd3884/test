package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.core.CoreNamedNode;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomNamedInformationItemSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomNamedInformationItemSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomNamedInformationItemSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace(final AxiomNamedInformationItem ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(final AxiomNamedInformationItem ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(final AxiomNamedInformationItem ajc$this_) {
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(final AxiomNamedInformationItem ajc$this_, final OMNamespace namespace) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace(namespace);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(null);
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalGetLocalName(final AxiomNamedInformationItem ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(final AxiomNamedInformationItem ajc$this_, final String localName) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(localName);
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getNamespace(final AxiomNamedInformationItem ajc$this_) {
        return ajc$this_.defaultGetNamespace();
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetNamespace(final AxiomNamedInformationItem ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$setLocalName(final AxiomNamedInformationItem ajc$this_, final String localName) {
        ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common$beforeSetLocalName();
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(localName);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(null);
    }
    
    public static QName ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getQName(final AxiomNamedInformationItem ajc$this_) {
        return ajc$this_.defaultGetQName();
    }
    
    public static QName ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetQName(final AxiomNamedInformationItem ajc$this_) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName() != null) {
            return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName();
        }
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace() != null) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(new QName(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace().getNamespaceURI(), ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(), ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace().getPrefix()));
        }
        else {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(new QName(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName()));
        }
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName();
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$hasName(final AxiomNamedInformationItem ajc$this_, final QName name) {
        if (name.getLocalPart().equals(ajc$this_.getLocalName())) {
            final OMNamespace ns = ajc$this_.getNamespace();
            return (ns == null && name.getNamespaceURI().length() == 0) || (ns != null && name.getNamespaceURI().equals(ns.getNamespaceURI()));
        }
        return false;
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetNamespaceURI(final AxiomNamedInformationItem ajc$this_) {
        final OMNamespace namespace = ajc$this_.getNamespace();
        return (namespace == null) ? "" : namespace.getNamespaceURI();
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetPrefix(final AxiomNamedInformationItem ajc$this_) {
        final OMNamespace namespace = ajc$this_.getNamespace();
        return (namespace == null) ? "" : namespace.getPrefix();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreSetName(final AxiomNamedInformationItem ajc$this_, final String namespaceURI, final String localName, final String prefix) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(localName);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace((OMNamespace)((namespaceURI.length() == 0 && prefix.length() == 0) ? null : new OMNamespaceImpl(namespaceURI, prefix)));
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(null);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$initName(final AxiomNamedInformationItem ajc$this_, final CoreNamedNode other) {
        final AxiomNamedInformationItem o = (AxiomNamedInformationItem)other;
        if (o instanceof AxiomSourcedElement && ((AxiomElement)ajc$this_).isExpanded()) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(o.coreGetLocalName());
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace(o.getNamespace());
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(o.getQName());
        }
        else {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(o.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName());
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace(o.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace());
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(o.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName());
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$updateLocalName(final AxiomNamedInformationItem ajc$this_) {
        throw new IllegalStateException();
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetLocalName(final AxiomNamedInformationItem ajc$this_) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName() == null) {
            ajc$this_.updateLocalName();
        }
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreSetPrefix(final AxiomNamedInformationItem ajc$this_, final String prefix) {
        final OMNamespace ns = ajc$this_.getNamespace();
        if (ns == null) {
            if (prefix.length() > 0) {
                throw new OMException("Cannot set prefix on an information item without namespace");
            }
        }
        else {
            ajc$this_.internalSetNamespace((OMNamespace)new OMNamespaceImpl(ns.getNamespaceURI(), prefix));
        }
    }
    
    public static AxiomNamedInformationItemSupport aspectOf() {
        if (AxiomNamedInformationItemSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport", AxiomNamedInformationItemSupport.ajc$initFailureCause);
        }
        return AxiomNamedInformationItemSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomNamedInformationItemSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomNamedInformationItemSupport();
    }
}
