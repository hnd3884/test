package org.apache.axiom.om.impl.llom;

import java.io.Writer;
import org.apache.axiom.om.OMOutputFormat;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.AxiomLeafNodeSupport;
import org.apache.axiom.om.impl.intf.AxiomLeafNode;

public abstract class OMLeafNode extends OMNodeImpl implements AxiomLeafNode
{
    public final void discard() throws OMException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$discard(this);
    }
    
    public final void serialize(final OutputStream output) throws XMLStreamException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serialize(this, output);
    }
    
    public final void serialize(final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serialize(this, output, format);
    }
    
    public final void serialize(final Writer writer) throws XMLStreamException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serialize(this, writer);
    }
    
    public final void serialize(final Writer writer2, final OMOutputFormat format) throws XMLStreamException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serialize(this, writer2, format);
    }
    
    public final void serializeAndConsume(final OutputStream output) throws XMLStreamException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serializeAndConsume(this, output);
    }
    
    public final void serializeAndConsume(final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serializeAndConsume(this, output, format);
    }
    
    public final void serializeAndConsume(final Writer writer) throws XMLStreamException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serializeAndConsume(this, writer);
    }
    
    public final void serializeAndConsume(final Writer writer2, final OMOutputFormat format) throws XMLStreamException {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serializeAndConsume(this, writer2, format);
    }
    
    public final void setComplete(final boolean state) {
        AxiomLeafNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$setComplete(this, state);
    }
}
