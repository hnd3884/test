package org.apache.axiom.soap.impl.llom;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.StringWriter;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.om.OMNode;
import java.util.List;
import org.apache.axiom.om.OMElement;
import java.util.ArrayList;
import org.apache.axiom.soap.impl.common.MURoleChecker;
import org.apache.axiom.soap.impl.common.RoleChecker;
import org.apache.axiom.soap.impl.common.Checker;
import org.apache.axiom.soap.impl.common.HeaderIterator;
import org.apache.axiom.soap.impl.common.RolePlayerChecker;
import java.util.Iterator;
import org.apache.axiom.soap.RolePlayer;
import javax.xml.namespace.QName;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.common.AxiomSOAPElementSupport;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeader;

public abstract class SOAPHeaderImpl extends SOAPElement implements AxiomSOAPHeader
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog((Class)SOAPHeaderImpl.class);
    }
    
    public SOAPHeaderBlock addHeaderBlock(final String localName, OMNamespace ns) throws OMException {
        if (ns == null || ns.getNamespaceURI().length() == 0) {
            throw new OMException("All the SOAP Header blocks should be namespace qualified");
        }
        final OMNamespace namespace = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$findNamespace(this, ns.getNamespaceURI(), ns.getPrefix());
        if (namespace != null) {
            ns = namespace;
        }
        SOAPHeaderBlock soapHeaderBlock;
        try {
            soapHeaderBlock = ((SOAPFactory)AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(this)).createSOAPHeaderBlock(localName, ns, (SOAPHeader)this);
        }
        catch (final SOAPProcessingException e) {
            throw new OMException((Throwable)e);
        }
        ((OMNodeEx)soapHeaderBlock).setComplete(true);
        return soapHeaderBlock;
    }
    
    public SOAPHeaderBlock addHeaderBlock(final QName qname) throws OMException {
        return this.addHeaderBlock(qname.getLocalPart(), AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(this).createOMNamespace(qname.getNamespaceURI(), qname.getPrefix()));
    }
    
    public Iterator getHeadersToProcess(final RolePlayer rolePlayer) {
        return new HeaderIterator((SOAPHeader)this, new RolePlayerChecker(rolePlayer));
    }
    
    public Iterator getHeadersToProcess(final RolePlayer rolePlayer, final String namespace) {
        return new HeaderIterator((SOAPHeader)this, new RolePlayerChecker(rolePlayer, namespace));
    }
    
    public Iterator examineHeaderBlocks(final String role) {
        return new HeaderIterator((SOAPHeader)this, new RoleChecker(role));
    }
    
    public abstract Iterator extractHeaderBlocks(final String p0);
    
    public Iterator examineMustUnderstandHeaderBlocks(final String actor) {
        return new HeaderIterator((SOAPHeader)this, new MURoleChecker(actor));
    }
    
    public Iterator examineAllHeaderBlocks() {
        class DefaultChecker implements Checker
        {
            public boolean checkHeader(final SOAPHeaderBlock header) {
                return true;
            }
        }
        return new HeaderIterator((SOAPHeader)this, new DefaultChecker());
    }
    
    public Iterator extractAllHeaderBlocks() {
        final List result = new ArrayList();
        final Iterator iter = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getChildElements(this);
        while (iter.hasNext()) {
            final OMElement headerBlock = iter.next();
            iter.remove();
            result.add(headerBlock);
        }
        return result.iterator();
    }
    
    public ArrayList getHeaderBlocksWithNSURI(final String nsURI) {
        ArrayList headers = null;
        OMElement header = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(this);
        if (header != null) {
            headers = new ArrayList();
        }
        for (OMNode node = (OMNode)header; node != null; node = node.getNextOMSibling()) {
            if (node.getType() == 1) {
                header = (OMElement)node;
                final OMNamespace namespace = header.getNamespace();
                if (nsURI == null) {
                    if (namespace == null) {
                        headers.add(header);
                    }
                }
                else if (namespace != null && nsURI.equals(namespace.getNamespaceURI())) {
                    headers.add(header);
                }
            }
        }
        return headers;
    }
    
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAPEnvelopeImpl)) {
            throw new SOAPProcessingException("Expecting an implementation of SOAP Envelope as the parent. But received some other implementation");
        }
    }
    
    @Override
    public void addChild(final OMNode child, final boolean fromBuilder) {
        if (SOAPHeaderImpl.log.isDebugEnabled() && child instanceof OMElement && !(child instanceof SOAPHeaderBlock)) {
            final Exception e = (Exception)new SOAPProcessingException("An attempt was made to add a normal OMElement as a child of a SOAPHeader.  This is not supported.  The child should be a SOAPHeaderBlock.");
            SOAPHeaderImpl.log.debug((Object)exceptionToString(e));
        }
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(this, child, fromBuilder);
    }
    
    public static String exceptionToString(final Throwable e) {
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter(sw);
        final PrintWriter pw = new PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        final String text = sw.getBuffer().toString();
        return text;
    }
}
