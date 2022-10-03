package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.ClonePolicy;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.impl.common.AxiomSourcedElementSupport;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public class OMSourcedElementImpl extends OMElementImpl implements AxiomSourcedElement
{
    public OMDataSource dataSource;
    public OMNamespace definedNamespace;
    public boolean definedNamespaceSet;
    public boolean isExpanded;
    
    public OMSourcedElementImpl() {
        AxiomSourcedElementSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource(this);
        AxiomSourcedElementSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace(this);
        AxiomSourcedElementSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet(this);
        AxiomSourcedElementSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(this);
    }
    
    public Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$coreGetNodeClass(this);
    }
    
    @Override
    public void forceExpand() {
        AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$forceExpand(this);
    }
    
    public OMDataSource getDataSource() {
        return AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getDataSource(this);
    }
    
    @Override
    public OMNamespace getNamespace() throws OMException {
        return AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getNamespace(this);
    }
    
    public Object getObject(final Class dataSourceClass) {
        return AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getObject(this, dataSourceClass);
    }
    
    @Override
    public QName getQName() {
        return AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getQName(this);
    }
    
    @Override
    public XMLStreamReader getXMLStreamReader(final boolean cache, final OMXMLStreamReaderConfiguration configuration) {
        return AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getXMLStreamReader(this, cache, configuration);
    }
    
    public void init(final String localName, final OMNamespace ns, final OMDataSource source) {
        AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(this, localName, ns, source);
    }
    
    public void init(final QName qName, final OMDataSource source) {
        AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(this, qName, source);
    }
    
    public void init(final OMDataSource source) {
        AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(this, source);
    }
    
    @Override
    public <T> void initSource(final ClonePolicy<T> policy, final T options, final CoreElement other) {
        AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$initSource(this, policy, options, other);
    }
    
    @Override
    public void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$internalSerialize(this, serializer, format, cache);
    }
    
    @Override
    public boolean isExpanded() {
        return AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(this);
    }
    
    @Override
    public void setComplete(final boolean complete) {
        AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$setComplete(this, complete);
    }
    
    public OMDataSource setDataSource(final OMDataSource dataSource) {
        return AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$setDataSource(this, dataSource);
    }
    
    @Override
    public final void updateLocalName() {
        AxiomSourcedElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$updateLocalName(this);
    }
}
