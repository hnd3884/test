package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import java.util.Iterator;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreElementSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreElementSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreElementSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute(final CoreElement ajc$this_) {
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$beforeDetach(final CoreElement ajc$this_) {
        if (CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getState(ajc$this_) == 1 && DeferringParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$getBuilder(ajc$this_) == CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParent(ajc$this_).getBuilder()) {
            ajc$this_.build();
        }
    }
    
    public static CoreAttribute ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetFirstAttribute(final CoreElement ajc$this_) {
        CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$forceExpand(ajc$this_);
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$internalSetFirstAttribute(final CoreElement ajc$this_, final CoreAttribute firstAttribute) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute(firstAttribute);
    }
    
    public static CoreAttribute ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetLastAttribute(final CoreElement ajc$this_) {
        CoreAttribute previousAttribute = null;
        for (CoreAttribute attribute = ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute(); attribute != null; attribute = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attribute)) {
            previousAttribute = attribute;
        }
        return previousAttribute;
    }
    
    public static CoreAttribute ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetAttribute(final CoreElement ajc$this_, final AttributeMatcher matcher, final String namespaceURI, final String name) {
        CoreAttribute attr;
        for (attr = ajc$this_.coreGetFirstAttribute(); attr != null && !matcher.matches(attr, namespaceURI, name); attr = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attr)) {}
        return attr;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreAppendAttribute(final CoreElement ajc$this_, final CoreAttribute attr) {
        CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalRemove(attr, null, ajc$this_);
        final CoreAttribute lastAttribute = ajc$this_.coreGetLastAttribute();
        if (lastAttribute == null) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute(attr);
        }
        else {
            CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetNextAttribute(lastAttribute, attr);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreSetAttribute(final CoreElement ajc$this_, final AttributeMatcher matcher, final String namespaceURI, final String name, final String prefix, final String value) {
        CoreAttribute attr = ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute();
        CoreAttribute previousAttr = null;
        while (attr != null && !matcher.matches(attr, namespaceURI, name)) {
            previousAttr = attr;
            attr = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attr);
        }
        if (attr == null) {
            final CoreAttribute newAttr = matcher.createAttribute(ajc$this_, namespaceURI, name, prefix, value);
            if (previousAttr == null) {
                ajc$this_.coreAppendAttribute(newAttr);
            }
            else {
                CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$insertAttributeAfter(previousAttr, newAttr);
            }
        }
        else {
            matcher.update(attr, prefix, value);
        }
    }
    
    public static CoreAttribute ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreSetAttribute(final CoreElement ajc$this_, final AttributeMatcher matcher, final CoreAttribute attr, final Semantics semantics) {
        if (CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetOwnerElement(attr) == ajc$this_) {
            return attr;
        }
        CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalRemove(attr, null, ajc$this_);
        final String namespaceURI = matcher.getNamespaceURI(attr);
        final String name = matcher.getName(attr);
        CoreAttribute existingAttr = ajc$this_.coreGetFirstAttribute();
        CoreAttribute previousAttr = null;
        while (existingAttr != null && !matcher.matches(existingAttr, namespaceURI, name)) {
            previousAttr = existingAttr;
            existingAttr = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(existingAttr);
        }
        if (existingAttr == null) {
            if (previousAttr == null) {
                ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute(attr);
            }
            else {
                CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetNextAttribute(previousAttr, attr);
            }
        }
        else {
            if (previousAttr == null) {
                ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute(attr);
            }
            else {
                CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetNextAttribute(previousAttr, attr);
            }
            CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalUnsetOwnerElement(existingAttr, semantics.getDetachPolicy().getNewOwnerDocument(ajc$this_));
            CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetNextAttribute(attr, CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(existingAttr));
            CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetNextAttribute(existingAttr, null);
        }
        return existingAttr;
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreRemoveAttribute(final CoreElement ajc$this_, final AttributeMatcher matcher, final String namespaceURI, final String name, final Semantics semantics) {
        final CoreAttribute att = ajc$this_.coreGetAttribute(matcher, namespaceURI, name);
        if (att != null) {
            CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreRemove(att, semantics);
            return true;
        }
        return false;
    }
    
    public static <T extends CoreAttribute, S> Iterator<S> ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetAttributesByType(final CoreElement ajc$this_, final Class<T> type, final Mapper<T, S> mapper, final Semantics semantics) {
        return AttributeIterator.create(ajc$this_, type, mapper, semantics);
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreLookupNamespaceURI(final CoreElement ajc$this_, final String prefix, final Semantics semantics) {
        if (!semantics.isUseStrictNamespaceLookup()) {
            final String namespaceURI = ajc$this_.getImplicitNamespaceURI(prefix);
            if (namespaceURI != null) {
                return namespaceURI;
            }
        }
        for (CoreAttribute attr = ajc$this_.coreGetFirstAttribute(); attr != null; attr = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attr)) {
            if (attr instanceof CoreNamespaceDeclaration) {
                final CoreNamespaceDeclaration decl = (CoreNamespaceDeclaration)attr;
                if (prefix.equals(decl.coreGetDeclaredPrefix())) {
                    return CoreCharacterDataContainingParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreCharacterDataContainingParentNodeSupport$org_apache_axiom_core_CoreCharacterDataContainingParentNode$coreGetCharacterData(decl).toString();
                }
            }
        }
        final CoreElement parentElement = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParentElement(ajc$this_);
        if (parentElement != null) {
            return parentElement.coreLookupNamespaceURI(prefix, semantics);
        }
        if (prefix.length() == 0) {
            return "";
        }
        return null;
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreLookupPrefix(final CoreElement ajc$this_, final String namespaceURI, final Semantics semantics) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        }
        if (!semantics.isUseStrictNamespaceLookup()) {
            final String prefix = ajc$this_.getImplicitPrefix(namespaceURI);
            if (prefix != null) {
                return prefix;
            }
        }
        for (CoreAttribute attr = ajc$this_.coreGetFirstAttribute(); attr != null; attr = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attr)) {
            if (attr instanceof CoreNamespaceDeclaration) {
                final CoreNamespaceDeclaration decl = (CoreNamespaceDeclaration)attr;
                if (CoreCharacterDataContainingParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreCharacterDataContainingParentNodeSupport$org_apache_axiom_core_CoreCharacterDataContainingParentNode$coreGetCharacterData(decl).toString().equals(namespaceURI)) {
                    return decl.coreGetDeclaredPrefix();
                }
            }
        }
        final CoreElement parentElement = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParentElement(ajc$this_);
        if (parentElement == null) {
            return null;
        }
        final String prefix2 = parentElement.coreLookupPrefix(namespaceURI, semantics);
        if (!semantics.isUseStrictNamespaceLookup() && ajc$this_.getImplicitNamespaceURI(prefix2) != null) {
            return null;
        }
        for (CoreAttribute attr2 = ajc$this_.coreGetFirstAttribute(); attr2 != null; attr2 = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attr2)) {
            if (attr2 instanceof CoreNamespaceDeclaration) {
                final CoreNamespaceDeclaration decl2 = (CoreNamespaceDeclaration)attr2;
                if (decl2.coreGetDeclaredPrefix().equals(prefix2)) {
                    return null;
                }
            }
        }
        return prefix2;
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$init(final CoreElement ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        final CoreElement o = (CoreElement)other;
        ajc$this_.initSource(policy, options, o);
        ajc$this_.initName(o);
        if (CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$isExpanded(ajc$this_)) {
            for (CoreAttribute attr = o.coreGetFirstAttribute(); attr != null; attr = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attr)) {
                ajc$this_.coreAppendAttribute((CoreAttribute)CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreClone(attr, policy, options));
            }
        }
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$initSource(final CoreElement ajc$this_, final ClonePolicy<T> policy, final T options, final CoreElement other) {
    }
    
    public static CoreElementSupport aspectOf() {
        if (CoreElementSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreElementSupport", CoreElementSupport.ajc$initFailureCause);
        }
        return CoreElementSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreElementSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreElementSupport();
    }
}
