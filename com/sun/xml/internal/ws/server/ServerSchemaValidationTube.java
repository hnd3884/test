package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.ls.LSResourceResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.validation.Validator;
import javax.xml.validation.Schema;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube;

public class ServerSchemaValidationTube extends AbstractSchemaValidationTube
{
    private static final Logger LOGGER;
    private final Schema schema;
    private final Validator validator;
    private final boolean noValidation;
    private final SEIModel seiModel;
    private final WSDLPort wsdlPort;
    
    public ServerSchemaValidationTube(final WSEndpoint endpoint, final WSBinding binding, final SEIModel seiModel, final WSDLPort wsdlPort, final Tube next) {
        super(binding, next);
        this.seiModel = seiModel;
        this.wsdlPort = wsdlPort;
        if (endpoint.getServiceDefinition() != null) {
            final MetadataResolverImpl mdresolver = new MetadataResolverImpl(endpoint.getServiceDefinition());
            final Source[] schemaSources;
            final Source[] sources = schemaSources = this.getSchemaSources(endpoint.getServiceDefinition(), mdresolver);
            for (final Source source : schemaSources) {
                ServerSchemaValidationTube.LOGGER.fine("Constructing service validation schema from = " + source.getSystemId());
            }
            if (sources.length != 0) {
                this.noValidation = false;
                this.sf.setResourceResolver(mdresolver);
                try {
                    this.schema = this.sf.newSchema(sources);
                }
                catch (final SAXException e) {
                    throw new WebServiceException(e);
                }
                this.validator = this.schema.newValidator();
                return;
            }
        }
        this.noValidation = true;
        this.schema = null;
        this.validator = null;
    }
    
    @Override
    protected Validator getValidator() {
        return this.validator;
    }
    
    @Override
    protected boolean isNoValidation() {
        return this.noValidation;
    }
    
    @Override
    public NextAction processRequest(final Packet request) {
        if (this.isNoValidation() || !this.feature.isInbound() || !request.getMessage().hasPayload() || request.getMessage().isFault()) {
            return super.processRequest(request);
        }
        try {
            this.doProcess(request);
        }
        catch (final SAXException se) {
            ServerSchemaValidationTube.LOGGER.log(Level.WARNING, "Client Request doesn't pass Service's Schema Validation", se);
            final SOAPVersion soapVersion = this.binding.getSOAPVersion();
            final Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, se, soapVersion.faultCodeClient);
            return this.doReturnWith(request.createServerResponse(faultMsg, this.wsdlPort, this.seiModel, this.binding));
        }
        return super.processRequest(request);
    }
    
    @Override
    public NextAction processResponse(final Packet response) {
        if (this.isNoValidation() || !this.feature.isOutbound() || response.getMessage() == null || !response.getMessage().hasPayload() || response.getMessage().isFault()) {
            return super.processResponse(response);
        }
        try {
            this.doProcess(response);
        }
        catch (final SAXException se) {
            throw new WebServiceException(se);
        }
        return super.processResponse(response);
    }
    
    protected ServerSchemaValidationTube(final ServerSchemaValidationTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.schema = that.schema;
        this.validator = this.schema.newValidator();
        this.noValidation = that.noValidation;
        this.seiModel = that.seiModel;
        this.wsdlPort = that.wsdlPort;
    }
    
    @Override
    public AbstractTubeImpl copy(final TubeCloner cloner) {
        return new ServerSchemaValidationTube(this, cloner);
    }
    
    static {
        LOGGER = Logger.getLogger(ServerSchemaValidationTube.class.getName());
    }
}
