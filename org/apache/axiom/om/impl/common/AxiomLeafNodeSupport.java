package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.OMOutputFormat;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import org.apache.axiom.om.impl.intf.AxiomLeafNode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomLeafNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomLeafNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomLeafNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serialize(final AxiomLeafNode ajc$this_, final OutputStream output) throws XMLStreamException {
        throw new UnsupportedOperationException("Only supported on OMContainer instances");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serialize(final AxiomLeafNode ajc$this_, final Writer writer) throws XMLStreamException {
        throw new UnsupportedOperationException("Only supported on OMContainer instances");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serializeAndConsume(final AxiomLeafNode ajc$this_, final OutputStream output) throws XMLStreamException {
        throw new UnsupportedOperationException("Only supported on OMContainer instances");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serializeAndConsume(final AxiomLeafNode ajc$this_, final Writer writer) throws XMLStreamException {
        throw new UnsupportedOperationException("Only supported on OMContainer instances");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serialize(final AxiomLeafNode ajc$this_, final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        throw new UnsupportedOperationException("Only supported on OMContainer instances");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serialize(final AxiomLeafNode ajc$this_, final Writer writer2, final OMOutputFormat format) throws XMLStreamException {
        throw new UnsupportedOperationException("Only supported on OMContainer instances");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serializeAndConsume(final AxiomLeafNode ajc$this_, final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        throw new UnsupportedOperationException("Only supported on OMContainer instances");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$serializeAndConsume(final AxiomLeafNode ajc$this_, final Writer writer2, final OMOutputFormat format) throws XMLStreamException {
        throw new UnsupportedOperationException("Only supported on OMContainer instances");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$setComplete(final AxiomLeafNode ajc$this_, final boolean state) {
        if (!state) {
            throw new IllegalStateException();
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomLeafNode$discard(final AxiomLeafNode ajc$this_) throws OMException {
        AxiomChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$detach(ajc$this_);
    }
    
    public static AxiomLeafNodeSupport aspectOf() {
        if (AxiomLeafNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomLeafNodeSupport", AxiomLeafNodeSupport.ajc$initFailureCause);
        }
        return AxiomLeafNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomLeafNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomLeafNodeSupport();
    }
}
