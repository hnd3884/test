package com.sun.xml.internal.ws.client;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import javax.xml.ws.handler.Handler;
import java.util.List;
import javax.xml.ws.soap.SOAPBinding;
import com.sun.xml.internal.ws.util.HandlerAnnotationProcessor;
import java.util.HashMap;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import java.util.Map;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import javax.xml.ws.handler.PortInfo;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.handler.HandlerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.client.WSPortInfo;

abstract class HandlerConfigurator
{
    abstract void configureHandlers(@NotNull final WSPortInfo p0, @NotNull final BindingImpl p1);
    
    abstract HandlerResolver getResolver();
    
    static final class HandlerResolverImpl extends HandlerConfigurator
    {
        @Nullable
        private final HandlerResolver resolver;
        
        public HandlerResolverImpl(final HandlerResolver resolver) {
            this.resolver = resolver;
        }
        
        @Override
        void configureHandlers(@NotNull final WSPortInfo port, @NotNull final BindingImpl binding) {
            if (this.resolver != null) {
                binding.setHandlerChain(this.resolver.getHandlerChain(port));
            }
        }
        
        @Override
        HandlerResolver getResolver() {
            return this.resolver;
        }
    }
    
    static final class AnnotationConfigurator extends HandlerConfigurator
    {
        private final HandlerChainsModel handlerModel;
        private final Map<WSPortInfo, HandlerAnnotationInfo> chainMap;
        private static final Logger logger;
        
        AnnotationConfigurator(final WSServiceDelegate delegate) {
            this.chainMap = new HashMap<WSPortInfo, HandlerAnnotationInfo>();
            this.handlerModel = HandlerAnnotationProcessor.buildHandlerChainsModel(delegate.getServiceClass());
            assert this.handlerModel != null;
        }
        
        @Override
        void configureHandlers(final WSPortInfo port, final BindingImpl binding) {
            HandlerAnnotationInfo chain = this.chainMap.get(port);
            if (chain == null) {
                this.logGetChain(port);
                chain = this.handlerModel.getHandlersForPortInfo(port);
                this.chainMap.put(port, chain);
            }
            if (binding instanceof SOAPBinding) {
                ((SOAPBinding)binding).setRoles(chain.getRoles());
            }
            this.logSetChain(port, chain);
            binding.setHandlerChain(chain.getHandlers());
        }
        
        @Override
        HandlerResolver getResolver() {
            return new HandlerResolver() {
                @Override
                public List<Handler> getHandlerChain(final PortInfo portInfo) {
                    return new ArrayList<Handler>(AnnotationConfigurator.this.handlerModel.getHandlersForPortInfo(portInfo).getHandlers());
                }
            };
        }
        
        private void logSetChain(final WSPortInfo info, final HandlerAnnotationInfo chain) {
            AnnotationConfigurator.logger.finer("Setting chain of length " + chain.getHandlers().size() + " for port info");
            this.logPortInfo(info, Level.FINER);
        }
        
        private void logGetChain(final WSPortInfo info) {
            AnnotationConfigurator.logger.fine("No handler chain found for port info:");
            this.logPortInfo(info, Level.FINE);
            AnnotationConfigurator.logger.fine("Existing handler chains:");
            if (this.chainMap.isEmpty()) {
                AnnotationConfigurator.logger.fine("none");
            }
            else {
                for (final WSPortInfo key : this.chainMap.keySet()) {
                    AnnotationConfigurator.logger.fine(this.chainMap.get(key).getHandlers().size() + " handlers for port info ");
                    this.logPortInfo(key, Level.FINE);
                }
            }
        }
        
        private void logPortInfo(final WSPortInfo info, final Level level) {
            AnnotationConfigurator.logger.log(level, "binding: " + info.getBindingID() + "\nservice: " + info.getServiceName() + "\nport: " + info.getPortName());
        }
        
        static {
            logger = Logger.getLogger("com.sun.xml.internal.ws.handler");
        }
    }
}
