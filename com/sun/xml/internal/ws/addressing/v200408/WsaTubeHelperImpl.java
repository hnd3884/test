package com.sun.xml.internal.ws.addressing.v200408;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;

public class WsaTubeHelperImpl extends WsaTubeHelper
{
    static final JAXBContext jc;
    
    public WsaTubeHelperImpl(final WSDLPort wsdlPort, final SEIModel seiModel, final WSBinding binding) {
        super(binding, seiModel, wsdlPort);
    }
    
    private Marshaller createMarshaller() throws JAXBException {
        final Marshaller marshaller = WsaTubeHelperImpl.jc.createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        return marshaller;
    }
    
    @Override
    public final void getProblemActionDetail(final String action, final Element element) {
        final ProblemAction pa = new ProblemAction(action);
        try {
            this.createMarshaller().marshal(pa, element);
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public final void getInvalidMapDetail(final QName name, final Element element) {
        final ProblemHeaderQName phq = new ProblemHeaderQName(name);
        try {
            this.createMarshaller().marshal(phq, element);
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public final void getMapRequiredDetail(final QName name, final Element element) {
        this.getInvalidMapDetail(name, element);
    }
    
    static {
        try {
            jc = JAXBContext.newInstance(ProblemAction.class, ProblemHeaderQName.class);
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
}
