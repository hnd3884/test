package com.sun.xml.internal.ws.api.addressing;

import com.sun.xml.internal.ws.message.stream.OutboundStreamHeader;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.addressing.WsaTubeHelperImpl;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import com.sun.xml.internal.ws.api.WSBinding;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;

public enum AddressingVersion
{
    W3C("http://www.w3.org/2005/08/addressing", "wsa", "<EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\">\n    <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>\n</EndpointReference>", "http://www.w3.org/2006/05/addressing/wsdl", "http://www.w3.org/2006/05/addressing/wsdl", "http://www.w3.org/2005/08/addressing/anonymous", "http://www.w3.org/2005/08/addressing/none", new EPR(W3CEndpointReference.class, "Address", "ServiceName", "EndpointName", "InterfaceName", new QName("http://www.w3.org/2005/08/addressing", "Metadata", "wsa"), "ReferenceParameters", null)) {
        @Override
        String getActionMismatchLocalName() {
            return "ActionMismatch";
        }
        
        @Override
        public boolean isReferenceParameter(final String localName) {
            return localName.equals("ReferenceParameters");
        }
        
        @Override
        public WsaTubeHelper getWsaHelper(final WSDLPort wsdlPort, final SEIModel seiModel, final WSBinding binding) {
            return new WsaTubeHelperImpl(wsdlPort, seiModel, binding);
        }
        
        @Override
        String getMapRequiredLocalName() {
            return "MessageAddressingHeaderRequired";
        }
        
        @Override
        public String getMapRequiredText() {
            return "A required header representing a Message Addressing Property is not present";
        }
        
        @Override
        String getInvalidAddressLocalName() {
            return "InvalidAddress";
        }
        
        @Override
        String getInvalidMapLocalName() {
            return "InvalidAddressingHeader";
        }
        
        @Override
        public String getInvalidMapText() {
            return "A header representing a Message Addressing Property is not valid and the message cannot be processed";
        }
        
        @Override
        String getInvalidCardinalityLocalName() {
            return "InvalidCardinality";
        }
        
        @Override
        Header createReferenceParameterHeader(final XMLStreamBuffer mark, final String nsUri, final String localName) {
            return new OutboundReferenceParameterHeader(mark, nsUri, localName);
        }
        
        @Override
        String getIsReferenceParameterLocalName() {
            return "IsReferenceParameter";
        }
        
        @Override
        String getWsdlAnonymousLocalName() {
            return "Anonymous";
        }
        
        @Override
        public String getPrefix() {
            return "wsa";
        }
        
        @Override
        public String getWsdlPrefix() {
            return "wsaw";
        }
        
        @Override
        public Class<? extends WebServiceFeature> getFeatureClass() {
            return AddressingFeature.class;
        }
    }, 
    MEMBER("http://schemas.xmlsoap.org/ws/2004/08/addressing", "wsa", "<EndpointReference xmlns=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n    <Address>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</Address>\n</EndpointReference>", "http://schemas.xmlsoap.org/ws/2004/08/addressing", "http://schemas.xmlsoap.org/ws/2004/08/addressing/policy", "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous", "", new EPR(MemberSubmissionEndpointReference.class, "Address", "ServiceName", "PortName", "PortType", MemberSubmissionAddressingConstants.MEX_METADATA, "ReferenceParameters", "ReferenceProperties")) {
        @Override
        String getActionMismatchLocalName() {
            return "InvalidMessageInformationHeader";
        }
        
        @Override
        public boolean isReferenceParameter(final String localName) {
            return localName.equals("ReferenceParameters") || localName.equals("ReferenceProperties");
        }
        
        @Override
        public WsaTubeHelper getWsaHelper(final WSDLPort wsdlPort, final SEIModel seiModel, final WSBinding binding) {
            return new com.sun.xml.internal.ws.addressing.v200408.WsaTubeHelperImpl(wsdlPort, seiModel, binding);
        }
        
        @Override
        String getMapRequiredLocalName() {
            return "MessageInformationHeaderRequired";
        }
        
        @Override
        public String getMapRequiredText() {
            return "A required message information header, To, MessageID, or Action, is not present.";
        }
        
        @Override
        String getInvalidAddressLocalName() {
            return this.getInvalidMapLocalName();
        }
        
        @Override
        String getInvalidMapLocalName() {
            return "InvalidMessageInformationHeader";
        }
        
        @Override
        public String getInvalidMapText() {
            return "A message information header is not valid and the message cannot be processed.";
        }
        
        @Override
        String getInvalidCardinalityLocalName() {
            return this.getInvalidMapLocalName();
        }
        
        @Override
        Header createReferenceParameterHeader(final XMLStreamBuffer mark, final String nsUri, final String localName) {
            return new OutboundStreamHeader(mark, nsUri, localName);
        }
        
        @Override
        String getIsReferenceParameterLocalName() {
            return "";
        }
        
        @Override
        String getWsdlAnonymousLocalName() {
            return "";
        }
        
        @Override
        public String getPrefix() {
            return "wsa";
        }
        
        @Override
        public String getWsdlPrefix() {
            return "wsaw";
        }
        
        @Override
        public Class<? extends WebServiceFeature> getFeatureClass() {
            return MemberSubmissionAddressingFeature.class;
        }
    };
    
