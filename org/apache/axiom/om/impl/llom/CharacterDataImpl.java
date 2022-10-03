package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.impl.common.AxiomCharacterDataNodeSupport;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.CoreLeafNode;
import org.apache.axiom.core.CoreLeafNodeSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.impl.intf.AxiomCoreLeafNode;
import org.apache.axiom.om.impl.common.AxiomCoreLeafNodeSupport;
import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreCharacterDataNodeSupport;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;

public class CharacterDataImpl extends OMTextImpl implements AxiomCharacterDataNode
{
    public Object data;
    
    public CharacterDataImpl() {
        CoreCharacterDataNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data(this);
    }
    
    public void build() {
        AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$build(this);
    }
    
    public final <T> void cloneChildrenIfNecessary(final ClonePolicy<T> policy, final T options, final CoreNode clone) {
        CoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreLeafNodeSupport$org_apache_axiom_core_CoreLeafNode$cloneChildrenIfNecessary(this, policy, options, clone);
    }
    
    public final Object coreGetCharacterData() {
        return CoreCharacterDataNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreGetCharacterData(this);
    }
    
    public final NodeType coreGetNodeType() {
        return CoreCharacterDataNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreGetNodeType(this);
    }
    
    public final boolean coreIsIgnorable() {
        return CoreCharacterDataNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreIsIgnorable(this);
    }
    
    public final void coreSetCharacterData(final Object data) {
        CoreCharacterDataNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreSetCharacterData(this, data);
    }
    
    public final void coreSetCharacterData(final Object data, final Semantics semantics) {
        CoreCharacterDataNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreSetCharacterData(this, data, semantics);
    }
    
    public final void coreSetIgnorable(final boolean ignorable) {
        CoreCharacterDataNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreSetIgnorable(this, ignorable);
    }
    
    public final OMXMLParserWrapper getBuilder() {
        return AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$getBuilder(this);
    }
    
    public final int getType() {
        return AxiomCharacterDataNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCharacterDataNodeSupport$org_apache_axiom_om_impl_intf_AxiomCharacterDataNode$getType(this);
    }
    
    public final <T> void init(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreCharacterDataNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$init(this, policy, options, other);
    }
    
    public final boolean isComplete() {
        return AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$isComplete(this);
    }
}
