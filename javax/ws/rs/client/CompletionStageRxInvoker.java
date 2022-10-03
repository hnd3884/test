package javax.ws.rs.client;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

public interface CompletionStageRxInvoker extends RxInvoker<CompletionStage>
{
    CompletionStage<Response> get();
    
     <T> CompletionStage<T> get(final Class<T> p0);
    
     <T> CompletionStage<T> get(final GenericType<T> p0);
    
    CompletionStage<Response> put(final Entity<?> p0);
    
     <T> CompletionStage<T> put(final Entity<?> p0, final Class<T> p1);
    
     <T> CompletionStage<T> put(final Entity<?> p0, final GenericType<T> p1);
    
    CompletionStage<Response> post(final Entity<?> p0);
    
     <T> CompletionStage<T> post(final Entity<?> p0, final Class<T> p1);
    
     <T> CompletionStage<T> post(final Entity<?> p0, final GenericType<T> p1);
    
    CompletionStage<Response> delete();
    
     <T> CompletionStage<T> delete(final Class<T> p0);
    
     <T> CompletionStage<T> delete(final GenericType<T> p0);
    
    CompletionStage<Response> head();
    
    CompletionStage<Response> options();
    
     <T> CompletionStage<T> options(final Class<T> p0);
    
     <T> CompletionStage<T> options(final GenericType<T> p0);
    
    CompletionStage<Response> trace();
    
     <T> CompletionStage<T> trace(final Class<T> p0);
    
     <T> CompletionStage<T> trace(final GenericType<T> p0);
    
    CompletionStage<Response> method(final String p0);
    
     <T> CompletionStage<T> method(final String p0, final Class<T> p1);
    
     <T> CompletionStage<T> method(final String p0, final GenericType<T> p1);
    
    CompletionStage<Response> method(final String p0, final Entity<?> p1);
    
     <T> CompletionStage<T> method(final String p0, final Entity<?> p1, final Class<T> p2);
    
     <T> CompletionStage<T> method(final String p0, final Entity<?> p1, final GenericType<T> p2);
}