    public final String nsUri;
    public final String wsdlNsUri;
    public final EPR eprType;
    public final String policyNsUri;
    @NotNull
    public final String anonymousUri;
    @NotNull
    public final String noneUri;
    public final WSEndpointReference anonymousEpr;
    public final QName toTag;
    public final QName fromTag;
    public final QName replyToTag;
    public final QName faultToTag;
    public final QName actionTag;
    public final QName messageIDTag;
    public final QName relatesToTag;
    public final QName mapRequiredTag;
    public final QName actionMismatchTag;
    public final QName actionNotSupportedTag;
    public final String actionNotSupportedText;
    public final QName invalidMapTag;
    public final QName invalidCardinalityTag;
    public final QName invalidAddressTag;
    public final QName problemHeaderQNameTag;
    public final QName problemActionTag;
    public final QName faultDetailTag;
    public final QName fault_missingAddressInEpr;
    public final QName wsdlActionTag;
    public final QName wsdlExtensionTag;
    public final QName wsdlAnonymousTag;
    public final QName isReferenceParameterTag;
    private static final String EXTENDED_FAULT_NAMESPACE = "http://jax-ws.dev.java.net/addressing/fault";
    public static final String UNSET_OUTPUT_ACTION = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
    public static final String UNSET_INPUT_ACTION = "http://jax-ws.dev.java.net/addressing/input-action-not-set";
    public static final QName fault_duplicateAddressInEpr;
    
    private AddressingVersion(final String nsUri, final String prefix, final String anonymousEprString, final String wsdlNsUri, final String policyNsUri, final String anonymousUri, final String noneUri, final EPR eprType) {
        this.nsUri = nsUri;
        this.wsdlNsUri = wsdlNsUri;
        this.policyNsUri = policyNsUri;
        this.anonymousUri = anonymousUri;
        this.noneUri = noneUri;
        this.toTag = new QName(nsUri, "To", prefix);
        this.fromTag = new QName(nsUri, "From", prefix);
        this.replyToTag = new QName(nsUri, "ReplyTo", prefix);
        this.faultToTag = new QName(nsUri, "FaultTo", prefix);
        this.actionTag = new QName(nsUri, "Action", prefix);
        this.messageIDTag = new QName(nsUri, "MessageID", prefix);
        this.relatesToTag = new QName(nsUri, "RelatesTo", prefix);
        this.mapRequiredTag = new QName(nsUri, this.getMapRequiredLocalName(), prefix);
        this.actionMismatchTag = new QName(nsUri, this.getActionMismatchLocalName(), prefix);
        this.actionNotSupportedTag = new QName(nsUri, "ActionNotSupported", prefix);
        this.actionNotSupportedText = "The \"%s\" cannot be processed at the receiver";
        this.invalidMapTag = new QName(nsUri, this.getInvalidMapLocalName(), prefix);
        this.invalidAddressTag = new QName(nsUri, this.getInvalidAddressLocalName(), prefix);
        this.invalidCardinalityTag = new QName(nsUri, this.getInvalidCardinalityLocalName(), prefix);
        this.faultDetailTag = new QName(nsUri, "FaultDetail", prefix);
        this.problemHeaderQNameTag = new QName(nsUri, "ProblemHeaderQName", prefix);
        this.problemActionTag = new QName(nsUri, "ProblemAction", prefix);
        this.fault_missingAddressInEpr = new QName(nsUri, "MissingAddressInEPR", prefix);
        this.isReferenceParameterTag = new QName(nsUri, this.getIsReferenceParameterLocalName(), prefix);
        this.wsdlActionTag = new QName(wsdlNsUri, "Action", prefix);
        this.wsdlExtensionTag = new QName(wsdlNsUri, "UsingAddressing", prefix);
        this.wsdlAnonymousTag = new QName(wsdlNsUri, this.getWsdlAnonymousLocalName(), prefix);
        try {
            this.anonymousEpr = new WSEndpointReference(new ByteArrayInputStream(anonymousEprString.getBytes("UTF-8")), this);
        }
        catch (final XMLStreamException e) {
            throw new Error(e);
        }
        catch (final UnsupportedEncodingException e2) {
            throw new Error(e2);
        }
        this.eprType = eprType;
    }
    
    abstract String getActionMismatchLocalName();
    
    public static AddressingVersion fromNsUri(final String nsUri) {
        if (nsUri.equals(AddressingVersion.W3C.nsUri)) {
            return AddressingVersion.W3C;
        }
        if (nsUri.equals(AddressingVersion.MEMBER.nsUri)) {
            return AddressingVersion.MEMBER;
        }
        return null;
    }
    
    @Nullable
    public static AddressingVersion fromBinding(final WSBinding binding) {
        if (binding.isFeatureEnabled(AddressingFeature.class)) {
            return AddressingVersion.W3C;
        }
        if (binding.isFeatureEnabled(MemberSubmissionAddressingFeature.class)) {
            return AddressingVersion.MEMBER;
        }
        return null;
    }
    
