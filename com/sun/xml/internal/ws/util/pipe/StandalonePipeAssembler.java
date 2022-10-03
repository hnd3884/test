package com.sun.xml.internal.ws.util.pipe;

import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.PipelineAssembler;

public class StandalonePipeAssembler implements PipelineAssembler
{
    private static final boolean dump;
    
    @NotNull
    @Override
    public Pipe createClient(final ClientPipeAssemblerContext context) {
        Pipe head = context.createTransportPipe();
        head = context.createSecurityPipe(head);
        if (StandalonePipeAssembler.dump) {
            head = context.createDumpPipe("client", System.out, head);
        }
        head = context.createWsaPipe(head);
        head = context.createClientMUPipe(head);
        return context.createHandlerPipe(head);
    }
    
    @Override
    public Pipe createServer(final ServerPipeAssemblerContext context) {
        Pipe head = context.getTerminalPipe();
        head = context.createHandlerPipe(head);
        head = context.createMonitoringPipe(head);
        head = context.createServerMUPipe(head);
        head = context.createWsaPipe(head);
        head = context.createSecurityPipe(head);
        return head;
    }
    
    static {
        boolean b = false;
        try {
            b = Boolean.getBoolean(StandalonePipeAssembler.class.getName() + ".dump");
        }
        catch (final Throwable t) {}
        dump = b;
    }
}
