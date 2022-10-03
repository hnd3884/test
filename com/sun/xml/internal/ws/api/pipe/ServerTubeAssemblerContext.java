package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.addressing.W3CWsaServerTube;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionWsaServerTube;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.server.ServerSchemaValidationTube;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.util.pipe.DumpTube;
import java.io.PrintStream;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.ServerPipelineHook;
import com.sun.xml.internal.ws.handler.HandlerTube;
import com.sun.xml.internal.ws.handler.ServerMessageHandlerTube;
import com.sun.xml.internal.ws.handler.ServerSOAPHandlerTube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.handler.ServerLogicalHandlerTube;
import com.sun.xml.internal.ws.protocol.soap.ServerMUTube;
import javax.xml.ws.soap.SOAPBinding;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.SEIModel;

public class ServerTubeAssemblerContext
{
    private final SEIModel seiModel;
    private final WSDLPort wsdlModel;
    private final WSEndpoint endpoint;
    private final BindingImpl binding;
    private final Tube terminal;
    private final boolean isSynchronous;
    @NotNull
    private Codec codec;
    
    public ServerTubeAssemblerContext(@Nullable final SEIModel seiModel, @Nullable final WSDLPort wsdlModel, @NotNull final WSEndpoint endpoint, @NotNull final Tube terminal, final boolean isSynchronous) {
        this.seiModel = seiModel;
        this.wsdlModel = wsdlModel;
        this.endpoint = endpoint;
        this.terminal = terminal;
        this.binding = (BindingImpl)endpoint.getBinding();
        this.isSynchronous = isSynchronous;
        this.codec = this.binding.createCodec();
    }
    
    @Nullable
    public SEIModel getSEIModel() {
        return this.seiModel;
    }
    
    @Nullable
    public WSDLPort getWsdlModel() {
        return this.wsdlModel;
    }
    
    @NotNull
    public WSEndpoint<?> getEndpoint() {
        return this.endpoint;
    }
    
    @NotNull
    public Tube getTerminalTube() {
        return this.terminal;
    }
    
    public boolean isSynchronous() {
        return this.isSynchronous;
    }
    
    @NotNull
    public Tube createServerMUTube(@NotNull final Tube next) {
        if (this.binding instanceof SOAPBinding) {
            return new ServerMUTube(this, next);
        }
        return next;
    }
    
    @NotNull
    public Tube createHandlerTube(@NotNull Tube next) {
        if (!this.binding.getHandlerChain().isEmpty()) {
            HandlerTube cousin = (HandlerTube)(next = new ServerLogicalHandlerTube(this.binding, this.seiModel, this.wsdlModel, next));
            if (this.binding instanceof SOAPBinding) {
                cousin = (HandlerTube)(next = new ServerSOAPHandlerTube(this.binding, next, cousin));
                next = new ServerMessageHandlerTube(this.seiModel, this.binding, next, cousin);
            }
        }
        return next;
    }
    
    @NotNull
    public Tube createMonitoringTube(@NotNull final Tube next) {
        final ServerPipelineHook hook = this.endpoint.getContainer().getSPI(ServerPipelineHook.class);
        if (hook != null) {
            final ServerPipeAssemblerContext ctxt = new ServerPipeAssemblerContext(this.seiModel, this.wsdlModel, this.endpoint, this.terminal, this.isSynchronous);
            return PipeAdapter.adapt(hook.createMonitoringPipe(ctxt, PipeAdapter.adapt(next)));
        }
        return next;
    }
    
    @NotNull
    public Tube createSecurityTube(@NotNull final Tube next) {
        final ServerPipelineHook hook = this.endpoint.getContainer().getSPI(ServerPipelineHook.class);
        if (hook != null) {
            final ServerPipeAssemblerContext ctxt = new ServerPipeAssemblerContext(this.seiModel, this.wsdlModel, this.endpoint, this.terminal, this.isSynchronous);
            return PipeAdapter.adapt(hook.createSecurityPipe(ctxt, PipeAdapter.adapt(next)));
        }
        return next;
    }
    
    public Tube createDumpTube(final String name, final PrintStream out, final Tube next) {
        return new DumpTube(name, out, next);
    }
    
    public Tube createValidationTube(final Tube next) {
        if (this.binding instanceof SOAPBinding && this.binding.isFeatureEnabled(SchemaValidationFeature.class) && this.wsdlModel != null) {
            return new ServerSchemaValidationTube(this.endpoint, this.binding, this.seiModel, this.wsdlModel, next);
        }
        return next;
    }
    
    public Tube createWsaTube(final Tube next) {
        if (!(this.binding instanceof SOAPBinding) || !AddressingVersion.isEnabled(this.binding)) {
            return next;
        }
        if (AddressingVersion.fromBinding(this.binding) == AddressingVersion.MEMBER) {
            return new MemberSubmissionWsaServerTube(this.endpoint, this.wsdlModel, this.binding, next);
        }
        return new W3CWsaServerTube(this.endpoint, this.wsdlModel, this.binding, next);
    }
    
    @NotNull
    public Codec getCodec() {
        return this.codec;
    }
    
    public void setCodec(@NotNull final Codec codec) {
        this.codec = codec;
    }
}
