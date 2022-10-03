package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.CoreLeafNode;
import org.apache.axiom.core.CoreLeafNodeSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.impl.common.AxiomEntityReferenceSupport;
import org.apache.axiom.om.impl.intf.AxiomCoreLeafNode;
import org.apache.axiom.om.impl.common.AxiomCoreLeafNodeSupport;
import org.apache.axiom.core.CoreEntityReference;
import org.apache.axiom.core.CoreEntityReferenceSupport;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;

public class OMEntityReferenceImpl extends OMLeafNode implements AxiomEntityReference
{
    public String name;
    public String replacementText;
    
    public OMEntityReferenceImpl() {
        CoreEntityReferenceSupport.ajc$interFieldInit$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$name(this);
        CoreEntityReferenceSupport.ajc$interFieldInit$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$replacementText(this);
    }
    
    public void build() {
        AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$build(this);
    }
    
    public final void buildWithAttachments() {
        AxiomEntityReferenceSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$buildWithAttachments(this);
    }
    
    public final <T> void cloneChildrenIfNecessary(final ClonePolicy<T> policy, final T options, final CoreNode clone) {
        CoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreLeafNodeSupport$org_apache_axiom_core_CoreLeafNode$cloneChildrenIfNecessary(this, policy, options, clone);
    }
    
    public final String coreGetName() {
        return CoreEntityReferenceSupport.ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreGetName(this);
    }
    
    public final NodeType coreGetNodeType() {
        return CoreEntityReferenceSupport.ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreGetNodeType(this);
    }
    
    public final String coreGetReplacementText() {
        return CoreEntityReferenceSupport.ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreGetReplacementText(this);
    }
    
    public final void coreSetName(final String name) {
        CoreEntityReferenceSupport.ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreSetName(this, name);
    }
    
    public final void coreSetReplacementText(final String replacementText) {
        CoreEntityReferenceSupport.ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreSetReplacementText(this, replacementText);
    }
    
    public final OMXMLParserWrapper getBuilder() {
        return AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$getBuilder(this);
    }
    
    public final String getName() {
        return AxiomEntityReferenceSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$getName(this);
    }
    
    public final String getReplacementText() {
        return AxiomEntityReferenceSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$getReplacementText(this);
    }
    
    public final int getType() {
        return AxiomEntityReferenceSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$getType(this);
    }
    
    public final <T> void init(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreEntityReferenceSupport.ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$init(this, policy, options, other);
    }
    
    public final void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        AxiomEntityReferenceSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$internalSerialize(this, serializer, format, cache);
    }
    
    public final boolean isComplete() {
        return AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$isComplete(this);
    }
}
