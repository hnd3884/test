package org.apache.axiom.om.impl.common;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.DetachPolicy;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NamespaceDeclarationMatcher;
import org.apache.axiom.core.NSAwareAttributeMatcher;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.Semantics;

public final class AxiomSemantics implements Semantics
{
    public static final AxiomSemantics INSTANCE;
    public static final AttributeMatcher ATTRIBUTE_MATCHER;
    public static final AttributeMatcher NAMESPACE_DECLARATION_MATCHER;
    public static final ClonePolicy<OMCloneOptions> CLONE_POLICY;
    
    static {
        INSTANCE = new AxiomSemantics();
        ATTRIBUTE_MATCHER = new NSAwareAttributeMatcher(AxiomSemantics.INSTANCE, false, false);
        NAMESPACE_DECLARATION_MATCHER = new NamespaceDeclarationMatcher(AxiomSemantics.INSTANCE);
        CLONE_POLICY = new ClonePolicy<OMCloneOptions>() {
            public Class<? extends CoreNode> getTargetNodeClass(final OMCloneOptions options, final CoreNode node) {
                if (options != null && options.isPreserveModel()) {
                    return node.coreGetNodeClass();
                }
                if (options != null && options.isCopyOMDataSources() && node instanceof AxiomSourcedElement) {
                    return AxiomSourcedElement.class;
                }
                return node.coreGetNodeType().getInterface();
            }
            
            public boolean repairNamespaces(final OMCloneOptions options) {
                return true;
            }
            
            public boolean cloneAttributes(final OMCloneOptions options) {
                return true;
            }
            
            public boolean cloneChildren(final OMCloneOptions options, final NodeType nodeType) {
                return true;
            }
            
            public void postProcess(final OMCloneOptions options, final CoreNode clone) {
                if (clone instanceof AxiomElement && ((AxiomElement)clone).isExpanded()) {
                    final AxiomElement element = (AxiomElement)clone;
                    NSUtil.handleNamespace(element, AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getNamespace(element), false, true);
                    for (CoreAttribute attr = element.coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
                        if (attr instanceof AxiomAttribute) {
                            NSUtil.handleNamespace(element, AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getNamespace((AxiomNamedInformationItem)attr), true, true);
                        }
                    }
                }
            }
        };
    }
    
    private AxiomSemantics() {
    }
    
    public DetachPolicy getDetachPolicy() {
        return DetachPolicy.NEW_DOCUMENT;
    }
    
    public boolean isUseStrictNamespaceLookup() {
        return true;
    }
    
    public boolean isParentNode(final NodeType nodeType) {
        return nodeType == NodeType.DOCUMENT || nodeType == NodeType.NS_AWARE_ELEMENT;
    }
    
    public RuntimeException toUncheckedException(final CoreModelException ex) {
        return (RuntimeException)AxiomExceptionTranslator.translate(ex);
    }
}
