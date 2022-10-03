package org.apache.tomcat.websocket.pojo;

import java.util.Set;
import javax.websocket.CloseReason;
import java.io.IOException;
import org.apache.tomcat.util.ExceptionUtils;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import javax.websocket.MessageHandler;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.juli.logging.LogFactory;
import java.util.Map;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import javax.websocket.Endpoint;

public abstract class PojoEndpointBase extends Endpoint
{
    private final Log log;
    private static final StringManager sm;
    private Object pojo;
    private final Map<String, String> pathParameters;
    private PojoMethodMapping methodMapping;
    
    protected PojoEndpointBase(final Map<String, String> pathParameters) {
        this.log = LogFactory.getLog((Class)PojoEndpointBase.class);
        this.pathParameters = pathParameters;
    }
    
    protected final void doOnOpen(final Session session, final EndpointConfig config) {
        final PojoMethodMapping methodMapping = this.getMethodMapping();
        final Object pojo = this.getPojo();
        for (final MessageHandler mh : methodMapping.getMessageHandlers(pojo, this.pathParameters, session, config)) {
            session.addMessageHandler(mh);
        }
        if (methodMapping.getOnOpen() != null) {
            try {
                methodMapping.getOnOpen().invoke(pojo, methodMapping.getOnOpenArgs(this.pathParameters, session, config));
            }
            catch (final IllegalAccessException e) {
                this.log.error((Object)PojoEndpointBase.sm.getString("pojoEndpointBase.onOpenFail", new Object[] { pojo.getClass().getName() }), (Throwable)e);
                this.handleOnOpenOrCloseError(session, e);
            }
            catch (final InvocationTargetException e2) {
                final Throwable cause = e2.getCause();
                this.handleOnOpenOrCloseError(session, cause);
            }
            catch (final Throwable t) {
                this.handleOnOpenOrCloseError(session, t);
            }
        }
    }
    
    private void handleOnOpenOrCloseError(final Session session, final Throwable t) {
        ExceptionUtils.handleThrowable(t);
        this.onError(session, t);
        try {
            session.close();
        }
        catch (final IOException ioe) {
            this.log.warn((Object)PojoEndpointBase.sm.getString("pojoEndpointBase.closeSessionFail"), (Throwable)ioe);
        }
    }
    
    public final void onClose(final Session session, final CloseReason closeReason) {
        if (this.methodMapping.getOnClose() != null) {
            try {
                this.methodMapping.getOnClose().invoke(this.pojo, this.methodMapping.getOnCloseArgs(this.pathParameters, session, closeReason));
            }
            catch (final Throwable t) {
                this.log.error((Object)PojoEndpointBase.sm.getString("pojoEndpointBase.onCloseFail", new Object[] { this.pojo.getClass().getName() }), t);
                this.handleOnOpenOrCloseError(session, t);
            }
        }
        final Set<MessageHandler> messageHandlers = session.getMessageHandlers();
        for (final MessageHandler messageHandler : messageHandlers) {
            if (messageHandler instanceof PojoMessageHandlerWholeBase) {
                ((PojoMessageHandlerWholeBase)messageHandler).onClose();
            }
        }
    }
    
    public final void onError(final Session session, final Throwable throwable) {
        if (this.methodMapping.getOnError() == null) {
            this.log.error((Object)PojoEndpointBase.sm.getString("pojoEndpointBase.onError", new Object[] { this.pojo.getClass().getName() }), throwable);
        }
        else {
            try {
                this.methodMapping.getOnError().invoke(this.pojo, this.methodMapping.getOnErrorArgs(this.pathParameters, session, throwable));
            }
            catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.log.error((Object)PojoEndpointBase.sm.getString("pojoEndpointBase.onErrorFail", new Object[] { this.pojo.getClass().getName() }), t);
            }
        }
    }
    
    protected Object getPojo() {
        return this.pojo;
    }
    
    protected void setPojo(final Object pojo) {
        this.pojo = pojo;
    }
    
    protected PojoMethodMapping getMethodMapping() {
        return this.methodMapping;
    }
    
    protected void setMethodMapping(final PojoMethodMapping methodMapping) {
        this.methodMapping = methodMapping;
    }
    
    static {
        sm = StringManager.getManager((Class)PojoEndpointBase.class);
    }
}
