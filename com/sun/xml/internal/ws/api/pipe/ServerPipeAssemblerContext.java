package com.sun.xml.internal.ws.api.pipe;

import java.io.PrintStream;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;

public final class ServerPipeAssemblerContext extends ServerTubeAssemblerContext
{
    public ServerPipeAssemblerContext(@Nullable final SEIModel seiModel, @Nullable final WSDLPort wsdlModel, @NotNull final WSEndpoint endpoint, @NotNull final Tube terminal, final boolean isSynchronous) {
        super(seiModel, wsdlModel, endpoint, terminal, isSynchronous);
    }
    
    @NotNull
    public Pipe createServerMUPipe(@NotNull final Pipe next) {
        return PipeAdapter.adapt(super.createServerMUTube(PipeAdapter.adapt(next)));
    }
    
    public Pipe createDumpPipe(final String name, final PrintStream out, final Pipe next) {
        return PipeAdapter.adapt(super.createDumpTube(name, out, PipeAdapter.adapt(next)));
    }
    
    @NotNull
    public Pipe createMonitoringPipe(@NotNull final Pipe next) {
        return PipeAdapter.adapt(super.createMonitoringTube(PipeAdapter.adapt(next)));
    }
    
    @NotNull
    public Pipe createSecurityPipe(@NotNull final Pipe next) {
        return PipeAdapter.adapt(super.createSecurityTube(PipeAdapter.adapt(next)));
    }
    
    @NotNull
    public Pipe createValidationPipe(@NotNull final Pipe next) {
        return PipeAdapter.adapt(super.createValidationTube(PipeAdapter.adapt(next)));
    }
    
    @NotNull
    public Pipe createHandlerPipe(@NotNull final Pipe next) {
        return PipeAdapter.adapt(super.createHandlerTube(PipeAdapter.adapt(next)));
    }
    
    @NotNull
    public Pipe getTerminalPipe() {
        return PipeAdapter.adapt(super.getTerminalTube());
    }
    
    public Pipe createWsaPipe(final Pipe next) {
        return PipeAdapter.adapt(super.createWsaTube(PipeAdapter.adapt(next)));
    }
}