    public static AddressingVersion fromPort(final WSDLPort port) {
        if (port == null) {
            return null;
        }
        WebServiceFeature wsf = port.getFeature(AddressingFeature.class);
        if (wsf == null) {
            wsf = port.getFeature(MemberSubmissionAddressingFeature.class);
        }
        if (wsf == null) {
            return null;
        }
        return fromFeature(wsf);
    }
    
    @Deprecated
    public String getNsUri() {
        return this.nsUri;
    }
    
    public abstract boolean isReferenceParameter(final String p0);
    
    @Deprecated
    public abstract WsaTubeHelper getWsaHelper(final WSDLPort p0, final SEIModel p1, final WSBinding p2);
    
    @Deprecated
    public final String getNoneUri() {
        return this.noneUri;
    }
    
    @Deprecated
    public final String getAnonymousUri() {
        return this.anonymousUri;
    }
    
    public String getDefaultFaultAction() {
        return this.nsUri + "/fault";
    }
    
    abstract String getMapRequiredLocalName();
    
    public abstract String getMapRequiredText();
    
    abstract String getInvalidAddressLocalName();
    
    abstract String getInvalidMapLocalName();
    
    public abstract String getInvalidMapText();
    
    abstract String getInvalidCardinalityLocalName();
    
    abstract String getWsdlAnonymousLocalName();
    
    public abstract String getPrefix();
    
    public abstract String getWsdlPrefix();
    
    public abstract Class<? extends WebServiceFeature> getFeatureClass();
    
    abstract Header createReferenceParameterHeader(final XMLStreamBuffer p0, final String p1, final String p2);
    
    abstract String getIsReferenceParameterLocalName();
    
    public static AddressingVersion fromFeature(final WebServiceFeature af) {
        if (af.getID().equals("http://www.w3.org/2005/08/addressing/module")) {
            return AddressingVersion.W3C;
        }
        if (af.getID().equals("http://java.sun.com/xml/ns/jaxws/2004/08/addressing")) {
            return AddressingVersion.MEMBER;
        }
        return null;
    }
    
    @NotNull
    public static WebServiceFeature getFeature(final String nsUri, final boolean enabled, final boolean required) {
        if (nsUri.equals(AddressingVersion.W3C.policyNsUri)) {
            return new AddressingFeature(enabled, required);
        }
        if (nsUri.equals(AddressingVersion.MEMBER.policyNsUri)) {
            return new MemberSubmissionAddressingFeature(enabled, required);
        }
        throw new WebServiceException("Unsupported namespace URI: " + nsUri);
    }
    
    @NotNull
    public static AddressingVersion fromSpecClass(final Class<? extends EndpointReference> eprClass) {
        if (eprClass == W3CEndpointReference.class) {
            return AddressingVersion.W3C;
        }
        if (eprClass == MemberSubmissionEndpointReference.class) {
            return AddressingVersion.MEMBER;
        }
        throw new WebServiceException("Unsupported EPR type: " + eprClass);
    }
    
    public static boolean isRequired(final WebServiceFeature wsf) {
        if (wsf.getID().equals("http://www.w3.org/2005/08/addressing/module")) {
            return ((AddressingFeature)wsf).isRequired();
        }
        if (wsf.getID().equals("http://java.sun.com/xml/ns/jaxws/2004/08/addressing")) {
            return ((MemberSubmissionAddressingFeature)wsf).isRequired();
        }
        throw new WebServiceException("WebServiceFeature not an Addressing feature: " + wsf.getID());
    }
    
    public static boolean isRequired(final WSBinding binding) {
        final AddressingFeature af = binding.getFeature(AddressingFeature.class);
        if (af != null) {
            return af.isRequired();
        }
        final MemberSubmissionAddressingFeature msaf = binding.getFeature(MemberSubmissionAddressingFeature.class);
        return msaf != null && msaf.isRequired();
    }
    
    public static boolean isEnabled(final WSBinding binding) {
        return binding.isFeatureEnabled(MemberSubmissionAddressingFeature.class) || binding.isFeatureEnabled(AddressingFeature.class);
    }
    
    static {
        fault_duplicateAddressInEpr = new QName("http://jax-ws.dev.java.net/addressing/fault", "DuplicateAddressInEpr", "wsa");
    }
    
    public static final class EPR
    {
        public final Class<? extends EndpointReference> eprClass;
        public final String address;
        public final String serviceName;
        public final String portName;
        public final String portTypeName;
        public final String referenceParameters;
        public final QName wsdlMetadata;
        public final String referenceProperties;
        
        public EPR(final Class<? extends EndpointReference> eprClass, final String address, final String serviceName, final String portName, final String portTypeName, final QName wsdlMetadata, final String referenceParameters, final String referenceProperties) {
            this.eprClass = eprClass;
            this.address = address;
            this.serviceName = serviceName;
            this.portName = portName;
            this.portTypeName = portTypeName;
            this.referenceParameters = referenceParameters;
            this.referenceProperties = referenceProperties;
            this.wsdlMetadata = wsdlMetadata;
        }
    }
}
