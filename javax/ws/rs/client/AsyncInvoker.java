package javax.ws.rs.client;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;

public interface AsyncInvoker
{
    Future<Response> get();
    
     <T> Future<T> get(final Class<T> p0);
    
     <T> Future<T> get(final GenericType<T> p0);
    
     <T> Future<T> get(final InvocationCallback<T> p0);
    
    Future<Response> put(final Entity<?> p0);
    
     <T> Future<T> put(final Entity<?> p0, final Class<T> p1);
    
     <T> Future<T> put(final Entity<?> p0, final GenericType<T> p1);
    
     <T> Future<T> put(final Entity<?> p0, final InvocationCallback<T> p1);
    
    Future<Response> post(final Entity<?> p0);
    
     <T> Future<T> post(final Entity<?> p0, final Class<T> p1);
    
     <T> Future<T> post(final Entity<?> p0, final GenericType<T> p1);
    
     <T> Future<T> post(final Entity<?> p0, final InvocationCallback<T> p1);
    
    Future<Response> delete();
    
     <T> Future<T> delete(final Class<T> p0);
    
     <T> Future<T> delete(final GenericType<T> p0);
    
     <T> Future<T> delete(final InvocationCallback<T> p0);
    
    Future<Response> head();
    
    Future<Response> head(final InvocationCallback<Response> p0);
    
    Future<Response> options();
    
     <T> Future<T> options(final Class<T> p0);
    
     <T> Future<T> options(final GenericType<T> p0);
    
     <T> Future<T> options(final InvocationCallback<T> p0);
    
    Future<Response> trace();
    
     <T> Future<T> trace(final Class<T> p0);
    
     <T> Future<T> trace(final GenericType<T> p0);
    
     <T> Future<T> trace(final InvocationCallback<T> p0);
    
    Future<Response> method(final String p0);
    
     <T> Future<T> method(final String p0, final Class<T> p1);
    
     <T> Future<T> method(final String p0, final GenericType<T> p1);
    
     <T> Future<T> method(final String p0, final InvocationCallback<T> p1);
    
    Future<Response> method(final String p0, final Entity<?> p1);
    
     <T> Future<T> method(final String p0, final Entity<?> p1, final Class<T> p2);
    
     <T> Future<T> method(final String p0, final Entity<?> p1, final GenericType<T> p2);
    
     <T> Future<T> method(final String p0, final Entity<?> p1, final InvocationCallback<T> p2);
}
