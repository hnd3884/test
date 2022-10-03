package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.impl.common.AxiomSerializableSupport;
import org.apache.axiom.om.impl.intf.AxiomSerializable;

public abstract class OMSerializableImpl extends OMInformationItemImpl implements AxiomSerializable
{
    public void close(final boolean build) {
        AxiomSerializableSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$close(this, build);
    }
    
    public final void serialize(final XMLStreamWriter xmlWriter) throws XMLStreamException {
        AxiomSerializableSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$serialize(this, xmlWriter);
    }
    
    public final void serialize(final XMLStreamWriter xmlWriter, final boolean cache) throws XMLStreamException {
        AxiomSerializableSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$serialize(this, xmlWriter, cache);
    }
    
    public final void serializeAndConsume(final XMLStreamWriter xmlWriter) throws XMLStreamException {
        AxiomSerializableSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$serializeAndConsume(this, xmlWriter);
    }
}
