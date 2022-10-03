package javax.ws.rs.client;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.Future;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public interface Invocation
{
    Invocation property(final String p0, final Object p1);
    
    Response invoke();
    
     <T> T invoke(final Class<T> p0);
    
     <T> T invoke(final GenericType<T> p0);
    
    Future<Response> submit();
    
     <T> Future<T> submit(final Class<T> p0);
    
     <T> Future<T> submit(final GenericType<T> p0);
    
     <T> Future<T> submit(final InvocationCallback<T> p0);
    
    public interface Builder extends SyncInvoker
    {
        Invocation build(final String p0);
        
        Invocation build(final String p0, final Entity<?> p1);
        
        Invocation buildGet();
        
        Invocation buildDelete();
        
        Invocation buildPost(final Entity<?> p0);
        
        Invocation buildPut(final Entity<?> p0);
        
        AsyncInvoker async();
        
        Builder accept(final String... p0);
        
        Builder accept(final MediaType... p0);
        
        Builder acceptLanguage(final Locale... p0);
        
        Builder acceptLanguage(final String... p0);
        
        Builder acceptEncoding(final String... p0);
        
        Builder cookie(final Cookie p0);
        
        Builder cookie(final String p0, final String p1);
        
        Builder cacheControl(final CacheControl p0);
        
        Builder header(final String p0, final Object p1);
        
        Builder headers(final MultivaluedMap<String, Object> p0);
        
        Builder property(final String p0, final Object p1);
        
        CompletionStageRxInvoker rx();
        
         <T extends RxInvoker> T rx(final Class<T> p0);
    }
}
