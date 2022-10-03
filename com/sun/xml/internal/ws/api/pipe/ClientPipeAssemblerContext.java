package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import java.io.PrintStream;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;

public final class ClientPipeAssemblerContext extends ClientTubeAssemblerContext
{
    public ClientPipeAssemblerContext(@NotNull final EndpointAddress address, @NotNull final WSDLPort wsdlModel, @NotNull final WSService rootOwner, @NotNull final WSBinding binding) {
        this(address, wsdlModel, rootOwner, binding, Container.NONE);
    }
    
    public ClientPipeAssemblerContext(@NotNull final EndpointAddress address, @NotNull final WSDLPort wsdlModel, @NotNull final WSService rootOwner, @NotNull final WSBinding binding, @NotNull final Container container) {
        super(address, wsdlModel, rootOwner, binding, container);
    }
    
    public Pipe createDumpPipe(final String name, final PrintStream out, final Pipe next) {
        return PipeAdapter.adapt(super.createDumpTube(name, out, PipeAdapter.adapt(next)));
    }
    
    public Pipe createWsaPipe(final Pipe next) {
        return PipeAdapter.adapt(super.createWsaTube(PipeAdapter.adapt(next)));
    }
    
    public Pipe createClientMUPipe(final Pipe next) {
        return PipeAdapter.adapt(super.createClientMUTube(PipeAdapter.adapt(next)));
    }
    
    public Pipe createValidationPipe(final Pipe next) {
        return PipeAdapter.adapt(super.createValidationTube(PipeAdapter.adapt(next)));
    }
    
    public Pipe createHandlerPipe(final Pipe next) {
        return PipeAdapter.adapt(super.createHandlerTube(PipeAdapter.adapt(next)));
    }
    
    @NotNull
    public Pipe createSecurityPipe(@NotNull final Pipe next) {
        return PipeAdapter.adapt(super.createSecurityTube(PipeAdapter.adapt(next)));
    }
    
    public Pipe createTransportPipe() {
        return PipeAdapter.adapt(super.createTransportTube());
    }
}
