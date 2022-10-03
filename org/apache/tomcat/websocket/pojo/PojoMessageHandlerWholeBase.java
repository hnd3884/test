package org.apache.tomcat.websocket.pojo;

import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import javax.websocket.DecodeException;
import javax.naming.NamingException;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.websocket.WsSession;
import java.util.ArrayList;
import org.apache.juli.logging.LogFactory;
import javax.websocket.Session;
import java.lang.reflect.Method;
import javax.websocket.Decoder;
import java.util.List;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import javax.websocket.MessageHandler;

public abstract class PojoMessageHandlerWholeBase<T> extends PojoMessageHandlerBase<T> implements MessageHandler.Whole<T>
{
    private final Log log;
    private static final StringManager sm;
    protected final List<Decoder> decoders;
    
    public PojoMessageHandlerWholeBase(final Object pojo, final Method method, final Session session, final Object[] params, final int indexPayload, final boolean convert, final int indexSession, final long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        this.log = LogFactory.getLog((Class)PojoMessageHandlerWholeBase.class);
        this.decoders = new ArrayList<Decoder>();
    }
    
    protected Decoder createDecoderInstance(final Class<? extends Decoder> clazz) throws ReflectiveOperationException, NamingException {
        final InstanceManager instanceManager = ((WsSession)this.session).getInstanceManager();
        if (instanceManager == null) {
            return (Decoder)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        return (Decoder)instanceManager.newInstance((Class)clazz);
    }
    
    public final void onMessage(final T message) {
        if (this.params.length == 1 && this.params[0] instanceof DecodeException) {
            ((WsSession)this.session).getLocal().onError(this.session, (Throwable)this.params[0]);
            return;
        }
        Object payload;
        try {
            payload = this.decode(message);
        }
        catch (final DecodeException de) {
            ((WsSession)this.session).getLocal().onError(this.session, (Throwable)de);
            return;
        }
        if (payload == null) {
            if (this.convert) {
                payload = this.convert(message);
            }
            else {
                payload = message;
            }
        }
        final Object[] parameters = this.params.clone();
        if (this.indexSession != -1) {
            parameters[this.indexSession] = this.session;
        }
        parameters[this.indexPayload] = payload;
        Object result = null;
        try {
            result = this.method.invoke(this.pojo, parameters);
        }
        catch (final IllegalAccessException | InvocationTargetException e) {
            this.handlePojoMethodException(e);
        }
        this.processResult(result);
    }
    
    protected void onClose() {
        final InstanceManager instanceManager = ((WsSession)this.session).getInstanceManager();
        for (final Decoder decoder : this.decoders) {
            decoder.destroy();
            if (instanceManager != null) {
                try {
                    instanceManager.destroyInstance((Object)decoder);
                }
                catch (final IllegalAccessException | InvocationTargetException e) {
                    this.log.warn((Object)PojoMessageHandlerWholeBase.sm.getString("pojoMessageHandlerWholeBase.decodeDestoryFailed", new Object[] { decoder.getClass() }), (Throwable)e);
                }
            }
        }
    }
    
    protected Object convert(final T message) {
        return message;
    }
    
    protected abstract Object decode(final T p0) throws DecodeException;
    
    static {
        sm = StringManager.getManager((Class)PojoMessageHandlerWholeBase.class);
    }
}
