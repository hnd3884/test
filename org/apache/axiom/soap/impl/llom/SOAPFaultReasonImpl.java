package org.apache.axiom.soap.impl.llom;

import java.util.Iterator;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import java.util.ArrayList;
import java.util.List;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultReason;

public abstract class SOAPFaultReasonImpl extends SOAPElement implements AxiomSOAPFaultReason
{
    public List getAllSoapTexts() {
        final List faultTexts = new ArrayList(1);
        final Iterator childrenIter = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildren(this);
        while (childrenIter.hasNext()) {
            final OMNode node = childrenIter.next();
            if (node.getType() == 1 && node instanceof SOAPFaultText) {
                faultTexts.add(node);
            }
        }
        return faultTexts;
    }
    
    public SOAPFaultText getSOAPFaultText(final String language) {
        final Iterator childrenIter = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildren(this);
        while (childrenIter.hasNext()) {
            final OMNode node = childrenIter.next();
            if (node.getType() == 1 && node instanceof SOAPFaultText && (language == null || language.equals(((SOAPFaultText)node).getLang()))) {
                return (SOAPFaultText)node;
            }
        }
        return null;
    }
}
