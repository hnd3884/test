package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import javax.activation.DataHandler;
import org.apache.axiom.om.impl.common.AxiomTextSupport;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.om.impl.intf.AxiomText;

public abstract class OMTextImpl extends OMLeafNode implements AxiomText
{
    public final void buildWithAttachments() {
        AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$buildWithAttachments(this);
    }
    
    public final String getContentID() {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getContentID(this);
    }
    
    public final DataHandler getDataHandler() {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getDataHandler(this);
    }
    
    public final OMNamespace getNamespace() {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getNamespace(this);
    }
    
    public final String getText() throws OMException {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getText(this);
    }
    
    public final QName getTextAsQName() throws OMException {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getTextAsQName(this);
    }
    
    public final char[] getTextCharacters() {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getTextCharacters(this);
    }
    
    public final void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$internalSerialize(this, serializer, format, cache);
    }
    
    public final boolean isBinary() {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$isBinary(this);
    }
    
    public final boolean isCharacters() {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$isCharacters(this);
    }
    
    public final boolean isOptimized() {
        return AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$isOptimized(this);
    }
    
    public final void setBinary(final boolean binary) {
        AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$setBinary(this, binary);
    }
    
    public final void setContentID(final String cid) {
        AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$setContentID(this, cid);
    }
    
    public final void setOptimize(final boolean optimize) {
        AxiomTextSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$setOptimize(this, optimize);
    }
}
