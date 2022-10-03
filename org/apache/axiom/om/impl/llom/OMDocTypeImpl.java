package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.shared.IDocumentTypeDeclaration;
import org.apache.axiom.shared.DocumentTypeDeclarationSupport;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.CoreLeafNode;
import org.apache.axiom.core.CoreLeafNodeSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.impl.common.AxiomDocTypeSupport;
import org.apache.axiom.om.impl.intf.AxiomCoreLeafNode;
import org.apache.axiom.om.impl.common.AxiomCoreLeafNodeSupport;
import org.apache.axiom.core.CoreDocumentTypeDeclaration;
import org.apache.axiom.core.CoreDocumentTypeDeclarationSupport;
import org.apache.axiom.om.impl.intf.AxiomDocType;

public class OMDocTypeImpl extends OMLeafNode implements AxiomDocType
{
    public String rootName;
    public String publicId;
    public String systemId;
    public String internalSubset;
    
    public OMDocTypeImpl() {
        CoreDocumentTypeDeclarationSupport.ajc$interFieldInit$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$rootName(this);
        CoreDocumentTypeDeclarationSupport.ajc$interFieldInit$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$publicId(this);
        CoreDocumentTypeDeclarationSupport.ajc$interFieldInit$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$systemId(this);
        CoreDocumentTypeDeclarationSupport.ajc$interFieldInit$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$internalSubset(this);
    }
    
    public void build() {
        AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$build(this);
    }
    
    public final void buildWithAttachments() {
        AxiomDocTypeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocTypeSupport$org_apache_axiom_om_impl_intf_AxiomDocType$buildWithAttachments(this);
    }
    
    public final <T> void cloneChildrenIfNecessary(final ClonePolicy<T> policy, final T options, final CoreNode clone) {
        CoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreLeafNodeSupport$org_apache_axiom_core_CoreLeafNode$cloneChildrenIfNecessary(this, policy, options, clone);
    }
    
    public final String coreGetInternalSubset() {
        return CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetInternalSubset(this);
    }
    
    public final NodeType coreGetNodeType() {
        return CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetNodeType(this);
    }
    
    public final String coreGetPublicId() {
        return CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetPublicId(this);
    }
    
    public final String coreGetRootName() {
        return CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetRootName(this);
    }
    
    public final String coreGetSystemId() {
        return CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreGetSystemId(this);
    }
    
    public final void coreSetInternalSubset(final String internalSubset) {
        CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreSetInternalSubset(this, internalSubset);
    }
    
    public final void coreSetPublicId(final String publicId) {
        CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreSetPublicId(this, publicId);
    }
    
    public final void coreSetRootName(final String rootName) {
        CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreSetRootName(this, rootName);
    }
    
    public final void coreSetSystemId(final String systemId) {
        CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$coreSetSystemId(this, systemId);
    }
    
    public final OMXMLParserWrapper getBuilder() {
        return AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$getBuilder(this);
    }
    
    public final String getInternalSubset() {
        return DocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_shared_DocumentTypeDeclarationSupport$org_apache_axiom_shared_IDocumentTypeDeclaration$getInternalSubset(this);
    }
    
    public final String getPublicId() {
        return DocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_shared_DocumentTypeDeclarationSupport$org_apache_axiom_shared_IDocumentTypeDeclaration$getPublicId(this);
    }
    
    public final String getRootName() {
        return AxiomDocTypeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocTypeSupport$org_apache_axiom_om_impl_intf_AxiomDocType$getRootName(this);
    }
    
    public final String getSystemId() {
        return DocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_shared_DocumentTypeDeclarationSupport$org_apache_axiom_shared_IDocumentTypeDeclaration$getSystemId(this);
    }
    
    public final int getType() {
        return AxiomDocTypeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocTypeSupport$org_apache_axiom_om_impl_intf_AxiomDocType$getType(this);
    }
    
    public final <T> void init(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreDocumentTypeDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentTypeDeclarationSupport$org_apache_axiom_core_CoreDocumentTypeDeclaration$init(this, policy, options, other);
    }
    
    public final void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        AxiomDocTypeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocTypeSupport$org_apache_axiom_om_impl_intf_AxiomDocType$internalSerialize(this, serializer, format, cache);
    }
    
    public final boolean isComplete() {
        return AxiomCoreLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$isComplete(this);
    }
}
