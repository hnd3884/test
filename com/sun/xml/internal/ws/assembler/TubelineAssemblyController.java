package com.sun.xml.internal.ws.assembler;

import java.net.URISyntaxException;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.istack.internal.logging.Logger;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import java.util.Iterator;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;
import java.util.LinkedList;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryList;
import java.net.URI;
import java.util.Collection;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;

final class TubelineAssemblyController
{
    private final MetroConfigName metroConfigName;
    
    TubelineAssemblyController(final MetroConfigName metroConfigName) {
        this.metroConfigName = metroConfigName;
    }
    
    Collection<TubeCreator> getTubeCreators(final ClientTubelineAssemblyContext context) {
        URI endpointUri;
        if (context.getPortInfo() != null) {
            endpointUri = this.createEndpointComponentUri(context.getPortInfo().getServiceName(), context.getPortInfo().getPortName());
        }
        else {
            endpointUri = null;
        }
        final MetroConfigLoader configLoader = new MetroConfigLoader(context.getContainer(), this.metroConfigName);
        return this.initializeTubeCreators(configLoader.getClientSideTubeFactories(endpointUri));
    }
    
    Collection<TubeCreator> getTubeCreators(final DefaultServerTubelineAssemblyContext context) {
        URI endpointUri;
        if (context.getEndpoint() != null) {
            endpointUri = this.createEndpointComponentUri(context.getEndpoint().getServiceName(), context.getEndpoint().getPortName());
        }
        else {
            endpointUri = null;
        }
        final MetroConfigLoader configLoader = new MetroConfigLoader(context.getEndpoint().getContainer(), this.metroConfigName);
        return this.initializeTubeCreators(configLoader.getEndpointSideTubeFactories(endpointUri));
    }
    
    private Collection<TubeCreator> initializeTubeCreators(final TubeFactoryList tfl) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final LinkedList<TubeCreator> tubeCreators = new LinkedList<TubeCreator>();
        for (final TubeFactoryConfig tubeFactoryConfig : tfl.getTubeFactoryConfigs()) {
            tubeCreators.addFirst(new TubeCreator(tubeFactoryConfig, contextClassLoader));
        }
        return tubeCreators;
    }
    
    private URI createEndpointComponentUri(@NotNull final QName serviceName, @NotNull final QName portName) {
        final StringBuilder sb = new StringBuilder(serviceName.getNamespaceURI()).append("#wsdl11.port(").append(serviceName.getLocalPart()).append('/').append(portName.getLocalPart()).append(')');
        try {
            return new URI(sb.toString());
        }
        catch (final URISyntaxException ex) {
            Logger.getLogger(TubelineAssemblyController.class).warning(TubelineassemblyMessages.MASM_0020_ERROR_CREATING_URI_FROM_GENERATED_STRING(sb.toString()), ex);
            return null;
        }
    }
}
