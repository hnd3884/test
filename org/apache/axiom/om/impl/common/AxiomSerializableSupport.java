package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.common.serializer.push.stax.StAXSerializer;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.impl.intf.AxiomSerializable;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSerializableSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSerializableSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSerializableSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$serialize(final AxiomSerializable ajc$this_, final XMLStreamWriter xmlWriter) throws XMLStreamException {
        ajc$this_.serialize(xmlWriter, true);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$serializeAndConsume(final AxiomSerializable ajc$this_, final XMLStreamWriter xmlWriter) throws XMLStreamException {
        ajc$this_.serialize(xmlWriter, false);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$serialize(final AxiomSerializable ajc$this_, final XMLStreamWriter xmlWriter, final boolean cache) throws XMLStreamException {
        final MTOMXMLStreamWriter writer = (xmlWriter instanceof MTOMXMLStreamWriter) ? xmlWriter : new MTOMXMLStreamWriter(xmlWriter);
        try {
            ajc$this_.internalSerialize(new StAXSerializer((OMSerializable)ajc$this_, (XMLStreamWriter)writer), writer.getOutputFormat(), cache);
        }
        catch (final OutputException ex) {
            throw (XMLStreamException)ex.getCause();
        }
        writer.flush();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$close(final AxiomSerializable ajc$this_, final boolean build) {
        final OMXMLParserWrapper builder = ajc$this_.getBuilder();
        if (build) {
            ajc$this_.build();
        }
        ajc$this_.setComplete(true);
        if (builder instanceof StAXBuilder && !((StAXBuilder)builder).isClosed()) {
            ((StAXBuilder)builder).close();
        }
    }
    
    public static AxiomSerializableSupport aspectOf() {
        if (AxiomSerializableSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomSerializableSupport", AxiomSerializableSupport.ajc$initFailureCause);
        }
        return AxiomSerializableSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSerializableSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSerializableSupport();
    }
}
