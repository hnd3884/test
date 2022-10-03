package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.om.impl.common.AxiomInformationItemSupport;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.impl.llom.factory.LLOMNodeFactory;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreNodeSupport;
import org.apache.axiom.om.impl.intf.AxiomInformationItem;

public abstract class OMInformationItemImpl implements AxiomInformationItem
{
    public int flags;
    
    public OMInformationItemImpl() {
        CoreNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags(this);
    }
    
    public NodeFactory coreGetNodeFactory() {
        return LLOMNodeFactory.INSTANCE;
    }
    
    public final OMMetaFactory getMetaFactory() {
        return (OMMetaFactory)OMLinkedListMetaFactory.INSTANCE;
    }
    
    public final OMInformationItem clone(final OMCloneOptions options) {
        return AxiomInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$clone(this, options);
    }
    
    public final <T> CoreNode coreClone(final ClonePolicy<T> policy, final T options) {
        return CoreNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreClone(this, policy, options);
    }
    
    public final <T extends CoreNode> T coreCreateNode(final Class<T> type) {
        return CoreNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreCreateNode(this, type);
    }
    
    public Class<? extends CoreNode> coreGetNodeClass() {
        return CoreNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreGetNodeClass(this);
    }
    
    public final CoreDocument coreGetOwnerDocument(final boolean create) {
        return CoreNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreGetOwnerDocument(this, create);
    }
    
    public final boolean coreHasSameOwnerDocument(final CoreNode other) {
        return CoreNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreHasSameOwnerDocument(this, other);
    }
    
    public OMFactory getOMFactory() {
        return AxiomInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(this);
    }
    
    public <T> void initAncillaryData(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$initAncillaryData(this, policy, options, other);
    }
    
    public final <T> CoreNode shallowClone(final ClonePolicy<T> policy, final T options) {
        return CoreNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$shallowClone(this, policy, options);
    }
    
    public void updateFiliation(final CoreNode creator) {
        CoreNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$updateFiliation(this, creator);
    }
}
