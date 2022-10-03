package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import javax.xml.transform.Source;
import java.util.Map;
import org.xml.sax.SAXException;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.ls.LSResourceResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import com.sun.xml.internal.ws.util.MetadataUtil;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import javax.xml.validation.Validator;
import javax.xml.validation.Schema;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube;

public class ClientSchemaValidationTube extends AbstractSchemaValidationTube
{
    private static final Logger LOGGER;
    private final Schema schema;
    private final Validator validator;
    private final boolean noValidation;
    private final WSDLPort port;
    
    public ClientSchemaValidationTube(final WSBinding binding, final WSDLPort port, final Tube next) {
        super(binding, next);
        this.port = port;
        if (port != null) {
            final String primaryWsdl = port.getOwner().getParent().getLocation().getSystemId();
            MetadataResolverImpl mdresolver = new MetadataResolverImpl();
            final Map<String, SDDocument> docs = MetadataUtil.getMetadataClosure(primaryWsdl, mdresolver, true);
            mdresolver = new MetadataResolverImpl(docs.values());
            final Source[] schemaSources;
            final Source[] sources = schemaSources = this.getSchemaSources(docs.values(), mdresolver);
            for (final Source source : schemaSources) {
                ClientSchemaValidationTube.LOGGER.fine("Constructing client validation schema from = " + source.getSystemId());
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
    
    protected ClientSchemaValidationTube(final ClientSchemaValidationTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.port = that.port;
        this.schema = that.schema;
        this.validator = this.schema.newValidator();
        this.noValidation = that.noValidation;
    }
    
    @Override
    public AbstractTubeImpl copy(final TubeCloner cloner) {
        return new ClientSchemaValidationTube(this, cloner);
    }
    
    @Override
    public NextAction processRequest(final Packet request) {
        if (this.isNoValidation() || !this.feature.isOutbound() || !request.getMessage().hasPayload() || request.getMessage().isFault()) {
            return super.processRequest(request);
        }
        try {
            this.doProcess(request);
        }
        catch (final SAXException se) {
            throw new WebServiceException(se);
        }
        return super.processRequest(request);
    }
    
    @Override
    public NextAction processResponse(final Packet response) {
        if (this.isNoValidation() || !this.feature.isInbound() || response.getMessage() == null || !response.getMessage().hasPayload() || response.getMessage().isFault()) {
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
    
    static {
        LOGGER = Logger.getLogger(ClientSchemaValidationTube.class.getName());
    }
}
