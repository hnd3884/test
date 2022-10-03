package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.impl.intf.AxiomCoreParentNode;
import org.apache.axiom.om.impl.common.AxiomCoreParentNodeSupport;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.core.CoreComment;
import org.apache.axiom.core.CoreCommentSupport;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.NodeFilter;
import org.apache.axiom.core.NodeIterator;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreCharacterDataContainingParentNode;
import org.apache.axiom.core.CoreCharacterDataContainingParentNodeSupport;
import org.apache.axiom.core.CoreDocumentFragment;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.impl.common.AxiomCommentSupport;
import org.apache.axiom.core.NonDeferringParentNode;
import org.apache.axiom.core.NonDeferringParentNodeSupport;
import org.apache.axiom.core.ElementAction;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.CoreParentNodeSupport;
import org.apache.axiom.om.impl.intf.AxiomComment;

public class OMCommentImpl extends OMLeafNode implements AxiomComment
{
    public Object content;
    
    public OMCommentImpl() {
        CoreParentNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(this);
    }
    
    public final void build() {
        NonDeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$build(this);
    }
    
    public void buildNext() {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$buildNext(this);
    }
    
    public final void buildWithAttachments() {
        AxiomCommentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$buildWithAttachments(this);
    }
    
    public final <T> void cloneChildrenIfNecessary(final ClonePolicy<T> policy, final T options, final CoreNode clone) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$cloneChildrenIfNecessary(this, policy, options, clone);
    }
    
    public final void coreAppendChild(final CoreChildNode child, final boolean fromBuilder) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChild(this, child, fromBuilder);
    }
    
    public final void coreAppendChildren(final CoreDocumentFragment fragment) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChildren(this, fragment);
    }
    
    public final Object coreGetCharacterData() {
        return CoreCharacterDataContainingParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataContainingParentNodeSupport$org_apache_axiom_core_CoreCharacterDataContainingParentNode$coreGetCharacterData(this);
    }
    
    public final <T extends CoreElement> NodeIterator<T> coreGetElements(final Axis axis, final Class<T> type, final ElementMatcher<? super T> matcher, final String namespaceURI, final String name, final Semantics semantics) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetElements(this, axis, type, matcher, namespaceURI, name, semantics);
    }
    
    public CoreChildNode coreGetFirstChild() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(this);
    }
    
    public final CoreChildNode coreGetFirstChild(final NodeFilter filter) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(this, filter);
    }
    
    public final CoreChildNode coreGetFirstChildIfAvailable() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChildIfAvailable(this);
    }
    
    public final CoreChildNode coreGetLastChild() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastChild(this);
    }
    
    public final CoreChildNode coreGetLastChild(final NodeFilter filter) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastChild(this, filter);
    }
    
    public CoreChildNode coreGetLastKnownChild() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastKnownChild(this);
    }
    
    public final NodeType coreGetNodeType() {
        return CoreCommentSupport.ajc$interMethod$org_apache_axiom_core_CoreCommentSupport$org_apache_axiom_core_CoreComment$coreGetNodeType(this);
    }
    
    public final <T> NodeIterator<T> coreGetNodes(final Axis axis, final Class<T> type, final Semantics semantics) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetNodes(this, axis, type, semantics);
    }
    
    public final void coreRemoveChildren(final Semantics semantics) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreRemoveChildren(this, semantics);
    }
    
    public final void coreSetBuilder(final OMXMLParserWrapper builder) {
        NonDeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$coreSetBuilder(this, builder);
    }
    
    public final void coreSetCharacterData(final Object data, final Semantics semantics) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetCharacterData(this, data, semantics);
    }
    
    public final void coreSetState(final int state) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetState(this, state);
    }
    
    public void forceExpand() {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$forceExpand(this);
    }
    
    public final OMXMLParserWrapper getBuilder() {
        return NonDeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$getBuilder(this);
    }
    
    public int getState() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getState(this);
    }
    
    public final int getType() {
        return AxiomCommentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$getType(this);
    }
    
    public String getValue() {
        return AxiomCommentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$getValue(this);
    }
    
    public final <T> void init(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreCommentSupport.ajc$interMethod$org_apache_axiom_core_CoreCommentSupport$org_apache_axiom_core_CoreComment$init(this, policy, options, other);
    }
    
    public final void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        AxiomCommentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$internalSerialize(this, serializer, format, cache);
    }
    
    public final boolean isComplete() {
        return AxiomCoreParentNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(this);
    }
    
    public boolean isExpanded() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$isExpanded(this);
    }
    
    public void setValue(final String text) {
        AxiomCommentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$setValue(this, text);
    }
}
